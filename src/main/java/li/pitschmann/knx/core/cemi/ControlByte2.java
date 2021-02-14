/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.SingleRawDataAware;
import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.KnxAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Second Control Field for {@link CEMI}, containing:
 * <ul>
 *     <li><strong>Destination Address Type (AT):</strong> This field shall specify if the
 *     destination address is individual or group address</li>
 *     <li><strong>Hop Count (HC):</strong> (bit 6-4) The number of hops that was done
 *     for this frame</li>
 *     <li><strong>Extended Frame Format (EFF):</strong> (bit 3 to 0) This field indicates
 *     the format of frame.</li>
 * </ul>
 * <pre>
 * +--7--+--6--+--5--+--4--+--3--+--2--+--1--+--0--+
 * | AT  |    Hop Count    | Extended Frame Format |
 * +-----+-----+-----+-----+-----+-----+-----+-----+
 * </pre>
 * See: KNX Specification, EMI/IMI
 *
 * @author PITSCHR
 */
public final class ControlByte2 implements SingleRawDataAware {
    public static final int DEFAULT_HOP_COUNT = 6;
    public static final int DEFAULT_FRAME_FORMAT = 0;
    private final AddressType addressType;
    private final int hopCount;
    private final int frameFormat;

    private ControlByte2(final byte ctrlRawData) {
        this(
                // x... .... destination address type
                // 0 = individual address
                // 1 = group address
                AddressType.valueOf((ctrlRawData & 0x80) >>> 7),
                // .xxx .... routing / hop count
                (ctrlRawData & 0x70) >>> 4,
                // .... xxxx extended frame format
                // .... 0000 for standard frame
                // .... 01xx for LTE frames
                // .... 1111 for Escape (reserved by KNX Assocation)
                ctrlRawData & 0xF
        );
    }

    private ControlByte2(final AddressType addressType,
                         final int hopCount,
                         final int frameFormat) {
        Preconditions.checkNonNull(addressType, "Address Type is required.");
        Preconditions.checkArgument(hopCount >= 0b0000_0000 && hopCount <= 0b0000_0111,
                "Incompatible hop count number. Expected [0..7] but was: {}", hopCount);
        Preconditions.checkArgument(frameFormat >= 0b0000_0000 && frameFormat <= 0b0000_1111,
                "Incompatible Extended Frame Format. Expected [0..15] but was: {}", frameFormat);

        this.addressType = addressType;
        this.hopCount = hopCount;
        this.frameFormat = frameFormat;
    }

    /**
     * Builds a new {@link ControlByte2} instance
     *
     * @param b byte
     * @return a new immutable {@link ControlByte2}
     */
    public static ControlByte2 of(final byte b) {
        return new ControlByte2(b);
    }

    /**
     * Creates a new {@link ControlByte2} instance with default settings:
     *
     * <ul>
     * <li>Address Type = {@link KnxAddress#getAddressType()}</li>
     * <li>Hop Count = {@link #DEFAULT_HOP_COUNT}</li>
     * <li>Frame Format = {@link #DEFAULT_FRAME_FORMAT}</li>
     * </ul>
     *
     * @param address to distinguish which address type from destination address should be used.
     * @return a new immutable {@link ControlByte1} with default hop count and frame format
     */
    public static ControlByte2 of(final KnxAddress address) {
        Preconditions.checkNonNull(address, "Address is required.");
        return new ControlByte2(address.getAddressType(), DEFAULT_HOP_COUNT, DEFAULT_FRAME_FORMAT);
    }

    /**
     * Creates a new {@link ControlByte2} instance
     *
     * @param addressType type of address used for the CEMI message
     * @param hopCount    routing maximum hops
     * @param frameFormat format of frame (standard, extended, ...)
     * @return a new immutable {@link ControlByte2}
     */
    public static ControlByte2 of(final AddressType addressType, final int hopCount, final int frameFormat) {
        return new ControlByte2(addressType, hopCount, frameFormat);
    }

    public AddressType getAddressType() {
        return this.addressType;
    }

    public int getHopCount() {
        return this.hopCount;
    }

    public int getFrameFormat() {
        return this.frameFormat;
    }

    @Override
    public byte toByte() {
        // x... .... destination address type
        // 0 = individual address
        // 1 = group address
        final var addressTypeAsByte = (byte) (addressType.getCodeAsByte() << 7);
        // .xxx .... routing / hop count
        final var hopCountAsByte = (byte) ((hopCount & 0x07) << 4);
        // .... xxxx extended frame format
        // .... 0000 for standard frame
        // .... 01xx for LTE frames
        // .... 1111 for Escape (reserved by KNX Assocation)
        final var frameFormatAsByte = (byte) (frameFormat & 0x0F);

        // create byte
        return (byte) (
                addressTypeAsByte            // bit 7: Destination Address Type (AT)
                        | hopCountAsByte     // bit 6+5+4: Hop Count (HC)
                        | frameFormatAsByte  // bit 3+2+1+0: Extended Frame Format (EFF)
        );
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("addressType", this.addressType.name())
                .add("hopCount", this.hopCount)
                .add("frameFormat", this.frameFormat)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ControlByte2) {
            final var other = (ControlByte2) obj;
            return Objects.equals(this.addressType, other.addressType) //
                    && this.hopCount == other.hopCount //
                    && this.frameFormat == other.frameFormat; //
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressType, hopCount, frameFormat);
    }
}
