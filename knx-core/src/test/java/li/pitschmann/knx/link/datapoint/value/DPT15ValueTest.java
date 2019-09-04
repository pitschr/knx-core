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

package li.pitschmann.knx.link.datapoint.value;

import li.pitschmann.knx.link.datapoint.DPT15;
import li.pitschmann.knx.link.datapoint.value.DPT15Value.Flags;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test {@link DPT15Value}
 *
 * @author PITSCHR
 */
public final class DPT15ValueTest {
    /**
     * Test {@link DPT15Value}
     */
    @Test
    public void test() {
        this.assertValue(
                new byte[]{0x00, 0x00, 0x00, 0x00},
                new byte[3],
                new Flags(false, false, false, false, 0),
                "data: 0x00 00 00, flags: 0x00"
        );
        this.assertValue(
                new byte[]{0x11, 0x22, 0x33, 0x0F},
                new byte[]{0x11, 0x22, 0x33},
                new Flags(false, false, false, false, 0x0F),
                "data: 0x11 22 33, flags: 0x0F"
        );
        this.assertValue(
                new byte[]{0x44, 0x55, 0x66, (byte) 0xFF},
                new byte[]{0x44, 0x55, 0x66},
                new Flags(true, true, true, true, 0x0F),
                "data: 0x44 55 66, flags: 0xFF"
        );
        this.assertValue(
                new byte[]{0x22, 0x44, 0x11, (byte) 0xA3},
                new byte[]{0x22, 0x44, 0x11},
                new Flags(true, false, true, false, 0x03),
                "data: 0x22 44 11, flags: 0xA3"
        );
        this.assertValue(
                new byte[]{0x33, 0x77, 0x00, (byte) 0x5C},
                new byte[]{0x33, 0x77, 0x00},
                new Flags(false, true, false, true, 0x0C),
                "data: 0x33 77 00, flags: 0x5C"
        );
    }

