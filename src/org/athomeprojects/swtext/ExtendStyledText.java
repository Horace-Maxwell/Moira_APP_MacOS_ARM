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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.athomeprojects.base.DataSet;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class ExtendStyledText extends StyledText {
	private final int MAX_STACK_SIZE = 100;

	static private Color[] hilite_color;

	static private String[] hilite_names;

	private boolean in_undo;

	private int bold_count;

	private int[] color_count;

	private LinkedList undo_stack, redo_stack;

	private String select_text;

	private StyleRange[] select_styles, modify_styles;

	public ExtendStyledText(Composite parent, int style) {
		super(parent, style);
		addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				if (in_undo || undo_stack == null || event.start >= event.end)
					return;
				modify_styles = getTextStyle(event.start, event.end
						- event.start);
			}
		});
		addExtendedModifyListener(new ExtendedModifyListener() {
			public void modifyText(ExtendedModifyEvent event) {
				if (in_undo || undo_stack == null)
					return;
				StackEntry entry = new StackEntry();
				entry.pos = event.start;
				entry.len = event.length;
				entry.str = event.replacedText;
				entry.styles = modify_styles;
				if (undo_stack.size() == MAX_STACK_SIZE)
					undo_stack.remove(undo_stack.size() - 1);
				undo_stack.add(0, entry);
				modify_styles = null;
				mergeHilite(entry.pos, entry.len);
			}
		});
		setUndoEnable(false);
	}

	static public String[] getHiliteNames() {
		if (hilite_color == null) {
			String prefix = Resource.getString("edit_hilite");
			String[] numbers = Resource.getStringArray("numbers");
			int[] color_data = Resource.getPrefIntArray("text_hilite_bg_color");
			hilite_color = new Color[color_data.length];
			hilite_names = new String[color_data.length];
			for (int i = 0; i < color_data.length; i++) {
				int rgb = color_data[i];
				hilite_color[i] = ColorManager.getColor(rgb);
				String name = prefix + numbers[i + 1];
				if (i == 0)
					name += "\tCtrl+H";
				hilite_names[i] = name;
			}
		}
		return hilite_names;
	}

	static public int getHiliteIndex(String name) {
		for (int i = 0; i < hilite_names.length; i++) {
			if (hilite_names[i].equals(name))
				return i;
		}
		return -1;
	}

	public void setUndoEnable(boolean enable) {
		if (enable) {
			undo_stack = new LinkedList();
			redo_stack = new LinkedList();
		} else {
			undo_stack = redo_stack = null;
		}
		select_styles = modify_styles = null;
		select_text = null;
		in_undo = false;
	}

	public void undo() {
		undoEdit(undo_stack, redo_stack);
	}

	public void redo() {
		undoEdit(redo_stack, undo_stack);
	}

	public void copy() {
		if (!recordSelection())
			return;
		super.copy();
	}

	public void cut() {
		if (!recordSelection())
			return;
		super.cut();
	}

	private boolean recordSelection() {
		if (undo_stack == null)
			return true;
		Point selection = getSelectionRange();
		if (selection.y <= 0)
			return false;
		select_text = getTextRange(selection.x, selection.y);
		select_styles = getTextStyle(selection.x, selection.y);
		return true;
	}

	public void paste() {
		super.paste();
		if (select_styles != null && select_styles.length > 0) {
			int e = this.getCaretOffset();
			int l = select_text.length();
			int s = e - l;
			if (s >= 0 && select_text.equals(getTextRange(s, l)))
				setTextStyle(select_styles, s);
		}
	}

	public void toggleBold() {
		Point selection = getSelectionRange();
		if (selection.y <= 0)
			return;
		StyleRange[] styles = getTextStyle(selection.x, selection.y);
		findFequency(styles);
		int threshold = selection.y;
		if ((threshold % 2) != 0)
			threshold = (threshold + 1) >> 1;
		else
			threshold >>= 1;
		boolean bold = bold_count < threshold;
		int best_count = selection.y;
		for (int i = 0; i < color_count.length; i++)
			best_count -= color_count[i];
		int hilite = -1;
		for (int i = 0; i < color_count.length; i++) {
			if (color_count[i] >= best_count) {
				best_count = color_count[i];
				hilite = i;
			}
		}
		setBoldHilite(selection, bold, hilite);
		setSelection(selection.x + selection.y);
		if (in_undo || undo_stack == null)
			return;
		StackEntry entry = new StackEntry();
		entry.pos = selection.x;
		entry.len = selection.y;
		entry.str = getTextRange(selection.x, selection.y);
		entry.styles = styles;
		if (undo_stack.size() == MAX_STACK_SIZE)
			undo_stack.remove(undo_stack.size() - 1);
		undo_stack.add(0, entry);
	}

	public void toggleHilite(int index) {
		Point selection = getSelectionRange();
		if (selection.y <= 0)
			return;
		StyleRange[] styles = getTextStyle(selection.x, selection.y);
		findFequency(styles);
		int threshold = selection.y;
		if ((threshold % 2) != 0)
			threshold = (threshold + 1) >> 1;
		else
			threshold >>= 1;
		boolean bold = bold_count >= threshold;
		int hilite = index;
		if (color_count[index] >= threshold)
			hilite = -1;
		setBoldHilite(selection, bold, hilite);
		setSelection(selection.x + selection.y);
		if (in_undo || undo_stack == null)
			return;
		StackEntry entry = new StackEntry();
		entry.pos = selection.x;
		entry.len = selection.y;
		entry.str = getTextRange(selection.x, selection.y);
		entry.styles = styles;
		if (undo_stack.size() == MAX_STACK_SIZE)
			undo_stack.remove(undo_stack.size() - 1);
		undo_stack.add(0, entry);
	}

	private void findFequency(StyleRange[] styles) {
		bold_count = 0;
		if (color_count == null)
			color_count = new int[hilite_color.length];
		else
			Arrays.fill(color_count, 0);
		for (int i = 0; i < styles.length; i++) {
			Color color = styles[i].background;
			if (color != null) {
				for (int j = 0; j < hilite_color.length; j++) {
					if (color == hilite_color[j]) {
						color_count[j] += styles[i].length;
						break;
					}
				}
			}
			if (styles[i].fontStyle == SWT.BOLD)
				bold_count += styles[i].length;
		}
	}

	public String getTextOnly() {
		return super.getText();
	}

	public String getText() {
		String str = hiliteToString();
		if (str == null)
			return super.getText();
		else
			return super.getText() + str;
	}

	public void setText(String str) {
		int index = str.indexOf(DataSet.STYLE_MARKER);
		if (index < 0) {
			super.setText(str);
		} else {
			super.setText(str.substring(0, index));
			stringToHilite(str.substring(index));
		}
	}

	private String hiliteToString() {
		StyleRange[] styles = getStyleRanges();
		if (styles.length == 0)
			return null;
		String str = "";
		for (int index = 0; index < hilite_color.length; index++) {
			str += DataSet.STYLE_MARKER;
			for (int i = 0; i < styles.length; i++) {
				StyleRange style = styles[i];
				if (style.background != hilite_color[index])
					continue;
				str += "," + ((style.fontStyle == SWT.BOLD) ? "b" : "")
						+ style.start + "," + style.length;
			}
		}
		str += DataSet.STYLE_MARKER;
		for (int i = 0; i < styles.length; i++) {
			StyleRange style = styles[i];
			if (style.background != null || style.fontStyle != SWT.BOLD)
				continue;
			str += "," + ((style.fontStyle == SWT.BOLD) ? "b" : "")
					+ style.start + "," + style.length;
		}
		return str;
	}

	private void stringToHilite(String str) {
		int index = -1;
		Point selection = new Point(0, 0);
		StringTokenizer mst = new StringTokenizer(str, DataSet.STYLE_MARKER,
				true);
		while (mst.hasMoreTokens()) {
			String token = mst.nextToken();
			if (token.equals(DataSet.STYLE_MARKER)) {
				index++;
			} else {
				StringTokenizer st = new StringTokenizer(token, ",");
				while (st.hasMoreTokens()) {
					String first = st.nextToken().trim();
					boolean bold = first.charAt(0) == 'b';
					if (bold)
						first = first.substring(1);
					String second = st.nextToken().trim();
					selection.x = FileIO.parseInt(first, -1, true);
					selection.y = FileIO.parseInt(second, -1, true);
					if (selection.x >= 0 && selection.y >= 0) {
						setBoldHilite(selection, bold,
								(index >= hilite_color.length) ? -1 : index);
					}
				}
			}
		}
	}

	private void setBoldHilite(Point selection, boolean bold, int index) {
		StyleRange style = new StyleRange();
		style.start = selection.x;
		style.length = selection.y;
		style.background = (index >= 0 && index < hilite_color.length) ? hilite_color[index]
				: null;
		style.fontStyle = bold ? SWT.BOLD : SWT.NORMAL;
		setStyleRange(style);
	}

	private void mergeHilite(int start, int len) {
		int s = start - 1;
		int e = start + len;
		StyleRange s_sr = (s >= 0) ? getStyleRangeAtOffset(s) : null;
		StyleRange e_sr = (e < getCharCount()) ? getStyleRangeAtOffset(e)
				: null;
		if (s_sr != null && e_sr != null && s_sr.background == e_sr.background
				&& s_sr.fontStyle == e_sr.fontStyle) {
			setBoldHilite(new Point(start, len), s_sr.fontStyle == SWT.BOLD,
					getHiliteIndex(s_sr.background));
		}
	}

	private int getHiliteIndex(Color color) {
		for (int i = 0; i < hilite_color.length; i++) {
			if (hilite_color[i] == color)
				return i;
		}
		return -1;
	}

	private void undoEdit(LinkedList from, LinkedList to) {
		if (from == null || from.size() == 0)
			return;
		in_undo = true;
		StackEntry entry = (StackEntry) from.remove(0);
		String str = null;
		try {
			str = getTextRange(entry.pos, entry.len);
		} catch (IllegalArgumentException e) {
			return;
		}
		StyleRange[] styles = getTextStyle(entry.pos, entry.len);
		replaceTextRange(entry.pos, entry.len, entry.str);
		setTextStyle(entry.styles, entry.pos);
		entry.len = entry.str.length();
		entry.str = str;
		entry.styles = styles;
		if (to.size() == MAX_STACK_SIZE)
			to.remove(to.size() - 1);
		to.add(0, entry);
		in_undo = false;
	}

	private StyleRange[] getTextStyle(int start, int len) {
		StyleRange[] styles = getStyleRanges(start, len);
		for (int i = 0; i < styles.length; i++) {
			StyleRange style = styles[i];
			styles[i] = new StyleRange(style.start - start, style.length,
					style.foreground, style.background, style.fontStyle);
		}
		return styles;
	}

	private void setTextStyle(StyleRange[] styles, int offset) {
		if (styles == null)
			return;
		for (int i = 0; i < styles.length; i++) {
			StyleRange style = styles[i];
			setStyleRange(new StyleRange(style.start + offset, style.length,
					style.foreground, style.background, style.fontStyle));
		}
	}

	private class StackEntry {
		public String str;

		public StyleRange[] styles;

		public int pos, len;
	}
}