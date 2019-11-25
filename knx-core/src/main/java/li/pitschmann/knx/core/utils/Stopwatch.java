package li.pitschmann.knx.core.utils;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Stopwatch to measure the elapsed time in nanoseconds.
 */
public final class Stopwatch {
    private AtomicBoolean isRunning = new AtomicBoolean();
    private long elapsedNanos;
    private long startTick;

    private Stopwatch() {
    }

    /**
     * Creates a new {@link Stopwatch} and starts it
     *
     * @return started {@link Stopwatch}
     */
    @Nonnull
    public static Stopwatch createStarted() {
        return new Stopwatch().start();
    }

    /**
     * Creates but does not start a new {@link Stopwatch}
     *
     * @return not started {@link Stopwatch}
     */
    @Nonnull
    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }

    /**
     * Internal Helper for {@link #toString()} method to get
     * {@link TimeUnit} that should be used for human-friendly
     * time representation.
     *
     * @param nanos
     * @return the type of {@link TimeUnit} to be printed in {@link #toString()}
     */
    private static TimeUnit chooseUnit(final long nanos) {
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        } else if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        } else if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        } else if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        } else {
            return NANOSECONDS;
        }
    }

    /**
     * Internal Helper for {@link #toString()} method to get a
     * human-friendly {@link TimeUnit}. Example: {@code s} for
     * seconds, {@code min} for minutes.
     *
     * @param unit
     * @return human-friendly {@link TimeUnit}
     */
    private static String toStringUnit(final @Nonnull TimeUnit unit) {
        switch (unit) {
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            default:
                return "ns";
        }
    }

    /**
     * Starts the {@link Stopwatch} if not done before or stopped before.
     *
     * @return myself
     */
    @Nonnull
    public Stopwatch start() {
        Preconditions.checkState(!isRunning.getAndSet(true), "This stopwatch is already running.");
        startTick = System.nanoTime();
        return this;
    }

    /**
     * Stops the {@link Stopwatch}
     *
     * @return myself
     * @throws IllegalStateException if Stopwatch was already stopped
     */
    @Nonnull
    public Stopwatch stop() {
        final var tick = System.nanoTime();
        Preconditions.checkState(isRunning.getAndSet(false), "This stopwatch is already stopped.");
        elapsedNanos += tick - startTick;
        return this;
    }

    /**
     * Resets the {@link Stopwatch} the elapsed time is set to {@code zero}
     *
     * @return myself
     */
    @Nonnull
    public Stopwatch reset() {
        elapsedNanos = 0;
        isRunning.set(false);
        return this;
    }

    /**
     * The elapsed time in {@link TimeUnit}
     *
     * @param desiredUnit
     * @return elapsed time in {@link TimeUnit}
     */
    public long elapsed(final @Nonnull TimeUnit desiredUnit) {
        return desiredUnit.convert(elapsedNanos(), NANOSECONDS);
    }

    /**
     * The elapsed time as {@link Duration}
     *
     * @return duration
     */
    public Duration elapsed() {
        return Duration.ofNanos(elapsedNanos());
    }

    /**
     * Human-friendly string representation of {@link Stopwatch} in current elapsed time.
     *
     * @return string
     */
    @Nonnull
    public String toString() {
        final var nanos = elapsedNanos();
        return toStringInternal(nanos, chooseUnit(nanos));
    }

    /**
     * Human-friendly string representation of {@link Stopwatch} in current elapsed time
     * and in desired {@link TimeUnit} format.
     *
     * @return string
     */
    @Nonnull
    public String toString(final @Nonnull TimeUnit unit) {
        return toStringInternal(elapsedNanos(), unit);
    }

    @Nonnull
    private String toStringInternal(final long nanos, final @Nonnull TimeUnit unit) {
        final var value = (double) nanos / NANOSECONDS.convert(1, unit);

        return String.format("%.4f %s", value, toStringUnit(unit));
    }

    /**
     * Elapsed time in nanoseconds
     *
     * @return nanoseconds
     */
    private long elapsedNanos() {
        return isRunning.get() ? System.nanoTime() - startTick + elapsedNanos : elapsedNanos;
    }
}
