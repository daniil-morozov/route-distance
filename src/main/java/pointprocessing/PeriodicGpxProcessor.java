package pointprocessing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import utils.gpx.GpxPoint;

/**
 * Periodic gpx processor. Starts a scheduler with a specified processing task and sets the task
 * interval depending on the interval calculation strategy. Interval may not be less than a second
 */
public class PeriodicGpxProcessor {

    private final LinkedList<GpxPoint> queue;
    private final long periodSeconds;
    private final ScheduledExecutorService executorService =
        Executors.newSingleThreadScheduledExecutor();
    private LocalDateTime latestTick;
    private final Consumer<GpxPoint> action;
    private State state = State.READY;
    private ScheduledFuture start;


    public enum State {
        READY,
        RUNNING,
        FINISHED
    }

    private PeriodicGpxProcessor(
        List<GpxPoint> points,
        Consumer<GpxPoint> action,
        PeriodCalculationStrategy strategy) {
        Objects.requireNonNull(points);
        Objects.requireNonNull(action);
        Objects.requireNonNull(strategy);

        queue = new LinkedList<>(points);
        this.action = action;

        if (points.size() == 0) {
            periodSeconds = 0;
        } else {
            periodSeconds = strategy.calculate(points);
            latestTick = points.get(0).getTimestamp();
        }
    }

    /**
     * Create with averaged interval for point timestamps For example with sequence of [00:00:00,
     * 00:00:05, 00:00:10, 00:00:15] the interval will be 5 seconds
     *
     * @param points points list
     * @param action action to do with every point
     * @return instance
     */
    public static PeriodicGpxProcessor createWithAverageInterval(
        List<GpxPoint> points,
        Consumer<GpxPoint> action) {
        return new PeriodicGpxProcessor(points, action, PeriodCalculationStrategy.AVERAGE_INTERVAL);
    }

    public void start() {
        if (state == State.READY && periodSeconds != 0) {
            state = State.RUNNING;
            doWork();
        }
    }

    public long getPeriodSeconds() {
        return periodSeconds;
    }

    public State getState() {
        return state;
    }

    private void doWork() {
        start = executorService.scheduleAtFixedRate(
            task(), periodSeconds, periodSeconds, TimeUnit.SECONDS);
    }

    private Runnable task() {
        return () -> {
            long secondsLeft = periodSeconds;
            while (!queue.isEmpty()) {
                GpxPoint point = queue.peek();

                if (point != null) {
                    long between = point.getTimeDiff(ChronoUnit.SECONDS, latestTick);

                    if (between > secondsLeft) {
                        break;
                    }
                    queue.pop();
                    action.accept(point);
                    secondsLeft -= between;
                }
            }

            if (queue.isEmpty()) {
                start.cancel(true);
                state = State.FINISHED;
            }
            latestTick = latestTick.plusSeconds(periodSeconds);
        };
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
