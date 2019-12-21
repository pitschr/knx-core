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
 * Exception being thrown in case the a number is out of the range.
 *
 * @author PITSCHR
 */
public final class KnxNumberOutOfRangeException extends KnxException {
    /**
     * Exception if {@code actual} number argument is out of the defined range ({@code startInclusive},
     * {@code endInclusive})
     *
     * @param argumentName   argument that is out of range
     * @param startInclusive start number inclusive
     * @param endInclusive   end number inclusive
     * @param actual         the actual number
     */
    public KnxNumberOutOfRangeException(final @Nullable String argumentName,
                                        final @Nullable Number startInclusive,
                                        final @Nullable Number endInclusive,
                                        final @Nullable Number actual) {
        super("Value '{}' for argument '{}' is out of range '{}'..'{}'.",
                actual, argumentName, startInclusive, endInclusive);
    }

    /**
     * Exception if {@code actual} number argument is out of the defined range ({@code startInclusive},
     * {@code endInclusive}). This method can also obtain the raw data.
     *
     * @param argumentName   argument that is out of range
     * @param startInclusive start number inclusive
     * @param endInclusive   end number inclusive
     * @param actual         the actual number
     * @param rawData        raw byte array
     */
    public KnxNumberOutOfRangeException(final @Nullable String argumentName,
                                        final @Nullable Number startInclusive,
                                        final @Nullable Number endInclusive,
                                        final @Nullable Number actual,
                                        final @Nullable byte[] rawData) {
        super("Value '{}' for argument '{}' is out of range '{}'..'{}'. Raw Data: {}",
                actual, argumentName, startInclusive, endInclusive, rawData);
    }
}
