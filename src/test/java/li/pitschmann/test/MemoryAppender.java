/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
     * Returns if at least one line that meets the predicate could be found
     *
     * @param predicate
     * @return {@code true} if found, otherwise {@code false}
     */
    public boolean anyMatch(final Predicate<String> predicate) {
        return Arrays //
                .stream(fetchAllInternal()) //
                .anyMatch(predicate);
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
