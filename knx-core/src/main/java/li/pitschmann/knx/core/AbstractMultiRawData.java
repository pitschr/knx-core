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
import java.util.Arrays;

/**
 * This class represents a raw data of KNX/IP and is immutable.
 *
 * @author PITSCHR
 */
public abstract class AbstractMultiRawData implements MultiRawDataAware {
    private final byte[] rawData;

    protected AbstractMultiRawData(final byte[] rawData) {
        this.validate(rawData);
        this.rawData = rawData.clone();
    }

    @Override
    public byte[] getRawData() {
        return this.rawData.clone();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass().equals(obj.getClass())) {
            return Arrays.equals(this.getRawData(), ((AbstractMultiRawData) obj).getRawData());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getRawData());
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    /**
     * Validates the given {@code rawData} raw data it qualifies for the
     * current class. In case the validation fails a {@link KnxException}
     * will be thrown.
     *
     * @param rawData byte array to be validated
     */
    protected abstract void validate(final byte[] rawData);

    /**
     * Returns a string representation for current object
     *
     * @param inclRawData if the raw data should be a part of string representation,
     *                    if {@code false} then it won't be a part of string
     * @return string representation with or without raw data
     */
    public abstract String toString(final boolean inclRawData);
}
