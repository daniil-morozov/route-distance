package utils;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import kafka.consumer.DistanceTotal;

/**
 * Formatters for kafka consumers
 */
public class Formatters {
    private static final DecimalFormat WINDOW_DISTANCE_FORMATTER = new DecimalFormat("#0.0000000");
    private static final DecimalFormat TOTAL_DISTANCE_FORMATTER = new DecimalFormat("#0.000");

    public static String formatWindowed(DistanceTotal distanceTotal) {
        return "Total distance after 1 minute (M): " + WINDOW_DISTANCE_FORMATTER.format(distanceTotal.getDistance());
    }

    public static String formatTotal(DistanceTotal distanceTotal) {
        return
            "| " + distanceTotal.getDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd H:mm:ss")) +
            " | " +
            TOTAL_DISTANCE_FORMATTER.format(distanceTotal.getDistance()) + " |";
    }
}
