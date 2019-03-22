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

package li.pitschmann.knx.server.strategy.impl;

import li.pitschmann.knx.link.body.Status;
import li.pitschmann.knx.server.strategy.ConnectStrategy;

/**
 * No More Connections implementation for {@link ConnectStrategy}
 */
public class ConnectNoMoreConnectionsStrategy extends DefaultConnectStrategy {
    /**
     * Return no more connections status
     *
     * @return status
     */
    protected Status getStatus() {
        return Status.E_NO_MORE_CONNECTIONS;
    }
}
