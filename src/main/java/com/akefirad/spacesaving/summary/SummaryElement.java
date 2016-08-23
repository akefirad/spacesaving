package com.akefirad.spacesaving.summary;

/**
 * A domain model to maintain stream summary information:
 * element (token) and error (overestimation)
 *
 * @param <T> type of stream token
 */
public class SummaryElement<T> {
	private final T element;
	private final int error;

	public SummaryElement(T element) {
		this(element, 0);
	}

	public SummaryElement(T element, int error) {
		this.element = element;
		this.error = error;
	}

	public T get() {
		return element;
	}

	public int error() {
		return error;
	}

	@Override
	public boolean equals(Object o) {
		return (this == o) ||
				(o != null && getClass() == o.getClass() &&
						element.equals(((SummaryElement<?>) o).element));
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public String toString() {
		return element + ":" + error;
	}
}
