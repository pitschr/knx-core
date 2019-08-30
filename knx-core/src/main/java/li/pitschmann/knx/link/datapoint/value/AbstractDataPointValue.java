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

import com.google.common.base.Strings;
import li.pitschmann.knx.link.datapoint.AbstractDataPointType;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Abstract implementation of {@link DataPointValue}
 *
 * @param <T>
 * @author PITSCHR
 */
abstract class AbstractDataPointValue<T extends AbstractDataPointType<?>> implements DataPointValue<T> {
    private final T dpt;

    public AbstractDataPointValue(final @Nonnull T dpt) {
        if (dpt == null) {
            throw new KnxNullPointerException("dpt");
        }
        this.dpt = dpt;
    }

    @Nonnull
    protected static String getValueAsText(final double value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Nonnull
    protected static String getValueAsText(final double value, final @Nullable String unit) {
        return getValueAsText(value) + getUnitAsSuffix(unit);
    }

    @Nonnull
    protected static String getValueAsText(final float value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Nonnull
    protected static String getValueAsText(final float value, final @Nullable String unit) {
        return getValueAsText(value) + getUnitAsSuffix(unit);
    }

    @Nonnull
    protected static String getValueAsText(final int value) {
        return Integer.toString(value);
    }

    @Nonnull
    protected static String getValueAsText(final int value, final @Nullable String unit) {
        return getValueAsText(value) + getUnitAsSuffix(unit);
    }

    @Nonnull
    protected static String getValueAsText(final long value) {
        return Long.toString(value);
    }

    @Nonnull
    protected static String getValueAsText(final long value, final @Nullable String unit) {
        return getValueAsText(value) + getUnitAsSuffix(unit);
    }

    @Nonnull
    protected static String getValueAsText(final Object value) {
        return String.valueOf(value);
    }

    @Nonnull
    protected static String getValueAsText(final Object value, final @Nullable String unit) {
        return getValueAsText(value) + getUnitAsSuffix(unit);
    }

    @Nonnull
    private static String getUnitAsSuffix(final @Nullable String unit) {
        return Strings.isNullOrEmpty(unit) ? "" : " " + unit;
    }

    /**
     * Returns the Data Point Type for the Value
     *
     * @return
     */
    @Nonnull
    @Override
    public T getDPT() {
        return this.dpt;
    }
}
