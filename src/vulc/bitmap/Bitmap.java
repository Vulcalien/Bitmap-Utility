/*******************************************************************************
 * Copyright 2019-2020 Vulcalien
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
import vulc.bitmap.raster.Raster;

@SuppressWarnings("unchecked")
public abstract class Bitmap<T> {

	protected final Class<T> type;

	public final int width;
	public final int height;
	public final Raster<T> raster;

	protected T[] transparentColors;
	protected Font font;

	public Bitmap(Class<T> type, Raster<T> raster) {
		this.type = type;

		this.raster = raster;
		this.width = raster.width;
		this.height = raster.height;

		this.transparentColors = (T[]) Array.newInstance(type, 0);
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
		return width * height;
	}

	public T[] getTransparentColors() {
		return transparentColors;
	}

	public void setTransparent(T... colors) {
		this.transparentColors = colors;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public T getPixel(int x, int y) {
		return raster.getPixel(x + y * width);
	}

	public void setPixel(int x, int y, T color) {
		raster.setPixel(x + y * width, color);
	}

	public void setPixel(int x, int y, T color, int transparency) {
		throw new UnsupportedOperationException();
	}

	public void clear(T color) {
		int size = size();
		for(int i = 0; i < size; i++) {
			raster.setPixel(i, color);
		}
	}

	public void fill(int x0, int y0, int x1, int y1, T color, int transparency) {
		transparency &= 0xff;

		x0 = Math.max(x0, 0);
		y0 = Math.max(y0, 0);

		x1 = Math.min(x1, width - 1);
		y1 = Math.min(y1, height - 1);

		for(int y = y0; y <= y1; y++) {
			for(int x = x0; x <= x1; x++) {
				if(transparency == 0xff) {
					setPixel(x, y, color);
				} else {
					setPixel(x, y, color, transparency);
				}
			}
		}
	}

	public void fill(int x0, int y0, int x1, int y1, T color) {
		this.fill(x0, y0, x1, y1, color, 0xff);
	}

	public void draw(Bitmap<T> image, int transparency, int x, int y) {
		transparency &= 0xff;

		int xStart = Math.max(0, -x);
		int yStart = Math.max(0, -y);

		int xEnd = Math.min(image.width, width - x);
		int yEnd = Math.min(image.height, height - y);

		for(int yi = yStart; yi < yEnd; yi++) {
			int yPix = yi + y;

			x_for:
			for(int xi = xStart; xi < xEnd; xi++) {
				int xPix = xi + x;

				T color = image.getPixel(xi, yi);
				for(int i = 0; i < transparentColors.length; i++) {
					if(color.equals(transparentColors[i])) continue x_for;
				}

				if(transparency == 0xff) {
					setPixel(xPix, yPix, color);
				} else {
					setPixel(xPix, yPix, color, transparency);
				}
			}
		}
	}

	public void draw(Bitmap<T> image, int x, int y) {
		this.draw(image, 0xff, x, y);
	}

	public void drawByte(Bitmap<Byte> image, T color, int transparency, int x, int y) {
		transparency &= 0xff;

		int xStart = Math.max(0, -x);
		int yStart = Math.max(0, -y);

		int xEnd = Math.min(image.width, width - x);
		int yEnd = Math.min(image.height, height - y);

		for(int yi = yStart; yi < yEnd; yi++) {
			int yPix = yi + y;

			for(int xi = xStart; xi < xEnd; xi++) {
				int xPix = xi + x;

				int alpha = Byte.toUnsignedInt(image.getPixel(xi, yi));
				setPixel(xPix, yPix, color, alpha * transparency / 0xff);
			}
		}
	}

	public void drawByte(Bitmap<Byte> image, T color, int x, int y) {
		this.drawByte(image, color, 0xff, x, y);
	}

	public void drawBool(Bitmap<Boolean> image, T color, int transparency, int x, int y) {
		transparency &= 0xff;

		int xStart = Math.max(0, -x);
		int yStart = Math.max(0, -y);

		int xEnd = Math.min(image.width, width - x);
		int yEnd = Math.min(image.height, height - y);

		for(int yi = yStart; yi < yEnd; yi++) {
			int yPix = yi + y;

			for(int xi = xStart; xi < xEnd; xi++) {
				int xPix = xi + x;

				boolean val = image.getPixel(xi, yi);
				if(val == true) {
					if(transparency == 0xff) {
						setPixel(xPix, yPix, color);
					} else {
						setPixel(xPix, yPix, color, transparency);
					}
				}
			}
		}
	}

	public void drawBool(Bitmap<Boolean> image, T color, int x, int y) {
		this.drawBool(image, color, 0xff, x, y);
	}

	public void write(String text, T color, int transparency, int x, int y) {
		font.write(this, text, color, transparency, x, y);
	}

	public void write(String text, T color, int x, int y) {
		font.write(this, text, color, x, y);
	}

	public Bitmap<T> getCopy() {
		Bitmap<T> copy = getSameTypeInstance(width, height);

		int size = size();
		for(int i = 0; i < size; i++) {
			copy.raster.setPixel(i, raster.getPixel(i));
		}
		return copy;
	}

	public Bitmap<T> getScaled(int xScale, int yScale) {
		Bitmap<T> result = getSameTypeInstance(width * xScale, height * yScale);

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				T color = getPixel(x, y);

				for(int yi = 0; yi < yScale; yi++) {
					for(int xi = 0; xi < xScale; xi++) {
						result.setPixel(x * xScale + xi, y * yScale + yi, color);
					}
				}
			}
		}
		return result;
	}

	public Bitmap<T> getScaled(int scale) {
		return getScaled(scale, scale);
	}

	// /!\ floating point /!\
	public Bitmap<T> fGetScaledByDimension(int width, int height) {
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

	// /!\ floating point /!\
	public Bitmap<T> fGetScaled(double xScale, double yScale) {
		return fGetScaledByDimension((int) (width * xScale), (int) (height * yScale));
	}

	// /!\ floating point /!\
	public Bitmap<T> fGetScaled(double scale) {
		return fGetScaled(scale, scale);
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
			int yPix = vertical ? height - y - 1 : y;

			for(int x = 0; x < width; x++) {
				int xPix = horizontal ? width - x - 1 : x;

				T color = getPixel(x, y);
				result.setPixel(xPix, yPix, color);
			}
		}
		return result;
	}

	public Bitmap<T> getRotated(int rot) {
		rot &= 3;

		Bitmap<T> result;
		if((rot & 1) == 0) {
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

	// /!\ floating point /!\
	public Bitmap<T> fGetRotated(double theta, T background) {
		theta %= Math.PI * 2;
		if(theta < 0) theta = Math.PI * 2 + theta;

		double cos = Math.cos(-theta);
		double sin = Math.sin(-theta);

		int w = (int) Math.round(Math.abs(cos * width) + Math.abs(sin * height));
		int h = (int) Math.round(Math.abs(cos * height) + Math.abs(sin * width));
		Bitmap<T> result = getSameTypeInstance(w, h, background);

		double cx0 = width / 2.0;
		double cy0 = height / 2.0;
		double cx1 = w / 2.0;
		double cy1 = h / 2.0;

		for(int y = 0; y < result.height; y++) {
			double yd = y - cy1;
			for(int x = 0; x < result.width; x++) {
				double xd = x - cx1;

				int x0 = (int) Math.round(xd * cos - yd * sin + cx0);
				int y0 = (int) Math.round(xd * sin + yd * cos + cy0);

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

			int size = size();
			for(int i = 0; i < size; i++) {
				if(!bitmap.raster.getPixel(i).equals(this.raster.getPixel(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
