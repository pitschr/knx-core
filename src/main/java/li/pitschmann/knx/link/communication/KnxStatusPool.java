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

package li.pitschmann.knx.link.communication;

import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * KNX Status Pool holding all statuses of KNX addresses which have been sent by KNX/Net IP device
 *
 * @author PITSCHR
 */
public interface KnxStatusPool {
    /**
     * Returns if the status for given {@link KnxAddress} is up to date immediately
     *
     * @param address {@link KnxAddress} for which the status should be returned
     * @return {@code true} if status is up to date, otherwise {@code false} (not up to date)
     */
    boolean isUpdated(final @Nonnull KnxAddress address);

    /**
     * Returns if the status for given {@link KnxAddress} is up to date or wait up to
     * {@code duration} and {@code unit} until it is up to date.
     *
     * @param address  {@link KnxAddress} for which the status should be returned
     * @param duration duration of time unit
     * @param unit     time unit
     * @return {@code true} if status is up to date, otherwise {@code false} when not being up to date within given time out
     */
    boolean isUpdated(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit);

    /**
     * Returns the current status for given {@link KnxAddress}. May return the invalidated status.
     *
     * @param address {@link KnxAddress} for which the status should be returned
     * @return {@link KnxStatusData} or {@code null} if no status was found for given address
     */
    @Nullable
    KnxStatusData getStatusFor(final @Nonnull KnxAddress address);

    /**
     * Returns the status for given {@link KnxAddress} immediately if it exists already or up to
     * given {@code duration} and {@code unit}. This method doesn't take care if the status data
     * must be up-to-date or not.
     *
     * @param address  {@link KnxAddress} for which the status should be returned
     * @param duration duration of time unit
     * @param unit     time unit
     * @return {@code KnxStatusData} if exists, otherwise {@code null} when not exists within given time out
     */
    @Nullable
    KnxStatusData getStatusFor(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit);

    /**
     * Returns the status for given {@link KnxAddress} immediately if it exists already or up to
     * given {@code duration} and {@code unit}
     *
     * @param address  {@link KnxAddress} for which the status should be returned
     * @param duration duration of time unit
     * @param unit     time unit
     * @param mustUpToDate defines the knx status data must be up-to-date (non-dirty):
     *                     if it is {@code true} then status data must be up-to-date (non-dirty) to be accepted,
     *                     if it is {@code false} then status data may be returned regardless if the status data is up-to-date or not
     * @return {@code KnxStatusData} if exists, otherwise {@code null} when not exists (or dirty) within given time
     */
    @Nullable
    KnxStatusData getStatusFor(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit, final boolean mustUpToDate);


        /**
         * Returns the DPT value for given {@link KnxAddress}. The data point type will be looked up using {@code dptId}
         *
         * @param address
         * @param dptId
         * @return an instance of {@link DataPointValue} or {@code null} if no value could be found.
         */
    @Nullable
    <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId);

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type is given {@code dpt}.
     *
     * @param address
     * @param dpt
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found.
     */
    @Nullable
    <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt);
}
