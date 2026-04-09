//
// Moira - A Chinese Astrology Charting Program
// Copyright (C) 2004-2015 At Home Projects
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package org.athomeprojects.base;

import java.util.Arrays;

public class DataEntry {
	static private final int SHORT_DESC_MIN_LENGTH = 10;

	static private int now_field_diff;

	private final int SHORT_NOTE_LINE = 5;

	private final int LINE_WRAP_LENGTH = 30;

	private final int SHORT_NOTE_LENGTH = SHORT_NOTE_LINE * LINE_WRAP_LENGTH;

	private boolean selected, sex, choice;

	private String name, country, city, zone, mountain_pos, override, note;

	private int[] birth_day, now_day;

	public DataEntry() {
		sex = true;
	}

	public String getName() {
		return (name == null) ? "" : name;
	}

	public void setName(String str) {
		name = str;
	}

	public boolean getSex() {
		return sex;
	}

	public void setSex(boolean set) {
		sex = set;
	}

	public boolean getChoice() {
		return choice;
	}

	public void setChoice(boolean set) {
		choice = set;
	}

	public String getMountainPos() {
		return mountain_pos;
	}

	public void setMountainPos(String val) {
		mountain_pos = val;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean yes) {
		selected = yes;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String str) {
		country = str;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String str) {
		city = str;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String str) {
		zone = str;
	}

	public String getOverride() {
		return override;
	}

	public void setOverride(String str) {
		override = str;
	}

	public String getNote(boolean full) {
		if (full || note == null)
			return note;
		String str = note.replaceAll("\t", "    ");
		if (str.length() > SHORT_NOTE_LENGTH)
			str = str.substring(0, SHORT_NOTE_LENGTH - 3);
		String f_str = "";
		int count = 0;
		for (int i = 0; i < SHORT_NOTE_LINE; i++) {
			int index = str.indexOf("\r");
			if (index < 0)
				index = str.indexOf("\n");
			if (index < 0) {
				if (str.trim().equals("")) {
					count++;
					continue;
				}
				index = str.length();
			}
			boolean wrap = false;
			if (index > LINE_WRAP_LENGTH + 5) {
				index = LINE_WRAP_LENGTH;
				wrap = true;
			}
			count++;
			if (!f_str.equals(""))
				f_str += "\r";
			f_str += str.substring(0, index);
			str = str.substring(index);
			if (!wrap) {
				if (str.startsWith("\r"))
					str = str.substring(1);
				if (str.startsWith("\n"))
					str = str.substring(1);
			}
		}
		if (f_str.equals(""))
			f_str = str;
		if (count > 1)
			f_str += "...";
		return f_str;
	}

	public void setNote(String str) {
		if (str != null && str.trim().equals(""))
			note = null;
		else
			note = str;
	}

	public int[] getBirthDay() {
		return (birth_day != null) ? ((int[]) birth_day.clone()) : null;
	}

	public int[] getBirthDayDirect() {
		return birth_day;
	}

	public void setBirthDay(int[] date) {
		birth_day = (date != null) ? ((int[]) date.clone()) : null;
	}

	public int[] getNowDay() {
		return (now_day != null) ? ((int[]) now_day.clone()) : null;
	}

	public void setNowDay(int[] date) {
		now_day = (date != null) ? ((int[]) date.clone()) : null;
	}

	public boolean isValid() {
		return birth_day != null && country != null && city != null
				&& zone != null;
	}

	public String packEntry(int type) {
		String[] map = DataSet.getMapString();
		DataSet.setMapString(null);
		FileIO string_io = new FileIO(null, false, true);
		DataSet.saveDataEntry(string_io, this, "", "", type);
		String data = string_io.getDataInString();
		string_io.dispose();
		DataSet.setMapString(map);
		return data;
	}

	public boolean unpackEntry(String data, int type) {
		String[] map = DataSet.getMapString();
		DataSet.setMapString(null);
		FileIO string_io = new FileIO(data, true);
		boolean success = DataSet.loadDataEntry(string_io, this, "", "", type);
		string_io.dispose();
		DataSet.setMapString(map);
		return success;
	}

	public boolean equals(DataEntry entry, boolean basic) {
		now_field_diff = -1;
		if (sex != entry.sex || !sameString(name, entry.name)
				|| !samePlace(entry) || !sameDate(birth_day, entry.birth_day))
			return false;
		if (basic)
			return true;
		if (choice != entry.choice
				|| !sameString(mountain_pos, entry.mountain_pos)
				|| !sameString(override, entry.override)
				|| !sameString(note, entry.note))
			return false;
		now_field_diff = sameDate(now_day, entry.now_day) ? 0 : 1;
		return now_field_diff == 0;
	}

	static public boolean nowFieldDifferOnly() {
		return now_field_diff == 1;
	}

	public boolean samePlace(DataEntry entry) {
		return sameString(country, entry.country)
				&& sameString(city, entry.city) && sameString(zone, entry.zone);
	}

	public boolean sameNote(String str) {
		if (str != null && str.trim().equals(""))
			str = null;
		return sameString(note, str);
	}

	static public boolean sameString(String a, String b) {
		if (a == null || b == null)
			return a == b;
		else
			return a.equals(b);
	}

	static public boolean sameDate(int[] a, int[] b) {
		if (a == null || b == null)
			return a == b;
		else
			return Arrays.equals(a, b);
	}

	static public String getOneLineDesc(String str, int width, boolean always) {
		if (str == null)
			return null;
		int r_index = str.indexOf("\r");
		if (r_index < 0)
			r_index = str.indexOf("\n");
		if (r_index < 0)
			r_index = Integer.MAX_VALUE;
		if (width > SHORT_DESC_MIN_LENGTH)
			r_index = Math.min(r_index, width - 3);
		if (r_index >= str.trim().length())
			return str;
		else if (!always && r_index <= width)
			return str.substring(0, r_index).trim();
		else
			return str.substring(0, r_index) + "...";
	}
}