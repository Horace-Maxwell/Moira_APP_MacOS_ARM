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

import java.awt.Graphics;

import javax.swing.JPanel;

public class AppletDataWrapper extends JPanel {
	private static final long serialVersionUID = -6441377740487021302L;

	private AppletData data = null;

	public void paint(Graphics g) {
		super.paint(g);
		data.refresh();
	}

	public void setContent(AppletData comp) {
		data = comp;
	}
}