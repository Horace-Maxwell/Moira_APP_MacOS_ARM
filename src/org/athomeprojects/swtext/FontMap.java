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

import org.athomeprojects.base.Resource;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class FontMap {
	static private FontData[] font_data_array;

	static private String font_name, en_font_name;

	static public void resetFontName() {
		font_name = en_font_name = null;
	}

	static public String getSwtFontName() {
		if (font_name == null)
			font_name = mapSwtFontName(Resource.getFontName());
		return font_name;
	}

	static public void setSwtFontName(String name) {
		font_name = name;
		Resource.setFontName(name);
	}

	static public String getSwtEnFontName() {
		if (en_font_name == null)
			en_font_name = mapSwtFontName(Resource.getEnFontName());
		return en_font_name;
	}

	static private String mapSwtFontName(String name) {
		if (font_data_array == null)
			font_data_array = Display.getCurrent().getFontList(null, true);
		String[] name_array = Resource.getPossibleFontName(name);
		if (name_array == null || font_data_array == null)
			return name;
		for (int i = 0; i < font_data_array.length; i++) {
			String f_name = font_data_array[i].getName();
	
			for (int j = 0; j < name_array.length; j++) {
				if (f_name.equalsIgnoreCase(name_array[j]))
					return f_name;
			}
		}
		return null;
	}
}