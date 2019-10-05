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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import li.pitschmann.knx.link.body.RequestBody;
import li.pitschmann.knx.link.body.RoutingIndicationBody;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.config.ConfigConstants;
import li.pitschmann.knx.link.datapoint.DataPointType;
import li.pitschmann.knx.link.datapoint.DataPointTypeRegistry;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * KNX Status Pool covering all current statuses of KNX group addresses.
 *
 * @author PITSCHR
 */
public final class KnxStatusPoolImpl implements KnxStatusPool {
    private static final Logger log = LoggerFactory.getLogger(KnxStatusPoolImpl.class);
    private final Map<KnxAddress, KnxStatusData> statusMap = Maps.newHashMapWithExpectedSize(1024);

    /**
     * Updates the status by given {@link KnxAddress} and {@link KnxStatusData}
     *
     * @param address    KNX address
     * @param statusData status data to be analyzed for pool
     */
    public void updateStatus(final @Nonnull KnxAddress address, final @Nullable KnxStatusData statusData) {
        log.trace("Update status by KNX address {}: {}", address, statusData);
        Preconditions.checkNotNull(address);
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
        Preconditions.checkNotNull(address);
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
        Preconditions.checkNotNull(address);
        final var knxStatus = this.statusMap.get(address);
        return knxStatus != null && !knxStatus.isDirty();
    }

    @Override
    public boolean isUpdated(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit) {
        return getStatusFor(address, duration, unit, true) != null;
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(final @Nonnull KnxAddress address) {
        return getStatusFor(address, ConfigConstants.Event.STATUS_LOOKUP_TIMEOUT, TimeUnit.MILLISECONDS, true);
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(@Nonnull KnxAddress address, boolean mustUpToDate) {
        if (mustUpToDate == true) {
            return getStatusFor(address);
        } else {
            final var statusData = this.statusMap.get(address);
            if (statusData == null) {
                log.debug("No KNX status data found for address: {}", address);
            }
            return statusData;
        }
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit) {
        return getStatusFor(address, duration, unit, true);
    }

    @Nullable
    @Override
    public KnxStatusData getStatusFor(final @Nonnull KnxAddress address, final long duration, final @Nonnull TimeUnit unit, final boolean mustUpToDate) {
        Preconditions.checkNotNull(address);
        Preconditions.checkNotNull(unit);
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
        } else {
            return statusData;
        }
    }

    @Nullable
    @Override
    public <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId) {
        final var statusData = this.getStatusFor(address);
        if (statusData != null) {
            @SuppressWarnings("unchecked") final V dataPointValue = (V) DataPointTypeRegistry.getDataPointType(dptId).toValue(statusData.getApciData());
            return dataPointValue;
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt) {
        final var statusData = this.getStatusFor(address);
        if (statusData != null) {
            return dpt.toValue(statusData.getApciData());
        }
        return null;
    }

    @Nonnull
    @Override
    public Map<KnxAddress, KnxStatusData> copyStatusMap() {
        return ImmutableMap.copyOf(this.statusMap);
    }

    @Nonnull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("statusMap", this.statusMap).toString();
    }
}
