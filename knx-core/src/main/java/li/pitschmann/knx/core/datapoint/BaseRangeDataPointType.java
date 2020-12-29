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
import li.pitschmann.knx.core.datapoint.value.DataPointValue;

/**
 * {@link BaseDataPointType} extended class with range capability
 *
 * @author PITSCHR
 */
public abstract class BaseRangeDataPointType<V extends DataPointValue, R extends Comparable<R>> extends BaseDataPointType<V> {
    private final R lowerValue;
    private final R upperValue;

    public BaseRangeDataPointType(final String description,
                                  final R lowerValue,
                                  final R upperValue,
                                  final @Nullable String unit) {
        super(description, unit);
        this.lowerValue = lowerValue;
        this.upperValue = upperValue;
    }

    public R getLowerValue() {
        return this.lowerValue;
    }

    public R getUpperValue() {
        return this.upperValue;
    }

    public final boolean isRangeClosed(final R value) {
        final var isWithinRange = value.compareTo(this.lowerValue) >= 0 && value.compareTo(this.upperValue) <= 0;
        if (!isWithinRange) {
            log.warn("Value '{}' is not within [{}, {}] for DPT '{}'", value, this.lowerValue, this.upperValue, this.getClass().getSimpleName());
        }
        return isWithinRange;
    }
}
