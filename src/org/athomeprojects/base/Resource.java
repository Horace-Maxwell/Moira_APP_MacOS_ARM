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
import java.awt.GraphicsEnvironment;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Resource {
    static public boolean trace = true;

    static public final String NAME = "Moira";

    static public final String NUMBER = "1.50 Final";

    static public final String COPYRIGHT_1 = "Copyright ";

    static public final String COPYRIGHT_2 = " 2004-2015 At Home Projects";

    static public final String DATA_EXT = "mri";

    static public final String RSRC_EXT = "prop";

    static public final String RULE_EXT = "rule";

    static public final String SIMPLIFIED_SUFFIX = "_s.";

    static public final String TRADITIONAL_SUFFIX = "_t.";

    static private final String SIMPLIFIED = "simplified";

    static private final String DATA_PREFIX = "moira";

    static private final String CUSTOM_PREFIX = "custom";

    static public String[] DATA_EXTENSIONS = { "*." + DATA_EXT };

    static public String[] RSRC_EXTENSIONS = { "*." + RSRC_EXT };

    static public String[] RULE_EXTENSIONS = { "*." + RULE_EXT };

    static public String[] ALL_EXTENSIONS = { "*." + DATA_EXT, "*." + RSRC_EXT,
            "*." + RULE_EXT };

    static private final String invalid_string = "?Invalid?";

    static private final String[] reject_font_array = { "Arial Unicode MS" };

    static public final String LOCAL_PREFIX = "local:";

    static public final int DIAGRAM_WIDTH = 640;

    static private Font[] font_array;

    static private boolean simplified, alt_exclusive;

    static private int pref_changed;

    static private FileIO resource;

    static private Preferences prefs = null;

    static private Hashtable alt_prefs = null;

    static private String font_name, en_font_name, pref_font_name, alt_command;

    static private int font_style, data_font_size, swt_data_font_size,
            swt_small_data_font_size;

    public Resource(Class clss, String language, String prefer_font_name,
            String mod_name, String eval_name)
    {
        resource = null;
        prefs = (clss == null) ? null : Preferences.userNodeForPackage(clss);
        simplified = hasPrefKey(SIMPLIFIED) ? (getPrefInt(SIMPLIFIED) != 0)
                : isSimplifiedLocale();
        if (language != null && language.equalsIgnoreCase("simplified"))
            simplified = true;
        pref_font_name = prefer_font_name;
        FileIO.setProgress(10);
        font_array = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAllFonts();
        FileIO.setProgress(20);
        if (trace) {
            loadModEval(mod_name, eval_name);
        } else {
            try {
                loadModEval(mod_name, eval_name);
            } catch (Exception e) {
                removeModEvalPref();
            }
        }
        putPrefInt("show_eval", RuleEntry.hasRuleEntry(true) ? 1 : 0);
    }

    private void loadModEval(String mod_name, String eval_name)
    {
        if (hasPrefKey("modification"))
            mod_name = getPrefString("modification");
        loadModification(mod_name);
        ChartMode.setChartMode();
        String prefix = ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_"
                : "";
        if (hasPrefKey(prefix + "evaluation"))
            eval_name = getPrefString(prefix + "evaluation");
        loadEvaluation(eval_name, false);
    }

    static public void removeModEvalPref()
    {
        removePref("modification");
        String prefix = ChartMode.isChartMode(ChartMode.PICK_MODE) ? "pick_"
                : "";
        removePref(prefix + "evaluation");
    }

    static public boolean isSimplifiedLocale()
    {
        return Locale.getDefault().toString().equals("zh_CN");
    }

    static public void loadModification(String mod_name)
    {
        if (resource != null)
            resource.dispose();
        String lang_suffix = simplified ? SIMPLIFIED_SUFFIX
                : TRADITIONAL_SUFFIX;
        String res_name = DATA_PREFIX + lang_suffix + RSRC_EXT;
        if (mod_name == null)
            mod_name = CUSTOM_PREFIX + lang_suffix + RSRC_EXT;
        RuleEntry.reset(false);
        resource = new FileIO(res_name, mod_name);
        RuleEntry.processRule();
        RuleEntry.saveTable(false);
        setDefaultFont(pref_font_name);
        font_style = getPrefInt("font_style");
        if (font_style == Integer.MIN_VALUE)
            font_style = Font.PLAIN;
        data_font_size = getPrefInt("data_font_size");
        swt_data_font_size = getPrefInt("swt_data_font_size");
        swt_small_data_font_size = getPrefInt("swt_small_data_font_size");
    }

    static public boolean loadEvaluation(String eval_name, boolean check)
    {
        if (eval_name == null) {
            String lang_suffix = simplified ? SIMPLIFIED_SUFFIX
                    : TRADITIONAL_SUFFIX;
            eval_name = DATA_PREFIX + lang_suffix + RULE_EXT;
        }
        if (check) {
            String cur_eval_name = getPrefString("evaluation_loaded");
            if (cur_eval_name.equals(eval_name))
                return false;
        }
        putPrefString("evaluation_loaded", eval_name);
        RuleEntry.reset(true);
        FileIO eval = new FileIO(eval_name, false);
        if (eval != null) {
            RuleEntry.processRule();
            RuleEntry.saveTable(true);
            eval.dispose();
        }
        return true;
    }

    static public boolean hasCustomData()
    {
        return resource.hasCustomData();
    }

    static public boolean isSimplified()
    {
        return simplified;
    }

    static public void setSimplified(boolean yes)
    {
        putPrefInt(SIMPLIFIED, yes ? 1 : 0);
    }

    static private void setDefaultFont(String prefer_font_name)
    {
        if (prefer_font_name == null)
            prefer_font_name = getPrefString("font_name");
        font_name = mapFont(prefer_font_name, null);
        FileIO.setProgress(30);
        en_font_name = mapFont(getString("en_font_name"), "abc");
    }

    static private String mapFont(String name_array, String font_check)
    {
        String f_name = null;
        StringTokenizer st = new StringTokenizer(name_array, ",");
        while (st.hasMoreTokens()) {
            f_name = st.nextToken().trim();
            String fn_name = getFontNameIfAvailable(f_name);
            if (fn_name != null && isFontAcceptable(fn_name, font_check))
                return fn_name;
        }
        String[] font_name_array = getFontArray(font_check);
        return (font_name_array == null) ? f_name : font_name_array[0];
    }

    static public boolean isFontAcceptable(String name, String font_check)
    {
        if (isRejectedFont(name))
            return false;
        if (font_check == null)
            font_check = getString("font_check");
        Font font = new Font(name, font_style, data_font_size);
        return font.canDisplayUpTo(font_check) < 0;
    }

    static private String getFontNameIfAvailable(String name)
    {
        for (int i = 0; i < font_array.length; i++) {
            if (name.equalsIgnoreCase(font_array[i].getFontName())
                    || name.equalsIgnoreCase(font_array[i]
                            .getFontName(Locale.ENGLISH))
                    || name.equalsIgnoreCase(font_array[i]
                            .getFontName(Locale.TRADITIONAL_CHINESE))
                    || name.equalsIgnoreCase(font_array[i]
                            .getFontName(Locale.SIMPLIFIED_CHINESE))) {
                return font_array[i].getFontName();
            }
        }
        return null;
    }

    static public String[] getPossibleFontName(String name)
    {
        for (int i = 0; i < font_array.length; i++) {
            if (name.equalsIgnoreCase(font_array[i].getFontName())) {
                String[] array = new String[3];
                array[0] = font_array[i].getFontName(Locale.ENGLISH);
                array[1] = font_array[i]
                        .getFontName(Locale.TRADITIONAL_CHINESE);
                array[2] = font_array[i].getFontName(Locale.SIMPLIFIED_CHINESE);
      
                return array;
            }
        }
        return null;
    }

    static public String[] getFontArray(String font_check)
    {
        if (font_check == null)
            font_check = getString("font_check");
        int size = 0;
        for (int i = 0; i < font_array.length; i++) {
            if (font_array[i].canDisplayUpTo(font_check) < 0
                    && !isRejectedFont(font_array[i].getFontName())) {
                size++;
            }
        }
        if (size == 0)
            return null;
        String[] font_list = new String[size];
        size = 0;
        for (int i = 0; i < font_array.length; i++) {
            if (font_array[i].canDisplayUpTo(font_check) < 0
                    && !isRejectedFont(font_array[i].getFontName())) {
                font_list[size++] = font_array[i].getFontName();
            }
        }
        return font_list;
    }

    static private boolean isRejectedFont(String name)
    {
        for (int i = 0; i < reject_font_array.length; i++) {
            if (reject_font_array[i].equals(name))
                return true;
        }
        return false;
    }

    static public String getModName()
    {
        String str = getString("mod_name");
        if (str == null || str.trim().equals(""))
            str = CUSTOM_PREFIX;
        return str;
    }

    static public boolean isExclusive()
    {
        return alt_exclusive;
    }

    static public String getString(String key)
    {
        return resource.getString(key);
    }

    static public String getProcessString(String key)
    {
        return resource.getString(key).replaceAll("@", "\n");
    }

    static public int getInt(String key)
    {
        return resource.getInt(key);
    }

    static public double getDouble(String key)
    {
        return resource.getDouble(key);
    }

    static public int getStringArray(String key, String[] array)
    {
        return resource.getStringArray(key, array);
    }

    static public String[] getStringArray(String key)
    {
        return resource.getStringArray(key);
    }

    static public LinkedList getStringList(String key)
    {
        return FileIO.toStringList(resource.getString(key));
    }

    static public int getIntArray(String key, int[] array)
    {
        return resource.getIntArray(key, array);
    }

    static public int[] getIntArray(String key)
    {
        return resource.getIntArray(key);
    }

    static public int getDoubleArray(String key, double[] array)
    {
        return resource.getDoubleArray(key, array);
    }

    static public double[] getDoubleArray(String key)
    {
        return resource.getDoubleArray(key);
    }

    static public void putPrefString(String key, String val)
    {
        if (prefs == null)
            return;
        if (alt_exclusive || hasAltKey(key))
            alt_prefs.put(key.toUpperCase(), val);
        else
            prefs.put(key, val);
    }

    static public String getPrefString(String key)
    {
        if (!hasPrefKey(key) && hasKey(key))
            return getString(key);
        else if (prefs == null)
            return invalid_string;
        else {
            if (hasAltKey(key)) {
                resource.setTable(alt_prefs);
                String str = getString(key);
                resource.setTable(null);
                return str;
            } else if (alt_exclusive) {
                return invalid_string;
            }
            return prefs.get(key, invalid_string);
        }
    }

    static public boolean hasEitherKey(String key)
    {
        return hasKey(key) || hasPrefKey(key);
    }

    static public boolean hasPrefKey(String key)
    {
        if (prefs == null)
            return false;
        if (hasAltKey(key))
            return true;
        if (alt_exclusive)
            return false;
        return !prefs.get(key, invalid_string).equals(invalid_string);
    }

    static public void removePref(String key)
    {
        if (prefs == null)
            return;
        if (hasAltKey(key)) {
            alt_prefs.remove(key.toUpperCase());
        } else if (!alt_exclusive) {
            prefs.remove(key);
        }
    }

    static public void putPrefInt(String key, int val)
    {
        if (prefs == null)
            return;
        if (alt_exclusive || hasAltKey(key))
            alt_prefs.put(key.toUpperCase(), Integer.toString(val));
        else
            prefs.putInt(key, val);
    }

    static public int getPrefInt(String key)
    {
        if (!hasPrefInt(key) && hasKey(key))
            return getInt(key);
        else if (prefs == null)
            return Integer.MIN_VALUE;
        else {
            if (hasAltKey(key)) {
                resource.setTable(alt_prefs);
                int n = getInt(key);
                resource.setTable(null);
                return n;
            } else if (alt_exclusive) {
                return Integer.MIN_VALUE;
            }
            return prefs.getInt(key, Integer.MIN_VALUE);
        }
    }

    static public boolean hasPrefInt(String key)
    {
        if (prefs == null)
            return false;
        if (hasAltKey(key)) {
            resource.setTable(alt_prefs);
            int n = getInt(key);
            resource.setTable(null);
            return n != Integer.MIN_VALUE;
        }
        if (alt_exclusive)
            return false;
        return prefs.getInt(key, Integer.MIN_VALUE) != Integer.MIN_VALUE;
    }

    static public void putPrefDouble(String key, double val)
    {
        if (prefs == null)
            return;
        if (alt_exclusive || hasAltKey(key))
            alt_prefs.put(key.toUpperCase(), Double.toString(val));
        else
            prefs.putDouble(key, val);
    }

    static public double getPrefDouble(String key)
    {
        if (!hasPrefDouble(key) && hasKey(key))
            return getDouble(key);
        else if (prefs == null)
            return Double.MIN_VALUE;
        else {
            if (hasAltKey(key)) {
                resource.setTable(alt_prefs);
                double d = getDouble(key);
                resource.setTable(null);
                return d;
            } else if (alt_exclusive) {
                return Double.MIN_VALUE;
            }
            return prefs.getDouble(key, Double.MIN_VALUE);
        }
    }

    static public boolean hasPrefDouble(String key)
    {
        if (prefs == null)
            return false;
        if (hasAltKey(key)) {
            resource.setTable(alt_prefs);
            double d = getDouble(key);
            resource.setTable(null);
            return d != Double.MIN_VALUE;
        }
        if (alt_exclusive)
            return false;
        return prefs.getDouble(key, Double.MIN_VALUE) != Double.MIN_VALUE;
    }

    static public void putPrefStringArray(String key, String[] data)
    {
        if (prefs == null || data == null)
            return;
        String str = null;
        for (int i = 0; i < data.length; i++) {
            if (i == 0)
                str = data[i];
            else
                str += "," + data[i];
        }
        if (alt_exclusive || hasAltKey(key))
            alt_prefs.put(key.toUpperCase(), str);
        else
            prefs.put(key, str);
    }

    static public String[] getPrefStringArray(String key)
    {
        if (hasPrefKey(key)) {
            return FileIO.toStringArray(getPrefString(key));
        } else {
            return resource.getStringArray(key);
        }
    }

    static public String[] getPrefStringArray(String prefix, String key)
    {
        String[] array = Resource
                .getStringArray(Resource.hasKey(prefix + key) ? (prefix + key)
                        : key);
        String[] p_array = Resource.getPrefStringArray(Resource
                .hasPrefKey(prefix + key) ? (prefix + key) : key);
        return (p_array != null && p_array.length == array.length) ? p_array
                : array;
    }

    static public void putPrefIntArray(String key, int[] data)
    {
        if (prefs == null || data == null)
            return;
        String str = null;
        for (int i = 0; i < data.length; i++) {
            String val = Integer.toString(data[i]);
            if (i == 0)
                str = val;
            else
                str += "," + val;
        }
        if (alt_exclusive || hasAltKey(key))
            alt_prefs.put(key.toUpperCase(), str);
        else
            prefs.put(key, str);
    }

    static public int[] getPrefIntArray(String key)
    {
        if (hasPrefKey(key)) {
            return FileIO.toIntArray(getPrefString(key));
        } else {
            return resource.getIntArray(key);
        }
    }

    static public int[] getPrefIntArray(String prefix, String key)
    {
        int[] array = Resource
                .getIntArray(Resource.hasKey(prefix + key) ? (prefix + key)
                        : key);
        int[] p_array = Resource.getPrefIntArray(Resource.hasPrefKey(prefix
                + key) ? (prefix + key) : key);
        return (p_array != null && p_array.length == array.length) ? p_array
                : array;
    }

    static public void putPrefDoubleArray(String key, double[] data)
    {
        if (prefs == null || data == null)
            return;
        String str = null;
        for (int i = 0; i < data.length; i++) {
            String val = Double.toString(data[i]);
            if (i == 0)
                str = val;
            else
                str += "," + val;
        }
        if (alt_exclusive || hasAltKey(key))
            alt_prefs.put(key.toUpperCase(), str);
        else
            prefs.put(key, str);
    }

    static public double[] getPrefDoubleArray(String key)
    {
        if (hasPrefKey(key)) {
            return FileIO.toDoubleArray(getPrefString(key));
        } else {
            return resource.getDoubleArray(key);
        }
    }

    static public double[] getPrefDoubleArray(String prefix, String key)
    {
        double[] array = Resource
                .getDoubleArray(Resource.hasKey(prefix + key) ? (prefix + key)
                        : key);
        double[] p_array = Resource.getPrefDoubleArray(Resource
                .hasPrefKey(prefix + key) ? (prefix + key) : key);
        return (p_array != null && p_array.length == array.length) ? p_array
                : array;
    }

    static public boolean hasKey(String key)
    {
        return resource != null && resource.hasKey(key);
    }

    static private boolean hasAltKey(String key)
    {
        return alt_prefs != null && alt_prefs.get(key.toUpperCase()) != null;
    }

    static public void prefClear(boolean remove)
    {
        if (prefs != null) {
            try {
                prefs.clear();
            } catch (BackingStoreException e) {
            }
            if (remove)
                prefs = null;
        }
    }

    static public boolean prefChanged()
    {
        return pref_changed != 0;
    }

    static public void enableAlternatePref(boolean enable, int changed)
    {
        pref_changed = changed;
        alt_prefs = enable ? (new Hashtable()) : null;
        resetAlternate();
    }

    static public void setAlternatePref(String data)
    {
        if (data == null) {
            if (pref_changed >= 0)
                pref_changed = (alt_prefs != null) ? 1 : 0;
            alt_prefs = null;
            resetAlternate();
            return;
        }
        enableAlternatePref(true, 1);
        if (pref_changed >= 0)
            pref_changed = 1;
        StringTokenizer st = new StringTokenizer(data, "|");
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            StringTokenizer nst = new StringTokenizer(str, ":");
            if (nst.countTokens() >= 2) {
                String key = nst.nextToken().trim().toUpperCase();
                String info = nst.nextToken().trim();
                while (nst.hasMoreTokens())
                    info += ":" + nst.nextToken().trim();
                if (key.equalsIgnoreCase("exclusive"))
                    alt_exclusive = !info.equals("0");
                else if (key.equalsIgnoreCase("command"))
                    alt_command = info;
                else
                    alt_prefs.put(key, info);
            }
        }
    }

    static private void resetAlternate()
    {
        alt_exclusive = false;
        alt_command = null;
    }

    static public boolean hasAlternatePref()
    {
        return alt_prefs != null;
    }

    static public String getAlternateCommand()
    {
        return alt_command;
    }

    static public String getAlternatePref()
    {
        if (alt_prefs == null || alt_prefs.isEmpty() && !alt_exclusive)
            return null;
        String data = alt_exclusive ? "exclusive:1" : null;
        for (Enumeration e = alt_prefs.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = (String) alt_prefs.get(key);
            if (data == null)
                data = key.toLowerCase() + ":" + val;
            else
                data += "|" + key.toLowerCase() + ":" + val;
        }
        if (alt_command != null)
            data += "|" + "command:" + alt_command;
        return data;
    }

    static public void dispose()
    {
        resource.dispose();
        resource = null;
    }

    static public boolean hasCustomFootnote()
    {
        return Resource.hasPrefKey("footnote");
    }

    static public String getFootnote()
    {
        if (Resource.hasPrefKey("footnote")) {
            return Resource.getPrefString("footnote");
        } else {
            return Resource.NAME + " version " + Resource.NUMBER + ", "
                    + COPYRIGHT_1 + Resource.getString("copyright")
                    + COPYRIGHT_2;
        }
    }

    static public void setFootnote(String str)
    {
        if (str != null && !str.equals(""))
            Resource.putPrefString("footnote", str);
        else
            Resource.removePref("footnote");
    }

    static public String getFontName()
    {
        return font_name;
    }

    static public String getEnFontName()
    {
        return en_font_name;
    }

    static public void setFontName(String name)
    {
        font_name = name;
        putPrefString("font_name", font_name);
    }

    static public int getFontStyle()
    {
        return font_style;
    }

    static public void setFontStyle(int style)
    {
        font_style = style;
        putPrefInt("font_style", font_style);
    }

    static public int getDataFontSize()
    {
        return data_font_size;
    }

    static public int getSwtDataFontSize()
    {
        return swt_data_font_size;
    }

    static public int getSwtSmallDataFontSize()
    {
        return swt_small_data_font_size;
    }

    static public void setSwtDataFontSize(int size)
    {
        swt_data_font_size = size;
        putPrefInt("swt_data_font_size", swt_data_font_size);
    }

    static public String preFilled(String str, int width, String fill)
    {
        while (str.length() < width)
            str = fill + str;
        return str;
    }

    static public String spacePreFilled(String str, int width)
    {
        return preFilled(str, width, " ");
    }

    static public String postFilled(String str, int width, String fill)
    {
        while (str.length() < width)
            str += fill;
        return str;
    }

    static public String spacePostFilled(String str, int width)
    {
        return postFilled(str, width, " ");
    }

    static public String getSpaceFilled(String key)
    {
        char[] array = key.toCharArray();
        String str = "";
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 0xff)
                str += "  ";
            else
                str += " ";
        }
        return str;
    }
}