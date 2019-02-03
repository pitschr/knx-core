package li.pitschmann.knx.link.plugin;

import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.test.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.*;

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
        var body = Mockito.mock(Body.class);
        Mockito.when(body.getServiceType()).thenReturn(ServiceType.CONNECT_REQUEST);
        Mockito.when(body.getRawData()).thenReturn(new byte[]{0x11, 0x22, 0x33});
        Mockito.when(body.getRawDataAsHexString()).thenReturn("0x11 22 33");

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
        var body = Mockito.mock(Body.class);
        Mockito.when(body.getServiceType()).thenReturn(ServiceType.TUNNELING_ACK);
        Mockito.when(body.getRawData()).thenReturn(new byte[]{0x22, 0x33});
        Mockito.when(body.getRawDataAsHexString()).thenReturn("0x22 33");

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
        var exception = Mockito.mock(RuntimeException.class);
        Mockito.when(exception.getMessage()).thenReturn("I am a RuntimeException");
        Mockito.when(exception.getStackTrace()).thenReturn(
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
        var logLines = appender.all();
        Assertions.assertThat(logLines).hasSize(1);
        Assertions.assertThat(logLines.get(0)).isEqualTo(expected);
    }
}