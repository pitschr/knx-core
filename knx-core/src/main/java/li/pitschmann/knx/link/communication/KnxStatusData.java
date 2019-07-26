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
import li.pitschmann.knx.link.body.address.KnxAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.utils.ByteFormatter;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Status data for KNX Address. This class preserves the immutability about KNX data,
 * but can be marked as dirty (=may not be update anymore) or as not-dirty (=updated).
 *
 * @author PITSCHR
 */
public final class KnxStatusData {
    private Instant timestamp;
    private KnxAddress sourceAddress;
    private APCI apci;
    private byte[] apciData;
    private boolean dirty;

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
        this.dirty = false; // reset the dirty flag
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

    /**
     * Returns if the instance is dirty
     *
     * @return Returns {@code true} if it is dirty or {@code false} if it is still false
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Sets if the current {@link KnxStatusData} is dirty or it isn't dirty.
     *
     * @param dirty dirty flag to be set ({@code true}) or cleared ({@code false})
     */
    void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(KnxStatusData.class)
                .add("dirty", this.dirty)
                .add("timestamp", this.timestamp)
                .add("sourceAddress", this.sourceAddress)
                .add("apci", this.apci)
                .add("apciData", ByteFormatter.formatHexAsString(this.apciData))
                .toString();
        // @formatter:on
    }
}