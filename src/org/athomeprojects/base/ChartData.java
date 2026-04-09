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
package org.athomeprojects.base;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.athomeprojects.swisseph.SweConst;

public class ChartData {
    static public final int SUN = 0;

    static public final int MOON = 1;

    static public final int VENUS = 2;

    static public final int JUPITER = 3;

    static public final int MERCURY = 4;

    static public final int MARS = 5;

    static public final int SATURN = 6;

    static public final int URANUS = 7;

    static public final int NEPTUNE = 8;

    static public final int PLUTO = 9;

    static public final int TRUE_NODE = 10;

    static public final int INV_TRUE_NODE = 11; // opposite of TRUE_NODE

    static public final int PURPLE = 12;

    static public final int MEAN_APOG = 13;

    static public final int FORTUNE = 14;

    static public final int ASC = 15;

    static public final int MC = 16;

    static public final int CHIRON = 17;

    static public final int CERES = 18;

    static public final int PALLAS = 19;

    static public final int JUNO = 20;

    static public final int VESTA = 21;

    private final int FIXSTAR_OFFSET = 10000;

    private final int CONSTELLATION_MAIN_OFFSET = 20000;

    private final int CONSTELLATION_SUB_OFFSET = 20100;

    private int[] planets = { SweConst.SE_SUN, SweConst.SE_MOON,
            SweConst.SE_VENUS, SweConst.SE_JUPITER, SweConst.SE_MERCURY,
            SweConst.SE_MARS, SweConst.SE_SATURN, SweConst.SE_URANUS,
            SweConst.SE_NEPTUNE, SweConst.SE_PLUTO, SweConst.SE_TRUE_NODE, -1,
            -1, SweConst.SE_MEAN_APOG, Calculate.SE_FORTUNE, Calculate.SE_ASC,
            Calculate.SE_MC, SweConst.SE_CHIRON, SweConst.SE_CERES,
            SweConst.SE_PALLAS, SweConst.SE_JUNO, SweConst.SE_VESTA };

    static public final int CH_YEAR = 0;

    static public final int CH_MONTH = 1;

    static public final int YEAR_POLE = 2;

    static public final int MONTH_POLE = 3;

    static public final int DAY_POLE = 4;

    static public final int HOUR_POLE = 5;

    static public final int POLE_OVERRIDE_OFFSET = 100;

    private final int MAX_TIP_COUNT = 24;

    private final int MAX_DATA_COUNT = 48;

    private final double DEGREE = 2.0 * Math.PI / 360.0;

    private final double MIN_ADVANCE = 1.0 / (10.0 * 365.25);

    private final double MAX_ADVANCE = 10.0 / 365.25;

    private final double TINY_DEGREE = 1.0e-5;

    private final double MILLISECOND_PER_YEAR = 365.25 * 24.0 * 60.0 * 60.0
            * 1000.0;

    private final double HORIZ_ZOOM = 20.0;

    private final double HORIZ_ZOOM_GAP = 1.0;

    private final int NUM_ROW_THRESHOLD = 17;

    private Calculate cal;

    private DiagramTip tip;

    private double[] solar_terms, new_moons;

    private SearchRecord solar_eclipse, lunar_eclipse;

    private int[] lunar_date, now_lunar_date, solar_date;

    private boolean lunar_leap_month, now_lunar_leap_month, has_pos_adj;

    private boolean day_birth, day_birth_override, has_day_birth_override,
            true_north, quick_azimuth, ten_god_mode, last_year_birth;

    private int wife_pos, mountain_mode, current_age, explain_length;

    private int[] draw_size = new int[3];

    private String[] wife_signs = new String[12];

    private String[] twelve_signs = new String[12];

    private String[] full_twelve_signs = new String[12];

    private String[] now_year_seq = new String[12];

    private String[] year_star_seq = new String[10];

    private int[] year_star_map = new int[10];

    private int[] year_star_range = new int[2];

    private String lunar_calendar, explain, explain_fixstar, explain_prefix,
            explain_suffix, azimuth_speed;

    private String birth_zone, year_char, month_char, day_char, hour_char;

    private String birth_year_info, current_year_info, pick_year_info;

    private String alt_birth_year_info, alt_current_year_info,
            alt_pick_year_info;

    private int[] eight_char_override;

    private String invalid_sign, birth_sign_status_fill;

    private String[] year_names = new String[60];

    private String[] sky_pole_names = new String[10];

    private String[] ten_god_seq1 = new String[10];

    private String[] ten_god_seq2 = new String[10];

    private int[] day_pole_long_life_seq = new int[10];

    private String[] earth_pole_names = new String[12];

    private String[] long_life_signs = new String[12];

    private String[] earth_god_seq = new String[12];

    private String[] star_equ_map;

    private int[] month_sky_pole_shifts = new int[10];

    private int month_earth_pole_shift;

    private int[] day_pole_base = new int[5];

    private int[] hour_sky_pole_shifts = new int[10];

    private int hour_earth_pole_shift;

    private String[] season_starts = new String[24];

    private String[] year_signs = new String[12], cusps_name = new String[12],
            house_name = new String[12];

    private String[] year_data = new String[20];

    private String[] alt_year_data = new String[20];

    private double[] limit_seq = new double[12];

    private double[] birth_cusp = new double[13], now_cusp = new double[13];

    private double[] birth_cusp_override;

    private double[] aspects_degree, aspects_orb;

    private int[] birth_speed_color, now_speed_color, now_state_color;

    private int[] aspects_color, aspects_style;

    private String[] aspects_sign;

    private int[][] aspects_index;

    private int num_asteroids;

    private String[] signs;

    private String[] sign_prefix;

    private String[] power_key, power_index;

    private String[] alt_pole_data;

    private int alt_pole_data_offset;

    private int[] sign_computation_type;

    private int[] sign_opposite;

    private int[] sign_display, aspects_display;

    private int[] sign_display_orders;

    private int[] birth_sign_display_sort_orders;

    private int[] now_sign_display_sort_orders;

    private double[] birth_sign_pos, sign_pos_shift;

    private double[] gauquelin_sign_region, gauquelin_sign_pos;

    private double[] birth_sign_azimuth, birth_sign_alt, birth_sign_diameter;

    private boolean[] sign_rev_flip, sign_lock;

    private int[] birth_sign_state;

    private String[] birth_sign_status;

    private String[] birth_rise_time = new String[2];

    private String[] birth_set_time = new String[2];

    private boolean[] equator_rise_set = new boolean[2];

    private double[] now_sign_pos;

    private double[] now_sign_azimuth, now_sign_alt;

    private int[] now_sign_state;

    private String[] now_sign_status;

    private String[] full_zodiac = new String[12];

    private String[] chinese_zodiac_signs = new String[12];

    private String[] full_stellar_signs = new String[28];

    private String[] half_stellar_signs = new String[28];

    private String[] mountain_signs = new String[24];

    private String[] full_mountain_main_signs = new String[24];

    private String[] full_mountain_secondary_signs = new String[24];

    private String[] full_mountain_signs, fixstar_signs, fixstar_names;

    private int[] child_seq, fly_seq_ying1, fly_seq_yang1, fly_seq_ying2,
            fly_seq_yang2, fly_seq_half_shift;

    private String[] stellar_names = new String[28];

    private String[] sidereal_stellar_names = new String[28];

    private String[] compass_stellar_names = new String[28];

    private double[] stellar_sign_pos = new double[28];

    private double[] compass_sign_pos = new double[28];

    private double[] fixstar_sign_pos, fixstar_sign_azimuth, fixstar_sign_alt;

    private int[] fixstar_sign_state;

    private double life_sign_pos, self_sign_pos, first_cusp_pos,
            tenth_cusp_pos, mountain_pos, mountain_offset, solar_return_pos,
            lunar_return_pos;

    private double magnetic_shift, true_north_magnetic_shift;

    private double angle_offset;

    private String magnetic_shift_message;

    private Hashtable birth_table, day_table, now_table, master_table,
            fixstar_table;

    private LinkedList good_styles, bad_styles, asteroid_list;

    private LinkedList ten_god_list, ten_god_list_org, ten_god_list_alt;

    private double[] birth_loc = new double[2], now_loc = new double[2];

    private double[] lunar_transits;

    private int[] birth_dst_date = new int[5], birth_adj_date = new int[5];

    private int[] now_dst_date = new int[5], now_adj_date = new int[5];

    private String birth_base_time, now_base_time;

    private Date now_date;

    private double now_degree, now_start_degree, now_end_degree;

    private double primary_speed, birth_ref_ut, now_ref_ut;

    private boolean use_primary_speed, equatorial_orbit;

    private String house_override;

    private String day_or_night, age_label, now_year;

    private String center_tip, birth_sign_tip, now_sign_tip;

    private String[] astro_sign_data = new String[2];

    private String astro_cusp_data, horiz_sign_data;

    private int[] astro_elemental = new int[4];

    private int[] astro_elemental_state = new int[3];

    private boolean show_now_set, show_aspects_set, show_gauquelin_set,
            show_fixstar_set, show_horiz_set;

    private boolean show_now, show_aspects, show_gauquelin, show_fixstar,
            show_compass, show_horiz, day_set, explain_star, no_color,
            dual_ring, single_wheel_mode;

    private boolean[] three_danger;

    private int time_adjust;

    private String[] birth_poles = new String[6], now_poles = new String[6];

    private String[] weak_house = new String[6], solid_house = new String[6];

    private Calendar calendar = Calendar.getInstance();

    private Calendar work_cal = Calendar.getInstance();

    private BaseTab tab, pole_tab, eval_tab;

    private DataEntry birth, now, cur_loc;

    static private ChartData self;

    public ChartData(BaseTab b_tab, BaseTab b_pole_tab, BaseTab b_eval_tab)
    {
        tab = b_tab;
        pole_tab = b_pole_tab;
        eval_tab = b_eval_tab;
        birth_table = day_table = now_table = null;
        house_override = null;
        has_day_birth_override = day_birth_override = false;
        now_degree = -1.0;
        no_color = dual_ring = use_primary_speed = show_fixstar = show_compass = single_wheel_mode = false;
        show_now_set = day_set = true;
        cal = new Calculate();
        loadResource();
        life_sign_pos = self_sign_pos = first_cusp_pos = tenth_cusp_pos = Calculate.INVALID;
        mountain_pos = 315.0;
        magnetic_shift = true_north_magnetic_shift = 0.0;
        magnetic_shift_message = "";
        self = this;
    }

    static public ChartData getData()
    {
        return self;
    }

    public void setEpheMode(boolean use_moseph)
    {
        cal.setEphMode(use_moseph);
        resetSignComputationType();
    }

    public void setTopocentricMode()
    {
        cal.setTopocentricMode(false, false);
    }

    public void loadResource()
    {
        cal.loadResource();
        wife_pos = Resource.getInt("birth_wife_sign");
        invalid_sign = Resource.getString("invalid_sign");
        year_char = Resource.getString("year_char");
        month_char = Resource.getString("month_char");
        day_char = Resource.getString("day_char");
        hour_char = Resource.getString("hour_char");
        lunar_calendar = Resource.getString("lunar_calendar");
        birth_year_info = Resource.getString("birth_year_info");
        current_year_info = Resource.getString("current_year_info");
        pick_year_info = Resource.getString("pick_year_info");
        alt_birth_year_info = Resource.getString("alt_birth_year_info");
        alt_current_year_info = Resource.getString("alt_current_year_info");
        alt_pick_year_info = Resource.getString("alt_pick_year_info");
        explain = Resource.getString("explain");
        explain_fixstar = Resource.getString("explain_fixstar");
        explain_prefix = Resource.getString("explain_prefix");
        explain_suffix = Resource.getString("explain_suffix");
        explain_length = explain_prefix.length() + explain_suffix.length();
        now_year = Resource.getString("current_date");
        Resource.getString("fixstar");
        Resource.getStringArray("birth_year_signs", year_signs);
        Resource.getStringArray("astro_cusps_name", cusps_name);
        Resource.getStringArray("house_name", house_name);
        Resource.getStringArray("wife_signs", wife_signs);
        Resource.getStringArray("twelve_signs", twelve_signs);
        Resource.getStringArray("full_twelve_signs", full_twelve_signs);
        Resource.getStringArray("chinese_zodiac_signs", chinese_zodiac_signs);
        Resource.getStringArray("birth_year_names", year_names);
        Resource.getStringArray("sky_pole_names", sky_pole_names);
        Resource.getStringArray("earth_pole_names", earth_pole_names);
        Resource.getStringArray("long_life_signs", long_life_signs);
        Resource.getStringArray("ten_god_seq1", ten_god_seq1);
        Resource.getStringArray("ten_god_seq2", ten_god_seq2);
        Resource.getIntArray("day_pole_long_life_seq", day_pole_long_life_seq);
        Resource.getStringArray("earth_god_seq", earth_god_seq);
        month_earth_pole_shift = Resource.getInt("month_earth_pole_shift");
        Resource.getIntArray("month_sky_pole_shifts", month_sky_pole_shifts);
        Resource.getIntArray("day_pole_base", day_pole_base);
        hour_earth_pole_shift = Resource.getInt("hour_earth_pole_shift");
        Resource.getIntArray("hour_sky_pole_shifts", hour_sky_pole_shifts);
        Resource.getStringArray("season_starts", season_starts);
        Resource.getStringArray("full_zodiac", full_zodiac);
        Resource.getStringArray("full_stellar_signs", full_stellar_signs);
        Resource.getStringArray("half_stellar_signs", half_stellar_signs);
        Resource.getStringArray("stellar_names", stellar_names);
        Resource.getStringArray(
                Resource.hasKey("sidereal_stellar_names") ? "sidereal_stellar_names"
                        : "stellar_names", sidereal_stellar_names);
        if (Resource.hasKey("sidereal_stellar_sizes")) {
            double[] sizes = Resource.getDoubleArray("sidereal_stellar_sizes");
            double base = FileIO.parseDouble(sidereal_stellar_names[0], 0.0,
                    true);
            for (int i = 1; i < sidereal_stellar_names.length; i++) {
                base += sizes[i - 1];
                sidereal_stellar_names[i] = FileIO.formatDouble(
                        City.normalizeDegree(base), 2, 2, true, false);
            }
        }
        Resource.getStringArray("compass_stellar_names", compass_stellar_names);
        Resource.getStringArray("mountain_signs", mountain_signs);
        Resource.getStringArray("full_mountain_main_signs",
                full_mountain_main_signs);
        Resource.getStringArray("full_mountain_secondary_signs",
                full_mountain_secondary_signs);
        ten_god_list_org = Resource.getStringList("ten_god_list_org");
        ten_god_list_alt = Resource.getStringList("ten_god_list_alt");
        fixstar_signs = Resource.getStringArray("fixstar_signs");
        fixstar_names = Resource.getStringArray("fixstar_names");
        Resource.getStringArray("now_year_seq", now_year_seq);
        Resource.getStringArray("year_star_seq", year_star_seq);
        Resource.getIntArray("year_star_map", year_star_map);
        Resource.getIntArray("year_star_range", year_star_range);
        power_key = Resource.getStringArray("power_key");
        power_index = Resource.getStringArray("power_index");
        star_equ_map = Resource.getStringArray("star_equ_map");
        child_seq = Resource.getIntArray("child_seq");
        fly_seq_ying1 = Resource.getIntArray("fly_seq_ying1");
        fly_seq_yang1 = Resource.getIntArray("fly_seq_yang1");
        fly_seq_ying2 = Resource.getIntArray("fly_seq_ying2");
        fly_seq_yang2 = Resource.getIntArray("fly_seq_yang2");
        fly_seq_half_shift = Resource.getIntArray("fly_seq_half_shift");
        Resource.getDoubleArray("limit_seq", limit_seq);
        aspects_color = Resource.getIntArray("aspects_color");
        aspects_style = Resource.getIntArray("aspects_style");
        aspects_degree = Resource.getPrefDoubleArray("_", "aspects_degree");
        aspects_sign = Resource.getPrefStringArray("_", "aspects_sign");
        Arrays.fill(stellar_sign_pos, Calculate.INVALID);
        Arrays.fill(compass_sign_pos, Calculate.INVALID);
        if (Resource.hasPrefKey("show_aspects_set"))
            show_aspects_set = Resource.getPrefInt("show_aspects_set") != 0;
        else
            show_aspects_set = false;
        if (Resource.hasPrefKey("show_gauquelin_set"))
            show_gauquelin_set = Resource.getPrefInt("show_gauquelin_set") != 0;
        else
            show_gauquelin_set = false;
        if (Resource.hasPrefKey("show_fixstar_set"))
            show_fixstar_set = Resource.getPrefInt("show_fixstar_set") != 0;
        else
            show_fixstar_set = false;
        if (Resource.hasPrefKey("show_horiz_set"))
            show_horiz_set = Resource.getPrefInt("show_horiz_set") != 0;
        else
            show_horiz_set = false;
        initSign();
        loadMasterTable();
    }

    public void resetSignComputationType()
    {
        Resource.getIntArray("sign_computation_type", sign_computation_type);
        sign_computation_type[SUN] = Math.max(0, sign_computation_type[SUN]);
        sign_computation_type[MOON] = Math.max(0, sign_computation_type[MOON]);
        int val = ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? 0 : -1;
        for (int i = signs.length - num_asteroids; i < signs.length; i++)
            sign_computation_type[i] = val;
        if (cal.getEphMode()) {
            // Moshier mode does not support asteriods
            sign_computation_type[CHIRON] = sign_computation_type[CERES] = sign_computation_type[PALLAS] = sign_computation_type[JUNO] = sign_computation_type[VESTA] = -1;
        }
    }

    private void initSign()
    {
        asteroid_list = new LinkedList();
        num_asteroids = Calculate.getAsteroidData(null, null, null,
                asteroid_list);
        signs = Resource.getStringArray("signs");
        if (num_asteroids > 0) {
            String[] sign_array = new String[signs.length + num_asteroids];
            int[] planet_array = new int[signs.length + num_asteroids];
            int i = 0;
            for (; i < signs.length; i++) {
                sign_array[i] = signs[i];
                planet_array[i] = planets[i];
            }
            for (ListIterator iter = asteroid_list.listIterator(); iter
                    .hasNext(); i++) {
                sign_array[i] = (String) iter.next();
                planet_array[i] = SweConst.SE_AST_OFFSET
                        + ((Integer) iter.next()).intValue();
            }
            signs = sign_array;
            planets = planet_array;
        }
        sign_prefix = new String[signs.length];
        sign_computation_type = new int[signs.length];
        resetSignComputationType();
        sign_lock = new boolean[signs.length];
        sign_opposite = new int[signs.length];
        birth_sign_pos = new double[signs.length];
        gauquelin_sign_region = new double[signs.length];
        gauquelin_sign_pos = new double[signs.length];
        birth_sign_state = new int[signs.length];
        birth_sign_status = new String[signs.length];
        birth_sign_azimuth = new double[signs.length];
        birth_sign_alt = new double[signs.length];
        birth_sign_diameter = new double[signs.length];
        fixstar_sign_pos = new double[fixstar_names.length];
        fixstar_sign_state = new int[fixstar_names.length];
        fixstar_sign_azimuth = new double[fixstar_names.length];
        fixstar_sign_alt = new double[fixstar_names.length];
        now_sign_pos = new double[signs.length];
        now_sign_state = new int[signs.length];
        now_sign_status = new String[signs.length];
        now_sign_azimuth = new double[signs.length];
        now_sign_alt = new double[signs.length];
        if (sign_pos_shift == null || signs.length != sign_pos_shift.length) {
            sign_pos_shift = new double[signs.length];
            sign_rev_flip = new boolean[signs.length];
            eight_char_override = null;
            birth_cusp_override = null;
        }
        Resource.getStringArray("sign_prefix", sign_prefix);
        Arrays.fill(sign_opposite, -1);
        Arrays.fill(birth_sign_pos, Calculate.INVALID);
        Arrays.fill(gauquelin_sign_region, Calculate.INVALID);
        Arrays.fill(gauquelin_sign_pos, Calculate.INVALID);
        Arrays.fill(birth_sign_azimuth, Calculate.INVALID);
        Arrays.fill(birth_sign_alt, Calculate.INVALID);
        Arrays.fill(now_sign_pos, Calculate.INVALID);
        Arrays.fill(now_sign_azimuth, Calculate.INVALID);
        Arrays.fill(now_sign_alt, Calculate.INVALID);
        for (int i = 0; i < sign_prefix.length; i++) {
            if (!Resource.hasKey(sign_prefix[i] + "_opposite"))
                continue;
            String name = Resource.getString(sign_prefix[i] + "_opposite");
            for (int j = 0; j < sign_prefix.length; j++) {
                if (name.equalsIgnoreCase(sign_prefix[j])) {
                    sign_opposite[i] = j;
                    break;
                }
            }
        }
        birth_speed_color = Resource
                .getPrefIntArray("chart_birth_ring_speed_color");
        now_speed_color = Resource
                .getPrefIntArray("chart_now_ring_speed_color");
        now_state_color = Resource
                .getPrefIntArray("chart_now_ring_state_color");
        int[] data = Resource.getPrefIntArray("aspects_color");
        if (data != null && data.length == aspects_color.length)
            aspects_color = data;
    }

    private void loadMasterTable()
    {
        master_table = new Hashtable();
        String[] master_list = Resource.getStringArray("master_list");
        for (int i = 0; i < master_list.length; i++) {
            String key = master_list[i];
            if (key.startsWith("+")) {
                String[] stars = Resource.getStringArray(key.substring(1));
                for (int j = 0; j < stars.length; j++) {
                    key = stars[j].substring(2);
                    master_table.put(key, "f");
                    master_table.put(now_year + key, "f");
                    if (Resource.hasKey(key)) {
                        String[] group = Resource.getStringArray(key);
                        for (int l = 0; l < group.length; l++) {
                            master_table.put(group[l], "f");
                            master_table.put(now_year + group[l], "f");
                        }
                    }
                }
            } else {
                master_table.put(key, "f");
                master_table.put(now_year + key, "f");
            }
        }
        String[] master_display_list = Resource
                .getStringArray("birth_master_display_list");
        for (int i = 0; i < master_display_list.length; i++) {
            String key = master_display_list[i];
            master_table.put(key, "t");
        }
        master_display_list = Resource
                .getStringArray("now_master_display_list");
        for (int i = 0; i < master_display_list.length; i++) {
            String key = master_display_list[i];
            master_table.put(now_year + key, "t");
        }
        master_display_list = Resource.hasKey("fixstar_display_list") ? Resource
                .getStringArray("fixstar_display_list") : null;
        String val = (master_display_list == null) ? "t" : "f";
        fixstar_table = new Hashtable();
        for (int i = 0; i < fixstar_signs.length; i++)
            fixstar_table.put(fixstar_signs[i], val);
        if (master_display_list != null) {
            for (int i = 0; i < master_display_list.length; i++) {
                String key = master_display_list[i];
                fixstar_table.put(key, "t");
            }
        }
        setDisplayList(master_table, "master_display");
        setDisplayList(fixstar_table, "fixstar_display");
    }

    private void setDisplayList(Hashtable table, String name)
    {
        boolean remove = false;
        String str = Resource.getPrefString(name);
        StringTokenizer st = new StringTokenizer(str, ",");
        if (st.countTokens() == table.size()) {
            for (Enumeration e = table.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String val = st.nextToken().trim();
                if (table.get(key) == null) {
                    // the list has been changed
                    remove = true;
                } else {
                    table.put(key, val);
                }
            }
        } else {
            remove = true;
        }
        if (remove)
            Resource.removePref(name);
    }

    public void pageDiagram(Graphics2D g2d, String prefix, int scaler,
            Point page_size, Point size, boolean applet, boolean pic,
            boolean use_bw, boolean chart_only, boolean footnote,
            boolean rotate, boolean vert, boolean set_tip)
    {
        DrawAWT draw = new DrawAWT();
        draw.init(g2d, scaler, prefix, rotate, vert);
        drawDiagram(draw, page_size, size, use_bw || no_color, true, !applet,
                set_tip);
        Point trim_page_size = page_size;
        if (footnote && Resource.hasCustomFootnote())
            trim_page_size = new Point(page_size.x, page_size.y - 4 * scaler);
        showDesc(draw, prefix, getYearInfoKey(true), page_size, trim_page_size,
                size, use_bw || no_color, applet, pic, chart_only, footnote);
    }

    public void drawDiagram(DrawAWT draw, Point page_size, Point size,
            boolean use_bw, boolean fit_page, boolean print, boolean set_tip)
    {
        int center_x, center_y;
        int diagram_width = size.x, diagram_height = size.y;
        draw.setForeground();
        draw.setBackground();
        {
            int color = draw.getBgColor("chart_window_bg_color", use_bw);
            if (color >= 0)
                draw.setBackground(color);
        }
        if (page_size != null)
            draw.fillRect(0, 0, page_size.x, page_size.y);
        if (fit_page) {
            double scaler = Resource.getDouble("print_diagram_size_scaler");
            int width = (int) (scaler * diagram_width);
            int height = (int) (scaler * diagram_height);
            int offset_x = (diagram_width - width) / 2;
            int offset_y = (diagram_height - height) / 2;
            draw.translate(offset_x, offset_y);
            diagram_width = width;
            diagram_height = height;
            center_x = offset_x + diagram_width / 2;
            center_y = offset_y + diagram_height / 2;
        } else {
            center_x = diagram_width / 2;
            center_y = diagram_height / 2;
        }
        if (page_size == null)
            draw.fillRect(0, 0, diagram_width, diagram_height);
        draw.setBackground();
        draw.setColor();
        boolean draw_self_master = Resource.getPrefInt("self_mode") != 0;
        int life_master_fg_color = draw.getColor("chart_life_master_fg_color",
                use_bw);
        int marker_color = draw.getColor("chart_ring_mk_color", use_bw);
        int horiz_color = draw.getColor("horiz_chart_mk_color", use_bw);
        int birth_degree_color = draw.getColor("chart_birth_degree_mk_color",
                use_bw);
        int now_degree_color = draw.getColor("chart_now_degree_mk_color",
                use_bw);
        int life_master_color = draw.getColor("chart_life_master_mk_color",
                use_bw);
        int self_master_color = draw.getColor("chart_self_master_mk_color",
                use_bw);
        int mountain_color = draw.getColor("chart_mountain_line_mk_color",
                use_bw);
        int now_line_color = draw.getColor("chart_now_line_mk_color", use_bw);
        int asc_color = draw.getColor("chart_asc_mk_color", use_bw);
        int mc_color = draw.getColor("chart_mc_mk_color", use_bw);
        int house_line_color = draw.getColor("chart_house_line_mk_color",
                use_bw);
        int reserve = Resource.getInt("chart_reserve");
        int half_width = diagram_width / 2;
        int half_height = diagram_height / 2;
        int radius = Math.min(half_width, half_height) - reserve;
        draw_size[0] = center_x;
        draw_size[1] = center_y;
        draw_size[2] = radius;
        tip.init(draw_size, set_tip && tip.getNeedUpdate());
        draw.translate(half_width, half_height);
        double base_offset = angle_offset;
        if (!show_horiz) {
            if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                if (Resource.getPrefInt("align_north") == 0)
                    base_offset -= true_north_magnetic_shift;
            } else if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                if (Calculate.isValid(first_cusp_pos))
                    base_offset += first_cusp_pos - 210.0;
            } else {
                base_offset += Resource.getPrefDouble("angle_offset");
            }
        }
        double radian_offset = base_offset * DEGREE;
        draw.rotate(radian_offset);
        AffineTransform save = draw.getTransform();
        int[] ring_draw_type = new int[32];
        int[] ring_label_type = new int[32];
        int[] ring_type = new int[32];
        double[] ring_pos = new double[32];
        int num_ring;
        String prefix;
        if (show_horiz) {
            prefix = "horiz_";
        } else {
            switch (ChartMode.getChartMode()) {
                case ChartMode.ASTRO_MODE:
                {
                    prefix = "astro_";
                    if (show_gauquelin)
                        prefix += "gauquelin_";
                    else if (dual_ring)
                        prefix += "dual_";
                    else if (print && !set_tip
                            || !ChartMode.hasReturnRingMode())
                        prefix += "basic_";
                }
                    break;
                case ChartMode.PICK_MODE:
                {
                    prefix = show_fixstar ? "fixstar_"
                            : (show_compass ? "compass_" : "pick_");
                }
                    break;
                default:
                {
                    if (show_aspects)
                        prefix = "aspects_";
                    else if (show_now)
                        prefix = "full_";
                    else
                        prefix = "half_";
                }
                    break;
            }
        }
        String type_prefix = prefix;
        if (single_wheel_mode)
            type_prefix += "single_";
        else if (ChartMode.isReturnMode())
            type_prefix += "return_";
        int mountain_ring = Resource.getInt(prefix + "mountain_ring") - 1;
        num_ring = Resource.getDoubleArray(prefix + "ring_pos", ring_pos);
        Resource.getIntArray(prefix + "ring_draw_type", ring_draw_type);
        Resource.getIntArray(prefix + "ring_label_type", ring_label_type);
        Resource.getIntArray(type_prefix + "ring_type", ring_type);
        if (print && Resource.hasKey(prefix + "ring_print_scaler")) {
            double scaler = Resource.getDouble(prefix + "ring_print_scaler");
            for (int i = 0; i < num_ring; i++)
                ring_pos[i] *= scaler;
        }
        boolean show_house_system = !ChartMode
                .isChartMode(ChartMode.ASTRO_MODE)
                && Resource.getPrefInt("show_house_system") != 0
                && !show_aspects;
        int house_system_draw_pos = Resource
                .getPrefInt("house_system_draw_pos");
        int house_system_draw_type = Resource
                .getPrefInt("house_system_draw_type");
        if (show_house_system)
            ring_draw_type[house_system_draw_pos] = house_system_draw_type;
        int horiz_dist = 0, horiz_y_offset = 0;
        int[] colors = new int[6];
        if (use_bw) {
            Arrays.fill(colors, -1);
        } else {
            colors[0] = Resource.getPrefInt("chart_base_ring_bg_color");
            colors[1] = Resource.getPrefInt("chart_birth_ring_bg_color");
            colors[2] = Resource.getPrefInt("chart_now_ring_bg_color");
        }
        // fill ring
        for (int i = num_ring - 1; i >= 0; i--) {
            int pos = (int) (ring_pos[i] * radius + 0.5);
            int type = ring_type[i];
            int color;
            if (show_horiz) {
                if (ring_draw_type[i] == -12) {
                    horiz_dist = (int) (ring_pos[i] * radius + 0.5);
                    horiz_y_offset = -((int) (0.33 * horiz_dist + 0.5));
                    int h_dist = horiz_dist / 2;
                    color = (type < 0) ? -1 : colors[type];
                    if (color >= 0)
                        draw.setBackground(color);
                    else
                        draw.setBackground();
                    draw.fillRect(-horiz_dist, -h_dist + horiz_y_offset,
                            2 * horiz_dist, horiz_dist);
                }
            } else {
                if (i == num_ring - 1) { // special outer ring use window color
                    color = draw.getBgColor("chart_window_bg_color", use_bw);
                } else {
                    color = (type < 0) ? -1 : colors[type];
                }
                if (color >= 0)
                    draw.setBackground(color);
                else
                    draw.setBackground();
                draw.fillCircle(-pos, -pos, 2 * pos, 2 * pos);
            }
            draw.setBackground();
        }
        if (use_bw) {
            Arrays.fill(colors, -1);
        } else {
            colors[0] = Resource.getPrefInt("chart_base_ring_fg_color");
            colors[1] = Resource.getPrefInt("chart_birth_ring_fg_color");
            colors[2] = Resource.getPrefInt("chart_now_ring_fg_color");
            colors[3] = Resource.getPrefInt("chart_weak_line_mk_color");
            colors[4] = Resource.getPrefInt("chart_solid_line_mk_color");
            colors[5] = Resource.getPrefInt("chart_house_system_mk_color");
        }
        int upper_mark = Resource.getInt(prefix + "limit_ring") - 1;
        int lower_mark = upper_mark - 1;
        int line_upper_end = (int) (ring_pos[upper_mark] * radius + 0.5) - 1;
        int line_lower_end = (int) (ring_pos[lower_mark] * radius + 0.5) - 1;
        int bar_width = (int) ((line_upper_end - line_lower_end) * Resource
                .getDouble("birth_bar_width_ratio"));
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (Resource.hasKey(prefix + "cusps")) {
                int asc_bg_color = draw
                        .getBgColor("chart_asc_bg_color", use_bw);
                int mc_bg_color = draw.getBgColor("chart_mc_bg_color", use_bw);
                int outer_edge = (int) (ring_pos[upper_mark + 1] * radius + 0.5) - 1;
                int ring = Resource.getInt(prefix + "cusps") - 1;
                int cusps_edge = (int) (ring_pos[ring] * radius + 0.5);
                int inf_upper_edge, inf_lower_edge;
                double asc_pos, mc_pos;
                double inf_ll, inf_lu, inf_rl, inf_ru;
                double inf_bl, inf_bu, inf_tl, inf_tu;
                asc_pos = first_cusp_pos;
                if (show_gauquelin) {
                    mc_pos = asc_pos - 90.0;
                    inf_ll = inf_rl = inf_bl = inf_tl = 10.0;
                    inf_lu = inf_tu = 30.0;
                    inf_ru = inf_bu = 20.0;
                    inf_upper_edge = (int) (ring_pos[ring + 1] * radius + 0.5) - 1;
                    inf_lower_edge = (int) (ring_pos[ring] * radius + 0.5) + 1;
                } else {
                    mc_pos = tenth_cusp_pos;
                    inf_ll = inf_lu = inf_rl = inf_ru = Resource
                            .getPrefDouble("asc_influence");
                    inf_bl = inf_bu = inf_tl = inf_tu = Resource
                            .getPrefDouble("mc_influence");
                    inf_upper_edge = (int) (ring_pos[ring] * radius + 0.5) - 1;
                    inf_lower_edge = (int) (ring_pos[ring - 1] * radius + 0.5) + 1;
                }
                if (inf_ll > 0.0 && Calculate.isValid(asc_pos)) {
                    draw.rotate(-DEGREE * asc_pos + 2.0 * Math.PI / 12);
                    draw.paintFillArc(asc_bg_color, inf_lower_edge,
                            inf_upper_edge, inf_ll * DEGREE, -inf_lu * DEGREE);
                    draw.rotate(Math.PI);
                    draw.paintFillArc(asc_bg_color, inf_lower_edge,
                            inf_upper_edge, inf_rl * DEGREE, -inf_ru * DEGREE);
                    draw.setTransform(save);
                }
                if (inf_tl > 0.0 && Calculate.isValid(mc_pos)) {
                    draw.rotate(-DEGREE * mc_pos + 2.0 * Math.PI / 12);
                    draw.paintFillArc(mc_bg_color, inf_lower_edge,
                            inf_upper_edge, inf_tl * DEGREE, -inf_tu * DEGREE);
                    draw.rotate(Math.PI);
                    draw.paintFillArc(mc_bg_color, inf_lower_edge,
                            inf_upper_edge, inf_bl * DEGREE, -inf_bu * DEGREE);
                    draw.setTransform(save);
                }
                if (Calculate.isValid(asc_pos)) {
                    draw.rotate(-DEGREE * asc_pos + 2.0 * Math.PI / 12);
                    draw.setForeground(asc_color);
                    draw.drawDashLine(DrawAWT.DOT, 0, 0, cusps_edge, 0);
                    draw.drawLine(line_upper_end + 1, 0, outer_edge, 0);
                    draw.rotate(Math.PI);
                    draw.drawDashLine(DrawAWT.DOT, 0, 0, cusps_edge, 0);
                    draw.drawLine(line_upper_end + 1, 0, outer_edge, 0);
                    draw.setForeground();
                    draw.setTransform(save);
                }
                if (Calculate.isValid(mc_pos)) {
                    draw.rotate(-DEGREE * mc_pos + 2.0 * Math.PI / 12);
                    draw.setForeground(mc_color);
                    draw.drawDashLine(DrawAWT.DOT, 0, 0, cusps_edge, 0);
                    draw.drawLine(line_upper_end + 1, 0, outer_edge, 0);
                    draw.rotate(Math.PI);
                    draw.drawDashLine(DrawAWT.DOT, 0, 0, cusps_edge, 0);
                    draw.drawLine(line_upper_end + 1, 0, outer_edge, 0);
                    draw.setForeground();
                    draw.setTransform(save);
                }
            }
            // draw solar return limit ring
            if (Resource.hasKey(prefix + "solar_return_ring")) {
                upper_mark = Resource.getInt(prefix + "solar_return_ring");
                lower_mark = upper_mark - 1;
                line_upper_end = (int) (ring_pos[upper_mark] * radius + 0.5) - 1;
                line_lower_end = (int) (ring_pos[lower_mark] * radius + 0.5) - 1;
                draw.rotate(-DEGREE * first_cusp_pos + 2.0 * Math.PI / 12);
                double angle = draw.getAngle();
                double delta = -360.0 * DEGREE / 100.0;
                int cur_age = 0;
                int cur_year = getYear(getDate(birth.getBirthDay()));
                int upper_pos = line_upper_end + 1;
                int lower_pos = line_lower_end + 1;
                String init_tip = Resource.getProcessString("tip_solar_return");
                for (int i = 0; i < 100; i++) {
                    draw.drawLine(lower_pos, 0, upper_pos, 0);
                    if (i > 0) {
                        int year = cur_year + cur_age;
                        boolean bc = year <= 0;
                        if (bc)
                            year = -year + 1;
                        tip.addDataToNextTip(cur_year + cur_age, init_tip);
                        tip.addTip(
                                lower_pos,
                                upper_pos,
                                angle + delta,
                                angle,
                                false,
                                0,
                                Integer.toString(cur_age + 1)
                                        + Resource.getString("age") + " "
                                        + Integer.toString(year)
                                        + Resource.getString("year_char")
                                        + (bc ? " B.C." : ""));
                        tip.clearNextTip();
                    }
                    draw.rotate(delta);
                    angle += delta;
                    cur_age++;
                }
                draw.setTransform(save);
            }
            // draw lunar return ring
            if (Resource.hasKey(prefix + "lunar_return_ring")) {
                upper_mark = Resource.getInt(prefix + "lunar_return_ring");
                lower_mark = upper_mark - 1;
                line_upper_end = (int) (ring_pos[upper_mark] * radius + 0.5) - 1;
                line_lower_end = (int) (ring_pos[lower_mark] * radius + 0.5) - 1;
                int transit_len = (lunar_transits == null) ? 0
                        : lunar_transits.length;
                int len = Math.max(12, transit_len);
                draw.rotate(-DEGREE * first_cusp_pos + 2.0 * Math.PI / 12);
                double angle = draw.getAngle();
                double delta = -360.0 * DEGREE / len;
                int upper_pos = line_upper_end + 1;
                int lower_pos = line_lower_end + 1;
                String init_tip = Resource.getProcessString("tip_lunar_return");
                String zone = now.getZone();
                int[] date_buf = new int[5];
                for (int i = 0; i < len; i++) {
                    draw.drawLine(lower_pos, 0, upper_pos, 0);
                    if (i < transit_len) {
                        Calculate.getDateFromJulianDayUT(lunar_transits[i],
                                date_buf);
                        String str = BaseCalendar.formatDate(zone, date_buf);
                        tip.addDataToNextTip(lunar_transits[i], init_tip);
                        tip.addTip(lower_pos, upper_pos, angle + delta, angle,
                                false, 0, str);
                        tip.clearNextTip();
                    }
                    draw.rotate(delta);
                    angle += delta;
                }
                draw.setTransform(save);
            }
        } else if (!show_horiz) {
            // draw life/self master bar
            draw.rotate(-DEGREE * life_sign_pos + 2.0 * Math.PI / 12);
            draw.setForeground(life_master_color);
            draw.drawWideLine(bar_width, line_lower_end, 0, line_upper_end, 0);
            draw.setTransform(save);
            if (draw_self_master) {
                draw.rotate(-DEGREE * self_sign_pos + 2.0 * Math.PI / 12);
                draw.setForeground(self_master_color);
                draw.drawWideLine(bar_width, line_lower_end, 0, line_upper_end,
                        0);
                draw.setTransform(save);
            }
            draw.setForeground();
        }
        // draw ring
        for (int i = num_ring - 1; i >= 0; i--) {
            int pos = (int) (ring_pos[i] * radius + 0.5);
            draw.setForeground(marker_color);
            if (ring_draw_type.length <= i || ring_draw_type[i] >= 0)
                draw.drawCircle(pos);
            else if (ring_draw_type[i] == -2)
                draw.drawDashCircle(DrawAWT.DASH, pos);
            else if (ring_draw_type[i] == -12) {
                draw.setTransform(save);
                draw.translate(0, horiz_y_offset);
                save = draw.getTransform();
                int h_dist = horiz_dist / 2;
                draw.drawRect(-horiz_dist, -h_dist, 2 * horiz_dist, horiz_dist);
                draw.setForeground(horiz_color);
                draw.drawDashLine(DrawAWT.DOT, -horiz_dist, 0, horiz_dist, 0);
                draw.drawDashLine(DrawAWT.DOT, 0, -h_dist, 0, h_dist);
                draw.drawDashLine(DrawAWT.DOT, -h_dist, -h_dist, -h_dist,
                        h_dist);
                draw.drawDashLine(DrawAWT.DOT, h_dist, -h_dist, h_dist, h_dist);
            }
            if (ring_draw_type.length <= i || ring_draw_type[i] <= 0
                    && ring_draw_type[i] > -10) {
                int next = (int) (ring_pos[i - 1] * radius + 0.5);
                for (int j = 0; j < 12; j++) {
                    draw.rotate(-2 * Math.PI / 12);
                    draw.drawLine(next, 0, pos, 0);
                }
                draw.setTransform(save);
            }
            if (ring_draw_type.length > i
                    && (ring_draw_type[i] == 2 || ring_draw_type[i] == -11)) {
                int next = (int) (ring_pos[i - 1] * radius + 0.5);
                double offset = Math.PI / 24;
                if (i - 1 == mountain_ring)
                    offset -= mountain_offset * DEGREE;
                draw.rotate(offset);
                for (int j = 0; j < 24; j++) {
                    draw.rotate(-2 * Math.PI / 24);
                    draw.drawLine(next, 0, pos, 0);
                }
                draw.setTransform(save);
            }
            if (ring_draw_type.length > i && ring_draw_type[i] == 3
                    && Calculate.isValid(life_sign_pos)) {
                String[] house_number = Resource.getStringArray("house_number");
                boolean mark_birth = Resource
                        .hasKey(prefix + "birth_sign_ring");
                boolean mark_now = !ChartMode.isChartMode(ChartMode.PICK_MODE)
                        && Resource.hasKey(prefix + "now_sign_ring");
                int birth_pos = 0, now_pos = 0, birth_pos_next = 0, now_pos_next = 0;
                int next = (int) (ring_pos[i - 1] * radius + 0.5);
                int height = pos - next;
                int font_height = height - 3;
                int color_index = i;
                if (mark_birth) {
                    int mark = Resource.getInt(prefix + "birth_sign_ring") - 1;
                    birth_pos = (int) (ring_pos[mark] * radius + 0.5);
                    birth_pos_next = (int) (ring_pos[mark + 1] * radius + 0.5);
                    color_index = mark + 1;
                }
                draw.setFittedFont(font_height, pos, 1, true, print);
                if (mark_now) {
                    int mark = Resource.getInt(prefix + "now_sign_ring") - 1;
                    now_pos = (int) (ring_pos[mark] * radius + 0.5);
                    now_pos_next = (int) (ring_pos[mark + 1] * radius + 0.5);
                }
                double last_cusp = birth_cusp[12];
                draw.rotate(-DEGREE * last_cusp + 2.0 * Math.PI / 12);
                for (int j = 1; j < birth_cusp.length; j++) {
                    double cusp = birth_cusp[j];
                    double gap = DEGREE
                            * Calculate.getDegreeGap(cusp, last_cusp);
                    draw.rotate(-0.5 * gap);
                    draw.setRingColor(color_index, ring_type, colors);
                    draw.drawRotatedStringHoriz(next, height,
                            house_number[(j == 1) ? 11 : (j - 2)]);
                    draw.setForeground(colors[5]);
                    draw.rotate(-0.5 * gap);
                    draw.drawLine(pos, 0, next, 0);
                    if (mark_birth) {
                        draw.drawDashLine(DrawAWT.DOT, birth_pos, 0,
                                birth_pos_next, 0);
                    }
                    if (mark_now) {
                        draw.drawDashLine(DrawAWT.DOT, now_pos, 0,
                                now_pos_next, 0);
                    }
                    draw.setForeground();
                    double angle = draw.getAngle();
                    String str = house_name[j - 1] + ": "
                            + cal.formatDegree(birth_cusp[j], true, true);
                    tip.addTip(next, pos, angle, angle + gap, true, 0, str);
                    last_cusp = cusp;
                }
                draw.setTransform(save);
            }
            draw.setForeground();
        }
        // draw ring data
        for (int ring = 0; ring < num_ring; ring++) {
            String ring_key = prefix + "ring" + (ring + 1);
            if (!Resource.hasKey(ring_key) || show_house_system
                    && ring + 1 == house_system_draw_pos) {
                continue;
            }
            int pos = (int) (ring_pos[ring] * radius + 0.5);
            int upper_pos = (int) (ring_pos[ring + 1] * radius + 0.5);
            int height = upper_pos - pos;
            String[] ring_data = Resource.getStringArray(ring_key);
            if (Resource.hasKey(ring_key + "_substring")) {
                int[] range = Resource.getIntArray(ring_key + "_substring");
                for (int i = 0; i < ring_data.length; i++)
                    ring_data[i] = ring_data[i].substring(range[0], range[1]);
            }
            double theta = -2 * Math.PI / ring_data.length;
            draw.setTransform(save);
            double offset = Math.PI / 12;
            if (ring == mountain_ring)
                offset -= mountain_offset * DEGREE;
            draw.rotate(offset);
            int font_height = height - 3;
            if (ring_label_type[ring] > 0)
                font_height /= ring_data[0].length();
            draw.setRingColor(ring + 1, ring_type, colors);
            draw.setFittedFont(font_height, pos, 1, true, print);
            for (int j = 0; j < ring_data.length; j++) {
                draw.rotate(theta);
                if (ring == 0) {
                    String sign_tip = null;
                    if (set_tip) {
                        sign_tip = full_twelve_signs[j].substring(2, 3) + "("
                                + full_twelve_signs[j].substring(1, 2) + ", "
                                + chinese_zodiac_signs[j] + ")";
                    }
                    String weak = getWeakHouse(twelve_signs[j]);
                    String solid = getSolidHouse(twelve_signs[j]);
                    if (!weak.equals("") || !solid.equals("")) {
                        AffineTransform rot = draw.getTransform();
                        int d_height = draw.getFontHeight();
                        int d_pos = upper_pos - d_height;
                        double degree = draw.degreeInWidth(d_pos);
                        if (!weak.equals("")) {
                            draw.rotate(Math.PI / 12 - 0.2 * degree * DEGREE);
                            if (!use_bw) {
                                draw.setForeground(colors[3]);
                                draw.setColor();
                            }
                            draw.drawRotatedSign(d_pos + 3 * d_height / 4,
                                    d_height / 4, false);
                            draw.setTransform(rot);
                        }
                        if (!solid.equals("")) {
                            draw.rotate(-Math.PI / 12 + 0.2 * degree * DEGREE);
                            if (!use_bw) {
                                draw.setForeground(colors[4]);
                                draw.setColor();
                            }
                            draw.drawRotatedSign(d_pos + 3 * d_height / 4,
                                    d_height / 4, true);
                            draw.setTransform(rot);
                        }
                        draw.setRingColor(ring + 1, ring_type, colors);
                        if (set_tip)
                            sign_tip += ": " + weak + solid;
                    }
                    if (set_tip) {
                        double angle = draw.getAngle();
                        double delta = 2 * Math.PI / 24;
                        tip.addTip(pos, upper_pos, angle - delta,
                                angle + delta, false, 0, sign_tip);
                    }
                }
                String key = ring_data[j];
                if (ring_label_type[ring] > 0) {
                    AffineTransform rot = draw.getTransform();
                    draw.drawRotatedStringVert(pos, height, key);
                    draw.setTransform(rot);
                } else {
                    draw.drawRotatedStringHoriz(pos, height, key);
                }
            }
            draw.setColor();
        }
        if (Resource.hasKey(prefix + "cusps")) {
            int ring = Resource.getInt(prefix + "cusps") - 1;
            int pos = (int) (ring_pos[ring] * radius + 0.5);
            int upper_pos = (int) (ring_pos[ring + 1] * radius + 0.5);
            int height = upper_pos - pos;
            int pos_base = pos;
            int pos_offset = 0, height_offset = 0;
            double[] cusps_offset = new double[2];
            if (Resource.hasKey(prefix + "cusps_offset")) {
                Resource.getDoubleArray(prefix + "cusps_offset", cusps_offset);
                pos_offset = (int) (cusps_offset[0] * height + 0.5);
                height_offset = (int) (cusps_offset[1] * height + 0.5);
            }
            pos_base += pos_offset;
            height -= pos_offset + height_offset;
            int font_height = height - 3;
            String[] ring_data = Resource.getStringArray(prefix + "cusps_name");
            draw.setTransform(save);
            draw.rotate(-DEGREE * first_cusp_pos + 2.0 * Math.PI / 12);
            draw.setFittedFont(font_height, pos_base, 1, true, print);
            AffineTransform rot = draw.getTransform();
            double[] cusp = single_wheel_mode ? now_cusp : birth_cusp;
            double offset = show_gauquelin ? 0.0 : cusp[1];
            double next_angle = 0.0;
            for (int j = 0; j < ring_data.length; j++) {
                double angle = next_angle;
                draw.rotate(DEGREE * angle);
                draw.setForeground(marker_color);
                draw.drawLine(pos, 0, upper_pos, 0);
                draw.setForeground();
                if (show_gauquelin) {
                    next_angle = offset + (j + 1) * 10.0;
                    if (next_angle < 0.0)
                        next_angle += 360.0;
                } else {
                    next_angle = offset - cusp[(j == 11) ? 1 : (j + 2)];
                    if (next_angle >= 0.0)
                        next_angle -= 360.0;
                }
                draw.setTransform(rot);
                draw.rotate(0.5 * DEGREE * (angle + next_angle));
                String key = ring_data[j];
                draw.setRingColor(ring + 1, ring_type, colors);
                if (show_gauquelin) {
                    draw.drawRotatedStringHoriz(pos_base, height, key);
                    boolean plus = gauquelinPlusZone(j + 1);
                    int d_height = draw.getFontHeight();
                    int d_pos = upper_pos - d_height;
                    double degree = draw.degreeInWidth(d_pos);
                    draw.rotate(Math.PI / 36.0 - 0.2 * degree * DEGREE);
                    if (!use_bw) {
                        draw.setForeground(colors[plus ? 4 : 3]);
                        draw.setColor();
                    }
                    draw.drawRotatedSign(d_pos + 3 * d_height / 4,
                            d_height / 4, plus);
                } else {
                    double cusp_width = angle - next_angle;
                    double width = draw.degreeInWidth(key, pos_base);
                    if (width <= cusp_width)
                        draw.drawRotatedStringHoriz(pos_base, height, key);
                }
                draw.setTransform(rot);
            }
            draw.setColor();
        }
        int arrow_width = Resource.getInt(print ? "wide_arrow_width"
                : "arrow_width");
        // draw degree mark
        for (int iter = 0; iter < 3; iter++) {
            String key = prefix + "mark_down_ring" + Integer.toString(iter);
            if (Resource.hasKey(key)) {
                int mark = Resource.getInt(key) - 1;
                int upper = (int) (ring_pos[mark] * radius + 0.5);
                int lower = (int) (ring_pos[mark - 1] * radius + 0.5);
                int h_upper = upper / 2;
                int h_lower = lower - h_upper;
                if (show_horiz) {
                    Position.setBound(-lower, -h_lower, lower, h_lower);
                    draw.setForeground(marker_color);
                    draw.setTransform(save);
                    int delta = (upper - lower) / 3;
                    for (int i = 1; i < 18; i++) {
                        int pos;
                        if ((i % 6) == 0) {
                            pos = lower;
                        } else if ((i % 2) == 0) {
                            pos = upper - 2 * delta;
                        } else {
                            pos = upper - delta;
                        }
                        int val = (int) (i * upper / 36.0 + 0.5);
                        draw.drawLine(-upper, val, -pos, val);
                        draw.drawLine(upper, val, pos, val);
                        draw.drawLine(-upper, -val, -pos, -val);
                        draw.drawLine(upper, -val, pos, -val);
                    }
                    for (int i = 1; i < 18; i++) {
                        int pos;
                        if ((i % 6) == 0) {
                            pos = h_lower;
                        } else if ((i % 2) == 0) {
                            pos = h_upper - 2 * delta;
                        } else {
                            pos = h_upper - delta;
                        }
                        int val = (int) (i * upper / 36.0 + 0.5);
                        draw.drawLine(val, -h_upper, val, -pos);
                        draw.drawLine(val, h_upper, val, pos);
                        draw.drawLine(-val, -h_upper, -val, -pos);
                        draw.drawLine(-val, h_upper, -val, pos);
                        draw.drawLine(h_upper + val, -h_upper, h_upper + val,
                                -pos);
                        draw.drawLine(h_upper + val, h_upper, h_upper + val,
                                pos);
                        draw.drawLine(-h_upper - val, -h_upper, -h_upper - val,
                                -pos);
                        draw.drawLine(-h_upper - val, h_upper, -h_upper - val,
                                pos);
                    }
                } else {
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        if (mountain_pos != Calculate.INVALID) {
                            draw.setTransform(save);
                            draw.rotate(-DEGREE
                                    * (mountain_pos - true_north_magnetic_shift)
                                    + 2.0 * Math.PI / 12);
                            draw.setForeground(mountain_color);
                            draw.drawWideLine(arrow_width, -lower, 0, -upper, 0);
                            draw.drawWideLine(arrow_width, lower, 0, upper, 0);
                        }
                    } else if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                        draw.setTransform(save);
                        draw.rotate(-DEGREE * life_sign_pos + 2.0 * Math.PI
                                / 12);
                        draw.setForeground(life_master_color);
                        draw.drawWideLine(bar_width, lower, 0, upper, 0);
                        if (draw_self_master) {
                            draw.setTransform(save);
                            draw.rotate(-DEGREE * self_sign_pos + 2.0 * Math.PI
                                    / 12);
                            draw.setForeground(self_master_color);
                            draw.drawWideLine(bar_width, lower, 0, upper, 0);
                        }
                    }
                    int delta = (upper - lower) / 3;
                    draw.setForeground(marker_color);
                    for (int i = 0; i < 360; i++) {
                        draw.setTransform(save);
                        double degree = i;
                        if (ChartMode.isChartMode(ChartMode.PICK_MODE))
                            degree += true_north_magnetic_shift;
                        draw.rotate(degree * DEGREE);
                        int pos;
                        if ((i % 30) == 0) {
                            pos = lower;
                        } else if ((i % 5) == 0) {
                            pos = upper - 2 * delta;
                        } else {
                            pos = upper - delta;
                        }
                        draw.drawLine(pos, 0, upper, 0);
                    }
                }
                draw.setForeground();
            }
            key = prefix + "mark_up_ring" + Integer.toString(iter);
            if (Resource.hasKey(key)) {
                int mark = Resource.getInt(key) - 1;
                int lower = (int) (ring_pos[mark] * radius + 0.5);
                int upper = (int) (ring_pos[mark + 1] * radius + 0.5);
                if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    if (mountain_pos != Calculate.INVALID) {
                        draw.setTransform(save);
                        draw.rotate(-DEGREE
                                * (mountain_pos - true_north_magnetic_shift)
                                + 2.0 * Math.PI / 12);
                        draw.setForeground(mountain_color);
                        draw.drawWideLine(arrow_width, -lower, 0, -upper, 0);
                        draw.drawWideLine(arrow_width, lower, 0, upper, 0);
                    }
                } else if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    draw.setTransform(save);
                    draw.rotate(-DEGREE * life_sign_pos + 2.0 * Math.PI / 12);
                    draw.setForeground(life_master_color);
                    draw.drawWideLine(bar_width, lower, 0, upper, 0);
                    if (draw_self_master) {
                        draw.setTransform(save);
                        draw.rotate(-DEGREE * self_sign_pos + 2.0 * Math.PI
                                / 12);
                        draw.setForeground(self_master_color);
                        draw.drawWideLine(bar_width, lower, 0, upper, 0);
                    }
                }
                int delta = (upper - lower) / 3;
                draw.setForeground(marker_color);
                for (int i = 0; i < 360; i++) {
                    draw.setTransform(save);
                    draw.rotate(i * DEGREE);
                    int pos;
                    if ((i % 30) == 0) {
                        pos = upper;
                    } else if ((i % 5) == 0) {
                        pos = lower + 2 * delta;
                    } else {
                        pos = lower + delta;
                    }
                    draw.drawLine(lower, 0, pos, 0);
                }
                draw.setForeground();
            }
        }
        // draw degree label and arrow, add change position tip
        if (!show_horiz && ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            int mark = Resource.getInt(prefix + "limit_ring") - 1;
            int pos = (int) (ring_pos[mark] * radius + 0.5);
            int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
            int height = upper_pos - pos;
            int m_mark = mountain_ring;
            int m_pos = (int) (ring_pos[m_mark] * radius + 0.5);
            int m_upper_pos = (int) (ring_pos[m_mark + 1] * radius + 0.5);
            draw.setTransform(save);
            draw.rotate(75 * DEGREE);
            for (int i = 0; i < 24; i++) {
                AffineTransform trans = draw.getTransform();
                draw.rotate(true_north_magnetic_shift * DEGREE);
                draw.setFittedFont(height - 3, pos, 1, true, print);
                draw.setRingColor(mark + 1, ring_type, colors);
                draw.drawRotatedStringHoriz(pos, height,
                        Integer.toString(i * 15));
                draw.setTransform(trans);
                // add mountain tip
                double angle = draw.getAngle() + mountain_offset * DEGREE;
                double delta = Math.PI / 24;
                tip.addTip(m_pos, m_upper_pos, angle - delta, angle + delta,
                        false, 0, full_mountain_signs[(43 - i) % 24]);
                draw.rotate(15 * DEGREE);
            }
            if (mountain_pos != Calculate.INVALID) {
                // draw arrow
                draw.setTransform(save);
                draw.rotate(-DEGREE
                        * (mountain_pos - true_north_magnetic_shift) + 2.0
                        * Math.PI / 12);
                draw.setForeground(mountain_color);
                int dist = (int) (ring_pos[0] * radius + 0.5) - 1;
                int len = dist / Resource.getInt("arrow_factor");
                draw.drawWideLine(arrow_width, -dist, 0, -dist + len, -len);
                draw.drawWideLine(arrow_width, -dist, 0, -dist + len, len);
                draw.drawWideLine(arrow_width, -dist, 0, dist, 0);
            }
            // change position tip
            mark = Resource.getInt(prefix + "change_pos_lower_ring") - 1;
            pos = (int) (ring_pos[mark] * radius + 0.5);
            mark = Resource.getInt(prefix + "change_pos_upper_ring") - 1;
            upper_pos = (int) (ring_pos[mark] * radius + 0.5);
            tip.addDegreeMarkerToNextTip(null);
            tip.addTip(
                    pos,
                    upper_pos,
                    true,
                    Resource.getString("tip_change_pos_left") + "\n"
                            + Resource.getString("tip_change_pos_right"));
            tip.clearNextTip();
            draw.setColor();
        }
        // draw birth year sign
        String cur_year_limit = "";
        if (Calculate.isValid(life_sign_pos)) {
            int max_length = Math.max(half_stellar_signs.length, signs.length
                    + fixstar_signs.length);
            Position[] position = new Position[max_length];
            if (Resource.hasKey(prefix + "birth_year_ring")) {
                int mark = Resource.getInt(prefix + "birth_year_ring") - 1;
                int pos = (int) (ring_pos[mark] * radius + 0.5);
                int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
                int height = upper_pos - pos;
                int limit_mark = Resource.getInt(prefix + "limit_ring") - 1;
                int limit_pos = (int) (ring_pos[limit_mark] * radius + 0.5);
                int limit_height = ((int) (ring_pos[limit_mark + 1] * radius + 0.5))
                        - limit_pos;
                int year_mark = Resource.getInt(prefix + "year_mark_ring") - 1;
                int year_pos = (int) (ring_pos[year_mark] * radius + 0.5);
                int year_upper_pos = (int) (ring_pos[year_mark + 1] * radius + 0.5);
                int year_mid_pos = (year_pos + year_upper_pos) / 2;
                int year_height = year_upper_pos - year_pos;
                double base = DEGREE * (((int) life_sign_pos) / 30 * 30);
                double theta = 2 * Math.PI / 12;
                now_date = getDate(now.getNowDay());
                Date now_start_date = getStartEndDate(calendar, now_date, false);
                Date now_end_date = getStartEndDate(calendar, now_date, true);
                Date date = getDate(birth.getBirthDay());
                Date cur_date = date;
                if (!ChartMode.isChartMode(ChartMode.PICK_MODE)
                        && !show_aspects) {
                    String[] c_str = cal.formatChineseDegree(life_sign_pos,
                            stellar_sign_pos, full_stellar_signs, print);
                    if (c_str != null) {
                        int c_pos = (int) (ring_pos[0] * radius + 0.5);
                        draw.setTransform(save);
                        draw.rotate(-base_offset * DEGREE - Math.PI / 2);
                        AffineTransform trans = draw.getTransform();
                        draw.setFittedFont(c_pos / 2, c_pos, 1, true, print);
                        draw.setRingColor(mark + 1, ring_type, colors);
                        draw.drawRotatedStringVert(-c_pos, 2 * c_pos, c_str[1]);
                        int f_width = (int) (1.1 * draw.getFontWidth());
                        draw.setTransform(trans);
                        draw.translate(0, f_width);
                        draw.drawRotatedStringVert(-c_pos, 2 * c_pos,
                                Resource.getString("set_life"));
                        draw.setTransform(trans);
                        draw.translate(0, -f_width);
                        draw.drawRotatedStringVert(-c_pos, 2 * c_pos, c_str[0]);
                        draw.setColor();
                    }
                }
                draw.setTransform(save);
                double base_angle = -Math.PI / 12 - base;
                draw.rotate(base_angle);
                now_degree = now_start_degree = now_end_degree = -1.0;
                double last_angle = 0.0;
                int cur_age = 0, cur_year = 0;
                String init_tip = Resource.getString("tip_change_year");
                int child_age_limit = getChildLimit(true);
                for (int i = 0; i < 12; i++) {
                    draw.rotate(theta);
                    AffineTransform rot = draw.getTransform();
                    if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        draw.rotate(-Math.PI / 12);
                        if (i == 0) { // draw birth line
                            draw.setForeground(birth_degree_color);
                            draw.drawLine(year_pos, 0, year_upper_pos, 0);
                            draw.setForeground();
                            last_angle = base_angle + theta - Math.PI / 12;
                            cur_age = 1;
                            cur_year = getYear(cur_date);
                        }
                        DateFormat format = new SimpleDateFormat("yyyy");
                        String marker = format.format(date);
                        draw.setRingColor(limit_mark + 1, ring_type, colors);
                        draw.setFittedFont(limit_height - 4, pos, 1, true,
                                print);
                        draw.drawRotatedStringHoriz(limit_pos, limit_height,
                                marker);
                        draw.setColor();
                        Date next_date;
                        if (i == 0) {
                            next_date = addChildYearToBirthDate();
                        } else {
                            next_date = addYearToBirthDate(limit_seq[i]);
                        }
                        if (print)
                            draw.setFittedFont(year_height - 2, year_pos, 1,
                                    true, true);
                        for (int j = 0;; j++) {
                            cur_date = (j == 0) ? getStartEndDate(work_cal,
                                    cur_date, true) : addYear(cur_date, 1);
                            if (!cur_date.before(next_date)) {
                                cur_date = addYear(cur_date, -1);
                                break;
                            }
                            double degree = 30.0
                                    * getFraction(cur_date, date, next_date)
                                    * DEGREE - Math.PI / 12;
                            draw.setTransform(rot);
                            draw.rotate(degree);
                            draw.setForeground(birth_degree_color);
                            draw.drawLine(year_mid_pos, 0, year_upper_pos, 0);
                            double angle = base_angle + (i + 1) * theta
                                    + degree;
                            if (show_now) {
                                tip.addDataToNextTip(cur_year + cur_age - 1,
                                        init_tip);
                            }
                            tip.addTip(
                                    year_pos,
                                    year_upper_pos,
                                    -radian_offset - angle,
                                    -radian_offset - last_angle,
                                    false,
                                    0,
                                    getLimitTip(cur_age, cur_year,
                                            child_age_limit));
                            tip.clearNextTip();
                            if (print) {
                                draw.setTransform(save);
                                draw.rotate(0.5 * (angle + last_angle));
                                draw.setRingColor(mark + 1, ring_type, colors);
                                draw.drawRotatedStringHoriz(year_pos,
                                        year_height, Integer.toString(cur_age));
                            }
                            last_angle = angle;
                            cur_age++;
                        }
                        draw.setForeground();
                        if (!now_start_date.before(date)
                                && now_start_date.before(next_date)) {
                            now_start_degree = 30.0
                                    * getFraction(now_start_date, date,
                                            next_date) + (i * theta) / DEGREE;
                            double offset = (((int) life_sign_pos) / 30) * 30;
                            now_start_degree = offset - now_start_degree + 30.0;
                            if (now_start_degree < 0.0)
                                now_start_degree += 360.0;
                        }
                        if (!now_end_date.before(date)
                                && now_end_date.before(next_date)) {
                            now_end_degree = 30.0
                                    * getFraction(now_end_date, date, next_date)
                                    + (i * theta) / DEGREE;
                            double offset = (((int) life_sign_pos) / 30) * 30;
                            now_end_degree = offset - now_end_degree + 30.0;
                            if (now_end_degree < 0.0)
                                now_end_degree += 360.0;
                        }
                        if (!now_date.before(date)
                                && now_date.before(next_date)) {
                            now_degree = 30.0
                                    * getFraction(now_date, date, next_date)
                                    + (i * theta) / DEGREE;
                            double offset = (((int) life_sign_pos) / 30) * 30;
                            now_degree = offset - now_degree + 30.0;
                            if (now_degree < 0.0)
                                now_degree += 360.0;
                        }
                        date = next_date;
                    }
                    draw.setTransform(rot);
                    if (i == 0) {
                        draw.setColor(life_master_fg_color);
                    } else {
                        draw.setRingColor(mark + 1, ring_type, colors);
                    }
                    draw.setFittedFont(height - 3, pos, 1, true, print);
                    draw.drawRotatedStringHoriz(pos, height, year_signs[i]);
                    draw.setTransform(rot);
                    if (birth_table != null) {
                        String sign_tip = year_signs[i]
                                + (String) birth_table.get(year_signs[i]);
                        double angle = draw.getAngle();
                        double delta = 2 * Math.PI / 24;
                        tip.addTip(pos, upper_pos, angle - delta,
                                angle + delta, false, 0, sign_tip);
                    }
                    draw.setColor();
                }
                if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    if (now_start_degree >= 0.0 && now_end_degree >= 0.0) {
                        draw.setTransform(save);
                        draw.rotate(theta);
                        draw.setBackground(now_line_color);
                        int width = (int) ((line_upper_end - line_lower_end) * Resource
                                .getDouble("now_bar_width_ratio"
                                        + (print ? "_with_year" : "")));
                        draw.fillArc(line_lower_end, line_lower_end + width,
                                now_start_degree * DEGREE, now_end_degree
                                        * DEGREE);
                        draw.setBackground();
                        cur_year_limit = cal.formatDegree(now_start_degree,
                                stellar_sign_pos, full_stellar_signs, "  ",
                                false, false)
                                + " "
                                + Resource.getString("to")
                                + " "
                                + cal.formatDegree(now_end_degree,
                                        stellar_sign_pos, full_stellar_signs,
                                        "  ", false, false);
                    }
                    String str = cur_year_limit;
                    if (!str.trim().equals(""))
                        str = Resource.getString("limit") + ": " + str;
                    tab.replace("limit", str);
                }
            }
            // draw the 28 constellations
            for (int iter = 0; iter < 2; iter++) {
                String ring_name = prefix
                        + ((iter == 0) ? "stellar_ring" : "compass_ring");
                double[] stellar_pos = (iter == 0) ? stellar_sign_pos
                        : compass_sign_pos;
                if (Resource.hasKey(ring_name)) {
                    int mark = Resource.getInt(ring_name) - 1;
                    int pos = (int) (ring_pos[mark] * radius + 0.5);
                    int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
                    int height = upper_pos - pos;
                    boolean use_half = Resource.getInt(prefix + "use_half") != 0;
                    String[] stellar_signs = use_half ? half_stellar_signs
                            : full_stellar_signs;
                    int len = stellar_signs.length;
                    int gap = height / 5;
                    draw.setFittedFont(
                            height / stellar_signs[0].length() - gap, pos, 1,
                            true, print);
                    double[] center_pos = computeCenterPos(stellar_pos);
                    initSignShift(0, len, stellar_signs, center_pos, null,
                            null, null, position);
                    computeSignShift(draw, pos, len, false, position);
                    for (int i = 0; i < len; i++) {
                        double degree = stellar_pos[i];
                        draw.setTransform(save);
                        draw.rotate(-(degree - 30.0) * DEGREE);
                        draw.setForeground(house_line_color);
                        draw.drawLine(pos, 0, upper_pos, 0);
                        if (!ChartMode.isChartMode(ChartMode.PICK_MODE))
                            draw.drawLine(line_lower_end, 0, line_upper_end, 0);
                        draw.setForeground();
                        draw.setTransform(save);
                        double delta = position[i].getShift();
                        double center = position[i].getLocation();
                        degree = center + delta;
                        draw.setTransform(save);
                        draw.rotate(-(degree - 30.0) * DEGREE);
                        draw.setRingColor(mark + 1, ring_type, colors);
                        draw.drawRotatedStringVert(pos + gap / 2, height - gap,
                                position[i].getName());
                        draw.setColor();
                        if (use_half) {
                            double lower = stellar_pos[i];
                            double upper = stellar_pos[(i + 1) % len];
                            if (upper < lower)
                                upper += 360.0;
                            delta = upper - lower;
                            center = 0.5 * (lower + upper);
                            center = (center - 30.0 - base_offset) * DEGREE;
                            delta *= 0.5 * DEGREE;
                            String str = full_stellar_signs[i];
                            if (iter == 0) {
                                boolean more = false;
                                String t_str = tip.getTip(
                                        CONSTELLATION_MAIN_OFFSET + i, true);
                                if (t_str != null) {
                                    if (!more) {
                                        more = true;
                                        str += ": ";
                                    }
                                    str += t_str;
                                }
                                t_str = tip.getTip(
                                        CONSTELLATION_SUB_OFFSET + i, true);
                                if (t_str != null) {
                                    if (!more) {
                                        more = true;
                                        str += ": ";
                                    } else {
                                        str += "; ";
                                    }
                                    str += Resource
                                            .getString("same_constellation")
                                            + ": " + t_str;
                                }
                            }
                            tip.addTip(pos, upper_pos, center - delta, center
                                    + delta, false, 0, str);
                        }
                    }
                }
            }
            int diameter = (int) (Resource.getDouble("dot_ratio") * radius + 0.5);
            diameter = Math.max(2, diameter);
            // draw birth sign
            if (Resource.hasKey(prefix + "birth_sign_ring")) {
                boolean outer = false;
                int mark, pos, lower_pos, circle_offset, other_circle_offset = 0;
                mark = Resource.getInt(prefix + "birth_sign_ring") - 1;
                if (dual_ring) {
                    int now_mark = Resource.getInt(prefix + "now_sign_ring") - 1;
                    if (now_mark < mark) {
                        outer = true;
                        pos = (int) (ring_pos[now_mark] * radius + 0.5);
                        lower_pos = (int) (ring_pos[now_mark - 1] * radius + 0.5);
                        other_circle_offset = lower_pos + (pos - lower_pos) / 2;
                    }
                }
                pos = (int) (ring_pos[mark] * radius + 0.5);
                lower_pos = (int) (ring_pos[mark - 1] * radius + 0.5);
                circle_offset = lower_pos + (pos - lower_pos) / 2;
                int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
                int height = upper_pos - pos;
                int line_dir = Resource.getInt(prefix + "birth_sign_ring_dir");
                int v_gap = (dual_ring && !outer) ? 0 : (height / 4);
                draw.setFittedFont(height - v_gap, pos, 1, true, print);
                int len = signs.length;
                int[] sign_state = single_wheel_mode ? now_sign_state
                        : birth_sign_state;
                double[] sign_pos = show_gauquelin ? gauquelin_sign_pos
                        : (single_wheel_mode ? now_sign_pos : birth_sign_pos);
                int[] speed_color = (single_wheel_mode || ChartMode
                        .isReturnMode()) ? now_speed_color : birth_speed_color;
                Arrays.fill(sign_lock, false);
                if (show_aspects)
                    sign_lock[ASC] = sign_lock[MC] = true;
                initSignShift(0, len, signs, sign_pos, sign_state, null,
                        sign_lock, position);
                computeSignShift(draw, pos, len, true, position);
                for (int i = 0; i < len; i++) {
                    if (!Calculate.isValid(position[i].getLocation()))
                        continue;
                    draw.setTransform(save);
                    double delta = position[i].getShift();
                    double degree = position[i].getLocation() + delta;
                    draw.rotate(-(degree - 30.0) * DEGREE);
                    AffineTransform rot = draw.getTransform();
                    double angle = draw.getAngle();
                    double delta_angle = Math
                            .toRadians(draw.degreeInWidth(pos)) / 2.0;
                    tip.addTip(pos, upper_pos, angle - delta_angle, angle
                            + delta_angle, position[i].getIndex(), true);
                    if (!use_bw)
                        draw.setColor(speed_color[position[i].getState()]);
                    int gap = draw.drawRotatedStringVert(pos, height,
                            position[i].getName());
                    draw.setColor();
                    draw.setTransform(rot);
                    draw.setForeground(birth_degree_color);
                    double cos_angle = Math.cos(delta * DEGREE);
                    double sin_angle = Math.sin(delta * DEGREE);
                    if (line_dir >= 0) { // upper line
                        int rot_x = (int) (upper_pos * cos_angle + 0.5);
                        int rot_y = (int) (upper_pos * sin_angle + 0.5);
                        draw.drawLine(rot_x, rot_y, upper_pos - gap, 0);
                    }
                    if (line_dir <= 0) { // lower line
                        int rot_x = (int) (pos * cos_angle + 0.5);
                        int rot_y = (int) (pos * sin_angle + 0.5);
                        draw.drawLine(rot_x, rot_y, pos + gap, 0);
                    }
                    if (show_aspects) {
                        draw.setTransform(save);
                        degree = position[i].getLocation();
                        draw.rotate(-(degree - 30.0) * DEGREE);
                        draw.setBackground(birth_degree_color);
                        draw.fillCircle(circle_offset - diameter / 2,
                                -diameter / 2, diameter, diameter);
                        if (outer) {
                            draw.drawDashLine(DrawAWT.DOT, other_circle_offset
                                    + 2 * diameter, 0, circle_offset - 2
                                    * diameter, 0);
                            draw.fillCircle(other_circle_offset - diameter / 2,
                                    -diameter / 2, diameter, diameter);
                        }
                        draw.setBackground();
                    }
                    draw.setForeground();
                }
                tip.addTip(pos, upper_pos, false, birth_sign_tip);
            }
            if (show_horiz) {
                int height = (int) (horiz_dist * 0.05 + 0.5);
                int gap = (int) (horiz_dist * 0.02 + 0.5);
                draw.setFittedFont(height, horiz_dist, 1, true, print);
                String[] top_label = Resource.getStringArray("horiz_top_axis");
                String[] bottom_label = Resource
                        .getStringArray("horiz_bottom_axis");
                draw.setTransform(save);
                for (int i = 0; i < top_label.length; i++) {
                    int x = (int) (90.0 * horiz_dist * (i - 2) / 180.0 + 0.5);
                    draw.drawString(top_label[i],
                            x - draw.stringWidth(top_label[i]) / 2, -horiz_dist
                                    / 2 - height / 2);
                    draw.drawString(bottom_label[i],
                            x - draw.stringWidth(bottom_label[i]) / 2, height
                                    + horiz_dist / 2);
                }
                int min_radius = (int) (horiz_dist / 180.0 + 0.5);
                boolean[] mark = new boolean[signs.length];
                boolean has_mark = computeSignPosition(draw, 1.0, horiz_dist,
                        min_radius, height, gap, 0.0, birth_sign_azimuth,
                        birth_sign_alt, birth_sign_diameter, mark, position);
                drawHorizSign(draw, save, 0, 0, min_radius, use_bw, null,
                        position);
                int h_width = (int) (0.23 * horiz_dist + 0.5);
                int y_offset = 3 * h_width / 8;
                int x_inc = horiz_dist / 2;
                int zoom_loc = 0;
                boolean[] zoom = new boolean[signs.length];
                boolean has_zoom = needZoom(horiz_dist / 180.0, zoom, position);
                if (has_zoom) {
                    for (int i = 0; i < signs.length; i++) {
                        if (!zoom[i])
                            continue;
                        computeSignPosition(draw, HORIZ_ZOOM, horiz_dist,
                                min_radius, height, gap, birth_sign_azimuth[i],
                                birth_sign_azimuth, birth_sign_alt,
                                birth_sign_diameter, null, position);
                        draw.setTransform(save);
                        draw.translate(
                                -horiz_dist + h_width + zoom_loc * x_inc,
                                horiz_dist / 2 + h_width + y_offset);
                        drawZoom(draw, save, i, HORIZ_ZOOM, min_radius,
                                h_width, use_bw, position);
                        zoom_loc++;
                        if (zoom_loc >= 4)
                            break;
                        zoom[i] = false;
                        clearMark(position[i], HORIZ_ZOOM * horiz_dist / 180.0,
                                0.75 * h_width, zoom, position);
                        clearMark(position[i], HORIZ_ZOOM * horiz_dist / 180.0,
                                0.75 * h_width, mark, position);
                    }
                }
                if (has_mark) {
                    double zoom_factor = 1.0;
                    for (int i = 0; i < 5; i++) {
                        zoom_factor = 2.0 + i;
                        if (!computeSignPosition(draw, zoom_factor, horiz_dist,
                                min_radius, height, gap, birth_sign_azimuth[i],
                                birth_sign_azimuth, birth_sign_alt,
                                birth_sign_diameter, null, position))
                            break;
                    }
                    int index = 0;
                    for (; zoom_loc < 4; zoom_loc++) {
                        for (; index < signs.length; index++) {
                            if (mark[index])
                                break;
                        }
                        if (index >= signs.length)
                            break;
                        draw.setTransform(save);
                        draw.translate(
                                -horiz_dist + h_width + zoom_loc * x_inc,
                                horiz_dist / 2 + h_width + y_offset);
                        drawZoom(draw, save, index, zoom_factor, min_radius,
                                h_width, use_bw, position);
                        // avoid duplicate zoom
                        clearMark(position[index], zoom_factor * horiz_dist
                                / 180.0, h_width - height, mark, position);
                        index++;
                    }
                }
            }
            // draw current sign
            if (Resource.hasKey(prefix + "now_sign_ring")) {
                boolean outer = false;
                int mark, pos, lower_pos, circle_offset, other_circle_offset = 0;
                mark = Resource.getInt(prefix + "now_sign_ring") - 1;
                if (dual_ring) {
                    int birth_mark = Resource
                            .getInt(prefix + "birth_sign_ring") - 1;
                    if (birth_mark < mark) {
                        outer = true;
                        pos = (int) (ring_pos[birth_mark] * radius + 0.5);
                        lower_pos = (int) (ring_pos[birth_mark - 1] * radius + 0.5);
                        other_circle_offset = lower_pos + (pos - lower_pos) / 2;
                    }
                }
                pos = (int) (ring_pos[mark] * radius + 0.5);
                lower_pos = (int) (ring_pos[mark - 1] * radius + 0.5);
                circle_offset = lower_pos + (pos - lower_pos) / 2;
                int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
                int height = upper_pos - pos;
                int len = signs.length;
                int line_dir = Resource.getInt(prefix + "now_sign_ring_dir");
                int v_gap = (dual_ring && !outer) ? 0 : (height / 4);
                int[] speed_color;
                draw.setFittedFont(height - v_gap, pos, 1, true, print);
                if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    initSignShift(0, len, signs, birth_sign_azimuth, null,
                            birth_sign_alt, null, position);
                    if (show_fixstar) {
                        initSignShift(len, fixstar_signs.length, fixstar_signs,
                                fixstar_sign_azimuth, null, fixstar_sign_alt,
                                null, position);
                        len += fixstar_signs.length;
                        v_gap = height / 8;
                        draw.setFittedFont((height - v_gap) / 2, pos, 1, true,
                                print);
                    }
                    computeSignShift(draw, pos, len, false, position);
                    speed_color = now_state_color;
                } else {
                    initSignShift(0, len, signs, now_sign_pos, now_sign_state,
                            null, null, position);
                    computeSignShift(draw, pos, len, false, position);
                    speed_color = now_speed_color;
                }
                for (int i = 0; i < len; i++) {
                    if (!Calculate.isValid(position[i].getLocation()))
                        continue;
                    draw.setTransform(save);
                    double delta = position[i].getShift();
                    double degree = position[i].getLocation() + delta;
                    draw.rotate(-(degree - 30.0) * DEGREE);
                    AffineTransform rot = draw.getTransform();
                    double angle = draw.getAngle();
                    double delta_angle = Math
                            .toRadians(draw.degreeInWidth(pos)) / 2.0;
                    int index = position[i].getIndex();
                    if (index < signs.length) {
                        tip.addTip(pos, upper_pos, angle - delta_angle, angle
                                + delta_angle, index, false);
                    } else {
                        tip.addTip(pos, upper_pos, angle - delta_angle, angle
                                + delta_angle, FIXSTAR_OFFSET + index
                                - signs.length, false);
                    }
                    if (!use_bw) {
                        draw.setColor(speed_color[position[i].getState()]);
                    }
                    int gap = draw.drawRotatedStringVert(pos, height,
                            position[i].getName());
                    draw.setColor();
                    draw.setTransform(rot);
                    draw.setForeground(now_degree_color);
                    double cos_angle = Math.cos(delta * DEGREE);
                    double sin_angle = Math.sin(delta * DEGREE);
                    if (line_dir >= 0) { // upper line
                        int rot_x = (int) (upper_pos * cos_angle + 0.5);
                        int rot_y = (int) (upper_pos * sin_angle + 0.5);
                        draw.drawLine(rot_x, rot_y, upper_pos - gap, 0);
                    }
                    if (line_dir <= 0) { // lower line
                        int rot_x = (int) (pos * cos_angle + 0.5);
                        int rot_y = (int) (pos * sin_angle + 0.5);
                        draw.drawLine(rot_x, rot_y, pos + gap, 0);
                    }
                    if (show_aspects) {
                        draw.setTransform(save);
                        degree = position[i].getLocation();
                        draw.rotate(-(degree - 30.0) * DEGREE);
                        draw.setBackground(now_degree_color);
                        draw.fillCircle(circle_offset - diameter / 2,
                                -diameter / 2, diameter, diameter);
                        if (outer) {
                            draw.drawDashLine(DrawAWT.DOT, other_circle_offset
                                    + 2 * diameter, 0, circle_offset - 2
                                    * diameter, 0);
                            draw.fillCircle(other_circle_offset - diameter / 2,
                                    -diameter / 2, diameter, diameter);
                        }
                        draw.setBackground();
                    }
                    draw.setForeground();
                }
                tip.addTip(pos, upper_pos, false, now_sign_tip);
            }
            // draw fixstar sign
            if (Resource.hasKey(prefix + "star_ring")) {
                int mark = Resource.getInt(prefix + "star_ring") - 1;
                int pos = (int) (ring_pos[mark] * radius + 0.5);
                int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
                int height = upper_pos - pos;
                int len = fixstar_signs.length;
                int line_dir = Resource.getInt(prefix + "star_ring_dir");
                int v_gap = height / 8;
                draw.setFittedFont((height - v_gap) / 2, pos, 1, true, print);
                initSignShift(0, len, fixstar_signs, fixstar_sign_pos,
                        fixstar_sign_state, null, null, position);
                computeSignShift(draw, pos, len, false, position);
                String init_tip = Resource
                        .getProcessString("tip_fixstar_change_pos");
                for (int i = 0; i < len; i++) {
                    if (!Calculate.isValid(position[i].getLocation()))
                        continue;
                    draw.setTransform(save);
                    double delta = position[i].getShift();
                    double degree = position[i].getLocation() + delta;
                    draw.rotate(-(degree - 30.0) * DEGREE);
                    AffineTransform rot = draw.getTransform();
                    double angle = draw.getAngle();
                    double delta_angle = Math
                            .toRadians(draw.degreeInWidth(pos)) / 2.0;
                    tip.addDataToNextTip(position[i].getLocation(), init_tip);
                    tip.addTip(pos, upper_pos, angle - delta_angle, angle
                            + delta_angle,
                            FIXSTAR_OFFSET + position[i].getIndex(), true);
                    if (!use_bw) {
                        int state = position[i].getState();
                        draw.setColor((state == Calculate.SPEED_NORMAL) ? birth_speed_color[state]
                                : now_state_color[3]);
                    }
                    int gap = draw.drawRotatedStringVert(pos, height + v_gap,
                            position[i].getName());
                    draw.setColor();
                    draw.setTransform(rot);
                    draw.setForeground(now_degree_color);
                    double cos_angle = Math.cos(delta * DEGREE);
                    double sin_angle = Math.sin(delta * DEGREE);
                    if (line_dir >= 0) { // upper line
                        int rot_x = (int) (upper_pos * cos_angle + 0.5);
                        int rot_y = (int) (upper_pos * sin_angle + 0.5);
                        draw.drawLine(rot_x, rot_y, upper_pos - gap, 0);
                    }
                    if (line_dir <= 0) { // lower line
                        int rot_x = (int) (pos * cos_angle + 0.5);
                        int rot_y = (int) (pos * sin_angle + 0.5);
                        draw.drawLine(rot_x, rot_y, pos + gap, 0);
                    }
                    draw.setForeground();
                }
            }
            // draw aspects
            if (show_aspects && Resource.hasKey(prefix + "aspects_ring")) {
                int mark = Resource.getInt(prefix + "aspects_ring") - 1;
                int pos = (int) (ring_pos[mark] * radius + 0.5);
                double[] b_sign_pos = show_gauquelin ? gauquelin_sign_pos
                        : (single_wheel_mode ? now_sign_pos : birth_sign_pos);
                double[] sign_pos = show_gauquelin ? gauquelin_sign_pos
                        : ((dual_ring || single_wheel_mode) ? now_sign_pos
                                : birth_sign_pos);
                draw.setTransform(save);
                for (int i = 0; i < sign_pos.length; i++) {
                    double f_degree = b_sign_pos[i];
                    if (!Calculate.isValid(f_degree))
                        continue;
                    for (int j = (dual_ring ? sign_pos.length : i) - 1; j >= 0; j--) {
                        double t_degree = sign_pos[j];
                        if (!Calculate.isValid(t_degree))
                            continue;
                        int index = aspects_index[i][j] - 1;
                        if (index < 0 || aspects_display[index] == 0)
                            continue;
                        double rad = -(f_degree - 30.0) * DEGREE;
                        double rad2 = -(t_degree - 30.0) * DEGREE;
                        if (use_bw)
                            draw.setForeground();
                        else
                            draw.setForeground(aspects_color[index]);
                        draw.drawDashLine(aspects_style[index], (int) (pos
                                * Math.cos(rad) + 0.5),
                                (int) (pos * Math.sin(rad) + 0.5), (int) (pos
                                        * Math.cos(rad2) + 0.5), (int) (pos
                                        * Math.sin(rad2) + 0.5));
                        draw.setForeground();
                    }
                }
            }
            // draw the stars
            for (int k = 0; k < 2; k++) {
                String ring_name;
                Hashtable table;
                if (k == 0) {
                    ring_name = "birth_";
                    table = birth_table;
                } else {
                    ring_name = "now_";
                    table = now_table;
                }
                if (Resource.hasKey(prefix + ring_name + "star_sign_ring")) {
                    boolean use_lower = Resource.hasKey(prefix + ring_name
                            + "star_sign_dual_ring")
                            && Resource.getInt(prefix + ring_name
                                    + "star_sign_dual_ring") != 0;
                    int mark = Resource.getInt(prefix + ring_name
                            + "star_sign_ring") - 1;
                    draw.setRingColor(mark + 1, ring_type, colors);
                    int pos = (int) (ring_pos[use_lower ? (mark - 1) : mark]
                            * radius + 0.5);
                    int upper_pos = (int) (ring_pos[mark + 1] * radius + 0.5);
                    int mid_pos = use_lower ? ((pos + upper_pos) / 2) : pos;
                    int height = upper_pos - pos;
                    if (use_lower)
                        height /= 2;
                    if (table != null) {
                        double theta = -2 * Math.PI / 12;
                        draw.setTransform(save);
                        draw.rotate(-theta);
                        for (int j = 0; j < 12; j++) {
                            draw.rotate(theta);
                            String[] stars = (String[]) table
                                    .get(twelve_signs[j]);
                            if (stars != null) {
                                stars = (String[]) stars.clone();
                                int len = stars.length / 2;
                                String str = getWeakHouse(twelve_signs[j])
                                        + getSolidHouse(twelve_signs[j]);
                                String fill = str.replaceAll(".", "  ");
                                if (!fill.equals("")) {
                                    str = "(" + str + ")";
                                    fill += "  ";
                                }
                                fill += "  ";
                                String sign_tip = twelve_signs[j] + str;
                                sign_tip += ": ";
                                int cnt = 0;
                                for (int i = 0; i < len; i++) {
                                    int d_cnt = stars[i].length();
                                    String stars_explain = explain_star ? stars[len
                                            + i]
                                            : null;
                                    if (stars_explain != null)
                                        d_cnt += stars_explain.length()
                                                + explain_length;
                                    if (cnt > 0
                                            && cnt + d_cnt + 2 > MAX_TIP_COUNT) {
                                        sign_tip += "\n  " + fill;
                                        cnt = d_cnt;
                                    } else {
                                        if (i != 0) {
                                            sign_tip += ", ";
                                            cnt += 2;
                                        }
                                        cnt += d_cnt;
                                    }
                                    sign_tip += stars[i];
                                    if (stars_explain != null)
                                        sign_tip += explain_prefix
                                                + stars[len + i]
                                                + explain_suffix;
                                }
                                double angle = draw.getAngle();
                                double delta = 2 * Math.PI / 12;
                                tip.addTip(pos, upper_pos, angle,
                                        angle + delta, false, 0, sign_tip);
                                double[] fit = draw.setFittedFont(
                                        (height - 3) / 2, mid_pos, len,
                                        use_lower, print && !use_lower);
                                int upper_max_entry = (int) fit[1];
                                double[] lower_fit = null;
                                int lower_max_entry = 0;
                                if (use_lower) {
                                    lower_fit = draw.setFittedFont(
                                            (height - 3) / 2, pos, len, true,
                                            false);
                                    lower_max_entry = (int) lower_fit[1];
                                }
                                int max_entry = lower_max_entry
                                        + upper_max_entry;
                                if (use_lower) {
                                    boolean high_res = false;
                                    if (print && max_entry < len) {
                                        high_res = true;
                                        fit = draw.setFittedFont(
                                                (height - 3) / 2, mid_pos, len,
                                                use_lower, true);
                                        upper_max_entry = (int) fit[1];
                                        lower_fit = draw.setFittedFont(
                                                (height - 3) / 2, pos, len,
                                                true, true);
                                        lower_max_entry = (int) lower_fit[1];
                                        max_entry = lower_max_entry
                                                + upper_max_entry;
                                    }
                                    if (max_entry > len) {
                                        lower_max_entry = (len - 1) / 2;
                                        upper_max_entry = len - lower_max_entry;
                                        if (upper_max_entry < 4) {
                                            upper_max_entry = Math.min(4, len);
                                            lower_max_entry = len
                                                    - upper_max_entry;
                                        }
                                        max_entry = lower_max_entry
                                                + upper_max_entry;
                                        fit = draw
                                                .setFittedFont(
                                                        (height - 3) / 2,
                                                        mid_pos,
                                                        upper_max_entry, true,
                                                        high_res);
                                        lower_fit = draw
                                                .setFittedFont(
                                                        (height - 3) / 2, pos,
                                                        lower_max_entry, true,
                                                        high_res);
                                    }
                                }
                                double min_degree = fit[0];
                                boolean dual_row = !use_lower
                                        && ((int) fit[2]) != 0;
                                if (dual_row) {
                                    if (len > 2 * max_entry - 1) {
                                        len = 2 * max_entry - 1;
                                        stars[len - 1] = Resource
                                                .getString("more_seq");
                                    }
                                } else {
                                    if (len > max_entry) {
                                        len = max_entry;
                                        stars[len - 1] = Resource
                                                .getString("more_seq");
                                    }
                                }
                                int offset = 0;
                                int t_pos = mid_pos;
                                AffineTransform rot = draw.getTransform();
                                for (int i = 0; i < len; i++) {
                                    if (dual_row) {
                                        double degree = ((i % max_entry) + 0.5)
                                                * min_degree;
                                        int s_pos = t_pos;
                                        if (i < max_entry) {
                                            s_pos += height / 2;
                                        } else {
                                            degree += 0.5 * min_degree;
                                        }
                                        draw.rotate(-degree * DEGREE);
                                        draw.drawRotatedStringVert(s_pos,
                                                height / 2, stars[i]);
                                    } else {
                                        if (use_lower && i == upper_max_entry) {
                                            offset = upper_max_entry;
                                            t_pos = pos;
                                            min_degree = lower_fit[0];
                                        }
                                        double degree = (i - offset + 0.5)
                                                * min_degree;
                                        draw.rotate(-degree * DEGREE);
                                        draw.drawRotatedStringVert(t_pos,
                                                height, stars[i]);
                                    }
                                    draw.setTransform(rot);
                                }
                            }
                        }
                    }
                    draw.setColor();
                }
            }
            if (show_horiz) {
                tip.addTip(-horiz_dist, -horiz_dist / 2 + horiz_y_offset,
                        2 * horiz_dist, horiz_dist, false, birth_sign_tip);
            } else if (center_tip != null && !center_tip.equals("")) {
                String merge_tip = center_tip;
                if (!cur_year_limit.equals(""))
                    merge_tip += "\n" + Resource.getString("limit") + ": "
                            + cur_year_limit.replaceAll("  ", " ");
                int l_pos = (int) (ring_pos[0] * radius + 0.5);
                tip.addTip(0, l_pos, false, merge_tip);
            }
        }
        if (set_tip)
            tip.setNeedUpdate(false);
        draw.reset();
    }

    private int getYearNameIndex(int age)
    {
        int index = solar_date[0] + age - 1;
        if (index < 0)
            index += 60;
        if (last_year_birth)
            index++;
        return index;
    }

    private String getLimitTip(int cur_age, int cur_year, int child_age_limit)
    {
        cur_age--; // convert to real age
        int index = getYearNameIndex(cur_age);
        int year = cur_year + cur_age;
        boolean bc = year <= 0;
        if (bc)
            year = -year + 1;
        String str = Integer.toString(cur_age + 1) + Resource.getString("age")
                + " " + Integer.toString(year)
                + Resource.getString("year_char") + (bc ? " B.C." : "") + " "
                + year_names[index % year_names.length] + "("
                + year_star_seq[index % 10] + ")\n";
        if (cur_age < child_age_limit)
            str += getChildLimit(cur_age, ":") + " ";
        str += getSmallLimit(cur_age + 1, ":");
        String f_str = getFlyLimit(cur_age, ":", child_age_limit);
        if (f_str != null)
            str += " " + f_str;
        return str;
    }

    public String getWeakHouse(String house)
    {
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
            if (house.equals(weak_house[i]))
                return Resource.getString("weak");
        }
        return "";
    }

    public String getSolidHouse(String house)
    {
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
            if (house.equals(solid_house[i]))
                return Resource.getString("solid");
        }
        return "";
    }

    private void showDesc(DrawAWT draw, String prefix, String year_info,
            Point page_size, Point trim_page_size, Point diagram_size,
            boolean use_bw, boolean applet, boolean pic, boolean chart_only,
            boolean footnote)
    {
        draw.setForeground(Resource.getPrefInt("chart_base_ring_fg_color"));
        draw.setColor();
        int mode = ChartMode.getChartMode();
        if (!chart_only) {
            int height = showDescLowerRightPage(draw, mode, trim_page_size,
                    diagram_size);
            int num_row = showDescSign(draw, prefix, mode, trim_page_size,
                    diagram_size, height, applet, use_bw);
            showDescNameDesc(draw, mode, year_info, trim_page_size,
                    diagram_size, show_now && num_row > NUM_ROW_THRESHOLD);
        }
        showDescLowerLeft(draw, mode, trim_page_size, diagram_size, applet);
        showDescLowerRight(draw, mode, trim_page_size, diagram_size, applet,
                chart_only && !show_horiz);
        showDescUpperLeft(draw, mode, trim_page_size, diagram_size, applet,
                chart_only);
        showDescUpperRight(draw, prefix, mode, trim_page_size, diagram_size,
                applet, use_bw);
        if (footnote)
            showDescFootNote(draw, page_size, diagram_size, applet || pic, 0);
        draw.setForeground();
        draw.setColor();
    }

    private void showDescNameDesc(DrawAWT draw, int mode, String year_info,
            Point page_size, Point diagram_size, boolean smaller)
    {
        String str;
        if (mode == ChartMode.ASTRO_MODE) {
            String eight_char = birth_poles[YEAR_POLE];
            String year_sound = getYearSoundName(birth_poles[YEAR_POLE]);
            eight_char += Resource.getString("year_sound_field").replaceFirst(
                    "%", year_sound.substring(2))
                    + " "
                    + birth_poles[MONTH_POLE]
                    + " "
                    + birth_poles[DAY_POLE] + " " + birth_poles[HOUR_POLE];
            String name = getBirthName();
            if (!name.equals("") && isVerticalName(name, true)) {
                str = ">" + name + "< ";
            } else {
                str = "%";
            }
            if (!ChartMode.isMultipleMode(false)) {
                str += eight_char + " "
                        + Resource.getString(getBirthSex() ? "male" : "female");
            }
            str += "||";
        } else {
            str = getYearInfo(birth_poles, show_now ? now_poles : null,
                    year_info, null, true);
        }
        if (str != null)
            draw.drawAlignStringVert(str, page_size.x, 0, smaller, false);
    }

    private int showDescSign(DrawAWT draw, String prefix, int mode,
            Point page_size, Point diagram_size, int y_offset, boolean applet,
            boolean use_bw)
    {
        String str, format;
        Point dim;
        int x = 0, y = 0, num_row = 0;
        switch (mode) {
            case ChartMode.ASTRO_MODE:
            {
                draw.setFontSizeOverride(Resource.getInt(prefix
                        + "print_astro_sign_font_size"));
                // just to get the size, any character is fine
                Point t_dim = draw.drawAlignStringHoriz(year_char, x, y, null,
                        null, true);
                String[] str_array = new String[2];
                String[] format_array = new String[2];
                Point[] dim_array = new Point[2];
                for (int iter = 0; iter < (dual_ring ? 2 : 1); iter++) {
                    double[] sign_pos = (iter == 0) ? (show_horiz ? birth_sign_azimuth
                            : (single_wheel_mode ? now_sign_pos
                                    : birth_sign_pos))
                            : now_sign_pos;
                    int[] sign_state = (iter == 0 && !single_wheel_mode) ? birth_sign_state
                            : now_sign_state;
                    String extra = dual_ring ? "" : " ";
                    String r_format = show_gauquelin ? "fh." : "";
                    str_array[iter] = "";
                    for (int i = 0; i < signs.length; i++) {
                        int order = (iter == 0) ? birth_sign_display_sort_orders[i]
                                : now_sign_display_sort_orders[i];
                        if (!Calculate.isValid(sign_pos[order]))
                            continue;
                        String gauquelin = show_horiz ? ""
                                : getGauquelin(order);
                        String d_str = show_horiz ? (cal.formatDegree(
                                birth_sign_azimuth[order],
                                birth_sign_alt[order], cal.getSpeedStateName(
                                        birth_sign_state[order], " ")) + " ")
                                : (cal.formatDegree(sign_pos[order],
                                        stellar_sign_pos, full_stellar_signs,
                                        cal.getSpeedStateName(
                                                sign_state[order], " "), false,
                                        false) + gauquelin);
                        str_array[iter] += extra + signs[order] + ":" + d_str
                                + "|";
                    }
                    format_array[iter] = show_horiz ? "fffhhhhhhEhhhhhf"
                            : (dual_ring ? "ffhhffhhf"
                                    : ("fffhhffhhE" + r_format));
                    dim_array[iter] = draw.drawAlignStringHoriz(
                            str_array[iter], x, y, format_array[iter], null,
                            true);
                }
                y = 0;
                Point l_dim = draw.drawAlignStringVert(" ", x, y, false, true);
                if (dual_ring) {
                    double[] sign_pos = single_wheel_mode ? now_sign_pos
                            : birth_sign_pos;
                    Object[] aspects = getAspectsTable(sign_pos, 0, "|", false);
                    if (aspects != null) {
                        int font_size = Resource.getInt(prefix
                                + "print_smallest_font_size");
                        int font_size_delta = Resource.getInt(prefix
                                + "print_aspects_font_size_delta");
                        font_size -= font_size_delta;
                        int n = getNumPlanets(sign_pos);
                        if (n >= 19)
                            font_size -= font_size_delta;
                        draw.setFontSizeOverride(font_size);
                        dim = null;
                        int[] colors = new int[2];
                        colors[0] = draw.getColor("chart_birth_ring_fg_color",
                                use_bw);
                        colors[1] = draw.getColor("chart_now_ring_fg_color",
                                use_bw);
                        int c_x = page_size.x
                                - (dim_array[0].x + dim_array[1].x) / 2 - 3
                                * t_dim.x - t_dim.x / 4;
                        for (int i = 0; i < 2; i++) {
                            dim = draw.drawAlignStringHoriz(
                                    (String) aspects[0], x, y,
                                    (String) aspects[1], colors, i == 0);
                            int height = draw.getFontHeight();
                            x = page_size.x - dim.x - height - l_dim.x;
                            int n_x = Math.max(diagram_size.x + t_dim.x, c_x
                                    - dim.x / 2);
                            x = Math.min(x, n_x);
                            y = 2 * height;
                        }
                        int asc_color = draw.getColor("chart_asc_mk_color",
                                use_bw);
                        draw.setForeground(asc_color);
                        draw.frameTable(0, x, y, dim.x, dim.y,
                                (LinkedList) aspects[2],
                                (LinkedList) aspects[3]);
                        y += dim.y + 4 * draw.getFontHeight();
                        draw.setFontSizeOverride(0);
                        draw.setForeground();
                        draw.setColor();
                    }
                }
                draw.setFontSizeOverride(Resource.getInt(prefix
                        + "print_astro_sign_font_size"));
                str = "|||";
                double[] cusp = single_wheel_mode ? now_cusp : birth_cusp;
                for (int i = 0; i < cusps_name.length; i++) {
                    str += cusps_name[i] + ":"
                            + cal.formatDegree(cusp[i + 1], true, false);
                    str += " |";
                }
                str += "||";
                format = getStringFormat(cusps_name[0], false) + "fhhffhhE";
                dim = draw.drawAlignStringHoriz(str, x, y, format, null, true);
                if (dual_ring) {
                    x = page_size.x - dim_array[0].x - dim_array[1].x - 3
                            * t_dim.x;
                    int n_x = x;
                    for (int iter = 0; iter < 2; iter++) {
                        if (ChartMode.isAstroMode(ChartMode.COMPARISON_MODE)) {
                            if (iter == 0)
                                str = limitStringLength(birth.getName(), 14);
                            else
                                str = limitStringLength(now.getName(), 14);
                        } else {
                            if (iter == 0)
                                str = Resource.getString("birth_chart");
                            else
                                str = ChartMode.getModeName(true, false);
                        }
                        draw.drawStringHoriz(str, n_x, y, false, false);
                        n_x += dim_array[iter].x + t_dim.x / 2;
                    }
                    y += t_dim.y;
                    for (int iter = 0; iter < 2; iter++) {
                        if (!use_bw)
                            draw.setSpecialStringColor(
                                    getSpeedColorArray(iter != 0),
                                    cal.getSpeedStateNameArray());
                        draw.drawAlignStringHoriz(str_array[iter], x, y,
                                format_array[iter], null, false);
                        x += dim_array[iter].x + t_dim.x / 2;
                    }
                } else {
                    int offset = 2 * l_dim.x;
                    if (!use_bw) {
                        draw.setSpecialStringColor(getSpeedColorArray(false),
                                cal.getSpeedStateNameArray());
                    }
                    int width = Math.max(dim.x, dim_array[0].x);
                    int mark_x = page_size.x - width - offset;
                    x = mark_x + (width - dim.x) / 2;
                    y = 0;
                    draw.drawAlignStringHoriz(str, x, y, format, null, false);
                    x = mark_x + (width - dim_array[0].x) / 2;
                    y += dim.y;
                    draw.drawAlignStringHoriz(str_array[0], x, y,
                            format_array[0], null, false);
                }
                draw.setSpecialStringColor(null, null);
                draw.setFontSizeOverride(0);
            }
                break;
            case ChartMode.PICK_MODE:
            {
                if (show_horiz) {
                    str = "";
                    format = birth_sign_status_fill.replaceAll("@", "f")
                            + "fffhhhhhhEhhhhhf";
                } else {
                    String life_master = Resource.getString("life_master");
                    String fill = applet ? "" : birth_sign_status_fill
                            .replaceAll("@", " ");
                    str = fill
                            + life_master
                            + ":"
                            + cal.formatDegree(life_sign_pos, null, null, " ",
                                    false, false) + "       ";
                    format = birth_sign_status_fill.replaceAll("@", "f")
                            + "fffhhffhhEhhffhhf";
                    num_row++;
                    if (Calculate.isValid(self_sign_pos)) {
                        str += "|"
                                + fill
                                + Resource.getString("self_master")
                                + ":"
                                + cal.formatDegree(self_sign_pos, null, null,
                                        " ", false, false) + "       ";
                        num_row++;
                    }
                    str += "||" + fill + "     "
                            + Resource.getString("sky_chart") + "     "
                            + Resource.getString("earth_chart") + "   |";
                }
                int degree_mode = ChartMode.getDegreeMode(quick_azimuth);
                for (int i = 0; i < signs.length; i++) {
                    int order = birth_sign_display_sort_orders[i];
                    if (!Calculate.isValid(birth_sign_pos[order]))
                        continue;
                    String d_str = show_horiz ? (cal
                            .formatDegree(birth_sign_azimuth[order],
                                    birth_sign_alt[order], cal
                                            .getSpeedStateName(
                                                    birth_sign_state[order],
                                                    " ")) + " ")
                            : (cal.formatDegree(birth_sign_pos[order], null,
                                    null, cal.getSpeedStateName(
                                            birth_sign_state[order], " "),
                                    false, false)
                                    + cal.formatDegree(
                                            birth_sign_azimuth[order],
                                            null,
                                            null,
                                            "",
                                            ChartMode
                                                    .mountainBased(degree_mode),
                                            false) + (Calculate
                                    .isValid(birth_sign_alt[order]) ? ((birth_sign_alt[order] >= 0) ? "+"
                                    : "-")
                                    : " "));
                    str += birth_sign_status[order].replaceAll("@", " ") + " "
                            + signs[order] + ":" + d_str + "|";
                    num_row++;
                }
                if (!show_horiz && show_fixstar) {
                    for (int i = 0; i < fixstar_signs.length; i++) {
                        if (!Calculate.isValid(fixstar_sign_azimuth[i]))
                            continue;
                        String d_str = cal.formatDegree(fixstar_sign_pos[i],
                                null, null, " ", false, false)
                                + cal.formatDegree(fixstar_sign_azimuth[i],
                                        null, null, "",
                                        ChartMode.mountainBased(degree_mode),
                                        false)
                                + (Calculate.isValid(fixstar_sign_alt[i]) ? ((fixstar_sign_alt[i] >= 0) ? "+"
                                        : "-")
                                        : " ");
                        str += birth_sign_status_fill.replaceAll("@", " ")
                                + fixstar_signs[i] + ":" + d_str + "|";
                        num_row++;
                    }
                }
                if (!use_bw) {
                    draw.setSpecialStringColor(getSpeedColorArray(false),
                            cal.getSpeedStateNameArray());
                }
                for (int i = 0; i < 2; i++) {
                    dim = draw.drawAlignStringHoriz(str, x, y, format, null,
                            i == 0);
                    x = page_size.x - dim.x;
                    y = page_size.y - dim.y - y_offset;
                }
                draw.setSpecialStringColor(null, null);
                if (show_horiz) {
                    str = birth_sign_status_fill.replaceAll("@", " ") + "   "
                            + Resource.getString("horiz_mode");
                    format = "f.";
                    for (int i = 0; i < 2; i++) {
                        dim = draw.drawAlignStringHoriz(str, x, y, format,
                                null, i == 0);
                        y -= dim.y;
                    }
                }
            }
                break;
            default:
            {
                if (show_horiz) {
                    str = "";
                    format = birth_sign_status_fill.replaceAll("@", "f")
                            + "fffhhhhhhEhhhhhf";
                } else {
                    String life_master = Resource.getString("life_master");
                    String fill = applet ? "" : birth_sign_status_fill
                            .replaceAll("@", " ");
                    str = fill
                            + life_master
                            + ":"
                            + cal.formatDegree(life_sign_pos, stellar_sign_pos,
                                    full_stellar_signs, " ", false, false)
                            + " ";
                    format = (applet ? "" : birth_sign_status_fill.replaceAll(
                            "@", "f")) + "fffhhffhhEhhffhhf";
                    num_row++;
                    if (Calculate.isValid(self_sign_pos)) {
                        str += "|"
                                + fill
                                + Resource.getString("self_master")
                                + ":"
                                + cal.formatDegree(self_sign_pos,
                                        stellar_sign_pos, full_stellar_signs,
                                        " ", false, false) + " ";
                        num_row++;
                    }
                }
                if (!applet) {
                    str += "|$";
                    for (int i = 0; i < signs.length; i++) {
                        int order = birth_sign_display_sort_orders[i];
                        if (!Calculate.isValid(birth_sign_pos[order]))
                            continue;
                        String d_str = show_horiz ? (cal.formatDegree(
                                birth_sign_azimuth[order],
                                birth_sign_alt[order], cal.getSpeedStateName(
                                        birth_sign_state[order], " ")) + " ")
                                : (cal.formatDegree(birth_sign_pos[order],
                                        stellar_sign_pos, full_stellar_signs,
                                        cal.getSpeedStateName(
                                                birth_sign_state[order], " "),
                                        false, false) + (Calculate
                                        .isValid(birth_sign_alt[order]) ? ((birth_sign_alt[order] >= 0) ? "+"
                                        : "-")
                                        : " "));
                        str += birth_sign_status[order].replaceAll("@", " ")
                                + " " + signs[order] + ":" + d_str + "|";
                        num_row++;
                    }
                }
                if (!use_bw) {
                    draw.setSpecialStringColor(getSpeedColorArray(false),
                            cal.getSpeedStateNameArray());
                }
                for (int i = 0; i < 2; i++) {
                    dim = draw.drawAlignStringHoriz(str, x, y, format, null,
                            i == 0);
                    x = page_size.x - dim.x;
                    y = page_size.y - dim.y - y_offset;
                }
                draw.setSpecialStringColor(null, null);
                if (show_horiz) {
                    str = birth_sign_status_fill.replaceAll("@", " ") + "   "
                            + Resource.getString("horiz_mode");
                    format = "f.";
                    draw.drawAlignStringHoriz(str, x, y, format, null, false);
                }
            }
                break;
        }
        return num_row;
    }

    private int showDescLowerRightPage(DrawAWT draw, int mode, Point page_size,
            Point diagram_size)
    {
        Point dim = null;
        int x = 0, y = 0;
        String str;
        String format = null;
        switch (mode) {
            case ChartMode.ASTRO_MODE:
            {
                str = ChartMode.getModeName(true, false);
                if (str != null) {
                    str += "|";
                    format = "f.";
                } else {
                    format = str = "";
                }
                String system = ChartMode.getSystemName("");
                str += system + "|";
                format += "ffff";
                for (int i = 4; i < system.length(); i++)
                    format += "h";
                String s_str = ChartMode.getSidrealSystem();
                if (s_str != null) {
                    str += s_str + "|";
                    format += "ffffh.";
                }
                str += Resource.spacePostFilled(
                        ChartMode.getComputationMethod(), 14)
                        + "|";
                format += "f.";
                if (dual_ring) {
                    str += "                    |";
                    format += "fhhhhfhhhhfhhhhfhhhd";
                    switch (ChartMode.getAstroMode()) {
                        case ChartMode.PRIMARY_DIRECTION_MODE:
                        case ChartMode.SECONDARY_PROGRESSION_MODE:
                        case ChartMode.SOLAR_ARC_MODE:
                        {
                            String advance = FileIO
                                    .formatDouble(
                                            Resource.getPrefDouble(ChartMode
                                                    .isAstroMode(ChartMode.PRIMARY_DIRECTION_MODE) ? "primary_advance"
                                                    : "secondary_advance"), 2,
                                            4, false, false);
                            str += Resource.getString("transit_compression")
                                    + " "
                                    + advance
                                    + " "
                                    + (ChartMode
                                            .isAstroMode(ChartMode.PRIMARY_DIRECTION_MODE) ? Resource
                                            .getString("degree") : day_char);
                            format += "fffffh";
                            for (int i = 0; i < advance.length(); i++)
                                format += "h";
                            format += "hf";
                        }
                            break;
                        default:
                            break;
                    }
                } else {
                    str += "|";
                    String[] elementals = Resource
                            .getStringArray("elemental_names");
                    for (int i = 0; i < 4; i++) {
                        str += elementals[i] + ":"
                                + FileIO.formatInt(astro_elemental[i], 2);
                        str += " ";
                    }
                    str += "|";
                    format += "fhhhhfhhhhfhhhhfhhhd";
                    String[] states = Resource
                            .getStringArray("elemental_states");
                    for (int i = 0; i < 3; i++) {
                        str += states[i] + ":"
                                + FileIO.formatInt(astro_elemental_state[i], 2);
                        str += " ";
                    }
                    format += "fhhhhfhhhhfhhhh";
                }
                str += "|";
            }
                break;
            case ChartMode.PICK_MODE:
            {
                str = Resource.getString("inner_chart")
                        + ":"
                        + Resource.getString("tropical_method")
                        + " "
                        + Resource.getString(day_set ? "pick_day_choice"
                                : "pick_night_choice")
                        + " |"
                        + Resource.getString("outer_chart")
                        + ":"
                        + Resource
                                .getString(quick_azimuth ? "quick_azimuth_method"
                                        : "local_horizon_method") + " |   "
                        + azimuth_speed + " |";
                format = "f.f.fff" + getStringFormat(azimuth_speed, false)
                        + "f";
                if (quick_azimuth) {
                    String sys_name = ChartMode.getSystemName("pick_");
                    str += " " + sys_name + "|";
                    format += "fffff"
                            + getStringFormat(sys_name.substring(4), false);
                }
                str += Resource.getString("mountain_degree")
                        + ":"
                        + getMountainPos(true)
                        + " "
                        + cal.formatDegree(mountain_pos
                                - true_north_magnetic_shift, null, null, "",
                                true, false) + " ";
                format += "fffffhhhhhhfhhffhhf";
            }
                break;
            default:
            {
                str = now_year + ":";
                format = "fff";
                if (show_now) {
                    String str1 = getShortDate(now_date, false);
                    String str2 = (now_lunar_leap_month ? Resource
                            .getString("leap") : "")
                            + BaseCalendar.chineseNumber(now_lunar_date[1],
                                    !now_lunar_leap_month, false)
                            + month_char
                            + BaseCalendar.chineseNumber(now_lunar_date[2],
                                    false, true) + day_char;
                    str += now_poles[YEAR_POLE] + " "
                            + getShortDate(now_date, true) + "  " + age_label
                            + "|" + Resource.getString("inner_chart") + ":"
                            + str1 + " " + str2 + " ";
                    format += "ffh.fff" + getStringFormat(str1, false) + "f"
                            + getStringFormat(str2, false) + "f";
                } else {
                    str += getShortDate(now_date, true) + "  " + age_label;
                    format += "h.";
                }
                if (now_start_degree >= 0.0 && now_end_degree >= 0.0) {
                    str += "|"
                            + Resource.getString("limit")
                            + ":"
                            + cal.formatDegree(now_start_degree,
                                    stellar_sign_pos, full_stellar_signs, " ",
                                    false, false)
                            + " | "
                            + Resource.getString("to")
                            + ":"
                            + cal.formatDegree(now_end_degree,
                                    stellar_sign_pos, full_stellar_signs, " ",
                                    false, false);
                    format += "fffhhffhhEhhffhhffffhhffhhEhhffhhf";
                } else {
                    str += "|                 ";
                    format += "fffhhffhhEhhffhhf";
                }
            }
                break;
        }
        str += "|";
        for (int i = 0; i < (show_horiz ? 1 : 2); i++) {
            dim = draw.drawAlignStringHoriz(str, x, y, format, null, i == 0);
            x = page_size.x - dim.x;
            y = page_size.y - dim.y;
        }
        return dim.y;
    }

    private void showDescLowerLeft(DrawAWT draw, int mode, Point page_size,
            Point diagram_size, boolean applet)
    {
        Point dim = null;
        int x = 0, y = 0;
        String str = "";
        if (ChartMode.isAstroMode(ChartMode.COMPARISON_MODE)
                || ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE))
            str += birth.getName() + "|";
        else if (single_wheel_mode)
            str += ChartMode.getModeName(true, false) + "|";
        else if (dual_ring)
            str += Resource.getString("birth_chart") + "|";
        str += (single_wheel_mode ? getNowDesc() : getBirthDesc()) + "|";
        for (int i = 0; i < 2; i++) {
            dim = draw.drawStringHoriz(str, x, y, false, i == 0);
            x = 0;
            y = page_size.y - dim.y;
        }
        if (applet || show_horiz || bad_styles == null)
            return;
        int n = bad_styles.size();
        if (n == 0)
            return;
        n = Math.min(n, Resource.getInt("print_max_styles"));
        str = Resource.getString("bad_styles");
        draw.drawStringHoriz(str, 0, 0, false, true);
        int height = draw.getFontHeight();
        y = 3 * diagram_size.y / 4 - height;
        y -= (height * n) / 2;
        for (ListIterator iter = bad_styles.listIterator(); iter.hasNext()
                && n > 0; n--) {
            str += "|" + (String) iter.next();
        }
        draw.drawStringHoriz(str, 0, y, false, false);
    }

    private void showDescLowerRight(DrawAWT draw, int mode, Point page_size,
            Point diagram_size, boolean applet, boolean chart_only)
    {
        String format = null;
        String str = "";
        if (chart_only) {
            switch (mode) {
                case ChartMode.ASTRO_MODE:
                    if (!dual_ring
                            && !ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE)) {
                        str += ChartMode.getModeName(true, false) + "|";
                        str += ChartMode.getSystemName("") + "|";
                        String s_str = ChartMode.getSidrealSystem();
                        if (s_str != null)
                            str += s_str + "|";
                        str += ChartMode.getComputationMethod() + "|";
                    }
                    break;
                case ChartMode.PICK_MODE:
                {
                    str = Resource.getString("inner_chart")
                            + ":"
                            + Resource.getString("tropical_method")
                            + " "
                            + Resource.getString(day_set ? "pick_day_choice"
                                    : "pick_night_choice")
                            + "|"
                            + Resource.getString("outer_chart")
                            + ":"
                            + Resource
                                    .getString(quick_azimuth ? "quick_azimuth_method"
                                            : "local_horizon_method") + " |   "
                            + azimuth_speed + " |";
                    format = "f.f.fff" + getStringFormat(azimuth_speed, false)
                            + "f";
                    if (quick_azimuth) {
                        String sys_name = ChartMode.getSystemName("pick_");
                        str += " " + sys_name + "|";
                        format += "fffff"
                                + getStringFormat(sys_name.substring(4), false);
                    }
                    str += Resource.getString("mountain_degree") + ":"
                            + getMountainPos(true) + "|";
                    format += "fffffh.";
                }
                    break;
                default:
                    if (show_now) {
                        str = now_year + ":";
                        format = "fff";
                        String str1 = getShortDate(now_date, false);
                        String str2 = (now_lunar_leap_month ? Resource
                                .getString("leap") : "")
                                + BaseCalendar.chineseNumber(now_lunar_date[1],
                                        !now_lunar_leap_month, false)
                                + month_char
                                + BaseCalendar.chineseNumber(now_lunar_date[2],
                                        false, true) + day_char;
                        str += now_poles[YEAR_POLE] + " "
                                + getShortDate(now_date, true) + "  "
                                + age_label + "|"
                                + Resource.getString("inner_chart") + ":"
                                + str1 + " " + str2 + " ";
                        format += "ffh.fff" + getStringFormat(str1, false)
                                + "f" + getStringFormat(str2, false) + "f";
                        if (now_start_degree >= 0.0 && now_end_degree >= 0.0) {
                            str += "|"
                                    + Resource.getString("limit")
                                    + ":"
                                    + cal.formatDegree(now_start_degree,
                                            stellar_sign_pos,
                                            full_stellar_signs, " ", false,
                                            false)
                                    + " | "
                                    + Resource.getString("to")
                                    + ":"
                                    + cal.formatDegree(now_end_degree,
                                            stellar_sign_pos,
                                            full_stellar_signs, " ", false,
                                            false) + "|";
                            format += "fffhhffhhEhhffhhffffhhffhhEhhffhhf";
                        } else {
                            str += "|                 ";
                            format += "fffhhffhhEhhffhhf";
                        }
                    }
                    break;
            }
        }
        if (format == null) {
            if (dual_ring || ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE)) {
                if (chart_only) {
                    str += ChartMode.getSystemName("") + "|";
                    String s_str = ChartMode.getSidrealSystem();
                    if (s_str != null)
                        str += s_str + "|";
                    str += ChartMode.getComputationMethod() + "||";
                }
                if (chart_only
                        || !ChartMode.isAstroMode(ChartMode.COMPARISON_MODE)
                        && !ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE))
                    str += ChartMode.getModeName(true, false) + "|";
                if (ChartMode.isAstroMode(ChartMode.COMPARISON_MODE)
                        || ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE))
                    str += now.getName() + "|";
                str += getNowDesc();
            } else if (!single_wheel_mode) {
                str += getBirthRiseSetDesc();
            } else {
                str += "||";
            }
        }
        boolean align_right = (dual_ring || chart_only || ChartMode
                .isAstroMode(ChartMode.COMPOSITE_MODE)) && format == null;
        Point dim = null;
        int x = 0, y = 0;
        for (int i = 0; i < 2; i++) {
            if (format == null) {
                dim = draw.drawStringHoriz(str, x, y, align_right, i == 0);
            } else {
                dim = draw
                        .drawAlignStringHoriz(str, x, y, format, null, i == 0);
            }
            x = diagram_size.x;
            if (!align_right)
                x -= dim.x;
            y = page_size.y - dim.y;
        }
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            int n_y = y;
            str = getOverridenStatus();
            if (!str.equals("")) {
                str += Resource.getString("mod_label") + "  |";
                for (int i = 0; i < 2; i++) {
                    dim = draw.drawStringHoriz(str, x, y, true, i == 0);
                    x = diagram_size.x;
                    y = n_y - dim.y;
                }
            }
            n_y = y;
            str = ChartMode.getComputationMethod() + "  ||";
            for (int i = 0; i < 2; i++) {
                dim = draw.drawStringHoriz(str, x, y, true, i == 0);
                x = diagram_size.x;
                y = n_y - dim.y;
            }
            if (Resource.getPrefInt("use_equator") != 0) {
                n_y = y;
                str = Resource.getString("equator_sunrise") + "  |";
                for (int i = 0; i < 2; i++) {
                    dim = draw.drawStringHoriz(str, x, y, true, i == 0);
                    x = diagram_size.x;
                    y = n_y - dim.y;
                }
            }
        }
    }

    private void showDescUpperLeft(DrawAWT draw, int mode, Point page_size,
            Point diagram_size, boolean applet, boolean chart_only)
    {
        String str = "";
        String name = getBirthName();
        if (!name.equals("") && (chart_only || !isVerticalName(name, true))) {
            draw.drawLargeBoldStringHoriz(name, 0, 0,
                    !FileIO.isAsciiString(name, true));
            if (Resource.hasCustomData())
                str = "||" + Resource.getModName();
        } else if (Resource.hasCustomData()) {
            str = Resource.getModName();
        }
        if (!str.equals(""))
            draw.drawLargeStringHoriz(str, 0, 0, false, false);
        if (applet || show_horiz || good_styles == null)
            return;
        int n = good_styles.size();
        if (n == 0)
            return;
        n = Math.min(n, Resource.getInt("print_max_styles"));
        str = Resource.getString("good_styles");
        draw.drawStringHoriz(str, 0, 0, false, true);
        int height = draw.getFontHeight();
        int y = diagram_size.y / 4 - height;
        y -= (height * n) / 2;
        for (ListIterator iter = good_styles.listIterator(); iter.hasNext()
                && n > 0; n--) {
            str += "|" + (String) iter.next();
        }
        draw.drawStringHoriz(str, 0, y, false, false);
    }

    private void showDescUpperRight(DrawAWT draw, String prefix, int mode,
            Point page_size, Point diagram_size, boolean applet, boolean use_bw)
    {
        if (show_horiz)
            return;
        switch (mode) {
            case ChartMode.ASTRO_MODE:
            {
                if (!dual_ring) {
                    double[] sign_pos = single_wheel_mode ? now_sign_pos
                            : birth_sign_pos;
                    Object[] aspects = getAspectsTable(sign_pos, 1, "|", false);
                    if (aspects != null) {
                        int font_size = Resource.getInt(prefix
                                + "print_smallest_font_size");
                        int font_size_delta = Resource.getInt(prefix
                                + "print_aspects_font_size_delta");
                        font_size -= font_size_delta;
                        int n = getNumPlanets(sign_pos);
                        if (n >= 20)
                            font_size -= font_size_delta;
                        Point dim = null;
                        draw.setFontSizeOverride(font_size);
                        int x = 0, y = 0;
                        for (int i = 0; i < 2; i++) {
                            dim = draw.drawAlignStringHoriz(
                                    (String) aspects[0], x, y,
                                    (String) aspects[1], null, i == 0);
                            int height = draw.getFontHeight();
                            x = diagram_size.x - height - dim.x;
                            y = height;
                        }
                        int asc_color = draw.getColor("chart_asc_mk_color",
                                use_bw);
                        draw.setForeground(asc_color);
                        draw.frameTable(1, x, y, dim.x, dim.y,
                                (LinkedList) aspects[2],
                                (LinkedList) aspects[3]);
                        draw.setFontSizeOverride(0);
                        draw.setForeground();
                        draw.setColor();
                    }
                }
            }
                break;
            default:
            {
                Point dim = null;
                int x = 0, y = 0;
                String str = null;
                if (!applet) {
                    String key = Resource.getString("life_helper_key");
                    str = " | |  ";
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        str += Resource.getString("alt_life_helper_key");
                    } else {
                        str += key;
                    }
                    str += "|";
                    for (int iter = 0; iter < (ChartMode
                            .isChartMode(ChartMode.PICK_MODE) ? 3 : 2); iter++) {
                        String sign;
                        switch (iter) {
                            case 0:
                                sign = cal.getStarSign(life_sign_pos,
                                        stellar_sign_pos, full_stellar_signs);
                                str += Resource.getString("degree");
                                break;
                            case 1:
                                sign = cal.getZodiac(life_sign_pos, true);
                                str += Resource.getString("zodiac_house");
                                break;
                            default:
                                sign = findMountainSign();
                                str += Resource.getString("mountain");
                                break;
                        }
                        str += ":";
                        String[] helper = Resource.getStringArray(key
                                + sign.substring(1));
                        for (int j = 0; j < 2; j++) {
                            for (int i = 0; i < helper.length; i++) {
                                if (helper[i].length() > j)
                                    str += helper[i].substring(j, j + 1);
                                else
                                    str += " ";
                            }
                            str += "|";
                            if (j == 0)
                                str += "  ";
                        }
                    }
                    for (int i = 0; i < 2; i++) {
                        dim = draw.drawAlignStringHoriz(str, x, y, null, null,
                                i == 0);
                        x = diagram_size.x - dim.x;
                    }
                    y += dim.y;
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        String[] helper_season = Resource
                                .getStringArray("season_helper_season");
                        int helper_index = getSeasonIndex(birth_adj_date);
                        str = helper_season[helper_index];
                        str = str.substring(0, 2) + str.substring(4) + "|";
                        for (int i = 0; i < 2; i++) {
                            dim = draw.drawAlignStringHoriz(str, x, y, null,
                                    null, i == 0);
                            x = diagram_size.x - dim.x;
                        }
                        y += dim.y;
                    }
                    str = "  " + year_char + month_char + day_char + hour_char
                            + "|" + Resource.getString("weak") + ":"
                            + weak_house[YEAR_POLE] + weak_house[MONTH_POLE]
                            + weak_house[DAY_POLE] + weak_house[HOUR_POLE]
                            + "|" + Resource.getString("solid") + ":"
                            + solid_house[YEAR_POLE] + solid_house[MONTH_POLE]
                            + solid_house[DAY_POLE] + solid_house[HOUR_POLE]
                            + "|";
                    for (int i = 0; i < 2; i++) {
                        dim = draw.drawAlignStringHoriz(str, x, y, null, null,
                                i == 0);
                        x = diagram_size.x - dim.x;
                    }
                    y += dim.y;
                    if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        int child_age_limit = getChildLimit(true);
                        str = getFlyLimit(current_age - 1, ":", child_age_limit);
                        if (str != null) {
                            for (int i = 0; i < 2; i++) {
                                dim = draw.drawAlignStringHoriz(str, x, y,
                                        null, null, i == 0);
                                x = diagram_size.x - dim.x;
                            }
                            y += dim.y;
                        }
                        if (current_age >= 1
                                && current_age - 1 < child_age_limit) {
                            str = getChildLimit(current_age - 1, ":");
                            for (int i = 0; i < 2; i++) {
                                dim = draw.drawAlignStringHoriz(str, x, y,
                                        null, null, i == 0);
                                x = diagram_size.x - dim.x;
                            }
                            y += dim.y;
                        }
                        str = getSmallLimit(current_age, ":");
                        for (int i = 0; i < 2; i++) {
                            dim = draw.drawAlignStringHoriz(str, x, y, null,
                                    null, i == 0);
                            x = diagram_size.x - dim.x;
                        }
                        y += dim.y;
                        if (show_now) {
                            str = getMonthLimit(current_age, ":");
                            for (int i = 0; i < 2; i++) {
                                dim = draw.drawAlignStringHoriz(str, x, y,
                                        null, null, i == 0);
                                x = diagram_size.x - dim.x;
                            }
                            y += dim.y;
                        }
                    }
                }
                str = ChartMode.getModeName(true, false);
                if (str != null) {
                    for (int i = 0; i < 2; i++) {
                        dim = draw.drawStringHoriz(str, x, y, false, i == 0);
                        x = diagram_size.x - dim.x;
                        y += dim.y;
                        if (i == 0)
                            y += dim.y;
                    }
                }
            }
                break;
        }
    }

    static public void showDescFootNote(DrawAWT draw, Point page_size,
            Point diagram_size, boolean regular, int page_no)
    {
        Point dim = null;
        int x = 0, y = 0;
        String name = Resource.getFootnote();
        for (int i = 0; i < 2; i++) {
            dim = draw.drawStyledStringHoriz(name, x, y, regular ? Font.PLAIN
                    : Font.ITALIC, i == 0);
            x = (page_size.x - dim.x) / 2;
            y = page_size.y - ((int) (1.15 * dim.y));
        }
        if (diagram_size != null && Resource.hasCustomFootnote()) {
            for (int i = 0; i < 2; i++) {
                dim = draw.drawStyledStringHoriz(Resource.NAME + " "
                        + Resource.NUMBER, x, y, regular ? Font.PLAIN
                        : Font.ITALIC, i == 0);
                x = 3 * diagram_size.x / 4 - dim.x;
                y = page_size.y - ((int) (3.75 * dim.y));
            }
        }
        if (page_no <= 0)
            return;
        for (int i = 0; i < 2; i++) {
            dim = draw.drawStyledStringHoriz(Integer.toString(page_no), x, y,
                    Font.PLAIN, i == 0);
            x = page_size.x - dim.x;
            y = page_size.y - ((int) (1.15 * dim.y));
        }
    }

    private String getBirthRiseSetDesc()
    {
        String str = "";
        String rise = Resource.getString("rise_time");
        String fall = Resource.getString("fall_time");
        if (time_adjust > 0)
            str += timeAdjustPrefix() + "|";
        for (int i = SUN; i <= MOON; i++) {
            str += signs[i] + rise + ": " + birth_rise_time[i] + "|" + signs[i]
                    + fall + ": " + birth_set_time[i];
            str += "|";
        }
        str += "|";
        return str;
    }

    private String getBirthDesc()
    {
        String str = getShortDate(birth_dst_date) + " "
                + BaseCalendar.formatDate(birth_dst_date, true, false);
        if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            str += " " + getBirthSeason(birth_adj_date);
        str += "|" + timeAdjustPrefix() + ": "
                + BaseCalendar.formatDate(birth_adj_date, true, false) + "|";
        str += lunar_calendar
                + ": "
                + birth_poles[CH_YEAR]
                + year_char
                + (lunar_leap_month ? Resource.getString("leap") : "")
                + BaseCalendar.chineseNumber(lunar_date[1], !lunar_leap_month,
                        false) + month_char
                + BaseCalendar.chineseNumber(lunar_date[2], false, true)
                + day_char + birth_poles[HOUR_POLE].substring(1, 2) + hour_char
                + " (" + day_or_night + ")|";
        String eclipse_date = getEclipseInfo(solar_eclipse, birth_zone, true,
                true);
        if (eclipse_date != null)
            str += eclipse_date + "|";
        eclipse_date = getEclipseInfo(lunar_eclipse, birth_zone, false, true);
        if (eclipse_date != null)
            str += eclipse_date + "|";
        str += Resource.getString("longitude") + ": "
                + City.formatLongLatitude(birth_loc[0], true, true, false)
                + "  " + Resource.getString("latitude") + ": "
                + City.formatLongLatitude(birth_loc[1], false, true, false);
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            str += "  "
                    + Resource.getString("magnetic_shift").substring(0, 2)
                    + ": "
                    + City.formatLongLatitude(magnetic_shift, true, true, false)
                    + ((magnetic_shift_message.length() > 1) ? magnetic_shift_message
                            .substring(0, 1) : "");
        }
        str += "|";
        DataEntry entry = (ChartMode.isAstroMode(ChartMode.ALT_NATAL_MODE) || ChartMode
                .isAstroMode(ChartMode.RELATIONSHIP_MODE)) ? now : birth;
        City city = ChartMode.isAstroMode(ChartMode.RELATIONSHIP_MODE) ? null
                : City.matchCity(entry.getCity(), entry.getCountry(), false);
        if (city == null) { // longitude/latitude
            str += entry.getZone();
        } else {
            str += entry.getCity() + ", " + entry.getCountry();
        }
        str += "|";
        return str;
    }

    private String getNowDesc()
    {
        String str = getShortDate(now_dst_date) + " "
                + BaseCalendar.formatDate(now_dst_date, true, false) + "|"
                + timeAdjustPrefix() + ": "
                + BaseCalendar.formatDate(now_adj_date, true, false) + "|";
        str += Resource.getString("longitude") + ": "
                + City.formatLongLatitude(now_loc[0], true, true, false) + "  "
                + Resource.getString("latitude") + ": "
                + City.formatLongLatitude(now_loc[1], false, true, false) + "|";
        City city = City.matchCity(now.getCity(), now.getCountry(), false);
        if (city == null) { // longitude/latitude
            str += now.getZone() + "|";
        } else {
            str += now.getCity() + ", " + now.getCountry() + "|";
        }
        str += "|";
        return str;
    }

    public String getYearInfo(boolean use_birth, Hashtable table)
    {
        return getYearInfo(use_birth ? birth_poles : now_poles, null,
                getYearInfoKey(use_birth), table, false);
    }

    private String getYearInfoKey(boolean use_birth)
    {
        if (use_birth) {
            if (ChartMode.isChartMode(ChartMode.PICK_MODE))
                return ten_god_mode ? alt_pick_year_info : pick_year_info;
            else
                return ten_god_mode ? alt_birth_year_info : birth_year_info;
        } else {
            return ten_god_mode ? alt_current_year_info : current_year_info;
        }
    }

    private String getYearInfo(String[] poles, String[] alt_poles,
            String year_info, Hashtable table, boolean fit_page)
    {
        if (!Calculate.isValid(life_sign_pos) || poles[YEAR_POLE] == null)
            return null;
        // vertical text from right to left
        String filler = "";
        String break_char = Resource.getString("break");
        Resource.getStringArray(poles[YEAR_POLE], year_data);
        String str = year_info;
        int y_index = FileIO.getArrayIndex(poles[YEAR_POLE], year_names);
        int alt_y_index = 0;
        if (alt_poles != null) {
            Resource.getStringArray(alt_poles[YEAR_POLE], alt_year_data);
            alt_y_index = FileIO
                    .getArrayIndex(alt_poles[YEAR_POLE], year_names);
            filler = "  ";
            str = str.replaceAll("@", "   ");
        } else {
            str = str.replaceAll("@", " ");
        }
        DecimalFormat format = new DecimalFormat("00");
        if (alt_pole_data != null) {
            for (int i = 0; i < alt_pole_data.length; i++) {
                String field = format.format(i + alt_pole_data_offset);
                String val = alt_pole_data[i];
                str = str.replaceFirst(field, val);
            }
        }
        for (int i = 0; i < year_data.length; i++) {
            String field = format.format(i + 1);
            String val = year_data[i];
            if (alt_poles != null)
                val += break_char + alt_year_data[i];
            str = str.replaceFirst(field, val);
        }
        if (poles == birth_poles) {
            int wife_index = ((int) life_sign_pos) / 30 - wife_pos;
            if (wife_index < 0)
                wife_index += 12;
            str = str.replaceFirst("00", wife_signs[wife_index] + filler);
            String[] array;
            String[] month_key_seq = Resource.getStringArray("month_key_seq");
            String[] month_index_seq = Resource
                    .getStringArray("month_index_seq");
            for (int k = 0; k < month_key_seq.length; k++) {
                array = Resource.getStringArray(month_key_seq[k]);
                String month_key = poles[MONTH_POLE].substring(1, 2);
                for (int i = 0; i < array.length; i++) {
                    if (month_key.equals(array[i].substring(0, 1))) {
                        str = str.replaceFirst(month_index_seq[k],
                                array[i].substring(2, 3) + filler);
                        break;
                    }
                }
            }
            int index = ((int) (life_sign_pos / 30.0)) + 11;
            if (index >= 12)
                index -= 12;
            String sign = twelve_signs[index];
            String[] data = Resource.getStringArray("year_birth_earth_key");
            array = Resource.getStringArray(data[0]);
            for (int i = 0; i < array.length; i++) {
                if (sign.equals(array[i].substring(0, 1))) {
                    str = str.replaceFirst(data[1], array[i].substring(2, 3)
                            + filler);
                    break;
                }
            }
            String[] sky_key_seq = Resource.getStringArray("sky_key_seq");
            String[] sky_index_seq = Resource.getStringArray("sky_index_seq");
            for (int k = 0; k < sky_key_seq.length; k++) {
                array = Resource.getStringArray(sky_key_seq[k]
                        + poles[YEAR_POLE].substring(0, 1));
                for (int i = 0; i < array.length; i++) {
                    if (sign.equals(array[i].substring(0, 1))) {
                        str = str.replaceFirst(sky_index_seq[k],
                                array[i].substring(2, 3) + filler);
                        break;
                    }
                }
            }
            for (int i = 0; i < power_key.length; i++) {
                array = Resource.getStringArray(power_key[i]);
                String key = poles[YEAR_POLE].substring(0, 1);
                for (int j = 0; j < array.length; j++) {
                    if (key.equals(array[j].substring(0, 1))) {
                        str = str.replaceFirst(power_index[i],
                                array[j].substring(2) + filler);
                        break;
                    }
                }
            }
        } else {
            str = str.replaceFirst("00", " " + filler);
        }
        for (int i = year_star_range[0]; i < year_star_range[1]; i++) {
            String field = format.format(i);
            String val = getYearStar(y_index, i);
            if (alt_poles != null)
                val += break_char + getYearStar(alt_y_index, i);
            str = str.replaceAll(field, val);
        }
        String year_sound_field = Resource.getString("year_sound_field");
        if (table != null) {
            String star_sign_key = Resource.getString("star_sign_key");
            StringTokenizer st = new StringTokenizer(str, "$%| ");
            while (st.hasMoreTokens()) {
                String key = st.nextToken();
                if (key.length() == 5) {
                    String s_key = key.substring(3, 4) + star_sign_key;
                    LinkedList star_list = (LinkedList) table.get(s_key);
                    if (star_list == null)
                        star_list = new LinkedList();
                    String val = key.substring(0, 2);
                    if (star_list.contains(val))
                        continue;
                    if (ten_god_list.contains(val))
                        star_list.addFirst(val);
                    else
                        star_list.addLast(val);
                    table.put(s_key, star_list);
                }
            }
        }
        str = str.replaceAll("%", fit_page ? "" : " ");
        String eight_char = poles[YEAR_POLE];
        if (poles == birth_poles) {
            String year_sound = getYearSoundName(birth_poles[YEAR_POLE]);
            eight_char += year_sound_field.replaceFirst("%",
                    year_sound.substring(2))
                    + " "
                    + poles[MONTH_POLE]
                    + " "
                    + poles[DAY_POLE]
                    + " "
                    + poles[HOUR_POLE];
        } else if (poles == now_poles && current_age > 0 && current_age < 100) {
            eight_char += " "
                    + BaseCalendar.chineseNumber(current_age, false, false)
                    + Resource.getString("age");
        }
        if (!ChartMode.isMultipleMode(false)) {
            str = eight_char + " "
                    + Resource.getString(getBirthSex() ? "male" : "female")
                    + str;
        }
        String name = getBirthName();
        if (!name.equals("") && isVerticalName(name, true)) {
            if (fit_page) {
                str = ">" + name + "< " + str;
            } else {
                str = name + " " + str;
            }
        } else {
            str = "%" + str;
        }
        return str;
    }

    public String[] getHorizData()
    {
        String[] horiz_data = new String[3];
        String name = getBirthName();
        if (!name.equals("")) {
            if (isVerticalName(name, true)) {
                horiz_data[0] = name + "$$"; // vertical
            } else {
                horiz_data[0] = ""; // vertical
                horiz_data[1] = name + "||"; // horziontal
            }
        }
        if (horiz_data[1] == null)
            horiz_data[1] = "|";
        String t_str = ChartMode.getComputationMethod();
        for (int i = t_str.length(); i < 8; i++)
            t_str += "  ";
        horiz_data[1] += t_str;
        horiz_data[2] = horiz_sign_data;
        return horiz_data;
    }

    public String[] getAstroData()
    {
        String[] astro_data = new String[11];
        String str;
        String name = getBirthName();
        if (!name.equals("")) {
            String eight_char;
            if (ChartMode.isMultipleMode(false)) {
                str = "";
                eight_char = "";
            } else {
                str = " "
                        + Resource.getString(getBirthSex() ? "male" : "female");
                eight_char = birth_poles[YEAR_POLE];
                String year_sound = getYearSoundName(birth_poles[YEAR_POLE]);
                eight_char += Resource.getString("year_sound_field")
                        .replaceFirst("%", year_sound.substring(2))
                        + " "
                        + birth_poles[MONTH_POLE]
                        + " "
                        + birth_poles[DAY_POLE]
                        + " " + birth_poles[HOUR_POLE];
            }
            if (isVerticalName(name, true)) {
                astro_data[0] = name + " " + eight_char + str + "$$"; // vertical
            } else {
                astro_data[0] = "  " + eight_char + str + "$$"; // vertical
                astro_data[1] = name + "||"; // horziontal
            }
        }
        if (astro_data[1] == null)
            astro_data[1] = "|";
        String t_str = ChartMode.getSidrealSystem();
        if (t_str != null)
            astro_data[1] += t_str + "|";
        t_str = ChartMode.getComputationMethod();
        for (int i = t_str.length(); i < 8; i++)
            t_str += "  ";
        astro_data[1] += t_str + "  "
                + limitStringLength(ChartMode.getSystemName(""), 18);
        if (dual_ring) {
            String title = ChartMode.isAstroMode(ChartMode.COMPARISON_MODE) ? birth
                    .getName() : Resource.getString("birth_chart");
            title = limitStringLength(title, 16);
            astro_data[2] = title + "|" + astro_sign_data[0];
            title = ChartMode.isAstroMode(ChartMode.COMPARISON_MODE) ? now
                    .getName() : ChartMode.getModeName(true, false);
            title = limitStringLength(title, 16);
            astro_data[3] = title + "|" + astro_sign_data[1];
        } else {
            astro_data[2] = astro_sign_data[single_wheel_mode ? 1 : 0];
            astro_data[3] = astro_cusp_data;
            String[] elementals = Resource.getStringArray("elemental_names");
            for (int i = 0; i < 4; i++) {
                astro_data[4 + i] = elementals[i] + ":"
                        + FileIO.formatInt(astro_elemental[i], 2);
            }
            String[] states = Resource.getStringArray("elemental_states");
            for (int i = 0; i < 3; i++) {
                astro_data[8 + i] = states[i] + ":"
                        + FileIO.formatInt(astro_elemental_state[i], 2);
            }
        }
        return astro_data;
    }

    private String limitStringLength(String str, int len)
    {
        char[] array = str.toCharArray();
        int n = 0;
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            n++;
            if (c > 0xff)
                n++;
            if (n > len) {
                str = str.substring(0, i);
                n = str.lastIndexOf(" ");
                if (n >= 0)
                    return str.substring(0, n).trim();
                n = str.length();
                for (i = 0; i < 3; i++) {
                    c = array[--n];
                    if (c > 0xff)
                        i++;
                }
                return str.substring(0, n) + "...";
            }
        }
        return str;
    }

    private String getStringFormat(String str, boolean full_only)
    {
        String format = "";
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++)
            format += (full_only || array[i] > 0xff) ? "f" : "h";
        return format;
    }

    private boolean isVerticalName(String key, boolean check_length)
    {
        if (check_length) {
            int max_len = Resource
                    .getInt(ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? "astro_vertical_name_length"
                            : "vertical_name_length");
            if (key.length() > max_len)
                return false;
        }
        return !FileIO.isAsciiString(key, false);
    }

    private int getYear(Date date)
    {
        work_cal.setTime(date);
        int year = work_cal.get(Calendar.YEAR);
        if (work_cal.get(Calendar.ERA) == GregorianCalendar.BC)
            year = -year + 1; // 1 B.C. is 0, 2 B.C. is -1, and so on
        return year;
    }

    private Date addYear(Date date, int year)
    {
        work_cal.setTime(date);
        work_cal.add(Calendar.YEAR, year);
        return work_cal.getTime();
    }

    private double getFraction(Date cur_date, Date date, Date next_date)
    {
        work_cal.setTime(date);
        long l = work_cal.getTimeInMillis();
        work_cal.setTime(cur_date);
        long c = work_cal.getTimeInMillis();
        work_cal.setTime(next_date);
        long u = work_cal.getTimeInMillis();
        return ((double) (c - l)) / (u - l);
    }

    private double[] nowYearPosition(int age)
    {
        if (age < 1 || age > 99)
            return null;
        int i = 0;
        double degree_offset = 0.0, s = 0.0, e = 0.0;
        Date date = getDate(birth.getBirthDay());
        work_cal.setTime(date);
        long e_milli = work_cal.getTimeInMillis();
        work_cal.set(Calendar.MONTH, 0);
        work_cal.set(Calendar.DAY_OF_MONTH, 1);
        work_cal.set(Calendar.HOUR_OF_DAY, 0);
        work_cal.set(Calendar.MINUTE, 0);
        long s_milli = work_cal.getTimeInMillis();
        double val = age - 1 - (e_milli - s_milli) / MILLISECOND_PER_YEAR;
        for (int iter = 0; iter < 2; iter++) {
            for (; i < limit_seq.length; i++) {
                double year = (i == 0) ? (getChildLimit(false) / 365.25)
                        : limit_seq[i];
                if (val < year) {
                    double degree = degree_offset + 30.0 * val / year;
                    if (iter == 0)
                        s = degree;
                    else
                        e = degree;
                    break;
                }
                degree_offset += 30.0;
                val -= year;
            }
            if (i >= limit_seq.length)
                return null;
            val++;
        }
        degree_offset = ((int) (life_sign_pos / 30.0)) * 30.0 + 30.0;
        double[] pos = new double[2];
        pos[0] = City.normalizeDegree(degree_offset - s);
        pos[1] = City.normalizeDegree(degree_offset - e);
        return pos;
    }

    private int[] snapToSignStart(int[] date)
    {
        BaseCalendar.setTime(work_cal, date);
        int hour = ((date[3] + 1) / 2) * 2 - 1;
        if (hour < 0)
            hour = 23;
        work_cal.set(Calendar.HOUR_OF_DAY, hour);
        if (date[3] < 1)
            work_cal.add(Calendar.DAY_OF_MONTH, -1);
        int[] result = new int[5];
        BaseCalendar.getTime(work_cal, result);
        result[4] = 0;
        return result;
    }

    private double[] computeCenterPos(double[] pos)
    {
        double[] c_pos = new double[28];
        for (int i = 0; i < 28; i++) {
            double next_pos = pos[(i + 1) % 28];
            if (next_pos < pos[i])
                next_pos += 360.0;
            c_pos[i] = 0.5 * (pos[i] + next_pos);
            if (c_pos[i] >= 360.0)
                c_pos[i] -= 360.0;
        }
        return c_pos;
    }

    private void initSignShift(int offset, int len, String[] name,
            double[] loc, int[] state, double[] alt, boolean[] lock,
            Position[] position)
    {
        for (int i = 0; i < len; i++) {
            int index = i + offset;
            int speed_state;
            if (alt != null) {
                speed_state = (alt[i] == Calculate.INVALID) ? 2
                        : ((alt[i] >= 0) ? 0 : 1);
            } else {
                speed_state = (state == null) ? Calculate.SPEED_NORMAL
                        : state[i];
            }
            position[index] = new Position(name[i], index, loc[i], speed_state,
                    lock != null && lock[i]);
        }
    }

    private void computeSignShift(DrawAWT draw, int dist, int len,
            boolean lock, Position[] position)
    {
        Arrays.sort(position, 0, len, new Comparator() {
            public int compare(Object a, Object b)
            {
                double p_a = ((Position) a).getLocation();
                double p_b = ((Position) b).getLocation();
                if (p_a < p_b)
                    return -1;
                else if (p_a > p_b)
                    return 1;
                else
                    return 0;
            }
        });
        draw.initFontMetric(dist);
        boolean again;
        do {
            again = false;
            for (int i = 0; i < len; i++) {
                Position right = Position.getPosition(position, len, i);
                if (!Calculate.isValid(right.getLocation()))
                    continue;
                Position left = null;
                int j = i - 1;
                for (;; j--) {
                    left = Position.getPosition(position, len, j);
                    if (Calculate.isValid(left.getLocation()))
                        break;
                }
                if (left == right)
                    continue;
                double gap = getGap(left, right);
                double min_gap = draw.degreeInWidth(left.getName(),
                        right.getName());
                if (gap < min_gap) {
                    double target = 1.1 * min_gap - gap;
                    target -= shiftLeftPosition(draw, position, len, j,
                            0.5 * target);
                    target -= shiftRightPosition(draw, position, len, i, target);
                    if (target > 0.0) {
                        target -= shiftLeftPosition(draw, position, len, j,
                                target);
                        if (lock && target > 0.0) {
                            lock = false;
                            again = true;
                            for (int k = 0; k < len; k++)
                                position[k].reset();
                            break;
                        }
                    }
                }
            }
        } while (again);
    }

    private boolean computeSignPosition(DrawAWT draw, double zoom, int dist,
            int min_radius, int height, int gap, double x_offset,
            double[] x_loc, double[] y_loc, double[] diameter, boolean[] mark,
            Position[] position)
    {
        Position.setGapFontHeight(gap, height);
        for (int i = 0; i < signs.length; i++) {
            if (!Calculate.isValid(x_loc[i])) {
                position[i] = null;
                continue;
            }
            double pos = 135.0 - x_loc[i] + x_offset;
            // map to -180 inclusive to 180 exclusive
            while (pos < -180.0)
                pos += 360.0;
            while (pos >= 180.0)
                pos -= 360.0;
            int x = (int) (zoom * dist * pos / 180.0 + 0.5);
            int y = (int) (-0.5 * zoom * dist * y_loc[i] / 90.0 + 0.5);
            int val = Math.max(min_radius, (int) (0.5 * dist * zoom
                    * diameter[i] / 180.0 + 0.5));
            double rad = draw.radianInWidth(val + gap);
            position[i] = new Position(i, x, y, val, 0.5 * rad);
            if (mark != null)
                position[i].blockBoundary(zoom);
        }
        for (int i = 0; i < signs.length; i++) {
            if (position[i] == null)
                continue;
            Position p = position[i];
            for (int j = 0; j < signs.length; j++) {
                if (i == j || position[j] == null)
                    continue;
                Position t_p = position[j];
                if (p.contain(t_p))
                    p.blockContain(t_p);
                else
                    // use half the actual radius for compact display
                    p.block(t_p, 0.5 * t_p.getExtendedRadius());
            }
        }
        Position[] t_position = (Position[]) position.clone();
        Arrays.sort(t_position, 0, signs.length, new Comparator() {
            public int compare(Object a, Object b)
            {
                if (a == null)
                    return (b == null) ? 0 : 1;
                else if (b == null)
                    return -1;
                double g_a = ((Position) a).getValidGap();
                double g_b = ((Position) b).getValidGap();
                if (g_a < g_b)
                    return -1;
                else if (g_a > g_b)
                    return 1;
                else
                    return 0;
            }
        });
        boolean has_mark = false;
        for (int i = 0; i < signs.length; i++) {
            Position p = t_position[i];
            if (p == null)
                continue;
            double val = p.getAngle();
            if (!Calculate.isValid(val)) {
                val = p.getValidAngle();
                if (Calculate.isValid(val)) {
                    pickAngle(i, val, t_position);
                } else {
                    has_mark = true;
                    if (mark != null)
                        mark[p.getIndex()] = true;
                }
            }
        }
        return has_mark;
    }

    private boolean needZoom(double scaler, boolean[] zoom, Position[] position)
    {
        // zoom if any planets within zoom gap
        boolean has_zoom = false;
        for (int i = 0; i < signs.length; i++) {
            zoom[i] = anyInRange(i, HORIZ_ZOOM_GAP, scaler, position);
            if (zoom[i])
                has_zoom = true;
        }
        return has_zoom;
    }

    private boolean anyInRange(int index, double degree, double scaler,
            Position[] position)
    {
        Position p = position[index];
        if (p == null)
            return false;
        for (int i = 0; i < signs.length; i++) {
            Position q = position[i];
            if (i == index || q == null)
                continue;
            if (p.contain(q, degree, scaler))
                return true;
        }
        return false;
    }

    private void clearMark(Position p, double scaler, double dist,
            boolean[] mark, Position[] position)
    {
        double degree = dist / scaler;
        for (int i = 0; i < signs.length; i++) {
            if (mark[i] && p.contain(position[i], degree, scaler))
                mark[i] = false;
        }
    }

    private void drawHorizSign(DrawAWT draw, AffineTransform save,
            int x_offset, int y_offset, int min_radius, boolean use_bw,
            Polygon bound, Position[] position)
    {
        for (int i = 0; i < signs.length; i++) {
            Position p = position[i];
            if (p == null)
                continue;
            draw.setTransform(save);
            int x = ((int) p.getX()) - x_offset;
            int y = ((int) p.getY()) - y_offset;
            if (bound != null && !bound.contains(x, y))
                continue;
            draw.translate(x, y);
            int rad = (int) p.getRadius();
            if (rad <= min_radius)
                draw.drawDiamond(rad);
            else
                draw.drawCircle(rad);
            double angle = p.getAngle();
            if (Calculate.isValid(angle)) {
                if (bound != null) {
                    int h_height = (int) (0.5 * Position.getFontHeight() + 0.5);
                    double t_radius = p.getRange() - h_height;
                    int t_x = (int) (x + t_radius * Math.cos(angle) + 0.5);
                    int t_y = (int) (y + t_radius * Math.sin(angle) + 0.5);
                    if (!bound.contains(t_x, t_y))
                        continue;
                }
                int index = p.getIndex();
                if (!use_bw)
                    draw.setColor(birth_speed_color[birth_sign_state[index]]);
                draw.rotate(angle);
                draw.drawRotatedStringVert((int) p.getExtendedRadius(),
                        Position.getFontHeight(), signs[index], true);
                draw.setColor();
            }
        }
    }

    private void drawZoom(DrawAWT draw, AffineTransform save, int index,
            double zoom, int min_radius, int h_width, boolean use_bw,
            Position[] position)
    {
        draw.drawRect(-h_width + 1, -h_width + 1, 2 * h_width - 2,
                2 * h_width - 2);
        AffineTransform trans = draw.getTransform();
        String label = Integer.toString((int) zoom) + "X";
        int str_width = draw.stringWidth(label);
        int offset = (int) (0.08 * h_width);
        // shrink by half font height
        int h_bound = h_width - Position.getFontHeight() / 2;
        int c_x = -h_bound + str_width + offset;
        int c_y = h_bound - Position.getFontHeight() - offset;
        Polygon bound = new Polygon();
        bound.addPoint(-h_bound, -h_bound);
        bound.addPoint(h_bound, -h_bound);
        bound.addPoint(h_bound, h_bound);
        bound.addPoint(c_x, h_bound);
        bound.addPoint(c_x, c_y);
        bound.addPoint(-h_bound, c_y);
        // clipping does not work on pdf driver
        drawHorizSign(draw, trans, (int) position[index].getX(),
                (int) position[index].getY(), min_radius, use_bw, bound,
                position);
        // draw.setClip(null);
        draw.setTransform(trans);
        draw.drawString(label, -h_width + offset, h_width - offset);
    }

    private void pickAngle(int index, double angle, Position[] position)
    {
        Position p = position[index];
        p.setAngle(angle);
        int h_height = (int) (0.5 * Position.getFontHeight() + 0.5);
        double t_radius = p.getRange() - h_height;
        int t_x = (int) (p.getX() + t_radius * Math.cos(angle) + 0.5);
        int t_y = (int) (p.getY() + t_radius * Math.sin(angle) + 0.5);
        Position t_p = new Position(index, t_x, t_y, h_height, 0.0);
        for (int i = 0; i < signs.length; i++) {
            if (i == index || position[i] == null)
                continue;
            position[i].block(t_p, t_p.getRadius());
        }
    }

    private double shiftLeftPosition(DrawAWT draw, Position[] position,
            int length, int index, double degree)
    {
        Position cur_pos = Position.getPosition(position, length, index);
        if (cur_pos.getLocked())
            return 0.0;
        cur_pos.addShift(-degree);
        Position next_pos;
        int j = index - 1;
        for (;; j--) {
            next_pos = Position.getPosition(position, length, j);
            if (Calculate.isValid(next_pos.getLocation()))
                break;
        }
        if (cur_pos == next_pos)
            return degree;
        double gap = getGap(next_pos, cur_pos);
        double min_gap = draw.degreeInWidth(next_pos.getName(),
                cur_pos.getName());
        min_gap -= gap;
        if (min_gap <= 0.0)
            return degree;
        double shift = shiftLeftPosition(draw, position, length, j, min_gap);
        cur_pos.addShift(degree);
        degree -= min_gap - shift;
        cur_pos.addShift(-degree);
        return degree;
    }

    private double shiftRightPosition(DrawAWT draw, Position[] position,
            int length, int index, double degree)
    {
        Position cur_pos = Position.getPosition(position, length, index);
        if (cur_pos.getLocked())
            return 0.0;
        cur_pos.addShift(degree);
        Position next_pos;
        int j = index + 1;
        for (;; j++) {
            next_pos = Position.getPosition(position, length, j);
            if (Calculate.isValid(next_pos.getLocation()))
                break;
        }
        if (cur_pos == next_pos)
            return degree;
        double gap = getGap(cur_pos, next_pos);
        double min_gap = draw.degreeInWidth(cur_pos.getName(),
                next_pos.getName());
        min_gap -= gap;
        if (min_gap <= 0.0)
            return degree;
        double shift = shiftRightPosition(draw, position, length, j, min_gap);
        cur_pos.addShift(-degree);
        degree -= min_gap - shift;
        cur_pos.addShift(degree);
        return degree;
    }

    private double getGap(Position left, Position right)
    {
        double l_pos = left.getLocation();
        double r_pos = right.getLocation();
        if (!Calculate.isValid(l_pos) || !Calculate.isValid(r_pos))
            return 360.0;
        double before_gap = r_pos - l_pos;
        double gap = right.getShiftedLocation() - left.getShiftedLocation();
        if (before_gap < 0.0)
            before_gap += 360.0;
        if (gap < 0.0)
            gap += 360.0;
        return (Math.abs(gap - before_gap) > 180.0) ? (gap - 360.0) : gap;
    }

    private Date getStartEndDate(Calendar t_cal, Date date, boolean end)
    {
        t_cal.setTime(date);
        t_cal.set(Calendar.MONTH, 0);
        t_cal.set(Calendar.DAY_OF_MONTH, 1);
        t_cal.set(Calendar.HOUR_OF_DAY, 0);
        t_cal.set(Calendar.MINUTE, 0);
        if (end)
            t_cal.add(Calendar.YEAR, 1);
        return t_cal.getTime();
    }

    private Date getDate(int[] date_buf)
    {
        BaseCalendar.setTime(calendar, date_buf);
        return calendar.getTime();
    }

    private Date addYearToBirthDate(double year)
    {
        int day = (int) (year * 365.25);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    private Date addChildYearToBirthDate()
    {
        int day = getChildLimit(false);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    private String getShortDate(Date date, boolean year_only)
    {
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        String str = Integer.toString(year);
        if (!year_only) {
            str = Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/"
                    + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))
                    + "/" + str;
        }
        if (calendar.get(Calendar.ERA) == GregorianCalendar.BC)
            str += " B.C.";
        return str;
    }

    private String getShortDate(int[] date)
    {
        boolean bc = date[0] <= 0; // 1 B.C. is 0, 2 B.C. is -1, and so on
        return Integer.toString(date[1]) + "/" + Integer.toString(date[2])
                + "/" + Integer.toString(bc ? (-date[0] + 1) : date[0])
                + (bc ? " B.C." : "");
    }

    public int getChildLimit(boolean round_to_year)
    {
        double degree = life_sign_pos;
        degree -= ((int) (degree / 30.0)) * 30.0;
        int day = (int) ((((Resource.getPrefInt("child_period") != 0) ? 10.0
                : 9.0) + degree / 3.0) * 365.25);
        if (round_to_year)
            return (int) ((day / 365.25) + 0.5);
        else
            return day;
    }

    public void reset()
    {
        has_day_birth_override = use_primary_speed = false;
        house_override = null;
        Arrays.fill(sign_pos_shift, 0.0);
        Arrays.fill(sign_rev_flip, false);
        eight_char_override = null;
        birth_cusp_override = null;
        tab.clear();
        pole_tab.clear();
    }

    public String compute(DataEntry d_birth, DataEntry d_now, DataEntry d_loc,
            DiagramTip d_tip)
    {
        birth = d_birth;
        now = d_now;
        cur_loc = d_loc;
        tip = d_tip;
        try {
            computeData();
        } catch (ArithmeticException e) {
            return e.getMessage();
        }
        showData();
        return null;
    }

    private void computeData() throws ArithmeticException
    {
        cal.setChartMode();
        show_gauquelin = ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                && show_gauquelin_set;
        show_aspects = ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                || !ChartMode.isChartMode(ChartMode.PICK_MODE)
                && show_aspects_set;
        show_now = (!show_horiz_set && show_now_set
                && !ChartMode.isChartMode(ChartMode.PICK_MODE) && !ChartMode
                .isChartMode(ChartMode.ASTRO_MODE));
        dual_ring = ChartMode.isAstroDualRingMode();
        single_wheel_mode = ChartMode.isSingleWheelMode();
        show_fixstar = show_fixstar_set
                && Resource.getPrefInt("enable_fixstar") != 0;
        show_compass = !show_fixstar
                && Resource.getPrefInt("show_compass") != 0;
        show_horiz = show_horiz_set;
        if (show_horiz)
            cal.setTopocentricMode(true, true);
        explain_star = Resource.getPrefInt("explain_star") != 0;
        ten_god_mode = Resource.getPrefInt("ten_god_mode") != 0;
        ten_god_list = ten_god_mode ? ten_god_list_alt : ten_god_list_org;
        true_north = Resource.getPrefInt("true_north") != 0;
        true_north_magnetic_shift = true_north ? magnetic_shift : 0.0;
        full_mountain_signs = (Resource.getPrefInt("main_mountain") != 0) ? full_mountain_main_signs
                : full_mountain_secondary_signs;
        quick_azimuth = ChartMode.isChartMode(ChartMode.PICK_MODE)
                && Resource.getPrefInt("quick_azimuth") != 0;
        mountain_mode = Resource.getPrefInt("mountain_mode");
        mountain_offset = (mountain_mode == 0) ? 0.0
                : ((mountain_mode == 1) ? -7.5 : 7.5);
        cal.setMountainOffset(mountain_offset);
        cal.setHouseSystemIndex(Resource.getPrefInt(ChartMode
                .isChartMode(ChartMode.PICK_MODE) ? "pick_house_system_index"
                : "house_system_index"));
        initSignDisplay();
        initAspects();
        birth_table = now_table = null;
        good_styles = bad_styles = null;
        aspects_index = null;
        alt_pole_data = null;
        solar_eclipse = lunar_eclipse = null;
        use_primary_speed = false;
        primary_speed = birth_ref_ut = now_ref_ut = 0.0;
        boolean return_mode = ChartMode.isReturnMode();
        if (!ChartMode.isAstroMode(ChartMode.LUNAR_RETURN_MODE))
            lunar_transits = null;
        if (ChartMode.isAstroMode(ChartMode.NATAL_MODE))
            solar_return_pos = lunar_return_pos = Calculate.INVALID;
        day_birth = false;
        angle_offset = (!show_horiz && ChartMode
                .isChartMode(ChartMode.PICK_MODE)) ? 15.0 : 0.0;
        life_sign_pos = self_sign_pos = first_cusp_pos = tenth_cusp_pos = Calculate.INVALID;
        Arrays.fill(birth_sign_pos, Calculate.INVALID);
        Arrays.fill(gauquelin_sign_region, Calculate.INVALID);
        Arrays.fill(gauquelin_sign_pos, Calculate.INVALID);
        Arrays.fill(fixstar_sign_pos, Calculate.INVALID);
        Arrays.fill(fixstar_sign_azimuth, Calculate.INVALID);
        has_pos_adj = false;
        for (int i = 0; i < sign_pos_shift.length; i++) {
            if (sign_rev_flip[i] || sign_pos_shift[i] != 0.0
                    || eight_char_override != null) {
                has_pos_adj = true;
                break;
            }
        }
        for (int j = 0; j < (ChartMode.isChartMode(ChartMode.PICK_MODE) ? 1 : 2); j++) {
            boolean birth_data = j == 0;
            double[] sign_pos, sign_azimuth, sign_alt, sign_diameter, cusps;
            int[] sign_state;
            if (birth_data) {
                DataEntry entry = (return_mode
                        || ChartMode.isAstroMode(ChartMode.ALT_NATAL_MODE) || ChartMode
                        .isAstroMode(ChartMode.RELATIONSHIP_MODE)) ? now
                        : birth;
                updateLocation(entry.getCity(), entry.getCountry());
                cal.getLocation(birth_loc);
                int[] date_buf = entry.getBirthDay();
                birth_zone = entry.getZone();
                BaseCalendar.addZoneOffset(birth_zone, date_buf, 0, false);
                if (!cal.setJulianDay(date_buf))
                    throw new ArithmeticException("Year out of range");
                birth_ref_ut = cal.getJulianDayUT();
                sign_pos = birth_sign_pos;
                sign_state = birth_sign_state;
                sign_azimuth = birth_sign_azimuth;
                sign_alt = birth_sign_alt;
                sign_diameter = birth_sign_diameter;
                cusps = birth_cusp;
                if (birth_cusp_override != null) {
                    for (int k = 1; k < birth_cusp_override.length; k++)
                        birth_cusp[k] = birth_cusp_override[k];
                } else {
                    cal.computeHouses(cusps);
                }
                // calculate master first
                // speed state must be computed last
                sign_pos[SUN] = computePlanet(SUN);
                sign_azimuth[SUN] = computeAzimuth(SUN, sign_pos, cusps);
                sign_alt[SUN] = computeAltitude(SUN);
                sign_state[SUN] = getSpeedState(SUN);
                sign_pos[MOON] = computePlanet(MOON);
                sign_azimuth[MOON] = computeAzimuth(MOON, sign_pos, cusps);
                sign_alt[MOON] = computeAltitude(MOON);
                sign_state[MOON] = getSpeedState(MOON);
                if (show_horiz) {
                    sign_diameter[SUN] = computePheno(SUN);
                    sign_diameter[MOON] = computePheno(MOON);
                }
                double[] sun_rise_set = new double[2], moon_rise_set = new double[2];
                equator_rise_set[SUN] = cal.computeRiseSet(birth_zone,
                        planets[SUN], sun_rise_set);
                day_birth = cal.isDayBirth(sun_rise_set);
                // adjust for minute error < 0.01 degree to make it sign
                // consistent
                if (!equator_rise_set[SUN] && Calculate.isValid(sign_alt[SUN])
                        && Math.abs(sign_alt[SUN]) < 0.01) {
                    if (day_birth)
                        sign_alt[SUN] = Math.max(TINY_DEGREE, sign_alt[SUN]);
                    else
                        sign_alt[SUN] = Math.min(-TINY_DEGREE, sign_alt[SUN]);
                }
                cal.initSpecial(sign_pos[SUN], sign_pos[MOON], day_birth);
                equator_rise_set[MOON] = cal.computeRiseSet(birth_zone,
                        planets[MOON], moon_rise_set);
                birth_base_time = getAdjustDate(birth_zone, birth_loc[0],
                        date_buf, birth_adj_date, birth_dst_date);
                solar_terms = cal.computeSolarTerms(birth_adj_date);
                if (solar_terms == null) {
                    throw new ArithmeticException("Year " + birth_adj_date[0]
                            + " computation failed");
                }
                last_year_birth = birth_ref_ut < solar_terms[3];
                new_moons = cal.computeNewMoons(birth_adj_date, solar_terms);
                int[] sun_rise_date = new int[5], sun_set_date = new int[5];
                int[] moon_rise_date = new int[5], moon_set_date = new int[5];
                int[] rise_set_date = (int[]) date_buf.clone();
                BaseCalendar.addZoneOffset(birth_zone, rise_set_date, -1, true);
                lunar_date = cal.getLunarCalendar(rise_set_date, solar_terms,
                        new_moons);
                lunar_leap_month = cal.isLeapMonth();
                solar_date = cal.getSolarCalendar(rise_set_date, solar_terms,
                        false);
                for (int i = SUN; i <= MOON; i++) {
                    birth_rise_time[i] = birth_set_time[i] = null;
                    double[] rise_set;
                    int[] rise_date, set_date;
                    if (i == SUN) {
                        rise_set = sun_rise_set;
                        rise_date = sun_rise_date;
                        set_date = sun_set_date;
                    } else {
                        rise_set = moon_rise_set;
                        rise_date = moon_rise_date;
                        set_date = moon_set_date;
                    }
                    if (Calculate.isValid(rise_set[0])
                            && Calculate.isValid(rise_set[1])) {
                        if (equator_rise_set[i]) {
                            birth_rise_time[i] = birth_set_time[i] = "*******";
                        } else {
                            Calculate.getDateFromJulianDayUT(rise_set[0],
                                    rise_set_date);
                            switch (time_adjust) {
                                case 2:
                                {
                                    birth_rise_time[i] = BaseCalendar
                                            .formatDate(
                                                    birth_loc[0],
                                                    rise_set_date,
                                                    rise_date,
                                                    cal.getLATDateFromDate(rise_set_date),
                                                    false, true);
                                }
                                    break;
                                case 1:
                                    birth_rise_time[i] = BaseCalendar
                                            .formatDate(birth_loc[0],
                                                    rise_set_date, rise_date,
                                                    0.0, false, true);
                                    break;
                                default:
                                    birth_rise_time[i] = BaseCalendar
                                            .formatDate(birth_zone,
                                                    rise_set_date, rise_date,
                                                    false, true);
                                    break;
                            }
                            Calculate.getDateFromJulianDayUT(rise_set[1],
                                    rise_set_date);
                            switch (time_adjust) {
                                case 2:
                                {
                                    birth_set_time[i] = BaseCalendar
                                            .formatDate(
                                                    birth_loc[0],
                                                    rise_set_date,
                                                    set_date,
                                                    cal.getLATDateFromDate(rise_set_date),
                                                    false, true);
                                }
                                    break;
                                case 1:
                                    birth_set_time[i] = BaseCalendar
                                            .formatDate(birth_loc[0],
                                                    rise_set_date, set_date,
                                                    0.0, false, true);
                                    break;
                                default:
                                    birth_set_time[i] = BaseCalendar
                                            .formatDate(birth_zone,
                                                    rise_set_date, set_date,
                                                    false, true);
                                    break;
                            }
                            if (i == SUN
                                    && Resource.getPrefInt("day_night_by_time") == 0) {
                                day_birth = cal.isDayBirthByZone(
                                        birth_adj_date[3], rise_date[3],
                                        set_date[3]);
                            }
                        }
                        if (Calculate.isValid(rise_set[0])
                                && Calculate.isValid(rise_set[1])) {
                            if (i == SUN) {
                                int[] date;
                                if (ChartMode.isChartMode(ChartMode.PICK_MODE)
                                        && !day_set) {
                                    date = sun_set_date;
                                } else if (Resource.getPrefInt("use_equator") != 0) {
                                    date = (int[]) sun_rise_date.clone();
                                    date[3] = 6;
                                    date[4] = 0;
                                } else {
                                    date = sun_rise_date;
                                }
                                life_sign_pos = computeLifeSign(date, cusps);
                                if (house_override != null
                                        && !house_override.equals(getHouse())) {
                                    life_sign_pos -= 30.0 * cal.getZodiacShift(
                                            house_override, life_sign_pos);
                                    if (life_sign_pos < 0.0)
                                        life_sign_pos += 360.0;
                                } else {
                                    house_override = null;
                                }
                            } else {
                                self_sign_pos = computeSelfSign(moon_rise_date,
                                        sun_set_date);
                            }
                        }
                    }
                }
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    if (has_day_birth_override
                            && day_birth_override != day_birth)
                        day_birth = day_birth_override;
                    else
                        has_day_birth_override = false;
                }
                computeEclipse();
                birth_table = chineseCalendar(birth_adj_date, birth_poles,
                        birth_sign_pos, eight_char_override, lunar_date,
                        solar_date);
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    boolean house_mode = false;
                    if (ChartMode.isChartMode(ChartMode.TRADITIONAL_MODE)
                            || ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        house_mode = Resource
                                .getPrefInt(ChartMode
                                        .isChartMode(ChartMode.PICK_MODE) ? "pick_house_mode"
                                        : "house_mode") != 0;
                    }
                    String[] array = (ChartMode
                            .isChartMode(ChartMode.SIDEREAL_MODE)
                            || ChartMode.isChartMode(ChartMode.PICK_MODE)
                            && Resource.getPrefInt("pick_sidereal_mode") != 0 || house_mode) ? sidereal_stellar_names
                            : stellar_names;
                    double offset = 0.0;
                    if (house_mode) {
                        if (Resource
                                .getPrefInt(ChartMode
                                        .isChartMode(ChartMode.PICK_MODE) ? "pick_adjust_mode"
                                        : "adjust_mode") != 0)
                            offset = cal.getAyanamsha();
                    }
                    for (int i = 0; i < array.length; i++) {
                        StringBuffer name_buf = new StringBuffer("," + array[i]);
                        stellar_sign_pos[i] = City.normalizeDegree(cal
                                .computeStar(name_buf, null) + offset);
                    }
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        array = compass_stellar_names;
                        for (int i = 0; i < array.length; i++) {
                            StringBuffer name_buf = new StringBuffer(","
                                    + array[i]);
                            compass_sign_pos[i] = City.normalizeDegree(cal
                                    .computeStar(name_buf, null) + offset);
                        }
                    }
                }
                if (ChartMode.isAstroMode(ChartMode.NATAL_MODE)) {
                    solar_return_pos = birth_sign_pos[SUN];
                    lunar_return_pos = birth_sign_pos[MOON];
                }
                if (Calculate.isValid(lunar_return_pos)
                        && (ChartMode.isAstroMode(ChartMode.NATAL_MODE) || ChartMode
                                .isAstroMode(ChartMode.SOLAR_RETURN_MODE))) {
                    // find lunar transits
                    double start_ut;
                    int[] date = new int[5];
                    if (ChartMode.isAstroMode(ChartMode.SOLAR_RETURN_MODE)) {
                        date[0] = date_buf[0];
                        date[1] = date[2] = 1;
                        date[3] = date[4] = 0;
                        start_ut = Calculate.getJulianDayUT(date);
                    } else { // natal
                        start_ut = Calculate.getJulianDayUT(date_buf) + 15.0;
                        date[0] = date_buf[0];
                    }
                    date[0]++;
                    date[1] = date[2] = 1;
                    date[3] = date[4] = 0;
                    double end_ut = Calculate.getJulianDayUT(date);
                    lunar_transits = cal.computeTransit(planets[MOON],
                            start_ut, end_ut, lunar_return_pos);
                }
                if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    azimuth_speed = Resource.getString("azimuth_speed")
                            + " "
                            + FileIO.formatDouble(
                                    cal.computePlanetAzimuthSpeed(
                                            initSunComputation(),
                                            cal.getJulianDayUT(), quick_azimuth),
                                    1, 2, true, false) + " "
                            + Resource.getString("degree");
                    if (!show_horiz && show_fixstar) {
                        for (int i = 0; i < fixstar_names.length; i++) {
                            if (!inTable(fixstar_table, fixstar_signs[i], false))
                                continue;
                            StringBuffer name_buf = new StringBuffer(","
                                    + fixstar_names[i]);
                            double pos = cal.computeStar(name_buf,
                                    fixstar_signs[i]);
                            fixstar_sign_pos[i] = pos;
                            fixstar_sign_state[i] = computeFixstarState(pos,
                                    birth_sign_pos[SUN]);
                            if (fixstar_sign_state[i] == Calculate.SPEED_REVERSE) {
                                fixstar_sign_azimuth[i] = computeAzimuth(pos,
                                        cusps, quick_azimuth);
                                fixstar_sign_alt[i] = cal.getAltitude();
                                String key = cal
                                        .getMountain(fixstar_sign_azimuth[i]);
                                LinkedList data = (LinkedList) birth_table
                                        .get(key);
                                if (data == null)
                                    data = new LinkedList();
                                data.add(fixstar_signs[i]);
                                birth_table.put(key, data);
                            }
                        }
                    }
                }
            } else {
                updateLocation(now.getCity(), now.getCountry());
                cal.getLocation(now_loc);
                int[] date_buf = (dual_ring || single_wheel_mode || ChartMode
                        .isAstroMode(ChartMode.COMPOSITE_MODE)) ? now
                        .getBirthDay() : now.getNowDay();
                String zone = now.getZone();
                BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
                if (!cal.setJulianDay(date_buf))
                    throw new ArithmeticException("Year out of range");
                if (ChartMode.isAstroMode(ChartMode.PRIMARY_DIRECTION_MODE)) {
                    primary_speed = getPrimarySpeed();
                    use_primary_speed = equatorial_orbit = true;
                } else if (ChartMode
                        .isAstroMode(ChartMode.SECONDARY_PROGRESSION_MODE)
                        || ChartMode.isAstroMode(ChartMode.SOLAR_ARC_MODE)) {
                    double ratio = getCompressionRatio();
                    cal.setJulianDay(birth_ref_ut + ratio
                            * (cal.getJulianDayUT() - birth_ref_ut));
                }
                now_ref_ut = cal.getJulianDayUT();
                sign_pos = now_sign_pos;
                sign_state = now_sign_state;
                sign_azimuth = now_sign_azimuth;
                sign_alt = now_sign_alt;
                sign_diameter = null;
                cusps = now_cusp;
                if (!ChartMode.isAstroMode(ChartMode.PRIMARY_DIRECTION_MODE)
                        && !ChartMode.isAstroMode(ChartMode.SOLAR_ARC_MODE)) {
                    if (ChartMode
                            .isAstroMode(ChartMode.SECONDARY_PROGRESSION_MODE)) {
                        cal.computeHouses(cusps, (int) (now_ref_ut)
                                + (birth_ref_ut - (int) birth_ref_ut));
                    } else {
                        cal.computeHouses(cusps);
                    }
                }
                // speed state must be computed last
                sign_pos[SUN] = computePlanet(SUN);
                sign_azimuth[SUN] = computeAzimuth(SUN, sign_pos, cusps);
                sign_alt[SUN] = computeAltitude(SUN);
                sign_state[SUN] = getSpeedState(SUN);
                if (ChartMode.isAstroMode(ChartMode.SOLAR_ARC_MODE)) {
                    double val = City.normalizeDegree(sign_pos[SUN]
                            - birth_sign_pos[SUN]);
                    double b_val = cal.getJulianDayUT() - birth_ref_ut;
                    if (Math.abs(b_val) < Calculate.MINUTE)
                        b_val = Calculate.MINUTE;
                    primary_speed = val / b_val;
                    use_primary_speed = true;
                    equatorial_orbit = false;
                }
                sign_pos[MOON] = computePlanet(MOON);
                sign_azimuth[MOON] = computeAzimuth(MOON, sign_pos, cusps);
                sign_alt[MOON] = computeAltitude(MOON);
                sign_state[MOON] = getSpeedState(MOON);
                if (!ChartMode.isAstroMode(ChartMode.PRIMARY_DIRECTION_MODE)
                        && !ChartMode.isAstroMode(ChartMode.SOLAR_ARC_MODE)) {
                    double[] sun_rise_set = new double[2];
                    cal.computeRiseSet(zone, planets[SUN], sun_rise_set);
                    boolean day_time = cal.isDayBirth(sun_rise_set);
                    cal.initSpecial(sign_pos[SUN], sign_pos[MOON], day_time);
                }
                if (show_now) {
                    double[] now_solar_terms = cal.computeSolarTerms(date_buf);
                    if (now_solar_terms == null) {
                        throw new ArithmeticException("Year " + date_buf[0]
                                + " computation failed");
                    }
                    double[] now_new_moons = cal.computeNewMoons(date_buf,
                            now_solar_terms);
                    int[] cal_date = (int[]) date_buf.clone();
                    BaseCalendar.addZoneOffset(zone, cal_date, -1, true);
                    now_lunar_date = cal.getLunarCalendar(cal_date,
                            now_solar_terms, now_new_moons);
                    now_lunar_leap_month = cal.isLeapMonth();
                    int[] now_solar_date = cal
                            .getSolarCalendar(
                                    cal_date,
                                    now_solar_terms,
                                    Resource.getPrefInt("start_at_winter_solstice") != 0);
                    now_table = chineseCalendar(date_buf, now_poles,
                            now_sign_pos, null, now_lunar_date, now_solar_date);
                }
            }
            for (int i = 0; i < signs.length; i++) {
                if (i != SUN && i != MOON) {
                    if (sign_computation_type[i] != 0 || i < planets.length
                            && planets[i] >= 0) {
                        // speed state must be computed last
                        sign_pos[i] = computePlanet(i);
                        if (i == 17)
                            i = 17;
                        sign_azimuth[i] = computeAzimuth(i, sign_pos, cusps);
                        sign_alt[i] = computeAltitude(i);
                        sign_state[i] = getSpeedState(i);
                        if (show_horiz && sign_diameter != null)
                            sign_diameter[i] = computePheno(i);
                    } else {
                        sign_pos[i] = sign_azimuth[i] = sign_alt[i] = Calculate.INVALID;
                        sign_state[i] = Calculate.SPEED_NORMAL;
                        if (sign_opposite[i] >= 0
                                && Calculate
                                        .isValid(sign_pos[sign_opposite[i]])) {
                            sign_pos[i] = computeOpposite(i, sign_pos);
                            sign_azimuth[i] = computeOpposite(i, sign_azimuth);
                            sign_alt[i] = -sign_alt[sign_opposite[i]];
                        }
                    }
                }
            }
            if (birth_data) {
                first_cusp_pos = birth_cusp[1];
                tenth_cusp_pos = birth_cusp[10];
                if (show_gauquelin) {
                    for (int i = 0; i < signs.length; i++)
                        computeGauquelin(i, gauquelin_sign_region,
                                gauquelin_sign_pos);
                }
            }
            if (Resource.getPrefInt("true_as_north") == 0) {
                double val = sign_pos[TRUE_NODE];
                sign_pos[TRUE_NODE] = sign_pos[INV_TRUE_NODE];
                sign_pos[INV_TRUE_NODE] = val;
                val = sign_azimuth[TRUE_NODE];
                sign_azimuth[TRUE_NODE] = sign_azimuth[INV_TRUE_NODE];
                sign_azimuth[INV_TRUE_NODE] = val;
            }
            for (int i = 0; i < signs.length; i++) {
                if (sign_display[i] <= 0 || signs[i].equals(invalid_sign)
                        || show_horiz && i >= FORTUNE && i <= MC) {
                    sign_pos[i] = sign_azimuth[i] = sign_alt[i] = gauquelin_sign_pos[i] = gauquelin_sign_region[i] = Calculate.INVALID;
                }
            }
            if (!birth_data) {
                if (single_wheel_mode) {
                    first_cusp_pos = now_cusp[1];
                    tenth_cusp_pos = now_cusp[10];
                    aspects_index = cal.computeAspects(now_sign_pos,
                            now_sign_pos, aspects_degree, aspects_orb);
                } else {
                    if (ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE)) {
                        for (int i = 0; i < signs.length; i++) {
                            if (!Calculate.isValid(birth_sign_pos[i])
                                    || !Calculate.isValid(now_sign_pos[i])) {
                                birth_sign_pos[i] = Calculate.INVALID;
                                continue;
                            }
                            double pos = 0.5 * (birth_sign_pos[i] + now_sign_pos[i]);
                            double gap = Math.abs(birth_sign_pos[i]
                                    - now_sign_pos[i]);
                            if (gap > 180.0)
                                pos -= 180.0;
                            birth_sign_pos[i] = City.normalizeDegree(pos);
                            birth_sign_state[i] = Calculate.SPEED_NORMAL;
                        }
                        double midheaven = 0.5 * (birth_cusp[10] + now_cusp[10]);
                        double gap = Math.abs(birth_cusp[10] - now_cusp[10]);
                        if (gap > 180.0)
                            midheaven -= 180.0;
                        midheaven = City.normalizeDegree(midheaven);
                        if (Resource.getPrefInt("composite_method_index") != 0) {
                            if (cur_loc != null)
                                updateLocation(cur_loc.getCity(),
                                        cur_loc.getCountry());
                            cal.computeHousesFromMidHeaven(birth_cusp,
                                    midheaven);
                        } else {
                            for (int i = 2; i < birth_cusp.length; i++) {
                                if (Calculate.isValid(birth_cusp[i - 1])
                                        && Calculate.isValid(birth_cusp[i])
                                        && birth_cusp[i - 1] >= birth_cusp[i]) {
                                    birth_cusp[i] += 360.0;
                                }
                                if (Calculate.isValid(now_cusp[i - 1])
                                        && Calculate.isValid(now_cusp[i])
                                        && now_cusp[i - 1] >= now_cusp[i]) {
                                    now_cusp[i] += 360.0;
                                }
                            }
                            double pos = 0.5 * (birth_cusp[10] + now_cusp[10]);
                            pos = City.normalizeDegree(pos);
                            double offset = (Math.abs(pos - midheaven) > 1.0) ? 180.0
                                    : 0.0;
                            for (int i = 1; i < birth_cusp.length; i++) {
                                if (!Calculate.isValid(birth_cusp[i])
                                        || !Calculate.isValid(now_cusp[i])) {
                                    birth_cusp[i] = Calculate.INVALID;
                                    continue;
                                }
                                pos = 0.5 * (birth_cusp[i] + now_cusp[i])
                                        - offset;
                                birth_cusp[i] = City.normalizeDegree(pos);
                            }
                        }
                        first_cusp_pos = birth_cusp[1];
                        tenth_cusp_pos = birth_cusp[10];
                        if (Calculate.isValid(birth_sign_pos[ASC]))
                            birth_sign_pos[ASC] = first_cusp_pos;
                        if (Calculate.isValid(birth_sign_pos[MC]))
                            birth_sign_pos[MC] = tenth_cusp_pos;
                    }
                    aspects_index = cal.computeAspects(birth_sign_pos,
                            dual_ring ? now_sign_pos : birth_sign_pos,
                            aspects_degree, aspects_orb);
                }
                if (aspects_index == null)
                    show_aspects = false;
            }
        }
        if (Resource.getPrefInt("astro_sign_display_sort") == 1
                && ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (!show_gauquelin && !show_horiz) {
                for (int iter = 0; iter < ((dual_ring || single_wheel_mode) ? 2
                        : 1); iter++) {
                    int[] sort_orders = (iter == 0) ? birth_sign_display_sort_orders
                            : now_sign_display_sort_orders;
                    double[] sign_pos = (iter == 0) ? birth_sign_pos
                            : now_sign_pos;
                    Position[] position = new Position[sign_pos.length];
                    initSignShift(0, sign_pos.length, signs, sign_pos, null,
                            null, null, position);
                    Arrays.sort(position, 0, sign_pos.length, new Comparator() {
                        public int compare(Object a, Object b)
                        {
                            if (a == null)
                                return (b == null) ? 0 : 1;
                            else if (b == null)
                                return -1;
                            double g_a = ((Position) a).getZodiacDegree();
                            double g_b = ((Position) b).getZodiacDegree();
                            if (g_a < g_b)
                                return -1;
                            else if (g_a > g_b)
                                return 1;
                            else
                                return 0;
                        }
                    });
                    for (int i = 0; i < sign_pos.length; i++)
                        sort_orders[i] = position[i].getIndex();
                }
            } else {
                System.arraycopy(sign_display_orders, 0,
                        birth_sign_display_sort_orders, 0,
                        sign_display_orders.length);
                System.arraycopy(sign_display_orders, 0,
                        now_sign_display_sort_orders, 0,
                        sign_display_orders.length);
            }
        }
        use_primary_speed = false;
        int[] date_buf = now.getNowDay();
        current_age = date_buf[0];
        date_buf = birth.getBirthDay();
        current_age -= date_buf[0] - 1;
        computeRules();
        if (show_horiz)
            cal.setTopocentricMode(false, false);
    }

    private void computeRules()
    {
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            return;
        EvalRule rule = new EvalRule(this);
        rule.initSign(full_zodiac, full_stellar_signs, birth.getSex());
        rule.setBirthStarSign(birth_table, master_table, ten_god_list, signs,
                star_equ_map, getYearInfo(true, birth_table));
        rule.setBirthInfo(cal, birth_poles, day_birth, year_signs,
                life_sign_pos, self_sign_pos, stellar_sign_pos, lunar_date,
                lunar_leap_month);
        rule.setBirthSign(
                cal,
                signs,
                birth_sign_pos,
                stellar_sign_pos,
                ChartMode.isChartMode(ChartMode.PICK_MODE) ? (mountain_pos - true_north_magnetic_shift)
                        : Calculate.INVALID);
        rule.initOptions(eval_tab);
        rule.computeStyles();
        if (Resource.getPrefInt("show_style") != 0) {
            good_styles = rule.getGoodStyles();
            bad_styles = rule.getBadStyles();
        }
        if (eval_tab == null || !RuleEntry.hasRuleEntry(true))
            return;
        rule.ruleHeader();
        rule.computeRules();
        if (show_now) {
            int date[] = now.getNowDay();
            int s = date[0] + rule.getYearOffsetStart();
            s = Math.max(s, birth_adj_date[0]);
            int e = date[0] + rule.getYearOffsetEnd();
            e = Math.min(e, s + 99);
            if (s > e)
                s = e = date[0];
            if (s == e && s == date[0]) {
                rule.initNow();
                rule.setNowStarSign(now_table, master_table, ten_god_list,
                        signs, star_equ_map, getYearInfo(false, now_table));
                rule.setNowInfo(cal, now_poles, date, current_age,
                        nowYearPosition(current_age), stellar_sign_pos,
                        now_lunar_date, now_lunar_leap_month);
                rule.setNowSign(cal, signs, now_sign_pos, stellar_sign_pos);
                rule.computeRules();
            } else {
                String[] poles = new String[6];
                date[1] = 7;
                date[2] = 1;
                date[3] = 12;
                date[4] = 0;
                int birth_year = birth.getBirthDay()[0];
                for (date[0] = s; date[0] <= e; date[0]++) {
                    int age = date[0] - birth_year + 1;
                    Hashtable table = computeNowData(date, poles, age);
                    rule.initNow();
                    rule.setNowStarSign(
                            table,
                            master_table,
                            ten_god_list,
                            signs,
                            star_equ_map,
                            getYearInfo(poles, null, getYearInfoKey(false),
                                    table, false));
                    if (rule.setNowInfo(cal, poles, date, age,
                            nowYearPosition(age), stellar_sign_pos, null, false)) {
                        rule.computeRules();
                    }
                }
            }
        }
        rule.ruleFooter();
    }

    private Hashtable computeNowData(int[] date, String[] poles, int age)
    {
        int index = getYearNameIndex(age - 1);
        poles[YEAR_POLE] = year_names[index % year_names.length];
        Hashtable table = getStarSigns(poles, null, false);
        return table;
    }

    private void showData()
    {
        tab.clear();
        pole_tab.clear();
        tip.reset();
        boolean dual_or_single = dual_ring || single_wheel_mode;
        day_table = null;
        center_tip = birth_sign_tip = now_sign_tip = astro_cusp_data = horiz_sign_data = null;
        day_or_night = "";
        three_danger = null;
        Arrays.fill(astro_elemental, 0);
        Arrays.fill(astro_elemental_state, 0);
        birth_sign_status_fill = "";
        int[] date_buf;
        int degree_mode = ChartMode.getDegreeMode(quick_azimuth);
        boolean return_mode = ChartMode.isReturnMode();
        boolean show_house_system = !ChartMode
                .isChartMode(ChartMode.ASTRO_MODE)
                && Resource.getPrefInt("show_house_system") != 0
                && !show_aspects;
        String house_gap = show_house_system ? ("   " + Resource
                .spacePostFilled("", house_name[0].length())) : "";
        String child_limit = null;
        if (!ChartMode.isChartMode(ChartMode.PICK_MODE) && current_age > 0
                && current_age < 100) {
            age_label = Integer.toString(current_age)
                    + Resource.getString("age");
        } else {
            age_label = "";
        }
        day_or_night = Resource.getString(day_birth ? "daytime" : "nighttime");
        String life_helper_key = Resource.getString("life_helper_key");
        String against_key = Resource.getString("against_key");
        for (int j = 0; j < (ChartMode.isChartMode(ChartMode.PICK_MODE) ? 1 : 2); j++) {
            boolean birth_data = j == 0;
            double[] sign_pos, sign_alt;
            int[] sign_state;
            String[] sign_status;
            astro_sign_data[j] = null;
            if (birth_data) {
                if (Resource.hasCustomData()) {
                    tab.appendLine(Resource.getString("mod_label") + ": "
                            + Resource.getModName());
                }
                if (show_gauquelin
                        || !ChartMode.isAstroMode(ChartMode.NATAL_MODE)
                        && !dual_or_single
                        || ChartMode.isAstroMode(ChartMode.COMPARISON_MODE)) {
                    tab.appendLine(ChartMode.getModeName(true, true));
                } else if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    tab.appendLine(Resource.getString("birth_chart"));
                }
                DataEntry entry = (return_mode
                        || ChartMode.isAstroMode(ChartMode.ALT_NATAL_MODE) || ChartMode
                        .isAstroMode(ChartMode.RELATIONSHIP_MODE)) ? now
                        : birth;
                boolean is_long_lat = updateLocation(entry.getCity(),
                        entry.getCountry());
                cal.getLocation(birth_loc);
                date_buf = entry.getBirthDay();
                String zone = entry.getZone();
                BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
                cal.setJulianDay(date_buf);
                if (ChartMode.isAstroMode(ChartMode.COMPARISON_MODE)
                        || ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE))
                    tab.setName(birth.getName(), birth.getSex(), false);
                else if (ChartMode.isMultipleMode(false))
                    tab.setName(getBirthName(), false);
                else
                    tab.setName(getBirthName(), getBirthSex(), false);
                sign_pos = birth_sign_pos;
                sign_state = birth_sign_state;
                sign_status = birth_sign_status;
                sign_alt = birth_sign_alt;
                String str;
                showLocationInfo(entry, is_long_lat, true, false, 0.0,
                        date_buf, birth_base_time);
                String solar_eclipse_info = getEclipseInfo(solar_eclipse, zone,
                        true, false);
                String lunar_eclipse_info = getEclipseInfo(lunar_eclipse, zone,
                        false, false);
                if (solar_eclipse_info != null || lunar_eclipse_info != null) {
                    if (solar_eclipse_info != null)
                        tab.appendLine(solar_eclipse_info);
                    if (lunar_eclipse_info != null)
                        tab.appendLine(lunar_eclipse_info);
                    tab.appendLine();
                }
                tab.appendLine(lunar_calendar
                        + ": "
                        + birth_poles[CH_YEAR]
                        + year_char
                        + " "
                        + (lunar_leap_month ? Resource.getString("leap") : "")
                        + BaseCalendar.chineseNumber(lunar_date[1],
                                !lunar_leap_month, false)
                        + month_char
                        + " "
                        + BaseCalendar
                                .chineseNumber(lunar_date[2], false, true)
                        + day_char + " "
                        + birth_poles[HOUR_POLE].substring(1, 2) + hour_char
                        + " (" + day_or_night + "),  "
                        + Resource.getString("sky_earths") + ": "
                        + birth_poles[CH_YEAR] + year_char + " "
                        + birth_poles[CH_MONTH] + month_char + " "
                        + birth_poles[DAY_POLE] + day_char + " "
                        + birth_poles[HOUR_POLE] + hour_char);
                tab.append(Resource.getString("eight_characters") + ": "
                        + birth_poles[YEAR_POLE] + year_char + " "
                        + birth_poles[MONTH_POLE] + month_char + " "
                        + birth_poles[DAY_POLE] + day_char + " "
                        + birth_poles[HOUR_POLE] + hour_char + " ["
                        + getYearSoundName(birth_poles[YEAR_POLE]) + "]");
                String birth_season = "";
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    tab.append(",   " + Resource.getString("weak")
                            + Resource.getString("zodiac_house") + ": "
                            + weak_house[YEAR_POLE] + weak_house[MONTH_POLE]
                            + weak_house[DAY_POLE] + weak_house[HOUR_POLE]
                            + ",   " + Resource.getString("solid")
                            + Resource.getString("zodiac_house") + ": "
                            + solid_house[YEAR_POLE] + solid_house[MONTH_POLE]
                            + solid_house[DAY_POLE] + solid_house[HOUR_POLE]);
                    tab.appendLine();
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        three_danger = computeThreeDanger();
                        if (three_danger != null) {
                            str = Resource.getString("mountain_name") + ": "
                                    + findMountainSign()
                                    + Resource.getString("confront");
                            if (three_danger[YEAR_POLE])
                                str += year_char;
                            if (three_danger[MONTH_POLE])
                                str += month_char;
                            if (three_danger[DAY_POLE])
                                str += day_char;
                            if (three_danger[HOUR_POLE])
                                str += hour_char;
                            str += Resource.getString("three_danger");
                            tab.appendLine(str);
                        }
                    }
                    birth_season = getBirthSeason(birth_adj_date);
                    if (!ChartMode.isChartMode(ChartMode.PICK_MODE))
                        tab.append(Resource.getString("birth_at") + ": ");
                    tab.appendLine(birth_season);
                    if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        int day = getChildLimit(false);
                        int[] period = BaseCalendar.timePeriod(date_buf, day);
                        str = "";
                        if (period[0] > 0)
                            str += Integer.toString(period[0]) + year_char;
                        if (period[1] > 0)
                            str += Integer.toString(period[1]) + month_char;
                        if (period[2] > 0)
                            str += Integer.toString(period[2]) + day_char;
                        child_limit = Resource.getString("child_limit") + ": "
                                + str;
                        tab.appendLine(child_limit);
                    }
                    tab.appendLine();
                    printSolarTerms(Resource.isExclusive()
                            || Resource.getPrefInt("show_new_moon") == 1);
                }
                tab.appendLine();
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    for (int i = 0; i < full_stellar_signs.length; i++) {
                        if (Calculate.isValid(life_sign_pos)) {
                            tab.append(full_stellar_signs[i]
                                    + ": "
                                    + cal.formatDegree(stellar_sign_pos[i],
                                            true, true));
                            if (Resource.getPrefInt("show_stellar_width") == 1) {
                                double width = stellar_sign_pos[(i == full_stellar_signs.length - 1) ? 0
                                        : (i + 1)]
                                        - stellar_sign_pos[i];
                                if (width < 0.0)
                                    width += 360.0;
                                tab.append(" ["
                                        + FileIO.formatDouble(width, 2, 2,
                                                true, false) + "]");
                            }
                        }
                        if ((i % 4) == 3)
                            tab.appendLine();
                        else
                            tab.append("  ");
                    }
                    tab.appendLine();
                    if (Resource.getPrefInt("show_stellar_width") == 1) {
                        for (int i = 0; i < full_zodiac.length; i++) {
                            tab.append(full_zodiac[i]
                                    + ": "
                                    + cal.formatDegree(i * 30.0,
                                            stellar_sign_pos,
                                            full_stellar_signs, true));
                            if ((i % 4) == 3)
                                tab.appendLine();
                            else
                                tab.append("  ");
                        }
                        tab.appendLine();
                        if (show_compass && Calculate.isValid(life_sign_pos)) {
                            for (int i = 0; i < full_stellar_signs.length; i++) {
                                tab.append(full_stellar_signs[i]
                                        + ": "
                                        + cal.formatDegree(compass_sign_pos[i],
                                                true, true));
                                double width = compass_sign_pos[(i == full_stellar_signs.length - 1) ? 0
                                        : (i + 1)]
                                        - compass_sign_pos[i];
                                if (width < 0.0)
                                    width += 360.0;
                                tab.append(" ["
                                        + FileIO.formatDouble(width, 2, 2,
                                                true, false) + "]");
                                if ((i % 4) == 3)
                                    tab.appendLine();
                                else
                                    tab.append("  ");
                            }
                            tab.appendLine();
                        }
                    }
                    str = ChartMode.getModeName(true, true);
                    if (str != null)
                        tab.appendLine(str);
                    tab.appendLine(ChartMode.getComputationMethod());
                    if (Resource.getPrefInt("use_equator") != 0)
                        tab.appendLine(Resource.getString("equator_sunrise"));
                    tab.appendLine();
                    if (show_house_system) {
                        tab.appendLine(ChartMode.getSystemName(ChartMode
                                .isChartMode(ChartMode.PICK_MODE) ? "pick_"
                                : ""));
                        showCuspData(birth_cusp, house_name);
                    }
                } else {
                    tab.appendLine();
                    if (ChartMode.isAstroMode(ChartMode.COMPOSITE_MODE)) {
                        is_long_lat = updateLocation(now.getCity(),
                                now.getCountry());
                        cal.getLocation(now_loc);
                        date_buf = now.getBirthDay();
                        zone = now.getZone();
                        BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
                        cal.setJulianDay(date_buf);
                        tab.setName(now.getName(), now.getSex(), false);
                        now_base_time = getAdjustDate(zone, now_loc[0],
                                date_buf, now_adj_date, now_dst_date);
                        showLocationInfo(now, is_long_lat, false, false, 0.0,
                                date_buf, now_base_time);
                    }
                    String s_str = ChartMode.getSidrealSystem();
                    if (s_str != null)
                        tab.appendLine(s_str);
                    tab.appendLine(ChartMode.getComputationMethod());
                    tab.appendLine();
                    tab.appendLine(ChartMode.getSystemName(""));
                    showCuspData(birth_cusp, cusps_name);
                }
                center_tip = "";
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    if (showStyles(good_styles, "good_styles")
                            && showStyles(bad_styles, "bad_styles"))
                        tab.appendLine();
                    str = Resource.getString("life_master")
                            + ": "
                            + cal.formatDegree(life_sign_pos, stellar_sign_pos,
                                    full_stellar_signs, "  ", false, true);
                    tab.append(str + "  ");
                    center_tip += str;
                    str = Resource.getString("self_master")
                            + ": "
                            + cal.formatDegree(self_sign_pos, stellar_sign_pos,
                                    full_stellar_signs, "  ", false, true);
                    tab.appendLine(str);
                    center_tip += "\n" + str;
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        center_tip += "\n"
                                + Resource.getString("mountain_name")
                                + ": "
                                + cal.formatDegree(mountain_pos
                                        - true_north_magnetic_shift,
                                        stellar_sign_pos, full_stellar_signs,
                                        "  ", true, false);
                        if (three_danger != null) {
                            center_tip += ",  "
                                    + Resource.getString("confront");
                            if (three_danger[YEAR_POLE])
                                center_tip += year_char;
                            if (three_danger[MONTH_POLE])
                                center_tip += month_char;
                            if (three_danger[DAY_POLE])
                                center_tip += day_char;
                            if (three_danger[HOUR_POLE])
                                center_tip += hour_char;
                            center_tip += Resource.getString("three_danger");
                        }
                    }
                    String alt_key = Resource.getString("alt_life_helper_key");
                    for (int iter = 0; iter < (ChartMode
                            .isChartMode(ChartMode.PICK_MODE) ? 3 : 2); iter++) {
                        String sign, name;
                        switch (iter) {
                            case 0:
                                sign = cal.getStarSign(life_sign_pos,
                                        stellar_sign_pos, full_stellar_signs);
                                name = Resource.getString("life_master_name");
                                break;
                            case 1:
                                sign = cal.getZodiac(life_sign_pos, true);
                                name = Resource.getString("life_sign_name");
                                break;
                            default:
                                sign = findMountainSign();
                                name = Resource.getString("mountain_name");
                                break;
                        }
                        String[] helper = Resource
                                .getStringArray(life_helper_key
                                        + sign.substring(1));
                        str = "";
                        for (int i = 0; i < helper.length; i++) {
                            if (i > 0)
                                str += ", ";
                            if (ChartMode.isChartMode(ChartMode.PICK_MODE))
                                str += alt_key.substring(i, i + 1);
                            else
                                str += life_helper_key.substring(i, i + 1);
                            str += ":" + helper[i];
                        }
                        tab.appendLine(name + ":" + sign + "  " + str);
                        center_tip += "\n" + name.substring(1) + ": " + str;
                    }
                    if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        String[] helper_season = Resource
                                .getStringArray("season_helper_season");
                        int helper_index = getSeasonIndex(birth_adj_date);
                        str = helper_season[helper_index].replaceFirst("-",
                                ": ");
                        tab.appendLine(str);
                        center_tip += "\n" + str;
                    }
                    center_tip += "\n" + Resource.getString("weak") + ": "
                            + weak_house[YEAR_POLE] + weak_house[MONTH_POLE]
                            + weak_house[DAY_POLE] + weak_house[HOUR_POLE]
                            + ", " + Resource.getString("solid") + ": "
                            + solid_house[YEAR_POLE] + solid_house[MONTH_POLE]
                            + solid_house[DAY_POLE] + solid_house[HOUR_POLE];
                    center_tip += "\n";
                }
                if (!dual_or_single) {
                    center_tip += lunar_calendar
                            + ": "
                            + birth_poles[CH_YEAR]
                            + year_char
                            + (lunar_leap_month ? Resource.getString("leap")
                                    : "")
                            + BaseCalendar.chineseNumber(lunar_date[1],
                                    !lunar_leap_month, false)
                            + month_char
                            + BaseCalendar.chineseNumber(lunar_date[2], false,
                                    true) + day_char + " (" + day_or_night
                            + ")\n";
                    if (solar_eclipse_info != null
                            || lunar_eclipse_info != null) {
                        if (solar_eclipse_info != null)
                            center_tip += solar_eclipse_info + "\n";
                        if (lunar_eclipse_info != null)
                            center_tip += lunar_eclipse_info + "\n";
                    }
                }
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    if (!ChartMode.isChartMode(ChartMode.PICK_MODE))
                        center_tip += Resource.getString("birth_at") + ": ";
                    center_tip += birth_season;
                    if (!ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                        if (!age_label.equals(""))
                            center_tip += "  " + age_label;
                        if (child_limit != null)
                            center_tip += "  " + child_limit;
                    }
                }
            } else {
                boolean is_long_lat = updateLocation(now.getCity(),
                        now.getCountry());
                cal.getLocation(now_loc);
                date_buf = (dual_or_single || ChartMode
                        .isAstroMode(ChartMode.COMPOSITE_MODE)) ? now
                        .getBirthDay() : now.getNowDay();
                String zone = now.getZone();
                BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
                cal.setJulianDay(date_buf);
                sign_pos = now_sign_pos;
                sign_state = now_sign_state;
                sign_status = now_sign_status;
                sign_alt = now_sign_alt;
                if (dual_or_single) {
                    tab.appendLine();
                    tab.appendLine();
                    if (ChartMode.isAstroMode(ChartMode.COMPARISON_MODE))
                        tab.setName(now.getName(), now.getSex(), false);
                    else
                        tab.appendLine(ChartMode.getModeName(true, true));
                    now_base_time = getAdjustDate(zone, now_loc[0], date_buf,
                            now_adj_date, now_dst_date);
                    boolean show_time = ChartMode
                            .isAstroMode(ChartMode.SECONDARY_PROGRESSION_MODE)
                            || ChartMode.isAstroMode(ChartMode.SOLAR_ARC_MODE);
                    showLocationInfo(now, is_long_lat, false, show_time,
                            now_ref_ut, date_buf, now_base_time);
                }
                if (show_now) {
                    tab.appendLine();
                    tab.appendLine(now_year + ":");
                    String str = now_poles[YEAR_POLE];
                    if (!age_label.equals(""))
                        str += "  " + age_label;
                    str += "  "
                            + lunar_calendar
                            + ": "
                            + (now_lunar_leap_month ? Resource
                                    .getString("leap") : "")
                            + BaseCalendar.chineseNumber(now_lunar_date[1],
                                    !now_lunar_leap_month, false)
                            + month_char
                            + BaseCalendar.chineseNumber(now_lunar_date[2],
                                    false, true) + day_char + "  ";
                    str += "limit"; // to be replaced later
                    tab.appendLine(str);
                    center_tip += "\n"
                            + now_year
                            + ": "
                            + now_poles[YEAR_POLE]
                            + "  "
                            + lunar_calendar
                            + ": "
                            + (now_lunar_leap_month ? Resource
                                    .getString("leap") : "")
                            + BaseCalendar.chineseNumber(now_lunar_date[1],
                                    !now_lunar_leap_month, false)
                            + month_char
                            + BaseCalendar.chineseNumber(now_lunar_date[2],
                                    false, true) + day_char + "\n";
                    int child_age_limit = getChildLimit(true);
                    if (current_age > 1 && current_age - 1 < child_age_limit)
                        center_tip += getChildLimit(current_age - 1, ": ")
                                + ", ";
                    center_tip += getSmallLimit(current_age, ": ") + ", "
                            + getMonthLimit(current_age, ": ");
                    String f_str = getFlyLimit(current_age - 1, ": ",
                            child_age_limit);
                    if (f_str != null)
                        center_tip += ", " + f_str;
                }
                if (single_wheel_mode)
                    showCuspData(now_cusp, cusps_name);
            }
            String[] status_seq = Resource.getStringArray("sign_status_seq");
            int[] seq_freq = new int[status_seq.length];
            Arrays.fill(seq_freq, 0);
            for (int i = 0; i < signs.length; i++) {
                if ((birth_data || dual_or_single || show_now)
                        && Calculate.isValid(sign_pos[i])) {
                    sign_status[i] = getSignStatus(signs[i], sign_pos[i],
                            status_seq, seq_freq);
                }
            }
            String sign_tip = "";
            if (!show_horiz) {
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    sign_tip += Resource.getString("life_master") + "     : ";
                    if (show_house_system)
                        sign_tip += house_gap;
                    sign_tip += cal.formatDegree(life_sign_pos,
                            stellar_sign_pos, full_stellar_signs, "  ", false,
                            true);
                    if (Calculate.isValid(self_sign_pos)) {
                        sign_tip += "\n" + Resource.getString("self_master")
                                + "     : ";
                        if (show_house_system)
                            sign_tip += house_gap;
                        sign_tip += cal.formatDegree(self_sign_pos,
                                stellar_sign_pos, full_stellar_signs, "  ",
                                false, true);
                    }
                }
                if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    now_sign_tip = Resource
                            .getString(quick_azimuth ? "quick_azimuth_method"
                                    : "local_horizon_method")
                            + "    " + azimuth_speed;
                    sign_tip = Resource.getString("tropical_method") + "\n"
                            + sign_tip;
                    if (quick_azimuth) {
                        tab.appendLine();
                        tab.appendLine(Resource
                                .getString("quick_azimuth_method")
                                + ChartMode.getSystemName("pick_"));
                        for (int i = 0; i < cusps_name.length; i++) {
                            String str = cusps_name[i]
                                    + ": "
                                    + cal.formatDegree(birth_cusp[i + 1], true,
                                            true);
                            tab.append(str);
                            if ((i % 2) == 1) {
                                tab.appendLine();
                            } else {
                                tab.append("  ");
                            }
                        }
                        tab.appendLine();
                    }
                    tab.appendLine(Resource.getString("tropical_method")
                            + "                              "
                            + formatStatusString("", status_seq, seq_freq)
                                    .replaceAll("@", "  ")
                            + house_gap
                            + Resource
                                    .getString(quick_azimuth ? "quick_azimuth_method"
                                            : "local_horizon_method") + "    "
                            + azimuth_speed);
                }
            }
            Hashtable table = birth_data ? birth_table : now_table;
            for (int i = 0; i < signs.length; i++) {
                int order = birth_data ? birth_sign_display_sort_orders[i]
                        : now_sign_display_sort_orders[i];
                if (!(birth_data || dual_or_single || show_now)
                        || !Calculate.isValid(sign_pos[order]))
                    continue;
                String prefix = signs[order];
                if (table != null) {
                    String key = (table != null) ? ((String) table
                            .get(signs[order])) : null;
                    if (key != null)
                        prefix += "(" + key + "): ";
                    else
                        prefix += "       : ";
                } else {
                    prefix += ": ";
                }
                String str, m_str, gauquelin;
                if (show_horiz) {
                    str = prefix
                            + cal.formatDegree(birth_sign_azimuth[order],
                                    birth_sign_alt[order], cal
                                            .getSpeedStateName(
                                                    sign_state[order], "  "))
                            + " ";
                    m_str = gauquelin = "";
                } else {
                    str = prefix;
                    if (show_house_system) {
                        int val = cal.getSignIndex(sign_pos[order], birth_cusp,
                                1);
                        if (val >= 1) {
                            str += "[" + house_name[val - 1] + "] ";
                        } else {
                            str += "   "
                                    + Resource.spacePostFilled("",
                                            house_name[0].length());
                        }
                    }
                    str += cal.formatDegree(sign_pos[order], stellar_sign_pos,
                            full_stellar_signs,
                            cal.getSpeedStateName(sign_state[order], "  "),
                            false, true)
                            + " ";
                    if (ChartMode.isChartMode(ChartMode.TRADITIONAL_MODE)
                            || ChartMode.isChartMode(ChartMode.SIDEREAL_MODE)) {
                        if (Calculate.isValid(sign_alt[order])) {
                            str += FileIO.formatDouble(sign_alt[order], 2, 1,
                                    true, true) + " ";
                        } else {
                            str += "      ";
                        }
                    }
                    m_str = show_aspects ? getAspects(
                            ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? "\n    "
                                    : "\n        ", order, birth_data)
                            : null;
                    if (m_str == null) {
                        m_str = "";
                        if (sign_state[order] == Calculate.SPEED_REVERSE
                                && !has_pos_adj) {
                            double ut_sav = cal.getJulianDayUT();
                            double ut = cal.computeSpeedTransit(planets[order],
                                    ut_sav, 0.0, false);
                            if (Calculate.isValid(ut)) {
                                cal.setJulianDay(ut);
                                double degree = cal.compute(planets[order]);
                                cal.setJulianDay(ut_sav);
                                m_str = "\n  "
                                        + Resource.getString("retreat_to")
                                        + ": "
                                        + cal.formatDegree(degree,
                                                stellar_sign_pos,
                                                full_stellar_signs, "  ",
                                                false, true);
                            }
                        }
                    }
                    if (table != null) {
                        String star_sign_key = Resource
                                .getString("star_sign_key");
                        LinkedList star_list = (LinkedList) table
                                .get(signs[order] + star_sign_key);
                        if (star_list != null) {
                            int count = 0;
                            for (ListIterator iter = star_list.listIterator(); iter
                                    .hasNext();) {
                                String val = (String) iter.next();
                                if (((count++) % 7) == 0)
                                    m_str += "\n  ";
                                else
                                    m_str += ", ";
                                m_str += val;
                            }
                        }
                    }
                    gauquelin = getGauquelin(order);
                }
                tip.addTip(order, birth_data, str + sign_status[order]
                        + gauquelin + m_str);
                if (birth_data && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    int index = cal.getSignIndex(sign_pos[order],
                            stellar_sign_pos, 0);
                    if (index >= 0) {
                        String stellar = full_stellar_signs[index];
                        String stellar_sign = stellar.substring(1);
                        for (int k = 0; k < full_stellar_signs.length; k++) {
                            int offset = (k == index) ? CONSTELLATION_MAIN_OFFSET
                                    : CONSTELLATION_SUB_OFFSET;
                            if (stellar_sign.equals(full_stellar_signs[k]
                                    .substring(1))) {
                                String t_str = tip.getTip(offset + k, true);
                                if (t_str != null) {
                                    t_str += ", " + signs[order];
                                } else {
                                    t_str = signs[order];
                                }
                                if (Resource.hasKey(against_key
                                        + stellar.substring(1))) {
                                    String[] against = Resource
                                            .getStringArray(against_key
                                                    + stellar.substring(1));
                                    for (int l = 0; l < against.length; l++) {
                                        String key = against[l];
                                        if (key.equals(signs[order])) {
                                            t_str += "["
                                                    + Resource
                                                            .getString("against_constellation")
                                                    + "]";
                                            break;
                                        }
                                    }
                                }
                                tip.addTip(offset + k, true, t_str);
                            }
                        }
                    }
                }
                sign_status[order] = formatStatusString(sign_status[order],
                        status_seq, seq_freq);
                String s_str = str;
                str += sign_status[order].replaceAll("@", "  ");
                if (!sign_tip.equals(""))
                    sign_tip += "\n";
                sign_tip += str + gauquelin;
                tab.append(str + gauquelin);
                if (!show_horiz && ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                    boolean astrolog_coord = Resource
                            .getPrefInt("astrolog_coord") != 0;
                    str = prefix
                            + cal.formatDegree(birth_sign_azimuth[order], "  ",
                                    show_compass ? compass_sign_pos : null,
                                    full_stellar_signs,
                                    ChartMode.mountainBased(degree_mode),
                                    astrolog_coord);
                    if (Calculate.isValid(birth_sign_alt[order])) {
                        str += " "
                                + FileIO.formatDouble(birth_sign_alt[order], 2,
                                        1, true, true);
                    }
                    tab.append("  " + str);
                    tip.addTip(order, false, str);
                    now_sign_tip += "\n" + str;
                }
                if (show_horiz) {
                    if (horiz_sign_data == null) {
                        horiz_sign_data = s_str + "|";
                    } else {
                        horiz_sign_data += s_str + "|";
                    }
                } else if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    if (astro_sign_data[j] == null) {
                        astro_sign_data[j] = str + "|";
                    } else {
                        astro_sign_data[j] += str + "|";
                    }
                    double pos = single_wheel_mode ? now_sign_pos[order]
                            : birth_sign_pos[order];
                    if (single_wheel_mode && !birth_data || !single_wheel_mode) {
                        astro_elemental[cal.getElementalIndex(pos)]++;
                        astro_elemental_state[cal.getElementalStateIndex(pos)]++;
                    }
                }
                tab.appendLine();
            }
            String sign_status_fill = formatStatusString("", status_seq,
                    seq_freq);
            if (birth_data) {
                birth_sign_status_fill = sign_status_fill;
                birth_sign_tip = sign_tip;
            } else {
                if (!dual_ring && ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    tab.appendLine();
                    tab.appendLine();
                    String[] elementals = Resource
                            .getStringArray("elemental_names");
                    for (int i = 0; i < 4; i++) {
                        tab.append(elementals[i] + ": "
                                + FileIO.formatInt(astro_elemental[i], 2) + " ");
                    }
                    tab.appendLine();
                    String[] states = Resource
                            .getStringArray("elemental_states");
                    for (int i = 0; i < 3; i++) {
                        tab.append(states[i] + ": "
                                + FileIO.formatInt(astro_elemental_state[i], 2)
                                + " ");
                    }
                }
                if (show_aspects) {
                    Object[] aspects = getAspectsTable(
                            single_wheel_mode ? now_sign_pos : birth_sign_pos,
                            0, "$", true);
                    if (aspects != null) {
                        tab.appendLine();
                        tab.appendLine();
                        String str = (String) aspects[0];
                        if (dual_ring) {
                            String title = ChartMode
                                    .isAstroMode(ChartMode.COMPARISON_MODE) ? now
                                    .getName() : ChartMode.getModeName(true,
                                    true);
                            int num_col = str.substring(str.indexOf("$") + 1)
                                    .indexOf("$");
                            int gap = num_col - 2 * title.length();
                            title = Resource.spacePreFilled("", gap / 2)
                                    + title;
                            tab.appendLine(title);
                        }
                        StringTokenizer st = new StringTokenizer(str, "$");
                        int num_row = st.countTokens();
                        String title = ChartMode
                                .isAstroMode(ChartMode.COMPARISON_MODE) ? birth
                                .getName() : Resource.getString("birth_chart");
                        int pos = -(num_row - title.length() - 2) / 2 - 2;
                        while (st.hasMoreTokens()) {
                            str = st.nextToken();
                            if (dual_ring) {
                                if (pos < 0 || pos >= title.length()) {
                                    str = "  " + str;
                                } else {
                                    String s = title.substring(pos, pos + 1);
                                    if (FileIO.isAsciiString(s, true))
                                        s += " ";
                                    str = s + str;
                                }
                                pos++;
                            }
                            tab.appendLine(str);
                        }
                    }
                }
                now_sign_tip = sign_tip;
            }
            if (!show_horiz && show_fixstar) {
                boolean has_fixstar = false;
                for (int i = 0; i < fixstar_names.length; i++) {
                    if (!Calculate.isValid(fixstar_sign_pos[i]))
                        continue;
                    String e_str = (explain_star && Resource
                            .hasKey(fixstar_signs[i] + explain_fixstar)) ? Resource
                            .getString(fixstar_signs[i] + explain_fixstar) : "";
                    tip.addTip(FIXSTAR_OFFSET + i, true, fixstar_signs[i]
                            + ": " + e_str);
                    if (!Calculate.isValid(fixstar_sign_azimuth[i]))
                        continue;
                    has_fixstar = true;
                    boolean astrolog_coord = Resource
                            .getPrefInt("astrolog_coord") != 0;
                    String prefix = fixstar_signs[i];
                    if (table != null) {
                        prefix += "     : ";
                    } else {
                        prefix += ": ";
                    }
                    String str = prefix
                            + cal.formatDegree(fixstar_sign_pos[i],
                                    stellar_sign_pos, full_stellar_signs, "  ",
                                    false, true) + "   "
                            + birth_sign_status_fill.replaceAll("@", "  ");
                    tab.append(str);
                    str = prefix
                            + cal.formatDegree(fixstar_sign_azimuth[i], "  ",
                                    null, null,
                                    ChartMode.mountainBased(degree_mode),
                                    astrolog_coord);
                    if (Calculate.isValid(fixstar_sign_alt[i])) {
                        str += " "
                                + FileIO.formatDouble(fixstar_sign_alt[i], 2,
                                        1, true, true);
                    }
                    if (!e_str.equals("")) {
                        e_str = "\n  "
                                + Resource.getSpaceFilled(fixstar_signs[i])
                                + e_str;
                    }
                    tip.addTip(FIXSTAR_OFFSET + i, false, str + e_str);
                    tab.appendLine(str);
                    now_sign_tip += "\n" + str;
                }
                if (has_fixstar) {
                    tab.appendLine();
                    showStarList(birth_table, fixstar_table, mountain_signs,
                            null, explain_fixstar, false);
                }
            }
            if (birth_data || show_now) {
                if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    tab.appendLine();
                    if (table != null) {
                        String star_sign_key = Resource
                                .getString("star_sign_key");
                        for (int i = 0; i < signs.length; i++) {
                            int order = sign_display_orders[i];
                            if (!Calculate.isValid(sign_pos[order]))
                                continue;
                            LinkedList star_list = (LinkedList) table
                                    .get(signs[order] + star_sign_key);
                            if (star_list != null) {
                                int count = 0;
                                tab.append(signs[order] + ": ");
                                for (ListIterator iter = star_list
                                        .listIterator(); iter.hasNext();) {
                                    String val = (String) iter.next();
                                    if (count++ == 0)
                                        tab.append(val);
                                    else
                                        tab.append(", " + val);
                                }
                                tab.appendLine();
                            }
                        }
                    }
                    tab.appendLine();
                    tab.appendLine(Resource.getString("star_sign_key"));
                }
                if (birth_data && !ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                    day_table = getStarSigns(birth_poles, birth_sign_pos, true);
                    computeEightCharData();
                }
            }
            if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                String[] seq = Resource.getStringArray("star_sign_seq");
                showStarList(table, master_table, seq, year_signs, explain,
                        !birth_data);
            }
        }
    }

    private void showCuspData(double[] cusp, String[] name)
    {
        astro_cusp_data = "";
        int width = 0;
        for (int i = 0; i < name.length; i++)
            width = Math.max(width, name[i].length());
        for (int i = 0; i < name.length; i++) {
            String str = name[i] + ": "
                    + cal.formatDegree(cusp[i + 1], true, true);
            tab.append(str);
            astro_cusp_data += str + "|";
            if ((i % 2) == 1) {
                tab.appendLine();
            } else {
                tab.append("  ");
            }
        }
        tab.appendLine();
    }

    private void showStarList(Hashtable table, Hashtable m_table, String[] seq,
            String[] desc, String suffix, boolean check_now)
    {
        if (table == null)
            return;
        int index = ((int) (life_sign_pos / 30.0)) + 2;
        String blank = Resource.spacePreFilled("", seq[0].length() * 2 + 8);
        for (int i = 0; i < seq.length; i++) {
            LinkedList head = (LinkedList) table.get(seq[i]);
            if (head != null) {
                String[] stars = expandStarList(head, m_table, suffix, false,
                        check_now);
                String str;
                if (desc != null) {
                    str = seq[i] + "(" + desc[(index + i) % 12] + "): ";
                } else {
                    str = seq[i] + "     : ";
                }
                if (stars != null) {
                    table.put(seq[i], stars);
                    int len = stars.length / 2;
                    int cnt = 0;
                    for (int k = 0; k < len; k++) {
                        int d_cnt = stars[k].length();
                        String stars_explain = explain_star ? stars[len + k]
                                : null;
                        if (stars_explain != null)
                            d_cnt += stars_explain.length() + explain_length;
                        if (cnt > 0 && cnt + d_cnt + 2 > MAX_DATA_COUNT) {
                            tab.appendLine(str);
                            str = blank;
                            cnt = d_cnt;
                        } else {
                            if (k != 0) {
                                str += ", ";
                                cnt += 2;
                            }
                            cnt += d_cnt;
                        }
                        str += stars[k];
                        if (stars_explain != null)
                            str += explain_prefix + stars[len + k]
                                    + explain_suffix;
                    }
                } else {
                    table.remove(seq[i]);
                }
                tab.appendLine(str);
            }
        }
    }

    private int getSeasonIndex(int[] date)
    {
        if (solar_terms == null)
            return 0;
        double ut = Calculate.getJulianDayUT(date);
        for (int i = 2; i <= 24; i++) {
            if (ut >= solar_terms[i - 1] && ut < solar_terms[i])
                return (i - 2) / 2;
        }
        return 11;
    }

    private void showLocationInfo(DataEntry entry, boolean is_long_lat,
            boolean show_rise_set, boolean show_ut, double ut, int[] date,
            String adj_time)
    {
        if (is_long_lat) {
            tab.append(Resource.getString("location")
                    + ": "
                    + City.formatLongLatitude(cal.getLongitude(), true, true,
                            false)
                    + ", "
                    + City.formatLongLatitude(cal.getLatitude(), false, true,
                            false));
            if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                tab.append(", "
                        + Resource.getString("magnetic_shift").substring(0, 2)
                        + ":"
                        + City.formatLongLatitude(magnetic_shift, true, true,
                                false) + magnetic_shift_message);
            }
            tab.append(", " + entry.getZone());
        } else {
            tab.append(Resource.getString("location") + ": " + entry.getCity()
                    + ", " + entry.getCountry());
            tab.append("    ["
                    + City.formatLongLatitude(cal.getLongitude(), true, true,
                            false)
                    + ", "
                    + City.formatLongLatitude(cal.getLatitude(), false, true,
                            false));
            if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
                tab.append(", "
                        + Resource.getString("magnetic_shift").substring(0, 2)
                        + ":"
                        + City.formatLongLatitude(magnetic_shift, true, true,
                                false) + magnetic_shift_message);
            }
            tab.append("]");
        }
        tab.appendLine();
        if (ChartMode.isChartMode(ChartMode.PICK_MODE)) {
            tab.appendLine(Resource.getString("mountain_degree")
                    + ": "
                    + cal.formatDegree(
                            mountain_pos - true_north_magnetic_shift,
                            stellar_sign_pos, full_stellar_signs, "  ", true,
                            false));
        }
        String st_time = BaseCalendar.formatDate(entry.getZone(), date, null,
                false, false);
        String dst_time = BaseCalendar.formatDate(entry.getZone(), date, null,
                true, false);
        String prefix;
        if (!dst_time.equals(st_time)) {
            prefix = Resource.getString("daylight_saving")
                    + Resource.getString("time") + ": ";
            tab.appendLine(prefix + dst_time);
            pole_tab.appendLine(prefix + dst_time);
        }
        prefix = Resource.getString("standard") + Resource.getString("time")
                + ": ";
        tab.appendLine(prefix + st_time);
        pole_tab.appendLine(prefix + st_time);
        if (time_adjust > 0) {
            prefix = timeAdjustPrefix() + ": ";
            tab.appendLine(prefix + adj_time);
            pole_tab.appendLine(prefix + adj_time);
        }
        if (show_rise_set) {
            for (int i = SUN; i <= MOON; i++) {
                if (birth_rise_time[i] != null && birth_set_time[i] != null) {
                    if (i > SUN)
                        tab.append(",  ");
                    else
                        tab.append(prefix);
                    tab.append(signs[i] + Resource.getString("rise_time")
                            + ": " + birth_rise_time[i] + ",  " + signs[i]
                            + Resource.getString("fall_time") + ": "
                            + birth_set_time[i]);
                }
            }
            tab.appendLine();
        }
        double val;
        if (show_ut) {
            val = ut;
            date = new int[5];
            Calculate.getDateFromJulianDayUT(val, date);
        } else {
            val = cal.getJulianDayUT();
        }
        String ut_time = BaseCalendar.formatDate("GMT", date, null, false,
                false);
        tab.append("[" + ut_time + ", ");
        DecimalFormat format = new DecimalFormat(".0000");
        tab.appendLine("UT: " + format.format(val) + ", ET: "
                + format.format(Calculate.getJulianDayFromUT(val)) + "]");
        tab.appendLine();
    }

    private String getAdjustDate(String zone, double longitude, int[] date,
            int[] base_date, int[] dst_date)
    {
        String str;
        BaseCalendar.formatDate(zone, date, dst_date, true, false);
        switch (time_adjust) {
            case 2:
            {
                str = BaseCalendar.formatDate(longitude, date, base_date,
                        cal.getLATDateFromDate(date), false, false);
            }
                break;
            case 1:
                str = BaseCalendar.formatDate(longitude, date, base_date, 0.0,
                        false, false);
                break;
            default:
                str = BaseCalendar.formatDate(zone, date, base_date, false,
                        false);
                break;
        }
        return str;
    }

    private boolean showStyles(LinkedList styles, String style_name)
    {
        if (styles == null)
            return false;
        String style_tip = Resource.getString(style_name) + ": ";
        tab.append(style_tip);
        int i = 0;
        for (ListIterator iter = styles.listIterator(); iter.hasNext();) {
            if (i > 0) {
                if ((i % 5) == 0) {
                    tab.appendLine();
                    tab.append("          ");
                } else {
                    tab.append(" ");
                }
                if ((i % 4) == 0) {
                    style_tip += "\n          ";
                } else {
                    style_tip += " ";
                }
            }
            String key = (String) iter.next();
            tab.append(key);
            style_tip += key;
            i++;
        }
        tab.appendLine();
        style_tip += "\n";
        if (i > 0)
            center_tip += style_tip;
        return true;
    }

    private String getEclipseInfo(SearchRecord data, String zone,
            boolean solar, boolean short_date)
    {
        if (data == null)
            return null;
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(data.getTime(), date);
        String str;
        if (short_date) {
            int[] l_date = new int[5];
            BaseCalendar.formatDate(zone, date, l_date, true, false);
            str = getShortDate(l_date) + " "
                    + BaseCalendar.formatDate(l_date, true, false);
        } else {
            str = BaseCalendar.formatDate(zone, date, null, true, false);
        }
        return signs[solar ? SUN : MOON] + data.getType() + ": " + str;
    }

    private String timeAdjustPrefix()
    {
        String str;
        if (time_adjust > 0) {
            if (time_adjust > 1) {
                str = Resource.getString("apparent_solar");
            } else {
                str = Resource.getString("longitude")
                        + Resource.getString("degree")
                        + Resource.getString("corrected");
            }
            str += Resource.getString("time");
        } else {
            str = Resource.getString("standard") + Resource.getString("time");
        }
        return str;
    }

    private void initSignDisplay()
    {
        String prefix = ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? "astro_"
                : "";
        sign_display_orders = new int[signs.length];
        sign_display = new int[signs.length];
        for (int i = 0; i < signs.length; i++) {
            sign_display_orders[i] = i;
            sign_display[i] = -1;
        }
        Resource.getIntArray(prefix + "sign_display_orders",
                sign_display_orders);
        birth_sign_display_sort_orders = (int[]) sign_display_orders.clone();
        now_sign_display_sort_orders = (int[]) sign_display_orders.clone();
        Resource.getIntArray(prefix + "sign_display", sign_display);
        if (num_asteroids > 0 && prefix.equals("astro_")) {
            for (int j = 0; j < num_asteroids; j++)
                sign_display[sign_display.length - j - 1] = 1;
        }
        int[] data = Resource.getPrefIntArray(prefix + "sign_display");
        if (data != null && data.length == sign_display.length)
            sign_display = data;
        for (int i = 0; i < sign_computation_type.length; i++) {
            if (sign_computation_type[i] < 0)
                sign_display[i] = -1;
        }
    }

    public void initAspects()
    {
        String prefix = ChartMode.getModePrefix();
        aspects_orb = Resource.getPrefDoubleArray(prefix, "aspects_orb");
        aspects_display = Resource.getPrefIntArray(prefix, "aspects_display");
    }

    private boolean[] computeThreeDanger()
    {
        String key = Resource.getString("three_danger");
        String mountain = findMountainSign().substring(0, 1);
        boolean[] danger = null;
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
            String[] data = Resource.getStringArray(key
                    + birth_poles[i].substring(1));
            for (int k = 0; k < data.length; k++) {
                if (data[k].equals(mountain)) {
                    if (danger == null)
                        danger = new boolean[birth_poles.length];
                    danger[i] = true;
                    break;
                }
            }
        }
        return danger;
    }

    private String findMountainSign()
    {
        double pos = 337.5 + mountain_pos - true_north_magnetic_shift
                - mountain_offset;
        if (pos < 0.0)
            pos += 360.0;
        if (pos >= 360.0)
            pos -= 360.0;
        return full_mountain_signs[((int) (pos / 15.0)) % 24];
    }

    private void computeEightCharData()
    {
        String day_pole_key = birth_poles[DAY_POLE].substring(0, 1);
        if (Resource.hasKey("alt_day_pole_key")) {
            alt_pole_data = Resource.getStringArray("alt_day_pole_key");
            alt_pole_data_offset = FileIO.parseInt(alt_pole_data[1], 0, true);
            alt_pole_data = Resource.getStringArray(alt_pole_data[0]
                    + day_pole_key);
        }
        pole_tab.appendLine(lunar_calendar
                + ": "
                + birth_poles[CH_YEAR]
                + year_char
                + " "
                + (lunar_leap_month ? Resource.getString("leap") : "")
                + BaseCalendar.chineseNumber(lunar_date[1], !lunar_leap_month,
                        false) + month_char + " "
                + BaseCalendar.chineseNumber(lunar_date[2], false, true)
                + day_char + " " + birth_poles[HOUR_POLE].substring(1, 2)
                + hour_char + " (" + day_or_night + ")");
        String birth_season = getBirthSeason(birth_adj_date);
        if (!ChartMode.isChartMode(ChartMode.PICK_MODE))
            pole_tab.append(Resource.getString("birth_at") + ": ");
        pole_tab.appendLine(birth_season);
        pole_tab.appendLine();
        String fill = (alt_pole_data == null) ? "" : "    ";
        pole_tab.appendLine("      "
                + getLongLifeName(birth_poles[YEAR_POLE].substring(1, 2),
                        day_pole_key, 2)
                + "    "
                + fill
                + getLongLifeName(birth_poles[MONTH_POLE].substring(1, 2),
                        day_pole_key, 2)
                + "    "
                + fill
                + getLongLifeName(birth_poles[DAY_POLE].substring(1, 2),
                        day_pole_key, 2)
                + "    "
                + fill
                + getLongLifeName(birth_poles[HOUR_POLE].substring(1, 2),
                        day_pole_key, 2));
        pole_tab.appendLine("      "
                + getTenGodName(birth_poles[YEAR_POLE].substring(0, 1),
                        day_pole_key, true)
                + "    "
                + getTenGodName(birth_poles[MONTH_POLE].substring(0, 1),
                        day_pole_key, true)
                + "    "
                + Resource.getString("self_label")
                + "    "
                + fill
                + getTenGodName(birth_poles[HOUR_POLE].substring(0, 1),
                        day_pole_key, true));
        pole_tab.appendLine("      " + getYearSoundName(birth_poles[YEAR_POLE])
                + "  " + fill + getYearSoundName(birth_poles[MONTH_POLE])
                + "  " + fill + getYearSoundName(birth_poles[DAY_POLE]) + "  "
                + fill + getYearSoundName(birth_poles[HOUR_POLE]));
        pole_tab.appendLine();
        String weak = computeWeakHouse(birth_poles[DAY_POLE], true);
        String weak_pole = Resource.getString("weak_pole");
        pole_tab.appendLine(Resource
                .getString(getBirthSex() ? "eight_char_male"
                        : "eight_char_female")
                + ": "
                + birth_poles[YEAR_POLE]
                + year_char
                + "  "
                + fill
                + birth_poles[MONTH_POLE]
                + month_char
                + "  "
                + fill
                + birth_poles[DAY_POLE]
                + day_char
                + "  "
                + fill
                + birth_poles[HOUR_POLE]
                + hour_char
                + "    ["
                + weak
                + weak_pole.substring(0, 1) + "]");
        {
            String str = "      ";
            for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
                int index = weak.indexOf(birth_poles[i].substring(1));
                str += (index >= 0) ? weak_pole : "    ";
                if (i < HOUR_POLE)
                    str += "    ";
            }
            if (!str.trim().equals(""))
                pole_tab.appendLine(str);
        }
        if (three_danger != null) {
            String key = Resource.getString("three_danger");
            String empty = "    ";
            pole_tab.appendLine("      "
                    + (three_danger[YEAR_POLE] ? key : empty) + "    " + fill
                    + (three_danger[MONTH_POLE] ? key : empty) + "    " + fill
                    + (three_danger[DAY_POLE] ? key : empty) + "    " + fill
                    + (three_danger[HOUR_POLE] ? key : empty));
        }
        pole_tab.appendLine();
        for (int k = 0; k < 3; k++) {
            boolean new_line = true;
            for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
                String str = getEarthGodSeq(birth_poles[i].substring(1, 2));
                if (str.length() > k) {
                    String key = str.substring(k, k + 1);
                    if (new_line) {
                        new_line = false;
                        pole_tab.append("      ");
                        for (int l = YEAR_POLE; l < i; l++)
                            pole_tab.append("        " + fill);
                    }
                    pole_tab.append(key
                            + getTenGodName(key, day_pole_key, true));
                } else if (!new_line) {
                    pole_tab.append("      " + fill);
                }
                if (!new_line) {
                    if (i < HOUR_POLE) {
                        pole_tab.append("  ");
                    } else {
                        pole_tab.appendLine();
                    }
                }
            }
        }
        pole_tab.appendLine();
        {
            for (int iter = 0; iter < 2; iter++) {
                Hashtable table = (iter == 0) ? birth_table : day_table;
                if (table == null)
                    continue;
                String[][] array = new String[6][];
                int length = 0;
                for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
                    String key = birth_poles[i].substring(1);
                    LinkedList head = (LinkedList) table.get(key);
                    if (head != null) {
                        array[i] = expandStarList(head, master_table, explain,
                                true, false);
                        length = Math.max(length, array[i].length);
                    }
                }
                if (length <= 1)
                    continue;
                pole_tab.appendLine(Resource
                        .getString((iter == 0) ? "star_sign_key"
                                : "day_pole_key")
                        + ":");
                for (int j = 1; j < length; j++) {
                    String line = "      ";
                    for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
                        String str = "";
                        String[] stars = array[i];
                        if (stars != null && j < stars.length)
                            str += stars[j];
                        else
                            str += "    ";
                        if (i < HOUR_POLE)
                            str += "    ";
                        line += str;
                    }
                    pole_tab.appendLine(line);
                }
            }
            pole_tab.appendLine();
        }
        int y_index = FileIO.getArrayIndex(birth_poles[YEAR_POLE], year_names);
        int m_index = FileIO.getArrayIndex(birth_poles[MONTH_POLE], year_names);
        int h_index = FileIO.getArrayIndex(birth_poles[HOUR_POLE], year_names);
        int dir = getBirthSex() ? 1 : -1;
        if ((y_index % 2) == 1)
            dir = (dir < 0) ? 1 : -1;
        double gap = getBigCycleStartGap(birth_adj_date, dir);
        int[] s_date = getBigCycleStartDate(birth_adj_date, gap);
        for (int iter = 0; iter < 4; iter++) {
            if (alt_pole_data == null && iter == 2)
                continue;
            pole_tab.append("      ");
            for (int i = 0; i < 8; i++) {
                int index = m_index + dir * (i + 1);
                while (index >= 60)
                    index -= 60;
                while (index < 0)
                    index += 60;
                String str;
                switch (iter) {
                    case 0:
                        str = getLongLifeName(
                                year_names[index].substring(1, 2),
                                day_pole_key, 0);
                        break;
                    case 1:
                        str = getTenGodName(year_names[index].substring(0, 1),
                                day_pole_key, false);
                        break;
                    case 2:
                        str = getAltPoleName(year_names[index].substring(0, 1),
                                day_pole_key).substring(1);
                        break;
                    default:
                        str = getYearSoundName(year_names[index]);
                        break;
                }
                for (int n = 3 - str.length(); n > 0; n--)
                    str += "  ";
                pole_tab.append(str);
                if (i < 7)
                    pole_tab.append(" ");
            }
            pole_tab.appendLine();
        }
        pole_tab.appendLine();
        pole_tab.append(Resource.getString("big_cycle") + ": ");
        for (int iter = 0; iter < 8; iter++) {
            int index = m_index + dir * (iter + 1);
            while (index >= 60)
                index -= 60;
            while (index < 0)
                index += 60;
            pole_tab.append(year_names[index]);
            if (iter < 7)
                pole_tab.append("   ");
        }
        pole_tab.appendLine();
        String age = Resource.getString("age");
        int age_offset = last_year_birth ? 2 : 1;
        pole_tab.append("      ");
        for (int iter = 0; iter < 8; iter++) {
            int year = s_date[0] + iter * 10;
            pole_tab.append(Resource.spacePreFilled(
                    Integer.toString(year - birth_adj_date[0] + age_offset), 2)
                    + age);
            if (iter < 7)
                pole_tab.append("   ");
        }
        pole_tab.appendLine();
        pole_tab.append(Resource.getString("start_at") + ":");
        DecimalFormat format = new DecimalFormat("0000");
        for (int iter = 0; iter < 8; iter++) {
            String str = format.format(s_date[0] + iter * 10);
            if (str.length() < 5)
                str = " " + str;
            pole_tab.append(str);
            if (iter < 7)
                pole_tab.append("  ");
        }
        pole_tab.appendLine();
        pole_tab.append(now_year + ": ");
        int n_index = cal.getChineseYear(s_date[0]) - 1;
        for (int y_iter = 0; y_iter < 10; y_iter++) {
            for (int iter = 0; iter < 8; iter++) {
                int index = n_index + iter * 10 + y_iter;
                index = index % 60;
                pole_tab.append(year_names[index]);
                if (iter < 7)
                    pole_tab.append("   ");
            }
            pole_tab.appendLine();
            if (y_iter < 9)
                pole_tab.append("      ");
        }
        pole_tab.append(Resource.getString("end_at") + ":");
        for (int iter = 0; iter < 8; iter++) {
            String str = format.format(s_date[0] + iter * 10 + 9);
            if (str.length() < 5)
                str = " " + str;
            pole_tab.append(str);
            if (iter < 7)
                pole_tab.append("  ");
        }
        pole_tab.appendLine();
        pole_tab.appendLine();
        String key = getBeforeBirthName(birth_poles[MONTH_POLE]);
        pole_tab.appendLine(Resource.getString("before_birth") + ": " + key
                + "  " + getYearSoundName(key));
        pole_tab.appendLine(Resource.getString("exchange_date") + ": "
                + Integer.toString(s_date[0]) + year_char
                + Integer.toString(s_date[1]) + month_char
                + Integer.toString(s_date[2]) + day_char
                + Integer.toString(s_date[3]) + hour_char);
        pole_tab.appendLine();
        int num_year = s_date[0] - birth_adj_date[0] + age_offset - 1;
        pole_tab.append("      ");
        for (int iter = 0; iter <= num_year; iter++) {
            int index = h_index + dir * (iter + 1);
            while (index >= 60)
                index -= 60;
            while (index < 0)
                index += 60;
            String str = getLongLifeName(year_names[index].substring(1, 2),
                    day_pole_key, 0);
            for (int n = 3 - str.length(); n > 0; n--)
                str += "  ";
            pole_tab.append(str);
            if (iter < num_year)
                pole_tab.append(" ");
        }
        pole_tab.appendLine();
        pole_tab.append(Resource.getString("small_cycle") + ": ");
        for (int iter = 0; iter <= num_year; iter++) {
            int index = h_index + dir * (iter + 1);
            while (index >= 60)
                index -= 60;
            while (index < 0)
                index += 60;
            pole_tab.append(year_names[index]);
            if (iter < num_year)
                pole_tab.append("   ");
        }
        pole_tab.appendLine();
        pole_tab.appendLine();
        pole_tab.append("      ");
        for (int iter = 0; iter <= num_year; iter++) {
            int index = y_index + iter;
            while (index >= 60)
                index -= 60;
            while (index < 0)
                index += 60;
            String str = getLongLifeName(year_names[index].substring(1, 2),
                    day_pole_key, 0);
            for (int n = 3 - str.length(); n > 0; n--)
                str += "  ";
            pole_tab.append(str);
            if (iter < num_year)
                pole_tab.append(" ");
        }
        pole_tab.appendLine();
        pole_tab.append(now_year + ": ");
        n_index = cal.getChineseYear(birth_adj_date[0]) - age_offset;
        if (n_index < 0)
            n_index += 60;
        for (int iter = 0; iter <= num_year; iter++) {
            pole_tab.append(year_names[(n_index + iter) % 60]);
            if (iter < num_year)
                pole_tab.append("   ");
        }
        pole_tab.appendLine();
        pole_tab.append(Resource.getString("weak") + age + ": ");
        for (int iter = 0; iter <= num_year; iter++) {
            pole_tab.append(Resource.spacePreFilled(Integer.toString(iter + 1),
                    2) + age);
            if (iter < num_year)
                pole_tab.append("   ");
        }
        pole_tab.appendLine();
        pole_tab.append("     ");
        for (int iter = 0; iter <= num_year; iter++) {
            String str = format.format(birth_adj_date[0] + iter - age_offset
                    + 1);
            if (str.length() < 5)
                str = " " + str;
            pole_tab.append(str);
            if (iter < num_year)
                pole_tab.append("  ");
        }
        pole_tab.appendLine();
    }

    private double computeLifeSign(int[] date, double[] cusp)
    {
        double sun_pos = computePlanet(SUN);
        if (!Calculate.isValid(sun_pos))
            return sun_pos;
        double pos;
        if (Resource.getPrefInt("life_mode") != 0 && Calculate.isValid(cusp[1])) {
            pos = cusp[1];
            if (Resource.getPrefInt("astro_snap_to_sun_pos") == 0)
                return pos;
        } else {
            double adj_ut = Calculate.getJulianDayUT(snapToSignStart(date))
                    - Calculate.getJulianDayUT(snapToSignStart(birth_adj_date));
            pos = City.normalizeDegree(sun_pos - adj_ut * 360.0); // forward
        }
        return (sun_pos % 30) + (((int) pos) / 30) * 30;
    }

    private double computeSelfSign(int[] moon_rise_date, int[] sun_set_date)
    {
        int self_mode = Resource.getPrefInt("self_mode");
        double moon_pos = computePlanet(MOON);
        if (self_mode == 0 || !Calculate.isValid(moon_pos))
            return moon_pos;
        double adj_ut;
        if (self_mode == 1) {
            adj_ut = Calculate.getJulianDayUT(snapToSignStart(sun_set_date))
                    - Calculate.getJulianDayUT(snapToSignStart(birth_adj_date));
        } else {
            adj_ut = Calculate.getJulianDayUT(snapToSignStart(moon_rise_date))
                    - Calculate.getJulianDayUT(snapToSignStart(birth_adj_date));
        }
        double pos = moon_pos + adj_ut * 360.0; // backward
        if (pos < 0.0)
            pos += 360.0;
        else if (pos >= 360.0)
            pos -= 360.0;
        return (moon_pos % 30) + (((int) pos) / 30) * 30;
    }

    private double computePlanet(int planet_no)
    {
        double pos;
        if (use_primary_speed) {
            pos = birth_sign_pos[planet_no];
            if (!Calculate.isValid(pos))
                return Calculate.INVALID;
            if (equatorial_orbit) {
                pos = City.normalizeDegree(135.0
                        - birth_sign_azimuth[planet_no] + magnetic_shift);
                cal.setEquOrbitData(planets[planet_no], primary_speed,
                        birth_ref_ut, pos, birth_sign_alt[planet_no]);
            } else {
                cal.setOrbitData(primary_speed, birth_ref_ut, pos);
            }
            return cal.compute(-1);
        }
        switch (sign_computation_type[planet_no]) {
            case 0:
                pos = cal.compute(planets[planet_no]);
                break;
            case -1:
                return Calculate.INVALID;
            default:
                setOrbitData(planet_no);
                pos = cal.compute(-1);
                break;
        }
        pos = City.normalizeDegree(pos + sign_pos_shift[planet_no]);
        return pos;
    }

    private void computeGauquelin(int planet_no, double[] sign_region,
            double[] sign_pos)
    {
        if (sign_computation_type[planet_no] != 0
                || sign_opposite[planet_no] > 0)
            return;
        sign_region[planet_no] = cal.computeGauquelin(planets[planet_no]);
        if (sign_region[planet_no] != Calculate.INVALID) {
            sign_pos[planet_no] = City.normalizeDegree(first_cusp_pos
                    - (sign_region[planet_no] - 1.0) * 10.0);
        }
    }

    private double computeOpposite(int planet_no, double[] sign_pos)
    {
        double degree = sign_pos[sign_opposite[planet_no]] - 180.0;
        degree += sign_pos_shift[planet_no]
                - sign_pos_shift[sign_opposite[planet_no]];
        return City.normalizeDegree(degree);
    }

    private int getSpeedState(int planet_no)
    {
        int state;
        if (!use_primary_speed && sign_computation_type[planet_no] == 0) {
            if (!ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
                if (planet_no == SUN || planet_no == MOON) {
                    state = cal.getEclipseState(planet_no == SUN);
                } else if (planet_no >= VENUS && planet_no <= SATURN)
                    state = cal.getSpeedState(planets[planet_no], planet_no
                            - VENUS);
                else if (planet_no >= URANUS && planet_no <= PLUTO)
                    state = cal.getSpeedState();
                else
                    state = Calculate.SPEED_NORMAL;
            } else {
                state = (planet_no >= VENUS && planet_no <= PLUTO) ? cal
                        .getSpeedState() : Calculate.SPEED_NORMAL;
            }
        } else {
            state = Calculate.SPEED_NORMAL;
        }
        return (sign_rev_flip != null && sign_rev_flip[planet_no]) ? ((state == Calculate.SPEED_REVERSE) ? Calculate.SPEED_NORMAL
                : Calculate.SPEED_REVERSE)
                : state;
    }

    private String getGauquelin(int planet_no)
    {
        if (!show_gauquelin)
            return "";
        double val = gauquelin_sign_region[planet_no];
        return " "
                + ((val == Calculate.INVALID) ? "       "
                        : ("(" + FileIO.formatDouble(val, 2, 1, true, false))
                                + (gauquelinPlusZone((int) gauquelin_sign_region[planet_no]) ? "+"
                                        : "-") + ")");
    }

    private boolean gauquelinPlusZone(int region)
    {
        return (region >= 1 && region <= 3 || region >= 9 && region <= 12
                || region >= 18 && region <= 20 || region >= 27 && region <= 29 || region == 36);
    }

    private double computeAzimuth(int planet_no, double[] sign_pos,
            double[] cusps)
    {
        if (sign_computation_type[planet_no] < 0)
            return Calculate.INVALID;
        else {
            return computeAzimuth(sign_pos[planet_no], cusps, quick_azimuth);
        }
    }

    private double computePheno(int planet_no)
    {
        if (sign_computation_type[planet_no] != 0)
            return Calculate.INVALID;
        else if (planet_no != SUN && planet_no != MOON)
            return 0.0;
        else
            return cal.computePheno(planets[planet_no]);
    }

    private double computeAzimuth(double sign_pos, double[] cusps, boolean quick)
    { // need
      // this
      // computation
      // to
      // get
      // altitude
        double pos = cal.computeAzimuth(true_north ? 0.0 : magnetic_shift);
        if (quick)
            return cal.computeAzimuth(true_north ? 0.0 : magnetic_shift,
                    sign_pos, cusps);
        else
            return pos;
    }

    private double computeAltitude(int planet_no)
    {
        return (sign_computation_type[planet_no] < 0 || planet_no >= FORTUNE
                && planet_no <= MC) ? Calculate.INVALID : cal.getAltitude();
    }

    private int computeFixstarState(double pos, double sun_pos)
    {
        double gap = Calculate.getDegreeGap(pos, sun_pos);
        if (gap > 1.5 && Math.abs(gap - 180.0) > 1.5)
            return Calculate.SPEED_NORMAL;
        double noon_ut = cal.getLocalJulianDayUT(birth_zone, true);
        double ut_sav = cal.getJulianDayUT();
        double best_gap = Double.MAX_VALUE;
        int best_index = 0;
        for (int i = -1; i <= 1; i++) {
            cal.setJulianDay(noon_ut + i);
            double val = computePlanet(SUN);
            gap = Calculate.getDegreeGap(pos, val);
            if (gap < best_gap) {
                best_gap = gap;
                best_index = i;
            }
            if (Math.abs(gap - 180.0) < best_gap) {
                best_gap = Math.abs(gap - 180.0);
                best_index = i;
            }
        }
        cal.setJulianDay(ut_sav);
        return (best_index == 0) ? Calculate.SPEED_REVERSE
                : Calculate.SPEED_NORMAL;
    }

    private void computeEclipse()
    {
        if (use_primary_speed || sign_computation_type[SUN] != 0
                || sign_computation_type[MOON] != 0)
            return;
        double range = Resource.getDouble("eclipse_window");
        double start_ut = cal.getJulianDayUT() - range;
        double end_ut = cal.getJulianDayUT() + range;
        LinkedList head = cal.computeSolarEclipse(start_ut, end_ut, true,
                false, false);
        solar_eclipse = head.isEmpty() ? null
                : ((SearchRecord) head.getFirst());
        head = cal.computeLunarEclipse(start_ut, end_ut, true, false);
        lunar_eclipse = head.isEmpty() ? null
                : ((SearchRecord) head.getFirst());
    }

    private String getAspects(String prefix, int index, boolean row)
    {
        String str = null;
        int cnt = 0;
        for (int j = 0; j < aspects_sign.length; j++) {
            if (aspects_display[j] == 0)
                continue;
            String key = aspects_sign[j];
            boolean skip = false;
            for (int i = 0; i < j; i++) {
                if (aspects_display[i] != 0 && key.equals(aspects_sign[i])) {
                    skip = true;
                    break;
                }
            }
            if (skip)
                continue;
            String seq = null;
            for (int i = 0; i < birth_sign_pos.length; i++) {
                int val = (row ? aspects_index[index][i]
                        : aspects_index[i][index]) - 1;
                if (val < 0 || aspects_display[val] == 0
                        || !key.equals(aspects_sign[val]))
                    continue;
                if (seq == null) {
                    seq = aspects_sign[j] + ":" + signs[i];
                } else {
                    seq += signs[i];
                }
            }
            if (seq != null) {
                if (str == null) {
                    str = prefix + seq;
                } else {
                    if ((cnt % 4) == 0)
                        str += prefix + seq;
                    else
                        str += " " + seq;
                }
                cnt++;
            }
        }
        return str;
    }

    // dir: 1 => upper, -1 => lower, 0 => full
    private Object[] getAspectsTable(double[] sign_pos, int dir, String eol,
            boolean list)
    {
        int width = getAspectsWidth();
        if (width < 1)
            return null;
        boolean single = !list && width > 1;
        if (single)
            width = 1;
        String fill = list ? "  " : " ";
        String v_sep = list ? "|" : "";
        String data = "";
        if (dir > 0) { // upper
            data += getXAxis(sign_pos, width, fill) + v_sep + fill;
        } else if (dir == 0) {
            data += fill + v_sep + getXAxis(sign_pos, width, fill);
        }
        String line;
        if (list && dir >= 0) {
            line = eol;
            if (dir == 0)
                line += "--+";
            int cnt = getNumPlanets(sign_pos);
            for (int j = 0; j < cnt * width; j++)
                line += "--";
            if (dir > 0)
                line += "+--";
            data += line;
        }
        String half = Resource.getString("aspects_half_prefix");
        String four = Resource.getString("aspects_four_prefix");
        LinkedList half_list = null, four_list = null;
        if (single) {
            half_list = new LinkedList();
            four_list = new LinkedList();
        }
        boolean skip_eol = data.equals("");
        for (int i = 0, row = 0; i < signs.length; i++) {
            int order = sign_display_orders[i];
            if (!Calculate.isValid(sign_pos[order]))
                continue;
            line = skip_eol ? "" : eol;
            skip_eol = false;
            if (dir <= 0)
                line += signs[order] + v_sep;
            for (int j = 0, col = 0; j < signs.length; j++) {
                int n_order = sign_display_orders[j];
                if (!Calculate.isValid(sign_pos[n_order]))
                    continue;
                int val = aspects_index[order][n_order] - 1;
                if (dir > 0 && j <= i || dir < 0 && j >= i)
                    val = -1;
                if (val >= 0 && aspects_display[val] != 0) {
                    String aspect = aspects_sign[val];
                    int k = aspect.length();
                    if (single && k > 1) {
                        String prefix = aspect.substring(0, 1);
                        if (prefix.equals(half)) {
                            half_list.add(new Point(col, row));
                        } else if (prefix.equals(four)) {
                            four_list.add(new Point(col, row));
                        }
                        aspect = aspect.substring(k - 1, k);
                    }
                    for (; k < width; k++)
                        line += fill;
                    line += aspect;
                } else {
                    for (int k = 0; k < width; k++)
                        line += fill;
                }
                col++;
            }
            if (dir > 0)
                line += v_sep + signs[order];
            data += line;
            row++;
        }
        if (dir < 0) { // lower
            if (list) {
                line = eol + "--+";
                int cnt = getNumPlanets(sign_pos);
                for (int j = 0; j < cnt * width; j++)
                    line += "--";
                data += line;
            }
            data += eol + fill + v_sep + getXAxis(sign_pos, width, fill);
        }
        Object[] array = new Object[4];
        array[0] = data;
        array[1] = "s.";
        array[2] = half_list;
        array[3] = four_list;
        return array;
    }

    private String getXAxis(double[] sign_pos, int width, String fill)
    {
        String str = "";
        for (int i = 0; i < signs.length; i++) {
            int order = sign_display_orders[i];
            if (!Calculate.isValid(sign_pos[order]))
                continue;
            for (int j = 1; j < width; j++)
                str += fill;
            str += signs[order];
        }
        return str;
    }

    private int getAspectsWidth()
    {
        boolean[] marker = new boolean[aspects_sign.length];
        for (int i = 0; i < signs.length; i++) {
            for (int j = 0; j < signs.length; j++) {
                int val = aspects_index[i][j] - 1;
                if (val >= 0)
                    marker[val] = true;
            }
        }
        int width = 0;
        for (int i = 0; i < aspects_sign.length; i++) {
            if (aspects_display[i] == 0 || !marker[i])
                continue;
            width = Math.max(width, aspects_sign[i].length());
        }
        return width;
    }

    private int getNumPlanets(double[] sign_pos)
    {
        int cnt = 0;
        for (int i = 0; i < signs.length; i++) {
            if (!Calculate.isValid(sign_pos[i]))
                continue;
            cnt++;
        }
        return cnt;
    }

    private String computeWeakHouse(String pole, boolean both)
    {
        int len = year_names.length;
        for (int i = 0; i < len; i++) {
            if (pole.equals(year_names[i])) {
                int index = 10 - 2 * (i / 10);
                if (both) {
                    return earth_pole_names[index]
                            + earth_pole_names[index + 1];
                } else {
                    if ((i % 2) == 1)
                        index++;
                    return earth_pole_names[index];
                }
            }
        }
        return "";
    }

    private String[] expandStarList(LinkedList head, Hashtable m_table,
            String suffix, boolean no_explain, boolean check_now)
    {
        LinkedList new_head = new LinkedList();
        LinkedList new_explain = no_explain ? null : new LinkedList();
        Hashtable table = new Hashtable();
        if (ChartMode.isChartMode(ChartMode.PICK_MODE))
            check_now = false;
        try {
            ListIterator iter = head.listIterator();
            for (;;) {
                String star = (String) iter.next();
                if (inTable(m_table, star, check_now)
                        && table.get(star) == null) {
                    new_head.add(star);
                    table.put(star, "t");
                    if (new_explain == null)
                        continue;
                    if (explain_star && Resource.hasKey(star + suffix)) {
                        String str = Resource.getString(star + suffix);
                        if (str.equals(""))
                            str = null;
                        new_explain.add(str);
                    } else {
                        new_explain.add(null);
                    }
                }
            }
        } catch (NoSuchElementException e) {
        }
        if (new_head.size() == 0)
            return null;
        if (new_explain != null) {
            try {
                ListIterator iter = new_explain.listIterator();
                for (;;)
                    new_head.add(iter.next());
            } catch (NoSuchElementException e) {
            }
        }
        return (String[]) new_head.toArray(new String[1]);
    }

    private String formatStatusString(String status, String[] seq, int[] freq)
    {
        String result = "";
        int j = 0;
        for (int i = 0; i < seq.length; i++) {
            String str = (i < status.length()) ? status.substring(i, i + 1)
                    : "";
            for (; j < seq.length; j++) {
                if (seq[j].equals(str)) {
                    result += str;
                    j++;
                    break;
                } else if (freq[j] > 0) {
                    result += "@";
                }
            }
        }
        return result;
    }

    private String getSignStatus(String sign, double degree, String[] seq,
            int[] freq)
    {
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            return "";
        String sign_status_key = Resource.getString("sign_status_key");
        String[] array = Resource.getStringArray(sign_status_key + sign);
        String result = "";
        String house = cal.getZodiac(degree, false);
        for (int i = 0; i < array.length; i++) {
            String str = array[i];
            if (house.equals(str.substring(0, 1))) {
                String status = str.substring(2, 3);
                for (int j = 0; j < seq.length; j++) {
                    if (seq[j].equals(status)) {
                        freq[j]++;
                        break;
                    }
                }
                result += status;
            }
        }
        String str = cal.getStarSign(degree, stellar_sign_pos,
                full_stellar_signs);
        if (str.substring(1).equals(sign)) {
            result = seq[0] + result;
            freq[0]++;
        }
        return result;
    }

    private Hashtable chineseCalendar(int[] date, String[] poles,
            double[] sign_pos, int[] pole_override, int[] lunar_cal,
            int[] solar_cal)
    {
        int y, m1, m2, index;
        String str;
        int[] adj_date = (int[]) date.clone();
        // after 11:00pm, it is the next day
        if (adj_date[3] == 23
                && Resource.getPrefInt("switch_day_at_11_pm") != 0)
            BaseCalendar.addTime(adj_date, Calendar.DAY_OF_MONTH, 1);
        if (lunar_cal != null) {
            poles[CH_YEAR] = year_names[lunar_cal[0] - 1];
            str = poles[CH_YEAR].substring(0, 1);
            for (y = 0; y < sky_pole_names.length; y++) {
                if (sky_pole_names[y].equals(str))
                    break;
            }
            index = lunar_cal[1] - 1;
            m1 = index + month_sky_pole_shifts[y];
            while (m1 >= sky_pole_names.length)
                m1 -= sky_pole_names.length;
            m2 = index + month_earth_pole_shift;
            while (m2 >= earth_pole_names.length)
                m2 -= earth_pole_names.length;
            poles[CH_MONTH] = sky_pole_names[m1] + earth_pole_names[m2];
        }
        int offset = (pole_override != null) ? pole_override[0] : 0;
        offset += solar_cal[0] - 1;
        poles[YEAR_POLE] = year_names[FileIO.boundNumber(offset,
                year_names.length)];
        index = solar_cal[1] - 1;
        str = poles[YEAR_POLE].substring(0, 1);
        for (y = 0; y < sky_pole_names.length; y++) {
            if (sky_pole_names[y].equals(str))
                break;
        }
        m1 = index + month_sky_pole_shifts[y];
        while (m1 >= sky_pole_names.length)
            m1 -= sky_pole_names.length;
        m2 = index + month_earth_pole_shift;
        while (m2 >= earth_pole_names.length)
            m2 -= earth_pole_names.length;
        poles[MONTH_POLE] = sky_pole_names[m1] + earth_pole_names[m2];
        offset = (pole_override != null) ? pole_override[1] : 0;
        if (offset != 0) {
            offset += FileIO.getArrayIndex(poles[MONTH_POLE], year_names);
            poles[MONTH_POLE] = year_names[FileIO.boundNumber(offset,
                    year_names.length)];
        }
        day_pole_base[3] = adj_date[3];
        day_pole_base[4] = adj_date[4];
        offset = (pole_override != null) ? pole_override[2] : 0;
        offset += cal.getDifferenceInDays(day_pole_base, adj_date);
        poles[DAY_POLE] = year_names[FileIO.boundNumber(offset,
                year_names.length)];
        str = poles[DAY_POLE].substring(0, 1);
        for (y = 0; y < sky_pole_names.length; y++) {
            if (sky_pole_names[y].equals(str))
                break;
        }
        // after 11:00pm and before 12:00am, sky pole is from next day
        if (adj_date[3] == 23
                && Resource.getPrefInt("switch_day_at_11_pm") == 0) {
            if (++y == sky_pole_names.length)
                y = 0;
        }
        index = (adj_date[3] + 1) / 2;
        while (index >= earth_pole_names.length)
            index -= earth_pole_names.length;
        m1 = index + hour_sky_pole_shifts[y];
        while (m1 >= sky_pole_names.length)
            m1 -= sky_pole_names.length;
        m2 = index + hour_earth_pole_shift;
        while (m2 >= earth_pole_names.length)
            m2 -= earth_pole_names.length;
        poles[HOUR_POLE] = sky_pole_names[m1] + earth_pole_names[m2];
        offset = (pole_override != null) ? pole_override[3] : 0;
        if (offset != 0) {
            offset += FileIO.getArrayIndex(poles[HOUR_POLE], year_names);
            poles[HOUR_POLE] = year_names[FileIO.boundNumber(offset,
                    year_names.length)];
        }
        return (sign_pos != null) ? getStarSigns(poles, sign_pos, false) : null;
    }

    private Hashtable getStarSigns(String[] poles, double[] sign_pos,
            boolean day_pole)
    {
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            return null;
        String main_pole = poles[day_pole ? DAY_POLE : YEAR_POLE];
        LinkedList head = new LinkedList();
        Resource.getStringArray(main_pole, year_data);
        String key = year_data[Resource.getInt("long_life_start") - 1];
        String[] array = Resource.getStringArray("long_life_pos");
        for (int i = 0; i < array.length; i++) {
            if (key.equals(array[i].substring(0, 1))) {
                int n = FileIO.parseInt(array[i].substring(2), -1, false);
                for (int j = 0; j < 12; j++) {
                    int m = n + j;
                    if (m >= 12)
                        m -= 12;
                    head.add(twelve_signs[m] + ":" + long_life_signs[j]);
                }
                break;
            }
        }
        if (!day_pole) {
            key = Resource.getString("star_sky_earth_key") + main_pole;
            array = Resource.getStringArray(key);
            if (array != null) {
                for (int i = 0; i < array.length; i++)
                    addStarSign(head, array[i]);
            }
        }
        key = Resource.getString("star_sky_key") + main_pole.substring(0, 1);
        array = Resource.getStringArray(key);
        if (array != null) {
            for (int i = 0; i < array.length; i++)
                addStarSign(head, array[i]);
        }
        if (!day_pole && sign_pos != null) {
            key = Resource.getString("star_sky_qi_key");
            String year_part = main_pole.substring(0, 1);
            key += year_part;
            String str = Resource.getString(key);
            double degree = day_birth ? sign_pos[SUN] : sign_pos[MOON];
            int shift = cal.getZodiacShift(str.substring(0, 1), degree);
            for (int i = 0; i < sky_pole_names.length; i++) {
                if (sky_pole_names[i].equals(year_part)) {
                    int index = i + shift;
                    while (index >= sky_pole_names.length)
                        index -= sky_pole_names.length;
                    key = Resource.getString("star_sky_qi_key")
                            + sky_pole_names[index];
                    str = Resource.getString(key);
                    head.add(str.substring(2, 3) + ":"
                            + Resource.getString("star_sky_qi_key"));
                    break;
                }
            }
        }
        key = Resource.getString("star_earth_key") + main_pole.substring(1, 2);
        array = Resource.getStringArray(key);
        if (array != null) {
            for (int i = 0; i < array.length; i++)
                addStarSign(head, array[i]);
        }
        if (!day_pole && poles[MONTH_POLE] != null) {
            key = Resource.getString("star_month_key")
                    + poles[MONTH_POLE].substring(1, 2);
            array = Resource.getStringArray(key);
            if (array != null) {
                for (int i = 0; i < array.length; i++)
                    addStarSign(head, array[i]);
            }
            key = Resource.getString("star_month_hour_key")
                    + poles[MONTH_POLE].substring(1, 2)
                    + poles[HOUR_POLE].substring(1, 2);
            array = Resource.getStringArray(key);
            if (array != null) {
                for (int i = 0; i < array.length; i++)
                    addStarSign(head, array[i]);
            }
        }
        Hashtable table = new Hashtable();
        for (ListIterator iter = head.listIterator(); iter.hasNext();) {
            String val = (String) iter.next();
            String pos = val.substring(0, 1);
            String field = val.substring(2);
            LinkedList data = (LinkedList) table.get(pos);
            if (data == null)
                data = new LinkedList();
            data.add(field);
            table.put(pos, data);
        }
        if (!day_pole) {
            if (poles == birth_poles) {
                for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
                    weak_house[i] = computeWeakHouse(birth_poles[i], false);
                    solid_house[i] = birth_poles[i].substring(1);
                }
            }
            computeYearSign(poles, table);
        }
        return table;
    }

    private void addStarSign(LinkedList head, String entry)
    {
        String key = entry.substring(2);
        String prefix = entry.substring(0, 2);
        head.add(entry);
        if (Resource.hasKey(key)) {
            String[] group = Resource.getStringArray(key);
            for (int l = 0; l < group.length; l++) {
                String star;
                if (group[l].startsWith("+")) {
                    star = group[l].substring(1);
                } else {
                    star = group[l];
                }
                head.add(prefix + star);
            }
        }
    }

    public boolean inMasterTable(String key, boolean use_now)
    {
        return inTable(master_table, key, use_now);
    }

    private boolean inTable(Hashtable table, String key, boolean use_now)
    {
        String val = (String) table.get(use_now ? (now_year + key) : key);
        if (val != null)
            return val.equals("t");
        for (int i = 0; i < star_equ_map.length; i += 2) {
            if (key.equals(star_equ_map[i + 1])) {
                key = star_equ_map[i];
                val = (String) table.get(use_now ? (now_year + key) : key);
                if (val != null)
                    return val.equals("t");
                else
                    break;
            }
        }
        return !use_now;
    }

    private void computeYearSign(String[] poles, Hashtable table)
    {
        boolean show_explain = explain_star
                && !ChartMode.isChartMode(ChartMode.PICK_MODE);
        String year_sign_key = Resource.getString("year_sign_key");
        int y_index = FileIO.getArrayIndex(poles[YEAR_POLE], year_names);
        for (int i = 0; i < 12; i++) {
            String[] array = Resource.getStringArray(year_signs[i]
                    + year_sign_key);
            int index = FileIO.parseInt(array[0], -1, false);
            if (index > 0) {
                String val = getYearStar(y_index, index);
                if (ten_god_mode) {
                    table.put(
                            val,
                            array[1].substring(1) + ","
                                    + array[1].substring(0, 1));
                } else {
                    table.put(
                            val,
                            array[1].substring(0, 1) + ","
                                    + array[1].substring(1));
                }
                if (poles == birth_poles) {
                    String house = cal.getZodiac(
                            City.normalizeDegree(life_sign_pos - i * 30.0),
                            true);
                    String half_house = house.substring(0, 1);
                    String str = getWeakHouse(half_house)
                            + getSolidHouse(half_house);
                    if (!str.equals(""))
                        str += ",";
                    str = "(" + str + val + "): " + array[2];
                    if (show_explain) {
                        str += "\n    "
                                + Resource.getString(year_signs[i] + explain)
                                        .replaceAll("\\|", "\n    ");
                    }
                    table.put(year_signs[i], str);
                    String s_key = house.substring(1)
                            + Resource.getString("star_sign_key");
                    LinkedList star_list = (LinkedList) table.get(s_key);
                    if (star_list == null)
                        star_list = new LinkedList();
                    star_list.addLast("[" + year_signs[i] + "]");
                    table.put(s_key, star_list);
                }
            }
        }
    }

    private String getYearStar(int year, int order)
    {
        order -= year_star_range[0];
        if (ten_god_mode && (year & 0x1) == 1)
            order = year_star_map[order % 10];
        int index = (year + order) % 10;
        return year_star_seq[index];
    }

    public String getChildLimit(int cur_age, String sep)
    {
        String str = null;
        cur_age = Math.min(cur_age, child_seq.length - 1);
        if (cur_age >= 0) {
            str = (sep == null) ? ""
                    : (Resource.getString("child_limit") + sep);
            str += cal.getZodiac(
                    City.normalizeDegree(life_sign_pos + 30.0
                            * child_seq[cur_age]), false);
        }
        return str;
    }

    public String getSmallLimit(int cur_age, String sep)
    {
        return Resource.getString("small_limit")
                + sep
                + cal.getZodiac(
                        City.normalizeDegree(life_sign_pos + (cur_age - 1)
                                * 30.0), false);
    }

    public String getMonthLimit(int cur_age, String sep)
    {
        int index = now_lunar_date[1] - lunar_date[1];
        if (index < 0) {
            index += 12;
            index += cur_age - 2; // use last year's small limit as base
        } else {
            index += cur_age - 1;
        }
        return Resource.getString("month_limit")
                + sep
                + cal.getZodiac(
                        City.normalizeDegree(life_sign_pos + index * 30.0),
                        false);
    }

    public String getFlyLimit(int cur_age, String sep, int child_age_limit)
    {
        String str = Resource.getString("fly_limit") + sep;
        int index = (int) (life_sign_pos / 30.0);
        int[] fly_seq;
        if (cur_age >= 0 && cur_age < child_age_limit) {
            fly_seq = ((index % 2) == 0) ? fly_seq_yang1 : fly_seq_ying1;
            str += cal.getZodiac(
                    City.normalizeDegree(life_sign_pos + 30.0
                            * fly_seq[cur_age % fly_seq.length]), false);
        } else if (cur_age >= child_age_limit) {
            cur_age -= child_age_limit;
            fly_seq = ((index % 2) == 0) ? fly_seq_yang2 : fly_seq_ying2;
            if (cur_age + 2 < fly_seq.length) {
                int last_index, cur_index;
                if (cur_age >= fly_seq_half_shift[0]
                        && cur_age < fly_seq_half_shift[1]) {
                    last_index = fly_seq[cur_age];
                    cur_index = fly_seq[cur_age + 1];
                } else if (cur_age >= fly_seq_half_shift[2]
                        && cur_age < fly_seq_half_shift[3]) {
                    last_index = fly_seq[cur_age + 1];
                    cur_index = fly_seq[cur_age + 2];
                } else if (cur_age >= fly_seq_half_shift[1]) {
                    last_index = cur_index = fly_seq[cur_age + 1];
                } else {
                    last_index = cur_index = fly_seq[cur_age];
                }
                if (last_index != cur_index) {
                    str += cal.getZodiac(
                            City.normalizeDegree(life_sign_pos + 30.0
                                    * last_index), false)
                            + cal.getZodiac(
                                    City.normalizeDegree(life_sign_pos + 30.0
                                            * cur_index), false)
                            + Resource.getString("each_half_year");
                } else {
                    str += cal.getZodiac(
                            City.normalizeDegree(life_sign_pos + 30.0
                                    * cur_index), false);
                }
            } else {
                str = null;
            }
        } else {
            str = null;
        }
        return str;
    }

    private String getBeforeBirthName(String name)
    {
        int index = FileIO.getArrayIndex(name, year_names) - 9;
        if (index < 0)
            index += 60;
        return year_names[index];
    }

    private String getYearSoundName(String name)
    {
        return Resource.getString(Resource.getString("year_sound_key") + name);
    }

    private String getTenGodName(String name, String day_name, boolean plus)
    {
        int i = FileIO.getArrayIndex(name, sky_pole_names);
        int j = FileIO.getArrayIndex(day_name, sky_pole_names);
        int index = i - 2 * (j / 2);
        if (index < 0)
            index += 10;
        String str = ((j % 2) == 0) ? ten_god_seq1[index] : ten_god_seq2[index];
        if (plus)
            str += getAltPoleName(name, day_name);
        return str;
    }

    private String getAltPoleName(String name, String day_name)
    {
        if (alt_pole_data == null)
            return "";
        int i = FileIO.getArrayIndex(name, sky_pole_names);
        return Resource.getString("separator") + alt_pole_data[i].substring(2);
    }

    private String getLongLifeName(String name, String day_name, int fill)
    {
        int i = FileIO.getArrayIndex(name, earth_pole_names);
        int j = FileIO.getArrayIndex(day_name, sky_pole_names);
        int dir = ((j % 2) == 0) ? -1 : 1;
        int index = day_pole_long_life_seq[j] + dir * i;
        while (index < 0)
            index += 12;
        while (index >= 12)
            index -= 12;
        String str = long_life_signs[index];
        for (int n = fill - str.length(); n > 0; n--)
            str += "  ";
        return str;
    }

    private String getEarthGodSeq(String name)
    {
        for (int i = 0; i < 12; i++) {
            if (name.equals(earth_god_seq[i].substring(0, 1)))
                return earth_god_seq[i].substring(2);
        }
        return "";
    }

    private void printSolarTerms(boolean full)
    {
        DecimalFormat format = new DecimalFormat("00");
        int[] date = new int[5];
        int s = full ? 0 : 1;
        int e = full ? solar_terms.length : 25;
        for (int i = s; i < e; i++) {
            Calculate.getDateFromJulianDayUT(solar_terms[i], date);
            tab.append(season_starts[(i + 23) % 24]
                    + ": "
                    + format.format(date[1])
                    + "/"
                    + format.format(date[2])
                    + " "
                    + BaseCalendar
                            .formatDate(0.0, date, null, 0.0, false, true));
            if (((i - s) % 4) == 3)
                tab.appendLine();
            else
                tab.append("  ");
        }
        if (((e - s) % 4) != 0)
            tab.appendLine();
        if (full)
            printNewMoons();
    }

    private void printNewMoons()
    {
        DecimalFormat format = new DecimalFormat("00");
        int[] date = new int[5];
        int i;
        tab.appendLine();
        tab.appendLine(Resource.getString("crescent") + ":");
        for (i = 0; i < new_moons.length; i++) {
            Calculate.getDateFromJulianDayUT(new_moons[i], date);
            tab.append(format.format(date[1])
                    + "/"
                    + format.format(date[2])
                    + " "
                    + BaseCalendar
                            .formatDate(0.0, date, null, 0.0, false, true));
            if ((i % 6) == 5)
                tab.appendLine();
            else
                tab.append("  ");
        }
        if (i > 12)
            tab.appendLine();
    }

    private String getBirthSeason(int[] date)
    {
        if (solar_terms != null) {
            double ut = Calculate.getJulianDayUT(date);
            for (int i = 1; i < solar_terms.length; i++) {
                if (ut >= solar_terms[i - 1] && ut < solar_terms[i]) {
                    double ratio = (ut - solar_terms[i - 1])
                            / (solar_terms[i] - solar_terms[i - 1]);
                    String state;
                    if (ratio < (1.0 / 3.0)) {
                        state = Resource.getString("season_begin");
                    } else if (ratio < (2.0 / 3.0)) {
                        state = Resource.getString("season_middle");
                    } else {
                        state = Resource.getString("season_end");
                    }
                    return season_starts[(i + 22) % 24] + state;
                }
            }
        }
        return "";
    }

    private double getBigCycleStartGap(int[] date, int dir)
    {
        double ut = Calculate.getJulianDayUT(date);
        double[] s_terms = solar_terms;
        for (int iter = 0; iter < 2; iter++) {
            for (int i = 3; i < s_terms.length; i += 2) {
                if (ut >= s_terms[i - 2] && ut < s_terms[i]) {
                    double gap;
                    if (dir > 0) {
                        gap = s_terms[i] - ut;
                    } else {
                        gap = ut - s_terms[i - 2];
                    }
                    return gap / 3.0;
                }
            }
            if (iter == 1)
                break;
            // must be end of year, use last year's solar term
            int[] t_date = (int[]) date.clone();
            t_date[0]--;
            t_date[1] = t_date[2] = 5; // mid year of last year
            s_terms = cal.computeSolarTerms(t_date);
        }
        return 0.0;
    }

    private int[] getBigCycleStartDate(int[] date, double gap)
    {
        int[] t_date = new int[5];
        double ut = Calculate.getJulianDayUT(date);
        Calculate.getDateFromJulianDayUT(ut + 365.25 * gap, t_date);
        return t_date;
    }

    private String getBirthName()
    {
        if (!ChartMode.isMultipleMode(false)) {
            return ChartMode.isAstroMode(ChartMode.ALT_NATAL_MODE) ? now
                    .getName() : birth.getName();
        } else {
            String name1 = birth.getName();
            String name2 = now.getName();
            if (name1.equals("") || name2.equals(""))
                return name1 + name2;
            else
                return getMergeName(name1, name2);
        }
    }

    public String getMergeName(String name1, String name2)
    {
        if (isVerticalName(name1 + name2, false))
            return name1 + Resource.getString("and") + name2;
        else
            return name1 + " and " + name2;
    }

    private boolean getBirthSex()
    {
        return ChartMode.isAstroMode(ChartMode.ALT_NATAL_MODE) ? now.getSex()
                : birth.getSex();
    }

    public void setHouse(String house)
    {
        house_override = house;
    }

    public String getHouse()
    {
        return cal.getZodiac(life_sign_pos, false);
    }

    public void setDayOrNight(boolean day, boolean set)
    {
        has_day_birth_override = set;
        day_birth_override = day;
    }

    public boolean getDayOrNight()
    {
        return day_birth;
    }

    public String getOverrideString()
    {
        if (house_override == null && eight_char_override == null
                && birth_cusp_override == null && !has_day_birth_override
                && !has_pos_adj) {
            return null;
        }
        return getCuspOverride() + getHouse()
                + Resource.getString(getDayOrNight() ? "daytime" : "nighttime")
                + getPosOverride();
    }

    private String getCuspOverride()
    {
        if (birth_cusp_override == null)
            return "";
        String str = "a";
        for (int i = 1; i < birth_cusp_override.length; i++) {
            if (i > 1)
                str += ":";
            str += FileIO.formatDouble(birth_cusp_override[i], 3, 1, false,
                    false);
        }
        return str + ",";
    }

    private String getPosOverride()
    {
        if (!has_pos_adj)
            return "";
        String str = "";
        if (eight_char_override != null) {
            for (int i = 0; i < 4; i++) {
                if (eight_char_override[i] != 0) {
                    str += "," + (POLE_OVERRIDE_OFFSET + i) + ":"
                            + eight_char_override[i];
                }
            }
        }
        for (int i = 0; i < birth_sign_pos.length; i++) {
            if (sign_pos_shift[i] != 0.0 || sign_rev_flip[i]) {
                str += "," + i + ":" + sign_pos_shift[i]
                        + (sign_rev_flip[i] ? "f" : "");
            }
        }
        return str;
    }

    public void setOverrideString(String override)
    {
        Arrays.fill(sign_pos_shift, 0.0);
        Arrays.fill(sign_rev_flip, false);
        eight_char_override = null;
        birth_cusp_override = null;
        if (override != null) {
            int n;
            if (override.startsWith("a")) {
                n = override.indexOf(',');
                setCuspOverride(override.substring(1, n));
                override = override.substring(n + 1);
            }
            n = override.indexOf(',');
            if (n >= 0) {
                setBirthPosOverride(override.substring(n + 1));
                override = override.substring(0, n);
            }
            setHouse(override.substring(0, 1));
            setDayOrNight(
                    override.substring(1, 2).equals(
                            Resource.getString("daytime")), true);
        } else {
            setHouse(null);
            setDayOrNight(false, false);
        }
    }

    private void setCuspOverride(String data)
    {
        StringTokenizer st = new StringTokenizer(data, ":");
        birth_cusp_override = new double[13];
        int n = 1;
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            birth_cusp_override[n++] = FileIO
                    .parseDouble(str.trim(), 0.0, true);
        }
    }

    private void setBirthPosOverride(String data)
    {
        StringTokenizer st = new StringTokenizer(data, ",");
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            int n = str.indexOf(':');
            int planet = FileIO.parseInt(str.substring(0, n), 0, true);
            str = str.substring(n + 1);
            if (planet >= POLE_OVERRIDE_OFFSET) {
                if (eight_char_override == null)
                    eight_char_override = new int[4];
                eight_char_override[planet - POLE_OVERRIDE_OFFSET] = FileIO
                        .parseInt(str, 0, false);
            } else {
                n = str.indexOf('f');
                if (n >= 0) {
                    sign_rev_flip[planet] = true;
                    str = str.substring(0, n);
                }
                sign_pos_shift[planet] = FileIO.parseDouble(str, 0.0, false);
            }
        }
    }

    public String getOverridenStatus()
    {
        String str = "";
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE))
            return str;
        if (house_override != null)
            str += Resource.getString("life_sign_name");
        if (has_day_birth_override)
            str += Resource.getString("daytime")
                    + Resource.getString("nighttime");
        if (has_pos_adj)
            str += Resource.getString("sign_pos");
        return str;
    }

    public String setMountainPos(String pos)
    {
        mountain_pos = City.parseMapPos(pos);
        return getMountainPos(false);
    }

    public String getMountainPos(boolean align)
    {
        if (mountain_pos == Calculate.INVALID)
            mountain_pos = 0.0;
        return City.formatMapPos(mountain_pos, align);
    }

    public void setDaySet(boolean set)
    {
        day_set = set;
    }

    public void setMagneticShift(double val, String message)
    {
        magnetic_shift = val;
        magnetic_shift_message = (message == null) ? "" : message;
        true_north = Resource.getPrefInt("true_north") != 0;
        true_north_magnetic_shift = true_north ? magnetic_shift : 0.0;
    }

    public int[] getDateAtSunPos(double degree, String country, String city,
            String zone, int[] start, boolean inner, boolean backward)
    {
        if (degree < 0.0) {
            degree = solar_return_pos;
            if (!Calculate.isValid(degree))
                return null;
        } else {
            if (true_north && Resource.getPrefInt("align_north") == 0)
                degree = City.normalizeDegree(degree - magnetic_shift);
        }
        double jd_ut = (start == null) ? cal.getJulianDayUT() : Calculate
                .getJulianDayUT(start);
        updateLocation(city, country);
        if (inner) {
            jd_ut = computePlanetTransit(SUN, jd_ut, degree, backward);
        } else {
            jd_ut = cal.computePlanetAzimuthTransit(initSunComputation(),
                    jd_ut, degree, true_north ? 0.0 : magnetic_shift,
                    Calculate.HALF_DEGREE_PRECISION, quick_azimuth, false,
                    backward, false);
        }
        if (!Calculate.isValid(jd_ut))
            return null;
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(jd_ut, date);
        BaseCalendar.addZoneOffset(zone, date, 0, true);
        return date;
    }

    public boolean getPlanetOffset(int planet_no, double degree)
    {
        if (!Calculate.isValid(degree))
            return false;
        degree = City.normalizeDegree(degree + 30.0 - birth_sign_pos[planet_no]
                + sign_pos_shift[planet_no]);
        if (degree > 180.0)
            degree -= 360.0;
        sign_pos_shift[planet_no] = degree;
        return true;
    }

    public int[] getDateAtPlanetPos(int planet_no, boolean birth_data,
            double degree, String country, String city, String zone,
            int[] start, boolean backward)
    {
        double jd_ut = (start == null) ? cal.getJulianDayUT() : Calculate
                .getJulianDayUT(start);
        if (Calculate.isValid(degree)) {
            degree = City.normalizeDegree(degree + 30.0);
        } else {
            // return to original position
            degree = birth_data ? birth_sign_pos[planet_no]
                    : now_sign_pos[planet_no];
            if (!Calculate.isValid(degree))
                return null;
        }
        degree = City.normalizeDegree(degree - sign_pos_shift[planet_no]);
        updateLocation(city, country);
        jd_ut = computePlanetTransit(planet_no, jd_ut, degree, backward);
        if (!Calculate.isValid(jd_ut))
            return null;
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(jd_ut, date);
        BaseCalendar.addZoneOffset(zone, date, 0, true);
        return date;
    }

    private double computePlanetTransit(int planet_no, double start_ut,
            double degree, boolean backward)
    {
        switch (sign_computation_type[planet_no]) {
            case 0:
                if (planet_no == INV_TRUE_NODE) {
                    planet_no = TRUE_NODE;
                    degree = City.normalizeDegree(degree + 180.0);
                }
                return cal.computePlanetTransit(planets[planet_no], start_ut,
                        degree, backward);
            case -1:
                return Calculate.INVALID;
            default:
                setOrbitData(planet_no);
                return cal.computePlanetTransit(-1, start_ut, degree, backward);
        }
    }

    private int initSunComputation()
    {
        switch (sign_computation_type[SUN]) {
            case 0:
            case -1: // should not be here
                return planets[SUN];
            default:
                setOrbitData(SUN);
                return -1;
        }
    }

    private void setOrbitData(int planet_no)
    {
        double speed = (Resource.hasKey(sign_prefix[planet_no] + "_speed")) ? Resource
                .getDouble(sign_prefix[planet_no] + "_speed")
                : (360.0 / Resource.getDouble(sign_prefix[planet_no]
                        + "_period"));
        double base_degree = Resource.getDouble(sign_prefix[planet_no]
                + "_base_degree");
        int[] base_date = Resource.getIntArray(sign_prefix[planet_no]
                + "_base_date");
        cal.setOrbitData(speed, Calculate.getJulianDayUT(base_date),
                base_degree);
    }

    public boolean canComputeTransit(int planet_no)
    {
        switch (sign_computation_type[planet_no]) {
            case 0:
                return planet_no <= MEAN_APOG;
            case -1:
                return false;
            default:
                return true;
        }
    }

    public String[] getTransitData(DataEntry entry, String country,
            String city, String zone, int mode)
    {
        int month = Resource.getPrefInt("transit_period");
        if (month <= 0)
            return null;
        int[] date = new int[5];
        BaseCalendar.auditDay(Resource.getPrefString("transit_date"), date);
        BaseCalendar.addZoneOffset(date, false);
        double start_ut = Calculate.getJulianDayUT(date);
        BaseCalendar.addTime(date, Calendar.MONTH, month);
        double end_ut = Calculate.getJulianDayUT(date);
        computeBirthSignPos(entry);
        double ref_ut = cal.getJulianDayUT();
        double s_ut, e_ut, ratio, speed;
        switch (mode) {
            case ChartMode.PRIMARY_DIRECTION_MODE:
                ratio = 1.0;
                speed = getPrimarySpeed();
                s_ut = start_ut;
                e_ut = end_ut;
                break;
            case ChartMode.SECONDARY_PROGRESSION_MODE:
            case ChartMode.SOLAR_ARC_MODE:
                ratio = getCompressionRatio();
                speed = 1.0;
                s_ut = ref_ut + ratio * (start_ut - ref_ut);
                e_ut = ref_ut + ratio * (end_ut - ref_ut);
                break;
            case ChartMode.TRANSIT_MODE:
                ratio = speed = 1.0;
                s_ut = start_ut;
                e_ut = end_ut;
                break;
            default:
                ratio = speed = 1.0;
                s_ut = start_ut;
                e_ut = end_ut;
                break;
        }
        LinkedList head = new LinkedList();
        computeTransitData(head, entry, s_ut, e_ut, ref_ut, speed, mode);
        if (head.isEmpty())
            return null;
        Transit[] array = (Transit[]) head.toArray(new Transit[1]);
        Transit.sort(array);
        return genTransitHTML(array, country, city, zone, start_ut, end_ut,
                ref_ut, ratio, mode);
    }

    private double getCompressionRatio()
    {
        double ratio = Resource.getPrefDouble("secondary_advance") / 365.25;
        ratio = Math.max(ratio, MIN_ADVANCE);
        ratio = Math.min(ratio, MAX_ADVANCE);
        return ratio;
    }

    private double getPrimarySpeed()
    {
        double speed = Resource.getPrefDouble("primary_advance") / 365.25;
        speed = Math.max(speed, MIN_ADVANCE);
        speed = Math.min(speed, MAX_ADVANCE);
        return speed;
    }

    private void computeBirthSignPos(DataEntry entry)
    {
        updateLocation(entry.getCity(), entry.getCountry());
        int[] date_buf = entry.getBirthDay();
        String zone = entry.getZone();
        BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
        cal.setJulianDay(date_buf);
        cal.computeHouses(birth_cusp);
        Arrays.fill(birth_sign_pos, Calculate.INVALID);
        birth_sign_pos[SUN] = computePlanet(SUN);
        birth_sign_state[SUN] = getSpeedState(SUN);
        birth_sign_pos[MOON] = computePlanet(MOON);
        birth_sign_state[MOON] = getSpeedState(MOON);
        double[] sun_rise_set = new double[2];
        cal.computeRiseSet(zone, planets[SUN], sun_rise_set);
        boolean day_time = cal.isDayBirth(sun_rise_set);
        cal.initSpecial(birth_sign_pos[SUN], birth_sign_pos[MOON], day_time);
        for (int i = 0; i < signs.length; i++) {
            if (i == SUN && i == MOON)
                continue;
            if (sign_computation_type[i] != 0 || i < planets.length
                    && planets[i] >= 0) {
                birth_sign_pos[i] = computePlanet(i);
                birth_sign_state[i] = getSpeedState(i);
            } else {
                birth_sign_pos[i] = Calculate.INVALID;
                birth_sign_state[i] = Calculate.SPEED_NORMAL;
                if (sign_opposite[i] >= 0
                        && Calculate.isValid(birth_sign_pos[sign_opposite[i]])) {
                    birth_sign_pos[i] = computeOpposite(i, birth_sign_pos);
                }
            }
        }
        if (Resource.getPrefInt("true_as_north") == 0) {
            double val = birth_sign_pos[TRUE_NODE];
            birth_sign_pos[TRUE_NODE] = birth_sign_pos[INV_TRUE_NODE];
            birth_sign_pos[INV_TRUE_NODE] = val;
        }
        for (int i = 0; i < signs.length; i++) {
            if (sign_display[i] <= 0 || signs[i].equals(invalid_sign)) {
                birth_sign_pos[i] = Calculate.INVALID;
            }
        }
    }

    private void computeTransitData(LinkedList head, DataEntry entry,
            double start_ut, double end_ut, double ref_ut, double speed,
            int mode)
    {
        int max_entry = Resource.getPrefInt("transit_max_entry");
        int[] sign_show_array = Resource
                .getPrefIntArray("transit_sign_display");
        int[] aspects_show_array = Resource
                .getPrefIntArray("transit_aspects_display");
        boolean[] symmetric = new boolean[aspects_degree.length];
        for (int j = 0; j < aspects_degree.length; j++) {
            symmetric[j] = Math.abs(aspects_degree[j]) < 0.1
                    || Math.abs(aspects_degree[j] - 180.0) < 0.1;
        }
        for (int i = 0; i < birth_sign_pos.length; i++) {
            int i_order = sign_display_orders[i];
            if (mode == ChartMode.NATAL_MODE
                    && (i_order >= sign_show_array.length || sign_show_array[i_order] == 0))
                continue;
            if (!Calculate.isValid(birth_sign_pos[i_order]))
                continue;
            int i_body = planets[i_order];
            double degree = birth_sign_pos[i_order];
            for (int j = 0; j < aspects_degree.length; j++) {
                if (aspects_show_array[j] == 0)
                    continue;
                for (int p = -1; p <= 1; p += 2) {
                    double angle = p * aspects_degree[j];
                    if (mode != ChartMode.NATAL_MODE)
                        angle += degree;
                    angle = City.normalizeDegree(angle);
                    for (int k = 0; k < birth_sign_pos.length; k++) {
                        int k_order = sign_display_orders[k];
                        if (k_order >= sign_show_array.length
                                || sign_show_array[k_order] == 0)
                            continue;
                        int k_body = planets[k_order];
                        if (i_body >= k_body && mode == ChartMode.NATAL_MODE)
                            continue;
                        double base_degree = birth_sign_pos[k_order];
                        double when = start_ut;
                        for (;;) {
                            if (mode == ChartMode.NATAL_MODE) {
                                when = cal.computePlanetRelativeTransit(i_body,
                                        k_body, when, end_ut, angle, false);
                            } else if (mode == ChartMode.PRIMARY_DIRECTION_MODE) {
                                double pos = City.normalizeDegree(135.0
                                        - birth_sign_azimuth[k_order]
                                        + magnetic_shift);
                                cal.setEquOrbitData(planets[k_order], speed,
                                        ref_ut, pos, birth_sign_alt[k_order]);
                                when = cal.computePlanetTransit(-1, when,
                                        angle, false);
                            } else if (mode == ChartMode.TRANSIT_MODE
                                    || mode == ChartMode.SECONDARY_PROGRESSION_MODE) {
                                when = cal.computePlanetTransit(k_body, when,
                                        angle, false);
                            } else {
                                double val = City.normalizeDegree(angle
                                        + birth_sign_pos[SUN] - base_degree);
                                when = cal.computePlanetTransit(
                                        SweConst.SE_SUN, when, val, false);
                            }
                            if (!Calculate.isValid(when) || when >= end_ut)
                                break;
                            head.add(new Transit(when, angle, i_order, j,
                                    k_order));
                            if (--max_entry <= 0)
                                return;
                            when += Calculate.TRANSIT_INC;
                        }
                    }
                    if (symmetric[j])
                        break;
                }
            }
        }
    }

    private String[] genTransitHTML(Transit[] transits, String country,
            String city, String zone, double start_ut, double end_ut,
            double ref_ut, double ratio, int mode)
    {
        String file_name = FileIO.getTempFileName(".html");
        if (file_name == null)
            return null;
        int entry_per_page = Resource.getInt("transit_entry_per_page");
        int num_page = transits.length / entry_per_page;
        if (num_page * entry_per_page < transits.length)
            num_page++;
        String[] data = new String[5];
        String header, title;
        if (mode == ChartMode.NATAL_MODE) {
            title = Resource.getString((ChartMode
                    .isChartMode(ChartMode.ASTRO_MODE) || ChartMode
                    .isChartMode(ChartMode.PICK_MODE)) ? "aspect_mode_name"
                    : "aspect_now_mode_name");
        } else {
            String[] mode_name = Resource.getStringArray("astro_mode_name");
            title = mode_name[mode] + Resource.getString("method");
        }
        switch (mode) {
            case ChartMode.PRIMARY_DIRECTION_MODE:
                header = "primary_header";
                break;
            case ChartMode.SECONDARY_PROGRESSION_MODE:
                header = "secondary_header";
                break;
            case ChartMode.SOLAR_ARC_MODE:
                header = "solar_arc_header";
                break;
            case ChartMode.TRANSIT_MODE:
                header = "transit_header";
                break;
            default:
                header = (ChartMode.isChartMode(ChartMode.ASTRO_MODE) || ChartMode
                        .isChartMode(ChartMode.PICK_MODE)) ? "aspect_header"
                        : "aspect_now_header";
                break;
        }
        data[0] = Resource.getString(title);
        data[1] = file_name;
        data[2] = country;
        data[3] = city;
        data[4] = zone;
        boolean list = Resource.isExclusive();
        HTMLData.init(file_name, (String) data[0], num_page);
        City c = City.matchCity(city, country, false);
        double[] long_lat = new double[2];
        if (c != null) {
            long_lat[0] = c.getLongitude();
            long_lat[1] = c.getLatitude();
        } else {
            City.parseLongLatitude(city, long_lat);
        }
        for (int page = 0; page < num_page; page++) {
            HTMLData.header();
            String str = "";
            String name = getBirthName();
            if (!name.equals(""))
                str += Resource.getString("name") + ": " + name + "  ";
            str += Resource.getString("sex") + ": "
                    + Resource.getString(getBirthSex() ? "male" : "female")
                    + "|";
            if (c == null) { // longitude/latitude
                str += Resource.getString("location")
                        + ": "
                        + City.formatLongLatitude(long_lat[0], true, true,
                                false)
                        + ", "
                        + City.formatLongLatitude(long_lat[1], false, true,
                                false) + ", " + zone;
            } else {
                str += Resource.getString("location")
                        + ": "
                        + city
                        + ", "
                        + country
                        + " ["
                        + City.formatLongLatitude(long_lat[0], true, true,
                                false)
                        + ", "
                        + City.formatLongLatitude(long_lat[1], false, true,
                                false) + "]";
            }
            HTMLData.paragraph(str);
            if (list)
                tab.appendLine(str);
            int[] date = new int[5];
            Calculate.getDateFromJulianDayUT(start_ut, date);
            str = data[0] + ": " + BaseCalendar.formatDate(zone, date) + " "
                    + Resource.getString("to") + " ";
            Calculate.getDateFromJulianDayUT(end_ut, date);
            str += BaseCalendar.formatDate(zone, date) + "  ["
                    + Integer.toString(transits.length)
                    + Resource.getString("row_count") + "]";
            HTMLData.paragraph(str);
            if (list)
                tab.appendLine(str);
            HTMLData.tableHeader(Resource.getStringArray(header));
            int s = entry_per_page * page;
            int e = Math.min(s + entry_per_page, transits.length);
            for (int i = s; i < e; i++) {
                Transit transit = transits[i];
                transit.jd_ut = ref_ut + (transit.jd_ut - ref_ut) / ratio;
                Calculate.getDateFromJulianDayUT(transit.jd_ut, date);
                BaseCalendar.addZoneOffset(zone, date, 0, true);
                double pos1, pos2;
                if (mode == ChartMode.NATAL_MODE) {
                    pos1 = cal.compute(transit.jd_ut,
                            planets[transit.from_index]);
                    pos2 = cal
                            .compute(transit.jd_ut, planets[transit.to_index]);
                } else {
                    pos1 = birth_sign_pos[transit.from_index];
                    pos2 = transit.pos;
                }
                HTMLData.tableRow(
                        transit.jd_ut,
                        BaseCalendar.formatDate(date, false, true),
                        signs[transit.from_index]
                                + ": "
                                + cal.formatDegree(pos1, null,
                                        full_stellar_signs, "", false, false),
                        aspects_sign[transit.aspect_index],
                        signs[transit.to_index]
                                + ": "
                                + cal.formatDegree(pos2, null,
                                        full_stellar_signs, "", false, false));
                if (list) {
                    tab.appendLine(BaseCalendar.formatDate(date, false, true)
                            + " ["
                            + FileIO.formatDouble(transit.jd_ut, 7, 4, true,
                                    false)
                            + "] "
                            + signs[transit.from_index]
                            + ": "
                            + cal.formatDegree(pos1, null, full_stellar_signs,
                                    "", false, false)
                            + " "
                            + aspects_sign[transit.aspect_index]
                            + " "
                            + signs[transit.to_index]
                            + ": "
                            + cal.formatDegree(pos2, null, full_stellar_signs,
                                    "", false, false));
                }
            }
            HTMLData.tableFooter();
            HTMLData.footer();
            if (list)
                tab.appendLine();
        }
        return data;
    }

    public boolean updateLocation(String city, String country)
    {
        City c = ChartMode.isAstroMode(ChartMode.RELATIONSHIP_MODE) ? null
                : City.matchCity(city, country, false);
        if (c != null) {
            cal.setLocation(c.getLongitude(), c.getLatitude());
        } else {
            double[] long_lat = new double[2];
            if (City.parseLongLatitude(city, long_lat)) {
                cal.setLocation(long_lat[0], long_lat[1]);
                return true;
            }
        }
        return false;
    }

    public void getLocation(double[] loc)
    {
        cal.getLocation(loc);
    }

    public void setNoColor(boolean val)
    {
        no_color = val;
    }

    public boolean getNoColor()
    {
        return no_color;
    }

    public void setTimeAdjust(int val)
    {
        time_adjust = val;
    }

    public int[] getWallTime(double ut, String country, String city, String zone)
    {
        updateLocation(city, country);
        cal.getLocation(birth_loc);
        birth_zone = zone;
        int[] date = new int[5];
        // start with desired eight char time
        Calculate.getDateFromJulianDayUT(ut, date);
        if (time_adjust > 0) {
            // get desired universal eight time time (no dst)
            BaseCalendar.pushSetDstAdjust(false);
            BaseCalendar.addZoneOffset(birth_zone, date, 0, true);
            BaseCalendar.popDstAdjust();
            // account for solar apparent time difference
            double diff = (time_adjust > 1) ? cal.getLATDateFromDate(date)
                    : 0.0;
            // convert to longitude adjusted time
            int[] t_date = (int[]) date.clone();
            BaseCalendar.formatDate(birth_loc[0], t_date, date, diff, true,
                    true);
        }
        // adjust for dst
        if (BaseCalendar.getDstAdjust())
            BaseCalendar.addZoneOffset(birth_zone, date, 1, true);
        return date;
    }

    public void setShowNow(boolean show)
    {
        show_now_set = show;
    }

    public boolean getShowNow()
    {
        return show_now_set;
    }

    public void setShowAspects(boolean show)
    {
        show_aspects_set = show;
    }

    public boolean getShowAspects()
    {
        return show_aspects_set;
    }

    public void setShowGauquelin(boolean show)
    {
        show_gauquelin_set = show;
    }

    public boolean getShowGauquelin()
    {
        return show_gauquelin_set;
    }

    public void setShowFixstar(boolean show)
    {
        show_fixstar_set = show;
    }

    public boolean getShowFixstar()
    {
        return show_fixstar_set;
    }

    public void setShowHoriz(boolean show)
    {
        show_horiz_set = show;
    }

    public boolean getShowHoriz()
    {
        return show_horiz_set;
    }

    public SearchRecord[] getEclipseData()
    {
        return new SearchRecord[] { solar_eclipse, lunar_eclipse };
    }

    public void setEclipseData(SearchRecord[] array)
    {
        if (array != null) {
            solar_eclipse = array[0];
            lunar_eclipse = array[1];
        } else {
            solar_eclipse = lunar_eclipse = null;
        }
    }

    public String getPlanetPos(int planet_no, double pos)
    {
        return signs[planet_no]
                + ": "
                + cal.formatDegree(pos, stellar_sign_pos, full_stellar_signs,
                        "  ", false, false);
    }

    public Hashtable[] getDisplayTable()
    {
        Hashtable[] array = new Hashtable[2];
        array[0] = (Hashtable) master_table.clone();
        array[1] = (ChartMode.isChartMode(ChartMode.PICK_MODE) && Resource
                .getPrefInt("enable_fixstar") != 0) ? ((Hashtable) fixstar_table
                .clone()) : null;
        return array;
    }

    public void updateDisplayTable(Hashtable[] table)
    {
        if (table == null) {
            Resource.removePref("master_display");
            Resource.removePref("fixstar_display");
            loadMasterTable();
        } else {
            master_table = table[0];
            saveDisplayList(master_table, "master_display");
            if (table[1] != null) {
                fixstar_table = table[1];
                saveDisplayList(fixstar_table, "fixstar_display");
            }
        }
    }

    private void saveDisplayList(Hashtable table, String name)
    {
        int i = 0;
        String str = null;
        for (Enumeration e = table.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = (String) table.get(key);
            if (i++ == 0)
                str = val;
            else
                str += "," + val;
        }
        Resource.putPrefString(name, str);
    }

    public String[] getSignArray()
    {
        return signs;
    }

    public double[] getSignDegreeShiftArray()
    {
        return (double[]) sign_pos_shift.clone();
    }

    public boolean[] getSignRevFlipArray()
    {
        return (boolean[]) sign_rev_flip.clone();
    }

    public double[] getCuspArray()
    {
        if (birth_cusp_override != null)
            return (double[]) birth_cusp_override.clone();
        else
            return (double[]) birth_cusp.clone();
    }

    public String getEightCharOverride()
    {
        if (eight_char_override == null)
            return "";
        return birth_poles[YEAR_POLE] + year_char + " "
                + birth_poles[MONTH_POLE] + month_char + " "
                + birth_poles[DAY_POLE] + day_char + " "
                + birth_poles[HOUR_POLE] + hour_char;
    }

    public int[] getOrderArray()
    {
        return sign_display_orders;
    }

    public String[] getAspectSignArray(boolean angle)
    {
        if (angle)
            return Resource.getStringArray("angle_marker_sign");
        else
            return (String[]) aspects_sign.clone();
    }

    public int[] getSignDisplayArray()
    {
        return (int[]) sign_display.clone();
    }

    public int[] getAspectDisplayArray(boolean angle)
    {
        if (angle)
            return Resource.getPrefIntArray("angle_marker_display");
        else
            return (int[]) aspects_display.clone();
    }

    public String[] getSpeedColorNameArray()
    {
        return cal.getSpeedStateNameArray();
    }

    public int[] getSpeedColorArray(boolean use_now)
    {
        return use_now ? ((int[]) now_speed_color.clone())
                : ((int[]) birth_speed_color.clone());
    }

    public int[] getStateColorArray()
    {
        return (int[]) now_state_color.clone();
    }

    public int[] getAspectColorArray(boolean angle)
    {
        if (angle)
            return Resource.getPrefIntArray("angle_marker_color");
        else
            return (int[]) aspects_color.clone();
    }

    public double[] getAspectDegreeArray(boolean angle)
    {
        if (angle)
            return Resource.getDoubleArray("angle_marker_degree");
        else
            return (double[]) aspects_degree.clone();
    }

    public double[] getAspectOrbArray()
    {
        return (double[]) aspects_orb.clone();
    }

    public boolean[] getAngleMarkerEnable()
    {
        boolean[] enable = new boolean[1];
        enable[0] = Resource.getPrefInt("show_angle_marker") != 0;
        return enable;
    }

    public void setSignDegreeShiftArray(double[] shift_array)
    {
        sign_pos_shift = shift_array;
    }

    public void setSignRevFlipArray(boolean[] sign_flip)
    {
        sign_rev_flip = sign_flip;
    }

    public void setCuspArray(double[] cusp)
    {
        boolean diff = false, remove = false;
        for (int i = 1; i < cusp.length; i++) {
            if (cusp[i] < 0.0) {
                remove = true;
                break;
            }
            if (Math.abs(birth_cusp[i] - cusp[i]) > 0.1)
                diff = true;
        }
        if (remove) {
            birth_cusp_override = null;
        } else if (diff) {
            birth_cusp_override = cusp;
        }
    }

    public void setEightCharOverride(String eight_char)
    {
        int[] prev_eight_char_override = eight_char_override;
        eight_char_override = null;
        if (eight_char.equals(""))
            return;
        String[] poles = parseEightChar(eight_char);
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
            int index = FileIO.getArrayIndex(poles[i], year_names);
            if (index < 0) {
                eight_char_override = null;
                return;
            }
            int diff = index - FileIO.getArrayIndex(birth_poles[i], year_names);
            if (prev_eight_char_override != null)
                diff += prev_eight_char_override[i - YEAR_POLE];
            if (diff == 0)
                continue;
            if (eight_char_override == null)
                eight_char_override = new int[4];
            eight_char_override[i - YEAR_POLE] = diff;
        }
    }

    public void setSignDisplayArray(int[] array)
    {
        String prefix = ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? "astro_"
                : "";
        sign_display = array;
        Resource.putPrefIntArray(prefix + "sign_display", array);
    }

    public void setAspectSignArray(String[] array)
    {
        aspects_sign = array;
        Resource.putPrefStringArray("aspects_sign", array);
    }

    public void setAspectDisplayArray(int[] array, boolean angle)
    {
        if (angle) {
            Resource.putPrefIntArray("angle_marker_display", array);
        } else {
            String prefix = ChartMode.getModePrefix();
            aspects_display = array;
            Resource.putPrefIntArray(prefix + "aspects_display", array);
        }
    }

    public void setSpeedColorArray(int[] array, int num_entry, boolean use_now)
    {
        String key = use_now ? "chart_now_ring_speed_color"
                : "chart_birth_ring_speed_color";
        if (array == null) {
            Resource.removePref(key);
            array = Resource.getIntArray(key);
        } else {
            Resource.putPrefIntArray(key, array);
        }
        int[] speed_color = use_now ? now_speed_color : birth_speed_color;
        if (num_entry == 0)
            num_entry = speed_color.length;
        for (int i = 0; i < num_entry; i++)
            speed_color[i] = array[i];
    }

    public void setStateColorArray(int[] array)
    {
        if (array == null) {
            Resource.removePref("chart_now_ring_state_color");
            array = Resource.getIntArray("chart_now_ring_state_color");
        } else {
            Resource.putPrefIntArray("chart_now_ring_state_color", array);
        }
        now_state_color = array;
    }

    public void setAspectColorArray(int[] array, boolean angle)
    {
        if (angle) {
            if (array == null) {
                Resource.removePref("angle_marker_color");
            } else {
                Resource.putPrefIntArray("angle_marker_color", array);
            }
        } else {
            if (array == null) {
                Resource.removePref("aspects_color");
                aspects_color = Resource.getIntArray("aspects_color");
            } else {
                aspects_color = array;
                Resource.putPrefIntArray("aspects_color", array);
            }
        }
    }

    public void setAspectDegreeArray(double[] array)
    {
        aspects_degree = array;
        Resource.putPrefDoubleArray("aspects_degree", array);
    }

    public void setAspectOrbArray(double[] array)
    {
        String prefix = ChartMode.getModePrefix();
        aspects_orb = array;
        Resource.putPrefDoubleArray(prefix + "aspects_orb", array);
    }

    public void setAngleMarkerEnable(boolean[] array)
    {
        Resource.putPrefInt("show_angle_marker", array[0] ? 1 : 0);
    }

    public int[] getDrawSize()
    {
        return draw_size;
    }

    public int[] getAngleMarkerArray(boolean check, boolean print, int scaler)
    {
        if (show_horiz || show_gauquelin)
            return null;
        boolean has_now = (show_now_set
                && !ChartMode.isChartMode(ChartMode.PICK_MODE) && !ChartMode
                .isChartMode(ChartMode.ASTRO_MODE));
        String prefix = ChartMode.isChartMode(ChartMode.ASTRO_MODE) ? "astro_"
                : (ChartMode.isChartMode(ChartMode.PICK_MODE) ? (show_fixstar ? "fixstar_"
                        : (show_compass ? "compass_" : "pick_"))
                        : (show_aspects ? "aspects_" : (has_now ? "full_"
                                : "half_")));
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
            if (ChartMode.isAstroDualRingMode())
                prefix += "dual_";
            else if (!ChartMode.hasReturnRingMode())
                prefix += "basic_";
        }
        if (!Resource.hasKey(prefix + "angle_marker"))
            return null;
        if (check)
            return planets; // any data
        double factor = scaler;
        if (print && Resource.hasKey(prefix + "ring_print_scaler")) {
            factor *= Resource.getDouble(prefix + "ring_print_scaler");
        }
        double[] ring_pos = Resource.getDoubleArray(prefix + "angle_marker");
        int[] array = new int[ring_pos.length];
        for (int i = 0; i < ring_pos.length; i++) {
            array[i] = (int) (ring_pos[i] * factor + 0.5);
        }
        return array;
    }

    public String[] getPoleDateData(int year, String eight_char)
    {
        String[] poles = parseEightChar(eight_char);
        if (poles == null)
            return null;
        boolean change_setting = false;
        LinkedList head = searchPoleDates(year, poles);
        if (head == null) {
            int switch_day_at_11_pm = Resource
                    .getPrefInt("switch_day_at_11_pm");
            Resource.putPrefInt("switch_day_at_11_pm",
                    (switch_day_at_11_pm == 0) ? 1 : 0);
            head = searchPoleDates(year, poles);
            Resource.putPrefInt("switch_day_at_11_pm", switch_day_at_11_pm);
            if (head == null)
                return null;
            change_setting = true;
        }
        return genPoleDateHTML(head, poles, change_setting);
    }

    private String[] parseEightChar(String eight_char)
    {
        String[] map_pole_char = Resource.getStringArray("map_pole_char");
        for (int i = 0; i < eight_char.length(); i++) {
            for (int j = 0; j < map_pole_char.length; j++) {
                eight_char = eight_char.replaceAll(
                        map_pole_char[j].substring(0, 1),
                        map_pole_char[j].substring(2, 3));
            }
        }
        StringTokenizer st = new StringTokenizer(eight_char,
                Resource.getString("pole_break_char"));
        String[] poles = new String[6];
        if (st.countTokens() == 1 && eight_char.length() == 8) {
            poles[YEAR_POLE] = eight_char.substring(0, 2);
            poles[MONTH_POLE] = eight_char.substring(2, 4);
            poles[DAY_POLE] = eight_char.substring(4, 6);
            poles[HOUR_POLE] = eight_char.substring(6);
        } else {
            if (st.countTokens() != 4)
                return null;
            poles[YEAR_POLE] = st.nextToken();
            poles[MONTH_POLE] = st.nextToken();
            poles[DAY_POLE] = st.nextToken();
            poles[HOUR_POLE] = st.nextToken();
        }
        // hour char may be in simplified or traditional chinese
        if (poles[HOUR_POLE].length() == 3)
            poles[HOUR_POLE] = poles[HOUR_POLE].substring(0, 2);
        // add wild character if needed
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
            if (poles[i].length() == 1)
                poles[i] = "*" + poles[i];
        }
        return poles;
    }

    private String[] genPoleDateHTML(LinkedList head, String[] poles,
            boolean change_setting)
    {
        String file_name = FileIO.getTempFileName(".html");
        if (file_name == null)
            return null;
        boolean list = Resource.isExclusive();
        String[] data = new String[2];
        data[0] = Resource.getString("eight_char_title");
        data[1] = file_name;
        HTMLData.init(file_name, (String) data[0], 1);
        HTMLData.header();
        String str = Resource.getString("eight_characters") + ": "
                + poles[YEAR_POLE] + " " + poles[MONTH_POLE] + " "
                + poles[DAY_POLE] + " " + poles[HOUR_POLE];
        HTMLData.paragraph(str);
        if (list)
            tab.appendLine(str);
        if (change_setting) {
            HTMLData.paragraph("***"
                    + Resource.getString((Resource
                            .getPrefInt("switch_day_at_11_pm") == 0) ? "need_to_select"
                            : "need_to_deselect")
                    + Resource.getString("dialog_time_switch_day"));
        }
        str = Integer.toString(head.size()) + Resource.getString("row_count");
        if (list)
            tab.appendLine(str);
        HTMLData.paragraph(str);
        boolean has_wild = poles[MONTH_POLE].startsWith("*")
                || poles[HOUR_POLE].startsWith("*");
        HTMLData.tableHeader(Resource
                .getStringArray(has_wild ? "eight_char_wild_header"
                        : "eight_char_header"));
        int[] date = new int[5];
        for (ListIterator iter = head.listIterator(); iter.hasNext();) {
            SearchRecord record = (SearchRecord) iter.next();
            Calculate.getDateFromJulianDayUT(record.getTime(), date);
            HTMLData.tableRow(record.getTime(), BaseCalendar.formatDate(date,
                    false, true), has_wild ? record.getData() : null, null,
                    null);
            if (list) {
                tab.appendLine(BaseCalendar.formatDate(date, false, true)
                        + " ["
                        + FileIO.formatDouble(record.getTime(), 7, 4, true,
                                false) + "] "
                        + (has_wild ? record.getData() : ""));
            }
        }
        HTMLData.tableFooter();
        HTMLData.footer();
        if (list)
            tab.appendLine();
        return data;
    }

    private LinkedList searchPoleDates(int year, String[] poles)
    {
        int hour = 2 * FileIO.getArrayIndex(poles[HOUR_POLE].substring(1, 2),
                earth_pole_names);
        if (hour < 0)
            return null;
        int[] date = new int[5], t_date = new int[5];
        date[1] = date[2] = 5;
        date[3] = date[4] = 0;
        String[] t_poles = new String[6];
        String[] wild_pole_names = new String[6];
        boolean[] wild_poles = new boolean[6];
        LinkedList head = new LinkedList();
        boolean ephe_mode = cal.getEphMode();
        cal.setEphMode(true);
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++)
            wild_poles[i] = poles[i].startsWith("*");
        int s_year, e_year;
        if (year < 0) {
            s_year = Math.max(year, -5390);
            e_year = 0;
        } else {
            s_year = 0;
            e_year = Math.min(year, 5390);
        }
        for (date[0] = e_year; date[0] >= s_year; date[0]--) {
            // match year
            String year_name = year_names[cal.getChineseYear(date[0]) - 1];
            if (wild_poles[YEAR_POLE]) {
                if (!poles[YEAR_POLE].endsWith(year_name.substring(1)))
                    continue;
            } else {
                if (!poles[YEAR_POLE].equals(year_name))
                    continue;
            }
            // match month
            int y, m1, m2, index;
            String str = year_name.substring(0, 1);
            for (y = 0; y < sky_pole_names.length; y++) {
                if (sky_pole_names[y].equals(str))
                    break;
            }
            for (index = 0; index < 12; index++) {
                m1 = index + month_sky_pole_shifts[y];
                while (m1 >= sky_pole_names.length)
                    m1 -= sky_pole_names.length;
                m2 = index + month_earth_pole_shift;
                while (m2 >= earth_pole_names.length)
                    m2 -= earth_pole_names.length;
                if (wild_poles[MONTH_POLE]) {
                    if (poles[MONTH_POLE].endsWith(earth_pole_names[m2]))
                        break;
                } else {
                    if (poles[MONTH_POLE].equals(sky_pole_names[m1]
                            + earth_pole_names[m2]))
                        break;
                }
            }
            if (index >= 12)
                continue;
            // match day
            double start_ut, end_ut, mark_start_ut, mark_end_ut;
            double[] s_terms;
            boolean recompute = false;
            if (index == 11) {
                date[0]++;
                s_terms = cal.computeSolarTerms(date);
                date[0]--;
                if (s_terms == null)
                    continue;
                start_ut = s_terms[1];
                end_ut = s_terms[3];
            } else {
                s_terms = cal.computeSolarTerms(date);
                if (s_terms == null)
                    continue;
                start_ut = s_terms[2 * index + 3];
                end_ut = s_terms[2 * index + 5];
                if (index == 10)
                    recompute = true;
            }
            mark_start_ut = start_ut + Calculate.MINUTE;
            mark_end_ut = end_ut - Calculate.MINUTE;
            start_ut -= 2.0;
            end_ut += 2.0;
            Calculate.getDateFromJulianDayUT(start_ut, t_date);
            t_date[3] = hour;
            t_date[4] = 0;
            start_ut = Calculate.getJulianDayUT(t_date);
            day_pole_base[3] = t_date[3];
            day_pole_base[4] = t_date[4];
            int s_offset = cal.getDifferenceInDays(day_pole_base, t_date);
            Calculate.getDateFromJulianDayUT(end_ut, t_date);
            t_date[3] = hour;
            t_date[4] = 0;
            end_ut = Calculate.getJulianDayUT(t_date);
            day_pole_base[3] = t_date[3];
            day_pole_base[4] = t_date[4];
            int e_offset = cal.getDifferenceInDays(day_pole_base, t_date);
            for (int i = s_offset; i <= e_offset; i++) {
                double ut;
                int offset = i % 60;
                if (offset < 0)
                    offset += 60;
                year_name = year_names[offset];
                if (wild_poles[DAY_POLE]) {
                    if (!year_name.endsWith(poles[DAY_POLE].substring(1)))
                        continue;
                } else {
                    if (!year_name.equals(poles[DAY_POLE]))
                        continue;
                }
                ut = start_ut + (double) (i - s_offset);
                // match hour
                str = year_name.substring(0, 1);
                for (y = 0; y < sky_pole_names.length; y++) {
                    if (sky_pole_names[y].equals(str))
                        break;
                }
                index = (hour + 1) / 2;
                while (index >= earth_pole_names.length)
                    index -= earth_pole_names.length;
                m1 = index + hour_sky_pole_shifts[y];
                while (m1 >= sky_pole_names.length)
                    m1 -= sky_pole_names.length;
                m2 = index + hour_earth_pole_shift;
                while (m2 >= earth_pole_names.length)
                    m2 -= earth_pole_names.length;
                // after 11:00pm and before 12:00am, sky pole is from next day
                boolean use_alt_m1 = false;
                if (hour == 0
                        && Resource.getPrefInt("switch_day_at_11_pm") == 0) {
                    int alt_y = y + 1;
                    if (alt_y == sky_pole_names.length)
                        alt_y = 0;
                    int alt_m1 = index + hour_sky_pole_shifts[alt_y];
                    while (alt_m1 >= sky_pole_names.length)
                        alt_m1 -= sky_pole_names.length;
                    if (wild_poles[HOUR_POLE]) {
                        if (poles[HOUR_POLE].endsWith(earth_pole_names[m2])) {
                            // set to 11:30pm
                            ut += 23.5 / 24.0;
                            use_alt_m1 = true;
                        }
                    } else {
                        if (poles[HOUR_POLE].equals(sky_pole_names[alt_m1]
                                + earth_pole_names[m2])) {
                            // set to 11:30pm
                            ut += 23.5 / 24.0;
                            use_alt_m1 = true;
                        }
                    }
                }
                if (!use_alt_m1) {
                    if (wild_poles[HOUR_POLE]) {
                        if (!poles[HOUR_POLE].endsWith(earth_pole_names[m2])) {
                            continue;
                        }
                    } else {
                        if (!poles[HOUR_POLE].equals(sky_pole_names[m1]
                                + earth_pole_names[m2])) {
                            continue;
                        }
                    }
                }
                // month may change any time, need to make sure it is in the
                // correct
                // month
                ut = Math.max(mark_start_ut, ut);
                ut = Math.min(mark_end_ut, ut);
                // verify just to make sure
                Calculate.getDateFromJulianDayUT(ut, t_date);
                if (recompute) {
                    // need to have next year's solar term to match what
                    // getSolarCalendar expects
                    s_terms = cal.computeSolarTerms(t_date);
                    if (s_terms == null)
                        continue;
                }
                int[] s_date = cal.getSolarCalendar(t_date, s_terms, false);
                chineseCalendar(t_date, t_poles, null, null, null, s_date);
                for (index = YEAR_POLE; index <= HOUR_POLE; index++) {
                    if (wild_poles[index]) {
                        if (!t_poles[index].endsWith(poles[index].substring(1)))
                            break;
                    } else {
                        if (!t_poles[index].equals(poles[index]))
                            break;
                    }
                }
                if (index <= HOUR_POLE)
                    continue;
                for (index = YEAR_POLE; index <= HOUR_POLE; index++) {
                    if (wild_poles[index]) {
                        if (wild_pole_names[index] == null)
                            wild_pole_names[index] = t_poles[index];
                        else if (!wild_pole_names[index].equals(t_poles[index]))
                            wild_pole_names[index] = poles[index];
                    }
                }
                head.add(new SearchRecord(ut, t_poles[YEAR_POLE] + " "
                        + t_poles[MONTH_POLE] + " " + t_poles[DAY_POLE] + " "
                        + t_poles[HOUR_POLE]));
                // only 1 possible day with a month unless wild character is
                // used
                if (!wild_poles[DAY_POLE])
                    break;
            }
        }
        cal.setEphMode(ephe_mode);
        for (int i = YEAR_POLE; i <= HOUR_POLE; i++) {
            if (wild_pole_names[i] != null)
                poles[i] = wild_pole_names[i];
        }
        return head.isEmpty() ? null : head;
    }

    public String[] getSearchResult(String country, String city, String zone,
            String mode_name, boolean local)
    {
        double start_ut, end_ut, degree;
        LinkedList head;
        int month = Resource.getPrefInt("search_period");
        if (month <= 0)
            return null;
        boolean forward = Resource.getPrefInt("search_forward") != 0;
        int[] date = new int[5];
        BaseCalendar.auditDay(Resource.getPrefString("search_date"), date);
        if (local)
            BaseCalendar.addZoneOffset(date, false);
        else
            BaseCalendar.addZoneOffset(zone, date, 0, false);
        if (forward) {
            start_ut = Calculate.getJulianDayUT(date);
            BaseCalendar.addTime(date, Calendar.MONTH, month);
            end_ut = Calculate.getJulianDayUT(date);
        } else {
            end_ut = Calculate.getJulianDayUT(date);
            BaseCalendar.addTime(date, Calendar.MONTH, -month);
            start_ut = Calculate.getJulianDayUT(date);
        }
        if (mode_name == "azimuth") {
            degree = Resource.getPrefDouble("search_degree");
            if (true_north)
                degree = City.normalizeDegree(degree - magnetic_shift);
            head = cal.computePlanetAzimuth(initSunComputation(), start_ut,
                    end_ut, degree,
                    Resource.getPrefDouble("azimuth_max_speed"),
                    true_north ? 0.0 : magnetic_shift, quick_azimuth, forward);
        } else {
            boolean solar = mode_name == "eclipse_solar";
            head = solar ? cal
                    .computeSolarEclipse(start_ut, end_ut, false, forward,
                            Resource.getPrefInt("eclipse_solar_anywhere") != 0)
                    : cal.computeLunarEclipse(start_ut, end_ut, false, forward);
            degree = -1.0;
        }
        return genSearchResultHTML(head, start_ut, end_ut, country, city, zone,
                degree, mode_name);
    }

    private String[] genSearchResultHTML(LinkedList head, double start_ut,
            double end_ut, String country, String city, String zone,
            double degree, String mode_name)
    {
        String file_name = FileIO.getTempFileName(".html");
        if (file_name == null)
            return null;
        boolean list = Resource.isExclusive();
        boolean anywhere = (mode_name == "eclipse_solar") ? (Resource
                .getPrefInt("eclipse_solar_anywhere") != 0) : false;
        String[] data = new String[5];
        data[0] = Resource.getString(mode_name + "_title");
        data[1] = file_name;
        data[2] = country;
        data[3] = city;
        data[4] = zone;
        if (list)
            tab.appendLine(data[0]);
        HTMLData.init(file_name, (String) data[0], 1);
        HTMLData.header();
        if (!anywhere) {
            City c = City.matchCity(city, country, false);
            double[] long_lat = new double[2];
            if (c != null) {
                long_lat[0] = c.getLongitude();
                long_lat[1] = c.getLatitude();
            } else {
                City.parseLongLatitude(city, long_lat);
            }
            String str;
            if (c == null) { // longitude/latitude
                str = Resource.getString("location")
                        + ": "
                        + City.formatLongLatitude(long_lat[0], true, true,
                                false)
                        + ", "
                        + City.formatLongLatitude(long_lat[1], false, true,
                                false) + ", " + zone;
            } else {
                str = Resource.getString("location")
                        + ": "
                        + city
                        + ", "
                        + country
                        + " ["
                        + City.formatLongLatitude(long_lat[0], true, true,
                                false)
                        + ", "
                        + City.formatLongLatitude(long_lat[1], false, true,
                                false) + "]";
            }
            HTMLData.paragraph(str);
            if (list)
                tab.appendLine(str);
        }
        int[] date = new int[5];
        Calculate.getDateFromJulianDayUT(start_ut, date);
        String str = data[0] + ": " + BaseCalendar.formatDate(zone, date) + " "
                + Resource.getString("to") + " ";
        Calculate.getDateFromJulianDayUT(end_ut, date);
        str += BaseCalendar.formatDate(zone, date) + "  ["
                + Integer.toString(head.size())
                + Resource.getString("row_count") + "]";
        HTMLData.paragraph(str);
        if (list)
            tab.appendLine(str);
        HTMLData.tableHeader(Resource.getStringArray(mode_name
                + (anywhere ? "_anywhere" : "") + "_header"));
        double[] loc = new double[10];
        double shift = true_north ? 0.0 : magnetic_shift;
        int degree_mode = ChartMode.getDegreeMode(quick_azimuth);
        int body = (degree < 0.0) ? 0 : initSunComputation();
        for (ListIterator iter = head.listIterator(); iter.hasNext();) {
            SearchRecord record = (SearchRecord) iter.next();
            String description, altitude, speed, target;
            double ut = record.getTime();
            Calculate.getDateFromJulianDayUT(ut, date);
            String zone_name = zone;
            if (degree < 0.0) {
                description = signs[(mode_name == "eclipse_solar") ? SUN : MOON]
                        + record.getType();
                if (anywhere) {
                    cal.computeSolarEclipseLocation(ut, loc);
                    int index = City.matchCityIndex(loc[0], loc[1],
                            City.MATCH_ERROR_SQ);
                    if (index >= 0) {
                        City c = City.getCity(index);
                        speed = c.getCityName() + ", " + c.getCountryName();
                        zone_name = c.getZoneName();
                        target = Double.toString(ut) + "|"
                                + Integer.toString(index);
                    } else {
                        speed = City.formatLongLatitude(loc[0], true, true,
                                true)
                                + ", "
                                + City.formatLongLatitude(loc[1], false, true,
                                        true);
                        target = Double.toString(ut)
                                + "|"
                                + FileIO.formatDouble(loc[0], 0, 6, false,
                                        false)
                                + ","
                                + FileIO.formatDouble(loc[1], 0, 6, false,
                                        false);
                        index = City.matchCityIndex(loc[0], loc[1],
                                City.ANY_MATCH_ERROR_SQ);
                        if (index >= 0) {
                            City c = City.getCity(index);
                            zone_name = c.getZoneName();
                            speed += ", " + c.getCountryName();
                        }
                        target += "|" + Integer.toString(index);
                    }
                } else {
                    target = Double.toString(ut);
                    speed = null;
                }
                altitude = null;
            } else {
                description = cal.formatDegree(cal.computePlanetAzimuth(body,
                        ut, shift, quick_azimuth), null, null, "", ChartMode
                        .mountainBased(degree_mode), false);
                speed = FileIO.formatDouble(
                        cal.computePlanetAzimuthSpeed(body, ut, quick_azimuth),
                        1, 2, true, false);
                altitude = FileIO.formatDouble(cal.getAltitude(), 2, 1, true,
                        true);
                target = Double.toString(ut);
            }
            BaseCalendar.addZoneOffset(zone_name, date, 0, true);
            HTMLData.tableRow(target,
                    BaseCalendar.formatDate(date, false, true), description,
                    altitude, speed);
            if (list) {
                tab.append(BaseCalendar.formatDate(date, false, true) + " ["
                        + FileIO.formatDouble(ut, 7, 4, true, false) + "]");
                if (description != null)
                    tab.append(", " + description);
                if (altitude != null)
                    tab.append(", " + altitude);
                if (speed != null)
                    tab.append(", " + speed);
                tab.appendLine();
            }
        }
        HTMLData.tableFooter();
        HTMLData.footer();
        if (list)
            tab.appendLine();
        return data;
    }

    public Calculate getCal()
    {
        return cal;
    }
}