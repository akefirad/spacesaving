package com.akefirad.spacesaving;

import java.io.Closeable;
import java.io.IOException;

public interface Stream<T> extends Closeable {
    T next () throws IOException;
}
