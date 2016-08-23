package com.akefirad.spacesaving;

import java.util.Map;

public interface StreamSummary<T> {
    void add (T value);

    Map<SummaryElement<T>, Integer> frequencies ();
}
