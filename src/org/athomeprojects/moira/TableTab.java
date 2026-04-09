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

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.City;
import org.athomeprojects.base.DataEntry;
import org.athomeprojects.base.DataSet;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Resource;
import org.athomeprojects.base.RuleEntry;
import org.athomeprojects.swtext.ColorManager;
import org.athomeprojects.swtext.FontMap;
import org.athomeprojects.swtext.LocationSpinner;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class TableTab {
    private final int INIT_ROW_SIZE = 100;

    private final int SHORT_DESC_LENGTH = 30;

    private final int TEXT_PADDING = 15;

    private final int CHECK_WIDTH = 20;

    private final int SELECT_WIDTH = 20;

    private final int REMOVE_WARN = 5;

    private final int CHECK = 0;

    private final int SELECT = 1;

    private final int NAME = 2;

    private final int SEX = 3;

    private final int DATE = 4;

    private final int BIRTHDAY = 4;

    private final int PLACE = 5;

    private final int BIRTHPLACE = 5;

    private final int MOUNTAIN = 6;

    private final int DATA_NOTE = 6;

    private final int DAYSET = 7;

    private final int PICK_NOTE = 8;

    private int date_index, place_index, note_index;

    private GC gc;

    private Table table;

    private TableEditor field_editor;

    private TableEditor[] check_editor, select_editor;

    private Text text_field, place_field;

    private Button place_button;

    private Label label_field;

    private Font font;

    private Color bg_color, odd_row_bg_color, hilite_bg_color;

    private Group group;

    private Composite container, bottom_container, relationship,
            desc_container, place_container;

    private Combo chart_type;

    private DataTab desc;

    private Text footer, label;

    private Button open, add, save, save_as, show, hide;

    private String group_name, male, female, day_choice, night_choice;

    private boolean name_up, place_up, birthday_up, has_both_set, need_save;

    private String[][] column_label;

    private int[][] column_align;

    private TableColumn[] column;

    private Entry[] row;

    private DataEntry[][] row_data;

    private DataEntry[] selected_data;

    private int[] num_row;

    private int type, update_depth, top_index, edit_row, edit_col, find_row,
            num_visible_row;

    private String edit_text;

    public Composite createTabFolderPage(CTabFolder tab_folder)
    {
        initFieldIndex(false);
        row = new Entry[INIT_ROW_SIZE];
        row_data = new DataEntry[DataSet.MAX_TYPE][];
        for (int iter = 0; iter < DataSet.MAX_TYPE; iter++)
            row_data[iter] = new DataEntry[INIT_ROW_SIZE];
        selected_data = new DataEntry[DataSet.MAX_TYPE];
        num_row = new int[DataSet.MAX_TYPE];
        Composite table_container = new Composite(tab_folder, SWT.NONE);
        table_container.setLayout(new FillLayout());
        group = new Group(table_container, SWT.NONE);
        group_name = "";
        group.setLayout(new FillLayout());
        container = new Composite(group, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event)
            {
                if (!table.isEnabled()) {
                    edit_row = edit_col = -1;
                    table.setRedraw(false);
                    if (top_index >= 0 && top_index < table.getItemCount()) {
                        showEntry(top_index, false);
                        top_index = -1;
                    }
                    hideField();
                    table.setEnabled(true);
                    setColumnSize();
                    setButtonEditors(false);
                    boolean visible = num_visible_row > num_row[type];
                    if (table.getLinesVisible() != visible)
                        table.setLinesVisible(visible);
                    table.update();
                    table.setRedraw(true);
                }
                container.layout();
            }
        });
        table = new Table(container, SWT.MULTI | SWT.VIRTUAL | SWT.BORDER
                | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                | GridData.FILL_VERTICAL));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setEnabled(false);
        Moira.addFocusListener(table);
        table.addListener(SWT.SetData, new Listener() {
            public void handleEvent(Event event)
            {
                TableItem item = (TableItem) event.item;
                int index = table.indexOf(item);
                Entry entry = row[index];
                if (entry == null || entry.item != null)
                    return;
                entry.initEntry(item, (index % 2) == 1);
                setColumnSize();
            }
        });
        table.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent event)
            {
                setButtonEditors(true);
                update();
            }
        });
        place_container = new Composite(table, SWT.NONE);
        GridLayout grid_layout = new GridLayout(2, false);
        grid_layout.marginWidth = grid_layout.marginHeight = 0;
        place_container.setLayout(grid_layout);
        place_button = new Button(place_container, SWT.ARROW | SWT.DOWN);
        place_button.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        place_button.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                if (!inPlaceContainer())
                    place_container.setVisible(false);
            }
        });
        place_button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (edit_row < 0 || edit_row >= num_row[type])
                    return;
                edit_text = place_field.getText();
                LocationSpinner loc = Moira.getChart().getSpinner();
                String country = loc.getCountryName();
                String city = loc.getCityName();
                String zone = loc.getZoneName();
                DataEntry entry = row_data[type][edit_row];
                loc.setCountryName(entry.getCountry());
                loc.setCityName(entry.getCity());
                loc.setZoneName(entry.getZone());
                (new LocationDialog(Moira.getShell())).open();
                String new_country = loc.getCountryName();
                String new_city = loc.getCityName();
                String new_zone = loc.getZoneName();
                String new_place = new_city + ", " + new_country;
                place_field.setText(new_place);
                if (!new_place.equals(edit_text)) {
                    entry.setCountry(new_country);
                    entry.setCity(new_city);
                    entry.setZone(new_zone);
                    need_save = true;
                }
                loc.setCountryName(country);
                loc.setCityName(city);
                loc.setZoneName(zone);
            }
        });
        place_field = new Text(place_container, SWT.LEFT);
        place_field.setLayoutData(new GridData(GridData.FILL_BOTH));
        place_field.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event)
            {
                if (edit_row >= 0 && edit_row < num_row[type] && edit_col >= 0
                        && edit_col < table.getColumnCount()) {
                    field_editor.getItem().setText(edit_col,
                            place_field.getText());
                    row[edit_row].updateEntry();
                }
            }
        });
        place_field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                if (!inPlaceContainer()) {
                    place_container.setVisible(false);
                    String new_place = place_field.getText();
                    if (!new_place.equals(edit_text))
                        need_save = true;
                }
            }
        });
        place_field.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent event)
            {
                switch (event.detail) {
                    case SWT.TRAVERSE_TAB_NEXT:
                    case SWT.TRAVERSE_TAB_PREVIOUS:
                        hideField();
                        setEditField((event.detail == SWT.TRAVERSE_TAB_NEXT) ? 1
                                : -1);
                        event.doit = false;
                        break;
                }
            }
        });
        text_field = new Text(table, SWT.LEFT);
        text_field.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event)
            {
                if (edit_row >= 0 && edit_row < num_row[type] && edit_col >= 0
                        && edit_col < table.getColumnCount()) {
                    field_editor.getItem().setText(edit_col,
                            text_field.getText());
                    row[edit_row].updateEntry();
                }
            }
        });
        text_field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                text_field.setVisible(false);
                String new_text = text_field.getText();
                if (!new_text.equals(edit_text))
                    need_save = true;
            }
        });
        text_field.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent event)
            {
                switch (event.detail) {
                    case SWT.TRAVERSE_TAB_NEXT:
                    case SWT.TRAVERSE_TAB_PREVIOUS:
                        hideField();
                        setEditField((event.detail == SWT.TRAVERSE_TAB_NEXT) ? 1
                                : -1);
                        event.doit = false;
                        break;
                }
            }
        });
        label_field = new Label(table, SWT.LEFT);
        label_field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                label_field.setVisible(false);
            }
        });
        label_field.addMouseTrackListener(new MouseTrackAdapter() {
            public void mouseExit(MouseEvent event)
            {
                label_field.setVisible(false);
            }
        });
        desc = new DataTab();
        desc_container = desc.createDataPage(TabManager.getPlaceHolder(), "",
                "table", false, true, false, false, true);
        field_editor = new TableEditor(table);
        field_editor.grabHorizontal = true;
        field_editor.horizontalAlignment = SWT.LEFT;
        table.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);
                if (item == null)
                    return;
                edit_row = -1;
                for (edit_col = NAME; edit_col < note_index; edit_col++) {
                    Rectangle rect = item.getBounds(edit_col);
                    if (rect.contains(pt)) {
                        edit_row = table.indexOf(item);
                        setEditField(0);
                        return;
                    }
                }
                table.deselectAll();
            }
        });
        table.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent event)
            {
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);
                if (item == null)
                    return;
                Rectangle rect = item.getBounds(note_index);
                if (!rect.contains(pt))
                    return;
                place_container.setVisible(false);
                text_field.setVisible(false);
                String s = item.getText(note_index);
                if (s.equals(""))
                    return;
                edit_row = -1;
                int width = column[note_index].getWidth();
                if (gc.textExtent(s).x > width) {
                    for (int i = s.length() - 1; i >= 0; i--) {
                        String str = s.substring(0, i) + "...";
                        if (gc.textExtent(str).x <= width) {
                            s = str;
                            break;
                        }
                    }
                }
                label_field.setText(s);
                field_editor.setEditor(label_field, item, note_index);
                label_field.setBackground(item.getBackground());
                Entry entry = row[table.indexOf(item)];
                label_field.setToolTipText(entry.entry.getNote(false));
                label_field.setVisible(true);
                label_field.setFocus();
            }
        });
        table.getVerticalBar().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                setButtonEditors(false);
            }
        });
        column_label = new String[DataSet.MAX_TYPE][];
        column_label[DataSet.DATA] = Resource
                .getStringArray("table_column_label");
        column_label[DataSet.PICK] = Resource
                .getStringArray("pick_table_column_label");
        column_align = new int[DataSet.MAX_TYPE][];
        column_align[DataSet.DATA] = Resource.getIntArray("table_column_align");
        column_align[DataSet.PICK] = Resource
                .getIntArray("pick_table_column_align");
        column = null;
        setColumn();
        gc = new GC(table);
        bottom_container = new Composite(container, SWT.NO_FOCUS);
        bottom_container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bottom_container.setLayout(new GridLayout(12, false));
        open = new Button(bottom_container, SWT.PUSH);
        open.setText(Resource.getString("open_button"));
        open.setToolTipText(Resource.getString("tip_open_button"));
        Moira.addFocusListener(open);
        open.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                openFile();
            }
        });
        add = new Button(bottom_container, SWT.PUSH);
        add.setText(Resource.getString("add_button"));
        add.setToolTipText(Resource.getString("tip_add_button"));
        Moira.addFocusListener(add);
        add.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                openFile(true, false, true, null);
            }
        });
        save = new Button(bottom_container, SWT.PUSH);
        save.setText(Resource.getString("save_button"));
        save.setToolTipText(Resource.getString("tip_save_button"));
        Moira.addFocusListener(save);
        save.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                saveFile(null, true);
            }
        });
        save_as = new Button(bottom_container, SWT.PUSH);
        save_as.setText(Resource.getString("save_as_button"));
        save_as.setToolTipText(Resource.getString("tip_save_as_button"));
        Moira.addFocusListener(save_as);
        save_as.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                saveFile(null, false);
            }
        });
        Button up = new Button(bottom_container, SWT.ARROW | SWT.UP);
        up.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        up.setToolTipText(Resource.getString("tip_up_button"));
        Moira.addFocusListener(up);
        up.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                moveSelection(type, true, false, true);
            }
        });
        Button down = new Button(bottom_container, SWT.ARROW | SWT.DOWN);
        down.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        down.setToolTipText(Resource.getString("tip_down_button"));
        Moira.addFocusListener(down);
        down.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                moveSelection(type, false, false, true);
            }
        });
        Button blank = new Button(bottom_container, SWT.PUSH);
        blank.setText(Resource.getString("new_button"));
        blank.setToolTipText(Resource.getString("tip_new_button"));
        Moira.addFocusListener(blank);
        blank.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                newEntry();
            }
        });
        Button remove = new Button(bottom_container, SWT.PUSH);
        remove.setText(Resource.getString("remove_button"));
        remove.setToolTipText(Resource.getString("tip_remove_button"));
        Moira.addFocusListener(remove);
        remove.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                removeSelection(true);
            }
        });
        Button update = new Button(bottom_container, SWT.PUSH);
        update.setText(Resource.getString("update_button"));
        update.setToolTipText(Resource.getString("tip_update_button"));
        Moira.addFocusListener(update);
        update.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                updateChart(true);
                Moira.getChart().resetCities();
            }
        });
        relationship = new Composite(TabManager.getPlaceHolder(), SWT.NONE);
        relationship.setLayout(new GridLayout(2, false));
        chart_type = new Combo(relationship, SWT.DROP_DOWN | SWT.READ_ONLY);
        String[] relationship_name = Resource
                .getStringArray("relationship_chart_name");
        for (int i = 0; i < relationship_name.length; i++)
            chart_type.add(relationship_name[i]);
        chart_type.select(Resource.getPrefInt("relationship_type"));
        chart_type.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Resource.putPrefInt("relationship_type", chart_type
                        .getSelectionIndex());
            }
        });
        Button relationship_chart = new Button(relationship, SWT.PUSH);
        relationship_chart.setText(Resource.getString("relationship_chart"));
        relationship_chart.setToolTipText(Resource
                .getString("tip_relationship_chart_button"));
        Moira.addFocusListener(relationship_chart);
        relationship_chart.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                int index = -1;
                for (int i = 0; i < num_row[type]; i++) {
                    if (row_data[type][i].getSelected()) {
                        if (index >= 0) {
                            index = -1;
                            break;
                        }
                        index = i;
                    }
                }
                if (index < 0 || selected_data[type] == null
                        || selected_data[type] == row_data[type][index]) {
                    Message.info(Resource
                            .getString("dialog_no_relationship_selection"));
                    return;
                }
                if (Moira.needUpdate()) {
                    Moira.update(false, true);
                    return;
                }
                setMultiMode(index);
            }
        });
        footer = new Text(bottom_container, SWT.CENTER | SWT.BORDER);
        footer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                | GridData.VERTICAL_ALIGN_CENTER | GridData.GRAB_VERTICAL));
        footer.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent focusEvent)
            {
                showDesc(true);
            }
        });
        show = new Button(bottom_container, SWT.ARROW | SWT.UP);
        show.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        show.setToolTipText(Resource.getString("tip_show_button"));
        Moira.addFocusListener(show);
        show.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                showDesc(true);
            }
        });
        hide = new Button(TabManager.getPlaceHolder(), SWT.ARROW | SWT.DOWN);
        hide.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        hide.setToolTipText(Resource.getString("tip_hide_button"));
        Moira.addFocusListener(hide);
        hide.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                showDesc(false);
            }
        });
        label = new Text(bottom_container, SWT.RIGHT);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER
                | GridData.GRAB_VERTICAL));
        label.setEditable(false);
        setFont();
        name_up = place_up = birthday_up = need_save = false;
        has_both_set = true;
        update_depth = 0;
        top_index = edit_row = edit_col = -1;
        male = Resource.getString("male");
        female = Resource.getString("female");
        day_choice = Resource.getString("day_choice");
        night_choice = Resource.getString("night_choice");
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            addChartButton();
        resetSearch();
        update();
        return table_container;
    }

    private void setEditField(int inc)
    {
        if (edit_row < 0 || edit_col < 0)
            return;
        TableItem item = table.getItem(edit_row);
        if (inc != 0) {
            edit_col += inc;
            if (edit_col >= note_index)
                edit_col = NAME;
            else if (edit_col < NAME)
                edit_col = note_index - 1;
            Rectangle rect = item.getBounds(edit_col);
            Display.getCurrent().setCursorLocation(
                    table.toDisplay(rect.x + rect.width / 2, rect.y
                            + rect.height / 2));
        }
        label_field.setVisible(false);
        edit_text = item.getText(edit_col);
        Color color = item.getBackground();
        Text field;
        if (edit_col == place_index) {
            field = place_field;
            place_container.setBackground(color);
            place_field.setBackground(color);
            place_button.setBackground(color);
            field_editor.setEditor(place_container, item, edit_col);
            place_container.setVisible(true);
        } else {
            field = text_field;
            field_editor.setEditor(text_field, item, edit_col);
            text_field.setVisible(true);
        }
        field.setText(edit_text);
        field.setEditable(true);
        field.selectAll();
        field.setBackground(color);
        field.setFocus();
    }

    public boolean setMultiMode(int index)
    {
        if (index < 0 && index >= row_data[type].length)
            return false;
        switch (Resource.getPrefInt("relationship_type")) {
            case 0:
                MenuFolder.setAstroMode(ChartMode.RELATIONSHIP_MODE);
                break;
            case 1:
                MenuFolder.setAstroMode(ChartMode.COMPOSITE_MODE);
                break;
            default:
                MenuFolder.setAstroMode(ChartMode.COMPARISON_MODE);
                break;
        }
        Moira.getChart().setMultiMode(row_data[type][index]);
        return true;
    }

    public void openFile()
    {
        ChartTab.hideTip();
        if (!checkForSave())
            return;
        int index = openFile(false, true, true, null);
        if (index == Integer.MIN_VALUE)
            return;
        if (Resource.prefChanged())
            Moira.updateModEval();
        boolean table_on_top = TabManager.tabOnTop(TabManager.TABLE_TAB_ORDER);
        if (index >= 0) {
            updateData(index, table_on_top);
            String command = Resource.getAlternateCommand();
            if (command != null) {
                Moira.flushEvents(false);
                MenuFolder.processCommand(command, true);
                Moira.getChart().updateAttribute();
            }
        } else if (!table_on_top) {
            Moira.update(false, false);
        }
    }

    public boolean isDescShown()
    {
        return show.getParent() == TabManager.getPlaceHolder();
    }

    public DataTab getDesc()
    {
        return desc;
    }

    private void showDesc(boolean set)
    {
        if (set) {
            if (show.getParent() == TabManager.getPlaceHolder()) {
                Moira.setFocus(desc_container);
                return;
            }
            show.setParent(TabManager.getPlaceHolder());
            table.setParent(TabManager.getPlaceHolder());
            hide.setParent(bottom_container);
            desc_container.setParent(container);
            desc_container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
                    | GridData.FILL_VERTICAL));
            hide.moveAbove(label);
            desc_container.moveAbove(bottom_container);
        } else {
            if (hide.getParent() == TabManager.getPlaceHolder())
                return;
            hide.setParent(TabManager.getPlaceHolder());
            desc_container.setParent(TabManager.getPlaceHolder());
            show.setParent(bottom_container);
            table.setParent(container);
            show.moveAbove(label);
            table.moveAbove(bottom_container);
        }
        String str = desc.getTextOnly().trim();
        footer.setText(DataEntry.getOneLineDesc(str, SHORT_DESC_LENGTH, true));
        bottom_container.layout();
        container.layout();
        bottom_container.update();
        container.update();
    }

    public boolean findTableNextEntry(String key, boolean forward)
    {
        hideField();
        showDesc(false);
        hiliteEntry(false);
        if (num_row[type] <= 0)
            return false;
        if (find_row < 0) {
            find_row = table.getTopIndex();
            if (!forward) {
                find_row += num_visible_row - 1;
                find_row = Math.min(find_row, num_row[type] - 1);
            }
        } else {
            if (forward)
                find_row++;
            else
                find_row--;
        }
        if (forward) {
            for (int i = find_row; i < num_row[type]; i++) {
                int index = matchEntry(row_data[type][i], key);
                if (index >= 0) {
                    find_row = i;
                    hiliteEntry(true);
                    return true;
                }
            }
            for (int i = 0; i < find_row; i++) {
                int index = matchEntry(row_data[type][i], key);
                if (index >= 0) {
                    find_row = i;
                    hiliteEntry(true);
                    return true;
                }
            }
        } else {
            for (int i = find_row; i >= 0; i--) {
                int index = matchEntry(row_data[type][i], key);
                if (index >= 0) {
                    find_row = i;
                    hiliteEntry(true);
                    return true;
                }
            }
            for (int i = num_row[type] - 1; i > find_row; i--) {
                int index = matchEntry(row_data[type][i], key);
                if (index >= 0) {
                    find_row = i;
                    hiliteEntry(true);
                    return true;
                }
            }
        }
        Message.warn(Resource.getString("dialog_find_fail"));
        return false;
    }

    public void resetSearch()
    {
        if (num_row != null)
            hiliteEntry(false);
        find_row = -1;
    }

    private void hiliteEntry(boolean hilite)
    {
        if (find_row < 0 || find_row >= num_row[type])
            return;
        if (hilite) {
            showEntry(find_row, true);
            TableItem item = table.getItem(find_row);
            item.setBackground(hilite_bg_color);
        } else {
            TableItem item = table.getItem(find_row);
            item.setBackground(((find_row % 2) == 1) ? odd_row_bg_color
                    : bg_color);
        }
        setButtonEditors(false);
    }

    private void showEntry(int index, boolean update)
    {
        int s = table.getTopIndex();
        int e = Math.min(num_row[type], s + num_visible_row);
        if (index < s || index >= e - 2) {
            table.setRedraw(false);
            table.setSelection(index);
            table.showSelection();
            table.deselectAll();
            table.setRedraw(true);
            if (update)
                update();
        }
    }

    private int getNumVisibleRow()
    {
        int height = table.getClientArea().height - table.getHeaderHeight();
        if (height <= 0)
            return 0;
        int item_height = table.getItemHeight();
        int n_row = height / item_height;
        if (n_row * item_height < height)
            n_row++;
        return n_row;
    }

    private int matchEntry(DataEntry entry, String key)
    {
        int n = entry.getName().indexOf(key);
        if (n >= 0)
            return NAME;
        String str = entry.getCity() + ", " + entry.getCountry();
        n = str.indexOf(key);
        if (n >= 0)
            return date_index;
        str = BaseCalendar.formatDate(entry.getBirthDay(), false, false);
        n = str.indexOf(key);
        if (n >= 0)
            return place_index;
        n = str.indexOf(key);
        if (n >= 0)
            return place_index;
        str = entry.getNote(true);
        if (str != null) {
            n = str.indexOf(key);
            if (n >= 0)
                return note_index;
        }
        return -1;
    }

    public void setMode()
    {
        if (!initFieldIndex(true))
            return;
        setColumn();
        int index = getSelectedIndex();
        if (num_row[type] > 0 && index < 0)
            index = 0;
        if (index >= 0)
            updateData(index, true);
        else
            newEntry();
        update();
    }

    public void updateButtonState(boolean enable)
    {
        open.setEnabled(enable);
        add.setEnabled(enable);
        save.setEnabled(enable);
        save_as.setEnabled(enable);
    }

    public void addChartButton()
    {
        if (relationship.getParent() == bottom_container)
            return;
        relationship.setParent(bottom_container);
        relationship.moveAbove(footer);
        GridLayout layout = (GridLayout) bottom_container.getLayout();
        layout.numColumns++;
        bottom_container.layout();
        bottom_container.update();
    }

    public void removeChartButton()
    {
        if (relationship.getParent() == TabManager.getPlaceHolder())
            return;
        relationship.setParent(TabManager.getPlaceHolder());
        GridLayout layout = (GridLayout) bottom_container.getLayout();
        layout.numColumns--;
        bottom_container.layout();
        bottom_container.update();
    }

    private void hideField()
    {
        place_container.setVisible(false);
        text_field.setVisible(false);
        label_field.setVisible(false);
    }

    private boolean inPlaceContainer()
    {
        Point pt = Display.getCurrent().getCursorLocation();
        pt = place_container.toControl(pt);
        Rectangle rect = place_container.getClientArea();
        return rect.contains(pt);
    }

    private boolean initFieldIndex(boolean check)
    {
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            if (check && type == DataSet.PICK)
                return false;
            type = DataSet.PICK;
            date_index = DATE;
            place_index = PLACE;
            note_index = PICK_NOTE;
        } else {
            if (check && type == DataSet.DATA)
                return false;
            type = DataSet.DATA;
            date_index = BIRTHDAY;
            place_index = BIRTHPLACE;
            note_index = DATA_NOTE;
        }
        return true;
    }

    private void setColumn()
    {
        if (column != null) {
            clearRows(
                    0,
                    num_row[ChartMode.isChartMode(ChartMode.PICK_MODE) ? DataSet.DATA
                            : DataSet.PICK], false);
            for (int i = 0; i < column.length; i++)
                column[i].dispose();
        }
        column = new TableColumn[column_label[type].length];
        for (int i = 0; i < column.length; i++) {
            column[i] = new TableColumn(table,
                    (column_align[type][i] != 0) ? SWT.CENTER : SWT.LEFT);
        }
        column[CHECK].setWidth(CHECK_WIDTH);
        column[SELECT].setWidth(SELECT_WIDTH);
        column[CHECK].setResizable(false);
        column[SELECT].setResizable(false);
        for (int i = column.length - 1; i >= 0; i--) {
            column[i].setText(column_label[type][i].replaceAll("x", ""));
            table.showColumn(column[i]);
        }
        column[CHECK].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                toggleCheck();
            }
        });
        column[SELECT].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                flipRow();
            }
        });
        column[NAME].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                sortName();
            }
        });
        column[place_index].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                sortPlace();
            }
        });
        column[date_index].addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                sortDay();
            }
        });
        refresh(false);
    }

    public int openFile(boolean multi, boolean clear, boolean check_mode,
            String file)
    {
        String path;
        String[] files;
        MenuFolder.disposeSubWin();
        FolderToolBar.resetSearch();
        if (file != null) {
            File f = new File(file);
            path = f.getParent();
            files = new String[1];
            files[0] = f.getName();
        } else {
            files = Moira.getIO().openFile(multi);
            path = Moira.getIO().getFilePath();
        }
        if (files != null) {
            table.setRedraw(false);
            if (clear)
                clearTable(false);
            int[] index = loadData(path, files, clear);
            ChartMode.setChartMode();
            refresh(true);
            if (clear) {
                if (check_mode && !Resource.hasAlternatePref()) {
                    int cur_type, other_type;
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        cur_type = DataSet.PICK;
                        other_type = DataSet.DATA;
                    } else {
                        cur_type = DataSet.DATA;
                        other_type = DataSet.PICK;
                    }
                    if (num_row[cur_type] == 0 && num_row[other_type] > 0) {
                        // switch to the other mode
                        Moira
                                .getMenu()
                                .setChartMode(
                                        ChartMode
                                                .isChartMode(ChartMode.PICK_MODE) ? ChartMode.TRADITIONAL_MODE
                                                : ChartMode.PICK_MODE);
                    }
                }
                has_both_set = num_row[DataSet.DATA] > 0
                        && num_row[DataSet.PICK] > 0;
            }
            table.setRedraw(true);
            top_index = index[ChartMode.isChartMode(ChartMode.PICK_MODE) ? DataSet.PICK
                    : DataSet.DATA];
            need_save = false;
            return top_index;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    private int[] loadData(String path_name, String[] files, boolean update)
    {
        if (path_name != null && path_name.equals(""))
            path_name = null;
        int[] index = new int[DataSet.MAX_TYPE];
        for (int iter = 0; iter < DataSet.MAX_TYPE; iter++)
            index[iter] = -1;
        for (int i = 0; i < files.length; i++) {
            String path_file_name;
            if (path_name != null)
                path_file_name = path_name + File.separator + files[i];
            else
                path_file_name = files[i];
            DataSet data_set = new DataSet();
            if (!data_set.loadData(path_file_name))
                continue;
            String str = data_set.getFooter();
            if (str != null) {
                desc.setText(str);
                desc.resetUndo();
                footer.setText(DataEntry.getOneLineDesc(str, SHORT_DESC_LENGTH,
                        true));
            }
            for (int iter = 0; iter < DataSet.MAX_TYPE; iter++) {
                index[iter] = addEntry(data_set, iter, false, true);
                if (update && index[iter] >= 0)
                    selected_data[iter] = row_data[iter][index[iter]];
            }
            if (update) {
                Moira.getIO().setLastOpenPath(path_name);
                if (files.length == 1)
                    Moira.getIO().setLastOpenFile(files[i]);
                else
                    Moira.getIO().removeLastOpenFile();
            }
        }
        return index;
    }

    public void clearTable(boolean update)
    {
        clearRows(0, num_row[type], true);
        for (int iter = 0; iter < DataSet.MAX_TYPE; iter++) {
            for (int i = 0; i < num_row[iter]; i++)
                row_data[iter][i] = null;
            num_row[iter] = 0;
            selected_data[iter] = null;
        }
        desc.setText("");
        desc.resetUndo();
        footer.setText("");
        if (update)
            update();
    }

    public void addCurrentEntry(boolean update)
    {
        ChartTab tab = Moira.getChart();
        DataSet data_set = new DataSet();
        data_set.setMaxDataEntry(1, type);
        DataEntry entry = data_set.getDataEntry(0, type);
        int[] date = new int[5];
        LocationSpinner loc = tab.getSpinner();
        String name = tab.getName();
        entry.setName(name);
        entry.setSex(tab.getSex());
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            entry.setChoice(tab.getDaySet());
            entry.setMountainPos(City.formatMapPos(City.parseMapPos(tab
                    .getMountainPos()), true));
        } else {
            tab.getNowDate(date);
            entry.setNowDay(date);
        }
        entry.setCountry(loc.getCountryName());
        entry.setCity(loc.getCityName());
        entry.setZone(loc.getZoneName());
        tab.getBirthDate(date);
        entry.setBirthDay(date);
        entry.setOverride(ChartTab.getData().getOverrideString());
        String str = ChartTab.getTab(ChartTab.NOTE_TAB).getNote(false);
        if (ChartTab.getTab(ChartTab.NOTE_TAB).hasValidNote(str))
            entry.setNote(str);
        int index = addEntry(data_set, type, true, update);
        if (index >= 0) {
            selected_data[type] = row_data[type][index];
            row[index].setData();
        }
        update();
        top_index = index;
    }

    public void updateNote(String str)
    {
        if (selected_data[type] == null)
            return;
        String name = Moira.getChart().getName();
        if (name == null || !name.equals(selected_data[type].getName()))
            return;
        if (selected_data[type].sameNote(str))
            return;
        need_save = true;
        selected_data[type].setNote(str);
        int index = getSelectedIndex();
        if (index >= 0)
            row[index].setData();
        update();
    }

    public void updateData(int index, boolean delay_update)
    {
        selected_data[type] = row_data[type][index];
        updateData(selected_data[type], delay_update);
    }

    private void updateData(DataEntry entry, boolean delay_update)
    {
        Moira.getChart().removeAstroControl();
        MenuFolder.setAstroMode(ChartMode.NATAL_MODE);
        ChartTab tab = Moira.getChart();
        LocationSpinner loc = tab.getSpinner();
        if (delay_update)
            tab.clearCacheRecord(false, true);
        tab.setName(entry.getName());
        tab.setSex(entry.getSex());
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            tab.setDaySet(entry.getChoice());
            tab.setMountainPos(entry.getMountainPos());
        } else {
            tab.setNowDate(entry.getNowDay());
        }
        tab.setBirthDate(entry.getBirthDay());
        loc.setCountryName(entry.getCountry());
        loc.setCityName(entry.getCity());
        loc.setZoneName(entry.getZone());
        ChartTab.getData().setOverrideString(entry.getOverride());
        ChartTab.getTab(ChartTab.NOTE_TAB).setNote(entry.getNote(true));
        Moira.update(delay_update, true);
    }

    public void saveFile(String file_name, boolean last)
    {
        ChartTab.getTab(ChartTab.NOTE_TAB).saveNote();
        boolean direct = file_name != null;
        if (!direct) {
            file_name = Moira.getIO().saveFile(last);
            if (file_name == null || file_name.equals("none")) {
                if (last)
                    saveFile(null, false);
                return;
            }
        }
        boolean single_entry = !Moira.isTableVisible();
        if (single_entry && selected_data[type] == null)
            return;
        DataSet data_set = new DataSet();
        if (single_entry) {
            data_set.setMaxDataEntry(1, type);
            data_set.setDataEntry(0, selected_data[type], type);
        } else {
            String str = desc.getText();
            if (!str.equals(""))
                data_set.setFooter(str);
            for (int iter = 0; iter < DataSet.MAX_TYPE; iter++) {
                if (num_row[iter] <= 0)
                    continue;
                int count = num_row[iter];
                if (count <= 0)
                    continue;
                data_set.setMaxDataEntry(count, iter);
                for (int i = 0; i < num_row[iter]; i++) {
                    data_set.setDataEntry(i, row_data[iter][i], iter);
                    if (count > 1 && selected_data[iter] == row_data[iter][i])
                        data_set.setLastIndex(i, iter);
                }
            }
            if (!direct && !(has_both_set && Moira.getIO().saveToLast())
                    && data_set.getMaxDataEntry(DataSet.DATA) > 0
                    && data_set.getMaxDataEntry(DataSet.PICK) > 0) {
                DataSetDialog dialog = new DataSetDialog(Moira.getShell());
                boolean save_data = false, save_pick = false;
                if (dialog.open() == Window.OK) {
                    save_data = dialog.saveData();
                    save_pick = dialog.savePick();
                }
                dialog.close();
                if (!save_data && !save_pick)
                    return;
                if (!save_data)
                    data_set.setMaxDataEntry(0, DataSet.DATA);
                if (!save_pick)
                    data_set.setMaxDataEntry(0, DataSet.PICK);
            }
        }
        if (direct) {
            data_set.saveData(file_name);
        } else {
            if (Resource.getPrefInt("backup") != 0) {
                if (!Moira.getIO().moveFile(file_name,
                        Moira.getIO().getFilePath(),
                        Resource.getPrefString("backup_dir"))) {
                    Message.warn(Resource.getString("dialog_backup_same"));
                    return;
                }
            }
            data_set.saveData(Moira.getIO().getFilePath() + File.separator
                    + file_name);
            Moira.getIO().setLastOpenPath(null);
            Moira.getIO().setLastOpenFile(file_name);
        }
        need_save = false;
    }

    public boolean checkForSave()
    {
        if (!need_save || !save.getEnabled()
                || Resource.getPrefInt("no_confirm_save") != 0)
            return true;
        ConfirmSaveDialog dialog = new ConfirmSaveDialog(Moira.getShell());
        int state = -1;
        if (dialog.open() == Window.OK)
            state = dialog.updateConfirmSave();
        dialog.close();
        if (state > 0)
            saveFile(null, true);
        return state >= 0;
    }

    private void removeSelection(boolean warn)
    {
        if (num_row[type] == 0)
            return;
        hiliteEntry(false);
        int count = 0;
        for (int i = 0; i < num_row[type]; i++) {
            if (row_data[type][i].getSelected())
                count++;
        }
        if (count == 0) {
            if (warn)
                Message.info(Resource.getString("dialog_no_selection"));
            return;
        }
        if (!warn
                || count < REMOVE_WARN
                || Message.question(Resource
                        .getString("dialog_remove_selection"))) {
            for (int i = 0; i < num_row[type]; i++) {
                if (row_data[type][i].getSelected()) {
                    if (selected_data[type] == row_data[type][i])
                        selected_data[type] = null;
                    row_data[type][i] = row[i].entry = null;
                }
            }
            int old_num_row = num_row[type];
            count = -1;
            for (int i = 0; i < num_row[type]; i++) {
                if (row_data[type][i] == null) {
                    if (count < 0)
                        count = i;
                } else if (count >= 0) {
                    row_data[type][count++] = row_data[type][i];
                }
            }
            num_row[type] = count;
            adjustSelection(old_num_row);
        }
    }

    private void adjustSelection(int old_num_row)
    {
        clearRows(num_row[type], old_num_row, true);
        refresh(true);
    }

    private void clearRows(int start, int end, boolean clear_data)
    {
        if (start >= end)
            return;
        for (int i = start; i < end; i++) {
            if (clear_data)
                row_data[type][i] = null;
            if (row[i] != null) {
                row[i].dispose();
                row[i] = null;
            }
        }
        table.remove(start, end - 1);
    }

    private void moveSelection(int iter, boolean up, boolean all, boolean warn)
    {
        if (num_row[iter] == 0)
            return;
        hiliteEntry(false);
        int shift = up ? -1 : 1;
        int first = row_data[iter][0].getName().equals("") ? 1 : 0;
        int s = up ? first : (num_row[iter] - 1);
        int e = up ? num_row[iter] : (first - 1);
        boolean shifted = false;
        for (int i = s; i != e; i -= shift) {
            if (!all && !row_data[iter][i].getSelected())
                continue;
            int n = i + shift;
            if (n < first || n >= num_row[iter])
                continue;
            DataEntry entry = row_data[iter][i];
            row_data[iter][i] = row_data[iter][n];
            row_data[iter][n] = entry;
            shifted = true;
        }
        if (shifted && iter == type)
            refresh(true);
        else if (warn)
            Message.info(Resource.getString("dialog_no_selection"));
    }

    private void flipRow()
    {
        if (num_row[type] < 2)
            return;
        hiliteEntry(false);
        int mid = num_row[type] / 2;
        for (int i = 0; i < mid; i++) {
            int j = num_row[type] - i - 1;
            DataEntry entry = row_data[type][i];
            row_data[type][i] = row_data[type][j];
            row_data[type][j] = entry;
        }
        refresh(true);
    }

    public int getNumEntry()
    {
        return num_row[type];
    }

    public void newEntry()
    {
        ChartTab tab = Moira.getChart();
        unselect();
        MenuFolder.setAstroMode(ChartMode.NATAL_MODE);
        tab.setName(null);
        tab.setSex(true);
        ChartTab.getTab(ChartTab.NOTE_TAB).setNote(null);
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            tab.setDaySet(true);
            tab.setBirthDate(null);
            addCurrentEntry(true);
        } else {
            tab.reset();
            addCurrentEntry(true);
            tab.refresh(true);
            Moira.updateOverride();
        }
    }

    public void unselect()
    {
        int index = getSelectedIndex();
        if (index >= 0)
            selected_data[type] = null;
    }

    private void toggleCheck()
    {
        if (num_row[type] == 0)
            return;
        int count = 0;
        for (int i = 0; i < num_row[type]; i++) {
            if (row_data[type][i].getSelected())
                count++;
        }
        boolean mark = 2 * count < num_row[type];
        for (int i = 0; i < num_row[type]; i++)
            row_data[type][i].setSelected(mark);
        setButtonEditors(false);
    }

    private void sortName()
    {
        if (num_row[type] == 0)
            return;
        int first = row_data[type][0].getName().equals("") ? 1 : 0;
        Arrays.sort(row_data[type], first, num_row[type], new Comparator() {
            public int compare(Object a, Object b)
            {
                String c_a = ((DataEntry) a).getName();
                String c_b = ((DataEntry) b).getName();
                int n = c_a.compareTo(c_b);
                return name_up ? (-n) : n;
            }
        });
        name_up = !name_up;
        refresh(true);
    }

    private void sortPlace()
    {
        if (num_row[type] == 0)
            return;
        int first = row_data[type][0].getName().equals("") ? 1 : 0;
        Arrays.sort(row_data[type], first, num_row[type], new Comparator() {
            public int compare(Object a, Object b)
            {
                String c_a = ((DataEntry) a).getCountry();
                String c_b = ((DataEntry) b).getCountry();
                int n = c_a.compareTo(c_b);
                if (n != 0)
                    return place_up ? (-n) : n;
                c_a = ((DataEntry) a).getCity();
                c_b = ((DataEntry) b).getCity();
                n = c_a.compareTo(c_b);
                return place_up ? (-n) : n;
            }
        });
        place_up = !place_up;
        refresh(true);
    }

    private void sortDay()
    {
        if (num_row[type] == 0)
            return;
        int first = row_data[type][0].getName().equals("") ? 1 : 0;
        Arrays.sort(row_data[type], first, num_row[type], new Comparator() {
            public int compare(Object a, Object b)
            {
                int[] b_a = ((DataEntry) a).getBirthDayDirect();
                int[] b_b = ((DataEntry) b).getBirthDayDirect();
                for (int i = 0; i < b_a.length; i++) {
                    int n = b_a[i] - b_b[i];
                    if (n != 0)
                        return birthday_up ? (-n) : n;
                }
                return 0;
            }
        });
        birthday_up = !birthday_up;
        refresh(true);
    }

    private void refresh(boolean update)
    {
        for (int i = 0; i < num_row[type]; i++) {
            if (row[i] == null) {
                row[i] = new Entry(row_data[type][i]);
                row[i].setData();
            } else {
                if (row[i].entry != row_data[type][i]) {
                    row[i].entry = row_data[type][i];
                    row[i].setData();
                }
            }
        }
        if (update)
            update();
    }

    private int addEntry(DataSet data_set, int iter, boolean first,
            boolean update)
    {
        int index = -1, last_index = -1;
        int max_entry = data_set.getMaxDataEntry(iter);
        if (max_entry > 1)
            last_index = data_set.getLastIndex(iter);
        for (int i = 0; i < max_entry; i++) {
            if (!data_set.hasDataEntry(i, iter))
                continue;
            int n = addEntry(data_set.getDataEntry(i, iter), iter, first,
                    update);
            if (last_index == i || index < 0)
                index = n;
        }
        return index;
    }

    private int addEntry(DataEntry entry, int iter, boolean first,
            boolean update)
    {
        int j;
        String str = entry.getName();
        for (j = 0; j < num_row[iter]; j++) {
            if (str.equals(row_data[iter][j].getName())) {
                if (!entry.equals(row_data[iter][j], false)) {
                    entry.setSelected(row_data[iter][j].getSelected());
                    if (row_data[iter][j] == selected_data[iter])
                        selected_data[iter] = entry;
                    row_data[iter][j] = row[j].entry = entry;
                    if (update && iter == type && entry == selected_data[iter])
                        updateData(selected_data[iter], false);
                    if (!DataEntry.nowFieldDifferOnly())
                        need_save = true;
                }
                if (iter == type)
                    row[j].setData();
                return j;
            }
        }
        if (!need_save && !entry.getName().equals(""))
            need_save = true;
        if (num_row[iter] > 0 && row_data[iter][0].getName().equals("")) {
            row_data[iter][0] = entry;
            if (iter == type) {
                row[0].entry = entry;
                row[0].setData();
            }
            return 0;
        }
        if (num_row[iter] >= row.length) {
            int new_len = row.length + Math.min(INIT_ROW_SIZE, row.length / 2);
            Entry[] new_row = new Entry[new_len];
            for (j = 0; j < row.length; j++)
                new_row[j] = row[j];
            for (int i = 0; i < DataSet.MAX_TYPE; i++) {
                DataEntry[] new_row_data = new DataEntry[new_len];
                for (j = 0; j < row.length; j++)
                    new_row_data[j] = row_data[i][j];
                row_data[i] = new_row_data;
            }
            row = new_row;
        }
        row_data[iter][num_row[iter]] = entry;
        if (row[num_row[iter]] == null) {
            row[num_row[iter]] = new Entry(entry);
        } else {
            row[num_row[iter]].entry = entry;
            if (iter == type)
                row[num_row[iter]].setData();
        }
        num_row[iter]++;
        if (first) {
            moveSelection(iter, false, true, false);
            return 0;
        } else {
            return num_row[iter] - 1;
        }
    }

    private int getSelectedIndex()
    {
        if (selected_data[type] == null)
            return -1;
        for (int i = 0; i < num_row[type]; i++) {
            if (row_data[type][i] == selected_data[type])
                return i;
        }
        return -1;
    }

    private void setColumnSize()
    {
        if (update_depth != 0)
            return;
        update_depth++;
        int[] width = new int[column.length];
        width[CHECK] = CHECK_WIDTH;
        width[SELECT] = SELECT_WIDTH;
        for (int i = SELECT + 1; i < column.length; i++)
            width[i] = textExtent(column_label[type][i]);
        for (int i = 0; i < num_row[type]; i++) {
            row[i].setMaxWidth(width);
        }
        int total_width = table.getClientArea().width;
        for (int i = 0; i < column.length; i++) {
            if (i != column.length - 1) {
                total_width -= width[i];
            } else {
                width[i] = Math.max(total_width, width[i]);
            }
            if (column[i].getResizable() && column[i].getWidth() != width[i])
                column[i].setWidth(width[i]);
        }
        update_depth--;
    }

    public void updateChart(boolean update)
    {
        if (selected_data[type] == null)
            return;
        for (int i = 0; i < num_row[type]; i++) {
            if (row[i].entry == selected_data[type]) {
                if (update)
                    row[i].updateEntry();
                updateData(row[i].entry, false);
                break;
            }
        }
    }

    private int textExtent(String str)
    {
        return gc.textExtent(str).x + TEXT_PADDING;
    }

    public void update()
    {
        table.setEnabled(false);
        table.setItemCount(num_row[type]);
        label.setText(Integer.toString(num_row[type]) + " "
                + Resource.getString("row_count"));
        bottom_container.layout();
        container.layout();
        bottom_container.update();
        container.update();
        container.redraw();
    }

    public void setGroupName()
    {
        group_name = Resource.getString("table_label");
        String mode = ChartMode.getModeName(false, true);
        if (mode != null)
            group_name += " - " + mode;
        if (Resource.hasCustomData())
            group_name += " - " + Resource.getModName();
        group.setText(group_name);
    }

    public void updateOverride()
    {
        String str = ChartTab.getData().getOverridenStatus();
        if (str.equals("")) {
            group.setText(group_name);
        } else {
            group.setText(group_name + "    [" + str
                    + Resource.getString("mod_label") + "]");
        }
    }

    private void setButtonEditors(boolean check)
    {
        num_visible_row = getNumVisibleRow();
        if (check_editor != null && check_editor.length >= num_visible_row) {
            if (check) {
                for (int i = num_visible_row; i < check_editor.length; i++)
                    setButtonEditorItem(i, null, null);
            }
        } else {
            if (check_editor != null) {
                for (int i = 0; i < check_editor.length; i++) {
                    disposeButtonField(check_editor[i]);
                    disposeButtonField(select_editor[i]);
                }
            }
            check_editor = new TableEditor[num_visible_row];
            select_editor = new TableEditor[num_visible_row];
            for (int i = 0; i < num_visible_row; i++) {
                check_editor[i] = addButtonField(SWT.CHECK, CHECK);
                select_editor[i] = addButtonField(SWT.RADIO, SELECT);
                getButton(check_editor[i]).addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event)
                            {
                                Button b = (Button) event.getSource();
                                for (int k = 0; k < num_visible_row; k++) {
                                    if (b == getButton(check_editor[k])) {
                                        int index = k + table.getTopIndex();
                                        if (index >= num_row[type])
                                            return;
                                        row_data[type][index].setSelected(b
                                                .getSelection());
                                        return;
                                    }
                                }
                            }
                        });
                getButton(select_editor[i]).addSelectionListener(
                        new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event)
                            {
                                Button b = (Button) event.getSource();
                                if (!b.getSelection())
                                    return;
                                for (int k = 0; k < num_visible_row; k++) {
                                    if (b == getButton(select_editor[k])) {
                                        int index = k + table.getTopIndex();
                                        if (index >= num_row[type])
                                            return;
                                        Entry r_entry = row[index];
                                        selected_data[type] = r_entry.entry;
                                        r_entry.updateEntry();
                                        updateData(r_entry.entry, false);
                                        Moira.getMenu()
                                                .setExampleSelectedIndex(
                                                        getSelectedIndex());
                                        return;
                                    }
                                }
                            }
                        });
            }
        }
        int top = table.getTopIndex();
        for (int i = 0; i < num_visible_row; i++) {
            int index = top + i;
            if (index >= num_row[type]) {
                setButtonEditorItem(i, null, null);
            } else {
                TableItem item = table.getItem(index);
                setButtonEditorItem(i, item, row_data[type][index]);
            }
        }
    }

    private void setButtonEditorItem(int index, TableItem item, DataEntry entry)
    {
        Button check = getButton(check_editor[index]);
        Button select = getButton(select_editor[index]);
        check_editor[index].setEditor(check, item, CHECK);
        select_editor[index].setEditor(select, item, SELECT);
        boolean visible = item != null;
        if (visible) {
            Color color = item.getBackground();
            if (check.getBackground() != color) {
                check.setBackground(color);
                select.setBackground(color);
            }
            if (entry != null) {
                if (check.getSelection() != entry.getSelected())
                    check.setSelection(entry.getSelected());
                boolean selected = selected_data[type] == entry;
                if (select.getSelection() != selected)
                    select.setSelection(selected);
            }
        }
        if (check.getVisible() != visible) {
            check.setVisible(visible);
            select.setVisible(visible);
        }
    }

    private TableEditor addButtonField(int style, int pos)
    {
        Button button = new Button(table, style);
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = true;
        editor.horizontalAlignment = SWT.LEFT;
        editor.setEditor(button, null, pos);
        return editor;
    }

    private void disposeButtonField(TableEditor editor)
    {
        getButton(editor).dispose();
        editor.dispose();
    }

    public void updateFont()
    {
        table.setRedraw(false);
        hideField();
        setFont();
        setColumnSize();
        table.update();
        setButtonEditors(true);
        table.setRedraw(true);
        update();
    }

    private void setFont()
    {
        if (font != null)
            font.dispose();
        font = new Font(Display.getCurrent(), "Dialog.bold",
                Resource.getSwtDataFontSize(), MenuFolder.getSwtFontStyle());
        gc.setFont(font);
        table.setFont(font);
        place_field.setFont(font);
        text_field.setFont(font);
        label_field.setFont(font);
        setColor();
    }

    public void setColor()
    {
        Color fg_color = ColorManager.getColor("table_font_color");
        bg_color = ColorManager.getColor("table_background_color");
        if (fg_color != table.getForeground()) {
            table.setForeground(fg_color);
            place_field.setForeground(fg_color);
            text_field.setForeground(fg_color);
            label_field.setForeground(fg_color);
        }
        if (bg_color != table.getBackground()) {
            table.setBackground(bg_color);
            chart_type.setBackground(bg_color);
            footer.setBackground(bg_color);
        }
        odd_row_bg_color = ColorManager.getColor("table_odd_row_bg_color");
        hilite_bg_color = ColorManager.getColor("table_hilite_bg_color");
        desc.updateAttribute(false);
    }

    public void dispose()
    {
        gc.dispose();
        font.dispose();
    }

    static public Button getButton(TableEditor editor)
    {
        return (Button) editor.getEditor();
    }

    public boolean genPicture(String dir_name)
    {
        File dir = new File(dir_name);
        if (!dir.canWrite())
            return false;
        boolean success = true;
        for (int i = 0; i < num_row[type]; i++) {
            updateData(row[i].entry, false);
            if (!Moira.getMenu().captureImage(dir_name, "case_" + (i + 1),
                    false)) {
                success = false;
                break;
            }
        }
        updateChart(false);
        return success;
    }

    public boolean golden()
    {
        String file_name = Resource.hasPrefKey("last_open_file") ? Resource
                .getPrefString("last_open_file") : null;
        if (file_name == null)
            return false;
        String data_ext = "." + Resource.DATA_EXT;
        int data_len = data_ext.length();
        file_name = file_name.substring(0, file_name.length() - data_len)
                + ".au";
        String file_path = Resource.hasPrefKey("last_open_path") ? Resource
                .getPrefString("last_open_path") : null;
        String full_path_name;
        if (file_path != null)
            full_path_name = file_path + File.separator + file_name;
        else
            full_path_name = file_name;
        logResult(full_path_name);
        return true;
    }

    public int regression(String dir_name, boolean update)
    {
        File dir = new File(dir_name);
        if (!dir.canWrite())
            return -1;
        int num_failed = 0;
        String last_path_name = Moira.getIO().getLastOpenPath();
        String last_file_name = Moira.getIO().getLastOpenFile();
        FileIO log = new FileIO(dir_name + File.separator + "regression.log",
                false, true);
        String data_ext = "." + Resource.DATA_EXT;
        int data_len = data_ext.length();
        File[] array = dir.listFiles();
        for (int i = 0; i < array.length; i++) {
            File file = array[i];
            String file_name = file.getName();
            if (!file_name.endsWith(data_ext))
                continue;
            // process each file in directory
            String base_name = file_name.substring(0, file_name.length()
                    - data_len);
            String full_base_name = dir_name + File.separator + base_name;
            log.putLine("Test " + base_name + ":");
            boolean pass = true;
            int index = openFile(false, true, false, dir_name + File.separator
                    + file_name);
            if (index == Integer.MIN_VALUE) {
                pass = false;
                log.putLine("  Cannot load data!");
            } else {
                Moira.updateModEval();
                Moira.setShellTitle(null, "[" + base_name + "]", false, true);
                if (index >= 0)
                    updateData(index, true);
                updateChart(false);
                Moira.flushEvents(false);
                String command = Resource.getAlternateCommand();
                if (command != null) {
                    MenuFolder.processCommand(command, true);
                    Moira.getChart().updateAttribute();
                }
                Moira.flushEvents(false);
                if (update) {
                    logResult(full_base_name + ".au");
                } else {
                    logResult(full_base_name + ".log");
                    pass = log.fileDiff(full_base_name + ".log", full_base_name
                            + ".au", null, false);
                    if (pass && Resource.getPrefInt("check_save") == 1) {
                        saveFile(full_base_name + ".sav", true);
                        pass = log.fileDiff(full_base_name + ".sav",
                                full_base_name + ".mri", "pref=", true);
                        if (!pass) {
                            log.putLine(full_base_name + ".sav and "
                                    + full_base_name + ".mri are different.");
                        }
                    }
                }
            }
            log.putLine(pass ? "  Passed." : "  Failed.");
            if (!pass)
                num_failed++;
        }
        log.dispose();
        Moira.loadData(last_path_name + File.separator + last_file_name, -1,
                false);
        return num_failed;
    }

    private void logResult(String file_name)
    {
        FileIO reg = new FileIO(file_name, false, true);
        reg.putLine("---");
        reg.putString(ChartTab.getTab(ChartTab.DATA_TAB).getText());
        reg.putLine("---");
        reg.putString(ChartTab.getTab(ChartTab.POLE_TAB).getText());
        reg.putLine("---");
        DataTab eval_tab = ChartTab.getTab(ChartTab.EVAL_TAB);
        if (eval_tab != null && RuleEntry.hasRuleEntry(true)) {
            reg.putString(eval_tab.getText());
            reg.putLine("---");
        }
        reg.putString(ChartTab.getTab(ChartTab.NOTE_TAB).getText());
        reg.putLine("---");
        reg.dispose();
    }

    public boolean copyEntry()
    {
        if (selected_data[type] == null)
            return false;
        String data = selected_data[type].packEntry(type);
        if (data == null)
            return false;
        Moira.toClipboard(data);
        return true;
    }

    public boolean pasteEntry()
    {
        String data = Moira.fromClipboard();
        if (data == null || data.trim().equals(""))
            return false;
        newEntry();
        if (selected_data[type] == null)
            return false;
        if (!selected_data[type].unpackEntry(data, type))
            return false;
        updateData(selected_data[type], false);
        return true;
    }

    private class Entry {
        private TableItem item;

        private DataEntry entry;

        public Entry(DataEntry data)
        {
            entry = data;
        }

        public void initEntry(TableItem cur_item, boolean odd)
        {
            item = cur_item;
            if (odd)
                item.setBackground(odd_row_bg_color);
            setData();
        }

        public void setData()
        {
            if (item == null)
                return;
            item.setText(NAME, entry.getName());
            item.setText(SEX, entry.getSex() ? male : female);
            if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                item.setText(MOUNTAIN, entry.getMountainPos());
                item.setText(DAYSET, entry.getChoice() ? day_choice
                        : night_choice);
            }
            item.setText(place_index, entry.getCity() + ", "
                    + entry.getCountry());
            item.setText(date_index, BaseCalendar.formatDate(entry
                    .getBirthDay(), false, false));
            String str = DataEntry.getOneLineDesc(entry.getNote(true),
                    SHORT_DESC_LENGTH, false);
            if (str == null || str.trim().equals(""))
                item.setText(note_index, "");
            else
                item.setText(note_index, str);
        }

        public void setMaxWidth(int[] width)
        {
            if (item == null)
                return;
            width[NAME] = Math.max(textExtent(item.getText(NAME)), width[NAME]);
            if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                width[MOUNTAIN] = Math.max(textExtent(item.getText(MOUNTAIN)),
                        width[MOUNTAIN]);
                width[DAYSET] = Math.max(textExtent(item.getText(DAYSET)),
                        width[DAYSET]);
            }
            width[SEX] = Math.max(textExtent(item.getText(SEX)), width[SEX]);
            width[place_index] = Math.max(
                    textExtent(item.getText(place_index)), width[place_index]);
            width[date_index] = Math.max(textExtent(item.getText(date_index)),
                    width[date_index]);
        }

        public void updateEntry()
        {
            auditName();
            String str = item.getText(NAME);
            entry.setName(str.equals("") ? null : str);
            auditSex();
            entry.setSex(item.getText(SEX).equals(male));
            entry.setBirthDay(auditDate());
            City city = auditPlace();
            str = item.getText(place_index);
            int index = str.lastIndexOf(',');
            String country_name = str.substring(index + 1).trim();
            String city_name = str.substring(0, index).trim();
            if (!city_name.equalsIgnoreCase(entry.getCity())
                    || !country_name.equalsIgnoreCase(entry.getCountry())) {
                if (city == null)
                    city = City.matchCity(city_name, country_name, false);
                if (city != null) {
                    entry.setCity(city_name);
                    entry.setCountry(country_name);
                    entry.setZone(city.getZoneName());
                }
            }
            if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                auditMountain();
                entry.setMountainPos(item.getText(MOUNTAIN));
                auditChoice();
                entry.setChoice(item.getText(DAYSET).equals(day_choice));
            }
        }

        private void auditName()
        {
            String str = item.getText(NAME);
            if (!str.trim().equals(str))
                item.setText(NAME, str.trim());
        }

        private void auditSex()
        {
            String str = item.getText(SEX).trim().toLowerCase();
            if (str.startsWith("f"))
                item.setText(SEX, female);
            else if (!str.equals(female))
                item.setText(SEX, male);
        }

        private void auditChoice()
        {
            if (!ChartMode.isChartMode(ChartMode.PICK_MODE))
                return;
            String str = item.getText(DAYSET).trim().toLowerCase();
            if (str.startsWith("s"))
                item.setText(DAYSET, night_choice);
            else if (!str.equals(night_choice))
                item.setText(DAYSET, day_choice);
        }

        private void auditMountain()
        {
            if (!ChartMode.isChartMode(ChartMode.PICK_MODE))
                return;
            String str = item.getText(MOUNTAIN).trim();
            str = City.formatMapPos(City.parseMapPos(str), true);
            item.setText(MOUNTAIN, str);
        }

        private int[] auditDate()
        {
            int[] date = new int[5];
            item.setText(DATE, BaseCalendar.auditDay(item.getText(DATE), date));
            return date;
        }

        private City auditPlace()
        {
            String str = item.getText(place_index);
            StringTokenizer st = new StringTokenizer(str, ",");
            int n_tok = st.countTokens();
            if (n_tok >= 1) {
                String tok_1 = st.nextToken();
                String tok_2 = (n_tok > 1) ? st.nextToken() : "";
                double long_val, lat_val;
                long_val = City.parseLongLatitude(tok_1, 'E', 'W');
                lat_val = City.parseLongLatitude(tok_2, 'N', 'S');
                if (long_val != City.INVALID && lat_val != City.INVALID) {
                    int iter;
                    City city = null;
                    for (iter = 0; iter < 2; iter++) {
                        city = City.matchCity(long_val, lat_val,
                                (iter > 0) ? City.ANY_MATCH_ERROR_SQ
                                        : City.MATCH_ERROR_SQ);
                        if (city != null)
                            break;
                    }
                    if (city != null) {
                        if (iter > 0) {
                            item.setText(place_index, City.formatLongLatitude(
                                    long_val, true, true, false)
                                    + ", "
                                    + City.formatLongLatitude(lat_val, false,
                                            true, false)
                                    + ", "
                                    + city.getCountryName());
                        } else {
                            item.setText(place_index, city.getCityName() + ", "
                                    + city.getCountryName());
                        }
                        return city;
                    }
                } else {
                    City city = City.matchCity(tok_1.trim(), tok_2.trim(),
                            false);
                    if (city != null) {
                        item.setText(place_index, city.getCityName() + ", "
                                + city.getCountryName());
                        return city;
                    }
                }
            }
            item.setText(place_index, City.getDefaultCity() + ", "
                    + City.getDefaultCountry());
            return null;
        }

        public void dispose()
        {
            item = null;
        }
    }
}