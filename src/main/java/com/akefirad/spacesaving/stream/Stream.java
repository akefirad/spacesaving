package com.akefirad.spacesaving.stream;

import java.io.Closeable;
import java.io.IOException;

public interface Stream<T> extends Closeable {
	T next() throws IOException;
}
