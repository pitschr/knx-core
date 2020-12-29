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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.value.DPTEnumValue;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Maps;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

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
 * @param <T> type of DPT enumeration
 * @author PITSCHR
 */
public final class DPTEnum<T extends Enum<T> & DataPointEnum<T>> extends BaseDataPointType<DPTEnumValue<T>> {
    private final Map<Integer, DPTEnumValue<T>> values = Maps.newHashMap(255);
    private final String id;

    /**
     * Constructor is visible for package only. It is subject to be called by {@link DataPointRegistry}.
     *
     * @param id          id of DPT enumeration
     * @param description description of DPT enumeration
     */
    public DPTEnum(final String id, final String description) {
        super(description);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * This method is visible for package only. It is subject to be called by {@link DataPointRegistry}.
     *
     * @param enumValue value that should be used for DPT enumeration
     */
    final void addValue(final DPTEnumValue<T> enumValue) {
        Preconditions.checkArgument(!this.values.containsKey(enumValue.getOrdinal()),
                "Data point field with value '{}' already registered. Please check your DPT implementation!", enumValue);
        this.values.put(enumValue.getOrdinal(), enumValue);
    }

    /**
     * Returns a {@link DPTEnumValue} for specified value.
     *
     * @param value the enumeration key to return the DPT enumeration instance
     * @return data point enumeration value
     * @throws KnxEnumNotFoundException if enumeration with given value could not be found
     */
    public final DPTEnumValue<T> of(final int value) {
        final var dptEnumValue = this.values.get(value);
        if (dptEnumValue == null) {
            throw new KnxEnumNotFoundException(
                    String.format("Could not find data point enum value for dpt '%s' and value '%s'.", this.getId(), value));
        }
        return dptEnumValue;
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 1;
    }

    @Override
    protected DPTEnumValue<T> parse(final byte[] bytes) {
        return this.of(Bytes.toUnsignedInt(bytes[0]));
    }

    @Override
    protected boolean isCompatible(String[] args) {
        return args.length == 1 && !Strings.isNullOrEmpty(args[0]);
    }

    @Override
    protected DPTEnumValue<T> parse(final String[] args) {
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
            return this.of(Integer.parseInt(args[0]));
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


    /**
     * When comparing we take care of the id only.
     *
     * @param obj return {@code true} if equals, otherwise {@code false}
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPTEnum) {
            final var other = (DPTEnum) obj;
            return this.id.equals(other.id);
        }
        return false;
    }

    /**
     * Returns the hash code of {@link #id}
     *
     * @return hash code of {@link #id}
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
