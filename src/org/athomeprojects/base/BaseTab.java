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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

abstract public class BaseTab {
	protected final String EOL = "\r\n";

	private JTextArea area;

	private int column_height, column_width, column_gap, column_extra,
			num_pages;

	public void clear() {
	}

	public void setWrapMode(boolean set) {
	}

	public void append(String str) {
	}

	public void appendLine(String str) {
	}

	public void appendLine() {
	}

	public void setName(String name, boolean sex, boolean replace) {
	}

	public void setName(String name, boolean replace) {
	}

	public void replace(String src, String dst) {
	}

	public int initPrint(Graphics2D g2d, String data, Point page_size) {
		area = new JTextArea(data);
		area.setDoubleBuffered(false);
		int scaler = Resource.getInt("print_scaler");
		int size = scaler * Resource.getInt("g2d_print_data_font_size");
		Font area_font = new Font(Resource.getFontName(), Resource
				.getFontStyle(), size);
		FontMetrics metric = g2d.getFontMetrics(area_font);
		column_gap = (int) (page_size.x * Resource
				.getDouble("print_dual_page_gap"));
		column_width = (page_size.x - column_gap) / 2;
		column_height = ((page_size.y / metric.getHeight()) - 1)
				* metric.getHeight();
		column_extra = (int) (0.05 * column_width);
		area.setFont(area_font);
		area.setLineWrap(true);
		area.setWrapStyleWord(false);
		area.setTabSize(4);
		// getPreferredSize does not seem to work properly, need to search for
		// it
		for (num_pages = 1; num_pages <= 10; num_pages++) {
			int new_height = 2 * num_pages * column_height;
			area.setSize(column_width, new_height);
			try {
				Rectangle rect = area.modelToView(data.length() - 1);
				if (rect.y + rect.height <= new_height)
					break;
			} catch (BadLocationException e) {
			}
		}
		return num_pages;
	}

	public boolean print(Graphics2D g2d, Point page_size, int page, int offset,
			boolean page_no) {
		if (page > num_pages + offset - 1)
			return true;
		int scaler = Resource.getInt("print_scaler");
		DrawAWT draw = new DrawAWT();
		draw.init(g2d, scaler, "", false, true);
		ChartData.showDescFootNote(draw, page_size, null, false,
				(!page_no && num_pages == 1) ? 0 : page);
		int y = 2 * (page - offset) * column_height;
		g2d.translate(0, -y);
		// clipping seems to be slightly off, need some extra width
		g2d.setClip(0, y, column_width + column_extra, column_height);
		area.update(g2d);
		g2d.translate(column_width + column_gap, -column_height);
		g2d.setClip(0, y + column_height, column_width + column_extra,
				column_height);
		area.update(g2d);
		return false;
	}
}