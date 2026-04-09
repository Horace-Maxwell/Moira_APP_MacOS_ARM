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

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.athomeprojects.base.Resource;
import org.athomeprojects.swtext.ColorManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ColorFieldDialog extends Dialog {
	static public final int TAB_FONT = 0;

	static public final int TAB_BACKGROUND = 1;

	static public final int FOREGROUND = 2;

	static public final int BACKGROUND = 3;

	static public final int BIRTH_SPEED = 4;

	static public final int NOW_SPEED = 5;

	static public final int MARKER = 6;

	static public final int CONNECT = 7;

	static public final int LINE = 8;

	static public final int ASPECT = 9;

	private final int NUM_GROUP = 10;

	private final int MAX_NUM_COLUMN = 7;

	private boolean reset;

	private String prefix;

	private String[] group_name;

	private LinkedList[] list;

	public ColorFieldDialog(Shell parent) {
		super(parent);
		group_name = new String[NUM_GROUP];
		list = new LinkedList[NUM_GROUP];
		for (int i = 0; i < NUM_GROUP; i++)
			list[i] = new LinkedList();
		prefix = "";
		reset = false;
	}

	public void setPrefix(String str) {
		prefix = str;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setGroupName(int index, String name) {
		group_name[index] = name;
	}

	public void addList(int index, String pref_name) {
		String name = pref_name + "_name";
		String color_name = Resource
				.getString(Resource.hasKey(prefix + name) ? (prefix + name)
						: name);
		int value = Resource.getPrefInt(pref_name);
		list[index].addLast(new ColorEntry(color_name, pref_name, value));
	}

	public void addList(int index, String[] name, int[] color, int num_entry) {
		if (num_entry == 0)
			num_entry = name.length;
		for (int i = 0; i < num_entry; i++)
			list[index].addLast(new ColorEntry(name[i], color, i));
	}

	public boolean updateColor(boolean print) {
		for (int i = 0; i < NUM_GROUP; i++) {
			try {
				ListIterator iter = list[i].listIterator();
				for (;;) {
					ColorEntry entry = (ColorEntry) iter.next();
					if (entry.pref_name == null) {
						entry.color_array[entry.index] = entry.color_val;
						if (print) {
							System.out.println("Array "
									+ Integer.toString(entry.index) + "=0x"
									+ Integer.toHexString(entry.color_val));
						}
					} else {
						Resource.putPrefInt(entry.pref_name, entry.color_val);
						if (print) {
							System.out.println(entry.pref_name + "=0x"
									+ Integer.toHexString(entry.color_val));
						}
					}
				}
			} catch (NoSuchElementException e) {
			}
		}
		return reset;
	}

	public void createButtonsForButtonBar(Composite parent) {
		Button reset_button = createButton(parent, IDialogConstants.NEXT_ID,
				Resource.getString("dialog_reset_color_button"), false);
		reset_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				for (int i = 0; i < NUM_GROUP; i++) {
					try {
						ListIterator it = list[i].listIterator();
						for (;;) {
							ColorEntry e = (ColorEntry) it.next();
							if (e.pref_name != null)
								e.color_val = Resource.getInt(e.pref_name);
						}
					} catch (NoSuchElementException e) {
					}
				}
				reset = true;
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setFocus();
	}

	public Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		for (int i = 0; i < NUM_GROUP; i++) {
			if (list[i] == null || list[i].size() == 0)
				continue;
			Group group = new Group(parent, SWT.NONE);
			group.setLayout(new GridLayout(Math.min(list[i].size(),
					MAX_NUM_COLUMN), true));
			group.setText(group_name[i]);
			try {
				ListIterator iter = list[i].listIterator();
				for (;;) {
					addButton(group, (ColorEntry) iter.next());
				}
			} catch (NoSuchElementException e) {
			}
		}
		return parent;
	}

	private void addButton(Group group, ColorEntry entry) {
		final Composite composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		final Label label = new Label(composite, SWT.NONE);
		label.setText(entry.name);
		final Label color = new Label(composite, SWT.BORDER);
		color.setText("    ");
		if (entry.color != null)
			entry.color.dispose();
		entry.color = ColorManager.allocateColor(entry.color_val);
		color.setBackground(entry.color);
		color.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent event) {
				String name = label.getText();
				String g_name = ((Group) color.getParent().getParent())
						.getText();
				int j = 0;
				for (; j < NUM_GROUP; j++) {
					if (group_name[j] == null)
						continue;
					if (g_name.equals(group_name[j]))
						break;
				}
				try {
					ListIterator it = list[j].listIterator();
					for (;;) {
						ColorEntry e = (ColorEntry) it.next();
						if (name.equals(e.name)) {
							e.color_val = getColor(e.color_val);
							if (e.color != null)
								e.color.dispose();
							e.color = ColorManager.allocateColor(e.color_val);
							color.setBackground(e.color);
							return;
						}
					}
				} catch (NoSuchElementException e) {
				}
			}
		});
	}

	static public int getColor(int value) {
		ColorDialog color = new ColorDialog(Moira.getShell());
		if (value >= 0) {
			color.setRGB(new RGB((value >> 16) & 0xff, (value >> 8) & 0xff,
					value & 0xff));
		}
		RGB rgb = color.open();
		return (rgb == null) ? value
				: ((rgb.red << 16) | (rgb.green << 8) | rgb.blue);
	}

	public void dispose() {
		for (int i = 0; i < NUM_GROUP; i++) {
			try {
				ListIterator iter = list[i].listIterator();
				for (;;) {
					ColorEntry entry = (ColorEntry) iter.next();
					if (entry.color != null)
						entry.color.dispose();
				}
			} catch (NoSuchElementException e) {
			}
		}
	}

	private class ColorEntry {
		public String name;

		public int[] color_array;

		public String pref_name;

		public int color_val, index;

		public Color color;

		public ColorEntry(String t_name, String p_name, int p_color) {
			name = t_name;
			pref_name = p_name;
			color_val = p_color;
			color = null;
		}

		public ColorEntry(String t_name, int[] array, int val) {
			name = t_name;
			color_array = array;
			index = val;
			color_val = array[index];
		}
	}
}