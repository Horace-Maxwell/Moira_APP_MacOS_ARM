//
//Moira - A Chinese Astrology Charting Program
//Copyright (C) 2004-2015 At Home Projects
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package org.athomeprojects.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class EvalRule {
	private ChartData data;

	private BaseTab out;

	private int year_offset_start, year_offset_end;

	private boolean list_variable, show_all_failure, show_failure;

	private Hashtable birth_table, now_table;

	private LinkedList good, bad;

	private String[] full_zodiac, full_stellar_signs;

	public EvalRule(ChartData t_data) {
		data = t_data;
	}

	public void initSign(String[] zodiac, String[] stellar_signs, boolean sex) {
		birth_table = new Hashtable();
		good = new LinkedList();
		bad = new LinkedList();
		now_table = null;
		full_zodiac = zodiac;
		full_stellar_signs = stellar_signs;
		RuleEntry.initBirth(birth_table, full_zodiac, full_stellar_signs, sex);
		String space = Resource.getString("non_white_space");
		birth_table.put("$s", space);
		space += space;
		birth_table.put("$s2", space);
		space += space;
		birth_table.put("$s4", space);
		space += space;
		birth_table.put("$s8", space);
		birth_table.put("$n", "\n");
		birth_table.put("$t", "\t");
		birth_table.put("?t", "t");
		birth_table.put("?" + Resource.getString(sex ? "male" : "female"), "t");
		birth_table.put("$e", new LinkedHashSet());
	}

	public void initNow() {
		now_table = new Hashtable();
		RuleEntry.initNow(now_table);
	}

	public void setBirthInfo(Calculate cal, String[] birth_poles,
			boolean day_birth, String[] year_signs, double life_sign_pos,
			double self_sign_pos, double[] stellar_sign_pos, int[] lunar_date,
			boolean leap_month) {
		String pole_char = Resource.getString("pole_char");
		String year_char = Resource.getString("year_char");
		String month_char = Resource.getString("month_char");
		String day_char = Resource.getString("day_char");
		String hour_char = Resource.getString("hour_char");
		String zodiac_house = Resource.getString("zodiac_house");
		birth_table.put("$" + year_char + pole_char,
				birth_poles[ChartData.YEAR_POLE]);
		birth_table.put("$" + month_char + pole_char,
				birth_poles[ChartData.MONTH_POLE]);
		birth_table.put("$" + day_char + pole_char,
				birth_poles[ChartData.DAY_POLE]);
		birth_table.put("$" + hour_char + pole_char,
				birth_poles[ChartData.HOUR_POLE]);
		String month = birth_poles[ChartData.MONTH_POLE].substring(1);
		String[] array = Resource.getStringArray("earth_pole_names");
		int val = FileIO.getArrayIndex(month, array) - 2;
		if (val < 0)
			val += 12;
		val /= 3;
		array = Resource.getStringArray("four_seasons");
		birth_table.put("$" + Resource.getString("season"), Integer
				.toString(val));
		birth_table.put("?" + array[val], "t");
		birth_table.put("?"
				+ Resource.getString(day_birth ? "daytime" : "nighttime"), "t");
		val = (int) (life_sign_pos / 30.0);
		for (int i = 0; i < year_signs.length; i++) {
			int n = val - i;
			if (n < 0)
				n += 12;
			birth_table.put("@" + year_signs[i], full_zodiac[n]);
			birth_table.put(
					"$" + full_zodiac[n].substring(0, 1) + zodiac_house,
					year_signs[i]);
			String key = full_zodiac[i].substring(0, 1);
			String value = data.getWeakHouse(key);
			if (!value.equals(""))
				birth_table.put("?" + value + key, "t");
			value = data.getSolidHouse(key);
			if (!value.equals(""))
				birth_table.put("?" + value + key, "t");
		}
		LinkedHashSet set = new LinkedHashSet();
		for (int i = 0; i < full_zodiac.length; i++)
			set.add(full_zodiac[i]);
		birth_table.put("$" + Resource.getString("zodiac_group"), set);
		String lunar = Resource.getString("lunar_calendar").substring(0, 1);
		birth_table
				.put("$" + lunar + year_char, birth_poles[ChartData.CH_YEAR]);
		if (leap_month)
			birth_table.put("?" + Resource.getString("leap") + month_char, "t");
		birth_table.put("$" + lunar + month_char, Integer
				.toString(lunar_date[1]));
		int[] range = Resource.getIntArray("lunar_face_1_range");
		boolean b_val;
		if (range[0] > range[1]) {
			b_val = lunar_date[2] >= range[0] || lunar_date[2] <= range[1];
		} else {
			b_val = lunar_date[2] >= range[0] && lunar_date[2] <= range[1];
		}
		if (b_val)
			birth_table.put("?" + Resource.getString("lunar_face_1"), "t");
		range = Resource.getIntArray("lunar_face_2_range");
		if (range[0] > range[1]) {
			b_val = lunar_date[2] >= range[0] || lunar_date[2] <= range[1];
		} else {
			b_val = lunar_date[2] >= range[0] && lunar_date[2] <= range[1];
		}
		if (b_val)
			birth_table.put("?" + Resource.getString("lunar_face_2"), "t");
		String key = Resource.getString("life_master").substring(0, 1);
		birth_table.put("@" + key, cal.getZodiac(life_sign_pos, true));
		birth_table.put("%" + key, cal.getStarSign(life_sign_pos,
				stellar_sign_pos, full_stellar_signs));
		double boundary_range = Resource.getDouble("boundary_range");
		double d_val = life_sign_pos % 30.0;
		if (d_val < boundary_range || d_val > 30.0 - boundary_range) {
			birth_table.put("?" + key + Resource.getString("zodiac_boundary"),
					"t");
		}
		if (withinSignBoundary(life_sign_pos, stellar_sign_pos, boundary_range)) {
			birth_table.put("?" + key + Resource.getString("stellar_boundary"),
					"t");
		}
		key = Resource.getString("self_master").substring(0, 1);
		birth_table.put("@" + key, cal.getZodiac(self_sign_pos, true));
		birth_table.put("%" + key, cal.getStarSign(self_sign_pos,
				stellar_sign_pos, full_stellar_signs));
		d_val = self_sign_pos % 30.0;
		if (d_val < boundary_range || d_val > 30.0 - boundary_range) {
			birth_table.put("?" + key + Resource.getString("zodiac_boundary"),
					"t");
		}
		if (withinSignBoundary(self_sign_pos, stellar_sign_pos, boundary_range)) {
			birth_table.put("?" + key + Resource.getString("stellar_boundary"),
					"t");
		}
		key = Resource.getString("life_helper_key");
		for (int iter = 0; iter < 2; iter++) {
			String sign, type;
			switch (iter) {
			case 0:
				sign = cal.getStarSign(life_sign_pos, stellar_sign_pos,
						full_stellar_signs);
				type = Resource.getString("degree");
				break;
			default:
				sign = cal.getZodiac(life_sign_pos, true);
				type = zodiac_house;
				break;
			}
			String[] helper = Resource.getStringArray(key + sign.substring(1));
			for (int i = 0; i < helper.length; i++)
				saveStringSetToTable(birth_table, "$" + key.substring(i, i + 1)
						+ type, helper[i], 1);
		}
	}

	public boolean setNowInfo(Calculate cal, String[] now_poles,
			int[] now_date, int age, double[] now_pos,
			double[] stellar_sign_pos, int[] now_lunar_date,
			boolean now_leap_month) {
		if (now_poles == null || now_date == null)
			return false;
		if (now_pos == null)
			return false;
		String str = getNowStellarPos(cal, now_pos, stellar_sign_pos);
		if (str == null)
			return false;
		saveStringSetToTable(now_table, "$" + Resource.getString("limit"), str,
				2);
		str = getNowZodiacPos(cal, now_pos);
		saveStringSetToTable(now_table, "$"
				+ Resource.getString("zodiac_limit"), str, 1);
		double pos, gap;
		if (now_pos[1] > now_pos[0]) {
			pos = City.normalizeDegree(0.5 * (now_pos[0] + now_pos[1] + 360.0));
			gap = now_pos[0] - now_pos[1] + 360.0;
		} else {
			pos = 0.5 * (now_pos[0] + now_pos[1]);
			gap = now_pos[0] - now_pos[1];
		}
		now_table.put("$" + Resource.getString("limit")
				+ Resource.getString("degree"), FileIO.formatDouble(pos, 1, 2,
				false, false));
		now_table.put("$" + Resource.getString("limit")
				+ Resource.getString("aspects_orb_name"), FileIO.formatDouble(
				gap, 1, 2, false, false));
		String pole_char = Resource.getString("pole_char");
		String year_char = Resource.getString("year_char");
		String current_date = Resource.getString("current_date");
		String now = current_date.substring(0, 1);
		now_table.put("$" + now + year_char + pole_char,
				now_poles[ChartData.YEAR_POLE]);
		if (now_lunar_date != null) {
			String month_char = Resource.getString("month_char");
			String day_char = Resource.getString("day_char");
			String hour_char = Resource.getString("hour_char");
			now_table.put("$" + now + month_char + pole_char,
					now_poles[ChartData.MONTH_POLE]);
			now_table.put("$" + now + day_char + pole_char,
					now_poles[ChartData.DAY_POLE]);
			now_table.put("$" + now + hour_char + pole_char,
					now_poles[ChartData.HOUR_POLE]);
			String lunar = Resource.getString("lunar_calendar").substring(0, 1);
			now_table.put("$" + now + lunar + year_char,
					now_poles[ChartData.CH_YEAR]);
			if (now_leap_month)
				now_table.put("?" + now + Resource.getString("leap")
						+ month_char, "t");
			now_table.put("$" + now + lunar + month_char, Integer
					.toString(now_lunar_date[1]));
		}
		if (age < 1 || age > 99)
			age = 1;
		now_table.put("$" + now + Resource.getString("age"), Integer
				.toString(age));
		now_table.put("$" + current_date, Integer.toString(now_date[0]));
		int child_age_limit = data.getChildLimit(true);
		String key = (age - 1 < child_age_limit) ? data.getChildLimit(age - 1,
				null) : null;
		if (key != null)
			now_table.put("$" + Resource.getString("child_limit"), key);
		key = data.getSmallLimit(age, ":");
		now_table.put("$" + key.substring(0, 2), key.substring(3));
		if (now_lunar_date != null) {
			key = data.getMonthLimit(age, ":");
			now_table.put("$" + key.substring(0, 2), key.substring(3));
		}
		key = data.getFlyLimit(age - 1, ":", child_age_limit);
		if (key != null) {
			String f_key = key.substring(0, 2);
			String f_val = key.substring(3);
			if (f_val.length() > 1)
				f_val = f_val.substring(0, 2);
			saveStringSetToTable(now_table, "$" + f_key, f_val, 1);
		}
		return true;
	}

	private String getNowStellarPos(Calculate cal, double[] pos,
			double[] sign_pos) {
		int s_index = cal.getSignIndex(pos[0], sign_pos, 0);
		int e_index = cal.getSignIndex(pos[1], sign_pos, 0);
		if (s_index < 0 || e_index < 0)
			return null;
		if (e_index > s_index)
			s_index += full_stellar_signs.length;
		String str = "";
		for (int i = s_index; i >= e_index; i--) {
			str += full_stellar_signs[i % full_stellar_signs.length];
		}
		return str;
	}

	private String getNowZodiacPos(Calculate cal, double[] pos) {
		String str = cal.getZodiac(pos[0], false);
		String e_str = cal.getZodiac(pos[1], false);
		return str.equals(e_str) ? str : (str + e_str);
	}

	private boolean withinSignBoundary(double pos, double[] sign_pos,
			double range) {
		for (int i = 0; i < sign_pos.length; i++) {
			double l_val = sign_pos[i] - range;
			double u_val = sign_pos[i] + range;
			if (l_val < 0.0) {
				if (pos < u_val || pos > l_val + 360.0)
					return true;
			} else if (u_val > 360.0) {
				if (pos < u_val - 360.0 || pos > l_val)
					return true;
			} else if (pos > l_val && pos < u_val) {
				return true;
			}
		}
		return false;
	}

	public void setBirthSign(Calculate cal, String[] signs, double[] sign_pos,
			double[] stellar_sign_pos, double mountain_pos) {
		if (Calculate.isValid(mountain_pos)) {
			birth_table.put("@" + Resource.getString("mountain_name"), cal
					.getZodiac(mountain_pos, true));
		}
		String against = Resource.getString("against");
		String[] enemies = Resource.getStringArray("enemies");
		String strengthen = Resource.getString("strengthen");
		String[] helpers = Resource.getStringArray("helpers");
		for (int i = 0; i < signs.length; i++) {
			for (int j = 0; j < enemies.length; j++) {
				if (signs[i].equals(enemies[j].substring(0, 1))) {
					birth_table.put("$" + against + signs[i], enemies[j]
							.substring(2, 3));
				}
			}
			for (int j = 0; j < helpers.length; j++) {
				if (signs[i].equals(helpers[j].substring(0, 1))) {
					birth_table.put("$" + strengthen + signs[i], helpers[j]
							.substring(2, 3));
				}
			}
		}
		Position[] positions = setSign(cal, birth_table, "", signs, sign_pos,
				stellar_sign_pos);
		if (positions == null || positions.length != 11)
			return;
		int lower = Position.getPositionIndex(positions, positions.length,
				ChartData.VENUS);
		for (;; lower--) {
			Position pos = Position.getPosition(positions, positions.length,
					lower - 1);
			int val = pos.getIndex();
			if (val != ChartData.SUN
					&& (val < ChartData.VENUS || val > ChartData.SATURN))
				break;
		}
		int upper = lower + 5;
		boolean around_sun = false;
		for (; lower < upper; lower++) {
			Position pos = Position.getPosition(positions, positions.length,
					lower);
			int val = pos.getIndex();
			if (val < ChartData.VENUS || val > ChartData.SATURN) {
				if (val == ChartData.SUN) {
					around_sun = true;
					upper++;
				} else {
					break;
				}
			}
		}
		if (lower == upper) {
			birth_table.put("?__sp" + (around_sun ? 5 : 4), "t");
			if (!around_sun) {
				Position pos = Position.getPosition(positions,
						positions.length, lower - 1);
				int val = pos.getIndex();
				if (val == ChartData.SUN)
					birth_table.put("?__sp" + 2, "t");
				else if (val == ChartData.MOON)
					birth_table.put("?__sp" + 3, "t");
			}
		}
		lower = Position.getPositionIndex(positions, positions.length,
				ChartData.SUN);
		for (;; lower--) {
			Position pos = Position.getPosition(positions, positions.length,
					lower - 1);
			int val = pos.getIndex();
			if (val > ChartData.SATURN)
				break;
		}
		upper = lower + 7;
		for (; lower < upper; lower++) {
			Position pos = Position.getPosition(positions, positions.length,
					lower);
			int val = pos.getIndex();
			if (val > ChartData.SATURN)
				break;
		}
		if (lower == upper)
			birth_table.put("?__sp" + 1, "t");
		lower = Position.getPositionIndex(positions, positions.length,
				ChartData.TRUE_NODE);
		for (;; lower--) {
			Position pos = Position.getPosition(positions, positions.length,
					lower - 1);
			int val = pos.getIndex();
			if (val != ChartData.MOON
					&& (val < ChartData.TRUE_NODE || val > ChartData.MEAN_APOG))
				break;
		}
		upper = lower + 5;
		for (; lower < upper; lower++) {
			Position pos = Position.getPosition(positions, positions.length,
					lower);
			int val = pos.getIndex();
			if (val != ChartData.MOON
					&& (val < ChartData.TRUE_NODE || val > ChartData.MEAN_APOG))
				break;
		}
		if (lower == upper)
			birth_table.put("?__sp" + 6, "t");
	}

	public void setNowSign(Calculate cal, String[] signs, double[] sign_pos,
			double[] stellar_sign_pos) {
		setSign(cal, now_table, Resource.getString("current_date").substring(0,
				1), signs, sign_pos, stellar_sign_pos);
	}

	public Position[] setSign(Calculate cal, Hashtable table, String prefix,
			String[] signs, double[] sign_pos, double[] stellar_sign_pos) {
		String[] directions = Resource.getStringArray("directions");
		String alone = Resource.getString("alone");
		String before = Resource.getString("before");
		String after = Resource.getString("after");
		String none = Resource.getString("none");
		String year_sign_key = Resource.getString("year_sign_key");
		String zodiac_house = Resource.getString("zodiac_house");
		String degree = Resource.getString("degree");
		double boundary_range = Resource.getDouble("boundary_range");
		LinkedList head = new LinkedList();
		for (int i = 0; i < signs.length; i++) {
			double pos = sign_pos[i];
			if (!Calculate.isValid(pos))
				continue;
			table.put("$" + prefix + signs[i] + degree, FileIO.formatDouble(
					sign_pos[i], 1, 2, false, false));
			double d_val = pos % 30.0;
			if (d_val < boundary_range || d_val > 30.0 - boundary_range) {
				birth_table.put("?" + prefix + signs[i]
						+ Resource.getString("zodiac_boundary"), "t");
			}
			if (withinSignBoundary(pos, stellar_sign_pos, boundary_range)) {
				birth_table.put("?" + prefix + signs[i]
						+ Resource.getString("stellar_boundary"), "t");
			}
			String h_val = cal.getZodiac(pos, true);
			String s_val = cal.getStarSign(pos, stellar_sign_pos,
					full_stellar_signs);
			table.put("@" + prefix + signs[i], h_val);
			table.put("%" + prefix + signs[i], s_val);
			if (i <= ChartData.MEAN_APOG && i != ChartData.URANUS
					&& i != ChartData.NEPTUNE && i != ChartData.PLUTO) {
				int n = (int) (pos / 30.0);
				table.put("?" + prefix + signs[i] + directions[n / 3], "t");
				head.add(new Position(signs[i], i, pos, Calculate.SPEED_NORMAL,
						false));
				LinkedHashSet set = (LinkedHashSet) table.get("$" + prefix
						+ h_val.substring(0, 1) + zodiac_house + year_sign_key);
				set.add(signs[i]);
				set = (LinkedHashSet) table.get("$" + prefix
						+ s_val.substring(0, 1) + degree + year_sign_key);
				set.add(signs[i]);
			}
		}
		if (head.isEmpty())
			return null;
		Position[] positions = (Position[]) head.toArray(new Position[1]);
		if (positions.length > 1) {
			Arrays.sort(positions, 0, positions.length, new Comparator() {
				public int compare(Object a, Object b) {
					double p_a = ((Position) a).getLocation();
					double p_b = ((Position) b).getLocation();
					if (p_a < p_b)
						return -1;
					else if (p_a > p_b)
						return 1;
					else
						return 0;
				}
			});
		}
		for (int i = 0; i < positions.length; i++) {
			Position pos = positions[i];
			Position p_pos = Position.getPosition(positions, positions.length,
					i - 1);
			Position n_pos = Position.getPosition(positions, positions.length,
					i + 1);
			int i_pos = (int) (pos.getLocation() / 30.0);
			int gap = i_pos - (int) (p_pos.getLocation() / 30.0);
			if (gap < 0)
				gap += 12;
			table.put("$" + prefix + pos.getName() + after, (gap < 2) ? p_pos
					.getName() : none);
			gap = (int) (n_pos.getLocation() / 30.0) - i_pos;
			if (gap < 0)
				gap += 12;
			table.put("$" + prefix + pos.getName() + before, (gap < 2) ? n_pos
					.getName() : none);
			String pos_sign = (String) table.get("@" + prefix + pos.getName());
			if (p_pos != pos
					&& pos_sign.equals((String) table.get("@" + prefix
							+ p_pos.getName()))) {
				continue;
			}
			if (n_pos != pos
					&& pos_sign.equals((String) table.get("@" + prefix
							+ n_pos.getName()))) {
				continue;
			}
			table.put("?" + prefix + alone + pos.getName(), "t");
		}
		return positions;
	}

	public void setBirthStarSign(Hashtable t_table, Hashtable master_table,
			LinkedList ten_god_list, String[] signs, String[] star_equ_map,
			String year_info) {
		setStarSign(t_table, birth_table, master_table, ten_god_list, signs,
				star_equ_map, year_info, false);
	}

	public void setNowStarSign(Hashtable t_table, Hashtable master_table,
			LinkedList ten_god_list, String[] signs, String[] star_equ_map,
			String year_info) {
		setStarSign(t_table, now_table, master_table, ten_god_list, signs,
				star_equ_map, year_info, true);
	}

	public void setStarSign(Hashtable t_table, Hashtable table,
			Hashtable master_table, LinkedList ten_god_list, String[] signs,
			String[] star_equ_map, String year_info, boolean now) {
		String sep = Resource.getString("year_info_sep");
		String star_sign_key = Resource.getString("star_sign_key");
		String year_sign_key = Resource.getString("year_sign_key");
		String zodiac_house = Resource.getString("zodiac_house");
		String degree = Resource.getString("degree");
		String prefix = now ? Resource.getString("current_date")
				.substring(0, 1) : "";
		for (int i = 0; i < 28; i++) {
			String key = full_stellar_signs[i].substring(0, 1);
			table.put("$" + prefix + key + degree + year_sign_key,
					new LinkedHashSet());
		}
		for (int i = 0; i < 12; i++) {
			String key = full_zodiac[i].substring(0, 1);
			table.put("$" + prefix + key + zodiac_house + year_sign_key,
					new LinkedHashSet());
			LinkedList head = (LinkedList) t_table.get(key);
			if (head == null)
				continue;
			LinkedList star_list = new LinkedList();
			for (ListIterator it = head.listIterator(); it.hasNext();) {
				String star = (String) it.next();
				if (data.inMasterTable(star, now)) {
					table.put("@" + prefix + star, full_zodiac[i]);
					star_list.add(star);
				}
			}
			setListSetToTable(table, "$" + prefix + key + zodiac_house
					+ star_sign_key, star_list);
		}
		for (int i = 0; i < star_equ_map.length; i += 2) {
			String value = (String) table.get("@" + prefix + star_equ_map[i]);
			if (value != null) {
				table.put("@" + prefix + star_equ_map[i + 1], value);
			} else {
				value = (String) table.get("@" + prefix + star_equ_map[i + 1]);
				if (value != null)
					table.put("@" + prefix + star_equ_map[i], value);
			}
		}
		if (year_info == null)
			return;
		year_info = year_info.substring(year_info.indexOf("|")).replaceAll(" ",
				"");
		String str = sep.substring(1, 2) + Resource.getString("none")
				+ sep.substring(2, 3);
		year_info = year_info.replaceAll(sep.substring(1), str);
		StringTokenizer st = new StringTokenizer(year_info, sep);
		while (st.hasMoreTokens()) {
			String key = st.nextToken();
			String value = st.nextToken();
			if (value.length() == 3)
				value = value.substring(0, 1) + value.substring(2);
			saveStringSetToTable(table, "$" + prefix + key, value, 1);
		}
		String change_to = Resource.getString("change_to");
		for (int i = 0; i < signs.length; i++) {
			LinkedList list = (LinkedList) t_table
					.get(signs[i] + star_sign_key);
			if (list == null)
				continue;
			for (int j = 0; j < 2 && !list.isEmpty(); j++) {
				String val = (String) list.getFirst();
				if (ten_god_list.contains(val)) {
					list.removeFirst();
					table.put("$" + prefix + signs[i] + change_to, val);
				}
			}
			setListSetToTable(table, "$" + prefix + signs[i] + star_sign_key,
					list);
		}
	}

	private void saveStringSetToTable(Hashtable t_table, String key,
			String value, int width) {
		if (value.length() / width > 1) {
			LinkedHashSet set = new LinkedHashSet();
			for (int j = 0; j < value.length(); j += width)
				set.add(value.substring(j, j + width));
			t_table.put(key, set);
		} else {
			t_table.put(key, value);
		}
	}

	private void setListSetToTable(Hashtable t_table, String key,
			LinkedList list) {
		LinkedHashSet set = new LinkedHashSet();
		set.addAll(list);
		t_table.put(key, set);
	}

	public void initOptions(BaseTab tab) {
		out = tab;
		if (out == null || !RuleEntry.hasRuleEntry(true))
			return;
		out.clear();
		RuleEntry.setOutputTab(out);
		RuleEntry.restoreTable(true);
		year_offset_start = year_offset_end = 0;
		String str = RuleEntry.getOutput("option");
		if (str != null) {
			StringTokenizer st = new StringTokenizer(
					str.replaceAll(" |\t", ""), Resource.getString(","));
			while (st.hasMoreTokens()) {
				StringTokenizer n_st = new StringTokenizer(st.nextToken(), "=");
				if (n_st.countTokens() != 2)
					continue;
				String key = n_st.nextToken();
				String value = n_st.nextToken();
				if (key.equals("year_offset")) {
					StringTokenizer v_st = new StringTokenizer(value, ":");
					if (v_st.countTokens() == 2) {
						year_offset_start = FileIO.parseInt(v_st.nextToken(),
								0, false);
						year_offset_end = FileIO.parseInt(v_st.nextToken(), 0,
								false);
					}
				} else if (key.equals("wrap")) {
					out.setWrapMode(!value.equals("0"));
				}
			}
		}
		list_variable = show_all_failure = show_failure = false;
		str = RuleEntry.getOutput("debug");
		if (str == null)
			return;
		StringTokenizer st = new StringTokenizer(str.replaceAll(" |\t", ""),
				",");
		while (st.hasMoreTokens()) {
			StringTokenizer n_st = new StringTokenizer(st.nextToken(), "=");
			if (n_st.countTokens() != 2)
				continue;
			String key = n_st.nextToken();
			String value = n_st.nextToken();
			if (key.equals("list_variable")) {
				list_variable = !value.equals("0");
			} else if (key.equals("show_all_failure")) {
				show_all_failure = !value.equals("0");
			} else if (key.equals("show_failure")) {
				show_failure = !value.equals("0");
			} else if (key.equals("trace_rule")) {
				RuleEntry.setTrace(value);
			} else if (key.equals("trace_variable")) {
				RuleEntry.setTraceVariable(value);
			} else if (key.equals("trace_rank")) {
				RuleEntry.setTraceLevelRank(RuleEntry.getLevelRank(value));
			}
		}
	}

	private void listStateTable(Hashtable table, String name) {
		if (out == null || !RuleEntry.hasRuleEntry(true))
			return;
		out
				.appendLine("--- begin predefined " + name
						+ " variable listing ---");
		Vector vector = new Vector(table.keySet());
		Collections.sort(vector);
		String str = "  ";
		int n = 0;
		// simple variables
		String space = Resource.getString("non_white_space");
		for (Iterator it = vector.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object object = table.get(key);
			if (object instanceof String) {
				String val = (String) object;
				if (val.equals("\t"))
					val = "tab";
				else if (val.equals("\n"))
					val = "newline";
				else if (val.startsWith(space))
					val = val.length() + " space";
				String value = FileIO.formatString(key + "=" + val, 7);
				str += value;
				n++;
				if (n >= 6) {
					out.appendLine(str);
					str = "  ";
					n = 0;
				}
			}
		}
		if (n > 0)
			out.appendLine(str);
		// set variables
		for (Iterator it = vector.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object object = table.get(key);
			if (object instanceof LinkedHashSet) {
				RuleEntry.showSetContent("  " + key + "=", object, 12);
			}
		}
		out.appendLine("--- end predefined " + name + " variable listing ---");
		out.appendLine();
	}

	public int getYearOffsetStart() {
		return year_offset_start;
	}

	public int getYearOffsetEnd() {
		return year_offset_end;
	}

	public void computeStyles() {
		RuleEntry.setDebugOption(show_all_failure);
		RuleEntry.restoreTable(false);
		int[] style_range = Resource.getIntArray("style_range");
		int level = style_range[1] - Resource.getPrefInt("style_level");
		RuleEntry.setRuleLevel(level, Resource.getPrefInt("fill_max_styles"));
		if (list_variable)
			listStateTable(birth_table, Resource.getString("birth_date"));
		RuleEntry.computeRules(good, bad);
	}

	public void ruleHeader() {
		RuleEntry.restoreTable(true);
		String str = RuleEntry.getOutput("header");
		if (str != null)
			out.appendLine(str);
	}

	public void ruleFooter() {
		String str = RuleEntry.getOutput("footer");
		if (str != null)
			out.appendLine(str);
	}

	public void computeRules() {
		RuleEntry.setDebugOption(show_all_failure || show_failure);
		LinkedList r_good = new LinkedList();
		LinkedList r_bad = new LinkedList();
		RuleEntry.setRuleLevel(0, Integer.MAX_VALUE);
		if (list_variable && now_table != null)
			listStateTable(now_table, Resource.getString("current_date"));
		RuleEntry.computeRules(r_good, r_bad);
		for (ListIterator iter = r_good.listIterator(); iter.hasNext();)
			out.appendLine((String) iter.next());
		for (ListIterator iter = r_bad.listIterator(); iter.hasNext();)
			out.appendLine((String) iter.next());
	}

	public LinkedList getGoodStyles() {
		return good;
	}

	public LinkedList getBadStyles() {
		return bad;
	}
}
