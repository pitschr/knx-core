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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.value.DPTEnumValue;

/**
 * Interface representing that data point type is an enumeration
 *
 * @param <T>
 * @author PITSCHR
 */
public interface DataPointTypeEnum<T extends Enum<T> & DataPointTypeEnum<T>> {
    /**
     * Returns the Data Point Value of current enumeration instance
     *
     * @return {@link DPTEnumValue}
     */
    @SuppressWarnings("unchecked")
    default DPTEnumValue<T> toValue() {
        final var thisObj = (T) this; // fake assignment to avoid 'unlikely-arg-type' warning
        return DataPointTypeRegistry.getDataPointType(thisObj);
    }

    /**
     * Handy method returning byte array to call {@link #toValue()#toByteArray()}
     *
     * @return byte array
     */
    default byte[] toByteArray() {
        return toValue().toByteArray();
    }

    /**
     * Returns the human friendly text of value
     *
     * @return
     */
    default String toText() {
        return toValue().getDescription();
    }

    /**
     * Returns the id
     *
     * @return id
     */
    default String getId() {
        final var value = toValue();
        return value.getDPT().getId() + " - " + value.getEnum();
    }

    /**
     * Returns the description
     *
     * @return description
     */
    default String getDescription() {
        final var value = toValue();
        return value.getDPT().getDescription() + " - " + value.getDescription();
    }
}
