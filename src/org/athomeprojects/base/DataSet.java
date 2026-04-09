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

public class DataSet {
	static public final int DATA = 0;

	static public final int PICK = 1;

	static public final int MAX_TYPE = 2;

	static public final String STYLE_MARKER = new String(new char[] { 6 });

	static private final String[] ALTERNATE_MAP = { "@a", "@b", "@c", "@d",
			"@e", "@f" };

	static private String[] special_map;

	private String footer;

	private int[] last_index, max_entry;

	private DataEntry[][] entries;

	public DataSet() {
		last_index = new int[MAX_TYPE];
		last_index[DATA] = last_index[PICK] = -1;
		max_entry = new int[MAX_TYPE];
		entries = new DataEntry[MAX_TYPE][];
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String val) {
		footer = val;
	}

	public boolean hasDataEntry(int index, int type) {
		return entries[type][index] != null;
	}

	public DataEntry getDataEntry(int index, int type) {
		if (entries[type][index] == null)
			entries[type][index] = new DataEntry();
		return entries[type][index];
	}

	public void setDataEntry(int index, DataEntry entry, int type) {
		entries[type][index] = entry;
	}

	public int getMaxDataEntry(int type) {
		return max_entry[type];
	}

	public void setMaxDataEntry(int val, int type) {
		max_entry[type] = val;
		if (entries[type] != null) {
			if (entries[type].length >= val)
				return;
			DataEntry[] new_entries = new DataEntry[val];
			for (int i = 0; i < entries[type].length; i++)
				new_entries[i] = entries[type][i];
			entries[type] = new_entries;
		} else {
			entries[type] = new DataEntry[val];
		}
	}

	public int getLastIndex(int type) {
		return last_index[type];
	}

	public void setLastIndex(int index, int type) {
		last_index[type] = index;
	}

	static public String[] getMapString() {
		return special_map;
	}

	static public void setMapString(String[] map) {
		special_map = map;
	}

	static public void setAlternateMapString() {
		special_map = ALTERNATE_MAP;
	}

	static public String removeStyle(String str) {
		int index = str.indexOf(STYLE_MARKER);
		if (index >= 0)
			str = str.substring(0, index);
		return str;
	}

	static private String mapString(String str) {
		if (special_map == null) {
			str = str.replaceAll("\"", new String(new char[] { 1 }));
			str = str.replaceAll("\r", new String(new char[] { 2 }));
			str = str.replaceAll("\n", new String(new char[] { 3 }));
			str = str.replaceAll("=", new String(new char[] { 4 }));
			str = str.replaceAll("#", new String(new char[] { 5 }));
		} else {
			str = str.replaceAll("\"", special_map[0]);
			str = str.replaceAll("\r", special_map[1]);
			str = str.replaceAll("\n", special_map[2]);
			if (special_map.length > 3) {
				str = str.replaceAll("=", special_map[3]);
				str = str.replaceAll("#", special_map[4]);
				if (special_map.length > 5)
					str = str.replaceAll(STYLE_MARKER, special_map[5]);
			}
		}
		return Character.isWhitespace(str.codePointAt(0)) ? (special_map[3] + str)
				: str;
	}

	static private String unmapString(String str) {
		if (special_map == null) {
			str = str.replaceAll(new String(new char[] { 1 }), "\"");
			str = str.replaceAll(new String(new char[] { 2 }), "\r");
			str = str.replaceAll(new String(new char[] { 3 }), "\n");
			str = str.replaceAll(new String(new char[] { 4 }), "=");
			str = str.replaceAll(new String(new char[] { 5 }), "#");
		} else {
			str = str.replaceAll(special_map[0], "\"");
			str = str.replaceAll(special_map[1], "\r");
			str = str.replaceAll(special_map[2], "\n");
			if (special_map.length > 3) {
				str = str.replaceAll(special_map[3], "=");
				str = str.replaceAll(special_map[4], "#");
				if (special_map.length > 5)
					str = str.replaceAll(special_map[5], STYLE_MARKER);
			}
		}
		return str.startsWith("=") ? str.substring(1) : str;
	}

	public boolean loadData(String file_name) {
		FileIO file = new FileIO(file_name, false);
		setMapString(file.hasKey("map_seq") ? file.getStringArray("map_seq")
				: null);
		if (file.hasKey("desc"))
			setFooter(unmapString(file.getString("desc")));
		Resource.setAlternatePref(file.hasKey("pref") ? file.getString("pref")
				: null);
		boolean has_data = false;
		for (int iter = 0; iter < MAX_TYPE; iter++) {
			String prefix = "";
			if (iter > 0)
				prefix = "t" + Integer.toString(iter) + "_";
			String key = "max_entry";
			if (iter > 0)
				key = prefix + key;
			max_entry[iter] = file.hasKey(key) ? file.getInt(key) : 1;
			key = "index";
			if (iter > 0)
				key = prefix + key;
			if (max_entry[iter] > 1 && file.hasKey(key))
				last_index[iter] = file.getInt(key);
			entries[iter] = new DataEntry[max_entry[iter]];
			boolean has_empty = false;
			for (int i = 0; i < max_entry[iter]; i++) {
				String suffix = (i > 0) ? Integer.toString(i) : "";
				DataEntry entry = getDataEntry(i, iter);
				if (!loadDataEntry(file, entry, prefix, suffix, iter)) {
					entries[iter][i] = null;
					has_empty = true;
				}
			}
			if (has_empty) {
				int count = -1;
				for (int i = 0; i < max_entry[iter]; i++) {
					if (entries[iter][i] == null) {
						if (count < 0)
							count = i;
					} else if (count >= 0) {
						entries[iter][count++] = entries[iter][i];
					}
				}
				max_entry[iter] = count;
				last_index[iter] = 0;
			}
			if (max_entry[iter] > 0)
				has_data = true;
		}
		file.dispose();
		return has_data;
	}

