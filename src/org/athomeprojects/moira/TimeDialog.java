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

import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class TimeDialog extends Dialog {
    private boolean dst_adjust;

    private boolean switch_day_at_11_pm, day_night_by_time,
            start_at_winter_solstice, child_period;

    private int longitude_adjust;

    public TimeDialog(Shell parent)
    {
        super(parent);
    }

    public Control createDialogArea(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        Group group = new Group(composite, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setText(Resource.getString("dialog_time_correct"));
        dst_adjust = Resource.getPrefInt("dst_adjust") != 0;
        final Button dst = new Button(group, SWT.CHECK);
        dst.setText(Resource.getString("dialog_time_dst"));
        dst.setSelection(dst_adjust);
        dst.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                dst_adjust = dst.getSelection();
            }
        });
        group = new Group(composite, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setText(Resource.getString("dialog_time_eight_char"));
        switch_day_at_11_pm = Resource.getPrefInt("switch_day_at_11_pm") != 0;
        final Button switch_day = new Button(group, SWT.CHECK);
        switch_day.setText(Resource.getString("dialog_time_switch_day"));
        switch_day.setSelection(switch_day_at_11_pm);
        switch_day.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                switch_day_at_11_pm = switch_day.getSelection();
            }
        });
        group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(Resource.getString("dialog_time_day_night"));
        day_night_by_time = Resource.getPrefInt("day_night_by_time") != 0;
        final Button day_night_time = new Button(group, SWT.RADIO);
        day_night_time.setText(Resource
                .getString("dialog_time_day_night_by_time"));
        day_night_time.setSelection(day_night_by_time);
        day_night_time.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (day_night_time.getSelection())
                    day_night_by_time = true;
            }
        });
        final Button day_night_zone = new Button(group, SWT.RADIO);
        day_night_zone.setText(Resource
                .getString("dialog_time_day_night_by_zone"));
        day_night_zone.setSelection(!day_night_by_time);
        day_night_zone.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (day_night_zone.getSelection())
                    day_night_by_time = false;
            }
        });
        group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(Resource.getString("dialog_current_year"));
        start_at_winter_solstice = Resource
                .getPrefInt("start_at_winter_solstice") != 0;
        final Button winter_start = new Button(group, SWT.RADIO);
        winter_start.setText(Resource
                .getString("dialog_current_year_start_at_winter"));
        winter_start.setSelection(start_at_winter_solstice);
        winter_start.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (winter_start.getSelection())
                    start_at_winter_solstice = true;
            }
        });
        final Button sprint_start = new Button(group, SWT.RADIO);
        sprint_start.setText(Resource
                .getString("dialog_current_year_start_at_sprint"));
        sprint_start.setSelection(!start_at_winter_solstice);
        sprint_start.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (sprint_start.getSelection())
                    start_at_winter_solstice = false;
            }
        });
        group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(Resource.getString("dialog_child_period"));
        child_period = Resource.getPrefInt("child_period") != 0;
        final Button child_period1 = new Button(group, SWT.RADIO);
        child_period1.setText(Resource.getString("dialog_child_period_9"));
        child_period1.setSelection(!child_period);
        child_period1.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (child_period1.getSelection())
                    child_period = false;
            }
        });
        final Button child_period2 = new Button(group, SWT.RADIO);
        child_period2.setText(Resource.getString("dialog_child_period_10"));
        child_period2.setSelection(child_period);
        child_period2.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (child_period2.getSelection())
                    child_period = true;
            }
        });
        group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(Resource.getString("dialog_time_select"));
        longitude_adjust = Resource.getPrefInt("longitude_adjust");
        final Button standard = new Button(group, SWT.RADIO);
        standard.setText(Resource.getString("dialog_time_standard"));
        standard.setSelection(longitude_adjust == 0);
        standard.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (standard.getSelection())
                    longitude_adjust = 0;
            }
        });
        final Button longitude = new Button(group, SWT.RADIO);
        longitude.setText(Resource.getString("dialog_time_longitude"));
        longitude.setSelection(longitude_adjust == 1);
        longitude.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (longitude.getSelection())
                    longitude_adjust = 1;
            }
        });
        final Button solar_time = new Button(group, SWT.RADIO);
        solar_time.setText(Resource.getString("dialog_time_apparent_solar"));
        solar_time.setSelection(longitude_adjust == 2);
        solar_time.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (solar_time.getSelection())
                    longitude_adjust = 2;
            }
        });
        return parent;
    }

    public boolean updateTime()
    {
        boolean changed = Resource.getPrefInt("longitude_adjust") != longitude_adjust
                || (Resource.getPrefInt("dst_adjust") != 0) != dst_adjust
                || (Resource.getPrefInt("switch_day_at_11_pm") != 0) != switch_day_at_11_pm
                || (Resource.getPrefInt("day_night_by_time") != 0) != day_night_by_time
                || (Resource.getPrefInt("start_at_winter_solstice") != 0) != start_at_winter_solstice
                || (Resource.getPrefInt("child_period") != 0) != child_period;
        Resource.putPrefInt("longitude_adjust", longitude_adjust);
        Resource.putPrefInt("dst_adjust", dst_adjust ? 1 : 0);
        Resource.putPrefInt("switch_day_at_11_pm", switch_day_at_11_pm ? 1 : 0);
        Resource.putPrefInt("day_night_by_time", day_night_by_time ? 1 : 0);
        Resource.putPrefInt("start_at_winter_solstice",
                start_at_winter_solstice ? 1 : 0);
        Resource.putPrefInt("child_period", child_period ? 1 : 0);
        return changed;
    }
}