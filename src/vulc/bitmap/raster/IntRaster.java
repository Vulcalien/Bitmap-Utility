package vulc.bitmap.raster;

public class IntRaster extends Raster<Integer> {

	public final int[] pixels;

	public IntRaster(int width, int height) {
		pixels = new int[width * height];
	}

	public void setPixel(int i, Integer color) {
		pixels[i] = color;
	}

	public Integer getPixel(int i) {
		return pixels[i];
	}

}
