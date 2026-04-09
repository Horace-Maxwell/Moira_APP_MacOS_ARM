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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class City {
	static public final double INVALID = Double.NEGATIVE_INFINITY;

	static public final String UNKNOWN_ZONE = "Unknown";

	static private final double MIN_ERROR_RATIO = 0.99;

	static private final double MATCH_ERROR = 0.125;

	static private final double TIGHT_MATCH_ERROR = 0.01;

	static private final double ANY_MATCH_ERROR = 180.0;

	static public final double MATCH_ERROR_SQ = MATCH_ERROR * MATCH_ERROR;

	static public final double TIGHT_MATCH_ERROR_SQ = TIGHT_MATCH_ERROR
			* TIGHT_MATCH_ERROR;

	static public final double ANY_MATCH_ERROR_SQ = ANY_MATCH_ERROR
			* ANY_MATCH_ERROR;

	static private City[] cities, map_cities;

	static private Hashtable dst_override;

	static private DstEntry dst_last;

	private String country, city, zone;

	private double longitude, latitude;

	public City(String which_country, String which_city, double long_val,
			double lat_val, String which_zone) {
		country = which_country;
		city = which_city;
		longitude = long_val;
		latitude = lat_val;
		zone = which_zone;
	}

	public String getCountryName() {
		return country;
	}

	public String getCityName() {
		return city;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getZoneName() {
		return zone;
	}

	public void setZoneName(String t_zone) {
		zone = t_zone;
	}

	static public void loadCities(String file_name) {
		dst_override = new Hashtable();
		LinkedList[] head_group = new LinkedList[2];
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					FileIO.getURL(file_name).openStream(), "UTF-16"));
			String[] field = new String[5];
			String line;
			// first 3 sections contain timezone data
			for (int iter = 0; iter < 3; iter++) {
				int index = iter;
				if (iter == 2)
					index = Resource.isSimplified() ? 0 : 1;
				if (head_group[index] == null)
					head_group[index] = new LinkedList();
				LinkedList head = head_group[index];
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					
					
					
					if (line.length() == 0 || line.startsWith("#"))
						continue;
					if (line.equals("---"))
						break;
					StringTokenizer st = new StringTokenizer(line, "|");
					int n = st.countTokens();
					if (n == 4 || n == 5) {
						if (n == 4)
							field[4] = null;
						for (int i = 0; i < n; i++)
							field[i] = st.nextToken().trim();
						try {
							City city = new City(field[0], field[1], Double
									.parseDouble(field[2]), Double
									.parseDouble(field[3]), field[4]);
							head.addLast(city);
						} catch (Exception e) {
						}
					}
				}
			}
			// fourth section contains daylight savings time override
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				if (line.equals("---"))
					break;
				StringTokenizer st = new StringTokenizer(line, "|");
				int n = st.countTokens();
				if (n == 4) {
					String zone_name = st.nextToken().trim();
					LinkedList head = (LinkedList) dst_override.get(zone_name);
					if (head == null) {
						head = new LinkedList();
						dst_override.put(zone_name, head);
					}
					TimeZone zone = TimeZone.getTimeZone(zone_name);
					DstEntry entry = new DstEntry();
					int[] date = new int[5];
					for (int i = 0; i < 2; i++) {
						StringTokenizer nst = new StringTokenizer(st
								.nextToken().trim(), ",");
						for (int j = 0; j < 5; j++) {
							date[j] = FileIO.parseInt(nst.nextToken().trim(),
									0, true);
						}
						if (i == 0)
							entry.start = BaseCalendar.getDate(date, zone);
						else
							entry.end = BaseCalendar.getDate(date, zone);
					}
					entry.offset = FileIO.parseInt(st.nextToken().trim(), 0,
							false);
					head.addLast(entry);
				}
			}
			reader.close();
		} catch (IOException e) {
		}
		if (Resource.isSimplified()) {
			cities = (City[]) head_group[0].toArray(new City[1]);
			map_cities = (City[]) head_group[1].toArray(new City[1]);
		} else {
			cities = (City[]) head_group[1].toArray(new City[1]);
			map_cities = (City[]) head_group[0].toArray(new City[1]);
		}
	}

	static public boolean inDaylightTime(TimeZone zone, String zone_name,
			int year, Date date) {
		dst_last = null;
		LinkedList head = (LinkedList) dst_override.get(zone_name);
		if (head != null) {
			for (ListIterator iter = head.listIterator(); iter.hasNext();) {
				DstEntry dst = (DstEntry) iter.next();
				if (!date.before(dst.start) && !date.after(dst.end)) {
					dst_last = dst;
					return dst.offset != 0;
				} else if (date.before(dst.start)) {
					break;
				}
			}
		}
		return zone.inDaylightTime(date);
	}

	static public int getDstOffset(Calendar cal) {
		return (dst_last == null) ? (cal.get(Calendar.DST_OFFSET) / BaseCalendar.MILLISECOND_PER_MINUTE)
				: dst_last.offset;
	}

	static public String getDefaultCountry() {
		return cities[0].getCountryName();
	}

	static public String getDefaultCity() {
		return cities[0].getCityName();
	}

	static public String getUnknownCountry() {
		return cities[cities.length - 1].getCountryName();
	}

	static public String toMinuteSeconds(double degree, boolean has_second) {
		int second = (int) Math.round(3600.0 * degree);
		int minute = second / 60;
		second -= 60 * minute;
		DecimalFormat format = new DecimalFormat("00");
		if (has_second) {
			return format.format(minute) + "'" + format.format(second);
		} else {
			return format.format(minute + ((second >= 30) ? 1 : 0));
		}
	}

	static public String formatLongLatitude(double degree, boolean is_long,
			boolean show_second, boolean align) {
		if (degree == INVALID)
			return "?invalid?";
		String str;
		if (align) {
			DecimalFormat format = new DecimalFormat("000");
			str = format.format((int) Math.abs(degree));
		} else {
			str = Integer.toString((int) Math.abs(degree));
		}
		if (degree < 0.0) {
			degree = -degree;
			str += is_long ? "W" : "S";
		} else {
			str += is_long ? "E" : "N";
		}
		degree -= (double) ((int) degree);
		return str + toMinuteSeconds(degree, show_second);
	}

	static public boolean parseLongLatitude(String city, String country,
			double[] long_lat) {
		City c = matchCity(city, country, false);
		if (c != null) {
			long_lat[0] = c.getLongitude();
			long_lat[1] = c.getLatitude();
			return true;
		} else {
			return parseLongLatitude(city, long_lat);
		}
	}

	static public boolean parseLongLatitude(String str, double[] long_lat) {
		String key = str.toUpperCase();
		StringTokenizer st = new StringTokenizer(key, ", ");
		if (st.countTokens() == 2) {
			double long_val, lat_val;
			long_val = parseLongLatitude(st.nextToken(), 'E', 'W');
			lat_val = parseLongLatitude(st.nextToken(), 'N', 'S');
			if (long_val != INVALID && lat_val != INVALID) {
				long_lat[0] = long_val;
				long_lat[1] = lat_val;
				return true;
			}
		}
		return false;
	}

	static public double parseLongLatitude(String val, char p_char, char n_char) {
		try {
			int ch_index;
			boolean negative = false;
			double degree = INVALID;
			for (int i = 0; i < 2; i++) {
				char c = (i == 0) ? p_char : n_char;
				ch_index = val.indexOf(c);
				if (ch_index < 0)
					ch_index = val.indexOf(Character.toLowerCase(c));
				if (ch_index >= 0) {
					if (ch_index == val.length() - 1) {
						int co_index = val.indexOf(':');
						if (co_index >= 0) {
							degree = Double.parseDouble(val.substring(0,
									co_index));
							val = val.substring(co_index + 1, ch_index);
						} else {
							degree = Double.parseDouble(val.substring(0,
									ch_index));
							val = "0";
						}
					} else {
						degree = Double.parseDouble(val.substring(0, ch_index));
						val = val.substring(ch_index + 1);
					}
					negative = i != 0;
				}
			}
			if (degree != INVALID) {
				double minute;
				ch_index = val.indexOf('\'');
				if (ch_index >= 0) {
					minute = Double.parseDouble(val.substring(0, ch_index));
					if (val.length() > ch_index + 1) {
						val = val.substring(ch_index + 1);
						double second = Double.parseDouble(val);
						minute += second / 60.0;
					}
				} else {
					minute = Double.parseDouble(val);
				}
				degree += minute / 60.0;
				if (negative)
					degree = -degree;
				return degree;
			}
			return Double.parseDouble(val);
		} catch (NumberFormatException e) {
			return INVALID;
		}
	}

	static public String mapZoneName(String name) {
		if (name.startsWith("Etc/GMT+")) {
			return name.replace('+', '-');
		} else if (name.startsWith("Etc/GMT-")) {
			return name.replace('-', '+');
		}
		return name;
	}

	static public String[] getAllZoneNames() {
		String[] zone_ids = TimeZone.getAvailableIDs();
		String[] zone_names = (String[]) zone_ids.clone();
		for (int i = 0; i < zone_names.length; i++) {
			zone_names[i] = mapZoneName(zone_names[i]);
		}
		return zone_names;
	}

	static public String[] getCountryList() {
		LinkedList head = new LinkedList();
		int len = cities.length;
		for (int i = 0; i < len; i++) {
			City c = cities[i];
			String name = c.getCountryName();
			if (i == 0 || !cities[i - 1].getCountryName().equals(name)) {
				head.add(name);
			}
		}
		return (String[]) head.toArray(new String[1]);
	}

	static public String[] getCityList(String country_name) {
		LinkedList head = new LinkedList();
		int len = cities.length;
		for (int i = 0; i < len; i++) {
			City c = cities[i];
			if (c.getCountryName().equals(country_name))
				head.add(c.getCityName());
		}
		return (String[]) head.toArray(new String[1]);
	}

	static public City matchCity(String city_name, String country_name,
			boolean use_map) {
		City[] array = use_map ? map_cities : cities;
		int len = array.length;
		City s_c = null;
		for (int i = 0; i < len; i++) {
			City c = array[i];
			if (c.getCountryName().equalsIgnoreCase(country_name)) {
				if (c.getCityName().equalsIgnoreCase(city_name))
					return c;
			} else if (!use_map && c.getCityName().equalsIgnoreCase(city_name)) {
				s_c = c;
			}
		}
		return s_c;
	}

	static public int matchCityIndex(double long_val, double lat_val,
			double error) {
		if (Resource.getPrefInt("match_city") == 0)
			return -1;
		double min_error = 2 * error;
		int index = -1;
		int len = cities.length;
		for (int i = 0; i < len; i++) {
			City c = cities[i];
			double val = c.getLongitude() - long_val;
			double long_error = val * val;
			val = c.getLatitude() - lat_val;
			double lat_error = val * val;
			if (long_error < error && lat_error < error
					&& long_error + lat_error < min_error) {
				index = i;
				min_error = MIN_ERROR_RATIO * (long_error + lat_error);
			}
		}
		return index;
	}

	static public City matchCity(double long_val, double lat_val, double error) {
		int index = matchCityIndex(long_val, lat_val, error);
		if (index >= 0) {
			return cities[index];
		} else {
			return null;
		}
	}

	static public City getCity(int index) {
		if (index >= 0) {
			return cities[index];
		} else {
			return null;
		}
	}

	static public City mapCountryCity(String country, String city) {
		if (country.equals(getDefaultCountry())) {
			// get country name by matching city name
			for (int i = 0; i < cities.length; i++) {
				City c = cities[i];
				if (c.getCityName().equals(city))
					return c;
			}
		} else if (map_cities != null) {
			City c = matchCity(city, country, true);
			if (c != null) {
				c = matchCity(c.getLongitude(), c.getLatitude(),
						TIGHT_MATCH_ERROR_SQ);
			}
			return c;
		}
		return null;
	}

	static public double normalizeDegree(double degree) {
		degree = degree % 360;
		if (degree < 0.0)
			degree += 360.0;
		return degree;
	}

	static public double parsePos(String pos, double def_val) {
		double degree;
		try {
			degree = normalizeDegree(Double.parseDouble(pos));
		} catch (NumberFormatException e) {
			degree = def_val;
		}
		return degree;
	}

	static public String formatPos(double val, int width, int fraction_width,
			boolean align) {
		return FileIO.formatDouble(normalizeDegree(val), width, fraction_width,
				align, false);
	}

	static public double parseMapPos(String pos) {
		if (pos == null || pos.trim().equals(""))
			pos = "0.0";
		return normalizeDegree(315.0 - parsePos(pos, 0.0));
	}

	static public String formatMapPos(double val, boolean align) {
		return formatPos(315.0 - val, 3, 2, align);
	}

	static private class DstEntry {
		public Date start, end;

		int offset;
	}
}
