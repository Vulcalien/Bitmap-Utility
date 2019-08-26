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

	public IntBitmap(int width, int height) {
		super(Integer.class, width, height);
		clear(0);
	}

	public IntBitmap(int width, int height, Integer color) {
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

	public void drawByte(Bitmap<Byte> image, Integer color, int x, int y) {
		int rCol = (color >> 16) & 0xff;
		int gCol = (color >> 8) & 0xff;
		int bCol = (color) & 0xff;

		for(int yi = 0; yi < image.height; yi++) {
			int yPix = yi + y;
			if(yPix < 0 || yPix >= height) continue;

			for(int xi = 0; xi < image.width; xi++) {
				int xPix = xi + x;
				if(xPix < 0 || xPix >= width) continue;

				int gradient = Byte.toUnsignedInt(image.getPixel(xi, yi));

				int r = rCol * gradient / 0xff;
				int g = gCol * gradient / 0xff;
				int b = bCol * gradient / 0xff;

				int drawColor = r << 16 | g << 8 | b;
				setPixel(xPix, yPix, drawColor);
			}
		}
	}

}