    /**
     * Test {@link DPT15Value} with invalid arguments
     */
    @Test
    public void testInvalid() {
        assertThatThrownBy(() -> new DPT15Value(new byte[0])).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new DPT15Value(new byte[0], null)).isInstanceOf(IllegalArgumentException.class);
    }

    private void assertValue(final byte[] bytes, final byte[] accessIdentificationData, final Flags flags, final String text) {
        final var dptValue = new DPT15Value(accessIdentificationData, flags);
        final var dptValueByByte = new DPT15Value(bytes);

        // instance methods
        assertThat(dptValue.getAccessIdentificationData()).containsExactly(accessIdentificationData);
        assertThat(dptValue.getFlags()).isEqualTo(flags);
        assertThat(dptValue.toByteArray()).containsExactly(bytes);
        assertThat(dptValue.toText()).isEqualTo(text);

        // class methods
        assertThat(DPT15Value.toByteArray(accessIdentificationData, flags)).containsExactly(bytes);

        // equals
        assertThat(dptValue).isEqualTo(dptValue);
        assertThat(dptValueByByte).isEqualTo(dptValue);
        assertThat(dptValueByByte).hasSameHashCodeAs(dptValue);

        // not equals
        final var anotherAccessIdentificationData = accessIdentificationData.clone();
        anotherAccessIdentificationData[0] = (byte) ((anotherAccessIdentificationData[0] & 0xFF) == 0x00 ? 0x01 : 0x00);
        final var flagByte = flags.getAsByte();
        final var anotherFlags = new Flags((byte) (((byte) (flagByte & 0x80) == 0) ? flagByte | 0x80 : flagByte & 0x7F));
        assertThat(dptValue).isNotEqualTo(null);
        assertThat(dptValue).isNotEqualTo(new Object());
        assertThat(dptValue).isNotEqualTo(new DPT15Value(anotherAccessIdentificationData, flags));
        assertThat(dptValue).isNotEqualTo(new DPT15Value(accessIdentificationData, anotherFlags));

        // toString
        final var toString = String.format("DPT15Value{dpt=%s, accessIdentificationData=%s, flags=%s, byteArray=%s}", DPT15.ACCESS_DATA,
                ByteFormatter.formatHexAsString(accessIdentificationData), flags, ByteFormatter.formatHexAsString(bytes));
        assertThat(dptValue).hasToString(toString);
        assertThat(dptValueByByte).hasToString(toString);
    }

    /**
     * Test the failures of {@link DPT15Value}
     */
    @Test
    public void testFailures() {
        assertThatThrownBy(() -> DPT15Value.toByteArray(new byte[4], new Flags((byte) 0x00))).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test special cases for {@link DPT15Value#toByteArray(byte[], Flags)}
     */
    @Test
    public void testToByteArrayPadding() {
        final var flags = new Flags((byte) 0x80);
        assertThat(DPT15Value.toByteArray(new byte[0], flags)).containsExactly(0x00, 0x00, 0x00, 0x80);
        assertThat(DPT15Value.toByteArray(new byte[]{0x11}, flags)).containsExactly(0x00, 0x00, 0x11, 0x80);
        assertThat(DPT15Value.toByteArray(new byte[]{0x12, 0x11}, flags)).containsExactly(0x00, 0x12, 0x11, 0x80);
    }

    /**
     * Test {@link Flags}
     */
    @Test
    public void testFlags() {
        this.assertFlags((byte) 0x00, false, false, false, false, 0);
        this.assertFlags((byte) 0x80, true, false, false, false, 0);
        this.assertFlags((byte) 0x40, false, true, false, false, 0);
        this.assertFlags((byte) 0x20, false, false, true, false, 0);
        this.assertFlags((byte) 0x10, false, false, false, true, 0);
        this.assertFlags((byte) 0x0F, false, false, false, false, 15);
    }

    private void assertFlags(final byte b, final boolean error, final boolean permissionAccepted, final boolean readDirectionRightToLeft,
                             final boolean encryptionEnabled, final int index) {
        final var flags = new Flags(error, permissionAccepted, readDirectionRightToLeft, encryptionEnabled, index);
        final var flagsByByte = new Flags(b);

        // instance methods
        assertThat(flags.isError()).isEqualTo(error);
        assertThat(flags.isPermissionAccepted()).isEqualTo(permissionAccepted);
        assertThat(flags.isReadDirectionRightToLeft()).isEqualTo(readDirectionRightToLeft);
        assertThat(flags.isEncryptionEnabled()).isEqualTo(encryptionEnabled);
        assertThat(flags.getIndex()).isEqualTo(index);
        assertThat(flags.getAsByte()).isEqualTo(b);

        // equals
        assertThat(flags).isEqualTo(flags);
        assertThat(flagsByByte).isEqualTo(flags);
        assertThat(flagsByByte).hasSameHashCodeAs(flags);

        // not equals
        assertThat(flags).isNotEqualTo(null);
        assertThat(flags).isNotEqualTo(new Object());
        assertThat(flags).isNotEqualTo(new Flags(!error, permissionAccepted, readDirectionRightToLeft, encryptionEnabled, index));
        assertThat(flags).isNotEqualTo(new Flags(error, !permissionAccepted, readDirectionRightToLeft, encryptionEnabled, index));
        assertThat(flags).isNotEqualTo(new Flags(error, permissionAccepted, !readDirectionRightToLeft, encryptionEnabled, index));
        assertThat(flags).isNotEqualTo(new Flags(error, permissionAccepted, readDirectionRightToLeft, !encryptionEnabled, index));
        assertThat(flags).isNotEqualTo(new Flags(error, permissionAccepted, readDirectionRightToLeft, encryptionEnabled, (index + 1) % 15));

        // toString
        final var toString = String.format("Flags{error=%s, permissionAccepted=%s, readDirectionRightToLeft=%s, encryptionEnabled=%s, index=%s}", error,
                permissionAccepted, readDirectionRightToLeft, encryptionEnabled, index);
        assertThat(flags).hasToString(toString);
        assertThat(flagsByByte).hasToString(toString);
    }
}
