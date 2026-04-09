/*
 * This is an extension to the Java port of the Swiss Ephemeris package of
 * Astrodienst AG, Zuerich (Switzerland). Thomas Mack, mack@idb.cs.tu-bs.de, 3rd
 * of December, 2001
 */
package org.athomeprojects.swisseph;

class Extensions {
    SwissEph sw;

    double bound_ut = 0.0; // Jimmy: add transit search bound to speed up search

    Extensions(SwissEph sw)
    {
        this.sw = sw;
    }

    // transitVal is the longitude or latitude or speed, for which the
    // transit is to be calculated.
    // getNextTransit() will return the current date and time, when the
    // transit ist occuring on that date. If you really want the next
    // transit AFTER that date, add at least calcTimePrecision(...) to
    // your jdET, as this is the minimum time difference, for which the
    // available precision allows.
    // You can NOT rely on the assumption that you will get realistically
    // differentiable transit values with a time difference of
    // calcTimePrecision(...), but at least it does not make ANY sense
    // to recalculate a transit with a time difference SMALLER than the
    // value returned by calcTimePrecision().
    //
    // A problem:
    // When a transit takes a long time, this means, when the planet
    // stays a long time very near to the transit point, the program
    // may appear to be abitrary in its results. The reason is, that
    // it does not look for the EXACT transit point, but for an area
    // around the exact transit point that is defined by the maximum
    // available precision for the position calculation.
    // You may get many transits for just one planetary transit, as we
    // cannot differentiate transits, when they are in an area of values
    // which is beyond the maximum available precision. E.g., when the
    // sun is in the latitudinal area of 0.0019 to 0.0021 for maybe two
    // days, there is no chance to differentiate between any dates in
    // this area of time: You will get the input date returned as the
    // transit date always, when the input date is in the area of these
    // two days.
    double getNextTransit(int planet, double transitVal, int flags,
            double tjdET, boolean backward) throws IllegalArgumentException,
            SwissephException
    {
        //System.out.print("xxx " + tjdET + " ");
        //System.out.println("# " + planet + "\n# " + transitVal + "\n# " +
        // flags + "\n# " + tjdET + "\n# " + backward);
        // List of all valid flags:
        int vFlags = SweConst.SEFLG_EPHMASK | SweConst.SEFLG_TOPOCTR
                | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_TRANSIT_LONGITUDE
                | SweConst.SEFLG_TRANSIT_LATITUDE
                | SweConst.SEFLG_TRANSIT_DISTANCE
                | SweConst.SEFLG_TRANSIT_SPEED;
        if ((flags & ~vFlags) != 0) {
            throw new IllegalArgumentException("Invalid flag(s): "
                    + (flags & ~vFlags));
        }
        // Allow only one of SEFLG_TRANSIT_LONGITUDE, SEFLG_TRANSIT_LATITUDE,
        // SEFLG_TRANSIT_DISTANCE:
        int type = flags
                & (SweConst.SEFLG_TRANSIT_LONGITUDE
                        | SweConst.SEFLG_TRANSIT_LATITUDE | SweConst.SEFLG_TRANSIT_DISTANCE);
        if (type != SweConst.SEFLG_TRANSIT_LONGITUDE
                && type != SweConst.SEFLG_TRANSIT_LATITUDE
                && type != SweConst.SEFLG_TRANSIT_DISTANCE) {
            throw new IllegalArgumentException(
                    "Invalid flag combination '"
                            + flags
                            + "': specify at least exactly one of SEFLG_TRANSIT_LONGITUDE ("
                            + SweConst.SEFLG_TRANSIT_LONGITUDE
                            + "), SEFLG_TRANSIT_LATITUDE ("
                            + SweConst.SEFLG_TRANSIT_LATITUDE
                            + "), SEFLG_TRANSIT_DISTANCE ("
                            + SweConst.SEFLG_TRANSIT_DISTANCE + ").");
        }
        if (planet < SweConst.SE_SUN || planet > SweConst.SE_TRUE_NODE) {
            throw new IllegalArgumentException("Unsupported planet: "
                    + sw.swe_get_planet_name(planet));
        }
        int idx = 0; // The index into the xx[] array in swe_calc()
        if ((flags & SweConst.SEFLG_TRANSIT_LATITUDE) != 0) { // Calculate
            // latitudinal
            // transits
            idx = 1;
        } else if ((flags & SweConst.SEFLG_TRANSIT_DISTANCE) != 0) { // Calculate
            // distance
            // transits
            idx = 2;
        }
        if ((flags & SweConst.SEFLG_TRANSIT_SPEED) != 0) { // Calculate speed
            // transits
            idx += 3;
            flags |= SweConst.SEFLG_SPEED;
        }
        double maxDegPerDay = getSpeed(false, flags, planet);
        double minDegPerDay = getSpeed(true, flags, planet);
        // Eliminate SEFLG_TRANSIT_* flags for use in swe_calc():
        flags &= ~(SweConst.SEFLG_TRANSIT_LONGITUDE
                | SweConst.SEFLG_TRANSIT_LATITUDE
                | SweConst.SEFLG_TRANSIT_DISTANCE | SweConst.SEFLG_TRANSIT_SPEED);
        return nextTransit(planet, transitVal, flags, tjdET, backward,
                maxDegPerDay, minDegPerDay, idx);
    }

