package vulc.util;

import vulc.bitmap.Bitmap;

/**
 * Geometry allows to draw some geometry shapes in a Bitmap.
 * @author Vulcalien
 */
public abstract class Geometry {

	public static <T> void drawLine(Bitmap<T> bitmap, T color, int x0, int y0, int x1, int y1) {
		int xd = x1 - x0;
		int yd = y1 - y0;
		double d = Math.sqrt(xd * xd + yd * yd);

		double angle = Math.atan2(yd, xd);

		for(int i = 0; i <= d; i++) {
			int xPix = (int) (x0 + i * Math.cos(angle) + 0.5);
			int yPix = (int) (y0 + i * Math.sin(angle) + 0.5);

			if(!(xPix < 0 || xPix >= bitmap.width || yPix < 0 || yPix >= bitmap.height)) {
				bitmap.setPixel(xPix, yPix, color);
			}
		}
	}

	protected static <T> void drawCircle(Bitmap<T> bitmap, T color, int xc, int yc, int radius, boolean fill) {
		for(int y = -radius; y <= radius; y++) {
			int yPix = yc + y;
			if(yPix < 0 || yPix >= bitmap.height) continue;

			for(int x = -radius; x <= radius; x++) {
				int xPix = xc + x;
				if(xPix < 0 || xPix >= bitmap.width) continue;

				int distance = (int) Math.round(Math.sqrt(x * x + y * y));
				if(fill) {
					if(distance > radius) continue;
				} else {
					if(distance != radius) continue;
				}
				bitmap.setPixel(xPix, yPix, color);
			}
		}
	}

	public static <T> void drawCircle(Bitmap<T> bitmap, T color, int xc, int yc, int radius) {
		drawCircle(bitmap, color, xc, yc, radius, false);
	}

	public static <T> void fillCircle(Bitmap<T> bitmap, T color, int xc, int yc, int radius) {
		drawCircle(bitmap, color, xc, yc, radius, true);
	}

}
