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
 * KNX Group Address (e.g. 1/2/100 or 1/330)
 *
 * @author PITSCHR
 */
public final class GroupAddress extends KnxAddress {
    private final int main;
    private final int middle;
    private final int sub;

    private GroupAddress(final byte[] addressRawData) {
        super(addressRawData);

        // byte 0: .xxx x...
        this.main = (addressRawData[0] & 0x78) >>> 3;
        // byte 0: .... .xxx
        this.middle = addressRawData[0] & 0x07;
        // byte 1: xxxx xxxx
        this.sub = Bytes.toUnsignedInt(addressRawData[1]);
    }

    /**
     * Returns an instance of {@link GroupAddress}
     *
     * @param bytes complete byte array for {@link GroupAddress}
     * @return immutable {@link GroupAddress}
     */
    public static GroupAddress of(final byte[] bytes) {
        // no validation required, validation will be done in KnxAddress class
        return new GroupAddress(bytes);
    }

    /**
     * Returns an instance of {@link GroupAddress} based on 3-level topology
     *
     * @param main
     * @param middle
     * @param sub
     * @return immutable {@link GroupAddress}
     */
    public static GroupAddress of(final int main, final int middle, final int sub) {
        if (main < 0 || main > 0x0F) {
            throw new KnxNumberOutOfRangeException("main", 0, 0x0F, middle);
        } else if (middle < 0 || middle > 0x07) {
            throw new KnxNumberOutOfRangeException("middle", 0, 0x07, middle);
        } else if (sub < 0 || sub > 0xFF) {
            throw new KnxNumberOutOfRangeException("sub", 0, 0xFF, sub);
        }

        // byte 0: .xxx x...
        final byte mainAsByte = (byte) ((main & 0x0F) << 3);
        // byte 0: .... .xxx
        final byte middleAsByte = (byte) (middle & 0x07);
        // byte 1: xxxx xxxx
        final byte subAsByte = (byte) sub;

        // create bytes
        final byte[] bytes = new byte[]{(byte) (mainAsByte | middleAsByte), subAsByte};
        return of(bytes);
    }

    /**
     * Returns an instance of {@link GroupAddress} based on 2-level topology
     *
     * @param main
     * @param sub
     * @return immutable {@link GroupAddress}
     */
    public static GroupAddress of(final int main, final int sub) {
        if (main < 0 || main > 0x0F) {
            throw new KnxNumberOutOfRangeException("main", 0, 0x0F, main);
        } else if (sub < 0 || sub > 0x7FF) {
            throw new KnxNumberOutOfRangeException("sub", 0, 0x7FF, sub);
        }

        // byte 0: .xxx x...
        final byte mainAsByte = (byte) ((main & 0x0F) << 3);
        // byte 0: .... .xxx
        final byte middleAsByte = (byte) ((sub & 0x0700) >>> 8);
        // byte 1: xxxx xxxx
        final byte subAsByte = (byte) (sub & 0x00FF);

        // create bytes
        final byte[] bytes = new byte[]{(byte) (mainAsByte | middleAsByte), subAsByte};
        return of(bytes);
    }

    @Override
    public AddressType getAddressType() {
        return AddressType.GROUP;
    }

    @Override
    public String getAddress() {
        return this.main + "/" + this.middle + "/" + this.sub;
    }

    public String getAddressLevel2() {
        return this.main + "/" + (this.middle << 8 | this.sub);
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("addressType", this.getAddressType())
                .add("address", this.getAddress())
                .add("address(2-level)", this.getAddressLevel2());
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
        } else if (obj instanceof GroupAddress) {
            final GroupAddress other = (GroupAddress) obj;
            return this.main == other.main && this.middle == other.middle && this.sub == other.sub;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAddressType(), this.main, this.middle, this.sub);
    }
}
