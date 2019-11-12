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

package li.pitschmann.knx.link.plugin.experimental;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.communication.KnxClient;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * TODO: TO IMPLEMENT
 *
 * <strong>Audit Packets</strong><br>
 * <ol>
 * <li><strong>timestamp</strong> in milliseconds since UTC epoch (non-null)</li>
 * <li><strong>status</strong>, if the audit packet is success/exception/... (non-null)</li>
 * <li><strong>source</strong>, the source of packet (non-null)</li>
 * <li><strong>serviceType</strong>, see: {@link ServiceType}</li>
 * <li><strong>raw</strong>, complete raw data (header and body)</li>
 * <li><strong>message</strong>, detailed message (e.g. exception)</li>
 * </ol>
 *
 * @author PITSCHR
 */
public final class AuditDatabasePlugin implements ObserverPlugin {
    private static final Logger log = LoggerFactory.getLogger(AuditDatabasePlugin.class);

    public AuditDatabasePlugin(/* TODO database connectivity */) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onInitialization(final @Nullable KnxClient client) {
        // do nothing
    }

    @Override
    public void onIncomingBody(final @Nullable Body item) {
        this.on("INCOMING", item);
    }

    @Override
    public void onOutgoingBody(final @Nullable Body item) {
        this.on("OUTGOING", item);
    }

    public void on(final @Nullable String text, final @Nullable Body item) {
        Sleeper.milliseconds(5000);
        log.debug("TEXT TO {} (SLOW): {}", text, item);
    }

    @Override
    public void onError(final @Nullable Throwable throwable) {
        // TODO Auto-generated method stub

    }
}