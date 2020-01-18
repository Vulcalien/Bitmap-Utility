package vulc.util;

import vulc.bitmap.Bitmap;

/**
 * Geometry allows to draw some geometry shapes in a Bitmap.
 * @author Vulcalien
 */
public abstract class Geometry {

	public static void drawLine(Bitmap<Integer> bitmap, int color, int x0, int y0, int x1, int y1) {
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

	public static void drawCircle(Bitmap<Integer> bitmap, int color, int x0, int y0, int radius) {
		int d = 2 * radius;
		for(int y = 0; y <= d; y++) {
			int yPix = y0 + y;
			if(yPix < 0 || yPix >= bitmap.height) continue;
			int yDist = y - radius;

			for(int x = 0; x <= d; x++) {
				int xPix = x0 + x;
				if(xPix < 0 || xPix >= bitmap.width) continue;
				int xDist = x - radius;

				if((int) Math.sqrt(xDist * xDist + yDist * yDist) != radius) continue;

				bitmap.setPixel(xPix, yPix, color);
			}
		}
	}

}
