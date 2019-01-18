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

import li.pitschmann.knx.link.datapoint.*;

import java.util.*;

/**
 * Exception when an incompatible syntax was provided.
 *
 * @author PITSCHR
 */
public final class DataPointTypeIncompatibleSyntaxException extends KnxException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception in case incompatible {@code commands} array have been provided for the {@code dpt}.
     *
     * @param dpt
     * @param commands
     */
    public DataPointTypeIncompatibleSyntaxException(final DataPointType<?> dpt, final String[] commands) {
        super(String.format("Looks like you chose a wrong DPT. Given syntax is not compatible or not sufficient for '%s': %s", dpt.getClass().getSimpleName(),
                Arrays.toString(commands)));
    }
}
