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

import com.vlkan.rfos.RotatingFileOutputStream;
import com.vlkan.rfos.RotationConfig;
import com.vlkan.rfos.policy.DailyRotationPolicy;
import com.vlkan.rfos.policy.SizeBasedRotationPolicy;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.link.plugin.config.EnumConfigValue;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.config.PathConfigValue;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Audit Plug-in that logs every signal arriving KNX client to a file
 * <p/>
 * JSON (default) and CSV formats are supported.
 *
 * @author PITSCHR
 */
public final class FileAuditPlugin implements ObserverPlugin, ExtensionPlugin {
    /**
     * File path
     */
    public static final PathConfigValue PATH = new PathConfigValue("path", () -> Paths.get("."), null);
    /**
     * File format (e.g. JSON, CSV)
     */
    public static final EnumConfigValue<FileAuditFormat> FORMAT = new EnumConfigValue<>("format", FileAuditFormat.class, () -> FileAuditFormat.JSON);
    private static final Logger log = LoggerFactory.getLogger(FileAuditPlugin.class);
    private static final String FILE_ROLLOVER_PATTERN = "-%d{yyyyMMdd-HHmmss-SSS}";

    private Path path;
    private FileAuditFormat format;
    private RotatingFileOutputStream fos;

    @Override
    public void onInitialization(final @Nullable KnxClient client) {
        // configurations
        path = client.getConfig().getSetting(FileAuditPlugin.PATH);
        format = client.getConfig().getSetting(FileAuditPlugin.FORMAT);

        final var baseFile = path.toString();

        // get file pattern for rollover
        final var lastExtensionDotPosition = baseFile.lastIndexOf('.');
        final var rolloverFile = new StringBuilder()
                .append(baseFile, 0, lastExtensionDotPosition)
                .append(FILE_ROLLOVER_PATTERN)
                .append(baseFile.substring(lastExtensionDotPosition))
                .toString();

        final var config = RotationConfig
                .builder()
                .file(baseFile)
                .filePattern(rolloverFile)
                .policy(DailyRotationPolicy.getInstance())
                .append(true)
                .build();

        // start rollover stream
        fos = new RotatingFileOutputStream(config);

        auditSignal(AuditType.INIT);
    }

    @Override
    public void onStart() {
        auditSignal(AuditType.START);
    }

    @Override
    public void onShutdown() {
        auditSignal(AuditType.SHUTDOWN);

        // close the rollover stream
        Closeables.closeQuietly(fos);
    }

    @Override
    public void onIncomingBody(final @Nonnull Body item) {
        auditBody(AuditType.INCOMING, item);
    }

    @Override
    public void onOutgoingBody(final @Nonnull Body item) {
        auditBody(AuditType.OUTGOING, item);
    }

    @Override
    public void onError(final @Nonnull Throwable throwable) {
        final var now = Instant.now();
        writeToAuditFile(String.format(format.getErrorTemplate(), //
                now.getEpochSecond(), // #1
                now.getNano(), // #2
                format.escape(AuditType.ERROR), // #3
                format.escape(throwable.getMessage()), // #4
                format.escape(throwable.getStackTrace()) // #5
        ));
    }

    /**
     * Prints audit for given {@link Body}
     *
     * @param type audit type
     * @param body body to be printed
     */
    private void auditBody(final @Nonnull AuditType type, final @Nonnull Body body) {
        final var now = Instant.now();
        final var header = Header.of(body);
        writeToAuditFile(String.format(format.getBodyTemplate(), //
                now.getEpochSecond(), // #1
                now.getNano(), // #2
                format.escape(type), // #3
                format.escape(header.getTotalLength()), // #4
                format.escape(header.getRawDataAsHexString()), // #5
                format.escape(ByteFormatter.formatHexAsString(body.getServiceType().getCodeAsBytes())), // #6
                format.escape(body.getServiceType().name()), // #7
                format.escape(body.getRawDataAsHexString()) // #8
        ));
    }

    /**
     * Prints audit for signal
     *
     * @param type audit type
     */
    private void auditSignal(final @Nonnull AuditType type) {
        final var now = Instant.now();
        writeToAuditFile(String.format(format.getSignalTemplate(), //
                now.getEpochSecond(), // #1
                now.getNano(), // #2
                format.escape(type) // #3
        ));
    }

    /**
     * Writes the {@link String} line to audit file
     *
     * @param line
     */
    private void writeToAuditFile(final @Nonnull String line) {
        try {
            fos.write(line.getBytes(StandardCharsets.UTF_8));
            fos.write(System.lineSeparator().getBytes());
        } catch (final IOException e) {
            log.error("Error writing to audit file '{}': {}", path, line, e);
        }
    }

    /**
     * Returns the type of Audit
     *
     * @author PITSCHR
     */
    private enum AuditType {
        /**
         * Indicates there was an initialization signal sent
         */
        INIT("init"),
        /**
         * Indicates that there was a start signal sent
         */
        START("start"),
        /**
         * Indicates that there was a shutdown signal sent
         */
        SHUTDOWN("shutdown"),
        /**
         * Indicates that packet arrived the INBOX
         */
        INCOMING("incoming"),
        /**
         * Indicates that packet is being sent to OUTBOX
         */
        OUTGOING("outgoing"),
        /**
         * Indicates that there was an exception thrown
         */
        ERROR("error");

        private final String type;

        AuditType(final String type) {
            this.type = type;
        }

        @Nonnull
        @Override
        public String toString() {
            return this.type;
        }
    }
}
