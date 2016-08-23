package com.akefirad.spacesaving.stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;

/**
 * Base class for streams, implementing general functionality.
 *
 * @param <T> type of element (token)
 */
public abstract class AbstractSimpleStream<T> implements SimpleStream<T> {
	private final Map<T, Integer> frequencies;

	public AbstractSimpleStream() {
		this.frequencies = new HashMap<>();
	}

	@Override
	public Map<T, Integer> frequencies() {
		return unmodifiableMap(frequencies);
	}

	@Override
	public Map<T, Integer> sortedFrequencies() {
		return unmodifiableMap(frequencies.entrySet().stream()
				.sorted(Map.Entry.<T, Integer>comparingByValue().reversed())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new)));
	}

	@Override
	public Map<T, Integer> frequencies(Set<T> alphabets) {
		return unmodifiableMap(frequencies.entrySet().stream()
				.filter(map -> alphabets.contains(map.getKey()))
				.collect(toMap(Entry::getKey, Entry::getValue)));
	}

	@Override
	public T next() throws IOException {
		T next = doNext();
		if (next != null) {
			Integer count = frequencies.get(next);
			frequencies.put(next, count == null ? 1 : count + 1);
		}
		return next;
	}

	protected abstract T doNext() throws IOException;
}

