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
package org.athomeprojects.moira;

import org.athomeprojects.base.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class TabManager {
	static public int MAIN_FOLDER = 0;

	static public int SUB_FOLDER = 1;

	static public int WINDOW_FOLDER = 2;

	static public int NUM_FOLDER = 3;

	static public int CHART_TAB_ORDER = 0;

	static public int BIRTH_TAB_ORDER = 1;

	static public int NOW_TAB_ORDER = 2;

	static public int DATA_TAB_ORDER = 3;

	static public int POLE_TAB_ORDER = 4;

	static public int EVAL_TAB_ORDER = 5;

	static public int NOTE_TAB_ORDER = 6;

	static public int TABLE_TAB_ORDER = 7;

	static public int NUM_TAB_ORDER = 8;

	static private CTabFolder[] tab_folder;

	static private Widget[][] tab_widget;

	static private String[] tab_name;

	static private Composite place_holder;

	static private boolean in_update = false;

	static public void initTabManager() {
		tab_folder = new CTabFolder[NUM_FOLDER];
		tab_widget = new Widget[NUM_FOLDER][];
		for (int i = 0; i < NUM_FOLDER; i++)
			tab_widget[i] = new Widget[NUM_TAB_ORDER];
		tab_name = new String[NUM_TAB_ORDER];
		place_holder = new Composite(Moira.getShell(), SWT.NONE);
	}

	static public Composite getPlaceHolder() {
		return place_holder;
	}

	static public void initFolder(int folder, CTabFolder t_folder) {
		tab_folder[folder] = t_folder;
	}

	static public void setTabItem(int folder, int index, Control ctrl,
			String name, boolean on_top) {
		if (folder == WINDOW_FOLDER) {
			tab_name[index] = name + "_";
			boolean docked = Resource.getPrefInt(tab_name[index] + "docked") != 0;
			if (docked) {
				Moira.getChart().dockControl(ctrl, tab_name[index], true);
				tab_widget[folder][index] = ctrl;
			} else {
				Shell shell = new Shell(Display.getCurrent());
				shell.setLayout(new FillLayout());
				ctrl.setParent(shell);
				ctrl.setVisible(true);
				tab_widget[folder][index] = ctrl;
				Moira.setShellTitle(shell, Resource.getString(name), false,
						false);
				Moira.setShellPosition(shell, tab_name[index]);
				shell.addShellListener(new ShellAdapter() {
					public void shellClosed(ShellEvent event) {
						Shell sh = (Shell) event.getSource();
						for (int i = 0; i < NUM_TAB_ORDER; i++) {
							Control t_ctrl = (Control) tab_widget[WINDOW_FOLDER][i];
							if (t_ctrl != null && t_ctrl.getShell() == sh) {
								Moira.saveShellBounds(sh, tab_name[i]);
								tab_widget[WINDOW_FOLDER][i] = null;
								tab_name[i] = null;
								if (!Moira.shutdown()) {
									t_ctrl.setParent(place_holder);
									Moira.getChart().restoreTabPos(
											(Composite) t_ctrl, false);
								}
								break;
							}
						}
					}
				});
				shell.open();
				shell.setActive();
			}
			return;
		}
		CTabItem item;
		CTabItem top_item = null;
		if (folder == MAIN_FOLDER) {
			item = tab_folder[MAIN_FOLDER].getSelection();
			if (item != null)
				top_item = item;
		}
		int top_index = -1;
		in_update = true;
		for (int i = index + 1; i < NUM_TAB_ORDER; i++) {
			item = (CTabItem) tab_widget[folder][i];
			if (item == null)
				continue;
			if (folder == MAIN_FOLDER && top_item == item)
				top_index = i;
			tab_widget[folder][i] = item.getControl();
			tab_name[i] = item.getText();
			item.dispose();
		}
		item = initTabItem(folder, Resource.getString(name));
		if (ctrl.getParent() != tab_folder[folder])
			ctrl.setParent(tab_folder[folder]);
		item.setControl(ctrl);
		tab_widget[folder][index] = item;
		tab_name[index] = null;
		for (int i = index + 1; i < NUM_TAB_ORDER; i++) {
			ctrl = (Control) tab_widget[folder][i];
			if (ctrl == null)
				continue;
			item = initTabItem(folder, tab_name[i]);
			item.setControl(ctrl);
			tab_widget[folder][i] = item;
			tab_name[i] = null;
		}
		in_update = false;
		if (!on_top && top_index >= 0) {
			on_top = true;
			index = top_index;
		}
		if (on_top) {
			tab_folder[folder].setSelection(tab_folder[folder]
					.indexOf((CTabItem) tab_widget[folder][index]));
			if (folder == SUB_FOLDER)
				tab_folder[MAIN_FOLDER].setSelection(0);
		}
	}

	static public CTabFolder initTabFolder(Composite parent, int height) {
		CTabFolder folder = new CTabFolder(parent, SWT.NONE);
		folder.setSimple(false);
		folder.setTabHeight((height != 0) ? height : 20);
		return folder;
	}

	static private CTabItem initTabItem(int folder, String name) {
		CTabItem item = new CTabItem(tab_folder[folder], SWT.NONE);
		item.setText(" " + name.trim() + " ");
		return item;
	}

	static public void removeTabItem(int index) {
		for (int i = 0; i < NUM_FOLDER; i++) {
			if (tab_widget[i][index] == null)
				continue;
			if (i == WINDOW_FOLDER) {
				Control ctrl = (Control) tab_widget[i][index];
				Shell shell = ctrl.getShell();
				boolean docked = ctrl.getShell() == Moira.getShell();
				if (docked) {
					Moira.getChart().dockControl(ctrl, tab_name[index], false);
					tab_widget[i][index] = null;
					tab_name[index] = null;
				} else {
					shell.close();
					// tab_widget and tab_name will be cleared in shellClosed
					// event
				}
			} else {
				tab_widget[i][index].dispose();
				tab_widget[i][index] = null;
				tab_name[index] = null;
			}
		}
	}

	static public boolean hasTabItem(int index) {
		for (int i = 0; i < NUM_FOLDER; i++) {
			if (tab_widget[i][index] != null)
				return true;
		}
		return false;
	}

	static public CTabItem getTabItem(int folder, int index) {
		return (in_update || folder == WINDOW_FOLDER) ? null
				: (CTabItem) tab_widget[folder][index];
	}

	static public boolean tabOnTop(int index) {
		boolean on_top = false;
		for (int i = 0; i < WINDOW_FOLDER; i++) {
			CTabItem item = (CTabItem) tab_widget[i][index];
			if (item == null)
				continue;
			on_top = tab_folder[i].getSelection() == item;
			if (i != MAIN_FOLDER
					&& tab_folder[MAIN_FOLDER].getSelectionIndex() != 0)
				on_top = false;
			break;
		}
		return on_top;
	}

	static public boolean tabInFolder(int folder, int index) {
		return tab_widget[folder][index] != null;
	}

	static public void setTabOnTop(int index) {
		if (tabOnTop(index))
			return;
		CTabItem item = (CTabItem) tab_widget[MAIN_FOLDER][index];
		if (item == null) {
			item = (CTabItem) tab_widget[SUB_FOLDER][index];
			if (item != null) {
				if (!tabOnTop(CHART_TAB_ORDER))
					setTabOnTop(CHART_TAB_ORDER);
				tab_folder[SUB_FOLDER].setSelection(item);
			}
			return;
		}
		tab_folder[MAIN_FOLDER].setSelection(item);
	}

	static public void dispose() {
		for (int i = 0; i < NUM_TAB_ORDER; i++) {
			if (tab_widget[WINDOW_FOLDER][i] == null)
				continue;
			Control ctrl = (Control) tab_widget[WINDOW_FOLDER][i];
			Shell shell = ctrl.getShell();
			if (ctrl.getShell() != Moira.getShell())
				shell.close();
		}
	}
}
