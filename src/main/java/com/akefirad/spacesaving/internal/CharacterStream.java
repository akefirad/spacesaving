package com.akefirad.spacesaving.internal;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;

/**
 * This stream skips new line characters
 */
public class CharacterStream extends AbstractSimpleStream<Character> {
    private final Reader reader;

    public CharacterStream (Reader reader) {
        this.reader = reader;
    }

    @Override
    protected Character doNext () throws IOException {
        int c = reader.read();
        while (c != -1 && StringUtils.isWhitespace(String.valueOf((char)c)))
            c = reader.read();
        return (c == -1) ? null : (char) c;
    }

    @Override
    public void close () throws IOException {
        reader.close();
    }
}
