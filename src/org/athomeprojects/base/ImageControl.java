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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageControl {
	static public final int SMALL_SIZE = 0;

	static public final int MEDIUM_SIZE = 1;

	static public final int LARGE_SIZE = 2;

	static public final int CUSTOM_SIZE = 3;

	static public final int MIN_SIZE = 320;

	static public String[] IMAGE_EXTENSIONS = { "*.png", "*.jpg" };

	static public int getSelectionFromSize(int[] size) {
		if(true) return MEDIUM_SIZE;
		int[] large_size = Resource.getIntArray("image_size_large");
		int[] medium_size = Resource.getIntArray("image_size_medium");
		int[] small_size = Resource.getIntArray("image_size_small");
		if (size[0] <= MIN_SIZE || size[1] <= MIN_SIZE)
			return MEDIUM_SIZE;
		else if (size[0] == large_size[0] && size[1] == large_size[1])
			return LARGE_SIZE;
		else if (size[0] == medium_size[0] && size[1] == medium_size[1])
			return MEDIUM_SIZE;
		else if (size[0] == small_size[0] && size[1] == small_size[1])
			return SMALL_SIZE;
		else
			return CUSTOM_SIZE;
	}

	static public int[] getSizeFromSelection(int sel, int[] size) {
		switch (sel) {
		case LARGE_SIZE:
			return Resource.getIntArray("image_size_large");
		case SMALL_SIZE:
			return Resource.getIntArray("image_size_small");
		case CUSTOM_SIZE: {
			int[] large_size = Resource.getIntArray("image_size_large");
			int[] medium_size = Resource.getIntArray("image_size_medium");
			int[] small_size = Resource.getIntArray("image_size_small");
			size[2] = (size[0] < medium_size[0]) ? small_size[2]
					: ((size[0] < large_size[0]) ? medium_size[2]
							: large_size[2]);
			size[3] = (size[0] < medium_size[0]) ? small_size[3]
					: ((size[0] < large_size[0]) ? medium_size[3]
							: large_size[3]);
			return size;
		}
		default:
			return Resource.getIntArray("image_size_medium");
		}
	}

	static public int[] getImageSize(int width, int height) {
		int[] size = new int[4];
		size[0] = width;
		size[1] = height;
		switch (getSelectionFromSize(size)) {
		case LARGE_SIZE:
			size = Resource.getIntArray("image_size_large");
			break;
		case SMALL_SIZE:
			size = Resource.getIntArray("image_size_small");
			break;
		case CUSTOM_SIZE: {
			int[] large_size = Resource.getIntArray("image_size_large");
			int[] medium_size = Resource.getIntArray("image_size_medium");
			int[] small_size = Resource.getIntArray("image_size_small");
			size[2] = (size[0] < medium_size[0]) ? small_size[2]
					: ((size[0] < large_size[0]) ? medium_size[2]
							: large_size[2]);
			size[3] = (size[0] < medium_size[0]) ? small_size[3]
					: ((size[0] < large_size[0]) ? medium_size[3]
							: large_size[3]);
			break;
		}
		default:
			size = Resource.getIntArray("image_size_medium");
			break;
		}
		return size;
	}

	static public BufferedImage captureImage(int[] image_desc) {
		boolean chart_only = Resource.getPrefInt("image_chart_only") != 0;
		if (chart_only) {
			image_desc[0] = image_desc[1] = Math.min(image_desc[0],
					image_desc[1]);
			image_desc[2] = image_desc[3] = Math.min(image_desc[2],
					image_desc[3]);
		}
		int image_width = image_desc[0] - 2 * image_desc[2];
		int image_height = image_desc[1] - 2 * image_desc[3];
		BufferedImage image = new BufferedImage(image_desc[0], image_desc[1],
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		DrawAWT.setFillColor(g2d, "chart_window_bg_color", false);
		g2d.fillRect(0, 0, image_desc[0], image_desc[1]);
		g2d.translate(image_desc[2], image_desc[3]);
		int width = Resource.DIAGRAM_WIDTH;
		int scaler = Resource.getInt("print_scaler");
		width *= scaler;
		double scale = (double) Math.min(image_width, image_height) / width;
		g2d.scale(scale, scale);
		int scaled_width = (int) (image_width / scale);
		int scaled_height = (int) (image_height / scale);
		ChartData.getData().pageDiagram(g2d, "", scaler,
				new java.awt.Point(scaled_width, scaled_height),
				new java.awt.Point(width, width), false, true, false,
				chart_only, true, false,
				Resource.getPrefInt("image_vertical_text") != 0, false);
		g2d.dispose();
		return image;
	}
}