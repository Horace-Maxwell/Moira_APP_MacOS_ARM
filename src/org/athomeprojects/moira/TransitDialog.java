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

import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
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

public class TransitDialog extends Dialog {
	private final double AVERAGE_SOLAR_DAILY_SPEED = 360.0 / 365.25;

	private String[] sign_array, aspects_sign_array;

	private int[] show_array, order_array, aspects_show_array;

	private double[] aspects_degree_array;

	private Button time_mode, search_mode, any;

	private Text date, search_date, entry, any_value, advance;

	private boolean do_search;

	private String transit_date, start_date;

	private int mode, max_entry, period, def_value;

	private double advance_value, def_advance_value;

	private Group search_group;

	private String[] selection_name, selection_value;

	private PlanetDialog planet_dialog;

	private AspectDialog aspect_dialog;

	public TransitDialog(Shell parent) {
		super(parent);
		planet_dialog = new PlanetDialog(parent);
		aspect_dialog = new AspectDialog(parent, true);
	}

	public void setTransitMode(int val) {
		mode = val;
	}

	public Control createDialogArea(Composite parent) {
		String str, selection_char;
		switch (mode) {
		case ChartMode.PRIMARY_DIRECTION_MODE:
			str = "dialog_primary_selection";
			def_advance_value = Resource.getDouble("primary_advance");
			selection_name = Resource.getStringArray("transit_year_selection");
			selection_value = Resource.getStringArray("transit_year_value");
			selection_char = Resource.getString("year_char");
			def_value = Resource.getInt("transit_year_default");
			break;
		case ChartMode.SECONDARY_PROGRESSION_MODE:
			str = "dialog_secondary_selection";
			def_advance_value = Resource.getDouble("secondary_advance");
			selection_name = Resource.getStringArray("transit_year_selection");
			selection_value = Resource.getStringArray("transit_year_value");
			selection_char = Resource.getString("year_char");
			def_value = Resource.getInt("transit_year_default");
			break;
		case ChartMode.SOLAR_ARC_MODE:
			str = "dialog_solar_arc_selection";
			def_advance_value = Resource.getDouble("secondary_advance");
			selection_name = Resource.getStringArray("transit_year_selection");
			selection_value = Resource.getStringArray("transit_year_value");
			selection_char = Resource.getString("year_char");
			def_value = Resource.getInt("transit_year_default");
			break;
		case ChartMode.TRANSIT_MODE:
			str = "dialog_transit_selection";
			def_advance_value = 0.0;
			selection_name = Resource.getStringArray("transit_month_selection");
			selection_value = Resource.getStringArray("transit_month_value");
			selection_char = Resource.getString("month_char");
			def_value = Resource.getInt("transit_month_default");
			break;
		default:
			str = null;
			def_advance_value = 0.0;
			selection_name = Resource.getStringArray("transit_month_selection");
			selection_value = Resource.getStringArray("transit_month_value");
			selection_char = Resource.getString("month_char");
			def_value = Resource.getInt("transit_month_default");
			break;
		}
		int[] now = new int[5];
		BaseCalendar.getCalendar(null, now);
		parent.setLayout(new GridLayout(1, false));
		if (mode != ChartMode.NATAL_MODE) {
			Group group = new Group(parent, SWT.NONE);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(data);
			GridLayout layout = new GridLayout(1, false);
			layout.marginHeight = layout.verticalSpacing = 0;
			group.setLayout(layout);
			Composite composite = new Composite(group, SWT.NONE);
			data = new GridData(GridData.FILL_HORIZONTAL);
			composite.setLayoutData(data);
			composite.setLayout(new GridLayout(4, false));
			group.setText(Resource.getString(str));
			time_mode = new Button(composite, SWT.RADIO);
			time_mode.setText(Resource.getString("dialog_transit_time"));
			time_mode.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (time_mode.getSelection())
						setSearch(false);
				}
			});
			date = new Text(composite, SWT.CENTER | SWT.BORDER);
			transit_date = BaseCalendar.formatDate(now, false, true);
			date.setText(transit_date);
			date.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent event) {
					transit_date = BaseCalendar.auditDay(date.getText(), null);
					date.setText(transit_date);
				}
			});
			Label label = new Label(composite, SWT.NONE);
			data = new GridData();
			data.widthHint = 30;
			label.setLayoutData(data);
			search_mode = new Button(composite, SWT.RADIO);
			search_mode.setText(Resource.getString("dialog_transit_search"));
			search_mode.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (search_mode.getSelection())
						setSearch(true);
				}
			});
			if (mode == ChartMode.PRIMARY_DIRECTION_MODE
					|| mode == ChartMode.SECONDARY_PROGRESSION_MODE
					|| mode == ChartMode.SOLAR_ARC_MODE) {
				composite = new Composite(group, SWT.NONE);
				data = new GridData(GridData.FILL_HORIZONTAL);
				composite.setLayoutData(data);
				composite.setLayout(new GridLayout(
						(mode == ChartMode.PRIMARY_DIRECTION_MODE) ? 5 : 3,
						false));
				label = new Label(composite, SWT.NONE);
				label.setText(Resource.getString("transit_compression"));
				advance = new Text(composite, SWT.RIGHT | SWT.BORDER);
				data = new GridData();
				data.widthHint = 50;
				advance.setLayoutData(data);
				setAdvanceValue(Resource
						.getPrefString((mode == ChartMode.PRIMARY_DIRECTION_MODE) ? "primary_advance"
								: "secondary_advance"));
				advance.addFocusListener(new FocusAdapter() {
					public void focusLost(FocusEvent event) {
						setAdvanceValue(advance.getText());
					}
				});
				label = new Label(composite, SWT.NONE);
				label
						.setText(Resource
								.getString((mode == ChartMode.PRIMARY_DIRECTION_MODE) ? "degree"
										: "day_char"));
				if (mode == ChartMode.PRIMARY_DIRECTION_MODE) {
					label = new Label(composite, SWT.NONE);
					data = new GridData(GridData.FILL_HORIZONTAL);
					label.setLayoutData(data);
					final Button solar_arc = new Button(composite, SWT.PUSH);
					solar_arc.setText(Resource
							.getString("dialog_transit_solar_arc"));
					solar_arc.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							setAdvanceValue(Double
									.toString(AVERAGE_SOLAR_DAILY_SPEED));
						}
					});
				}
			}
		}
		search_group = new Group(parent, SWT.NONE);
		search_group.setText(Resource.getString("dialog_transit_range"));
		search_group.setLayout(new GridLayout(2, false));
		Group range_group = new Group(search_group, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		range_group.setLayoutData(data);
		range_group.setLayout(new GridLayout(selection_name.length + 2, false));
		Label label = new Label(range_group, SWT.NONE);
		label.setText(Resource.getString("start_at"));
		search_date = new Text(range_group, SWT.CENTER | SWT.BORDER);
		start_date = BaseCalendar.formatDate(now, false, true);
		search_date.setText(start_date);
		search_date.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				start_date = BaseCalendar.auditDay(search_date.getText(), null);
				search_date.setText(start_date);
			}
		});
		data = new GridData();
		data.horizontalSpan = 2;
		search_date.setLayoutData(data);
		label = new Label(range_group, SWT.NONE);
		label.setText(Resource.getString("dialog_transit_max_entry"));
		entry = new Text(range_group, SWT.RIGHT | SWT.BORDER);
		final int def_max_entry = Resource.getInt("transit_max_entry");
		max_entry = Resource.getPrefInt("transit_max_entry");
		entry.setText(FileIO.formatInt(max_entry, 5));
		entry.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				max_entry = FileIO.parseInt(entry.getText(), def_max_entry,
						true);
				entry.setText(FileIO.formatInt(max_entry, 4));
			}
		});
		label = new Label(range_group, SWT.NONE);
		label.setText(Resource.getString("dialog_transit_time"));
		final Button[] button = new Button[selection_name.length];
		for (int i = 0; i < selection_name.length; i++) {
			button[i] = new Button(range_group, SWT.RADIO);
			data = new GridData();
			data.widthHint = 50;
			button[i].setLayoutData(data);
			button[i].setText(selection_name[i]);
			button[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					Button push = (Button) event.getSource();
					if (!push.getSelection())
						return;
					any.setSelection(false);
					String name = push.getText();
					for (int j = 0; j < selection_name.length; j++) {
						if (name.equals(selection_name[j])) {
							setAnyValue(selection_value[j], def_value);
							break;
						}
					}
					any_value.setEditable(false);
				}
			});
		}
		Composite composite = new Composite(range_group, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = layout.marginWidth = layout.verticalSpacing = 0;
		layout.horizontalSpacing = 4;
		composite.setLayout(layout);
		any = new Button(composite, SWT.RADIO);
		any.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (!any.getSelection())
					return;
				for (int j = 0; j < button.length; j++) {
					button[j].setSelection(false);
				}
				any_value.setEditable(true);
			}
		});
		any_value = new Text(composite, SWT.CENTER | SWT.BORDER);
		data = new GridData();
		data.widthHint = 20;
		any_value.setLayoutData(data);
		setAnyValue("", def_value);
		any_value.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				setAnyValue(any_value.getText(), def_value);
			}
		});
		label = new Label(composite, SWT.NONE);
		label.setText(selection_char);
		sign_array = Resource.getStringArray("signs");
		show_array = Resource.getPrefIntArray("transit_sign_display");
		order_array = Resource.getIntArray("astro_sign_display_orders");
		aspects_sign_array = Resource.getPrefStringArray("aspects_sign");
		aspects_show_array = Resource
				.getPrefIntArray("transit_aspects_display");
		aspects_degree_array = Resource.getPrefDoubleArray("aspects_degree");
		aspect_dialog.setSignArray(aspects_sign_array, aspects_degree_array,
				null, aspects_show_array, null, null);
		planet_dialog.setSignArray(sign_array, order_array, show_array);
		aspect_dialog.addAspectsControls(search_group,
				(mode == ChartMode.NATAL_MODE) ? null : Resource
						.getString("dialog_transit_search_aspects"));
		planet_dialog
				.addSignControls(search_group, Resource
						.getString("dialog_transit_search_signs"), 2,
						show_array.length);
		boolean select_any = true;
		for (int j = 0; j < button.length; j++) {
			if (selection_value[j].equals(Integer.toString(period))) {
				button[j].setSelection(true);
				select_any = false;
			} else {
				button[j].setSelection(false);
			}
		}
		any.setSelection(select_any);
		any_value.setEditable(select_any);
		setSearch(mode == ChartMode.NATAL_MODE);
		if (time_mode != null)
			time_mode.setSelection(!do_search);
		return parent;
	}

	private void setAnyValue(String val, int def_val) {
		period = FileIO.parseInt(val, def_val, true);
		any_value.setText(FileIO.formatInt(period, 4));
	}

	private void setAdvanceValue(String val) {
		advance_value = FileIO.parseDouble(val, def_advance_value, true);
		advance.setText(Resource.spacePreFilled(FileIO.formatDouble(
				advance_value, 2, 6, false, false), 9));
	}

	private void setSearch(boolean enable) {
		do_search = enable;
		if (date != null)
			date.setEnabled(!enable);
		search_group.setEnabled(enable);
		enableComposite(search_group, enable);
	}

	static public void enableComposite(Composite composite, boolean enable) {
		if (composite == null)
			return;
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].setEnabled(enable);
			if (children[i] instanceof Composite)
				enableComposite((Composite) children[i], enable);
		}
	}

	public boolean updateTransit() {
		Resource.putPrefString("transit_date", do_search ? start_date
				: transit_date);
		if (mode == ChartMode.PRIMARY_DIRECTION_MODE) {
			Resource.putPrefDouble("primary_advance", advance_value);
			period *= 12;
		}
		if (mode == ChartMode.SECONDARY_PROGRESSION_MODE
				|| mode == ChartMode.SOLAR_ARC_MODE) {
			Resource.putPrefDouble("secondary_advance", advance_value);
			period *= 12;
		}
		Resource.putPrefInt("transit_max_entry", max_entry);
		Resource.putPrefInt("transit_period", period);
		Resource.putPrefIntArray("transit_sign_display", show_array);
		Resource.putPrefIntArray("transit_aspects_display", aspects_show_array);
		return do_search;
	}
}