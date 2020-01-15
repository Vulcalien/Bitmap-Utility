package vulc.bitmap.font.charset;

import java.io.DataInputStream;
import java.io.IOException;

import vulc.bitmap.Bitmap;
import vulc.bitmap.BoolBitmap;
import vulc.bitmap.font.Font;

public class BoolCharset extends Charset {

	private final Bitmap<Boolean>[] imgs;

	public BoolCharset(int chars) {
		this.imgs = new BoolBitmap[chars];
	}

	public int size() {
		return imgs.length;
	}

	// version: 1
	public void load(Font font, DataInputStream in) throws IOException {
		int height = font.getHeight();
		isMonospaced = true;

		for(int i = 0; i < imgs.length; i++) {
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

			if(isMonospaced) {
				isMonospaced = (width == imgs[0].width);
			}
		}
	}

	public <T> void draw(Bitmap<T> bitmap, int charCode, T color, int transparency, int x, int y) {
		bitmap.drawBool(imgs[charCode], color, transparency, x, y);
	}

	public Charset getScaled(int xScale, int yScale) {
		BoolCharset result = new BoolCharset(imgs.length);
		result.isMonospaced = this.isMonospaced;

		for(int i = 0; i < imgs.length; i++) {
			Bitmap<Boolean> img = imgs[i];
			result.imgs[i] = img.getScaled(xScale, yScale);
		}
		return result;
	}

	public int widthOf(int charCode) {
		return imgs[charCode].width;
	}

	private static boolean[] bytesToBits(byte[] bytes) {
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
