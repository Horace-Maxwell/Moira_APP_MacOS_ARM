//
// Moira - A Chinese Astrology Charting Program
// Copyright (C) 2004-2015 At Home Projects
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package org.athomeprojects.moira;

import org.athomeprojects.base.ChartData;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.City;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
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

public class PlanetDialog extends Dialog {
	private String[] sign_array;

	private int[] order_array, show_array;

	private boolean topocentric, true_as_north, night_fortune_mode,
			asteroids_changed, select_house_system, show_house_system;

	private int altitude;

	private int[] system_index = new int[2];

	private double asc_inf, mc_inf;

	public PlanetDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		select_house_system = !ChartMode.isChartMode(ChartMode.ASTRO_MODE);
		show_house_system = Resource.getPrefInt("show_house_system") != 0;
		GridLayout layout = new GridLayout(select_house_system ? 2 : 1, false);
		layout.marginWidth = layout.marginHeight = 0;
		parent.setLayout(layout);
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		addSignControls(container, Resource.getString("dialog_sign_planet"), 5,
				100);
		Composite composite = new Composite(container, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 20;
		composite.setLayout(layout);
		final Button night_fortune = new Button(composite, SWT.CHECK);
		night_fortune.setText(Resource.getString(Resource
				.getString("dialog_night_fortune")));
		night_fortune_mode = Resource.getPrefInt("night_fortune_mode") != 0;
		night_fortune.setSelection(night_fortune_mode);
		night_fortune.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				night_fortune_mode = night_fortune.getSelection();
			}
		});
		asteroids_changed = false;
		if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
			final Button asteroid = new Button(composite, SWT.PUSH);
			asteroid.setText(Resource.getString(Resource
					.getString("dialog_set_asteroid")));
			asteroid.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					AsteroidDialog dialog = new AsteroidDialog(Moira.getShell());
					if (dialog.open() == Window.OK && dialog.updateAsteroid()) {
						// asteroid list changed
						asteroids_changed = true;
						buttonPressed(IDialogConstants.OK_ID);
					}
					dialog.close();
				}
			});
		}
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText(Resource.getString("dialog_sign_true_select"));
		true_as_north = Resource.getPrefInt("true_as_north") != 0;
		final Button north = new Button(group, SWT.RADIO);
		north.setText(Resource.getString("dialog_sign_true_north"));
		north.setSelection(true_as_north);
		north.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				true_as_north = north.getSelection();
			}
		});
		final Button south = new Button(group, SWT.RADIO);
		south.setText(Resource.getString("dialog_sign_true_south"));
		south.setSelection(!true_as_north);
		south.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				true_as_north = !south.getSelection();
			}
		});
		if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
			asc_inf = Resource.getPrefDouble("asc_influence");
			mc_inf = Resource.getPrefDouble("mc_influence");
			group = new Group(container, SWT.NONE);
			layout = new GridLayout(2, false);
			layout.horizontalSpacing = 20;
			group.setLayout(layout);
			group.setText(Resource.getString("dialog_influence_range"));
			composite = new Composite(group, SWT.NONE);
			layout = new GridLayout(2, false);
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
			layout.horizontalSpacing = 4;
			composite.setLayout(layout);
			Label label = new Label(composite, SWT.NONE);
			label.setText(sign_array[ChartData.ASC]);
			final Text asc = new Text(composite, SWT.BORDER | SWT.RIGHT);
			GridData data = new GridData();
			data.widthHint = 20;
			asc.setLayoutData(data);
			asc.setText(City.formatPos(asc_inf, 1, 1, false));
			asc.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					asc_inf = City.parsePos(asc.getText().trim(), 0.0);
					asc_inf = Math.min(asc_inf, 30.0);
					asc.setText(City.formatPos(asc_inf, 1, 1, false));
				}
			});
			composite = new Composite(group, SWT.NONE);
			layout = new GridLayout(2, false);
			layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
			layout.horizontalSpacing = 4;
			composite.setLayout(layout);
			label = new Label(composite, SWT.NONE);
			label.setText(sign_array[ChartData.MC]);
			final Text mc = new Text(composite, SWT.BORDER | SWT.RIGHT);
			data = new GridData();
			data.widthHint = 20;
			mc.setLayoutData(data);
			mc.setText(City.formatPos(mc_inf, 1, 1, false));
			mc.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					mc_inf = City.parsePos(mc.getText().trim(), 0.0);
					mc_inf = Math.min(mc_inf, 30.0);
					mc.setText(City.formatPos(mc_inf, 1, 1, false));
				}
			});
		}
		group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setText(Resource.getString("dialog_sign_system"));
		topocentric = Resource.getPrefInt("topocentric") != 0;
		altitude = Resource.getPrefInt("altitude");
		final Button geo = new Button(group, SWT.RADIO);
		geo.setText(Resource.getString("dialog_sign_geocentric"));
		geo.setSelection(!topocentric);
		GridData data = new GridData();
		data.widthHint = 55;
		geo.setLayoutData(data);
		final Button topo = new Button(group, SWT.RADIO);
		topo.setText(Resource.getString("dialog_sign_topocentric"));
		topo.setSelection(topocentric);
		composite = new Composite(group, SWT.NONE);
		layout = new GridLayout(3, false);
		layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
		layout.horizontalSpacing = 4;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.NONE);
		label.setText(Resource.getString("dialog_sign_altitude"));
		final Text alt = new Text(composite, SWT.BORDER | SWT.RIGHT);
		alt.setText(FileIO.formatInt(altitude, 6));
		alt.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				altitude = FileIO.parseInt(alt.getText(), altitude, false);
				alt.setText(FileIO.formatInt(altitude, 6));
			}
		});
		label = new Label(composite, SWT.NONE);
		label.setText(Resource.getString("dialog_sign_meter"));
		geo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (geo.getSelection()) {
					topocentric = false;
					alt.setEnabled(false);
				}
			}
		});
		topo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (topo.getSelection()) {
					topocentric = true;
					alt.setEnabled(true);
				}
			}
		});
		alt.setEnabled(topocentric);
		if (select_house_system) {
			final Button enable = new Button(container, SWT.CHECK);
			enable.setText(Resource.getString(Resource
					.getString("dialog_show_house_system")));
			enable.setSelection(show_house_system);
			final Group system_group = new Group(parent, SWT.NONE);
			enable.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					show_house_system = enable.getSelection();
					enableGroup(system_group, show_house_system);
				}
			});
			system_group.setLayout(new GridLayout(1, false));
			SystemDialog
					.addHouseSystemControl(
							system_group,
							"dialog_system_selection",
							ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_house_system_index"
									: "house_system_index", system_index);
			enableGroup(system_group, show_house_system);
		}
		return parent;
	}

	private void enableGroup(Group group, boolean enable) {
		group.setEnabled(enable);
		Control[] array = group.getChildren();
		for (int i = 0; i < array.length; i++)
			array[i].setEnabled(enable);
	}

	public void addSignControls(Composite parent, String title, int num_col,
			int max_num_sign) {
		Group group = new Group(parent, SWT.NONE);
		if (title != null)
			group.setText(title);
		GridData data = new GridData(GridData.FILL_VERTICAL);
		group.setLayoutData(data);
		group.setLayout(new GridLayout(num_col, false));
		max_num_sign = Math.min(max_num_sign, sign_array.length);
		for (int i = 0; i < max_num_sign; i++) {
			int order = order_array[i];
			if (show_array[order] < 0)
				continue;
			Button button = new Button(group, SWT.CHECK);
			button.setText(sign_array[order]);
			button.setSelection(show_array[order] > 0);
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					String name = push.getText();
					for (int j = 0; j < sign_array.length; j++) {
						int index = order_array[j];
						if (name.equals(sign_array[index])) {
							show_array[index] = push.getSelection() ? 1 : 0;
							break;
						}
					}
				}
			});
		}
	}

	public void setSignArray(String[] sign, int[] order, int[] show) {
		sign_array = sign;
		order_array = order;
		show_array = show;
	}

	public boolean updateInfluence() {
		Resource.putPrefInt("true_as_north", true_as_north ? 1 : 0);
		Resource.putPrefInt("night_fortune_mode", night_fortune_mode ? 1 : 0);
		Resource.putPrefInt("topocentric", topocentric ? 1 : 0);
		Resource.putPrefInt("altitude", altitude);
		if (select_house_system) {
			Resource.putPrefInt("show_house_system", show_house_system ? 1 : 0);
			Resource
					.putPrefInt(
							ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_house_system_index"
									: "house_system_index", system_index[0]);
		}
		if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE))
			return false;
		Resource.putPrefDouble("asc_influence", asc_inf);
		Resource.putPrefDouble("mc_influence", mc_inf);
		return asteroids_changed;
	}
}