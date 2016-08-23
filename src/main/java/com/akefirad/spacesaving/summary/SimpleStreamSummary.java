package com.akefirad.spacesaving.summary;

import org.slf4j.Logger;

import java.util.*;

import static com.akefirad.spacesaving.Asserts.assertNotNull;
import static com.akefirad.spacesaving.Asserts.assertValidSize;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Simple implementation of Space Saving algorithm.
 * Notes:
 * 1. There is a map (elements) to quickly access count of each elements.
 * 2. There is a sorted map to maintain elements with the same counts.
 * In the original paper, a doubly linked list is used. if the size of
 * stream summary is big, the operation of finding a specific counter
 * will be expensive. And also, again in the original paper, for storing
 * elements with the same counter is one place, a linked list is used.
 * Depending on the stream and alphabet size and distribution, the process
 * of finding elements and removing them (the there is no space), might
 * be expensive. In this implementation a HashSet (or LinkedHashSet) is used.
 *
 * @param <T> type of stream token
 */
public class SimpleStreamSummary<T> implements StreamSummary<T> {
	private static final Logger logger = getLogger(SimpleStreamSummary.class);

	private final int size;
	private final Map<SummaryElement<T>, Integer> elements;
	private final NavigableMap<Integer, Set<SummaryElement<T>>> counts;

	public SimpleStreamSummary(int size) {
		assertValidSize(size, "size of stream summary");
		this.size = size;
		this.elements = new HashMap<>();
		this.counts = new TreeMap<>();
	}

	@Override
	public void add(T object) {
		assertNotNull(object, "value");

		SummaryElement<T> element = new SummaryElement<>(object);
		logger.trace("Adding element {}...", element);

		Integer count = elements.get(element);
		// If it's not a new element:
		if (count != null) {
			logger.trace("Existing element. Increasing count {}...", element, count);
			assert counts.get(count).contains(element);

			Set<SummaryElement<T>> elements = counts.get(count);
			boolean removed = elements.remove(element);
			assert removed : "Expecting element '" + object + "' in the set!";

			if (elements.isEmpty()) {
				logger.trace("The set for count {} is empty. Removing it...", count);
				counts.remove(count);
			}

			doAdd(element, count + 1);
		}
		// If it's a new element, and no space is available:
		else if (elements.size() == size) {
			assert !counts.firstEntry().getValue().isEmpty();

			logger.trace("New element, but no space. Removing one element...");

			Integer minCount = counts.firstKey();
			Set<SummaryElement<T>> minElements = counts.get(minCount);
			assert minElements.size() > 0 : "Expecting non-empty set!";

			// Might be important which one should be removed:

			// Option1: Get the first element!
			SummaryElement<T> minElement = minElements.iterator().next();

			// Option2: Get the element with the biggest error!
			//SummaryElement<T> minElement = minElements.stream()
			//        .sorted((o1, o2) -> o2.error() - o1.error())
			//        .findFirst().orElseThrow(AssertionError::new);

			// Trying option2 might slightly improve the overestimation.

			logger.trace("Minimum count is {}. Removing '{}'...", minCount, minElement);

			minElements.remove(minElement);
			if (minElements.isEmpty()) {
				logger.trace("The set for count {} is empty. Removing it...", minCount);
				counts.remove(minCount);
			}

			Integer expected = elements.remove(minElement);
			assert Objects.equals(expected, minCount);

			// Ask for more information: Should the old error be added?
			doAdd(new SummaryElement<>(object, minCount), minCount + 1);
		}
		// If it's a new element, and some space is available:
		else {
			doAdd(element, 1);
		}
	}

	@Override
	public Map<SummaryElement<T>, Integer> frequencies() {
		return unmodifiableMap(elements);
	}

	@Override
	public Map<SummaryElement<T>, Integer> sortedFrequencies() {
		return unmodifiableMap(elements.entrySet().stream()
				.sorted(Map.Entry.<SummaryElement<T>, Integer>comparingByValue().reversed())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public Map<SummaryElement<T>, Integer> mostFrequents() {
		Map<SummaryElement<T>, Integer> map = new LinkedHashMap<>();
		List<Map.Entry<SummaryElement<T>, Integer>> entries = new ArrayList<>(sortedFrequencies().entrySet());
		range(0, entries.size() - 1).forEach(idx -> {
			Map.Entry<SummaryElement<T>, Integer> entry = entries.get(idx);
			if (entry.getValue() - entry.getKey().error() > entries.get(idx + 1).getValue())
				map.put(entry.getKey(), entry.getValue());
		});
		return unmodifiableMap(map);
	}

	private void doAdd(SummaryElement<T> element, int count) {
		assert element != null && count > 0;

		logger.trace("Inserting {} with count {}...", element, count);
		elements.put(element, count);
		Set<SummaryElement<T>> elements = counts.get(count);
		if (elements == null) {
			logger.trace("Create a new set for count {}...", count);
			elements = new HashSet<>(); // Should be LinkedHashSet?
			counts.put(count, elements);
		}
		elements.add(element);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		elements.forEach((element, count) -> sb.append(element.get())
				.append(":").append(count)
				.append(":").append(element.error())
				.append(", "));
		sb.append("}");
		return sb.toString();
	}
}
