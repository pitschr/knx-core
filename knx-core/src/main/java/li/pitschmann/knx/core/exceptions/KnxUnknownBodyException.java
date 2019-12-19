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

package li.pitschmann.knx.core.exceptions;

import li.pitschmann.knx.core.annotations.Nullable;

/**
 * Exception in case no Body could be parsed of raw data.
 *
 * @author PITSCHR
 */
public final class KnxUnknownBodyException extends KnxCommunicationException {

    /**
     * Constructor for {@link KnxUnknownBodyException} in case the body could not be parsed based on given
     * {@code rawData}.
     *
     * @param rawData
     */
    public KnxUnknownBodyException(final @Nullable byte[] rawData) {
        super("Unknown body received for raw data: {}", rawData);
    }

}
