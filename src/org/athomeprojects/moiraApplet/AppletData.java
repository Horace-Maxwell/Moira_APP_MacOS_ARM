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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.athomeprojects.base.BaseTab;
import org.athomeprojects.base.Resource;

public class AppletData extends BaseTab {
	private JTextArea text;

	private JScrollPane scroll;

	private AppletDataWrapper container;

	private String data;

	public AppletData(boolean read_only) {
		text = new JTextArea();
		text.setForeground(Color.blue);
		text.setLineWrap(!read_only);
		text.setWrapStyleWord(!read_only);
		text.setEditable(!read_only);
		scroll = new JScrollPane(text);
		setFont();
		clear();
		if (!read_only)
			setNote(null);
	}

	public void setFont() {
		int size = Resource.getDataFontSize();
		Font font = new Font(Resource.getFontName(), Resource.getFontStyle(),
				size);
		text.setFont(font);
		JScrollBar bar = scroll.getVerticalScrollBar();
		if (bar != null) {
			bar.setUnitIncrement(size);
			bar.setBlockIncrement(10 * size);
		}
		bar = scroll.getHorizontalScrollBar();
		if (bar != null) {
			bar.setUnitIncrement(size);
			bar.setBlockIncrement(10 * size);
		}
	}

	public void setContainer(AppletDataWrapper panel) {
		container = panel;
		container.setBackground(Color.white);
		container.setLayout(new BorderLayout());
		container.add(scroll, BorderLayout.CENTER);
		container.setContent(this);
	}

	public void clear() {
		data = "";
		text.setText(data);
	}

	public void setWrapMode(boolean set) {
		text.setLineWrap(set);
		text.setWrapStyleWord(set);
		refresh();
	}

	public void append(String str) {
		data += str;
	}

	public void appendLine() {
		append(EOL);
	}

	public void appendLine(String str) {
		append(str);
		appendLine();
	}

	public void setName(String name, boolean sex, boolean replace) {
		String str = "";
		if (!name.equals(""))
			str += Resource.getString("name") + ": " + name + "  ";
		str += Resource.getString("sex") + ": "
				+ Resource.getString(sex ? "male" : "female");
		appendLine(str);
	}

	public void replace(String src, String dst) {
		data = data.replaceAll(src, dst);
		text.setText(data);
		text.setCaretPosition(0);
	}

	public String getText() {
		return text.getText();
	}

	public String getNote() {
		return text.getText();
	}

	public void setNote(String note) {
		if (note == null || note.trim().equals(""))
			note = Resource.getString("enter_note_here");
		text.setText(note);
	}

	public boolean hasValidNote(String note) {
		return !note.trim().equals(Resource.getString("enter_note_here"));
	}

	public void refresh() {
		scroll.setPreferredSize(container.getSize());
		scroll.invalidate();
	}

	public void update() {
		appendLine();
		text.setText(data);
		text.setCaretPosition(0);
		refresh();
	}
}