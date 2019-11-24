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

package li.pitschmann.knx.core.body.address;

import li.pitschmann.knx.core.exceptions.KnxIllegalArgumentException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    private IndividualAddress(final @Nonnull byte[] addressRawData) {
        super(addressRawData);

        // byte 0: xxxx ....
        this.area = (addressRawData[0] & 0xF0) >>> 4;
        // byte 0: .... xxxx
        this.line = addressRawData[0] & 0x0F;
        // byte 1: xxxx xxxx
        this.device = Bytes.toUnsignedInt(addressRawData[1]);
    }

    /**
     * Returns an instance of {@link IndividualAddress}
     *
     * @param bytes complete byte array for {@link IndividualAddress}
     * @return a new immutable {@link IndividualAddress}
     */
    @Nonnull
    public static IndividualAddress of(final @Nonnull byte[] bytes) {
        // no validation required, validation will be done in KnxAddress class
        return new IndividualAddress(bytes);
    }

    /**
     * Returns the default {@link IndividualAddress} ({@code 0.0.0}). This will usually use the address from the
     * KNX Net/IP device.
     *
     * @return re-usable immutable default {@link IndividualAddress} ({@code 0.0.0})
     */
    @Nonnull
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
     * @param addressAsString
     * @return An new instance of {@link IndividualAddress}
     * or {@link KnxIllegalArgumentException} when a wrong format was provided
     */
    @Nonnull
    public static IndividualAddress of(final @Nonnull String addressAsString) {
        final String[] individualAddressAreas = addressAsString.split("\\.");
        if (individualAddressAreas.length == 3) {
            return of( //
                    Integer.valueOf(individualAddressAreas[0]), //
                    Integer.valueOf(individualAddressAreas[1]), //
                    Integer.valueOf(individualAddressAreas[2]) //
            );
        }
        throw new KnxIllegalArgumentException("Invalid Individual Address provided: " + addressAsString);
    }

    /**
     * Returns an instance of {@link IndividualAddress}
     *
     * @param area   [0..15]
     * @param line   [0..15]
     * @param device [0..255]
     * @return a new immutable {@link IndividualAddress}
     */
    @Nonnull
    public static IndividualAddress of(final int area, final int line, final int device) {
        if (area < 0 || area > 0x0F) {
            throw new KnxNumberOutOfRangeException("area", 0, 0x0F, area);
        } else if (line < 0 || line > 0x0F) {
            throw new KnxNumberOutOfRangeException("line", 0, 0x0F, line);
        } else if (device < 0 || device > 0xFF) {
            throw new KnxNumberOutOfRangeException("device", 0, 0xFF, device);
        }

        // byte 0: xxxx ....
        final var areaAsByte = (byte) ((area & 0x0F) << 4);
        // byte 0: .... xxxx
        final var lineAsByte = (byte) (line & 0x0F);
        // byte 1: xxxx xxxx
        final var deviceAsByte = (byte) device;

        // create bytes
        final var bytes = new byte[]{(byte) (areaAsByte | lineAsByte), deviceAsByte};
        return of(bytes);
    }

    @Nonnull
    @Override
    public AddressType getAddressType() {
        return AddressType.INDIVIDUAL;
    }

    @Nonnull
    @Override
    public String getAddress() {
        return this.area + "." + this.line + "." + this.device;
    }

    @Nonnull
    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = Strings.toStringHelper(this)
                .add("addressType", this.getAddressType())
                .add("address", this.getAddress());
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
        } else if (obj instanceof IndividualAddress) {
            final var other = (IndividualAddress) obj;
            return this.area == other.area && this.line == other.line && this.device == other.device;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getAddressType(), this.area, this.line, this.device);
    }
}
