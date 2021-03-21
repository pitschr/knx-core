/*
 * Copyright (C) 2021 Pitschmann Christoph
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
import li.pitschmann.knx.core.cemi.APCI;
import li.pitschmann.knx.core.cemi.CEMI;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Strings;

import java.time.Instant;
import java.util.Arrays;

/**
 * Status data for KNX Address. This class preserves the immutability about KNX data,
 * but can be marked as dirty (=may not be update anymore) or as not-dirty (=updated).
 *
 * @author PITSCHR
 */
public final class KnxStatusData {
    private final Instant timestamp;
    private final KnxAddress sourceAddress;
    private final APCI apci;
    private final byte[] data;
    private boolean dirty;

    /**
     * Creates a new instance of {@link CEMI}
     *
     * @param cemi an instance of CEMI
     */
    public KnxStatusData(final CEMI cemi) {
        this(cemi.getSourceAddress(), cemi.getAPCI(), cemi.getData());
    }

    /**
     * Creates a new instance of {@link KnxStatusData}
     *
     * @param sourceAddress the initiator who updated the status
     * @param apci          the purpose why it was set
     * @param data          the value in byte array
     */
    public KnxStatusData(final KnxAddress sourceAddress, final APCI apci, final @Nullable byte[] data) {
        this.timestamp = Instant.now();
        this.apci = apci;
        this.data = data == null ? new byte[0] : data.clone(); // defensive copy
        this.sourceAddress = sourceAddress;
        this.dirty = false; // reset the dirty flag
    }

    /**
     * Returns the timestamp when this object has been created
     *
     * @return The {@link Instant} when this object has been created
     */
    public Instant getTimestamp() {
        return this.timestamp;
    }

    /**
     * Returns the source address about the last status
     *
     * @return A {@link KnxAddress}
     */
    public KnxAddress getSourceAddress() {
        return this.sourceAddress;
    }

    /**
     * Returns the APCI type
     *
     * @return An {@link APCI}
     */
    public APCI getAPCI() {
        return this.apci;
    }

    /**
     * Returns the APCI data
     *
     * @return byte array with APCI data
     */
    public byte[] getData() {
        return this.data.clone(); // defensive copy
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
     * <p>
     * Used for internal purposes only (e.g. marking it as dirty when
     * requested for an update to KNX Net/IP device)
     *
     * @param dirty dirty flag to be set ({@code true}) or cleared ({@code false})
     */
    void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dirty", this.dirty)
                .add("timestamp", this.timestamp)
                .add("sourceAddress", this.sourceAddress)
                .add("apci", this.apci)
                .add("data", Arrays.toString(this.data) + " (" + ByteFormatter.formatHexAsString(this.data) + ")")
                .toString();
        // @formatter:on
    }
}
