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
 * Bitmap is a RGB image that contains the pixels as an int array.
 * @author Vulcalien
 */
public class Bitmap {

	public final int width;
	public final int height;
	public final int[] pixels;

	protected int[] transparentColors = new int[0];

	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public Bitmap(int width, int height, int color) {
		this(width, height);
		clear(color);
	}

	/**
	 * Creates a Bitmap from a BufferedImage.
	 * @param img the BufferedImage
	 */
	public Bitmap(BufferedImage img) {
		this(img.getWidth(), img.getHeight());
		img.getRGB(0, 0, width, height, pixels, 0, width);

		for(int i = 0; i < pixels.length; i++) {
			int color = pixels[i];

			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = color & 0xff;

			pixels[i] = r << 16 | g << 8 | b;
		}
	}

	/**
	 * Sets the colors that should not be drawed.
	 * @param colors the colors to avoid
	 */
	public void setTransparent(int... colors) {
		this.transparentColors = colors;
	}

	public void setPixel(int x, int y, int color) {
		pixels[x + y * width] = color;
	}

	public int getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	public void clear(int color) {
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = color;
		}
	}

	public void draw(Bitmap bitmap, int x, int y) {
		for(int yi = 0; yi < bitmap.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			x_for:
			for(int xi = 0; xi < bitmap.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				int color = bitmap.getPixel(xi, yi);
				for(int i = 0; i < transparentColors.length; i++) {
					if(color == transparentColors[i]) continue x_for;
				}
				setPixel(xPix, yPix, color);
			}
		}
	}

	public void draw(BoolBitmap bitmap, int color, int x, int y) {
		for(int yi = 0; yi < bitmap.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			for(int xi = 0; xi < bitmap.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				boolean val = bitmap.getPixel(xi, yi);
				if(val == true) setPixel(xPix, yPix, color);
			}
		}
	}

	public void fill(int x0, int y0, int x1, int y1, int color) {
		for(int y = y0; y <= y1; y++) {
			if(y < 0 || y >= height) continue;

			for(int x = x0; x <= x1; x++) {
				if(x < 0 || x >= width) continue;

				setPixel(x, y, color);
			}
		}
	}

	public Bitmap getScaled(int xScale, int yScale) {
		Bitmap result = new Bitmap(width * xScale, height * yScale);

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int color = getPixel(x, y);

				int xPix = x * xScale;
				int yPix = y * yScale;

				for(int yi = 0; yi < yScale; yi++) {
					for(int xi = 0; xi < xScale; xi++) {
						result.setPixel(xPix + xi, yPix + yi, color);
					}
				}
			}
		}
		return result;
	}

	public Bitmap getScaled(int scale) {
		return getScaled(scale, scale);
	}

	public Bitmap getScaledByDimension(int width, int height) {
		Bitmap result = new Bitmap(width, height);

		double xScale = (double) width / this.width;
		double yScale = (double) height / this.height;

		for(int y1 = 0; y1 < height; y1++) {
			int y0 = (int) (y1 / yScale);
			for(int x1 = 0; x1 < width; x1++) {
				int x0 = (int) (x1 / xScale);

				int col = getPixel(x0, y0);
				result.setPixel(x1, y1, col);
			}
		}
		return result;
	}

	public Bitmap getScaled(double xScale, double yScale) {
		return getScaledByDimension((int) (width * xScale), (int) (height * yScale));
	}

	public Bitmap getScaled(double scale) {
		return getScaled(scale, scale);
	}

	public Bitmap getSubimage(int x, int y, int width, int height) {
		Bitmap result = new Bitmap(width, height);

		for(int yi = 0; yi < height; yi++) {
			int yPix = yi + y;

			for(int xi = 0; xi < width; xi++) {
				int xPix = xi + x;

				int color = getPixel(xPix, yPix);
				result.setPixel(xi, yi, color);
			}
		}
		return result;
	}

	public Bitmap getFlipped(boolean horizontal, boolean vertical) {
		Bitmap result = new Bitmap(width, height);

		for(int y = 0; y < height; y++) {
			int yPix;
			if(vertical) yPix = height - y - 1;
			else yPix = y;

			for(int x = 0; x < width; x++) {
				int xPix;
				if(horizontal) xPix = width - x - 1;
				else xPix = x;

				int color = getPixel(x, y);
				result.setPixel(xPix, yPix, color);
			}
		}
		return result;
	}

	/**
	 * Returns a rotated image.
	 * @param rot the number of 90 degrees rotations to right
	 * @return the rotated image
	 */
	public Bitmap getRotated(int rot) {
		rot %= 4;
		if(rot < 0) rot = 4 + rot;

		Bitmap result;
		if(rot % 2 == 0) {
			result = new Bitmap(width, height);
		} else {
			result = new Bitmap(height, width);
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

				int color = getPixel(x, y);
				result.setPixel(x1, y1, color);
			}
		}
		return result;
	}

	/**
	 * Returns a rotated image.
	 * @param theta the rotation angle
	 * @param background the background color
	 * @return the rotated image
	 */
	public Bitmap getRotated(double theta, int background) {
		theta %= Math.PI * 2;
		if(theta < 0) theta = Math.PI * 2 + theta;

		double cos0 = Math.cos(-theta);
		double sin0 = Math.sin(-theta);

		int w = (int) (Math.abs(cos0 * width) + Math.abs(sin0 * height) + 0.5);
		int h = (int) (Math.abs(cos0 * height) + Math.abs(sin0 * width) + 0.5);
		Bitmap result = new Bitmap(w, h, background);

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
