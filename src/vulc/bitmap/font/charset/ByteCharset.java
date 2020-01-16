package vulc.bitmap.font.charset;

import java.io.DataInputStream;
import java.io.IOException;

import vulc.bitmap.Bitmap;
import vulc.bitmap.ByteBitmap;
import vulc.bitmap.font.Font;

public class ByteCharset extends Charset {

	private final Bitmap<Byte>[] imgs;

	public ByteCharset(int chars) {
		this.imgs = new ByteBitmap[chars];
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

			Bitmap<Byte> img = new ByteBitmap(width, height);
			imgs[i] = img;

			int nPixels = width * height;
			for(int p = 0; p < nPixels; p++) {
				byte pixel = in.readByte();
				img.raster.setPixel(p, pixel);
			}

			if(isMonospaced && width != imgs[0].width) {
				isMonospaced = false;
			}
		}
	}

	public <T> void draw(Bitmap<T> bitmap, int charCode, T color, int transparency, int x, int y) {
		bitmap.drawByte(imgs[charCode], color, transparency, x, y);
	}

	public Charset getScaled(int xScale, int yScale) {
		ByteCharset result = new ByteCharset(imgs.length);
		result.isMonospaced = this.isMonospaced;

		for(int i = 0; i < imgs.length; i++) {
			Bitmap<Byte> img = imgs[i];
			result.imgs[i] = img.getScaled(xScale, yScale);
		}
		return result;
	}

	public int widthOf(int charCode) {
		return imgs[charCode].width;
	}

}
