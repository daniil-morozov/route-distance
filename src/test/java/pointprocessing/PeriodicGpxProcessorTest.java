package pointprocessing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import pointprocessing.PeriodicGpxProcessor.State;
import utils.gpx.GpxPoint;

public class PeriodicGpxProcessorTest {

    private static final List<GpxPoint> POINTS = Arrays.asList(
        GpxPoint.of(50.71677966, 6.445616148, LocalDateTime.of(2020, 11, 24, 0, 0, 0)),
        GpxPoint.of(50.71677958, 6.445615981, LocalDateTime.of(2020, 11, 24, 0, 0, 1)),
        GpxPoint.of(50.71677916, 6.44561707, LocalDateTime.of(2020, 11, 24, 0, 0, 2)),
        GpxPoint.of(50.7167582, 6.445621429, LocalDateTime.of(2020, 11, 24, 0, 0, 3)),
        GpxPoint.of(50.71676751, 6.445646156, LocalDateTime.of(2020, 11, 24, 0, 0, 4)),
        GpxPoint.of(50.71675343, 6.445663925, LocalDateTime.of(2020, 11, 24, 0, 0, 5))
    );

    @Test
    public void periodShouldBeOneSecond() {
        PeriodicGpxProcessor processor = PeriodicGpxProcessor
            .createWithAverageInterval(POINTS, PeriodicGpxProcessorTest::doNothing);
        assertThat(processor.getPeriodSeconds(), is(1L));
    }

    @Test
    public void processorShouldAddAllRecords() {
        List<GpxPoint> inserted = new ArrayList<>();

        PeriodicGpxProcessor processor = PeriodicGpxProcessor
            .createWithAverageInterval(POINTS, inserted::add);
        processor.start();
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat(inserted.size(), is(POINTS.size()));
        assertThat(processor.getState(), is(State.FINISHED));
    }

    private static void doNothing(GpxPoint point) {}
}
