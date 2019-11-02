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

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.utils.Maps;
import li.pitschmann.utils.Preconditions;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * KNX Status Pool covering all current statuses of KNX group addresses.
 *
 * @author PITSCHR
 */
public final class InternalKnxStatusPool implements KnxStatusPool {
    private static final Logger log = LoggerFactory.getLogger(InternalKnxStatusPool.class);
    private final Map<KnxAddress, KnxStatusData> statusMap = Maps.newHashMap(1024);

    /**
     * KNX status pool (package protected)
     */
    InternalKnxStatusPool() {
        log.trace("Internal KNX Status Pool object created.");
    }

    /**
     * Updates the status by given {@link KnxAddress} and {@link KnxStatusData}
     *
     * @param address    KNX address
     * @param statusData status data to be analyzed for pool
     */
    public void updateStatus(final @Nonnull KnxAddress address, final @Nonnull KnxStatusData statusData) {
        Preconditions.checkNonNull(address);
        Preconditions.checkNonNull(statusData);
        log.trace("Update status by KNX address {}: {}", address, statusData);
        this.statusMap.put(address, statusData);
    }

    /**
     * Updates the status by given {@link CEMI}
     *
     * @param cemi an instance of {@link CEMI} to be analyzed for pool
     */
    public void updateStatus(final @Nonnull CEMI cemi) {
        this.updateStatus(cemi.getDestinationAddress(), new KnxStatusData(cemi));
    }

    /**
     * Marks the status for given {@link KnxAddress} as dirty (not up-to-date)
     *
     * @param address {@link KnxAddress} for which the status should be marked as dirty
     */
    public void setDirty(final @Nonnull KnxAddress address) {
        Preconditions.checkNonNull(address);
        final var knxStatus = this.statusMap.get(address);
        if (knxStatus != null) {
            knxStatus.setDirty(true);
        }
    }

    /**
     * Marks the status for given RequestBody as dirty (not up-to-date)
     *
     * @param requestBody
     */
    public void setDirty(final @Nullable RequestBody requestBody) {
        // for tunneling
        if (requestBody instanceof TunnelingRequestBody) {
            setDirty(((TunnelingRequestBody) requestBody).getCEMI().getDestinationAddress());
        }
        // for routing
        else if (requestBody instanceof RoutingIndicationBody) {
            setDirty(((RoutingIndicationBody) requestBody).getCEMI().getDestinationAddress());
        }
    }

    @Override
    public boolean isUpdated(final @Nonnull KnxAddress address) {
        final var knxStatus = this.statusMap.get(Objects.requireNonNull(address));
        return knxStatus != null && !knxStatus.isDirty();
    }

    @Override
    public boolean existsStatusFor(final @Nonnull KnxAddress address) {
        return this.statusMap.containsKey(address);
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(final @Nonnull KnxAddress address) {
        return getStatusFor(address, true);
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(@Nonnull KnxAddress address, boolean mustUpToDate) {
        return getStatusForInternal(address, ConfigConstants.Event.STATUS_LOOKUP_TIMEOUT, TimeUnit.MILLISECONDS, true);
    }

    /**
     * Returns the status for given {@link KnxAddress} immediately if it exists already or up to
     * given {@code duration} and {@code unit}
     *
     * @param address      {@link KnxAddress} for which the status should be returned
     * @param duration     duration of time unit
     * @param unit         time unit
     * @param mustUpToDate defines the knx status data must be up-to-date (non-dirty):
     *                     if it is {@code true} then status data must be up-to-date (non-dirty) to be accepted,
     *                     if it is {@code false} then status data may be returned regardless if the status data is up-to-date or not
     * @return {@code KnxStatusData} if exists, otherwise {@code null} when not exists (or dirty) within given time
     */
    @Nullable
    private KnxStatusData getStatusForInternal(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit, final boolean mustUpToDate) {
        Preconditions.checkNonNull(address);
        Preconditions.checkNonNull(unit);
        final var end = System.currentTimeMillis() + unit.toMillis(duration);
        KnxStatusData statusData;
        do {
            statusData = this.statusMap.get(address);
        } while ((statusData == null || (mustUpToDate && statusData.isDirty())) && Sleeper.milliseconds(10) && System.currentTimeMillis() < end);

        if (statusData == null) {
            log.warn("No KNX status data found for address within defined time out: {}", address);
            return null;
        } else if (mustUpToDate && statusData.isDirty()) {
            log.warn("No up-to-date KNX status data for address within defined timeout: {}", address);
            return null;
        }
        return statusData;
    }

    @Nullable
    @Override
    public <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId) {
        return getValue(address, dptId, true);
    }

    @Nullable
    @Override
    public <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId, final boolean mustUpToDate) {
        final var statusData = this.getStatusFor(address, mustUpToDate);
        if (statusData != null) {
            @SuppressWarnings("unchecked") final V dataPointValue = (V) DataPointTypeRegistry.getDataPointType(dptId).toValue(statusData.getApciData());
            return dataPointValue;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt) {
        return getValue(address, dpt, true);
    }

    @Nullable
    @Override
    public <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt, final boolean mustUpToDate) {
        final var statusData = this.getStatusFor(address, mustUpToDate);
        if (statusData != null) {
            return dpt.toValue(statusData.getApciData());
        }
        return null;
    }

    @Nonnull
    @Override
    public Map<KnxAddress, KnxStatusData> copyStatusMap() {
        return Map.copyOf(this.statusMap);
    }

    @Nonnull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("statusMap", this.statusMap).toString();
    }
}
