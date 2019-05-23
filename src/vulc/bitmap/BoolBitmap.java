/*******************************************************************************
 * Copyright 2019 Vulcalien
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package vulc.bitmap;

import java.awt.image.BufferedImage;

/**
 * BoolBitmap is a binary image that contains the pixels as a boolean array.
 * @author Vulcalien
 */
public class BoolBitmap {

	public final int width;
	public final int height;
	public final boolean[] pixels;

	public BoolBitmap(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new boolean[width * height];
	}

	/**
	 * Creates a BoolBitmap from a BufferedImage.<br>
	 * Black pixels (0x000000) are <b>true</b> while all the other values are <b>false</b>.
	 * @param img the BufferedImage
	 */
	public BoolBitmap(BufferedImage img) {
		this(img.getWidth(), img.getHeight());
		int[] pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);

		for(int i = 0; i < pixels.length; i++) {
			int color = pixels[i];

			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = color & 0xff;

			color = r << 16 | g << 8 | b;
			if(color == 0x000000) this.pixels[i] = true;
			else this.pixels[i] = false;
		}
	}

	public void setPixel(int x, int y, boolean val) {
		pixels[x + y * width] = val;
	}

	public boolean getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	public void clear(boolean val) {
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = val;
		}
	}

	public void draw(BoolBitmap bitmap, int x, int y) {
		for(int yi = 0; yi < bitmap.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			for(int xi = 0; xi < bitmap.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				boolean val = bitmap.getPixel(xi, yi);
				setPixel(xPix, yPix, val);
			}
		}
	}

	public void fill(int x0, int y0, int x1, int y1, boolean val) {
		for(int y = y0; y <= y1; y++) {
			if(y < 0 || y >= height) continue;

			for(int x = x0; x <= x1; x++) {
				if(x < 0 || x >= width) continue;

				setPixel(x, y, val);
			}
		}
	}

	public BoolBitmap getScaled(int xScale, int yScale) {
		BoolBitmap result = new BoolBitmap(width * xScale, height * yScale);

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				boolean val = getPixel(x, y);

				int xPix = x * xScale;
				int yPix = y * yScale;

				for(int yi = 0; yi < yScale; yi++) {
					for(int xi = 0; xi < xScale; xi++) {
						result.setPixel(xPix + xi, yPix + yi, val);
					}
				}
			}
		}
		return result;
	}

	public BoolBitmap getScaled(int scale) {
		return getScaled(scale, scale);
	}

	public BoolBitmap getScaledByDimension(int width, int height) {
		BoolBitmap result = new BoolBitmap(width, height);

		double xScale = (double) width / this.width;
		double yScale = (double) height / this.height;

		for(int y1 = 0; y1 < height; y1++) {
			int y0 = (int) (y1 / yScale);
			for(int x1 = 0; x1 < width; x1++) {
				int x0 = (int) (x1 / xScale);

				boolean val = getPixel(x0, y0);
				result.setPixel(x1, y1, val);
			}
		}
		return result;
	}

	public BoolBitmap getScaled(double xScale, double yScale) {
		return getScaledByDimension((int) (width * xScale), (int) (height * yScale));
	}

	public BoolBitmap getScaled(double scale) {
		return getScaled(scale, scale);
	}

	public BoolBitmap getSubimage(int x, int y, int width, int height) {
		BoolBitmap result = new BoolBitmap(width, height);

		for(int yi = 0; yi < height; yi++) {
			int yPixel = yi + y;

			for(int xi = 0; xi < width; xi++) {
				int xPix = xi + x;

				boolean val = getPixel(xPix, yPixel);
				result.setPixel(xi, yi, val);
			}
		}
		return result;
	}

	public BoolBitmap getFlipped(boolean horizontal, boolean vertical) {
		BoolBitmap result = new BoolBitmap(width, height);

		for(int y = 0; y < height; y++) {
			int yPix;
			if(vertical) yPix = height - y - 1;
			else yPix = y;

			for(int x = 0; x < width; x++) {
				int xPix;
				if(horizontal) xPix = width - x - 1;
				else xPix = x;

				boolean val = getPixel(x, y);
				result.setPixel(xPix, yPix, val);
			}
		}
		return result;
	}

	/**
	 * Returns a rotated image.
	 * @param rot the number of 90 degrees rotations to right
	 * @return the rotated image
	 */
	public BoolBitmap getRotated(int rot) {
		rot %= 4;
		if(rot < 0) rot = 4 + rot;

		BoolBitmap result;
		if(rot % 2 == 0) {
			result = new BoolBitmap(width, height);
		} else {
			result = new BoolBitmap(height, width);
		}

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {

				int x1 = 0, y1 = 0;
				if(rot == 0) {
					x1 = x;
					y1 = y;
				} else if(rot == 1) {
					x1 = height - y - 1;
					y1 = x;
				} else if(rot == 2) {
					x1 = width - x - 1;
					y1 = height - y - 1;
				} else {
					x1 = y;
					y1 = width - x - 1;
				}

				boolean val = getPixel(x, y);
				result.setPixel(x1, y1, val);
			}
		}
		return result;
	}

	/**
	 * Returns a rotated image.
	 * @param theta the rotation angle
	 * @return the rotated image
	 */
	public BoolBitmap getRotated(double theta) {
		theta %= Math.PI * 2;
		if(theta < 0) theta = Math.PI * 2 + theta;

		double cos0 = Math.cos(-theta);
		double sin0 = Math.sin(-theta);

		int w = (int) (Math.abs(cos0 * width) + Math.abs(sin0 * height) + 0.5);
		int h = (int) (Math.abs(cos0 * height) + Math.abs(sin0 * width) + 0.5);
		BoolBitmap result = new BoolBitmap(w, h);

		double c0x = width / 2.0;
		double c0y = height / 2.0;
		double c1x = w / 2.0;
		double c1y = h / 2.0;

		for(int y = 0; y < result.height; y++) {
			double yd = y - c1y;
			for(int x = 0; x < result.width; x++) {
				double xd = x - c1x;

				int x0 = (int) (xd * cos0 - yd * sin0 + 0.5 + c0x);
				int y0 = (int) (xd * sin0 + yd * cos0 + 0.5 + c0y);

				//offset correction
				if(theta >= Math.PI / 2 && theta <= Math.PI) y0--;
				if(theta >= Math.PI && theta <= Math.PI / 2 * 3) x0--;

				if(!(x0 < 0 || x0 >= width || y0 < 0 || y0 >= height)) {
					result.setPixel(x, y, getPixel(x0, y0));
				}
			}
		}
		return result;
	}

}
