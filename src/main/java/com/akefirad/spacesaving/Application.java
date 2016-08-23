package com.akefirad.spacesaving;

import com.akefirad.spacesaving.stream.Stream;
import com.akefirad.spacesaving.stream.StringStream;
import com.akefirad.spacesaving.summary.SimpleStreamSummary;
import com.akefirad.spacesaving.summary.StreamSummary;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

import static com.akefirad.spacesaving.Asserts.assertValidSize;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.slf4j.LoggerFactory.getLogger;

public class Application {
	private static final Logger logger = getLogger(Application.class);


	public static void main(String[] args) {
		if (args.length < 2)
			throw new IllegalArgumentException("Stream length and element count are needed!");

		int int1 = Integer.parseInt(args[0].trim());
		int int2 = Integer.parseInt(args[1].trim());

		int length = Math.max(int1, int2);
		int size = Math.min(int1, int2);
		assertValidSize(length, "stream length");
		assertValidSize(size, "element count");

		logger.info("Going to read {} elements and store {} of them...", length, size);

		StreamSummary<String> summary = new SimpleStreamSummary<>(size + 1);
		try (Stream<String> stream = new StringStream(new InputStreamReader(System.in))) {
			String string;
			while (length-- > 0 && (string = stream.next()) != null) {
				if (logger.isDebugEnabled())
					System.out.print(string + " ");
				summary.add(string);
			}

			logger.info("Read {} elements and store {} of them.", length, size);

			// Find longest element;
			Integer maxLength = summary.frequencies().keySet().stream()
					.map(element -> element.get().length())
					.max(Comparator.naturalOrder())
					.orElseThrow(AssertionError::new);

			// Find number of digits of the maximum error;
			Integer maxError = summary.frequencies().keySet().stream()
					.map(element -> valueOf(element.error()).length())
					.max(Comparator.naturalOrder())
					.orElseThrow(AssertionError::new);

			// Find number of digits of the maximum count;
			Integer maxCount = summary.frequencies().values().stream()
					.max(Comparator.naturalOrder())
					.orElseThrow(AssertionError::new)
					.toString().length();

			// Log most frequent elements
			StringBuilder sb = new StringBuilder();
			summary.frequencies().entrySet().stream()
					.sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
					.forEach(entry -> sb.append("\telement: ").append(rightPad(entry.getKey().get(), maxLength))
							.append(" count: ").append(leftPad(entry.getValue().toString(), maxCount))
							.append(", error: ").append(leftPad(valueOf(entry.getKey().error()), maxError))
							.append("\n"));
			logger.info("Most frequent elements are:\n{}", sb);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
