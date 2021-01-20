package vulc.bitmap.raster;

public abstract class Raster<T> {

	public final int width, height;

	public Raster(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public abstract void setPixel(int i, T color);

	public abstract T getPixel(int i);

}
