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

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Point;

public class DiagramTip {
	static private final double SCALER = 1000000.0;

	static private final int TWO_PI_SCALED = (int) (SCALER * 2.0 * Math.PI);

	static private final double TWO_PI_MINUS = 2.0 * Math.PI - 0.000001;

	private final double RADIAN = 360.0 / (2.0 * Math.PI);

	private final int INIT_TIP_COUNT = 3;

	private final int DEGREE_MARKER = -1000;

	private final int INVALID_INT = Integer.MIN_VALUE;

	private final double INVALID_DOUBLE = Double.MIN_VALUE;

	private int center_x, center_y, radius, init_tip_count = 0;

	private double scaler;

	private boolean enabled = true, need_update = true;

	private Entry last_entry, next_tip = new Entry();

	private LinkedList region, planet;

	private class Entry {
		int planet_no;

		boolean birth_data, rectangle;

		double l_radius2, u_radius2, l_radian, u_radian;

		int int_data;

		double double_data;

		String init_tip, tip;

		public Entry() {
			int_data = INVALID_INT;
			double_data = INVALID_DOUBLE;
		}
	}

	public void reset() {
		region = new LinkedList();
		planet = new LinkedList();
		clearNextTip();
		enabled = need_update = true;
	}

	public void init(int[] size, boolean enable) {
		enabled = enable;
		if (!enabled)
			return;
		center_x = size[0];
		center_y = size[1];
		radius = size[2];
		scaler = 1.0;
		region = new LinkedList();
		clearNextTip();
	}

	public void setTipScale(int[] data) {
		center_x = data[0];
		center_y = data[1];
		scaler = (double) radius / data[2];
	}

	public void resetInitCount() {
		init_tip_count = 0;
	}

	public boolean getNeedUpdate() {
		return need_update;
	}

	public void setNeedUpdate(boolean set) {
		need_update = set;
	}

	public String formatTip(String tip, String indent, int max_len) {
		if (tip.length() <= max_len)
			return tip;
		String break_char = Resource.getString("break_char");
		StringTokenizer st = new StringTokenizer(tip, break_char);
		tip = "";
		int cur_len = 0;
		while (st.hasMoreTokens()) {
			String str = st.nextToken().trim();
			int len = str.length();
			if (len == 0)
				continue;
			if (cur_len > 0 && cur_len + len + 2 > max_len) {
				tip += "\n" + indent + str;
				cur_len = indent.length() + len;
			} else {
				if (!tip.equals(""))
					tip += break_char.substring(0, 1) + " ";
				tip += str;
				cur_len += len + 2;
			}
		}
		return tip;
	}

	public void addTip(int planet_no, boolean birth_data, String tip) {
		if (!enabled)
			return;
		Entry entry = getTipEntry(planet_no, birth_data);
		if (entry == null) {
			entry = new Entry();
			entry.planet_no = planet_no;
			entry.birth_data = birth_data;
			planet.add(entry);
			checkAddNextTip(entry);
		}
		entry.tip = tip;
	}

	public String getTip(int planet_no, boolean birth_data) {
		Entry entry = getTipEntry(planet_no, birth_data);
		return (entry != null) ? entry.tip : null;
	}

	public void addTip(double lower_radius, double upper_radius,
			double lower_radian, double upper_radian, int planet_no,
			boolean birth_data) {
		if (!enabled)
			return;
		Entry entry = getTipEntry(planet_no, birth_data);
		if (entry != null) {
			addTip(lower_radius, upper_radius, lower_radian, upper_radian,
					birth_data, planet_no + 1, entry.tip);
		}
	}