    // We should see that the base precision in degrees is as low as up to 1"
    // (0.00027778), so 0.00019 will be considered a transit over 0.00020!
    private double nextTransit(int pl, double offset, int flgs, double jdET,
            boolean back, double max, double min, int idx)
    {
        double[] xx = new double[6];
        StringBuffer serr = new StringBuffer();
        double jdPlus, jdMinus;
        double lastJD = jdET;
        boolean found = false;
        boolean above;
        double lastVal;
        // Rollover 360 degrees to 0 degrees etc. or continuous values:
        boolean rollover = (idx == 0); // We need a rollover of 360 degrees
        // being
        // equal to 0 degrees for longitudinal
        // position transits only.
        // Similar rollover considerations for the latitude will be necessary,
        // if
        // swe_calc() would return latitudinal values beyond -90 and +90
        // degrees.
        if (idx == 0) { // Longitude from 0 to 360 degrees:
            while (offset < 0.) {
                offset += 360.;
            }
            offset %= 360.;
        } else if (idx == 1) { // Latitude from -90 to +90 degrees:
            while (offset < -90.) {
                offset += 180.;
            }
            while (offset > 90.) {
                offset -= 180.;
            }
        }
        boolean xneg = (max < 0);
        boolean mneg = (min < 0);
        double degPrec = calcPrecision(pl, jdET, idx) / 2.; // Divided by two to
        // have a range of
        // +-degPrec
        double timePrec = calcTimePrecision(degPrec, min, max);
        int ret = sw.swe_calc(jdET, pl, flgs, xx, serr);
        if (ret < 0) {
            throw new SwissephException(SwissephException.UNDEFINED,
                    "Calculation failed with return code " + ret + ":\n"
                            + serr.toString());
        }
        if (offset - xx[idx] == 0.) { // If not 0.0 but "very small", then
            // interpolate after another calculation
            // in the calculation loop below
            return jdET;
        }
        if (max == 0. && min == 0.) { // No possible change in position or speed
            throw new SwissephException(SwissephException.OUT_OF_TIME_RANGE,
                    "Planet does not vary " + (idx > 2 ? "speed" : "position")
                            + ".");
        }
        while (true) {
            above = (xx[idx] >= offset);
            lastJD = jdET;
            lastVal = xx[idx];
            if (rollover && xx[idx] < offset) {
                xx[idx] += 360.;
            }
            // Find next reasonable point to probe.
            if (rollover) {
                if (back) {
                    jdPlus = (xx[idx] - offset - (xneg ? 360. : 0)) / max;
                    jdMinus = (xx[idx] - offset - (mneg ? 360. : 0)) / min;
                    jdET -= Math.min(jdPlus, jdMinus);
                } else {
                    jdPlus = (offset - xx[idx] + (xneg ? 0 : 360.)) / max;
                    jdMinus = (offset - xx[idx] + (mneg ? 0 : 360.)) / min;
                    jdET += Math.min(jdPlus, jdMinus);
                }
            } else {
                jdPlus = (back ? (xx[idx] - offset) : (offset - xx[idx])) / max;
                jdMinus = (back ? (xx[idx] - offset) : (offset - xx[idx]))
                        / min;
                if (jdPlus < 0) {
                    if (jdMinus < 0) { // When both min and max have the same
                        // sign
                        throw new SwissephException(
                                SwissephException.OUT_OF_TIME_RANGE, -1,
                                "No transit in ephemeris time range."); // I
                        // mean:
                        // No
                        // transits
                        // possible...
                    } else {
                        jdET += (back ? -jdMinus : jdMinus);
                    }
                } else {
                    if (jdMinus < 0) {
                        jdET += (back ? -jdPlus : jdPlus);
                    } else {
                        jdET += (back ? -Math.min(jdPlus, jdMinus) : Math.min(
                                jdPlus, jdMinus));
                    }
                }
            }
            // Add at least "timePrec" time to the last time:
            if (Math.abs(jdET - lastJD) < timePrec) {
                jdET = lastJD + (back ? -timePrec : +timePrec);
            }
            ret = sw.swe_calc(jdET, pl, flgs, xx, serr);
            if (ret < 0) {
                throw new SwissephException(SwissephException.UNDEFINED,
                        "Calculation failed with return code " + ret + ":\n"
                                + serr.toString());
            }
            // Hits the transiting point exactly...:
            if (offset - xx[idx] == 0.) {
                return jdET;
            }
            // The planet may have moved forward or backward, in one of these
            // directions it would have crossed the transit point.
            //
            // Whatever distance could have been reached in lesser time (forward
            // or
            // backward move), we take it to be the direction of movement.
            boolean pxway = true;
            if (rollover) {
                double deltadeg1 = xx[idx] - lastVal;
                if (deltadeg1 < 0) {
                    deltadeg1 += 360.;
                }
                double deltadeg2 = lastVal - xx[idx];
                if (deltadeg2 < 0) {
                    deltadeg2 += 360.;
                }
                pxway = (mneg && !xneg ? Math.abs(deltadeg1 / max) < Math
                        .abs(deltadeg2 / min) : !back);
                if (mneg && xneg) {
                    pxway = !pxway;
                }
            } else {
                pxway = lastVal <= xx[idx];
            }
            found = // reached the maximum calc. precision:
            (Math.abs(offset - xx[idx]) < degPrec)
                    ||
                    // transits from higher deg. to lower deg.:
                    (above && xx[idx] <= offset && !pxway)
                    ||
                    // transits from lower deg. to higher deg.:
                    (!above && xx[idx] >= offset && pxway)
                    || (rollover && (
                    // transits from above the transit degree via rollover over
                    // 0 degrees to a higher degree:
                    (offset < lastVal && xx[idx] > 340. && lastVal < 20. && !pxway)
                            ||
                            // transits from below the transit degree via
                            // rollover over
                            // 360 degrees to a lower degree:
                            (offset > lastVal && xx[idx] < 20.
                                    && lastVal > 340. && pxway)
                            ||
                            // transits from below the transit degree via
                            // rollover over
                            // 0 degrees to a higher degree:
                            (offset > xx[idx] && xx[idx] > 340.
                                    && lastVal < 20. && !pxway) ||
                    // transits from above the transit degree via rollover over
                    // 360 degrees to a lower degree:
                    (offset < xx[idx] && xx[idx] < 20. && lastVal > 340. && pxway)));
            //System.out.println(found + "\t(1: " +
            // (Math.abs(offset-xx[idx])<degPrec) + ": " + offset + " / " +
            // xx[idx] + " / " + degPrec +
            //                     ")\n\t(2: " + (above && xx[idx]<=offset && !pxway) +
            //                     ")\n\t(3: " + (!above && xx[idx]>=offset && pxway) +
            //                     ")\n\t(4: " + pxway + " r: " + rollover + ") " + (offset-lastVal)
            // + " [" + (xx[idx]-lastVal) + "] " + (jdET-lastJD));
            if (found) { // Return an interpolated value, but not prior to
                // (after) the initial time (if backward):
                //System.out.println("!!! " +
                // (lastJD+(jdET-lastJD)*(offset-lastVal)/(xx[idx]-lastVal)) +
                // ": "
                // + lastJD + " + " + (jdET-lastJD) + " * " + (offset-lastVal) +
                // " /
                // " + (xx[idx]-lastVal));
                double jdRet = lastJD + (jdET - lastJD) * (offset - lastVal)
                        / (xx[idx] - lastVal);
                if (back) {
                    //System.out.println("???b " + Math.min(jdRet, jdET) + " "
                    // + timePrec);
                    return Math.min(jdRet, jdET);
                } else {
                    //System.out.println("???f " + Math.min(jdRet, jdET) + " "
                    // + timePrec);
                    return Math.max(jdRet, jdET);
                }
            }
        }
    }

