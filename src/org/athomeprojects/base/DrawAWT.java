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
package org.athomeprojects.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class DrawAWT {
	private final int POLYGON_PRECISION = 10;

	static public final int LINE = 0;

	static public final int DASH = 1;

	static public final int SPARSE_DASH = 2;

	static public final int DENSE_DASH = 3;

	static public final int DOT_DASH = 4;

	static public final int DOT = 5;

	static private Graphics2D g2d = null;

	static private Hashtable font_table = new Hashtable();

	static final float dash[] = { 6.0f, 6.0f };

	static final float sparse_dash[] = { 5.0f, 10.0f };

	static final float dense_dash[] = { 10.0f, 5.0f };

	static final float dot_dash[] = { 21.0f, 9.0f, 3.0f, 9.0f };

	static final float dot[] = { 3.0f, 3.0f };

	static final BasicStroke dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);

	static final BasicStroke sparse_dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, sparse_dash,
			0.0f);

	static final BasicStroke dense_dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dense_dash,
			0.0f);

	static final BasicStroke dot_dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dot_dash, 0.0f);

	static final BasicStroke dotted = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dot, 0.0f);

	private int fg_color_org, bg_color_org;

	private int fg_color, bg_color;

	private int font_scaler = 1;

	private String prefix;

	private String[] color_key;

	private int[] color_key_color;

	private Color color_save;

	private FontMetrics cur_metric;

	private int font_size_override;

	private int cur_radius;

	private boolean vertical;

	private double init_angle;

	private Stroke stroke_save;

	private AffineTransform mark_tran;

	static private class FontRecord {
		private double width, height;

		private FontMetrics font_metrics;

		public int getWidth() {
			return (int) width;
		}

		public int getHeight() {
			return (int) height;
		}

		public Font getFont() {
			return getFontMetrics().getFont();
		}

		public FontMetrics getFontMetrics() {
			return font_metrics;
		}
	}

	private void initColor() {
		fg_color = fg_color_org = 0; // black
		bg_color = bg_color_org = 0xffffff; // white
		mark_tran = g2d.getTransform();
	}

	public void translate(int x, int y) {
		g2d.translate(x, y);
	}

	public void rotate(double angle) {
		g2d.rotate(angle);
	}

	public AffineTransform getTransform() {
		return g2d.getTransform();
	}

	public void setTransform(AffineTransform tran) {
		g2d.setTransform(tran);
	}

	public double getAngle() {
		double[] matrix = new double[6];
		g2d.getTransform().getMatrix(matrix);
		return Math.atan2(matrix[2], matrix[3]);
	}

	public void setColor(int color) {
		if (color >= 0) {
			g2d.setColor(new Color(color));
		} else {
			setColor();
		}
	}

	public void setColor() {
		if (fg_color < 0)
			g2d.setColor(Color.BLACK);
		else
			setColor(fg_color);
	}

	public void pushSetColor(int color) {
		color_save = g2d.getColor();
		setColor(color);
	}

	public void popSetColor() {
		g2d.setColor(color_save);
	}

	public int getColor(String key, boolean use_bw) {
		if (use_bw)
			return fg_color;
		int color = Resource.getPrefInt(key);
		return (color >= 0) ? color : fg_color;
	}

	public int getBgColor(String key, boolean use_bw) {
		if (use_bw)
			return bg_color;
		int color = Resource.getPrefInt(key);
		return (color >= 0) ? color : bg_color;
	}

	static public void setFillColor(Graphics2D g, String key, boolean use_bw) {
		if (use_bw)
			return;
		int color = Resource.getPrefInt(key);
		if (color >= 0)
			g.setColor(new Color(color));
	}

	public void setForeground(int color) {
		fg_color = color;
	}

	public void setForeground() {
		fg_color = fg_color_org;
	}

	public void setBackground(int color) {
		bg_color = color;
	}

	public void setBackground() {
		bg_color = bg_color_org;
	}

	public void setRingColor(int ring, int[] types, int[] colors) {
		int type = types[ring];
		int color = (type < 0) ? -1 : colors[type];
		setColor(color);
	}

	public void drawRotatedStringHoriz(int pos, int space, String key) {
		FontRecord r = getFontRecord();
		FontMetrics metric = r.getFontMetrics();
		int height = metric.getHeight() - metric.getLeading();
		int descent = metric.getDescent();
		int center = height / 2 - descent;
		int width = metric.stringWidth(key);
		int length = key.length();
		int delta = 0;
		boolean vert = vertical && !FileIO.isAsciiString(key, true);
		for (int k = 0; k < length; k++) {
			AffineTransform rot = g2d.getTransform();
			String str = key.substring(k, k + 1);
			int char_width = metric.stringWidth(str);
			delta += char_width / 2;
			g2d.rotate(-(0.5 * width - delta) / pos);
			g2d.translate(pos + space / 2, 0);
			if (vert) {
				g2d.rotate(getAngle() + init_angle);
			} else {
				g2d.rotate(Math.PI / 2);
			}
			g2d.translate(-char_width / 2, center);
			g2d.drawString(str, 0, 0);
			delta += char_width - (char_width / 2);
			g2d.setTransform(rot);
		}
	}

	public int getFontWidth() {
		FontRecord r = getFontRecord();
		FontMetrics metric = r.getFontMetrics();
		return getMaxAdvance(metric);
	}

	public int drawRotatedStringVert(int pos, int space, String key,
			boolean vert) {
		FontRecord r = getFontRecord();
		FontMetrics metric = r.getFontMetrics();
		int len = key.length();
		int max_width = 0;
		for (int i = 0; i < len; i++) {
			int w = metric.charWidth(key.charAt(i));
			max_width = Math.max(w, max_width);
		}
		int height = metric.getHeight();
		int leading = metric.getLeading();
		int descent = metric.getDescent();
		int t_height = height * len - leading;
		int gap = (space - t_height) / 2;
		int offset = pos + gap + descent;
		int center = (height - leading) / 2 - descent;
		AffineTransform rot = g2d.getTransform();
		for (int i = 0; i < len; i++) {
			String str = key.substring(i, i + 1);
			int char_width = metric.stringWidth(str);
			g2d.setTransform(rot);
			g2d.translate(offset + height * (len - i - 1) + center, 0);
			if (vert) {
				g2d.rotate(getAngle() + init_angle);
			} else {
				g2d.rotate(Math.PI / 2);
			}
			g2d.translate(-char_width / 2, center);
			g2d.drawString(str, 0, 0);
		}
		return gap;
	}

	public int drawRotatedStringVert(int pos, int space, String key) {
		return drawRotatedStringVert(pos, space, key, vertical);
	}

	public void drawRotatedSign(int pos, int width, boolean plus) {
		g2d.translate(pos, 0);
		if (vertical) {
			g2d.rotate(getAngle() + init_angle);
		} else {
			g2d.rotate(Math.PI / 2);
		}
		g2d.drawLine(-width / 2, 0, width / 2, 0);
		if (plus)
			g2d.drawLine(0, -width / 2, 0, width / 2);
	}

	public void drawString(String str, int x, int y) {
		g2d.drawString(str, x, y);
	}

	public int stringWidth(String str) {
		FontRecord r = getFontRecord();
		FontMetrics metric = r.getFontMetrics();
		return metric.stringWidth(str);
	}

	public double[] setFittedFont(int max_height, int radius, int n_entry,
			boolean single_row, boolean high_res) {
		int size, height, width, n_col, single_max_entry = 1;
		int[] font_size = new int[3];
		int style = Resource.getFontStyle();
		Resource.getIntArray("g2d_font_size", font_size);
		if (font_scaler > 1) {
			for (int i = 0; i < font_size.length; i++) {
				font_size[i] *= font_scaler;
			}
		}
		// decrease font size lower limit for high resolution output
		if (high_res)
			font_size[0] /= 2;
		for (int i = 0; i < (single_row ? 1 : 2); i++) {
			if (i == 0) { // single row check
				n_col = n_entry;
				height = max_height;
				size = (height == 0) ? font_size[0] : font_size[1];
			} else { // dual row check
				n_col = (n_entry / 2) + 1;
				size = font_size[1];
				height = max_height / 2;
			}
			width = (int) ((2.0 * Math.PI * radius) / (12.0 * n_col));
			for (; size >= font_size[0]; size -= font_size[2]) {
				FontRecord r = getFontRecord(size, style);
				int h = r.getHeight();
				int w = r.getWidth();
				if (h <= height && w <= width
						|| (n_entry == 1 || i > 0 || single_row)
						&& size == font_size[0]) {
					g2d.setFont(r.getFont());
					if (n_entry > 1 && i > 0 && !(h <= height && w <= width)) {
						// does not fit in dual row
						if (n_entry > single_max_entry) {
							return setFittedFont(max_height, radius,
									single_max_entry, single_row, high_res);
						} else {
							i = 0; // does not fit no matter what
							n_col = n_entry;
						}
					}
					double min_degree = degreeInWidth(radius);
					double max_entry = 30.0 / min_degree;
					if (max_entry > n_col)
						min_degree *= max_entry / n_col;
					else
						min_degree = 30.0 / (int) max_entry;
					double[] result = new double[3];
					result[0] = min_degree; // degree gap
					result[1] = Math.min((int) max_entry, n_col); // number of
					// possible entry
					result[2] = i; // is dual row
					return result;
				} else if (i == 0 && size == font_size[0]) {
					g2d.setFont(r.getFont());
					double min_degree = degreeInWidth(radius);
					single_max_entry = (int) (30.0 / min_degree);
					if (single_max_entry < 0)
						single_max_entry = 0;
				}
			}
		}
		return null;
	}

	private FontRecord getFontRecord(int size, int style) {
		int map_style = style;
		Point p = new Point(size, map_style);
		FontRecord r = (FontRecord) font_table.get(p);
		if (r == null) {
			r = new FontRecord();
			Font font = new Font(Resource.getFontName(), style, size);
			// jre 1.5 returns different FontMetrics if coordinate system is
			// rotated
			AffineTransform tr = g2d.getTransform();
			g2d.setTransform(new AffineTransform());
			Rectangle2D b = font.getMaxCharBounds(g2d.getFontRenderContext());
			r.font_metrics = g2d.getFontMetrics(font);
			r.width = b.getWidth();
			r.height = b.getHeight();
			g2d.setTransform(tr);
			font_table.put(p, r);
		}
		return r;
	}

	private FontRecord getFontRecord() {
		Font font = g2d.getFont();
		return getFontRecord(font.getSize(), font.getStyle());
	}

	public double radianInWidth(int radius) {
		FontRecord r = getFontRecord();
		return ((double) r.width) / radius;
	}

	public double degreeInWidth(int radius) {
		return radianInWidth(radius) / (2.0 * Math.PI) * 360.0;
	}

	public double degreeInWidth(String str, int radius) {
		FontRecord r = getFontRecord();
		int width = r.getFontMetrics().stringWidth(str);
		return width / (2.0 * Math.PI * radius) * 360.0;
	}

	public void initFontMetric(int radius) {
		FontRecord r = getFontRecord();
		cur_metric = r.getFontMetrics();
		cur_radius = radius;
	}

	public double degreeInWidth(String left, String right) {
		char[] array = left.toCharArray();
		int l_width = 0;
		for (int i = 0; i < array.length; i++)
			l_width = Math.max(l_width, cur_metric.charWidth(array[i]));
		array = right.toCharArray();
		int r_width = 0;
		for (int i = 0; i < array.length; i++)
			r_width = Math.max(r_width, cur_metric.charWidth(array[i]));
		return 0.5 * (l_width + r_width) / (2.0 * Math.PI * cur_radius) * 360.0;
	}

	public int getFontHeight() {
		FontRecord r = getFontRecord();
		return r.getHeight();
	}

	public void init(Graphics2D new_g2d, int scaler, String pre,
			boolean rotate, boolean vert) {
		g2d = new_g2d;
		initColor();
		font_scaler = scaler;
		prefix = pre;
		color_key = null;
		color_key_color = null;
		font_size_override = 0;
		vertical = vert;
		init_angle = rotate ? (Math.PI / 2.0) : 0.0;
	}

	public void reset() {
		g2d.setTransform(mark_tran);
		setColor();
	}

	public void drawRect(int x, int y, int width, int height) {
		setColor();
		g2d.drawRect(x, y, width, height);
	}

	public void fillRect(int x, int y, int width, int height) {
		setColor(bg_color);
		g2d.fillRect(x, y, width, height);
	}

	public void drawCircle(int radius) {
		setColor();
		g2d.drawOval(-radius, -radius, radius * 2, radius * 2);
	}

	public void drawDiamond(int radius) {
		setColor();
		g2d.drawLine(-radius, 0, 0, radius);
		g2d.drawLine(0, radius, radius, 0);
		g2d.drawLine(radius, 0, 0, -radius);
		g2d.drawLine(0, -radius, -radius, 0);
	}

	public void drawDashCircle(int dash_type, int radius) {
		setColor();
		pushSetStroke(dash_type);
		g2d.drawOval(-radius, -radius, radius * 2, radius * 2);
		popStroke();
	}

	public void fillCircle(int x, int y, int width, int height) {
		setColor(bg_color);
		g2d.fillArc(x, y, width, height, 0, 360);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		setColor();
		g2d.drawLine(x1, y1, x2, y2);
	}

	public void drawDashLine(int dash_type, int x1, int y1, int x2, int y2) {
		setColor();
		pushSetStroke(dash_type);
		g2d.drawLine(x1, y1, x2, y2);
		popStroke();
	}

	public void drawWideLine(int width, int x1, int y1, int x2, int y2) {
		setColor();
		Stroke stroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));
		g2d.drawLine(x1, y1, x2, y2);
		g2d.setStroke(stroke);
	}

	private void pushSetStroke(int dash_type) {
		stroke_save = g2d.getStroke();
		switch (dash_type) {
		case DASH:
			g2d.setStroke(dashed);
			break;
		case SPARSE_DASH:
			g2d.setStroke(sparse_dashed);
			break;
		case DENSE_DASH:
			g2d.setStroke(dense_dashed);
			break;
		case DOT_DASH:
			g2d.setStroke(dot_dashed);
			break;
		case DOT:
			g2d.setStroke(dotted);
			break;
		default: // solid
			break;
		}
	}

	private void popStroke() {
		g2d.setStroke(stroke_save);
	}

	public void paintFillArc(int color, int lower_radius, int upper_radius,
			double start_rad, double end_rad) {
		Paint save = g2d.getPaint();
		g2d.setPaint(new Color(color));
		Polygon p = ArcToPolygon(lower_radius, upper_radius, start_rad, end_rad);
		g2d.fill(p);
		g2d.setPaint(save);
	}

	public void fillArc(int lower_radius, int upper_radius, double start_rad,
			double end_rad) {
		Polygon p = ArcToPolygon(lower_radius, upper_radius, start_rad, end_rad);
		setColor(bg_color);
		g2d.fill(p);
	}

	private Polygon ArcToPolygon(int lower_radius, int upper_radius,
			double start_rad, double end_rad) {
		double delta = end_rad - start_rad;
		if (delta > 0.0)
			delta -= 2.0 * Math.PI;
		int line_width = (int) Math.ceil(-delta * (lower_radius + upper_radius)
				/ 2.0);
		int num_line = line_width / POLYGON_PRECISION;
		if (num_line < 1)
			num_line = 1;
		line_width = (int) Math.ceil(((double) line_width) / num_line);
		delta /= num_line;
		AffineTransform rot = new AffineTransform();
		rot.rotate(-start_rad);
		Point2D pt = new Point2D.Double(0.0, 0.0);
		Polygon p = new Polygon();
		for (int i = 0; i <= num_line; i++) {
			pt.setLocation(upper_radius, 0);
			rot.transform(pt, pt);
			p
					.addPoint((int) Math.round(pt.getX()), (int) Math.round(pt
							.getY()));
			rot.rotate(-delta);
		}
		for (int i = 0; i <= num_line; i++) {
			rot.rotate(delta);
			pt.setLocation(lower_radius, 0);
			rot.transform(pt, pt);
			p
					.addPoint((int) Math.round(pt.getX()), (int) Math.round(pt
							.getY()));
		}
		return p;
	}

	public void setSpecialStringColor(int[] color, String[] key) {
		color_key = key;
		color_key_color = color;
	}

	public Point drawAlignStringVert(String key, int x, int y, boolean smaller,
			boolean find_size) {
		int big_size = Resource.getInt(prefix + "print_big_font_size")
				* font_scaler;
		FontRecord r = getFontRecord(big_size, Font.BOLD);
		Font big_font = r.getFont();
		g2d.setFont(big_font);
		FontMetrics metric = g2d.getFontMetrics();
		int big_width = getMaxAdvance(metric);
		int big_height = metric.getHeight();
		int size = Resource.getInt(prefix
				+ (smaller ? "print_smaller_med_font_size"
						: "print_small_med_font_size"))
				* font_scaler;
		r = getFontRecord(size, Font.PLAIN);
		Font med_font = r.getFont();
		g2d.setFont(med_font);
		metric = g2d.getFontMetrics();
		int width = getMaxAdvance(metric);
		int height = metric.getHeight();
		int n_x = x, n_y = y;
		width = -width;
		big_width = -big_width;
		n_x += big_width;
		int max_y = n_y;
		boolean use_big = false;
		int len = key.length();
		for (int i = 0; i < len; i++) {
			String str = key.substring(i, i + 1);
			if (str.equals("|")) {
				max_y = Math.max(max_y, n_y);
				n_y = y;
				n_x += use_big ? big_width : width;
			} else if (str.equals("$")) {
				n_y = y;
				n_x += (use_big ? big_width : width) / 4;
			} else if (str.equals("%")) {
				n_y += height / 2;
			} else if (str.equals(">")) {
				use_big = true;
				g2d.setFont(big_font);
			} else if (str.equals("<")) {
				use_big = false;
				g2d.setFont(med_font);
			} else {
				n_y += use_big ? big_height : height;
				if (!find_size)
					g2d.drawString(str, n_x, n_y);
			}
		}
		max_y = Math.max(max_y, n_y);
		return new Point(x - n_x, max_y - y);
	}

	public void drawLargeBoldStringHoriz(String key, int x, int y,
			boolean non_eng) {
		int size = Resource.getInt(prefix + "print_med_big_font_size")
				* font_scaler;
		g2d.setFont(new Font(non_eng ? Resource.getFontName() : Resource
				.getEnFontName(), Font.BOLD, size));
		FontMetrics metric = g2d.getFontMetrics();
		g2d.drawString(key, x, y + metric.getHeight());
	}

	public Point drawStyledStringHoriz(String key, int x, int y, int style,
			boolean find_size) {
		int size = Resource.getInt(prefix + "print_smallest_font_size")
				* font_scaler;
		FontRecord r = getFontRecord(size, style);
		g2d.setFont(r.getFont());
		FontMetrics metric = g2d.getFontMetrics();
		if (!find_size)
			g2d.drawString(key, x, y + metric.getHeight());
		return new Point(metric.stringWidth(key), metric.getHeight());
	}

	// template: 'h' for half width, 'f' for full width, 'q' for 3 quarter width
	// 'd' for double width, 's' for width same as height, 'D' for double with
	// with centering, 'E' for 1.5 with centering. '.' extend last format until
	// end of line
	public Point drawAlignStringHoriz(String key, int x, int y,
			String template, int[] color_array, boolean find_size) {
		int size = ((font_size_override > 0) ? font_size_override : Resource
				.getInt(prefix + "print_smallest_font_size"))
				* font_scaler;
		FontRecord r = getFontRecord(size, Font.PLAIN);
		g2d.setFont(r.getFont());
		FontMetrics metric = g2d.getFontMetrics();
		int width = getMaxAdvance(metric);
		int height = metric.getHeight();
		int n_x = x, n_y = y + height;
		int max_x = 0;
		int len = key.length();
		int[] char_width = null;
		if (template != null) {
			char_width = new int[template.length()];
			for (int i = 0; i < char_width.length; i++) {
				switch (template.charAt(i)) {
				case 'h':
					char_width[i] = width / 2;
					break;
				case 'q':
					char_width[i] = 3 * width / 4;
					break;
				case 'd':
					char_width[i] = 2 * width;
					break;
				case 's':
					char_width[i] = height;
					break;
				case 'D':
					char_width[i] = -2 * width;
					break;
				case 'E':
					char_width[i] = -(width + width / 2);
					break;
				case '.':
					char_width[i] = 0;
					break;
				default:
					char_width[i] = width;
					break;
				}
			}
		}
		int index = 0, row = 0, col = 0;
		for (int i = 0; i < len; i++) {
			String str = key.substring(i, i + 1);
			if (str.equals("|")) {
				max_x = Math.max(max_x, n_x);
				n_x = x;
				n_y += height;
				if (char_width != null && index < char_width.length
						&& char_width[index] == 0)
					index++;
				row++;
				col = 0;
			} else if (str.equals("$")) {
				n_x = x;
				n_y += height / 4;
				if (char_width != null && index < char_width.length
						&& char_width[index] == 0)
					index++;
				row++;
			} else {
				int advance = width;
				if (char_width != null) {
					if (index >= char_width.length)
						index = 0;
					advance = char_width[index++];
					if (advance == 0)
						advance = char_width[--index - 1];
					if (advance < 0) {
						// center
						advance = -advance;
						int pre = (advance - width) / 2;
						n_x += pre;
						advance = advance - pre;
					}
				}
				if (!find_size) {
					if (!drawColorString(str, n_x, n_y)) {
						if (color_array != null
								&& (row == 0 && col > 0 || col == 0 && row > 0)) {
							Color color = g2d.getColor();
							setColor(color_array[(row == 0) ? 1 : 0]);
							g2d.drawString(str, n_x, n_y);
							g2d.setColor(color);
						} else {
							g2d.drawString(str, n_x, n_y);
						}
					}
				}
				n_x += advance;
				col++;
			}
		}
		max_x = Math.max(max_x, n_x);
		return new Point(max_x - x, n_y - y);
	}

	public void setFontSizeOverride(int val) {
		font_size_override = val;
	}

	public Point drawStringHoriz(String key, int x, int y, boolean align_right,
			boolean find_size) {
		return drawSizeStringHoriz("print_smallest_font_size", key, x, y,
				align_right, find_size);
	}

	public Point drawLargeStringHoriz(String key, int x, int y,
			boolean align_right, boolean find_size) {
		return drawSizeStringHoriz("print_med_font_size", key, x, y,
				align_right, find_size);
	}

	public Point drawSizeStringHoriz(String font_size, String key, int x,
			int y, boolean align_right, boolean find_size) {
		int size = Resource.getInt(prefix + font_size) * font_scaler;
		FontRecord r = getFontRecord(size, Font.PLAIN);
		g2d.setFont(r.getFont());
		FontMetrics metric = g2d.getFontMetrics();
		int max_width = 0;
		int height = metric.getHeight();
		int n_y = y + height;
		while (key != null && !key.equals("")) {
			String str;
			int index = key.indexOf("|");
			if (index >= 0) {
				str = key.substring(0, index);
				key = key.substring(index + 1);
			} else {
				str = key;
				key = null;
			}
			int width = metric.stringWidth(str);
			max_width = Math.max(width, max_width);
			if (!find_size) {
				int n_x = align_right ? (x - width) : x;
				if (!drawColorString(str, n_x, n_y))
					g2d.drawString(str, n_x, n_y);
			}
			n_y += height;
		}
		return new Point(max_width, n_y - y - height);
	}

	private boolean drawColorString(String str, int x, int y) {
		if (color_key != null) {
			for (int i = 0; i < color_key.length; i++) {
				if (str.equals(color_key[i])) {
					Color color = g2d.getColor();
					setColor(color_key_color[i]);
					g2d.drawString(str, x, y);
					g2d.setColor(color);
					return true;
				}
			}
		}
		return false;
	}

	private int getMaxAdvance(FontMetrics metric) {
		// may return negative value in jre 1.6.0_05 (bug???)
		return Math.abs(metric.getMaxAdvance());
	}

	// dir: 1 => upper, -1 => lower, 0 => full
	public void frameTable(int dir, int start_x, int start_y, int width,
			int height, LinkedList half_list, LinkedList four_list) {
		FontMetrics metric = g2d.getFontMetrics();
		int x_inc = metric.getHeight();
		int y_inc = x_inc;
		start_x -= getMaxAdvance(metric) / 16;
		start_y += 3 * metric.getDescent();
		int end_x = start_x + width;
		int end_y = start_y + height;
		int x, y;
		if (dir >= 0) {
			y = start_y + y_inc;
			int i = 0;
			for (; y < end_y; y += y_inc, i++) {
				for (x = start_x + i * x_inc; x < end_x; x += x_inc)
					drawLine(x, y, end_x, y);
			}
			x = end_x - x_inc;
			i = 0;
			for (; x > start_x; x -= x_inc, i++) {
				for (y = end_y - i * y_inc; y > start_y; y -= y_inc)
					drawLine(x, start_y, x, y);
			}
		}
		if (dir <= 0) {
			y = end_y - y_inc;
			int i = 0;
			for (; y > start_y; y -= y_inc, i++) {
				for (x = end_x - i * x_inc; x > start_x; x -= x_inc)
					drawLine(start_x, y, x, y);
			}
			x = start_x + x_inc;
			i = 0;
			for (; x < end_x; x += x_inc, i++) {
				for (y = start_y + i * y_inc; y < end_y; y += y_inc)
					drawLine(x, y, x, end_y);
			}
		}
		start_x += (dir > 0) ? 0 : x_inc;
		start_y += (dir < 0) ? 0 : y_inc;
		for (int i = 0; i < 2; i++) {
			LinkedList list = (i == 0) ? half_list : four_list;
			if (list == null)
				continue;
			try {
				ListIterator iter = list.listIterator();
				for (;;) {
					Point loc = (Point) iter.next();
					x = start_x + loc.x * x_inc;
					y = start_y + loc.y * y_inc;
					drawLine(x + x_inc, y, x, y + y_inc);
					if (i > 0)
						drawLine(x, y, x + x_inc, y + y_inc);
				}
			} catch (NoSuchElementException e) {
			}
		}
	}

	static public void resetFontTable() {
		font_table = new Hashtable();
	}
}