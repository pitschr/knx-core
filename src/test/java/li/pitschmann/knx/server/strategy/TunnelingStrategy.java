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

package li.pitschmann.knx.server.strategy;

import li.pitschmann.knx.link.body.cemi.CEMI;

/**
 * Marker interface for tunneling strategy to handle tunneling request and
 * acknowledge packets. The tunneling packets are communicated bi-directional
 * <p/>
 * <u>Possible workflows:</u><br>
 * KNX client will send a read/write tunneling request to the KNX mock server and
 * the request will be acknowledged by the KNX mock server - in parallel the request
 * from client will be forwarded to KNX devices.
 * <pre>
 * [ Client ] --- request --> [ Mock Server ]
 * [ Client ] <-- response -- [ Mock Server ]
 * </pre>
 * If a status has been updated in a KNX device then they may inform all other KNX devices.
 * In our case the tunneling request will be simulated as a forward from KNX mock server
 * to the KNX client. As usually, the KNX client should respond with a tunneling acknowledge.
 * <pre>
 * [ Client ] <-- request --- [ Mock Server ]
 * [ Client ] -- response --> [ Mock Server ]
 * </pre>
 */
public interface TunnelingStrategy extends RequestStrategy<CEMI>, ResponseStrategy {
    // empty
}
