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
package vulc.bitmap.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import vulc.bitmap.Bitmap;
import vulc.bitmap.BoolBitmap;

/**
 * Font class creates a font charset by a LinkFont file.
 * @author Vulcalien
 */
public class Font {

	protected int chars;
	protected int height;
	protected boolean monospaced;

	protected Bitmap<Boolean>[] imgs;
	protected int letterSpacing;

	public Font(InputStream in) {
		init(in);
	}

	public Font(File file) {
		try {
			init(new FileInputStream(file));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Font(String file) {
		this(new File(file));
	}

	protected Font() {
	}

	protected void init(InputStream in) {
		try {
			byte[] info = new byte[9];
			in.read(info);

			this.chars = (info[0] & 0xff) << 24 | (info[1] & 0xff) << 16 | (info[2] & 0xff) << 8 | info[3] & 0xff;
			this.letterSpacing =
			        (info[4] & 0xff) << 24 | (info[5] & 0xff) << 16 | (info[6] & 0xff) << 8 | info[7] & 0xff;
			this.height = info[8] & 0xff;

			this.imgs = new BoolBitmap[chars];
			monospaced = true;
			for(int i = 0; i < chars; i++) {
				int width = in.read();

				if(monospaced && i != 0) {
					monospaced = width == imgs[0].width;
				}

				Bitmap<Boolean> img = new BoolBitmap(width, height);
				imgs[i] = img;

				byte[] pixels = new byte[width * height];
				in.read(pixels);
				for(int p = 0; p < pixels.length; p++) {
					int color = pixels[p] & 0xff;

					if(color == 0xff) img.pixels[p] = true;
					else img.pixels[p] = false;
				}
			}
			in.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Font getScaled(int xScale, int yScale) {
		Font font = new Font();
		font.chars = chars;
		font.letterSpacing = letterSpacing * xScale;
		font.height = height * yScale;

		font.imgs = new BoolBitmap[chars];
		for(int i = 0; i < chars; i++) {
			Bitmap<Boolean> img = imgs[i];
			font.imgs[i] = img.getScaled(xScale, yScale);
		}
		return font;
	}

	public Font getScaled(int scale) {
		return getScaled(scale, scale);
	}

	public void write(String text, int color, Bitmap<Integer> bitmap, int x, int y) {
		int offset = x;
		for(int i = 0; i < text.length(); i++) {
			int code = text.charAt(i) - 32;

			Bitmap<Boolean> img = imgs[code];
			bitmap.drawBool(img, color, offset, y);

			offset += img.width + letterSpacing;
		}
	}

	public void write(String text, Bitmap<Integer> bitmap, int x, int y) {
		this.write(text, 0x000000, bitmap, x, y);
	}

	public void writeToByte(String text, byte color, Bitmap<Byte> bitmap, int x, int y) {
		int offset = x;
		for(int i = 0; i < text.length(); i++) {
			int code = text.charAt(i) - 32;

			Bitmap<Boolean> img = imgs[code];
			bitmap.drawBool(img, color, offset, y);

			offset += img.width + letterSpacing;
		}
	}

	public void writeToBool(String text, boolean color, Bitmap<Boolean> bitmap, int x, int y) {
		int offset = x;
		for(int i = 0; i < text.length(); i++) {
			int code = text.charAt(i) - 32;

			Bitmap<Boolean> img = imgs[code];
			bitmap.drawBool(img, color, offset, y);

			offset += img.width + letterSpacing;
		}
	}

	public int lengthOf(String text) {
		if(text.length() == 0) return 0;

		int width = 0;
		for(int i = 0; i < text.length(); i++) {
			int code = text.charAt(i) - 32;
			width += imgs[code].width + letterSpacing;
		}
		return width - letterSpacing;
	}

	public int lengthOf(char character) {
		return imgs[character - 32].width;
	}

	public void setLetterSpacing(int spacing) {
		letterSpacing = spacing;
	}

	public int getLetterSpacing() {
		return letterSpacing;
	}

	public int getNumberOfChars() {
		return chars;
	}

	public int getHeight() {
		return height;
	}

	public boolean isMonospaced() {
		return monospaced;
	}

}
