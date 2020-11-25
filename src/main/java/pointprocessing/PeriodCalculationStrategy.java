package pointprocessing;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import utils.gpx.GpxPoint;

/**
 * Period calculation strategy. Default is averaged interval
 */
public enum PeriodCalculationStrategy {
    AVERAGE_INTERVAL(PeriodCalculationStrategy::getAvgInterval);

    private Function<List<GpxPoint>, Long> strategy;

    PeriodCalculationStrategy(Function<List<GpxPoint>, Long> strategy) {
        this.strategy = strategy;
    }

    public long calculate(List<GpxPoint> trackables) {
        return strategy.apply(trackables);
    }

    private static Long getAvgInterval(List<GpxPoint> trackables) {
        int size = trackables.size();

        if (size == 0) {
            return 0L;
        }

        long totalDifferenceSeconds = ChronoUnit.SECONDS.between(
            trackables.get(0).getTimestamp(),
            trackables.get(trackables.size() - 1).getTimestamp());

        return totalDifferenceSeconds < size ? 1 : totalDifferenceSeconds / size;
    }
}
