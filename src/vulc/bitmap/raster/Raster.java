package vulc.bitmap.raster;

public abstract class Raster<T> {

	public abstract void setPixel(int i, T color);

	public abstract T getPixel(int i);

}
