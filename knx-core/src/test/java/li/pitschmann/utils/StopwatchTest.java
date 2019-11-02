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

package li.pitschmann.utils;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Test cases for {@link Stopwatch} class
 *
 * @author PITSCHR
 */
public class StopwatchTest {

    @Test
    @DisplayName("Test started stop watch")
    public void testCreatedStarted() {
        final var sw = Stopwatch.createStarted();

        // wait 100 (elapsed should be greater than 100)
        Sleeper.milliseconds(100);

        assertThat(sw.elapsed().toMillis()).isGreaterThan(99L);
        assertThat(sw.elapsed(TimeUnit.MILLISECONDS)).isGreaterThan(99L);

        // restart (should throw exception)
        assertThatThrownBy(() -> sw.start())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("This stopwatch is already running.");

        // stop
        assertThat(sw.stop()).isSameAs(sw);

        // elapsed should not be changed
        final var tick = sw.elapsed().toMillis();
        Sleeper.milliseconds(100);
        assertThat(sw.elapsed(TimeUnit.MILLISECONDS)).isEqualTo(tick);

        // reset
        assertThat(sw.reset()).isSameAs(sw);
        assertThat(sw.elapsed(TimeUnit.MILLISECONDS)).isZero();
        assertThat(sw.elapsed().toSeconds()).isZero();
    }

    @Test
    @DisplayName("Test non-started stop watch")
    public void testCreatedUnstarted() {
        final var sw = Stopwatch.createUnstarted();

        // wait 100 (elapsed should remain unchanged as the stopwatch is not started)
        Sleeper.milliseconds(100);
        assertThat(sw.elapsed().toMillis()).isZero();
        assertThat(sw.elapsed(TimeUnit.MILLISECONDS)).isZero();

        // start
        assertThat(sw.start()).isSameAs(sw);

        // elapsed should be changed now be changed
        Sleeper.milliseconds(100);
        assertThat(sw.elapsed().toMillis()).isGreaterThan(99L);
        assertThat(sw.elapsed(TimeUnit.MILLISECONDS)).isGreaterThan(99L);
    }

    @Test
    @DisplayName("Test elapsed methods of stop watch")
    public void testElapsed() {
        final var sw = Stopwatch.createStarted();

        // elapsed #1
        Sleeper.milliseconds(100);
        final var tick1 = sw.elapsed().toMillis();
        assertThat(tick1).isBetween(100L, 1000L);

        // elapsed #2
        Sleeper.milliseconds(100);
        final var tick2 = sw.elapsed().toMillis();
        assertThat(tick2).isGreaterThan(tick1);
        assertThat(tick2).isBetween(tick1 + 100L, tick1 + 1000L);
    }

    @Test
    @DisplayName("Test #toString() of stop watch")
    public void testToString() {
        final var sw = Stopwatch.createStarted();

        Sleeper.milliseconds(10);
        assertThat(sw.toString()).matches("\\d+\\.\\d+ ms");

        Sleeper.milliseconds(1000);
        assertThat(sw.toString()).matches("\\d+\\.\\d+ s");
    }

    @Test
    @DisplayName("Test #toString(TimeUnit) of stop watch")
    public void testToStringWithUnit() {
        final var sw = Stopwatch.createStarted();

        Sleeper.milliseconds(10);
        assertThat(sw.toString(TimeUnit.NANOSECONDS)).matches("\\d+\\.\\d+ ns");
        assertThat(sw.toString(TimeUnit.MICROSECONDS)).matches("\\d+\\.\\d+ μs");
        assertThat(sw.toString(TimeUnit.MILLISECONDS)).matches("\\d+\\.\\d+ ms");
        assertThat(sw.toString(TimeUnit.SECONDS)).matches("\\d+\\.\\d+ s");
        assertThat(sw.toString(TimeUnit.MINUTES)).matches("\\d+\\.\\d+ min");
        assertThat(sw.toString(TimeUnit.HOURS)).matches("\\d+\\.\\d+ h");
        assertThat(sw.toString(TimeUnit.DAYS)).matches("\\d+\\.\\d+ d");
    }
}
