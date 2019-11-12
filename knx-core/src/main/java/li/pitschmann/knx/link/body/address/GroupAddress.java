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

import li.pitschmann.knx.link.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.Bytes;
import li.pitschmann.utils.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * KNX Group Address (e.g. 1/2/100 or 1/330)
 *
 * @author PITSCHR
 */
public final class GroupAddress extends KnxAddress {
    private byte[] address;

    /**
     * Private Constructor for {@link GroupAddress}
     *
     * @param addressRawData
     */
    private GroupAddress(final @Nonnull byte[] addressRawData) {
        super(addressRawData);
        address = getRawData();
    }


    /**
     * Returns an instance of {@link GroupAddress}
     *
     * @param bytes complete byte array for {@link GroupAddress}
     * @return a new immutable {@link GroupAddress}
     */
    @Nonnull
    public static GroupAddress of(final @Nonnull byte[] bytes) {
        // no validation required, validation will be done in KnxAddress class
        return new GroupAddress(bytes);
    }

    /**
     * Returns an instance of {@link GroupAddress} based on format:
     * <ul>
     * <li>{@code X    } (The range must be between 1 and 65535)</li>
     * <li>{@code X/Y  } (The range must be between 0/1 and 31/2047)</li>
     * <li>{@code X/Y/Z} (The range must be between 0/0/1 and 31/7/255)</li>
     * </ul>
     * This method will call, based on occurrence of {@code /} (slash) character,
     * either {@link #of(int)}, {@link #of(int, int)} or {@link #of(int, int, int)}
     *
     * @param addressAsString
     * @return a new instance of {@link GroupAddress}
     * or {@link KnxIllegalArgumentException} when a wrong format was provided
     */
    @Nonnull
    public static GroupAddress of(final String addressAsString) {
        final String[] groupAddressAreas = addressAsString.split("/");
        if (groupAddressAreas.length == 3) {
            return of( //
                    Integer.valueOf(groupAddressAreas[0]), //
                    Integer.valueOf(groupAddressAreas[1]), //
                    Integer.valueOf(groupAddressAreas[2]) //
            );
        } else if (groupAddressAreas.length == 2) {
            return of( //
                    Integer.valueOf(groupAddressAreas[0]), //
                    Integer.valueOf(groupAddressAreas[1]) //
            );
        } else if (groupAddressAreas.length == 1) {
            return of(Integer.valueOf(groupAddressAreas[0]));
        }
        throw new KnxIllegalArgumentException("Invalid Group Address provided: " + addressAsString);
    }

    /**
     * Returns an instance of {@link GroupAddress} based on free-level topology.
     * <p/>
     * The range must be between 1 and 65535
     *
     * @param address
     * @return a new immutable {@link GroupAddress}
     */
    @Nonnull
    public static GroupAddress of(final int address) {
        if (address < 1 || address > 0xFFFF) {
            throw new KnxNumberOutOfRangeException("address", 1, 0xFFFF, address);
        }

        // byte 0: xxxx xxxx
        final var byte0 = (byte) (address >>> 8);
        // byte 1: xxxx xxxx
        final var byte1 = (byte) (address & 0xFF);

        // create bytes
        return of(new byte[]{byte0, byte1});
    }

    /**
     * Returns an instance of {@link GroupAddress} based on 2-level topology
     * <p/>
     * The range must be between 0/1 and 31/2047
     *
     * @param main
     * @param sub
     * @return a new immutable {@link GroupAddress}
     */
    @Nonnull
    public static GroupAddress of(final int main, final int sub) {
        if (main < 0 || main > 0x1F) {
            throw new KnxNumberOutOfRangeException("main", 0, 0x1F, main);
        } else if (sub < 0 || sub > 0x7FF) {
            throw new KnxNumberOutOfRangeException("sub", 0, 0x7FF, sub);
        } else if (main == 0 && sub == 0) {
            throw new KnxIllegalArgumentException("Group Address 0/0 is not allowed.");
        }

        // byte 0: xxxx x...
        final var mainAsByte = (byte) ((main & 0x1F) << 3);
        // byte 0: .... .xxx
        final var middleAsByte = (byte) ((sub & 0x0700) >>> 8);
        // byte 1: xxxx xxxx
        final var subAsByte = (byte) (sub & 0x00FF);

        // create bytes
        final var bytes = new byte[]{(byte) (mainAsByte | middleAsByte), subAsByte};
        return of(bytes);
    }

    /**
     * Returns an instance of {@link GroupAddress} based on 3-level topology
     * <p/>
     * The range must be between 0/0/1 and 31/7/255
     *
     * @param main
     * @param middle
     * @param sub
     * @return a new immutable {@link GroupAddress}
     */
    public static GroupAddress of(final int main, final int middle, final int sub) {
        if (main < 0 || main > 0x1F) {
            throw new KnxNumberOutOfRangeException("main", 0, 0x1F, main);
        } else if (middle < 0 || middle > 0x07) {
            throw new KnxNumberOutOfRangeException("middle", 0, 0x07, middle);
        } else if (sub < 0 || sub > 0xFF) {
            throw new KnxNumberOutOfRangeException("sub", 0, 0xFF, sub);
        } else if (main == 0 && middle == 0 && sub == 0) {
            throw new KnxIllegalArgumentException("Group Address 0/0/0 is not allowed.");
        }

        // byte 0: xxxx x...
        final var mainAsByte = (byte) ((main & 0x1F) << 3);
        // byte 0: .... .xxx
        final var middleAsByte = (byte) (middle & 0x07);
        // byte 1: xxxx xxxx
        final var subAsByte = (byte) sub;

        // create bytes
        final var bytes = new byte[]{(byte) (mainAsByte | middleAsByte), subAsByte};
        return of(bytes);
    }

    @Nonnull
    @Override
    public AddressType getAddressType() {
        return AddressType.GROUP;
    }

    /**
     * Returns Group Address in Free-Level
     *
     * @return group address in Free-level
     */
    @Nonnull
    @Override
    public String getAddress() {
        return String.valueOf(getAddressAsInt());
    }

    /**
     * Returns Group Address in Free-Level as an int
     *
     * @return group address as int (free-level)
     */
    public int getAddressAsInt() {
        return Bytes.toUnsignedInt(this.address[0], this.address[1]);
    }

    /**
     * Returns Group Address in 2-Level
     *
     * @return group address in 2-level
     */
    @Nonnull
    public String getAddressLevel2() {
        // byte 0: xxxx x...
        final var main = (address[0] & 0xF8) >>> 3;
        // byte 0: .... .xxx
        final var middle = address[0] & 0x07;
        // byte 1: xxxx xxxx
        final var sub = Bytes.toUnsignedInt(address[1]);

        return main + "/" + (middle << 8 | sub);
    }

    /**
     * Returns Group Address in 3-Level
     *
     * @return group address in 3-level
     */
    @Nonnull
    public String getAddressLevel3() {
        // byte 0: xxxx x...
        final var main = (address[0] & 0xF8) >>> 3;
        // byte 0: .... .xxx
        final var middle = address[0] & 0x07;
        // byte 1: xxxx xxxx
        final var sub = Bytes.toUnsignedInt(address[1]);

        return main + "/" + middle + "/" + sub;
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("addressType", this.getAddressType())
                .add("address", this.getAddress())
                .add("address(2-level)", this.getAddressLevel2())
                .add("address(3-level)", this.getAddressLevel3());
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof GroupAddress) {
            final var other = (GroupAddress) obj;
            return Objects.equals(this.address[0], other.address[0]) && Objects.equals(this.address[1], other.address[1]);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAddressType(), this.address[0], this.address[1]);
    }
}
