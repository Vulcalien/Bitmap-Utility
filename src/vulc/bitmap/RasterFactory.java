package vulc.bitmap;

import vulc.bitmap.raster.BoolRaster;
import vulc.bitmap.raster.ByteRaster;
import vulc.bitmap.raster.IntRaster;
import vulc.bitmap.raster.Raster;

abstract class RasterFactory {

	@SuppressWarnings("unchecked")
	public static <T> Raster<T> getRaster(int width, int height, Class<T> type) {
		if(type == Integer.class)
		    return (Raster<T>) new IntRaster(width, height);
		else if(type == Byte.class)
		    return (Raster<T>) new ByteRaster(width, height);
		else if(type == Boolean.class)
		    return (Raster<T>) new BoolRaster(width, height);
		else
		    throw new RuntimeException("Error: unknown raster type: " + type);

	}

}
