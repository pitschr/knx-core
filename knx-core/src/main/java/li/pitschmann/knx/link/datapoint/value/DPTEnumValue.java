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

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.datapoint.DPT20;
import li.pitschmann.knx.link.datapoint.DPT23;
import li.pitschmann.knx.link.datapoint.DPTEnum;
import li.pitschmann.knx.link.datapoint.DataPointTypeEnum;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An immutable Data Point Value for enumerated data point types like {@link DPT20}, {@link DPT23}, ...
 *
 * @author PITSCHR
 */
public final class DPTEnumValue<T extends Enum<T> & DataPointTypeEnum<T>> implements DataPointValue<DPTEnum<T>> {
    private final DPTEnum<T> dpt;
    private final T enumField;
    private final int ordinal;
    private final String description;
    private final byte[] byteArray;

    /**
     * Constructor is visible for package only. It is subject to be called by {@link DataPointTypeRegistry}.
     *
     * @param dpEnum
     * @param enumField
     * @param ordinal
     * @param description
     */
    public DPTEnumValue(final @Nonnull DPTEnum<T> dpEnum, final @Nonnull T enumField, final int ordinal, final @Nullable String description) {
        Preconditions.checkNonNull(dpEnum);
        Preconditions.checkNonNull(enumField);
        Preconditions.checkArgument(ordinal >= 0 && ordinal <= 0xFF, "The ordinal of enum should be between 0 and 255.");
        this.dpt = dpEnum;
        this.enumField = enumField;
        this.ordinal = ordinal;
        this.description = Objects.toString(description, "");
        this.byteArray = new byte[]{(byte) (this.ordinal & 0xFF)};
    }

    @Nonnull
    @Override
    public DPTEnum<T> getDPT() {
        return this.dpt;
    }

    /**
     * Returns the enum field value instance
     *
     * @return enum field value instance
     */
    @Nonnull
    public T getEnum() {
        return this.enumField;
    }

    /**
     * Returns the ordinal value of current enum value
     *
     * @return ordinal value
     */
    public int getOrdinal() {
        return this.ordinal;
    }

    /**
     * Returns the human-friendly description of enum value
     *
     * @return description
     */
    @Nonnull
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    @Override
    public byte[] toByteArray() {
        return this.byteArray;
    }

    @Nonnull
    @Override
    public String toText() {
        return getDescription();
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(DPTEnumValue.class)
                .add("dpt", this.dpt)
                .add("ordinal", this.ordinal)
                .add("enumField", this.enumField.getDeclaringClass().getName() + "." + this.enumField.name())
                .add("description", this.description)
                .add("byteArray", ByteFormatter.formatHexAsString(this.byteArray))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPTEnumValue) {
            final var other = (DPTEnumValue) obj;
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
