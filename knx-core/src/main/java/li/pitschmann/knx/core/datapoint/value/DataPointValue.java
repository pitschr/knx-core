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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.datapoint.DataPointType;
import li.pitschmann.knx.core.utils.ByteFormatter;

/**
 * Interface for Data Point Value implementations
 *
 * @author PITSCHR
 */
public interface DataPointValue {
    /**
     * Returns the current Data Point Type instance
     *
     * @return {@link DataPointType}
     */
    DataPointType getDPT();

    /**
     * Returns the current Data Point Value as a byte array.
     *
     * @return byte array
     */
    byte[] toByteArray();

    /**
     * Returns the human-friendly text representation of Data Point Value
     * <p>
     * <strong>This text is not STABLE and should only be used for display purposes only!</strong>
     *
     * @return text
     */
    default String toText() {
        return ByteFormatter.formatHexAsString(toByteArray());
    }
}
