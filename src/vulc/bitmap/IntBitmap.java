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

public class IntBitmap extends Bitmap<Integer> {

	protected int[] transparentColors = new int[0];

	public IntBitmap(int width, int height) {
		super(Integer.class, width, height);
	}

	public IntBitmap(int width, int height, int color) {
		this(width, height);
		clear(color);
	}

	public IntBitmap(BufferedImage img) {
		this(img.getWidth(), img.getHeight());

		int[] buffer = new int[width * height];
		img.getRGB(0, 0, width, height, buffer, 0, width);

		for(int i = 0; i < buffer.length; i++) {
			int color = buffer[i];

			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = color & 0xff;

			pixels[i] = r << 16 | g << 8 | b;
		}
	}

	public void setTransparent(int... colors) {
		this.transparentColors = colors;
	}

	public void draw(Bitmap<Integer> bitmap, int x, int y) {
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

	public void draw(Bitmap<Boolean> bitmap, int color, int x, int y) {
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

}
