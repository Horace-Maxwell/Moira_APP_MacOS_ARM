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
import org.eclipse.swt.widgets.Shell;

public class DataSetDialog extends Dialog {
	private boolean save_data, save_pick;

	private Button data, pick;

	public DataSetDialog(Shell parent) {
		super(parent);
		save_data = save_pick = true;
	}

	public Control createDialogArea(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText(Resource.getString("dialog_save_set"));
		data = new Button(group, SWT.CHECK);
		data.setText(Resource.getString("dialog_save_data"));
		data.setSelection(save_data);
		data.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				save_data = data.getSelection();
			}
		});
		pick = new Button(group, SWT.CHECK);
		pick.setText(Resource.getString("dialog_save_pick"));
		pick.setSelection(save_pick);
		pick.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				save_pick = pick.getSelection();
			}
		});
		return parent;
	}

	public boolean saveData() {
		return save_data;
	}

	public boolean savePick() {
		return save_pick;
	}
}