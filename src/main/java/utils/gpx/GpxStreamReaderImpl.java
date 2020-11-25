package utils.gpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Gpx point stream reader
 */
public class GpxStreamReaderImpl implements GpxStreamReader<GpxPoint, InputStream> {

    private final Function<String, GpxPoint> mapper;

    private GpxStreamReaderImpl(Function<String, GpxPoint> mapper) {
        this.mapper = mapper;
    }

    /**
     * Create the reader with specified mapper
     * @param mapper mapper
     * @return instance
     */
    public static GpxStreamReaderImpl ofMapper(Function<String, GpxPoint> mapper) {
        return new GpxStreamReaderImpl(mapper);
    }

    @Override
    public List<GpxPoint> readAll(InputStream stream) {
        Objects.requireNonNull(stream);
        List<GpxPoint> points = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            points = reader.lines().map(mapper)
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println(e);
        }

        return points;
    }
}
