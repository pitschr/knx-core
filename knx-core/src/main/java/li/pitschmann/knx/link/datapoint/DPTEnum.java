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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import li.pitschmann.knx.link.datapoint.value.DPTEnumValue;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.Bytes;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Data Point Type for enumeration fields (8 bits)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Unsigned Value)              |
 * Encoding    | U   U   U   U   U   U   U   U |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bit (U<sub>8</sub>)
 * </pre>
 *
 * @param <T>
 * @author PITSCHR
 */
public final class DPTEnum<T extends Enum<T> & DataPointTypeEnum<T>> extends AbstractDataPointType<DPTEnumValue<T>> {
    private final Map<Integer, DPTEnumValue<T>> values = Maps.newHashMapWithExpectedSize(255);

    /**
     * Constructor is visible for package only. It is subject to be called by {@link DataPointTypeRegistry}.
     *
     * @param id
     * @param description
     */
    public DPTEnum(final @Nonnull String id, final @Nonnull String description) {
        super(id, description);
    }

    /**
     * This method is visible for package only. It is subject to be called by {@link DataPointTypeRegistry}.
     *
     * @param enumValue
     */
    final void addValue(final @Nonnull DPTEnumValue<T> enumValue) {
        Preconditions.checkArgument(!this.values.containsKey(enumValue.getOrdinal()),
                String.format("Data point field with value '%s' already registered. Please check your DPT implementation!", enumValue));
        this.values.put(enumValue.getOrdinal(), enumValue);
    }

    /**
     * Returns a {@link DPTEnumValue} for specified value.
     *
     * @param value
     * @return data point enumeration value
     * @throws KnxEnumNotFoundException if enumeration with given value could not be found
     */
    @Nonnull
    public final DPTEnumValue<T> toValue(final int value) {
        final var dptEnumValue = this.values.get(value);
        if (dptEnumValue == null) {
            throw new KnxEnumNotFoundException(
                    String.format("Could not find data point enum value for dpt '%s' and value '%s'.", this.getId(), value));
        }
        return dptEnumValue;
    }

    @Nonnull
    @Override
    protected boolean isCompatible(final @Nonnull byte[] bytes) {
        return bytes.length == 1;
    }

    @Nonnull
    @Override
    protected DPTEnumValue<T> parse(final @Nonnull byte[] bytes) {
        return this.toValue(Bytes.toUnsignedInt(bytes[0]));
    }

    @Override
    protected boolean isCompatible(String[] args) {
        return args.length == 1 && !Strings.isNullOrEmpty(args[0]);
    }

    @Nonnull
    @Override
    protected DPTEnumValue<T> parse(final @Nonnull String[] args) {
        // first try to parse it as digits only
        var digitsOnly = true;
        for (final var c : args[0].toCharArray()) {
            if (!Character.isDigit(c)) {
                digitsOnly = false;
                break;
            }
        }

        if (digitsOnly) {
            // digits only
            return this.toValue(Integer.valueOf(args[0]));
        } else {
            // not digits only -> try with value name
            for (final var value : this.values.values()) {
                if (args[0].equalsIgnoreCase(value.getDescription())) {
                    // found
                    return value;
                }
            }
            // not found
            throw new KnxEnumNotFoundException(
                    String.format("Could not find data point enum value for dpt '%s' and value '%s'.", this.getId(), args[0]));
        }
    }
}
