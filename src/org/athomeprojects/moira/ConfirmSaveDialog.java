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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ConfirmSaveDialog extends Dialog {
	private boolean no_confirm_save;

	private int state;

	public ConfirmSaveDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 16;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.NONE);
		label.setText(Resource.getString("dialog_need_to_save"));
		no_confirm_save = Resource.getPrefInt("no_confirm_save") != 0;
		final Button no_confirm = new Button(composite, SWT.CHECK);
		no_confirm.setSelection(no_confirm_save);
		no_confirm.setText(Resource.getString("dialog_no_confirm_save"));
		no_confirm.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				no_confirm_save = no_confirm.getSelection();
			}
		});
		return parent;
	}

	public void createButtonsForButtonBar(Composite parent) {
		Button yes_button = createButton(parent, IDialogConstants.YES_ID,
				IDialogConstants.YES_LABEL, true);
		yes_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				state = 1;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		Button no_button = createButton(parent, IDialogConstants.NO_ID,
				IDialogConstants.NO_LABEL, false);
		no_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				state = 0;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		Button cancel_button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		cancel_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				state = -1;
			}
		});
	}

	public int updateConfirmSave() {
		Resource.putPrefInt("no_confirm_save", no_confirm_save ? 1 : 0);
		return state;
	}
}