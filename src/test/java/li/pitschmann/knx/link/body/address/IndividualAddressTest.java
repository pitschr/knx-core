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

import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import li.pitschmann.utils.ByteFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link IndividualAddress}
 *
 * @author PITSCHR
 */
public final class IndividualAddressTest {
    /**
     * Tests <strong>valid</strong> addresses
     */
    @Test
    public void testValid() {
        this.assertIndividualAddress(0, 0, 0, new byte[]{0x00, 0x00});
        this.assertIndividualAddress(8, 7, 128, new byte[]{(byte) 0x87, (byte) 0x80});
        this.assertIndividualAddress(15, 15, 255, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link IndividualAddress#of(byte[])}
     */
    @Test
    public void invalidValueOf() {
        // null
        assertThatThrownBy(() -> IndividualAddress.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("addressRawData");

        // address should have 2 bytes
        assertThatThrownBy(() -> IndividualAddress.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("addressRawData");
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link IndividualAddress#of(int, int, int)}
     */
    @Test
    public void invalidCreate() {
        // negative numbers
        assertThatThrownBy(() -> IndividualAddress.of(-1, 0, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("area");
        assertThatThrownBy(() -> IndividualAddress.of(0, -1, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("line");
        assertThatThrownBy(() -> IndividualAddress.of(0, 0, -1)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("device");

        // too big numbers
        assertThatThrownBy(() -> IndividualAddress.of(0x0F + 1, 0, 0)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("area");
        assertThatThrownBy(() -> IndividualAddress.of(0, 0x0F + 1, 0)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("line");
        assertThatThrownBy(() -> IndividualAddress.of(0, 0, 0xFF + 1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("device");
    }

    /**
     * Test {@link IndividualAddress#useDefault()}
     */
    @Test
    public void testUseDefault() {
        final var addr = IndividualAddress.useDefault();
        assertThat(addr.getAddress()).isEqualTo("0.0.0");
        assertThat(addr.getRawData()).containsExactly(new byte[2]);
    }

    /**
     * Test {@link IndividualAddress#equals(Object)} and {@link IndividualAddress#hashCode()}
     */
    @Test
    public void testEqualsAndHashcode() {
        final var addrA = IndividualAddress.of(new byte[]{(byte) 0x93, 0x66});
        final var addrB = IndividualAddress.of(9, 3, 102);
        final var addrC = IndividualAddress.of(9, 3, 102);

        // equals
        assertThat(addrA).isEqualTo(addrA);
        assertThat(addrB).isEqualTo(addrA);
        assertThat(addrC).isEqualTo(addrA);
        assertThat(addrA).hasSameHashCodeAs(addrA);
        assertThat(addrB).hasSameHashCodeAs(addrA);
        assertThat(addrC).hasSameHashCodeAs(addrA);

        // not equals
        assertThat(addrA).isNotEqualTo(GroupAddress.of(new byte[]{(byte) 0x93, 0x66}));
        assertThat(addrA).isNotEqualTo(GroupAddress.of(9, 3, 102));
        assertThat(addrA).isNotEqualTo(IndividualAddress.of(8, 3, 102));
        assertThat(addrA).isNotEqualTo(IndividualAddress.of(9, 4, 102));
        assertThat(addrA).isNotEqualTo(IndividualAddress.of(9, 3, 101));
    }

    /**
     * Asserts the {@link IndividualAddress} if area, line and device parameters are correctly parsed
     * <ul>
     * <li>{@link IndividualAddress#of(int, int, int)}</li>
     * <li>{@link IndividualAddress#of(byte[])}</li>
     * </ul>
     * <p>
     * The param {@code bytes} is the stream to be compared as well against other parameters.
     *
     * @param area
     * @param line
     * @param bytes
     */
    private void assertIndividualAddress(final int area, final int line, final int device, final byte[] bytes) {
        final var testByCreate = IndividualAddress.of(area, line, device);
        final var testByCreateRawData = IndividualAddress.of(testByCreate.getRawData());
        final var testByValueOfRawData = IndividualAddress.of(bytes);

        assertThat(testByCreate.getRawData()).containsExactly(testByCreateRawData.getRawData());
        assertThat(testByCreate.getRawData()).containsExactly(testByValueOfRawData.getRawData());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);

        // check address print
        final var addressAsString = String.format("%s.%s.%s", area, line, device);
        assertThat(testByCreate.getAddress()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddress()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddress()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("IndividualAddress{addressType=%s, address=%s, rawData=%s}", AddressType.INDIVIDUAL,
                testByCreate.getAddress(), ByteFormatter.formatHexAsString(bytes)));
    }
}
