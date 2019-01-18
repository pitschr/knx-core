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

package li.pitschmann.knx.link.body.cemi;

import com.google.common.base.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;

public final class ControlByte2 extends AbstractSingleRawData {
    private static final int DEFAULT_HOP_COUNT = 6;
    private static final int DEFAULT_FRAME_FORMAT = 0;
    private final AddressType addressType;
    private final int hopCount;
    private final int extendedFrameFormat;

    private ControlByte2(final byte ctrlRawData) {
        super(ctrlRawData);

        // x... .... destination address type
        // 0 = individual address
        // 1 = group address
        this.addressType = AddressType.valueOf((ctrlRawData & 0x80) >>> 7);
        // .xxx .... routing / hop count
        this.hopCount = (ctrlRawData & 0x70) >>> 4;
        // .... xxxx extended frame format
        // .... 0000 for standard frame
        // .... 01xx for LTE frames
        // .... 1111 for Escape (reserved by KNX Assocation)
        this.extendedFrameFormat = ctrlRawData & 0xF;
    }

    /**
     * Builds a new {@link ControlByte2} instance
     *
     * @param b byte
     * @return immutable {@link ControlByte2}
     */
    public static ControlByte2 valueOf(final byte b) {
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
     * @param knxAddress to distinguish which address type should be used.
     * @return immutable {@link ControlByte1} with default hop count and frame format
     */
    public static ControlByte2 useDefault(final KnxAddress knxAddress) {
        // validate
        if (knxAddress == null) {
            throw new KnxNullPointerException("knxAddress");
        }
        return create(knxAddress.getAddressType(), DEFAULT_HOP_COUNT, DEFAULT_FRAME_FORMAT);
    }

    /**
     * Creates a new {@link ControlByte2} instance
     *
     * @param addressType
     * @param hopCount
     * @param frameFormat
     * @return immutable {@link ControlByte2}
     */
    public static ControlByte2 create(final AddressType addressType, final int hopCount, final int frameFormat) {
        // validate
        if (addressType == null) {
            throw new KnxNullPointerException("addressType");
        } else if (hopCount < 0 || hopCount > 0x07) {
            throw new KnxNumberOutOfRangeException("hopCount", 0, 0x07, hopCount);
        } else if (frameFormat < 0 || frameFormat > 0x0F) {
            throw new KnxNumberOutOfRangeException("frameFormat", 0, 0x0F, frameFormat);
        }

        // x... .... destination address type
        // 0 = individual address
        // 1 = group address
        final byte addressTypeAsByte = (byte) (addressType.getCodeAsByte() << 7);
        // .xxx .... routing / hop count
        final byte hopCountAsByte = (byte) ((hopCount & 0x07) << 4);
        // .... xxxx extended frame format
        // .... 0000 for standard frame
        // .... 01xx for LTE frames
        // .... 1111 for Escape (reserved by KNX Assocation)
        final byte frameFormatAsByte = (byte) (frameFormat & 0x0F);

        // create byte
        final byte b = (byte) (addressTypeAsByte | hopCountAsByte | frameFormatAsByte);
        return valueOf(b);
    }

    @Override
    protected void validate(final byte ctrlRawData) {
        // nothing to be validated
    }

    public AddressType getAddressType() {
        return this.addressType;
    }

    public int getHopCount() {
        return this.hopCount;
    }

    public int getExtendedFrameFormat() {
        return this.extendedFrameFormat;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("addressType", this.addressType)
                .add("hopCount", this.hopCount + " (" + ByteFormatter.formatHex(this.hopCount) + ")")
                .add("extendedFrameFormat", this.extendedFrameFormat + " (" + ByteFormatter.formatHex(this.extendedFrameFormat) + ")")
                .add("rawData", this.getRawDataAsHexString())
                .toString();
        // @formatter:on
    }
}
