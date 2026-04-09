

/*
   This is a port of the Swiss Ephemeris Free Edition, Version 1.64.01
   of Astrodienst AG, Switzerland from the original C Code to Java. For
   copyright see the original copyright notices below and additional
   copyright notes in the file named LICENSE, or - if this file is not
   available - the copyright notes at http://www.astro.ch/org.athomeprojects.swisseph/ and
   following.

   For any questions or comments regarding this port to Java, you should
   ONLY contact me and not Astrodienst, as the Astrodienst AG is not involved
   in this port in any way.

   Thomas Mack, mack@idb.cs.tu-bs.de, 23th of April 2001

*/
/* Copyright (C) 1997 - 2000 Astrodienst AG, Switzerland.
   All rights reserved.

  This file is part of Swiss Ephemeris Free Edition.

  Swiss Ephemeris is distributed with NO WARRANTY OF ANY KIND.  No author
  or distributor accepts any responsibility for the consequences of using it,
  or for whether it serves any particular purpose or works at all, unless he
  or she says so in writing.  Refer to the Swiss Ephemeris Public License
  ("SEPL" or the "License") for full details.

  Every copy of Swiss Ephemeris must include a copy of the License,
  normally in a plain ASCII text file named LICENSE.  The License grants you
  the right to copy, modify and redistribute Swiss Ephemeris, but only
  under certain conditions described in the License.  Among other things, the
  License requires that the copyright notices and this notice be preserved on
  all copies.

  For uses of the Swiss Ephemeris which do not fall under the definitions
  laid down in the Public License, the Swiss Ephemeris Professional Edition
  must be purchased by the developer before he/she distributes any of his
  software or makes available any product or service built upon the use of
  the Swiss Ephemeris.

  Authors of the Swiss Ephemeris: Dieter Koch and Alois Treindl

  The authors of Swiss Ephemeris have no control or influence over any of
  the derived works, i.e. over software or services created by other
  programmers which use Swiss Ephemeris functions.

  The names of the authors or of the copyright holder (Astrodienst) must not
  be used for promoting any software, product or service which uses or contains
  the Swiss Ephemeris. This copyright notice is the ONLY place where the
  names of the authors can legally appear, except in cases where they have
  given special permission in writing.

  The trademarks 'Swiss Ephemeris' and 'Swiss Ephemeris inside' may be used
  for promoting such software, products or services.
*/
package org.athomeprojects.swisseph;

/*
* This class contains many constants for internal use only.
* It does not need to be instantiated.
*/
class SwephData {

  // Mean node seems to have a constant speed and distance?

  // Aus: sweodef.h:
  static final double M_PI = 3.14159265358979323846;

  // Aus sweph.h:

  /* planetary radii in meters */
  static final int NDIAM = (SweConst.SE_VESTA + 1);
  static final double pla_diam[] = {1392000000.0, /* Sun */
                           3476300.0, /* Moon */
                           2439000.0 * 2, /* Mercury */
                           6052000.0 * 2, /* Venus */
                           3397200.0 * 2, /* Mars */
                          71398000.0 * 2, /* Jupiter */
                          60000000.0 * 2, /* Saturn */
                          25400000.0 * 2, /* Uranus */
                          24300000.0 * 2, /* Neptune */
                           2500000.0 * 2, /* Pluto */
                           0, 0, 0, 0,    /* nodes and apogees */
                           6378140.0 * 2, /* Earth */
                                 0.0, /* Chiron */
                                 0.0, /* Pholus */
                            913000.0, /* Ceres */
                            523000.0, /* Pallas */
                            244000.0, /* Juno */
                            501000.0, /* Vesta */
                        };

  static final double J2000 = 2451545.0;        // 2000 January 1.5
  static final double B1950 = 2433282.42345905; // 1950 January 0.923
  static final double J1900 = 2415020.0;        // 1900 January 0.5

  static final int MPC_CERES = 1;
  static final int MPC_PALLAS = 2;
  static final int MPC_JUNO = 3;
  static final int MPC_VESTA = 4;
  static final int MPC_CHIRON = 2060;
  static final int MPC_PHOLUS = 5145;

  static final String SE_NAME_SUN = "Sun";
  static final String SE_NAME_MOON = "Moon";
  static final String SE_NAME_MERCURY = "Mercury";
  static final String SE_NAME_VENUS = "Venus";
  static final String SE_NAME_MARS = "Mars";
  static final String SE_NAME_JUPITER = "Jupiter";
  static final String SE_NAME_SATURN = "Saturn";
  static final String SE_NAME_URANUS = "Uranus";
  static final String SE_NAME_NEPTUNE = "Neptune";
  static final String SE_NAME_PLUTO = "Pluto";
  static final String SE_NAME_MEAN_NODE = "mean Node";
  static final String SE_NAME_TRUE_NODE = "true Node";
  static final String SE_NAME_MEAN_APOG = "mean Apogee";
  static final String SE_NAME_OSCU_APOG = "osc. Apogee";
  static final String SE_NAME_EARTH = "Earth";
  static final String SE_NAME_CERES = "Ceres";
  static final String SE_NAME_PALLAS = "Pallas";
  static final String SE_NAME_JUNO = "Juno";
  static final String SE_NAME_VESTA = "Vesta";
  static final String SE_NAME_CHIRON = "Chiron";
  static final String SE_NAME_PHOLUS = "Pholus";


  static final String SE_NAME_CUPIDO = "Cupido";
  static final String SE_NAME_HADES = "Hades";
  static final String SE_NAME_ZEUS = "Zeus";
  static final String SE_NAME_KRONOS = "Kronos";
  static final String SE_NAME_APOLLON = "Apollon";
  static final String SE_NAME_ADMETOS = "Admetos";
  static final String SE_NAME_VULKANUS = "Vulkanus";
  static final String SE_NAME_POSEIDON = "Poseidon";
  static final String SE_NAME_ISIS = "Isis";
  static final String SE_NAME_NIBIRU = "Nibiru";
  static final String SE_NAME_HARRINGTON = "Harrington";
  static final String SE_NAME_NEPTUNE_LEVERRIER = "Leverrier";
  static final String SE_NAME_NEPTUNE_ADAMS = "Adams";
  static final String SE_NAME_PLUTO_LOWELL = "Lowell";
  static final String SE_NAME_PLUTO_PICKERING = "Pickering";
  static final String SE_NAME_VULCAN = "Vulcan";
  static final String SE_NAME_WHITE_MOON = "White Moon";

  static final AyaInit[] ayanamsa = new AyaInit[] {
    new AyaInit(2433282.5, 24.042044444),  /* 0: Fagan/Bradley (Default) */
    new AyaInit(J1900, 360 - 337.53953),   /* 1: Lahiri (Robert Hand) */
    new AyaInit(J1900, 360 - 333.58695),   /* 2: De Luce (Robert Hand) */
    new AyaInit(J1900, 360 - 338.98556),   /* 3: Raman (Robert Hand) */
    new AyaInit(J1900, 360 - 341.33904),   /* 4: Ushashashi (Robert Hand) */
    new AyaInit(J1900, 360 - 337.636111),  /* 5: Krishnamurti (Robert Hand) */
    new AyaInit(J1900, 360 - 333.0369024), /* 6: Djwhal Khool; (Graham Dawson)
                                            *    Aquarius entered on 1 July 2117 */
    new AyaInit(J1900, 360 - 338.917778),  /* 7: Yukteshwar; (David Cochrane) */
    new AyaInit(J1900, 360 - 338.634444),  /* 8: JN Bhasin; (David Cochrane) */
    new AyaInit(1684532.5, -3.36667),      /* 9: Babylonian, Kugler 1 */
    new AyaInit(1684532.5, -4.76667),      /*10: Babylonian, Kugler 2 */
    new AyaInit(1684532.5, -5.61667),      /*11: Babylonian, Kugler 3 */
    new AyaInit(1684532.5, -4.56667),      /*12: Babylonian, Huber */
    new AyaInit(1673941, -5.079167),       /*13: Babylonian, Mercier;
                                            *    eta Piscium culminates with zero point */
    new AyaInit(1684532.5, -4.44088389),   /*14: t0 is defined by Aldebaran at 15 Taurus */
    new AyaInit(1674484, -9.33333),        /*15: Hipparchos */
    new AyaInit(1927135.8747793, 0),       /*16: Sassanian */
    new AyaInit(1746443.513, 0),           /*17: Galactic Center at 0 Sagittarius */
    new AyaInit(J2000, 0),                 /*18: J2000 */
    new AyaInit(J1900, 0),                 /*19: J1900 */
    new AyaInit(B1950, 0),                 /*20: B1950 */
        };

/*
 * earlier content
 */

  static final double PI = M_PI;   // 3.14159265358979323846, math.h
  static final double TWOPI = 2.0 * PI;

//  static final int ENDMARK = -99;

  static final int SEI_EPSILON = -2;
  static final int SEI_NUTATION = -1;
  static final int SEI_EMB = 0;
  static final int SEI_EARTH = 0;
  static final int SEI_SUN = 0;
  static final int SEI_MOON = 1;
  static final int SEI_MERCURY = 2;
  static final int SEI_VENUS = 3;
  static final int SEI_MARS = 4;
  static final int SEI_JUPITER = 5;
  static final int SEI_SATURN = 6;
  static final int SEI_URANUS = 7;
  static final int SEI_NEPTUNE = 8;
  static final int SEI_PLUTO = 9;
  static final int SEI_SUNBARY = 10;     // barycentric sun
  static final int SEI_ANYBODY = 11;     // any asteroid
  static final int SEI_CHIRON = 12;
  static final int SEI_PHOLUS = 13;
  static final int SEI_CERES = 14;
  static final int SEI_PALLAS = 15;
  static final int SEI_JUNO = 16;
  static final int SEI_VESTA = 17;

  static final int SEI_NPLANETS = 18;

  static final int SEI_MEAN_NODE = 0;
  static final int SEI_TRUE_NODE = 1;
  static final int SEI_MEAN_APOG = 2;
  static final int SEI_OSCU_APOG = 3;

  static final int SEI_NNODE_ETC = 4;

  static final int SEI_FLG_HELIO = 1;
  static final int SEI_FLG_ROTATE = 2;
  static final int SEI_FLG_ELLIPSE = 4;
  static final int SEI_FLG_EMBHEL = 8; // TRUE, if heliocentric earth is given
                                     // instead of barycentric sun
                                     // i.e. bary sun is computed from
                                     // barycentric and heliocentric earth

  static final int SEI_FILE_PLANET = 0;
  static final int SEI_FILE_MOON = 1;
  static final int SEI_FILE_MAIN_AST = 2;
  static final int SEI_FILE_ANY_AST = 3;
  static final int SEI_FILE_FIXSTAR = 4;

  // Aus swephexph.h:
  static final int SEI_FILE_TEST_ENDIAN = 0x616263;   // abc
  static final int SEI_FILE_BIGENDIAN = 0;
  static final int SEI_FILE_NOREORD = 0;
  static final int SEI_FILE_LITENDIAN = 1;
  static final int SEI_FILE_REORD = 2;

  static final int SEI_FILE_NMAXPLAN = 50;
  static final int SEI_FILE_EFPOSBEGIN = 500;

  static final String SE_FILE_SUFFIX = "se1";

  static final int SEI_NEPHFILES = 7;
  static final int SEI_CURR_FPOS = -1;

/* Chiron's orbit becomes chaotic
 * before 720 AD and after 4606 AD, because of close encounters
 * with Saturn. Accepting a maximum error of 5 degrees,
 * the ephemeris is good between the following dates:
 */
  static final double CHIRON_START = 1958470.5;      // 1.1.650
  static final double CHIRON_END = 3419437.5;        // 1.1.4650

/* Pholus's orbit is unstable as well, because he sometimes
 * approaches Saturn.
 * Accepting a maximum error of 5 degrees,
 * the ephemeris is good after the following date:
 */
  static final double PHOLUS_START = 314845.5;       // 1.1.-3850

  static final double MOSHPLEPH_START =  625000.5;
  static final double MOSHPLEPH_END =   2818000.5;
  static final double MOSHLUEPH_START =  625000.5;
  static final double MOSHLUEPH_END =   2818000.5;
  static final double MOSHNDEPH_START = -254900.5; // 14 Feb -5410 00:00 ET jul.
  static final double MOSHNDEPH_END =   3697000.5; // 11 Dec 5409 00:00 ET, greg.


  static final int MAXORD = 40;

  static final double NCTIES = 6.0;    // number of centuries per eph. file

  static final int NOT_AVAILABLE = -2;
  static final int BEYOND_EPH_LIMITS = -3;

  static final int J_TO_J2000 = 1;
  static final int J2000_TO_J = -1;

// we always use Astronomical Almanac constants, if available
  static final double MOON_MEAN_DIST = 384400000.0;           // in m, AA 1996, F2
  static final double MOON_MEAN_INCL = 5.1453964;             // AA 1996, D2
  static final double MOON_MEAN_ECC = 0.054900489;            // AA 1996, F2
  /*static final double SUN_EARTH_MRAT = 328900.5;*/               // Su/(Ea+Mo) AA 1996, K6
  static final double SUN_EARTH_MRAT = 332946.0;              // Su / (Ea only)
  static final double EARTH_MOON_MRAT = 1 / 0.012300034;      // AA 1996, K6
  static final double CLIGHT = 2.99792458e+8;                 // m/s, AA 1996 K6
  static final double HELGRAVCONST = 1.32712438e+20; // G * M(sun), m^3/sec^2, AA 1996 K6
  static final double GEOGCONST = 3.98600448e+14; // G * M(earth) m^3/sec^2, AA 1996 K6
  static final double KGAUSS = 0.01720209895; // Gaussian gravitational constant K6
  static final double KGAUSS_GEO = 0.0000298122353216;        // Earth only
  // static final double KGAUSS_GEO = 0.0000299502129737        // Earth + Moon
  static final double PRECESSCONST = 50.290966;               // AA 1996 K6
  static final double SUN_RADIUS = 959.63 / 3600 * SwissData.DEGTORAD;  // Meeus germ. p 391
  static final double EARTH_RADIUS = 6378137.0;               // AA 1998 K13
  static final double EARTH_OBLATENESS = 1.0/ 298.257223563;  // AA 1998 K13
  static final double EARTH_ROT_SPEED = 7.2921151467e-5 * 86400; // in rad/day, expl. suppl., p 162

  static final double LIGHTTIME_AUNIT = 499.004782/3600/24;  // 8.3167 minutes (days), AA K6


  /* node of ecliptic measured on ecliptic 2000 */
  static final double SSY_PLANE_NODE_E2000 = 107.582569 * SwissData.DEGTORAD;
  /* node of ecliptic measured on solar system rotation plane */
  static final double SSY_PLANE_NODE = 107.58883388 * SwissData.DEGTORAD;
  /* inclination of ecliptic against solar system rotation plane */
  static final double SSY_PLANE_INCL = 1.578701 * SwissData.DEGTORAD;

  static final double KM_S_TO_AU_CTY = 21.095;                  // km/s to AU/year
  static final double MOON_SPEED_INTV = 0.00005;         // 4.32 seconds (in days)
  static final double PLAN_SPEED_INTV = 0.0001;          // 8.64 seconds (in days)
  static final double MEAN_NODE_SPEED_INTV = 0.001;
  static final double NODE_CALC_INTV = 0.0001;
  static final double NODE_CALC_INTV_MOSH = 0.1;
  static final double NUT_SPEED_INTV = 0.0001;
  static final double DEFL_SPEED_INTV = 0.0000005;


/*
 * stuff exported from swemplan.c and swemmoon.c
 * and constants used inside these functions.
************************************************************/

  static final double STR = 4.8481368110953599359e-6;   // radians per arc second


  // Aus sweph.c:
  static final int IS_PLANET = 0;
  static final int IS_MOON = 1;
  static final int IS_ANY_BODY = 2;
  static final int IS_MAIN_ASTEROID = 3;

  static final boolean DO_SAVE = true;
  static final boolean NO_SAVE = false;

//  java.io.RandomAccessFile fixfp = null;     // fixed stars


//  static final int pnoext2int[] = {SEI_SUN, SEI_MOON, SEI_MERCURY, SEI_VENUS,
//    SEI_MARS, SEI_JUPITER, SEI_SATURN, SEI_URANUS, SEI_NEPTUNE, SEI_PLUTO,
//    0, 0, 0, 0, SEI_EARTH, SEI_CHIRON, SEI_PHOLUS, SEI_CERES, SEI_PALLAS,
//    SEI_JUNO, SEI_VESTA, };


//////////////////////////////////////////////////////////////////////////////
// extensions: ///////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////
// SURYA: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double SURYA_MAX_LON_SPEED = 1.025;
// NOTOPO, JPL: 1.0233362482825890 198.1481527339882689 656297.5935287680476904 /    nan    nan    nan

  static final double SURYA_MIN_LON_SPEED = 0.946;
// NOTOPO, JPL: 0.9495985191372013 13.6641834118152925 630178.1503921914845705 /    nan    nan    nan

  static final double SURYA_MAX_LON_ACCEL = 0.000735;
// NOTOPO, JPL: 0.0007321244674241 111.1489600158149074 632105.3974533881992102 632105.4891571882180870 /    nan    nan    nan

  static final double SURYA_MIN_LON_ACCEL = -0.000720;
// NOTOPO, JPL: -0.0007172603891239 281.9291936621758623 663685.2516582887619734 663685.3433620887808502 /    nan    nan    nan