    // Jimmy: add transit search bound to speed up search
    void setTransitSearchBound(double ut)
    {
        bound_ut = ut;
    }

    double getRelativeTransit(int tPlanet, int rPlanet, double transitVal,
            int flags, double tjdET, boolean backward)
            throws IllegalArgumentException, SwissephException
    {
        // Check parameter:
        // //////////////////////////////////////////////////////
        // List of all valid flags:
        int vFlags = SweConst.SEFLG_EPHMASK | SweConst.SEFLG_TOPOCTR
                | SweConst.SEFLG_TRANSIT_LONGITUDE
                | SweConst.SEFLG_TRANSIT_LATITUDE
                | SweConst.SEFLG_TRANSIT_DISTANCE
                | SweConst.SEFLG_TRANSIT_SPEED;
        if ((flags & ~vFlags) != 0) {
            throw new IllegalArgumentException("Invalid flag(s): "
                    + (flags & ~vFlags));
        }
        // Allow only one of SEFLG_TRANSIT_LONGITUDE, SEFLG_TRANSIT_LATITUDE,
        // SEFLG_TRANSIT_DISTANCE:
        int type = flags
                & (SweConst.SEFLG_TRANSIT_LONGITUDE
                        | SweConst.SEFLG_TRANSIT_LATITUDE | SweConst.SEFLG_TRANSIT_DISTANCE);
        if (type != SweConst.SEFLG_TRANSIT_LONGITUDE
                && type != SweConst.SEFLG_TRANSIT_LATITUDE
                && type != SweConst.SEFLG_TRANSIT_DISTANCE) {
            throw new IllegalArgumentException(
                    "Invalid flag combination '"
                            + flags
                            + "': specify at least exactly one of SEFLG_TRANSIT_LONGITUDE ("
                            + SweConst.SEFLG_TRANSIT_LONGITUDE
                            + "), SEFLG_TRANSIT_LATITUDE ("
                            + SweConst.SEFLG_TRANSIT_LATITUDE
                            + "), SEFLG_TRANSIT_DISTANCE ("
                            + SweConst.SEFLG_TRANSIT_DISTANCE + ").");
        }
        // Planets have to be different and are restricted to some of them:
        if (tPlanet < SweConst.SE_SUN || tPlanet > SweConst.SE_TRUE_NODE
                || rPlanet < SweConst.SE_SUN || rPlanet > SweConst.SE_TRUE_NODE
                || rPlanet == tPlanet) {
            throw new IllegalArgumentException(
                    (tPlanet == rPlanet ? "Transiting and referred planet have to be different!"
                            : "Unsupported planet: "
                                    + sw.swe_get_planet_name(tPlanet) + " or "
                                    + sw.swe_get_planet_name(rPlanet)));
        }
        // Calculate basic parameters:
        // ///////////////////////////////////////////
        // Relative transits don't care about sidereal or tropical...???
        flags &= ~SweConst.SEFLG_SIDEREAL;
        // The index into the xx[] array in swe_calc() to use:
        int idx = 0;
        if ((flags & SweConst.SEFLG_TRANSIT_LATITUDE) != 0) { // Calculate
            // latitudinal
            // transits
            idx = 1;
        } else if ((flags & SweConst.SEFLG_TRANSIT_DISTANCE) != 0) { // Calculate
            // distance
            // transits
            idx = 2;
        }
        if ((flags & SweConst.SEFLG_TRANSIT_SPEED) != 0) { // Calculate speed
            // transits
            idx += 3;
        }
        // Maximum and minimum speeds of the transiting and referenced planet:
        double tMaxDegPerDay = getSpeed(false, flags, tPlanet);
        double tMinDegPerDay = getSpeed(true, flags, tPlanet);
        double rMaxDegPerDay = getSpeed(false, flags, rPlanet);
        double rMinDegPerDay = getSpeed(true, flags, rPlanet);
        // For swe_calc calculations, we don't need the TRANSIT flags:
        flags &= ~(SweConst.SEFLG_TRANSIT_LONGITUDE
                | SweConst.SEFLG_TRANSIT_LATITUDE
                | SweConst.SEFLG_TRANSIT_DISTANCE | SweConst.SEFLG_TRANSIT_SPEED);
        // ..., but we may need the speed flag:
        flags |= (idx > 2 ? SweConst.SEFLG_SPEED : 0);
        return relTransit(tPlanet, rPlanet, transitVal, flags, tjdET, backward,
                tMaxDegPerDay, tMinDegPerDay, rMaxDegPerDay, rMinDegPerDay, idx);
    }

