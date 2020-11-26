package kafka.consumer;

import io.jenetics.jpx.WayPoint;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.kafka.support.serializer.JsonSerde;
import utils.Constants;
import utils.Formatters;
import utils.gpx.DistanceCalculator;
import utils.gpx.GpxPoint;
import utils.gpx.Mappers;

/**
 * Consumes total distance and prints every event with total distance to console
 */
public class TotalDistanceConsumer {

    public static KafkaStreams createStream(Properties props) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, GpxPoint> totalDistanceStream = builder
            .stream(Constants.TOPIC, Consumed.with(
                Serdes.String(),
                new JsonSerde<>(GpxPoint.class)
            ));

        totalDistanceStream
            .mapValues(Mappers::mapToWayPointFromGpx)
            .groupByKey()
            .aggregate(
                TotalDistanceConsumer::init,
                TotalDistanceConsumer::agg,
                Materialized.with(Serdes.String(), new JsonSerde<>(DistanceTotal.class)))
            .toStream()
            .foreach((key, value) -> System.out.println(Formatters.formatTotal(value)));

        return new KafkaStreams(builder.build(), props);
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
