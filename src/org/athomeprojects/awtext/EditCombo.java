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
package org.athomeprojects.awtext;

import java.awt.Dimension;

import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.plaf.metal.MetalComboBoxEditor;

public class EditCombo extends JComboBox {
	private static final long serialVersionUID = 1220734120040635773L;

	private Editor edit;

	private int preferred_width;

	private class Editor extends MetalComboBoxEditor.UIResource {
		public InputMap getTextInputMap() {
			return editor.getInputMap();
		}

		public String getSelection(boolean cut) {
			String str = editor.getSelectedText();
			if (cut)
				editor.replaceSelection("");
			return str;
		}

		public void setSelection(String str) {
			editor.replaceSelection(str);
		}
	}

	public EditCombo() {
		initData();
	}

	public EditCombo(Object[] items) {
		super(items);
		initData();
	}

	private void initData() {
		setEditable(true);
		edit = new Editor();
		setEditor(edit);
	}

	public InputMap getTextInputMap() {
		return edit.getTextInputMap();
	}

	public String getSelection(boolean cut) {
		return edit.getSelection(cut);
	}

	public void setSelection(String str) {
		edit.setSelection(str);
	}

	public void setPreferredWidth(int width) {
		preferred_width = width;
	}

	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		if (preferred_width > 0 && dim.width != preferred_width) {
			dim.width = preferred_width;
			super.setPreferredSize(dim);
		}
		return super.getPreferredSize();
	}
}