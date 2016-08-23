package com.akefirad.spacesaving;

import org.slf4j.Logger;

import java.util.*;

import static com.akefirad.spacesaving.Asserts.assertNotNull;
import static com.akefirad.spacesaving.Asserts.assertValidSize;
import static java.util.Collections.unmodifiableMap;
import static org.slf4j.LoggerFactory.getLogger;

public class SimpleStreamSummary<T> implements StreamSummary<T> {
    private static final Logger logger = getLogger(SimpleStreamSummary.class);

    private final int size;
    private final Map<SummaryElement<T>, Integer> elements;
    private final NavigableMap<Integer, Set<SummaryElement<T>>> counts;

    public SimpleStreamSummary (int size) {
        assertValidSize(size, "size of stream summary");
        this.size = size;
        this.elements = new HashMap<>();
        this.counts = new TreeMap<>();
    }

    @Override
    public void add (T object) {
        assertNotNull(object, "value");

        SummaryElement<T> element = new SummaryElement<>(object);
        logger.debug("Adding element {}...", element);

        Integer count = elements.get(element);
        // If it's not a new element:
        if (count != null) {
            logger.debug("Existing element. Increasing count {}...", element, count);
            assert counts.get(count).contains(element);

            Set<SummaryElement<T>> elements = counts.get(count);
            boolean removed = elements.remove(element);
            assert removed : "Expecting object '" + object + "' in the set!";

            if (elements.isEmpty()) {
                logger.debug("The set for count {} is empty. Removing it...", count);
                counts.remove(count);
            }

            doAdd(element, count + 1);
        }
        // If it's a new element, and no space is available:
        else if (elements.size() == size) {
            assert !counts.firstEntry().getValue().isEmpty();

            logger.debug("New element, but no space. Removing one element...");

            Integer minCount = counts.firstKey();
            Set<SummaryElement<T>> minElements = counts.get(minCount);
            assert minElements.size() > 0 : "Expecting non-empty set!";

            // Might be important which one should be removed:

            // Option1: Get the first element!
            SummaryElement<T> minElement = minElements.iterator().next();

            // Option2: Get the element with the biggest error!
            //SummaryElement<T> minElement = minElements.stream()
            //        .sorted((o1, o2) -> o2.error() - o1.error())
            //        .findFirst().orElseThrow(AssertionError::new);

            // Trying option2 might slightly improve the overestimation.

            logger.debug("Minimum count is {}. Removing '{}'...", minCount, minElement);

            minElements.remove(minElement);
            if (minElements.isEmpty()) {
                logger.debug("The set for count {} is empty. Removing it...", minCount);
                counts.remove(minCount);
            }

            Integer expected = elements.remove(minElement);
            assert Objects.equals(expected, minCount);

            // Ask for more information: Should the old error be added?
            doAdd(new SummaryElement<>(object, minCount), minCount + 1);
        }
        // If it's a new element, and some space is available:
        else {
            doAdd(element, 1);
        }
    }

    @Override
    public Map<SummaryElement<T>, Integer> frequencies () {
        return unmodifiableMap(elements);
    }

    private void doAdd (SummaryElement<T> element, int count) {
        assert element != null && count > 0;

        logger.debug("Inserting {} with count {}...", element, count);
        elements.put(element, count);
        Set<SummaryElement<T>> elements = counts.get(count);
        if (elements == null) {
            logger.debug("Create a new set for count {}...", count);
            elements = new HashSet<>(); // Should be LinkedHashSet?
            counts.put(count, elements);
        }
        elements.add(element);
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder("{");
        elements.forEach((element, count) -> sb.append(element.get())
                .append(":").append(count)
                .append(":").append(element.error())
                .append(", "));
        sb.append("}");
        return sb.toString();
    }
}
