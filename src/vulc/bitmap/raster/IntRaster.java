package vulc.bitmap.raster;

public class IntRaster extends Raster<Integer> {

	private final int[] pixels;

	public IntRaster(int width, int height, int[] pixels) {
		super(width, height);
		this.pixels = pixels;
	}

	public IntRaster(int width, int height) {
		this(width, height, new int[width * height]);
	}

	public void setPixel(int i, Integer color) {
		pixels[i] = color;
	}

	public Integer getPixel(int i) {
		return pixels[i];
	}

}
