package utils.gpx;

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;

/**
 * Class for distance calculations
 */
public class DistanceCalculator {
    public static double distanceByGeoid(WayPoint start, WayPoint end) {
        return Geoid.WGS84.distance(start, end).doubleValue();
    }
}
