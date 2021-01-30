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

package li.pitschmann.knx.core.address;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;

/**
 * KNX Group Address (examples: {@code 1/2/100} or {@code 1/330})
 * <p>
 * The Group Address identifies one or several entities in the network
 * (one Shared Variable or several devices). It is a two octet value.
 * A device may have/know more than one Group Address.
 * <p>
 * Group Addresses are defined globally for the whole network. However
 * the specification allows local and global Group Addresses by defining
 * in each frame the maximum number of Routers to be crossed.
 *
 * <pre>
 * +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * | Byte 1                      | Byte 2                          |
 * | (1 octet)                   | (1 octet)                       |
 * +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * Source: KNX Specification, Glossary
 *
 * @author PITSCHR
 */
public final class GroupAddress implements KnxAddress {
    private final byte[] address;

    /**
     * Private Constructor for {@link GroupAddress}
     *
     * @param addressRawData address in two-byte array
     */
    private GroupAddress(final byte[] addressRawData) {
        address = addressRawData;
    }

    /**
     * Returns an instance of {@link GroupAddress}
     *
     * @param bytes complete byte array for {@link GroupAddress}
     * @return a new immutable {@link GroupAddress}
     */
    public static GroupAddress of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == KnxAddress.STRUCTURE_LENGTH,
                "2 Bytes is expected but got: {}", Arrays.toString(bytes));

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
     * @param addressAsString address in a string format to be parsed
     * @return a new instance of {@link GroupAddress}
     * or {@link KnxIllegalArgumentException} when a wrong format was provided
     */
    public static GroupAddress of(final String addressAsString) {
        final String[] groupAddressAreas = addressAsString.split("/");
        if (groupAddressAreas.length == 3) {
            return of( //
                    Integer.parseInt(groupAddressAreas[0]), //
                    Integer.parseInt(groupAddressAreas[1]), //
                    Integer.parseInt(groupAddressAreas[2]) //
            );
        } else if (groupAddressAreas.length == 2) {
            return of( //
                    Integer.parseInt(groupAddressAreas[0]), //
                    Integer.parseInt(groupAddressAreas[1]) //
            );
        } else if (groupAddressAreas.length == 1) {
            return of(Integer.parseInt(groupAddressAreas[0]));
        }
        throw new IllegalArgumentException("Invalid Group Address provided: " + addressAsString);
    }

    /**
     * Returns an instance of {@link GroupAddress} based on free-level topology.
     * <p>
     * The range must be between 1 and 65535
     *
     * @param address address as integer
     * @return a new immutable {@link GroupAddress}
     */
    public static GroupAddress of(final int address) {
        Preconditions.checkArgument(address >= 1 && address <= 65535,
                "Free-Level address must be between [1, 65535] but was: {}", address);

        // byte 0: xxxx xxxx
        final var byte0 = (byte) (address >>> 8);
        // byte 1: xxxx xxxx
        final var byte1 = (byte) (address & 0xFF);

        // create bytes
        return of(new byte[]{byte0, byte1});
    }

    /**
     * Returns an instance of {@link GroupAddress} based on 2-level topology
     * <p>
     * The range must be between 0/1 and 31/2047
     *
     * @param main main group [0..31]
     * @param sub  sub group [0..2047] (0 only allowed when main group != 0)
     * @return a new immutable {@link GroupAddress}
     */
    public static GroupAddress of(final int main, final int sub) {
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Main group of 2-Level address must be between [0, 31] but was: {}", main);
        Preconditions.checkArgument(sub >= 0 && sub <= 2047,
                "Sub group of 2-Level address must be between [0, 2047] but was: {}", sub);

        // special logic: 0/0 is not allowed
        if (main == 0 && sub == 0) {
            throw new IllegalArgumentException("Group address '0/0' is not allowed.");
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
     * <p>
     * The range must be between 0/0/1 and 31/7/255
     *
     * @param main   main group [0..31]
     * @param middle middle group [0..7]
     * @param sub    sub group [0..255] (0 only allowed, when main group != 0 or middle group != 0)
     * @return a new immutable {@link GroupAddress}
     */
    public static GroupAddress of(final int main, final int middle, final int sub) {
        Preconditions.checkArgument(main >= 0 && main <= 31,
                "Main group of 3-Level address must be between [0, 31] but was: {}", main);
        Preconditions.checkArgument(middle >= 0 && middle <= 7,
                "Middle group of 3-Level address must be between [0, 7] but was: {}", middle);
        Preconditions.checkArgument(sub >= 0 && sub <= 255,
                "Sub group of 3-Level address must be between [0, 255] but was: {}", sub);

        // special logic: 0/0/0 is not allowed
        if (main == 0 && middle == 0 && sub == 0) {
            throw new IllegalArgumentException("Group address '0/0/0' is not allowed.");
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

    @Override
    public AddressType getAddressType() {
        return AddressType.GROUP;
    }

    /**
     * Returns Group Address in Free-Level
     *
     * @return group address in Free-level
     */
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
    public String getAddressLevel2() {
        // byte 0: xxxx x...
        final var main = (address[0] & 0xF8) >>> 3;
        // byte 0: .... .xxx
        final var middle = address[0] & 0x07;
        // byte 1: xxxx xxxx
        final var sub = Byte.toUnsignedInt(address[1]);

        return main + "/" + (middle << 8 | sub);
    }

    /**
     * Returns Group Address in 3-Level
     *
     * @return group address in 3-level
     */
    public String getAddressLevel3() {
        // byte 0: xxxx x...
        final var main = (address[0] & 0xF8) >>> 3;
        // byte 0: .... .xxx
        final var middle = address[0] & 0x07;
        // byte 1: xxxx xxxx
        final var sub = Byte.toUnsignedInt(address[1]);

        return main + "/" + middle + "/" + sub;
    }

    @Override
    public byte[] getRawData() {
        return toByteArray();
    }

    public byte[] toByteArray() {
        return address.clone();
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("addressType", getAddressType())
                .add("address", getAddress())
                .add("address(2-level)", getAddressLevel2())
                .add("address(3-level)", getAddressLevel3())
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof GroupAddress) {
            final var other = (GroupAddress) obj;
            return Arrays.equals(this.address, other.address);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(address);
    }
}