  static final double SURYA_MAX_TOPO_LON_SPEED = 1.04;
// TOPO, SWI:   1.0384413287108927 0.0000000000000000 -199501.5087046779808588 / -0.5433742788 -4.7293263997 -1664.2795669022
// TOPO, JPL:   1.0365074888828785 209.2107463691459657 795098.6519823583075777 / -148.9   17.7 12497.5

  static final double SURYA_MIN_TOPO_LON_SPEED = 0.93;
// TOPO, SWI:   0.9349095448292457 0.0000000000000000 -224529.9592720646178350 -16.3604237359 -3.3169784104 3529.4848477192
// TOPO, JPL:   0.9365203548203047 30.4143819284346542 717121.0431665068026632 /  -79.7    7.4 -48617.2

  static final double SURYA_MAX_TOPO_LON_ACCEL = 0.06;
// TOPO, SWI:   0.0593799785863376
// TOPO, JPL:   0.0583456448574779 275.0416878063256263 2700635.1911227395758033 2700635.2918350920081139 /   11.0   52.2 -110000.0

  static final double SURYA_MIN_TOPO_LON_ACCEL = -0.06;
// TOPO, SWI:   -0.0594559646638496
// TOPO, JPL:   -0.0584748042751905 271.9731910425273895 1139219.1189967475365847 1139219.2191002487670630 /   11.0   52.2 -110000.0


  static final double SURYA_MAX_LAT_SPEED = 0.0000620;
// NOTOPO, JPL: 0.0000618025182438 241.4261311889077604 1113622.1226309132762253 /    nan    nan    nan

  static final double SURYA_MIN_LAT_SPEED = -0.0000618;
// NOTOPO, JPL: -0.0000614244567336 299.9816306303840747 1156413.5820919747930020 /    nan    nan    nan

  static final double SURYA_MAX_LAT_ACCEL = 0.0000203;
// NOTOPO, JPL: 0.0000200049293864 85.4713554114340468 1067082.5648221261799335 1067082.6565259261988103 /    nan    nan    nan

  static final double SURYA_MIN_LAT_ACCEL = -0.0000204;
// NOTOPO, JPL: -0.0000202677724199 267.1036516425933200 836064.9195209722965956 836065.0112247723154724 /    nan    nan    nan

  static final double SURYA_MAX_TOPO_LAT_SPEED = 0.0066;
// TOPO, SWI:   0.0064145372870949
// TOPO, JPL:   0.0063447811040511 167.6535710049226680 943711.7743394003482535 /  129.0    4.8 10365.7

  static final double SURYA_MIN_TOPO_LAT_SPEED = -0.0065;
// TOPO, SWI:   -0.0064584410123400
// TOPO, JPL:   -0.0063137486844727 183.6555443690479876 1222042.6236723833717406 /  -94.5   -3.3 33636.2

  static final double SURYA_MAX_TOPO_LAT_ACCEL = 0.025;
// TOPO, SWI:   0.0244212760934252
// TOPO, JPL:   0.0238296790897776 192.2272313218551290 632914.0236237654462457 632914.1249053360661492 /   11.0   52.2 -110000.0

  static final double SURYA_MIN_TOPO_LAT_ACCEL = -0.025;
// TOPO, SWI:   -0.0244146697145761
// TOPO, JPL:   -0.0238413888237050 204.3178283029897102 638404.4897474966710433 638404.5898247783770785 /   11.0   52.2 -110000.0


  static final double SURYA_MAX_DIST_SPEED = 0.000328;
// NOTOPO, JPL: 0.0003251647876611 285.1359892485851901 630451.5194200477562845 /    nan    nan    nan

  static final double SURYA_MIN_DIST_SPEED = -0.000327;
// NOTOPO, JPL: -0.0003247521770552 108.3678967045579498 669357.4068008563481271 /    nan    nan    nan

  static final double SURYA_MAX_DIST_ACCEL = 0.00000734;
// NOTOPO, JPL: 0.0000073151160377 197.0004801545529460 647895.7830748385749757 647895.8747786385938525 /    nan    nan    nan

  static final double SURYA_MIN_DIST_ACCEL = -0.00000694;
// NOTOPO, JPL: -0.0000069158807111 15.5783679317648520 641502.5609527225606143 641502.6526565225794911 /    nan    nan    nan

  static final double SURYA_MAX_TOPO_DIST_SPEED = 0.00059;
// TOPO, SWI:   0.0005789367418679
// TOPO, JPL:   0.0005633150154512 318.6451137771567801 1329557.3832753794267774 /   84.1    1.8 -19441.6

  static final double SURYA_MIN_TOPO_DIST_SPEED = -0.00058;
// TOPO, SWI:   -0.0005724228279118
// TOPO, JPL:   -0.0005647002245768 174.5410821021679340 2142079.3515738314017653 /  143.7    1.1 -26129.6

  static final double SURYA_MAX_TOPO_DIST_ACCEL = 0.00104;
// TOPO, SWI:   0.0010219054361193
// TOPO, JPL:   0.0010043877043150 178.6859059007219059 713984.7248655840521678 713984.8256328357383609 /   11.0   52.2 -110000.0

  static final double SURYA_MIN_TOPO_DIST_ACCEL = -0.0013;
// TOPO, SWI:   -0.0012818853764991
// TOPO, JPL:   -0.0010042541999776 357.1345657907744453 833598.1178349015535787 833598.2180397533811629 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// CHANDRA: ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double CHANDRA_MAX_LON_SPEED = 15.41;
// NOTOPO, JPL: 15.4041164556993593 180.5585719899691526 867780.5870537008158863 /    nan    nan    nan

  static final double CHANDRA_MIN_LON_SPEED = 11.75;
// NOTOPO, JPL: 11.7572903229686911 48.7868453140692111 1045632.5874787108041346 /    nan    nan    nan

  static final double CHANDRA_MAX_LON_ACCEL = 0.522;
// NOTOPO, JPL: 0.5186414758242096 126.4203023968987338 1065011.4344986998476088 1065011.5262024998664856 /    nan    nan    nan

  static final double CHANDRA_MIN_LON_ACCEL = -0.520;
// NOTOPO, JPL: -0.5185377158655800 124.9880532266025597 1139170.4634979651309550 1139170.5552017651498318 /    nan    nan    nan

  static final double CHANDRA_MAX_TOPO_LON_SPEED = 22.0;
// TOPO, SWI:   21.4309682969720328 179.1777778485675867 3558401.0245797489769757 / 123.7452556088 1.2763677912 -9647.8126131759
// TOPO, JPL:   21.1149235251773284 21.1660559007296314 2077012.0076510021463037 /  167.6    1.2 -56742.6

  static final double CHANDRA_MIN_TOPO_LON_SPEED = 6.0;
// TOPO, SWI:   6.2043978283097267 91.2062770401382892 110594.4898514153319411 -82.1318313870 -2.2805770007 3320.8680109613
// TOPO, JPL:   6.3597932870754974 7.2417677341565536 2248084.8536576554179192 /   72.4   -4.6 18046.7

  static final double CHANDRA_MAX_TOPO_LON_ACCEL = 23.5;
// TOPO, SWI:   22.9729155357766466
//              22.5191319837460533 189.8858251817875384 2303261.6343037686310709 2303261.7453831625171006 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   22.5728018782278816 188.2658986628977686 1030021.1688936793943867 1030021.2732220485340804 /   11.0   52.2 -110000.0

  static final double CHANDRA_MIN_TOPO_LON_ACCEL = -23.2;
// min topo3:   -38.1716335
// TOPO, SWI:   -22.9387038817123141
//              -22.5127674732828922 160.2778837680125719 218788.6020565493090544 218788.7164031058782712 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   -22.5391436800995280 164.1286691196248171 2558171.2196601303294301 2558171.3217258220538497 /   11.0   52.2 -110000.0


  static final double CHANDRA_MAX_LAT_SPEED = 1.44;
// NOTOPO, JPL: 1.4161178854830674 179.4697049090585210 2804426.0038991500623524 /    nan    nan    nan

  static final double CHANDRA_MIN_LAT_SPEED = -1.44;
// NOTOPO, JPL: -1.4172129253432328 178.3605766215862332 628523.9972474509850144 /    nan    nan    nan

  static final double CHANDRA_MAX_LAT_ACCEL = 0.366;
// NOTOPO, JPL: 0.3639886778563629 178.7949269887757282 1886142.7578099258244038 1886142.8495137258432806 /    nan    nan    nan

  static final double CHANDRA_MIN_LAT_ACCEL = -0.366;
// NOTOPO, JPL: -0.3636913084572940 180.6303963665253036 912666.9377527404576540 912667.0294565404765308 /    nan    nan    nan

  static final double CHANDRA_MAX_TOPO_LAT_SPEED = 4.2;
// TOPO, SWI:   4.0196744317594568
// TOPO, JPL:   3.8905318518976895 4.1726025186527806 1045864.4757649956736714 /  -49.8    1.0 -330810.4

  static final double CHANDRA_MIN_TOPO_LAT_SPEED = -4.7;
// TOPO, SWI:   -4.1184649007547964
// TOPO, JPL:   -3.9483356515061541 8.3800829312503424 944132.7195180986309424 /  -82.7   16.8 -54378.5

  static final double CHANDRA_MAX_TOPO_LAT_ACCEL = 12.5;
// TOPO, SWI:   12.3783195665162751
// TOPO, JPL:   12.0090702595465828 156.9810838245764160 1658430.5072499848902225 1658430.6155795308295637 /   11.0   52.2 -110000.0

  static final double CHANDRA_MIN_TOPO_LAT_ACCEL = -12.5;
// TOPO, SWI:   -12.0415108646951037
// TOPO, JPL:   -11.8365401411952877 18.3511634305493487 713555.6669830741593614 713555.7701688770903274 /   11.0   52.2 -110000.0


  static final double CHANDRA_MAX_DIST_SPEED = 0.000044;
// NOTOPO, JPL: 0.0000430106329181 118.0467546874002380 857921.8783288714475930 /    nan    nan    nan

  static final double CHANDRA_MIN_DIST_SPEED = -0.0000434;
// NOTOPO, JPL: -0.0000430670864277 117.1142513980806257 751632.3483938642311841 /    nan    nan    nan

  static final double CHANDRA_MAX_DIST_ACCEL = 0.0000140;
// NOTOPO, JPL: 0.0000137704986304 179.2396768958410860 867780.5870537008158863 867780.6787575008347631 /    nan    nan    nan

  static final double CHANDRA_MIN_DIST_ACCEL = -0.00000898;
// NOTOPO, JPL: -0.0000089378018376 90.1277326204999270 784496.9382487572729588 784497.0299525572918355 /    nan    nan    nan

  static final double CHANDRA_MAX_TOPO_DIST_SPEED = 0.00030;
// TOPO, SWI:   0.0002987444506570
// TOPO, JPL:   0.0002909664964957 284.8643480099901808 1080096.5486003318801522 /  144.6   -2.3 4895.6

  static final double CHANDRA_MIN_TOPO_DIST_SPEED = -0.00031;
// TOPO, SWI:   -0.0002975759131239
// TOPO, JPL:   -0.0002895923864196 55.7017996303879528 1362930.4575669008772820 /  167.6   -2.3 17165.7

  static final double CHANDRA_MAX_TOPO_DIST_ACCEL = 0.00099;
// TOPO, SWI:   0.0009695039477059
// TOPO, JPL:   0.0009507373452100 177.1836363491260897 827412.1154932569479570 827412.2165193189866841 /   11.0   52.2 -110000.0

  static final double CHANDRA_MIN_TOPO_DIST_ACCEL = -0.00098;
// TOPO, SWI:   -0.0009611955640193
// TOPO, JPL:   -0.0009452968840833 241.9355511752623897 733411.5362529422855005 733411.6373956773895770 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// BUDHA: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double BUDHA_MAX_LON_SPEED = 2.23;
// NOTOPO, SWI: 2.2121426791396726
//              2.2118740410345783 2.0229662866546647 3632579.7922155149281025
// NOTOPO, JPL: 2.1964845502320962 3.0367044971105202 2816224.3305718521587551 / 

  static final double BUDHA_MIN_LON_SPEED = -1.40;
// NOTOPO, SWI: -1.3908298594148474
//              -1.3898959805347326 0.5092469431197060 3682809.9214275842532516
// NOTOPO, JPL: -1.3521147816768901 3.0331619351929362 2777343.6256446754559875 / 

  static final double BUDHA_MAX_LON_ACCEL = 0.20;
// NOTOPO, SWI: 0.1967914822899190 14.2061443309504511
//              0.1968017050259578 14.4414680144461727 -175436.4430393589718733 -175436.0775205177196767
// NOTOPO, JPL: 0.1965936384404356 14.3754648083684629 643469.9357961784116924 643470.2695755218155682 / 

  static final double BUDHA_MIN_LON_ACCEL = -0.199;
// NOTOPO, JPL: -0.1986619469289480 14.0525985387717469 2385435.3617391046136618 2385435.5139587139710784 / 

  static final double BUDHA_MAX_TOPO_LON_SPEED = 2.3;
// TOPO, SWI:   2.2212980742115511 2.1541861391796004 3632579.8976156003773212 / 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   2.2019660581364016 3.0549461351521785 2794671.4474170133471489 /   11.0   52.2 -110000.0


  static final double BUDHA_MIN_TOPO_LON_SPEED = -1.49;
// TOPO, SWI:   -1.4063442078716697 0.1278834320364695 3606099.4173800637945533 25.4514719644 3.6416897111 -8080.5345996378
// TOPO, JPL:   -1.3655028210686915 3.0368029001502350 2726937.0293830074369907 /   11.0   52.2 -110000.0

  static final double BUDHA_MAX_TOPO_LON_ACCEL = 0.281;
// TOPO, SWI:   0.2720747632986203
//              0.2702626383612824 14.4371256403208434 -134531.0275387382425833 -134530.9010568788216915 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.2675637274842573 13.7209457445615612 2597974.1161503684706986 2597974.2541675390675664 /   11.0   52.2 -110000.0

  static final double BUDHA_MIN_TOPO_LON_ACCEL = -0.282;
// TOPO, JPL:   -0.2730133058585249 13.0135107334394888 2267820.7196086999028921 2267820.8311376208439469 /   11.0   52.2 -110000.0


  static final double BUDHA_MAX_LAT_SPEED = 0.35;
// NOTOPO, SWI: 0.3461375049101383
// NOTOPO, JPL: 0.3459340957539888 3.1275672465673381 2046046.1421363854315132 / 

  static final double BUDHA_MIN_LAT_SPEED = -0.31;
// NOTOPO, SWI: -0.3029605350228085
// NOTOPO, JPL: -0.2959580096865397 4.5857649974633290 2786261.3525251694954932 / 

  static final double BUDHA_MAX_LAT_ACCEL = 0.036;
// NOTOPO, JPL: 0.0348286193410149 6.1558577433976609 656209.0999486010987312 656209.3120646663010120 / 

  static final double BUDHA_MIN_LAT_ACCEL = -0.044;
// NOTOPO, JPL: -0.0431614487026422 3.7132593670662573 2795997.5029838741756976 2795997.6633058190345764 / 

  static final double BUDHA_MAX_TOPO_LAT_SPEED = 0.37;
// TOPO, SWI:   0.3535327026595066
// TOPO, JPL:   0.3516211846308093 4.3248275513309125 2127856.8769019907340407 / -112.4   20.8 -471130.3

  static final double BUDHA_MIN_TOPO_LAT_SPEED = -0.34;
// TOPO, SWI:   -0.3110366765430237
// TOPO, JPL:   -0.3024348731924316 3.4445804868699952 2815115.3181047569960356 /   11.0   52.2 -110000.0

  static final double BUDHA_MAX_TOPO_LAT_ACCEL = 0.078;
// TOPO, SWI:   0.0709869852337391
// TOPO, JPL:   0.0681552201699322 5.8364023444659665 2681169.2107728449627757 2681169.3127622539177537 /   11.0   52.2 -110000.0

  static final double BUDHA_MIN_TOPO_LAT_ACCEL = -0.10;
// TOPO, SWI:   -0.0922493674654878
// TOPO, JPL:   -0.0729233751466286 3.0273764042146354 2815000.2646535192616284 2815000.4375549573451281 /   11.0   52.2 -110000.0


  static final double BUDHA_MAX_DIST_SPEED = 0.0286;
// NOTOPO, SWI: 0.0285361509872121
// NOTOPO, JPL: 0.0284435836536158 17.8220744613078921 2813056.3789104702882469 / 

  static final double BUDHA_MIN_DIST_SPEED = -0.0285;
// NOTOPO, SWI: -0.0283017805993105
// NOTOPO, JPL: -0.0282165685365427 341.9215268668302770 2782882.9571671239100397 / 

  static final double BUDHA_MAX_DIST_ACCEL = 0.00325;
// NOTOPO, SWI: 0.0032332968656052
// NOTOPO, JPL: 0.0032071536310416 3.0073281498403617 1478471.6755875691305846 1478474.2478824988938868 / 

  static final double BUDHA_MIN_DIST_ACCEL = -0.00150;
// NOTOPO, SWI: -0.0014746281509695
// NOTOPO, JPL: -0.0014443126861754 3.0209131307652735 1212364.5652429726906121 1212365.4193637575954199 / 

  static final double BUDHA_MAX_TOPO_DIST_SPEED = 0.031;
// TOPO, SWI:   0.0287123956183378
// TOPO, JPL:   0.0285904873909507 17.8171901085420359 2779452.2660587546415627 /   11.0   52.2 -110000.0

