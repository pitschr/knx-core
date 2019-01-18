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

package li.pitschmann.knx.link.body.address;

import com.google.common.base.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;

import java.util.Objects;

/**
 * Individual Address (e.g. 1.0.100)
 *
 * @author PITSCHR
 */
public final class IndividualAddress extends KnxAddress {
    private static final IndividualAddress DEFAULT = new IndividualAddress(new byte[]{0x00, 0x00});
    private final int area;
    private final int line;
    private final int device;

    private IndividualAddress(final byte[] addressRawData) {
        super(addressRawData);

        // byte 0: xxxx ....
        this.area = (addressRawData[0] & 0xF0) >>> 4;
        // byte 0: .... xxxx
        this.line = addressRawData[0] & 0x0F;
        // byte 1: xxxx xxxx
        this.device = Bytes.toUnsignedInt(addressRawData[1]);
    }

    /**
     * Returns the default {@link IndividualAddress} ({@code 0.0.0}). This will usually use the address from the
     * KNX Net/IP router.
     *
     * @return default {@link IndividualAddress} ({@code 0.0.0})
     */
    public static IndividualAddress useDefault() {
        return DEFAULT;
    }

    /**
     * Returns an instance of {@link IndividualAddress}
     *
     * @param bytes complete byte array for {@link IndividualAddress}
     * @return immutable {@link IndividualAddress}
     */
    public static IndividualAddress of(final byte[] bytes) {
        // no validation required, validation will be done in KnxAddress class
        return new IndividualAddress(bytes);
    }

    /**
     * Returns an instance of {@link IndividualAddress}
     *
     * @param area
     * @param line
     * @param device
     * @return immutable {@link IndividualAddress}
     */
    public static IndividualAddress of(final int area, final int line, final int device) {
        if (area < 0 || area > 0x0F) {
            throw new KnxNumberOutOfRangeException("area", 0, 0x0F, area);
        } else if (line < 0 || line > 0x0F) {
            throw new KnxNumberOutOfRangeException("line", 0, 0x0F, line);
        } else if (device < 0 || device > 0xFF) {
            throw new KnxNumberOutOfRangeException("device", 0, 0xFF, device);
        }

        // byte 0: xxxx ....
        final byte areaAsByte = (byte) ((area & 0x0F) << 4);
        // byte 0: .... xxxx
        final byte lineAsByte = (byte) (line & 0x0F);
        // byte 1: xxxx xxxx
        final byte deviceAsByte = (byte) device;

        // create bytes
        final byte[] bytes = new byte[]{(byte) (areaAsByte | lineAsByte), deviceAsByte};
        return of(bytes);
    }

    @Override
    public AddressType getAddressType() {
        return AddressType.INDIVIDUAL;
    }

    @Override
    public String getAddress() {
        return this.area + "." + this.line + "." + this.device;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("addressType", this.getAddressType())
                .add("address", this.getAddress());
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof IndividualAddress) {
            final IndividualAddress other = (IndividualAddress) obj;
            return this.area == other.area && this.line == other.line && this.device == other.device;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAddressType(), this.area, this.line, this.device);
    }
}
