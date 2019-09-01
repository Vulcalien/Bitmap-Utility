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

import java.lang.reflect.Array;

import vulc.bitmap.font.Font;

@SuppressWarnings("unchecked")
public abstract class Bitmap<T> {

	protected final Class<T> type;

	public final int width;
	public final int height;
	public final T[] pixels;

	protected T[] transparentColors;
	protected Font font;

	public Bitmap(Class<T> type, int width, int height) {
		this.type = type;

		this.width = width;
		this.height = height;
		this.pixels = (T[]) Array.newInstance(type, width * height);
		this.transparentColors = (T[]) Array.newInstance(type, 0);
	}

	public Bitmap(Class<T> type, int width, int height, T color) {
		this(type, width, height);
		clear(color);
	}

	protected Bitmap<T> getSameTypeInstance(int width, int height) {
		try {
			return getClass().getConstructor(Integer.TYPE, Integer.TYPE).newInstance(width, height);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Bitmap<T> getSameTypeInstance(int width, int height, T color) {
		try {
			return getClass().getConstructor(Integer.TYPE, Integer.TYPE, type).newInstance(width, height, color);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int size() {
		return pixels.length;
	}

	public void setTransparent(T... colors) {
		this.transparentColors = colors;
	}

	public T[] getTransparentColors() {
		return transparentColors;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public void setPixel(int x, int y, T color) {
		pixels[x + y * width] = color;
	}

	public T getPixel(int x, int y) {
		return pixels[x + y * width];
	}

	public void clear(T color) {
		for(int i = 0; i < size(); i++) {
			pixels[i] = color;
		}
	}

	public void fill(int x0, int y0, int x1, int y1, T color) {
		for(int y = y0; y <= y1; y++) {
			if(y < 0 || y >= height) continue;

			for(int x = x0; x <= x1; x++) {
				if(x < 0 || x >= width) continue;

				setPixel(x, y, color);
			}
		}
	}

	public void write(String text, T color, int x, int y) {
		font.write(this, text, color, x, y);
	}

	public void draw(Bitmap<T> image, int x, int y) {
		for(int yi = 0; yi < image.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			x_for:
			for(int xi = 0; xi < image.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				T color = image.getPixel(xi, yi);
				for(int i = 0; i < transparentColors.length; i++) {
					if(color.equals(transparentColors[i])) continue x_for;
				}
				setPixel(xPix, yPix, color);
			}
		}
	}

	public void draw(Bitmap<T> image, int transparency, int x, int y) {
		throw new UnsupportedOperationException();
	}

	public void drawByte(Bitmap<Byte> image, T color, int x, int y) {
		throw new UnsupportedOperationException();
	}

	public void drawByte(Bitmap<Byte> image, T color, int transparency, int x, int y) {
		throw new UnsupportedOperationException();
	}

	public void drawBool(Bitmap<Boolean> image, T color, int x, int y) {
		for(int yi = 0; yi < image.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			for(int xi = 0; xi < image.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				boolean val = image.getPixel(xi, yi);
				if(val == true) setPixel(xPix, yPix, color);
			}
		}
	}

	public void drawBool(Bitmap<Boolean> image, T color, int transparency, int x, int y) {
		throw new UnsupportedOperationException();
	}

	public Bitmap<T> getCopy() {
		Bitmap<T> copy = getSameTypeInstance(width, height);
		for(int i = 0; i < this.size(); i++) {
			copy.pixels[i] = this.pixels[i];
		}
		return copy;
	}

	public Bitmap<T> getScaled(int xScale, int yScale) {
		Bitmap<T> result = getSameTypeInstance(width * xScale, height * yScale);

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				T color = getPixel(x, y);

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

	public Bitmap<T> getScaled(int scale) {
		return getScaled(scale, scale);
	}

	public Bitmap<T> dGetScaledByDimension(int width, int height) {
		Bitmap<T> result = getSameTypeInstance(width, height);

		double xScale = (double) width / this.width;
		double yScale = (double) height / this.height;

		for(int y1 = 0; y1 < height; y1++) {
			int y0 = (int) (y1 / yScale);
			for(int x1 = 0; x1 < width; x1++) {
				int x0 = (int) (x1 / xScale);

				T color = getPixel(x0, y0);
				result.setPixel(x1, y1, color);
			}
		}
		return result;
	}

	public Bitmap<T> dGetScaled(double xScale, double yScale) {
		return dGetScaledByDimension((int) (width * xScale), (int) (height * yScale));
	}

	public Bitmap<T> dGetScaled(double scale) {
		return dGetScaled(scale, scale);
	}

	public Bitmap<T> getSubimage(int x, int y, int width, int height) {
		Bitmap<T> result = getSameTypeInstance(width, height);

		for(int yi = 0; yi < height; yi++) {
			int yPix = yi + y;

			for(int xi = 0; xi < width; xi++) {
				int xPix = xi + x;

				T color = getPixel(xPix, yPix);
				result.setPixel(xi, yi, color);
			}
		}
		return result;
	}

	public Bitmap<T> getFlipped(boolean horizontal, boolean vertical) {
		Bitmap<T> result = getSameTypeInstance(width, height);

		for(int y = 0; y < height; y++) {
			int yPix;
			if(vertical) yPix = height - y - 1;
			else yPix = y;

			for(int x = 0; x < width; x++) {
				int xPix;
				if(horizontal) xPix = width - x - 1;
				else xPix = x;

				T color = getPixel(x, y);
				result.setPixel(xPix, yPix, color);
			}
		}
		return result;
	}

	public Bitmap<T> getRotated(int rot) {
		rot %= 4;
		if(rot < 0) rot = 4 + rot;

		Bitmap<T> result;
		if(rot % 2 == 0) {
			result = getSameTypeInstance(width, height);
		} else {
			result = getSameTypeInstance(height, width);
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

				T color = getPixel(x, y);
				result.setPixel(x1, y1, color);
			}
		}
		return result;
	}

	public Bitmap<T> dGetRotated(double theta, T background) {
		theta %= Math.PI * 2;
		if(theta < 0) theta = Math.PI * 2 + theta;

		double cos0 = Math.cos(-theta);
		double sin0 = Math.sin(-theta);

		int w = (int) (Math.abs(cos0 * width) + Math.abs(sin0 * height) + 0.5);
		int h = (int) (Math.abs(cos0 * height) + Math.abs(sin0 * width) + 0.5);
		Bitmap<T> result = getSameTypeInstance(w, h, background);

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

				// offset correction
				if(theta >= Math.PI / 2 && theta <= Math.PI) y0--;
				if(theta >= Math.PI && theta <= Math.PI / 2 * 3) x0--;

				if(!(x0 < 0 || x0 >= width || y0 < 0 || y0 >= height)) {
					result.setPixel(x, y, getPixel(x0, y0));
				}
			}
		}
		return result;
	}

	public boolean equals(Object obj) {
		if(obj.getClass() == this.getClass()) {
			Bitmap<T> bitmap = (Bitmap<T>) obj;
			if(bitmap.width != this.width || bitmap.height != this.height) return false;

			for(int i = 0; i < bitmap.size(); i++) {
				if(!bitmap.pixels[i].equals(this.pixels[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
