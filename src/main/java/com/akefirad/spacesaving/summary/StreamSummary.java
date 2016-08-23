package com.akefirad.spacesaving.summary;

import java.util.Map;

/**
 * Base interface for stream summary implementations
 *
 * @param <T>
 */
public interface StreamSummary<T> {
	/**
	 * Add (monitor) an element (token) in to the summary
	 *
	 * @param element element (token)
	 */
	void add(T element);

	/**
	 * Returns the captured frequencies (unsorted)
	 *
	 * @return frequencies in a map containing element, error, counter
	 */
	Map<SummaryElement<T>, Integer> frequencies();

	/**
	 * Returns the captured frequencies (sorted)
	 *
	 * @return frequencies in a map containing element, error, counter
	 */
	Map<SummaryElement<T>, Integer> sortedFrequencies();

	/**
	 * Returns the most frequent elements (guaranteed, sorted)
	 *
	 * @return frequencies in a map containing element, error, counter
	 */
	Map<SummaryElement<T>, Integer> mostFrequents();
}
