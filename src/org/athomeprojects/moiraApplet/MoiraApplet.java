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
package org.athomeprojects.moiraApplet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.athomeprojects.awtext.CalendarSelect;
import org.athomeprojects.awtext.EditCombo;
import org.athomeprojects.awtext.LocationSelect;
import org.athomeprojects.base.BaseCalendar;
import org.athomeprojects.base.ChartData;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.City;
import org.athomeprojects.base.DataEntry;
import org.athomeprojects.base.DataSet;
import org.athomeprojects.base.DiagramTip;
import org.athomeprojects.base.DrawAWT;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.ImageControl;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Print;
import org.athomeprojects.base.Resource;
import org.athomeprojects.base.RuleEntry;

public class MoiraApplet extends JApplet {
    private static final long serialVersionUID = -3916303219541658637L;

    private final int NAME_WIDTH = 200;

    private final int MAX_NUM_RECORD = 20;

    private final String STORE = "store";

    private boolean has_access;

    private Container pane;

    private JTabbedPane tab_pane;

    private JPanel diagram_tab, data_tab, pole_tab, eval_tab, note_tab;

    private JCheckBox remember;

    private EditCombo name;

    private JComboBox sex;

    private AppletChart diagram;

    private AppletDataWrapper data_panel, pole_panel, eval_panel, note_panel;

    private CalendarSelect birth, now;

    private LocationSelect location;

    private ChartData data;

    private AppletIO io;

    private AppletData tab, pole, eval, note;

    private Print printer = null;

    private JButton print, reset, update, open, save, capture;

    private JComboBox font_select, image_size;

    private JTextField image_width, image_height;

    private JCheckBox dst_adj, time_adj;

    private String[] data_array;

    private boolean eval_tab_shown;

    private int eval_tab_index;

    private class SaveFilter extends FileFilter {
        public boolean accept(File file)
        {
            if (file.isDirectory())
                return true;
            String file_name = file.getName();
            int i = file_name.lastIndexOf('.');
            if (i <= 0 || i >= file_name.length() - 1)
                return false;
            String ext = file_name.substring(i + 1);
            for (int j = 0; j < data_array.length; j++) {
                if (ext.equalsIgnoreCase(data_array[j].substring(2)))
                    return true;
            }
            return false;
        }

        public String getDescription()
        {
            String allowed = "";
            for (int j = 0; j < data_array.length; j++) {
                if (j > 0)
                    allowed += ", ";
                allowed += data_array[j].substring(2);
            }
            return allowed;
        }
    }

    private class ImageFilter extends FileFilter {
        public boolean accept(File file)
        {
            if (file.isDirectory())
                return true;
            String file_name = file.getName();
            int i = file_name.lastIndexOf('.');
            if (i <= 0 || i >= file_name.length() - 1)
                return false;
            String ext = file_name.substring(i + 1);
            for (int j = 0; j < ImageControl.IMAGE_EXTENSIONS.length; j++) {
                if (ext.equalsIgnoreCase(ImageControl.IMAGE_EXTENSIONS[j]
                        .substring(2)))
                    return true;
            }
            return false;
        }

        public String getDescription()
        {
            String allowed = "";
            for (int j = 0; j < ImageControl.IMAGE_EXTENSIONS.length; j++) {
                if (j > 0)
                    allowed += ", ";
                allowed += ImageControl.IMAGE_EXTENSIONS[j].substring(2);
            }
            return allowed;
        }
    }

