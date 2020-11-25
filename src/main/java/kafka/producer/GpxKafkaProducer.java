package kafka.producer;

import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import utils.Constants;
import utils.gpx.GpxPoint;

public class GpxKafkaProducer {

    private static final String KEY = UUID.randomUUID().toString();
    private final KafkaProducer<String, GpxPoint> producer;

    private GpxKafkaProducer(Properties props) {
        producer = new KafkaProducer<>(props);
    }

    public static GpxKafkaProducer createSampleProducer(Properties props) {
        return new GpxKafkaProducer(props);
    }

    @Override
    protected void finalize() {
        producer.close();
    }

    public void send(GpxPoint point) {
        producer.send(new ProducerRecord<>(
            Constants.TOPIC,
            KEY,
            point));
    }
}
