package vulc.bitmap.raster;

public class BoolRaster extends Raster<Boolean> {

	private final boolean[] pixels;

	public BoolRaster(int width, int height) {
		pixels = new boolean[width * height];
	}

	public void setPixel(int i, Boolean color) {
		pixels[i] = color;
	}

	public Boolean getPixel(int i) {
		return pixels[i];
	}

}