	static public boolean loadDataEntry(FileIO file, DataEntry entry,
			String prefix, String suffix, int type) {
		int[] date = new int[5];
		boolean good = file.getIntArray(prefix + "date" + suffix, date) == 5;
		if (!good)
			return false;
		String key;
		entry.setBirthDay(date);
		key = prefix + "name" + suffix;
		entry.setName(file.hasKey(key) ? file.getString(key) : null);
		key = prefix + "sex" + suffix;
		entry.setSex(file.hasKey(key) ? file.getString(key).equals("male")
				: true);
		if (type == PICK) {
			key = prefix + "dayset" + suffix;
			entry.setChoice(file.hasKey(key) ? file.getString(key).equals(
					"day_choice") : true);
			key = prefix + "degree" + suffix;
			entry
					.setMountainPos(file.hasKey(key) ? file.getString(key)
							: "0.0");
		} else {
			key = prefix + "now" + suffix;
			if (file.hasKey(key)) {
				if (file.getIntArray(key, date) != 5)
					entry.setNowDay(null);
				else
					entry.setNowDay(date);
			}
		}
		setCountryCity(entry, file.getString(prefix + "country" + suffix), file
				.getString(prefix + "city" + suffix));
		entry.setZone(file.getString(prefix + "zone" + suffix));
		key = prefix + "override" + suffix;
		entry.setOverride(file.hasKey(key) ? file.getString(key) : null);
		key = prefix + "note" + suffix;
		entry.setNote(file.hasKey(key) ? unmapString(file.getString(key))
				: null);
		return true;
	}

	static private void setCountryCity(DataEntry entry, String country,
			String city) {
		City c = City.mapCountryCity(country, city);
		if (c != null) {
			entry.setCountry(c.getCountryName());
			entry.setCity(c.getCityName());
		} else {
			entry.setCountry(country);
			entry.setCity(city);
		}
	}

	public void saveData(String file_name) {
		FileIO file = new FileIO(file_name, false, true);
		if (special_map != null)
			file.putStringArray("map_seq", special_map);
		if (footer != null && !footer.trim().equals(""))
			file.putString("desc", mapString(footer));
		String pref = Resource.getAlternatePref();
		if (pref != null)
			file.putString("pref", pref);
		for (int iter = 0; iter < MAX_TYPE; iter++) {
			String prefix = "";
			if (iter > 0)
				prefix = "t" + Integer.toString(iter) + "_";
			if (max_entry[iter] > 1) {
				if (last_index[iter] > 0)
					file.putInt(prefix + "index", last_index[iter]);
				file.putInt(prefix + "max_entry", max_entry[iter]);
			}
			for (int i = 0; i < max_entry[iter]; i++) {
				if (!hasDataEntry(i, iter))
					continue;
				String suffix = (i > 0) ? Integer.toString(i) : "";
				DataEntry entry = getDataEntry(i, iter);
				saveDataEntry(file, entry, prefix, suffix, iter);
			}
		}
		file.dispose();
	}

	static public void saveDataEntry(FileIO file, DataEntry entry,
			String prefix, String suffix, int type) {
		if (entry.getName() != null)
			file.putString(prefix + "name" + suffix, entry.getName());
		file.putString(prefix + "sex" + suffix, entry.getSex() ? "male"
				: "female");
		if (type == PICK) {
			file.putString(prefix + "dayset" + suffix,
					entry.getChoice() ? "day_choice" : "night_choice");
			file.putString(prefix + "degree" + suffix, entry.getMountainPos());
		} else {
			int[] date = entry.getNowDay();
			if (date != null
					&& !BaseCalendar.withinDateRange(date,
							BaseCalendar.DATE_WITHIN_RANGE)) {
				file.putIntArray(prefix + "now" + suffix, date);
			}
		}
		file.putIntArray(prefix + "date" + suffix, entry.getBirthDay());
		file.putString(prefix + "country" + suffix, entry.getCountry());
		file.putString(prefix + "city" + suffix, entry.getCity());
		file.putString(prefix + "zone" + suffix, entry.getZone());
		if (entry.getOverride() != null)
			file.putString(prefix + "override" + suffix, entry.getOverride());
		String note = entry.getNote(true);
		if (note != null)
			file.putString(prefix + "note" + suffix, mapString(note));
	}
}