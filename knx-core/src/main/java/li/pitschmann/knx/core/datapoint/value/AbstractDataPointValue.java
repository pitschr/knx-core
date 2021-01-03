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
import li.pitschmann.knx.core.datapoint.DataPointType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Abstract implementation of {@link DataPointValue}
 *
 * @author PITSCHR
 */
abstract class AbstractDataPointValue<T extends DataPointType> implements DataPointValue {
    private final T dpt;

    protected AbstractDataPointValue(final T dpt) {
        this.dpt = Objects.requireNonNull(dpt);
    }

    protected static String getValueAsText(final double value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    protected static String getValueAsText(final float value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    protected static String getValueAsText(final int value) {
        return Integer.toString(value);
    }

    protected static String getValueAsText(final long value) {
        return Long.toString(value);
    }

    protected static String getValueAsText(final @Nullable Object value) {
        return String.valueOf(value);
    }

    /**
     * Returns the Data Point Type for the Value
     *
     * @return data point type
     */
    public final T getDPT() {
        return this.dpt;
    }
}
