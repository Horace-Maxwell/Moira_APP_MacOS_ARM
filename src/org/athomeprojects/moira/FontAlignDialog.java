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

public class FontAlignDialog extends Dialog {
	private boolean display_vertical_text;

	private boolean image_vertical_text;

	private boolean print_vertical_text;

	public FontAlignDialog(Shell parent) {
		super(parent);
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText(Resource.getString("dialog_align_display"));
		display_vertical_text = Resource.getPrefInt("display_vertical_text") != 0;
		final Button display_vert = new Button(group, SWT.RADIO);
		display_vert.setText(Resource.getString("dialog_align_vertical"));
		display_vert.setSelection(display_vertical_text);
		display_vert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				display_vertical_text = display_vert.getSelection();
			}
		});
		final Button display_center = new Button(group, SWT.RADIO);
		display_center.setText(Resource.getString("dialog_align_center"));
		display_center.setSelection(!display_vertical_text);
		display_center.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				display_vertical_text = !display_center.getSelection();
			}
		});
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText(Resource.getString("dialog_align_image"));
		image_vertical_text = Resource.getPrefInt("image_vertical_text") != 0;
		final Button image_vert = new Button(group, SWT.RADIO);
		image_vert.setText(Resource.getString("dialog_align_vertical"));
		image_vert.setSelection(image_vertical_text);
		image_vert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				image_vertical_text = image_vert.getSelection();
			}
		});
		final Button image_center = new Button(group, SWT.RADIO);
		image_center.setText(Resource.getString("dialog_align_center"));
		image_center.setSelection(!image_vertical_text);
		image_center.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				image_vertical_text = !image_center.getSelection();
			}
		});
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText(Resource.getString("dialog_align_print"));
		print_vertical_text = Resource.getPrefInt("print_vertical_text") != 0;
		final Button print_vert = new Button(group, SWT.RADIO);
		print_vert.setText(Resource.getString("dialog_align_vertical"));
		print_vert.setSelection(print_vertical_text);
		print_vert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				print_vertical_text = print_vert.getSelection();
			}
		});
		final Button print_center = new Button(group, SWT.RADIO);
		print_center.setText(Resource.getString("dialog_align_center"));
		print_center.setSelection(!print_vertical_text);
		print_center.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				print_vertical_text = !print_center.getSelection();
			}
		});
		return parent;
	}

	public boolean updateFontAlign() {
		boolean changed = (Resource.getPrefInt("display_vertical_text") != 0) != display_vertical_text
				|| (Resource.getPrefInt("image_vertical_text") != 0) != image_vertical_text
				|| (Resource.getPrefInt("print_vertical_text") != 0) != print_vertical_text;
		Resource.putPrefInt("display_vertical_text", display_vertical_text ? 1
				: 0);
		Resource.putPrefInt("image_vertical_text", image_vertical_text ? 1 : 0);
		Resource.putPrefInt("print_vertical_text", print_vertical_text ? 1 : 0);
		return changed;
	}
}