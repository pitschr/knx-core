/*
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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPTEnumValue;

/**
 * Interface representing that data point type is an enumeration
 *
 * @param <T> the enumeration that should be a data point type enumeration
 * @author PITSCHR
 */
public interface DataPointEnum<T extends Enum<T> & DataPointEnum<T>> {
    /**
     * Returns the Data Point Value of current enumeration instance
     *
     * @return {@link DPTEnumValue}
     */
    default DPTEnumValue<T> of() {
        @SuppressWarnings("unchecked") final var thisObj = (T) this; // fake assignment to avoid 'unlikely-arg-type' warning
        return DataPointRegistry.getDataPointType(thisObj);
    }

    /**
     * Handy method returning byte array to call {@link #of()#toByteArray()}
     *
     * @return byte array
     */
    default byte[] toByteArray() {
        return of().toByteArray();
    }

    /**
     * Returns the human friendly text of value
     *
     * @return text
     */
    default String toText() {
        return of().toText();
    }

    /**
     * Returns the id
     *
     * @return id
     */
    default String getId() {
        return of().getId();
    }

    /**
     * Returns the human-friendly full description of enum value
     * <p>
     * Example: {@code High}
     *
     * @return description
     */
    default String getDescription() {
        return of().getDescription();
    }

    /**
     * Returns the human-friendly full description of enum field and value
     * <p>
     * Example: {@code Priority - High}
     *
     * @return full description
     */
    default String getFullDescription() {
        return of().getFullDescription();
    }

    /**
     * Returns the {@link DataPointType}
     *
     * @return DPTEnum
     */
    default DPTEnum<T> getDPT() {
        return of().getDPT();
    }

    /**
     * Returns the value of {@link DataPointEnum}
     *
     * @return value
     */
    default int getValue() {
        return of().getValue();
    }
}