  static final double BUDHA_MIN_TOPO_DIST_SPEED = -0.031;
// TOPO, SWI:   -0.0284478735129602
// TOPO, JPL:   -0.0283720842286670 341.9244805616797294 2720424.8167121694423258 /   11.0   52.2 -110000.0

  static final double BUDHA_MAX_TOPO_DIST_ACCEL = 0.0045;
// TOPO, SWI:   0.0042518661363709
// TOPO, JPL:   0.0041135772523493 3.5253503113738702 823188.5811508881160989 823188.6866559227928519 /   11.0   52.2 -110000.0

  static final double BUDHA_MIN_TOPO_DIST_ACCEL = -0.0026;
// TOPO, SWI:   -0.0024527311665292 0.5167782633536149 -5744.8122126676598782
// TOPO, JPL:   -0.0024167702503400 3.3085032796661267 912476.0649550299858674 912476.1788295382866636 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// SHUKRA: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double SHUKRA_MAX_LON_SPEED = 1.266;
// NOTOPO, SWI: 1.2647832575015769 8.0946758136625334 -223273.6698087960830890
// NOTOPO, JPL: 1.2626880451301978 6.8592131288181690 655532.6087605641223490 / 

  static final double SHUKRA_MIN_LON_SPEED = -0.65;
// NOTOPO, SWI: -0.6442598039379197
//              -0.6433716551442837 0.1763535479380494 -92731.2864470719941892
// NOTOPO, JPL: -0.6342143262436917 3.0671365776595962 786068.8421976494137198 / 

  static final double SHUKRA_MAX_LON_ACCEL = 0.0427;
// NOTOPO, SWI: 0.0424074627825448 24.8562467442907575 -233439.0194156236830167 -233438.6142852465854958
// NOTOPO, JPL: 0.0422089427157020 25.5555337892983800 648281.9859329168684781 648282.5466118947369978 / 

  static final double SHUKRA_MIN_LON_ACCEL = -0.0432;
// NOTOPO, SWI: -0.0431008459219351
//              -0.0430347501870898 25.2030500717861798 -114352.8623767224780750 -114352.3139205740008038
// NOTOPO, JPL: -0.0428785353088984 25.7126449364186271 764448.4358926375862211 764448.7770163168897852 / 


  static final double SHUKRA_MAX_TOPO_LON_SPEED = 1.28;
// TOPO, SWI:   1.2730954589558330 5.3700635638143410 -119909.2988172726909397 / -134.0001301493 1.8017167065 -8673.2746795623
// TOPO, JPL:   1.2703027775692810 5.4922725269008765 661377.0886009712703526 /   94.5   -6.2 -491257.9

  static final double SHUKRA_MIN_TOPO_LON_SPEED = -0.70;
// TOPO, SWI:   -0.6936977087256924 1.2466270148142939 445643.9324010963318869 53.4798848587 -4.4726827850 -7886.1605683664
// TOPO, JPL:   -0.6676040834690141 3.6769337485729210 2802347.7158071650192142 /  117.3    9.6 -79312.5

  static final double SHUKRA_MAX_TOPO_LON_ACCEL = 0.246;
// TOPO, SWI:   0.2433226750300663
// TOPO, JPL:   0.2394634181851154 15.7214745858053107 2391279.1253627552650869 2391279.2436700705438852 /   11.0   52.2 -110000.0

  static final double SHUKRA_MIN_TOPO_LON_ACCEL = -0.245;
// TOPO, SWI:   -0.2440019874759702
//              -0.2392102393704883 17.8923225230373077 1870399.8055008733645082 1870399.9183341816533357 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   -0.2390424284021380 14.9008573529047226 2136669.7269426635466516 2136669.8495701653882861 /   11.0   52.2 -110000.0


  static final double SHUKRA_MAX_LAT_SPEED = 0.264;
// NOTOPO, SWI: 0.2626687595047291
// NOTOPO, JPL: 0.2624782757931561 11.9262022773096987 2731687.9197727106511593 / 

  static final double SHUKRA_MIN_LAT_SPEED = -0.251;
// NOTOPO, SWI: -0.2497768135289706
// NOTOPO, JPL: -0.2433475539005757 16.5902349882487457 2771998.1607629279606044 / 

  static final double SHUKRA_MAX_LAT_ACCEL = 0.0167;
// NOTOPO, SWI: 0.0165804236663561
//              0.0165804236663561 0.3206358128090869 -237545.8259068329643924
//              0.0165666745904057 1.1351846329151130 -237546.7287194099626504
// NOTOPO, JPL: 0.0162686363775788 3.2561579239047944 647092.7725445065880194 647096.1254414529539645 / 

  static final double SHUKRA_MIN_LAT_ACCEL = -0.0170;
// NOTOPO, SWI: -0.0168485103248528 0.9333608749641940 2927894.9631067705340683
// NOTOPO, JPL: -0.0166690055753542 3.1949847660659429 2750381.4620271674357355 2750384.3717524991370738 / 

  static final double SHUKRA_MAX_TOPO_LAT_SPEED = 0.29;
// TOPO, SWI:   0.2831649021588232
// TOPO, JPL:   0.2807925180336351 4.8540417206386053 1867489.0201850079465657 /  155.1    5.2 -228627.5

  static final double SHUKRA_MIN_TOPO_LAT_SPEED = -0.27;
// TOPO, SWI:   -0.2679560347521213
// TOPO, JPL:   -0.2575136019376459 11.7339008245196439 2662801.1060125296935439 /   98.9   35.9 -67809.4

  static final double SHUKRA_MAX_TOPO_LAT_ACCEL = 0.13;
// TOPO, SWI:   0.1378583409260294 46.1909521927493358 107038.4291893199551851 ???
// TOPO, JPL:   0.1097291552617890 6.1041719535868992 2541331.0570884267799556 2541331.1783968498930335 /   11.0   52.2 -110000.0

  static final double SHUKRA_MIN_TOPO_LAT_ACCEL = -0.113;
// TOPO, JPL:   -0.1096757005292673 4.5047168297550897 2798261.1054993667639792 2798261.2056133821606636 /   11.0   52.2 -110000.0


  static final double SHUKRA_MAX_DIST_SPEED = 0.00806;
// NOTOPO, SWI: 0.0080571513631017
// NOTOPO, JPL: 0.0080535495091922 45.7633608410767465 2709578.0732003846205771 / 

  static final double SHUKRA_MIN_DIST_SPEED = -0.0083;
// NOTOPO, SWI: -0.0082727691197702
// NOTOPO, JPL: -0.0082468779032532 45.1463900807501659 702493.7312306788517162 / 

  static final double SHUKRA_MAX_DIST_ACCEL = 0.000316;
// NOTOPO, SWI: 0.0003155080110194 0.2863655720027083 2456084.3681465862318873
// NOTOPO, JPL: 0.0003148617982150 3.3090977992377191 2459003.4718500413000584 2459006.3288830956444144 / 

  static final double SHUKRA_MIN_DIST_ACCEL = -0.0000625;
// NOTOPO, JPL: -0.0000621028061790 3.3456973295425030 628716.0880901443306357 628716.5565334432758391 / 

  static final double SHUKRA_MAX_TOPO_DIST_SPEED = 0.0084;
// TOPO, SWI:   0.0082875554200721
// TOPO, JPL:   0.0082556409498835 45.6687320289578338 1993688.0229605080094188 /   58.8   10.4 -195796.3

  static final double SHUKRA_MIN_TOPO_DIST_SPEED = -0.0086;
// TOPO, SWI:   -0.0085208451408857
// TOPO, JPL:   -0.0084076102734088 45.1293851548699507 885846.4561085053719580 /   23.5  -20.4 -1514448.1

  static final double SHUKRA_MAX_TOPO_DIST_ACCEL = 0.0015;
// TOPO, SWI:   0.0016133029911976 37.5183635742056012 1578004.9928161685820669
//              0.0015287997796808 34.0913638843335960 173686.5690354531398043
//              0.0013268597184979 0.4098277826846652 77186.5576645124237984
//              0.0013197716218647 2.7821530204166303 68429.5604983622906730
//              0.0013188910532682 2.6655471745887542 -117845.1594293188245501
//              0.0013187923505480 4.4488933416622478 -203677.0819703953166027
// TOPO, JPL:   0.0013027897288363 352.7189377535082713 996869.4578490457497537 996869.5620547852013260 /   11.0   52.2 -110000.0

  static final double SHUKRA_MIN_TOPO_DIST_ACCEL = -0.00108;
// TOPO, SWI:   -0.0010817151373617
// TOPO, JPL:   -0.0010497207173777 10.8307320066870307 959247.0450016697868705 959247.1561946503352374 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// MANGALA: ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double MANGALA_MAX_LON_SPEED = 0.794;
// NOTOPO, SWI: 0.7924011034802300
//              0.7923999077785300 11.1435914022693989 3637945.7966404478065670
// NOTOPO, JPL: 0.7916988325596889 6.9167119757790374 2801868.8330281274393201 / 

  static final double MANGALA_MIN_LON_SPEED = -0.404;
// NOTOPO, SWI: -0.4035876030446059 181.1075756175283971 -133381.5106629475776572
// NOTOPO, JPL: -0.4030886924891714 178.7517637163851987 765886.5689680018695071 / 

  static final double MANGALA_MAX_LON_ACCEL = 0.0146;
// NOTOPO, SWI: 0.0144201733055495 138.9408217217996935 3674267.4152489653788507 3674267.7395811337046325
// NOTOPO, JPL: 0.0143482099752648 140.2555642042337070 2775001.6652576737105846 2775002.1903921556659043 / 

  static final double MANGALA_MIN_LON_ACCEL = -0.0152;
// NOTOPO, SWI: -0.0151951611243569 139.3789158418101692 2995705.9497467507608235 2995706.3421629047952592
// NOTOPO, JPL: -0.0151820773782104 139.4438975414638549 2701669.7693393989466131 2701670.3199220290407538 / 

  static final double MANGALA_MAX_TOPO_LON_SPEED = 0.81;
// TOPO, SWI:   0.7978851329427278 10.6081894629459370 3637948.2466299664229155 / -73.1792811466 0.3048884826 -2277.6546379790
// TOPO, JPL:   0.7960217543847412 8.4886529063329021 2375236.6571041969582438 /  -55.8  -29.9 -148873.0

  static final double MANGALA_MIN_TOPO_LON_SPEED = -0.425;
// TOPO, SWI:   -0.4251354442483760 178.7310683058734924 910171.7125146181788296 164.9964835365 -3.6069752029 -1770.8396081481
// TOPO, JPL:   -0.4217527221879939 177.4278470210484215 1624600.6255326191894710 / -172.9    6.1 -11919880.7

  static final double MANGALA_MAX_TOPO_LON_ACCEL = 0.16;
// TOPO, SWI:   0.1594830996296811
// TOPO, JPL:   0.1581185147318458 164.8598694285395254 1587935.7841508002020419 1587935.8985285095404834 /   11.0   52.2 -110000.0

  static final double MANGALA_MIN_TOPO_LON_ACCEL = -0.159;
// TOPO, SWI:   -0.1600745968788799
// TOPO, JPL:   -0.1575627566541741 195.6087128096599201 1011543.7506678751669824 1011543.8712390246801078 /   11.0   52.2 -110000.0


  static final double MANGALA_MAX_LAT_SPEED = 0.084;
// NOTOPO, SWI: 0.0838791710904641
// NOTOPO, JPL: 0.0826741249250192 149.2256722049210964 2775802.2559480732306838 / 

  static final double MANGALA_MIN_LAT_SPEED = -0.0839;
// NOTOPO, SWI: -0.0839678022834778
// NOTOPO, JPL: -0.0835340883879389 212.7210446501029537 642620.7923325931187719 / 

  static final double MANGALA_MAX_LAT_ACCEL = 0.0035;
// TOPO, SWI:   0.0150844307385327
// NOTOPO, JPL: 0.0034312254609595 179.0833441786972458 1864807.3567141420207918 1864807.9489276432432234 / 

  static final double MANGALA_MIN_LAT_ACCEL = -0.00209;
// NOTOPO, JL:  -0.0020809749032211 181.0128907777466907 641061.4951947031076998 641062.4194994299905375 / 

  static final double MANGALA_MAX_TOPO_LAT_SPEED = 0.095;
// TOPO, SWI:   0.0946720976575474
// TOPO, JPL:   0.0934035953265691 141.4811497310967923 2452914.9284494239836931 / -167.4   -2.0 -138827.1

  static final double MANGALA_MIN_TOPO_LAT_SPEED = -0.099;
// TOPO, SWI:   -0.0987979399086970
// TOPO, JPL:   -0.0983824901801166 215.6813984319298356 671476.8615393596701324 /  148.2   -4.8 -163423.1

  static final double MANGALA_MAX_TOPO_LAT_ACCEL = 0.0805;
// TOPO, SWI:   0.0815886852824428
// TOPO, JPL:   0.0790804782253402 180.4389968986509984 1968536.3902757749892771 1968536.4967305311001837 /   11.0   52.2 -110000.0

  static final double MANGALA_MIN_TOPO_LAT_ACCEL = -0.074;
// TOPO, SWI:   -0.0743551255625984 182.0980439241689623 1467039.2266495153307915
//              -0.0740879859432727 176.2125902326463347 1247878.3800865092780441
//              -0.0737518990689433 173.0047672387801754 1011562.5490168932592496
//              -0.0737143543505757 180.5839243595314088 965544.5294687077403069
// TOPO, JPL:   -0.0724526307717358 175.4123787295656030 1069278.4057305362075567 1069278.5368578748311847 /   11.0   52.2 -110000.0


  static final double MANGALA_MAX_DIST_SPEED = 0.0101;
// NOTOPO, SWI: 0.0100696780219029
// NOTOPO, JPL: 0.0100643683479973 269.1737141447110844 1168446.4021066790446639 / 

  static final double MANGALA_MIN_DIST_SPEED = -0.01028;
// NOTOPO, SWI: -0.0102784274030799
// NOTOPO, JPL: -0.0102749478825399 89.3012469145790249 2768620.8741885176859796 / 

  static final double MANGALA_MAX_DIST_ACCEL = 0.000234;
// NOTOPO, JPL: 0.0002316456520849 179.5008629993195086 627831.8675719088641927 627832.2966464573983103 / 

  static final double MANGALA_MIN_DIST_ACCEL = -0.0000695;
// NOTOPO, JPL: -0.0000691512115045 11.0863619757200809 2717708.7934102085418999 2717709.6335888337343931 / 

  static final double MANGALA_MAX_TOPO_DIST_SPEED = 0.0103;
// TOPO, SWI:   0.0102954964561265
// TOPO, JPL:   0.0102750877650412 268.7006393613465889 1006998.9210776721592993 /   73.4  -18.6 -558591.5

  static final double MANGALA_MIN_TOPO_DIST_SPEED = -0.0105;
// TOPO, SWI:   -0.0104995980338152
// TOPO, JPL:   -0.0104858185492511 87.8865272461379448 1857654.5719206309877336 /   -6.4  -17.5 -118807.5

  static final double MANGALA_MAX_TOPO_DIST_ACCEL = 0.00123;
// TOPO, SWI:   0.0131818153230207 284.9959789257900411 2332152.0145044443197548 ???
//              0.0012887713662012 66.6736237261732754 -199097.2332733191724401
//              0.0012319051338675 186.5730858460568982 -219956.4541361356095877
//              0.0012209446543432 191.8766370799966694 -237121.3987246403994504
// TOPO, JPL:   0.0012161423072440 177.4947770238408111 2709471.4839383927173913 2709471.5907205441035330 /   11.0   52.2 -110000.0

  static final double MANGALA_MIN_TOPO_DIST_ACCEL = -0.0011;
// TOPO, SWI:   -0.0011484037922081
// TOPO, JPL:   -0.0010620822452651 6.5326082583946175 2458748.4108516005799174 2458748.5186086511239409 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// GURU: //////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double GURU_MAX_LON_SPEED = 0.244;
// NOTOPO, SWI: 0.2440421007508337
//              0.2440259758282694 2.4045041461050332 3673053.9168258565478027
// NOTOPO, JPL: 0.2428335951258063 3.5461868267948500 2789118.9510614373721182 / 

  static final double GURU_MIN_LON_SPEED = -0.1369;
// NOTOPO, SWI: -0.1368829493523590
//              -0.1368820455359945 180.5832645904395974 -126113.4864928905444685
// NOTOPO, JPL: -0.1368635903173349 180.2193463784294352 692797.0914358530426398 / 

  static final double GURU_MAX_LON_ACCEL = 0.00354;
// NOTOPO, SWI: 0.0035312724306166 118.2817491102233305 943749.3540715674171224 943749.9294602031586692
// NOTOPO, JPL: 0.0035309507851668 118.5755994645865883 1502585.2467730506323278 1502585.3801854311022907 / 

  static final double GURU_MIN_LON_ACCEL = -0.00341;
// NOTOPO, SWI: -0.0034097053893441 119.9554019791229251 1983930.3803575735073537 1983931.0591147949453443
// NOTOPO, JPL: -0.0034095472991935 120.0438930832596185 1983930.2414474771358073 1983931.1471178678330034 / 

  static final double GURU_MAX_TOPO_LON_SPEED = 0.247;
// TOPO, SWI:   0.2460278822288060 2.1064740906293338 3322029.5618714890442789 / 96.3836141934 -9.9928840953 1721.5766618645
// TOPO, JPL:   0.2442154523229398 3.3737604511064614 2754425.1945888693444431 /  122.8   25.8 -897076.0

