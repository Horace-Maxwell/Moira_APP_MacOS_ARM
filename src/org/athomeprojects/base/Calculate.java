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
// From: http://aa.usno.navy.mil/data/docs/JulianDate.html
// The changeover from the Julian calendar to the Gregorian calendar
// occurred in October of 1582, according to the scheme instituted by Pope
// Gregory XIII. Specifically, for dates on or before 4 October 1582, the
// Julian calendar is used; for dates on or after 15 October 1582, the
// Gregorian calendar is used. Thus, there is a ten-day gap in calendar
// dates, but no discontinuity in Julian dates or days of the week: 4
// October 1582 (Julian) is a Thursday, which begins at JD 2299159.5; and 15
// October 1582 (Gregorian) is a Friday, which begins at JD 2299160.5. The
// omission of ten days of calendar dates was necessitated by the
// astronomical error built up by the Julian calendar over its many
// centuries of use, due to its too-frequent leap years.
//
package org.athomeprojects.base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.athomeprojects.swisseph.DblObj;
import org.athomeprojects.swisseph.SweConst;
import org.athomeprojects.swisseph.SweDate;
import org.athomeprojects.swisseph.SwissEph;
import org.athomeprojects.swisseph.SwissephException;

public class Calculate {
    static public final int SE_START = 1000;

    static public final int SE_FORTUNE = 1000;

    static public final int SE_ASC = 1001;

    static public final int SE_MC = 1002;

    static public final int SE_END = 1002;

    static public final int SPEED_NORMAL = 0;

    static public final int SPEED_REVERSE = 1;

    static public final int SPEED_ECLIPSE = 2;

    static public final int SPEED_STATIONARY = 3;

    static public final int SPEED_INVISIBLE = 4;

    static public final int SPEED_SLOW = 5;

    static public final int SPEED_FAST = 6;

    static public final double TRANSIT_INC = 1.0;

    static public final double DEGREE_PRECISION = 1.0;

    static public final double HALF_DEGREE_PRECISION = 0.5;

    static public final double QUARTER_DEGREE_PRECISION = 0.25;

    static public final double MAX_SPEED = 1.0;

    static public final double MINUTE = 1.0 / (24.0 * 60.0);

    static public final double HALF_MINUTE = 0.5 * MINUTE;

    static public final double INVALID = Double.MIN_VALUE;

    static private final int SWITCH_YEAR = 1582;

    static private final int SWITCH_MONTH = 10;

    static private final int SWITCH_DAY = 15;

    static private final double SWITCH_UT = 2299160.5;

    static private final double BEIJING_TIME_OFFSET = 8.0 / 24.0;

    private final double MIN_UT = -247640.0;

    private final double MAX_UT = 3690082.0;

    private final double ONE_HOUR = 1.0 / 24.0;

    private final double REJECT_DEGREE_PRECISION = 1.0;

    // sidereal day = 23 hours 56 minutes on every day of the year
    private final double TO_SIDEREAL_SPEED = 360.0 / 360.98564736629;

    private final double TO_SIDEREAL_OFFSET = 1.0 / 360.98564736629;

    private final double JUMP_SPEED = 1.2;

    private final double JUMP_PERIOD = 30.0;

    private final double TIME_PERIOD = 1.05;

    private final double TIME_PRECISION = 1.0 / (24.0 * 60.0 * TIME_PERIOD);

    private final double PERIOD_RANGE = 0.5;

    private final double TRUE_NODE_AVERAGE_SPEED = -0.05299;

    private final double NEWTON_DEGREE_PRECISION = 0.01;

    private final int NEWTON_MAX_ITER = 100;

    private int[] sidereal_systems = { SweConst.SE_SIDM_FAGAN_BRADLEY,
            SweConst.SE_SIDM_LAHIRI, SweConst.SE_SIDM_YUKTESHWAR,
            SweConst.SE_SIDM_RAMAN, SweConst.SE_SIDM_JN_BHASIN,
            SweConst.SE_SIDM_DELUCE, SweConst.SE_SIDM_USHASHASHI,
            SweConst.SE_SIDM_KRISHNAMURTI, SweConst.SE_SIDM_DJWHAL_KHUL,
            SweConst.SE_SIDM_YUKTESHWAR, SweConst.SE_SIDM_HIPPARCHOS,
            SweConst.SE_SIDM_SASSANIAN, SweConst.SE_SIDM_BABYL_KUGLER1,
            SweConst.SE_SIDM_BABYL_KUGLER2, SweConst.SE_SIDM_BABYL_KUGLER3,
            SweConst.SE_SIDM_BABYL_ETPSC, SweConst.SE_SIDM_BABYL_HUBER };

    private int ephe_flag, house_system_index;

    private double julian_day_ut, julian_day, mountain_offset;

    static private SweDate jdate = new SweDate();

    private SwissEph eph;

    private boolean computed, leap_month, day_fortune_mode, sidereal_mode;

    private double sun_pos, moon_pos;

    private double[] stationary_gap, invisible_gap, slow_speed, fast_speed;

    private double[] location = new double[3];

    private double[] computation = new double[6];

    private double[] ascmc = new double[10];

    private double[] azimuth = new double[3];

    private double[] pheno = new double[20]; // required size

    private double[] orbit_data = new double[4];

    private double[] temp_cusp = new double[13];

    private String[] zodiac, full_zodiac, zodiac_name, speed_state,
            mountain_name, house_system_char;

    private String correction_key;

    private boolean equatorial_orbit;

    private int orbit_body;

    private Hashtable load_table = new Hashtable();

    public Calculate()
    {
        eph = new SwissEph();
        julian_day = julian_day_ut = INVALID;
        computed = false;
        ephe_flag = SweConst.SEFLG_SWIEPH;
        location[0] = location[1] = location[2] = 0.0;
        setLocation(location);
        eph.swe_set_ephe_path(AppRuntime.getEphemerisSearchPath());
    }

    public void loadResource()
    {
        zodiac = Resource.getStringArray("zodiac");
        full_zodiac = Resource.getStringArray("full_zodiac");
        speed_state = Resource.getStringArray("speed_state");
        house_system_char = Resource.getStringArray("house_system_char");
        zodiac_name = Resource.getStringArray(ChartMode
                .isChartMode(ChartMode.ASTRO_MODE) ? "astro_zodiac_marker"
                : "zodiac_marker");
        mountain_name = Resource.getStringArray("mountain_signs");
        stationary_gap = Resource.getDoubleArray("stationary_gap");
        invisible_gap = Resource.getDoubleArray("invisible_gap");
        slow_speed = Resource.getDoubleArray("slow_speed");
        fast_speed = Resource.getDoubleArray("fast_speed");
        correction_key = Resource.getString("fixstar_equ_adjustments");
        setTopocentricMode(false, false);
    }

    public void setEphMode(boolean use_moseph)
    {
        ephe_flag &= ~(SweConst.SEFLG_MOSEPH | SweConst.SEFLG_SWIEPH);
        ephe_flag |= use_moseph ? SweConst.SEFLG_MOSEPH : SweConst.SEFLG_SWIEPH;
        if (use_moseph)
            eph.initSwephMosh();
        else
            eph.swe_set_ephe_path(AppRuntime.getEphemerisSearchPath());
    }

    public boolean getEphMode()
    {
        return (ephe_flag & SweConst.SEFLG_MOSEPH) == SweConst.SEFLG_MOSEPH;
    }

    public void setTopocentricMode(boolean override, boolean val)
    {
        boolean set = override ? val
                : (Resource.getPrefInt("topocentric") != 0);
        if (set) {
            ephe_flag |= SweConst.SEFLG_TOPOCTR;
            location[2] = (double) Resource.getPrefInt("altitude");
        } else {
            ephe_flag &= ~SweConst.SEFLG_TOPOCTR;
            location[2] = 0.0;
        }
    }

    public void setChartMode()
    {
        if (ChartMode.isChartMode(ChartMode.ASTRO_MODE)
                && Resource.getPrefInt("astro_system_mode") != 0) {
            sidereal_mode = true;
            eph.swe_set_sid_mode(sidereal_systems[Resource
                    .getPrefInt("astro_sidereal_index")], 0.0, 0.0);
        } else if (ChartMode.isChartMode(ChartMode.SIDEREAL_MODE)
                || ChartMode.isChartMode(ChartMode.PICK_MODE)
                && Resource.getPrefInt("pick_sidereal_mode") != 0) {
            sidereal_mode = true;
            if (Resource.hasKey("ayanamsha_base_degree")
                    && Resource.hasKey("ayanamsha_base_date")) {
                eph.swe_set_sid_mode(SweConst.SE_SIDM_USER,
                        getJulianDayUT(Resource
                                .getIntArray("ayanamsha_base_date")), Resource
                                .getDouble("ayanamsha_base_degree"));
            } else {
                eph.swe_set_sid_mode(SweConst.SE_SIDM_FAGAN_BRADLEY, 0.0, 0.0);
            }
        } else {
            sidereal_mode = false;
        }
        zodiac_name = Resource.getStringArray(ChartMode
                .isChartMode(ChartMode.ASTRO_MODE) ? "astro_zodiac_marker"
                : "zodiac_marker");
    }

    public double getAyanamsha()
    {
        if (Resource.hasKey("ayanamsha_base_degree")
                && Resource.hasKey("ayanamsha_base_date")) {
            double offset = Resource.getDouble("ayanamsha_base_degree");
            eph.swe_set_sid_mode(
                    SweConst.SE_SIDM_USER,
                    getJulianDayUT(Resource.getIntArray("ayanamsha_base_date")),
                    offset);
            return eph.swe_get_ayanamsa_ut(julian_day_ut);
        } else {
            return 0.0;
        }
    }

    public boolean setJulianDay(int[] date)
    {
        boolean success = setJulianDay(getJulianDayUT(date));
        if (julian_day_ut < MIN_UT || julian_day_ut > MAX_UT)
            success = false;
        return success;
    }

