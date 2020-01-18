package vulc.bitmap;

public class ByteBitmap extends Bitmap<Byte> {

	public ByteBitmap(int width, int height, Byte color) {
		super(Byte.class, width, height);
		clear(color);
	}

	public ByteBitmap(int width, int height) {
		this(width, height, (byte) 0);
	}

}
