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

import com.google.common.base.*;
import com.google.common.collect.*;
import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.body.cemi.*;
import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.knx.link.datapoint.value.*;
import org.slf4j.*;

import javax.annotation.*;
import java.util.*;

/**
 * KNX Status Pool covering all current statuses of KNX group addresses.
 *
 * @author PITSCHR
 */
public final class KnxStatusPool {
    private static final Logger LOG = LoggerFactory.getLogger(KnxStatusPool.class);
    private final Map<KnxAddress, KnxStatusData> statusMap = Maps.newHashMapWithExpectedSize(1024);

    /**
     * Updates the status by given {@link KnxAddress} and {@link KnxStatusData}
     *
     * @param address
     * @param statusData
     */
    public void updateStatus(final @Nonnull KnxAddress address, final KnxStatusData statusData) {
        LOG.trace("Update status by KNX address {}: {}", address, statusData);
        Preconditions.checkNotNull(address);
        this.statusMap.put(address, statusData);
    }

    /**
     * Updates the status by given {@link CEMI}
     *
     * @param cemi an instance of CEMI
     */
    public void updateStatus(final @Nonnull CEMI cemi) {
        this.updateStatus(cemi.getDestinationAddress(), new KnxStatusData(cemi));
    }

    /**
     * Returns the current status for given {@link KnxAddress}
     *
     * @param address
     * @return {@link KnxStatusData} or {@code null} if no status was found for given address
     */
    public @Nullable
    KnxStatusData getStatusFor(final KnxAddress address) {
        final KnxStatusData statusData = this.statusMap.get(address);
        if (statusData == null) {
            LOG.warn("No KNX status data found for address: {}", address);
        }
        return statusData;
    }

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type will be looked up using {@code dptId}
     *
     * @param address
     * @param dptId
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found.
     */
    public @Nullable
    <V extends DataPointValue<?>> V getValue(final KnxAddress address, final String dptId) {
        final KnxStatusData statusData = this.getStatusFor(address);
        if (statusData != null) {
            @SuppressWarnings("unchecked") final V dataPointValue = (V) DataPointTypeRegistry.getDataPointType(dptId).toValue(statusData.getApciData());
            return dataPointValue;
        }
        return null;
    }

    /**
     * Returns the DPT value for given {@link KnxAddress}. The data point type is given {@code dpt}.
     *
     * @param address
     * @param dpt
     * @return an instance of {@link DataPointValue} or {@code null} if no value could be found.
     */
    public @Nullable
    <T extends DataPointType<V>, V extends DataPointValue<T>> V getValue(final KnxAddress address, final T dpt) {
        final KnxStatusData statusData = this.getStatusFor(address);
        if (statusData != null) {
            return dpt.toValue(statusData.getApciData());
        }
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("statusMap", this.statusMap).toString();
    }
}
