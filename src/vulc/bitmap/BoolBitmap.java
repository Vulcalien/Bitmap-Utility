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

public class BoolBitmap extends Bitmap<Boolean> {

	public BoolBitmap(int width, int height, Boolean color) {
		super(Boolean.class, width, height);
		clear(color);
	}

	public BoolBitmap(int width, int height) {
		this(width, height, false);
	}

	public BoolBitmap(BufferedImage img, int trueColor) {
		this(img.getWidth(), img.getHeight());

		int[] buffer = new int[width * height];
		img.getRGB(0, 0, width, height, buffer, 0, width);

		for(int i = 0; i < buffer.length; i++) {
			int color = buffer[i];

			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = color & 0xff;

			color = r << 16 | g << 8 | b;

			pixels[i] = (color == trueColor);
		}
	}

	public BoolBitmap(Bitmap<Integer> bitmap, int trueColor) {
		this(bitmap.width, bitmap.height);

		for(int i = 0; i < bitmap.size(); i++) {
			int color = bitmap.pixels[i];

			pixels[i] = (color == trueColor);
		}
	}

}
