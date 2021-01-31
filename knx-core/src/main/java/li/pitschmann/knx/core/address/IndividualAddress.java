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
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Individual Address in format {@code x.y.z} (example: {@code 1.0.100})
 * <p>
 * Address of a given Device in an installation. The Individual Address
 * is a two octet value that consists of an eight bit Subnetwork Address
 * and an eight bit Device Address. If the Device Address is unique, then
 * also the Individual Address will be unique in the installation.
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
public final class IndividualAddress implements KnxAddress {
    private static final IndividualAddress DEFAULT = new IndividualAddress(0, 0, 0);
    private final int area;
    private final int line;
    private final int device;

    private IndividualAddress(final byte[] addressRawData) {
        this(
                // byte[0]: xxxx .... => area
                (addressRawData[0] & 0xF0) >>> 4,
                // byte[0]: .... xxxx => line
                addressRawData[0] & 0x0F,
                // byte[1]: xxxx xxxx => device
                Byte.toUnsignedInt(addressRawData[1])
        );
    }

    private IndividualAddress(final int area, final int line, final int device) {
        Preconditions.checkArgument(area >= 0 && area <= 0x0F,
                "Invalid area provided. Expected [0..15] but was: {}", area);
        Preconditions.checkArgument(line >= 0 && line <= 0x0F,
                "Invalid line provided. Expected [0..15] but was: {}", line);
        Preconditions.checkArgument(device >= 0 && device <= 0xFF,
                "Invalid device provided. Expected [0..255] but was: {}", device);

        this.area = area;
        this.line = line;
        this.device = device;
    }

    /**
     * Returns an instance of {@link IndividualAddress}
     *
     * @param bytes complete byte array for {@link IndividualAddress}
     * @return a new immutable {@link IndividualAddress}
     */
    public static IndividualAddress of(final byte[] bytes) {
        Preconditions.checkArgument(bytes.length == KnxAddress.STRUCTURE_LENGTH,
                "2 Bytes is expected but got: {}", Arrays.toString(bytes));

        return new IndividualAddress(bytes);
    }

    /**
     * Returns an instance of {@link IndividualAddress}
     *
     * @param area   [0..15]
     * @param line   [0..15]
     * @param device [0..255]
     * @return a new immutable {@link IndividualAddress}
     */
    public static IndividualAddress of(final int area, final int line, final int device) {
        return new IndividualAddress(area, line, device);
    }

    /**
     * Returns the default {@link IndividualAddress} ({@code 0.0.0}).
     * This will usually use the address from the KNX Net/IP device.
     *
     * @return re-usable immutable default {@link IndividualAddress} ({@code 0.0.0})
     */
    public static IndividualAddress useDefault() {
        return DEFAULT;
    }

    /**
     * Returns an instance of {@link IndividualAddress} based on format:
     * <ul>
     * <li>{@code X.Y.Z} (The range must be between 0.0.0 and 15.15.255)</li>
     * </ul>
     * This method will split based of {@code .} delimiter and will call
     * {@link #of(int, int, int)}
     *
     * @param addressAsString address in a string format to be parsed
     * @return An new instance of {@link IndividualAddress}
     * or {@link KnxIllegalArgumentException} when a wrong format was provided
     */
    public static IndividualAddress of(final String addressAsString) {
        final String[] individualAddressAreas = addressAsString.split("\\.");
        if (individualAddressAreas.length == 3) {
            return of( //
                    Integer.parseInt(individualAddressAreas[0]), //
                    Integer.parseInt(individualAddressAreas[1]), //
                    Integer.parseInt(individualAddressAreas[2]) //
            );
        }
        throw new IllegalArgumentException("Invalid Individual Address provided: " + addressAsString);
    }

    @Override
    public AddressType getAddressType() {
        return AddressType.INDIVIDUAL;
    }

    @Override
    public String getAddress() {
        return area + "." + line + "." + device;
    }

    @Override
    public byte[] toByteArray() {
        // byte 0: xxxx ....
        final var areaAsByte = (byte) ((area & 0x0F) << 4);
        // byte 0: .... xxxx
        final var lineAsByte = (byte) (line & 0x0F);
        // byte 1: xxxx xxxx
        final var deviceAsByte = (byte) device;

        // create bytes
        return new byte[]{(byte) (areaAsByte | lineAsByte), deviceAsByte};
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("address", getAddress())
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof IndividualAddress) {
            final var other = (IndividualAddress) obj;
            return Objects.equals(this.area, other.area)
                    && Objects.equals(this.line, other.line)
                    && Objects.equals(this.device, other.device);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, line, device);
    }
}
