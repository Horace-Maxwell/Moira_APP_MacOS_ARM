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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CommandDialog extends Dialog {
	private String command;

	public CommandDialog(Shell parent) {
		super(parent);
		command = "";
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText(Resource.getString("dialog_command_name"));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 300;
		final Text text = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL);
		text.setLayoutData(data);
		text.setText(command);
		text.selectAll();
		text.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				command = text.getText();
			}
		});
		return parent;
	}

	public void setCommand(String str) {
		command = str;
	}

	public String getCommand() {
		return command.trim();
	}
}