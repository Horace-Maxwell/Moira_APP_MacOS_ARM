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

import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditDialog extends Dialog {
	private String[] sign_array;

	private boolean[] flip_array;

	private int[] display_array;

	private double[] degree_array;

	private double[] cusp_array;

	private String eight_char;

	private Button[] button_group;

	private Text[] degree_group;

	private Text[] cusp_group;

	private boolean reset;

	public EditDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setText(Resource.getString("dialog_edit_sign"));
		group.setLayout(new GridLayout(7, false));
		for (int i = 0; i < 2; i++) {
			Label title = new Label(group, SWT.RIGHT);
			title.setText(Resource.getString("dialog_edit_sign_sign"));
			title = new Label(group, SWT.RIGHT);
			GridData data = new GridData();
			data.widthHint = 30;
			title.setLayoutData(data);
			title.setText(Resource.getString("dialog_edit_sign_offset"));
			title = new Label(group, SWT.RIGHT);
			title.setText(Resource.getString("dialog_edit_sign_flip"));
			if (i == 0) {
				Label spacer = new Label(group, SWT.NONE);
				data = new GridData();
				data.widthHint = 20;
				spacer.setLayoutData(data);
			}
		}
		button_group = new Button[sign_array.length];
		degree_group = new Text[sign_array.length];
		int count = 0;
		for (int i = 0; i < sign_array.length; i++) {
			if (display_array[i] <= 0)
				continue;
			Label label = new Label(group, SWT.RIGHT);
			label.setText(sign_array[i]);
			degree_group[i] = new Text(group, SWT.BORDER | SWT.RIGHT);
			degree_group[i].setText(FileIO.formatDouble(degree_array[i], 3, 1,
					false, false));
			GridData data = new GridData();
			data.widthHint = 30;
			degree_group[i].setLayoutData(data);
			degree_group[i].addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					Text text = (Text) event.getSource();
					for (int j = 0; j < degree_group.length; j++) {
						if (text == degree_group[j]) {
							degree_array[j] = FileIO.parseDouble(text.getText()
									.trim(), 0.0, false);
							text.setText(FileIO.formatDouble(degree_array[j],
									3, 1, false, false));
							break;
						}
					}
				}
			});
			button_group[i] = new Button(group, SWT.CHECK);
			button_group[i].setSelection(flip_array[i]);
			button_group[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					for (int j = 0; j < button_group.length; j++) {
						if (push == button_group[j]) {
							flip_array[j] = push.getSelection();
							break;
						}
					}
				}
			});
			if ((count++) % 2 == 0)
				new Label(group, SWT.NONE);
		}
		if (cusp_array != null) {
			group = new Group(parent, SWT.NONE);
			group.setText(Resource.getString("dialog_edit_house"));
			group.setLayout(new GridLayout(6, false));
			String[] house_name = Resource.getStringArray("house_name");
			cusp_group = new Text[house_name.length];
			for (int i = 0; i < house_name.length; i++) {
				Label title = new Label(group, SWT.RIGHT);
				title.setText(house_name[i]);
				cusp_group[i] = new Text(group, SWT.BORDER | SWT.RIGHT);
				cusp_group[i].setText(FileIO.formatDouble(cusp_array[i + 1], 3,
						1, false, false));
				GridData data = new GridData();
				data.widthHint = 40;
				cusp_group[i].setLayoutData(data);
				cusp_group[i].addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						Text text = (Text) event.getSource();
						for (int j = 0; j < cusp_group.length; j++) {
							if (text == cusp_group[j]) {
								cusp_array[j + 1] = FileIO.parseDouble(text
										.getText().trim(), 0.0, true);
								text.setText(FileIO.formatDouble(
										cusp_array[j + 1], 3, 1, false, false));
								break;
							}
						}
					}
				});
			}
		}
		group = new Group(parent, SWT.NONE);
		group.setText(Resource.getString("dialog_edit_eight_char"));
		group.setLayout(new GridLayout(1, false));
		final Text text = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL);
		GridData data = new GridData();
		data.widthHint = 200;
		text.setLayoutData(data);
		text.setText(eight_char);
		text.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				eight_char = text.getText().trim();
			}
		});
		return parent;
	}

	public void createButtonsForButtonBar(Composite parent) {
		Button reset_button = createButton(parent, IDialogConstants.NEXT_ID,
				Resource.getString("reset_button"), false);
		reset = false;
		reset_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				reset = true;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setFocus();
	}

	public void setSignArray(String[] sign, double[] degree, boolean[] flip,
			double[] cusp, String poles, int[] show) {
		sign_array = sign;
		degree_array = degree;
		flip_array = flip;
		cusp_array = cusp;
		eight_char = poles;
		display_array = show;
	}

	public String getEightChar() {
		return eight_char;
	}

	public boolean updateEdit() {
		if (reset) {
			Arrays.fill(degree_array, 0.0);
			Arrays.fill(flip_array, false);
			if (cusp_array != null)
				Arrays.fill(cusp_array, -1.0);
			eight_char = "";
		}
		return true;
	}
}