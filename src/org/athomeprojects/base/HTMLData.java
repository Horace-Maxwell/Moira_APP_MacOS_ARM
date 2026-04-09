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

import java.io.File;
import java.util.StringTokenizer;

public class HTMLData {
	static int num_page, cur_page;

	static String base_name, page_title;

	static private FileIO file;

	static public void init(String file_name, String title, int n_page) {
		cur_page = 0;
		num_page = n_page;
		base_name = file_name;
		page_title = title;
	}

	static public void header() {
		String file_name = getFileName(cur_page);
		if (cur_page > 0)
			FileIO.setTempFile(file_name);
		file = new FileIO(file_name, false, false);
		file.putLine("<html>");
		file.putLine("<head>");
		file
				.putLine("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
		file
				.putLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
		file.putLine("<title>" + htmlString(page_title) + "</title>");
		file.putLine("</head>");
		file.putLine("<body>");
	}

	static public void paragraph(String data) {
		file.putLine("<p>");
		StringTokenizer st = new StringTokenizer(data, "|");
		while (st.hasMoreTokens())
			file.putLine(htmlString(st.nextToken()) + "<br>");
		file.putLine("</p>");
	}

	static public void tableHeader(String[] row_header) {
		if (num_page > 1)
			addLink();
		file.putLine("<table border=\"1\" id=\"table1\" cellpadding=\"4\">");
		file.putLine("<tr>");
		for (int i = 0; i < row_header.length; i++)
			file.putLine("<th>" + htmlString(row_header[i]) + "</th>");
		file.putLine("</tr>");
	}

	static public void tableFooter() {
		file.putLine("</table>");
		if (num_page > 1)
			addLink();
	}

	static private void addLink() {
		file.putLine("<p>");
		if (cur_page > 0) {
			file.putLine("<a href=\"" + getFileName(cur_page - 1)
					+ "\"><img border=\"0\" src=\""
					+ FileIO.getURL("icon/backward.ico").toString()
					+ "\" width=\"16\" height=\"12\"></a>");
		}
		for (int i = 0; i < num_page; i++) {
			String name = getFileName(i);
			String num = Integer.toString(i + 1);
			if (i == cur_page)
				num = "[" + num + "]";
			file.putLine("<a href=\"" + name + "\">" + num + "</a>");
		}
		if (cur_page < num_page - 1) {
			file.putLine("<a href=\"" + getFileName(cur_page + 1)
					+ "\"><img border=\"0\" src=\""
					+ FileIO.getURL("icon/forward.ico").toString()
					+ "\" width=\"16\" height=\"12\"></a>");
		}
		file.putLine("</p>");
	}

	static private String getFileName(int page) {
		int index = base_name.lastIndexOf(".");
		String file_name = base_name.substring(0, index);
		if (page > 0)
			file_name += "_" + Integer.toString(page);
		return file_name + ".html";
	}

	static public void footer() {
		file.putLine("</body>");
		file.putLine("</html>");
		file.dispose();
		cur_page++;
	}

	static public void tableRow(double jd_ut, String date, String s_sign,
			String aspect, String e_sign) {
		tableRow(Double.toString(jd_ut), date, s_sign, aspect, e_sign);
	}

	static public void tableRow(String target, String date, String s_sign,
			String aspect, String e_sign) {
		file.putLine("<tr>");
		file.putLine("<td align=\"center\"><a href=\"" + target + "\">" + date
				+ "</a></td>");
		if (s_sign != null)
			file
					.putLine("<td align=\"center\">" + htmlString(s_sign)
							+ "</td>");
		if (aspect != null)
			file
					.putLine("<td align=\"center\">" + htmlString(aspect)
							+ "</td>");
		if (e_sign != null)
			file
					.putLine("<td align=\"center\">" + htmlString(e_sign)
							+ "</td>");
		file.putLine("</tr>");
	}

	static public void tableRow(String[] data) {
		file.putLine("<tr>");
		for (int i = 0; i < data.length; i++) {
			file.putLine("<td align=\"center\">" + htmlString(data[i])
					+ "</td>");
		}
		file.putLine("</tr>");
	}

	static public String htmlString(String str) {
		char[] array = str.toCharArray();
		String val = "";
		for (int i = 0; i < array.length; i++) {
			val += "&#" + Integer.toString((int) array[i]) + ";";
		}
		return val;
	}

	static public String extractData(String str) {
		if (str == null)
			return null;
		int index = str.lastIndexOf(File.separator);
		return (index >= 0) ? str.substring(index + 1) : str;
	}
}