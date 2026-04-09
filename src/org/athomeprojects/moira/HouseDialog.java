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

import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class HouseDialog extends Dialog {
	private boolean day_birth;

	private String house;

	private Button[] button;

	public HouseDialog(Shell parent) {
		super(parent);
		day_birth = true;
		house = null;
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(ChartMode
				.isChartMode(ChartMode.PICK_MODE) ? 1 : 2, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(6, false));
		group.setText(Resource.getString("house_correction"));
		String[] house_name = Resource.getStringArray("zodiac");
		button = new Button[house_name.length];
		for (int i = 0; i < house_name.length; i++) {
			button[i] = new Button(group, SWT.RADIO);
			button[i].setText(house_name[i]);
			button[i].setSelection(house.equals(house_name[i]));
			button[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					house = ((Button) event.getSource()).getText();
				}
			});
		}
		if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
			group = new Group(parent, SWT.NONE);
			group.setText(Resource.getString("birth_at"));
			group.setLayout(new GridLayout(2, false));
			final Button day = new Button(group, SWT.RADIO);
			day.setText(Resource.getString("daytime"));
			day.setSelection(day_birth);
			day.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					day_birth = day.getSelection();
				}
			});
			final Button night = new Button(group, SWT.RADIO);
			night.setText(Resource.getString("nighttime"));
			night.setSelection(!day_birth);
			night.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					day_birth = !night.getSelection();
				}
			});
		}
		return parent;
	}

	public void setDayOrNight(boolean day) {
		day_birth = day;
	}

	public boolean getDayOrNight() {
		return day_birth;
	}

	public void setHouse(String value) {
		house = value;
	}

	public String getHouse() {
		return house;
	}
}