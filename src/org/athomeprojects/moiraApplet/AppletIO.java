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
package org.athomeprojects.moiraApplet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

import netscape.javascript.JSObject;

import org.athomeprojects.awtext.CalendarSelect;
import org.athomeprojects.awtext.LocationSelect;
import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.BaseIO;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;

public class AppletIO extends BaseIO {
	private final String OPTION = "option";

	private Hashtable option;

	private MoiraApplet applet;

	private LinkedList data;

	private class Record {
		String name;

		int sex;

		String date, now_date;

		String country, city, zone;
	}

	public void setApplet(MoiraApplet app) {
		applet = app;
		loadOption();
	}

	public String getFileName(String file_name) {
		return file_name.replace(File.separatorChar, '/');
	}

	public URL getURL(String file_name) {
		if (file_name.startsWith(Resource.LOCAL_PREFIX))
			return getFileURL(file_name.substring(Resource.LOCAL_PREFIX
					.length()));
		String name = getFileName(file_name);
		try {
			return new URL(applet.getCodeBase().toString() + name);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private URL getFileURL(String file_name) {
		File file = new File(file_name);
		try {
			return file.toURL();
		} catch (MalformedURLException e) {
		}
		return null;
	}

	private void loadOption() {
		option = new Hashtable();
		String info = getCookie(OPTION);
		StringTokenizer st = new StringTokenizer(info, ";");
		if (st.countTokens() >= 2) {
			while (st.hasMoreTokens()) {
				extractOption(st.nextToken());
			}
		} else {
			extractOption(info);
		}
	}

	private void extractOption(String entry) {
		StringTokenizer st = new StringTokenizer(entry, "|");
		if (st.countTokens() == 2) {
			String key = st.nextToken().trim();
			String value = st.nextToken().trim();
			option.put(key, value);
		}
	}

	public boolean getBoolean(String name) {
		String value = (String) option.get(name);
		return value != null && FileIO.parseInt(value, 0, false) != 0;
	}

	public void setBoolean(String name, boolean set) {
		option.put(name, set ? "1" : "0");
		saveOption();
	}

	public boolean hasKey(String name) {
		return option.containsKey(name);
	}

	public void remove(String name) {
		option.remove(name);
		saveOption();
	}

	public String getString(String name) {
		String value = (String) option.get(name);
		return value;
	}

	public void setString(String name, String value) {
		option.put(name, value);
		saveOption();
	}

	private void saveOption() {
		String info = null;
		for (Enumeration e = option.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (info == null)
				info = "";
			else
				info += ";";
			info += key + "|" + ((String) option.get(key));
		}
		if (info == null)
			info = "";
		setCookie(OPTION, info);
	}

	public String[] loadFromStore(String name) {
		data = new LinkedList();
		String info = getCookie(name);
		if (info == null)
			return null;
		LinkedList head = new LinkedList();
		StringTokenizer st = new StringTokenizer(info, ";");
		if (st.countTokens() >= 2) {
			while (st.hasMoreTokens()) {
				String key = storeEntry(st.nextToken());
				if (key != null)
					head.add(key);
			}
		} else {
			String key = storeEntry(info);
			if (key != null)
				head.add(key);
		}
		if (head.isEmpty())
			return null;
		return (String[]) head.toArray(new String[1]);
	}

	private String storeEntry(String entry) {
		StringTokenizer st = new StringTokenizer(entry, "|");
		int size = st.countTokens();
		if (size < 5)
			return null;
		Record record = new Record();
		record.name = st.nextToken();
		record.date = st.nextToken();
		record.country = st.nextToken();
		record.city = st.nextToken();
		record.zone = st.nextToken();
		size -= 5;
		while (size > 0) {
			size--;
			String str = st.nextToken();
			if (str.equals("t") || str.equals("f")) {
				record.sex = str.equals("t") ? 1 : 0;
			} else {
				record.now_date = str;
			}
		}
		data.addLast(record);
		return record.name;
	}

	public boolean loadData(String name, int[] sex, CalendarSelect birth,
			CalendarSelect now, LocationSelect loc) {
		Record record = findRecord(name);
		if (record == null)
			return false;
		boolean same = true;
		int[] date = new int[5], now_date = new int[5];
		birth.getCalendar(date);
		int[] old_date = (int[]) date.clone();
		StringTokenizer st = new StringTokenizer(record.date, ",");
		for (int i = 0; i < 5; i++) {
			date[i] = FileIO.parseInt(st.nextToken(), -1, false);
			if (date[i] != old_date[i])
				same = false;
		}
		if (!loc.getCountryName().equals(record.country)
				|| !loc.getCityName().equals(record.city)
				|| !loc.getZoneName().equals(record.zone)
				|| sex[0] != record.sex)
			same = false;
		if (record.now_date != null) {
			now.getCalendar(now_date);
			old_date = (int[]) now_date.clone();
			st = new StringTokenizer(record.now_date, ",");
			for (int i = 0; i < 5; i++) {
				now_date[i] = FileIO.parseInt(st.nextToken(), -1, false);
				if (now_date[i] != old_date[i])
					same = false;
			}
		}
		if (same)
			return false;
		birth.setCalendar(date);
		now.setCalendar((record.now_date != null) ? now_date : null);
		loc.setCountryName(record.country);
		loc.setCityName(record.city);
		loc.setZoneName(record.zone);
		sex[0] = record.sex;
		return true;
	}

	public void saveData(String name, int sex, CalendarSelect birth,
			CalendarSelect now, LocationSelect loc) {
		Record record = findRecord(name);
		if (record == null) {
			record = new Record();
			record.name = name;
		} else {
			data.remove(record);
		}
		int[] date = new int[5];
		birth.getCalendar(date);
		record.date = Integer.toString(date[0]) + ","
				+ Integer.toString(date[1]) + "," + Integer.toString(date[2])
				+ "," + Integer.toString(date[3]) + ","
				+ Integer.toString(date[4]);
		record.country = loc.getCountryName();
		record.city = loc.getCityName();
		record.zone = loc.getZoneName();
		record.sex = sex;
		now.getCalendar(date);
		if (!BaseCalendar.withinDateRange(date, BaseCalendar.DATE_WITHIN_RANGE)) {
			record.now_date = Integer.toString(date[0]) + ","
					+ Integer.toString(date[1]) + ","
					+ Integer.toString(date[2]) + ","
					+ Integer.toString(date[3]) + ","
					+ Integer.toString(date[4]);
		} else {
			record.now_date = null;
		}
		data.addFirst(record);
	}

	private Record findRecord(String name) {
		ListIterator iter = data.listIterator();
		while (iter.hasNext()) {
			Record record = (Record) iter.next();
			if (name.equals(record.name))
				return record;
		}
		return null;
	}

	public void saveToStore(String name, int max_entry) {
		if (data == null)
			return;
		if (max_entry <= 0) {
			setCookie(name, "");
			return;
		}
		int count = 0;
		String cookie = null;
		ListIterator iter = data.listIterator();
		while (iter.hasNext()) {
			Record record = (Record) iter.next();
			String value = record.name + "|" + record.date + "|"
					+ record.country + "|" + record.city + "|" + record.zone
					+ "|" + ((record.sex != 0) ? "t" : "f");
			if (record.now_date != null)
				value += "|" + record.now_date;
			if (cookie == null) {
				cookie = value;
			} else {
				cookie += ";" + value;
			}
			count++;
			if (count >= max_entry)
				break;
		}
		if (cookie == null)
			cookie = "";
		setCookie(name, cookie);
	}

	private String getCookie(String name) {
//		try {
//			JSObject browser = (JSObject) JSObject.getWindow(applet);
//			Object[] arg = new Object[1];
//			arg[0] = name;
//			String str = (String) browser.call("getCookie", arg);
//			return (str == null) ? "" : str;
//		} catch (Exception e) {
//			return "";
//		}
		return "";
	}

	private void setCookie(String name, String val) {
//		try {
//			JSObject browser = (JSObject) JSObject.getWindow(applet);
//			Object[] arg = new Object[2];
//			arg[0] = name;
//			arg[1] = val;
//			browser.call("setCookie", arg);
//		} catch (Exception e) {
//		}
	}

	public String getClipboard() {
//		try {
//			JSObject browser = (JSObject) JSObject.getWindow(applet);
//			Object[] arg = new Object[1];
//			arg[0] = "Text";
//			return (String) browser.call("getClipboard", arg);
//		} catch (Exception e) {
//			return "";
//		}
		return "";
	}

	public void setClipboard(String str) {
//		try {
//			JSObject browser = (JSObject) JSObject.getWindow(applet);
//			Object[] arg = new Object[2];
//			arg[0] = "Text";
//			arg[1] = str;
//			browser.call("setClipboard", arg);
//		} catch (Exception e) {
//		}
	}
}