    public boolean setJulianDay(double jd_ut)
    {
        computed = false;
        julian_day_ut = jd_ut;
        if (julian_day_ut != INVALID) {
            julian_day = julian_day_ut + SweDate.getDeltaT(julian_day_ut);
            return true;
        } else {
            julian_day = INVALID;
            return false;
        }
    }

    static public double getJulianDayFromUT(double ut)
    {
        return ut + SweDate.getDeltaT(ut);
    }

    static public double getJulianDayUT(int[] date)
    {
        double d_hour = ((double) date[3]) + ((double) date[4]) / 60.0;
        boolean cal_type = (date[0] < SWITCH_YEAR || date[0] == SWITCH_YEAR
                && (date[1] < SWITCH_MONTH || date[1] == SWITCH_MONTH
                        && date[2] < SWITCH_DAY)) ? SweDate.SE_JUL_CAL
                : SweDate.SE_GREG_CAL;
        if (jdate.checkDate(date[0], date[1], date[2], d_hour, cal_type)) {
            return SweDate.getJulDay(date[0], date[1], date[2], d_hour,
                    cal_type);
        } else {
            return INVALID;
        }
    }

    static public void getDateFromJulianDayUT(double jd_ut, int[] date)
    {
        jdate.setCalendarType((jd_ut < SWITCH_UT) ? SweDate.SE_JUL_CAL
                : SweDate.SE_GREG_CAL, SweDate.SE_KEEP_JD);
        jdate.setJulDay(jd_ut + HALF_MINUTE);
        date[0] = jdate.getYear();
        date[1] = jdate.getMonth();
        date[2] = jdate.getDay();
        double hour = jdate.getHour();
        date[3] = (int) Math.floor(hour);
        date[4] = (int) Math.floor(60.0 * (hour - date[3]));
    }

    public double getLATDateFromDate(int[] date)
    {
        if ((ephe_flag & SweConst.SEFLG_MOSEPH) == SweConst.SEFLG_MOSEPH)
            return 0.0; // not in Moshier mode
        double ut = getJulianDayUT(date);
        StringBuffer error = new StringBuffer();
        DblObj diff = new DblObj();
        eph.swe_time_equ(ut, diff, error);
        return 1440.0 * diff.val;
    }

    static public void computeMidPoint(DataEntry a, DataEntry b, DataEntry r)
    {
        int[] date_buf = a.getBirthDay();
        String zone = a.getZone();
        BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
        double a_ut = getJulianDayUT(date_buf);
        double[] a_val = new double[2];
        City.parseLongLatitude(a.getCity(), a.getCountry(), a_val);
        date_buf = b.getBirthDay();
        zone = b.getZone();
        BaseCalendar.addZoneOffset(zone, date_buf, 0, false);
        double b_ut = getJulianDayUT(date_buf);
        double[] b_val = new double[2];
        City.parseLongLatitude(b.getCity(), b.getCountry(), b_val);
        getDateFromJulianDayUT(0.5 * (a_ut + b_ut), date_buf);
        r.setBirthDay(date_buf);
        r.setCity(City.formatLongLatitude(0.5 * (a_val[0] + b_val[0]), true,
                true, false)
                + ", "
                + City.formatLongLatitude(0.5 * (a_val[1] + b_val[1]), false,
                        true, false));
        r.setCountry(City.getUnknownCountry());
        r.setZone("GMT");
    }

    public double getJulianDayUT()
    {
        return julian_day_ut;
    }

    public double getJulianDay()
    {
        return julian_day;
    }

    public void setLocation(double[] loc)
    {
        location[0] = loc[0];
        location[1] = loc[1];
        eph.swe_set_topo(location[0], location[1], location[2]);
    }

    public void setLocation(double longitude, double latitude)
    {
        location[0] = longitude;
        location[1] = latitude;
        eph.swe_set_topo(location[0], location[1], location[2]);
    }

    public void getLocation(double[] loc)
    {
        loc[0] = location[0];
        loc[1] = location[1];
    }

    public double getLongitude()
    {
        return location[0];
    }

    public double getLatitude()
    {
        return location[1];
    }

    public int getDifferenceInDays(int[] from_date, int[] to_date)
    {
        return (int) Math.rint(getJulianDayUT(to_date)
                - getJulianDayUT(from_date));
    }

    public double compute(double jd_ut, int body)
    {
        double ut_sav = julian_day_ut;
        julian_day_ut = jd_ut;
        double val = compute(body);
        julian_day_ut = ut_sav;
        return val;
    }

    public double compute(int body)
    {
        if (body >= SE_START && body <= SE_END)
            return computeSpecial(body);
        else if (body < 0)
            return computeOrbit();
        StringBuffer error = new StringBuffer();
        int i_flag = ephe_flag | SweConst.SEFLG_SPEED;
        int o_flag = SweConst.ERR;
        if (sidereal_mode)
            i_flag |= SweConst.SEFLG_SIDEREAL;
        for (int iter = 0; iter < 2; iter++) {
            try {
                o_flag = eph.swe_calc_ut(julian_day_ut, body, i_flag,
                        computation, error);
            } catch (SwissephException e) {
                String index = getEphIndex(e.getMessage());
                if (index != null && loadEphIndex(index))
                    return compute(body);
                computation[0] = INVALID;
                return INVALID;
            }
            if (o_flag != SweConst.NOT_AVAILABLE)
                break;
            if (iter == 0) {
                String index = getEphIndex(error.toString());
                if (index == null || !loadEphIndex(index)) {
                    computation[0] = INVALID;
                    return INVALID;
                }
            }
        }
        computed = o_flag != SweConst.ERR;
        if (!computed)
            computation[0] = INVALID;
        return computation[0];
    }

    public double computeGauquelin(int body)
    {
        StringBuffer error = new StringBuffer();
        int i_flag = ephe_flag | SweConst.SEFLG_SPEED;
        int o_flag = SweConst.ERR;
        if (sidereal_mode)
            i_flag |= SweConst.SEFLG_SIDEREAL;
        double val = INVALID;
        for (int iter = 0; iter < 2; iter++) {
            try {
                DblObj pos = new DblObj();
                o_flag = eph.swe_gauquelin_sector(julian_day_ut, body, null,
                        i_flag, 3, location, 0.0, 20.0, pos, error);
                val = pos.val;
            } catch (SwissephException e) {
                String index = getEphIndex(e.getMessage());
                if (index != null && loadEphIndex(index))
                    return computeGauquelin(body);
                return INVALID;
            }
            if (o_flag != SweConst.NOT_AVAILABLE)
                break;
            if (iter == 0) {
                String index = getEphIndex(error.toString());
                if (index == null || !loadEphIndex(index)) {
                    return INVALID;
                }
            }
        }
        if (o_flag == SweConst.ERR)
            val = INVALID;
        return val;
    }

    // diameter
    public double computePheno(int body)
    {
        StringBuffer error = new StringBuffer();
        int i_flag = ephe_flag | SweConst.SEFLG_SPEED;
        int o_flag = SweConst.ERR;
        if (sidereal_mode)
            i_flag |= SweConst.SEFLG_SIDEREAL;
        pheno[3] = INVALID;
        for (int iter = 0; iter < 2; iter++) {
            try {
                o_flag = eph.swe_pheno_ut(julian_day_ut, body, i_flag, pheno,
                        error);
            } catch (SwissephException e) {
                String index = getEphIndex(e.getMessage());
                if (index != null && loadEphIndex(index))
                    return computePheno(body);
                return INVALID;
            }
            if (o_flag != SweConst.NOT_AVAILABLE)
                break;
            if (iter == 0) {
                String index = getEphIndex(error.toString());
                if (index == null || !loadEphIndex(index)) {
                    return INVALID;
                }
            }
        }
        if (o_flag == SweConst.ERR)
            pheno[3] = INVALID;
        return pheno[3];
    }

    public void setOrbitData(double speed, double base_date, double base_degree)
    {
        orbit_data[0] = speed;
        orbit_data[1] = base_date;
        orbit_data[2] = base_degree;
        orbit_data[3] = 0.0;
        equatorial_orbit = false;
    }

    public void setEquOrbitData(int body, double speed, double base_date,
            double base_azimuth, double base_altitude)
    {
        orbit_body = body;
        // sidereal day = 23 hours 56 minutes on every day of the year
        orbit_data[0] = speed * TO_SIDEREAL_SPEED;
        orbit_data[1] = base_date;
        azimuth[0] = base_azimuth;
        azimuth[1] = base_altitude;
        eph.swe_azalt_rev(base_date, SweConst.SE_HOR2EQU, location, azimuth,
                computation);
        orbit_data[2] = computation[0];
        orbit_data[3] = computation[1];
        equatorial_orbit = true;
    }

    private double computeOrbit()
    {
        double day = getJulianDayUT();
        if (day != INVALID) {
            computed = true;
            double offset = orbit_data[0] * (day - orbit_data[1]);
            double degree = orbit_data[2] + offset;
            if (equatorial_orbit) {
                // account for right ascension passed thru in this time
                // span planet due to it's own orbital speed
                double ut = orbit_data[1] - TO_SIDEREAL_OFFSET * offset;
                compute(ut, orbit_body);
                eclToEqu(ut);
                degree = computation[0] + offset;
            }
            if (sidereal_mode)
                degree -= eph.swe_get_ayanamsa_ut(day);
            computation[0] = City.normalizeDegree(degree);
        } else {
            computed = false;
            computation[0] = INVALID;
        }
        computation[1] = orbit_data[3]; // latitude
        computation[2] = 1.0; // distance in AU
        if (equatorial_orbit)
            equToEcl(day);
        computation[3] = orbit_data[0]; // speed in longitude (degree / day)
        return computation[0]; // longitude
    }

    private double getSpeed()
    {
        return computed ? computation[3] : 0.0;
    }

