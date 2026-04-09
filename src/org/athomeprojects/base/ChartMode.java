//
//Moira - A Chinese Astrology Charting Program
//Copyright (C) 2004-2015 At Home Projects
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package org.athomeprojects.base;

public class ChartMode {
	static public final int TRADITIONAL_MODE = 0;

	static public final int SIDEREAL_MODE = 1;

	static public final int PICK_MODE = 2;

	static public final int ASTRO_MODE = 3;

	static public final int NUM_MODE = 4;

	static public final int NATAL_MODE = 0;

	static public final int ALT_NATAL_MODE = 1;

	static public final int SOLAR_RETURN_MODE = 2;

	static public final int LUNAR_RETURN_MODE = 3;

	static public final int RELATIONSHIP_MODE = 4;

	static public final int COMPOSITE_MODE = 5;

	static public final int TRANSIT_MODE = 6; // start dual ring mode

	static public final int PRIMARY_DIRECTION_MODE = 7;

	static public final int SECONDARY_PROGRESSION_MODE = 8;

	static public final int SOLAR_ARC_MODE = 9;

	static public final int COMPARISON_MODE = 10;

	static public final int NUM_ASTRO_MODE = 11;

	static public final int MOUNTAIN_MODE = 0;

	static public final int ZODIAC_MODE = 1;

	static private int chart_mode, astro_mode;

	static private boolean single_wheel_mode;

	static public void initChartMode() {
		chart_mode = Resource.getPrefInt("chart_mode");
		if (chart_mode < 0 || chart_mode >= NUM_MODE)
			chart_mode = TRADITIONAL_MODE;
		astro_mode = NATAL_MODE;
		single_wheel_mode = false;
	}

	static public boolean hasChartMode() {
		return Resource.hasPrefInt("chart_mode");
	}

	static public void setChartMode(int mode) {
		chart_mode = mode;
		Resource.putPrefInt("chart_mode", mode);
	}

	static public void setChartMode() {
		chart_mode = Resource.getPrefInt("chart_mode");
	}

	static public int getChartMode() {
		return chart_mode;
	}

	static public int modeToGroup(int mode) {
		return (mode == SIDEREAL_MODE) ? TRADITIONAL_MODE : mode;
	}

	static public boolean isChartMode(int mode) {
		return getChartMode() == mode;
	}

	static public void setAstroMode(int mode) {
		astro_mode = mode;
	}

	static public int getAstroMode() {
		return astro_mode;
	}

	static public boolean isAstroMode(int mode) {
		return astro_mode == mode;
	}

	static public boolean isAstroDualRingMode() {
		return !single_wheel_mode && astro_mode >= TRANSIT_MODE;
	}

	static public void setSingleWheelMode(boolean set) {
		single_wheel_mode = set && hasSingleWheelMode();
	}

	static public boolean isSingleWheelMode() {
		return single_wheel_mode;
	}

	static public boolean hasSingleWheelMode() {
		return astro_mode == SECONDARY_PROGRESSION_MODE;
	}

	static public boolean isMultipleMode(boolean extended) {
		return isMultipleMode(astro_mode, extended);
	}

	static public boolean isMultipleMode(int mode, boolean extended) {
		return mode == COMPARISON_MODE || mode == RELATIONSHIP_MODE
				|| mode == COMPOSITE_MODE || extended && mode == ALT_NATAL_MODE;
	}

	static public boolean isReturnMode() {
		return astro_mode == SOLAR_RETURN_MODE
				|| astro_mode == LUNAR_RETURN_MODE;
	}

	static public String getModeName() {
		boolean show_gauquelin = ChartData.getData().getShowGauquelin();
		String[] chart_names = Resource
				.getStringArray(show_gauquelin ? "gauquelin_mode_name"
						: "astro_mode_name");
		return chart_names[astro_mode];
	}

	static public String getModePrefix() {
		String[] chart_prefix = Resource.getStringArray("astro_mode_prefix");
		String prefix = chart_prefix[astro_mode];
		return prefix.equalsIgnoreCase("x") ? "" : prefix;
	}

	static public boolean hasReturnRingMode() {
		return astro_mode <= LUNAR_RETURN_MODE && astro_mode != ALT_NATAL_MODE;
	}

	static public String getModeName(boolean label, boolean full) {
		switch (getChartMode()) {
		case SIDEREAL_MODE:
			return Resource.getString("sidereal_mode");
		case PICK_MODE:
			if (label) {
				String str = null;
				if (full) {
					boolean sidereal_mode = Resource
							.getPrefInt("pick_sidereal_mode") != 0;
					boolean house_mode = Resource.getPrefInt("pick_house_mode") != 0;
					boolean adjust_mode = Resource
							.getPrefInt("pick_adjust_mode") != 0;
					if (sidereal_mode) {
						str = Resource.getString("sidereal_mode");
					} else {
						str = house_mode ? Resource
								.getString(adjust_mode ? "ancient_adjust_house_mode"
										: "ancient_house_mode")
								: null;
					}
				}
				return Resource.getString("pick_chart")
						+ ((str == null) ? "" : (" - " + str));
			}
			return null;
		case ASTRO_MODE: {
			return getModeName() + Resource.getString("chart_char");
		}
		default: {
			boolean house_mode = Resource.getPrefInt("house_mode") != 0;
			boolean adjust_mode = Resource.getPrefInt("adjust_mode") != 0;
			if (label) {
				return house_mode ? Resource
						.getString(adjust_mode ? "ancient_adjust_house_mode"
								: "ancient_house_mode") : null;
			}
			return Resource
					.getString(house_mode ? (adjust_mode ? "tropical_ancient_adjust_house_mode"
							: "tropical_ancient_house_mode")
							: "tropical_mode");
		}
		}
	}

	static public String getModeTitle() {
		if (Resource.hasPrefKey("alternate_title"))
			return Resource.getPrefString("alternate_title");
		if (Resource.hasPrefInt("chart_mode")) {
			switch (Resource.getPrefInt("chart_mode")) {
			case PICK_MODE:
				return Resource.getString("pick_title");
			case ASTRO_MODE:
				return Resource.getString("western_title");
			default:
				return Resource.getString("eastern_title");
			}
		} else {
			return Resource.getString("dialog_mode_selection");
		}
	}

	static public String getSystemName(String prefix) {
		String[] systems = Resource.getStringArray("house_system");
		int index = Resource.getPrefInt(prefix + "house_system_index");
		return Resource.getString("house_system_name") + ":" + systems[index];
	}

	static public String getComputationMethod() {
		boolean topo = ChartData.getData().getShowHoriz()
				|| Resource.getPrefInt("topocentric") != 0;
		return Resource.getString(topo ? "topocentric_method"
				: "geocentric_method");
	}

	static public String getSidrealSystem() {
		if (chart_mode != ASTRO_MODE
				|| Resource.getPrefInt("astro_system_mode") == 0)
			return null;
		String[] systems = Resource.getStringArray("astro_sidereal_system");
		int index = Resource.getPrefInt("astro_sidereal_index");
		return Resource.getString("sidereal_mode") + ":" + systems[index];
	}

	static public int getDegreeMode(boolean quick_azimuth) {
		int degree_mode = Resource.getPrefInt("degree_mode");
		if (quick_azimuth)
			degree_mode = ZODIAC_MODE;
		return degree_mode;
	}

	static public boolean mountainBased(int degree_mode) {
		return degree_mode == MOUNTAIN_MODE;
	}
}