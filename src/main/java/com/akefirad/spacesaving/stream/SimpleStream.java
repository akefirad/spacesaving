package com.akefirad.spacesaving.stream;

import java.util.Map;
import java.util.Set;

/**
 * Base interface for simple stream.
 * A simple stream maintains the information about the frequencies of elements.
 * This type of stream is useful for development and testing.
 *
 * @param <T> type of element (token)
 */
public interface SimpleStream<T> extends Stream<T> {
	/**
	 * Returns the captured frequencies (unsorted)
	 *
	 * @return frequencies in a map containing element, error, counter
	 */
	Map<T, Integer> frequencies();

	/**
	 * Returns the captured frequencies (sorted)
	 *
	 * @return frequencies in a map containing element, error, counter
	 */
	Map<T, Integer> sortedFrequencies();

	/**
	 * Returns the most frequent elements (guaranteed, sorted)
	 *
	 * @return frequencies in a map containing element, error, counter
	 */
	Map<T, Integer> frequencies(Set<T> alphabets);
}
