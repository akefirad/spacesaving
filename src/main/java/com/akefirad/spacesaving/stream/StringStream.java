package com.akefirad.spacesaving.stream;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static org.apache.commons.lang3.StringUtils.split;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A simple stream with non-empty lowercase string tokens.
 */
public class StringStream extends AbstractSimpleStream<String> {
	private static final Logger logger = getLogger(StringStream.class);

	private final BufferedReader reader;
	private Iterator<String> elements;

	public StringStream(Reader reader) {
		this.reader = new BufferedReader(reader);
		this.elements = emptyIterator();
	}

	@Override
	protected String doNext() throws IOException {
		if (!elements.hasNext()) {
			String line = "";
			while (line != null && line.trim().isEmpty())
				line = reader.readLine();
			elements = line != null ? asList(split(line.trim())).iterator() : emptyIterator();
		}
		return elements.hasNext() ? elements.next().trim().toLowerCase() : null;
	}

	@Override
	public void close() throws IOException {
	}
}
