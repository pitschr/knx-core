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

package li.pitschmann.knx.link.body;

import li.pitschmann.knx.link.MultiRawDataAware;
import li.pitschmann.knx.link.header.Header;
import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.utils.Bytes;

import javax.annotation.Nonnull;

/**
 * Marker interface for Body
 *
 * @author PITSCHR
 */
public interface Body extends MultiRawDataAware {

    /**
     * Returns the {@link ServiceType} to which the body belongs to
     *
     * @return {@link ServiceType}
     */
    @Nonnull
    ServiceType getServiceType();

    /**
     * Returns the header and body as raw data in byte array
     *
     * @param withHeader if {@code true} then returned byte array will also contain the KNX header array
     * @return byte array
     */
    @Nonnull
    default byte[] getRawData(final boolean withHeader) {
        if (withHeader) {
            return Bytes.concat(Header.of(this).getRawData(), getRawData());
        } else {
            return getRawData();
        }
    }
}