  static final double GURU_MIN_TOPO_LON_SPEED = -0.1405;
// TOPO, SWI:   -0.1403693939861765 179.9408142246988405 25463.4141226465580985 -91.5628546501 10.5366468238 -9284.9218899931
// TOPO, JPL:   -0.1396304331965231 180.0090680002970203 2248445.7305190265178680 /  -84.7    6.2 -317954.6

  static final double GURU_MAX_TOPO_LON_ACCEL = 0.0167;
// TOPO, SWI:   0.0166640244576276 233.6485834274197657 3647377.9916880093514919 3647378.0923984586261213 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.0166038808349592 139.5821134077839929 627013.4600588139146566 627013.5647328060586005 /   11.0   52.2 -110000.0

  static final double GURU_MIN_TOPO_LON_ACCEL = -0.0167;
// TOPO, SWI:   -0.2986882995560264
//              -0.0167151480688178 133.2242486904405609 3595432.6970099876634777 3595432.8167069852352142 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   -0.0164094314567562 143.2682748867993894 2768159.3273530141450465 2768159.4401315618306398 /   11.0   52.2 -110000.0


  static final double GURU_MAX_LAT_SPEED = 0.0063;
// NOTOPO, SWI: 0.0079894240821371 0.0601560551081519 78316.4185760746622691
//              0.0066476398233534 110.1376464895660945 -221383.3737826585129369
//              0.0066473160629214 110.6666841914722283 -221383.9030312743270770
//              0.0066457814709434 111.5006250978595972 -221384.7351701248844620
// NOTOPO, JPL: 0.0061908432229009 112.4269545307679437 649380.0055553948041052 / 

  static final double GURU_MIN_LAT_SPEED = -0.0062;
// NOTOPO, SWI: -0.0082475742115319 0.0446656653053310 2372287.4256182522512972
//              -0.0081679544726280 0.0566917774685862 1999729.6744490496348590
//              -0.0078159575125308 0.0314825326859705 1284929.4563959073275328
//              -0.0071024444607525 0.0636199730191720 1038020.3583645855542272
//              -0.0066793857937898 0.0852753287601900 -205301.8630813817726448
//              -0.0066175032562629 246.6068273788177123 -221909.5236041555763222
//              -0.0066170859380376 247.3438921056017250 -221910.2942210272594821
//              -0.0066158312691210 248.0264418159172806 -221911.0099419929028954
//              -0.0066137971865199 248.7655093363266019 -221911.7872256411646958
// NOTOPO, JPL: -0.0061251895775237 248.6116143133203593 644464.3267154396744445 / 

  static final double GURU_MAX_LAT_ACCEL = 0.000164;
// NOTOPO, JPL: 0.0001631012173349 172.8998017132045675 688014.7479439852759242 688015.2237591258017346 / 

  static final double GURU_MIN_LAT_ACCEL = -0.000144;
// NOTOPO, JPL: -0.0001428277204568 180.4011236746345617 660078.9308878154261038 660079.5882852224167436 / 

  static final double GURU_MAX_TOPO_LAT_SPEED = 0.0074;
// TOPO, SWI:   0.0080521928636294 0.0566192805632610 918765.6333176256157458
//              0.0074940630316397 114.0953024122408124 -247314.5005948057223577
//              0.0074740423582702 117.1124623989528573 -247317.4677769242262002
//              0.0073365824436177 124.3399166578952872 -247324.4473215647449251
// TOPO, JPL:   0.0072166314932738 109.0304781092945063 701237.9247726293979213 /  -42.0    9.3 -11969401.6

  static final double GURU_MIN_TOPO_LAT_SPEED = -0.0074;
// TOPO, SWI:   -0.0080876975949050 0.0340482972944187 877668.4739827105076984
//              -0.0079257941735561 0.0548291341236222 353535.4688255244982429
//              -0.0074868031086838 244.7003594482700066 -195980.6305575540463906
//              -0.0074791206548048 239.8774896996048938 -226290.6132767098606564
//              -0.0074452255799583 252.2918423249091120 -226303.5950242244580295
//              -0.0074393971860641 253.1862647845265144 -226304.5564436963759363
// TOPO, JPL:   -0.0072441693905558 249.4712924749894398 648851.6447883903747424 /  178.5    4.1 -175983.9

  static final double GURU_MAX_TOPO_LAT_ACCEL = 0.0064;
// TOPO, SWI:   0.0064370403082856 160.9893348765497763 -243369.5462068344349973
// TOPO, JPL:   0.0062836812390572 173.4311152344720313 804886.0647488388931379 804886.1658009625971317 /   11.0   52.2 -110000.0

  static final double GURU_MIN_TOPO_LAT_ACCEL = -0.00601;
// TOPO, SWI:   -0.1037547091237699 0.0125813165357158 505110.7392954186070710
//              -0.0378655962663130 0.1083443186038267 -138278.1849216190457810
//              -0.0360539293255533 0.1266684501299267 -240004.5811667661473621
//              -0.0061777644919788 168.4064816875281565 -243376.0363520069804508
//              -0.0060227498357402 173.1109147544705138 -245394.4910242093901616
//              -0.0058877265072794 206.9527394385040679 -247797.8501246784289833
//              -0.0058695732378666 222.0752014979657929 -247811.8569919402361847
// TOPO, JPL:   -0.0060022250346072 176.9020558316282461 960847.4024146875599399 960847.5054992398945615 /   11.0   52.2 -110000.0


  static final double GURU_MAX_DIST_SPEED = 0.0163;
// NOTOPO, SWI: 0.0162313440373101
// NOTOPO, JPL: 0.0162125062673481 269.9885757091312826 2746737.7407946651801467 / 

  static final double GURU_MIN_DIST_SPEED = -0.0164;
// NOTOPO, SWI: -0.0163119973755655
// NOTOPO, JPL: -0.0162962764487217 89.9528066433123001 2813565.0229882504791021 / 

  static final double GURU_MAX_DIST_ACCEL = 0.000325; // ???
// NOTOPO, SWI: 0.0028131695540130 252.0199342955070279 3389970.8170315762981772
//              0.0022264700183521 78.0262633819467624 2959356.3896456728689373
//              0.0021058202921303 56.3220986050345331 689082.5046896804124117
//              0.0014148556073934 312.1296276860581997 138881.9097125186235644
//              0.0003629278483912 175.2126664382685988 103648.3659716135007329
//              0.0003215421089300 179.8650599366537790 -130102.0682320412161062
//              0.0003215231118657 180.5493323734873172 -130102.6579265795007814
//              0.0003214350810835 180.8083546044778416 -130102.8811461286386475
// NOTOPO, JPL: 0.0003212791490395 179.1139733917144667 749437.0917171647306532 749437.5507154314545915 / 

  static final double GURU_MIN_DIST_ACCEL = -0.000225;
// NOTOPO, SWI: -0.0026706423885953 54.9988325446786490 2244330.1662299656309187
//              -0.0011107400422637 240.5622614243510213 1324971.5582129028625786
//              -0.0010463882202215 46.6627522885998758 -45687.3579329831190989
//              -0.0007706904056781 37.7983780909854090 -80098.6155146874953061
//              -0.0002399030274792 1.4728896000519569 -102383.2875191967032151
//              -0.0002340892813117 37.6730895540927406 -214825.2211170222435612
//              -0.0002202596930024 0.6216123376105145 -236411.7266815737239085
// NOTOPO, JPL: -0.0002200487479365 3.0727983914445929 820633.5672682302538306 820635.0164746360387653 / 

  static final double GURU_MAX_TOPO_DIST_SPEED = 0.0165;
// TOPO, SWI:   0.0164463865294178
// TOPO, JPL:   0.0163933358236029 269.7074039544052084 2798591.6030700867995620 /  -32.5  -27.2 -252818.2

  static final double GURU_MIN_TOPO_DIST_SPEED = -0.0166;
// TOPO, SWI:   -0.0165316055601204
// TOPO, JPL:   -0.0164783521489150 90.7489162720576417 2354449.6322963917627931 /  -52.4   -5.9 -559160.2

  static final double GURU_MAX_TOPO_DIST_ACCEL = 0.00133; // ???
// TOPO, SWI:   0.0207137582627566 173.7974512158892821 3371308.6819511819630861
//              0.0095682916573331 98.6288452876610506 2352478.9396295030601323
//              0.0017434341399401 3.7222567351049491 1672652.3170623441692442
//              0.0013784181224652 262.1285954707602173 385732.5318781036767177
//              0.0013364439220739 175.2229360370665177 220911.8912446679605637
//              0.0013343946830275 188.1894899974843725 -207878.5227978868060745
// TOPO, JPL:   0.0013140810305337 183.5053747524114840 1177835.8770452847238630 1177835.9799993557389826 /   11.0   52.2 -110000.0

  static final double GURU_MIN_TOPO_DIST_ACCEL = -0.00122;
// TOPO, SWI:   -0.0344816332659920 53.5806245234392833 -59632.7241606430397951
//              -0.0012291085316648 11.8839758867454179 -110381.6067325769108720
//              -0.0012237729790972 6.0617096993218524 -218870.4614361103449482
//              -0.0012218538662633 17.5817593377373669 -231605.5168245942331851
//              -0.0012137029216628 18.3605833587742779 -248803.3674780579749495
//              -0.0012040965556822 20.9413774401508306 -249151.5155578464036807
// TOPO, JPL:   -0.0012179154714779 8.0924125333128529 1401019.6981153180822730 1401019.8004333155695349 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// SHANI: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double SHANI_MAX_LON_SPEED = 0.13404;
// NOTOPO, SWI: 0.1340290794104446
//              0.1340109648692031 2.1226092234817315 -163206.2606100863777101
// NOTOPO, JPL: 0.1327805183874537 3.0429635221465219 644396.1415654856245965 / 

  static final double SHANI_MIN_LON_SPEED = -0.084;
// NOTOPO, SWI: -0.0843874858704462
//              -0.0843863197679707 180.0316197536598963 -119529.3726371085940627
// NOTOPO, JPL: -0.0839205001027148 179.7538351952555615 634005.4940949525916949 / 

  static final double SHANI_MAX_LON_ACCEL = 0.00199;
// NOTOPO, SWI: 0.0019860635653615 108.7583081509525584 -238564.3131895941623952 -238563.9070349260582589
// NOTOPO, JPL: 0.0019704643133465 107.7955867333490403 773209.0539155729347840 773209.4716971723828465 / 

  static final double SHANI_MIN_LON_ACCEL = -0.00195;
// NOTOPO, SWI: -0.0019461144643805 250.6294677312978081 -162317.6448787293047644 -162317.4296173398906831
// NOTOPO, JPL: -0.0019411597276414 107.9127693051768802 881969.6117141979048029 881969.9048477461328730 / 

  static final double SHANI_MAX_TOPO_LON_SPEED = 0.134;
// TOPO, SWI:   0.1352241917838550 2.6437337111698298 -120105.1467321325326338 / -135.7469844874 -1.5235966413 -9149.1494438681
// TOPO, JPL:   0.1335384540876906 356.4228793916646509 859531.2081036102026701 /   11.0   52.2 -110000.0

  static final double SHANI_MIN_TOPO_LON_SPEED = -0.0855;
// TOPO, SWI:   -0.0857898683346270
//              -0.0857859482664416 180.4511195292874675 -205733.7354889601992909 57.1340338590 -6.0390360608 2936.3009470154
// TOPO, JPL:   -0.0849641804905260 180.3456957179522249 956896.5353130036965013 / -131.2  -32.9 -486057.8

  static final double SHANI_MAX_TOPO_LON_ACCEL = 0.0086;
// TOPO, SWI:   0.0088537626365099 (Problems up to 0.5749467950953074 observed...)
//              0.0086874752939179 130.3835869430225216 -228378.3960538489336614 -228378.2941615929012187 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.0085688265882089 235.0153479090199369 2399366.5584685457870364 2399366.6685493290424347 /   11.0   52.2 -110000.0

  static final double SHANI_MIN_TOPO_LON_ACCEL = -0.00864;
// TOPO, SWI:   -0.0086350102928645 240.9329383039785171 -217516.6292963425803464 -217516.5270764300366864 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   -0.0086289532791422 120.5645061952920400 1850273.4720761652570218 1850273.5765058014076203 /   11.0   52.2 -110000.0


  static final double SHANI_MAX_LAT_SPEED = 0.0055;
// NOTOPO, SWI: 0.0088659593013574 0.0357855361262125 1216463.8803999235387892
//              0.0081800060242820 0.0636244767979335 -9686.2452881572171464
//              0.0055101641996105 103.1816729109932567 -248384.6794485895079561
//              0.0055092577650856 103.9915755785259250 -248385.4973858597222716
//              0.0055085351164070 104.2998464057047556 -248385.8082728039298672
//              0.0055061985781587 104.9731251070553526 -248386.4864187667553779
// NOTOPO, JPL: 0.0054479762180281 101.5087719957672618 633699.7182570304721594 / 

  static final double SHANI_MIN_LAT_SPEED = -0.0054;
// NOTOPO, SWI: -0.0093448258414916 0.0480108040459299 2405172.1235335594974458
//              -0.0091669774974187 0.0437628531739165 1017947.0968454070389271
//              -0.0082309467247685 0.0735537640593975 -46761.3712922931881621
//              -0.0059085247284603 0.0737067784886278 -186653.2304984880902339
//              -0.0054935401134247 256.1125920323701166 -227737.3836737677629571
//              -0.0054924209794990 256.8583947276435424 -227738.1576579157263041
//              -0.0054893589421577 257.5657401830940216 -227738.8932042158266995
//              -0.0054866869674104 257.9952243323996868 -227739.3405110478342976
//              -0.0054846770087000 258.2735656374735527 -227739.6306852521665860
// NOTOPO, JPL: -0.0052759566378740 255.1213087279579099 632794.2191756315296516 / 

  static final double SHANI_MAX_LAT_ACCEL = 0.000123;
// NOTOPO, JPL: 0.0001228331245459 182.3412334534269803 730035.7467638771049678 730036.3191362058278173 / 

  static final double SHANI_MIN_LAT_ACCEL = -0.000104;
// NOTOPO, JPL: -0.0001036095956202 181.2808113227449383 2541859.8319580527022481 2541860.2949275760911405 / 

  static final double SHANI_MAX_TOPO_LAT_SPEED = 0.006;
// TOPO, JPL:   0.0059829280424917 254.7257709873013880 1203859.3423178326338530 /  120.7    8.1 -268428.1

  static final double SHANI_MIN_TOPO_LAT_SPEED = -0.0059;
// TOPO, SWI:   -0.0092855309799147
// TOPO, JPL:   -0.0057956247321725 253.1921945803524636 632796.2324350061826408 / -170.8  -37.9 -11959137.5

  static final double SHANI_MAX_TOPO_LAT_ACCEL = 0.0032;
// TOPO, SWI:   0.1291812952134777 0.0193139412162679 652331.3204417930683121
//              0.1103411440697414 48.3201736850982115 337716.2472735045594163 ???
//              0.0406681081950653 0.1330846070859195 140796.5907405762118287
//              0.0248291370689390 0.1817467500329428 -25209.8627429512453091
//              0.0192489692315691 0.3364410508429501 -208203.9115095509332605
//              0.0154213763216727 0.0787234050482084 -229755.7019914422999136
//              0.0106410980662519 0.0625985933783042 -246369.5177199522149749
//              0.0033936272027591 196.6164942212680273 -249231.4910817339550704
//              0.0033477521784859 184.6683619648643742 -249600.4166902992583346
//              0.0032692627409362 158.9229430463847450 -249956.4608254602062516
//              0.0032065821914758 188.1027910938801142 -249983.3395146315742750
// TOPO, JPL:   0.0031905232971144 163.4044039250840399 665019.1348916188580915 665019.2585872148629278 /   11.0   52.2 -110000.0

  static final double SHANI_MIN_TOPO_LAT_ACCEL = -0.00301;
// TOPO, SWI:   -0.1943805609996636 0.0254947304431425 3093302.9852960119023919
//              -0.1535901747923263 0.0044428449655527 399030.9463442072155885
//              -0.0967942061450560 0.0375247351963210 33416.6073811288442812
//              -0.0244403526037960 0.1095505791075055 -63374.4509422093688045
//              -0.0222436811141467 0.0710847034876565 -208204.2186171786452178
//              -0.0105777641070009 0.4226321633859698 -229755.1313364572997671
//              -0.0076972631775804 0.8873846478899594 -246368.5475377420661971
//              -0.0031570907055125 133.9147902428757675 -249174.1276894060720224
//              -0.0031355393451151 160.1263956863335522 -249577.9961592703766655
//              -0.0031248092521445 146.5321463786839047 -249945.0155428809521254
//              -0.0030320551971996 215.1038187030328004 -250008.7760764319973532
// TOPO, JPL:   -0.0029976150401222 175.5931959224585910 643456.6917577013373375 643456.7943882376421243 /   11.0   52.2 -110000.0


  static final double SHANI_MAX_DIST_SPEED = 0.0168;
// NOTOPO, SWI: 0.0167980101137831
// NOTOPO, JPL: 0.0167470550342229 270.1922148977790812 775126.5858114481670782 / 

  static final double SHANI_MIN_DIST_SPEED = -0.0169;
// NOTOPO, SWI: -0.0168541607915517
// NOTOPO, JPL: -0.0168033574476849 90.2359820156960382 887610.1793931820429862 / 

  static final double SHANI_MAX_DIST_ACCEL = 0.000322;
// NOTOPO, JPL: 0.0003217576664089 180.6124093899778131 924759.4654094257857651 924760.1071097153471783 / 

