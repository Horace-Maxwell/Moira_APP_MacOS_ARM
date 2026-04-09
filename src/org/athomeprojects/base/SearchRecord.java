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

public class SearchRecord {
	static public final int UNKNOWN = 0;

	static public final int PARTIAL_ECLIPSE = 1;

	static public final int ANNULAR_ECLIPSE = 2;

	static public final int PENUMBRAL_ECLIPSE = 3;

	static public final int TOTAL_ECLIPSE = 4;

	private int type;

	private double jd_ut;

	private String data;

	public SearchRecord(double val, int kind) {
		jd_ut = val;
		type = kind;
	}

	public SearchRecord(double val, String mesg) {
		jd_ut = val;
		data = mesg;
	}

	public double getTime() {
		return jd_ut;
	}

	public String getData() {
		return data;
	}

	public boolean isType(int kind) {
		return type == kind;
	}

	public String getType() {
		switch (type) {
		case ANNULAR_ECLIPSE:
			return Resource.getString("annular_eclipse");
		case PENUMBRAL_ECLIPSE:
			return Resource.getString("penumbral_eclipse");
		case TOTAL_ECLIPSE:
			return Resource.getString("total_eclipse");
		case PARTIAL_ECLIPSE:
			return Resource.getString("partial_eclipse");
		default:
			return "";
		}
	}
}