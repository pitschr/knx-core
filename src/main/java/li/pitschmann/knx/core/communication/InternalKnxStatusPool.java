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

import li.pitschmann.knx.core.CEMIAware;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.body.RequestBody;
import li.pitschmann.knx.core.body.RoutingIndicationBody;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.datapoint.BaseDataPointType;
import li.pitschmann.knx.core.datapoint.DataPointRegistry;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.utils.Maps;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Sleeper;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Map<KnxAddress, KnxStatusData> statusMap = Maps.newLinkedHashMap(1000);

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
    public void updateStatus(final KnxAddress address, final KnxStatusData statusData) {
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
    public void updateStatus(final CEMI cemi) {
        this.updateStatus(cemi.getDestinationAddress(), new KnxStatusData(cemi));
    }

    /**
     * Marks the status for given {@link KnxAddress} as dirty (not up-to-date)
     *
     * @param address {@link KnxAddress} for which the status should be marked as dirty
     */
    public void setDirty(final KnxAddress address) {
        Preconditions.checkNonNull(address);
        final var knxStatus = this.statusMap.get(address);
        if (knxStatus != null) {
            knxStatus.setDirty(true);
        }
    }

    /**
     * Marks the status for given RequestBody as dirty (not up-to-date) to inform
     * the status pool that the value of given request body may be obsolete (and
     * we have requested for most recent value from KNX)
     *
     * @param requestBody the request body we want to mark as dirty
     */
    public void setDirty(final @Nullable RequestBody requestBody) {
        if (requestBody instanceof CEMIAware) {
            final var cemi = ((CEMIAware) requestBody).getCEMI();
            setDirty(cemi.getDestinationAddress());
        }
    }

    @Override
    public boolean isUpdated(final KnxAddress address) {
        final var knxStatus = this.statusMap.get(Objects.requireNonNull(address));
        return knxStatus != null && !knxStatus.isDirty();
    }

    @Override
    public boolean existsStatusFor(final KnxAddress address) {
        return this.statusMap.containsKey(address);
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(final KnxAddress address) {
        return getStatusFor(address, true);
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(KnxAddress address, boolean mustUpToDate) {
        return getStatusForInternal(address, CoreConfigs.Event.STATUS_LOOKUP_TIMEOUT, TimeUnit.MILLISECONDS, true);
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
    private KnxStatusData getStatusForInternal(final KnxAddress address, final long duration, final TimeUnit unit, final boolean mustUpToDate) {
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
    public <V extends DataPointValue> V getValue(final KnxAddress address, final String dptId) {
        return getValue(address, dptId, true);
    }

    @Nullable
    @Override
    public <V extends DataPointValue> V getValue(final KnxAddress address, final String dptId, final boolean mustUpToDate) {
        final var statusData = this.getStatusFor(address, mustUpToDate);
        if (statusData != null) {
            @SuppressWarnings("unchecked") final V dataPointValue = (V) DataPointRegistry.getDataPointType(dptId).of(statusData.getData());
            return dataPointValue;
        }
        return null;
    }

    @Nullable
    @Override
    public <V extends DataPointValue> V getValue(final KnxAddress address, final BaseDataPointType<V> dpt) {
        return getValue(address, dpt, true);
    }

    @Nullable
    @Override
    public <V extends DataPointValue> V getValue(final KnxAddress address, final BaseDataPointType<V> dpt, final boolean mustUpToDate) {
        final var statusData = this.getStatusFor(address, mustUpToDate);
        if (statusData != null) {
            return dpt.of(statusData.getData());
        }
        return null;
    }

    @Override
    public Map<KnxAddress, KnxStatusData> copyStatusMap() {
        return Map.copyOf(this.statusMap);
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this) //
                .add("statusMap", this.statusMap) //
                .toString();
    }
}
