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

import java.io.File;
import java.net.URL;

import org.athomeprojects.base.AppRuntime;
import org.athomeprojects.base.BaseIO;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class AppIO extends BaseIO {
	private boolean save_to_last;

	private String install_path;

	private String file_path;

	public AppIO(String path) {
		AppRuntime.init(Moira.class, path);
		install_path = AppRuntime.getInstallRootPath() + File.separator;
		file_path = null;
	}

	public String[] getExecCommand() {
		String launcher = AppRuntime.getLauncherCommand();
		if (launcher != null) {
			return new String[] { launcher };
		}
		String[] array = new String[2];
		array[0] = install_path + Resource.NAME + ".exe";
		File file = new File(array[0]);
		if (!file.canRead()) { // no canExecute() on os x
			array[0] = install_path + Resource.NAME + ".sh";
		}
		array[1] = install_path;
		return array;
	}

	public String getFileName(String file_name) {
		return AppRuntime.getInstallFile(file_name).getAbsolutePath();
	}

	public URL getURL(String file_name) {
		if (file_name.startsWith("$"))
			file_name = getLastOpenPath() + file_name.substring(1);
		return AppRuntime.getInstallURL(file_name);
	}

	public String[] openFile(boolean multi) {
		FileDialog dialog = new FileDialog(Moira.getShell(),
				multi ? (SWT.OPEN | SWT.MULTI) : SWT.OPEN);
		dialog.setFilterExtensions(Resource.DATA_EXTENSIONS);
		if (Resource.hasPrefKey("last_open_path"))
			dialog.setFilterPath(Resource.getPrefString("last_open_path"));
		if (dialog.open() == null)
			return null;
		String[] files = dialog.getFileNames();
		file_path = dialog.getFilterPath();
		if (files != null && files.length == 0)
			files = null;
		return files;
	}

	public String saveFile(boolean last) {
		String file_name = null;
		if (last) {
			file_path = Resource.hasPrefKey("last_open_path") ? Resource
					.getPrefString("last_open_path") : null;
			file_name = Resource.hasPrefKey("last_open_file") ? Resource
					.getPrefString("last_open_file") : null;
			if (file_name == null)
				last = false;
		}
		if (!last) {
			FileDialog dialog = new FileDialog(Moira.getShell(), SWT.SAVE);
			dialog.setFilterExtensions(Resource.DATA_EXTENSIONS);
			if (Resource.hasPrefKey("last_open_path"))
				dialog.setFilterPath(Resource.getPrefString("last_open_path"));
			if (dialog.open() == null)
				return null;
			file_name = dialog.getFileName().trim();
			if (file_name == null || file_name.equals(""))
				return null;
			file_path = dialog.getFilterPath();
		}
		File save_file;
		if (file_path != null)
			save_file = new File(file_path + File.separator + file_name);
		else
			save_file = new File(file_name);
		if (save_file.exists()
				&& !Message.question(Resource
						.getString("dialog_save_as_question")))
			return null;
		save_to_last = last;
		return file_name;
	}

	public boolean moveFile(String file_name, String src_dir, String dst_dir) {
		File src_file = new File(src_dir + File.separator + file_name);
		if (!src_file.canRead())
			return true;
		File dst_file = new File(dst_dir + File.separator + file_name);
		if (src_file.compareTo(dst_file) == 0)
			return false; // same location
		dst_file.delete();
		src_file.renameTo(dst_file);
		return true;
	}

	public boolean saveToLast() {
		return save_to_last;
	}

	public String getFilePath() {
		return file_path;
	}

	public void setLastOpenPath(String path) {
		if (path != null)
			file_path = path;
		if (file_path != null)
			Resource.putPrefString("last_open_path", file_path);
		else
			Resource.removePref("last_open_path");
	}

	public String getLastOpenPath() {
		file_path = Resource.hasPrefKey("last_open_path") ? Resource
				.getPrefString("last_open_path") : null;
		return file_path;
	}

	public void setLastOpenFile(String file) {
		Resource.putPrefString("last_open_file", file);
	}

	public String getLastOpenFile() {
		return Resource.hasPrefKey("last_open_file") ? Resource
				.getPrefString("last_open_file") : null;
	}

	public void removeLastOpenFile() {
		Resource.removePref("last_open_file");
	}

	public void setProgress(int val) {
		Moira.setProgress(val);
	}
}
