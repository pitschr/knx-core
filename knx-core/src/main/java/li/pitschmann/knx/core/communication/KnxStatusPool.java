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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.datapoint.DataPointType;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;

import java.util.Map;

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
    boolean isUpdated(final KnxAddress address);

    /**
     * Returns if {@link KnxStatusData} exists for given {@link KnxAddress}
     *
     * @param address {@link KnxAddress} for which the status should be checked for its existence
     * @return {@code true} if it exists (regardless if it is up-to-date or not), otherwise {@code false}
     */
    boolean existsStatusFor(final KnxAddress address);

    /**
     * Returns the current status for given {@link KnxAddress} immediately if it exists and is up-to-date already.
     * It will use a specific timeout default timeout taken from {@link CoreConfigs.Event#STATUS_LOOKUP_TIMEOUT}.
     *
     * @param address {@link KnxAddress} for which the status should be returned
     * @return {@code KnxStatusData} if exists, otherwise {@code null} when not exists or dirty within given default time
     */
    @Nullable
    KnxStatusData getStatusFor(final KnxAddress address);

    /**
     * Returns the current status for given {@link KnxAddress}.
     *
     * @param address      {@link KnxAddress} for which the status should be returned
     * @param mustUpToDate defines the knx status data must be up-to-date (non-dirty):
     *                     if it is {@code true} then status data must be up-to-date (non-dirty) to be accepted,
     *                     if it is {@code false} then status data may be returned regardless if the status data is up-to-date or not
     * @return {@code KnxStatusData} if exists, otherwise {@code null} when not exists (or dirty) within given time
     */
    @Nullable
    KnxStatusData getStatusFor(final KnxAddress address, final boolean mustUpToDate);

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type will be looked up using {@code dptId}
     *
     * @param address {@link KnxAddress} for which the {@link DataPointValue} should be returned
     * @param dptId   the DPT id we are looking for to get the {@link DataPointValue}
     * @param <V>     an instance of {@link DataPointValue}
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found or was not up-to-date for default time
     */
    @Nullable
    <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId);

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type will be looked up using {@code dptId}
     *
     * @param address      {@link KnxAddress} for which the {@link DataPointValue} should be returned
     * @param dptId        the DPT id we are looking for to get the {@link DataPointValue}
     * @param mustUpToDate defines the knx status data must be up-to-date (non-dirty):
     *                     if it is {@code true} then status data must be up-to-date (non-dirty) to be accepted,
     *                     if it is {@code false} then status data may be returned regardless if the status data is up-to-date or not
     * @param <V>          an instance of {@link DataPointValue}
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found.
     */
    @Nullable
    <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId, final boolean mustUpToDate);

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type is given {@code dpt}.
     *
     * @param address {@link KnxAddress} for which the {@link DataPointValue} should be returned
     * @param dpt     the DPT instance we are using to get the {@link DataPointValue}
     * @param <T>     an instance of {@link DataPointType}
     * @param <V>     an instance of {@link DataPointValue}
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found or was not up-to-date for default time
     */
    @Nullable
    <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt);

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type is given {@code dpt}.
     *
     * @param address      {@link KnxAddress} for which the {@link DataPointValue} should be returned
     * @param dpt          the DPT instance we are using to get the {@link DataPointValue}
     * @param mustUpToDate defines the knx status data must be up-to-date (non-dirty):
     *                     if it is {@code true} then status data must be up-to-date (non-dirty) to be accepted,
     *                     if it is {@code false} then status data may be returned regardless if the status data is up-to-date or not
     * @param <T>          an instance of {@link DataPointType}
     * @param <V>          an instance of {@link DataPointValue}
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found.
     */
    @Nullable
    <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt, final boolean mustUpToDate);

    /**
     * Returns copy of current status map with {@link KnxAddress} as key and {@link KnxStatusData} as value
     *
     * @return an immutable map
     */
    Map<KnxAddress, KnxStatusData> copyStatusMap();
}
