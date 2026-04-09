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

public class SiderealDialog extends Dialog {
	private int astro_system_mode, astro_sidereal_index;

	private Group mode;

	public SiderealDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, true));
		astro_system_mode = Resource.getPrefInt("astro_system_mode");
		group.setText(Resource.getString("dialog_sidereal_system_selection"));
		Button tropical = new Button(group, SWT.RADIO);
		tropical.setText(Resource.getString("tropical_mode"));
		tropical.setSelection(astro_system_mode == 0);
		tropical.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Resource.putPrefInt("astro_system_mode", 0);
				setSiderealMode(false);
			}
		});
		Button sidereal = new Button(group, SWT.RADIO);
		sidereal.setText(Resource.getString("sidereal_mode"));
		sidereal.setSelection(astro_system_mode != 0);
		sidereal.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Resource.putPrefInt("astro_system_mode", 1);
				setSiderealMode(true);
			}
		});
		mode = new Group(parent, SWT.NONE);
		mode.setLayout(new GridLayout(2, true));
		astro_sidereal_index = Resource.getPrefInt("astro_sidereal_index");
		final String[] sidereal_systems = Resource
				.getStringArray("astro_sidereal_system");
		mode.setText(Resource.getString("dialog_sidereal_selection"));
		for (int i = 0; i < sidereal_systems.length; i++) {
			Button choice = new Button(mode, SWT.RADIO);
			choice.setText(sidereal_systems[i]);
			choice.setSelection(i == astro_sidereal_index);
			choice.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					if (!push.getSelection())
						return;
					String name = push.getText();
					for (int j = 0; j < sidereal_systems.length; j++) {
						if (name.equals(sidereal_systems[j])) {
							Resource.putPrefInt("astro_sidereal_index", j);
							break;
						}
					}
				}
			});
		}
		setSiderealMode(astro_system_mode != 0);
		return parent;
	}

	private void setSiderealMode(boolean enable) {
		mode.setEnabled(enable);
		TransitDialog.enableComposite(mode, enable);
	}

	public boolean updateSiderealSystem() {
		return Resource.getPrefInt("astro_system_mode") != astro_system_mode
				|| Resource.getPrefInt("astro_sidereal_index") != astro_sidereal_index;
	}
}