package com.akefirad.spacesaving;

public final class Asserts {
	private Asserts() {
	}

	public static void assertNotNull(Object object, String name) {
		if (object == null)
			throw new IllegalArgumentException(name + " is null!");
	}

	public static void assertValidSize(int size, String name) {
		if (size <= 0)
			throw new IllegalArgumentException(name + " is non-positive!");
	}
}
