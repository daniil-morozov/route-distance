package kafka.consumer;

import io.jenetics.jpx.WayPoint;
import java.time.Duration;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.kafka.support.serializer.JsonSerde;
import utils.Constants;
import utils.Formatters;
import utils.gpx.DistanceCalculator;
import utils.gpx.GpxPoint;
import utils.gpx.Mappers;

/**
 * Consumes total distance per minute and prints it to console
 */
public class WindowedConsumer {

    public static void start(Properties props) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, GpxPoint> windowedDistance = builder
            .stream(Constants.TOPIC, Consumed.with(
                Serdes.String(),
                new JsonSerde<>(GpxPoint.class)
            ));

        windowedDistance
            .mapValues(Mappers::mapToWayPointFromGpx)
            .groupByKey()
            .windowedBy(
                TimeWindows.of(Duration.ofMinutes(1L)).grace(Duration.ZERO)
            )
            .aggregate(
                WindowedConsumer::init,
                WindowedConsumer::agg,
                Materialized.with(Serdes.String(), new JsonSerde<>(DistanceTotal.class))
            )
            .suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
            .toStream()
            .foreach((key, value) -> System.out.println(Formatters.formatWindowed(value)));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

    private static DistanceTotal init() {
        return DistanceTotal.createInitial();
    }

    private static DistanceTotal agg(String id, WayPoint point, DistanceTotal distanceTotal) {
        double aggregatedDistance = distanceTotal.getDistance();
        double distance = distanceTotal.isInit() ?
            DistanceCalculator.distanceByGeoid(
                point,
                WayPoint.of(distanceTotal.getLat(), distanceTotal.getLon())) : 0;

        return DistanceTotal.createAggregated(
            point.getLatitude().doubleValue(),
            point.getLongitude().doubleValue(),
            aggregatedDistance + distance,
            point.getTime().get());
    }
}
