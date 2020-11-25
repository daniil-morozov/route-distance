package utils.tracking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface Trackable {
    LocalDateTime getTimestamp();

    default long getTimeDiff(ChronoUnit unit, LocalDateTime startTime) {
        return unit.between(startTime, getTimestamp());
    }
}
