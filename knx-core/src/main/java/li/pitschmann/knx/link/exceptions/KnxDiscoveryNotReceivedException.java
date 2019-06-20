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

package li.pitschmann.knx.link.exceptions;

import li.pitschmann.knx.link.body.Body;

/**
 * Exception when discovery information could not be received by KNX Net/IP device.
 * This exception is a subclass of {@link KnxCommunicationException}.
 *
 * @author PITSCHR
 */
public final class KnxDiscoveryNotReceivedException extends KnxCommunicationException {

    public KnxDiscoveryNotReceivedException(final Body body) {
        super(String.format("Could not get discovery from KNX Net/IP device: %s", body));
    }
}
