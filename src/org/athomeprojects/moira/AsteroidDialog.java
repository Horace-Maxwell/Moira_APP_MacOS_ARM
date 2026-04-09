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
// All named asteroids
// http://www.astro.com/swisseph/astlist.htm
//
package org.athomeprojects.moira;

import org.athomeprojects.base.Calculate;
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

public class AsteroidDialog extends Dialog {
	private final int MAX_NUM_ASTEROIDS = 256;

	private final int MIN_NUM_ASTEROIDS = 8;

	private boolean[] show_array;

	private String[] sign_array;

	private int[] number_array;

	private Button[] button_group;

	private Text[] sign_group, number_group;

	private String asteroids;

	public AsteroidDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		sign_array = new String[MAX_NUM_ASTEROIDS];
		number_array = new int[MAX_NUM_ASTEROIDS];
		show_array = new boolean[MAX_NUM_ASTEROIDS];
		int num_asteroids = Calculate.getAsteroidData(sign_array, number_array,
				show_array, null);
		asteroids = (num_asteroids == 0) ? "" : Resource
				.getPrefString("asteroids");
		num_asteroids = Math.max(num_asteroids + 1, MIN_NUM_ASTEROIDS);
		if ((num_asteroids % 2) == 1)
			num_asteroids++;
		num_asteroids = Math.min(num_asteroids, MAX_NUM_ASTEROIDS);
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(5, false));
		group.setText(Resource.getString("dialog_asteroid_select"));
		for (int i = 0; i < 2; i++) {
			Label title = new Label(group, SWT.CENTER);
			title.setText(Resource.getString("asteroids_name"));
			title = new Label(group, SWT.CENTER);
			title.setText(Resource.getString("asteroids_number"));
			if (i == 0)
				new Label(group, SWT.NONE);
		}
		button_group = new Button[num_asteroids];
		sign_group = new Text[num_asteroids];
		number_group = new Text[num_asteroids];
		for (int i = 0; i < num_asteroids; i++) {
			Composite composite = new Composite(group, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
			layout.horizontalSpacing = 4;
			composite.setLayout(layout);
			button_group[i] = new Button(composite, SWT.CHECK);
			sign_group[i] = new Text(composite, SWT.BORDER | SWT.LEFT);
			sign_group[i].setEditable(true);
			GridData data = new GridData();
			data.widthHint = 150;
			sign_group[i].setLayoutData(data);
			sign_group[i].setText(sign_array[i]);
			sign_group[i].addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					Text text = (Text) event.getSource();
					for (int j = 0; j < sign_group.length; j++) {
						if (text == sign_group[j]) {
							String str = text.getText().trim();
							if (!str.equals("") && str.indexOf(':') < 0
									&& str.indexOf(',') < 0) {
								if (Calculate.extractAsteroidKey(str) == null)
									str = "";
								sign_array[j] = str;
							}
							text.setText(sign_array[j]);
							break;
						}
					}
				}
			});
			button_group[i].setSelection(show_array[i]);
			button_group[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					for (int j = 0; j < button_group.length; j++) {
						if (push == button_group[j]) {
							show_array[j] = push.getSelection();
							break;
						}
					}
				}
			});
			number_group[i] = new Text(group, SWT.BORDER | SWT.RIGHT);
			number_group[i].setEditable(true);
			number_group[i].setText((number_array[i] <= 0) ? "" : FileIO
					.formatInt(number_array[i], 6));
			data = new GridData();
			data.widthHint = 30;
			number_group[i].setLayoutData(data);
			number_group[i].addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					Text text = (Text) event.getSource();
					for (int j = 0; j < number_group.length; j++) {
						if (text == number_group[j]) {
							number_array[j] = FileIO.parseInt(text.getText()
									.trim(), 0, true);
							text.setText((number_array[j] <= 0) ? "" : FileIO
									.formatInt(number_array[j], 6));
							break;
						}
					}
				}
			});
			if (i % 2 == 0)
				new Label(group, SWT.NONE);
		}
		return parent;
	}

	public boolean updateAsteroid() {
		String str = setAsteroidData(sign_array, number_array, show_array);
		if (asteroids.equals(str))
			return false;
		if (str.equals(""))
			Resource.removePref("asteroids");
		else
			Resource.putPrefString("asteroids", str);
		return true;
	}

	private String setAsteroidData(String[] name, int[] number, boolean[] show) {
		String str = null;
		for (int i = 0; i < name.length; i++) {
			if (number[i] <= 0)
				continue;
			String key = name[i].trim();
			if (key.equals(""))
				continue;
			key += ":" + number[i] + ":" + (show[i] ? "1" : "0");
			if (str == null)
				str = key;
			else
				str += ", " + key;
		}
		if (str == null)
			str = "";
		return str;
	}
}