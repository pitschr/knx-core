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
import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.body.cemi.*;
import li.pitschmann.utils.*;

import javax.annotation.*;
import java.time.*;

/**
 * Status data for KNX Address. This class preserves the immutability.
 *
 * @author PITSCHR
 */
public final class KnxStatusData {
    private Instant timestamp;
    private KnxAddress sourceAddress;
    private APCI apci;
    private byte[] apciData;

    /**
     * Creates a new instance of {@link CEMI}
     *
     * @param cemi an instance of CEMI
     */
    public KnxStatusData(final CEMI cemi) {
        this(cemi.getSourceAddress(), cemi.getApci(), cemi.getApciData());
    }

    /**
     * Creates a new instance of {@link KnxStatusData}
     *
     * @param sourceAddress the initiator who updated the status
     * @param apci          the purpose why it was set
     * @param apciData      the value in byte array
     */
    public KnxStatusData(final KnxAddress sourceAddress, final APCI apci, final byte[] apciData) {
        this.timestamp = Instant.now();
        this.apci = apci;
        this.apciData = apciData.clone(); // defensive copy
        this.sourceAddress = sourceAddress;
    }

    /**
     * Returns the timestamp when this object has been created
     *
     * @return The {@link Instant} when this object has been created
     */
    public @Nonnull
    Instant getTimestamp() {
        return this.timestamp;
    }

    /**
     * Returns the source address about the last status
     *
     * @return A {@link KnxAddress}
     */
    public @Nonnull
    KnxAddress getSourceAddress() {
        return this.sourceAddress;
    }

    /**
     * Returns the type for APCI data
     *
     * @return An {@link APCI}
     */
    public @Nonnull
    APCI getApci() {
        return this.apci;
    }

    /**
     * Returns the APCI data
     *
     * @return byte array with APCI data
     */
    public @Nonnull
    byte[] getApciData() {
        return this.apciData.clone(); // defensive copy
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(KnxStatusData.class)
                .add("timestamp", this.timestamp)
                .add("sourceAddress", this.sourceAddress)
                .add("apci", this.apci)
                .add("apciData", ByteFormatter.formatHexAsString(this.apciData))
                .toString();
        // @formatter:on
    }
}
