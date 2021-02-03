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

package li.pitschmann.knx.core.plugin.audit;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.header.ServiceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link FileAuditPlugin}
 */
public class FileAuditPluginTest {

    @Test
    @DisplayName("JSON: Test File Audit Signal and Incoming")
    public void auditIncomingBodyJson() throws IOException {
        final var path = Paths.get("target/test-FileAuditPluginTest-auditIncomingBodyJson-" + UUID.randomUUID() + ".log");
        final var plugin = new FileAuditPlugin();

        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.CONNECT_REQUEST);
        when(body.toByteArray()).thenReturn(new byte[]{0x11, 0x22, 0x33});

        plugin.onInitialization(mockKnxClient(path, FileAuditFormat.JSON));
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
                    "\\Q" + // start pattern quote
                    "\"type\":\"incoming\"," +
                    "\"header\":{" +
                        "\"totalLength\":9," +
                        "\"raw\":\"0x06 10 02 05 00 09\"" +
                    "}," +
                    "\"body\":{" +
                        "\"service\":{" +
                            "\"code\":\"0x02 05\"," +
                            "\"text\":\"CONNECT_REQUEST\"" +
                        "}," +
                        "\"raw\":\"0x11 22 33\"" +
                    "}" +
                    "\\E" + // end pattern quote
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

    @Test
    @DisplayName("JSON: Test File Audit Outgoing")
    public void auditOutgoingBodyJson() throws IOException {
        final var path = Paths.get("target/test-FileAuditPluginTest-auditOutgoingBodyJson-" + UUID.randomUUID() + ".log");
        final var plugin = new FileAuditPlugin();

        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.TUNNELING_ACK);
        when(body.toByteArray()).thenReturn(new byte[]{0x22, 0x33});

