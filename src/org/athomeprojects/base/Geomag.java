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
// Software and Model Support
// National Geophysical Data Center
// NOAA EGC/2
// 325 Broadway
// Boulder, CO 80303 USA
// Attn: Susan McLean or Stefan Maus
// Phone: (303) 497-6478 or -6522
// Email: Susan.McLean@noaa.gov or Stefan.Maus@noaa.gov
// Web: http://www.ngdc.noaa.gov/seg/WMM/
//
// Sponsoring Government Agency
// National Geospatial-Intelligence Agency
// PRG / CSAT, M.S. L-41
// 3838 Vogel Road
// Arnold, MO 63010
// Attn: Craig Rollins
// Phone: (314) 263-4186
// Email: Craig.M.Rollins@Nga.Mil
//
// Original Program By:
// Dr. John Quinn
// FLEET PRODUCTS DIVISION, CODE N342
// NAVAL OCEANOGRAPHIC OFFICE (NAVOCEANO)
// STENNIS SPACE CENTER (SSC), MS 39522-5001
//
// 25 November 2005 - Version 2
//
package org.athomeprojects.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;

public class Geomag {
	static private boolean not_available = false;

	static private int maxdeg, maxord, n, m, j, D1, D2, D3, D4;

	static private double dec, gv, pi, dtr, a, b, re, a2, b2, c2, a4, b4, c4,
			epoch, gnm, hnm, dgnm, dhnm, flnmj, otime, oalt, olat, olon, dt,
			rlon, rlat, srlon, srlat, crlon, crlat, srlat2, crlat2, q, q1, q2,
			ct, st, r2, r, d, ca, sa, aor, ar, br, bt, bp, bpp, par, temp1,
			temp2, parp, bx, by;

	static private double year = Double.MIN_VALUE;

	static private boolean out_of_range;

	static private double[] snorm, sp, cp, fn, fm, pp;

	static private double[][] c, cd, tc, dp, k;

	static private double min_year = 3000.0, max_year = 1000.0;

	static private int num_data_year = 0, cur_cof_year = 0;

	static private int[] data_year = new int[100];

	static private String warning;

	static public double getMagneticShift(int[] date, double longitude,
			double latitude) {
		if (not_available)
			return 0.0;
		if (num_data_year == 0) {
			for (int y = 2005; y <= 2030; y += 5) {
				File cof = new File(FileIO.getFileName("WMM" + y + ".COF"));
				if (cof.exists()) {
					data_year[num_data_year++] = y;
					if (y - 5 < min_year)
						min_year = y - 5;
					if (y + 10 > max_year)
						max_year = y + 10;
				}
			}
			if (num_data_year == 0) {
				not_available = true;
				return 0.0;
			}
		}
		if (date != null) {
			setDate(date);
		} else if (year == Double.MIN_VALUE) {
			date = new int[5];
			BaseCalendar.getCalendar(null, date);
			setDate(date);
		}
		if (out_of_range)
			return 0.0;
		int cof_year = 0;
		for (int i = num_data_year - 1; i >= 0; i--) {
			cof_year = data_year[i];
			if ((int) year >= cof_year)
				break;
		}
		if (cur_cof_year != cof_year) {
			cur_cof_year = cof_year;
			snorm = new double[169];
			sp = new double[13];
			cp = new double[13];
			fn = new double[13];
			fm = new double[13];
			pp = new double[13];
			c = new double[13][13];
			cd = new double[13][13];
			tc = new double[13][13];
			dp = new double[13][13];
			k = new double[13][13];
			maxdeg = 12;
			E0000(0, 0.0, 0.0, 0.0, 0.0);
		}
		E0000(1, 0.0, latitude, longitude, year);
		return (dec == Double.NaN) ? 0.0 : dec;
	}

	static public String outOfRangeMessage() {
		if (warning == null) {
			warning = Resource.getString("valid_range") + ((int) min_year)
					+ Resource.getString("to") + ((int) max_year)
					+ Resource.getString("outside");
		}
		return out_of_range ? warning : null;
	}

	static private void setDate(int[] date) {
		out_of_range = false;
		year = (double) date[0] + (double) (date[1] - 1) / 12.0;
		if (year < min_year) {
			year = min_year;
			out_of_range = true;
		} else if (year > max_year) {
			year = max_year;
			out_of_range = true;
		}
	}