    // Jimmy: bug in the code, works for 0 offset only (at least if idx == 0)
    // quick fix: apply offset as shift to position of rPl and set offset to 0
    private double relTransit(int tPl, int rPl, double offset, int flgs,
            double jdET, boolean back, double tMax, double tMin, double rMax,
            double rMin, int idx) throws IllegalArgumentException
    {
        double[] xxr = new double[6]; // Referenced planet
        double[] xxt = new double[6]; // Transiting planet
        StringBuffer serr = new StringBuffer();
        double jdPlus, jdMinus;
        double lastJD = jdET;
        boolean found = false;
        boolean above;
        double lastDelta;
        // Rollover 360 degrees to 0 degrees etc. or continuous values:
        boolean rollover = (idx == 0); // We need a rollover of 360 degrees
        // being
        // equal to 0 degrees for longitudinal
        // position transits only.
        // Similar rollover considerations for the latitude will be necessary,
        // if
        // swe_calc() would return latitudinal values beyond -90 and +90
        // degrees.
        double r_shift = 0.0;
        if (idx == 0) { // Longitude from 0 to 360 degrees:
            while (offset < 0.) {
                offset += 360.;
            }
            offset %= 360.;
            // Jimmy: shift position of rpl by offset and set offset to 0
            r_shift = offset;
            offset = 0.0;
        } else if (idx == 1) { // Latitude from -90 to +90 degrees:
            while (offset < -90.) {
                offset += 180.;
            }
            while (offset > 90.) {
                offset -= 180.;
            }
        }
        // Maximum and minimum speed, both planets can approach each other:
        double deltaMax = tMax - rMin;
        double deltaMin = tMin - rMax;
        boolean xneg = (deltaMax < 0);
        boolean mneg = (deltaMin < 0);
        double degPrec = Math.max(calcPrecision(tPl, jdET, idx), calcPrecision(
                rPl, jdET, idx)) / 2.; // Divided by two to have a range of
        // +-degPrec
        double timePrec = calcTimePrecision(degPrec, Math.min(Math.abs(tMin),
                Math.abs(rMin)), Math.min(Math.abs(tMax), Math.abs(rMax)));
        int ret = sw.swe_calc(jdET, rPl, flgs, xxr, serr);
        if (ret < 0) {
            throw new SwissephException(SwissephException.UNDEFINED,
                    "Calculation failed with return code " + ret + ":\n"
                            + serr.toString());
        }
        // Jimmy: shift position
        if (rollover && r_shift > 0.0) {
            xxr[0] += r_shift;
            if (xxr[0] >= 360.0)
                xxr[0] -= 360.0;
        }
        ret = sw.swe_calc(jdET, tPl, flgs, xxt, serr);
        if (ret < 0) {
            throw new SwissephException(SwissephException.UNDEFINED,
                    "Calculation failed with return code " + ret + ":\n"
                            + serr.toString());
        }
        if (Math.abs(offset - (xxt[idx] - xxr[idx])) == 0.) {
            // If not 0.0 but "very small", then
            // interpolate after another calculation
            // in the calculation loop below
            return jdET;
        }
        if (deltaMax == 0. && deltaMin == 0.) { // No possible change in
            // position or speed
            throw new SwissephException(SwissephException.OUT_OF_TIME_RANGE,
                    "Planets do not vary their relative "
                            + (idx > 2 ? "speed" : "position") + ".");
        }
        while (true) {
            double delta = xxt[idx] - xxr[idx];
            above = (delta >= offset);
            lastJD = jdET;
            lastDelta = delta;
            if (rollover && delta < offset) {
                delta += 360.;
            }
            // Find next reasonable point to probe.
            if (rollover) {
                if (back) {
                    jdPlus = (delta - offset - (xneg ? 360. : 0)) / deltaMax;
                    jdMinus = (delta - offset - (mneg ? 360. : 0)) / deltaMin;
                    jdET -= Math.min(jdPlus, jdMinus);
                } else {
                    jdPlus = (offset - delta + (xneg ? 0 : 360.)) / deltaMax;
                    jdMinus = (offset - delta + (mneg ? 0 : 360.)) / deltaMin;
                    jdET += Math.min(jdPlus, jdMinus);
                }
            } else {
                jdPlus = (back ? (delta - offset) : (offset - delta))
                        / deltaMax;
                jdMinus = (back ? (delta - offset) : (offset - delta))
                        / deltaMin;
                if (jdPlus < 0) {
                    if (jdMinus < 0) { // When both deltaMin and deltaMax have
                        // the same sign
                        throw new SwissephException(
                                SwissephException.OUT_OF_TIME_RANGE, -1,
                                "No transit in ephemeris time range."); // I
                        // mean:
                        // No
                        // transits
                        // possible...
                    } else {
                        jdET += (back ? -jdMinus : jdMinus);
                    }
                } else {
                    if (jdMinus < 0) {
                        jdET += (back ? -jdPlus : jdPlus);
                    } else {
                        jdET += (back ? -Math.min(jdPlus, jdMinus) : Math.min(
                                jdPlus, jdMinus));
                    }
                }
            }
            // Add at least "timePrec" time to the last time:
            if (Math.abs(jdET - lastJD) < timePrec) {
                jdET = lastJD + (back ? -timePrec : +timePrec);
            }
            // Jimmy: check for search beyound bound
            if (bound_ut != 0.0
                    && (back && jdET < bound_ut || !back && jdET > bound_ut)) {
                throw new IllegalArgumentException("Out of bound");
            }
            ret = sw.swe_calc(jdET, rPl, flgs, xxr, serr);
            if (ret < 0) {
                throw new SwissephException(SwissephException.UNDEFINED,
                        "Calculation failed with return code " + ret + ":\n"
                                + serr.toString());
            }
            // Jimmy: shift position
            if (rollover && r_shift > 0.0) {
                xxr[0] += r_shift;
                if (xxr[0] >= 360.0)
                    xxr[0] -= 360.0;
            }
            ret = sw.swe_calc(jdET, tPl, flgs, xxt, serr);
            if (ret < 0) {
                throw new SwissephException(SwissephException.UNDEFINED,
                        "Calculation failed with return code " + ret + ":\n"
                                + serr.toString());
            }
            delta = xxt[idx] - xxr[idx];
            // Hits the transiting point exactly...:
            if (offset - delta == 0.) {
                return jdET;
            }
            // Jimmy: condition cannot be true, removed
            // if (rollover && offset < 0.) {
            //	delta -= 360.;
            // }
            // The planets may have moved forward or backward, in one of these
            // directions it would have crossed the transit point.
            //
            // Whatever distance could have been reached in lesser time (forward
            // or
            // backward move), we take it to be the direction of movement.
            boolean pxway = true;
            if (rollover) {
                // Jimmy: need to roll back if exceed 360 degree
                if (delta > offset && lastDelta < offset
                        && delta - lastDelta > 360.) {
                    delta -= 360.;
                } else if (delta < offset && lastDelta > offset
                        && lastDelta - delta > 360.) {
                    delta += 360.;
                }
                double deltadeg1 = delta - lastDelta;
                if (deltadeg1 < 0) {
                    deltadeg1 += 360.;
                }
                double deltadeg2 = lastDelta - delta;
                if (deltadeg2 < 0) {
                    deltadeg2 += 360.;
                }
                pxway = (mneg && !xneg ? Math.abs(deltadeg1 / deltaMax) < Math
                        .abs(deltadeg2 / deltaMin) : !back);
                if (mneg && xneg) {
                    pxway = !pxway;
                }
            } else {
                pxway = lastDelta <= delta;
            }
            found = // reached the maximum calc. precision:
            (Math.abs(offset - delta) < degPrec)
                    ||
                    // transits from higher deg. to lower deg.:
                    (above && delta <= offset && !pxway)
                    ||
                    // transits from lower deg. to higher deg.:
                    (!above && delta >= offset && pxway)
                    || (rollover && (
                    // transits from above the transit degree via rollover over
                    // 0 degrees to a higher degree:
                    (offset < lastDelta && delta > 340. && lastDelta < 20. && !pxway)
                            ||
                            // transits from below the transit degree via
                            // rollover over
                            // 360 degrees to a lower degree:
                            (offset > lastDelta && delta < 20.
                                    && lastDelta > 340. && pxway)
                            ||
                            // transits from below the transit degree via
                            // rollover over
                            // 0 degrees to a higher degree:
                            (offset > delta && delta > 340. && lastDelta < 20. && !pxway) ||
                    // transits from above the transit degree via rollover over
                    // 360 degrees to a lower degree:
                    (offset < delta && delta < 20. && lastDelta > 340. && pxway)));
            // Hits the transiting point exactly...:
            if (Math.abs(offset - delta) < degPrec) {
                return jdET;
            }
            if (found) { // Return an interpolated value, but not prior to
                // (after) the initial time (if backward):
                //System.out.println("!!! " +
                // (lastJD+(jdET-lastJD)*(offset-lastDelta)/(delta-lastDelta)) +
                // ":
                // " + lastJD + " + " + (jdET-lastJD) + " * " +
                // (offset-lastDelta) +
                // " / " + (delta-lastDelta));
                double jdRet = lastJD + (jdET - lastJD) * (offset - lastDelta)
                        / (delta - lastDelta);
                if (back) {
                    //System.out.println("???b " + Math.min(jdRet, jdET) + " "
                    // + timePrec);
                    return Math.min(jdRet, jdET);
                } else {
                    //System.out.println("???f " + Math.min(jdRet, jdET) + " "
                    // + timePrec);
                    return Math.max(jdRet, jdET);
                }
            }
            //      above=offset>0.;
            //      found=(offset==0.) ||
            //            (lastTjdDiff==0.) ||
            //            (above != lastAbove &&
            //             Math.abs(offset) <= maxPrec &&
            //             Math.abs(lastDelta) <= maxPrec);
            lastJD = jdET;
        }
    }

