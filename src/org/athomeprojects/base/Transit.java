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
import java.util.Comparator;

public class Transit {
	public double jd_ut, pos;

	public int from_index, to_index, aspect_index;

	public Transit(double time, double degree, int f_index, int a_index,
			int t_index) {
		jd_ut = time;
		pos = degree;
		from_index = f_index;
		to_index = t_index;
		aspect_index = a_index;
	}

	static public void sort(Transit[] array) {
		Arrays.sort(array, 0, array.length, new Comparator() {
			public int compare(Object a, Object b) {
				double p_a = ((Transit) a).jd_ut;
				double p_b = ((Transit) b).jd_ut;
				if (p_a < p_b)
					return -1;
				else if (p_a > p_b)
					return 1;
				else
					return 0;
			}
		});
	}
}