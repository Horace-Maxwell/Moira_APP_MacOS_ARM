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

import org.athomeprojects.base.DiagramTip;
import org.athomeprojects.base.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class HoverTipSWT implements MouseListener, MouseMoveListener,
		MouseTrackListener {
	private Shell shell;

	private Font font;

	private Label text;

	private boolean mouse_down, timer_hint, hidden;

	private DiagramTip tip = null;

	public HoverTipSWT(Shell parent) {
		final Display display = parent.getDisplay();
		shell = new Shell(parent, SWT.ON_TOP);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		shell.setLayout(gridLayout);
		shell.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		text = new Label(shell, SWT.NONE);
		text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		text.setBackground(ColorManager.getColor("tip_bg_color"));
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
	/*	font = new Font(Display.getCurrent(), FontMap.getSwtFontName(),
				Resource.getInt("swt_tip_font_size"), SWT.NORMAL);*/

		 font = new Font(Display.getCurrent(), "Dialog.bold",
					Resource.getInt("swt_tip_font_size"), SWT.NORMAL);
		text.setFont(font);
		hidden = true;
	}

	public void setTipData(DiagramTip data) {
		tip = data;
	}

	public boolean isTipShell(Shell sh) {
		return sh == shell;
	}

	public void hide() {
		if (!hidden && !timer_hint && shell != null && !shell.isDisposed()
				&& shell.isVisible()) {
			shell.setVisible(false);
			shell.update();
			hidden = true;
		}
	}

	public void mouseDown(MouseEvent event) {
		timer_hint = false;
		hide();
		mouse_down = true;
	}

	public void mouseUp(MouseEvent event) {
		mouse_down = false;
	}

	public void mouseDoubleClick(MouseEvent event) {
	}

	public void mouseMove(MouseEvent event) {
		hide();
	}

	public void mouseHover(MouseEvent event) {
		if (tip == null || mouse_down || timer_hint)
			return;
		String str = tip.getTipFromPoint(event.x, event.y);
		if (str == null)
			return;
		Control ctrl = (Control) event.getSource();
		Point pos = ctrl.toDisplay(new Point(event.x, event.y));
		text.setText(str);
		shell.pack();
		setHoverLocation(pos);
		shell.setVisible(true);
		hidden = false;
	}

	public void showTimerHint(String mesg, Canvas canvas, int x, int y,
			int second) {
		hide();
		text.setText(mesg.replace('|', '\n'));
		shell.pack();
		Rectangle bounds = shell.getBounds();
		if (x < 0)
			x += canvas.getClientArea().width - bounds.width;
		if (y < 0)
			y += canvas.getClientArea().height - bounds.height;
		Point pt = canvas.toDisplay(x, y);
		bounds.x = pt.x;
		bounds.y = pt.y;
		shell.setBounds(bounds);
		shell.setVisible(true);
		timer_hint = true;
		hidden = false;
		Display.getCurrent().timerExec(second * 1000, new Runnable() {
			public void run() {
				if (!timer_hint || shell.isDisposed())
					return;
				hideTimerHint();
			}
		});
	}

	public void hideTimerHint() {
		timer_hint = false;
		hide();
	}

	public void mouseEnter(MouseEvent event) {
	}

	public void mouseExit(MouseEvent event) {
		hide();
	}

	public void activateHoverHelp(Control ctrl) {
		ctrl.addMouseListener(this);
		ctrl.addMouseMoveListener(this);
		ctrl.addMouseTrackListener(this);
	}

	private void setHoverLocation(Point position) {
		Rectangle display_bounds = shell.getDisplay().getClientArea();
		Rectangle shell_bounds = shell.getBounds();
		shell_bounds.x = Math.max(Math.min(position.x, display_bounds.width
				- shell_bounds.width), 0);
		shell_bounds.y = Math.max(Math.min(position.y + 16,
				display_bounds.height - shell_bounds.height), 0);
		if (shell_bounds.contains(position)) {
			shell_bounds.y = position.y - shell_bounds.height - 2;
			shell_bounds.y = Math.max(0, shell_bounds.y);
			if (shell_bounds.contains(position))
				shell_bounds.x += 16;
		}
		shell.setBounds(shell_bounds);
	}

	public void dispose() {
		if (shell == null)
			return;
		text.dispose();
		font.dispose();
		shell.close();
		shell = null;
	}
}