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
package org.athomeprojects.base;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.Attribute;

public class Print implements Pageable, Printable {
	static private boolean color_printer = false;

	private BaseTab eval_tab, note_tab;

	private String eval_data, note_data;

	private int multi_page, start_eval, end_eval, start_note, end_note;

	private PageFormat page_format;

	public int getNumberOfPages() {
		return (multi_page > 0) ? Pageable.UNKNOWN_NUMBER_OF_PAGES : 1;
	}

	public PageFormat getPageFormat(int page) throws IndexOutOfBoundsException {
		if (page < 0 || multi_page == 0 && page >= 1)
			throw new IndexOutOfBoundsException();
		return page_format;
	}

	public Printable getPrintable(int page) throws IndexOutOfBoundsException {
		if (page < 0 || multi_page == 0 && page >= 1)
			throw new IndexOutOfBoundsException();
		return this;
	}

	// page 0: diagram, page 1 and later: eval_data and note_data
	public int print(Graphics g, PageFormat format, int page) {
		if (page < 0 || multi_page == 0 && page >= 1)
			return NO_SUCH_PAGE;
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform save = g2d.getTransform();
		double image_width = format.getImageableWidth();
		double image_height = format.getImageableHeight();
		g2d.translate(format.getImageableX(), format.getImageableY());
		if (image_width < image_height) {
			g2d.rotate(Math.PI / 2.0);
			g2d.translate(0.0, -image_width);
		}
		double diagram_size = Math.min(image_width, image_height);
		int width = Resource.DIAGRAM_WIDTH;
		int scaler = Resource.getInt("print_scaler");
		width *= scaler;
		double scale = diagram_size / width;
		g2d.scale(scale, scale);
		int page_width = (int) (Math.max(image_width, image_height) / scale);
		int page_height = (int) (Math.min(image_width, image_height) / scale);
		Point size = new Point(page_width, page_height);
		boolean done;
		int num_pages = 1;
		if (eval_data != null && start_eval == 0) {
			start_eval = num_pages;
			num_pages += eval_tab.initPrint(g2d, eval_data, size);
			end_eval = num_pages;
		}
		if (note_data != null && start_note == 0) {
			start_note = num_pages;
			num_pages += note_tab.initPrint(g2d, note_data, size);
			end_note = num_pages;
		}
		if (page == 0) { // diagram
			ChartData.getData().pageDiagram(g2d, "", scaler, size,
					new Point(width, width), false, false, !color_printer,
					false, true, image_width < image_height,
					Resource.getPrefInt("print_vertical_text") != 0, false);
			done = false;
		} else if (multi_page > 0) {
			done = true;
			if (eval_data != null && page >= start_eval && page < end_eval) {
				done = eval_tab.print(g2d, size, page, start_eval,
						multi_page > 1);
			}
			if (note_data != null && page >= start_note && page < end_note) {
				done = note_tab.print(g2d, size, page, start_note,
						multi_page > 1);
			}
		} else {
			done = true;
		}
		g2d.setTransform(save);
		return done ? NO_SUCH_PAGE : PAGE_EXISTS;
	}

	public void printPage(BaseTab t_eval_tab, String t_eval_data,
			BaseTab t_note_tab, String t_note_data) {
		eval_tab = t_eval_tab;
		eval_data = t_eval_data;
		note_tab = t_note_tab;
		note_data = t_note_data;
		multi_page = start_eval = start_note = 0;
		if (eval_data != null)
			multi_page++;
		if (note_data != null)
			multi_page++;
		PrinterJob job = PrinterJob.getPrinterJob();
		page_format = (PageFormat) job.defaultPage().clone();
		Paper paper = (Paper) page_format.getPaper().clone();
		double h_margin = Resource.getDouble("print_horiz_margin") * 72;
		double v_margin = Resource.getDouble("print_vert_margin") * 72;
		double width = paper.getWidth() - 2.0 * h_margin;
		double height = paper.getHeight() - 2.0 * v_margin;
		paper.setImageableArea(h_margin, v_margin, width, height);
		page_format.setPaper(paper);
		job.setPageable(this);
		job.setJobName(Resource.NAME);
		if (!job.printDialog())
			return;
		Attribute[] attr = job.getPrintService().getAttributes().toArray();
		color_printer = false;
		for (int i = 0; i < attr.length; i++) {
			if (attr[i].getName().equals("color-supported")) {
				if (attr[i].toString().equals("supported"))
					color_printer = true;
				break;
			}
		}
		try {
			job.print();
		} catch (PrinterException e) {
		}
	}
}