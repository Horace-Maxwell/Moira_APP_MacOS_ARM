//
// Moira - A Chinese Astrology Charting Program
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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.Calculate;
import org.athomeprojects.base.ChartData;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.City;
import org.athomeprojects.base.DataEntry;
import org.athomeprojects.base.DiagramTip;
import org.athomeprojects.base.DrawAWT;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Resource;
import org.athomeprojects.base.SearchRecord;
import org.athomeprojects.swtext.CButton;
import org.athomeprojects.swtext.CalendarSpinner;
import org.athomeprojects.swtext.ColorManager;
import org.athomeprojects.swtext.FontMap;
import org.athomeprojects.swtext.HoverTipSWT;
import org.athomeprojects.swtext.ImageManager;
import org.athomeprojects.swtext.LocationSpinner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class ChartTab {
    static public final int DATA_TAB = 0;

    static public final int POLE_TAB = 1;

    static public final int EVAL_TAB = 2;

    static public final int NOTE_TAB = 3;

    static public final int NUM_TAB = 4;

    private final int TRANSPARENT_COLOR = 0x123456;

    private final int HINT_DELAY = 10;

    private final double DESC_DATA_SCALER = 1.04;

    private final double DESC_SCROLL_SCALER = 1.02;

    private final double DESC_WIDTH_SCALER = 1.30;

    private final PaletteData PALETTE_DATA = new PaletteData(0xFF0000, 0xFF00,
            0xFF);

    static private ChartData data;

    static private HoverTipSWT tip_handler;

    static private boolean ui_mode;

    private CalendarSpinner birth, now;

    private LocationSpinner location;

    static private DataTab[] tabs;

    private Composite[] tab_controls;

    private int[] tab_orders;

    private Group[] desc_group = new Group[2];

    private Canvas[] desc = new Canvas[2];

    private CTabFolder sub_folder;

    private Group group, ui_group, user_group, now_group, astro_group;

    private Text pick_degree, pole_degree;

    private Combo pick_choice;

    private Composite top_composite, ctrl_composite, date_composite,
            pick_composite;

    private Button update;

    private Font font = null;

    private String group_name;

    private Canvas diagram;

    private CanvasUI ui_diagram;

    private Composite top, combo, lr_combo;

    private StackLayout top_layout;

    private SashForm dock_sash;

    private Menu toggle_menu;

    private MenuItem[] toggle_menu_item;

    private boolean in_drag, reset_draw;

    private DiagramTip drag_tip;

    private int planet_no;

    private Button[] astro_mode_button = new Button[ChartMode.NUM_ASTRO_MODE];

    private CacheEntry[] cache_record = new CacheEntry[ChartMode.NUM_ASTRO_MODE];

    public Composite createTabFolderPage(CTabFolder tab_folder)
    {
        ui_mode = Resource.getPrefInt("ui_mode") != 0;
        top = new Composite(tab_folder, SWT.NONE);
        top_layout = new StackLayout();
        top_layout.marginWidth = top_layout.marginHeight = 0;
        top.setLayout(top_layout);
        combo = new Composite(top, SWT.NONE);
        top_layout.topControl = combo;
        combo.setLayout(new FillLayout());
        dock_sash = new SashForm(combo, SWT.VERTICAL);
        combo.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event e)
            {
                setSashWeights();
            }
        });
        lr_combo = new Composite(dock_sash, SWT.NONE);
        lr_combo.setLayout(new GridLayout(2, false));
        lr_combo.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event e)
            {
                layout(false);
            }
        });
        user_group = new Group(lr_combo, SWT.NONE);
        user_group.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        user_group.setLayout(new FillLayout());
        final ScrolledComposite diagram_scroll = new ScrolledComposite(
                user_group, SWT.H_SCROLL | SWT.V_SCROLL);
        diagram_scroll.setExpandVertical(true);
        diagram_scroll.setExpandHorizontal(true);
        diagram_scroll.setLayout(new FillLayout());
        tip_handler = new HoverTipSWT(Moira.getShell());
        diagram = new Canvas(diagram_scroll, SWT.NO_BACKGROUND | SWT.BORDER);
        MenuFolder.addCommandListener(diagram);
        addDiagramListener(diagram);
        diagram_scroll.setContent(diagram);
        int[] size = new int[2];
        Resource.getIntArray("chart_min_size", size);
        diagram_scroll.setMinSize(size[0], size[1]);
        ui_group = new Group(top, SWT.NONE);
        ui_group.setLayout(new FillLayout());
        ui_diagram = new CanvasUI(ui_group, SWT.BORDER);
        addDiagramListener(ui_diagram);
        top_composite = new Composite(lr_combo, SWT.NONE);
        top_composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout grid_layout = new GridLayout(1, false);
        grid_layout.marginWidth = grid_layout.marginHeight = 0;
        top_composite.setLayout(grid_layout);
        ctrl_composite = new Composite(top_composite, SWT.NONE);
        ctrl_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        grid_layout = new GridLayout(1, false);
        grid_layout.marginWidth = grid_layout.marginHeight = 0;
        ctrl_composite.setLayout(grid_layout);
        group = new Group(ctrl_composite, SWT.NONE);
        group_name = "";
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout(1, true));
        date_composite = new Composite(group, SWT.NONE);
        date_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        grid_layout = new GridLayout(2, false);
        grid_layout.marginWidth = grid_layout.marginHeight = 0;
        date_composite.setLayout(grid_layout);
        birth = new CalendarSpinner(date_composite, SWT.BORDER);
        birth.init(false);
        birth.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        update = new Button(date_composite, SWT.PUSH);
        ImageManager.setImageButton(update, "button_icon");
        update.setToolTipText(Resource.getString("tip_update_button")
                + "    F1");
        update.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                Moira.getMenu().enterName(true);
            }
        });
        date_composite.pack();
        location = new LocationSpinner(group, SWT.BORDER);
        location.init("cities.prop");
        location.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        location.limitWidth(date_composite.getSize().x);
        sub_folder = TabManager.initTabFolder(top_composite, 0);
        sub_folder.setLayoutData(new GridData(GridData.FILL_BOTH));
        TabManager.initFolder(TabManager.SUB_FOLDER, sub_folder);
        final ScrolledComposite[] desc_scroll = new ScrolledComposite[2];
        for (int i = 0; i < 2; i++) {
            desc_group[i] = new Group(sub_folder, SWT.NONE);
            String key = Resource
                    .getString((i == 0) ? "year_data_plus_eight_char"
                            : "year_data");
            desc_group[i].setText(key);
            desc_group[i].setLayout(new FillLayout());
            desc_scroll[i] = new ScrolledComposite(desc_group[i], SWT.H_SCROLL
                    | SWT.V_SCROLL);
            desc_scroll[i].setExpandVertical(true);
            desc_scroll[i].setExpandHorizontal(true);
            desc_scroll[i].setLayout(new FillLayout());
            desc[i] = new Canvas(desc_scroll[i], SWT.BORDER);
            desc_scroll[i].setContent(desc[i]);
        }
        desc[0].addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event event)
            {
                showData(event.gc, desc_scroll[0], desc[0], true);
            }
        });
        desc[1].addListener(SWT.Paint, new Listener() {
            public void handleEvent(Event event)
            {
                showData(event.gc, desc_scroll[1], desc[1], false);
            }
        });
        tabs = new DataTab[NUM_TAB];
        tab_controls = new Composite[NUM_TAB];
        tab_orders = new int[NUM_TAB];
        tabs[DATA_TAB] = new DataTab();
        tab_controls[DATA_TAB] = tabs[DATA_TAB].createDataPage(sub_folder,
                Resource.getString("data_label"), "data", true, false, false,
                true, false);
        tabs[DATA_TAB].setGroupName(false);
        tab_orders[DATA_TAB] = TabManager.DATA_TAB_ORDER;
        tabs[POLE_TAB] = new DataTab();
        tab_controls[POLE_TAB] = tabs[POLE_TAB].createDataPage(sub_folder,
                Resource.getString("pole_label"), "pole", true, false, false,
                true, false);
        tabs[POLE_TAB].setGroupName(false);
        tab_orders[POLE_TAB] = TabManager.POLE_TAB_ORDER;
        tabs[EVAL_TAB] = new DataTab();
        tab_controls[EVAL_TAB] = tabs[EVAL_TAB].createDataPage(sub_folder,
                Resource.getString("eval_label"), "eval", true, false, false,
                true, false);
        tabs[EVAL_TAB].setGroupName(false);
        tab_orders[EVAL_TAB] = TabManager.EVAL_TAB_ORDER;
        tabs[NOTE_TAB] = new DataTab();
        tab_controls[NOTE_TAB] = tabs[NOTE_TAB].createDataPage(sub_folder,
                Resource.getString("note_label"), "note", false, true, true,
                true, false);
        tabs[NOTE_TAB].setGroupName(false);
        tab_orders[NOTE_TAB] = TabManager.NOTE_TAB_ORDER;
        now_group = new Group(TabManager.getPlaceHolder(), SWT.NONE);
        now_group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        now_group.setText(Resource.getString("current_date"));
        now_group.setLayout(new GridLayout(1, false));
        now = new CalendarSpinner(now_group, SWT.BORDER);
        now.init(false);
        pick_composite = new Composite(TabManager.getPlaceHolder(), SWT.NONE);
        pick_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pick_composite.setLayout(new GridLayout(3, false));
        Group degree_group = new Group(pick_composite, SWT.NONE);
        degree_group.setText(Resource.getString("mountain_degree"));
        degree_group.setLayout(new GridLayout(1, false));
        pick_degree = new Text(degree_group, SWT.RIGHT | SWT.BORDER);
        pick_degree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pick_degree.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                pick_degree.setText(data.setMountainPos(pick_degree.getText()));
            }
        });
        Group choice_group = new Group(pick_composite, SWT.NONE);
        choice_group.setText(Resource.getString("pick_coice_select"));
        choice_group.setLayout(new GridLayout(1, false));
        pick_choice = new Combo(choice_group, SWT.DROP_DOWN);
        pick_choice.setToolTipText(Resource.getString("tip_pick_choice"));
        pick_choice.add(Resource.getString("pick_day_choice"));
        pick_choice.add(Resource.getString("pick_night_choice"));
        pick_choice.select(0);
        pick_choice.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                data.setDaySet(pick_choice.getSelectionIndex() == 0);
            }
        });
        Group angle_group = new Group(pick_composite, SWT.NONE);
        angle_group.setText(Resource.getString("magnetic_shift"));
        angle_group.setLayout(new GridLayout(1, false));
        pole_degree = new Text(angle_group, SWT.RIGHT | SWT.BORDER);
        pole_degree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pole_degree.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                savePoleDegree(null);
            }
        });
        astro_group = new Group(TabManager.getPlaceHolder(), SWT.NONE);
        astro_group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        astro_group.setLayout(new GridLayout(1, false));
        astro_group.setText(Resource.getString("astro_group"));
        updateColor();
        FileIO.setProgress(60);
        data = new ChartData(tabs[DATA_TAB], tabs[POLE_TAB], tabs[EVAL_TAB]);
        FileIO.setProgress(70);
        TabManager.setTabItem(TabManager.SUB_FOLDER,
                TabManager.BIRTH_TAB_ORDER, desc_group[0], "birth_date", false);
        boolean show_now = Resource.getPrefInt("show_now") != 0
                && !data.getShowAspects();
        data.setShowNow(show_now);
        switch (ChartMode.getChartMode()) {
            case ChartMode.PICK_MODE:
                addPickControl();
                break;
            case ChartMode.ASTRO_MODE:
                break;
            default:
                if (show_now)
                    addNowControl(false);
                break;
        }
        updateAdjNorth(null);
        sub_folder.setSelection(0);
        BaseCalendar.setDstAdjust(Resource.getPrefInt("dst_adjust") != 0);
        data.setTimeAdjust(Resource.getPrefInt("longitude_adjust"));
        if (ui_mode) {
            moveEntryField(ui_diagram.getEntryField());
            top_layout.topControl = ui_group;
            top.layout();
        }
        if (data.getAngleMarkerEnable()[0]) {
            DrawSWT.initMarker(getDiagram(), data.getAspectColorArray(true),
                    data.getAspectDisplayArray(true),
                    data.getAspectDegreeArray(true),
                    Resource.getInt("angle_marker_width"));
        }
        refresh(false);
        return top;
    }

    private void addDiagramListener(Canvas canvas)
    {
        tip_handler.activateHoverHelp(canvas);
        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent event)
            {
                if (event.width == 0 || event.height == 0 || Moira.noUpdate())
                    return;
                Rectangle area = ((Canvas) event.getSource()).getClientArea();
                showDiagram(event, new Point(area.width, area.height));
            }
        });
        canvas.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent event)
            {
                toggleUI();
            }

            public void mouseUp(MouseEvent event)
            {
                if (in_drag) {
                    Moira.setCursor(0, false);
                    Control c = (Control) event.getSource();
                    c.setCapture(false);
                    in_drag = false;
                    if (!DrawSWT.endRubberBandLine(c
                            .toDisplay(event.x, event.y))
                            || !processDrag(event)) {
                        Rectangle bound = DrawSWT.getRubberBandMessageBound(c);
                        c.redraw(bound.x, bound.y, bound.width, bound.height,
                                false);
                    }
                } else if (!processClick(event) && event.button != 1
                        && (event.stateMask & SWT.CONTROL) != SWT.CONTROL) {
                    boolean[] enable = data.getAngleMarkerEnable();
                    enable[0] = !enable[0];
                    data.setAngleMarkerEnable(enable);
                    updateAngleMarker(event.x, event.y);
                }
            }

            public void mouseDown(MouseEvent event)
            {
                if (event.button == 1
                        && (ChartMode.isChartMode(ChartMode.TRADITIONAL_MODE) || ChartMode
                                .isChartMode(ChartMode.SIDEREAL_MODE))) {
                    drag_tip = getCacheEntry().tip;
                    if (drag_tip == null)
                        return;
                    planet_no = drag_tip.getPlanetFromPoint(event.x, event.y);
                    if (planet_no >= 0
                            && (!drag_tip.isBirthPlanet() || Resource
                                    .getPrefInt("edit_sign_menu") != 0)
                            && data.canComputeTransit(planet_no)) {
                        in_drag = true;
                        Point loc = drag_tip.getCenterPoint();
                        Control c = (Control) event.getSource();
                        c.setCapture(true);
                        Moira.setCursor(SWT.CURSOR_CROSS, true);
                        DrawSWT.initRubberBandLine(c.toDisplay(2, 2),
                                c.toDisplay(loc), c.toDisplay(event.x, event.y));
                    }
                } else if (event.button != 1
                        && (event.stateMask & SWT.CONTROL) == SWT.CONTROL
                        && ui_mode) {
                    ui_diagram.setMouseLocation();
                    buildPopupMenu();
                    toggle_menu.setVisible(true);
                }
            }
        });
        canvas.addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent event)
            {
                if (in_drag) {
                    double degree = drag_tip.getDegreeFromPoint(event.x,
                            event.y, true);
                    String mesg = drag_tip.isDoubleValid(degree) ? data
                            .getPlanetPos(planet_no,
                                    City.normalizeDegree(degree + 30.0)) : null;
                    Control c = (Control) event.getSource();
                    DrawSWT.drawRubberBandLine(mesg,
                            c.toDisplay(event.x, event.y));
                }
                DrawSWT.drawMarker(event.x, event.y);
                CButton.deselectAll();
            }
        });
        canvas.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent event)
            {
                DrawSWT.drawCancel();
            }
        });
        canvas.addMouseTrackListener(new MouseTrackAdapter() {
            public void mouseEnter(MouseEvent event)
            {
                Moira.setFocus((Control) event.getSource());
                if (ui_mode && ui_diagram.showUIHint()) {
                    tip_handler.showTimerHint(
                            Resource.getString("hint_show_form"), ui_diagram,
                            -10, 10, HINT_DELAY);
                }
            }

            public void mouseExit(MouseEvent event)
            {
                DrawSWT.drawCancel();
            }
        });
        final Menu drag_menu = new Menu(Moira.getShell(), SWT.POP_UP);
        MenuItem forward = new MenuItem(drag_menu, SWT.NONE);
        forward.setText(Resource.getString("popup_drag_forward"));
        forward.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                getDateAtPlanetPos(getCacheEntry().tip, Calculate.INVALID,
                        false);
            }
        });
        MenuItem backward = new MenuItem(drag_menu, SWT.NONE);
        backward.setText(Resource.getString("popup_drag_backward"));
        backward.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                getDateAtPlanetPos(getCacheEntry().tip, Calculate.INVALID, true);
            }
        });
        toggle_menu = new Menu(Moira.getShell(), SWT.POP_UP);
        canvas.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event)
            {
                DiagramTip tip = getCacheEntry().tip;
                if (tip == null || ChartMode.isChartMode(ChartMode.PICK_MODE))
                    return;
                Canvas c = (Canvas) event.widget;
                Point pt = c
                        .toControl(Display.getCurrent().getCursorLocation());
                planet_no = tip.getPlanetFromPoint(pt.x, pt.y);
                if (planet_no >= 0 && !tip.isBirthPlanet()
                        && data.canComputeTransit(planet_no)) {
                    drag_menu.setVisible(true);
                }
            }
        });
    }

    private void buildPopupMenu()
    {
        MenuFolder.removeAllMenuItems(toggle_menu, false);
        if (toggle_menu_item == null)
            toggle_menu_item = new MenuItem[NUM_TAB];
        int overlay = ui_diagram.getOverlay();
        for (int i = 0; i < NUM_TAB; i++) {
            if (tabs[i].isTabVisible()) {
                toggle_menu_item[i] = new MenuItem(toggle_menu, SWT.CHECK);
                toggle_menu_item[i].setText(Resource.getString("popup_toggle_"
                        + tabs[i].getType() + "_overlay"));
                toggle_menu_item[i].setSelection(overlay == i);
                toggle_menu_item[i]
                        .addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent event)
                            {
                                for (int j = 0; j < NUM_TAB; j++) {
                                    if (toggle_menu_item[j] == event
                                            .getSource()) {
                                        ui_diagram.toggleOverlay(j);
                                        return;
                                    }
                                }
                            }
                        });
            } else {
                toggle_menu_item[i] = null;
            }
        }
        new MenuItem(toggle_menu, SWT.SEPARATOR);
        final MenuItem boundary = new MenuItem(toggle_menu, SWT.NONE);
        boundary.setText(Resource.getString("popup_set_boundary"));
        boundary.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                ui_diagram.setOverlayBoundary();
            }
        });
    }

    private boolean processDrag(MouseEvent event)
    {
        if (drag_tip == null)
            return false;
        double degree = drag_tip.getDegreeFromPoint(event.x, event.y, true);
        if (!drag_tip.isDoubleValid(degree))
            return false;
        if (drag_tip.isBirthPlanet()) {
            if (data.getPlanetOffset(planet_no, degree))
                update(true, false);
        } else {
            getDateAtPlanetPos(drag_tip, degree,
                    (event.stateMask & SWT.SHIFT) != 0);
        }
        return true;
    }

    private void getDateAtPlanetPos(DiagramTip tip, double degree,
            boolean backward)
    {
        if (tip == null)
            return;
        int[] date = new int[5];
        boolean birth_data = tip.isBirthPlanet();
        if (birth_data)
            getBirthDate(date);
        else
            getNowDate(date);
        date = data.getDateAtPlanetPos(planet_no, birth_data, degree,
                location.getCountryName(), location.getCityName(),
                location.getZoneName(), date, backward);
        if (date != null) {
            if (birth_data)
                setBirthDate(date);
            else
                setNowDate(date);
            update(true, false);
        }
    }

    private boolean processClick(MouseEvent event)
    {
        DiagramTip tip = getCacheEntry().tip;
        if (tip == null)
            return false;
        switch (ChartMode.getChartMode()) {
            case ChartMode.PICK_MODE:
            {
                if (data.getShowFixstar()) {
                    if (tip.hasDataFromPoint(event.x, event.y)) {
                        double degree = tip.getDoubleFromLastPoint();
                        if (tip.isDoubleValid(degree)) {
                            if (event.button != 1)
                                degree = City.normalizeDegree(degree + 180.0);
                            int[] date = data.getDateAtSunPos(degree,
                                    location.getCountryName(),
                                    location.getCityName(),
                                    location.getZoneName(), null, true,
                                    (event.stateMask & SWT.SHIFT) != 0);
                            if (date != null) {
                                setBirthDate(date);
                                update(true, false);
                            }
                            return true;
                        }
                    }
                }
                double degree = tip.getDegreeFromPoint(event.x, event.y, false);
                if (tip.isDoubleValid(degree)) {
                    int[] date = data.getDateAtSunPos(
                            City.normalizeDegree(degree + 45.0),
                            location.getCountryName(), location.getCityName(),
                            location.getZoneName(), null, event.button == 1,
                            (event.stateMask & SWT.SHIFT) != 0);
                    if (date != null) {
                        setBirthDate(date);
                        update(true, false);
                    }
                    return true;
                }
            }
                break;
            case ChartMode.ASTRO_MODE:
                if (tip.hasDataFromPoint(event.x, event.y)) {
                    int year = tip.getIntFromLastPoint();
                    if (tip.isIntValid(year))
                        solarReturn(year);
                    double ut = tip.getDoubleFromLastPoint();
                    if (tip.isDoubleValid(ut))
                        lunarReturn(ut);
                    return true;
                }
                break;
            default:
            {
                if (data.getShowNow()) {
                    int year = tip.getIntFromPoint(event.x, event.y);
                    if (tip.isIntValid(year)) {
                        int[] date = new int[5];
                        date[0] = year;
                        date[1] = 7;
                        date[2] = 1;
                        date[3] = 12;
                        date[4] = 0;
                        setNowDate(date);
                        update(true, false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean solarReturn(int year)
    {
        CacheEntry entry = cache_record[ChartMode.NATAL_MODE];
        int[] date = entry.entry.getBirthDay();
        if (date == null)
            return false;
        if (year > date[0]) {
            date[0] = year;
            date[1] = date[2] = 1;
            date[3] = date[4] = 0;
            date = data.getDateAtSunPos(-1.0, location.getCountryName(),
                    location.getCityName(), location.getZoneName(), date, true,
                    false);
        } else {
            return false;
        }
        if (date == null) {
            birth.setCalendar(entry.entry.getBirthDay());
            MenuFolder.setAstroMode(ChartMode.NATAL_MODE);
        } else {
            birth.setCalendar(date);
            MenuFolder.setAstroMode(ChartMode.SOLAR_RETURN_MODE);
        }
        update(false, false);
        addAstroControl();
        return true;
    }

    public boolean lunarReturn(double ut)
    {
        if (ut == Double.MIN_VALUE)
            return false;
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(ut, date);
        data.updateLocation(location.getCityName(), location.getCountryName());
        BaseCalendar.addZoneOffset(location.getZoneName(), date, 0, true);
        birth.setCalendar(date);
        MenuFolder.setAstroMode(ChartMode.LUNAR_RETURN_MODE);
        update(false, false);
        addAstroControl();
        return true;
    }

    public String[] getTransitData(int mode)
    {
        return data.getTransitData(cache_record[ChartMode.NATAL_MODE].entry,
                location.getCountryName(), location.getCityName(),
                location.getZoneName(), mode);
    }

    public String[] getSearchResult(String prefix, boolean local)
    {
        return data.getSearchResult(location.getCountryName(),
                location.getCityName(), location.getZoneName(), prefix, local);
    }

    public boolean setDateFromLunarDate(int[] date, boolean leap)
    {
        int[] c_date = getCal().getDateFromLunarDate(date, leap);
        if (c_date == null)
            return false;
        setBirthDate(c_date);
        return true;
    }

    public void restoreBirthDay()
    {
        CacheEntry entry = cache_record[ChartMode.NATAL_MODE];
        if (entry != null)
            entry.restore(true);
    }

    public void setTransitTime(int mode, double ut, String country,
            String city, String zone, boolean set_now, boolean local)
    {
        MenuFolder.setAstroMode(mode);
        setTime(ut, country, city, zone, set_now, local);
        addAstroControl();
    }

    public void setTransitTime(int mode, double ut, boolean set_now,
            boolean local)
    {
        MenuFolder.setAstroMode(mode);
        setTime(ut, location.getCountryName(), location.getCityName(),
                location.getZoneName(), set_now, local);
        addAstroControl();
    }

    public void setTime(double ut, String country, String city, String zone,
            boolean set_now, boolean local)
    {
        if (country != null)
            location.setName(country, city, zone);
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(ut, date);
        if (!local)
            BaseCalendar.addZoneOffset(zone, date, 0, true);
        if (set_now)
            now.setCalendar(date);
        else
            birth.setCalendar(date);
        update(false, false);
    }

    public void setTime(double ut, boolean set_now, boolean local)
    {
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(ut, date);
        if (!local)
            BaseCalendar.addZoneOffset(location.getZoneName(), date, 0, true);
        if (set_now)
            now.setCalendar(date);
        else
            birth.setCalendar(date);
        update(false, false);
    }

    public void setEightCharTime(double ut)
    {
        birth.setCalendar(data.getWallTime(ut, location.getCountryName(),
                location.getCityName(), location.getZoneName()));
        update(true, true);
    }

    public boolean dumpAllPlanetData(String dir, int start_year, int end_year,
            boolean speed_group)
    {
        return getCal().dumpPlanetData(dir, start_year, end_year, speed_group);
    }

    public String getDateStringFromUJulianDayUT(double ut)
    {
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(ut, date);
        BaseCalendar.addZoneOffset(location.getZoneName(), date, 0, true);
        return BaseCalendar.formatDate(date, false, false);
    }

    public void setMultiMode(DataEntry other)
    {
        CacheEntry entry = getCacheEntry(ChartMode.ALT_NATAL_MODE);
        entry.save(other);
        entry.saveNameSex(other);
        update(false, false);
        addAstroControl();
    }

    public void setSingleWheelMode(boolean set)
    {
        CacheEntry entry = getCacheEntry();
        entry.single = set;
    }

    private void setLastAstroMode(int mode)
    {
        CacheEntry entry = cache_record[mode];
        if (entry == null)
            return;
        reset_draw = DrawSWT.isMarkerEnabled();
        MenuFolder.setAstroMode(mode);
        ChartMode.setSingleWheelMode(entry.single);
        location.setAllowCityMatch(false);
        entry.restore(false);
        String name = entry.getName();
        setName(name);
        setSex(entry.getSex());
        location.setAllowCityMatch(true);
        if (entry.tip == null) { // first time
            update(false, false);
            addAstroControl();
        } else {
            Moira.setGroupName();
            Moira.updateOverride();
            setBirthGroupName();
            tip_handler.setTipData(entry.tip);
            data.setEclipseData(entry.eclipse_data);
            getDiagram().redraw();
            desc[0].redraw();
            desc[1].redraw();
            FolderToolBar.updateFolderBarState();
        }
    }

    public boolean hasAstroData()
    {
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            return false;
        return numModeChoice() > 1;
    }

    public void updateNonAstroTab(int index)
    {
        boolean show_tab = Resource.getPrefInt("show_" + tabs[index].getType()) != 0;
        boolean has_tab = TabManager.hasTabItem(tab_orders[index]);
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (show_tab && has_tab)
                showHideTab(index, false, false, false);
        } else {
            if (show_tab && !has_tab)
                showHideTab(index, true, false, false);
        }
    }

    public void dockControl(Control ctrl, String prefix, boolean dock)
    {
        Shell shell = Moira.getShell();
        boolean maximized = shell.getMaximized();
        Rectangle shell_bound = shell.getBounds();
        if (dock) {
            int height = lr_combo.getSize().y;
            int trim = shell_bound.height - height;
            int[] win_bound = FileIO.toIntArray(Resource.getPrefString(prefix
                    + "bounds"));
            shell_bound.height += win_bound[3];
            ctrl.setParent(dock_sash);
            ctrl.setVisible(true);
            if (!maximized) {
                setSashWeights(new int[] {
                        height,
                        shell_bound.height - trim - height
                                - dock_sash.SASH_WIDTH });
            }
        } else {
            int height = ctrl.getSize().y;
            shell_bound.height -= height + dock_sash.SASH_WIDTH;
            ctrl.setParent(TabManager.getPlaceHolder());
            setSashWeights(new int[] { 100 });
        }
        Resource.putPrefInt("has_docked", dock ? 1 : 0);
        if (!maximized) {
            Resource.putPrefString("bounds", shell_bound.x + ", "
                    + shell_bound.y + ", " + shell_bound.width + ", "
                    + shell_bound.height);
            Moira.setShellPosition(null, null);
            Moira.setShellState();
        }
        dock_sash.layout();
    }

    private void setSashWeights(int[] weights)
    {
        String prefix = Moira.getShell().getMaximized() ? "maximized_" : "";
        if (weights[0] == 0) {
            if (!Resource.hasPrefKey(prefix + "sash_weights")) {
                if (!Resource.hasPrefKey("sash_weights"))
                    return;
                prefix = "";
            }
            weights = Resource.getPrefIntArray(prefix + "sash_weights");
        } else {
            Resource.putPrefIntArray(prefix + "sash_weights", weights);
        }
        dock_sash.setWeights(weights);
    }

    public void saveSashWeights()
    {
        String prefix = Moira.getShell().getMaximized() ? "maximized_" : "";
        Resource.putPrefIntArray(prefix + "sash_weights",
                dock_sash.getWeights());
    }

    public void setSashWeights()
    {
        String prefix = Moira.getShell().getMaximized() ? "maximized_" : "";
        if (!Resource.hasPrefKey(prefix + "sash_weights")) {
            if (!Resource.hasPrefKey("sash_weights"))
                return;
            prefix = "";
        }
        int[] weights = Resource.getPrefIntArray(prefix + "sash_weights");
        int[] old_weights = dock_sash.getWeights();
        if (weights.length != old_weights.length)
            dock_sash.setWeights(weights);
    }

    public void showHideTab(int index, boolean show, boolean on_top,
            boolean update_f)
    {
        ChartTab.hideTip();
        if (show) {
            if (TabManager.hasTabItem(tab_orders[index]))
                TabManager.removeTabItem(tab_orders[index]);
            int folder = tabs[index].getFolderIndex();
            if (ui_mode && folder == TabManager.SUB_FOLDER) {
                folder = TabManager.MAIN_FOLDER;
            }
            TabManager.setTabItem(folder, tab_orders[index],
                    tab_controls[index], tabs[index].getType(), on_top);
            tabs[index].updateAttribute(true);
        } else {
            TabManager.removeTabItem(tab_orders[index]);
        }
        if (update_f) {
            Resource.putPrefInt("show_" + tabs[index].getType(), show ? 1 : 0);
            Moira.getMenu().showTabMenu(index, show);
        }
    }

    public void restoreTabPos(Composite composite, boolean hide)
    {
        for (int i = 0; i < NUM_TAB; i++) {
            if (composite == tab_controls[i]
                    && !((i == POLE_TAB || i == EVAL_TAB) && ChartMode
                            .isChartMode(ChartMode.ASTRO_MODE))) {
                if (hide)
                    showHideTab(i, false, false, false);
                int pos = Resource.getPrefInt(tabs[i].getType()
                        + "_folder_last");
                tabs[i].changePos(pos);
                showHideTab(i, true, true, true);
                return;
            }
        }
    }

    public DataTab getTopTab()
    {
        for (int i = 0; i < NUM_TAB; i++) {
            if (!tabs[i].isTabVisible())
                continue;
            if (TabManager.tabOnTop(tab_orders[i]))
                return tabs[i];
        }
        return null;
    }

    public void changeTabPos(int index, int pos)
    {
        if (!tabs[index].isTabVisible())
            return;
        boolean on_top = TabManager.tabOnTop(tab_orders[index]);
        showHideTab(index, false, on_top, false);
        tabs[index].changePos(pos);
        showHideTab(index, true, on_top, false);
    }

    public void changeTabPos(Composite composite, int pos)
    {
        for (int i = 0; i < NUM_TAB; i++) {
            if (composite == tab_controls[i]) {
                changeTabPos(i, pos);
                return;
            }
        }
    }

    private void showData(GC gc, ScrolledComposite desc_scroll, Canvas canvas,
            boolean use_birth)
    {
        boolean show_horiz = data.getShowHoriz();
        if (show_horiz || ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (!use_birth)
                return;
            CacheEntry entry = getCacheEntry();
            String[] array = show_horiz ? entry.horiz_data : entry.astro_data;
            if (array == null || array[2] == null)
                return;
            boolean dual_ring = ChartMode.isAstroDualRingMode();
            setFittedFontInCanvas(gc, "astro", desc_scroll.getSize());
            FontMetrics metric = gc.getFontMetrics();
            int font_height = metric.getHeight();
            int font_width = metric.getAverageCharWidth();
            Point v_size = null, t_size = null;
            if (array[0] != null) {
                v_size = DrawSWT.drawStringVert(gc, array[0], 0, 0, true);
            }
            t_size = DrawSWT.drawStringHoriz(gc, array[1], 0, 0, true);
            Point l_size = DrawSWT.drawStringHoriz(gc, array[2], 0, 0, true);
            Point r_size = (array.length > 3) ? DrawSWT.drawStringHoriz(gc,
                    array[3], 0, 0, true) : (new Point(0, 0));
            int width = l_size.x + font_width + r_size.x;
            width = Math.max(width, t_size.x);
            if (v_size != null)
                width += v_size.x;
            int height = Math.max(l_size.y, r_size.y) + t_size.y;
            if (v_size != null)
                height = Math.max(height, v_size.y);
            height += 2 * font_height;
            desc_scroll.setMinSize((int) (DESC_SCROLL_SCALER * width),
                    (int) (DESC_SCROLL_SCALER * height));
            Point c_size = canvas.getSize();
            int x = (c_size.x - width) / 2;
            int y = (c_size.y - height) / 2;
            y = Math.min(y, x);
            if (v_size != null) {
                DrawSWT.drawStringVert(gc, array[0], c_size.x - x, y, false);
            }
            DrawSWT.drawStringHoriz(gc, array[1], x, y, false);
            y += t_size.y;
            if (data.getNoColor()) {
                DrawSWT.drawStringHoriz(gc, array[2], x, y, false);
                if (array.length > 3) {
                    x += l_size.x + font_width;
                    DrawSWT.drawStringHoriz(gc, array[3], x, y, false);
                }
            } else {
                String[] speed_state = data.getSpeedColorNameArray();
                int[] color = data.getSpeedColorArray(!(ChartMode
                        .isAstroMode(ChartMode.NATAL_MODE) || dual_ring));
                DrawSWT.setSpecialStringColor(color, speed_state);
                DrawSWT.drawStringHoriz(gc, array[2], x, y, false);
                DrawSWT.disposeSpecialStringColor();
                if (array.length > 3) {
                    x += l_size.x + font_width;
                    color = data.getSpeedColorArray(true);
                    DrawSWT.setSpecialStringColor(color, speed_state);
                    DrawSWT.drawStringHoriz(gc, array[3], x, y, false);
                    DrawSWT.disposeSpecialStringColor();
                }
            }
            if (!show_horiz && !dual_ring) {
                // elements
                int tot_width = 0;
                for (int i = 4; i < 8; i++) {
                    v_size = DrawSWT.drawStringHoriz(gc, array[i], 0, 0, true);
                    tot_width += v_size.x;
                }
                int gap_width = (width - tot_width) / 3;
                x = (c_size.x - width) / 2;
                y = (c_size.y - height) / 2;
                y = Math.min(y, x);
                y = c_size.y - y - 2 * font_height;
                int[] pos = new int[4];
                for (int i = 4; i < 8; i++) {
                    pos[i - 4] = x;
                    v_size = DrawSWT.drawStringHoriz(gc, array[i], x, y, false);
                    x += v_size.x + gap_width;
                }
                // elemental states
                y += font_height;
                for (int i = 8; i < 11; i++) {
                    DrawSWT.drawStringHoriz(gc, array[i], pos[i - 8], y, false);
                }
            }
        } else {
            String str = data.getYearInfo(use_birth, null);
            if (str == null)
                return;
            setFittedFontInCanvas(gc, "year", desc_scroll.getSize());
            Point d_size = DrawSWT.drawStringVert(gc, str, 0, 0, true);
            desc_scroll.setMinSize((int) (DESC_SCROLL_SCALER * d_size.x),
                    (int) (DESC_SCROLL_SCALER * d_size.y));
            Point c_size = canvas.getSize();
            int x = c_size.x - ((c_size.x - d_size.x) / 2);
            int y = (c_size.y - d_size.y) / 2;
            DrawSWT.drawStringVert(gc, str, x, y, false);
        }
    }

    private void setFittedFontInCanvas(GC gc, String type, Point s_size)
    {
        int[] font_size = new int[3], data_size = new int[2];
        Resource.getIntArray("swt_font_size", font_size);
        Resource.getIntArray("swt_" + type + "_data_size", data_size);
        FontMetrics metric;
        for (int size = font_size[1]; size >= font_size[0]; size -= font_size[2]) {
            if (font != null)
                font.dispose();
            font = new Font(Display.getCurrent(), FontMap.getSwtFontName(),
                    size, MenuFolder.getSwtFontStyle());
            gc.setFont(font);
            metric = gc.getFontMetrics();
            int font_height = metric.getHeight();
            int font_width = font_height;
            int max_height = data_size[0] * font_height;
            int max_width = (int) (DESC_WIDTH_SCALER * data_size[1] * font_width);
            if ((int) (DESC_DATA_SCALER * max_width) <= s_size.x
                    && (int) (DESC_DATA_SCALER * max_height) <= s_size.y)
                break;
        }
    }

    public void reset()
    {
        MenuFolder.setAstroMode(ChartMode.NATAL_MODE);
        data.reset();
        birth.reset();
        now.reset();
        clearCacheRecord(false, false);
    }

    public void addNowControl(boolean on_top)
    {
        if (TabManager.getTabItem(TabManager.SUB_FOLDER,
                TabManager.NOW_TAB_ORDER) != null)
            return;
        now_group.setParent(ctrl_composite);
        now_group.moveBelow(group);
        TabManager.setTabItem(TabManager.SUB_FOLDER, TabManager.NOW_TAB_ORDER,
                desc_group[1], "current_date", on_top);
        Composite composite = ui_mode ? ui_diagram : top_composite;
        composite.layout();
        composite.update();
    }

    public void removeNowControl()
    {
        if (TabManager.getTabItem(TabManager.SUB_FOLDER,
                TabManager.NOW_TAB_ORDER) == null)
            return;
        TabManager.removeTabItem(TabManager.NOW_TAB_ORDER);
        now_group.setParent(TabManager.getPlaceHolder());
        Composite composite = ui_mode ? ui_diagram : top_composite;
        composite.layout();
        composite.update();
    }

    public void addPickControl()
    {
        if (pick_composite.getParent() == ctrl_composite)
            return;
        pick_degree.setText("");
        pick_composite.setParent(ctrl_composite);
        Composite composite = ui_mode ? ui_diagram : top_composite;
        composite.layout();
        composite.update();
        reset();
        CacheEntry entry = getCacheEntry();
        entry.setName(null);
        tabs[NOTE_TAB].setNote(null);
    }

    public void removePickControl()
    {
        if (pick_composite.getParent() == TabManager.getPlaceHolder())
            return;
        pick_composite.setParent(TabManager.getPlaceHolder());
        Composite composite = ui_mode ? ui_diagram : top_composite;
        composite.layout();
        composite.update();
    }

    public void addAstroControl()
    {
        clearCacheRecord(true, false);
        int num_choice = numModeChoice();
        if (num_choice < 2)
            return;
        astro_group.setVisible(false);
        String[] astro_mode_button_label = Resource
                .getStringArray("astro_mode_name");
        String chart_char = Resource.getString("chart_char");
        boolean add_suffix = cache_record[ChartMode.NATAL_MODE] != null
                && cache_record[ChartMode.ALT_NATAL_MODE] != null;
        String[] numbers = add_suffix ? Resource.getStringArray("numbers")
                : null;
        for (int i = 0; i < cache_record.length; i++) {
            if (cache_record[i] == null) {
                if (astro_mode_button[i] != null) {
                    astro_mode_button[i].dispose();
                    astro_mode_button[i] = null;
                }
            } else {
                if (astro_mode_button[i] == null) {
                    astro_mode_button[i] = new Button(astro_group, SWT.RADIO);
                    String name = astro_mode_button_label[i] + chart_char;
                    if (add_suffix) {
                        if (i == ChartMode.NATAL_MODE)
                            name += numbers[1];
                        else if (i == ChartMode.ALT_NATAL_MODE)
                            name += numbers[2];
                    }
                    astro_mode_button[i].setText(name);
                    astro_mode_button[i]
                            .addSelectionListener(new SelectionAdapter() {
                                public void widgetSelected(SelectionEvent event)
                                {
                                    Button push = (Button) event.getSource();
                                    if (!push.getSelection())
                                        return;
                                    for (int j = 0; j < astro_mode_button.length; j++) {
                                        if (push == astro_mode_button[j]) {
                                            Moira.flushEvents(false);
                                            setLastAstroMode(j);
                                            Moira.flushEvents(false);
                                            return;
                                        }
                                    }
                                }
                            });
                } else if (i == ChartMode.NATAL_MODE) {
                    String name = astro_mode_button_label[i] + chart_char;
                    if (add_suffix)
                        name += numbers[1];
                    astro_mode_button[i].setText(name);
                }
                astro_mode_button[i].setSelection(ChartMode.isAstroMode(i));
            }
        }
        GridLayout layout = (GridLayout) astro_group.getLayout();
        layout.numColumns = Math.min(4, num_choice);
        astro_group.setVisible(true);
        if (astro_group.getParent() != ctrl_composite)
            astro_group.setParent(ctrl_composite);
        astro_group.layout();
        astro_group.update();
        ctrl_composite.layout();
        ctrl_composite.update();
        Composite composite = ui_mode ? ui_diagram : top_composite;
        composite.layout();
        composite.update();
        FolderToolBar.updateFolderBarState();
    }

    private int numModeChoice()
    {
        int num_choice = 0;
        for (int i = 0; i < cache_record.length; i++) {
            if (cache_record[i] != null)
                num_choice++;
        }
        return num_choice;
    }

    public void removeAstroControl()
    {
        if (astro_group.getParent() == TabManager.getPlaceHolder())
            return;
        clearCacheRecord(false, false);
        astro_group.setParent(TabManager.getPlaceHolder());
        Composite composite = ui_mode ? ui_diagram : top_composite;
        composite.layout();
        composite.update();
    }

    private void showDiagram(PaintEvent event, Point size)
    {
        CacheEntry entry = getCacheEntry();
        if (!entry.sameSize(entry.getImage(), size)) {
            event.gc.fillRectangle(event.x, event.y, event.width, event.height);
            if (entry.getImage() != null)
                entry.getImage().dispose();
            entry.setImage(createImage(entry, size, ui_mode));
            if (ui_mode) {
                int edge_spacing = Resource.getInt("ui_diagram_edge_spacing");
                int image_width = getUIDiagramWidth(size.x, edge_spacing);
                int image_height = size.y - 2 * edge_spacing;
                ui_diagram.setTrigger(Math.min(image_width, image_height)
                        + edge_spacing);
                if (ui_diagram.showUIHint()) {
                    tip_handler.showTimerHint(
                            Resource.getString("hint_show_form"), ui_diagram,
                            -10, 10, HINT_DELAY);
                }
            }
            reset_draw = DrawSWT.isMarkerEnabled();
        }
        DrawSWT.drawCancel();
        event.gc.drawImage(entry.getImage(), 0, 0);
        if (entry.tip != null)
            entry.tip.setTipScale(entry.getDrawSize());
        if (reset_draw) {
            DrawSWT.endMarker();
            DrawSWT.initMarker(getDiagram(), data.getAspectColorArray(true),
                    data.getAspectDisplayArray(true),
                    data.getAspectDegreeArray(true),
                    Resource.getInt("angle_marker_width"));
            int[] pos = data.getAngleMarkerArray(false, ui_mode,
                    entry.getRadius());
            DrawSWT.initMarkerPos(entry.getDrawSize(), pos);
            reset_draw = false;
        }
        DrawSWT.paintMarker(event.gc);
    }

    private Image createImage(final CacheEntry entry, final Point size,
            final boolean render_ui_mode)
    {
        return new Image(Display.getCurrent(), new ImageDataProvider() {
            private Hashtable cache = new Hashtable();

            public ImageData getImageData(int zoom)
            {
                Integer key = new Integer((zoom <= 0) ? 100 : zoom);
                ImageData image_data = (ImageData) cache.get(key);
                if (image_data == null) {
                    image_data = renderImageData(entry, size, render_ui_mode,
                            key.intValue());
                    cache.put(key, image_data);
                }
                return image_data;
            }
        });
    }

    private ImageData renderImageData(CacheEntry entry, Point size,
            boolean render_ui_mode, int zoom)
    {
        int image_width = scaleForZoom(size.x, zoom);
        int image_height = scaleForZoom(size.y, zoom);
        BufferedImage image = new BufferedImage(image_width, image_height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        applyRenderHints(g2d);
        double x_zoom = (double) image_width / size.x;
        double y_zoom = (double) image_height / size.y;
        g2d.scale(x_zoom, y_zoom);
        if (render_ui_mode) {
            int edge_spacing = Resource.getInt("ui_diagram_edge_spacing");
            int width = Resource.DIAGRAM_WIDTH;
            int scaler = getScreenScaler(zoom);
            int draw_width = getUIDiagramWidth(size.x, edge_spacing);
            int draw_height = size.y - 2 * edge_spacing;
            DrawAWT.setFillColor(g2d, "chart_window_bg_color",
                    data.getNoColor());
            g2d.fillRect(0, 0, size.x, size.y);
            g2d.translate(edge_spacing, edge_spacing);
            width *= scaler;
            double scale = (double) Math.min(draw_width, draw_height) / width;
            g2d.scale(scale, scale);
            int scaled_width = (int) (draw_width / scale);
            int scaled_height = (int) (draw_height / scale);
            data.pageDiagram(g2d, "", scaler, new java.awt.Point(scaled_width,
                    scaled_height), new java.awt.Point(width, width), false,
                    true, data.getNoColor(), false, false, false,
                    Resource.getPrefInt("display_vertical_text") != 0, true);
            entry.setDrawSize(data.getDrawSize(), scale, edge_spacing);
        } else {
            DrawAWT draw = new DrawAWT();
            draw.init(g2d, 1, "", false,
                    Resource.getPrefInt("display_vertical_text") != 0);
            data.drawDiagram(draw, null, new java.awt.Point(size.x, size.y),
                    data.getNoColor(), false, false, true);
            entry.setDrawSize(data.getDrawSize(), 1.0, 0);
        }
        g2d.dispose();
        return copyImageData(image);
    }

    private void applyRenderHints(Graphics2D g2d)
    {
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private int getScreenScaler(int zoom)
    {
        int scaler = Math.max(1, (zoom + 99) / 100);
        return Math.min(4, scaler);
    }

    private int getUIDiagramWidth(int width, int edge_spacing)
    {
        int total = Math.max(1, width - 2 * edge_spacing);
        int reserved = ui_diagram.getReservedWidth();
        int available = total - reserved;
        int min_width = Resource.DIAGRAM_WIDTH / 2;
        if (available < Math.min(min_width, total))
            available = total;
        return Math.max(1, available);
    }

    private int scaleForZoom(int val, int zoom)
    {
        if (zoom <= 100)
            return val;
        return Math.max(1, (val * zoom + 99) / 100);
    }

    private ImageData copyImageData(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        ImageData image_data = new ImageData(width, height, 24, PALETTE_DATA);
        image_data.transparentPixel = TRANSPARENT_COLOR;
        byte[] swt_data = image_data.data;
        int[] g2d_data = new int[width * height];
        image.getRGB(0, 0, width, height, g2d_data, 0, width);
        for (int i = 0; i < height; i++) {
            int idx = i * image_data.bytesPerLine;
            for (int j = 0; j < width; j++) {
                int rgb = g2d_data[j + i * width];
                for (int k = image_data.depth - 8; k >= 0; k -= 8) {
                    swt_data[idx++] = (byte) ((rgb >> k) & 0xFF);
                }
            }
        }
        return image_data;
    }

    public void setBirthDate(int[] date)
    {
        if (date == null) {
            birth.reset();
        } else {
            birth.setCalendar(date);
        }
    }

    public void getBirthDate(int[] date)
    {
        birth.getCalendar(date);
    }

    public void setNowDate(int[] date)
    {
        if (date == null)
            now.reset();
        else
            now.setCalendar(date);
    }

    public void getNowDate(int[] date)
    {
        now.getCalendar(date);
    }

    public void setName(String name)
    {
        CacheEntry entry = getCacheEntry();
        entry.setName(name);
        ui_diagram.setName(name);
        setUserGroupName();
    }

    public String getName()
    {
        CacheEntry entry = getCacheEntry();
        return entry.getName();
    }

    public void setSex(boolean sex)
    {
        CacheEntry entry = getCacheEntry();
        entry.setSex(sex);
        ui_diagram.setSex(sex);
    }

    public boolean getSex()
    {
        CacheEntry entry = getCacheEntry();
        return entry.getSex();
    }

    public void setDaySet(boolean set)
    {
        pick_choice.select(set ? 0 : 1);
        data.setDaySet(set);
    }

    public boolean getDaySet()
    {
        return pick_choice.getSelectionIndex() == 0;
    }

    public void setMountainPos(String pos)
    {
        pick_degree.setText(data.setMountainPos(pos));
    }

    public String getMountainPos()
    {
        return data.getMountainPos(false);
    }

    public LocationSpinner getSpinner()
    {
        return location;
    }

    public void updateAttribute()
    {
        for (int i = 0; i < NUM_TAB; i++)
            tabs[i].updateAttribute(false);
    }

    public void setGroupName(boolean add_mod)
    {
        for (int i = 0; i < NUM_TAB; i++)
            tabs[i].setGroupName(add_mod);
    }

    public void setUserGroupName()
    {
        String name;
        CacheEntry entry = getCacheEntry();
        if (!ChartMode.isMultipleMode(false)) {
            name = entry.getName();
        } else {
            CacheEntry b_entry = getCacheEntry(ChartMode.NATAL_MODE);
            String name1 = b_entry.getName();
            String name2 = entry.getName();
            if (name1.equals("") || name2.equals(""))
                name = name1 + name2;
            else
                name = data.getMergeName(name1, name2);
        }
        String mode = ChartMode.getModeName(false, true);
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            if (name.equals(""))
                group_name = Resource.getString("pick_chart");
            else
                group_name = name + " - " + Resource.getString("pick_chart");
        } else if (name.equals("")) {
            group_name = Resource.getString("chart");
        } else {
            group_name = name + Resource.getString("user_data");
        }
        if (mode != null)
            group_name += " - " + mode;
        if (Resource.hasCustomData()) {
            if (!group_name.equals(""))
                group_name += " - ";
            group_name += Resource.getModName();
        }
        user_group.setText(group_name);
        ui_group.setText(group_name);
    }

    public void updateOverride()
    {
        String str = data.getOverridenStatus();
        if (str.equals("")) {
            str = group_name;
        } else {
            str = group_name + "    [" + str + Resource.getString("mod_label")
                    + "]";
        }
        user_group.setText(str);
        ui_group.setText(str);
        for (int i = 0; i < NUM_TAB; i++)
            tabs[i].updateOverride();
    }

    public void setShowNow(boolean show, boolean force_update, boolean no_update)
    {
        ChartTab.hideTip();
        if (show != data.getShowNow()) {
            data.setShowNow(show);
            if (show) {
                addNowControl(false);
            } else {
                removeNowControl();
            }
            if (!no_update)
                Moira.update(false, false);
        } else if (force_update) {
            Moira.update(false, false);
        }
    }

    public void setDstAdjust(boolean adj)
    {
        if (adj != BaseCalendar.getDstAdjust()) {
            BaseCalendar.setDstAdjust(adj);
            Moira.update(false, false);
        }
    }

    public void refresh(boolean desc_only)
    {
        hideTip();
        if (!desc_only) {
            getCacheEntry().disposeImage();
            getDiagram().redraw();
        }
        desc[0].redraw();
        desc[1].redraw();
    }

    public void removeTip()
    {
        getCacheEntry().tip = null;
    }

    public void updateColor()
    {
        if (data != null) {
            ui_diagram.updateColor(data.getNoColor());
        }
        Color color = ColorManager.getColor("chart_window_bg_color");
        Color text_color = getContrastingTextColor(color);
        if (color != ui_diagram.getBackground()) {
            for (int i = 0; i < desc.length; i++) {
                desc[i].setBackground(color);
                desc[i].setForeground(text_color);
            }
            ui_diagram.setBackground(color);
            diagram.setBackground(color);
        } else {
            for (int i = 0; i < desc.length; i++) {
                desc[i].setForeground(text_color);
            }
        }
    }

    private Color getContrastingTextColor(Color background)
    {
        int red = background.getRed();
        int green = background.getGreen();
        int blue = background.getBlue();
        int brightness = red * 299 + green * 587 + blue * 114;
        return ColorManager.getColor((brightness >= 128000) ? 0x000000
                : 0xffffff);
    }

    public void updateAngleMarker(int x, int y)
    {
        if (DrawSWT.isMarkerEnabled())
            DrawSWT.endMarker();
        boolean[] enable = data.getAngleMarkerEnable();
        if (enable[0]) {
            CacheEntry entry = getCacheEntry();
            int[] pos = data.getAngleMarkerArray(false, ui_mode,
                    entry.getRadius());
            if (pos != null) {
                if (x >= 0 && y >= 0) { // toggle check
                    int[] draw_size = entry.getDrawSize();
                    int dist_sq = DrawSWT.getPointDistSqFromCenter(
                            draw_size[0], draw_size[1], x, y);
                    if (dist_sq <= draw_size[2] * draw_size[2])
                        enable[0] = false;
                }
            } else {
                enable[0] = false;
            }
            if (enable[0]) {
                DrawSWT.initMarker(getDiagram(),
                        data.getAspectColorArray(true),
                        data.getAspectDisplayArray(true),
                        data.getAspectDegreeArray(true),
                        Resource.getInt("angle_marker_width"));
                DrawSWT.initMarkerPos(entry.getDrawSize(), pos);
            } else {
                data.setAngleMarkerEnable(enable);
            }
        }
    }

    static public void hideTip()
    {
        tip_handler.hide();
    }

    static public void hideTimerHint()
    {
        tip_handler.hideTimerHint();
    }

    static public boolean isTipShell(Shell sh)
    {
        return tip_handler.isTipShell(sh);
    }

    private String savePoleDegree(int[] date)
    {
        String message = null;
        if (date != null)
            message = updateAdjNorth(date);
        double degree = City.parseLongLatitude(pole_degree.getText(), 'E', 'W');
        if (degree == City.INVALID)
            degree = 0.0;
        pole_degree.setText(City.formatLongLatitude(degree, true, true, false));
        if (Resource.getPrefInt("adj_north") == 0)
            Resource.putPrefDouble("magnetic_shift", degree);
        return message;
    }

    public String updateAdjNorth(int[] date)
    {
        boolean adj_north = Resource.getPrefInt("adj_north") != 0;
        String message = location.setMagneticShift(date, (ChartMode
                .isChartMode(ChartMode.PICK_MODE) && adj_north) ? pole_degree
                : null);
        pole_degree.setEditable(!adj_north);
        if (!adj_north) {
            double degree = Resource.hasPrefDouble("magnetic_shift") ? Resource
                    .getPrefDouble("magnetic_shift") : 0.0;
            pole_degree.setText(City.formatLongLatitude(degree, true, true,
                    false));
        }
        return message;
    }

    public void compute()
    {
        hideTip();
        CacheEntry birth_entry = cache_record[ChartMode.NATAL_MODE];
        CacheEntry entry = getCacheEntry();
        ChartMode.setSingleWheelMode(entry.single);
        if (entry.tip == null)
            entry.tip = new DiagramTip();
        DiagramTip tip = entry.tip;
        DataEntry cur_entry = null;
        if (ChartMode.isMultipleMode(false)) {
            CacheEntry alt_entry = getCacheEntry(ChartMode.ALT_NATAL_MODE);
            entry.saveNameSex(alt_entry.entry);
            if (ChartMode.isAstroMode(ChartMode.RELATIONSHIP_MODE)) {
                Calculate.computeMidPoint(birth_entry.entry, alt_entry.entry,
                        entry.entry);
                location.setAllowCityMatch(false);
                location.setName(entry.entry.getCountry(),
                        entry.entry.getCity(), entry.entry.getZone());
                birth.setCalendar(entry.entry.getBirthDay());
                entry.save(true);
                location.setAllowCityMatch(true);
            } else {
                entry.save(true);
                cur_entry = entry.entry;
                entry = alt_entry;
            }
        } else {
            entry.save(true);
        }
        String message = savePoleDegree(birth_entry.entry.getBirthDay());
        data.setMountainPos(pick_degree.getText());
        double degree = ChartMode.isChartMode(ChartMode.PICK_MODE) ? City
                .parseLongLatitude(pole_degree.getText(), 'E', 'W') : 0.0;
        if (degree == City.INVALID)
            degree = 0.0;
        data.setMagneticShift(degree, message);
        String err = data.compute(birth_entry.entry, entry.entry, cur_entry,
                tip);
        if (err != null) {
            Message.info(err + ".\nReset to current date.");
            reset();
            entry.save(true);
            err = data.compute(birth_entry.entry, entry.entry, cur_entry,
                    entry.tip);
            if (err != null) {
                Message.error(err
                        + ".\nThis could be due to missing data file.\nYou will need an active internet connection to download the missing data file.");
            }
        }
        tip_handler.setTipData(tip);
        if (data.getShowHoriz() || ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            entry = cache_record[ChartMode.getAstroMode()];
            entry.save(false);
            if (!ChartMode.isMultipleMode(true))
                entry.saveNameSex(birth_entry.entry);
        }
        pick_degree.setText(data.getMountainPos(false));
        setBirthGroupName();
        refresh(false);
        for (int i = 0; i < NUM_TAB; i++)
            tabs[i].update();
    }

    private void setBirthGroupName()
    {
        String label, tab_key, sub_tab_key;
        String[] array = Resource.getStringArray("astro_mode_name");
        String suffix = Resource.getString("method")
                + Resource.getString("time_and_place");
        int mode = ChartMode.getAstroMode();
        switch (mode) {
            case ChartMode.ALT_NATAL_MODE:
                label = Resource.getString("birthday_and_place");
                tab_key = "birth_chart";
                sub_tab_key = "astro_tab";
                birth.setEnabled(false);
                location.setEnabled(false);
                break;
            case ChartMode.SOLAR_RETURN_MODE:
            case ChartMode.LUNAR_RETURN_MODE:
                label = array[mode] + suffix;
                tab_key = "astro_group";
                sub_tab_key = "astro_tab";
                birth.setEnabled(false);
                location.setEnabled(true);
                break;
            case ChartMode.COMPARISON_MODE:
            case ChartMode.RELATIONSHIP_MODE:
            case ChartMode.COMPOSITE_MODE:
                label = array[mode] + suffix;
                tab_key = "astro_group";
                sub_tab_key = "astro_tab";
                birth.setEnabled(false);
                location.setEnabled(false);
                break;
            case ChartMode.TRANSIT_MODE:
            case ChartMode.PRIMARY_DIRECTION_MODE:
            case ChartMode.SECONDARY_PROGRESSION_MODE:
            case ChartMode.SOLAR_ARC_MODE:
                label = array[mode] + suffix;
                tab_key = "astro_group";
                sub_tab_key = "astro_tab";
                birth.setEnabled(true);
                location.setEnabled(true);
                break;
            default:
                switch (ChartMode.getChartMode()) {
                    case ChartMode.PICK_MODE:
                        label = Resource.getString("pick_birthday_and_place");
                        tab_key = "pick_birth_date";
                        sub_tab_key = "year_data_plus_eight_char";
                        break;
                    case ChartMode.ASTRO_MODE:
                        label = Resource.getString("birthday_and_place");
                        tab_key = "birth_chart";
                        sub_tab_key = "astro_tab";
                        break;
                    default:
                        label = Resource.getString("birthday_and_place");
                        tab_key = "birth_date";
                        sub_tab_key = "year_data_plus_eight_char";
                        break;
                }
                birth.setEnabled(true);
                location.setEnabled(true);
                break;
        }
        group.setText(label);
        CTabItem item = TabManager.getTabItem(TabManager.SUB_FOLDER,
                TabManager.BIRTH_TAB_ORDER);
        if (item != null)
            item.setText(Resource.getString(tab_key));
        desc_group[0].setText(Resource.getString(sub_tab_key));
    }

    public void update(boolean add_entry, boolean preserve)
    {
        location.setAllowCityMatch(ChartMode.isAstroMode(ChartMode.NATAL_MODE));
        if (!preserve && !ChartMode.isMultipleMode(false)
                && TabManager.tabOnTop(TabManager.TABLE_TAB_ORDER)) {
            Moira.getTable().updateChart(true);
        } else {
            if (ChartMode.isAstroMode(ChartMode.NATAL_MODE)) {
                if (!preserve)
                    removeAstroControl();
            } else {
                add_entry = false;
            }
            Moira.update(false, true);
            if (add_entry)
                Moira.getTable().addCurrentEntry(false);
        }
        location.setAllowCityMatch(true);
    }

    public void resetCities()
    {
        location.resetCities();
    }

    static public ChartData getData()
    {
        return data;
    }

    static public Calculate getCal()
    {
        return data.getCal();
    }

    static public DataTab getTab(int index)
    {
        return tabs[index];
    }

    public void dispose()
    {
        DrawSWT.endMarker();
        for (int i = 0; i < NUM_TAB; i++)
            tabs[i].dispose();
        if (font != null)
            font.dispose();
        ui_diagram.dispose();
        tip_handler.dispose();
        clearCacheRecord(false, true);
        birth.dispose();
        now.dispose();
        location.dispose();
    }

    public CacheEntry getCacheEntry()
    {
        return getCacheEntry(ChartMode.getAstroMode());
    }

    private CacheEntry getCacheEntry(int index)
    {
        if (cache_record[index] == null)
            cache_record[index] = new CacheEntry();
        return cache_record[index];
    }

    public void clearCacheRecord(boolean check, boolean all)
    {
        boolean multi = ChartMode.isMultipleMode(true);
        for (int i = 0; i < cache_record.length; i++) {
            if (!all && i == ChartMode.NATAL_MODE)
                continue;
            if (check && multi == ChartMode.isMultipleMode(i, true))
                continue;
            if (cache_record[i] != null)
                cache_record[i].dispose();
            cache_record[i] = null;
        }
        if (!check)
            MenuFolder.disposeSubWin();
    }

    public void toggleUI()
    {
        Moira.setCursor(SWT.CURSOR_WAIT, true);
        Moira.saveShellBounds(null, null);
        Moira.getShell().setVisible(false);
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            setLastAstroMode(ChartMode.NATAL_MODE);
            Moira.update(false, false);
        }
        if (top_layout.topControl == (Control) combo) {
            ui_mode = true;
            for (int i = 0; i < NUM_TAB; i++) {
                if (TabManager
                        .tabInFolder(TabManager.SUB_FOLDER, tab_orders[i])) {
                    showHideTab(i, false, false, false);
                    showHideTab(i, true, false, false);
                }
            }
            moveEntryField(ui_diagram.getEntryField());
            top_layout.topControl = ui_group;
        } else {
            ui_mode = false;
            moveEntryField(null);
            for (int i = 0; i < NUM_TAB; i++) {
                if (TabManager.tabInFolder(TabManager.MAIN_FOLDER,
                        tab_orders[i])
                        && tabs[i].getFolderIndex() != TabManager.MAIN_FOLDER) {
                    showHideTab(i, false, false, false);
                    showHideTab(i, true, false, false);
                }
            }
            top_layout.topControl = combo;
        }
        Moira.setShellPosition(null, null);
        Moira.setShellState();
        Resource.putPrefInt("ui_mode", ui_mode ? 1 : 0);
        if (hasAstroData()) {
            setLastAstroMode(ChartMode.NATAL_MODE);
            removeAstroControl();
        }
        reset_draw = DrawSWT.isMarkerEnabled();
        top.layout();
        Moira.getMenu().updateMenu(null);
        Moira.setCursor(0, false);
        layout(true);
        Moira.flushEvents(false);
        Moira.getShell().setVisible(true);
    }

    public void layout(boolean force)
    {
        Point size = user_group.getSize();
        GridData grid_data = (GridData) user_group.getLayoutData();
        if (force || grid_data.widthHint != size.y) {
            grid_data.widthHint = size.y;
            lr_combo.layout();
        }
    }

    private void moveEntryField(Composite parent)
    {
        if (parent == null) {
            ctrl_composite.setParent(top_composite);
            ctrl_composite.moveAbove(sub_folder);
            top_composite.layout();
        } else {
            ctrl_composite.setParent(parent);
            ui_diagram.layout();
            top_composite.layout();
            parent.layout();
        }
    }

    public void moveLocationField(Group parent)
    {
        if (parent == null) {
            location.setParent(group);
            location.moveBelow(date_composite);
            group.layout();
        } else {
            location.setParent(parent);
            parent.layout();
        }
    }

    public void moveToField(boolean ui_only, boolean name_field)
    {
        if (ui_only && !ui_mode)
            return;
        if (ui_mode)
            ui_diagram.showUI();
        if (name_field)
            ui_diagram.moveToNameField();
        else
            Moira.moveToControl(update);
    }

    public void hideUIInfo()
    {
        if (ui_mode) {
            ui_diagram.hideInfo();
            ChartTab.hideTimerHint();
        }
    }

    static public boolean getUIMode()
    {
        return ui_mode;
    }

    public Canvas getDiagram()
    {
        return ui_mode ? ui_diagram : diagram;
    }

    public class CacheEntry {
        private DataEntry entry;

        private String[] astro_data, horiz_data;

        private SearchRecord[] eclipse_data;

        private int[] draw_size = new int[3], alt_draw_size = new int[3];

        private DiagramTip tip;

        private Image image, alt_image;

        private boolean single;

        public void save(boolean partial)
        {
            if (entry == null)
                entry = new DataEntry();
            // name and sex is eiter default or already been saved
            int[] date = new int[5];
            birth.getCalendar(date);
            entry.setBirthDay(date);
            now.getCalendar(date);
            entry.setNowDay(date);
            entry.setCountry(location.getCountryName());
            entry.setCity(location.getCityName());
            entry.setZone(location.getZoneName());
            if (partial)
                return;
            entry.setNote(tabs[DATA_TAB].getText());
            astro_data = data.getAstroData();
            horiz_data = data.getHorizData();
            eclipse_data = data.getEclipseData();
        }

        public void save(DataEntry d_entry)
        {
            if (entry == null) {
                entry = new DataEntry();
            } else if (entry.equals(d_entry, true)) {
                return;
            } else {
                disposeImage();
            }
            saveNameSex(d_entry);
            entry.setBirthDay(d_entry.getBirthDay());
            entry.setNowDay(d_entry.getBirthDay());
            entry.setCountry(d_entry.getCountry());
            entry.setCity(d_entry.getCity());
            entry.setZone(d_entry.getZone());
        }

        public void saveNameSex(DataEntry d_entry)
        {
            if (entry == null)
                entry = new DataEntry();
            entry.setName(d_entry.getName());
            entry.setSex(d_entry.getSex());
        }

        public void setName(String name)
        {
            if (entry == null)
                entry = new DataEntry();
            entry.setName(name);
        }

        public String getName()
        {
            if (entry == null)
                entry = new DataEntry();
            return entry.getName();
        }

        public void setSex(boolean sex)
        {
            if (entry == null)
                entry = new DataEntry();
            entry.setSex(sex);
        }

        public boolean getSex()
        {
            if (entry == null)
                entry = new DataEntry();
            return entry.getSex();
        }

        public Image getImage()
        {
            return ui_mode ? alt_image : image;
        }

        public void setImage(Image img)
        {
            if (ui_mode)
                alt_image = img;
            else
                image = img;
        }

        public boolean hasAnyImage()
        {
            return image != null || alt_image != null;
        }

        public int[] getDrawSize()
        {
            return ui_mode ? alt_draw_size : draw_size;
        }

        public void setDrawSize(int[] size, double scaler, int offset)
        {
            int[] array = getDrawSize();
            array[0] = ((int) (scaler * size[0] + 0.5)) + offset;
            array[1] = ((int) (scaler * size[1] + 0.5)) + offset;
            array[2] = (int) (scaler * size[2] + 0.5);
        }

        public int getRadius()
        {
            return getDrawSize()[2];
        }

        public void restore(boolean partial)
        {
            if (entry == null || !entry.isValid() || partial && !hasAnyImage())
                return;
            birth.setCalendar(entry.getBirthDay());
            now.setCalendar(entry.getNowDay());
            location.setName(entry.getCountry(), entry.getCity(),
                    entry.getZone());
            if (partial || !hasAnyImage())
                return;
            tabs[DATA_TAB].setText(entry.getNote(true));
        }

        public boolean sameSize(Image img, Point size)
        {
            if (img == null)
                return false;
            return img.getBounds().width == size.x
                    && img.getBounds().height == size.y;
        }

        public void dispose()
        {
            disposeImage();
        }

        public void disposeImage()
        {
            if (image != null)
                image.dispose();
            if (alt_image != null)
                alt_image.dispose();
            image = alt_image = null;
        }
    }
}
