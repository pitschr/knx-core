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

import li.pitschmann.knx.core.exceptions.KnxException;

import javax.annotation.Nullable;

/**
 * This class represents a raw data of KNX/IP and is immutable.
 *
 * @author PITSCHR
 */
public abstract class AbstractSingleRawData implements SingleRawDataAware {
    private final byte rawData;

    protected AbstractSingleRawData(final byte rawData) {
        this.validate(rawData);
        this.rawData = rawData;
    }

    @Override
    public byte getRawData() {
        return this.rawData;
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass().equals(obj.getClass())) {
            return this.rawData == ((AbstractSingleRawData) obj).getRawData();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(this.rawData);
    }

    /**
     * Validates the given {@code rawData} raw data it qualifies for the current class. In case the validation fails
     * then a {@link KnxException} will be thrown.
     *
     * @param rawData byte array to be validated
     * @throws KnxException will be thrown when validation fails
     */
    protected abstract void validate(final byte rawData);
}
