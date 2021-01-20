package vulc.bitmap;

import java.awt.image.BufferedImage;

import vulc.bitmap.raster.IntRaster;

public class IntBitmap extends Bitmap<Integer> {

	public IntBitmap(int width, int height) {
		super(Integer.class, new IntRaster(width, height));
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
			int color = buffer[i] & 0xffffff;
			raster.setPixel(i, color);
		}
	}

	public void setPixel(int x, int y, Integer color, int transparency) {
		int oldColor = getPixel(x, y);
		setPixel(x, y, compositColors(color, oldColor, transparency));
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
