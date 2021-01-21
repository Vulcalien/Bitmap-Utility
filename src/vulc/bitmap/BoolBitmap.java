package vulc.bitmap;

import java.awt.image.BufferedImage;

import vulc.bitmap.raster.BoolRaster;

public class BoolBitmap extends Bitmap<Boolean> {

	public BoolBitmap(int width, int height) {
		super(Boolean.class, new BoolRaster(width, height));
	}

	public BoolBitmap(int width, int height, boolean[] raster) {
		super(Boolean.class, new BoolRaster(width, height, raster));
	}

	public BoolBitmap(int width, int height, Boolean color) {
		this(width, height);
		clear(color);
	}

	public BoolBitmap(BufferedImage img, int trueColor) {
		this(img.getWidth(), img.getHeight());

		int[] buffer = new int[width * height];
		img.getRGB(0, 0, width, height, buffer, 0, width);

		for(int i = 0; i < buffer.length; i++) {
			int color = buffer[i] & 0xffffff;
			raster.setPixel(i, color == trueColor);
		}
	}

	public BoolBitmap(Bitmap<Integer> bitmap, int trueColor) {
		this(bitmap.width, bitmap.height);

		for(int i = 0; i < bitmap.size(); i++) {
			int color = bitmap.raster.getPixel(i);
			raster.setPixel(i, color == trueColor);
		}
	}

}
