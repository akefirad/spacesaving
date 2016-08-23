package com.akefirad.spacesaving.internal;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toMap;

public abstract class AbstractSimpleStream<T> implements SimpleStream<T> {
    private final Map<T, Long> frequencies;

    public AbstractSimpleStream () {
        this.frequencies = new HashMap<>();
    }

    @Override
    public int count () {
        return frequencies.size();
    }

    @Override
    public Set<T> alphabets () {
        return unmodifiableSet(frequencies.keySet());
    }

    @Override
    public Map<T, Long> frequencies () {
        return unmodifiableMap(frequencies);
    }

    @Override
    public Map<T, Long> frequencies (Set<T> alphabets) {
        return unmodifiableMap(frequencies.entrySet().stream()
                .filter(map -> alphabets.contains(map.getKey()))
                .collect(toMap(Entry::getKey, Entry::getValue)));
    }

    @Override
    public T next () throws IOException {
        T next = doNext();
        if (next != null) {
            Long count = frequencies.get(next);
            frequencies.put(next, count == null ? 1 : count + 1);
        }
        return next;
    }

    protected abstract T doNext () throws IOException;
}

