package com.akefirad.spacesaving;

import com.akefirad.spacesaving.internal.AbstractSimpleStream;
import org.slf4j.Logger;

import java.io.*;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

public class SystemInputStringStream extends AbstractSimpleStream<String> {
    private static final Logger logger = getLogger(SystemInputStringStream.class);

    private final Scanner scanner;

    public SystemInputStringStream () {
        this.scanner = new Scanner(System.in);
    }

    @Override
    protected String doNext () throws IOException {
        String string = scanner.hasNext() ? scanner.next() : null;
        logger.debug("Next element: {}", string);
        return string;
    }

    @Override
    public void close () throws IOException {
    }
}