    public void init()
    {
        try {
            System.getProperty("java.home");
            has_access = true;
        } catch (SecurityException e) {
            has_access = false;
        }
        io = new AppletIO();
        io.setApplet(this);
        FileIO.setBaseIO(io);
        String mod_file = null, eval_file = null;
        if (has_access) {
            mod_file = io.getString("modification");
            if (mod_file != null)
                mod_file = Resource.LOCAL_PREFIX + mod_file;
            eval_file = io.getString("evaluation");
            if (eval_file != null)
                eval_file = Resource.LOCAL_PREFIX + eval_file;
        }
        new Resource(null, getParameter("Language"), io.getString("font_name"),
                mod_file, eval_file);
        new AppletMessage(this);
        Message.info("Initializing.  Please wait...");
        initUIManager();
        pane = getContentPane();
        pane.setBackground(Color.white);
        pane.setLayout(new BorderLayout());
        JPanel ctrl = new JPanel();
        ctrl.setBackground(Color.white);
        ctrl.setLayout(new GridLayout(3, 1, 0, 0));
        JPanel top_ctrl = new JPanel();
        top_ctrl.setBackground(Color.white);
        top_ctrl.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        birth = new CalendarSelect();
        birth.init(Resource.getString("birth_label"), false);
        top_ctrl.add(birth);
        top_ctrl.add(Box.createHorizontalStrut(20));
        location = new LocationSelect();
        location.init("cities.prop", null);
        top_ctrl.add(location);
        ctrl.add(top_ctrl);
        JPanel middle_ctrl = new JPanel();
        middle_ctrl.setBackground(Color.white);
        middle_ctrl.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        now = new CalendarSelect();
        now.init(Resource.getString("current_date"), true);
        now.setSelected(!io.getBoolean("not_show_now"));
        now.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                data.setShowNow(now.isSelected());
                io.setBoolean("not_show_now", !now.isSelected());
                compute();
            }
        });
        middle_ctrl.add(now);
        middle_ctrl.add(Box.createHorizontalStrut(20));
        createNameComboBox(new Font(Resource.getFontName(), Font.BOLD, 16),
                middle_ctrl, NAME_WIDTH, Resource.getString("tip_name"));
        Font font = new Font(Resource.getFontName(), Font.BOLD, 14);
        Font small_font = new Font(Resource.getFontName(), Font.PLAIN, 12);
        String[] selections = { Resource.getString("male"),
                Resource.getString("female") };
        sex = new JComboBox(selections);
        sex.setBackground(Color.white);
        sex.setFont(font);
        sex.setSelectedIndex(0);
        middle_ctrl.add(sex);
        middle_ctrl.add(Box.createHorizontalStrut(20));
        if (has_access) {
            open = createButton(Resource.getString("open_button"), font,
                    middle_ctrl);
            open.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    loadDataFrom();
                }
            });
            save = createButton(Resource.getString("save_button"), font,
                    middle_ctrl);
            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    saveDataAs();
                }
            });
        }
        print = createButton(Resource.getString("print_button"), font,
                middle_ctrl);
        print.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                diagram.hideTip();
                if (printer == null)
                    printer = new Print();
                String eval_str = eval_tab_shown ? eval.getText() : null;
                String note_str = note.getNote();
                printer.printPage(eval, eval_str, note, note
                        .hasValidNote(note_str) ? note_str : null);
            }
        });
        reset = createButton(Resource.getString("reset_button"), font,
                middle_ctrl);
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                if (has_access) {
                    if (io.hasKey("modification")) {
                        loadModification(null);
                        io.remove("modification");
                    }
                    if (io.hasKey("evaluation")) {
                        loadEvaluation(null);
                        io.remove("evaluation");
                    }
                    updateEvalTab();
                }
                sex.setSelectedIndex(0);
                reset(true);
                note.setNote(null);
                name.setSelectedItem(Resource.getString("nameless"));
                updateData();
            }
        });
        update = createButton(Resource.getString("update_button"), font,
                middle_ctrl);
        update.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                updateData();
            }
        });
        ctrl.add(middle_ctrl);
        JPanel bottom_ctrl = new JPanel();
        bottom_ctrl.setBackground(Color.white);
        bottom_ctrl.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        dst_adj = createCheckBox(Resource.getString("auto_correct")
                + Resource.getString("daylight_saving")
                + Resource.getString("time"), font, bottom_ctrl, !io
                .getBoolean("not_dst_adj"));
        dst_adj.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                BaseCalendar.setDstAdjust(dst_adj.isSelected());
                io.setBoolean("not_dst_adj", !dst_adj.isSelected());
                updateData();
            }
        });
        time_adj = createCheckBox(Resource.getString("apparent_solar")
                + Resource.getString("time"), font, bottom_ctrl, !io
                .getBoolean("not_time_adj"));
        time_adj.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                data.setTimeAdjust(time_adj.isSelected() ? 2 : 0);
                io.setBoolean("not_time_adj", !time_adj.isSelected());
                updateData();
            }
        });
        final String[] font_array = Resource.getFontArray(null);
        if (font_array != null) {
            bottom_ctrl.add(Box.createHorizontalStrut(20));
            font_select = createCombBox(font_array, small_font, bottom_ctrl);
            font_select.setSelectedItem(Resource.getFontName());
            font_select.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    int sel = font_select.getSelectedIndex();
                    if (sel >= 0) {
                        Resource.setFontName(font_array[sel]);
                        DrawAWT.resetFontTable();
                        tab.setFont();
                        pole.setFont();
                        eval.setFont();
                        note.setFont();
                        updateData();
                        io.setString("font_name", font_array[sel]);
                    }
                }
            });
        }
        if (has_access) {
            bottom_ctrl.add(Box.createHorizontalStrut(20));
            final String[] size_choice = new String[4];
            size_choice[ImageControl.LARGE_SIZE] = Resource
                    .getString("image_button_large");
            size_choice[ImageControl.MEDIUM_SIZE] = Resource
                    .getString("image_button_medium");
            size_choice[ImageControl.SMALL_SIZE] = Resource
                    .getString("image_button_small");
            size_choice[ImageControl.CUSTOM_SIZE] = Resource
                    .getString("image_button_custom");
            image_size = createCombBox(size_choice, small_font, bottom_ctrl);
            image_size.setSelectedIndex(ImageControl.MEDIUM_SIZE);
            image_size.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    int sel = image_size.getSelectedIndex();
                    int[] size = ImageControl.getSizeFromSelection(sel,
                            getImageSize());
                    setImageSizeState(sel);
                    image_width.setText(Integer.toString(size[0]));
                    image_height.setText(Integer.toString(size[1]));
                }
            });
            JPanel custom_ctrl = new JPanel();
            custom_ctrl.setBackground(Color.white);
            custom_ctrl.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            image_width = createTextField(small_font, custom_ctrl, 4);
            createLabel(new Font(Resource.getEnFontName(), Font.PLAIN, 12),
                    custom_ctrl, "x");
            image_height = createTextField(small_font, custom_ctrl, 4);
            initSize();
            bottom_ctrl.add(custom_ctrl);
            capture = createButton(Resource.getString("capture_button"), font,
                    bottom_ctrl);
            capture.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event)
                {
                    captureDataAs();
                }
            });
        }
        ctrl.add(bottom_ctrl);
        pane.add(ctrl, BorderLayout.PAGE_START);
        tab_pane = new JTabbedPane();
        tab_pane.setBackground(Color.white);
        tab_pane.setFont(font);
        diagram_tab = new JPanel();
        diagram_tab.setBackground(Color.white);
        diagram_tab.setLayout(new BorderLayout());
        diagram = new AppletChart();
        diagram.setBackground(Color.white);
        diagram_tab.add(diagram, BorderLayout.CENTER);
        int index = 0;
        tab_pane.add(diagram_tab, index);
        tab_pane.setTitleAt(index++, Resource.getString("chart"));
        data_tab = new JPanel();
        data_tab.setBackground(Color.white);
        data_tab.setLayout(new BorderLayout());
        data_panel = new AppletDataWrapper();
        data_tab.add(data_panel, BorderLayout.CENTER);
        tab_pane.add(data_tab, index);
        tab_pane.setTitleAt(index++, Resource.getString("data"));
        pole_tab = new JPanel();
        pole_tab.setBackground(Color.white);
        pole_tab.setLayout(new BorderLayout());
        pole_panel = new AppletDataWrapper();
        pole_tab.add(pole_panel, BorderLayout.CENTER);
        tab_pane.add(pole_tab, index);
        tab_pane.setTitleAt(index++, Resource.getString("pole"));
        eval_tab = new JPanel();
        eval_tab.setBackground(Color.white);
        eval_tab.setLayout(new BorderLayout());
        eval_panel = new AppletDataWrapper();
        eval_tab.add(eval_panel, BorderLayout.CENTER);
        tab_pane.add(eval_tab, index);
        eval_tab_index = index;
        tab_pane.setTitleAt(index++, Resource.getString("eval"));
        note_tab = new JPanel();
        note_tab.setBackground(Color.white);
        note_tab.setLayout(new BorderLayout());
        note_panel = new AppletDataWrapper();
        note_tab.add(note_panel, BorderLayout.CENTER);
        if (has_access) {
            tab_pane.add(note_tab, index);
            tab_pane.setTitleAt(index++, Resource.getString("note"));
        }
        tab_pane.setSelectedIndex(0);
        pane.add(tab_pane, BorderLayout.CENTER);
        initData();
        initKeyMap();
        compute();
        eval_tab_shown = true;
        updateEvalTab();
    }

    private void initUIManager()
    {
        UIManager.put("ComboBox.disabledBackground", Color.WHITE);
        UIManager.put("ToolTip.font", new Font(Resource.getFontName(),
                Font.PLAIN, 14));
    }

    public void destroy()
    {
        Resource.dispose();
        super.destroy();
    }

    public void setSize(int width, int height)
    {
        super.setSize(width, height);
        validate();
    }

    private int[] getImageSize()
    {
        int[] size = new int[4];
        try {
            size[0] = Integer.parseInt(image_width.getText());
            size[1] = Integer.parseInt(image_height.getText());
        } catch (NumberFormatException e) {
            size[0] = size[1] = 0;
        }
        return size;
    }

    private void initSize()
    {
        int[] size = getImageSize();
        size = ImageControl.getImageSize(size[0], size[1]);
        String value = io.getString("image_size");
        if (value != null) {
            StringTokenizer st = new StringTokenizer(value, "x");
            if (st.countTokens() == 2) {
                try {
                    value = st.nextToken().trim();
                    size[0] = Integer.parseInt(value);
                    value = st.nextToken().trim();
                    size[1] = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                }
            }
        }
        int sel = ImageControl.getSelectionFromSize(size);
        image_size.setSelectedIndex(sel);
        setImageSizeState(sel);
        image_width.setText(Integer.toString(size[0]));
        image_height.setText(Integer.toString(size[1]));
    }

    private void setImageSizeState(int sel)
    {
        if (sel == ImageControl.CUSTOM_SIZE) {
            image_width.setEditable(true);
            image_height.setEditable(true);
        } else {
            image_width.setEditable(false);
            image_height.setEditable(false);
        }
    }

    private JButton createButton(String label, Font font, JPanel panel)
    {
        JButton button = new JButton(label);
        button.setBackground(Color.white);
        button.setFont(font);
        panel.add(button);
        return button;
    }

    private JCheckBox createCheckBox(String label, Font font, JPanel panel,
            boolean set)
    {
        JCheckBox check_box = new JCheckBox(label, set);
        check_box.setBackground(Color.white);
        check_box.setFont(font);
        panel.add(check_box);
        return check_box;
    }

    private JComboBox createCombBox(String[] choice, Font font, JPanel panel)
    {
        JComboBox combo = new JComboBox(choice);
        combo.setBackground(Color.white);
        combo.setFont(font);
        panel.add(combo);
        return combo;
    }

    private void createNameComboBox(Font font, JPanel panel, int width,
            String tool_tip)
    {
        remember = new JCheckBox(Resource.getString("remember"));
        remember.setBackground(Color.white);
        remember.setFont(new Font(Resource.getFontName(), Font.BOLD, 14));
        remember.setSelected(io.getBoolean("remember"));
        if (!has_access) {
            if (!remember.isSelected())
                io.saveToStore(STORE, 0);
            panel.add(remember);
        }
        String[] items = io.loadFromStore(STORE);
        if (items == null) {
            name = new EditCombo();
            name.setSelectedItem(Resource.getString("nameless"));
        } else {
            name = new EditCombo(items);
            name.setSelectedIndex(0);
        }
        name.setPreferredWidth(NAME_WIDTH);
        name.setBackground(Color.white);
        name.setFont(font);
        name.setToolTipText(tool_tip);
        name.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                int[] s_data = new int[1];
                s_data[0] = sex.getSelectedIndex();
                if (name.getSelectedIndex() >= 0
                        && io.loadData(
                                ((String) name.getSelectedItem()).trim(),
                                s_data, birth, now, location)) {
                    sex.setSelectedIndex(s_data[0]);
                    note.setNote(null);
                    updateData();
                }
            }
        });
        remember.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
                io.setBoolean("remember", remember.isSelected());
                io.saveToStore(STORE, remember.isSelected() ? MAX_NUM_RECORD
                        : 0);
            }
        });
        panel.add(name);
    }

    private JTextField createTextField(Font font, JPanel panel, int width)
    {
        JTextField field = new JTextField(width);
        field.setBackground(Color.white);
        field.setFont(font);
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(field);
        return field;
    }

    private JLabel createLabel(Font font, JPanel panel, String str)
    {
        JLabel label = new JLabel(str, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.white);
        label.setFont(font);
        panel.add(label);
        return label;
    }

    private void initKeyMap()
    {
        InputMap map = name.getTextInputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK),
                new AbstractAction() {
                    private static final long serialVersionUID = 7107239970977588835L;

                    public void actionPerformed(ActionEvent event)
                    {
                        String str = name.getSelection(false);
                        if (!str.equals(""))
                            io.setClipboard(str);
                    }
                });
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK),
                new AbstractAction() {
                    private static final long serialVersionUID = -6920118985662196559L;

                    public void actionPerformed(ActionEvent event)
                    {
                        String str = name.getSelection(true);
                        if (!str.equals(""))
                            io.setClipboard(str);
                    }
                });
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK),
                new AbstractAction() {
                    private static final long serialVersionUID = 7574027670952306865L;

                    public void actionPerformed(ActionEvent event)
                    {
                        String str = io.getClipboard();
                        if (!str.equals(""))
                            name.setSelection(str);
                    }
                });
    }

    private void initData()
    {
        ChartMode.initChartMode();
        tab = new AppletData(true);
        tab.setContainer(data_panel);
        pole = new AppletData(true);
        pole.setContainer(pole_panel);
        eval = new AppletData(true);
        eval.setContainer(eval_panel);
        note = new AppletData(false);
        note.setContainer(note_panel);
        data = new ChartData(tab, pole, eval);
        diagram.init(data);
        diagram.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent event)
            {
                if (!data.getShowNow())
                    return;
                DiagramTip tip = diagram.getDiagramTip();
                Point p = diagram.getOffset();
                int year = tip.getIntFromPoint(event.getX() - p.x, event.getY()
                        - p.y);
                if (tip.isIntValid(year)) {
                    int[] date = new int[5];
                    date[0] = year;
                    date[1] = 7;
                    date[2] = 1;
                    date[3] = 12;
                    date[4] = 0;
                    now.setCalendar(date);
                    updateData();
                }
            }
        });
        data.setShowNow(now.isSelected());
        data.setTimeAdjust(time_adj.isSelected() ? 2 : 0);
        boolean from_file = false;
        if (has_access) {
            String path_name = io.getString("path");
            String file_name = io.getString("file");
            if (path_name != null && file_name != null) {
                if (!loadData(path_name + File.separator + file_name)) {
                    reset(false);
                } else {
                    from_file = true;
                }
            }
        }
        int[] s_data = new int[1];
        s_data[0] = sex.getSelectedIndex();
        if (!from_file) {
            if (io.loadData(((String) name.getSelectedItem()).trim(), s_data,
                    birth, now, location)) {
                sex.setSelectedIndex(s_data[0]);
            } else {
                reset(false);
            }
            note.setNote(null);
        }
    }

    private void compute()
    {
        diagram.hideTip();
        DataEntry entry = new DataEntry();
        entry.setName(((String) name.getSelectedItem()).trim());
        entry.setSex(sex.getSelectedIndex() == 0);
        int[] date = new int[5];
        birth.getCalendar(date);
        entry.setBirthDay(date);
        now.getCalendar(date);
        entry.setNowDay(date);
        entry.setCountry(location.getCountryName());
        entry.setCity(location.getCityName());
        entry.setZone(location.getZoneName());
        String err = data.compute(entry, entry, null, diagram.getDiagramTip());
        if (err != null) {
            Message.info(err + ".\nReset to current date.");
            reset(true);
            birth.getCalendar(date);
            entry.setBirthDay(date);
            now.getCalendar(date);
            entry.setNowDay(date);
            entry.setCountry(location.getCountryName());
            entry.setCity(location.getCityName());
            entry.setZone(location.getZoneName());
            data.compute(entry, entry, null, diagram.getDiagramTip());
        }
        diagram.recompute();
        tab.update();
        pole.update();
        eval.update();
        note.refresh();
        if (tab_pane.getSelectedIndex() != 0)
            tab_pane.setSelectedIndex(0);
        repaint();
    }

    private void reset(boolean full)
    {
        ChartMode.setAstroMode(ChartMode.NATAL_MODE);
        data.reset();
        if (!full)
            return;
        birth.reset();
        now.reset();
        location.setCountryName(City.getDefaultCountry());
        location.setCityName(City.getDefaultCity());
    }

    private void updateData()
    {
        compute();
        String key = ((String) name.getSelectedItem()).trim();
        if (key.equals("") || key.equals(Resource.getString("nameless")))
            return;
        if (name.getSelectedIndex() < 0) {
            int i;
            for (i = name.getItemCount() - 1; i >= 0; i--) {
                String str = (String) name.getItemAt(i);
                if (str.equals(key))
                    break;
            }
            if (i < 0)
                name.addItem(key);
        }
        io.saveData(key, sex.getSelectedIndex(), birth, now, location);
        if (!has_access)
            io.saveToStore(STORE, remember.isSelected() ? MAX_NUM_RECORD : 0);
    }

    private void captureDataAs()
    {
        diagram.hideTip();
        int[] size = getImageSize();
        if (size[0] == 0 && size[1] == 0) {
            Message.warn("Width or height must be integers");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        String path = io.getString("image_path");
        if (path == null)
            path = io.getString("path");
        if (path != null)
            chooser.setCurrentDirectory(new File(path.toString()));
        chooser.addChoosableFileFilter(new ImageFilter());
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showSaveDialog(pane) != JFileChooser.APPROVE_OPTION)
            return;
        File capture_file = chooser.getSelectedFile();
        String file_name = capture_file.getName();
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
            capture_file = new File(capture_file.getAbsolutePath() + "."
                    + format);
        }
        int[] image_desc = ImageControl.getImageSize(size[0], size[1]);
        io.setString("image_size", Integer.toString(image_desc[0]) + "x"
                + Integer.toString(image_desc[1]));
        BufferedImage image = ImageControl.captureImage(image_desc);
        try {
            ImageIO.write(image, format, capture_file);
            File open_path = chooser.getCurrentDirectory();
            io.setString("image_path", open_path.getAbsolutePath());
        } catch (IOException e) {
            Message.warn(e.getMessage());
        }
    }

    private boolean loadData(String file_name)
    {
        DataSet data_set = new DataSet();
        if (!data_set.loadData(Resource.LOCAL_PREFIX + file_name))
            return false;
        int max_entry = data_set.getMaxDataEntry(DataSet.DATA);
        int last_index = -1;
        if (max_entry > 1)
            last_index = data_set.getLastIndex(DataSet.DATA);
        for (int i = 0; i < max_entry; i++) {
            if (last_index >= 0 && i != last_index
                    || !data_set.hasDataEntry(i, DataSet.DATA))
                continue;
            DataEntry entry = data_set.getDataEntry(i, DataSet.DATA);
            name.setSelectedItem((entry.getName() == null) ? Resource
                    .getString("nameless") : entry.getName());
            sex.setSelectedIndex(entry.getSex() ? 0 : 1);
            birth.setCalendar(entry.getBirthDay());
            now.setCalendar(entry.getNowDay());
            location.setCountryName(entry.getCountry());
            location.setCityName(entry.getCity());
            location.setZoneName(entry.getZone());
            note.setNote(DataSet.removeStyle(entry.getNote(true)));
            return true;
        }
        return false;
    }

    private void loadDataFrom()
    {
        diagram.hideTip();
        JFileChooser chooser = new JFileChooser();
        String path = io.getString("path");
        if (path != null)
            chooser.setCurrentDirectory(new File(path.toString()));
        data_array = Resource.ALL_EXTENSIONS;
        chooser.addChoosableFileFilter(new SaveFilter());
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(pane) != JFileChooser.APPROVE_OPTION)
            return;
        File open_file = chooser.getSelectedFile();
        File open_path = chooser.getCurrentDirectory();
        String file_name = open_file.getName();
        String path_name = open_path.getAbsolutePath();
        String path_file_name = path_name + File.separator + file_name;
        if (file_name.toLowerCase().endsWith("." + Resource.RSRC_EXT)) {
            loadModification(Resource.LOCAL_PREFIX + path_file_name);
            updateData();
            io.setString("modification", path_file_name);
        } else if (file_name.toLowerCase().endsWith("." + Resource.RULE_EXT)) {
            loadEvaluation(Resource.LOCAL_PREFIX + path_file_name);
            updateData();
            io.setString("evaluation", path_file_name);
            updateEvalTab();
        } else {
            if (!loadData(path_file_name))
                return;
            updateData();
            io.setString("path", path_name);
            io.setString("file", file_name);
        }
    }

    private void updateEvalTab()
    {
        if (eval_tab_shown == RuleEntry.hasRuleEntry(true))
            return;
        eval_tab_shown = !eval_tab_shown;
        if (eval_tab_shown) {
            tab_pane.add(eval_tab, eval_tab_index);
            tab_pane.setTitleAt(eval_tab_index, Resource.getString("eval"));
        } else {
            tab_pane.remove(eval_tab);
        }
    }

    private void loadModification(String file_name)
    {
        Resource.loadModification(file_name);
        DrawAWT.resetFontTable();
        tab.setFont();
        pole.setFont();
        eval.setFont();
        note.setFont();
        data.loadResource();
    }

    private void loadEvaluation(String file_name)
    {
        Resource.loadEvaluation(file_name, false);
    }

    private void saveDataAs()
    {
        diagram.hideTip();
        JFileChooser chooser = new JFileChooser();
        String path = io.getString("path");
        if (path != null)
            chooser.setCurrentDirectory(new File(path.toString()));
        data_array = Resource.DATA_EXTENSIONS;
        chooser.addChoosableFileFilter(new SaveFilter());
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showSaveDialog(pane) != JFileChooser.APPROVE_OPTION)
            return;
        File save_file = chooser.getSelectedFile();
        File save_path = chooser.getCurrentDirectory();
        String file_name = save_file.getName();
        String format = null;
        for (int i = 0; i < Resource.DATA_EXTENSIONS.length; i++) {
            if (file_name.toLowerCase().endsWith(
                    Resource.DATA_EXTENSIONS[i].substring(1).toLowerCase())) {
                format = Resource.DATA_EXTENSIONS[i].substring(2);
                break;
            }
        }
        if (format == null)
            file_name += "." + Resource.DATA_EXTENSIONS[0].substring(2);
        String path_name = save_path.getAbsolutePath();
        String path_file_name = path_name + File.separator + file_name;
        int[] date = new int[5], now_date = new int[5];
        birth.getCalendar(date);
        now.getCalendar(now_date);
        String user = (String) name.getSelectedItem();
        if (user.equals(Resource.getString("nameless")))
            user = null;
        String str = note.getNote();
        if (!note.hasValidNote(str))
            str = null;
        DataSet data_set = new DataSet();
        data_set.setMaxDataEntry(1, DataSet.DATA);
        DataEntry entry = data_set.getDataEntry(0, DataSet.DATA);
        entry.setName(user);
        entry.setSex(sex.getSelectedIndex() == 0);
        entry.setBirthDay(date);
        entry.setNowDay(now_date);
        entry.setCountry(location.getCountryName());
        entry.setCity(location.getCityName());
        entry.setZone(location.getZoneName());
        entry.setOverride(null);
        entry.setNote(str);
        data_set.saveData(path_file_name);
        io.setString("path", path_name);
        io.setString("file", file_name);
    }
}