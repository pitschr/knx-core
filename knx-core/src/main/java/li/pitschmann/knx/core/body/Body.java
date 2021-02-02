/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

package li.pitschmann.knx.core.body;

import li.pitschmann.knx.core.MultiRawDataAware;
import li.pitschmann.knx.core.header.Header;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Bytes;

/**
 * Marker interface for KNX packet body
 *
 * @author PITSCHR
 */
public interface Body extends MultiRawDataAware {

    /**
     * Returns the {@link ServiceType} to which the body belongs to
     *
     * @return {@link ServiceType}
     */
    ServiceType getServiceType();

    /**
     * Returns the byte array.
     * <p>
     * If {@code includeHeader} is {@code true} then byte array also contains
     * {@link Header} byte array, otherwise it is equivalent to {@link #toByteArray()}.
     *
     * @param includeHeader if {@code true} then returned byte array will also contain the KNX header array
     * @return byte array
     */
    default byte[] toByteArray(final boolean includeHeader) {
        if (includeHeader) {
            return Bytes.concat(Header.of(this).toByteArray(), toByteArray());
        } else {
            return toByteArray();
        }
    }
}