        plugin.onInitialization(mockKnxClient(path, FileAuditFormat.JSON));
        plugin.onStart();
        plugin.onOutgoingBody(body);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(4);
        assertThat(lines.get(2)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\\Q" + // start pattern quote
                    "\"type\":\"outgoing\"," + //
                    "\"header\":{" +
                        "\"totalLength\":8," +
                        "\"raw\":\"0x06 10 04 21 00 08\"" +
                    "}," +
                    "\"body\":{" +
                        "\"service\":{" +
                            "\"code\":\"0x04 21\"," +
                            "\"text\":\"TUNNELING_ACK\"" +
                        "}," +
                        "\"raw\":\"0x22 33\"" +
                    "}" +
                    "\\E" + // end pattern quote
                "\\}"
                // @formatter:on
        );
    }

    @Test
    @DisplayName("JSON: Test File Audit Error")
    public void auditOnErrorJson() throws IOException {
        final var path = Paths.get("target/test-FileAuditPluginTest-auditOnErrorJson-" + UUID.randomUUID() + ".log");
        final var plugin = new FileAuditPlugin();
        final var exceptionMock = mockException();

        plugin.onInitialization(mockKnxClient(path, FileAuditFormat.JSON));
        plugin.onStart();
        plugin.onError(exceptionMock);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(4);
        assertThat(lines.get(2)).containsPattern(
                // @formatter:off
                "\\{" +
                    "\"time\":\\d+\\.\\d+," +
                    "\\Q" + // start pattern quote
                    "\"type\":\"error\"," +
                    "\"message\":\"I'm a \\\"Runtime\\tException\\\"!\"," +
                    "\"stacktrace\":[" +
                        "\"org.class.Foo.add(Foo.java:123)\"," +
                        "\"org.class.Bar.addAll(Bar.java:456)\"" +
                    "]" +
                    "\\E" + // end pattern quote
                "\\}"
                // @formatter:on
        );
    }

    @Test
    @DisplayName("TSV: Test File Audit Signal and Incoming")
    public void auditIncomingBodyTsv() throws IOException {
        final var path = Paths.get("target/test-FileAuditPluginTest-auditIncomingBodyTsv-" + UUID.randomUUID() + ".log");
        final var plugin = new FileAuditPlugin();

        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.CONNECT_REQUEST);
        when(body.toByteArray()).thenReturn(new byte[]{0x33, 0x44, 0x55});

        plugin.onInitialization(mockKnxClient(path, FileAuditFormat.TSV));
        plugin.onStart();
        plugin.onIncomingBody(body);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(5);

        // header
        assertThat(lines.get(0)).isEqualTo(
                // @formatter:off
            "Time\t" +
            "Type\t" +
            "Header Total Length\tHeader Raw\t" +
            "Body Service Code\tBody Service Text\tBody Raw\t" +
            "Error Message\tError Stack Trace"
            // @formatter:on
        );
        // init
        assertThat(lines.get(1)).containsPattern("\\d+\\.\\d+\tinit");
        // start
        assertThat(lines.get(2)).containsPattern("\\d+\\.\\d+\tstart");
        // incoming
        assertThat(lines.get(3)).containsPattern(
                // @formatter:off
                "\\d+\\.\\d+\t" +
                        "\\Q" +  // start pattern quote
                        "incoming\t" +
                        "9\t0x06 10 02 05 00 09\t" + // header
                        "0x02 05\tCONNECT_REQUEST\t0x33 44 55" + // body
                        "\\E" // end pattern quote
                // @formatter:on
        );
        // shutdown
        assertThat(lines.get(4)).containsPattern("\\d+\\.\\d+\tshutdown");
    }

    @Test
    @DisplayName("TSV: Test File Audit Outgoing")
    public void auditOutgoingBodyTsv() throws IOException {
        final var path = Paths.get("target/test-FileAuditPluginTest-auditOutgoingBodyTsv-" + UUID.randomUUID() + ".log");
        final var plugin = new FileAuditPlugin();

        final var body = mock(Body.class);
        when(body.getServiceType()).thenReturn(ServiceType.TUNNELING_ACK);
        when(body.toByteArray()).thenReturn(new byte[]{0x44, 0x55});

        plugin.onInitialization(mockKnxClient(path, FileAuditFormat.TSV));
        plugin.onStart();
        plugin.onOutgoingBody(body);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(5);
        assertThat(lines.get(3)).containsPattern(
                // @formatter:off
                "\\d+\\.\\d+\t" +
                        "\\Q" +  // start pattern quote
                        "outgoing\t" +
                        "8\t0x06 10 04 21 00 08\t" + // header
                        "0x04 21\tTUNNELING_ACK\t0x44 55" + // body
                        "\\E" // end pattern quote
                // @formatter:on
        );
    }

    @Test
    @DisplayName("TSV: Test File Audit Error")
    public void auditOnErrorTsv() throws IOException {
        final var path = Paths.get("target/test-FileAuditPluginTest-auditOnErrorTsv-" + UUID.randomUUID() + ".log");
        final var plugin = new FileAuditPlugin();
        final var exceptionMock = mockException();

        plugin.onInitialization(mockKnxClient(path, FileAuditFormat.TSV));
        plugin.onStart();
        plugin.onError(exceptionMock);
        plugin.onShutdown();

        final var lines = Files.readAllLines(path);
        assertThat(lines).hasSize(5);
        assertThat(lines.get(3)).containsPattern(
                // @formatter:off
                "\\d+\\.\\d+\t" +
                        "\\Q" +  // start pattern quote
                        "error\t\t\t\t\t\t" +
                        "I'm a \"RuntimeException\"!\t" +  // \t removed, because it is not allowed in tab-separated format
                        "[org.class.Foo.add(Foo.java:123), org.class.Bar.addAll(Bar.java:456)]" +
                        "\\E" // end pattern quote
                // @formatter:on
        );
    }

    private KnxClient mockKnxClient(final Path path, final FileAuditFormat format) {
        final var knxClientMock = mock(KnxClient.class);
        final var configMock = mock(Config.class);
        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getConfig(any())).thenCallRealMethod();

        when(configMock.getValue(eq(FileAuditPlugin.PATH))).thenReturn(path);
        when(configMock.getValue(eq(FileAuditPlugin.FORMAT))).thenReturn(format);
        return knxClientMock;
    }

    private Exception mockException() {
        final var exception = mock(RuntimeException.class);
        when(exception.getMessage()).thenReturn("I'm a \"Runtime\tException\"!");
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
        return exception;
    }

}
