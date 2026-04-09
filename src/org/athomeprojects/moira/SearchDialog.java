//
// Moira - A Chinese Astrology Charting Program
// Copyright (C) 2004-2015 At Home Projects
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package org.athomeprojects.moira;

import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.Calculate;
import org.athomeprojects.base.City;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SearchDialog extends Dialog {
	private boolean forward, has_pos, use_current, anywhere;

	private Text search_date, any_value, degree_value, speed_value;

	private String mode, start_date;

	private Button any;

	private String[] selection_name, selection_value;

	private int period, def_value;

	private double pos_value, speed;

	public SearchDialog(Shell parent) {
		super(parent);
	}

	public void setSearchMode(String str, boolean start, boolean current,
			boolean pos) {
		mode = str;
		forward = start;
		use_current = current;
		has_pos = pos;
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setText(Resource.getString("dialog_" + mode + "_time"));
		group.setLayout(new GridLayout(2, false));
		Composite composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		final Button start_at = new Button(composite, SWT.RADIO);
		start_at.setText(Resource.getString("start_at"));
		start_at.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (start_at.getSelection())
					forward = true;
			}
		});
		final Button end_at = new Button(composite, SWT.RADIO);
		end_at.setText(Resource.getString("end_at"));
		end_at.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (end_at.getSelection())
					forward = false;
			}
		});
		search_date = new Text(group, SWT.CENTER | SWT.BORDER);
		int[] date = new int[5];
		if (use_current)
			BaseCalendar.getCalendar(null, date);
		else
			Moira.getChart().getBirthDate(date);
		start_date = BaseCalendar.formatDate(date, false, false);
		search_date.setText(start_date);
		search_date.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				start_date = BaseCalendar.auditDay(search_date.getText(), null);
				search_date.setText(start_date);
			}
		});
		if (has_pos) {
			composite = new Composite(group, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
			layout.horizontalSpacing = 4;
			composite.setLayout(layout);
			Label label = new Label(composite, SWT.NONE);
			label.setText(Resource.spacePreFilled(Resource.getString("dialog_"
					+ mode + "_pos"), 4));
			degree_value = new Text(composite, SWT.RIGHT | SWT.BORDER);
			GridData data = new GridData();
			data.widthHint = 30;
			degree_value.setLayoutData(data);
			pos_value = City.normalizeDegree(City.parseMapPos(ChartTab
					.getData().getMountainPos(false)) + 180.0);
			degree_value.setText(City.formatMapPos(pos_value, false));
			degree_value.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					pos_value = City.parseMapPos(degree_value.getText());
					degree_value.setText(City.formatMapPos(pos_value, false));
				}
			});
			composite = new Composite(group, SWT.NONE);
			layout = new GridLayout(3, false);
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
			layout.horizontalSpacing = 4;
			composite.setLayout(layout);
			label = new Label(composite, SWT.NONE);
			label.setText(Resource.getString("dialog_" + mode + "_speed"));
			speed_value = new Text(composite, SWT.RIGHT | SWT.BORDER);
			data = new GridData();
			data.widthHint = 20;
			speed_value.setLayoutData(data);
			speed = Resource.getPrefDouble(mode + "_max_speed");
			speed_value.setText(FileIO.formatDouble(speed, 1, 2, false, false));
			speed_value.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					speed = FileIO.parseDouble(speed_value.getText(), Resource
							.getDouble(mode + "_max_speed"), true);
					speed = Math.min(speed, Calculate.MAX_SPEED);
					speed_value.setText(FileIO.formatDouble(speed, 1, 2, false,
							false));
				}
			});
			composite.setLayout(layout);
			label = new Label(composite, SWT.NONE);
			label.setText(Resource.getString("dialog_" + mode + "_speed_unit"));
		}
		group = new Group(parent, SWT.NONE);
		group.setText(Resource.getString("dialog_search_range"));
		selection_name = Resource.getStringArray(mode + "_selection");
		selection_value = Resource.getStringArray(mode + "_value");
		def_value = Resource.getInt(mode + "_default");
		group.setLayout(new GridLayout(selection_name.length + 1, false));
		final Button[] button = new Button[selection_name.length];
		for (int i = 0; i < selection_name.length; i++) {
			button[i] = new Button(group, SWT.RADIO);
			GridData data = new GridData();
			data.widthHint = 50;
			button[i].setLayoutData(data);
			button[i].setText(selection_name[i]);
			button[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					if (!push.getSelection())
						return;
					any.setSelection(false);
					String name = push.getText();
					for (int j = 0; j < selection_name.length; j++) {
						if (name.equals(selection_name[j])) {
							setAnyValue(selection_value[j], def_value);
							break;
						}
					}
					any_value.setEditable(false);
				}
			});
		}
		composite = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
		layout.horizontalSpacing = 4;
		composite.setLayout(layout);
		any = new Button(composite, SWT.RADIO);
		any.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (!any.getSelection())
					return;
				for (int j = 0; j < button.length; j++) {
					button[j].setSelection(false);
				}
				any_value.setEditable(true);
			}
		});
		any_value = new Text(composite, SWT.CENTER | SWT.BORDER);
		GridData data = new GridData();
		data.widthHint = 20;
		any_value.setLayoutData(data);
		setAnyValue("", def_value);
		any_value.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				setAnyValue(any_value.getText(), def_value);
			}
		});
		Label label = new Label(composite, SWT.NONE);
		label.setText(Resource.getString(mode + "_unit_char"));
		boolean select_any = true;
		for (int j = 0; j < button.length; j++) {
			if (selection_value[j].equals(Integer.toString(period))) {
				button[j].setSelection(true);
				select_any = false;
			} else {
				button[j].setSelection(false);
			}
		}
		if (Resource.hasKey(mode + "_anywhere")) {
			anywhere = Resource.getPrefInt(mode + "_anywhere") != 0;
			final Button any_place = new Button(parent, SWT.CHECK);
			any_place.setText(Resource
					.getString("dialog_eclipse_solar_anywhere"));
			any_place.setSelection(anywhere);
			any_place.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					anywhere = any_place.getSelection();
				}
			});
		}
		any.setSelection(select_any);
		any_value.setEditable(select_any);
		start_at.setSelection(forward);
		end_at.setSelection(!forward);
		return parent;
	}

	private void setAnyValue(String val, int def_val) {
		period = FileIO.parseInt(val, def_val, true);
		any_value.setText(FileIO.formatInt(period, 4));
	}

	public void updateSearch() {
		Resource.putPrefInt("search_forward", forward ? 1 : 0);
		Resource.putPrefString("search_date", start_date);
		if (has_pos) {
			Resource.putPrefDouble("search_degree", pos_value);
			Resource.putPrefInt("search_period", period);
			Resource.putPrefDouble(mode + "_max_speed", speed);
		} else {
			Resource.putPrefInt("search_period", 12 * period);
		}
		if (Resource.hasKey(mode + "_anywhere"))
			Resource.putPrefInt(mode + "_anywhere", anywhere ? 1 : 0);
	}
}