  static final double SHANI_MIN_DIST_ACCEL = -0.00027;
// NOTOPO, JPL: -0.0002649687841804 3.1537517381507030 940823.5889109726995230 940828.3583899141522124 / 

  static final double SHANI_MAX_TOPO_DIST_SPEED = 0.017;
// TOPO, SWI:   0.0170041976808882
// TOPO, JPL:   0.0168998454699344 272.9172717291329491 784956.1169768150430173 /   83.1   20.2 -559218.8

  static final double SHANI_MIN_TOPO_DIST_SPEED = -0.01702;
// TOPO, SWI:   -0.0170839780153652
// TOPO, JPL:   -0.0170159989383753 90.6219157816285019 929959.2261485378257930 /  -34.6   16.0 -275006.2

  static final double SHANI_MAX_TOPO_DIST_ACCEL = 0.00133;
// TOPO, JPL:   0.0013147485636233 187.2595322712119810 773528.1895404278766364 773528.3051549824886024 /   11.0   52.2 -110000.0

  static final double SHANI_MIN_TOPO_DIST_ACCEL = -0.00127;
// TOPO, JPL:   -0.0012570696593106 5.8823054507581105 1155577.9214635647367686 1155578.0259339711628854 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// URANUS: ////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double URANUS_MAX_LON_SPEED = 0.067;
// NOTOPO, SWI: 0.0672787417212825 2.2044883144310603 -198847.3157617567048874
// NOTOPO, JPL: 0.0635219827469326 3.0865801839384801 1120454.5702892334666103 / 

  static final double URANUS_MIN_LON_SPEED = -0.044;
// NOTOPO, SWI: -0.0439753677256215
//              -0.0439749353957933 179.1371858363940532 2805890.5954912416636944
// NOTOPO, JPL: -0.0439737691898081 179.7845571918695100 2805891.2095267488621175 / 

  static final double URANUS_MAX_LON_ACCEL = 0.000929;
// NOTOPO, SWI: 0.0009287961354995 96.5197421388259897 3663572.4564131293445826 3663573.4440292418003082
// NOTOPO, JPL: 0.0009282638132935 97.8842174477335902 2529097.6063587637618184 2529098.3723707860335708 / 

  static final double URANUS_MIN_LON_ACCEL = -0.000975;
// NOTOPO, SWI: -0.0009749151187470 99.5153225338921601 74425.5413598613231443 74426.1704895895527443
// NOTOPO, JPL: -0.0009743654979471 101.0515095115334816 2501959.5002480042167008 2501960.0762184909544885 / 

  static final double URANUS_MAX_TOPO_LON_SPEED = 0.067;
// TOPO, SWI:   0.0678142990549242
//              0.0659933143859741 2.6624668048619782 -229528.4862648595299106 / 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.0639666873114370 3.7716406512730885 1181818.1529458123259246 /  -36.6  -13.9 -1936737.7

  static final double URANUS_MIN_TOPO_LON_SPEED = -0.045;
// TOPO, SWI:   -0.0446931660419553 181.3720736420628157 995316.5729154800064862 -148.1483065444 23.8884500241 -8408.4451827214
// TOPO, JPL:   -0.0445080603496004 177.0986226535454193 2558587.0008950615301728 / -146.4    7.0 -302922.1

  static final double URANUS_MAX_TOPO_LON_ACCEL = 0.00408;
// TOPO, SWI:   0.0040746860036361 250.1776503311332363 995014.1325893194880337 995014.2447810187004507 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.0040768658697398 258.9330392610905847 688949.3911929061869159 688949.4914812404895201 /   11.0   52.2 -110000.0

  static final double URANUS_MIN_TOPO_LON_ACCEL = -0.00414;
// TOPO, SWI:   -0.0041389274636002 108.3810328551985265 -229420.5110600728949066 -229420.4092381796508562 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   -0.0041032458702222 111.1018605931466396 1149396.9018011584412307 1149397.0059658905956894 /   11.0   52.2 -110000.0


  static final double URANUS_MAX_LAT_SPEED = 0.00082;
// NOTOPO, JPL: 0.0008167139359186 101.7249222392620567 630392.2795268078334630 / 

  static final double URANUS_MIN_LAT_SPEED = -0.00079;
// NOTOPO, JPL: -0.0007872844416086 94.5979876900013466 632406.4064464361872524 / 

  static final double URANUS_MAX_LAT_ACCEL = 0.000073;
// NOTOPO, JPL: 0.0000725796777657 3.0532664846880948 2473394.5464777238667011 2473397.1248878035694361 / 

  static final double URANUS_MIN_LAT_ACCEL = -0.000078;
// NOTOPO, JPL: -0.0000770026994627 3.0639491173097326 2603143.1314133340492845 2603145.6577144251205027 / 

  static final double URANUS_MAX_TOPO_LAT_SPEED = 0.0011;
// TOPO, JPL:   0.0010938727239962 86.5558131871995897 967133.4299628505250439 / -119.3  -19.8 -139450.6

  static final double URANUS_MIN_TOPO_LAT_SPEED = -0.0011;
// TOPO, JPL:   -0.0010622759299074 96.7133449758128023 2255928.0951205361634493 /  111.7   -5.9 -34208.6

  static final double URANUS_MAX_TOPO_LAT_ACCEL = 0.0018;
// TOPO, JPL:   0.0017903892764677 149.1340385089452809 2389696.5752421738579869 2389696.6956723732873797 /  -32.4  -63.4 -1536076.6

  static final double URANUS_MIN_TOPO_LAT_ACCEL = -0.0014;
// TOPO, JPL:   -0.0013941980551326 203.0822592660931605 813099.1251212942879647 813099.2264039237052202 /   11.0   52.2 -110000.0


  static final double URANUS_MAX_DIST_SPEED = 0.0174;
// NOTOPO, SWI: 0.0173920870984149
// NOTOPO, JPL: 0.0173585770210396 90.0711143387830475 703751.9874883674783632 / 

  static final double URANUS_MIN_DIST_SPEED = -0.0174;
// NOTOPO, SWI: -0.0173768671135560
// NOTOPO, JPL: -0.0173396482859767 90.1712916619785716 625941.9552030125632882 / 

  static final double URANUS_MAX_DIST_ACCEL = 0.00032;
// NOTOPO, SWI: 0.0124048986737664 20.5447553654863668 697587.8714966527186334
//              0.0005308301000862 194.0508279385882133 89653.0467164388392121
//              0.0004054523102576 231.6225436385040837 -237907.2030542625288945
//              0.0003186821836528 178.6186060500050701 -238595.9672204693779349
//              0.0003184016477014 178.6329745040906971 -238595.9806884480349254
//              0.0003183838157893 179.5486894765174952 -238596.8390383592632134
// NOTOPO, JPL: 0.0003182930717874 180.4026778810304563 898832.5701296664774418 898833.3199369597714394 / 

  static final double URANUS_MIN_DIST_ACCEL = -0.00029;
// NOTOPO, SWI: -0.0070443778023334 56.5696520951671431 1915268.8080441316124052
//              -0.0024268224701898 222.2131202933724694 1622588.7918466471601278
//              -0.0017978805103116 112.0036669852078717 1034069.5693735613022000
//              -0.0017711586866005 299.6443816880482132 316745.1417552463826723
//              -0.0009565967124733 34.2481328372568896 -97158.9052647117496235
//              -0.0002965673693959 3.1728844936351379 -126771.9740064809302567
//              -0.0002953008853553 16.8151157502424269 -220272.7407736064051278
//              -0.0002870073111414 2.3807250107649054 -223242.7754700276127551
//              -0.0002869421133855 1.4524556291287922 -223243.7389360640954692
//              -0.0002868014122837 1.1682960365388055 -223244.0338751582312398
//              -0.0002867361242725 3.7817533448034055 -223980.3368454804876819
//              -0.0002865876339975 2.4346127875054151 -223981.7355972216755617
// NOTOPO, JPL: -0.0002869171845722 3.2030561927724648 760035.8014352507889271 760040.9284781528403983 / 

  static final double URANUS_MAX_TOPO_DIST_SPEED = 0.0177;
// TOPO, SWI:   0.0176107889477238
// TOPO, JPL:   0.0175545304775976 92.9313722310350556 1440106.4267600025050342 /  168.4   10.7 -96661.6

  static final double URANUS_MIN_TOPO_DIST_SPEED = -0.0176;
// TOPO, SWI:   -0.0175993482429765
// TOPO, JPL:   -0.0175275406010507 89.7400205375481050 964177.0357202672166750 /   40.8   16.0 -877247.0

  static final double URANUS_MAX_TOPO_DIST_ACCEL = 0.0014;
// TOPO, SWI:   0.0189433989824949 174.5079152323185099 657074.3099226328777149
//              0.0183572432261317 224.5189864726799556 63373.2618242262324202
//              0.0013319739502499 182.7145275240009425 -174644.4776922487071715
//              0.0013240294434584 168.9088235694555067 -174657.4400828294456005
//              0.0013224702513523 167.7986954449400230 -175744.5029267666395754
//              0.0013215195981625 183.3128958273755984 -205701.3786467498575803
// TOPO, JPL:   0.0013896236227717 149.1340385089452809 2389696.5752421738579869 2389696.6956723732873797 /  -32.4  -63.4 -1536076.6

  static final double URANUS_MIN_TOPO_DIST_ACCEL = -0.0013;
// TOPO, SWI:   -0.0074983375520822 237.2702478815059806 2190097.4634127663448453
//              -0.0036695677976724 95.1997580518125233 318393.4452091892017052
//              -0.0015032462816689 29.4131290187358729 -153420.1933402899012435
//              -0.0012987197092415 8.8866741555319493 -159656.5122229879780207
//              -0.0012951371415311 5.1144519034210418 -161148.4825953901745379
//              -0.0012921027532530 10.9717383066954142 -222148.3436549269245006
//              -0.0012787761964680 6.4137141371829784 -223621.4039706184703391
// TOPO, JPL:   -0.0012774206853281 3.0570588538432162 1278670.8627330390736461 1278670.9655325952917337 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// NEPTUNE: ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double NEPTUNE_MAX_LON_SPEED = 0.040;
// NOTOPO, SWI: 0.0435201332499242
//              0.0396972276200028 2.0827344696075443 2575780.0351443360559642
// NOTOPO, JPL: 0.0380456461242105 3.1532708267034764 2514772.0896095521748066 / 

  static final double NEPTUNE_MIN_LON_SPEED = -0.0286;
// NOTOPO, SWI: -0.0285951074753113 179.8185040704009623 -114773.5779159839439671
// NOTOPO, JPL: -0.0285175042960974 180.4726995259502189 789980.0941408453509212 / 

  static final double NEPTUNE_MAX_LON_ACCEL = 0.000612;
// NOTOPO, SWI: 0.0006117892571967 95.0693851270330867 1141011.6886422061361372 1141011.9912163538392633
// NOTOPO, JPL: 0.0006117853683019 94.6052391940140183 659604.8761492961784825 659605.1621642899699509 / 

  static final double NEPTUNE_MIN_LON_ACCEL = -0.000606;
// NOTOPO, SWI: -0.0006059351401622 95.1618440250847897 196771.1343181393167470 196771.7955272420076653
// NOTOPO, JPL: -0.0006003368094838 98.0787203190911896 917049.5164440356893465 917049.9205574790248647 / 

  static final double NEPTUNE_MAX_TOPO_LON_SPEED = 0.043; // ???
// TOPO, SWI:   0.0422970690081757 2.2898781842913536 2453407.6579371863044798 / 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.0383563887565795 3.0390931791095852 826906.0045657418668270 /  135.2    5.9 -547814.0

  static final double NEPTUNE_MIN_TOPO_LON_SPEED = -0.0295;
// TOPO, SWI:   -0.0290291625574319 180.5744992676552840 426167.0060721903573722
//              -0.0290038106992851 178.5349119796019011 188768.7348955178749748 112.9643788335 -12.4859860679 4158.5890999517
// TOPO, JPL:   -0.0288775621192141 177.7015335690367692 1088009.2940130003262311 /  -44.5    2.8 -84124.2

  static final double NEPTUNE_MAX_TOPO_LON_ACCEL = 0.00253;
// TOPO, SWI:   0.0025292120034634 (SY problem up to 0.2261463302930795 observed)
//              0.0042628483667480 122.0246905452054591 3521196.7267022081650794 3493290.0288146338425577 -133.4574916488 45.5312312476 -3817.1061038599
//              0.0025187380665741 260.4326793894730372 3493289.9254662604071200 55089.2956650374981109 11.0000000000 52.2400000000 -110000.0000000000
//              0.0025184397438746 93.8376591582873800 55089.1874535102106165 -125715.4170647663704585 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   0.0025131255216818 95.6708957219361480 892959.2571875500725582 892959.3594027562066913 /   11.0   52.2 -110000.0

  static final double NEPTUNE_MIN_TOPO_LON_ACCEL = -0.00252;
// TOPO, SWI:   -0.0025161885478298 (SY problem down to -0.3324043321743462 observed)
//              -0.0025049885835764 97.7823583964682399 2414925.5690259789116681 2414925.6727004703134298 11.0000000000 52.2400000000 -110000.0000000000
// TOPO, JPL:   -0.0025029416562368 100.6905068192704391 1577057.7301410164218396 1577057.8535416696686298 /   11.0   52.2 -110000.0


  static final double NEPTUNE_MAX_LAT_SPEED = 0.0013;
// NOTOPO, JPL: 0.0012992698973563 94.8672612292906479 666219.2212012477684766 / 

  static final double NEPTUNE_MIN_LAT_SPEED = -0.0013;
// NOTOPO, JPL: -0.0012783972113913 268.1060882042605158 663109.7084374728146940 / 

  static final double NEPTUNE_MAX_LAT_ACCEL = 0.000069;
// NOTOPO, JPL: 0.0000688858521642 3.0106510603266088 2269663.3859712700359523 2269665.7029609824530780 / 

  static final double NEPTUNE_MIN_LAT_ACCEL = -0.000065;
// NOTOPO, JPL: -0.0000645053184318 3.1813985225708450 2059463.7634919909760356 2059466.4900546479038894 / 

  static final double NEPTUNE_MAX_TOPO_LAT_SPEED = 0.00156;
// TOPO, SWI:   0.0090000517239580 0.0524548520168651 1972364.6034868198912591
//              0.0089956406518853 0.0582866623987570 1491690.9895345694385469
//              0.0089730249324625 0.0600284356005858 1341026.7986733743455261
//              0.0080895274797669 0.0723554736281216 -161993.2757087929057889
//              0.0060016807506635 0.0288506759888136 -221526.1047314178140368
//              0.0035285398313926 0.0632601727365483 -221893.4920628319960088
//              0.0015526225326993 98.6415321471926347 -235230.4668798064230941
//              0.0015499860150981 88.2389236872085405 -235587.4842110734607559
//              0.0015467816098779 91.2033835105225421 -236325.4807206140249036
//              0.0015466858680102 92.1773040803388710 -236326.4611918719019741
// TOPO, JPL:   0.0014551000399715 95.4889062662710444 785651.9289954347768798 /   93.5   11.4 -889863.7

  static final double NEPTUNE_MIN_TOPO_LAT_SPEED = -0.00143;
// TOPO, JPL:   -0.0014127851155073 96.1163954950283568 755520.3564604987623170 /  133.6   16.3 -11928444.7

  static final double NEPTUNE_MAX_TOPO_LAT_ACCEL = 0.00087;
// TOPO, JPL:   0.0008698934809757 151.6828949764235404 659917.2505363596137613 659917.3554017660208046 /   11.0   52.2 -110000.0

  static final double NEPTUNE_MIN_TOPO_LAT_ACCEL = -0.000854;
// TOPO, JPL:   -0.0008534031453892 170.1151339811219145 807974.2306540678255260 807974.3415011959150434 /   11.0   52.2 -110000.0


  static final double NEPTUNE_MAX_DIST_SPEED = 0.0175;
// NOTOPO, SWI: 0.0174167945328918
// NOTOPO, JPL: 0.0174065088712856 90.0141956230224594 716570.6086513907648623 / 

  static final double NEPTUNE_MIN_DIST_SPEED = -0.0175;
// NOTOPO, SWI: -0.0174181120306995
// NOTOPO, JPL: -0.0174082572247792 90.1133447416499962 864489.7592409640783444 / 

  static final double NEPTUNE_MAX_DIST_ACCEL = 0.000316;
// NOTOPO, SWI: 0.0135819670381752 48.2733891582497563 5525.0933320791637016
//              0.0029873866850574 69.0549169938272769 -192795.6600356508279219
//              0.0004085467817404 165.5440851032710725 -235295.4374565259495284
//              0.0003252402971657 163.9956620539999506 -235661.4083584317122586
//              0.0003154919293185 177.7271030118340036 -235674.4588167968322523
//              0.0003154876427962 179.4769426327440272 -236411.0038810773694422
//              0.0003154687802688 180.1591877981425398 -236411.6524607480387203
// NOTOPO, JPL: 0.0003155423489853 181.7260481566145245 730447.6796687617897987 730448.2887378348968923 / 

