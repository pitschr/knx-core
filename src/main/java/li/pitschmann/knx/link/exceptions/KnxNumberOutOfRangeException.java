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

import li.pitschmann.utils.ByteFormatter;

/**
 * Exception being thrown in case the a number is out of the range.
 *
 * @author PITSCHR
 */
public final class KnxNumberOutOfRangeException extends KnxException {
    /**
     * Exception if {@code actual} number argument is out of the defined range ({@code startInclusive},
     * {@code endInclusive})
     *
     * @param argumentName
     * @param startInclusive
     * @param endInclusive
     * @param actual
     */
    public KnxNumberOutOfRangeException(final String argumentName, final Number startInclusive, final Number endInclusive, final Number actual) {
        super(String.format("Value '%s' for argument '%s' is out of range '%s'..'%s'.", actual, argumentName, startInclusive, endInclusive));
    }

    /**
     * Exception if {@code actual} number argument is out of the defined range ({@code startInclusive},
     * {@code endInclusive}). This method can also obtain the raw data.
     *
     * @param argumentName
     * @param startInclusive
     * @param endInclusive
     * @param actual
     * @param rawData
     */
    public KnxNumberOutOfRangeException(final String argumentName, final Number startInclusive, final Number endInclusive, final Number actual,
                                        final byte[] rawData) {
        super(String.format("Value '%s' for argument '%s' is out of range '%s'..'%s'. Raw Data: %s", actual, argumentName, startInclusive,
                endInclusive, ByteFormatter.formatHexAsString(rawData)));
    }
}
