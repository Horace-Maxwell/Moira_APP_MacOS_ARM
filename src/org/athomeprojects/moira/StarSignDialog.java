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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class StarSignDialog extends Dialog {
	private boolean reset, ten_god_mode;

	private Hashtable master_table, fixstar_table;

	private Button[] button;

	public StarSignDialog(Shell parent) {
		super(parent);
		reset = false;
	}

	public void createButtonsForButtonBar(Composite parent) {
		Button reset_button = createButton(parent, IDialogConstants.NEXT_ID,
				Resource.getString("reset_button"), false);
		reset_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				reset = true;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		Button all_button = createButton(parent,
				IDialogConstants.SELECT_ALL_ID, Resource
						.getString("dialog_sign_select_all"), false);
		all_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				for (int i = 0; i < button.length; i++) {
					Button push = button[i];
					if (push != null && !push.getSelection())
						push.setSelection(true);
				}
				for (Enumeration e = master_table.keys(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					master_table.put(key, "t");
				}
				if (fixstar_table != null) {
					for (Enumeration e = fixstar_table.keys(); e
							.hasMoreElements();) {
						String key = (String) e.nextElement();
						fixstar_table.put(key, "t");
					}
				}
			}
		});
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setFocus();
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		int size = master_table.size();
		if (fixstar_table != null)
			size += fixstar_table.size();
		button = new Button[size];
		int i = 0;
		final String now_year = Resource.getString("current_date");
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(10, false));
		group.setText(Resource.getString("dialog_sign_birth"));
		String[] keys = (String[]) master_table.keySet().toArray(new String[1]);
		Arrays.sort(keys, 0, keys.length);
		for (int k = 0; k < keys.length; k++) {
			if (keys[k].startsWith(now_year))
				continue;
			String val = (String) master_table.get(keys[k]);
			button[i] = new Button(group, SWT.CHECK);
			button[i].setText(keys[k]);
			button[i].setSelection(val.equals("t"));
			button[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					master_table.put(push.getText(), push.getSelection() ? "t"
							: "f");
				}
			});
			i++;
		}
		if (fixstar_table != null) {
			group = new Group(parent, SWT.NONE);
			group.setLayout(new GridLayout(10, false));
			keys = (String[]) fixstar_table.keySet().toArray(new String[1]);
			Arrays.sort(keys, 0, keys.length);
			group.setText(Resource.getString("dialog_sign_fixstar"));
			for (int k = 0; k < keys.length; k++) {
				String val = (String) fixstar_table.get(keys[k]);
				button[i] = new Button(group, SWT.CHECK);
				button[i].setText(keys[k]);
				button[i].setSelection(val.equals("t"));
				button[i].addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						Button push = (Button) event.getSource();
						fixstar_table.put(push.getText(),
								push.getSelection() ? "t" : "f");
					}
				});
				i++;
			}
		} else if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
			group = new Group(parent, SWT.NONE);
			group.setLayout(new GridLayout(10, false));
			group.setText(Resource.getString("dialog_sign_now"));
			for (int k = 0; k < keys.length; k++) {
				if (!keys[k].startsWith(now_year))
					continue;
				String val = (String) master_table.get(keys[k]);
				button[i] = new Button(group, SWT.CHECK);
				button[i].setText(keys[k].substring(2));
				button[i].setSelection(val.equals("t"));
				button[i].addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						Button push = (Button) event.getSource();
						master_table.put(now_year + push.getText(), push
								.getSelection() ? "t" : "f");
					}
				});
				i++;
			}
		}
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText(Resource.getString("dialog_sign_ten_god"));
		ten_god_mode = Resource.getPrefInt("ten_god_mode") != 0;
		Button org_mode = new Button(group, SWT.RADIO);
		org_mode.setText(Resource.getString("dialog_sign_org"));
		org_mode.setSelection(!ten_god_mode);
		org_mode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Button push = (Button) event.getSource();
				ten_god_mode = !push.getSelection();
			}
		});
		Button alt_mode = new Button(group, SWT.RADIO);
		alt_mode.setText(Resource.getString("dialog_sign_alt"));
		alt_mode.setSelection(ten_god_mode);
		alt_mode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Button push = (Button) event.getSource();
				ten_god_mode = push.getSelection();
			}
		});
		return parent;
	}

	public void setDisplayTable(Hashtable[] array) {
		master_table = array[0];
		fixstar_table = array[1];
	}

	public Hashtable[] getDisplayTable() {
		Resource.putPrefInt("ten_god_mode", ten_god_mode ? 1 : 0);
		if (reset)
			return null;
		Hashtable[] array = new Hashtable[2];
		array[0] = master_table;
		array[1] = fixstar_table;
		return array;
	}
}