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

import org.athomeprojects.swtext.ColorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class DrawSWT {
	static private final double DEGREE = 2.0 * Math.PI / 360.0;

	static private final int DRAG_ERROR = 5;

	static private final int CLEAR_EXTEND = 1;

	static private final double MARKER_RADIUS_SCALER = 1.05;

	static private Color[] color_key_color = null;

	static private String[] color_key = null;

	static private GC rubber_band_gc = null;

	static private Canvas tracker_canvas = null;

	static private int tracker_radius_sq, tracker_center_x, tracker_center_y,
			tracker_x, tracker_y, tracker_line_width;

	static private int[] tracker_pos;

	static private int mouse_x, mouse_y, rubber_band_x, rubber_band_y,
			rubber_band_mesg_x, rubber_band_mesg_y, rubber_band_width,
			rubber_band_height, rubber_band_max_width, rubber_band_max_height;

	static private String rubber_band_mesg;

	static private Rectangle dock_bbox, drag_bbox;

	static private boolean docked;

	static private double[] tracker_angle;

	static private Color[] tracker_color;

	static public void setSpecialStringColor(int[] color, String[] key) {
		if (color == null) {
			color_key = null;
			return;
		}
		color_key = key;
		if (color_key_color == null) {
			color_key_color = new Color[color.length];
			for (int i = 0; i < color.length; i++)
				color_key_color[i] = ColorManager.getColor(color[i]);
		}
	}

	static public void disposeSpecialStringColor() {
		color_key = null;
	}

	static public Point drawStringVert(GC gc, String key, int x, int y,
			boolean find_size) {
		FontMetrics metric = gc.getFontMetrics();
		int font_height = metric.getHeight();
		int font_width = getMaxWidth(gc, key);
		font_width += (int) (0.2 * font_width);
		int n_x = x - font_width, n_y = y;
		int max_y = n_y;
		for (int i = 0; i < key.length(); i++) {
			String str = key.substring(i, i + 1);
			if (str.equals("|")) {
				max_y = Math.max(max_y, n_y);
				n_y = y;
				n_x -= font_width;
			} else if (str.equals("%")) {
				n_y += font_height / 2;
			} else if (str.equals("$")) {
				max_y = Math.max(max_y, n_y);
				n_y = y;
				n_x -= font_width / 4;
			} else {
				if (!drawColorString(gc, str, n_x, n_y))
					gc.drawString(str, n_x, n_y);
				n_y += font_height;
			}
		}
		max_y = Math.max(max_y, n_y);
		return new Point(x - n_x, max_y - y);
	}

	static public Point drawStringHoriz(GC gc, String key, int x, int y,
			boolean find_size) {
		FontMetrics metric = gc.getFontMetrics();
		int font_height = metric.getHeight();
		int n_x = x, n_y = y;
		int max_x = x;
		int len = key.length();
		for (int i = 0; i < len; i++) {
			String str = key.substring(i, i + 1);
			if (str.equals("|")) {
				max_x = Math.max(max_x, n_x);
				n_x = x;
				n_y += font_height;
			} else if (str.equals("$")) {
				n_x = x;
				n_y += font_height / 4;
			} else {
				if (!find_size) {
					if (!drawColorString(gc, str, n_x, n_y))
						gc.drawString(str, n_x, n_y);
				}
				n_x += gc.getAdvanceWidth(str.charAt(0));
			}
		}
		max_x = Math.max(max_x, n_x);
		n_y += font_height;
		return new Point(max_x - x, n_y - y);
	}

	static private boolean drawColorString(GC gc, String str, int x, int y) {
		if (color_key != null) {
			for (int i = 0; i < color_key.length; i++) {
				if (str.equals(color_key[i])) {
					Color color = gc.getForeground();
					gc.setForeground(color_key_color[i]);
					gc.drawString(str, x, y);
					gc.setForeground(color);
					return true;
				}
			}
		}
		return false;
	}

	static private int getMaxWidth(GC gc, String key) {
		int font_width = 0;
		int len = key.length();
		for (int i = 0; i < len; i++) {
			char c = key.charAt(i);
			int width = gc.getAdvanceWidth(c);
			font_width = Math.max(width, font_width);
		}
		return font_width;
	}

	static public boolean isMarkerEnabled() {
		return tracker_canvas != null;
	}

	static public void initMarker(Canvas canvas, int[] color, int[] display,
			double[] angle, int width) {
		if (angle == null)
			return;
		if (tracker_canvas != null)
			clearMarker(false);
		tracker_canvas = canvas;
		tracker_line_width = width;
		tracker_color = new Color[color.length];
		for (int i = 0; i < color.length; i++) {
			if (display[i] == 0) {
				tracker_color[i] = null;
			} else {
				int val = ~color[i]; // invert color
				tracker_color[i] = ColorManager.allocateColor(val);
			}
		}
		tracker_pos = null;
		tracker_angle = angle;
		tracker_x = tracker_y = -1;
	}

	static public boolean initMarkerPos(int[] center, int[] pos) {
		if (tracker_canvas == null || pos == null || (pos.length % 2) == 1) {
			tracker_pos = null;
			return false;
		}
		tracker_center_x = center[0];
		tracker_center_y = center[1];
		tracker_radius_sq = (int) (MARKER_RADIUS_SCALER * center[2]);
		tracker_radius_sq *= tracker_radius_sq;
		tracker_pos = pos;
		tracker_x = tracker_y = -1;
		return true;
	}

	static public void drawMarker(int x, int y) {
		if (tracker_pos == null)
			return;
		ChartTab.hideTip();
		int dist_sq = getPointDistSqFromCenter(tracker_center_x,
				tracker_center_y, x, y);
		int n_x = x;
		int n_y = y;
		if (tracker_radius_sq > 0 && dist_sq > tracker_radius_sq) {
			n_x = n_y = -1;
		}
		if (tracker_x == n_x && tracker_y == n_y)
			return;
		tracker_x = n_x;
		tracker_y = n_y;
		redrawMarker();
	}

	static public void paintMarker(GC gc) {
		if (tracker_canvas == null || tracker_pos == null || tracker_x < 0
				|| tracker_y < 0)
			return;
		Color fg = gc.getForeground();
		int line_width = gc.getLineWidth();
		gc.setLineWidth(tracker_line_width);
		drawMarkerLines(gc, tracker_x, tracker_y);
		gc.setForeground(fg);
		gc.setLineWidth(line_width);
	}

	static private void drawMarkerLines(GC gc, int x, int y) {
		x -= tracker_center_x;
		y -= tracker_center_y;
		if (x == 0 && y == 0)
			return;
		double base = Math.atan2(y, x);
		for (int j = 0; j < tracker_angle.length; j++) {
			Color color = tracker_color[j];
			if (color == null)
				continue;
			double degree = tracker_angle[j];
			gc.setForeground(color);
			for (int k = -1; k <= 1; k += 2) {
				double angle = base + k * degree * DEGREE;
				double cos = Math.cos(angle);
				double sin = Math.sin(angle);
				for (int i = 0; i < tracker_pos.length; i += 2) {
					int lower = tracker_pos[i];
					int upper = tracker_pos[i + 1];
					gc.drawLine((int) (lower * cos) + tracker_center_x,
							(int) (lower * sin) + tracker_center_y,
							(int) (upper * cos) + tracker_center_x,
							(int) (upper * sin) + tracker_center_y);
				}
				if (Math.abs(degree) < 0.1 || Math.abs(degree - 180.0) < 0.1)
					break;
			}
		}
	}

	static public void endMarker() {
		if (tracker_canvas == null)
			return;
		clearMarker(true);
	}

	static private void clearMarker(boolean redraw) {
		Canvas canvas = tracker_canvas;
		for (int i = 0; i < tracker_color.length; i++) {
			Color color = tracker_color[i];
			if (color != null)
				color.dispose();
		}
		tracker_color = null;
		tracker_pos = null;
		tracker_angle = null;
		tracker_canvas = null;
		tracker_x = tracker_y = -1;
		if (redraw && canvas != null && !canvas.isDisposed())
			canvas.redraw();
	}

	static private void redrawMarker() {
		if (tracker_canvas != null && !tracker_canvas.isDisposed())
			tracker_canvas.redraw();
	}

	static public int getPointDistSqFromCenter(int center_x, int center_y,
			int x, int y) {
		x -= center_x;
		y -= center_y;
		return x * x + y * y;
	}

	static public boolean isRubberBandEnabled() {
		return rubber_band_gc != null;
	}

	static public void initRubberBandRect(Point mouse_pt, Point box_pt,
			int width, int height, Rectangle dock_bound) {
		if (rubber_band_gc != null)
			return;
		Display display = Display.getCurrent();
		rubber_band_gc = new GC(display);
		rubber_band_gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		rubber_band_gc.setXORMode(true);
		rubber_band_gc.setLineWidth(1);
		mouse_x = mouse_pt.x;
		mouse_y = mouse_pt.y;
		rubber_band_x = box_pt.x;
		rubber_band_y = box_pt.y;
		rubber_band_width = width;
		rubber_band_height = height;
		dock_bbox = dock_bound;
		drag_bbox = null;
		rubber_band_gc.drawRectangle(rubber_band_x, rubber_band_y,
				rubber_band_width, rubber_band_height);
	}

	static public void drawRubberBandRect(Point mouse_pt) {
		if (rubber_band_gc == null)
			return;
		if (dock_bbox != null && dock_bbox.contains(mouse_x, mouse_y)) {
			rubber_band_gc.drawRectangle(dock_bbox.x, dock_bbox.y,
					dock_bbox.width, dock_bbox.height);
		} else {
			rubber_band_gc.drawRectangle(rubber_band_x, rubber_band_y,
					rubber_band_width, rubber_band_height);
		}
		rubber_band_x += mouse_pt.x - mouse_x;
		rubber_band_y += mouse_pt.y - mouse_y;
		if (dock_bbox != null && dock_bbox.contains(mouse_pt)) {
			rubber_band_gc.drawRectangle(dock_bbox.x, dock_bbox.y,
					dock_bbox.width, dock_bbox.height);
		} else {
			rubber_band_gc.drawRectangle(rubber_band_x, rubber_band_y,
					rubber_band_width, rubber_band_height);
		}
		mouse_x = mouse_pt.x;
		mouse_y = mouse_pt.y;
	}

	static public Rectangle endRubberBandRect() {
		if (rubber_band_gc == null)
			return null;
		if (dock_bbox != null && dock_bbox.contains(mouse_x, mouse_y)) {
			rubber_band_gc.drawRectangle(dock_bbox.x, dock_bbox.y,
					dock_bbox.width, dock_bbox.height);
			docked = true;
		} else {
			rubber_band_gc.drawRectangle(rubber_band_x, rubber_band_y,
					rubber_band_width, rubber_band_height);
			dock_bbox = null;
			docked = false;
		}
		rubber_band_gc.dispose();
		rubber_band_gc = null;
		return (dock_bbox != null) ? dock_bbox : (new Rectangle(rubber_band_x,
				rubber_band_y, rubber_band_width, rubber_band_height));
	}

	static public void initRubberBandLine(Point origin, Point mouse_pt, Point pt) {
		if (rubber_band_gc != null)
			return;
		Display display = Display.getCurrent();
		rubber_band_gc = new GC(display);
		rubber_band_gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		rubber_band_gc.setXORMode(true);
		rubber_band_gc.setLineWidth(1);
		rubber_band_gc.setLineStyle(SWT.LINE_DOT);
		rubber_band_mesg_x = origin.x;
		rubber_band_mesg_y = origin.y;
		rubber_band_mesg = null;
		mouse_x = mouse_pt.x;
		mouse_y = mouse_pt.y;
		rubber_band_x = pt.x;
		rubber_band_y = pt.y;
		dock_bbox = null;
		drag_bbox = new Rectangle(mouse_x - DRAG_ERROR, mouse_y - DRAG_ERROR,
				2 * DRAG_ERROR, 2 * DRAG_ERROR);
		if (!drag_bbox.contains(rubber_band_x, rubber_band_y)) {
			rubber_band_gc.drawLine(mouse_x, mouse_y, rubber_band_x,
					rubber_band_y);
		}
	}

	static public void drawRubberBandLine(String mesg, Point pt) {
		if (rubber_band_gc == null)
			return;
		if (rubber_band_x >= 0 && drag_bbox != null
				&& !drag_bbox.contains(rubber_band_x, rubber_band_y)) {
			rubber_band_gc.drawLine(mouse_x, mouse_y, rubber_band_x,
					rubber_band_y);
			clearMesg();
		}
		rubber_band_mesg = mesg;
		if (pt == null || mesg == null) {
			rubber_band_x = -1;
		} else {
			rubber_band_x = pt.x;
			rubber_band_y = pt.y;
			if (drag_bbox != null
					&& !drag_bbox.contains(rubber_band_x, rubber_band_y)) {
				rubber_band_gc.drawLine(mouse_x, mouse_y, rubber_band_x,
						rubber_band_y);
				Display display = Display.getCurrent();
				rubber_band_gc.setXORMode(false);
				rubber_band_gc.setForeground(display
						.getSystemColor(SWT.COLOR_BLACK));
				rubber_band_gc.drawString(rubber_band_mesg, rubber_band_mesg_x,
						rubber_band_mesg_y);
				rubber_band_gc.setForeground(display
						.getSystemColor(SWT.COLOR_WHITE));
				rubber_band_gc.setXORMode(true);
			}
		}
	}

	static public boolean endRubberBandLine(Point pt) {
		if (rubber_band_gc == null)
			return false;
		drawRubberBandLine(null, null);
		rubber_band_gc.dispose();
		rubber_band_gc = null;
		return !drag_bbox.contains(pt.x, pt.y);
	}

	static public Rectangle getRubberBandMessageBound(Control ctrl) {
		Point pt = ctrl.toControl(rubber_band_mesg_x - CLEAR_EXTEND,
				rubber_band_mesg_y - CLEAR_EXTEND);
		return new Rectangle(pt.x, pt.y, rubber_band_max_width + 2
				* CLEAR_EXTEND, rubber_band_max_height + 2 * CLEAR_EXTEND);
	}

	static private void clearMesg() {
		if (rubber_band_mesg == null)
			return;
		Point size = rubber_band_gc.stringExtent(rubber_band_mesg);
		rubber_band_max_width = Math.max(size.x + 1, rubber_band_max_width);
		rubber_band_max_height = Math.max(size.y + 1, rubber_band_max_height);
		rubber_band_gc.setXORMode(false);
		rubber_band_gc.fillRectangle(rubber_band_mesg_x - CLEAR_EXTEND,
				rubber_band_mesg_y - CLEAR_EXTEND, rubber_band_max_width + 2
						* CLEAR_EXTEND, rubber_band_max_height + 2
						* CLEAR_EXTEND);
		rubber_band_gc.setXORMode(true);
	}

	static public boolean isDocked() {
		return docked;
	}

	static public void drawCancel() {
		DrawSWT.drawRubberBandLine(null, null);
		DrawSWT.drawMarker(-1, -1);
	}
}
