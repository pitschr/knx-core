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

package li.pitschmann.knx.core.datapoint.value;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.DPT15;
import li.pitschmann.knx.core.utils.ByteFormatter;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data Point Value for {@link DPT15} (15.xxx)
 *
 * <pre>
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names  | (Access Identification Data)                                  |
 *              | (D6)            (D5)            (D4)            (D3)          |
 * Encoding     | U   U   U   U   U   U   U   U   U   U   U   U   U   U   U   U |
 *              +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *              | (Access Identification Data)  | E   P   D   C   (Index)       |
 *              | (D2)            (D1)          |                               |
 *              | U   U   U   U   U   U   U   U | b   b   b   b   N   N   N   N |
 *              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> U<sub>4</sub> B<sub>4</sub> N<sub>4</sub>)
 * Range:      D6, D5, D4, D3, D2, D1 = [0 .. 9]
 *                Binary Encoded Value
 *                Digits of Access Identification code. Only a card or key number should be used.
 *                If 24 bits are not necessary, the most significant positions shall be set to zero.
 *             N = [0 .. 15]  Index
 *                Binary Encoded Value
 *             E = {0, 1}  Detection Error
 *                0 = No Error
 *                1 = Reading of Access Information Code were not successful
 *             P = {0, 1}  Permission
 *                0 = Not Accepted
 *                1 = Accepted
 *             D = {0, 1}  Read Direction (e.g. of Badge)
 *                0 = Left to Right
 *                1 = Right to Left
 *             C = {0, 1}  Encryption of Access Information
 *                0 = No Encryption
 *                1 = Encryption
 *
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT15Value extends AbstractDataPointValue<DPT15> {
    private final byte[] accessIdentificationData;
    private final Flags flags;

    public DPT15Value(final byte[] bytes) {
        super(DPT15.ACCESS_DATA);
        Preconditions.checkArgument(bytes.length == 4);
        // access identification data (byte 0 + byte 1 + byte 2)
        this.accessIdentificationData = new byte[3];
        System.arraycopy(bytes, 0, this.accessIdentificationData, 0, 3);
        // flags
        this.flags = new Flags(bytes[3]);
    }

    public DPT15Value(final byte[] accessIdentificationData, final Flags flags) {
        super(DPT15.ACCESS_DATA);
        Preconditions.checkArgument(accessIdentificationData.length == 3);
        // access identification data
        this.accessIdentificationData = getAccessIdentificationData(accessIdentificationData);
        // flags
        this.flags = Objects.requireNonNull(flags);
    }

    /**
     * Provide Access Identification Data with padding zeros in case the access identification data is smaller than 24
     * bits.
     *
     * @param accessIdentificationData byte array with access identification data
     * @return 3 byte array / 24 bits array
     */
    private static byte[] getAccessIdentificationData(final byte[] accessIdentificationData) {
        Preconditions.checkArgument(accessIdentificationData.length <= 3, "Access Identification Data must be 3 bytes or less.");
        byte[] newData = new byte[3];
        System.arraycopy(accessIdentificationData, 0, newData, newData.length - accessIdentificationData.length, accessIdentificationData.length);
        return newData;
    }

    /**
     * Converts 'Access Identification Data' data to 4-byte array
     *
     * @param accessIdentificationData byte array with access identification data
     * @param flags                    flags for access identification data
     * @return byte array
     */
    public static byte[] toByteArray(final byte[] accessIdentificationData, final Flags flags) {
        // ensure that access identification data is 24bits
        byte[] accessIdentificationDataEnsured = getAccessIdentificationData(accessIdentificationData);

        return new byte[]{ //
                accessIdentificationDataEnsured[0], //
                accessIdentificationDataEnsured[1], //
                accessIdentificationDataEnsured[2], //
                flags.getAsByte()};
    }

    public byte[] getAccessIdentificationData() {
        return this.accessIdentificationData.clone();
    }

    /**
     * Returns the flags for DPT15Value. See: {@link Flags}
     *
     * @return flags
     */
    public Flags getFlags() {
        return this.flags;
    }

    @Override
    public byte[] toByteArray() {
        return toByteArray(this.accessIdentificationData, this.flags);
    }

    @Override
    public String toText() {
        final var sb = new StringBuilder(30);
        sb.append("data: ")
                .append(ByteFormatter.formatHexAsString(this.accessIdentificationData))
                .append(", flags: ")
                .append(this.flags.toText());
        return sb.toString();
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("dpt", this.getDPT())
                .add("accessIdentificationData", ByteFormatter.formatHexAsString(this.accessIdentificationData))
                .add("flags", this.flags)
                .add("byteArray", ByteFormatter.formatHexAsString(this.toByteArray()))
                .toString();
        // @formatter:on
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DPT15Value) {
            final var other = (DPT15Value) obj;
            return Arrays.equals(this.accessIdentificationData, other.accessIdentificationData) //
                    && Objects.equals(this.flags, other.flags);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(this.accessIdentificationData), this.flags);
    }

    /**
     * Flags for {@link DPT15Value}
     *
     * @author PITSCHR
     */
    public static final class Flags {
        private boolean error;
        private boolean permissionAccepted;
        private boolean readDirectionRightToLeft;
        private boolean encryptionEnabled;
        private int index;

        /**
         * Create {@link Flags} given byte parameter
         *
         * @param b byte
         * @return new instance of {@link Flags}
         */
        public Flags(final byte b) {
            // flags
            this.error = (b & 0x80) != 0x00;
            this.permissionAccepted = (b & 0x40) != 0x00;
            this.readDirectionRightToLeft = (b & 0x20) != 0x00;
            this.encryptionEnabled = (b & 0x10) != 0x00;
            // index
            this.index = b & 0x0F;
        }

        /**
         * Create {@link Flags} with given parameters
         *
         * @param error                    if there was an error
         * @param permissionAccepted       if permission was accepted
         * @param readDirectionRightToLeft read from right to left (e.g. from badge)
         * @param encryptionEnabled        if encryption is enabled
         * @param index                    the index
         * @return new instance of {@link Flags}
         */
        public Flags(final boolean error,
                     final boolean permissionAccepted,
                     final boolean readDirectionRightToLeft,
                     final boolean encryptionEnabled,
                     final int index) {
            this.error = error;
            this.permissionAccepted = permissionAccepted;
            this.readDirectionRightToLeft = readDirectionRightToLeft;
            this.encryptionEnabled = encryptionEnabled;
            this.index = index;
        }

        public boolean isError() {
            return this.error;
        }

        public boolean isPermissionAccepted() {
            return this.permissionAccepted;
        }

        public boolean isReadDirectionRightToLeft() {
            return this.readDirectionRightToLeft;
        }

        public boolean isEncryptionEnabled() {
            return this.encryptionEnabled;
        }

        public int getIndex() {
            return this.index;
        }

        /**
         * Returns the byte flag settings as byte for {@link DPT15Value}
         *
         * @return byte
         */
        public byte getAsByte() {
            // byte with 4 bit flags and 4 bit index
            var b = (byte) this.index;
            if (this.error) {
                b |= 0x80;
            }
            if (this.permissionAccepted) {
                b |= 0x40;
            }
            if (this.readDirectionRightToLeft) {
                b |= 0x20;
            }
            if (this.encryptionEnabled) {
                b |= 0x10;
            }
            return b;
        }

        public String toText() {
            return ByteFormatter.formatHex(getAsByte());
        }

        @Override
        public String toString() {
            // @formatter:off
            return Strings.toStringHelper(this)
                    .add("error", this.error)
                    .add("permissionAccepted", this.permissionAccepted)
                    .add("readDirectionRightToLeft", this.readDirectionRightToLeft)
                    .add("encryptionEnabled", this.encryptionEnabled)
                    .add("index", this.index)
                    .toString();
            // @formatter:on
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Flags) {
                final var other = (Flags) obj;
                return Objects.equals(this.error, other.error) //
                        && Objects.equals(this.permissionAccepted, other.permissionAccepted) //
                        && Objects.equals(this.readDirectionRightToLeft, other.readDirectionRightToLeft) //
                        && Objects.equals(this.encryptionEnabled, other.encryptionEnabled) //
                        && Objects.equals(this.index, other.index);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.error, //
                    this.permissionAccepted, //
                    this.readDirectionRightToLeft, //
                    this.encryptionEnabled, //
                    this.index);
        }
    }
}
