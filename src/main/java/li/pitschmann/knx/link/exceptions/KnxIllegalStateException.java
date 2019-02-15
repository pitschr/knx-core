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

import java.util.Arrays;

/**
 * Exception for KNX/IP specific Illegal State
 *
 * @author PITSCHR
 */
public final class KnxIllegalStateException extends KnxException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception for KNX/IP Illegal State with message
     *
     * @param message
     */
    public KnxIllegalStateException(final String message) {
        super(message);
    }

    /**
     * Creates an exception for KNX/IP Illegal State with message and objects which contains more diagnostic data.
     *
     * @param message
     * @param obj
     */
    public KnxIllegalStateException(final String message, final Object... obj) {
        super(String.format("KNX Illegal State: '%s'. Actual data: %s", message, Arrays.toString(obj)));
    }
}
