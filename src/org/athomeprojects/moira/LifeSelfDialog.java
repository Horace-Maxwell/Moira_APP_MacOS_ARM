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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class LifeSelfDialog extends Dialog {
    private int life_mode, self_mode, equator_mode, snap_mode;

    private Button equator, snap;

    public LifeSelfDialog(Shell parent)
    {
        super(parent);
    }

    public Control createDialogArea(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(Resource.getString("dialog_life_method"));
        life_mode = Resource.getPrefInt("life_mode");
        final Button traditional = new Button(group, SWT.RADIO);
        traditional.setText(Resource.getString("dialog_life_traditional"));
        traditional.setSelection(life_mode == 0);
        traditional.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (traditional.getSelection())
                    life_mode = 0;
                equator.setEnabled(life_mode == 0);
                snap.setEnabled(life_mode == 1);
            }
        });
        final Button astro = new Button(group, SWT.RADIO);
        astro.setText(Resource.getString("dialog_life_astro"));
        astro.setSelection(life_mode == 1);
        astro.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (astro.getSelection())
                    life_mode = 1;
                equator.setEnabled(life_mode == 0);
                snap.setEnabled(life_mode == 1);
            }
        });
        equator = new Button(group, SWT.CHECK);
        equator_mode = Resource.getPrefInt("use_equator");
        equator.setText(Resource.getString("dialog_life_equator"));
        equator.setSelection(equator_mode != 0);
        equator.setEnabled(life_mode == 0);
        equator.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                equator_mode = equator.getSelection() ? 1 : 0;
            }
        });
        snap = new Button(group, SWT.CHECK);
        snap_mode = Resource.getPrefInt("astro_snap_to_sun_pos");
        snap.setText(Resource.getString("dialog_life_astro_degree"));
        snap.setSelection(snap_mode != 0);
        snap.setEnabled(life_mode == 1);
        snap.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                snap_mode = snap.getSelection() ? 1 : 0;
            }
        });
        group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(Resource.getString("dialog_self_method"));
        self_mode = Resource.getPrefInt("self_mode");
        final Button self = new Button(group, SWT.RADIO);
        self.setText(Resource.getString("dialog_self_self"));
        self.setSelection(self_mode == 0);
        self.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (self.getSelection())
                    self_mode = 0;
            }
        });
        final Button sunset = new Button(group, SWT.RADIO);
        sunset.setText(Resource.getString("dialog_self_sunset"));
        sunset.setSelection(self_mode == 1);
        sunset.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (sunset.getSelection())
                    self_mode = 1;
            }
        });
        final Button moonrise = new Button(group, SWT.RADIO);
        moonrise.setText(Resource.getString("dialog_self_moonrise"));
        moonrise.setSelection(self_mode == 2);
        moonrise.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                if (moonrise.getSelection())
                    self_mode = 2;
            }
        });
        return parent;
    }

    public boolean updateLifeSelf()
    {
        boolean changed = Resource.getPrefInt("life_mode") != life_mode
                || Resource.getPrefInt("self_mode") != self_mode
                || Resource.getPrefInt("use_equator") != equator_mode
                || Resource.getPrefInt("astro_snap_to_sun_pos") != snap_mode;
        Resource.putPrefInt("life_mode", life_mode);
        Resource.putPrefInt("self_mode", self_mode);
        Resource.putPrefInt("use_equator", equator_mode);
        Resource.putPrefInt("astro_snap_to_sun_pos", snap_mode);
        return changed;
    }
}