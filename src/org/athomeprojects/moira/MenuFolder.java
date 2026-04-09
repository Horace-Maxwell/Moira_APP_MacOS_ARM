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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.Calculate;
import org.athomeprojects.base.ChartData;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.City;
import org.athomeprojects.base.DataSet;
import org.athomeprojects.base.DrawAWT;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.HTMLData;
import org.athomeprojects.base.ImageControl;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Print;
import org.athomeprojects.base.Resource;
import org.athomeprojects.base.RuleEntry;
import org.athomeprojects.swtext.CButton;
import org.athomeprojects.swtext.ExtendStyledText;
import org.athomeprojects.swtext.FontMap;
import org.athomeprojects.swtext.ImageManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuFolder {
    static private final int SUB_FILE = 0;

    static private final int SUB_EDIT = 1;

    static private final int SUB_PREF = 2;

    static private final int SUB_SEARCH = 3;

    static private final int SUB_WIN = 4;

    static private final int SUB_HELP = 5;

    static private Menu[] sub_menu;

    static private MenuItem open, save, save_as, name, show_data, show_pole,
            show_note, show_table, show_now, show_fixstar, show_compass,
            show_chart_mode, show_horiz, show_aspects, aspects, angles, alt_ui;

    static private Shell html_shell, data_shell;

    private int example_index;

    private ExampleData[] example_data;

    public MenuFolder(Shell parent)
    {
        Menu menu = new Menu(parent, SWT.BAR);
        parent.setMenuBar(menu);
        sub_menu = new Menu[6];
        buildMenuHeader(menu, sub_menu);
    }

    private void buildMenuHeader(Menu menu, Menu[] sub)
    {
        MenuItem file = new MenuItem(menu, SWT.CASCADE);
        file.setText(Resource.getString("file_menu"));
        sub[SUB_FILE] = new Menu(file);
        file.setMenu(sub[SUB_FILE]);
        MenuItem edit = new MenuItem(menu, SWT.CASCADE);
        edit.setText(Resource.getString("edit_menu"));
        sub[SUB_EDIT] = new Menu(edit);
        edit.setMenu(sub[SUB_EDIT]);
        MenuItem pref = new MenuItem(menu, SWT.CASCADE);
        pref.setText(Resource.getString("pref_menu"));
        sub[SUB_PREF] = new Menu(pref);
        pref.setMenu(sub[SUB_PREF]);
        MenuItem search = new MenuItem(menu, SWT.CASCADE);
        search.setText(Resource.getString("search_menu"));
        sub[SUB_SEARCH] = new Menu(search);
        search.setMenu(sub[SUB_SEARCH]);
        MenuItem win = new MenuItem(menu, SWT.CASCADE);
        win.setText(Resource.getString("win_menu"));
        sub[SUB_WIN] = new Menu(win);
        win.setMenu(sub[SUB_WIN]);
        MenuItem help = new MenuItem(menu, SWT.CASCADE);
        help.setText(Resource.getString("help_menu"));
        sub[SUB_HELP] = new Menu(help);
        help.setMenu(sub[SUB_HELP]);
    }

    private void addFileMenu(Menu menu)
    {
        MenuItem blank = new MenuItem(menu, SWT.NONE);
        blank.setText(Resource.getString("file_new"));
        blank.setImage(ImageManager.getImage("blank_icon"));
        blank.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doBlank();
            }
        });
        open = new MenuItem(menu, SWT.NONE);
        open.setText(Resource.getString("file_open") + "\tCtrl+O");
        open.setAccelerator(SWT.CTRL + 'O');
        open.setImage(ImageManager.getImage("open_icon"));
        open.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doOpen();
            }
        });
        save = new MenuItem(menu, SWT.NONE);
        save.setText(Resource.getString("file_save") + "\tCtrl+S");
        save.setAccelerator(SWT.CTRL + 'S');
        save.setImage(ImageManager.getImage("save_icon"));
        save.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doSave(true);
            }
        });
        save_as = new MenuItem(menu, SWT.NONE);
        save_as.setText(Resource.getString("file_save_as"));
        save_as.setImage(ImageManager.getImage("saveas_icon"));
        save_as.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doSave(false);
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem print = new MenuItem(menu, SWT.NONE);
        print.setText(Resource.getString("file_print") + "\tCtrl-P");
        print.setAccelerator(SWT.CTRL + 'P');
        print.setImage(ImageManager.getImage("print_icon"));
        print.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doPrint();
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem capture_setting = new MenuItem(menu, SWT.NONE);
        capture_setting.setText(Resource.getString("file_capture_setting"));
        capture_setting.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                captureSetting();
            }
        });
        MenuItem capture = new MenuItem(menu, SWT.NONE);
        capture.setText(Resource.getString("file_capture") + "\tCtrl-I");
        capture.setAccelerator(SWT.CTRL + 'I');
        capture.setImage(ImageManager.getImage("capture_icon"));
        capture.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doCapture();
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem mod = new MenuItem(menu, SWT.NONE);
        mod.setText(Resource.getString("file_mod"));
        mod.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                loadModification();
            }
        });
        MenuItem mod_clear = new MenuItem(menu, SWT.NONE);
        mod_clear.setText(Resource.getString("file_mod_clear"));
        mod_clear.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                clearModification();
            }
        });
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            new MenuItem(menu, SWT.SEPARATOR);
            MenuItem eval = new MenuItem(menu, SWT.NONE);
            eval.setText(Resource.getString("file_eval"));
            eval.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    loadEvaluation(false);
                }
            });
            MenuItem eval_clear = new MenuItem(menu, SWT.NONE);
            eval_clear.setText(Resource.getString("file_eval_clear"));
            eval_clear.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    clearEvaluation();
                }
            });
            if (Resource.getPrefInt("show_eval") != 0) {
                MenuItem eval_refresh = new MenuItem(menu, SWT.NONE);
                eval_refresh.setText(Resource.getString("file_eval_refresh")
                        + "\tF3");
                eval_refresh.setAccelerator(SWT.F3);
                eval_refresh.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event)
                    {
                        loadEvaluation(true);
                    }
                });
            }
        }
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem footer = new MenuItem(menu, SWT.NONE);
        footer.setText(Resource.getString("file_footer"));
        footer.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                enterFooter();
            }
        });
        MenuItem save_setting = new MenuItem(menu, SWT.NONE);
        save_setting.setText(Resource.getString("file_save_setting"));
        save_setting.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                saveSetting();
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem close = new MenuItem(menu, SWT.NONE);
        close.setText(Resource.getString("file_close"));
        close.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                ChartTab.hideTip();
                if (!Moira.getTable().checkForSave())
                    return;
                Moira.exit(false);
            }
        });
    }

    private void addEditMenu(Menu menu)
    {
        name = new MenuItem(menu, SWT.NONE);
        name.setText(Resource.getString("edit_name") + "\tCtrl+N");
        name.setAccelerator(SWT.CTRL + 'N');
        name.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                enterName(false);
            }
        });
        MenuItem lunar = new MenuItem(menu, SWT.NONE);
        lunar.setText(Resource.getString("edit_lunar"));
        lunar.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                enterLunarDate();
            }
        });
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            new MenuItem(menu, SWT.SEPARATOR);
            MenuItem house = new MenuItem(menu, SWT.NONE);
            house.setText(Resource.getString("edit_house"));
            house.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    enterHouse();
                }
            });
        }
        if (Resource.getPrefInt("edit_sign_menu") != 0) {
            MenuItem sign_pos = new MenuItem(menu, SWT.NONE);
            sign_pos.setText(Resource.getString("edit_sign"));
            sign_pos.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    enterAdjustment();
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        addTextEditMenu(menu, false, false);
    }

    public void addTextEditMenu(Menu menu, boolean read_only, boolean add_style)
    {
        if (!read_only) {
            MenuItem undo = new MenuItem(menu, SWT.NONE);
            undo.setText(Resource.getString("edit_undo") + "\tCtrl+Z");
            undo.setImage(ImageManager.getImage("undo_icon"));
            undo.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doUndo();
                }
            });
            MenuItem redo = new MenuItem(menu, SWT.NONE);
            redo.setText(Resource.getString("edit_redo") + "\tCtrl+Y");
            redo.setImage(ImageManager.getImage("redo_icon"));
            redo.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doRedo();
                }
            });
            new MenuItem(menu, SWT.SEPARATOR);
            MenuItem cut = new MenuItem(menu, SWT.NONE);
            cut.setText(Resource.getString("edit_cut") + "\tCtrl+X");
            cut.setImage(ImageManager.getImage("cut_icon"));
            cut.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doCut();
                }
            });
        }
        MenuItem copy = new MenuItem(menu, SWT.NONE);
        copy.setText(Resource.getString("edit_copy") + "\tCtrl+C");
        copy.setImage(ImageManager.getImage("copy_icon"));
        copy.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doCopy();
            }
        });
        if (!read_only) {
            MenuItem paste = new MenuItem(menu, SWT.NONE);
            paste.setText(Resource.getString("edit_paste") + "\tCtrl+V");
            paste.setImage(ImageManager.getImage("paste_icon"));
            paste.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doPaste();
                }
            });
            MenuItem delete = new MenuItem(menu, SWT.NONE);
            delete.setText(Resource.getString("edit_delete") + "\tDEL");
            delete.setImage(ImageManager.getImage("delete_icon"));
            delete.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doDelete();
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem select_all = new MenuItem(menu, SWT.NONE);
        select_all.setText(Resource.getString("edit_select_all") + "\tCtrl+A");
        select_all.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.postKeyEvent('a', SWT.CONTROL);
            }
        });
        if (add_style) {
            new MenuItem(menu, SWT.SEPARATOR);
            MenuItem bold = new MenuItem(menu, SWT.NONE);
            bold.setText(Resource.getString("edit_bold") + "\tCtrl+B");
            bold.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    DataTab.toggleBold();
                }
            });
            new MenuItem(menu, SWT.SEPARATOR);
            String[] item_names = ExtendStyledText.getHiliteNames();
            int num_hilite = item_names.length;
            MenuItem[] items = new MenuItem[num_hilite];
            for (int i = 0; i < items.length; i++) {
                items[i] = new MenuItem(menu, SWT.NONE);
                items[i].setText(item_names[i]);
                items[i].addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event)
                    {
                        DataTab.toggleHilite(ExtendStyledText
                                .getHiliteIndex(((MenuItem) event.getSource())
                                        .getText()));
                    }
                });
            }
        }
    }

    private void addPrefMenu(Menu menu)
    {
        boolean normal_mode = !ChartMode.isChartMode(ChartMode.PICK_MODE)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE);
        if (menu.getItemCount() == 0) {
            MenuItem mode = new MenuItem(menu, SWT.NONE);
            mode.setText(Resource.getString("pref_chart_mode"));
            mode.setImage(ImageManager.getImage("mode_icon"));
            mode.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doMode();
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        show_chart_mode = null;
        if (normal_mode && !ChartTab.getData().getShowAspects()
                && !ChartTab.getData().getShowHoriz()) {
            show_now = new MenuItem(menu, SWT.CHECK);
            show_now.setText(Resource.getString("pref_show") + "\tCtrl+R");
            show_now.setImage(ImageManager.getImage("chart_icon"));
            show_now.setAccelerator(SWT.CTRL + 'R');
            show_now.setSelection(ChartTab.getData().getShowNow());
            Moira.getChart().setShowNow(show_now.getSelection(), false, true);
            show_now.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doShowNow(false);
                }
            });
        } else if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            show_compass = new MenuItem(menu, SWT.CHECK);
            show_compass.setText(Resource.getString("pref_show_compass")
                    + "\tCtrl+R");
            show_compass.setImage(ImageManager.getImage("chart_icon"));
            show_compass.setAccelerator(SWT.CTRL + 'R');
            show_compass.setSelection(Resource.getPrefInt("show_compass") != 0);
            show_compass.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doShowCompass(false);
                }
            });
            if (Resource.getPrefInt("enable_fixstar") != 0) {
                show_fixstar = new MenuItem(menu, SWT.CHECK);
                show_fixstar.setText(Resource.getString("pref_show_fixstar"));
                show_fixstar.setImage(ImageManager.getImage("chart_icon"));
                show_fixstar.setSelection(ChartTab.getData().getShowFixstar());
                show_fixstar.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event)
                    {
                        doShowFixstar(false);
                    }
                });
            }
        } else if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            show_chart_mode = new MenuItem(menu, SWT.CHECK);
            show_chart_mode.setText(Resource.getString("pref_show_gauquelin")
                    + "\tCtrl+R");
            show_chart_mode.setImage(ImageManager.getImage("chart_icon"));
            show_chart_mode.setAccelerator(SWT.CTRL + 'R');
            show_chart_mode.setSelection(ChartTab.getData().getShowGauquelin());
            show_chart_mode.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doShowChartMode(false);
                }
            });
        }
        show_horiz = new MenuItem(menu, SWT.CHECK);
        show_horiz.setText(Resource.getString("pref_show_horiz") + "\tCtrl+P");
        show_horiz.setImage(ImageManager.getImage("horiz_icon"));
        show_horiz.setAccelerator(SWT.CTRL + 'P');
        show_horiz.setSelection(ChartTab.getData().getShowHoriz());
        show_horiz.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doShowHoriz(false);
            }
        });
        if (normal_mode) {
            show_aspects = new MenuItem(menu, SWT.CHECK);
            show_aspects.setText(Resource.getString("pref_show_aspects")
                    + "\tCtrl+L");
            show_aspects.setImage(ImageManager.getImage("aspects_icon"));
            show_aspects.setAccelerator(SWT.CTRL + 'L');
            show_aspects.setSelection(ChartTab.getData().getShowAspects());
            show_aspects.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    doShowAspects(false);
                }
            });
        }
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            final MenuItem explain_star = new MenuItem(menu, SWT.CHECK);
            explain_star.setText(Resource.getString("pref_explain_star")
                    + "\tCtrl+E");
            explain_star.setAccelerator(SWT.CTRL + 'E');
            explain_star.setSelection(Resource.getPrefInt("explain_star") != 0);
            explain_star.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    Resource.putPrefInt("explain_star",
                            explain_star.getSelection() ? 1 : 0);
                    Moira.update(false, false);
                }
            });
            new MenuItem(menu, SWT.SEPARATOR);
        }
        MenuItem pref_adj_time = new MenuItem(menu, SWT.NONE);
        pref_adj_time.setText(Resource.getString("pref_adj_time"));
        pref_adj_time.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                selectTimeAdjust();
            }
        });
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            MenuItem pref_life_self = new MenuItem(menu, SWT.NONE);
            pref_life_self.setText(Resource.getString("pref_life_self"));
            pref_life_self.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectLifeSelfMode();
                }
            });
        }
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            new MenuItem(menu, SWT.SEPARATOR);
            final MenuItem select_north = new MenuItem(menu, SWT.NONE);
            select_north.setText(Resource.getString("pref_select_north"));
            select_north.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectNorth();
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem show_tab = new MenuItem(menu, SWT.CASCADE);
        show_tab.setText(Resource.getString("pref_show_tab"));
        Menu show_tab_menu = new Menu(show_tab);
        show_tab.setMenu(show_tab_menu);
        show_data = new MenuItem(show_tab_menu, SWT.CHECK);
        show_data.setText(Resource.getString("pref_show_data"));
        show_data.setSelection(ChartTab.getTab(ChartTab.DATA_TAB)
                .isTabVisible());
        show_data.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.getChart().showHideTab(ChartTab.DATA_TAB,
                        show_data.getSelection(), true, true);
            }
        });
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            show_pole = new MenuItem(show_tab_menu, SWT.CHECK);
            show_pole.setText(Resource.getString("pref_show_pole"));
            show_pole.setSelection(ChartTab.getTab(ChartTab.POLE_TAB)
                    .isTabVisible());
            show_pole.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    if (!ChartMode.isAstroMode(ChartMode.NATAL_MODE)) {
                        boolean set = Resource.getPrefInt("show_pole") != 0;
                        if (set != show_pole.getSelection())
                            show_pole.setSelection(set);
                        return;
                    }
                    Moira.getChart().showHideTab(ChartTab.POLE_TAB,
                            show_pole.getSelection(), true, true);
                }
            });
        }
        show_note = new MenuItem(show_tab_menu, SWT.CHECK);
        show_note.setText(Resource.getString("pref_show_note"));
        show_note.setSelection(ChartTab.getTab(ChartTab.NOTE_TAB)
                .isTabVisible());
        show_note.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.getChart().showHideTab(ChartTab.NOTE_TAB,
                        show_note.getSelection(), true, true);
            }
        });
        show_table = new MenuItem(show_tab_menu, SWT.CHECK);
        show_table.setText(Resource.getString("pref_show_table"));
        show_table.setSelection(Moira.isTableVisible());
        show_table.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.showHideTable(show_table.getSelection(), false);
                updateMenu(null);
            }
        });
        MenuItem show_toolbar = new MenuItem(menu, SWT.CASCADE);
        show_toolbar.setText(Resource.getString("pref_show_toolbar"));
        Menu show_toolbar_menu = new Menu(show_toolbar);
        show_toolbar.setMenu(show_toolbar_menu);
        MenuItem show_file = new MenuItem(show_toolbar_menu, SWT.CHECK);
        show_file.setText(Resource.getString("pref_show_file"));
        show_file.setSelection(Resource.getPrefInt("show_file") != 0);
        show_file.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Resource.putPrefInt("show_file",
                        ((MenuItem) event.getSource()).getSelection() ? 1 : 0);
                FolderToolBar.updateFolderToolBar();
            }
        });
        MenuItem show_edit = new MenuItem(show_toolbar_menu, SWT.CHECK);
        show_edit.setText(Resource.getString("pref_show_edit"));
        show_edit.setSelection(Resource.getPrefInt("show_edit") != 0);
        show_edit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Resource.putPrefInt("show_edit",
                        ((MenuItem) event.getSource()).getSelection() ? 1 : 0);
                FolderToolBar.updateFolderToolBar();
            }
        });
        MenuItem show_pref = new MenuItem(show_toolbar_menu, SWT.CHECK);
        show_pref.setText(Resource.getString("pref_show_pref"));
        show_pref.setSelection(Resource.getPrefInt("show_pref") != 0);
        show_pref.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Resource.putPrefInt("show_pref",
                        ((MenuItem) event.getSource()).getSelection() ? 1 : 0);
                FolderToolBar.updateFolderToolBar();
            }
        });
        MenuItem show_find = new MenuItem(show_toolbar_menu, SWT.CHECK);
        show_find.setText(Resource.getString("pref_show_find") + "\tCtrl+F");
        show_find.setAccelerator(SWT.CTRL + 'F');
        show_find.setSelection(Resource.getPrefInt("show_find") != 0);
        show_find.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Resource.putPrefInt("show_find",
                        ((MenuItem) event.getSource()).getSelection() ? 1 : 0);
                FolderToolBar.updateFolderToolBar();
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        final MenuItem stars = new MenuItem(menu, SWT.NONE);
        stars.setText(Resource.getString(ChartMode
                .isChartMode(ChartMode.ASTRO_MODE) ? "pref_astro_star"
                : "pref_star"));
        stars.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                selectPlanets();
            }
        });
        angles = aspects = null;
        if (ChartTab.getData().getAngleMarkerArray(true, ChartTab.getUIMode(),
                0) != null) {
            angles = new MenuItem(menu, SWT.NONE);
            angles.setText(Resource.getString("pref_angles"));
            angles.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectAspects(true);
                }
            });
        }
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                || !ChartMode.isChartMode(ChartMode.PICK_MODE)
                && ChartTab.getData().getShowAspects()) {
            aspects = new MenuItem(menu, SWT.NONE);
            aspects.setText(Resource.getString("pref_aspects"));
            aspects.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectAspects(false);
                }
            });
        }
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            MenuItem systems = new MenuItem(menu, SWT.NONE);
            systems.setText(Resource.getString("pref_systems"));
            systems.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectSystems();
                }
            });
            MenuItem sidereal = new MenuItem(menu, SWT.NONE);
            sidereal.setText(Resource.getString("pref_sidereal"));
            sidereal.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectSiderealSystem();
                }
            });
            MenuItem relationship = new MenuItem(menu, SWT.NONE);
            relationship.setText(Resource.getString("pref_relationship"));
            relationship.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectRelationship();
                }
            });
        } else {
            MenuItem signs = new MenuItem(menu, SWT.NONE);
            signs.setText(Resource.getString("pref_sign"));
            signs.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectStarSigns();
                }
            });
            MenuItem styles = new MenuItem(menu, SWT.NONE);
            styles.setText(Resource.getString("pref_styles"));
            styles.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectStyles();
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem font_align = new MenuItem(menu, SWT.NONE);
        font_align.setText(Resource.getString("pref_font_align"));
        font_align.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                selectFontAlign();
            }
        });
        MenuItem font = new MenuItem(menu, SWT.NONE);
        font.setText(Resource.getString("pref_font"));
        font.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                selectFont();
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem color = new MenuItem(menu, SWT.NONE);
        color.setText(Resource.getString("pref_color"));
        color.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                selectColor();
            }
        });
        final MenuItem no_color = new MenuItem(menu, SWT.CHECK);
        no_color.setText(Resource.getString("pref_black_and_white"));
        no_color.setSelection(ChartTab.getData().getNoColor());
        no_color.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                ChartTab.getData().setNoColor(no_color.getSelection());
                Moira.getChart().updateColor();
                Moira.getChart().refresh(false);
            }
        });
        if (Moira.setTrayIcon(true)) {
            new MenuItem(menu, SWT.SEPARATOR);
            final MenuItem tray = new MenuItem(menu, SWT.CHECK);
            tray.setText(Resource.getString("pref_tray"));
            tray.setSelection(Resource.getPrefInt("tray_icon") != 0);
            Moira.setTrayIcon(false);
            tray.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    Resource.putPrefInt("tray_icon", tray.getSelection() ? 1
                            : 0);
                    Moira.setTrayIcon(false);
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        final MenuItem lang = new MenuItem(menu, SWT.NONE);
        final boolean is_simplified = Resource.isSimplified();
        lang.setText(Resource.getString("pref_lang_"
                + (is_simplified ? "traditional" : "simplified")));
        lang.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                ChartTab.hideTip();
                if (!Message.question(Resource.getString("dialog_pref_lang_"
                        + (is_simplified ? "traditional" : "simplified"))))
                    return;
                if (!Moira.getTable().checkForSave())
                    return;
                Resource.setSimplified(!is_simplified);
                Resource.removePref("master_display");
                Moira.exit(true);
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem clear = new MenuItem(menu, SWT.NONE);
        clear.setText(Resource.getString("pref_reset_all"));
        clear.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                ChartTab.hideTip();
                if (!Message.question(Resource
                        .getString("dialog_pref_reset_question")))
                    return;
                if (!Moira.getTable().checkForSave())
                    return;
                Resource.prefClear(true);
                Moira.exit(true);
            }
        });
    }

    private void addSearchMenu(Menu menu)
    {
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            MenuItem transits = new MenuItem(menu, SWT.NONE);
            transits.setText(Resource.getString("search_transits") + "\tCtrl+K");
            transits.setAccelerator(SWT.CTRL + 'K');
            transits.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectTransits(ChartMode.TRANSIT_MODE);
                }
            });
            MenuItem primary = new MenuItem(menu, SWT.NONE);
            primary.setText(Resource.getString("search_primary") + "\tCtrl+R");
            primary.setAccelerator(SWT.CTRL + 'R');
            primary.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectTransits(ChartMode.PRIMARY_DIRECTION_MODE);
                }
            });
            MenuItem secondary = new MenuItem(menu, SWT.NONE);
            secondary.setText(Resource.getString("search_secondary")
                    + "\tCtrl+L");
            secondary.setAccelerator(SWT.CTRL + 'L');
            secondary.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectTransits(ChartMode.SECONDARY_PROGRESSION_MODE);
                }
            });
            MenuItem solar_arc = new MenuItem(menu, SWT.NONE);
            solar_arc.setText(Resource.getString("search_solar_arc")
                    + "\tCtrl+E");
            solar_arc.setAccelerator(SWT.CTRL + 'E');
            solar_arc.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    selectTransits(ChartMode.SOLAR_ARC_MODE);
                }
            });
            new MenuItem(menu, SWT.SEPARATOR);
        } else if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            MenuItem azimuth = new MenuItem(menu, SWT.NONE);
            azimuth.setText(Resource.getString("search_azimuth") + "\tCtrl+L");
            azimuth.setAccelerator(SWT.CTRL + 'L');
            azimuth.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    searchAzimuth();
                }
            });
            new MenuItem(menu, SWT.SEPARATOR);
        }
        MenuItem eight_char = new MenuItem(menu, SWT.NONE);
        eight_char.setText(Resource.getString("search_eight_char"));
        eight_char.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                searchEightChar();
            }
        });
        MenuItem solar_eclipse = new MenuItem(menu, SWT.NONE);
        solar_eclipse.setText(Resource.getString("search_solar_eclipse"));
        solar_eclipse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                searchEclipse(true);
            }
        });
        MenuItem lunar_eclipse = new MenuItem(menu, SWT.NONE);
        lunar_eclipse.setText(Resource.getString("search_lunar_eclipse"));
        lunar_eclipse.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                searchEclipse(false);
            }
        });
        MenuItem aspect = new MenuItem(menu, SWT.NONE);
        aspect.setText(Resource.getString("search_aspects"));
        aspect.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                selectTransits(ChartMode.NATAL_MODE);
            }
        });
    }

    private void addWinMenu(Menu menu)
    {
        MenuItem update = new MenuItem(menu, SWT.NONE);
        update.setText(Resource.getString("win_update") + "\tF1");
        update.setAccelerator(SWT.F1);
        update.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.getChart().resetCities();
                Moira.getChart().update(true, false);
            }
        });
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            new MenuItem(menu, SWT.SEPARATOR);
            MenuItem current_time = new MenuItem(menu, SWT.NONE);
            current_time.setText(Resource.getString("win_current_time")
                    + "\tF2");
            current_time.setAccelerator(SWT.F2);
            current_time.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event)
                {
                    ChartTab tab = Moira.getChart();
                    setAstroMode(ChartMode.NATAL_MODE);
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        tab.setBirthDate(null);
                    } else {
                        tab.setNowDate(null);
                    }
                    tab.update(true, false);
                }
            });
        }
        new MenuItem(menu, SWT.SEPARATOR);
        alt_ui = new MenuItem(menu, SWT.CHECK);
        alt_ui.setText(Resource.getString("win_UI"));
        alt_ui.setSelection(Resource.getPrefInt("ui_mode") != 0);
        alt_ui.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.getChart().toggleUI();
            }
        });
        getExampleData();
        if (example_data != null
                && (ChartMode.isChartMode(ChartMode.TRADITIONAL_MODE) || ChartMode
                        .isChartMode(ChartMode.SIDEREAL_MODE))) {
            new MenuItem(menu, SWT.SEPARATOR);
            for (int i = 0; i < example_data.length; i++) {
                MenuItem example = new MenuItem(menu, SWT.CHECK);
                example.setText(example_data[i].title);
                example.setSelection(example_data[i].selected);
                example.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event)
                    {
                        ChartTab.hideTip();
                        MenuItem item = (MenuItem) event.getSource();
                        example_index = getExampleDataIndex(item.getText());
                        setExampleDataSelection(example_index,
                                item.getSelection());
                        if (example_data[example_index].selected) {
                            if (!Moira.getTable().checkForSave()) {
                                example_data[example_index].selected = false;
                                item.setSelection(example_data[example_index].selected);
                                return;
                            }
                            Resource.enableAlternatePref(true, -1);
                            Moira.loadData(
                                    example_data[example_index].file,
                                    getExampleSelectedIndex(),
                                    TabManager
                                            .tabOnTop(TabManager.TABLE_TAB_ORDER));
                        } else {
                            Resource.enableAlternatePref(false, -1);
                            Moira.loadData(null, -1, TabManager
                                    .tabOnTop(TabManager.TABLE_TAB_ORDER));
                            Resource.enableAlternatePref(false, 0);
                        }
                        updateOpenSaveButtons(!example_data[example_index].selected);
                    }
                });
            }
        }
    }

    private void setExampleDataSelection(int index, boolean set)
    {
        for (int i = 0; i < example_data.length; i++)
            example_data[i].selected = false;
        example_data[index].selected = set;
    }

    private void getExampleData()
    {
        if (example_data != null) {
            for (int i = 0; i < example_data.length; i++) {
                if (example_data[i].selected)
                    return;
            }
        }
        File dir = new File(Moira.getIO().getFileName(
                Resource.getString("example")));
        if (dir.canRead()) {
            String data_ext = (Resource.isSimplified() ? Resource.SIMPLIFIED_SUFFIX
                    : Resource.TRADITIONAL_SUFFIX)
                    + Resource.DATA_EXT;
            File[] array = dir.listFiles();
            LinkedList head = new LinkedList();
            for (int i = 0; i < array.length; i++) {
                File file = array[i];
                String file_name = file.getName();
                if (!file_name.endsWith(data_ext))
                    continue;
                file_name = file.getPath();
                FileIO example = new FileIO(file_name, "pref", false);
                if (example != null) {
                    if (example.hasKey("pref")) {
                        int priority = 0, mode = -1;
                        String name = null;
                        StringTokenizer st = new StringTokenizer(
                                example.getString("pref"), "|");
                        while (st.hasMoreTokens()) {
                            String str = st.nextToken();
                            StringTokenizer nst = new StringTokenizer(str, ":");
                            if (nst.countTokens() == 2) {
                                String key = nst.nextToken().trim();
                                String info = nst.nextToken().trim();
                                if (key.equalsIgnoreCase("chart_mode"))
                                    mode = FileIO.parseInt(info, -1, true);
                                else if (key.equalsIgnoreCase("priority"))
                                    priority = FileIO.parseInt(info, 0, true);
                                else if (key
                                        .equalsIgnoreCase("alternate_title"))
                                    name = info;
                            }
                        }
                        if (name != null
                                && ChartMode.modeToGroup(mode) == ChartMode
                                        .modeToGroup(ChartMode.getChartMode())) {
                            for (ListIterator iter = head.listIterator(); iter
                                    .hasNext();) {
                                ExampleData data = (ExampleData) iter.next();
                                if (data.title.equalsIgnoreCase(name)) {
                                    if (data.priority < priority) {
                                        data.priority = priority;
                                        data.file = file_name;
                                    }
                                    mode = -1;
                                    break;
                                }
                            }
                            if (mode >= 0)
                                head.addLast(new ExampleData(name, file_name,
                                        priority));
                        }
                    }
                    example.dispose();
                }
            }
            example_data = (head.size() == 0) ? null : ((ExampleData[]) head
                    .toArray(new ExampleData[head.size()]));
        }
    }

    private int getExampleDataIndex(String file_title)
    {
        for (int i = 0; i < example_data.length; i++) {
            if (file_title.equals(example_data[i].title))
                return i;
        }
        return -1;
    }

    private void addHelpMenu(Menu menu)
    {
        final MenuItem overview = new MenuItem(menu, SWT.NONE);
        overview.setText(Resource.getString("help_overview"));
        overview.setImage(ImageManager.getImage("help_icon"));
        overview.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                doHelp();
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);
        MenuItem about = new MenuItem(menu, SWT.NONE);
        about.setText(Resource.getString("help_about") + "  "
                + ChartMode.getModeTitle() + " - " + Resource.NAME);
        about.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                showHTML("about_file", "help_about", true, false);
            }
        });
    }

    public void updateMenu(Menu[] menu)
    {
        if (menu == null)
            menu = sub_menu;
        removeAllMenuItems(menu[SUB_FILE], false);
        removeAllMenuItems(menu[SUB_EDIT], false);
        removeAllMenuItems(menu[SUB_PREF], true);
        removeAllMenuItems(menu[SUB_SEARCH], false);
        removeAllMenuItems(menu[SUB_WIN], false);
        removeAllMenuItems(menu[SUB_HELP], false);
        addFileMenu(menu[SUB_FILE]);
        addEditMenu(menu[SUB_EDIT]);
        addPrefMenu(menu[SUB_PREF]);
        addSearchMenu(menu[SUB_SEARCH]);
        addWinMenu(menu[SUB_WIN]);
        addHelpMenu(menu[SUB_HELP]);
        FolderToolBar.updateFolderBarState();
    }

    private void updateOpenSaveButtons(boolean enable)
    {
        Moira.getTable().updateButtonState(enable);
        FolderToolBar.updateOpenSaveState(enable);
        open.setEnabled(enable);
        save.setEnabled(enable);
        save_as.setEnabled(enable);
    }

    static public void updateChartModeMenu(boolean enable, boolean gauquelin)
    {
        if (show_chart_mode == null)
            return;
        show_chart_mode.setEnabled(enable);
        show_chart_mode.setSelection(ChartMode.isSingleWheelMode());
        show_chart_mode.setText(Resource
                .getString(gauquelin ? "pref_show_gauquelin"
                        : "pref_show_single")
                + "\tCtrl+R");
    }

    static public void updateHorizMenu(boolean enable)
    {
        show_horiz.setEnabled(enable);
    }

    static public void removeAllMenuItems(Menu menu, boolean keep_first)
    {
        if (menu.getItemCount() == 0)
            return;
        MenuItem[] items = menu.getItems();
        int last = keep_first ? 1 : 0;
        for (int i = items.length - 1; i >= last; i--)
            items[i].dispose();
    }

    private void selectTimeAdjust()
    {
        ChartTab.hideTip();
        TimeDialog dialog = new TimeDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updateTime()) {
            ChartTab.getData().setTimeAdjust(
                    Resource.getPrefInt("longitude_adjust"));
            Moira.getChart().setDstAdjust(
                    Resource.getPrefInt("dst_adjust") != 0);
            Moira.update(false, false);
        }
        dialog.close();
    }

    private void selectLifeSelfMode()
    {
        ChartTab.hideTip();
        LifeSelfDialog dialog = new LifeSelfDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updateLifeSelf())
            Moira.update(false, false);
        dialog.close();
    }

    private void selectFontAlign()
    {
        ChartTab.hideTip();
        FontAlignDialog dialog = new FontAlignDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updateFontAlign())
            Moira.update(false, false);
        dialog.close();
    }

    private void selectFont()
    {
        ChartTab.hideTip();
        FontDialog dialog = new FontDialog(Moira.getShell());
        String font_name = FontMap.getSwtFontName();
        FontData[] data = new FontData[1];
        data[0] = new FontData(font_name, Resource.getSwtDataFontSize(),
                getSwtFontStyle());
        dialog.setFontList(data);
        dialog.open();
        data = dialog.getFontList();
        if (data == null
                || (data[0].getName().equals(font_name)
                        && data[0].getStyle() == getSwtFontStyle() && data[0]
                        .getHeight() == Resource.getSwtDataFontSize())
                || !Resource.isFontAcceptable(data[0].getName(), null))
            return;
        FontMap.setSwtFontName(data[0].getName());
        setSwtFontStyle(data[0].getStyle());
        Resource.setSwtDataFontSize(data[0].getHeight());
        DrawAWT.resetFontTable();
        Moira.getChart().refresh(false);
        Moira.getChart().updateAttribute();
        Moira.getTable().updateFont();
    }

    private void setSwtFontStyle(int style)
    {
        int awt_style;
        switch (style) {
            case SWT.BOLD:
                awt_style = java.awt.Font.BOLD;
                break;
            case SWT.ITALIC:
                awt_style = java.awt.Font.ITALIC;
                break;
            default:
                awt_style = java.awt.Font.PLAIN;
                break;
        }
        Resource.setFontStyle(awt_style);
    }

    private void selectColor()
    {
        ChartTab.hideTip();
        ColorFieldDialog dialog = new ColorFieldDialog(Moira.getShell());
        dialog.setPrefix(ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_"
                : "");
        dialog.setGroupName(ColorFieldDialog.TAB_FONT,
                Resource.getString("dialog_color_group_tab_font"));
        dialog.addList(ColorFieldDialog.TAB_FONT, "data_font_color");
        dialog.addList(ColorFieldDialog.TAB_FONT, "pole_font_color");
        if (ChartTab.getTab(ChartTab.EVAL_TAB).isTabVisible())
            dialog.addList(ColorFieldDialog.TAB_FONT, "eval_font_color");
        dialog.addList(ColorFieldDialog.TAB_FONT, "note_font_color");
        dialog.addList(ColorFieldDialog.TAB_FONT, "table_font_color");
        dialog.setGroupName(ColorFieldDialog.TAB_BACKGROUND,
                Resource.getString("dialog_color_group_tab_background"));
        dialog.addList(ColorFieldDialog.TAB_BACKGROUND, "data_background_color");
        dialog.addList(ColorFieldDialog.TAB_BACKGROUND, "pole_background_color");
        if (ChartTab.getTab(ChartTab.EVAL_TAB).isTabVisible())
            dialog.addList(ColorFieldDialog.TAB_BACKGROUND,
                    "eval_background_color");
        dialog.addList(ColorFieldDialog.TAB_BACKGROUND, "note_background_color");
        dialog.addList(ColorFieldDialog.TAB_BACKGROUND,
                "table_background_color");
        dialog.setGroupName(ColorFieldDialog.FOREGROUND,
                Resource.getString("dialog_color_group_fore"));
        dialog.addList(ColorFieldDialog.FOREGROUND, "chart_base_ring_fg_color");
        dialog.addList(ColorFieldDialog.FOREGROUND, "chart_now_ring_fg_color");
        dialog.addList(ColorFieldDialog.FOREGROUND, "chart_birth_ring_fg_color");
        dialog.addList(ColorFieldDialog.FOREGROUND,
                "chart_life_master_fg_color");
        dialog.setGroupName(ColorFieldDialog.BACKGROUND,
                Resource.getString("dialog_color_group_back"));
        dialog.addList(ColorFieldDialog.BACKGROUND, "chart_base_ring_bg_color");
        dialog.addList(ColorFieldDialog.BACKGROUND, "chart_now_ring_bg_color");
        dialog.addList(ColorFieldDialog.BACKGROUND, "chart_birth_ring_bg_color");
        dialog.addList(ColorFieldDialog.BACKGROUND, "chart_window_bg_color");
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            dialog.addList(ColorFieldDialog.BACKGROUND, "chart_asc_bg_color");
            dialog.addList(ColorFieldDialog.BACKGROUND, "chart_mc_bg_color");
        }
        ChartData data = ChartTab.getData();
        int num_speed_color = ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? 2
                : 0;
        String[] speed_state = data.getSpeedColorNameArray();
        dialog.setGroupName(
                ColorFieldDialog.BIRTH_SPEED,
                Resource.getString(dialog.getPrefix()
                        + "chart_birth_ring_speed"));
        int[] birth_speed_color = data.getSpeedColorArray(false);
        dialog.addList(ColorFieldDialog.BIRTH_SPEED, speed_state,
                birth_speed_color, num_speed_color);
        int[] now_color;
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            dialog.setGroupName(ColorFieldDialog.NOW_SPEED,
                    Resource.getString("chart_now_ring_state"));
            now_color = data.getStateColorArray();
            dialog.addList(ColorFieldDialog.NOW_SPEED, Resource
                    .getStringArray("alt_state"), now_color, data
                    .getShowFixstar() ? now_color.length
                    : (now_color.length - 1));
        } else {
            dialog.setGroupName(
                    ColorFieldDialog.NOW_SPEED,
                    Resource.getString(dialog.getPrefix()
                            + "chart_now_ring_speed"));
            now_color = data.getSpeedColorArray(true);
            dialog.addList(ColorFieldDialog.NOW_SPEED, speed_state, now_color,
                    num_speed_color);
        }
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            dialog.setGroupName(ColorFieldDialog.MARKER,
                    Resource.getString("dialog_color_group_mark"));
            dialog.addList(ColorFieldDialog.MARKER,
                    "chart_life_master_mk_color");
            if (Resource.getPrefInt("self_mode") != 0) {
                dialog.addList(ColorFieldDialog.MARKER,
                        "chart_self_master_mk_color");
            }
            dialog.addList(
                    ColorFieldDialog.MARKER,
                    ChartMode.isChartMode(ChartMode.PICK_MODE) ? "chart_mountain_line_mk_color"
                            : "chart_now_line_mk_color");
            dialog.addList(ColorFieldDialog.MARKER, "chart_weak_line_mk_color");
            dialog.addList(ColorFieldDialog.MARKER, "chart_solid_line_mk_color");
        } else {
            dialog.setGroupName(ColorFieldDialog.MARKER,
                    Resource.getString("dialog_color_group_axis"));
            dialog.addList(ColorFieldDialog.MARKER, "chart_asc_mk_color");
            dialog.addList(ColorFieldDialog.MARKER, "chart_mc_mk_color");
        }
        dialog.setGroupName(ColorFieldDialog.CONNECT,
                Resource.getString("dialog_color_group_connect"));
        dialog.addList(ColorFieldDialog.CONNECT, "chart_now_degree_mk_color");
        dialog.addList(ColorFieldDialog.CONNECT, "chart_birth_degree_mk_color");
        dialog.setGroupName(ColorFieldDialog.LINE,
                Resource.getString("dialog_color_group_line"));
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            dialog.addList(ColorFieldDialog.LINE, "chart_house_line_mk_color");
            dialog.addList(ColorFieldDialog.LINE, "chart_house_system_mk_color");
        }
        dialog.addList(ColorFieldDialog.LINE, "chart_ring_mk_color");
        if (ChartTab.getData().getShowHoriz())
            dialog.addList(ColorFieldDialog.LINE, "horiz_chart_mk_color");
        dialog.setGroupName(
                ColorFieldDialog.ASPECT,
                Resource.getString((angles != null && aspects != null) ? "dialog_color_group_aspect_angle"
                        : ((angles != null) ? "dialog_color_group_angle"
                                : "dialog_color_group_aspect")));
        boolean angle = !ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                && (ChartMode.isChartMode(ChartMode.PICK_MODE) || !ChartTab
                        .getData().getShowAspects());
        String[] sign_array = data.getAspectSignArray(angle);
        int[] color_array = data.getAspectColorArray(angle);
        dialog.addList(ColorFieldDialog.ASPECT, sign_array, color_array, 0);
        if (dialog.open() == Window.OK) {
            if (dialog.updateColor(false)) {
                data.setSpeedColorArray(null, 0, false);
                data.setSpeedColorArray(null, 0, true);
                data.setStateColorArray(null);
                data.setAspectColorArray(null, angle);
            } else {
                data.setSpeedColorArray(birth_speed_color, num_speed_color,
                        false);
                if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    data.setStateColorArray(now_color);
                } else {
                    data.setSpeedColorArray(now_color, num_speed_color, true);
                }
                data.setAspectColorArray(color_array, angle);
            }
            ChartTab.getTab(ChartTab.DATA_TAB).updateAttribute(false);
            ChartTab.getTab(ChartTab.POLE_TAB).updateAttribute(false);
            if (ChartTab.getTab(ChartTab.EVAL_TAB).isTabVisible())
                ChartTab.getTab(ChartTab.EVAL_TAB).updateAttribute(false);
            ChartTab.getTab(ChartTab.NOTE_TAB).updateAttribute(false);
            Moira.getTable().setColor();
            Moira.getChart().updateColor();
            Moira.getChart().updateAngleMarker(-1, -1);
            Moira.update(false, false);
        }
        dialog.dispose();
        dialog.close();
    }

    public void enterName(boolean check)
    {
        ChartTab tab = Moira.getChart();
        tab.resetCities();
        if (check && (!tab.getName().equals("") || ChartTab.getUIMode())) {
            if (ChartMode.isAstroMode(ChartMode.NATAL_MODE))
                tab.removeAstroControl();
            Moira.update(false, false);
            if (ChartMode.isAstroMode(ChartMode.NATAL_MODE))
                Moira.getTable().addCurrentEntry(false);
            return;
        }
        ChartTab.hideTip();
        NameDialog dialog = new NameDialog(Moira.getShell());
        dialog.setName(tab.getName());
        dialog.setSex(tab.getSex());
        if (dialog.open() == Window.OK) {
            String str = dialog.getName().trim();
            boolean sex = dialog.getSex();
            ChartTab.getTab(ChartTab.DATA_TAB).setName(str, sex, true);
            tab.setName(str);
            if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                    || tab.getSex() != sex) {
                tab.setSex(sex);
                tab.removeAstroControl();
                Moira.update(false, false);
                Moira.getTable().addCurrentEntry(false);
            } else {
                if (check) {
                    Moira.update(false, false);
                    Moira.getTable().addCurrentEntry(false);
                } else {
                    tab.refresh(true);
                    Moira.updateOverride();
                    Moira.getTable().addCurrentEntry(true);
                }
            }
        }
        dialog.close();
    }

    public void enterFooter()
    {
        ChartTab.hideTip();
        FooterDialog dialog = new FooterDialog(Moira.getShell());
        if (dialog.open() == Window.OK)
            dialog.updateFooter();
        dialog.close();
    }

    public void saveSetting()
    {
        ChartTab.hideTip();
        SaveControlDialog dialog = new SaveControlDialog(Moira.getShell());
        if (dialog.open() == Window.OK)
            dialog.updateSaveControl();
        dialog.close();
    }

    private void enterHouse()
    {
        ChartTab.hideTip();
        HouseDialog dialog = new HouseDialog(Moira.getShell());
        dialog.setHouse(ChartTab.getData().getHouse());
        dialog.setDayOrNight(ChartTab.getData().getDayOrNight());
        if (dialog.open() == Window.OK) {
            ChartTab.getData().setHouse(dialog.getHouse());
            ChartTab.getData().setDayOrNight(dialog.getDayOrNight(), true);
            Moira.update(false, false);
            Moira.getTable().addCurrentEntry(false);
        }
        dialog.close();
    }

    private void enterAdjustment()
    {
        ChartTab.hideTip();
        EditDialog dialog = new EditDialog(Moira.getShell());
        ChartData data = ChartTab.getData();
        boolean[] flip_array = data.getSignRevFlipArray();
        double[] degree_array = data.getSignDegreeShiftArray();
        double[] cusp_array = (!ChartMode.isChartMode(ChartMode.ASTRO_MODE) && Resource
                .getPrefInt("show_house_system") != 0) ? data.getCuspArray()
                : null;
        dialog.setSignArray(data.getSignArray(), degree_array, flip_array,
                cusp_array, data.getEightCharOverride(),
                data.getSignDisplayArray());
        if (dialog.open() == Window.OK && dialog.updateEdit()) {
            data.setSignDegreeShiftArray(degree_array);
            data.setSignRevFlipArray(flip_array);
            if (cusp_array != null)
                data.setCuspArray(cusp_array);
            data.setEightCharOverride(dialog.getEightChar());
            Moira.update(false, false);
            Moira.getTable().addCurrentEntry(false);
        }
        dialog.close();
    }

    private void selectNorth()
    {
        ChartTab.hideTip();
        PickDialog dialog = new PickDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updatePick()) {
            Moira.getChart().updateAdjNorth(null);
            Moira.update(false, false);
        }
        dialog.close();
    }

    private void selectPlanets()
    {
        ChartTab.hideTip();
        PlanetDialog dialog = new PlanetDialog(Moira.getShell());
        ChartData data = ChartTab.getData();
        int[] array = data.getSignDisplayArray();
        dialog.setSignArray(data.getSignArray(), data.getOrderArray(), array);
        boolean need_update = dialog.open() == Window.OK;
        if (need_update) {
            boolean asteroid_changed = dialog.updateInfluence();
            data.setSignDisplayArray(array);
            ChartTab.getData().setTopocentricMode();
            if (asteroid_changed)
                Moira.updateModEval();
        }
        dialog.close();
        if (need_update)
            Moira.update(false, false);
    }

    private void selectStyles()
    {
        ChartTab.hideTip();
        StyleDialog dialog = new StyleDialog(Moira.getShell());
        if (dialog.open() == Window.OK) {
            dialog.updateStyle();
            Moira.update(false, false);
        }
        dialog.close();
    }

    private void selectAspects(boolean angle)
    {
        ChartTab.hideTip();
        AspectDialog dialog = new AspectDialog(Moira.getShell(), angle);
        ChartData data = ChartTab.getData();
        if (!angle)
            data.initAspects();
        String[] sign_array = data.getAspectSignArray(angle);
        int[] aspect_array = data.getAspectDisplayArray(angle);
        double[] degree_array = data.getAspectDegreeArray(angle);
        double[] orb_array = angle ? null : data.getAspectOrbArray();
        boolean[] enable = angle ? data.getAngleMarkerEnable() : null;
        dialog.setSignArray(sign_array, degree_array, orb_array, aspect_array,
                angle ? "dialog_enable_angles" : null, enable);
        if (dialog.open() == Window.OK) {
            if (!angle) {
                data.setAspectSignArray(sign_array);
                data.setAspectDegreeArray(degree_array);
                data.setAspectOrbArray(orb_array);
            }
            data.setAspectDisplayArray(aspect_array, angle);
            if (angle) {
                data.setAngleMarkerEnable(enable);
                Moira.getChart().updateAngleMarker(-1, -1);
            }
            Moira.update(false, false);
        }
        dialog.close();
    }

    private void selectTransits(int transit_mode)
    {
        ChartTab.hideTip();
        TransitDialog dialog = new TransitDialog(Moira.getShell());
        dialog.setTransitMode(transit_mode);
        if (dialog.open() == Window.OK) {
            if (dialog.updateTransit()) {
                showTransits(transit_mode);
            } else {
                int[] date = new int[5];
                BaseCalendar.auditDay(Resource.getPrefString("transit_date"),
                        date);
                BaseCalendar.addZoneOffset(date, false);
                Moira.getChart().setTransitTime(transit_mode,
                        Calculate.getJulianDayUT(date), false, false);
            }
        }
        dialog.close();
    }

    static private void showTransits(int show_mode)
    {
        disposeSubWin(data_shell);
        String[] data = Moira.getChart().getTransitData(show_mode);
        if (data == null)
            return;
        final Browser browser = openDataShell(data[0], FileIO.getURL(data[1])
                .toString(), "transit_");
        final int transit_mode = show_mode;
        final String country = data[2];
        final String city = data[3];
        final String zone = data[4];
        browser.addLocationListener(new LocationAdapter() {
            public void changing(LocationEvent event)
            {
                try {
                    String str = HTMLData.extractData(event.location);
                    double val = Double.parseDouble(str);
                    event.doit = false;
                    if (transit_mode == ChartMode.NATAL_MODE) {
                        Moira.getChart()
                                .setTime(
                                        val,
                                        country,
                                        city,
                                        zone,
                                        !(ChartMode
                                                .isChartMode(ChartMode.ASTRO_MODE) || ChartMode
                                                .isChartMode(ChartMode.PICK_MODE)),
                                        false);
                    } else {
                        Moira.getChart().setTransitTime(transit_mode, val,
                                country, city, zone, false, false);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        data_shell.open();
        data_shell.setActive();
    }

    private void searchEightChar()
    {
        ChartTab.hideTip();
        EightCharDialog dialog = new EightCharDialog(Moira.getShell());
        if (dialog.open() == Window.OK)
            findPoleDate(dialog.getYear(), dialog.getEightChar());
        dialog.close();
    }

    private void searchEclipse(boolean solar)
    {
        ChartTab.hideTip();
        SearchDialog dialog = new SearchDialog(Moira.getShell());
        String mode_prefix = solar ? "eclipse_solar" : "eclipse_lunar";
        dialog.setSearchMode(mode_prefix, false, true, false);
        if (dialog.open() == Window.OK) {
            dialog.updateSearch();
            showSearchResult(mode_prefix, true);
        }
        dialog.close();
    }

    private void searchAzimuth()
    {
        ChartTab.hideTip();
        SearchDialog dialog = new SearchDialog(Moira.getShell());
        dialog.setSearchMode("azimuth", true, false, true);
        if (dialog.open() == Window.OK) {
            dialog.updateSearch();
            showSearchResult("azimuth", false);
        }
        dialog.close();
    }

    static private void showSearchResult(String prefix, boolean local)
    {
        disposeSubWin(data_shell);
        String[] data = Moira.getChart().getSearchResult(prefix, local);
        if (data == null)
            return;
        final Browser browser = openDataShell(data[0], FileIO.getURL(data[1])
                .toString(), prefix + "_");
        final String country = data[2];
        final String city = data[3];
        final String zone = data[4];
        browser.addLocationListener(new LocationAdapter() {
            public void changing(LocationEvent event)
            {
                try {
                    String str = HTMLData.extractData(event.location);
                    event.doit = false;
                    MenuFolder.setAstroMode(ChartMode.NATAL_MODE);
                    StringTokenizer st = new StringTokenizer(str, "|");
                    int n_tok = st.countTokens();
                    if (n_tok > 1) {
                        double ut = Double.parseDouble(st.nextToken().trim());
                        String t_city, t_country, t_zone;
                        if (n_tok == 2) {
                            int index = FileIO.parseInt(st.nextToken().trim(),
                                    0, true);
                            City c = City.getCity(index);
                            t_city = c.getCityName();
                            t_country = c.getCountryName();
                            t_zone = c.getZoneName();
                        } else {
                            t_city = st.nextToken().trim();
                            int index = FileIO.parseInt(st.nextToken().trim(),
                                    -1, false);
                            if (index >= 0) {
                                City c = City.getCity(index);
                                t_country = c.getCountryName();
                                t_zone = c.getZoneName();
                            } else {
                                t_country = City.getDefaultCountry();
                                t_zone = "GMT";
                            }
                        }
                        Moira.getChart().setTime(ut, t_country, t_city, t_zone,
                                false, false);
                    } else {
                        Moira.getChart().setTime(Double.parseDouble(str),
                                country, city, zone, false, false);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        data_shell.open();
        data_shell.setActive();
    }

    private void enterLunarDate()
    {
        int[] date = null;
        boolean leap = false;
        ChartTab.hideTip();
        LunarDialog dialog = new LunarDialog(Moira.getShell());
        if (dialog.open() == Window.OK) {
            date = dialog.getLunarDate();
            leap = dialog.isLeapMonth();
        }
        dialog.close();
        if (date != null) {
            if (Moira.getChart().setDateFromLunarDate(date, leap))
                Moira.getChart().moveToField(false, false);
            else
                Message.warn(Resource
                        .getString("dialog_lunar_date_search_fail"));
        }
    }

    static private void findPoleDate(int year, String eight_char)
    {
        disposeSubWin(data_shell);
        String[] data = ChartTab.getData().getPoleDateData(year, eight_char);
        if (data == null) {
            Message.warn(Resource.getString("dialog_eight_char_search_fail"));
            return;
        }
        final Browser browser = openDataShell(data[0], FileIO.getURL(data[1])
                .toString(), "eight_char_");
        browser.addLocationListener(new LocationAdapter() {
            public void changing(LocationEvent event)
            {
                try {
                    String str = HTMLData.extractData(event.location);
                    double val = Double.parseDouble(str);
                    event.doit = false;
                    Moira.getChart().setEightCharTime(val);
                } catch (NumberFormatException e) {
                }
            }
        });
        data_shell.open();
        data_shell.setActive();
    }

    static private Browser openDataShell(String title, String url_path,
            String prefix)
    {
        data_shell = new Shell(Display.getCurrent());
        data_shell.setLayout(new GridLayout());
        Browser browser = new Browser(data_shell, SWT.NONE);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        browser.setUrl(url_path);
        data_shell.setText(title + " - " + Resource.NAME);
        data_shell.setImage(Moira.getShell().getImage());
        final String bound_prefix = prefix;
        data_shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e)
            {
                Moira.saveShellBounds(data_shell, bound_prefix);
                data_shell = null;
            }
        });
        Moira.setShellPosition(data_shell, bound_prefix);
        return browser;
    }

    private void selectSystems()
    {
        ChartTab.hideTip();
        SystemDialog dialog = new SystemDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updateSystem())
            Moira.update(false, false);
        dialog.close();
    }

    private void selectSiderealSystem()
    {
        ChartTab.hideTip();
        SiderealDialog dialog = new SiderealDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updateSiderealSystem())
            Moira.update(false, false);
        dialog.close();
    }

    private void selectRelationship()
    {
        ChartTab.hideTip();
        RelationshipDialog dialog = new RelationshipDialog(Moira.getShell());
        if (dialog.open() == Window.OK && dialog.updateRelationship())
            Moira.update(false, false);
        dialog.close();
    }

    private void selectStarSigns()
    {
        ChartTab.hideTip();
        StarSignDialog dialog = new StarSignDialog(Moira.getShell());
        dialog.setDisplayTable(ChartTab.getData().getDisplayTable());
        if (dialog.open() == Window.OK) {
            ChartTab.getData().updateDisplayTable(dialog.getDisplayTable());
            Moira.update(false, false);
        }
        dialog.close();
    }

    public void doBlank()
    {
        ChartTab.hideTip();
        Moira.getTable().newEntry();
        if (TabManager.tabOnTop(TabManager.TABLE_TAB_ORDER))
            return;
        if (!TabManager.tabOnTop(TabManager.CHART_TAB_ORDER))
            TabManager.setTabOnTop(TabManager.CHART_TAB_ORDER);
        Moira.getChart().moveToField(true, true);
    }

    public void doOpen()
    {
        Moira.getTable().openFile();
    }

    public void doSave(boolean last)
    {
        ChartTab.hideTip();
        Moira.getTable().saveFile(null, last);
    }

    public void doPrint()
    {
        ChartTab.hideTip();
        if (Moira.getChart().hasAstroData())
            Moira.getChart().update(false, true);
        Print printer = new Print();
        DataTab eval_tab = ChartTab.getTab(ChartTab.EVAL_TAB);
        String eval_data = eval_tab.getTextOnly().trim();
        if (!eval_tab.isTabVisible() || eval_data.equals(""))
            eval_data = null;
        DataTab note_tab = ChartTab.getTab(ChartTab.NOTE_TAB);
        String note_data = note_tab.getNote(true);
        if (!note_tab.hasValidNote(note_data))
            note_data = null;
        printer.printPage(eval_tab, eval_data, note_tab, note_data);
    }

    private void captureSetting()
    {
        ChartTab.hideTip();
        ImageControlDialog dialog = new ImageControlDialog(Moira.getShell());
        if (dialog.open() == Window.OK) {
            dialog.saveImageSize();
        }
        dialog.close();
    }

    public void doCapture()
    {
        ChartTab.hideTip();
        FileDialog dialog = new FileDialog(Moira.getShell(), SWT.SAVE);
        dialog.setFilterExtensions(ImageControl.IMAGE_EXTENSIONS);
        String path_name = Resource.hasPrefKey("last_image_path") ? Resource
                .getPrefString("last_image_path") : null;
        if (path_name == null && Resource.hasPrefKey("last_open_path"))
            path_name = Resource.getPrefString("last_open_path");
        if (path_name != null)
            dialog.setFilterPath(path_name);
        if (dialog.open() == null)
            return;
        String file_name = dialog.getFileName().trim();
        if (file_name == null || file_name.equals(""))
            return;
        path_name = dialog.getFilterPath();
        if (captureImage(path_name, file_name, true))
            Resource.putPrefString("last_image_path", path_name);
    }

    public boolean captureImage(String path_name, String file_name,
            boolean check)
    {
        File capture_file = new File(path_name + File.separator + file_name);
        if (check
                && capture_file.exists()
                && !Message.question(Resource
                        .getString("dialog_save_as_question"))) {
            return false;
        }
        if (Moira.getChart().hasAstroData())
            Moira.getChart().update(false, true);
        String format = null;
        for (int i = 0; i < ImageControl.IMAGE_EXTENSIONS.length; i++) {
            if (file_name.toLowerCase()
                    .endsWith(
                            ImageControl.IMAGE_EXTENSIONS[i].substring(1)
                                    .toLowerCase())) {
                format = ImageControl.IMAGE_EXTENSIONS[i].substring(2);
                break;
            }
        }
        if (format == null) {
            format = ImageControl.IMAGE_EXTENSIONS[0].substring(2);
            capture_file = new File(path_name + File.separator + file_name
                    + "." + format);
        }
        BufferedImage image = ImageControl.captureImage(ImageControlDialog
                .getImageSize());
        try {
            ImageIO.write(image, format, capture_file);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void doUndo()
    {
        Moira.postKeyEvent('z', SWT.CONTROL);
    }

    public void doRedo()
    {
        Moira.postKeyEvent('y', SWT.CONTROL);
    }

    public void doCopy()
    {
        Moira.postKeyEvent('c', SWT.CONTROL);
    }

    public void doCut()
    {
        Moira.postKeyEvent('x', SWT.CONTROL);
    }

    public void doPaste()
    {
        Moira.postKeyEvent('v', SWT.CONTROL);
    }

    public void doDelete()
    {
        Moira.postKeyEvent(SWT.DEL, 0);
    }

    public void doMode()
    {
        FolderToolBar.resetSearch();
        int old_chart_mode = ChartMode.getChartMode();
        int old_house_mode = Resource.getPrefInt("house_mode");
        int old_adjust_mode = Resource.getPrefInt("adjust_mode");
        int new_chart_mode = selectChartMode(false, Moira.getShell());
        int new_house_mode = Resource.getPrefInt("house_mode");
        int new_adjust_mode = Resource.getPrefInt("adjust_mode");
        if (old_chart_mode != new_chart_mode
                || old_house_mode != new_house_mode
                || old_adjust_mode != new_adjust_mode) {
            // may not be able to change chart mode
            Moira.setNoUpdate(true);
            disposeSubWin();
            ChartMode.setChartMode(new_chart_mode);
            ChartMode.setChartMode();
            setChartMode(ChartMode.getChartMode());
            Moira.setNoUpdate(false);
            Moira.update(TabManager.tabOnTop(TabManager.TABLE_TAB_ORDER), true);
        }
    }

    public void doShowNow(boolean toogle)
    {
        if (!ChartMode.isChartMode(ChartMode.PICK_MODE)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                && !ChartTab.getData().getShowAspects()) {
            if (toogle)
                show_now.setSelection(!show_now.getSelection());
            Resource.putPrefInt("show_now", show_now.getSelection() ? 1 : 0);
            Moira.getChart().setShowNow(show_now.getSelection(), false, false);
        }
    }

    public void doShowCompass(boolean toogle)
    {
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            if (toogle)
                show_compass.setSelection(!show_compass.getSelection());
            Resource.putPrefInt("show_compass", show_compass.getSelection() ? 1
                    : 0);
            Moira.update(false, false);
        }
    }

    public void doShowFixstar(boolean toogle)
    {
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)
                && Resource.getPrefInt("enable_fixstar") != 0) {
            if (toogle)
                show_fixstar.setSelection(!show_fixstar.getSelection());
            ChartTab.getData().setShowFixstar(show_fixstar.getSelection());
            Moira.update(false, false);
        }
    }

    public void doShowChartMode(boolean toogle)
    {
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (toogle)
                show_chart_mode.setSelection(!show_chart_mode.getSelection());
            if (ChartMode.isAstroMode(ChartMode.NATAL_MODE)) {
                ChartTab.getData().setShowGauquelin(
                        show_chart_mode.getSelection());
                setAstroMode(ChartMode.NATAL_MODE);
                Moira.getChart().removeAstroControl();
            } else {
                Moira.getChart().setSingleWheelMode(
                        show_chart_mode.getSelection());
            }
            Moira.update(false, false);
        }
    }

    public void doShowHoriz(boolean toogle)
    {
        if (toogle)
            show_horiz.setSelection(!show_horiz.getSelection());
        ChartTab.getData().setShowHoriz(show_horiz.getSelection());
        boolean enable_now = !ChartMode.isChartMode(ChartMode.PICK_MODE)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                && !show_horiz.getSelection() && !show_aspects.getSelection()
                && Resource.getPrefInt("show_now") != 0;
        Moira.getChart().setShowNow(enable_now, true, false);
        updateMenu(null);
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            Moira.getChart().removeAstroControl();
    }

    public void doShowAspects(boolean toogle)
    {
        if (!ChartMode.isChartMode(ChartMode.PICK_MODE)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (toogle)
                show_aspects.setSelection(!show_aspects.getSelection());
            ChartTab.getData().setShowAspects(show_aspects.getSelection());
            boolean enable_now = !show_horiz.getSelection()
                    && !show_aspects.getSelection()
                    && Resource.getPrefInt("show_now") != 0;
            Moira.getChart().setShowNow(enable_now, true, false);
            updateMenu(null);
        }
    }

    public void doHelp()
    {
        showHTML(Resource.isSimplified() ? "help_simplified"
                : "help_traditional", "help_overview", true, true);
    }

    private void loadModification()
    {
        ChartTab.hideTip();
        FileDialog dialog = new FileDialog(Moira.getShell(), SWT.OPEN);
        dialog.setFilterExtensions(Resource.RSRC_EXTENSIONS);
        if (Resource.hasPrefKey("last_mod_path")) {
            dialog.setFilterPath(Resource.getPrefString("last_mod_path"));
        }
        if (dialog.open() == null)
            return;
        String file_name = dialog.getFileName();
        if (file_name == null)
            return;
        String path_name = dialog.getFilterPath();
        String path_file_name;
        if (path_name == null || path_name.equals("")) {
            path_name = "";
            path_file_name = file_name;
        } else {
            path_file_name = path_name + File.separator + file_name;
        }
        setModification(path_file_name, true);
        Resource.putPrefString("last_mod_path", path_name);
        Resource.putPrefString("modification", path_file_name);
    }

    private void clearModification()
    {
        ChartTab.hideTip();
        setModification(null, true);
        Resource.removePref("modification");
    }

    public void setModification(String str, boolean update)
    {
        Resource.loadModification(str);
        FontMap.resetFontName();
        DrawAWT.resetFontTable();
        Moira.getChart().updateAttribute();
        ChartData.getData().loadResource();
        ChartData.getData().setShowNow(
                Resource.getPrefInt("show_now") != 0
                        && !ChartTab.getData().getShowAspects());
        ChartData.getData().setTimeAdjust(
                Resource.getPrefInt("longitude_adjust"));
        Moira.getChart().updateAdjNorth(null);
        Moira.getChart().setDstAdjust(Resource.getPrefInt("dst_adjust") != 0);
        ChartMode.setChartMode();
        setChartMode(ChartMode.getChartMode());
        updateMenu(null);
        Moira.setGroupName();
        if (update)
            Moira.update(false, false);
    }

    private void loadEvaluation(boolean reload)
    {
        ChartTab.hideTip();
        String path_name, file_name, path_file_name;
        if (reload) {
            String key = ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_evaluation"
                    : "evaluation";
            path_file_name = Resource.hasPrefKey(key) ? Resource
                    .getPrefString(key) : null;
            path_name = null;
            if (path_file_name == null)
                return;
        } else {
            FileDialog dialog = new FileDialog(Moira.getShell(), SWT.OPEN);
            dialog.setFilterExtensions(Resource.RULE_EXTENSIONS);
            if (Resource.hasPrefKey("last_eval_path")) {
                dialog.setFilterPath(Resource.getPrefString("last_eval_path"));
            }
            if (dialog.open() == null)
                return;
            file_name = dialog.getFileName();
            if (file_name == null)
                return;
            path_name = dialog.getFilterPath();
            if (path_name == null || path_name.equals("")) {
                path_name = "";
                path_file_name = file_name;
            } else {
                path_file_name = path_name + File.separator + file_name;
            }
        }
        setEvaluation(path_file_name);
        if (path_name != null)
            Resource.putPrefString("last_eval_path", path_name);
        Resource.putPrefString(
                ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_evaluation"
                        : "evaluation", path_file_name);
        updateMenu(null);
    }

    private void clearEvaluation()
    {
        ChartTab.hideTip();
        setEvaluation(null);
        Resource.removePref(ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_evaluation"
                : "evaluation");
        updateMenu(null);
    }

    private void reloadEvaluation()
    {
        String key = ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_evaluation"
                : "evaluation";
        String str = Resource.hasPrefKey(key) ? Resource.getPrefString(key)
                : null;
        if (!Resource.loadEvaluation(str, true))
            return;
        boolean show_eval = Resource.getPrefInt("show_eval") != 0;
        if (show_eval != RuleEntry.hasRuleEntry(true)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            Moira.getChart().showHideTab(ChartTab.EVAL_TAB, !show_eval, false,
                    true);
        }
    }

    private void setEvaluation(String str)
    {
        Resource.loadEvaluation(str, false);
        boolean show_eval = Resource.getPrefInt("show_eval") != 0;
        if (show_eval != RuleEntry.hasRuleEntry(true)
                && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            Moira.getChart().showHideTab(ChartTab.EVAL_TAB, !show_eval, false,
                    true);
        }
        Moira.update(false, false);
    }

    private void showHTML(String key, String title_key, boolean os_stat,
            boolean gap)
    {
        String title = Resource.getString(title_key);
        int index = title.indexOf("(");
        if (index >= 0)
            title = title.substring(0, index);
        if (gap)
            title += "      ";
        if (os_stat) {
            String version = System.getProperty("java.version");
            String os_name = System.getProperty("os.name");
            title += ChartMode.getModeTitle() + " - " + Resource.NAME
                    + "      [Java " + version + ", " + os_name + "]";
        } else {
            title += " - " + Resource.NAME;
        }
        if (html_shell != null) {
            String str = html_shell.getText();
            if (title.equals(str)) {
                html_shell.setActive();
                return;
            } else {
                html_shell.close();
                Moira.flushEvents(false);
            }
        }
        ChartTab.hideTip();
        html_shell = new Shell(Display.getCurrent());
        html_shell.setLayout(new GridLayout());
        CoolBar cool_bar = new CoolBar(html_shell, SWT.NONE);
        cool_bar.setBackground(html_shell.getBackground());
        Composite container = new Composite(cool_bar, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        container.setLayout(layout);
        CButton backward = new CButton(container, "backward_icon", null);
        CButton forward = new CButton(container, "forward_icon", null);
        CoolItem cool_item = new CoolItem(cool_bar, SWT.NONE);
        cool_item.setControl(container);
        container.pack();
        Point size = container.getSize();
        size = cool_item.computeSize(size.x, size.y);
        cool_item.setPreferredSize(size);
        final Browser browser = new Browser(html_shell, SWT.NONE);
        browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        browser.setUrl(FileIO.getURL(Resource.getString(key)).toString());
        backward.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent event)
            {
                browser.back();
            }
        });
        forward.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent event)
            {
                browser.forward();
            }
        });
        html_shell.setText(title);
        html_shell.setImage(Moira.getShell().getImage());
        Moira.setShellPosition(html_shell, null);
        html_shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e)
            {
                html_shell = null;
            }
        });
        html_shell.open();
        html_shell.setActive();
    }

    public void showTabMenu(int index, boolean show)
    {
        switch (index) {
            case ChartTab.DATA_TAB:
                if (show_data.getSelection() != show)
                    show_data.setSelection(show);
                break;
            case ChartTab.POLE_TAB:
                if (show_pole.getSelection() != show)
                    show_pole.setSelection(show);
                break;
            case ChartTab.NOTE_TAB:
                if (show_note.getSelection() != show)
                    show_note.setSelection(show);
                break;
            default:
                break;
        }
    }

    public void showTableMenu(boolean show)
    {
        if (show_table.getSelection() != show)
            show_table.setSelection(show);
    }

    static public int getSwtFontStyle()
    {
        switch (Resource.getFontStyle()) {
            case java.awt.Font.BOLD:
                return SWT.BOLD;
            case java.awt.Font.ITALIC:
                return SWT.ITALIC;
            default:
                return SWT.NORMAL;
        }
    }

    static public int selectChartMode(boolean quick_mode, Shell shell)
    {
        ModeDialog dialog = new ModeDialog(shell);
        if (quick_mode) {
            dialog.setMode(1);
        } else {
            ChartTab.hideTip();
        }
        int chart_mode = (dialog.open() == Window.OK) ? dialog.getChartMode()
                : (quick_mode ? ChartMode.TRADITIONAL_MODE : ChartMode
                        .getChartMode());
        dialog.close();
        return chart_mode;
    }

    public void setChartMode(int chart_mode)
    {
        ChartTab chart = Moira.getChart();
        disposeSubWin();
        ChartMode.setChartMode(chart_mode);
        Moira.setShellTitle(null, null, false, false);
        chart.restoreBirthDay();
        chart.updateAdjNorth(null);
        switch (chart_mode) {
            case ChartMode.PICK_MODE:
                chart.removeNowControl();
                chart.addPickControl();
                chart.removeAstroControl();
                Moira.getTable().removeChartButton();
                break;
            case ChartMode.ASTRO_MODE:
                chart.removeNowControl();
                chart.removePickControl();
                Moira.getTable().addChartButton();
                break;
            default:
                chart.removePickControl();
                chart.removeAstroControl();
                if (ChartTab.getData().getShowNow())
                    chart.addNowControl(false);
                else
                    chart.removeNowControl();
                Moira.getTable().removeChartButton();
                break;
        }
        chart.updateNonAstroTab(ChartTab.POLE_TAB);
        chart.updateNonAstroTab(ChartTab.EVAL_TAB);
        setAstroMode(ChartMode.NATAL_MODE);
        ChartTab.getData().resetSignComputationType();
        Moira.getTable().setMode();
        chart.removeTip();
        reloadEvaluation();
        updateMenu(null);
    }

    static public void setAstroMode(int astro_mode)
    {
        if (name != null)
            name.setEnabled(astro_mode == ChartMode.NATAL_MODE);
        ChartMode.setAstroMode(astro_mode);
    }

    static public void addCommandListener(Composite composite)
    {
        composite.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event)
            {
                if (event.stateMask == (SWT.CONTROL | SWT.ALT)
                        && event.character == 0x1a) { // ctrl-alt-Z
                    CommandDialog dialog = new CommandDialog(Moira.getShell());
                    if (Resource.hasPrefKey("last_command"))
                        dialog.setCommand(Resource
                                .getPrefString("last_command"));
                    if (dialog.open() == Window.OK) {
                        String command = dialog.getCommand();
                        if (command.equals("")) {
                            Resource.removePref("last_command");
                        } else {
                            Resource.putPrefString("last_command", command);
                            processCommand(command, false);
                        }
                    }
                    dialog.close();
                } else if (event.keyCode == SWT.F5 || event.keyCode == SWT.F6) {
                    FolderToolBar.findNextEntry(event.keyCode == SWT.F5);
                }
            }
        });
    }

    static public void processCommand(String line, boolean silent)
    {
        boolean success = true, skip_message = false;
        String command = null, message = null;
        String fail_message = "Invalid command or command failed";
        StringTokenizer st = new StringTokenizer(line, "=");
        int n_tok = st.countTokens();
        if (n_tok == 1 && line.endsWith("=") || n_tok == 2) { // preference
            String key = st.nextToken().trim();
            if (n_tok == 1) {
                Resource.removePref(key);
                message = "Remove preference \"" + key + "\"";
            } else {
                String info = st.nextToken().trim();
                if (info.startsWith("\"") && info.endsWith("\"")) {
                    info = info.substring(1, info.length() - 1).trim();
                }
                Resource.putPrefString(key, info);
                message = "Set preference \"" + key + "\" to \"" + info + "\"";
            }
        } else { // command
            st = new StringTokenizer(line, "\t ,");
            n_tok = st.countTokens();
            if (n_tok == 0) {
                success = false;
            } else {
                command = st.nextToken().trim();
                if (n_tok == 1) {
                    if (command.startsWith("?")) {
                        String key = command.substring(1);
                        String value = Resource.getPrefString(key);
                        message = ((!Resource.hasPrefKey(key) && Resource
                                .hasKey(key)) ? "Resource" : "Preference")
                                + " \"" + key + "\" set to \"" + value + "\"";
                        Moira.toClipboard(key + "=\"" + value + "\"");
                    } else if (command.equalsIgnoreCase("setTextMapSequence")) {
                        DataSet.setAlternateMapString();
                    } else if (command.equalsIgnoreCase("copyEntry")) {
                        success = Moira.getTable().copyEntry();
                    } else if (command.equalsIgnoreCase("pasteEntry")) {
                        success = Moira.getTable().pasteEntry();
                    } else if (command.equalsIgnoreCase("golden")) {
                        success = Moira.getTable().golden();
                    } else if (command.equalsIgnoreCase("getColor")) {
                        int color = ColorFieldDialog.getColor(-1);
                        success = color >= 0;
                        if (success) {
                            String result = "0x" + Integer.toHexString(color);
                            message = "Color code: " + result;
                            Moira.toClipboard(result);
                        }
                    } else if (command.equalsIgnoreCase("azimuth")) {
                        showSearchResult("azimuth", false);
                    } else if (command.equalsIgnoreCase("solarEclipse")) {
                        showSearchResult("eclipse_solar", true);
                    } else if (command.equalsIgnoreCase("lunarEclipse")) {
                        showSearchResult("eclipse_lunar", true);
                    } else {
                        success = false;
                    }
                } else if (n_tok == 2) {
                    if (command.equalsIgnoreCase("genPicture")) {
                        String dir = st.nextToken().trim();
                        success = Moira.getTable().genPicture(dir);
                    } else if (command.equalsIgnoreCase("regression")
                            || command.equalsIgnoreCase("updateGolden")) {
                        String dir = st.nextToken().trim();
                        int num_failed = Moira.getTable().regression(dir,
                                command.equalsIgnoreCase("updateGolden"));
                        success = num_failed == 0;
                        if (success) {
                            skip_message = false;
                            message = "Regression passed.";
                        } else {
                            fail_message = (num_failed < 0) ? "Regression failed (no write access)!"
                                    : (num_failed + " regression(s) failed!");
                        }
                    } else if (command.equalsIgnoreCase("solarReturn")) {
                        int year = FileIO.parseInt(st.nextToken().trim(), 0,
                                false);
                        success = Moira.getChart().solarReturn(year);
                    } else if (command.equalsIgnoreCase("lunarReturn")) {
                        double ut = FileIO.parseDouble(st.nextToken().trim(),
                                Double.MIN_VALUE, true);
                        success = Moira.getChart().lunarReturn(ut);
                    } else if (command.equalsIgnoreCase("transit")) {
                        double ut = FileIO.parseDouble(st.nextToken().trim(),
                                Double.MIN_VALUE, true);
                        Moira.getChart().setTransitTime(ChartMode.TRANSIT_MODE,
                                ut, false, false);
                    } else if (command.equalsIgnoreCase("primary")) {
                        double ut = FileIO.parseDouble(st.nextToken().trim(),
                                Double.MIN_VALUE, true);
                        Moira.getChart().setTransitTime(
                                ChartMode.PRIMARY_DIRECTION_MODE, ut, false,
                                false);
                    } else if (command.equalsIgnoreCase("solarArc")) {
                        double ut = FileIO.parseDouble(st.nextToken().trim(),
                                Double.MIN_VALUE, true);
                        Moira.getChart().setTransitTime(
                                ChartMode.SOLAR_ARC_MODE, ut, false, false);
                    } else if (command.equalsIgnoreCase("relationship")) {
                        int index = FileIO.parseInt(st.nextToken().trim(), 0,
                                true);
                        if (Moira.needUpdate())
                            Moira.update(false, true);
                        success = Moira.getTable().setMultiMode(index);
                    } else if (command.equalsIgnoreCase("utToDate")) {
                        double ut = FileIO.parseDouble(st.nextToken().trim(),
                                Double.MIN_VALUE, true);
                        skip_message = false;
                        message = Moira.getChart()
                                .getDateStringFromUJulianDayUT(ut);
                        Moira.toClipboard(message);
                    } else if (command.equalsIgnoreCase("transitSearch")) {
                        int mode = FileIO.parseInt(st.nextToken().trim(), 0,
                                true);
                        showTransits(mode);
                    } else {
                        success = false;
                    }
                } else if (n_tok == 3) {
                    if (command.equalsIgnoreCase("findStarByPos")) {
                        success = findStarByPos(st.nextToken().trim(), st
                                .nextToken().trim());
                        if (success)
                            skip_message = true;
                    } else if (command.equalsIgnoreCase("eightChar")) {
                        int year = FileIO.parseInt(st.nextToken().trim(), 0,
                                false);
                        findPoleDate(year, st.nextToken().trim());
                    } else if (command.equalsIgnoreCase("secondary")) {
                        double ut = FileIO.parseDouble(st.nextToken().trim(),
                                Double.MIN_VALUE, true);
                        int single = FileIO.parseInt(st.nextToken().trim(), 0,
                                false);
                        Moira.getChart().setTransitTime(
                                ChartMode.SECONDARY_PROGRESSION_MODE, ut,
                                false, false);
                        if (single != 0)
                            Moira.getMenu().doShowChartMode(true);
                    } else {
                        success = false;
                    }
                } else if (n_tok == 4) {
                    if (command.equalsIgnoreCase("dumpPlanetSpeed")
                            || command.equalsIgnoreCase("dumpPlanetAttribute")) {
                        success = Moira
                                .getChart()
                                .dumpAllPlanetData(
                                        st.nextToken().trim(),
                                        FileIO.parseInt(st.nextToken().trim(),
                                                0, true),
                                        FileIO.parseInt(st.nextToken().trim(),
                                                0, true),
                                        command.equalsIgnoreCase("dumpPlanetSpeed"));
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                }
            }
        }
        if (silent)
            return;
        if (success) {
            if (!skip_message) {
                Message.info((message != null) ? message : ("Command \""
                        + command + "\" executed"));
            }
        } else {
            Message.warn(fail_message);
        }
    }

    static private boolean findStarByPos(String ra, String dec)
    {
        double[] pos = new double[2];
        pos[0] = FileIO.parseAscDec(ra, 0.0, false);
        pos[1] = FileIO.parseAscDec(dec, 0.0, true);
        Calculate cal = ChartTab.getCal();
        LinkedList head = cal.findStarByEquPos(pos);
        if (head == null)
            return false;
        String file_name = FileIO.getTempFileName(".html");
        if (file_name == null)
            return false;
        String title = "Find star by position";
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(cal.getJulianDayUT(), date);
        HTMLData.init(file_name, title, 1);
        HTMLData.header();
        HTMLData.paragraph("Search for closest star at "
                + City.formatLongLatitude(pos[0], true, true, false) + ", "
                + City.formatLongLatitude(pos[1], false, true, false) + " ["
                + ra + ", " + dec + "] at "
                + BaseCalendar.formatDate(null, date));
        String[] array = { "Symbol", "Position", "Magnitude", "Delta",
                "RMS Error" };
        HTMLData.tableHeader(array);
        int i = 0;
        for (ListIterator iter = head.listIterator(); iter.hasNext();) {
            String[] result = cal.getStarEquPosData(pos, iter.next());
            if (result == null)
                break;
            HTMLData.tableRow(result);
            if (++i >= 100)
                break;
        }
        HTMLData.tableFooter();
        HTMLData.footer();
        disposeSubWin(data_shell);
        openDataShell(title, FileIO.getURL(file_name).toString(), "find_star_");
        data_shell.open();
        data_shell.setActive();
        return true;
    }

    public void setExampleSelectedIndex(int index)
    {
        if (example_data == null || index < 0 || example_index < 0
                || example_index >= example_data.length
                || !example_data[example_index].selected)
            return;
        Resource.putPrefInt("example_index_" + example_index, index);
    }

    private int getExampleSelectedIndex()
    {
        return Resource.getPrefInt("example_index_" + example_index);
    }

    static private void disposeSubWin(Shell shell)
    {
        if (shell == null)
            return;
        shell.close();
        Moira.flushEvents(false);
    }

    static public void disposeSubWin()
    {
        disposeSubWin(html_shell);
        disposeSubWin(data_shell);
    }

    private class ExampleData {
        String title, file;

        int priority;

        boolean selected;

        public ExampleData(String t_title, String t_file, int t_priority)
        {
            title = t_title;
            file = t_file;
            priority = t_priority;
        }
    }
}