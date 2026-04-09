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
package org.athomeprojects.moira;

import org.athomeprojects.base.Resource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NameDialog extends Dialog {
    private boolean sex;

    private String name;

    public NameDialog(Shell parent)
    {
        super(parent);
        sex = true;
        name = null;
    }

    public Control createDialogArea(Composite parent)
    {
        parent.setLayout(new GridLayout(2, false));
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setText(Resource.getString("dialog_name_name"));
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.widthHint = 160;
		final Text text = new Text(group, SWT.SINGLE | SWT.BORDER
                | SWT.H_SCROLL);
        text.setLayoutData(data);
        text.setText((name == null) ? "" : name);
        text.addListener(SWT.FocusOut, new Listener() {
            public void handleEvent(Event event)
            {
                name = text.getText();
            }
        });
        group = new Group(parent, SWT.NONE);
        group.setText(Resource.getString("sex"));
        group.setLayout(new GridLayout(2, false));
        final Button male = new Button(group, SWT.RADIO);
        male.setText(Resource.getString("male"));
        male.setSelection(sex);
        male.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                sex = male.getSelection();
            }
        });
        final Button female = new Button(group, SWT.RADIO);
        female.setText(Resource.getString("female"));
        female.setSelection(!sex);
        female.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                sex = !female.getSelection();
            }
        });
        return parent;
    }

    public void setSex(boolean male)
    {
        sex = male;
    }

    public boolean getSex()
    {
        return sex;
    }

    public void setName(String value)
    {
        name = value;
    }

    public String getName()
    {
        return (name == null) ? "" : name;
    }
}