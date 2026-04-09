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
package org.athomeprojects.swtext;

import java.util.Enumeration;
import java.util.Hashtable;

import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ImageManager {
	static private Hashtable table = new Hashtable();

	static public Image getImageDirect(String name) {
		Image image = (Image) table.get(name);
		if (image == null) {
			try {
				image = new Image(Display.getCurrent(), FileIO
						.getFileName(name));
			} catch (SWTException e) {
				return null;
			}
			table.put(name, image);
		}
		return image;
	}

	static public Image getImage(String name) {
		Image image = (Image) table.get(name);
		if (image == null) {
			image = new Image(Display.getCurrent(), FileIO.getFileName(Resource
					.getString(name)));
			table.put(name, image);
		}
		return image;
	}

	static public Image getGrayImage(String name) {
		Image image = (Image) table.get(name + " ");
		if (image == null) {
			image = new Image(Display.getCurrent(), getImage(name),
					SWT.IMAGE_GRAY);
			table.put(name + " ", image);
		}
		return image;
	}

	static public void setImageButton(Button button, String name) {
		Image image = getImage(name);
		GridData grid_data = new GridData(image.getBounds().width, image
				.getBounds().height);
		button.setLayoutData(grid_data);
		button.setImage(image);
	}

	static public void setImageLabel(Label label, String name) {
		Image image = getImage(name);
		GridData grid_data = new GridData(image.getBounds().width, image
				.getBounds().height);
		label.setLayoutData(grid_data);
		label.setImage(image);
	}

	static public void dispose() {
		if (table == null)
			return;
		for (Enumeration e = table.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			Image image = (Image) table.get(key);
			image.dispose();
		}
		table = null;
	}
}
