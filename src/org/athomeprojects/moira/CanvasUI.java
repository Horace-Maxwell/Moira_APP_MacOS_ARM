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

import org.athomeprojects.base.Resource;
import org.athomeprojects.swtext.ColorManager;
import org.athomeprojects.swtext.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public class CanvasUI extends Canvas implements MouseMoveListener {
	private static final int ENTRY_MARGIN = 12;

	private static final int ENTRY_PANEL_WIDTH = 420;

	private Composite button_composite, entry_composite, info_composite;

	private Text name;

	private Button male, female;

	private Label expand;

	private boolean pin_ui, show_ui, skip_overlay;

	private int trigger, info_overlay;

	private double info_overlay_ratio;

	private Point cursor_location;

	public CanvasUI(Composite parent, int style) {
		super(parent, style);
		pin_ui = Resource.getPrefInt("pin_ui") != 0;
		show_ui = Resource.getPrefInt("show_ui") != 0;
		info_overlay = Resource.getPrefInt("info_overlay");
		info_overlay_ratio = Resource.getPrefDouble("info_overlay_ratio");
		setLayout(new FormLayout());
		setBackgroundMode(SWT.INHERIT_FORCE);
		button_composite = new Composite(this, SWT.NONE);
		MenuFolder.addCommandListener(button_composite);
		FormData form_data = new FormData();
		form_data.top = new FormAttachment(0, ENTRY_MARGIN);
		form_data.right = new FormAttachment(100, -ENTRY_MARGIN);
		button_composite.setLayoutData(form_data);
		GridLayout grid_layout = new GridLayout(1, false);
		grid_layout.marginWidth = grid_layout.marginHeight = 0;
		button_composite.setLayout(grid_layout);
		expand = new Label(button_composite, SWT.NONE);
		updateColor(false);
		ImageManager.setImageLabel(expand, "expand_icon");
		expand.setToolTipText(Resource.getString("tip_expand_button"));
		expand.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				showUI();
			}
		});
		button_composite.setVisible(!show_ui);
		entry_composite = new Composite(this, SWT.BORDER);
		grid_layout = new GridLayout(1, false);
		grid_layout.marginWidth = grid_layout.marginHeight = 10;
		grid_layout.verticalSpacing = 8;
		entry_composite.setLayout(grid_layout);
		entry_composite.setBackgroundMode(SWT.INHERIT_FORCE);
		form_data = new FormData();
		form_data.top = new FormAttachment(0, ENTRY_MARGIN);
		form_data.right = new FormAttachment(100, -ENTRY_MARGIN);
		form_data.width = ENTRY_PANEL_WIDTH;
		entry_composite.setLayoutData(form_data);
		entry_composite.setVisible(false);
		trigger = 0;
		Composite composite = new Composite(entry_composite, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new FormLayout());
		Composite pin_container = new Composite(composite, SWT.NONE);
		form_data = new FormData();
		form_data.top = form_data.left = new FormAttachment(0);
		pin_container.setLayoutData(form_data);
		grid_layout = new GridLayout(1, false);
		grid_layout.marginWidth = grid_layout.marginHeight = 0;
		pin_container.setLayout(grid_layout);
		Label pin = new Label(pin_container, SWT.NONE);
		ImageManager.setImageLabel(pin, pin_ui ? "pindown_icon" : "pinup_icon");
		pin.setToolTipText(Resource.getString("tip_pin_button"));
		pin.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				Label label = (Label) event.getSource();
				pin_ui = !pin_ui;
				Resource.putPrefInt("pin_ui", pin_ui ? 1 : 0);
				label.setImage(ImageManager.getImage(pin_ui ? "pindown_icon"
						: "pinup_icon"));
			}
		});
		Composite shrink_container = new Composite(composite, SWT.NONE);
		form_data = new FormData();
		form_data.top = new FormAttachment(0);
		form_data.right = new FormAttachment(100);
		shrink_container.setLayoutData(form_data);
		grid_layout = new GridLayout(1, false);
		grid_layout.marginWidth = grid_layout.marginHeight = 0;
		shrink_container.setLayout(grid_layout);
		Label shrink = new Label(shrink_container, SWT.NONE);
		ImageManager.setImageLabel(shrink, "shrink_icon");
		shrink.setToolTipText(Resource.getString("tip_shrink_button"));
		shrink.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				show_ui = false;
				Resource.putPrefInt("show_ui", 0);
				entry_composite.setVisible(false);
				button_composite.setVisible(true);
			}
		});
		composite = new Composite(entry_composite, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		grid_layout = new GridLayout(2, false);
		grid_layout.marginWidth = grid_layout.marginHeight = 0;
		composite.setLayout(grid_layout);
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(1, false));
		group.setText(Resource.getString("dialog_name_name"));
		name = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		name.setText(Moira.getChart().getName());
		name.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				ChartTab tab = Moira.getChart();
				tab.setName(name.getText());
				name.setText(tab.getName());
			}
		});
		group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
		group.setText(Resource.getString("sex"));
		group.setLayout(new GridLayout(2, false));
		male = new Button(group, SWT.RADIO);
		male.setText(Resource.getString("male"));
		male.setSelection(Moira.getChart().getSex());
		male.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Moira.getChart().setSex(male.getSelection());
			}
		});
		female = new Button(group, SWT.RADIO);
		female.setText(Resource.getString("female"));
		female.setSelection(!Moira.getChart().getSex());
		female.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Moira.getChart().setSex(!female.getSelection());
			}
		});
		info_composite = new Composite(this, SWT.NONE);
		info_composite.setBackgroundMode(SWT.INHERIT_FORCE);
		FillLayout fill_layout = new FillLayout();
		fill_layout.marginWidth = fill_layout.marginHeight = fill_layout.spacing = 0;
		info_composite.setLayout(fill_layout);
		form_data = new FormData();
		form_data.top = new FormAttachment(0);
		form_data.left = new FormAttachment(0);
		form_data.bottom = new FormAttachment(100);
		info_composite.setLayoutData(form_data);
		addMouseMoveListener(this);
		info_composite.setVisible(false);
		entry_composite.setVisible(show_ui);
	}

	public void hideInfo() {
		skip_overlay = false;
		if (info_composite.getVisible()) {
			info_composite.setVisible(false);
			ChartTab.getTab(info_overlay).setAlternateContainer(null);
		}
	}

	public void showUI() {
		show_ui = true;
		Resource.putPrefInt("show_ui", 1);
		button_composite.setVisible(false);
		entry_composite.setVisible(true);
		disableUIHint();
	}

	public void moveToNameField() {
		Moira.moveToControl(name);
	}

	public void mouseMove(MouseEvent event) {
		if (show_ui && !pin_ui) {
			Rectangle bounds = entry_composite.getBounds();
			boolean visible = event.x >= trigger
					&& bounds.contains(event.x, event.y);
			if (entry_composite.getVisible() != visible) {
				entry_composite.setVisible(visible);
				button_composite.setVisible(!visible);
				if (!visible)
					button_composite.setFocus();
			}
		}
		if (info_overlay >= 0 && ChartTab.getTab(info_overlay).isTabVisible()) {
			if (event.stateMask == SWT.CONTROL) {
				skip_overlay = true;
				return;
			}
			Rectangle area = getClientArea();
			int width = (info_overlay_ratio < 0.0) ? area.height
					: ((int) (info_overlay_ratio * area.width));
			width = Math.min(width, area.width - 20);
			Rectangle bounds = info_composite.getBounds();
			if (bounds.width != width) {
				bounds.width = width;
				info_composite.setBounds(bounds.x, bounds.y, bounds.width,
						bounds.height);
			}
			boolean visible = bounds.contains(event.x, event.y);
			if (!visible)
				skip_overlay = false;
			else if (skip_overlay)
				return;
			if (visible != info_composite.getVisible()) {
				ChartTab.getTab(info_overlay).setAlternateContainer(
						visible ? info_composite : null);
				info_composite.setVisible(visible);
				info_composite.layout();
			}
		}
	}

	public int getOverlay() {
		return info_overlay;
	}

	public void toggleOverlay(int index) {
		info_overlay = (info_overlay == index) ? -1 : index;
		Resource.putPrefInt("info_overlay", info_overlay);
	}

	public void setOverlayBoundary() {
		Point pt = toControl(cursor_location);
		Rectangle area = getClientArea();
		if (pt.x <= area.height) {
			Resource.removePref("info_overlay_ratio");
		} else {
			Resource.putPrefDouble("info_overlay_ratio", ((double) pt.x)
					/ area.width);
		}
		info_overlay_ratio = Resource.getPrefDouble("info_overlay_ratio");
		int width = (info_overlay_ratio < 0.0) ? area.height
				: ((int) (info_overlay_ratio * area.width));
		width = Math.min(width, area.width - 20);
		Rectangle bounds = info_composite.getBounds();
		if (bounds.width != width) {
			bounds.width = width;
			info_composite.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
		}
	}

	public void setMouseLocation() {
		cursor_location = Display.getCurrent().getCursorLocation();
	}

	public void setTrigger(int val) {
		trigger = val;
	}

	public void setName(String str) {
		name.setText((str == null) ? "" : str);
	}

	public void setSex(boolean sex) {
		male.setSelection(sex);
		female.setSelection(!sex);
	}

	public Composite getEntryField() {
		return entry_composite;
	}

	public int getReservedWidth() {
		return ENTRY_PANEL_WIDTH + 2 * ENTRY_MARGIN;
	}

	public void updateColor(boolean no_color) {
		Color panel_color = ColorManager.getColor(0x000000);
		Color field_color = ColorManager.getColor(0x141414);
		Color text_color = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		applyOpaqueTheme(button_composite, panel_color, field_color, text_color);
		applyOpaqueTheme(entry_composite, panel_color, field_color, text_color);
	}

	private void applyOpaqueTheme(Control control, Color color,
			Color field_color, Color text_color) {
		if (control == null || control.isDisposed())
			return;
		if (control instanceof Text || control instanceof Combo
				|| control instanceof CCombo) {
			control.setBackground(field_color);
		} else {
			control.setBackground(color);
		}
		control.setForeground(text_color);
		if (control instanceof Composite) {
			Control[] children = ((Composite) control).getChildren();
			for (int i = 0; i < children.length; i++) {
				applyOpaqueTheme(children[i], color, field_color, text_color);
			}
		}
	}

	public boolean showUIHint() {
		return Resource.getPrefInt("show_ui_hint") != 0;
	}

	private void disableUIHint() {
		if (!showUIHint())
			return;
		Resource.putPrefInt("show_ui_hint", 0);
		ChartTab.hideTimerHint();
	}
}
