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
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
package org.athomeprojects.moira;

import java.io.File;
import java.io.IOException;

import org.athomeprojects.base.AppRuntime;
import org.athomeprojects.base.ChartMode;
import org.athomeprojects.base.FileIO;
import org.athomeprojects.base.Message;
import org.athomeprojects.base.Resource;
import org.athomeprojects.swtext.ColorManager;
import org.athomeprojects.swtext.FontMap;
import org.athomeprojects.swtext.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class Moira {
	static private final int RESIZE_DELAY = 400;

	static private Display display;

	static private Shell shell, progress_shell;

	static private ProgressBar progress_indicator;

	static private Label progress_label;

	static private AppIO io;

	static private MenuFolder menu_folder;

	static private CTabFolder tab_folder;

	static private ChartTab chart_tab;

	static private TableTab table_tab;

	static private Composite table_control;

	static private TrayItem tray_item;

	static private Text transfer;

	static private int resizing;

	static private Rectangle adjust_position;

	static private boolean need_update, active, shut_down, skip_update,
			closing;

	public Moira(Shell parent, String install_path, String file) {
		new Resource(getClass(), null, null, null, null);
		FontMap.resetFontName();
		setShellTitle(progress_shell, null, true, false);
		setProgress(40);
		new AppMessage();
		if (!ChartMode.hasChartMode()) {
			Shell dialog_shell = getDotShell();
			dialog_shell.setActive();
			ChartMode.setChartMode(MenuFolder.selectChartMode(true,
					dialog_shell));
			dialog_shell.close();
		}
		if (!Resource.hasPrefInt("ui_mode")) {
			int[] trigger_size = Resource.getIntArray("ui_trigger_window_size");
			Rectangle bounds = display.getBounds();
			Resource
					.putPrefInt(
							"ui_mode",
							(bounds.width >= trigger_size[0] && bounds.height >= trigger_size[1]) ? 1
									: 0);
		}
		ChartMode.initChartMode();
		TabManager.initTabManager();
		transfer = new Text(parent, SWT.MULTI);
		chart_tab = new ChartTab();
		setProgress(50);
		table_tab = new TableTab();
		menu_folder = new MenuFolder(parent);
		tab_folder = TabManager.initTabFolder(parent, 22);
		tab_folder.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent event) {
				chart_tab.hideUIInfo();
			}
		});
		TabManager.initFolder(TabManager.MAIN_FOLDER, tab_folder);
		TabManager.setTabItem(TabManager.MAIN_FOLDER,
				TabManager.CHART_TAB_ORDER, chart_tab
						.createTabFolderPage(tab_folder), "chart", false);
		table_control = table_tab.createTabFolderPage(tab_folder);
		menu_folder.updateMenu(null);
		setProgress(80);
		for (int i = 0; i < ChartTab.NUM_TAB; i++) {
			if ((i == ChartTab.POLE_TAB || i == ChartTab.EVAL_TAB)
					&& ChartMode.isChartMode(ChartMode.ASTRO_MODE)) {
				chart_tab.showHideTab(i, false, false, false);
			} else {
				chart_tab.showHideTab(i, ChartTab.getTab(i).isTabVisible(),
						false, true);
			}
		}
		showHideTable(isTableVisible(), true);
		chart_tab.updateAdjNorth(null);
		FolderToolBar.init(tab_folder);
		tab_folder.setSelection(0);
		setProgress(90);
		if (Resource.trace) {
			loadData(file, -1, false);
		} else {
			try {
				loadData(file, -1, false);
			} catch (Exception e) {
				Resource.removeModEvalPref();
				attemptRecovery();
				exit(false);
			}
		}
		tab_folder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (need_update
						&& tab_folder.getSelection() != TabManager.getTabItem(
								TabManager.MAIN_FOLDER,
								TabManager.TABLE_TAB_ORDER)) {
					update(false, false);
				}
			}
		});
		setProgress(100);
	}

	static public void loadData(String file, int specified_index,
			boolean delay_update) {
		if (file == null) {
			file = io.getLastOpenFile();
			if (file != null) {
				String path = io.getLastOpenPath();
				if (path != null)
					file = path + File.separator + file;
			}
		}
		int index = (file == null) ? -1 : table_tab.openFile(false, true, true,
				file);
		if (Resource.prefChanged())
			updateModEval();
		if (specified_index >= 0 && specified_index < table_tab.getNumEntry()) {
			table_tab.unselect();
			index = specified_index;
		}
		if (index >= 0) {
			table_tab.updateData(index, delay_update);
		} else {
			chart_tab.setName(null);
			chart_tab.setSex(true);
			ChartTab.getTab(ChartTab.NOTE_TAB).setNote(null);
			chart_tab.reset();
			table_tab.clearTable(true);
			update(delay_update, true);
		}
	}

	static public void updateModEval() {
		String mod_name = Resource.hasPrefKey("modification") ? Resource
				.getPrefString("modification") : null;
		skip_update = true;
		boolean show_eval = Resource.getPrefInt("show_eval") != 0;
		chart_tab.showHideTab(ChartTab.EVAL_TAB, show_eval, false, true);
		Resource.putPrefString("evaluation_loaded", "none");
		menu_folder.setModification(mod_name, false);
		skip_update = false;
	}

	private Shell getDotShell() {
		Shell sh = new Shell(display, SWT.SYSTEM_MODAL);
		setShellTitle(sh, null, false, false);
		Rectangle rect = display.getClientArea();
		sh.setLocation(rect.width / 2, rect.height / 2);
		sh.setSize(5, 5);
		return sh;
	}

	static public boolean isTableVisible() {
		return Resource.getPrefInt("show_table") != 0;
	}

	static public void showHideTable(boolean show, boolean no_warn) {
		ChartTab.hideTip();
		if (!show && !no_warn)
			Message.warn(Resource.getString("dialog_no_table"));
		if (show) {
			TabManager.setTabItem(TabManager.MAIN_FOLDER,
					TabManager.TABLE_TAB_ORDER, table_control, "table", true);
		} else {
			TabManager.removeTabItem(TabManager.TABLE_TAB_ORDER);
		}
		Resource.putPrefInt("show_table", show ? 1 : 0);
		menu_folder.showTableMenu(show);
	}

	static public ChartTab getChart() {
		return chart_tab;
	}

	static public TableTab getTable() {
		return table_tab;
	}

	static public AppIO getIO() {
		return io;
	}

	static public MenuFolder getMenu() {
		return menu_folder;
	}

	static public Shell getShell() {
		return shell;
	}

	static public void setFocus(Control control) {
		if (active)
			control.setFocus();
	}

	static public void moveToControl(Control control) {
		Rectangle rect = control.getBounds();
		Display.getCurrent().setCursorLocation(
				control.toDisplay(rect.width / 2, rect.height / 2));
		control.setFocus();
	}

	static public void addFocusListener(Control button) {
		button.addMouseTrackListener(new MouseTrackAdapter() {
			public void mouseEnter(MouseEvent event) {
				setFocus((Control) event.getSource());
			}
		});
	}

	static public void exit(boolean restart) {
		if (closing)
			return;
		closing = true;
		FolderToolBar.saveSettings();
		saveShellBounds(null, null);
		setSystemTray(false);
		if (restart) {
			try {
				Runtime.getRuntime().exec(io.getExecCommand());
			} catch (IOException e) {
			}
		}
		shell.close();
	}

	static public void update(boolean delay_update, boolean diagram_on_top) {
		if (skip_update)
			return;
		if (delay_update) {
			need_update = true;
		} else {
			need_update = false;
			setCursor(SWT.CURSOR_WAIT, true);
			if (diagram_on_top && tab_folder.getSelectionIndex() != 0)
				tab_folder.setSelection(0);
			chart_tab.compute();
			chart_tab.updateColor();
			table_tab.update();
			setGroupName();
			updateOverride();
			shell.setActive();
			setCursor(0, false);
		}
	}

	static public boolean needUpdate() {
		return need_update;
	}

	static public void setGroupName() {
		table_tab.setGroupName();
		chart_tab.setGroupName(true);
		chart_tab.setUserGroupName();
	}

	static public void updateOverride() {
		chart_tab.updateOverride();
		table_tab.updateOverride();
	}

	static public void dispose() {
		flushEvents(false);
		setSystemTray(false);
		MenuFolder.disposeSubWin();
		TabManager.dispose();
		chart_tab.dispose();
		table_tab.dispose();
		ImageManager.dispose();
		ColorManager.dispose();
		Resource.dispose();
		tab_folder = null;
	}

	static private void initProgressBar() {
		progress_shell = new Shell(display, SWT.TITLE | SWT.BORDER);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 0;
		progress_shell.setLayout(layout);
		Composite composite = new Composite(progress_shell, SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		composite.setLayout(layout);
		Image image = ImageManager.getImageDirect("splash.png");
		if (image != null) {
			Label label = new Label(composite, SWT.NONE);
			label.setImage(image);
		}
		progress_indicator = new ProgressBar(composite, SWT.HORIZONTAL);
		progress_indicator
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progress_indicator.setMinimum(0);
		progress_indicator.setMaximum(100);
		progress_label = new Label(composite, SWT.CENTER | SWT.BORDER);
		progress_label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setProgress(0);
		progress_shell.pack();
		setShellPosition(progress_shell, "...");
		progress_shell.open();
	}

	static public void setProgress(int val) {
		if (progress_indicator == null)
			return;
		progress_indicator.setSelection(val);
		progress_label.setText("Loading...  (" + val + "% done)");
		progress_label.update();
	}

	static private void endProgress() {
		progress_shell.close();
		progress_shell = null;
		progress_indicator = null;
		progress_label = null;
	}

	static private void openShell() {
		adjust_position = null;
		if (Resource.trace) {
			shell.open();
		} else {
			try {
				// workaround for linux/gtk setBound and getBound mismatch bug
				Rectangle before_bound = shell.getBounds();
				shell.open();
				Rectangle after_bound = shell.getBounds();
				if (!after_bound.equals(before_bound)) {
					adjust_position = new Rectangle(before_bound.x
							- after_bound.x, before_bound.y - after_bound.y,
							before_bound.width - after_bound.width,
							before_bound.height - after_bound.height);
				}
			} catch (StackOverflowError e) {
				attemptRecovery();
				return;
			} catch (Exception e) {
				attemptRecovery();
				return;
			}
		}
		active = true;
		// workaround for linux/gtk bug
		chart_tab.getDiagram().layout();
	}

	static public boolean shutdown() {
		return shut_down;
	}

	static public boolean noUpdate() {
		return skip_update || resizing > 0;
	}

	static public void setNoUpdate(boolean val) {
		skip_update = val;
	}

	static public void flushEvents(boolean sleep) {
		while (!shell.isDisposed()) {
			if (Resource.trace) {
				while (display.readAndDispatch()) {
				}
				if (sleep) {
					display.sleep();
				} else {
					return;
				}
			} else {
				try {
					while (display.readAndDispatch()) {
					}
					if (sleep) {
						display.sleep();
					} else {
						return;
					}
				} catch (StackOverflowError e) {
					attemptRecovery();
					return;
				} catch (Exception e) {
					attemptRecovery();
					return;
				}
			}
		}
	}

	static private void attemptRecovery() {
		if (Message.question(Resource.getString("dialog_error_reset")))
			Resource.prefClear(false);
	}

	static public void setCursor(int type, boolean set) {
		shell.setCursor(set ? display.getSystemCursor(type) : null);
	}

	static public boolean setTrayIcon(boolean check) {
		if (check) {
			if (display.getSystemTray() != null)
				return true;
			Resource.putPrefInt("tray_icon", 0);
			return false;
		}
		return setSystemTray(Resource.getPrefInt("tray_icon") != 0);
	}

	static private boolean setSystemTray(boolean enable) {
		if (enable) {
			if (tray_item != null)
				return true;
			Tray tray = display.getSystemTray();
			if (tray == null) {
				Resource.putPrefInt("tray_icon", 0);
				return false;
			}
			tray_item = new TrayItem(tray, SWT.NONE);
			tray_item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					if (shell.getVisible()) {
						shell.setMinimized(true);
						shell.setVisible(false);
					} else {
						shell.setVisible(true);
						shell.setMinimized(false);
					}
					flushEvents(false);
				}
			});
			final Menu menu = new Menu(shell, SWT.POP_UP);
			MenuItem close = new MenuItem(menu, SWT.PUSH);
			close.setText(Resource.getString("tray_close"));
			close.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (!table_tab.checkForSaveOnClose())
						return;
					exit(false);
				}
			});
			menu.setDefaultItem(close);
			tray_item.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					menu.setVisible(true);
				}
			});
			tray_item.setImage(ImageManager.getImage("icon"));
			tray_item.setToolTipText(ChartMode.getModeTitle() + " - "
					+ Resource.NAME);
		} else {
			if (tray_item == null)
				return true;
			tray_item.dispose();
			tray_item = null;
		}
		return true;
	}

	static public void setShellTitle(Shell sh, String title, boolean version,
			boolean append) {
		if (sh == null)
			sh = shell;
		if (title == null)
			title = ChartMode.getModeTitle();
		if (append) {
			String str = sh.getText();
			int index = str.lastIndexOf("-");
			if (index >= 0)
				str = str.substring(0, index);
			title = str + title;
		}
		title += " - " + Resource.NAME;
		if (version)
			title += " " + Resource.NUMBER;
		sh.setText(title);
		sh.setImage(ImageManager.getImage("icon"));
		if (sh == shell && tray_item != null) {
			tray_item.setToolTipText(ChartMode.getModeTitle() + " - "
					+ Resource.NAME);
		}
	}

	static public void setShellPosition(Shell win, String prefix) {
		if (win == null) {
			win = shell;
			prefix = ChartTab.getUIMode() ? "ui_diagram_" : "";
		}
		Rectangle pos = getShellPosition(win, prefix, false, null);
		if (prefix == null)
			prefix = "";
		win.setBounds(pos);
	}

	static public void setShellState() {
		String prefix = ChartTab.getUIMode() ? "ui_diagram_" : "";
		if (Resource.hasPrefInt(prefix + "maximized")) {
			boolean maximized = Resource.getPrefInt(prefix + "maximized") != 0;
			if (shell.getMaximized() != maximized)
				shell.setMaximized(maximized);
		}
	}

	static private Rectangle getShellPosition(Shell win, String prefix,
			boolean default_only, Rectangle adjust) {
		Rectangle pos = new Rectangle(-1, -1, 0, 0);
		Rectangle max_rect = getClientArea();
		if (default_only || win == null || prefix != null) {
			if (prefix == null)
				prefix = "";
			if (win == null)
				win = shell;
			if (!default_only && Resource.hasPrefKey(prefix + "bounds")) {
				int[] bounds = FileIO.toIntArray(Resource.getPrefString(prefix
						+ "bounds"));
				pos.x = bounds[0];
				pos.y = bounds[1];
				pos.width = Math.min(max_rect.width, bounds[2]);
				pos.height = Math.min(max_rect.height, bounds[3]);
			} else {
				if (Resource.hasKey(prefix + "window_size")) {
					int[] dim = new int[2];
					Resource.getIntArray(prefix + "window_size", dim);
					pos.width = Math.min(max_rect.width, dim[0]);
					pos.height = Math.min(max_rect.height, dim[1]);
				} else {
					Point size = win.getSize();
					pos.width = size.x;
					pos.height = size.y;
				}
				pos.x = pos.y = -1;
			}
			if (pos.x < 0 || pos.y < 0 || pos.x + pos.width > max_rect.width
					|| pos.y + pos.height > max_rect.height) {
				if (!win.getMaximized()
						&& adjustByPreferRatio(prefix, max_rect, pos, false)) {
					pos.x = (max_rect.width - pos.width) / 2;
					pos.y = (max_rect.height - pos.height) / 2;
				} else {
					int fraction = (max_rect.width / 2 > pos.width && max_rect.height / 2 > pos.height) ? 2
							: 4;
					pos.x = (max_rect.width - pos.width) / 2;
					pos.y = (max_rect.height - pos.height) / fraction;
				}
			}
		} else {
			Rectangle rect = shell.getBounds();
			if (adjust != null) {
				rect.x += adjust.x;
				rect.y += adjust.y;
				rect.width += adjust.width;
				rect.height += adjust.height;
			}
			pos.width = (int) (0.95 * rect.width);
			if ((pos.width % 2) == 1)
				pos.width--;
			pos.height = (int) (0.95 * rect.height);
			if ((pos.height % 2) == 1)
				pos.height--;
			adjustByPreferRatio(prefix, max_rect, pos, false);
			pos.x = (rect.width - pos.width) / 2 + rect.x;
			pos.y = (rect.width - pos.width) / 4 + rect.y;
		}
		return pos;
	}

	static private void auditShellPosition() {
		Rectangle max_rect = getClientArea();
		Rectangle pos = shell.getBounds();
		String prefix = ChartTab.getUIMode() ? "ui_diagram_" : "";
		if (adjustByPreferRatio(prefix, max_rect, pos, true)) {
			if (pos.x < 0 || pos.x + pos.width > max_rect.width)
				pos.x = (max_rect.width - pos.width) / 2;
			if (pos.y < 0 || pos.y + pos.height > max_rect.height)
				pos.y = (max_rect.height - pos.height) / 2;
			shell.setBounds(pos);
		}
	}

	static private boolean adjustByPreferRatio(String prefix,
			Rectangle max_rect, Rectangle pos, boolean at_least) {
		if (!Resource.hasKey(prefix + "prefer_ratio"))
			return false;
		double ratio = Resource.getDouble(prefix + "prefer_ratio");
		int x_trim = 8, y_trim = 53;
		Rectangle rect = new Rectangle(0, 0, pos.width - x_trim, pos.height
				- y_trim);
		int val = (int) (rect.width / ratio);
		if ((val % 2) == 1)
			val--;
		if (val <= rect.height) {
			pos.width = rect.width;
			pos.height = val;
		} else {
			if (at_least) {
				pos.width = rect.width;
			} else {
				pos.width = (int) (rect.height * ratio);
				if ((pos.width % 2) == 1)
					pos.width--;
			}
			pos.height = rect.height;
		}
		pos.width += x_trim;
		pos.height += y_trim;
		return true;
	}

	static private Rectangle getClientArea() {
		Rectangle rect = display.getClientArea();
		if (Resource.hasPrefKey("limit_display_size")) {
			Rectangle d_rect = display.getBounds();
			int x_shrink = d_rect.width - rect.width;
			int y_shrink = d_rect.height - rect.height;
			int[] dim = Resource.getPrefIntArray("limit_display_size");
			rect = new Rectangle(0, 0, dim[0] - x_shrink, dim[1] - y_shrink);
		}
		return rect;
	}

	static public void saveShellBounds(Shell sh, String prefix) {
		if (sh == null) {
			sh = shell;
			prefix = ChartTab.getUIMode() ? "ui_diagram_" : "";
		}
		if (sh == shell) {
			Resource
					.putPrefInt(prefix + "maximized", sh.getMaximized() ? 1 : 0);
		}
		if (sh.getMaximized() || sh.getMinimized())
			return;
		if (adjust_position != null) {
			if (sh != shell)
				return;
		}
		Rectangle pos = getShellPosition(sh, prefix, true, null);
		Rectangle bound = sh.getBounds();
		if (adjust_position != null) {
			bound.x += adjust_position.x;
			bound.y += adjust_position.y;
			bound.width += adjust_position.width;
			bound.height += adjust_position.height;
		}
		if (bound.x == pos.x && bound.y == pos.y && bound.width == pos.width
				&& bound.height == pos.height) {
			Resource.removePref(prefix + "bounds");
		} else {
			Resource.putPrefString(prefix + "bounds", bound.x + ", " + bound.y
					+ ", " + bound.width + ", " + bound.height);
		}
	}

	static public boolean hasShellBounds(Shell sh, String prefix) {
		if (sh == null) {
			prefix = ChartTab.getUIMode() ? "ui_diagram_" : "";
		}
		return Resource.hasPrefKey(prefix + "bounds");
	}

	static public void postKeyEvent(char c, int code) {
		Event event;
		if (code != 0) {
			event = new Event();
			event.type = SWT.KeyDown;
			event.keyCode = code;
			display.post(event);
		}
		if (c != 0) {
			event = new Event();
			event.type = SWT.KeyDown;
			event.character = c;
			display.post(event);
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		if (c != 0) {
			event = new Event();
			event.type = SWT.KeyUp;
			event.character = c;
			display.post(event);
		}
		if (code != 0) {
			event = new Event();
			event.type = SWT.KeyUp;
			event.keyCode = code;
			display.post(event);
		}
	}

	static public void toClipboard(String data) {
		transfer.setText(data);
		transfer.selectAll();
		transfer.copy();
	}

	static public String fromClipboard() {
		transfer.selectAll();
		transfer.paste();
		return transfer.getText();
	}

	static public void main(String[] args) {
		String explicit_install_path = null, file = null;
		if (args.length > 0) {
			if (AppRuntime.isInstallRoot(args[0])) {
				explicit_install_path = args[0];
				if (args.length > 1)
					file = args[1];
			} else {
				file = args[0];
				if (args.length > 1 && AppRuntime.isInstallRoot(args[1]))
					explicit_install_path = args[1];
			}
		}
		AppRuntime.init(Moira.class, explicit_install_path);
		final String install_path = AppRuntime.getInstallRootPath();
		final String open_file = file;
		display = new Display();
		shell = new Shell(display);
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		final StackLayout layout = new StackLayout();
		layout.marginWidth = layout.marginHeight = 0;
		shell.setLayout(layout);
		io = new AppIO(install_path);
		FileIO.setBaseIO(io);
		initProgressBar();
		display.asyncExec(new Runnable() {
			public void run() {
				new Moira(shell, install_path, open_file);
				layout.topControl = tab_folder;
				setShellTitle(null, null, false, false);
				setShellPosition(null, null);
				setShellState();
				chart_tab.setSashWeights();
				chart_tab.layout(true);
				shell.addShellListener(new ShellListener() {
					public void shellClosed(ShellEvent e) {
						ChartTab.hideTip();
						if (!closing && !table_tab.checkForSaveOnClose()) {
							e.doit = false;
							return;
						}
						active = false;
						shut_down = true;
						exit(false);
					}

					public void shellActivated(ShellEvent e) {
						active = true;
					}

					public void shellDeactivated(ShellEvent e) {
						active = false;
					}

					public void shellIconified(ShellEvent e) {
						if (Resource.getPrefInt("tray_icon") == 0)
							return;
						ChartTab.hideTip();
						Shell[] shells = display.getShells();
						for (int i = 0; i < shells.length; i++) {
							if (shells[i] != shell
									&& shells[i].getParent() == null
									&& !ChartTab.isTipShell(shells[i]))
								shells[i].setVisible(false);
						}
						shell.setVisible(false);
					}

					public void shellDeiconified(ShellEvent e) {
						if (Resource.getPrefInt("tray_icon") == 0)
							return;
						ChartTab.hideTip();
						shell.setVisible(true);
						Shell[] shells = display.getShells();
						for (int i = 0; i < shells.length; i++) {
							if (shells[i] != shell
									&& shells[i].getParent() == null
									&& !ChartTab.isTipShell(shells[i]))
								shells[i].setVisible(true);
						}
					}
				});
				shell.addListener(SWT.Resize, new Listener() {
					public void handleEvent(Event event) {
						if (shell.getMaximized() || shell.getMinimized())
							return;
						resizing++;
						display.timerExec(RESIZE_DELAY, new Runnable() {
							public void run() {
								if (shell.isDisposed())
									return;
								if (--resizing == 0) {
									if (!shell.getMaximized()
											&& !shell.getMinimized())
										auditShellPosition();
									Canvas canvas = chart_tab.getDiagram();
									canvas.getParent().layout();
									canvas.redraw();
								}
							}
						});
					}
				});
				shell.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent event) {
						if (event.keyCode == SWT.F5 || event.keyCode == SWT.F6) {
							FolderToolBar
									.findNextEntry(event.keyCode == SWT.F5);
						}
					}
				});
				openShell();
			}
		});
		flushEvents(false);
		endProgress();
		flushEvents(true);
	}
}
