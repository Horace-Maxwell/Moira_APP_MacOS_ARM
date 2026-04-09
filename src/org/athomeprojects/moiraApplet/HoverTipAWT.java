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
package org.athomeprojects.moiraApplet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.athomeprojects.base.DiagramTip;
import org.athomeprojects.base.Resource;

public class HoverTipAWT extends JTextArea {
	private static final long serialVersionUID = 363798399814959404L;

	static private final int TIMER_DELAY = 400;

	private JPanel parent;

	private int mouse_x, mouse_y;

	private int offset_x, offset_y;

	private boolean enabled;

	private Rectangle bound = new Rectangle();

	private DiagramTip tip = null;

	private Timer timer = null;

	private class MouseTimer extends TimerTask {
		public void run() {
			if (tip == null)
				return;
			String str = tip.getTipFromPoint(mouse_x - offset_x, mouse_y
					- offset_y);
			if (str == null)
				return;
			setText(str);
			setHoverLocation(mouse_x, mouse_y);
			enabled = true;
			parent.repaint(bound);
		}
	}

	public HoverTipAWT(JPanel panel) {
		super();
		parent = panel;
		enabled = false;
		setEditable(false);
		setWrapStyleWord(false);
		setVisible(false);
		setFont(new Font(Resource.getFontName(), Font.PLAIN, Resource
				.getInt("g2d_tip_font_size")));
		setText("");
		setBackground(new Color(Resource.getInt("tip_bg_color")));
		setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(this);
	}

	public void paint(Graphics g) {
		if (!enabled)
			return;
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform trans = g2d.getTransform();
		g2d.translate(bound.x, bound.y);
		super.paint(g);
		g2d.setTransform(trans);
	}

	public void setTipData(DiagramTip data, int off_x, int off_y) {
		tip = data;
		offset_x = off_x;
		offset_y = off_y;
	}

	public void activateHoverHelp() {
		parent.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				hideTip();
			}
		});
		parent.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent event) {
				mouse_x = event.getX();
				mouse_y = event.getY();
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				timer = new Timer();
				timer.schedule(new MouseTimer(), TIMER_DELAY);
				hideTip();
			}
		});
	}

	public void hideTip() {
		if (enabled) {
			enabled = false;
			parent.repaint(bound);
		}
	}

	private void setHoverLocation(int x, int y) {
		Dimension p_size = parent.getSize();
		Dimension size = getPreferredSize();
		bound.x = Math.max(Math.min(x, p_size.width - size.width), 0);
		bound.y = Math.max(Math.min(y + 16, p_size.height - size.height), 0);
		bound.width = size.width;
		bound.height = size.height;
		setBounds(bound);
	}
}