  static final double NEPTUNE_MIN_DIST_ACCEL = -0.0003;
// NOTOPO, SWI: -0.0926625037231506 173.2327775500569942 708404.3574317961465567
//              -0.0051669956985789 111.0832620716790444 -227659.2020879369229078
//              -0.0003041835232219 7.0299840806517295 -234383.0605990467884112
//              -0.0002863472168228 2.3091629537422023 -250198.7555277845531236
//              -0.0002862723385372 3.1424049270648595 -250199.6271795822540298
//              -0.0002862542929178 3.4005205436072288 -250199.8970782387768850
//              -0.0002861746751199 4.0429712268202422 -250200.5686231650470290
// NOTOPO, JPL: -0.0002952785868367 3.2715414145909278 639491.6505929509876296 639495.8581704762764275 / 

  static final double NEPTUNE_MAX_TOPO_DIST_SPEED = 0.0177;
// TOPO, SWI:   0.0176337286177421
// TOPO, JPL:   0.0175995232752972 88.9667806227172662 778309.2456948457984254 /   42.3    6.9 -489807.4

  static final double NEPTUNE_MIN_TOPO_DIST_SPEED = -0.0177;
// TOPO, SWI:   -0.0176367861495518
// TOPO, JPL:   -0.0176267292572482 87.5072744454238318 1890876.3556474931538105 /  148.4    4.0 -106588.7

  static final double NEPTUNE_MAX_TOPO_DIST_ACCEL = 0.00135;
// TOPO, SWI:   0.6461977268067690 25.3701797814048433 1939634.5921939297113568
//              0.0041164964926524 206.4912262049541027 480897.9479083669139072
//              0.0037451465792928 136.6200955115339752 441925.1899133427650668
//              0.0014718970994787 93.5523667367900771 44993.4780828971197479
//              0.0013515382920695 211.2758559578730058 -162885.4319869252503850
//              0.0013232817060093 189.9636687918127222 -231644.2943363734520972
//              0.0013096974841549 189.6233126453089426 -232746.2574978842749260
// TOPO, JPL:   0.0013092533141867 189.5421929498741918 1087653.0362668419256806 1087653.1421054021921009 /   11.0   52.2 -110000.0

  static final double NEPTUNE_MIN_TOPO_DIST_ACCEL = -0.00129;
// TOPO, SWI:   -0.1132083825141817 142.5236047281373715 1681538.3904155539348722
//              -0.0792670482834736 154.3516542613503475 821240.9412590151187032
//              -0.0035286930591954 8.2707438707448304 434060.6570531763718463
//              -0.0020499282815566 327.4975658185502994 368987.5497646926087327
//              -0.0018412778091957 66.0445618612218794 -210436.4600420471979305
//              -0.0012986500971886 2.5730292615316017 -217119.1991163263737690
//              -0.0012882855151135 0.7261626876730247 -232184.3240317180461716
//              -0.0012688484178225 14.7256797835751740 -232201.2589514517167117
//              -0.0012600964431873 13.3128784618060649 -232567.3151242690801155
// TOPO, JPL:   -0.0012849455246221 5.7304803388833534 997431.1147520339582115 997431.2203086471417919 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// PLUTO: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double PLUTO_MAX_LON_SPEED = 0.041;
// NOTOPO, SWI: 0.0409947189148399
// NOTOPO, JPL: 0.0408338960827420 3.1371236364667254 732954.4751370932208374 / 

  static final double PLUTO_MIN_LON_SPEED = -0.0284;
// NOTOPO, SWI: -0.0283698947779465
// NOTOPO, JPL: -0.0283272575709131 179.1674723407538181 1632754.8288194262422621 / 

  static final double PLUTO_MAX_LON_ACCEL = 0.000601;
// NOTOPO, JPL: 0.0005990791437332 97.3066735347333349 822701.2720944840693846 822702.0496401527198032 / 

  static final double PLUTO_MIN_LON_ACCEL = -0.00065; // ????
// NOTOPO, SWI: -0.0007143739642233 (SY problem down to -0.1089928395917400 observed)
// NOTOPO, JPL: -0.0006327247387071 94.8617743602447092 1632669.1280191596597433 1632669.6611433816142380 / 

  static final double PLUTO_MAX_TOPO_LON_SPEED = 0.0413;
// TOPO, SWI:   0.0412884373492034
// TOPO, JPL:   0.0411060581436741 3.1111305873843946 643830.5274781107436866 /   11.0   52.2 -110000.0

  static final double PLUTO_MIN_TOPO_LON_SPEED = -0.0288;
// TOPO, SWI:   -0.0286905230717167
// TOPO, JPL:   -0.0286678286669824 176.7873496422886319 2718300.2481787498109043 /  -23.0   -6.5 -11682194.0

  static final double PLUTO_MAX_TOPO_LON_ACCEL = 0.00253;
// TOPO, JPL:   0.0025178590195568 104.4522615939965249 2809692.6709229107946157 2809692.8023088881745934 /   11.0   52.2 -110000.0

  static final double PLUTO_MIN_TOPO_LON_ACCEL = -0.0026; // ????
// TOPO, SWI:   -0.0372004141085517 (SY problem down to -0.4008096521768991 observed)
// TOPO, JPL:   -0.0025588306695524 99.9199502376179680 2720792.5705627049319446 2720792.6829605293460190 /   11.0   52.2 -110000.0


  static final double PLUTO_MAX_LAT_SPEED = 0.0101;
// NOTOPO, SWI: 0.0099918860824316
// NOTOPO, JPL: 0.0099791426368035 94.4854202402405292 639883.3305302684893832 / 

  static final double PLUTO_MIN_LAT_SPEED = -0.00998;
// NOTOPO, SWI: -0.0099656330945712
// NOTOPO, JPL: -0.0099307674698353 93.1252446492206047 732853.5206388467922807 / 

  static final double PLUTO_MAX_LAT_ACCEL = 0.000159;
// NOTOPO, JPL: 0.0001577503032733 5.4037598154724265 641999.4772995261009783 642000.1200399320805445 / 

  static final double PLUTO_MIN_LAT_ACCEL = -0.000188;
// NOTOPO, JPL: -0.0001858085104703 177.6618672571143804 1000479.4034898622194305 1000480.3014068943448365 / 

  static final double PLUTO_MAX_TOPO_LAT_SPEED = 0.0102;
// TOPO, SWI:   0.0101887620952181
// TOPO, JPL:   0.0101587910721185 93.3203292923736569 729735.3805221099173650 /   11.0   52.2 -110000.0

  static final double PLUTO_MIN_TOPO_LAT_SPEED = -0.011;
// TOPO, SWI:   -0.0109564169565936
// TOPO, JPL:   -0.0101079839297460 92.3334029317985596 733222.3447666474385187 /   11.0   52.2 -110000.0

  static final double PLUTO_MAX_TOPO_LAT_ACCEL = 0.0013;
// TOPO, SWI:   0.1146408060915522 0.0754882456864365 713120.5580753037938848
//              0.0941206511202854 49.4010660634788081 456088.8688647721428424
//              0.0255255252969479 0.1806868458375845 116473.0549504257651279
//              0.0253178489595343 0.2471611952441393 -4971.5581980942633891
//              0.0121348936844913 0.1709038475643752 -62866.7451490048188134
//              0.0095585608319143 0.0073487506721790 -62866.9096804023429286
//              0.0045413279302522 0.5515540013628879 -152719.6797022641985677
//              0.0040083267072832 0.5350913166472111 -184677.7962954391550738
//              0.0023768429757906 0.2974980519578878 -242574.1132811341085471
//              0.0011173870417524 56.2797968672246895 -250713.2092755420599133
//              0.0010944966591291 64.0983224175128328 -250721.2586086901428644
//              0.0009852994797639 129.8867192572130307 -250891.6895662871829700
// TOPO, JPL:   0.0012931870141345 12.5130488940067437 640168.3879828155040741 640168.4985908751841635 /   11.0   52.2 -110000.0

  static final double PLUTO_MIN_TOPO_LAT_ACCEL = -0.0014;
// TOPO, SWI:   -0.1323288495285307 0.0571969808381283 1883765.3306468296796083
//              -0.0743827345599630 0.1194495083565243 713120.6057449358049780
//              -0.0347908909623849 0.1204501519061694 -152720.3629573613288812
//              -0.0088254384157201 0.1686240986828693 -152720.4120519654243253
//              -0.0033765420206368 0.1258497784927819 -184678.5095836789405439
//              -0.0017693116405143 0.4127381851168934 -242573.3918600576289464
//              -0.0012369578816304 188.1500182824425451 -250464.4232595347566530
//              -0.0011875762707375 171.6292642547027185 -250481.3294816084962804
//              -0.0011793623663488 171.5160188829716787 -250849.2928314248274546
//              -0.0011041529654153 131.2960704322420895 -250890.2601939455198590
// TOPO, JPL:   -0.0013933132467849 197.2859828842642855 641088.3418429015437141 641088.4793755571590737 /   11.0   52.2 -110000.0


  static final double PLUTO_MAX_DIST_SPEED = 0.01805;
// NOTOPO, SWI: 0.0180201864709247
// NOTOPO, JPL: 0.0179673538522285 89.7263334479476384 747190.6329123422037810 / 

  static final double PLUTO_MIN_DIST_SPEED = -0.01805;
// NOTOPO, SWI: -0.0180372446419509
// NOTOPO, JPL: -0.0180007004030953 90.0245545393799773 626294.2538914401084185 / 

  static final double PLUTO_MAX_DIST_ACCEL = 0.000315;
// NOTOPO, SWI: 0.0407853605615538 83.8263720967590871 3624026.5748034995049238
//              0.0069468201512656 196.2677638094518784 1815013.3748994071502239
//              0.0048377509693545 47.2315784566411594 -143964.7589193212625105
//              0.0006369295143076 196.0402002069912157 -234332.2497064112685621
//              0.0003005188008673 176.0412684223048814 -241282.3776702757168096
//              0.0002952326969226 176.8597734140736861 -242751.7179506011016201
//              0.0002935577048952 177.7294647668737753 -243854.2899573702889029
//              0.0002935450072931 177.8110411867463938 -243854.3732949701952748
// NOTOPO, JPL: 0.0003136979868652 179.6743020621852907 1341170.5058408293407410 1341171.2995680202730000 / 

  static final double PLUTO_MIN_DIST_ACCEL = -0.000296;
// NOTOPO, SWI: -0.0152198272306129 237.8828584819669913 690902.4017224303679541
//              -0.0096311018334681 1.4497561645164865 307184.0997986468719319
//              -0.0026457749260651 331.2636077184097871 73186.7185329681087751
//              -0.0019568281177972 40.5484034124101242 -136261.9220611743803602
//              -0.0006521190722939 30.9038516469837532 -223109.5922839867707808
//              -0.0003360489734281 17.5393798737692634 -245493.2611005441867746
//              -0.0002946613707863 2.5375739709260188 -245508.5060571530484594
//              -0.0002946504435585 1.7204731079822864 -245509.3345663794898428
//              -0.0002946048677604 1.5721621402714163 -245509.4849347961135209
//              -0.0002944878278406 1.0907226602467972 -245509.9730266212136485
// NOTOPO, JPL: -0.0002959509564196 3.2369661234292266 1645067.6523841423913836 1645072.8514915483538061 / 

  static final double PLUTO_MAX_TOPO_DIST_SPEED = 0.0183;
// TOPO, SWI:   0.0182120589472379
// TOPO, JPL:   0.0181415307873287 89.7000340036246655 1108803.2886870261281729 /  -58.1    8.6 -108903.9

  static final double PLUTO_MIN_TOPO_DIST_SPEED = -0.0183;
// TOPO, SWI:   -0.0182358280933419
// TOPO, JPL:   -0.0181426062874089 88.6500692581212064 625559.5932220611721277 /   11.0   52.2 -110000.0

  static final double PLUTO_MAX_TOPO_DIST_ACCEL = 0.00135;
// TOPO, SWI:   0.0130128963192127 17.1368763588973749 1241262.9272272512316704
//              0.0023151043479321 111.4547636320583877 52165.7623227584044798
//              0.0017030775099116 178.4354706217193325 -175329.3589742311451118
//              0.0016646685105212 238.7184089560146276 -227044.6033776346594095
//              0.0014068279408472 55.1948022699443754 -240060.5752828968106769
//              0.0012963542904493 191.5096459801829099 -241665.2621815337915905
//              0.0012948705921776 169.6181203906081123 -244213.2961760257894639
// TOPO, JPL:   0.0013027104602608 180.1840886067463998 793263.3965677092783153 793263.5139706216286868 /   11.0   52.2 -110000.0

  static final double PLUTO_MIN_TOPO_DIST_ACCEL = -0.0013;
// TOPO, SWI:   -0.0179021842533406 214.6365146216283222 1198529.3581569930538535
//              -0.0032151436390044 3.9705683390313311 -20364.6199331984498713
//              -0.0028143398711598 252.9002190420136458 -35995.5853714871991542
//              -0.0013100334546386 2.1773699952934749 -154186.4357591727748513
//              -0.0013036739026851 3.6193122178814860 -156028.3848112957202829
//              -0.0013024841249698 13.3133296954911486 -242927.3159189936995972
//              -0.0012885072393509 17.9576457466834825 -243693.2539283887890633
//              -0.0012856628157103 13.0601184579426786 -244763.3268022972624749
//              -0.0012845931611976 2.0770303236098471 -246978.2600213123077992
//              -0.0012587293344394 12.3076703741073743 -247335.2735762960510328
//              -0.0012440334269146 19.5256853756665123 -248430.3081937007373199
//              -0.0012362014952662 13.8258049766299678 -249199.2258719038800336
//              -0.0012140398754897 15.1842583574879768 -249568.1970763951831032
// TOPO, JPL:   -0.0012825097800167 4.0535447294597020 830888.3153882571496069 830888.4365303290542215 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// MNODE: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double MNODE_MAX_LON_SPEED = -0.0528;
// NOTOPO, JPL: -0.0528841758228763 87.4506108213789730 2800705.2652022368274629 / 

  static final double MNODE_MIN_LON_SPEED = -0.0531;
// NOTOPO, SWI: -0.0530037505698459
// NOTOPO, JPL: -0.0530021964831903 124.0050853633128298 644588.8616734195966274 / 

  static final double MNODE_MAX_LON_ACCEL = 0.0000249;
// NOTOPO, JPL: 0.0000248040638846 201.9963197288654442 1977761.7032133250031620 1977761.8148180888965726 / 

  static final double MNODE_MIN_LON_ACCEL = -0.0000244;
// NOTOPO, JPL: -0.0000242121990862 268.1333251052160449 2773317.7025739247910678 2773317.8079387117177248 / 

  static final double MNODE_MAX_TOPO_LON_SPEED = -0.0528;
// TOPO, JPL:   -0.0528842254910542 87.4505199997736327 2800705.2666699616238475 /   11.0   52.2 -110000.0

  static final double MNODE_MIN_TOPO_LON_SPEED = -0.0531;
// TOPO, SWI:   -0.0530037595813937
// TOPO, JPL:   -0.0530022464081250 123.9781716707397123 644588.8357337675988674 /  176.5  -37.7 -10953765.3

  static final double MNODE_MAX_TOPO_LON_ACCEL = 0.0000247;
// TOPO, JPL:   0.0000243945457281 63.5362877416933571 1291321.3731930041685700 1291321.4759153190534562 /   11.0   52.2 -110000.0

  static final double MNODE_MIN_TOPO_LON_ACCEL = -0.0000252;
// TOPO, JPL:   -0.0000251435327552 136.9860162044397498 2480991.0153021169826388 2480991.1231008274480700 /   11.0   52.2 -110000.0


  static final double MNODE_MAX_LAT_SPEED = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.7029183002887294 / 

  static final double MNODE_MIN_LAT_SPEED = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.7029183002887294 / 

  static final double MNODE_MAX_LAT_ACCEL = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.5000000000000000 625361.7029183002887294 / 

  static final double MNODE_MIN_LAT_ACCEL = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.5000000000000000 625361.7029183002887294 / 

  static final double MNODE_MAX_TOPO_LAT_SPEED = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.8994183215545490 /   11.0   52.2 -110000.0

  static final double MNODE_MIN_TOPO_LAT_SPEED = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.8994183215545490 /   11.0   52.2 -110000.0

  static final double MNODE_MAX_TOPO_LAT_ACCEL = 0.;
// TOPO, SWI:   0.0000000000000000 42.5366762476106146 625361.5000000000000000 625361.8994183215545490 /   11.0   52.2 -110000.0

  static final double MNODE_MIN_TOPO_LAT_ACCEL = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.5000000000000000 625361.8994183215545490 /   11.0   52.2 -110000.0


  static final double MNODE_MAX_DIST_SPEED = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.7029183002887294 / 

  static final double MNODE_MIN_DIST_SPEED = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.7029183002887294 / 

  static final double MNODE_MAX_DIST_ACCEL = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.5000000000000000 625361.7029183002887294 / 

  static final double MNODE_MIN_DIST_ACCEL = 0.;
// NOTOPO, JPL: 0.0000000000000000 42.7373829367315352 625361.5000000000000000 625361.7029183002887294 / 

  static final double MNODE_MAX_TOPO_DIST_SPEED = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.8994183215545490 /   11.0   52.2 -110000.0

  static final double MNODE_MIN_TOPO_DIST_SPEED = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.8994183215545490 /   11.0   52.2 -110000.0

  static final double MNODE_MAX_TOPO_DIST_ACCEL = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.5000000000000000 625361.8994183215545490 /   11.0   52.2 -110000.0

  static final double MNODE_MIN_TOPO_DIST_ACCEL = 0.;
// TOPO, JPL:   0.0000000000000000 42.5366762476106146 625361.5000000000000000 625361.8994183215545490 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// TNODE: /////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////
  static final double TNODE_MAX_LON_SPEED = 0.0328;
// NOTOPO, JPL: 0.0326855962208268 28.5243572070390314 722775.8339081415906549 / 

