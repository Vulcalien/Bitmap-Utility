package vulc.bitmap.raster;

public class ByteRaster extends Raster<Byte> {

	private final byte[] pixels;

	public ByteRaster(int width, int height) {
		pixels = new byte[width * height];
	}

	public void setPixel(int i, Byte color) {
		pixels[i] = color;
	}

	public Byte getPixel(int i) {
		return pixels[i];
	}

}
