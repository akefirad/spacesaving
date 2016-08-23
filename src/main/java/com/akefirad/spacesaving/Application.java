package com.akefirad.spacesaving;

import org.slf4j.Logger;

import java.io.*;

import static com.akefirad.spacesaving.Asserts.assertValidSize;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class Application {
    private static final Logger logger = getLogger(Application.class);

    public static void main (String[] args) {
        if (args.length < 2)
            throw new IllegalArgumentException("Stream length and element count are needed!");

        int int1 = Integer.parseInt(args[0].trim());
        int int2 = Integer.parseInt(args[1].trim());

        int length = Math.max(int1, int2);
        int size = Math.min(int1, int2);
        assertValidSize(length, "stream length");
        assertValidSize(size, "element count");

        logger.info("Going to read {} elements and store {} of them...", length, size);

        StreamSummary<String> summary = new SimpleStreamSummary<>(size);
        try (Stream<String> stream = new SystemInputStringStream()) {
            String string;
            while (size-- > 0 && (string = stream.next()) != null)
                summary.add(string);

            logger.info("most frequent elements:\n{}", summary.frequencies().entrySet().stream()
                    .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                    .map(e -> "" + e.getKey().get() + "(c:" + e.getValue() + ",e:" + e.getKey().error() + ")")
                    .collect(toList()));
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

//    public static void debug () {
//        final String SHORT_TEXT = "simple-short.txt";
//        final String LONG_TEXT = "crime-and-punishment.txt";
//        final int COUNT = 10;
//
//        StreamSummary<Character> summary = new SimpleStreamSummary<>(COUNT);
//        ClassLoader ccl = currentThread().getContextClassLoader();
//        InputStream input = ofNullable(ccl.getResourceAsStream(LONG_TEXT))
//                .orElseThrow(NullPointerException::new);
//        try (SimpleStream<Character> stream =
//                     new CharacterStream(new InputStreamReader(input))) {
//            Character character;
//            while ((character = stream.next()) != null) {
//                summary.add(character);
//            }
//
//            logger.info("expected:\n{}", stream.frequencies().entrySet().stream()
//                    .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
//                    .collect(toList()));
//            logger.info("actual:\n{}", summary.frequencies().entrySet().stream()
//                    .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
//                    .collect(toList()));
//
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
