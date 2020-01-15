package vulc.bitmap.font.charset;

import java.io.DataInputStream;
import java.io.IOException;

import vulc.bitmap.Bitmap;
import vulc.bitmap.font.Font;

public abstract class Charset {

	public boolean isMonospaced;

	public abstract int size();

	public abstract void load(Font font, DataInputStream input) throws IOException;

	public abstract <T> void draw(Bitmap<T> bitmap, int charCode, T color, int transparency, int x, int y);

	public abstract Charset getScaled(int xScale, int yScale);

	public abstract int widthOf(int charCode);

}