  static final double TNODE_MIN_LON_SPEED = -0.261;
// NOTOPO, JPL: -0.2601780541622186 87.0307941091399471 871073.2885009105084464 / 

  static final double TNODE_MAX_LON_ACCEL = 0.054;
// NOTOPO, JPL: 0.0539136658625242 96.3340571413317832 1139185.0864348253235221 1139185.4419494222383946 / 

  static final double TNODE_MIN_LON_ACCEL = -0.054;
// NOTOPO, JPL: -0.0539712493483143 92.9162103985022156 687359.6763768544187769 687359.7991852302802727 / 

  static final double TNODE_MAX_TOPO_LON_SPEED = 0.0328;
// TOPO, SWI:   0.0327411094922690 (maybe much more, e.g. 0.4216115680677051)
//              0.4216115680677051 13.8633424027789545 3585899.6871486338786781
//              0.3996447565119488 191.6855534133834738 1843240.2456777647603303
//              0.2235892399173331 148.7626066628442913 30120.7841977981188393
//              0.0327411094922690 30.7612542764135526 -146545.2495332568651065
//              0.0325982260105773 208.4145973857591798 -224358.2317474005394615
//              0.0323529232146644 30.5854516341142357 -247697.1525798988586757
// TOPO, JPL:   0.0327082146129712 28.6110973826417876 722775.7452402360504493 /   11.0   52.2 -110000.0

  static final double TNODE_MIN_TOPO_LON_SPEED = -0.261;
// TOPO, SWI:   -0.2607482296372162 maybe much less, e.g. -0.5493115235934243)
//              -0.5493115235934243 42.7298002047974705 250061.6466701690806076
//              -0.5077009885677132 241.4997864307243276 39158.6958460557289072
//              -0.2607482296372162 93.1224119348595707 -69475.6147848475084174
//              -0.2605016817789270 93.1145743769104968 -76060.9473834989621537
//              -0.2594970823080265 84.6379656825472466 -140703.5355969415395521
//              -0.2594213076977080 84.6888169562490134 -140703.5749870793370064
//              -0.2581229158578309 91.1754560203589222 -168092.5823965377057903
// TOPO, JPL:   -0.2603922043003269 87.2476101631761765 864487.7687863860046491 /   -9.6  -71.6 -451209.5

  static final double TNODE_MAX_TOPO_LON_ACCEL = 0.054;
// TOPO, SWI:   0.0563873394044947 (maybe much more, e.g. 4.7468566498283602)
//              4.7468566498283602 241.5981374622122644 39158.7957134488242446
//              0.3122228876570153 148.7626066628442913 30120.7841977981188393
//              0.0563873394044947 164.3187040187730190 -140084.0362958694167901
//              0.0537976700912474 87.6324656033127383 -154224.3777706396358553
//              0.0537427144570444 87.6659956936547076 -154224.4070253355603199
//              0.0536532049411242 94.8920470111433900 -212281.3224629964388441
// TOPO, JPL:   0.0538962795836710 86.2171194267930332 2066563.6492133960127831 2066563.8136807992123067 /   11.0   52.2 -110000.0

  static final double TNODE_MIN_TOPO_LON_ACCEL = -0.054;
// TOPO, SWI:   -0.0539831464262423 maybe much less, e.g. -5.6865435545693899)
//              -5.6865435545693899 148.7128969225232140 30120.8330104324704735
//              -0.0539831464262423 94.8056785030036338 -195131.4903258501726668
//              -0.0536432799836339 95.3676455106551941 -201717.3139034592604730
//              -0.0536428549665791 87.1500816759324408 -240018.2256629223702475
//              -0.0535586258892041 87.2176697339374840 -246603.5270144374808297
// TOPO, JPL:   -0.0539120620264503 84.8636084025309998 738831.5274397874018177 738831.8096256844000891 /   11.0   52.2 -110000.0


  static final double TNODE_MAX_LAT_SPEED = 0.;
// NOTOPO, JPL: 0.0000000000000000 41.0794032263436293 625361.7998687432846054 / 

  static final double TNODE_MIN_LAT_SPEED = 0.;
// NOTOPO, JPL: 0.0000000000000000 41.0794032263436293 625361.7998687432846054 / 

  static final double TNODE_MAX_LAT_ACCEL = 0.;
// NOTOPO, JPL: 0.0000000000000000 41.0794032263436293 625361.5000000000000000 625361.7998687432846054 / 

  static final double TNODE_MIN_LAT_ACCEL = 0.;
// NOTOPO, JPL: 0.0000000000000000 41.0794032263436293 625361.5000000000000000 625361.7998687432846054 / 

  static final double TNODE_MAX_TOPO_LAT_SPEED = 0.;
// TOPO, JPL:   0.0000000000000000 40.3789042855210027 625362.4439763929694891 /   11.0   52.2 -110000.0

  static final double TNODE_MIN_TOPO_LAT_SPEED = 0.;
// TOPO, JPL:   0.0000000000000000 40.3789042855210027 625362.4439763929694891 /   11.0   52.2 -110000.0

  static final double TNODE_MAX_TOPO_LAT_ACCEL = 0.;
// TOPO, JPL:   0.0000000000000000 40.3789042855210027 625361.5000000000000000 625362.4439763929694891 /   11.0   52.2 -110000.0

  static final double TNODE_MIN_TOPO_LAT_ACCEL = 0.;
// TOPO, JPL:   0.0000000000000000 40.3789042855210027 625361.5000000000000000 625362.4439763929694891 /   11.0   52.2 -110000.0


  static final double TNODE_MAX_DIST_SPEED = 0.0000228;
// NOTOPO, SWI: 0.0000227657371966
// NOTOPO, JPL: 0.0000213536998323 120.1149812893362423 754925.7251749879214913 / 

  static final double TNODE_MIN_DIST_SPEED = -0.0000216;
// NOTOPO, SWI: -0.0000215585382077
// NOTOPO, JPL: -0.0000213990807928 114.3803667754305309 861213.9748676187591627 / 

  static final double TNODE_MAX_DIST_ACCEL = 0.00000835;
// NOTOPO, SWI: 0.0000750703538360 33.0382069743494640 1046857.9201584004331380
//              0.0000295730661174 132.1285899593370914 751004.2467315234243870
//              0.0000229566919869 6.1158485301780985 277754.1283083962043747
//              0.0000131148016195 196.2613424548156615 -14958.5557277744010207
//              0.0000083076652793 99.7438212565191975 -15397.8175420094139554
//              0.0000083031871586 98.6244788123381824 -225448.4339384726190474
//              0.0000082342371095 98.9047249793631238 -238618.8604936520860065
// NOTOPO, JPL: 0.0000083367008151 88.5064147393362077 674539.7194071344565600 674540.0587246073409915 / 

  static final double TNODE_MIN_DIST_ACCEL = -0.0000086;
// NOTOPO, SWI: -0.0002872607354344 106.8098325992096846 655114.2552040057489648
//              -0.0000161941268880 76.5510956058315912 347687.6100133685395122
//              -0.0000088751590264 169.1897512402919688 238764.2694866273377556
//              -0.0000085451497521 181.7548695594740877 197156.1960955614340492
//              -0.0000085156406539 177.1760048851402587 61286.1568801726316451
//              -0.0000084695707129 189.9098971100671918 -78.2180907315612757
//              -0.0000084538261730 1.7385989643092614 -30746.1787060799397295
//              -0.0000084417926183 173.1584750134485660 -67999.0639539538824465
//              -0.0000084251014809 174.0109261711857016 -129363.5261933571891859
//              -0.0000084210976167 3.0071993379622199 -247382.2838002044882160
// NOTOPO, JPL: -0.0000085231218245 179.9598301820871882 1073062.1182626546360552 1073062.7212474753614515 / 

  static final double TNODE_MAX_TOPO_DIST_SPEED = 0.000022; // ????
// TOPO, SWI:   0.0000320574867052
// TOPO, JPL:   0.0000213717861342 115.0970956228481725 1284910.9963867058977485 /   11.0   52.2 -110000.0

  static final double TNODE_MIN_TOPO_DIST_SPEED = -0.000022; // ????
// TOPO, SWI:   -0.0000332648162204
// TOPO, JPL:   -0.0000214083084746 114.3533718935811976 854628.5975370522355661 /   11.0   52.2 -110000.0

  static final double TNODE_MAX_TOPO_DIST_ACCEL = 0.00000835;
// TOPO, SWI:   0.0001074598743278 271.3472021376313705 2657201.8088382319547236
//              0.0000634652596772 193.5558352806767743 1221774.3783414242789149
//              0.0000538869946502 73.4934083541807581 31.3934498392767622
//              0.0000094186598250 173.4454735818177369 -92606.7031143940985203
//              0.0000083076607859 90.8051476176185872 -194780.8783414751815144
//              0.0000083008875115 98.4001552972325442 -225448.6528204880887643
//              0.0000082571491516 98.8692616102437398 -232033.5335670034401119
//              0.0000082542040884 98.3977016616702684 -232033.9945917097211350
//              0.0000082231815334 98.3834756777576303 -238619.3730163456057198
//              0.0000082187722697 98.9192470588687058 -245204.2546059744490776
// TOPO, JPL:   0.0000083343622490 92.4754649371095070 1027046.1513708923012018 1027046.5722648203372955 /   11.0   52.2 -110000.0

  static final double TNODE_MIN_TOPO_DIST_ACCEL = -0.00000856;
// TOPO, SWI:   -0.0001068121184233 79.6861647504438793 1764819.9418648579157889
//              -0.0000784099630606 109.2705757601478638 323742.6587682426907122
//              -0.0000433144853973 58.9190511727564825 87048.6964661899837665
//              -0.0000110202297946 32.7308601455830797 72582.4401404987002024
//              -0.0000085451174671 177.4027674611190264 61285.9358465782570420
//              -0.0000085190648985 177.2361821151802133 54700.7609249259112403
//              -0.0000084900566429 177.5682195794680638 48115.0708514538491727
//              -0.0000084618897872 1.6125954998670124 -30746.0549506582174217
//              -0.0000084498993674 173.1389537705036332 -67999.0444112504046643
//              -0.0000084444766972 2.9733688488841779 -240796.9001949433586560
// TOPO, JPL:   -0.0000085540381454 179.8284028667385996 1073062.4194052561651915 1073062.5930666599888355 /   11.0   52.2 -110000.0



///////////////////////////////////////////////////////////////
// SURYA S max notopo:  1.0243864993
// SURYA S min notopo:  0.9486638198
// SURYA S max topo3:   1.0388159322
// SURYA S min topo3:   0.9347718616
// CHANDRA max notopo:  15.4042491124
// CHANDRA min notopo: 11.7571075192
// CHANDRA max topo3:   21.5666962324
// CHANDRA min topo3:  6.1294391379
// CHANDRA max notopo:  0.5186888
// CHANDRA min notopo:  -0.5188292
// CHANDRA max topo3:   38.1641267
// CHANDRA min topo3:   -38.1716335
// BUDHA max notopo:  2.2121102506 //??? 2.2537837349
// BUDHA min notopo:  -1.3908278667
// BUDHA max topo3:   2.2229697247
// BUDHA min topo3:   -1.4125035236
// BUDHA max notopo:  0.1968066, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// BUDHA min notopo: -0.1986822, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// BUDHA max topo3: 8.4292471 // SY-Problem? Realistic: 0.3244554
// BUDHA min topo3: -34.4871693 // SY-Problem? Realistic: -0.3282049
// SHUKRA max notopo: 1.2644483889
// SHUKRA max topo3:  1.2733039990
// SHUKRA min notopo: -0.6437175784
// SHUKRA min topo3:  -0.6963885604 // Sy-Problem???
// SHUKRA max notopo:  0.0423762, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// SHUKRA min notopo: -0.0430388, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// SHUKRA max topo3: 1.1127520 // SY-Problem? Realistic: 0.3878079
// SHUKRA min topo3: -2.9288472 // SY-Problem? Realistic: -0.3884931
// MANGALA max notopo: 0.7924011226
// MANGALA max topo3:  0.7985878049
// MANGALA min notopo: -0.4035104555
// MANGALA min topo3:  -0.4261278855
// MANGALA max notopo:  0.0144224, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// MANGALA min notopo: -0.0152010, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// MANGALA max topo3:   5.3859140 // SY-Problem? Realistic: 0.2652052
// MANGALA min topo3:   -0.7430761 // SY-Problem? Realistic: -0.2653766
// SHANI max notopo: 0.1338471290 // Sy-Problem 0.1342611535?
// SHANI max topo3:  0.1353554794
// SHANI min notopo: -0.0842836027
// SHANI min topo3:  -0.0860881714
// SHANI max notopo:  0.0020007, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// SHANI min notopo: -0.0019460, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// SHANI max topo3: 47.8021777 // SY-problem? Realistic: 0.0135465
// SHANI min topo3: -9.9611508 // SY-problem? Realistic: -0.0134697
// GURU S max notopo: 0.2440423861
// GURU S min notopo: -0.1368372415
// GURU S max topo3:  0.2465672865
// GURU S min topo3:  -0.1403921701
// GURU A max notopo:  0.0036155, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// GURU A min notopo: -0.0034178, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// GURU A max topo3: 3.6034504 // SY-problem? Realistic: 0.0266245
// GURU A min topo3: -16.4451885 // SY-problem? Realistic: -0.0267212
// URANUS A max notopo:  0.0009501, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// URANUS A min notopo: -0.0009897, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// URANUS A max topo3: 8.4594472 // SY-problem? Realistic: 0.0063635
// URANUS A min topo3: -44.1816134 // SY-problem? Realistic: -0.0064026
// URANUS S max notopo: 0.0637152951 // Sy-Problem beyond?
// URANUS S max topo3:  0.0681905640
// URANUS S min notopo: -0.0439752018
// URANUS S min topo3:  -0.0447987374
// NEPTUNE A max notopo:  0.0006291, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// NEPTUNE A min notopo: -0.0006255, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// NEPTUNE A max topo3: 5.9498810 // SY-problem? Realistic: 0.0039052
// NEPTUNE A min topo3: -12.7924566 // SY-problem? Realistic: -0.0038898
// NEPTUNE S max notopo: 0.0381288654 // Sy-Problem? 0.0432424224
// NEPTUNE S max topo3:  0.0437376690
// NEPTUNE S min notopo: -0.0285824820
// NEPTUNE S min topo3:  -0.0290549670
// PLUTO A max notopo:  0.0006226, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// PLUTO A min notopo: -0.0006607, everything beyond seems to be unexact calculation
//                         due to too much nearness to the sun!
// PLUTO A max topo3: 1.2429646 // SY-problem? Realistic: 0.0040215
// PLUTO A min topo3: -1.7390824 // SY-problem? Realistic: -0.0040603
// PLUTO S max notopo: 0.0409935333
// PLUTO S max topo3:  0.0414652881
// PLUTO S min notopo: -0.0283697947
// PLUTO S min topo3:  -0.0289179185
// MNODE A max notopo:  0.0000508
// MNODE A min notopo: -0.0000508
// MNODE A max topo3:  0.0000508
// MNODE A min topo3: -0.0000508
// MNODE S max notopo: -0.0528817186
// MNODE S max topo3:  -0.0528817186
// MNODE S min notopo: -0.0530035225
// MNODE S min topo3:  -0.0530035225
// TNODE A max notopo: 573.1032358  // Errors in calculations???
// TNODE A min notopo: -444.7164997 // Errors in calculations???
// TNODE A max topo3: 695.8627388 // Errors in calculations???
// TNODE A min topo3: -444.6272115 // Errors in calculations???
// TNODE S max notopo: 0.4448616947
// TNODE S max topo3:  0.4448616947
// TNODE S min notopo: -0.5805968771
// TNODE S min topo3:  -0.5805968771
///////////////////////////////////////////////////////////////



