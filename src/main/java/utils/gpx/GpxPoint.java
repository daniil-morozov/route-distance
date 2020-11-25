package utils.gpx;

import java.time.LocalDateTime;
import utils.tracking.Trackable;

public class GpxPoint implements Trackable {

    private final double lat;
    private final double lon;
    private final LocalDateTime timestamp;

    public GpxPoint() {
        lat = 0;
        lon = 0;
        timestamp = LocalDateTime.now();
    }

    public GpxPoint(double lat, double lon, LocalDateTime timestamp) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }

    public static GpxPoint of(double lat, double lon, LocalDateTime timestamp) {
        return new GpxPoint(lat, lon, timestamp);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
