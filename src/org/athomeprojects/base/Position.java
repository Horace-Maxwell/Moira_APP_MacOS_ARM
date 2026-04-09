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
package org.athomeprojects.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Position {
	private final double TWO_PI = 2.0 * Math.PI;

	private final double HALF_PI = 0.5 * Math.PI;

	private final double QUARTER_PI = 0.25 * Math.PI;

	private final double EPSILON = 1.0e-5;

	private final double BEST_ANGLE = HALF_PI;

	private final double SECOND_BEST_ANGLE = 1.5 * Math.PI;

	static private double lx, ly, ux, uy, gap, font_height;

	static private Angle[] prefer_angle;

	private int index, state;

	private String name;

	private boolean locked;

	private double pos, shift;

	private double pos_x, pos_y, radius, h_radian;

	private LinkedList block_list;

	public Position(String str, int ind, double val, int s_state, boolean lock) {
		name = str;
		index = ind;
		pos = val;
		shift = 0.0;
		state = s_state;
		locked = lock;
	}

	public Position(int ind, int x, int y, int rad, double h_rad) {
		index = ind;
		pos_x = x;
		pos_y = y;
		radius = rad;
		h_radian = h_rad;
		pos = Calculate.INVALID;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public double getLocation() {
		return pos;
	}

	public double getZodiacDegree() {
		return pos % 30.0;
	}

	public int getState() {
		return state;
	}

	public boolean getLocked() {
		return locked;
	}

	public double getShift() {
		return shift;
	}

	public double getShiftedLocation() {
		if (pos == Double.NEGATIVE_INFINITY) {
			return Double.NEGATIVE_INFINITY;
		} else {
			double degree = pos + shift;
			while (degree < 0.0)
				degree += 360.0;
			while (degree >= 360.0)
				degree -= 360.0;
			return degree;
		}
	}

	public void setShift(double val) {
		shift = val;
	}

	public void addShift(double val) {
		shift += val;
	}

	public double getRadius() {
		return radius;
	}

	public double getHalfAngle() {
		return h_radian;
	}

	public void setAngle(double angle) {
		pos = angle;
	}

	public double getAngle() {
		return pos;
	}

	public double getX() {
		return pos_x;
	}

	public double getY() {
		return pos_y;
	}

	public double getExtendedRadius() {
		return radius + gap;
	}

	public double getRange() {
		return radius + gap + font_height;
	}

	public void reset() {
		shift = 0.0;
		locked = false;
		block_list = null;
	}

	public void blockBoundary(double zoom) {
		double range = getRange();
		// left edge
		double d = pos_x - zoom * lx;
		if (d < range) {
			double angle = Math.acos(d / range) + h_radian;
			blockAngle(Math.PI, angle, null);
		}
		// right edge
		d = zoom * ux - pos_x;
		if (d < range) {
			double angle = Math.acos(d / range) + h_radian;
			blockAngle(0.0, angle, null);
		}
		// bottom edge
		d = zoom * uy - pos_y;
		if (d < range) {
			double angle = Math.acos(d / range) + h_radian;
			blockAngle(HALF_PI, angle, null);
		}
		// top edge
		d = pos_y - zoom * ly;
		if (d < range) {
			double angle = Math.acos(d / range) + h_radian;
			blockAngle(1.5 * Math.PI, angle, null);
		}
	}

	private void blockAngle(double orientation, double half_angle, Position p) {
		if (block_list == null)
			block_list = new LinkedList();
		double lower = orientation - half_angle;
		double upper = orientation + half_angle;
		if (lower < 0.0) {
			Range range = new Range(lower + TWO_PI, TWO_PI + EPSILON,
					orientation, p);
			block_list.add(range);
			lower = -EPSILON;
		}
		if (upper >= TWO_PI) {
			Range range = new Range(-EPSILON, upper - TWO_PI, orientation, p);
			block_list.add(range);
			upper = TWO_PI + EPSILON;
		}
		Range range = new Range(lower, upper, orientation, p);
		block_list.add(range);
	}

	public double distance(Position p) {
		double dx = p.pos_x - pos_x;
		double dy = p.pos_y - pos_y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean contain(Position p) {
		return distance(p) + p.getRadius() <= getExtendedRadius();
	}

	public void block(Position p, double rad) {
		double dx = p.pos_x - pos_x;
		double dy = p.pos_y - pos_y;
		double d = Math.sqrt(dx * dx + dy * dy);
		double range = getRange();
		if (rad + range <= d)
			return;
		double orientation = Math.atan2(dy, dx);
		if (orientation < 0.0)
			orientation += TWO_PI;
		double angle;
		if (d + rad <= range) { // complete inside
			angle = (rad >= d) ? (HALF_PI - EPSILON)
					: (Math.asin(rad / d) + h_radian);
		} else {
			// cosine rule
			angle = Math.acos((d * d + range * range - rad * rad)
					/ (2.0 * d * range))
					+ h_radian;
		}
		blockAngle(orientation, angle, p);
	}

	public void blockContain(Position p) {
		double dx = p.pos_x - pos_x;
		double dy = p.pos_y - pos_y;
		double orientation = Math.atan2(dy, dx);
		if (orientation < 0.0)
			orientation += TWO_PI;
		blockAngle(orientation, 0.5 * QUARTER_PI, p);
	}

	// wrap around -18- to 180 degree boundary
	public boolean contain(Position p, double degree, double scaler) {
		double dx = Math.abs(p.pos_x - pos_x) / scaler;
		if (dx > 180.0)
			dx = 360.0 - dx;
		double dy = (p.pos_y - pos_y) / scaler;
		double d = Math.sqrt(dx * dx + dy * dy);
		return d <= degree;
	}

	public double getValidAngle() {
		if (block_list == null)
			return BEST_ANGLE;
		if (prefer_angle == null) {
			prefer_angle = new Angle[8];
			for (int i = 0; i < prefer_angle.length; i++)
				prefer_angle[i] = new Angle(i * QUARTER_PI);
		}
		sortBlockList();
		double orientation = getPreferOrientation();
		for (int i = 0; i < prefer_angle.length; i++)
			prefer_angle[i].setOrientation(orientation);
		Arrays.sort(prefer_angle, 0, prefer_angle.length, new Comparator() {
			public int compare(Object a, Object b) {
				double d_a = ((Angle) a).delta;
				double d_b = ((Angle) b).delta;
				if (d_a < d_b)
					return -1;
				else if (d_a > d_b)
					return 1;
				else
					return 0;
			}
		});
		// prefer angle
		for (int i = 0; i < prefer_angle.length; i++) {
			if (isVacant(prefer_angle[i].angle))
				return prefer_angle[i].angle;
		}
		// any available angle
		double val = 0.0;
		for (ListIterator iter = block_list.listIterator(); iter.hasNext();) {
			Range range = (Range) iter.next();
			if (range.lower >= val)
				return val;
			else if (range.upper > val)
				val = range.upper;
		}
		return Calculate.INVALID;
	}

	private boolean isVacant(double val) {
		for (ListIterator iter = block_list.listIterator(); iter.hasNext();) {
			Range range = (Range) iter.next();
			if (range.lower >= val)
				return true;
			else if (range.upper > val)
				return false;
		}
		return true;
	}

	public double getValidGap() {
		if (block_list == null)
			return TWO_PI;
		sortBlockList();
		double val = 0.0, valid_gap = 0.0;
		for (ListIterator iter = block_list.listIterator(); iter.hasNext();) {
			Range range = (Range) iter.next();
			if (range.lower > val) {
				valid_gap += range.lower - val;
				val = range.upper;
			} else if (range.upper > val) {
				val = range.upper;
			}
		}
		if (val < TWO_PI)
			valid_gap += TWO_PI - val;
		return valid_gap;
	}

	private double getPreferOrientation() {
		double val = 0.0;
		double first_width = -1.0, width;
		double best_width = -1.0, best_pos = BEST_ANGLE;
		boolean has_second_best = false;
		for (ListIterator iter = block_list.listIterator(); iter.hasNext();) {
			Range range = (Range) iter.next();
			if (range.lower >= val) {
				width = range.lower - val;
				if (width > best_width) {
					best_width = width;
					if (cover(val, val + width, BEST_ANGLE))
						return BEST_ANGLE;
					else if (cover(val, val + width, SECOND_BEST_ANGLE))
						has_second_best = true;
					else
						best_pos = val + 0.5 * width;
				}
				if (val == 0.0)
					first_width = width;
				val = range.upper;
			} else if (range.upper > val) {
				val = range.upper;
			}
		}
		width = TWO_PI - val;
		if (width >= 0.0) {
			if (first_width >= 0)
				width += first_width;
			if (width > best_width) {
				best_width = width;
				if (cover(val, val + width, BEST_ANGLE))
					return BEST_ANGLE;
				else if (cover(val, val + width, SECOND_BEST_ANGLE))
					has_second_best = true;
				else
					best_pos = val + 0.5 * width;
				if (best_pos >= TWO_PI)
					best_pos -= TWO_PI;
			}
		}
		return has_second_best ? SECOND_BEST_ANGLE : best_pos;
	}

	private boolean cover(double lower, double upper, double target) {
		return lower + QUARTER_PI <= target && upper - QUARTER_PI >= target;
	}

	private void sortBlockList() {
		if (block_list.size() == 1)
			return;
		Collections.sort(block_list, new Comparator() {
			public int compare(Object a, Object b) {
				Range p_a = (Range) a;
				Range p_b = (Range) b;
				if (p_a.lower < p_b.lower)
					return -1;
				else if (p_a.lower > p_b.lower)
					return 1;
				else
					return 0;
			}
		});
	}

	static public Position getPosition(Position[] position, int length,
			int index) {
		while (index < length)
			index += length;
		while (index >= length)
			index -= length;
		return position[index];
	}

	static public int getPositionIndex(Position[] position, int length,
			int index) {
		for (int i = 0; i < length; i++) {
			if (position[i].index == index)
				return i;
		}
		return -1;
	}

	static public void setBound(int t_lx, int t_ly, int t_ux, int t_uy) {
		lx = t_lx;
		ly = t_ly;
		ux = t_ux;
		uy = t_uy;
	}

	static public void setGapFontHeight(int t_gap, int t_height) {
		gap = t_gap;
		font_height = t_height;
	}

	static public int getGap() {
		return (int) gap;
	}

	static public int getFontHeight() {
		return (int) font_height;
	}

	private class Range {
		private double lower, upper;

		public Range(double l_val, double u_val, double orient, Position p) {
			lower = l_val;
			upper = u_val;
		}
	}

	private class Angle {
		private double angle, delta;

		public Angle(double t_angle) {
			angle = t_angle;
		}

		public void setOrientation(double orient) {
			delta = Math.abs(angle - orient);
			if (delta > Math.PI)
				delta = TWO_PI - delta;
		}
	}
}