    public int getEclipseState(boolean sun)
    {
        if (sun) {
            LinkedList head = computeSolarEclipse(julian_day_ut - ONE_HOUR,
                    julian_day_ut + ONE_HOUR, true, false, false);
            if (!head.isEmpty())
                return SPEED_ECLIPSE;
        } else {
            LinkedList head = computeLunarEclipse(julian_day_ut - 0.5,
                    julian_day_ut + 0.5, true, false);
            if (!head.isEmpty())
                return SPEED_ECLIPSE;
        }
        return SPEED_NORMAL;
    }

    // need pre-computed sun position (sun_pos)
    public int getSpeedState(int body, int index)
    {
        double pos = computation[0];
        double speed = computation[3];
        double ut_sav = julian_day_ut;
        julian_day_ut = computeSpeedTransit(body, ut_sav, 0.0, true);
        if (julian_day_ut == INVALID) {
            julian_day_ut = ut_sav;
            return getSpeedState(speed);
        }
        double p_pos = compute(body);
        if (getDegreeGap(pos, p_pos) <= stationary_gap[index]) {
            double p_ut = julian_day_ut;
            julian_day_ut = computeTransit(body, ut_sav,
                    City.normalizeDegree(pos + 180.0), true, true);
            if (julian_day_ut > ut_sav || julian_day_ut < p_ut) {
                julian_day_ut = ut_sav;
                return SPEED_STATIONARY;
            }
        }
        julian_day_ut = computeSpeedTransit(body, ut_sav, 0.0, false);
        if (julian_day_ut == INVALID) {
            julian_day_ut = ut_sav;
            return getSpeedState(speed);
        }
        double n_pos = compute(body);
        if (getDegreeGap(pos, n_pos) <= stationary_gap[index]) {
            double n_ut = julian_day_ut;
            julian_day_ut = computeTransit(body, ut_sav,
                    City.normalizeDegree(pos + 180.0), true, false);
            if (julian_day_ut < ut_sav || julian_day_ut > n_ut) {
                julian_day_ut = ut_sav;
                return SPEED_STATIONARY;
            }
        }
        julian_day_ut = ut_sav;
        if (getDegreeGap(pos, sun_pos) <= invisible_gap[index]) {
            return SPEED_INVISIBLE;
        } else if (speed < 0.0) {
            return SPEED_REVERSE;
        } else if (speed >= fast_speed[index]) {
            return SPEED_FAST;
        } else if (speed <= slow_speed[index]) {
            return SPEED_SLOW;
        } else {
            return SPEED_NORMAL;
        }
    }

    static public double getDegreeGap(double pos1, double pos2)
    {
        double gap = Math.abs(pos1 - pos2);
        if (gap > 180.0)
            gap = 360.0 - gap;
        return gap;
    }

    public int getSpeedState()
    {
        return getSpeedState(getSpeed());
    }

    private int getSpeedState(double speed)
    {
        return (speed < 0.0) ? SPEED_REVERSE : SPEED_NORMAL;
    }

    public String getSpeedStateName(int state, String blank)
    {
        return (state == SPEED_NORMAL) ? blank : speed_state[state];
    }

    public String[] getSpeedStateNameArray()
    {
        return speed_state;
    }

    public void setHouseSystemIndex(int index)
    {
        house_system_index = index;
    }

    public void computeHouses(double[] cusps)
    {
        computeHouses(cusps, julian_day_ut);
    }

    public void computeHouses(double[] cusps, double ut)
    {
        int i_flag = ephe_flag;
        if (sidereal_mode)
            i_flag |= SweConst.SEFLG_SIDEREAL;
        if (eph.swe_houses(ut, i_flag, location[1], location[0],
                house_system_char[house_system_index].charAt(0), cusps, ascmc) != SweConst.OK) {
            ascmc[0] = ascmc[1] = INVALID;
        }
    }

    public void computeHousesFromMidHeaven(double[] cusps, double midheaven)
    {
        double ut_save = julian_day_ut;
        for (;;) {
            computeHouses(cusps);
            double delta = cusps[10] - midheaven;
            if (delta >= 180.0)
                delta = 360.0 - delta;
            else if (delta <= -180.0)
                delta = 360.0 + delta;
            double d_ut = delta / 360.0;
            if (Math.abs(d_ut) <= HALF_MINUTE)
                break;
            julian_day_ut -= d_ut;
        }
        julian_day_ut = ut_save;
    }

    public int[][] computeAspects(double[] f_pos, double[] t_pos,
            double[] aspects_degree, double[] aspects_tolerance)
    {
        int[][] aspects = new int[f_pos.length][t_pos.length];
        for (int i = 0; i < f_pos.length; i++) {
            if (f_pos[i] == INVALID)
                continue;
            for (int j = 0; j < t_pos.length; j++) {
                if (t_pos[j] == INVALID || i == j && f_pos == t_pos)
                    continue;
                double angle = getDegreeGap(f_pos[i], t_pos[j]);
                for (int k = 0; k < aspects_tolerance.length; k++) {
                    if (Math.abs(angle - aspects_degree[k]) <= aspects_tolerance[k]) {
                        aspects[i][j] = k + 1;
                        break;
                    }
                }
            }
        }
        return aspects;
    }

    public void initSpecial(double sun_long, double moon_long, boolean day)
    {
        sun_pos = sun_long;
        moon_pos = moon_long;
        day_fortune_mode = day
                || Resource.getPrefInt("night_fortune_mode") == 0;
    }

    private double computeSpecial(int body)
    {
        computation[0] = INVALID;
        computation[1] = computation[2] = computation[3] = 0.0;
        switch (body) {
            case SE_ASC:
                computation[0] = ascmc[0];
                break;
            case SE_MC:
                computation[0] = ascmc[1];
                break;
            case SE_FORTUNE:
                if (ascmc[0] != INVALID) {
                    double gap = moon_pos - sun_pos;
                    if (!day_fortune_mode)
                        gap = -gap;
                    computation[0] = City.normalizeDegree(ascmc[0] + gap);
                }
                break;
            default:
                break;
        }
        return computation[0];
    }

    public double computeAzimuth(double magnetic_shift)
    {
        if (computation[0] == INVALID)
            return INVALID;
        double t = computation[0];
        if (sidereal_mode)
            computation[0] += eph.swe_get_ayanamsa_ut(julian_day_ut);
        eph.swe_azalt(julian_day_ut, SweConst.SE_ECL2HOR, location, 0.0, 20.0,
                computation, azimuth);
        computation[0] = t;
        azimuth[0] = City.normalizeDegree(135.0 - azimuth[0] + magnetic_shift);
        return azimuth[0];
    }

    public double computeAzimuth(double magnetic_shift, double degree,
            double[] cusps)
    {
        if (degree != INVALID) {
            // need this computation to get altitude
            computeAzimuth(magnetic_shift);
            double ratio = 0.0;
            double last_pos = cusps[12];
            for (int i = 1; i <= 12; i++) {
                double pos = cusps[i];
                boolean in_house;
                if (pos > last_pos) {
                    in_house = degree >= last_pos && degree < pos;
                    if (in_house)
                        ratio = (degree - last_pos) / (pos - last_pos);
                } else {
                    in_house = degree >= last_pos || degree < pos;
                    if (in_house) {
                        degree -= last_pos;
                        if (degree < 0.0)
                            degree += 360.0;
                        ratio = degree / (pos - last_pos + 360.0);
                    }
                }
                if (in_house) {
                    // scale degree within house
                    degree = 30.0 * ratio + 180.0 + (i - 1) * 30.0
                            + magnetic_shift;
                    return City.normalizeDegree(degree);
                }
                last_pos = pos;
            }
        }
        return INVALID;
    }

    public double getAltitude()
    {
        if (computation[0] == INVALID)
            return INVALID;
        return azimuth[1];
    }

    // yuk: a discontinous curve, do a heuristic bisection first, if failed
    // do a linear search
    public double computePlanetAzimuthTransit(int body, double start_ut,
            double degree, double magnetic_shift, double precision,
            boolean quick_azimuth, boolean check_speed, boolean backward,
            boolean no_warn)
    {
        double s, e;
        if (backward) {
            e = start_ut;
            s = e - TIME_PERIOD;
        } else {
            s = start_ut;
            e = s + TIME_PERIOD;
        }
        double ref = computePlanetAzimuth(body, s, magnetic_shift,
                quick_azimuth);
        double deg = (degree > ref) ? (degree - 360.0) : degree;
        for (;;) {
            double m = 0.5 * (s + e);
            double m_val = computePlanetAzimuth(body, m, magnetic_shift,
                    quick_azimuth);
            if (m_val > ref)
                m_val -= 360.0;
            if (Math.abs(deg - m_val) < precision)
                return m;
            if (deg > m_val) {
                e = m;
            } else {
                s = m;
                ref = m_val;
            }
            if (Math.abs(s - e) < TIME_PRECISION)
                break;
        }
        return computePlanetAzimuthTransitSearch(body, start_ut, degree,
                magnetic_shift, precision, quick_azimuth, check_speed, no_warn);
    }

    private double computePlanetAzimuthTransitSearch(int body, double start_ut,
            double degree, double magnetic_shift, double precision,
            boolean quick_azimuth, boolean check_speed, boolean no_warn)
    {
        double s = start_ut;
        double e = s + TIME_PERIOD;
        double best_v = INVALID;
        double l_gap = Double.MAX_VALUE, r_gap = Double.MAX_VALUE;
        for (; s < e; s += TIME_PRECISION) {
            double val = computePlanetAzimuth(body, s, magnetic_shift,
                    quick_azimuth);
            double gap = val - degree;
            if (Math.abs(gap) < precision
                    && check_speed
                    && Math.abs(gap) < DEGREE_PRECISION
                    && computePlanetAzimuthSpeed(body, s, quick_azimuth) > JUMP_SPEED) {
                return s;
            }
            double gap_l, gap_r;
            if (gap < 0) {
                gap_l = gap;
                gap_r = gap + 360.0;
            } else {
                gap_l = gap - 360.0;
                gap_r = gap;
            }
            if (-gap_l < l_gap) {
                l_gap = -gap_l;
                if (l_gap < r_gap)
                    best_v = s;
            }
            if (gap_r < r_gap) {
                r_gap = gap_r;
                if (r_gap < l_gap)
                    best_v = s;
            }
        }
        if (Math.min(l_gap, r_gap) > REJECT_DEGREE_PRECISION) {
            if (!no_warn) {
                DecimalFormat format = new DecimalFormat("#.##");
                String str = Resource.getString("dialog_best_value_found")
                        + " ";
                double val = City.normalizeDegree(315.0 - (degree - l_gap));
                str += format.format(val);
                str += " " + Resource.getString("or") + " ";
                val = City.normalizeDegree(315.0 - (degree + r_gap));
                str += format.format(val);
                Message.warn(str);
            }
            return INVALID;
        } else {
            return best_v;
        }
    }

