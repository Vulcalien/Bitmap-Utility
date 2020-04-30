package vulc.bitmap;

public class ByteBitmap extends Bitmap<Byte> {

	public ByteBitmap(int width, int height) {
		super(Byte.class, width, height);
	}

	public ByteBitmap(int width, int height, Byte color) {
		this(width, height);
		clear(color);
	}

}
