package vulc.bitmap;

import vulc.bitmap.raster.ByteRaster;

public class ByteBitmap extends Bitmap<Byte> {

	public ByteBitmap(int width, int height) {
		super(Byte.class, new ByteRaster(width, height));
	}

	public ByteBitmap(int width, int height, Byte color) {
		this(width, height);
		clear(color);
	}

}
