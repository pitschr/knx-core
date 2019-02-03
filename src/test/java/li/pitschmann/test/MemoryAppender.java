package li.pitschmann.test;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.encoder.*;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.core.*;
import org.slf4j.Logger;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * A memory appender - this will forward specific log lines to memory for testing
 * <p/>
 * This appender is a singleton.
 *
 * @author PITSCHR
 */
public final class MemoryAppender extends OutputStreamAppender<ILoggingEvent> {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(16000); // 16KB
    private final Lock lock = new ReentrantLock();

    /**
     * Private constructor for {@link MemoryAppender}. Will be instantiated once time only!
     */
    public MemoryAppender() {
        // sets the name
        setName("MEMORY");

        // set layout and encoder
        var ple = new PatternLayoutEncoder();
        ple.setPattern("%msg%n");
        ple.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        ple.start();
        setEncoder(ple);

        // set output stream
        setOutputStream(outputStream);

        // start appender
        start();
    }

    /**
     * Returns all log lines from memory
     *
     * @return list of unmodifiable log lines
     */
    public List<String> all() {
        return Arrays //
                .stream(fetchAllInternal()) //
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all filter-matched log lines from memory
     *
     * @param predicate find log files that matches with {@link Predicate}
     * @return list of unmodifiable log lines
     */
    public List<String> filter(final Predicate<String> predicate) {
        return Arrays //
                .stream(fetchAllInternal()) //
                .filter(predicate) //
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Resets the {@link MemoryAppender}. This will remove all log lines from memory.
     */
    public final void reset() {
        lock.lock();
        try {
            outputStream.reset();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds the {@link MemoryAppender} to given {@link Logger}
     *
     * @param logger logger that should be attached with appender
     */
    public final void addForLogger(final Logger logger) {
        var loggerImpl = (ch.qos.logback.classic.Logger) logger;

        if (!loggerImpl.isAttached(this)) {
            loggerImpl.setLevel(Level.ALL);
            loggerImpl.addAppender(this);
            loggerImpl.setAdditive(false);
        }
    }

    /**
     * Detaches the {@link MemoryAppender} for given {@link Logger}
     *
     * @param logger logger that should be attached with appender
     */
    public final void detachForLogger(final Logger logger) {
        ((ch.qos.logback.classic.Logger) logger).detachAppender(this);
    }

    /**
     * Returns all log lines from memory internally
     *
     * @return log lines as String array
     */
    private final String[] fetchAllInternal() {
        lock.lock();
        try {
            if (outputStream.size() == 0) {
                return new String[0];
            } else {
                return outputStream.toString(StandardCharsets.UTF_8).split(System.lineSeparator());
            }
        } finally {
            lock.unlock();
        }
    }
}
