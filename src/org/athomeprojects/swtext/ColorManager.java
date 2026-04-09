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
package org.athomeprojects.swtext;

import java.util.Enumeration;
import java.util.Hashtable;

import org.athomeprojects.base.Resource;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	static private Hashtable table = new Hashtable();

	static public Color getColor(String name) {
		return getColor(Resource.getPrefInt(name));
	}

	static public Color getColor(int rgb) {
		Color color = (Color) table.get(Integer.toString(rgb));
		if (color == null) {
			color = allocateColor(rgb);
			table.put(Integer.toString(rgb), color);
		}
		return color;
	}

	static public Color allocateColor(int rgb) {
		return new Color(Display.getCurrent(), (rgb >> 16) & 0xff,
				(rgb >> 8) & 0xff, rgb & 0xff);
	}

	static public void dispose() {
		if (table == null)
			return;
		for (Enumeration e = table.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			Color color = (Color) table.get(key);
			color.dispose();
		}
		table = null;
	}
}