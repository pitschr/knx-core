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

package li.pitschmann.knx.link.datapoint.value;

import com.google.common.base.*;
import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.utils.*;

import java.util.Objects;

/**
 * An immutable Data Point Value for enumerated data point types like {@link DPT20}, {@link DPT23}, ...
 *
 * @author PITSCHR
 */
public final class DPTEnumValue<T extends Enum<T> & DataPointTypeEnum<T>> implements DataPointValueEnum<T> {
    private final DPTEnum<T> dpt;
    private final T enumField;
    private final int value;
    private final String description;
    private final byte[] byteArray;

    /**
     * Constructor is visible for package only. It is subject to be called by {@link DataPointTypeRegistry}.
     *
     * @param dpEnum
     * @param enumField
     * @param value
     * @param description
     */
    public DPTEnumValue(final DPTEnum<T> dpEnum, final T enumField, final int value, final String description) {
        Preconditions.checkNotNull(dpEnum);
        Preconditions.checkNotNull(enumField);
        Preconditions.checkArgument(value >= 0 && value <= 0xFF, "Value should be between 0 and 255.");
        this.dpt = dpEnum;
        this.enumField = enumField;
        this.value = value;
        this.description = description;
        this.byteArray = new byte[]{(byte) (this.value & 0xFF)};
    }

    @Override
    public DPTEnum<T> getDPT() {
        return this.dpt;
    }

    public T getEnumField() {
        return this.enumField;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public byte[] toByteArray() {
        return this.byteArray;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(DPTEnumValue.class)
                .add("dpt", this.dpt)
                .add("enumField", this.enumField.getDeclaringClass().getName() + "." + this.enumField.name())
                .add("value", this.value)
                .add("description", this.description)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPTEnumValue) {
            final DPTEnumValue<?> other = (DPTEnumValue<?>) obj;
            return Objects.equals(this.dpt, other.dpt) //
                    && Objects.equals(this.enumField, other.enumField);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dpt, this.enumField);
    }
}
