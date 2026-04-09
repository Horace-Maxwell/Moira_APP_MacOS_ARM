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

import org.athomeprojects.base.Resource;
import org.athomeprojects.swtext.CalendarSpinner;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class LunarDialog extends Dialog {
	private CalendarSpinner spinner;

	private boolean leap_month;

	private int[] lunar_date = new int[5];

	public LunarDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText(Resource.getString("dialog_lunar_name"));
		spinner = new CalendarSpinner(group, SWT.BORDER);
		spinner.init(true);
		ChartTab tab = Moira.getChart();
		int[] date = new int[5];
		tab.getBirthDate(date);
		date = ChartTab.getCal().getLunarDate(date);
		if (date != null) {
			spinner.setCalendar(date);
			leap_month = ChartTab.getCal().isLeapMonth();
		}
		final Button leap = new Button(group, SWT.CHECK);
		leap.setText(Resource.getString("dialog_lunar_leap_month"));
		leap.setSelection(leap_month);
		leap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				leap_month = leap.getSelection();
			}
		});
		return parent;
	}

	public Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData grid_data = new GridData();
		composite.setLayoutData(grid_data);
		composite.setLayout(new GridLayout(2, false));
		Button ok_button = createButton(composite, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		ok_button.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				spinner.getCalendar(lunar_date);
			}
		});
		createButton(composite, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		return composite;
	}

	public int[] getLunarDate() {
		return lunar_date;
	}

	public boolean isLeapMonth() {
		return leap_month;
	}
}