	static private void E0000(int mode, double alt, double glat, double glon,
			double time) {
		switch (mode) {
		case 0: {
			try {
				URL url = FileIO.getURL("WMM" + cur_cof_year + ".COF");
				if (url == null) {
					not_available = true;
					return;
				}
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream()));
				/* INITIALIZE CONSTANTS */
				maxord = maxdeg;
				sp[0] = 0.0;
				cp[0] = snorm[0] = pp[0] = 1.0;
				dp[0][0] = 0.0;
				a = 6378.137;
				b = 6356.7523142;
				re = 6371.2;
				a2 = a * a;
				b2 = b * b;
				c2 = a2 - b2;
				a4 = a2 * a2;
				b4 = b2 * b2;
				c4 = a4 - b4;
				/* READ WORLD MAGNETIC MODEL SPHERICAL HARMONIC COEFFICIENTS */
				c[0][0] = 0.0;
				cd[0][0] = 0.0;
				String c_str = reader.readLine();
				StringTokenizer tok = new StringTokenizer(c_str, " \t");
				epoch = getDouble(tok.nextToken());
				tok.nextToken(); // model
				for (;;) {
					c_str = reader.readLine();
					/* CHECK FOR LAST LINE IN FILE */
					if (c_str.substring(0, 4).equals("9999"))
						break;
					/* END OF FILE NOT ENCOUNTERED, GET VALUES */
					tok = new StringTokenizer(c_str, " \t");
					n = getInt(tok.nextToken());
					m = getInt(tok.nextToken());
					gnm = getDouble(tok.nextToken());
					hnm = getDouble(tok.nextToken());
					dgnm = getDouble(tok.nextToken());
					dhnm = getDouble(tok.nextToken());
					if (m <= n) {
						c[m][n] = gnm;
						cd[m][n] = dgnm;
						if (m != 0) {
							c[n][m - 1] = hnm;
							cd[n][m - 1] = dhnm;
						}
					}
				}
				/*
				 * CONVERT SCHMIDT NORMALIZED GAUSS COEFFICIENTS TO UNNORMALIZED
				 */
				snorm[0] = 1.0;
				for (n = 1; n <= maxord; n++) {
					snorm[n] = snorm[n - 1] * (double) (2 * n - 1) / (double) n;
					j = 2;
					for (m = 0, D1 = 1, D2 = (n - m + D1) / D1; D2 > 0; D2--, m += D1) {
						k[m][n] = (double) (((n - 1) * (n - 1)) - (m * m))
								/ (double) ((2 * n - 1) * (2 * n - 3));
						if (m > 0) {
							flnmj = (double) ((n - m + 1) * j)
									/ (double) (n + m);
							snorm[n + m * 13] = snorm[n + (m - 1) * 13]
									* Math.sqrt(flnmj);
							j = 1;
							c[n][m - 1] = snorm[n + m * 13] * c[n][m - 1];
							cd[n][m - 1] = snorm[n + m * 13] * cd[n][m - 1];
						}
						c[m][n] = snorm[n + m * 13] * c[m][n];
						cd[m][n] = snorm[n + m * 13] * cd[m][n];
					}
					fn[n] = (double) (n + 1);
					fm[n] = (double) n;
				}
				k[1][1] = 0.0;
				otime = oalt = olat = olon = -1000.0;
				reader.close();
			} catch (IOException e) {
				not_available = true;
			}
		}
			break;
		case 1: {
			dt = time - epoch;
			pi = 3.14159265359;
			dtr = pi / 180.0;
			rlon = glon * dtr;
			rlat = glat * dtr;
			srlon = Math.sin(rlon);
			srlat = Math.sin(rlat);
			crlon = Math.cos(rlon);
			crlat = Math.cos(rlat);
			srlat2 = srlat * srlat;
			crlat2 = crlat * crlat;
			sp[1] = srlon;
			cp[1] = crlon;
			/* CONVERT FROM GEODETIC COORDS. TO SPHERICAL COORDS. */
			if (alt != oalt || glat != olat) {
				q = Math.sqrt(a2 - c2 * srlat2);
				q1 = alt * q;
				q2 = ((q1 + a2) / (q1 + b2)) * ((q1 + a2) / (q1 + b2));
				ct = srlat / Math.sqrt(q2 * crlat2 + srlat2);
				st = Math.sqrt(1.0 - (ct * ct));
				r2 = (alt * alt) + 2.0 * q1 + (a4 - c4 * srlat2) / (q * q);
				r = Math.sqrt(r2);
				d = Math.sqrt(a2 * crlat2 + b2 * srlat2);
				ca = (alt + d) / r;
				sa = c2 * crlat * srlat / (r * d);
			}
			if (glon != olon) {
				for (m = 2; m <= maxord; m++) {
					sp[m] = sp[1] * cp[m - 1] + cp[1] * sp[m - 1];
					cp[m] = cp[1] * cp[m - 1] - sp[1] * sp[m - 1];
				}
			}
			aor = re / r;
			ar = aor * aor;
			br = bt = bp = bpp = 0.0;
			for (n = 1; n <= maxord; n++) {
				ar = ar * aor;
				for (m = 0, D3 = 1, D4 = (n + m + D3) / D3; D4 > 0; D4--, m += D3) {
					/*
					 * COMPUTE UNNORMALIZED ASSOCIATED LEGENDRE POLYNOMIALS AND
					 * DERIVATIVES VIA RECURSION RELATIONS
					 */
					if (alt != oalt || glat != olat) {
						if (n == m) {
							snorm[n + m * 13] = st
									* snorm[n - 1 + (m - 1) * 13];
							dp[m][n] = st * dp[m - 1][n - 1] + ct
									* snorm[n - 1 + (m - 1) * 13];
						} else if (n == 1 && m == 0) {
							snorm[n + m * 13] = ct * snorm[n - 1 + m * 13];
							dp[m][n] = ct * dp[m][n - 1] - st
									* snorm[n - 1 + m * 13];
						} else if (n > 1 && n != m) {
							if (m > n - 2)
								snorm[n - 2 + m * 13] = 0.0;
							if (m > n - 2)
								dp[m][n - 2] = 0.0;
							snorm[n + m * 13] = ct * snorm[n - 1 + m * 13]
									- k[m][n] * snorm[n - 2 + m * 13];
							dp[m][n] = ct * dp[m][n - 1] - st
									* snorm[n - 1 + m * 13] - k[m][n]
									* dp[m][n - 2];
						}
					}
					/*
					 * TIME ADJUST THE GAUSS COEFFICIENTS
					 */
					if (time != otime) {
						tc[m][n] = c[m][n] + dt * cd[m][n];
						if (m != 0)
							tc[n][m - 1] = c[n][m - 1] + dt * cd[n][m - 1];
					}
					/*
					 * ACCUMULATE TERMS OF THE SPHERICAL HARMONIC EXPANSIONS
					 */
					par = ar * snorm[n + m * 13];
					if (m == 0) {
						temp1 = tc[m][n] * cp[m];
						temp2 = tc[m][n] * sp[m];
					} else {
						temp1 = tc[m][n] * cp[m] + tc[n][m - 1] * sp[m];
						temp2 = tc[m][n] * sp[m] - tc[n][m - 1] * cp[m];
					}
					bt = bt - ar * temp1 * dp[m][n];
					bp += (fm[m] * temp2 * par);
					br += (fn[n] * temp1 * par);
					/*
					 * SPECIAL CASE: NORTH/SOUTH GEOGRAPHIC POLES
					 */
					if (st == 0.0 && m == 1) {
						if (n == 1)
							pp[n] = pp[n - 1];
						else
							pp[n] = ct * pp[n - 1] - k[m][n] * pp[n - 2];
						parp = ar * pp[n];
						bpp += (fm[m] * temp2 * parp);
					}
				}
			}
			if (st == 0.0)
				bp = bpp;
			else
				bp /= st;
			/*
			 * ROTATE MAGNETIC VECTOR COMPONENTS FROM SPHERICAL TO GEODETIC
			 * COORDINATES
			 */
			bx = -bt * ca - br * sa;
			by = bp;
			// bz = bt * sa - br * ca;
			/*
			 * COMPUTE DECLINATION (DEC), INCLINATION (DIP) AND TOTAL INTENSITY
			 * (TI)
			 */
			// bh = Math.sqrt((bx * bx) + (by * by));
			// ti = Math.sqrt((bh * bh) + (bz * bz));
			dec = Math.atan2(by, bx) / dtr;
			// dip = Math.atan2(bz, bh) / dtr;
			/*
			 * COMPUTE MAGNETIC GRID VARIATION IF THE CURRENT GEODETIC POSITION
			 * IS IN THE ARCTIC OR ANTARCTIC (I.E. GLAT > +55 DEGREES OR GLAT <
			 * -55 DEGREES) OTHERWISE, SET MAGNETIC GRID VARIATION TO -999.0
			 */
			gv = -999.0;
			if (Math.abs(glat) >= 55.) {
				if (glat > 0.0 && glon >= 0.0)
					gv = dec - glon;
				if (glat > 0.0 && glon < 0.0)
					gv = dec + Math.abs(glon);
				if (glat < 0.0 && glon >= 0.0)
					gv = dec + glon;
				if (glat < 0.0 && glon < 0.0)
					gv = dec - Math.abs(glon);
				if (gv > +180.0)
					gv -= 360.0;
				if (gv < -180.0)
					gv += 360.0;
			}
			otime = time;
			oalt = alt;
			olat = glat;
			olon = glon;
		}
			break;
		default:
			break;
		}
	}

	static private double getDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	static private int getInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
