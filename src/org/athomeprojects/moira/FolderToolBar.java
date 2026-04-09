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

import java.util.LinkedList;

import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Resource;
import org.athomeprojects.swtext.CButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class FolderToolBar {
    static private final int TOOLBAR_VERSION = 1;

    static private final int ICON_SPACING = 4;

    static private final int FILE_BAR_ORDER = 0;

    static private final int EDIT_BAR_ORDER = 1;

    static private final int PREF_BAR_ORDER = 2;

    static private final int FIND_BAR_ORDER = 3;

    static private final int NUM_BAR_ORDER = 4;

    static private CTabFolder parent_folder;

    static private CoolBar folder_bar;

    static private CButton chart, horiz, aspects;

    static private Composite folder_container, place_holder;

    static private Composite[] containers = new Composite[NUM_BAR_ORDER];

    static private CoolItem[] items = new CoolItem[NUM_BAR_ORDER];

    static private boolean[] visibles = new boolean[NUM_BAR_ORDER];

    static private LinkedList find_history = new LinkedList();

    static private CButton open, save, save_as, help;

    static private Combo find_field;

    static private Object find_tab;

    static private int find_pos, max_find_history;

    static private boolean no_reset;

    static public void init(CTabFolder folder)
    {
        parent_folder = folder;
        place_holder = TabManager.getPlaceHolder();
        folder_container = new Composite(place_holder, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = ICON_SPACING;
        folder_container.setLayout(layout);
        help = new CButton(folder_container, "help_icon", Resource
                .getString("help_overview"));
        help.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        help.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doHelp();
            }
        });
        folder_bar = new CoolBar(place_holder, SWT.NONE);
        folder_bar.setBackground(folder_container.getBackground());
        containers[FILE_BAR_ORDER] = new Composite(folder_bar, SWT.NONE);
        layout = new GridLayout(6, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = ICON_SPACING;
        containers[FILE_BAR_ORDER].setLayout(layout);
        CButton blank = new CButton(containers[FILE_BAR_ORDER], "blank_icon",
                Resource.getString("file_new"));
        blank.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        blank.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doBlank();
            }
        });
        open = new CButton(containers[FILE_BAR_ORDER], "open_icon", Resource
                .getString("file_open"));
        open.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        open.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doOpen();
            }
        });
        save = new CButton(containers[FILE_BAR_ORDER], "save_icon", Resource
                .getString("file_save"));
        save.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        save.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doSave(true);
            }
        });
        save_as = new CButton(containers[FILE_BAR_ORDER], "saveas_icon",
                Resource.getString("file_save_as"));
        save_as.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        save_as.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doSave(false);
            }
        });
        CButton print = new CButton(containers[FILE_BAR_ORDER], "print_icon",
                Resource.getString("file_print"));
        print.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        print.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doPrint();
            }
        });
        CButton capture = new CButton(containers[FILE_BAR_ORDER],
                "capture_icon", Resource.getString("file_capture"));
        capture.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        capture.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doCapture();
            }
        });
        containers[EDIT_BAR_ORDER] = new Composite(folder_bar, SWT.NONE);
        layout = new GridLayout(6, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = ICON_SPACING;
        containers[EDIT_BAR_ORDER].setLayout(layout);
        CButton copy = new CButton(containers[EDIT_BAR_ORDER], "copy_icon",
                Resource.getString("edit_copy"));
        copy.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        copy.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doCopy();
            }
        });
        CButton cut = new CButton(containers[EDIT_BAR_ORDER], "cut_icon",
                Resource.getString("edit_cut"));
        cut.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        cut.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doCut();
            }
        });
        CButton paste = new CButton(containers[EDIT_BAR_ORDER], "paste_icon",
                Resource.getString("edit_paste"));
        paste.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        paste.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doPaste();
            }
        });
        CButton delete = new CButton(containers[EDIT_BAR_ORDER], "delete_icon",
                Resource.getString("edit_delete"));
        delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        delete.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doDelete();
            }
        });
        CButton undo = new CButton(containers[EDIT_BAR_ORDER], "undo_icon",
                Resource.getString("edit_undo"));
        undo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        undo.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doUndo();
            }
        });
        CButton redo = new CButton(containers[EDIT_BAR_ORDER], "redo_icon",
                Resource.getString("edit_redo"));
        redo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        redo.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doRedo();
            }
        });
        containers[PREF_BAR_ORDER] = new Composite(folder_bar, SWT.NONE);
        layout = new GridLayout(4, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = ICON_SPACING;
        containers[PREF_BAR_ORDER].setLayout(layout);
        CButton mode = new CButton(containers[PREF_BAR_ORDER], "mode_icon",
                Resource.getString("pref_chart_mode"));
        mode.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        mode.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doMode();
            }
        });
        chart = new CButton(containers[PREF_BAR_ORDER], "chart_icon", Resource
                .getString("pref_show"));
        chart.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        chart.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doShowNow(true);
                Moira.getMenu().doShowCompass(true);
                Moira.getMenu().doShowChartMode(true);
            }
        });
        horiz = new CButton(containers[PREF_BAR_ORDER], "horiz_icon", Resource
                .getString("pref_show_horiz"));
        horiz.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        horiz.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doShowHoriz(true);
            }
        });
        aspects = new CButton(containers[PREF_BAR_ORDER], "aspects_icon",
                Resource.getString("pref_show_aspects"));
        aspects.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        aspects.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Moira.getMenu().doShowAspects(true);
            }
        });
        containers[FIND_BAR_ORDER] = new Composite(folder_bar, SWT.NONE);
        layout = new GridLayout(3, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.horizontalSpacing = ICON_SPACING;
        containers[FIND_BAR_ORDER].setLayout(layout);
        find_field = new Combo(containers[FIND_BAR_ORDER], SWT.BORDER);
        setSearchField(false);
        GridData data = new GridData(GridData.VERTICAL_ALIGN_CENTER);
        data.widthHint = 120;
        find_field.setLayoutData(data);
        initFindHistory();
        find_field.addListener(SWT.DefaultSelection, new Listener() {
            public void handleEvent(Event e)
            {
                findNextEntry(true);
            }
        });
        find_field.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event)
            {
                if (event.stateMask == SWT.CONTROL && event.character == 0x01) {
                    findSelectAll(); // Ctrl-A
                } else if (event.keyCode == SWT.F5 || event.keyCode == SWT.F6) {
                    findNextEntry(event.keyCode == SWT.F5);
                }
            }
        });
        find_field.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event)
            {
                setSearchField(true);
            }

            public void focusLost(FocusEvent event)
            {
                setSearchField(false);
            }
        });
        find_field.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event)
            {
                if (no_reset)
                    return;
                resetSearch();
            }
        });
        CButton next = new CButton(containers[FIND_BAR_ORDER], "next_icon",
                Resource.getString("tip_next_button") + "    F5");
        next.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        next.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                findNextEntry(true);
            }
        });
        CButton prev = new CButton(containers[FIND_BAR_ORDER], "prev_icon",
                Resource.getString("tip_prev_button") + "    F6");
        prev.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_CENTER));
        prev.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                findNextEntry(false);
            }
        });
        resetSearch();
        updateFolderToolBar();
        FolderToolBar.loadSettings();
    }

    static private void initFindHistory()
    {
        max_find_history = Resource.getPrefInt("max_find_history");
        if (!Resource.hasPrefKey("find_history"))
            return;
        String[] history = Resource.getPrefStringArray("find_history");
        for (int i = 0; i < Math.min(max_find_history, history.length); i++) {
            find_history.addFirst(history[i]);
            find_field.add(history[i]);
        }
        find_field.setVisibleItemCount(Math.min(20, find_history.size()));
    }

    static private void addFindHistory(String field)
    {
        if (max_find_history == 0)
            return;
        no_reset = true;
        if (find_history.indexOf(field) >= 0) {
            find_history.remove(field);
            find_field.remove(field);
        }
        if (find_history.size() >= max_find_history) {
            find_field.remove((String) find_history.getLast());
            find_history.removeLast();
        }
        find_history.addFirst(field);
        find_field.add(field, 0);
        find_field.setVisibleItemCount(Math.min(20, find_history.size()));
        find_field.select(0);
        find_field.update();
        no_reset = false;
    }

    static public void updateFolderToolBar()
    {
        if (folder_bar == null)
            return;
        if (folder_container.getParent() == parent_folder) {
            parent_folder.setTopRight(null);
            folder_container.setParent(place_holder);
        }
        if (folder_bar.getParent() == folder_container)
            folder_bar.setParent(place_holder);
        visibles[FILE_BAR_ORDER] = Resource.getPrefInt("show_file") != 0;
        visibles[EDIT_BAR_ORDER] = Resource.getPrefInt("show_edit") != 0;
        visibles[PREF_BAR_ORDER] = Resource.getPrefInt("show_pref") != 0;
        visibles[FIND_BAR_ORDER] = Resource.getPrefInt("show_find") != 0;
        removeAllCoolItems();
        boolean has_visible = false;
        for (int i = 0; i < visibles.length; i++) {
            if (visibles[i]) {
                addCoolItem(i);
                has_visible = true;
            }
        }
        if (has_visible) {
            if (visibles[FIND_BAR_ORDER]) {
                if (!TabManager.tabOnTop(TabManager.TABLE_TAB_ORDER)) {
                    DataTab tab = Moira.getChart().getTopTab();
                    if (tab != null && tab.allowFind()) {
                        String selection = tab.getSelectionText();
                        if (!selection.trim().equals("")) {
                            setSearchField(true);
                            find_field.setText(selection);
                            resetSearch();
                        }
                    }
                }
                if (!isEmptyString(null)) {
                    find_field.setFocus();
                    findSelectAll();
                }
            }
            folder_bar.setParent(folder_container);
            folder_bar.moveAbove(help);
        }
        folder_container.setParent(parent_folder);
        parent_folder.setTopRight(folder_container);
        updateFolderBarState();
    }

    static private void findSelectAll()
    {
        String str = find_field.getText();
        if (str.length() > 0)
            find_field.setSelection(new Point(0, str.length()));
    }

    static private void addCoolItem(int index)
    {
        containers[index].setParent(folder_bar);
        items[index] = new CoolItem(folder_bar, SWT.NONE);
        items[index].setControl(containers[index]);
        containers[index].pack();
        Point size = containers[index].getSize();
        items[index].setPreferredSize(items[index].computeSize(size.x, size.y));
    }

    static private void removeAllCoolItems()
    {
        for (int i = 0; i < NUM_BAR_ORDER; i++) {
            if (items[i] != null) {
                containers[i].setParent(place_holder);
                items[i].dispose();
                items[i] = null;
            }
        }
    }

    static public void updateFolderBarState()
    {
        if (folder_bar == null)
            return;
        boolean normal_mode = !ChartMode.isChartMode(ChartMode.PICK_MODE)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE);
        aspects.setEnabled(normal_mode);
        boolean chart_enable = !ChartTab.getData().getShowHoriz()
                && (normal_mode && !ChartTab.getData().getShowAspects()
                        || ChartMode.isChartMode(ChartMode.PICK_MODE) || ChartMode
                        .isChartMode(ChartMode.ASTRO_MODE)
                        && (ChartMode.isAstroMode(ChartMode.NATAL_MODE) || ChartMode
                                .hasSingleWheelMode()));
        chart.setEnabled(chart_enable);
        boolean gauquelin = !chart_enable || !ChartMode.hasSingleWheelMode();
        if (chart_enable) {
            chart
                    .setToolTipText(Resource
                            .getString(ChartMode
                                    .isChartMode(ChartMode.PICK_MODE) ? "pref_show_compass"
                                    : (ChartMode
                                            .isChartMode(ChartMode.ASTRO_MODE) ? (gauquelin ? "pref_show_gauquelin"
                                            : "pref_show_single")
                                            : "pref_show")));
        }
        MenuFolder.updateChartModeMenu(chart_enable, gauquelin);
        boolean horiz_enable = ChartMode.isAstroMode(ChartMode.NATAL_MODE);
        horiz.setEnabled(horiz_enable);
        MenuFolder.updateHorizMenu(horiz_enable);
    }

    static public void updateOpenSaveState(boolean enable)
    {
        open.setEnabled(enable);
        save.setEnabled(enable);
        save_as.setEnabled(enable);
    }

    static private void loadSettings()
    {
        if (!Resource.hasPrefKey("toolbar_order"))
            return;
        if (Resource.getPrefInt("toolbar_version") != TOOLBAR_VERSION) {
            Resource.putPrefInt("toolbar_version", TOOLBAR_VERSION);
            return;
        }
        int[] orders = Resource.getPrefIntArray("toolbar_order");
        int[] array = Resource.getPrefIntArray("toolbar_size");
        int num_item = folder_bar.getItemCount();
        if (num_item != array.length || num_item != orders.length)
            return;
        folder_bar.pack();
        Point[] sizes = folder_bar.getItemSizes();
        for (int i = 0; i < array.length; i++)
            sizes[i].x = array[i];
        folder_bar.setItemLayout(orders, null, sizes);
    }

    static public void saveSettings()
    {
        if (find_history.size() > 0) {
            Resource.putPrefStringArray("find_history", (String[]) find_history
                    .toArray(new String[1]));
        }
        if (folder_bar.getItemCount() > 0) {
            Resource
                    .putPrefIntArray("toolbar_order", folder_bar.getItemOrder());
            Point[] sizes = folder_bar.getItemSizes();
            int[] array = new int[sizes.length];
            for (int i = 0; i < array.length; i++)
                array[i] = sizes[i].x;
            Resource.putPrefIntArray("toolbar_size", array);
        }
    }

    static public void findNextEntry(boolean forward)
    {
        if (!isVisible())
            return;
        String key = find_field.getText().trim();
        if (isEmptyString(key))
            return;
        TableTab table = Moira.getTable();
        boolean success = TabManager.tabOnTop(TabManager.TABLE_TAB_ORDER) ? (table
                .isDescShown() ? findTabNextEntry(table.getDesc(), key, forward)
                : table.findTableNextEntry(key, forward))
                : findTabNextEntry(null, key, forward);
        if (success)
            addFindHistory(key);
    }

    static private boolean findTabNextEntry(DataTab tab, String key,
            boolean forward)
    {
        if (tab == null) {
            tab = Moira.getChart().getTopTab();
            if (tab == null || !tab.allowFind()) {
                TabManager.setTabOnTop(TabManager.NOTE_TAB_ORDER);
                tab = ChartTab.getTab(ChartTab.NOTE_TAB);
            }
        }
        if (tab != find_tab) {
            find_tab = tab;
            find_pos = 0;
        }
        String str = tab.getTextOnly();
        if (forward) {
            if (find_pos < str.length()) {
                int index = str.substring(find_pos).indexOf(key);
                if (index >= 0) {
                    tab.setSelection(find_pos + index, key.length());
                    find_pos += index + 1;
                    return true;
                }
            }
            if (find_pos > 0) {
                int len = Math.min(find_pos + key.length(), str.length());
                int index = str.substring(0, len).indexOf(key);
                if (index >= 0) {
                    tab.setSelection(index, key.length());
                    find_pos = index + 1;
                    return true;
                }
            }
        } else {
            if (find_pos > 0) {
                int len = Math.min(find_pos + key.length() - 1, str.length());
                int index = str.substring(0, len).lastIndexOf(key);
                if (index >= 0) {
                    tab.setSelection(index, key.length());
                    find_pos = index - 1;
                    return true;
                }
            }
            if (find_pos < str.length()) {
                int index = str.substring(find_pos).lastIndexOf(key);
                if (index >= 0) {
                    tab.setSelection(find_pos + index, key.length());
                    find_pos += index - 1;
                    return true;
                }
            }
        }
        Message.warn(Resource.getString("dialog_find_fail"));
        return false;
    }

    static public boolean isVisible()
    {
        return folder_bar != null && folder_bar.getParent() == folder_container;
    }

    static private boolean isEmptyString(String str)
    {
        if (str == null)
            str = find_field.getText().trim();
        return str.equals("")
                || str.equals(Resource.getString("find_entry_here"));
    }

    static private void setSearchField(boolean focus)
    {
        boolean empty = isEmptyString(null);
        if (focus) {
            if (empty) {
                find_field.setText("");
                find_field.setForeground(Display.getCurrent().getSystemColor(
                        SWT.COLOR_BLACK));
            }
        } else {
            if (empty) {
                find_field.setText(Resource.getString("find_entry_here"));
                find_field.setForeground(Display.getCurrent().getSystemColor(
                        SWT.COLOR_GRAY));
            }
        }
    }

    static public void resetSearch()
    {
        find_tab = null;
        find_pos = 0;
        Moira.getTable().resetSearch();
    }
}
