package com.akefirad.spacesaving;

import com.akefirad.spacesaving.stream.CharacterStream;
import com.akefirad.spacesaving.stream.SimpleStream;
import com.akefirad.spacesaving.summary.SimpleStreamSummary;
import com.akefirad.spacesaving.summary.StreamSummary;
import com.akefirad.spacesaving.summary.SummaryElement;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Thread.currentThread;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

public class TestSimpleStreamSummary {
	private static final Logger logger = getLogger(TestSimpleStreamSummary.class);

	private static final String SHORT_TEXT = "random-and-short.txt";
	private static final String LONG_TEXT = "crime-and-punishment.txt";
	private static final int SIZE = 20;

	private static SimpleStream<Character> stream;
	private static StreamSummary<Character> summary;

	private static Map<Character, Integer> realFrequencies;
	private static Map<SummaryElement<Character>, Integer> calculatedFrequencies;
	private static Set<Character> countedElements;
	private static Set<Character> uncountedElements;
	private static long length = 0;
	private static int minCount = 0;
	private static int maxError = 0;

	@BeforeClass
	public static void setup() {
		summary = new SimpleStreamSummary<>(SIZE + 1);
		ClassLoader ccl = currentThread().getContextClassLoader();
		InputStream input = ofNullable(ccl.getResourceAsStream(LONG_TEXT))
				.orElseThrow(NullPointerException::new);
		try {
			stream = new CharacterStream(new InputStreamReader(input));
			Character character;
			while ((character = stream.next()) != null) {
				summary.add(character);
				++length;
			}

			realFrequencies = stream.sortedFrequencies();
			calculatedFrequencies = summary.sortedFrequencies();

			logger.info("{} elements have been read from the stream.", length);
			logger.info("{} distinct elements have been found.", stream.frequencies().size());

			logger.info("stream:\n{}", realFrequencies.entrySet().stream().collect(toList()));
			logger.info("summary:\n{}", calculatedFrequencies.entrySet().stream().collect(toList()));

			List<Integer> counters = new ArrayList<>(summary.frequencies().values());
			Collections.sort(counters);
			minCount = counters.get(0);

			maxError = calculatedFrequencies.keySet().stream()
					.sorted((o1, o2) -> o2.error() - o1.error())
					.findFirst()
					.orElseThrow(AssertionError::new).error();

			countedElements = calculatedFrequencies.keySet().stream()
					.map(SummaryElement::get)
					.collect(toSet());

			uncountedElements = new HashSet<>(stream.frequencies().keySet());
			uncountedElements.removeAll(countedElements);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Test
	public void testSmallestCounter() {
		logger.info("Verifying the smallest counter...");
		assertThat((long) minCount, lessThanOrEqualTo((length / SIZE)));
	}

	@Test
	public void testLargestError() {
		logger.info("Verifying the largest error...");
		assertThat(maxError, lessThanOrEqualTo(minCount));
	}

	@Test
	public void testCountersVsRanks() {
		logger.info("Verifying counters vs. ranks...");
		List<Integer> ranks = new ArrayList<>(realFrequencies.values());
		List<Integer> counters = new ArrayList<>(calculatedFrequencies.values());
		range(0, counters.size()).forEach(idx ->
				assertThat(counters.get(idx), greaterThanOrEqualTo(ranks.get(idx))));
	}

	@Test
	public void testUncountedElements() {
		logger.info("Verifying the uncounted elements...");
		stream.frequencies(uncountedElements)
				.forEach((element, count) -> {
					logger.debug("testUncountedElements >>> element: {}, count: {}", element, count);
					assertThat(count, lessThan(minCount));
				});
	}

	@Test
	public void testCountedElements() {
		logger.info("Verifying the counted elements...");
		stream.frequencies().entrySet().stream()
				.filter(entry -> entry.getValue() > (length / SIZE))
				.forEach(entry -> {
					logger.debug("testCountedElements >>> element: {}, count: {}", entry.getKey(), entry.getValue());
					assertTrue(countedElements.contains(entry.getKey()));
				});
	}

	@Test
	public void testMostFrequents() {
		logger.info("Verifying the most frequent elements...");
		Set<Character> actual = summary.mostFrequents().keySet().stream()
				.map(SummaryElement::get)
				.collect(toSet());

		Set<Character> expected = new HashSet<>(new ArrayList<>(stream.sortedFrequencies().keySet())
				.subList(0, actual.size()));

		assertThat(actual, equalTo(expected));
	}
}
