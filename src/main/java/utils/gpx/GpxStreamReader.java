package utils.gpx;

import java.io.InputStream;
import java.util.List;

/**
 * Gpx stream reader
 * @param <TPoint> Coordinate type
 * @param <TStream> Stream type
 */
public interface GpxStreamReader<TPoint, TStream extends InputStream> {
    List<TPoint> readAll(TStream stream);
}
