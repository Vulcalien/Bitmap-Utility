package vulc.bitmap.raster;

public class ByteRaster extends Raster<Byte> {

	private final byte[] pixels;

	public ByteRaster(int width, int height, byte[] pixels) {
		super(width, height);
		this.pixels = pixels;

		if(width * height != pixels.length) throw new IllegalArgumentException("Raster size != width * height");
	}

	public ByteRaster(int width, int height) {
		this(width, height, new byte[width * height]);
	}

	public void setPixel(int i, Byte color) {
		pixels[i] = color;
	}

	public Byte getPixel(int i) {
		return pixels[i];
	}

}
