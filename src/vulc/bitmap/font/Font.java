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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import vulc.bitmap.Bitmap;
import vulc.bitmap.BoolBitmap;

/**
 * Font class allows to write characters into a Bitmap.<br>
 * It uses a charset inside a binary file.<br>
 * <br>
 * File-version: 3
 *
 * @author Vulcalien
 */
public class Font {

	protected int chars;
	protected int height;
	protected boolean monospaced;

	protected Bitmap<Boolean>[] imgs;
	protected int letterSpacing;
	protected int lineSpacing;

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

	protected void init(InputStream inputStream) {
		try {
			DataInputStream in = new DataInputStream(inputStream);

			this.chars = in.readInt();
			this.height = in.readByte();

			this.letterSpacing = in.readByte();
			this.lineSpacing = in.readByte();

			this.imgs = new BoolBitmap[chars];
			monospaced = true;

			for(int i = 0; i < chars; i++) {
				int width = in.readByte();

				Bitmap<Boolean> img = new BoolBitmap(width, height);
				imgs[i] = img;

				int nPixels = width * height;
				int nBytes = nPixels / 8 + (nPixels % 8 != 0 ? 1 : 0);

				byte[] dataBuffer = new byte[nBytes];
				in.read(dataBuffer);

				// each byte contains 8 pixels
				boolean[] pixels = bytesToBits(dataBuffer);

				// nPixels is used because there can be padding bits
				for(int p = 0; p < nPixels; p++) {
					boolean pixel = pixels[p];

					img.raster.setPixel(p, pixel);
				}

				if(monospaced) {
					monospaced = (width == imgs[0].width);
				}
			}

			in.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
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

	public void setLetterSpacing(int spacing) {
		this.letterSpacing = spacing;
	}

	public int getLetterSpacing() {
		return letterSpacing;
	}

	public void setLineSpacing(int spacing) {
		this.lineSpacing = spacing;
	}

	public int getLineSpacing() {
		return lineSpacing;
	}

	public Font getScaled(int xScale, int yScale) {
		Font font = new Font();
		font.chars = chars;
		font.height = height * yScale;

		font.letterSpacing = letterSpacing * xScale;
		font.lineSpacing = lineSpacing * yScale;

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

	public <T> void write(Bitmap<T> bitmap, String text, T color, int transparency, int x, int y) {
		int xOffset = x;
		int yOffset = y;

		for(int i = 0; i < text.length(); i++) {
			char character = text.charAt(i);

			if(character == '\n') {
				xOffset = x;
				yOffset += height + lineSpacing;
			} else {
				Bitmap<Boolean> img = imgs[character - 32];
				bitmap.drawBool(img, color, transparency, xOffset, yOffset);

				xOffset += img.width + letterSpacing;
			}
		}
	}

	public <T> void write(Bitmap<T> bitmap, String text, T color, int x, int y) {
		this.write(bitmap, text, color, 0xff, x, y);
	}

	public int widthOf(String text) {
		if(text.length() == 0) return 0;

		int width = 0;
		for(int i = 0; i < text.length(); i++) {
			int code = text.charAt(i) - 32;
			width += imgs[code].width + letterSpacing;
		}
		return width - letterSpacing;
	}

	public int widthOf(char character) {
		return imgs[character - 32].width;
	}

	protected static boolean[] bytesToBits(byte[] bytes) {
		boolean[] result = new boolean[bytes.length * 8];

		for(int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];

			for(int j = 0; j < 8; j++) {
				result[i * 8 + j] = ((b >> (7 - j)) & 1) != 0;
			}
		}
		return result;
	}

}