    double getSpeed(boolean min, int flags, int planet)
    {
        boolean lat = ((flags & SweConst.SEFLG_TRANSIT_LATITUDE) != 0);
        boolean dist = ((flags & SweConst.SEFLG_TRANSIT_DISTANCE) != 0);
        boolean lon = (!lat && !dist);
        boolean speed = ((flags & SweConst.SEFLG_TRANSIT_SPEED) != 0);
        boolean topo = ((flags & SweConst.SEFLG_TOPOCTR) != 0);
        // Some topocentric speeds are very different to the geocentric
        // speeds, so we use other values than for geocentric calculations:
        if (topo) {
            if (!sw.swed.geopos_is_set) {
                throw new IllegalArgumentException(
                        "Geographic position is not set for "
                                + "requested topocentric calculations.");
            }
            if (sw.swed.topd.geoalt > 50000.) {
                throw new IllegalArgumentException(
                        "Topocentric transit calculations "
                                + "are restricted to a maximum "
                                + "altitude of 50km so far.");
            } else if (sw.swed.topd.geoalt < -12000000) {
                throw new IllegalArgumentException(
                        "Topocentric transit calculations "
                                + "are restricted to a minimum "
                                + "altitude of -12000km so far.");
            }
            if (speed) {
                if (lat) {
                    return (min ? SwephData.minTopoLatAccel[planet]
                            : SwephData.maxTopoLatAccel[planet]);
                } else if (dist) {
                    return (min ? SwephData.minTopoDistAccel[planet]
                            : SwephData.maxTopoDistAccel[planet]);
                } else {
                    return (min ? SwephData.minTopoLonAccel[planet]
                            : SwephData.maxTopoLonAccel[planet]);
                }
            } else {
                if (lat) {
                    return (min ? SwephData.minTopoLatSpeed[planet]
                            : SwephData.maxTopoLatSpeed[planet]);
                } else if (dist) {
                    return (min ? SwephData.minTopoDistSpeed[planet]
                            : SwephData.maxTopoDistSpeed[planet]);
                } else {
                    return (min ? SwephData.minTopoLonSpeed[planet]
                            : SwephData.maxTopoLonSpeed[planet]);
                }
            }
        }
        // Geocentric:
        if (speed) {
            if (lat) {
                return (min ? SwephData.minLatAccel[planet]
                        : SwephData.maxLatAccel[planet]);
            } else if (dist) {
                return (min ? SwephData.minDistAccel[planet]
                        : SwephData.maxDistAccel[planet]);
            } else {
                return (min ? SwephData.minLonAccel[planet]
                        : SwephData.maxLonAccel[planet]);
            }
        } else {
            if (lat) {
                return (min ? SwephData.minLatSpeed[planet]
                        : SwephData.maxLatSpeed[planet]);
            } else if (dist) {
                return (min ? SwephData.minDistSpeed[planet]
                        : SwephData.maxDistSpeed[planet]);
            } else {
                return (min ? SwephData.minLonSpeed[planet]
                        : SwephData.maxLonSpeed[planet]);
            }
        }
    }