  // Speeds and accelerations in longitudinal direction:
  // Maximum geocentric speeds in longitudinal direction:
  static final double[] maxLonSpeed = new double[]
      {SURYA_MAX_LON_SPEED,  CHANDRA_MAX_LON_SPEED, BUDHA_MAX_LON_SPEED,
       SHUKRA_MAX_LON_SPEED, MANGALA_MAX_LON_SPEED, GURU_MAX_LON_SPEED,
       SHANI_MAX_LON_SPEED,  URANUS_MAX_LON_SPEED,  NEPTUNE_MAX_LON_SPEED,
       PLUTO_MAX_LON_SPEED,  MNODE_MAX_LON_SPEED,   TNODE_MAX_LON_SPEED};
  // Minimum geocentric speeds in longitudinal direction:
  static final double[] minLonSpeed = new double[]
      {SURYA_MIN_LON_SPEED,  CHANDRA_MIN_LON_SPEED, BUDHA_MIN_LON_SPEED,
       SHUKRA_MIN_LON_SPEED, MANGALA_MIN_LON_SPEED, GURU_MIN_LON_SPEED,
       SHANI_MIN_LON_SPEED,  URANUS_MIN_LON_SPEED,  NEPTUNE_MIN_LON_SPEED,
       PLUTO_MIN_LON_SPEED,  MNODE_MIN_LON_SPEED,   TNODE_MIN_LON_SPEED};
  // Maximum topocentric speeds in longitudinal direction, up to 50000m in height:
  static final double[] maxTopoLonSpeed = new double[]
      {SURYA_MAX_TOPO_LON_SPEED,   CHANDRA_MAX_TOPO_LON_SPEED,
       BUDHA_MAX_TOPO_LON_SPEED,   SHUKRA_MAX_TOPO_LON_SPEED,
       MANGALA_MAX_TOPO_LON_SPEED, GURU_MAX_TOPO_LON_SPEED,
       SHANI_MAX_TOPO_LON_SPEED,   URANUS_MAX_TOPO_LON_SPEED,
       NEPTUNE_MAX_TOPO_LON_SPEED, PLUTO_MAX_TOPO_LON_SPEED,
       MNODE_MAX_TOPO_LON_SPEED,   TNODE_MAX_TOPO_LON_SPEED};
  // Minimum topocentric speeds in longitudinal direction, up to 50000m in height:
  static final double[] minTopoLonSpeed = new double[]
      {SURYA_MIN_TOPO_LON_SPEED,   CHANDRA_MIN_TOPO_LON_SPEED,
       BUDHA_MIN_TOPO_LON_SPEED,   SHUKRA_MIN_TOPO_LON_SPEED,
       MANGALA_MIN_TOPO_LON_SPEED, GURU_MIN_TOPO_LON_SPEED,
       SHANI_MIN_TOPO_LON_SPEED,   URANUS_MIN_TOPO_LON_SPEED,
       NEPTUNE_MIN_TOPO_LON_SPEED, PLUTO_MIN_TOPO_LON_SPEED,
       MNODE_MIN_TOPO_LON_SPEED,   TNODE_MIN_TOPO_LON_SPEED};
  // Maximum geocentric accelerations in longitudinal direction:
  static final double[] maxLonAccel = new double[]
      {SURYA_MAX_LON_ACCEL,  CHANDRA_MAX_LON_ACCEL, BUDHA_MAX_LON_ACCEL,
       SHUKRA_MAX_LON_ACCEL, MANGALA_MAX_LON_ACCEL, GURU_MAX_LON_ACCEL,
       SHANI_MAX_LON_ACCEL,  URANUS_MAX_LON_ACCEL,  NEPTUNE_MAX_LON_ACCEL,
       PLUTO_MAX_LON_ACCEL,  MNODE_MAX_LON_ACCEL,   TNODE_MAX_LON_ACCEL};
  // Minimum geocentric accelerations in longitudinal direction:
  static final double[] minLonAccel = new double[]
      {SURYA_MIN_LON_ACCEL,  CHANDRA_MIN_LON_ACCEL, BUDHA_MIN_LON_ACCEL,
       SHUKRA_MIN_LON_ACCEL, MANGALA_MIN_LON_ACCEL, GURU_MIN_LON_ACCEL,
       SHANI_MIN_LON_ACCEL,  URANUS_MIN_LON_ACCEL,  NEPTUNE_MIN_LON_ACCEL,
       PLUTO_MIN_LON_ACCEL,  MNODE_MIN_LON_ACCEL,   TNODE_MIN_LON_ACCEL};
  // Maximum topocentric accelerations in longitudinal direction, up to 50000m in height:
  static final double[] maxTopoLonAccel = new double[]
      {SURYA_MAX_TOPO_LON_ACCEL,   CHANDRA_MAX_TOPO_LON_ACCEL,
       BUDHA_MAX_TOPO_LON_ACCEL,   SHUKRA_MAX_TOPO_LON_ACCEL,
       MANGALA_MAX_TOPO_LON_ACCEL, GURU_MAX_TOPO_LON_ACCEL,
       SHANI_MAX_TOPO_LON_ACCEL,   URANUS_MAX_TOPO_LON_ACCEL,
       NEPTUNE_MAX_TOPO_LON_ACCEL, PLUTO_MAX_TOPO_LON_ACCEL,
       MNODE_MAX_TOPO_LON_ACCEL,   TNODE_MAX_TOPO_LON_ACCEL};
  // Minimum topocentric accelerations in longitudinal direction, up to 50000m in height:
  static final double[] minTopoLonAccel = new double[]
      {SURYA_MIN_TOPO_LON_ACCEL,   CHANDRA_MIN_TOPO_LON_ACCEL,
       BUDHA_MIN_TOPO_LON_ACCEL,   SHUKRA_MIN_TOPO_LON_ACCEL,
       MANGALA_MIN_TOPO_LON_ACCEL, GURU_MIN_TOPO_LON_ACCEL,
       SHANI_MIN_TOPO_LON_ACCEL,   URANUS_MIN_TOPO_LON_ACCEL,
       NEPTUNE_MIN_TOPO_LON_ACCEL, PLUTO_MIN_TOPO_LON_ACCEL,
       MNODE_MIN_TOPO_LON_ACCEL,   TNODE_MIN_TOPO_LON_ACCEL};


  // Speeds and accelerations in latitudinal direction:
  // Maximum geocentric speeds in latitudinal direction:
  static final double[] maxLatSpeed = new double[]
      {SURYA_MAX_LAT_SPEED,  CHANDRA_MAX_LAT_SPEED, BUDHA_MAX_LAT_SPEED,
       SHUKRA_MAX_LAT_SPEED, MANGALA_MAX_LAT_SPEED, GURU_MAX_LAT_SPEED,
       SHANI_MAX_LAT_SPEED,  URANUS_MAX_LAT_SPEED,  NEPTUNE_MAX_LAT_SPEED,
       PLUTO_MAX_LAT_SPEED,  MNODE_MAX_LAT_SPEED,   TNODE_MAX_LAT_SPEED};
  // Minimum geocentric speeds in latitudinal direction:
  static final double[] minLatSpeed = new double[]
      {SURYA_MIN_LAT_SPEED,  CHANDRA_MIN_LAT_SPEED, BUDHA_MIN_LAT_SPEED,
       SHUKRA_MIN_LAT_SPEED, MANGALA_MIN_LAT_SPEED, GURU_MIN_LAT_SPEED,
       SHANI_MIN_LAT_SPEED,  URANUS_MIN_LAT_SPEED,  NEPTUNE_MIN_LAT_SPEED,
       PLUTO_MIN_LAT_SPEED,  MNODE_MIN_LAT_SPEED,   TNODE_MIN_LAT_SPEED};
  // Maximum topocentric speeds in latitudinal direction, up to 50000m in height:
  static final double[] maxTopoLatSpeed = new double[]
      {SURYA_MAX_TOPO_LAT_SPEED,   CHANDRA_MAX_TOPO_LAT_SPEED,
       BUDHA_MAX_TOPO_LAT_SPEED,   SHUKRA_MAX_TOPO_LAT_SPEED,
       MANGALA_MAX_TOPO_LAT_SPEED, GURU_MAX_TOPO_LAT_SPEED,
       SHANI_MAX_TOPO_LAT_SPEED,   URANUS_MAX_TOPO_LAT_SPEED,
       NEPTUNE_MAX_TOPO_LAT_SPEED, PLUTO_MAX_TOPO_LAT_SPEED,
       MNODE_MAX_TOPO_LAT_SPEED,   TNODE_MAX_TOPO_LAT_SPEED};
  // Minimum topocentric speeds in latitudinal direction, up to 50000m in height:
  static final double[] minTopoLatSpeed = new double[]
      {SURYA_MIN_TOPO_LAT_SPEED,   CHANDRA_MIN_TOPO_LAT_SPEED,
       BUDHA_MIN_TOPO_LAT_SPEED,   SHUKRA_MIN_TOPO_LAT_SPEED,
       MANGALA_MIN_TOPO_LAT_SPEED, GURU_MIN_TOPO_LAT_SPEED,
       SHANI_MIN_TOPO_LAT_SPEED,   URANUS_MIN_TOPO_LAT_SPEED,
       NEPTUNE_MIN_TOPO_LAT_SPEED, PLUTO_MIN_TOPO_LAT_SPEED,
       MNODE_MIN_TOPO_LAT_SPEED,   TNODE_MIN_TOPO_LAT_SPEED};
  // Maximum geocentric accelerations in latitudinal direction:
  static final double[] maxLatAccel = new double[]
      {SURYA_MAX_LAT_ACCEL,  CHANDRA_MAX_LAT_ACCEL, BUDHA_MAX_LAT_ACCEL,
       SHUKRA_MAX_LAT_ACCEL, MANGALA_MAX_LAT_ACCEL, GURU_MAX_LAT_ACCEL,
       SHANI_MAX_LAT_ACCEL,  URANUS_MAX_LAT_ACCEL,  NEPTUNE_MAX_LAT_ACCEL,
       PLUTO_MAX_LAT_ACCEL,  MNODE_MAX_LAT_ACCEL,   TNODE_MAX_LAT_ACCEL};
  // Minimum geocentric accelerations in latitudinal direction:
  static final double[] minLatAccel = new double[]
      {SURYA_MIN_LAT_ACCEL,  CHANDRA_MIN_LAT_ACCEL, BUDHA_MIN_LAT_ACCEL,
       SHUKRA_MIN_LAT_ACCEL, MANGALA_MIN_LAT_ACCEL, GURU_MIN_LAT_ACCEL,
       SHANI_MIN_LAT_ACCEL,  URANUS_MIN_LAT_ACCEL,  NEPTUNE_MIN_LAT_ACCEL,
       PLUTO_MIN_LAT_ACCEL,  MNODE_MIN_LAT_ACCEL,   TNODE_MIN_LAT_ACCEL};
  // Maximum topocentric accelerations in latitudinal direction, up to 50000m in height:
  static final double[] maxTopoLatAccel = new double[]
      {SURYA_MAX_TOPO_LAT_ACCEL,   CHANDRA_MAX_TOPO_LAT_ACCEL,
       BUDHA_MAX_TOPO_LAT_ACCEL,   SHUKRA_MAX_TOPO_LAT_ACCEL,
       MANGALA_MAX_TOPO_LAT_ACCEL, GURU_MAX_TOPO_LAT_ACCEL,
       SHANI_MAX_TOPO_LAT_ACCEL,   URANUS_MAX_TOPO_LAT_ACCEL,
       NEPTUNE_MAX_TOPO_LAT_ACCEL, PLUTO_MAX_TOPO_LAT_ACCEL,
       MNODE_MAX_TOPO_LAT_ACCEL,   TNODE_MAX_TOPO_LAT_ACCEL};
  // Minimum topocentric accelerations in latitudinal direction, up to 50000m in height:
  static final double[] minTopoLatAccel = new double[]
      {SURYA_MIN_TOPO_LAT_ACCEL,   CHANDRA_MIN_TOPO_LAT_ACCEL,
       BUDHA_MIN_TOPO_LAT_ACCEL,   SHUKRA_MIN_TOPO_LAT_ACCEL,
       MANGALA_MIN_TOPO_LAT_ACCEL, GURU_MIN_TOPO_LAT_ACCEL,
       SHANI_MIN_TOPO_LAT_ACCEL,   URANUS_MIN_TOPO_LAT_ACCEL,
       NEPTUNE_MIN_TOPO_LAT_ACCEL, PLUTO_MIN_TOPO_LAT_ACCEL,
       MNODE_MIN_TOPO_LAT_ACCEL,   TNODE_MIN_TOPO_LAT_ACCEL};


  // Speeds and accelerations in the direction of the distance to the earth:
  // Maximum geocentric speeds in the direction of the distance to the earth:
  static final double[] maxDistSpeed = new double[]
      {SURYA_MAX_DIST_SPEED,  CHANDRA_MAX_DIST_SPEED, BUDHA_MAX_DIST_SPEED,
       SHUKRA_MAX_DIST_SPEED, MANGALA_MAX_DIST_SPEED, GURU_MAX_DIST_SPEED,
       SHANI_MAX_DIST_SPEED,  URANUS_MAX_DIST_SPEED,  NEPTUNE_MAX_DIST_SPEED,
       PLUTO_MAX_DIST_SPEED,  MNODE_MAX_DIST_SPEED,   TNODE_MAX_DIST_SPEED};
  // Minimum geocentric speeds in the direction of the distance to the earth:
  static final double[] minDistSpeed = new double[]
      {SURYA_MIN_DIST_SPEED,  CHANDRA_MIN_DIST_SPEED, BUDHA_MIN_DIST_SPEED,
       SHUKRA_MIN_DIST_SPEED, MANGALA_MIN_DIST_SPEED, GURU_MIN_DIST_SPEED,
       SHANI_MIN_DIST_SPEED,  URANUS_MIN_DIST_SPEED,  NEPTUNE_MIN_DIST_SPEED,
       PLUTO_MIN_DIST_SPEED,  MNODE_MIN_DIST_SPEED,   TNODE_MIN_DIST_SPEED};
  // Maximum topocentric speeds in the direction of the distance to the earth:
  static final double[] maxTopoDistSpeed = new double[]
      {SURYA_MAX_TOPO_DIST_SPEED,   CHANDRA_MAX_TOPO_DIST_SPEED,
       BUDHA_MAX_TOPO_DIST_SPEED,   SHUKRA_MAX_TOPO_DIST_SPEED,
       MANGALA_MAX_TOPO_DIST_SPEED, GURU_MAX_TOPO_DIST_SPEED,
       SHANI_MAX_TOPO_DIST_SPEED,   URANUS_MAX_TOPO_DIST_SPEED,
       NEPTUNE_MAX_TOPO_DIST_SPEED, PLUTO_MAX_TOPO_DIST_SPEED,
       MNODE_MAX_TOPO_DIST_SPEED,   TNODE_MAX_TOPO_DIST_SPEED};
  // Minimum topocentric speeds in the direction of the distance to the earth:
  static final double[] minTopoDistSpeed = new double[]
      {SURYA_MIN_TOPO_DIST_SPEED,   CHANDRA_MIN_TOPO_DIST_SPEED,
       BUDHA_MIN_TOPO_DIST_SPEED,   SHUKRA_MIN_TOPO_DIST_SPEED,
       MANGALA_MIN_TOPO_DIST_SPEED, GURU_MIN_TOPO_DIST_SPEED,
       SHANI_MIN_TOPO_DIST_SPEED,   URANUS_MIN_TOPO_DIST_SPEED,
       NEPTUNE_MIN_TOPO_DIST_SPEED, PLUTO_MIN_TOPO_DIST_SPEED,
       MNODE_MIN_TOPO_DIST_SPEED,   TNODE_MIN_TOPO_DIST_SPEED};
  // Maximum geocentric accelerations in the direction of the distance to the earth:
  static final double[] maxDistAccel = new double[]
      {SURYA_MAX_DIST_ACCEL,  CHANDRA_MAX_DIST_ACCEL, BUDHA_MAX_DIST_ACCEL,
       SHUKRA_MAX_DIST_ACCEL, MANGALA_MAX_DIST_ACCEL, GURU_MAX_DIST_ACCEL,
       SHANI_MAX_DIST_ACCEL,  URANUS_MAX_DIST_ACCEL,  NEPTUNE_MAX_DIST_ACCEL,
       PLUTO_MAX_DIST_ACCEL,  MNODE_MAX_DIST_ACCEL,   TNODE_MAX_DIST_ACCEL};
  // Minimum geocentric accelerations in the direction of the distance to the earth:
  static final double[] minDistAccel = new double[]
      {SURYA_MIN_DIST_ACCEL,  CHANDRA_MIN_DIST_ACCEL, BUDHA_MIN_DIST_ACCEL,
       SHUKRA_MIN_DIST_ACCEL, MANGALA_MIN_DIST_ACCEL, GURU_MIN_DIST_ACCEL,
       SHANI_MIN_DIST_ACCEL,  URANUS_MIN_DIST_ACCEL,  NEPTUNE_MIN_DIST_ACCEL,
       PLUTO_MIN_DIST_ACCEL,  MNODE_MIN_DIST_ACCEL,   TNODE_MIN_DIST_ACCEL};
  // Maximum topocentric accelerations in the direction of the distance to the earth:
  static final double[] maxTopoDistAccel = new double[]
      {SURYA_MAX_TOPO_DIST_ACCEL,   CHANDRA_MAX_TOPO_DIST_ACCEL,
       BUDHA_MAX_TOPO_DIST_ACCEL,   SHUKRA_MAX_TOPO_DIST_ACCEL,
       MANGALA_MAX_TOPO_DIST_ACCEL, GURU_MAX_TOPO_DIST_ACCEL,
       SHANI_MAX_TOPO_DIST_ACCEL,   URANUS_MAX_TOPO_DIST_ACCEL,
       NEPTUNE_MAX_TOPO_DIST_ACCEL, PLUTO_MAX_TOPO_DIST_ACCEL,
       MNODE_MAX_TOPO_DIST_ACCEL,   TNODE_MAX_TOPO_DIST_ACCEL};
  // Minimum topocentric accelerations in the direction of the distance to the earth:
  static final double[] minTopoDistAccel = new double[]
      {SURYA_MIN_TOPO_DIST_ACCEL,   CHANDRA_MIN_TOPO_DIST_ACCEL,
       BUDHA_MIN_TOPO_DIST_ACCEL,   SHUKRA_MIN_TOPO_DIST_ACCEL,
       MANGALA_MIN_TOPO_DIST_ACCEL, GURU_MIN_TOPO_DIST_ACCEL,
       SHANI_MIN_TOPO_DIST_ACCEL,   URANUS_MIN_TOPO_DIST_ACCEL,
       NEPTUNE_MIN_TOPO_DIST_ACCEL, PLUTO_MIN_TOPO_DIST_ACCEL,
       MNODE_MIN_TOPO_DIST_ACCEL,   TNODE_MIN_TOPO_DIST_ACCEL};

//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////

  /**
  * Just to inhibit instantiation of this class, which is never necessary.
  */
  private SwephData() { }
}
