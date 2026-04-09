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

import org.athomeprojects.base.ImageControl;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ImageControlDialog extends Dialog {
	static private int size_selection, image_width, image_height;

	private Button large, medium, small, custom;

	private Text width, height;

	private boolean chart_only;

	public ImageControlDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(Resource.getString("image_button_selection"));
		GridLayout grid_layout = new GridLayout(5, false);
		grid_layout.verticalSpacing = 0;
		group.setLayout(grid_layout);
		large = new Button(group, SWT.RADIO);
		large.setText(Resource.getString("image_button_large"));
		large.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (large.getSelection())
					setButton(ImageControl.LARGE_SIZE);
			}
		});
		medium = new Button(group, SWT.RADIO);
		medium.setText(Resource.getString("image_button_medium"));
		medium.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (medium.getSelection())
					setButton(ImageControl.MEDIUM_SIZE);
			}
		});
		small = new Button(group, SWT.RADIO);
		small.setText(Resource.getString("image_button_small"));
		small.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (small.getSelection())
					setButton(ImageControl.SMALL_SIZE);
			}
		});
		custom = new Button(group, SWT.RADIO);
		custom.setText(Resource.getString("image_button_custom"));
		custom.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (custom.getSelection())
					setButton(ImageControl.CUSTOM_SIZE);
			}
		});
		Composite custom_holder = new Composite(group, SWT.NO_FOCUS);
		GridLayout holder_layout = new GridLayout(3, false);
		custom_holder.setLayout(holder_layout);
		width = new Text(custom_holder, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		Label sep = new Label(custom_holder, SWT.SHADOW_NONE);
		sep.setText("x");
		height = new Text(custom_holder, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		image_width = image_height = 0;
		if (Resource.hasPrefInt("image_size_width")
				&& Resource.hasPrefInt("image_size_height")) {
			image_width = Resource.getPrefInt("image_size_width");
			image_height = Resource.getPrefInt("image_size_height");
		}
		int[] size = ImageControl.getImageSize(image_width, image_height);
		size_selection = ImageControl.getSelectionFromSize(size);
		image_width = size[0];
		image_height = size[1];
		width.setText(Integer.toString(image_width));
		width.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.FocusOut) {
					try {
						image_width = Integer.parseInt(width.getText());
						image_width = Math.max(0, image_width);
					} catch (NumberFormatException e) {
						image_width = 0;
					}
				}
			}
		});
		height.setText(Integer.toString(image_height));
		height.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.FocusOut) {
					try {
						image_height = Integer.parseInt(height.getText());
						image_height = Math.max(0, image_height);
					} catch (NumberFormatException e) {
						image_height = 0;
					}
				}
			}
		});
		chart_only = Resource.getPrefInt("image_chart_only") != 0;
		final Button chart_button = new Button(group, SWT.CHECK);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		chart_button.setLayoutData(data);
		chart_button
				.setText(Resource.getString("dialog_image_show_chart_only"));
		chart_button.setSelection(chart_only);
		chart_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				chart_only = chart_button.getSelection();
			}
		});
		setButton(size_selection);
		return parent;
	}

	private void setButton(int sel) {
		size_selection = sel;
		large.setSelection(sel == ImageControl.LARGE_SIZE);
		medium.setSelection(sel == ImageControl.MEDIUM_SIZE);
		small.setSelection(sel == ImageControl.SMALL_SIZE);
		custom.setSelection(sel == ImageControl.CUSTOM_SIZE);
		width.setEditable(sel == ImageControl.CUSTOM_SIZE);
		height.setEditable(sel == ImageControl.CUSTOM_SIZE);
		int[] size = new int[5];
		size[0] = image_width;
		size[1] = image_height;
		size = ImageControl.getSizeFromSelection(sel, size);
		image_width = size[0];
		image_height = size[1];
		width.setText(Integer.toString(image_width));
		height.setText(Integer.toString(image_height));
	}

	static public int[] getImageSize() {
		if (Resource.hasPrefInt("image_size_width")
				&& Resource.hasPrefInt("image_size_height")) {
			image_width = Resource.getPrefInt("image_size_width");
			image_height = Resource.getPrefInt("image_size_height");
		}
		return ImageControl.getImageSize(image_width, image_height);
	}

	public void saveImageSize() {
		Resource.putPrefInt("image_size_width", image_width);
		Resource.putPrefInt("image_size_height", image_height);
		Resource.putPrefInt("image_chart_only", chart_only ? 1 : 0);
	}
}