    public double computePlanetAzimuth(int body, double jd_ut,
            double magnetic_shift, boolean quick_azimuth)
    {
        double ut_sav = julian_day_ut;
        julian_day_ut = jd_ut;
        double val = compute(body);
        if (quick_azimuth) {
            computeHouses(temp_cusp);
            val = computeAzimuth(magnetic_shift, val, temp_cusp);
        } else {
            val = computeAzimuth(magnetic_shift);
        }
        julian_day_ut = ut_sav;
        return val;
    }

    public double computePlanetAzimuthSpeed(int body, double jd_ut,
            boolean quick_azimuth)
    {
        double l_degree = computePlanetAzimuth(body, jd_ut - HALF_MINUTE, 0.0,
                quick_azimuth);
        double u_degree = computePlanetAzimuth(body, jd_ut + HALF_MINUTE, 0.0,
                quick_azimuth);
        double gap = l_degree - u_degree;
        if (gap < -180.0)
            gap += 360.0;
        else if (gap > 180.0)
            gap -= 360.0;
        return gap;
    }

    public double getLocalJulianDayUT(String zone, boolean noon)
    {
        int[] date = new int[5];
        getDateFromJulianDayUT(julian_day_ut, date);
        if (zone == null || zone.equals(City.UNKNOWN_ZONE)) {
            // longitude adjusted
            BaseCalendar.formatDate(location[0], date, date, 0.0, false, true);
        } else {
            BaseCalendar.addZoneOffset(zone, date, 0, true);
        }
        // noon local time
        double jd_ut = julian_day_ut - (date[3] + date[4] / 60.0) / 24.0;
        return noon ? (jd_ut + 0.5) : jd_ut;
    }

