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

package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.test.MemoryAppender;
import li.pitschmann.test.MemoryLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link AuditPlugin}
 */
public class AuditPluginTest {
    private final AuditPlugin plugin = new AuditPlugin();

    /**
     * Test {@link AuditPlugin#onIncomingBody(Body)}
     *
     * @param appender memory appender instance
     */
    @Test
    @MemoryLog(AuditPlugin.class)
    public void auditIncomingBody(final MemoryAppender appender) {
        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.CONNECT_REQUEST);
        when(body.getRawData()).thenReturn(new byte[]{0x11, 0x22, 0x33});
        when(body.getRawDataAsHexString()).thenReturn("0x11 22 33");

        plugin.onIncomingBody(body);

        assertLogLine(appender,
                // @formatter:off
                "{\"type\":\"incoming\"," + //
                        "\"header\":{" +
                            "\"totalLength\":9," +
                            "\"raw\":\"0x06 10 02 05 00 09\"" +
                        "},"+
                        "\"body\":{"+
                            "\"service\":{"+
                                "\"code\":\"0x02 05\","+
                                "\"text\":\"CONNECT_REQUEST\""+
                            "},"+
                            "\"raw\":\"0x11 22 33\""+
                        "}"+
                "}"
                // @formatter:on
        );
    }

    /**
     * Test {@link AuditPlugin#onOutgoingBody(Body)}
     *
     * @param appender memory appender instance
     */
    @Test
    @MemoryLog(AuditPlugin.class)
    public void auditOutgoingBody(final MemoryAppender appender) {
        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.TUNNELING_ACK);
        when(body.getRawData()).thenReturn(new byte[]{0x22, 0x33});
        when(body.getRawDataAsHexString()).thenReturn("0x22 33");

        plugin.onOutgoingBody(body);

        assertLogLine(appender,
                // @formatter:off
                "{\"type\":\"outgoing\"," + //
                        "\"header\":{" +
                            "\"totalLength\":8," +
                            "\"raw\":\"0x06 10 04 21 00 08\"" +
                        "},"+
                        "\"body\":{"+
                            "\"service\":{"+
                                "\"code\":\"0x04 21\","+
                                "\"text\":\"TUNNELING_ACK\""+
                            "},"+
                            "\"raw\":\"0x22 33\""+
                        "}"+
                "}"
                // @formatter:on
        );
    }

    /**
     * Test {@link AuditPlugin#onError(Throwable)}
     *
     * @param appender memory appender instance
     */
    @Test
    @MemoryLog(AuditPlugin.class)
    public void auditOnError(final MemoryAppender appender) {
        final var exception = mock(RuntimeException.class);
        when(exception.getMessage()).thenReturn("I am a RuntimeException");
        when(exception.getStackTrace()).thenReturn(
                new StackTraceElement[]{
                        new StackTraceElement(
                                "org.class.Foo",
                                "add",
                                "Foo.java",
                                123
                        ),
                        new StackTraceElement(
                                "org.class.Bar",
                                "addAll",
                                "Bar.java",
                                456
                        )
                }
        );

        plugin.onError(exception);

        assertLogLine(appender,
                // @formatter:off
                "{\"type\":\"error\"," +
                        "\"message\":\"I am a RuntimeException\"," +
                        "\"stacktrace\":[" +
                            "\"org.class.Foo.add(Foo.java:123)\"," +
                            "\"org.class.Bar.addAll(Bar.java:456)\"" +
                        "]" +
                "}"
                // @formatter:on
        );
    }

    /**
     * Test {@link AuditPlugin#onInitialization(KnxClient)}
     *
     * @param appender memory appender instance
     */
    @Test
    @MemoryLog(AuditPlugin.class)
    public void auditOnInitialiization(final MemoryAppender appender) {
        // send init signal (No client required for plugin)
        plugin.onInitialization(null);

        assertLogLine(appender, "{\"type\":\"init\"}");
    }

    /**
     * Test {@link AuditPlugin#onStart()}
     *
     * @param appender memory appender instance
     */
    @Test
    @MemoryLog(AuditPlugin.class)
    public void auditOnStart(final MemoryAppender appender) {
        // send start signal
        plugin.onStart();

        assertLogLine(appender, "{\"type\":\"start\"}");
    }

    /**
     * Test {@link AuditPlugin#onShutdown()}
     *
     * @param appender memory appender instance
     */
    @Test
    @MemoryLog(AuditPlugin.class)
    public void auditOnShutdown(final MemoryAppender appender) {
        // send shutdown signal
        plugin.onShutdown();

        assertLogLine(appender, "{\"type\":\"shutdown\"}");
    }

    /**
     * Assert the first log line in {@link MemoryAppender}
     *
     * @param appender appender that contains the log lines
     * @param expected the expected string
     */
    private void assertLogLine(final MemoryAppender appender, final String expected) {
        final var logLines = appender.all();
        Assertions.assertThat(logLines).hasSize(1);
        Assertions.assertThat(logLines.get(0)).isEqualTo(expected);
    }
}
