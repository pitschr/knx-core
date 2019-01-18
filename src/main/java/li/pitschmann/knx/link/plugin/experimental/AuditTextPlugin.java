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

import com.google.common.primitives.Bytes;
import li.pitschmann.knx.link.body.*;
import li.pitschmann.knx.link.communication.*;
import li.pitschmann.knx.link.header.*;
import li.pitschmann.knx.link.plugin.*;
import li.pitschmann.utils.*;
import org.slf4j.*;

/**
 * TODO: TO IMPLEMENT
 *
 * <strong>Audit Packets</strong><br>
 * <ol>
 * <li><strong>timestamp</strong> in milliseconds since UTC epoch (non-null)</li>
 * <li><strong>status</strong>, if the audit packet is success/exception/... (non-null)</li>
 * <li><strong>source</strong>, the source of packet (non-null)</li>
 * <li><strong>serviceType</strong>, see: {@link ServiceType}</li>
 * <li><strong>raw</strong>, complete raw data incl. header and body</li>
 * <li><strong>message</strong>, detailed message (e.g. exception)</li>
 * </ol>
 *
 * @author PITSCHR
 */
public final class AuditTextPlugin implements ObserverPlugin, ExtensionPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(AuditTextPlugin.class);

    @Override
    public void onIncomingBody(Body item) {
        this.on("INCOMING", item);
    }

    @Override
    public void onOutgoingBody(Body item) {
        this.on("OUTGOING", item);
    }

    public void on(final String text, final Body item) {
        MDC.put("TYPE", text);
        if (item == null) {
            LOG.info("(null)\t(null)");
        } else {
            LOG.info("{}\t{}", item.getServiceType().name(),
                    ByteFormatter.formatHexAsString(Bytes.concat(Header.create(item).getRawData(), item.getRawData())));
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        MDC.put("TYPE", "ERROR");
        LOG.error("{}", throwable.getMessage());
    }

    @Override
    public void onInitialization(KnxClient client) {
        this.on("INIT", null);
    }

    @Override
    public void onStart() {
        this.on("START", null);
    }

    @Override
    public void onShutdown() {
        this.on("SHUTDOWN", null);
    }

}
