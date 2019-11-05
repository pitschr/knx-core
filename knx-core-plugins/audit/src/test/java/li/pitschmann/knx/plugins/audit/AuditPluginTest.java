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

package li.pitschmann.knx.plugins.audit;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.header.ServiceType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link AuditPlugin}
 */
public class AuditPluginTest {
    /**
     * Test {@link AuditPlugin#onIncomingBody(Body)}
     */
    @Test
    public void auditIncomingBody() throws IOException {
        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.CONNECT_REQUEST);
        when(body.getRawData()).thenReturn(new byte[]{0x11, 0x22, 0x33});
        when(body.getRawDataAsHexString()).thenReturn("0x11 22 33");

        final var path = Paths.get("target/test-AuditPluginTest-auditIncomingBody-" + UUID.randomUUID() + ".log");
        final var plugin = new AuditPlugin(path);
        plugin.onInitialization(mock(KnxClient.class));
        plugin.onStart();
        plugin.onIncomingBody(body);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(4);

        // init
        assertThat(lines.get(0)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\"type\":\"init\"" +
                "\\}"
                // @formatter:on
        );
        // start
        assertThat(lines.get(1)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\"type\":\"start\"" +
                "\\}"
                // @formatter:on
        );
        // incoming
        assertThat(lines.get(2)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\"type\":\"incoming\"," +
                    "\"header\":\\{" +
                        "\"totalLength\":9," +
                        "\"raw\":\"0x06 10 02 05 00 09\"" +
                    "\\}," +
                    "\"body\":\\{" +
                        "\"service\":\\{" +
                            "\"code\":\"0x02 05\"," +
                            "\"text\":\"CONNECT_REQUEST\"" +
                        "\\}," +
                        "\"raw\":\"0x11 22 33\"" +
                    "\\}" +
                "\\}"
                // @formatter:on
        );
        // shutdown
        assertThat(lines.get(3)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\"type\":\"shutdown\"" +
                "\\}"
                // @formatter:on
        );
    }


    /**
     * Test {@link AuditPlugin#onOutgoingBody(Body)}
     */
    @Test
    public void auditOutgoingBody() throws IOException {
        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.TUNNELING_ACK);
        when(body.getRawData()).thenReturn(new byte[]{0x22, 0x33});
        when(body.getRawDataAsHexString()).thenReturn("0x22 33");

        final var path = Paths.get("target/test-AuditPluginTest-auditOutgoingBody-" + UUID.randomUUID() + ".log");
        final var plugin = new AuditPlugin(path);
        plugin.onInitialization(mock(KnxClient.class));
        plugin.onStart();
        plugin.onOutgoingBody(body);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(4);
        assertThat(lines.get(2)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\"type\":\"outgoing\"," + //
                    "\"header\":\\{" +
                        "\"totalLength\":8," +
                        "\"raw\":\"0x06 10 04 21 00 08\"" +
                    "\\}," +
                    "\"body\":\\{" +
                        "\"service\":\\{" +
                            "\"code\":\"0x04 21\"," +
                            "\"text\":\"TUNNELING_ACK\"" +
                        "\\}," +
                        "\"raw\":\"0x22 33\"" +
                    "\\}" +
                "\\}"
                // @formatter:on
        );
    }

    /**
     * Test {@link AuditPlugin#onError(Throwable)}
     */
    @Test
    public void auditOnError() throws IOException {
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

        final var path = Paths.get("target/test-AuditPluginTest-auditOnError-" + UUID.randomUUID() + ".log");
        final var plugin = new AuditPlugin(path);
        plugin.onInitialization(mock(KnxClient.class));
        plugin.onStart();
        plugin.onError(exception);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(4);
        assertThat(lines.get(2)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\"type\":\"error\"," +
                    "\"message\":\"I am a RuntimeException\"," +
                    "\"stacktrace\":\\[" +
                        "\"org.class.Foo.add\\(Foo.java:123\\)\"," +
                        "\"org.class.Bar.addAll\\(Bar.java:456\\)\"" +
                    "\\]" +
                "\\}"
                // @formatter:on
        );
        // @formatter:on
    }
}