	public void addTip(double lower_radius, double upper_radius,
			double lower_radian, double upper_radian, boolean birth_data,
			int planet_no, String tip) {
		if (!enabled)
			return;
		Entry entry = new Entry();
		entry.l_radius2 = lower_radius * lower_radius;
		entry.u_radius2 = upper_radius * upper_radius;
		lower_radian = boundRadian(lower_radian);
		upper_radian = boundRadian(upper_radian);
		if (upper_radian < lower_radian) {
			// cross from 360 to 0 degree
			double bound = 2.0 * Math.PI;
			if (upper_radian < bound) {
				entry.l_radian = lower_radian;
				entry.u_radian = bound;
				entry.birth_data = birth_data;
				entry.planet_no = planet_no;
				entry.tip = tip;
				checkAddNextTip(entry);
				region.addFirst(entry);
				entry = new Entry();
				entry.l_radius2 = lower_radius * lower_radius;
				entry.u_radius2 = upper_radius * upper_radius;
			}
			lower_radian = 0.0;
		}
		if (lower_radian < upper_radian) {
			entry.l_radian = lower_radian;
			entry.u_radian = upper_radian;
			entry.birth_data = birth_data;
			entry.planet_no = planet_no;
			entry.tip = tip;
			checkAddNextTip(entry);
			region.addFirst(entry);
		}
	}

	public void addDataToNextTip(int val, String init_tip) {
		if (next_tip == null)
			next_tip = new Entry();
		next_tip.int_data = val;
		next_tip.double_data = INVALID_DOUBLE;
		next_tip.init_tip = init_tip;
	}

	public void addDataToNextTip(double val, String init_tip) {
		if (next_tip == null)
			next_tip = new Entry();
		next_tip.double_data = val;
		next_tip.int_data = INVALID_INT;
		next_tip.init_tip = init_tip;
	}

	public void clearNextTip() {
		next_tip.int_data = INVALID_INT;
		next_tip.double_data = INVALID_DOUBLE;
		next_tip.init_tip = null;
	}

	private void checkAddNextTip(Entry entry) {
		entry.int_data = next_tip.int_data;
		entry.double_data = next_tip.double_data;
		entry.init_tip = next_tip.init_tip;
	}

	public void addDegreeMarkerToNextTip(String init_tip) {
		addDataToNextTip(DEGREE_MARKER, init_tip);
	}

	public void addTip(double lower_radius, double upper_radius, boolean first,
			String tip) {
		if (!enabled)
			return;
		Entry entry = new Entry();
		entry.l_radius2 = lower_radius * lower_radius;
		entry.u_radius2 = upper_radius * upper_radius;
		entry.l_radian = 0.0;
		entry.u_radian = TWO_PI_MINUS;
		entry.tip = tip;
		checkAddNextTip(entry);
		if (first)
			region.addFirst(entry);
		else
			region.addLast(entry);
	}

	public void addTip(int lx, int ly, int width, int height, boolean first,
			String tip) {
		if (!enabled)
			return;
		Entry entry = new Entry();
		entry.rectangle = true;
		entry.l_radian = lx;
		entry.u_radian = ly;
		entry.l_radius2 = lx + width;
		entry.u_radius2 = ly + height;
		entry.tip = tip;
		checkAddNextTip(entry);
		if (first)
			region.addFirst(entry);
		else
			region.addLast(entry);
	}

	public int getIntFromLastPoint() {
		return (last_entry != null) ? last_entry.int_data : INVALID_INT;
	}

	public double getDoubleFromLastPoint() {
		return (last_entry != null) ? last_entry.double_data : INVALID_DOUBLE;
	}

	public int getIntFromPoint(int x, int y) {
		if (hasDataFromPoint(x, y))
			return getIntFromLastPoint();
		else
			return INVALID_INT;
	}

	public double getDoubleFromPoint(int x, int y) {
		if (hasDataFromPoint(x, y))
			return getDoubleFromLastPoint();
		else
			return INVALID_DOUBLE;
	}

	public boolean hasDataFromPoint(int x, int y) {
		double d_x = scaler * (x - center_x);
		double d_y = scaler * (y - center_y);
		double radius2 = d_x * d_x + d_y * d_y;
		double radian = Math.atan2(-d_y, d_x);
		radian = boundRadian(radian);
		last_entry = null;
		for (ListIterator iter = region.listIterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (entry.rectangle)
				continue;
			if (radius2 >= entry.l_radius2 && radius2 < entry.u_radius2
					&& radian >= entry.l_radian && radian < entry.u_radian) {
				if (entry.int_data != INVALID_INT
						|| entry.double_data != INVALID_DOUBLE) {
					last_entry = entry;
					return true;
				}
			}
		}
		return false;
	}