    private double maxBaryDist[] = new double[] { 1.017261973, // Sun == 0
            1.019846623, // Moon == 1
            0.466604085, // Mercury == 2
            0.728698831, // Venus == 3
            0.728698831, // Mars == 4
            4.955912195, // Jupiter == 5
            8.968685733, // Saturn == 6
            19.893326756, // Uranus == 7
            30.326750627, // Neptune == 8
            41.499626899, // Pluto == 9
            0.002569555, // MeanNode == 10
            0.002774851, // TrueNode == 11
    };

    double calcTimePrecision(double degPrec, double min, double max)
    {
        // Recalculate degPrec to mean the minimum time, in which the planet can
        // possibly move that degree:
        return degPrec / Math.max(Math.abs(min), Math.abs(max));
    }

    double calcPrecision(int planet, double jd, int what)
    {
        // Calculate the planet's minimum movement regarding the maximum
        // available
        // precision.
        //
        // For all calculations, we assume the following minimum exactnesses
        // based on the discussions on
        // http://www.astro.com/org.athomeprojects.swisseph, even
        // though
        // these values are nothing more than very crude estimations which
        // should
        // leave us on the save side always, even more, when seeing that we
        // always
        // consider the maximum possible speed / acceleration of a planet in the
        // transit calculations and not the real speed.
        //
        // Take degPrec to be the minimum exact degree in longitude
        double degPrec = 0.005;
        if (what > 2) { // Speed
            // "The speed precision is now better than 0.002" for all planets"
            degPrec = 0.002;
        } else { // Degrees
            // years 1980 to 2099: 0.005"
            // years before 1980: 0.08" (from sun to jupiter)
            // years 1900 to 1980: 0.08" (from saturn to neptune) (added: nodes)
            // years before 1900: 1" (from saturn to neptune) (added: nodes)
            // years after 2099: same as before 1900
            //
            if (planet >= SweConst.SE_SUN && planet <= SweConst.SE_JUPITER) {
                if (jd < 1980 || jd > 2099) {
                    degPrec = 0.08;
                }
            } else {
                if (jd >= 1900 && jd < 1980) {
                    degPrec = 0.08;
                } else if (jd < 1900 || jd > 2099) {
                    degPrec = 1;
                }
            }
        }
        degPrec /= 3600.;
        degPrec *= 0.7; // We take the precision to BETTER THAN ... as it is
        // stated somewhere
        // We recalculate these degrees to the minimum time difference that CAN
        // possibly give us data differing more than the above given precision.
        switch (what) {
            case 0: // Longitude
            case 1: // Latitude
            case 3: // Speed in longitude
            case 4: // Speed in latitude
                break;
            case 2: // Distance
            case 5: // Speed in distance
                // We need to recalculate the precision in degrees to a distance
                // value.
                // For this we need the maximum distance to the centre of
                // calculation,
                // which is the barycentre for the main planets.
                degPrec *= maxBaryDist[planet];
        }
        return degPrec;
        // Barycentre:
        // Sun: 0.982747149 AU 1.017261973 AU
        // Moon: 0.980136691 AU 1.019846623 AU
        // Mercury: 0.307590579 AU 0.466604085 AU
        // Venus: 0.717960758 AU 0.728698831 AU
        // Mars: 1.382830768 AU 0.728698831 AU
        // Jupiter: 5.448547595 AU 4.955912195 AU
        // Saturn: 10.117683425 AU 8.968685733 AU
        // Uranus: 18.327870391 AU 19.893326756 AU
        // Neptune: 29.935653168 AU 30.326750627 AU
        // Pluto: 29.830132096 AU 41.499626899 AU
        // MeanNode: 0.002569555 AU 0.002569555 AU
        // TrueNode: 0.002361814 AU 0.002774851 AU
        //
        // Minimum and maximum (barycentric) distances:
        // Sun: 0.000095 AU 0.01034 AU
        // Moon: 0.972939 AU 1.02625 AU
        // Mercury: 0.298782 AU 0.47569 AU
        // Venus: 0.709190 AU 0.73723 AU
        // Mars: 1.370003 AU 1.67685 AU
        // Jupiter: 4.912031 AU 5.47705 AU
        // Saturn: 8.948669 AU 10.13792 AU
        // Uranus: 18.257511 AU 20.12033 AU
        // Neptune: 29.780622 AU 30.36938 AU
        // Pluto: 29.636944 AU 49.43648 AU
        // MeanNode: - AU - AU ?
        // TrueNode: - AU - AU ?
        // Maximum and minimum (geocentric) distances:
        // Sun: 1.016688129 AU 0.983320477 AU
        // Moon: 0.002710279 AU 0.002439921 AU
        // Mercury: 0.549188094 AU 1.448731236 AU
        // Saturn: 7.84 / 7.85 AU 11.25/11.26 AU
        // Uranus: 21.147/21.148 AU AU
    }
}