import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import kafka.consumer.TotalDistanceConsumer;
import kafka.consumer.WindowedConsumer;
import kafka.producer.GpxKafkaProducer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.StreamsConfig;
import pointprocessing.PeriodicGpxProcessor;
import utils.Constants;
import utils.gpx.GpxPoint;
import utils.gpx.GpxStreamReaderImpl;
import utils.gpx.Mappers;

public class Main {
    public static void main(String[] args) {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(
                "cycle_gpx.csv");

        List<GpxPoint> points = GpxStreamReaderImpl
            .ofMapper(Mappers::mapToGpxFromCsv)
            .readAll(resourceAsStream);

        PeriodicGpxProcessor processor = PeriodicGpxProcessor
            .createWithAverageInterval(
                points,
                GpxKafkaProducer.createSampleProducer(getProducerProps())::send);
        processor.start();

        WindowedConsumer.start(getWindowedConsumerProps());
        TotalDistanceConsumer.start(getTotalConsumerProps());
    }

    private static Properties getWindowedConsumerProps() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, Constants.WINDOWED_CONSUMER_GROUP);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        return props;
    }

    private static Properties getTotalConsumerProps() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, Constants.TOTAL_CONSUMER_GROUP);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        return props;
    }

    private static Properties getProducerProps() {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
            "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            "org.springframework.kafka.support.serializer.JsonSerializer");

        return props;
    }
}