    public boolean computeRiseSet(String zone, int body, double[] rise_set)
    {
        // mid-night local time
        double when = getLocalJulianDayUT(zone, false);
        boolean equator = false;
        int i_flag = ephe_flag;
        rise_set[0] = rise_set[1] = INVALID;
        StringBuffer error = new StringBuffer();
        DblObj rise = new DblObj(), set = new DblObj();
        if (eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_RISE,
                location, 0.0, 20.0, rise, error) != SweConst.OK) {
            // change to equator
            double lat_val = location[1];
            location[1] = 0.0;
            eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_RISE,
                    location, 0.0, 20.0, rise, error);
            location[1] = lat_val;
            equator = true;
        }
        if (eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_SET,
                location, 0.0, 20.0, set, error) != SweConst.OK) {
            // change to equator
            double lat_val = location[1];
            location[1] = 0.0;
            eph.swe_rise_trans(when, body, null, i_flag, SweConst.SE_CALC_SET,
                    location, 0.0, 20.0, set, error);
            location[1] = lat_val;
            equator = true;
        }
        rise_set[0] = rise.val;
        rise_set[1] = set.val;
        return equator;
    }

    public boolean isDayBirth(double[] rise_set)
    {
        // in the sky, valid for sun only
        return julian_day_ut >= rise_set[0] && julian_day_ut < rise_set[1];
    }

    public boolean isDayBirthByZone(int hour, int rise_hour, int set_hour)
    {
        hour = toZone(hour);
        rise_hour = toZone(rise_hour);
        set_hour = toZone(set_hour);
        return hour >= rise_hour && hour < set_hour;
    }

    private int toZone(int hour)
    {
        hour = (hour + 1) / 2;
        if (hour == 12)
            hour = 0;
        return hour;
    }

    public double computeStar(StringBuffer name_buf, String equ_key)
    {
        String star_name = name_buf.toString();
        StringBuffer error = new StringBuffer();
        int i_flag = ephe_flag;
        if (sidereal_mode)
            i_flag |= SweConst.SEFLG_SIDEREAL;
        int o_flag = eph.swe_fixstar_ut(name_buf, julian_day_ut, i_flag,
                computation, error);
        if (o_flag == SweConst.ERR) {
            // check for fixed values
            if (star_name.startsWith(",")) {
                try {
                    computation[0] = Double.parseDouble(star_name.substring(1));
                    if (computation[0] < 0.0 || computation[0] >= 360.0)
                        computation[0] = INVALID;
                } catch (NumberFormatException e) {
                    computation[0] = INVALID;
                }
            } else {
                computation[0] = INVALID;
            }
        } else {
            if (equ_key != null) {
                // correction in equatorial coordinate system
                String name = equ_key + correction_key;
                if (Resource.hasKey(name)) {
                    double[] correction = Resource.getDoubleArray(name);
                    if (correction != null && correction.length == 2) {
                        eclToEqu(julian_day_ut);
                        computation[0] = City.normalizeDegree(computation[0]
                                - correction[0]);
                        computation[1] -= correction[1];
                        equToEcl(julian_day_ut);
                    }
                }
            } else {
                String name = name_buf.toString();
                name = name.replaceFirst(".*,", "");
                if (Resource.hasKey(name)) {
                    double correction = Resource.getDouble(name);
                    if (correction != INVALID) {
                        computation[0] = City.normalizeDegree(computation[0]
                                - correction);
                    }
                }
            }
        }
        return computation[0];
    }

    private void eclToEqu(double ut)
    {
        eph.swe_azalt(ut, SweConst.SE_ECL2HOR, location, 0.0, 20.0,
                computation, azimuth);
        eph.swe_azalt_rev(ut, SweConst.SE_HOR2EQU, location, azimuth,
                computation);
    }

    private void equToEcl(double ut)
    {
        eph.swe_azalt(ut, SweConst.SE_EQU2HOR, location, 0.0, 20.0,
                computation, azimuth);
        eph.swe_azalt_rev(ut, SweConst.SE_HOR2ECL, location, azimuth,
                computation);
    }

    // Beijing time
    // 26 solar terms starting from last year's winter solstice
    public double[] computeSolarTerms(int[] birth_date)
    {
        int[] date = (int[]) birth_date.clone();
        double[] solar_terms = new double[26];
        date[0]--;
        date[1] = 12;
        date[2] = 1;
        double when = getJulianDayUT(date);
        setTopocentricMode(true, false);
        for (int i = 0; i < 26; i++) {
            double degree = 15.0 * i - 90.0;
            if (degree < 0.0)
                degree += 360.0;
            when = computeTransit(SweConst.SE_SUN, when, degree, false, false);
            if (when == INVALID) {
                setTopocentricMode(false, false);
                return null;
            }
            solar_terms[i] = BeijingTime(when); // map to Beijing time
            when += 13.0;
        }
        setTopocentricMode(false, false);
        return solar_terms;
    }

    private double computeTransit(int body, double start_ut, double degree,
            boolean sidereal_adjust, boolean backward)
    {
        try {
            int i_flag = ephe_flag | SweConst.SEFLG_TRANSIT_LONGITUDE;
            if (sidereal_adjust && sidereal_mode)
                i_flag |= SweConst.SEFLG_SIDEREAL;
            return eph.getNextTransitUT(body, degree, i_flag, start_ut,
                    backward);
        } catch (SwissephException e) {
            String index = getEphIndex(e.getMessage());
            if (index != null && loadEphIndex(index))
                return computeTransit(body, start_ut, degree, sidereal_adjust,
                        backward);
            return INVALID;
        } catch (IllegalArgumentException e) { // unsupported planets
            return INVALID;
        }
    }

    private double computeRelativeTransit(int i_body, int k_body,
            double start_ut, double end_ut, double offset,
            boolean sidereal_adjust, boolean backward)
    {
        try {
            int i_flag = ephe_flag | SweConst.SEFLG_TRANSIT_LONGITUDE;
            eph.setTransitSearchBound(end_ut);
            double ut = eph.getRelativeTransitUT(k_body, i_body, offset,
                    i_flag, start_ut, backward);
            eph.setTransitSearchBound(0.0);
            return ut;
        } catch (SwissephException e) {
            String index = getEphIndex(e.getMessage());
            if (index != null && loadEphIndex(index))
                return computeRelativeTransit(i_body, k_body, start_ut, end_ut,
                        offset, sidereal_adjust, backward);
            eph.setTransitSearchBound(0.0);
            computation[0] = INVALID;
            return INVALID;
        } catch (IllegalArgumentException e) {
            // unsupported planets or out of bound
            eph.setTransitSearchBound(0.0);
            computation[0] = INVALID;
            return INVALID;
        }
    }

    public double computeSpeedTransit(int body, double start_ut, double speed,
            boolean backward)
    {
        ephe_flag |= SweConst.SEFLG_TRANSIT_SPEED;
        double ut = computeTransit(body, start_ut, speed, true, backward);
        ephe_flag &= ~SweConst.SEFLG_TRANSIT_SPEED;
        return ut;
    }

    public double computePlanetTransit(int body, double start_ut,
            double degree, boolean backward)
    {
        double ut_sav = julian_day_ut;
        double start_degree = compute(body);
        boolean period = Math.abs(start_degree - degree) < PERIOD_RANGE;
        double ut;
        // SwissEPH only support transit computation for real planets
        if (!isRealPlanet(body)) {
            if (period)
                start_ut += backward ? -2922.0 : 2922.0; // 8 years
            ut = computeNewtonRapshonTransit(body, start_ut, degree, backward);
        } else {
            if (period)
                start_ut += backward ? -1.0 : 1.0;
            ut = computeTransit(body, start_ut, degree, true, backward);
        }
        julian_day_ut = ut_sav;
        return ut;
    }

    public double computePlanetRelativeTransit(int i_body, int k_body,
            double start_ut, double end_ut, double offset, boolean backward)
    {
        double ut_sav = julian_day_ut;
        double ut;
        // SwissEPH only support transit computation for real planets
        if (!isRealPlanet(i_body) || !isRealPlanet(k_body)) {
            return INVALID;
        } else {
            ut = computeRelativeTransit(i_body, k_body, start_ut, end_ut,
                    offset, true, backward);
        }
        julian_day_ut = ut_sav;
        return ut;
    }

    private boolean isRealPlanet(int body)
    {
        return !(body < 0 || body == SweConst.SE_TRUE_NODE || body == SweConst.SE_MEAN_APOG);
    }

    // Modified Newton-Raphson with bisection method
    private double computeNewtonRapshonTransit(int body, double start_ut,
            double degree, boolean backward)
    {
        boolean true_node = body == SweConst.SE_TRUE_NODE;
        double val, gap, speed, l_ut = 0.0, u_ut = 0.0;
        double l_gap = Double.MAX_VALUE, u_gap = Double.MAX_VALUE;
        double init_val = INVALID, new_ut = INVALID;
        julian_day_ut = start_ut;
        // starts with Newton-Raphson, when swing out of best bound, switch to
        // bisection
        boolean bisection = false;
        for (int i = 0; i < NEWTON_MAX_ITER; i++) {
            val = compute(body);
            // True node speed fluctuates a lot and Newton's method needs a
            // steady 1st derivative, use average speed instead
            speed = true_node ? TRUE_NODE_AVERAGE_SPEED : getSpeed();
            if (val == INVALID)
                break;
            // since degree goes from 360 back to 0, the curve is discountinous,
            // use simple adjustment to join the curves
            if (init_val == INVALID) {
                if (speed < 0.0)
                    backward = !backward;
                if (backward) {
                    if (val <= degree + NEWTON_DEGREE_PRECISION) {
                        degree -= 360.0;
                    }
                    init_val = val + NEWTON_DEGREE_PRECISION;
                } else {
                    if (val >= degree - NEWTON_DEGREE_PRECISION) {
                        degree += 360.0;
                    }
                    init_val = val - NEWTON_DEGREE_PRECISION;
                }
            }
            if (backward) {
                if (init_val <= val)
                    val -= 360.0;
            } else {
                if (init_val >= val)
                    val += 360.0;
            }
            val -= degree;
            gap = Math.abs(val);
            // keep track of best bound and switch to bisection if swing out of
            // bound
            if (!bisection) {
                if (val < 0.0) {
                    if (gap < l_gap) {
                        l_gap = gap;
                        l_ut = julian_day_ut;
                    } else {
                        bisection = l_ut > 0.0 && u_ut > 0.0;
                        if (bisection)
                            julian_day_ut = l_ut;
                    }
                } else {
                    if (gap < u_gap) {
                        u_gap = gap;
                        u_ut = julian_day_ut;
                    } else {
                        bisection = l_ut > 0.0 && u_ut > 0.0;
                        if (bisection)
                            julian_day_ut = u_ut;
                    }
                }
            }
            if (bisection) { // bisection
                if (val < 0.0) {
                    l_ut = julian_day_ut;
                } else {
                    u_ut = julian_day_ut;
                }
                julian_day_ut = 0.5 * (l_ut + u_ut);
            } else { // Newton-Raphson
                julian_day_ut -= val / speed;
            }
            if (gap <= NEWTON_DEGREE_PRECISION) {
                new_ut = julian_day_ut;
                break;
            }
        }
        return new_ut;
    }

    public double[] computeTransit(int body, double start_ut, double end_ut,
            double degree)
    {
        LinkedList head = new LinkedList();
        double when = start_ut;
        for (;;) {
            when = computeTransit(body, when, degree, true, false);
            if (when == INVALID || when >= end_ut)
                break;
            head.addLast(new Double(when));
            when += TRANSIT_INC;
        }
        if (head.isEmpty())
            return null;
        double[] data = new double[head.size()];
        try {
            ListIterator iter = head.listIterator();
            for (int i = 0; i < data.length; i++)
                data[i] = ((Double) iter.next()).doubleValue();
        } catch (NoSuchElementException e) {
        }
        return data;
    }

    public LinkedList findStarByEquPos(double[] pos)
    {
        LinkedList head = new LinkedList();
        StringBuffer error = new StringBuffer();
        for (int i = 1;; i++) {
            StringBuffer name_buf = new StringBuffer(Integer.toString(i));
            int o_flag = eph.swe_fixstar_ut(name_buf, julian_day_ut, ephe_flag,
                    computation, error);
            if (o_flag == SweConst.ERR)
                break;
            eclToEqu(julian_day_ut);
            double dx = getDegreeGap(pos[0], computation[0]);
            double dy = pos[1] - computation[1];
            head.add(new StarEntry(eph.swe_last_fixstar_entry(), dx * dx + dy
                    * dy));
        }
        if (head.isEmpty())
            return null;
        Collections.sort(head);
        return head;
    }

    public String[] getStarEquPosData(double[] pos, Object obj)
    {
        StarEntry entry = (StarEntry) obj;
        String name = null;
        double mag = 9.9;
        int index = entry.data.indexOf(',');
        if (index >= 0) {
            StringTokenizer st = new StringTokenizer(
                    entry.data.substring(index + 1), ",");
            if (st.countTokens() == 15) {
                name = st.nextToken().trim();
                for (int i = 0; i < 11; i++)
                    st.nextToken();
                mag = FileIO.parseDouble(st.nextToken().trim(), 9.9, false);
            }
        } else {
            return null;
        }
        StringBuffer error = new StringBuffer();
        StringBuffer name_buf = new StringBuffer("," + name);
        int o_flag = eph.swe_fixstar_ut(name_buf, julian_day_ut, ephe_flag,
                computation, error);
        if (o_flag == SweConst.ERR)
            return null;
        eclToEqu(julian_day_ut);
        String[] data = new String[5];
        data[0] = name;
        data[1] = City.formatLongLatitude(computation[0], true, true, false)
                + ", "
                + City.formatLongLatitude(computation[1], false, true, false);
        data[2] = FileIO.formatDouble(mag, 1, 2, true, true);
        double d_x = computation[0] - pos[0];
        double d_y = computation[1] - pos[1];
        if (d_x > 180.0)
            d_x = 360 - d_x;
        data[3] = FileIO.formatDouble(d_x, 2, 2, false, false) + ", "
                + FileIO.formatDouble(d_y, 2, 2, false, false);
        data[4] = FileIO
                .formatDouble(Math.sqrt(entry.error), 2, 2, true, false);
        return data;
    }

    public LinkedList computePlanetAzimuth(int body, double start_ut,
            double end_ut, double degree, double max_speed, double shift,
            boolean quick_azimuth, boolean add_last)
    {
        LinkedList head = new LinkedList();
        int[] date = new int[5];
        for (double ut = start_ut; ut < end_ut;) {
            double n_ut = computePlanetAzimuthTransit(body, ut, degree, shift,
                    QUARTER_DEGREE_PRECISION, quick_azimuth, true, false, true);
            if (n_ut != INVALID) {
                getDateFromJulianDayUT(n_ut, date);
                ut = getJulianDayUT(date);
                double speed = computePlanetAzimuthSpeed(body, ut,
                        quick_azimuth);
                if (speed <= JUMP_SPEED) {
                    if (speed <= max_speed) {
                        SearchRecord record = new SearchRecord(ut,
                                SearchRecord.UNKNOWN);
                        if (add_last)
                            head.addLast(record);
                        else
                            head.addFirst(record);
                    }
                    ut += 0.90;
                    continue;
                }
            }
            double period = JUMP_PERIOD;
            double t_ut;
            do {
                t_ut = Math.min(end_ut, ut + period);
                n_ut = computePlanetAzimuthTransit(body, t_ut, degree, shift,
                        DEGREE_PRECISION, quick_azimuth, false, false, true);
                if (n_ut == INVALID
                        || computePlanetAzimuthSpeed(body, n_ut, quick_azimuth) > max_speed)
                    break;
                period *= 0.5;
            } while (period > TIME_PERIOD);
            ut = t_ut;
        }
        return head;
    }

    public boolean computeSolarEclipseLocation(double ut, double[] pos)
    {
        StringBuffer error = new StringBuffer();
        double[] attr = new double[20];
        return (eph.swe_sol_eclipse_where(ut, ephe_flag, pos, attr, error) != SweConst.ERR);
    }

    public LinkedList computeSolarEclipse(double start_ut, double end_ut,
            boolean win, boolean add_last, boolean anywhere)
    {
        LinkedList head = new LinkedList();
        StringBuffer error = new StringBuffer();
        double[] tret = new double[10], attr = new double[20];
        if (!win)
            loadEphIfNeeded(start_ut, end_ut);
        for (double ut = start_ut; ut < end_ut;) {
            int type;
            if (anywhere) {
                type = eph.swe_sol_eclipse_when_glob(ut, ephe_flag, 0, tret, 0,
                        error);
            } else {
                type = eph.swe_sol_eclipse_when_loc(ut, ephe_flag, location,
                        tret, attr, 0, error);
            }
            if (type < 0 || tret[0] > end_ut)
                return head;
            SearchRecord record = new SearchRecord(
                    tret[0],
                    ((type & SweConst.SE_ECL_TOTAL) == SweConst.SE_ECL_TOTAL) ? SearchRecord.TOTAL_ECLIPSE
                            : (((type & SweConst.SE_ECL_ANNULAR) == SweConst.SE_ECL_ANNULAR) ? SearchRecord.ANNULAR_ECLIPSE
                                    : SearchRecord.PARTIAL_ECLIPSE));
            if (add_last)
                head.addLast(record);
            else
                head.addFirst(record);
            if (win)
                return head;
            ut = tret[0] + 1.0;
        }
        return head;
    }

    public LinkedList computeLunarEclipse(double start_ut, double end_ut,
            boolean win, boolean add_last)
    {
        LinkedList head = new LinkedList();
        StringBuffer error = new StringBuffer();
        double[] tret = new double[10], attr = new double[20];
        if (!win)
            loadEphIfNeeded(start_ut, end_ut);
        for (double ut = start_ut; ut < end_ut;) {
            int type = eph.swe_lun_eclipse_when(ut, ephe_flag, 0, tret, 0,
                    error);
            if (type < 0 || tret[0] > end_ut)
                return head;
            type = eph.swe_lun_eclipse_how(tret[0], ephe_flag, location, attr,
                    error);
            if (type <= 0)
                return head;
            SearchRecord record = new SearchRecord(
                    tret[0],
                    ((type & SweConst.SE_ECL_TOTAL) == SweConst.SE_ECL_TOTAL) ? SearchRecord.TOTAL_ECLIPSE
                            : (((type & SweConst.SE_ECL_PENUMBRAL) == SweConst.SE_ECL_PENUMBRAL) ? SearchRecord.PENUMBRAL_ECLIPSE
                                    : SearchRecord.PARTIAL_ECLIPSE));
            if (add_last)
                head.addLast(record);
            else
                head.addFirst(record);
            if (win)
                return head;
            ut = tret[0] + 1.0;
        }
        return head;
    }

    private void loadEphIfNeeded(double s_ut, double e_ut)
    {
        if (getEphMode())
            return;
        compute(s_ut, SweConst.SE_SUN);
        compute(e_ut, SweConst.SE_SUN);
    }

    private boolean loadEphIndex(String index)
    {
        if ((ephe_flag & SweConst.SEFLG_MOSEPH) != 0) {
            // already in Moseph mode and still failing, must be asteroids
            return false;
        }
        if (index.startsWith("ast")) {
            int n = index.indexOf('\\');
            if (n < 0)
                n = index.indexOf('/');
            String ast_dir_name = index.substring(0, n);
            index = index.substring(n + 1);
            if (load_table.get(index) != null)
                return false; // failed before
            int num = FileIO.parseInt(index.substring(2), 0, true);
            if (!Message.question(Resource.getString("dialog_need_data_3")
                    + " " + num + " "
                    + Resource.getString("dialog_need_data_4"))) {
                load_table.put(index, "t");
                return false;
            }
            String ast_name = index + ".se1";
            String url_base = Resource.getString("asteroid_url");
            File ast_file = findExistingEphemerisFile(ast_dir_name
                    + File.separator + ast_name);
            File ast_tmp = getWritableEphemerisFile(ast_dir_name
                    + File.separator + index + ".tmp");
            File writable_ast_file = getWritableEphemerisFile(ast_dir_name
                    + File.separator + ast_name);
            File ast_dir = writable_ast_file.getParentFile();
            if (ast_file != null
                    || ((ast_dir.isDirectory() || ast_dir.mkdirs())
                            && copyFileFromURL(url_base + "/" + ast_dir_name
                                    + "/" + ast_name, ast_tmp.getPath(), true))) {
                if (ast_file == null && ast_tmp.exists())
                    ast_tmp.renameTo(writable_ast_file);
                return true;
            } else {
                ast_tmp.deleteOnExit();
                Message.warn(Resource.getString("dialog_download_fail_1") + " "
                        + Resource.getString("asteroid_url") + " "
                        + Resource.getString("dialog_download_fail_3"));
                load_table.put(index, "t");
                return false;
            }
        } else {
            boolean bc = index.startsWith("m");
            int year = FileIO.parseInt(index.substring(1), 0, true) * 100;
            String s_date, e_date;
            if (bc) {
                s_date = (year + 1) + " B.C.";
                e_date = (year - 600 + 2) + " B.C.";
            } else {
                s_date = (year == 0) ? "1 B.C." : (year + " A.D.");
                e_date = (year + 600) + " A.D.";
            }
            if (!Message.question(Resource.getString("dialog_need_data_1")
                    + " " + s_date + " " + Resource.getString("to") + " "
                    + e_date + " " + Resource.getString("dialog_need_data_2"))) {
                Message.info(Resource.getString("dialog_download_switch"));
                setEphMode(true);
                return true;
            }
            String pl_name = "sepl" + index + ".se1";
            String mo_name = "semo" + index + ".se1";
            String as_name = "seas" + index + ".se1";
            String url_base = Resource.getString("ephe_url");
            File pl_file = findExistingEphemerisFile(pl_name);
            File mo_file = findExistingEphemerisFile(mo_name);
            File as_file = findExistingEphemerisFile(as_name);
            File writable_pl = getWritableEphemerisFile(pl_name);
            File writable_mo = getWritableEphemerisFile(mo_name);
            File writable_as = getWritableEphemerisFile(as_name);
            File pl_tmp = getWritableEphemerisFile("sepl" + index + ".tmp");
            File mo_tmp = getWritableEphemerisFile("semo" + index + ".tmp");
            File as_tmp = getWritableEphemerisFile("seas" + index + ".tmp");
            if ((pl_file != null || copyFileFromURL(url_base + "/" + pl_name,
                    pl_tmp.getPath(), true))
                    && (mo_file != null || copyFileFromURL(url_base + "/"
                            + mo_name, mo_tmp.getPath(), true))
                    && (as_file != null || copyFileFromURL(url_base + "/"
                            + as_name, as_tmp.getPath(), true))) {
                if (pl_file == null && pl_tmp.exists())
                    pl_tmp.renameTo(writable_pl);
                if (mo_file == null && mo_tmp.exists())
                    mo_tmp.renameTo(writable_mo);
                if (as_file == null && as_tmp.exists())
                    as_tmp.renameTo(writable_as);
                return true;
            } else {
                pl_tmp.deleteOnExit();
                mo_tmp.deleteOnExit();
                as_tmp.deleteOnExit();
                Message.warn(Resource.getString("dialog_download_fail_1") + " "
                        + Resource.getString("ephe_url") + " "
                        + Resource.getString("dialog_download_fail_2"));
                setEphMode(true);
                return true;
            }
        }
    }

    private File findExistingEphemerisFile(String file_name)
    {
        return AppRuntime.findExistingFile("ephe" + File.separator + file_name);
    }

    private File getWritableEphemerisFile(String file_name)
    {
        File file = AppRuntime.getDataFile("ephe" + File.separator + file_name);
        File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory())
            parent.mkdirs();
        return file;
    }

    private boolean copyFileFromURL(String url_name, String out_name,
            boolean again)
    {
        try {
            URL url = new URL(url_name);
            URLConnection conn = url.openConnection();
            BufferedInputStream in = new BufferedInputStream(
                    conn.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(out_name));
            int size;
            byte[] buffer = new byte[4096];
            while ((size = in.read(buffer)) >= 0) {
                out.write(buffer, 0, size);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            if (again)
                return copyFileFromURL(url_name, out_name, false);
            return false;
        }
        return true;
    }

    private String getEphIndex(String message)
    {
        final String missing_ast_file = "SwissEph file 'ast";
        final String missing_pl_file = "SwissEph file 'sepl";
        final String missing_mo_file = "SwissEph file 'semo";
        final String missing_as_file = "SwissEph file 'seas";
        int index = message.indexOf(missing_ast_file);
        if (index >= 0) {
            message = message.substring(message.indexOf("'") + 1);
        } else {
            index = message.indexOf(missing_pl_file);
            if (index < 0) {
                index = message.indexOf(missing_mo_file);
                if (index < 0) {
                    index = message.indexOf(missing_as_file);
                    if (index < 0)
                        return null;
                }
            }
            message = message.substring(index + missing_pl_file.length());
        }
        index = message.indexOf(".");
        return message.substring(0, index);
    }

    static public double BeijingTime(double ut)
    {
        return ut + BEIJING_TIME_OFFSET;
    }

    // Beijing time
    // the first new moon is right on or before last year's winter solstice and
    // the last new moon is right after this year's winter solstice
    public double[] computeNewMoons(int[] birth_date, double[] solar_terms)
    {
        double[] new_moons = new double[16];
        double when = solar_terms[0] + 2.0;
        boolean backward = true;
        setTopocentricMode(true, false);
        int size;
        for (size = 0; size < 16; size++) {
            when = computeNewMoon(when, backward);
            new_moons[size] = BeijingTime(when); // map to Beijing time
            when = trimHour(new_moons[size]);
            if (backward) {
                if (when <= solar_terms[0]) {
                    backward = false;
                } else {
                    size--;
                }
            } else if (when > solar_terms[24]) {
                break;
            }
            when += backward ? -25.0 : 25.0;
        }
        setTopocentricMode(false, false);
        double[] array = new double[size + 1];
        for (int j = 0; j <= size; j++)
            array[j] = new_moons[j];
        new_moons = array;
        return new_moons;
    }

    private double computeNewMoon(double start_ut, boolean backward)
    {
        try {
            int i_flag = ephe_flag | SweConst.SEFLG_TRANSIT_LONGITUDE;
            return eph.getRelativeTransitUT(SweConst.SE_MOON, SweConst.SE_SUN,
                    0.0, i_flag, start_ut, backward);
        } catch (Exception e) {
            return INVALID;
        }
    }

    // NOTE: Chinese calendar computation is based on Beijing time.
    // For non-Beijing zone, we need to use local time to compare with Beijing
    // time.
    // It is not logical, but that is how it is worldwide.
    // Argument date[] is in local time.
    public int[] getSolarCalendar(int[] date, double[] solar_terms,
            boolean start_at_winter_solstice)
    {
        int[] solar_date = (int[]) date.clone();
        double jd = getJulianDayUT(date);
        if (start_at_winter_solstice) {
            // after winter solstice, it is next year
            solar_date[1] = 1;
            if (jd >= solar_terms[24]) {
                solar_date[0]++;
            } else {
                for (int i = 4; i <= 24; i += 2) {
                    if (jd >= solar_terms[i - 2] && jd < solar_terms[i])
                        solar_date[1] = i / 2;
                }
            }
        } else {
            solar_date[1] = 11;
            if (jd < solar_terms[1]) {
                solar_date[0]--;
            } else {
                for (int i = 3; i <= 24; i += 2) {
                    if (jd >= solar_terms[i - 2] && jd < solar_terms[i]) {
                        if (i == 3) {
                            solar_date[0]--;
                            solar_date[1] = 12;
                        } else {
                            solar_date[1] = (i - 3) / 2;
                        }
                    }
                }
            }
        }
        solar_date[0] = getChineseYear(solar_date[0]);
        return solar_date;
    }

    // NOTE: Chinese calendar computation is based on Beijing time.
    // For non-Beijing zone, we need to use local time to compare with Beijing
    // time.
    // It is not logical, but that is how it is worldwide.
    // Argument date[] is in local time.
    public int[] getLunarCalendar(int[] date, double[] solar_terms,
            double[] new_moons)
    {
        int[] lunar_date = (int[]) date.clone();
        double jd = getJulianDayUT(date);
        lunar_date[0] = getChineseYear(date[0]);
        int index;
        for (index = 1; index < new_moons.length; index++) {
            if (jd >= trimHour(new_moons[index - 1])
                    && jd < trimHour(new_moons[index])) {
                break;
            }
        }
        int leap_index = getLeapMonthIndex(solar_terms, new_moons);
        leap_month = (index - 1) == leap_index;
        int month = index + 10 - (index - 1 >= leap_index ? 1 : 0);
        if (month <= 12) {
            lunar_date[0]--; // last year
        } else {
            month -= 12;
            if (month == 12) {
                // need to check for leap month
                date[0]++;
                double[] s_terms = computeSolarTerms(date);
                double[] n_moons = computeNewMoons(date, s_terms);
                leap_index = getLeapMonthIndex(s_terms, n_moons);
                if (leap_index == 1) {
                    leap_month = true;
                    month--;
                }
                date[0]--;
            }
        }
        lunar_date[1] = month;
        lunar_date[2] = (int) (trimHour(jd) - trimHour(new_moons[index - 1])) + 1;
        lunar_date[0] = fixYear(lunar_date[0]);
        return lunar_date;
    }

    public boolean isLeapMonth()
    {
        return leap_month;
    }

    public int[] getLunarDate(int[] date)
    {
        double[] solar_terms = computeSolarTerms(date);
        if (solar_terms == null)
            return null;
        double[] new_moons = computeNewMoons(date, solar_terms);
        int year = date[0];
        int lunar_year = getChineseYear(date[0]);
        int[] l_date = getLunarCalendar(date, solar_terms, new_moons);
        if (l_date != null)
            l_date[0] = (l_date[0] == lunar_year) ? year : (year - 1);
        return l_date;
    }

    public int[] getDateFromLunarDate(int[] lunar_date, boolean leap)
    {
        int[] s_date = (int[]) lunar_date.clone();
        s_date[1] = 6;
        s_date[2] = 15;
        double[] solar_terms = computeSolarTerms(s_date);
        if (solar_terms == null)
            return null;
        double[] new_moons = computeNewMoons(s_date, solar_terms);
        int[] date = (int[]) lunar_date.clone();
        lunar_date[0] = getChineseYear(lunar_date[0]);
        date[1] = 12;
        date[2] = 31;
        int[] l_date = getLunarCalendar(date, solar_terms, new_moons);
        int val = compareLunarDate(lunar_date, leap, l_date, isLeapMonth());
        if (val == 0)
            return date;
        double s_ut, e_ut;
        if (val < 0) {
            e_ut = getJulianDayUT(date);
            s_ut = e_ut - 365.0; // within this year
        } else { // next year
            s_ut = getJulianDayUT(date);
            e_ut = s_ut + 180.0; // within next six month
            s_date[0]++;
            solar_terms = computeSolarTerms(s_date);
            if (solar_terms == null)
                return null;
            new_moons = computeNewMoons(s_date, solar_terms);
        }
        while (e_ut - s_ut > 0.5) {
            double m_ut = 0.5 * (s_ut + e_ut);
            getDateFromJulianDayUT(m_ut, date);
            date[3] = lunar_date[3];
            date[4] = lunar_date[4];
            l_date = getLunarCalendar(date, solar_terms, new_moons);
            val = compareLunarDate(lunar_date, leap, l_date, isLeapMonth());
            if (val == 0)
                return date;
            if (val < 0) {
                e_ut = m_ut;
            } else {
                s_ut = m_ut;
            }
        }
        return null;
    }

    // only works if the year is the same or off by 1 and does not check for
    // hour and minute
    private int compareLunarDate(int[] date_1, boolean leap_1, int[] date_2,
            boolean leap_2)
    {
        if (date_1[0] != date_2[0])
            return (fixYear(date_1[0] + 1) == date_2[0]) ? -1 : 1;
        if (date_1[1] != date_2[1])
            return date_1[1] - date_2[1];
        if (leap_1 != leap_2)
            return leap_1 ? 1 : -1;
        return date_1[2] - date_2[2];
    }

    private double trimHour(double val)
    {
        // trim to midnight
        return ((int) (val - 0.5)) + 0.5;
    }

    public int getChineseYear(int year)
    {
        year = (year - Resource.getInt("birth_year_base") + 1) % 60;
        return fixYear(year);
    }

    private int fixYear(int year)
    {
        while (year < 0)
            year += 60;
        if (year == 0)
            year = 60;
        return year;
    }

    private int getLeapMonthIndex(double[] solar_terms, double[] new_moons)
    {
        if (new_moons.length == 14)
            return 100;
        for (int j = 1; j < new_moons.length; j++) {
            double start = trimHour(new_moons[j - 1]), end = trimHour(new_moons[j]);
            boolean mid_term = false;
            for (int i = 0; i < solar_terms.length; i += 2) {
                double center = solar_terms[i];
                if (center >= start && center < end) {
                    mid_term = true;
                    break;
                }
            }
            if (!mid_term)
                return j - 1;
        }
        return 100;
    }

    static public boolean isValid(double val)
    {
        return val != INVALID;
    }

    public String getStarSign(double degree, double[] sign_pos, String[] signs)
    {
        int len = signs.length;
        double last_pos = sign_pos[len - 1];
        for (int i = 0; i < len; i++) {
            double val = degree;
            double pos = sign_pos[i];
            if (pos < last_pos) {
                pos += 360.0;
                if (val < last_pos)
                    val += 360.0;
            }
            if (val >= last_pos && val < pos) {
                return signs[(i > 0) ? (i - 1) : (len - 1)];
            }
            last_pos = sign_pos[i];
        }
        return "?invalid?";
    }

    public String getZodiac(double degree, boolean full)
    {
        int index = (int) (degree / 30.0);
        return full ? full_zodiac[index] : zodiac[index];
    }

    public int getZodiacShift(String sign, double degree)
    {
        int index = (int) (degree / 30.0);
        for (int i = 0; i < zodiac.length; i++) {
            if (zodiac[i].equals(sign)) {
                int gap = index - i;
                if (gap < 0)
                    gap += 12;
                return gap;
            }
        }
        return 0;
    }

    public String getMountain(double degree)
    {
        double val = degree - 22.5 + mountain_offset;
        if (val < 0.0)
            val += 360.0;
        int index = (int) (val / 15.0);
        val = 15.0 + index * 15.0 - val;
        return mountain_name[index];
    }

    public int getElementalIndex(double degree)
    {
        return ((int) (degree / 30.0)) % 4;
    }

    public int getElementalStateIndex(double degree)
    {
        return ((int) (degree / 30.0)) % 3;
    }

    public void setMountainOffset(double val)
    {
        mountain_offset = val;
    }

    public String formatDegree(double degree, boolean use_sign,
            boolean show_second)
    {
        if (!isValid(degree))
            return "?invalid?";
        String str;
        DecimalFormat format = new DecimalFormat("00");
        double val = degree;
        if (use_sign) { // zodiac
            int index = (int) (val / 30.0);
            val -= index * 30.0;
            str = format.format((int) val) + zodiac_name[index];
        } else { // mountain
            val -= 22.5 + mountain_offset;
            if (val < 0.0)
                val += 360.0;
            int index = (int) (val / 15.0);
            val = 15.0 + index * 15.0 - val;
            str = format.format((int) val) + mountain_name[index];
        }
        val -= (double) ((int) val);
        return str
                + City.toMinuteSeconds((val < 0.0) ? -val : val, show_second);
    }

    public int getSignIndex(double degree, double[] sign_pos, int start)
    {
        if (!isValid(degree))
            return -1;
        int len = sign_pos.length;
        double last_pos = sign_pos[len - 1];
        for (int i = start; i < len; i++) {
            double val = degree;
            double pos = sign_pos[i];
            if (pos < last_pos) {
                pos += 360.0;
                if (val < last_pos)
                    val += 360.0;
            }
            if (val >= last_pos && val < pos)
                return (i > start) ? (i - 1) : (len - 1);
            last_pos = sign_pos[i];
        }
        return -1;
    }

    public String formatDegree(double degree, double[] sign_pos,
            String[] signs, boolean show_second)
    {
        if (!isValid(degree))
            return "?invalid?";
        DecimalFormat format = new DecimalFormat("00");
        int len = signs.length;
        double last_pos = sign_pos[len - 1];
        for (int i = 0; i < len; i++) {
            double val = degree;
            double pos = sign_pos[i];
            if (pos < last_pos) {
                pos += 360.0;
                if (val < last_pos)
                    val += 360.0;
            }
            if (val >= last_pos && val < pos) {
                val -= last_pos;
                String str = format.format((int) val)
                        + signs[(i > 0) ? (i - 1) : (len - 1)];
                val -= (double) ((int) val);
                return str
                        + City.toMinuteSeconds((val < 0.0) ? -val : val,
                                show_second);
            }
            last_pos = sign_pos[i];
        }
        return "?invalid?";
    }

    public String formatDegree(double degree, double[] sign_pos,
            String[] signs, String reverse, boolean use_mountain,
            boolean show_second)
    {
        boolean half = sign_pos == null
                || ChartMode.isChartMode(ChartMode.ASTRO_MODE);
        String str = "";
        if (!half)
            str += formatDegree(degree, sign_pos, signs, show_second) + reverse;
        str += formatDegree(degree, !use_mountain, show_second);
        if (half)
            str += reverse;
        return str;
    }

    public String formatDegree(double degree, String space, double[] sign_pos,
            String[] signs, boolean use_mountain, boolean astrolog_coord)
    {
        double val = astrolog_coord ? City.normalizeDegree(degree + 135.0)
                : City.normalizeDegree(-degree - 45);
        String str = formatDegree(degree, !use_mountain, true) + space;
        if (sign_pos != null) {
            str += formatDegree(degree, sign_pos, signs, true) + space;
        }
        str += FileIO.formatDouble(val, 3, 2, true, false);
        return str;
    }

    public String formatDegree(double pos, double alt, String space)
    {
        double val = City.normalizeDegree(-pos - 45);
        String str = FileIO.formatDouble(val, 3, 2, true, false) + space
                + FileIO.formatDouble(alt, 2, 1, true, true);
        return str;
    }

    public String[] formatChineseDegree(double degree, double[] sign_pos,
            String[] signs, boolean extend)
    {
        if (!isValid(degree))
            return null;
        int len = signs.length;
        double last_pos = sign_pos[len - 1];
        for (int i = 0; i < len; i++) {
            double val = degree;
            double pos = sign_pos[i];
            if (pos < last_pos) {
                pos += 360.0;
                if (val < last_pos)
                    val += 360.0;
            }
            if (val >= last_pos && val < pos) {
                val -= last_pos;
                String[] array = new String[2];
                array[0] = signs[(i > 0) ? (i - 1) : (len - 1)];
                array[1] = BaseCalendar.chineseNumber((int) Math.round(val),
                        false, false);
                if (extend || array[1].length() < 3)
                    array[1] += Resource.getString("degree");
                return array;
            }
            last_pos = sign_pos[i];
        }
        return null;
    }

    static public int getAsteroidData(String[] name, int[] number,
            boolean[] show, LinkedList head)
    {
        if (head == null) {
            Arrays.fill(name, "");
            Arrays.fill(number, 0);
            Arrays.fill(show, false);
        }
        if (!Resource.hasEitherKey("asteroids"))
            return 0;
        String[] array = Resource.getPrefStringArray("asteroids");
        int size = 0;
        for (int i = 0; i < array.length; i++) {
            String str = array[i];
            int n = str.indexOf(':');
            String key = str.substring(0, n);
            str = str.substring(n + 1);
            n = str.indexOf(':');
            int num = FileIO.parseInt(str.substring(0, n), 0, true);
            boolean selected = FileIO.parseInt(str.substring(n + 1), 0, true) != 0;
            if (!selected && head != null)
                continue; // filter out deselected asteroids
            if (head != null) {
                key = extractAsteroidKey(key);
                if (key == null)
                    continue; // invalid name
                head.addLast(key);
                head.addLast(new Integer(num));
            } else {
                show[size] = selected;
                name[size] = key;
                number[size] = num;
            }
            size++;
        }
        return size;
    }

    static public String extractAsteroidKey(String key)
    {
        int n = key.indexOf('[');
        if (n >= 0)
            key = key.substring(n + 1);
        n = key.indexOf(']');
        if (n >= 0)
            key = key.substring(0, n);
        key = key.trim();
        return (key.length() == 0) ? null : key.substring(0, 1);
    }

    public void dispose()
    {
        eph.swe_close();
    }

    public boolean dumpPlanetData(String dir, int start_year, int end_year,
            boolean speed_group)
    {
        if (end_year <= start_year)
            return false;
        double ut_sav = julian_day_ut;
        int[] date = new int[5];
        date[0] = start_year;
        date[1] = date[2] = 1;
        date[3] = date[4] = 0;
        setJulianDay(date);
        double period = 365.25 * (end_year - start_year);
        if (speed_group) {
            FileIO sum = new FileIO(dir + "/" + "summary.txt", false, true);
            dumpPlanetSpeed(SweConst.SE_MERCURY, ChartData.MERCURY, period,
                    dir, sum);
            dumpPlanetSpeed(SweConst.SE_VENUS, ChartData.VENUS, period, dir,
                    sum);
            dumpPlanetSpeed(SweConst.SE_MARS, ChartData.MARS, period, dir, sum);
            dumpPlanetSpeed(SweConst.SE_JUPITER, ChartData.JUPITER, period,
                    dir, sum);
            dumpPlanetSpeed(SweConst.SE_SATURN, ChartData.SATURN, period, dir,
                    sum);
            sum.dispose();
        } else {
            dumpPlanetAttribute(SweConst.SE_MERCURY, period, dir);
            dumpPlanetAttribute(SweConst.SE_VENUS, period, dir);
            dumpPlanetAttribute(SweConst.SE_MARS, period, dir);
            dumpPlanetAttribute(SweConst.SE_JUPITER, period, dir);
            dumpPlanetAttribute(SweConst.SE_SATURN, period, dir);
            dumpPlanetAttribute(SweConst.SE_URANUS, period, dir);
            dumpPlanetAttribute(SweConst.SE_NEPTUNE, period, dir);
            dumpPlanetAttribute(SweConst.SE_PLUTO, period, dir);
        }
        julian_day_ut = ut_sav;
        return true;
    }

    private void dumpPlanetAttribute(int body, double period, String dir)
    {
        StringBuffer error = new StringBuffer();
        double ut = julian_day_ut;
        FileIO out = new FileIO(dir + "/" + eph.swe_get_planet_name(body)
                + ".csv", false, false);
        double pos = compute(body);
        double end_ut = julian_day_ut + (double) period;
        double delta_ut = (end_ut - julian_day_ut) / 999.99;
        double[] xnasc = new double[6], xndsc = new double[6], xperi = new double[6], xaphe = new double[6];
        out.putLine("ut,speed,position,sun diff,xperi,xaphe,xperi diff,xaphe diff");
        for (; julian_day_ut < end_ut; julian_day_ut += delta_ut) {
            double s_pos = compute(SweConst.SE_SUN);
            pos = compute(body);
            double speed = getSpeed();
            eph.swe_nod_aps_ut(julian_day_ut, body, ephe_flag,
                    SweConst.SE_NODBIT_MEAN, xnasc, xndsc, xperi, xaphe, error);
            double diff = Math.abs(s_pos - pos);
            if (diff > 180.0)
                diff = 360.0 - diff;
            String str = "" + julian_day_ut + "," + speed + "," + pos + ","
                    + diff + "," + xperi[0] + "," + xaphe[0];
            diff = Math.abs(xperi[0] - pos);
            if (diff > 180.0)
                diff = 360.0 - diff;
            str += "," + diff;
            diff = Math.abs(xaphe[0] - pos);
            if (diff > 180.0)
                diff = 360.0 - diff;
            str += "," + diff;
            out.putLine(str);
        }
        out.dispose();
        julian_day_ut = ut;
    }

    private void dumpPlanetSpeed(int body, int index, double period,
            String dir, FileIO sum)
    {
        index -= ChartData.VENUS; // shift index
        double ut = julian_day_ut;
        String stat = eph.swe_get_planet_name(body);
        FileIO out = new FileIO(dir + "/" + eph.swe_get_planet_name(body)
                + ".txt", false, true);
        double end_ut = julian_day_ut + (double) period;
        double delta_ut = 1.0, p_ut = 0.0, p_pos = 0.0, l_pos = 0.0, l_ut = 0.0;
        int p_state = -1, state_count = 0;
        int total = 0;
        int[] count = new int[speed_state.length];
        for (; julian_day_ut < end_ut; julian_day_ut += delta_ut) {
            sun_pos = compute(SweConst.SE_SUN);
            double pos = compute(body);
            int state = getSpeedState(body, index);
            if (p_state == state) {
                state_count++;
            } else {
                if (state_count > 0) {
                    out.putLine(speed_state[p_state] + ": " + state_count
                            + " [" + p_ut + "-" + l_ut + ", " + p_pos + "-"
                            + l_pos + "]");
                }
                state_count = 1;
                p_state = state;
                p_ut = julian_day_ut;
                p_pos = pos;
            }
            l_pos = pos;
            l_ut = julian_day_ut;
            count[state]++;
            total++;
        }
        if (state_count > 0) {
            out.putLine(speed_state[p_state] + ": " + state_count + " [" + p_ut
                    + "-" + l_ut + ", " + p_pos + "-" + l_pos + "]");
        }
        out.dispose();
        julian_day_ut = ut;
        sum.putLine(stat + ":");
        for (int i = 0; i < speed_state.length; i++) {
            sum.putLine(speed_state[i]
                    + ": "
                    + count[i]
                    + " ("
                    + FileIO.formatDouble(100.0 * count[i] / total, 2, 1,
                            false, false) + "%)");
        }
    }

    class StarEntry implements Comparable {
        String data;

        double error;

        public StarEntry(String str, double err)
        {
            data = str;
            error = err;
        }

        public int compareTo(Object obj)
        {
            return Double.compare(error, ((StarEntry) obj).error);
        }
    }
}
