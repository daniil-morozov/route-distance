package utils.gpx;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.WayPoint;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Mappers {

    private static final String COMMA = ",";
    private static final DateTimeFormatter originalFormatter = DateTimeFormatter
        .ofPattern("yyyy/MM/dd H:mm:ss");

    /**
     * Map a gpx point into a way point with lat/lon/date parameters
     *
     * @param point gpx point
     * @return WayPoint
     */
    public static WayPoint mapToWayPointFromGpx(GpxPoint point) {
        Objects.requireNonNull(point);
        return WayPoint.of(
            Latitude.ofDegrees(point.getLat()),
            Longitude.ofDegrees(point.getLon()),
            ZonedDateTime.of(point.getTimestamp(), ZoneId.systemDefault())
        );
    }

    /**
     * Map a csv line into a gpx point with lat/lon/date parameters
     *
     * @param line csv line
     * @return Gpx point
     */
    public static GpxPoint mapToGpxFromCsv(String line) {
        String[] elements = line.split(COMMA);

        if (elements.length == 3) {
            return GpxPoint.of(
                Double.parseDouble(elements[0]),
                Double.parseDouble(elements[1]),
                parseLocalDateTime(elements[2].trim()));
        }

        return GpxPoint.of(0F, 0F, LocalDateTime.now());
    }

    private static LocalDateTime parseLocalDateTime(String input) {
        return LocalDateTime.parse(input.trim(), originalFormatter);
    }
}
