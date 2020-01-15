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
import vulc.bitmap.font.charset.BoolCharset;
import vulc.bitmap.font.charset.Charset;

/**
 * Font class allows to write characters into a Bitmap.<br>
 * It uses a charset inside a binary file.<br>
 * <br>
 * File-version: 4
 *
 * @author Vulcalien
 */
public class Font {

	protected static final int TYPE_BOOL = 0;
	protected static final int TYPE_BYTE = 1;

	protected int fontType;
	protected int height;

	protected Charset charset;
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

			// FILE-HEADER
			this.fontType = in.readByte();

			int chars = in.readInt();
			this.height = in.readByte();

			this.letterSpacing = in.readByte();
			this.lineSpacing = in.readByte();

			// FILE-BODY
			if(fontType == TYPE_BOOL) {
				this.charset = new BoolCharset(chars);
			} else if(fontType == TYPE_BYTE) {
//				this.charset = new ByteCharset();
			}

			charset.load(this, in);

			in.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getFontType() {
		return fontType;
	}

	public int getNumberOfChars() {
		return charset.size();
	}

	public int getHeight() {
		return height;
	}

	public boolean isMonospaced() {
		return charset.isMonospaced;
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
		font.height = height * yScale;

		font.letterSpacing = letterSpacing * xScale;
		font.lineSpacing = lineSpacing * yScale;

		font.charset = charset.getScaled(xScale, yScale);
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
				int charCode = character - 32;
				charset.draw(bitmap, charCode, color, transparency, xOffset, yOffset);

				xOffset += charset.widthOf(charCode) + letterSpacing;
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
			int charCode = text.charAt(i) - 32;
			width += charset.widthOf(charCode) + letterSpacing;
		}
		return width - letterSpacing;
	}

	public int widthOf(char character) {
		return charset.widthOf(character - 32);
	}

}
