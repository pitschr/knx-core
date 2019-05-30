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

import li.pitschmann.knx.link.datapoint.value.DataPointValue;

/**
 * {@link AbstractDataPointType} extended class with range and unit
 *
 * @author PITSCHR
 */
public abstract class AbstractRangeUnitDataPointType<T extends DataPointValue<?>, R extends Comparable<R>> extends AbstractDataPointType<T> {
    private final R lowerValue;
    private final R upperValue;
    private final String unit;

    public AbstractRangeUnitDataPointType(final String id, final String description, final R lowerValue, final R upperValue, final String unit) {
        super(id, description);
        this.lowerValue = lowerValue;
        this.upperValue = upperValue;
        this.unit = unit;
    }

    public R getLowerValue() {
        return this.lowerValue;
    }

    public R getUpperValue() {
        return this.upperValue;
    }

    public boolean isRangeClosed(final R value) {
        final var isWithinRange = value.compareTo(this.lowerValue) >= 0 && value.compareTo(this.upperValue) <= 0;
        if (!isWithinRange) {
            log.warn("Value '{}' is not within [{}, {}] for DPT '{}'", value, this.lowerValue, this.upperValue, this.getClass().getSimpleName());
        }
        return isWithinRange;
    }

    public String getUnit() {
        return this.unit;
    }

    @Override
    public String getDescription() {
        if (this.unit == null) {
            return super.getDescription();
        } else {
            return super.getDescription() + " (" + this.unit + ")";
        }
    }
}
