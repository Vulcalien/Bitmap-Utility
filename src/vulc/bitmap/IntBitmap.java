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

	public IntBitmap(int width, int height, Integer color) {
		super(Integer.class, width, height);
		clear(color);
	}

	public IntBitmap(int width, int height) {
		this(width, height, 0);
	}

	public IntBitmap(BufferedImage img) {
		this(img.getWidth(), img.getHeight());

		int[] buffer = new int[width * height];
		img.getRGB(0, 0, width, height, buffer, 0, width);

		for(int i = 0; i < buffer.length; i++) {
			int color = buffer[i] & 0xffffff;
			raster.setPixel(i, color);
		}
	}

	public void setPixel(int x, int y, Integer color, int transparency) {
		int oldColor = getPixel(x, y);
		setPixel(x, y, compositColors(color, oldColor, transparency));
	}

	public void drawByte(Bitmap<Byte> image, Integer color, int transparency, int x, int y) {
		transparency &= 0xff;

		for(int yi = 0; yi < image.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			for(int xi = 0; xi < image.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				int alpha = Byte.toUnsignedInt(image.getPixel(xi, yi));
				setPixel(xPix, yPix, color, alpha * transparency / 0xff);
			}
		}
	}

	protected int compositColors(int newColor, int oldColor, int transparency) {
		int r0 = (newColor >> 16) & 0xff;
		int g0 = (newColor >> 8) & 0xff;
		int b0 = newColor & 0xff;

		int r1 = (oldColor >> 16) & 0xff;
		int g1 = (oldColor >> 8) & 0xff;
		int b1 = oldColor & 0xff;

		int r = (r0 * transparency + r1 * (0xff - transparency)) / 0xff;
		int g = (g0 * transparency + g1 * (0xff - transparency)) / 0xff;
		int b = (b0 * transparency + b1 * (0xff - transparency)) / 0xff;

		return r << 16 | g << 8 | b;
	}

}
