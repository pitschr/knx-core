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

import com.google.gson.Gson;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.utils.ByteFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Audit Plug-in that logs every signal arriving KNX client to a log file
 *
 * @author PITSCHR
 */
public final class AuditPlugin implements ObserverPlugin, ExtensionPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(AuditPlugin.class);
    private static final Gson gson = new Gson();

    // @formatter:off
    /**
     * JSON Audit Template for signal
     */
    private static final String JSON_TEMPLATE_SIGNAL = "" + //
            "{" + //
                "\"type\":\"%1$s\"" + //
            "}"; //
    /**
     * JSON Audit Template for error
     */
    private static final String JSON_TEMPLATE_ERROR = "" + //
            "{" + //
                "\"type\":\"%1$s\"," + //
                "\"message\":%2$s," + //
                "\"stacktrace\":[%3$s]" + //
            "}"; //
    /**
     * JSON Audit Template with body details
     */
    private static final String JSON_TEMPLATE_BODY = "" + //
            "{" + //
                "\"type\":\"%1$s\"," + //
                "\"header\":{" + //
                    "\"totalLength\":%5$s," + //
                    "\"raw\":\"%6$s\"" + //
                "}," + //
                "\"body\":{" + //
                    "\"service\":{" + //
                        "\"code\":\"%2$s\"," + //
                        "\"text\":\"%3$s\"" + //
                    "}," + //
                    "\"raw\":\"%4$s\"" + //
                "}" + //
            "}"; //
    // @formatter:on

    @Override
    public void onInitialization(KnxClient client) {
        auditSignal(AuditType.INIT);
    }

    @Override
    public void onStart() {
        auditSignal(AuditType.START);
    }

    @Override
    public void onShutdown() {
        auditSignal(AuditType.SHUTDOWN);
    }

    @Override
    public void onIncomingBody(Body item) {
        auditBody(AuditType.INCOMING, item);
    }

    @Override
    public void onOutgoingBody(Body item) {
        auditBody(AuditType.OUTGOING, item);
    }

    @Override
    public void onError(final Throwable throwable) {
        LOG.info(String.format(JSON_TEMPLATE_ERROR, //
                AuditType.ERROR, // #1
                gson.toJson(throwable.getMessage()), // #2
                Arrays.stream(throwable.getStackTrace()).map(e -> gson.toJson(e.toString())).collect(Collectors.joining(",")) // #3
        ));
    }

    /**
     * Prints audit for given {@link Body}
     *
     * @param type audit type
     * @param body body to be printed
     */
    private void auditBody(final @Nonnull AuditType type, final Body body) {
        var header = Header.create(body);
        LOG.info(String.format(JSON_TEMPLATE_BODY, //
                type, // #1
                ByteFormatter.formatHexAsString(body.getServiceType().getCodeAsBytes()), // #2
                body.getServiceType().name(), // #3
                body.getRawDataAsHexString(), // #4
                header.getTotalLength(), // #5
                header.getRawDataAsHexString() // #6
        ));
    }

    /**
     * Prints audit for signal
     *
     * @param type audit type
     */
    private void auditSignal(final @Nonnull AuditType type) {
        LOG.info(String.format(JSON_TEMPLATE_SIGNAL, type));
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

        @Override
        public String toString() {
            return this.type;
        }
    }
}
