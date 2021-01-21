package vulc.bitmap.raster;

public class BoolRaster extends Raster<Boolean> {

	private final boolean[] pixels;

	public BoolRaster(int width, int height, boolean[] pixels) {
		super(width, height);
		this.pixels = pixels;

		if(width * height != pixels.length) throw new IllegalArgumentException("Raster size != width * height");
	}

	public BoolRaster(int width, int height) {
		this(width, height, new boolean[width * height]);
	}

	public void setPixel(int i, Boolean color) {
		pixels[i] = color;
	}

	public Boolean getPixel(int i) {
		return pixels[i];
	}

}
