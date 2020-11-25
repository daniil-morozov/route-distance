package kafka.consumer;

import java.time.ZonedDateTime;

public class DistanceTotal {

    private final double lat;
    private final double lon;
    private final double distance;
    private final ZonedDateTime dateTime;

    public DistanceTotal() {
        this(0, 0, 0, false, ZonedDateTime.now());
    }

    public DistanceTotal(double lat, double lon, double distance, boolean init,
        ZonedDateTime dateTime) {
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
        this.init = init;
        this.dateTime = dateTime;
    }

    static DistanceTotal createInitial() {
        return new DistanceTotal(0, 0, 0);
    }

    static DistanceTotal createAggregated(
        double lat,
        double lon,
        double distance,
        ZonedDateTime dateTime) {
        return new DistanceTotal(lat, lon, distance, true, dateTime);
    }

    public boolean isInit() {
        return init;
    }

    private final boolean init;

    private DistanceTotal(double lat, double lon, double distance) {
        this(lat, lon, distance, false, ZonedDateTime.now());
    }

    public double getDistance() {
        return distance;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Aggregated{" +
            "lat=" + lat +
            ", lon=" + lon +
            ", distance=" + distance +
            ", dateTime=" + dateTime +
            '}';
    }
}
