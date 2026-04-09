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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ModeDialog extends Dialog {
	private boolean house_mode, adjust_mode;

	private int dialog_mode, chart_mode;

	private String prefix;

	private Shell shell;

	private Composite house_composite;

	private Button adjust, current, ancient;

	public ModeDialog(Shell parent) {
		super(parent);
		shell = parent;
		dialog_mode = 0;
		prefix = "";
	}

	// mode: 1 => init mode selection, 0 => normal mode selection, -1 => part of
	// pick mode
	public void setMode(int mode) {
		dialog_mode = mode;
		prefix = (dialog_mode < 0) ? "pick_" : "";
	}

	public Control createDialogArea(Composite parent) {
		if (dialog_mode == 0) {
			chart_mode = ChartMode.getChartMode();
		} else if (dialog_mode > 0) {
			chart_mode = ChartMode.TRADITIONAL_MODE;
		} else {
			chart_mode = (Resource.getPrefInt(prefix + "sidereal_mode") != 0) ? ChartMode.SIDEREAL_MODE
					: ChartMode.TRADITIONAL_MODE;
		}
		house_mode = Resource.getPrefInt(prefix + "house_mode") != 0;
		adjust_mode = Resource.getPrefInt(prefix + "adjust_mode") != 0;
		house_composite = null;
		parent.setLayout(new GridLayout((dialog_mode != 0) ? 1 : 2, false));
		if (dialog_mode >= 0) {
			Group group = new Group(parent, SWT.NONE);
			group.setLayout(new GridLayout((dialog_mode != 0) ? 3 : 2, false));
			group.setText(Resource.getString("dialog_mode_selection"));
			final Button eastern = new Button(group, SWT.RADIO);
			eastern.setText(Resource.getString("dialog_mode_eastern"));
			eastern.setSelection(chart_mode == ChartMode.TRADITIONAL_MODE);
			eastern.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (eastern.getSelection()) {
						chart_mode = ChartMode.TRADITIONAL_MODE;
						enableComposite();
					}
				}
			});
			if (dialog_mode == 0) {
				final Button sidereal = new Button(group, SWT.RADIO);
				sidereal.setText(Resource.getString("dialog_mode_sidereal"));
				sidereal.setSelection(chart_mode == ChartMode.SIDEREAL_MODE);
				sidereal.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						if (sidereal.getSelection()) {
							chart_mode = ChartMode.SIDEREAL_MODE;
							enableComposite();
						}
					}
				});
			}
			final Button pick = new Button(group, SWT.RADIO);
			pick.setText(Resource.getString("dialog_mode_pick"));
			pick.setSelection(chart_mode == ChartMode.PICK_MODE);
			pick.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (pick.getSelection()) {
						chart_mode = ChartMode.PICK_MODE;
						enableComposite();
					}
				}
			});
			final Button western = new Button(group, SWT.RADIO);
			western.setText(Resource.getString("dialog_mode_western"));
			western.setSelection(chart_mode == ChartMode.ASTRO_MODE);
			western.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (western.getSelection()) {
						chart_mode = ChartMode.ASTRO_MODE;
						enableComposite();
					}
				}
			});
		}
		if (dialog_mode > 0) {
			shell.open();
		} else {
			house_composite = new Composite(parent, SWT.NONE);
			house_composite.setLayout(new GridLayout(1, true));
			Group group = new Group(house_composite, SWT.NONE);
			group.setLayout(new GridLayout((dialog_mode < 0) ? 5 : 2, false));
			group.setText(Resource
					.getString((dialog_mode < 0) ? "dialog_pick_selection"
							: "dialog_house_selection"));
			if (dialog_mode < 0) {
				final Button sidereal = new Button(group, SWT.RADIO);
				sidereal.setText(Resource.getString("dialog_mode_sidereal")
						.substring(4));
				sidereal.setSelection(chart_mode == ChartMode.SIDEREAL_MODE);
				sidereal.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						if (sidereal.getSelection()) {
							chart_mode = ChartMode.SIDEREAL_MODE;
							enableComposite();
						}
					}
				});
			}
			current = new Button(group, SWT.RADIO);
			current.setText(Resource.getString("dialog_house_current"));
			current
					.setSelection(!house_mode
							&& (dialog_mode >= 0 || chart_mode == ChartMode.TRADITIONAL_MODE));
			current.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (current.getSelection()) {
						chart_mode = ChartMode.TRADITIONAL_MODE;
						house_mode = false;
					}
					adjust.setEnabled(house_mode);
				}
			});
			ancient = new Button(group, SWT.RADIO);
			ancient.setText(Resource.getString("dialog_house_ancient"));
			ancient
					.setSelection(house_mode
							&& (dialog_mode >= 0 || chart_mode == ChartMode.TRADITIONAL_MODE));
			ancient.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (ancient.getSelection()) {
						chart_mode = ChartMode.TRADITIONAL_MODE;
						house_mode = true;
					}
					adjust.setEnabled(house_mode);
				}
			});
			if (dialog_mode < 0) {
				Label spacer = new Label(group, SWT.NONE);
				GridData data = new GridData();
				data.widthHint = 20;
				spacer.setLayoutData(data);
			}
			adjust = new Button((dialog_mode < 0) ? group : house_composite,
					SWT.CHECK);
			adjust.setText(Resource.getString("dialog_house_adjust"));
			adjust.setSelection(adjust_mode);
			adjust.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					adjust_mode = adjust.getSelection();
				}
			});
			adjust.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			enableComposite();
		}
		return parent;
	}

	private void enableComposite() {
		if (dialog_mode >= 0) {
			TransitDialog.enableComposite(house_composite,
					chart_mode == ChartMode.TRADITIONAL_MODE);
		}
		if (adjust != null)
			adjust.setEnabled(house_mode
					&& chart_mode == ChartMode.TRADITIONAL_MODE);
	}

	public boolean updateChartMode() {
		boolean changed = Resource.getPrefInt(prefix + "sidereal_mode") != chart_mode
				|| (Resource.getPrefInt(prefix + "house_mode") != 0) != house_mode
				|| (Resource.getPrefInt(prefix + "adjust_mode") != 0) != adjust_mode;
		Resource.putPrefInt(prefix + "sidereal_mode", getChartMode());
		return changed;
	}

	public int getChartMode() {
		Resource.putPrefInt(prefix + "house_mode", house_mode ? 1 : 0);
		Resource.putPrefInt(prefix + "adjust_mode", adjust_mode ? 1 : 0);
		return chart_mode;
	}
}