	public double getDegreeFromPoint(int x, int y, boolean radius_check) {
		double d_x = scaler * (x - center_x);
		double d_y = scaler * (y - center_y);
		double radius2 = d_x * d_x + d_y * d_y;
		double radian = Math.atan2(-d_y, d_x);
		radian = boundRadian(radian);
		if (radius_check) {
			return (last_entry != null && radius2 >= last_entry.l_radius2 && radius2 < last_entry.u_radius2) ? (radian * RADIAN)
					: INVALID_DOUBLE;
		}
		for (ListIterator iter = region.listIterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (entry.rectangle)
				continue;
			if (radius2 >= entry.l_radius2 && radius2 < entry.u_radius2
					&& radian >= entry.l_radian && radian < entry.u_radian) {
				return (entry.int_data == DEGREE_MARKER) ? (radian * RADIAN)
						: INVALID_DOUBLE;
			}
		}
		return INVALID_DOUBLE;
	}

	public int getPlanetFromPoint(int x, int y) {
		double d_x = scaler * (x - center_x);
		double d_y = scaler * (y - center_y);
		double radius2 = d_x * d_x + d_y * d_y;
		double radian = Math.atan2(-d_y, d_x);
		radian = boundRadian(radian);
		for (ListIterator iter = region.listIterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (entry.rectangle)
				continue;
			if (radius2 >= entry.l_radius2 && radius2 < entry.u_radius2
					&& radian >= entry.l_radian && radian < entry.u_radian
					&& entry.planet_no > 0) {
				last_entry = entry;
				return entry.planet_no - 1;
			}
		}
		last_entry = null;
		return -1;
	}

	public boolean isBirthPlanet() {
		return last_entry != null && last_entry.birth_data;
	}

	public Point getCenterPoint() {
		if (last_entry == null || last_entry.rectangle)
			return null;
		double radian = 0.5 * (last_entry.l_radian + last_entry.u_radian);
		double rad = 0.5
				* (Math.sqrt(last_entry.l_radius2) + Math
						.sqrt(last_entry.u_radius2)) / scaler;
		return new Point((int) (center_x + rad * Math.cos(radian)),
				(int) (center_y + rad * Math.sin(-radian)));
	}

	public boolean isIntValid(int val) {
		return val != INVALID_INT;
	}

	public boolean isDoubleValid(double val) {
		return val != INVALID_DOUBLE;
	}

	public String getTipFromPoint(int x, int y) {
		double d_x = scaler * (x - center_x);
		double d_y = scaler * (y - center_y);
		double radius2 = d_x * d_x + d_y * d_y;
		double radian = Math.atan2(-d_y, d_x);
		radian = boundRadian(radian);
		for (ListIterator iter = region.listIterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (entry.rectangle) {
				if (d_x >= entry.l_radian && d_y >= entry.u_radian
						&& d_x <= entry.l_radius2 && d_y <= entry.u_radius2) {
					if (entry.init_tip != null
							&& (entry.int_data != INVALID_INT || entry.double_data != INVALID_DOUBLE)
							&& entry.int_data != DEGREE_MARKER
							&& ++init_tip_count <= INIT_TIP_COUNT) {
						return entry.tip + "\n\n" + entry.init_tip;
					} else {
						return entry.tip;
					}
				}
			} else if (radius2 >= entry.l_radius2 && radius2 < entry.u_radius2
					&& radian >= entry.l_radian && radian < entry.u_radian) {
				if (entry.init_tip != null
						&& (entry.int_data != INVALID_INT || entry.double_data != INVALID_DOUBLE)
						&& entry.int_data != DEGREE_MARKER
						&& ++init_tip_count <= INIT_TIP_COUNT) {
					return entry.tip + "\n\n" + entry.init_tip;
				} else {
					return entry.tip;
				}
			}
		}
		return null;
	}

	private Entry getTipEntry(int planet_no, boolean birth_data) {
		for (ListIterator iter = planet.listIterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			if (planet_no == entry.planet_no && birth_data == entry.birth_data)
				return entry;
		}
		return null;
	}

	private double boundRadian(double radian) {
		int val = (int) (SCALER * radian);
		while (val >= TWO_PI_SCALED)
			val -= TWO_PI_SCALED;
		while (val < 0)
			val += TWO_PI_SCALED;
		return val / SCALER;
	}
}