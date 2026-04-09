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

import org.athomeprojects.base.ChartMode;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class PickDialog extends Dialog {
	private boolean true_north, adj_north, align_north, main_mountain,
			quick_azimuth;

	private int mountain_mode, degree_mode;

	private int[] system_index = new int[2];

	private Button align;

	private Group base_group, system_group;

	private ModeDialog mode_dialog;

	public PickDialog(Shell parent) {
		super(parent);
		mode_dialog = new ModeDialog(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		final Button adjust = new Button(parent, SWT.CHECK);
		adjust.setText(Resource.getString(Resource
				.getString("dialog_adjust_north")));
		adj_north = Resource.getPrefInt("adj_north") != 0;
		adjust.setSelection(adj_north);
		adjust.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				adj_north = adjust.getSelection();
			}
		});
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		{
			Group container_group = new Group(composite, SWT.NONE);
			container_group.setText(Resource
					.getString("dialog_azimuth_selection"));
			container_group.setLayout(new GridLayout(1, false));
			Group group = new Group(container_group, SWT.NONE);
			group.setLayout(new GridLayout(2, false));
			quick_azimuth = Resource.getPrefInt("quick_azimuth") != 0;
			final Button local_method = new Button(group, SWT.RADIO);
			local_method.setText(Resource.getString("local_horizon_method"));
			local_method.setSelection(!quick_azimuth);
			local_method.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (local_method.getSelection()) {
						quick_azimuth = false;
						enableBaseGroup();
					}
				}
			});
			final Button quick_method = new Button(group, SWT.RADIO);
			quick_method.setText(Resource.getString("quick_azimuth_method"));
			quick_method.setSelection(quick_azimuth);
			quick_method.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (quick_method.getSelection()) {
						quick_azimuth = true;
						enableBaseGroup();
					}
				}
			});
			base_group = new Group(container_group, SWT.NONE);
			base_group.setLayout(new GridLayout(2, false));
			base_group.setText(Resource.getString("dialog_earth_degree"));
			degree_mode = Resource.getPrefInt("degree_mode");
			final Button mountain = new Button(base_group, SWT.RADIO);
			mountain.setText(Resource.getString("dialog_mountain_based"));
			mountain.setSelection(degree_mode == ChartMode.MOUNTAIN_MODE);
			mountain.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (mountain.getSelection())
						degree_mode = ChartMode.MOUNTAIN_MODE;
				}
			});
			final Button zodiac = new Button(base_group, SWT.RADIO);
			zodiac.setText(Resource.getString("dialog_zodiac_based"));
			zodiac.setSelection(degree_mode == ChartMode.ZODIAC_MODE);
			zodiac.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (zodiac.getSelection())
						degree_mode = ChartMode.ZODIAC_MODE;
				}
			});
			group = new Group(container_group, SWT.NONE);
			group.setText(Resource.getString("dialog_north_selection"));
			group.setLayout(new GridLayout(2, false));
			true_north = Resource.getPrefInt("true_north") != 0;
			final Button b_true_north = new Button(group, SWT.RADIO);
			b_true_north.setText(Resource.getString("dialog_true_north"));
			b_true_north.setSelection(true_north);
			b_true_north.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (b_true_north.getSelection()) {
						true_north = true;
						align.setEnabled(true);
					}
				}
			});
			final Button b_magnetic_north = new Button(group, SWT.RADIO);
			b_magnetic_north.setText(Resource
					.getString("dialog_magnetic_north"));
			b_magnetic_north.setSelection(!true_north);
			b_magnetic_north.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (b_magnetic_north.getSelection()) {
						true_north = false;
						align.setEnabled(false);
					}
				}
			});
			align = new Button(group, SWT.CHECK);
			GridData data = new GridData();
			data.horizontalSpan = 2;
			align.setLayoutData(data);
			align.setEnabled(true_north);
			align.setText(Resource.getString(Resource
					.getString("dialog_align_north")));
			align_north = Resource.getPrefInt("align_north") != 0;
			align.setSelection(align_north);
			align.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					align_north = align.getSelection();
				}
			});
		}
		{
			system_group = new Group(composite, SWT.NONE);
			GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			data.verticalSpan = 2;
			system_group.setLayoutData(data);
			system_group.setLayout(new GridLayout(1, false));
			SystemDialog.addHouseSystemControl(system_group,
					"dialog_pick_system_selection", "pick_house_system_index",
					system_index);
		}
		{
			Group container_group = new Group(composite, SWT.NONE);
			container_group.setText(Resource.getString("dialog_mountain_mode"));
			container_group.setLayout(new GridLayout(1, false));
			Group group = new Group(container_group, SWT.NONE);
			group.setLayout(new GridLayout(3, false));
			mountain_mode = Resource.getPrefInt("mountain_mode");
			final Button sky = new Button(group, SWT.RADIO);
			sky.setText(Resource.getString("dialog_sky_mountain"));
			sky.setSelection(mountain_mode == 1);
			sky.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (sky.getSelection())
						mountain_mode = 1;
				}
			});
			final Button earth = new Button(group, SWT.RADIO);
			earth.setText(Resource.getString("dialog_earth_mountain"));
			earth.setSelection(mountain_mode == 0);
			earth.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (earth.getSelection())
						mountain_mode = 0;
				}
			});
			final Button people = new Button(group, SWT.RADIO);
			people.setText(Resource.getString("dialog_people_mountain"));
			people.setSelection(mountain_mode == 2);
			people.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (people.getSelection())
						mountain_mode = 2;
				}
			});
			group = new Group(container_group, SWT.NONE);
			group.setLayout(new GridLayout(2, false));
			main_mountain = Resource.getPrefInt("main_mountain") != 0;
			final Button main = new Button(group, SWT.RADIO);
			main.setText(Resource.getString("dialog_main_mountain"));
			main.setSelection(main_mountain);
			main.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (main.getSelection())
						main_mountain = true;
				}
			});
			final Button secondary = new Button(group, SWT.RADIO);
			secondary.setText(Resource.getString("dialog_secondary_mountain"));
			secondary.setSelection(!main_mountain);
			secondary.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (secondary.getSelection())
						main_mountain = false;
				}
			});
		}
		mode_dialog.setMode(-1);
		mode_dialog.createDialogArea(parent);
		enableBaseGroup();
		return parent;
	}

	private void enableBaseGroup() {
		enableGroup(base_group, !quick_azimuth);
		enableGroup(system_group, quick_azimuth);
	}

	private void enableGroup(Group group, boolean enable) {
		group.setEnabled(enable);
		Control[] array = group.getChildren();
		for (int i = 0; i < array.length; i++)
			array[i].setEnabled(enable);
	}

	public boolean updatePick() {
		boolean changed = mode_dialog.updateChartMode()
				|| (Resource.getPrefInt("true_north") != 0) != true_north
				|| (Resource.getPrefInt("main_mountain") != 0) != main_mountain
				|| (Resource.getPrefInt("adj_north") != 0) != adj_north
				|| Resource.getPrefInt("mountain_mode") != mountain_mode
				|| Resource.getPrefInt("degree_mode") != degree_mode
				|| (Resource.getPrefInt("align_north") != 0) != align_north
				|| (Resource.getPrefInt("quick_azimuth") != 0) != quick_azimuth
				|| system_index[0] != system_index[1];
		Resource.putPrefInt("true_north", true_north ? 1 : 0);
		Resource.putPrefInt("main_mountain", main_mountain ? 1 : 0);
		Resource.putPrefInt("adj_north", adj_north ? 1 : 0);
		Resource.putPrefInt("mountain_mode", mountain_mode);
		Resource.putPrefInt("degree_mode", degree_mode);
		Resource.putPrefInt("align_north", align_north ? 1 : 0);
		Resource.putPrefInt("quick_azimuth", quick_azimuth ? 1 : 0);
		Resource.putPrefInt("pick_house_system_index", system_index[0]);
		return changed;
	}
}