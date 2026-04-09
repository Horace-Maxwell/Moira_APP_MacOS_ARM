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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.athomeprojects.base.ChartData;
import org.athomeprojects.base.DiagramTip;

public class AppletChart extends JPanel {
	private static final long serialVersionUID = 597375491653063671L;

	private int RESERVE = 5;

	private int MINSIZE = 480;

	private boolean recompute;

	private ChartData data = null;

	private BufferedImage buffer = null;

	private Graphics2D buffer_g2d = null;

	private HoverTipAWT tip;

	private DiagramTip diagram_tip;

	public void init(ChartData chart_data) {
		data = chart_data;
		data.setEpheMode(true);
		recompute = true;
		tip = new HoverTipAWT(this);
		diagram_tip = new DiagramTip();
		tip.setTipData(diagram_tip, RESERVE, RESERVE);
		tip.activateHoverHelp();
	}

	public DiagramTip getDiagramTip() {
		return diagram_tip;
	}

	public Point getOffset() {
		return new Point(RESERVE, RESERVE);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (data == null)
			return;
		Dimension dim = getSize();
		int min_dim = Math.min(dim.width - 2 * RESERVE, dim.height - 2
				* RESERVE);
		if (min_dim < MINSIZE)
			return;
		Point page_size = new Point(dim.width - 2 * RESERVE, dim.height - 2
				* RESERVE);
		if (buffer != null
				&& (buffer.getWidth() != page_size.x || buffer.getHeight() != page_size.y)) {
			buffer = null;
		}
		if (recompute || buffer == null) {
			if (buffer == null) {
				buffer = new BufferedImage(page_size.x, page_size.y,
						BufferedImage.TYPE_INT_BGR);
				buffer_g2d = (Graphics2D) buffer.createGraphics();
			}
			Graphics2D g2d = buffer_g2d;
			Point size = new Point(min_dim, min_dim);
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, page_size.x, page_size.y);
			data.pageDiagram(g2d, "applet_", 1, page_size, size, true, false,
					false, false, true, false, true, true);
			recompute = false;
		}
		g.drawImage(buffer, RESERVE, RESERVE, this);
		tip.paint(g);
	}

	public void hideTip() {
		tip.hideTip();
	}

	public void recompute() {
		recompute = true;
	}
}