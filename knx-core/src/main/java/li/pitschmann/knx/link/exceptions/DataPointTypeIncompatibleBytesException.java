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

import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.knx.utils.ByteFormatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Exception when an incompatible byte array were provided.
 *
 * @author PITSCHR
 */
public final class DataPointTypeIncompatibleBytesException extends KnxException {

    /**
     * Creates an exception in case incompatible {@code bytes} array have been provided for the {@code dpt}.
     *
     * @param dpt
     * @param bytes
     */
    public DataPointTypeIncompatibleBytesException(final @Nonnull DataPointType<?> dpt, final @Nullable byte[] bytes) {
        super(String.format("Looks like you chose a wrong DPT. Given bytes is not compatible for '%s': %s", dpt.getClass().getSimpleName(),
                ByteFormatter.formatHexAsString(bytes)));
    }
}
