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
package org.athomeprojects.moira;

import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EightCharDialog extends Dialog {
	private int init_year, year;

	private String eight_char;

	public EightCharDialog(Shell parent) {
		super(parent);
		int[] date = new int[5];
		BaseCalendar.getCalendar(null, date);
		init_year = year = date[0];
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText(Resource.getString("dialog_eight_char_data"));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 160;
		final Text text = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL);
		text.setLayoutData(data);
		text.setText((eight_char == null) ? "" : eight_char);
		text.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				eight_char = text.getText();
			}
		});
		group = new Group(parent, SWT.NONE);
		group.setText(Resource.getString("end_at"));
		group.setLayout(new GridLayout(2, false));
		final Text date = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL);
		date.setText(FileIO.formatInt(year, 4));
		date.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				year = FileIO.parseInt(date.getText(), init_year, false);
				date.setText(FileIO.formatInt(year, 4));
			}
		});
		Label label = new Label(group, SWT.NONE);
		label.setText(Resource.getString("year_char"));
		return parent;
	}

	public String getEightChar() {
		return (eight_char == null) ? "" : eight_char.trim();
	}

	public int getYear() {
		return year;
	}
}