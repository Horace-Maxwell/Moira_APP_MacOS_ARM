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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

public class FileIO {
	static private BaseIO base = null;

	private Hashtable table = null, table_save = null;

	private boolean has_custom_data = false;

	private BufferedWriter writer = null;

	private StringWriter string_writer = null;

	private String search_key = null;

	public FileIO(String file_name, String custom_name) {
		initRead(file_name, custom_name, false);
	}

	public FileIO(String file_name, boolean string_reader) {
		initRead(file_name, null, string_reader);
	}

	public FileIO(String file_name, String search, boolean string_reader) {
		search_key = search;
		initRead(file_name, null, string_reader);
	}

	public FileIO(String file_name, boolean append, boolean unicode) {
		try {
			if (file_name == null) {
				string_writer = new StringWriter();
				writer = new BufferedWriter(string_writer);
			} else if (unicode) {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file_name, append), "UTF-16"));
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file_name, append)));
			}
		} catch (IOException e) {
			string_writer = null;
			writer = null;
		}
	}

	public String getDataInString() {
		if (string_writer == null || writer == null)
			return null;
		try {
			writer.close();
		} catch (Exception e) {
		}
		writer = null;
		return string_writer.toString();
	}

	private void initRead(String file_name, String custom_name,
			boolean string_reader) {
		int rule_index = 1;
		URL url = null;
		if (string_reader) {
			if (file_name == null || file_name.trim().equals(""))
				return;
		} else {
			url = getURL(file_name);
			if (url == null)
				return;
		}
		table = table_save = new Hashtable();
		try {
			for (int i = 0; i < 2; i++) {
				BufferedReader reader;
				if (string_reader) {
					reader = new BufferedReader(new StringReader(file_name
							.trim()));
				} else {
					reader = new BufferedReader(new InputStreamReader(url
							.openStream(), "UTF-16"));
				}
				String line;
				while ((line = reader.readLine()) != null) {
					while (line.endsWith("\\")) {
						line = line.substring(0, line.length() - 1);
						String n_line = reader.readLine();
						if (n_line == null)
							break;
						line += n_line;
					}
					line = line.trim();
					if (line.length() == 0 || line.startsWith("#"))
						continue;
					int index = line.indexOf("#");
					if (index >= 0)
						line = line.substring(0, index).trim();
					char c = line.charAt(0);
					if (c == '?' || c == '{') {
						line = line.replaceAll(" |\t", "");
						int n = line.indexOf(':');
						if (n <= 0)
							continue;
						String key = line.substring(1, n);
						String info = line.substring(n + 1);
						if (c == '{') {
							String rank = RuleEntry.getRankString(info);
							String new_key = "+__" + rule_index;
							String remain = info.substring(rank.length());
							key = rank + key.substring(0, key.length() - 1);
							RuleEntry.addRule(new_key, key);
							key = "__" + rule_index++;
							info = remain;
						}
						if (info.length() == 0) {
							// start with next line until $
							info = null;
							while ((line = reader.readLine()) != null) {
								if (line.length() > 0 && line.charAt(0) == '$')
									break;
								while (line.endsWith("\\")) {
									line = line.substring(0, line.length() - 1);
									String n_line = reader.readLine();
									if (n_line == null)
										break;
									line += n_line;
								}
								if (info == null)
									info = line;
								else
									info += "\n" + line;
							}
						}
						if (info != null)
							RuleEntry.addOutput(key, info);
					} else {
						boolean expr = c == '+' || c == '-' || c == '=';
						StringTokenizer st = new StringTokenizer(line,
								expr ? ":" : "=");
						int n_tok = st.countTokens();
						if (n_tok == 1 || n_tok == 2) {
							String key = st.nextToken().trim().toUpperCase();
							boolean assign = false;
							if (key.endsWith(":")) {
								assign = true;
								key = key.substring(0, key.length() - 1).trim();
							}
							if (n_tok == 1) {
								if (line.endsWith("="))
									table.remove(key);
							} else {
								String info = st.nextToken().trim();
								if (assign) {
									info = "^" + info;
								} else if (info.startsWith("\"")
										&& info.endsWith("\"")) {
									info = info.substring(1, info.length() - 1)
											.trim();
								}
								if (info != null) {
									if (expr) {
										RuleEntry.addRule(key.replaceAll(
												" |\t", ""), info.replaceAll(
												" |\t", ""));
									} else {
										table.put(key, info);
										if (search_key != null
												&& search_key
														.equalsIgnoreCase(key)) {
											break;
										}
									}
								}
								if (i > 0)
									has_custom_data = true;
							}
						}
					}
				}
				reader.close();
				if (custom_name == null)
					break;
				url = getURL(custom_name);
				if (url == null)
					break;
				custom_name = null;
			}
		} catch (IOException e) {
		}
	}

	public boolean fileDiff(String file1, String file2, String filter_prefix,
			boolean inline) {
		boolean same = true;
		Vector data1 = new Vector(500);
		Vector data2 = new Vector(500);
		int size1 = 0, size2 = 0;
		BufferedReader in1 = null, in2 = null;
		try {
			URL url = (new File(file1)).toURL();
			if (url == null)
				throw new IOException(file1);
			in1 = new BufferedReader(new InputStreamReader(url.openStream(),
					"UTF-16"));
			url = (new File(file2)).toURL();
			if (url == null)
				throw new IOException(file2);
			in2 = new BufferedReader(new InputStreamReader(url.openStream(),
					"UTF-16"));
			if (inline) {
				for (;;) {
					String line1, line2;
					while ((line1 = in1.readLine()) != null) {
						if (filter_prefix != null
								&& line1.startsWith(filter_prefix))
							continue;
						break;
					}
					while ((line2 = in2.readLine()) != null) {
						if (filter_prefix != null
								&& line2.startsWith(filter_prefix))
							continue;
						break;
					}
					if (line1 == null && line2 == null) {
						return true;
					} else if (line1 == null || line2 == null
							|| !line1.equals(line2)) {
						if (line1 != null)
							putLine("< " + line1);
						if (line2 != null)
							putLine("> " + line2);
						return false;
					}
				}
			} else {
				String line;
				while ((line = in1.readLine()) != null) {
					if (filter_prefix != null && line.startsWith(filter_prefix))
						continue;
					data1.add(size1++, line);
				}
				while ((line = in2.readLine()) != null) {
					if (filter_prefix != null && line.startsWith(filter_prefix))
						continue;
					data2.add(size2++, line);
				}
				// opt[i][j] = length of LCS of data1[i..size1] and
				// data2[j..size2]
				int[][] opt = new int[size1 + 1][size2 + 1];
				// compute length of LCS and all subproblems via dynamic
				// programming
				for (int i = size1 - 1; i >= 0; i--) {
					for (int j = size2 - 1; j >= 0; j--) {
						if (data1.get(i).equals(data2.get(j)))
							opt[i][j] = opt[i + 1][j + 1] + 1;
						else
							opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
					}
				}
				// recover LCS itself and print out non-matching lines to
				// standard
				// output
				int i = 0, j = 0;
				while (i < size1 && j < size2) {
					if (data1.get(i).equals(data2.get(j))) {
						i++;
						j++;
					} else {
						same = false;
						if (opt[i + 1][j] >= opt[i][j + 1])
							putLine("< " + data1.get(i++));
						else
							putLine("> " + data2.get(j++));
					}
				}
				// dump out one remainder of one string if the other is
				// exhausted
				while (i < size1 || j < size2) {
					if (i == size1 || j == size2) {
						same = false;
						if (i == size1)
							putLine("> " + data2.get(j++));
						else
							putLine("< " + data1.get(i++));
					}
				}
			}
		} catch (IOException e) {
			same = false;
			putLine("  Cannot read from file "
					+ ((in1 == null) ? file1 : file2) + "!");
		}
		return same;
	}

	public boolean hasCustomData() {
		return has_custom_data;
	}

	public String getString(String key) {
		return getKey(key);
	}

	public void putString(String key, String val) {
		if (writer == null)
			return;
		try {
			writer.write(key + "=\"" + val + "\"");
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public void putString(String val) {
		if (writer == null)
			return;
		try {
			writer.write(val);
		} catch (Exception e) {
		}
	}

	public void putLine(String val) {
		if (writer == null)
			return;
		try {
			writer.write(val);
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public int getInt(String key) {
		String value = getKey(key);
		try {
			int n = Integer.parseInt(value);
			return n;
		} catch (NumberFormatException e) {
			if (value.toLowerCase().startsWith("0x")) {
				try {
					int n = Integer.parseInt(value.substring(2), 16);
					return n;
				} catch (NumberFormatException er) {
					return Integer.MIN_VALUE;
				}
			}
			return Integer.MIN_VALUE;
		}
	}

	public void putInt(String key, int val) {
		if (writer == null)
			return;
		try {
			writer.write(key + "=" + val);
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public double getDouble(String key) {
		String value = getKey(key);
		try {
			double f = Double.parseDouble(value);
			return f;
		} catch (NumberFormatException e) {
			return Double.NEGATIVE_INFINITY;
		}
	}

	public void putDouble(String key, double val) {
		if (writer == null)
			return;
		try {
			writer.write(key + "=" + val);
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public int getStringArray(String key, String[] array) {
		String value = getKey(key);
		StringTokenizer st = new StringTokenizer(value, ",");
		int size = st.countTokens();
		if (size <= 0 || array == null)
			return 0;
		size = Math.min(size, array.length);
		for (int i = 0; i < size; i++) {
			String field = st.nextToken().trim();
			if (field.startsWith("\'") && field.endsWith("\'"))
				field = field.substring(1, field.length() - 1);
			array[i] = field;
		}
		return size;
	}

	public String[] getStringArray(String key) {
		return toStringArray(getKey(key));
	}

	static public String[] toStringArray(String value) {
		StringTokenizer st = new StringTokenizer(value, ",");
		int size = st.countTokens();
		if (size <= 0)
			return null;
		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			String field = st.nextToken().trim();
			if (field.startsWith("\'") && field.endsWith("\'"))
				field = field.substring(1, field.length() - 1);
			array[i] = field;
		}
		return array;
	}

	static public LinkedList toStringList(String value) {
		LinkedList head = new LinkedList();
		String[] array = toStringArray(value);
		if (array != null) {
			for (int i = 0; i < array.length; i++)
				head.addLast(array[i]);
		}
		return head;
	}

	public void putStringArray(String key, String[] array) {
		if (writer == null)
			return;
		try {
			writer.write(key + "=");
			for (int i = 0; i < array.length; i++) {
				if (i > 0)
					writer.write(", ");
				writer.write(array[i]);
			}
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public int getIntArray(String key, int[] array) {
		String value = getKey(key);
		StringTokenizer st = new StringTokenizer(value, ", ");
		int size = st.countTokens();
		if (size <= 0 || array == null)
			return 0;
		size = Math.min(size, array.length);
		for (int i = 0; i < size; i++) {
			String field = st.nextToken();
			try {
				array[i] = Integer.parseInt(field);
			} catch (NumberFormatException e) {
				if (field.toLowerCase().startsWith("0x")) {
					try {
						array[i] = Integer.parseInt(field.substring(2), 16);
					} catch (NumberFormatException er) {
						return -1;
					}
				} else {
					return -1;
				}
			}
		}
		return size;
	}

	public int[] getIntArray(String key) {
		return toIntArray(getKey(key));
	}

	static public int[] toIntArray(String value) {
		StringTokenizer st = new StringTokenizer(value, ", ");
		int size = st.countTokens();
		if (size <= 0)
			return null;
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			String field = st.nextToken();
			try {
				array[i] = Integer.parseInt(field);
			} catch (NumberFormatException e) {
				if (field.toLowerCase().startsWith("0x")) {
					try {
						array[i] = Integer.parseInt(field.substring(2), 16);
					} catch (NumberFormatException er) {
						return null;
					}
				} else {
					return null;
				}
			}
		}
		return array;
	}

	public void putIntArray(String key, int[] array) {
		if (writer == null)
			return;
		try {
			writer.write(key + "=");
			for (int i = 0; i < array.length; i++) {
				if (i > 0)
					writer.write(", ");
				writer.write(Integer.toString(array[i]));
			}
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public int getDoubleArray(String key, double[] array) {
		String value = getKey(key);
		try {
			StringTokenizer st = new StringTokenizer(value, ", ");
			int size = st.countTokens();
			if (size <= 0 || array == null)
				return 0;
			size = Math.min(size, array.length);
			for (int i = 0; i < size; i++) {
				String field = st.nextToken();
				array[i] = Double.parseDouble(field);
			}
			return size;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public double[] getDoubleArray(String key) {
		return toDoubleArray(getKey(key));
	}

	static public double[] toDoubleArray(String value) {
		StringTokenizer st = new StringTokenizer(value, ", ");
		int size = st.countTokens();
		if (size <= 0)
			return null;
		double[] array = new double[size];
		for (int i = 0; i < size; i++) {
			String field = st.nextToken();
			try {
				array[i] = Double.parseDouble(field);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return array;
	}

	public void putDoubleArray(String key, double[] array) {
		if (writer == null)
			return;
		try {
			writer.write(key + "=");
			for (int i = 0; i < array.length; i++) {
				if (i > 0)
					writer.write(", ");
				writer.write(Double.toString(array[i]));
			}
			writer.newLine();
		} catch (Exception e) {
		}
	}

	public boolean hasKey(String key) {
		String str = (String) table.get(key.toUpperCase());
		if (str != null && str.length() > 0 && str.charAt(0) == '^')
			return hasKey(str.substring(1));
		return str != null;
	}

	private String getKey(String key) {
		String str = (String) table.get(key.toUpperCase());
		if (str != null && str.length() > 0 && str.charAt(0) == '^')
			return getKey(str.substring(1));
		return (str == null) ? key.toUpperCase() : str;
	}

	public void setTable(Hashtable alt_table) {
		table = (alt_table == null) ? table_save : alt_table;
	}

	static public int parseInt(String str, int def_val, boolean positive) {
		int val;
		try {
			val = Integer.parseInt(str.trim());
			if (positive && val < 0)
				val = def_val;
		} catch (NumberFormatException e) {
			val = def_val;
		}
		return val;
	}

	static public double parseDouble(String str, double def_val,
			boolean positive) {
		double val;
		try {
			val = Double.parseDouble(str.trim());
			if (positive && val < 0.0)
				val = def_val;
		} catch (NumberFormatException e) {
			val = def_val;
		}
		return val;
	}

	static public double parseAscDec(String str, double def_val, boolean dec) {
		boolean negative = str.startsWith("-");
		if (negative)
			str = str.substring(1);
		StringTokenizer st = new StringTokenizer(str, ":");
		int size = st.countTokens();
		double degree;
		if (size == 0) {
			degree = parseDouble(str, def_val, false);
		} else {
			if (size != 2 && size != 3)
				return def_val;
			int hour = parseInt(st.nextToken().trim(), 0, true);
			int minute = parseInt(st.nextToken().trim(), 0, true);
			int second = st.hasMoreTokens() ? parseInt(st.nextToken().trim(),
					0, true) : 0;
			degree = (minute + ((double) second) / 60.0) / 60.0;
			if (dec)
				degree += (double) hour;
			else
				degree = 360.0 / 24.0 * (hour + degree);
		}
		if (negative)
			degree = -degree;
		return dec ? degree : City.normalizeDegree(degree);
	}

	static public String formatInt(int val, int width) {
		String str = Integer.toString(val);
		while (str.length() < width)
			str = " " + str;
		return str;
	}

	static public String formatDouble(double val, int width,
			int fraction_width, boolean align, boolean sign) {
		boolean negative = val < 0.0;
		if (negative && sign)
			val = -val;
		String seq = "";
		if (align) {
			for (int i = 1; i < width; i++)
				seq += "0";
		}
		seq += "0.";
		for (int i = 0; i < fraction_width; i++)
			seq += align ? "0" : "#";
		DecimalFormat format = new DecimalFormat(seq);
		return (sign ? (negative ? "-" : "+") : "") + format.format(val);
	}

	static public int boundNumber(int val, int max) {
		val %= max;
		if (val < 0)
			val += max;
		return val;
	}

	static public boolean isAsciiString(String key, boolean all) {
		char[] array = key.toCharArray();
		for (int i = 0; i < array.length; i++) {
			if (all) {
				if (array[i] > 0xff)
					return false;
			} else { // any
				if (isAlphaDigit(array[i]))
					return true;
			}
		}
		return all;
	}

	static private boolean isAlphaDigit(char c) {
		return Character.isUpperCase(c) || Character.isLowerCase(c)
				|| Character.isDigit(c);
	}

	static public String formatString(String val, int width) {
		char[] array = val.toCharArray();
		int remain = 2 * width;
		for (int i = 0; i < array.length; i++) {
			remain--;
			if (array[i] > 0xff)
				remain--;
		}
		while (remain-- > 0)
			val += " ";
		return val;
	}

	static public int getArrayIndex(String name, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (name.equals(array[i]))
				return i;
		}
		return -1;
	}

	public void dispose() {
		table = table_save = null;
		if (writer != null) {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
		string_writer = null;
	}

	static public void setBaseIO(BaseIO io) {
		base = io;
	}

	static public String getFileName(String file_name) {
		return (base == null) ? null : base.getFileName(file_name);
	}

	static public URL getURL(String file_name) {
		return (base == null) ? null : base.getURL(file_name);
	}

	static public String getTempFileName(String suffix) {
		String file_name = null;
		try {
			File file = File.createTempFile(Resource.NAME, suffix);
			file.deleteOnExit();
			file_name = file.getAbsolutePath();
		} catch (IOException e) {
			return null;
		}
		return file_name;
	}

	static public void setTempFile(String file_name) {
		try {
			File file = new File(file_name);
			file.deleteOnExit();
		} catch (NullPointerException e) {
		}
	}

	static public void setProgress(int val) {
		if (base != null)
			base.setProgress(val);
	}
}
