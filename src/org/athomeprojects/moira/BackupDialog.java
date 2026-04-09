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

import java.io.File;

import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BackupDialog extends Dialog {
	private boolean backup;

	private String folder;

	private Text text;

	public BackupDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setText(Resource.getString("dialog_backup_name"));
		GridData data = new GridData();
		data.horizontalSpan = 3;
		final Button enable = new Button(group, SWT.CHECK);
		enable.setLayoutData(data);
		enable.setText(Resource.getString("dialog_backup_enable"));
		backup = Resource.getPrefInt("backup") != 0;
		enable.setSelection(backup);
		enable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				backup = enable.getSelection();
			}
		});
		Label title = new Label(group, SWT.RIGHT);
		title.setText(Resource.getString("dialog_backup_dir"));
		folder = Resource.hasPrefKey("backup_dir") ? Resource
				.getPrefString("backup_dir") : "";
		text = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.widthHint = 200;
		text.setLayoutData(data);
		text.setText(folder);
		text.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				folder = text.getText().trim();
			}
		});
		Button browse = new Button(group, SWT.PUSH);
		browse.setText(Resource.getString("dialog_backup_browse"));
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectDirectory();
			}
		});
		return parent;
	}

	private void selectDirectory() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		if (Resource.hasPrefKey("last_open_path"))
			dialog.setFilterPath(Resource.getPrefString("last_open_path"));
		dialog.setMessage(Resource.getString("dialog_backup_browse_dir"));
		Moira.flushEvents(false);
		String path_name = dialog.open();
		if (path_name == null)
			return;
		File file = new File(path_name);
		folder = file.isDirectory() ? path_name.trim() : "";
		text.setText(folder);
	}

	public void updateBackup() {
		File file = new File(folder);
		if (!file.isDirectory()) {
			folder = "";
			backup = false;
		}
		Resource.putPrefInt("backup", backup ? 1 : 0);
		Resource.putPrefString("backup_dir", folder);
	}
}