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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class StyleDialog extends Dialog {
	private boolean show_style;

	private int style_level;

	private Group group;

	private Slider slider;

	public StyleDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		show_style = Resource.getPrefInt("show_style") != 0;
		final Button check = new Button(parent, SWT.CHECK);
		check.setText(Resource.getString("dialog_style_show"));
		check.setSelection(show_style);
		style_level = Resource.getPrefInt("style_level");
		int[] style_range = Resource.getIntArray("style_range");
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		final Label min_label = new Label(group, SWT.NONE);
		min_label.setText(Resource.getString("dialog_style_level_min"));
		group.setText(Resource.getString("dialog_style_level"));
		slider = new Slider(group, SWT.HORIZONTAL);
		slider.setValues(style_level, style_range[0], style_range[1] + 1, 1, 1,
				style_range[1]);
		slider.setToolTipText(group.getText() + ":" + style_level);
		slider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int val = slider.getSelection();
				if (val != style_level) {
					style_level = val;
					slider.setToolTipText(group.getText() + ":" + style_level);
				}
			}
		});
		final Label max_label = new Label(group, SWT.NONE);
		max_label.setText(Resource.getString("dialog_style_level_max"));
		group.setEnabled(show_style);
		check.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				show_style = check.getSelection();
				if (group.getEnabled() != show_style) {
					group.setEnabled(show_style);
					min_label.setEnabled(show_style);
					max_label.setEnabled(show_style);
				}
			}
		});
		return parent;
	}

	public void updateStyle() {
		Resource.putPrefInt("show_style", show_style ? 1 : 0);
		Resource.putPrefInt("style_level", style_level);
	}
}