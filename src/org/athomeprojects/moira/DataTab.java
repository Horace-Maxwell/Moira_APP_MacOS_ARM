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

import org.athomeprojects.base.BaseTab;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.Resource;
import org.athomeprojects.moira.ChartTab.CacheEntry;
import org.athomeprojects.swtext.ColorManager;
import org.athomeprojects.swtext.ExtendStyledText;
import org.athomeprojects.swtext.FontMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;

class DataTab extends BaseTab {
    private final int SMALL_MARGIN = 5;

    private final int LARGE_MARGIN = 10;

    static private int hilite_offset;

    private ScrolledComposite scroll;

    private ExtendStyledText text;

    private Font font;

    private Group group;

    private Composite margin, container, detach_container;

    private String group_name, tab_name;

    private int before_pos, after_pos, margin_width, folder_index;

    private boolean wrap, multi_pos, rubber_band, small, allow_find, detached;

    private Point drag_pt;

    private String type;

    public Composite createDataPage(Composite parent, String name,
            String type_name, boolean read_only, boolean is_wrap,
            boolean is_note, boolean enable_find, boolean direct)
    {
        tab_name = name;
        type = type_name;
        wrap = is_wrap;
        allow_find = enable_find;
        if (direct) {
            group = null;
            container = new Composite(parent, SWT.NONE);
            multi_pos = false;
        } else {
            group = new Group(parent, SWT.NONE);
            container = group;
            multi_pos = Resource.hasKey(type + "_folder");
        }
        folder_index = multi_pos ? Resource.getPrefInt(type + "_folder") : 0;
        margin_width = LARGE_MARGIN;
        if (multi_pos) {
            group.addMouseListener(new MouseAdapter() {
                public void mouseDown(MouseEvent event)
                {
                    if (folder_index == TabManager.WINDOW_FOLDER)
                        return;
                    group.setCapture(true);
                    Rectangle shell_bound;
                    if (ChartTab.getUIMode()
                            || Resource.getPrefInt("has_docked") == 1) {
                        shell_bound = null;
                    } else {
                        shell_bound = Moira.getShell().getBounds();
                        shell_bound.y += shell_bound.height;
                        shell_bound.height = Resource.getInt("dock_height");
                    }
                    Rectangle bound = group.getBounds();
                    drag_pt = group.toDisplay(event.x, event.y);
                    Point box_pt = group.getParent()
                            .toDisplay(bound.x, bound.y);
                    DrawSWT.initRubberBandRect(drag_pt, box_pt, bound.width,
                            bound.height, shell_bound);
                    rubber_band = true;
                }

                public void mouseUp(MouseEvent event)
                {
                    if (folder_index == TabManager.WINDOW_FOLDER)
                        return;
                    rubber_band = false;
                    Rectangle win_bound = DrawSWT.endRubberBandRect();
                    group.setCapture(false);
                    int drag_error = Resource.getInt("drag_error");
                    Rectangle bound = new Rectangle(drag_pt.x - drag_error,
                            drag_pt.y - drag_error, 2 * drag_error,
                            2 * drag_error);
                    if (win_bound != null
                            && !bound.contains(group
                                    .toDisplay(event.x, event.y))) {
                        Resource
                                .putPrefInt(type + "_folder_last", folder_index);
                        Resource.putPrefInt(type + "_docked", DrawSWT
                                .isDocked() ? 1 : 0);
                        Resource.putPrefString(type + "_bounds", win_bound.x
                                + ", " + win_bound.y + ", " + win_bound.width
                                + ", " + win_bound.height);
                        Moira.saveShellBounds(null, null);
                        Moira.getChart().changeTabPos(group,
                                TabManager.WINDOW_FOLDER);
                    }
                }
            });
            group.addMouseMoveListener(new MouseMoveListener() {
                public void mouseMove(MouseEvent event)
                {
                    if (rubber_band)
                        DrawSWT.drawRubberBandRect(group.toDisplay(event.x,
                                event.y));
                }
            });
        }
        group_name = "";
        container.setLayout(new FillLayout());
        scroll = new ScrolledComposite(container, wrap ? SWT.V_SCROLL
                : (SWT.H_SCROLL | SWT.V_SCROLL));
        scroll.setExpandVertical(true);
        scroll.setExpandHorizontal(true);
        scroll.setLayout(new FillLayout());
        scroll.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent event)
            {
                updateScrollSize();
            }
        });
        scroll.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event)
            {
                focusText();
            }
        });
        margin = new Composite(scroll, SWT.BORDER);
        FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = margin_width;
        margin.setLayout(layout);
        text = new ExtendStyledText(margin, SWT.MULTI);
        text.setDoubleClickEnabled(!multi_pos);
        text.setWordWrap(wrap);
        text.setEditable(!read_only);
        text.addMouseTrackListener(new MouseTrackAdapter() {
            public void mouseEnter(MouseEvent event)
            {
                if (folder_index == TabManager.SUB_FOLDER
                        || folder_index == TabManager.WINDOW_FOLDER
                        && Resource.getPrefInt(type + "_docked") != 0)
                    Moira.setFocus(text);
            }
        });
        text.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event)
            {
                if (event.stateMask == SWT.CONTROL) {
                    switch (event.character) {
                        case 0x01: // Ctrl-A
                            text.selectAll();
                            break;
                        case 0x19: // Ctrl-Y
                            text.redo();
                            break;
                        case 0x1a: // Ctrl-Z
                            text.undo();
                            break;
                        case 0x02: // Ctrl-B
                            if (text.getEditable())
                                text.toggleBold();
                            break;
                        case 0x08: // Ctrl-H
                            if (text.getEditable()) {
                                text.toggleHilite(hilite_offset);
                                hilite_offset = 0; // reset to 0
                            }
                            break;
                        case 0x03: // Ctrl-C
                        case 0x16: // Ctrl-V
                        case 0x18: // Ctrl-X
                            break;
                        default:
                            if (folder_index == TabManager.WINDOW_FOLDER
                                    && Character
                                            .isLowerCase((char) event.keyCode)) {
                                Moira.getShell().setFocus();
                                Moira.postKeyEvent((char) event.keyCode,
                                        SWT.CONTROL);
                            }
                            break;
                    }
                    updateScrollSize();
                } else if (allow_find
                        && (event.keyCode == SWT.F5 || event.keyCode == SWT.F6)) {
                    FolderToolBar.findNextEntry(event.keyCode == SWT.F5);
                } else if (folder_index == TabManager.WINDOW_FOLDER
                        && event.keyCode >= SWT.F1 && event.keyCode <= SWT.F15) {
                    Moira.getShell().setFocus();
                    Moira.postKeyEvent((char) 0, event.keyCode);
                }
            }
        });
        text.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event)
            {
                focusText();
            }
        });
        scroll.setContent(margin);
        clear();
        if (!read_only) {
            resetUndo();
            if (is_note) {
                text.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent event)
                    {
                        saveNote();
                    }
                });
                setNote(null);
            }
        }
        if (multi_pos) {
            text.addMouseListener(new MouseAdapter() {
                public void mouseDoubleClick(MouseEvent event)
                {
                    if (folder_index == TabManager.WINDOW_FOLDER) {
                        if (Resource.getPrefInt(type + "_docked") == 0) {
                            group.getShell().close();
                        } else {
                            Moira.saveShellBounds(null, null);
                            Moira.getChart().restoreTabPos(group, true);
                        }
                    } else if (ChartTab.getUIMode()) {
                        return;
                    }
                    Moira.getChart().changeTabPos(group, -1);
                }
            });
        }
        final Menu pop_up = new Menu(Moira.getShell(), SWT.POP_UP);
        Moira.getMenu().addTextEditMenu(pop_up, read_only, text.getEditable());
        text.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event)
            {
                pop_up.setVisible(true);
            }
        });
        updateAttribute(false);
        return container;
    }

    static public void toggleBold()
    {
        Moira.postKeyEvent('b', SWT.CONTROL);
    }

    static public void toggleHilite(int index)
    {
        hilite_offset = index;
        if (hilite_offset < 0)
            return;
        Moira.postKeyEvent('h', SWT.CONTROL);
    }

    public void setAlternateContainer(Composite parent)
    {
        if (parent == null) {
            detached = false;
            if (detach_container != null)
                detach_container.setParent(TabManager.getPlaceHolder());
            scroll.setParent(group);
            group.layout();
        } else {
            detached = true;
            if (detach_container == null) {
                detach_container = new Composite(parent, SWT.NONE);
                detach_container.setLayout(new FillLayout());
            } else {
                detach_container.setParent(parent);
            }
            scroll.setParent(detach_container);
            detach_container.layout();
        }
    }

    public void setGroupName(boolean add_mod)
    {
        if (group == null)
            return;
        if (!ChartMode.isMultipleMode(false)) {
            CacheEntry entry = Moira.getChart().getCacheEntry();
            group_name = entry.getName();
            if (!group_name.equals(""))
                group_name += " - ";
        } else {
            group_name = "";
        }
        group_name += ChartMode.isChartMode(ChartMode.PICK_MODE) ? Resource
                .getString("pick_data_label") : tab_name;
        if (add_mod) {
            String mode = ChartMode.getModeName(false, true);
            if (mode != null)
                group_name += " - " + mode;
            if (Resource.hasCustomData())
                group_name += " - " + Resource.getModName();
        }
        group.setText(group_name);
    }

    public void updateOverride()
    {
        if (group == null)
            return;
        int folder = Resource.getPrefInt(type + "_folder");
        if (folder == TabManager.MAIN_FOLDER
                || folder == TabManager.WINDOW_FOLDER) {
            String str = ChartTab.getData().getOverridenStatus();
            if (str.equals("")) {
                group.setText(group_name);
            } else {
                group.setText(group_name + "    [" + str
                        + Resource.getString("mod_label") + "]");
            }
        } else {
            String str = ChartMode.isChartMode(ChartMode.PICK_MODE) ? Resource
                    .getString("pick_data_label") : tab_name;
            group.setText(str);
        }
        group.setToolTipText((folder == TabManager.WINDOW_FOLDER) ? ((Resource
                .getPrefInt(type + "_docked") != 0) ? Resource
                .getString("tip_restore_window") : null) : (Resource
                .getString("tip_drag_window_1")
                + tab_name + Resource.getString("tip_drag_window_2")));
    }

    private void setFont(boolean small_font)
    {
        if (font != null) {
            text.setFont(null);
            font.dispose();
        }
        int size = Resource.getSwtDataFontSize();
        if (small_font)
            size = Math.min(Resource.getSwtSmallDataFontSize(), size);
        font = new Font(Display.getCurrent(),  "Dialog.bold", size,
                MenuFolder.getSwtFontStyle());
        int height = font.getFontData()[0].getHeight();
        ScrollBar bar = scroll.getVerticalBar();
        bar.setIncrement(height);
        bar.setPageIncrement(10 * height);
        if (!wrap) {
            bar = scroll.getHorizontalBar();
            bar.setIncrement(height);
            bar.setPageIncrement(10 * height);
        }
        text.setFont(font);
        Color fg_color = ColorManager.getColor(type + "_font_color");
        text.setForeground(fg_color);
        Color bg_color = ColorManager.getColor(type + "_background_color");
        text.setBackground(bg_color);
        margin.setBackground(bg_color);
    }

    public void clear()
    {
        if (text == null)
            return;
        before_pos = -1;
        text.setText("");
    }

    public void setWrapMode(boolean set)
    {
        if (wrap == set)
            return;
        wrap = set;
        scroll.setExpandHorizontal(wrap);
        text.setWordWrap(wrap);
        updateAttribute(false);
    }

    public void append(String str)
    {
        text.append(str);
    }

    public void appendLine(String str)
    {
        text.append(str);
        appendLine();
    }

    public void appendLine()
    {
        text.append(EOL);
    }

    public void setName(String name, boolean sex, boolean replace)
    {
        String str = "";
        if (!name.equals(""))
            str += Resource.getString("name") + ": " + name + "  ";
        str += Resource.getString("sex") + ": "
                + Resource.getString(sex ? "male" : "female");
        replaceName(str, replace);
    }

    public void setName(String name, boolean replace)
    {
        String str = "";
        if (!name.equals(""))
            str += Resource.getString("name") + ": " + name;
        replaceName(str, replace);
    }

    private void replaceName(String str, boolean replace)
    {
        if (replace && before_pos >= 0) { // inititialize
            String info = text.getText();
            text.setText(info.substring(0, before_pos));
            info = info.substring(after_pos);
            if (!str.equals(""))
                appendLine(str);
            after_pos = text.getCharCount();
            text.append(info);
        } else { // replacement
            before_pos = text.getCharCount();
            if (!str.equals(""))
                appendLine(str);
            after_pos = text.getCharCount();
        }
    }

    public void replace(String src, String dst)
    {
        String str = text.getText();
        str = str.replaceAll(src, dst);
        text.setText(str);
    }

    public void setText(String str)
    {
        if (str == null)
            return;
        text.setText(str);
        updateScrollSize();
    }

    public String getText()
    {
        return text.getText();
    }

    public String getTextOnly()
    {
        return text.getTextOnly();
    }

    public void saveNote()
    {
        String str = getNote(false);
        Moira.getTable().updateNote(hasValidNote(str) ? str : null);
    }

    public String getNote(boolean text_only)
    {
        return text_only ? text.getTextOnly() : text.getText();
    }

    public void setNote(String note)
    {
        if (note == null || note.trim().equals(""))
            note = Resource.getString("enter_note_here");
        text.setText(note);
        resetUndo();
        FolderToolBar.resetSearch();
        updateScrollSize();
    }

    public void resetUndo()
    {
        text.setUndoEnable(true);
    }

    public String getSelectionText()
    {
        return text.getSelectionText();
    }

    public void setSelection(int pos, int len)
    {
        text.setSelectionRange(pos, len);
        Rectangle bound = scroll.getClientArea();
        int height = text.getLineHeight();
        Point pt = scroll.toDisplay(bound.x, bound.y);
        bound.x = pt.x;
        bound.y = pt.y;
        bound.height -= height;
        Point s_pt = text.toDisplay(text.getLocationAtOffset(pos));
        Point e_pt = text.toDisplay(text.getLocationAtOffset(pos + len));
        if (bound.contains(s_pt) && bound.contains(e_pt))
            return;
        s_pt = margin.toControl(s_pt);
        s_pt.y = Math.max(0, s_pt.y - bound.height / 3);
        scroll.setOrigin(s_pt);
    }

    public boolean hasValidNote(String note)
    {
        return !note.trim().equals(Resource.getString("enter_note_here"));
    }

    public void update()
    {
        if (!text.getEditable())
            appendLine();
        updateScrollSize();
    }

    public boolean isTabVisible()
    {
        return Resource.getPrefInt("show_" + type) != 0;
    }

    public boolean allowFind()
    {
        return allow_find;
    }

    public String getType()
    {
        return type;
    }

    public void changePos(int index)
    {
        if (index < 0)
            index = (Resource.getPrefInt(type + "_folder") != 0) ? 0 : 1;
        Resource.putPrefInt(type + "_folder", index);
        updateOverride();
    }

    public int getFolderIndex()
    {
        folder_index = multi_pos ? Resource.getPrefInt(type + "_folder") : 0;
        return folder_index;
    }

    private void focusText()
    {
        updateAttribute(true);
        folder_index = multi_pos ? Resource.getPrefInt(type + "_folder") : 0;
        if (folder_index == TabManager.MAIN_FOLDER)
            Moira.setFocus(text);
    }

    public void updateAttribute(boolean check)
    {
        Composite composite = detached ? detach_container : container;
        boolean t_small = folder_index != TabManager.WINDOW_FOLDER
                && composite.getClientArea().width < Resource
                        .getInt("large_tab_width");
        if (check && small == t_small)
            return;
        small = t_small;
        setFont(small);
        margin_width = small ? SMALL_MARGIN : LARGE_MARGIN;
        FillLayout layout = (FillLayout) margin.getLayout();
        layout.marginHeight = layout.marginWidth = margin_width;
        margin.layout();
        updateScrollSize();
    }

    private void updateScrollSize()
    {
        Point p;
        if (wrap) {
            int width = scroll.getClientArea().width;
            int d_width = 2 * (margin.getBorderWidth() + margin_width);
            int t_width = width - d_width;
            if (t_width > 0) {
                p = text.computeSize(t_width, SWT.DEFAULT);
                p.x = width;
                p.y += d_width;
            } else {
                p = margin.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            }
        } else {
            p = margin.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        }
        scroll.setMinSize(p);
    }

    public void dispose()
    {
        if (font != null)
            font.dispose();
        font = null;
    }
}