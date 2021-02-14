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

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT20;
import li.pitschmann.knx.core.datapoint.DPT23;
import li.pitschmann.knx.core.datapoint.DPTEnum;
import li.pitschmann.knx.core.datapoint.DataPointEnum;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * An immutable Data Point Value for enumerated data point types like {@link DPT20}, {@link DPT23}, ...
 *
 * @author PITSCHR
 */
public final class DPTEnumValue<T extends Enum<T> & DataPointEnum<T>> implements DataPointValue {
    private final DPTEnum<T> dpt;
    private final T enumField;
    private final int value;
    private final String description;
    private final byte[] byteArray;

    /**
     * Constructor is visible for package only. It is subject to be called by {@link DataPointRegistry}.
     *
     * @param dpEnum      data point enum type
     * @param enumField   DPT enum value
     * @param value     enum ordinal value
     * @param description description for DPT enum value
     */
    public DPTEnumValue(final DPTEnum<T> dpEnum, final T enumField, final int value, final @Nullable String description) {
        Preconditions.checkNonNull(dpEnum);
        Preconditions.checkNonNull(enumField);
        Preconditions.checkArgument(value >= 0 && value <= 0xFF, "The value of enum should be between 0 and 255.");
        this.dpt = dpEnum;
        this.enumField = enumField;
        this.value = value;
        this.description = Objects.toString(description, "");
        this.byteArray = new byte[]{(byte) (value & 0xFF)};
    }

    /**
     * Returns the complete id like:
     * {@code 20.004 - High}
     *
     * @return complete id
     */
    public String getId() {
        return dpt.getId() + " - " + enumField.name();
    }

    /**
     * Returns the human-friendly full description of enum value
     * <p>
     * Example: {@code High}
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the human-friendly full description of enum field and value
     * <p>
     * Example: {@code Priority - High}
     *
     * @return full description
     */
    public String getFullDescription() {
        return dpt.getDescription() + " - " + description;
    }

    /**
     * Returns the {@link DPTEnum} of current value
     *
     * @return the actual {@link DPTEnum}
     */
    public DPTEnum<T> getDPT() {
        return dpt;
    }

    /**
     * Returns the enum value instance
     *
     * @return enum value instance
     */
    public T getEnum() {
        return enumField;
    }

    /**
     * Returns the value of current enum value
     *
     * @return value
     */
    public int getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[]{(byte) value};
    }

    @Override
    public String toText() {
        return getDescription();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", dpt.getId())
                .add("value", value)
                .add("enumField", enumField.getDeclaringClass().getName() + "." + enumField.name())
                .add("description", description)
                .add("byteArray", ByteFormatter.formatHexAsString(toByteArray()))
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
        return Objects.hash(dpt, enumField);
    }
}
