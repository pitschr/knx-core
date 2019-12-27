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

package li.pitschmann.knx.core;

import li.pitschmann.knx.core.utils.ByteFormatter;

/**
 * Raw Data Aware interface.
 * <p>
 * Classes which implements this interface are aware of the raw data in single byte format.
 *
 * @author PITSCHR
 */
public interface SingleRawDataAware {
    /**
     * Returns the body as raw data in byte
     *
     * @return byte
     */
    byte getRawData();

    /**
     * Returns the raw data in hex-decimal string format
     *
     * @return raw data
     */
    default String getRawDataAsHexString() {
        return ByteFormatter.formatHex(getRawData());
    }
}
