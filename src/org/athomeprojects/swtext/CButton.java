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
package org.athomeprojects.swtext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class CButton extends Composite {
	static private CButton active;

	private CLabel inside, outside, selected;

	private StackLayout layout;

	private String name;

	private Image image, gray_image;

	public CButton(Composite parent, String image_name, String tool_tip) {
		super(parent, SWT.NONE);
		name = image_name;
		image = ImageManager.getImage(image_name);
		layout = new StackLayout();
		layout.marginWidth = layout.marginHeight = 0;
		setLayout(layout);
		outside = new CLabel(this, SWT.NONE);
		outside.setImage(image);
		outside.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent event) {
				setTopControl(inside);
				active = (CButton) inside.getParent();
			}
		});
		inside = new CLabel(this, SWT.SHADOW_OUT);
		setToolTipText(tool_tip);
		inside.setImage(image);
		inside.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseExit(MouseEvent event) {
				setTopControl(outside);
			}
		});
		inside.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent event) {
				setTopControl(inside);
				active = (CButton) inside.getParent();
			}

			public void mouseDown(MouseEvent event) {
				setTopControl(selected);
			}
		});
		selected = new CLabel(this, SWT.SHADOW_IN);
		selected.setImage(image);
		selected.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent event) {
				setTopControl(inside);
				active = (CButton) inside.getParent();
			}
		});
		layout.topControl = outside;
		active = null;
	}

	public void setToolTipText(String tool_tip) {
		if (tool_tip != null)
			tool_tip = tool_tip.replaceAll("\\(&.\\)", "");
		inside.setToolTipText(tool_tip);
	}

	public void setEnabled(boolean set) {
		layout.topControl = outside;
		if (set) {
			outside.setImage(image);
		} else {
			if (gray_image == null)
				gray_image = ImageManager.getGrayImage(name);
			outside.setImage(gray_image);
		}
		outside.setEnabled(set);
	}

	public void addMouseListener(MouseListener listener) {
		inside.addMouseListener(listener);
	}

	private void setTopControl(CLabel top) {
		layout.topControl = top;
		layout();
		if (active != this)
			deselectAll();
	}

	private void resetTopControl() {
		layout.topControl = outside;
		layout();
	}

	public void dispose() {
		active = null;
		inside.dispose();
		outside.dispose();
		selected.dispose();
		super.dispose();
	}

	static public void deselectAll() {
		if (active != null && !active.isDisposed()) {
			active.resetTopControl();
			active = null;
		}
	}
}