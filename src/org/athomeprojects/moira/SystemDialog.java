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

public class SystemDialog extends Dialog {
	private int[] index_array = new int[2];

	public SystemDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(3, true));
		addHouseSystemControl(group, "dialog_system_selection",
				"house_system_index", index_array);
		return parent;
	}

	static public void addHouseSystemControl(Group group, String group_res,
			String index_name, int[] data) {
		final int[] array = data;
		array[0] = array[1] = Resource.getPrefInt(index_name);
		final String[] systems = Resource.getStringArray("house_system");
		group.setText(Resource.getString(group_res));
		for (int i = 0; i < systems.length; i++) {
			Button choice = new Button(group, SWT.RADIO);
			choice.setText(systems[i]);
			choice.setSelection(i == array[0]);
			choice.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					if (!push.getSelection())
						return;
					String name = push.getText();
					for (int j = 0; j < systems.length; j++) {
						if (name.equals(systems[j])) {
							array[0] = j;
							break;
						}
					}
				}
			});
		}
	}

	public boolean updateSystem() {
		Resource.putPrefInt("house_system_index", index_array[0]);
		return index_array[0] != index_array[1];
	}
}