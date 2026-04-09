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
import org.athomeprojects.base.City;
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

public class AspectDialog extends Dialog {
	private String[] sign_array;

	private int[] show_array;

	private double[] degree_array, orb_array;

	private String check_label;

	private boolean[] check_enabled;

	private Button[] button_group;

	private Text[] sign_group, degree_group, orb_group;

	private String select_title;

	private boolean partial;

	public AspectDialog(Shell parent, boolean angle) {
		super(parent);
		if (angle) {
			select_title = Resource.getString("dialog_sign_angles");
		} else {
			select_title = ChartMode.getModeName()
					+ Resource.getString("chart_char")
					+ Resource.getString("dialog_sign_aspects");
			partial = !ChartMode.getModePrefix().equals("");
		}
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		if (check_label != null) {
			final Button check = new Button(parent, SWT.CHECK);
			check.setText(Resource.getString(check_label));
			if (Resource.hasKey("tip_" + check_label))
				check.setToolTipText(Resource.getString("tip_" + check_label));
			check.setSelection(check_enabled[0]);
			check.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					check_enabled[0] = check.getSelection();
				}
			});
		}
		addAspectsControls(parent, select_title);
		return parent;
	}

	public void addAspectsControls(Composite parent, String group_title) {
		boolean full = orb_array != null;
		boolean editable = full && !partial;
		int field_flags = full ? (SWT.BORDER | SWT.RIGHT) : SWT.RIGHT;
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(full ? 7 : 5, full));
		if (group_title != null)
			group.setText(group_title);
		for (int i = 0; i < 2; i++) {
			Label title = new Label(group, SWT.CENTER);
			title.setText(Resource.getString("aspects_name"));
			title = new Label(group, SWT.CENTER);
			title.setText(Resource.getString("aspects_degree_name"));
			if (full) {
				title = new Label(group, SWT.CENTER);
				title.setText(Resource.getString("aspects_orb_name"));
			}
			if (i == 0)
				new Label(group, SWT.NONE);
		}
		button_group = new Button[sign_array.length];
		sign_group = new Text[sign_array.length];
		degree_group = new Text[sign_array.length];
		if (full)
			orb_group = new Text[sign_array.length];
		for (int i = 0; i < sign_array.length; i++) {
			Composite composite = new Composite(group, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
			layout.horizontalSpacing = 4;
			composite.setLayout(layout);
			button_group[i] = new Button(composite, SWT.CHECK);
			sign_group[i] = new Text(composite, field_flags);
			sign_group[i].setEditable(editable);
			GridData data = new GridData();
			data.widthHint = 20;
			sign_group[i].setLayoutData(data);
			sign_group[i].setText(sign_array[i]);
			if (editable) {
				sign_group[i].addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						Text text = (Text) event.getSource();
						for (int j = 0; j < sign_group.length; j++) {
							if (text == sign_group[j]) {
								String str = text.getText().trim();
								if (!str.equals("") && str.indexOf(",") < 0
										&& str.indexOf(" ") < 0) {
									sign_array[j] = (str.length() > 2) ? str
											.substring(0, 2) : str;
								}
								text.setText(sign_array[j]);
								break;
							}
						}
					}
				});
			}
			button_group[i].setSelection(show_array[i] != 0);
			button_group[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					for (int j = 0; j < button_group.length; j++) {
						if (push == button_group[j]) {
							show_array[j] = push.getSelection() ? 1 : 0;
							break;
						}
					}
				}
			});
			degree_group[i] = new Text(group, field_flags);
			degree_group[i].setEditable(editable);
			degree_group[i].setText(City.formatPos(degree_array[i], 3, 1,
					!editable));
			if (editable) {
				data = new GridData();
				data.widthHint = 30;
				degree_group[i].setLayoutData(data);
				degree_group[i].addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						Text text = (Text) event.getSource();
						for (int j = 0; j < degree_group.length; j++) {
							if (text == degree_group[j]) {
								degree_array[j] = City.parsePos(text.getText()
										.trim(), 0.0);
								text.setText(City.formatPos(degree_array[j], 3,
										1, false));
								break;
							}
						}
					}
				});
			}
			if (full) {
				orb_group[i] = new Text(group, SWT.BORDER | SWT.RIGHT);
				data = new GridData();
				data.widthHint = 30;
				orb_group[i].setLayoutData(data);
				orb_group[i].setText(City.formatPos(orb_array[i], 3, 1, false));
				orb_group[i].addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						Text text = (Text) event.getSource();
						for (int j = 0; j < orb_group.length; j++) {
							if (text == orb_group[j]) {
								orb_array[j] = City.parsePos(text.getText()
										.trim(), 0.0);
								text.setText(City.formatPos(orb_array[j], 3, 1,
										false));
								break;
							}
						}
					}
				});
			}
			if (i % 2 == 0)
				new Label(group, SWT.NONE);
		}
	}

	public void setSignArray(String[] sign, double[] degree, double[] orb,
			int[] show, String check_text, boolean[] check_value) {
		sign_array = sign;
		degree_array = degree;
		orb_array = orb;
		show_array = show;
		check_label = check_text;
		check_enabled = check_value;
	}
}