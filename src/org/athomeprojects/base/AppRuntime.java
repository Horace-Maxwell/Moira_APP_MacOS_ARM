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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AppRuntime {
	static private final String APP_SUPPORT = "Library/Application Support";

	static private File install_root = null, data_root = null,
			launcher_command = null;

	static public synchronized void init(Class app_class,
			String explicit_install_path) {
		launcher_command = detectLauncherCommand();
		install_root = detectInstallRoot(app_class, explicit_install_path);
		data_root = new File(new File(System.getProperty("user.home"),
				APP_SUPPORT), Resource.NAME);
		ensureDirectory(data_root);
		ensureDirectory(getDataFile("ephe"));
	}

	static public synchronized boolean isInstallRoot(String path) {
		return path != null && isInstallRoot(new File(path));
	}

	static public synchronized String getInstallRootPath() {
		ensureInitialized();
		return install_root.getAbsolutePath();
	}

	static public synchronized String getLauncherCommand() {
		ensureInitialized();
		if (launcher_command == null)
			return null;
		String command = launcher_command.getAbsolutePath();
		String name = launcher_command.getName().toLowerCase();
		if (name.equals("java") || name.equals("java.exe")
				|| name.equals("javaw.exe"))
			return null;
		return command;
	}

	static public synchronized File getInstallFile(String file_name) {
		ensureInitialized();
		File file = new File(file_name);
		if (file.isAbsolute())
			return file;
		return new File(install_root, file_name);
	}

	static public synchronized URL getInstallURL(String file_name) {
		File file = getInstallFile(file_name);
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	static public synchronized File getDataFile(String file_name) {
		ensureInitialized();
		File file = new File(file_name);
		if (file.isAbsolute())
			return file;
		return new File(data_root, file_name);
	}

	static public synchronized File findExistingFile(String file_name) {
		File file = new File(file_name);
		if (file.isAbsolute())
			return file.exists() ? file : null;
		File data_file = getDataFile(file_name);
		if (data_file.exists())
			return data_file;
		File install_file = getInstallFile(file_name);
		return install_file.exists() ? install_file : null;
	}

	static public synchronized String getEphemerisSearchPath() {
		LinkedList paths = new LinkedList();
		File data_ephe = getDataFile("ephe");
		if (data_ephe.isDirectory())
			paths.add(escapePath(data_ephe.getAbsolutePath()));
		File install_ephe = getInstallFile("ephe");
		if (install_ephe.isDirectory())
			paths.add(escapePath(install_ephe.getAbsolutePath()));
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < paths.size(); i++) {
			if (i > 0)
				buf.append(File.pathSeparatorChar);
			buf.append((String) paths.get(i));
		}
		return buf.toString();
	}

	static private void ensureInitialized() {
		if (install_root == null || data_root == null) {
			init(AppRuntime.class, null);
		}
	}

	static private File detectInstallRoot(Class app_class,
			String explicit_install_path) {
		List candidates = new LinkedList();
		addCandidate(candidates, explicit_install_path == null ? null : new File(
				explicit_install_path));
		addCandidate(candidates, detectAppBundleRoot());
		addCodeSourceCandidates(candidates, app_class);
		addCandidate(candidates, new File(System.getProperty("user.dir")));
		for (int i = 0; i < candidates.size(); i++) {
			File dir = (File) candidates.get(i);
			if (isInstallRoot(dir))
				return dir.getAbsoluteFile();
		}
		if (explicit_install_path != null) {
			return new File(explicit_install_path).getAbsoluteFile();
		}
		return new File(System.getProperty("user.dir")).getAbsoluteFile();
	}

	static private void addCodeSourceCandidates(List candidates, Class app_class) {
		if (app_class == null)
			return;
		try {
			CodeSource source = app_class.getProtectionDomain().getCodeSource();
			if (source == null)
				return;
			URI uri = source.getLocation().toURI();
			File file = new File(uri).getAbsoluteFile();
			if (file.isFile())
				file = file.getParentFile();
			addCandidate(candidates, file);
			addCandidate(candidates, file == null ? null : file.getParentFile());
		} catch (URISyntaxException e) {
		}
	}

	static private File detectLauncherCommand() {
		try {
			Optional info = ProcessHandle.current().info().command();
			return info.isPresent() ? new File((String) info.get())
					.getAbsoluteFile() : null;
		} catch (Throwable e) {
			return null;
		}
	}

	static private File detectAppBundleRoot() {
		if (launcher_command == null)
			return null;
		File macos = launcher_command.getParentFile();
		if (macos == null || !macos.getName().equals("MacOS"))
			return null;
		File contents = macos.getParentFile();
		if (contents == null || !contents.getName().equals("Contents"))
			return null;
		File app = new File(contents, "app");
		return app.isDirectory() ? app : null;
	}

	static private void addCandidate(List candidates, File candidate) {
		if (candidate == null)
			return;
		candidate = candidate.getAbsoluteFile();
		for (int i = 0; i < candidates.size(); i++) {
			if (candidate.equals(candidates.get(i)))
				return;
		}
		candidates.add(candidate);
	}

	static private boolean isInstallRoot(File dir) {
		if (dir == null || !dir.isDirectory())
			return false;
		return new File(dir, "moira_s.prop").canRead()
				&& new File(dir, "icon").isDirectory();
	}

	static private void ensureDirectory(File dir) {
		if (dir != null && !dir.isDirectory())
			dir.mkdirs();
	}

	static private String escapePath(String path) {
		path = path.replaceAll("\\\\", "\\\\\\\\");
		path = path.replaceAll(" ", "\\\\ ");
		path = path.replaceAll(":", "\\\\:");
		return path.replaceAll(";", "\\\\;");
	}
}
