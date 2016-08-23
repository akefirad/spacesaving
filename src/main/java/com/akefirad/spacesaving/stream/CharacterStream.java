package com.akefirad.spacesaving.stream;


import java.io.IOException;
import java.io.Reader;

import static java.lang.Character.isLetterOrDigit;

/**
 * This stream gives only letters or digits characters
 */
public class CharacterStream extends AbstractSimpleStream<Character> {
	private final Reader reader;

	public CharacterStream(Reader reader) {
		this.reader = reader;
	}

	@Override
	protected Character doNext() throws IOException {
		int c = reader.read();
		while (c != -1 && !isLetterOrDigit((char) c))
			c = reader.read();
		return (c == -1) ? null : (char) c;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
