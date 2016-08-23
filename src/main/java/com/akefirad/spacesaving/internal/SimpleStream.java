package com.akefirad.spacesaving.internal;

import com.akefirad.spacesaving.Stream;

import java.util.Map;
import java.util.Set;

public interface SimpleStream<T> extends Stream<T> {
    int count ();

    Set<T> alphabets ();

    Map<T, Long> frequencies ();

    Map<T, Long> frequencies (Set<T> alphabets);
}
