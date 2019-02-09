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

import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test case for {@link GroupAddress}
 *
 * @author PITSCHR
 */
public final class GroupAddressTest {
    /**
     * Tests <strong>valid</strong> addresses for free-level group address structure
     */
    @Test
    public void testValidFreeLevel() {
        this.assertGroupAddressFreeLevel(1, new byte[]{0x00, 0x01});
        this.assertGroupAddressFreeLevel(4610, new byte[]{(byte) 0x12, (byte) 0x02});
        this.assertGroupAddressFreeLevel(65535, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    /**
     * Tests <strong>valid</strong> addresses for level 2 group address structure
     */
    @Test
    public void testValid2Level() {
        this.assertGroupAddress2Level(0, 0, new byte[]{0x00, 0x00});
        this.assertGroupAddress2Level(7, 1024, new byte[]{(byte) 0x3C, (byte) 0x00});
        this.assertGroupAddress2Level(31, 2047, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    /**
     * Tests <strong>valid</strong> addresses for level 3 group address structure
     */
    @Test
    public void testValid3Level() {
        this.assertGroupAddress3Level(0, 0, 0, new byte[]{0x00, 0x00});
        this.assertGroupAddress3Level(7, 3, 128, new byte[]{(byte) 0x3B, (byte) 0x80});
        this.assertGroupAddress3Level(31, 7, 255, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link GroupAddress#of(byte[])}
     */
    @Test
    public void invalidValueOf() {
        // null
        assertThatThrownBy(() -> GroupAddress.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("addressRawData");

        // address should have 2 bytes
        assertThatThrownBy(() -> GroupAddress.of(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("addressRawData");
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link GroupAddress#of(int)}
     */
    @Test
    public void invalidCreateFreeLevel() {
        // negative numbers
        assertThatThrownBy(() -> GroupAddress.of(-1)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("address");
        // 0 not possible (GA value, 0)
        assertThatThrownBy(() -> GroupAddress.of(0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("address");
        // too big numbers
        assertThatThrownBy(() -> GroupAddress.of(65536)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("address");
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link GroupAddress#of(int, int)}
     */
    @Test
    public void invalidCreate2Level() {
        // negative numbers
        assertThatThrownBy(() -> GroupAddress.of(-1, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("main");
        assertThatThrownBy(() -> GroupAddress.of(0, -1)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("sub");

        // too big numbers
        assertThatThrownBy(() -> GroupAddress.of(32, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("main");
        assertThatThrownBy(() -> GroupAddress.of(0, 2048)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("sub");
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link GroupAddress#of(int, int, int)}
     */
    @Test
    public void invalidCreate3Level() {
        // negative numbers
        assertThatThrownBy(() -> GroupAddress.of(-1, 0, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("main");
        assertThatThrownBy(() -> GroupAddress.of(0, -1, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("middle");
        assertThatThrownBy(() -> GroupAddress.of(0, 0, -1)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("sub");

        // too big numbers
        assertThatThrownBy(() -> GroupAddress.of(32, 0, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("main");
        assertThatThrownBy(() -> GroupAddress.of(0, 8, 0)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("middle");
        assertThatThrownBy(() -> GroupAddress.of(0, 0, 256)).isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("sub");
    }

    /**
     * Test {@link GroupAddress#equals(Object)} and {@link GroupAddress#hashCode()}
     */
    @Test
    public void testEqualsAndHashcode() {
        final var addrA = GroupAddress.of(new byte[]{0x19, (byte) 0xC8});
        final var addrB = GroupAddress.of(3, 456);
        final var addrC = GroupAddress.of(3, 456);
        final var addrD = GroupAddress.of(3, 1, 200);
        final var addrE = GroupAddress.of(6600);

        // equals
        assertThat(addrA).isEqualTo(addrA);
        assertThat(addrB).isEqualTo(addrA);
        assertThat(addrC).isEqualTo(addrA);
        assertThat(addrD).isEqualTo(addrA);
        assertThat(addrE).isEqualTo(addrA);
        assertThat(addrA).hasSameHashCodeAs(addrA);
        assertThat(addrB).hasSameHashCodeAs(addrA);
        assertThat(addrC).hasSameHashCodeAs(addrA);
        assertThat(addrD).hasSameHashCodeAs(addrA);
        assertThat(addrE).hasSameHashCodeAs(addrA);

        // not equals
        assertThat(addrA).isNotEqualTo(IndividualAddress.of(new byte[]{0x19, (byte) 0xC8}));
        assertThat(addrA).isNotEqualTo(IndividualAddress.of(3, 1, 200));
        assertThat(addrA).isNotEqualTo(GroupAddress.of(3, 457));
        assertThat(addrA).isNotEqualTo(GroupAddress.of(2, 456));
        assertThat(addrA).isNotEqualTo(GroupAddress.of(3, 968));
    }

    /**
     * Asserts the {@link GroupAddress} (Free-Level) if group are correctly parsed
     * <ul>
     * <li>{@link GroupAddress#of(int)}</li>
     * <li>{@link GroupAddress#of(byte[])}</li>
     * </ul>
     * <p>
     * The param {@code bytes} is the stream to be compared as well against other parameters.
     *
     * @param address
     * @param bytes
     */
    private void assertGroupAddressFreeLevel(final int address, final byte[] bytes) {
        final GroupAddress testByCreate = GroupAddress.of(address);
        final GroupAddress testByCreateRawData = GroupAddress.of(testByCreate.getRawData());
        final GroupAddress testByValueOfRawData = GroupAddress.of(bytes);

        assertThat(testByCreate.getRawData()).containsExactly(testByCreateRawData.getRawData());
        assertThat(testByCreate.getRawData()).containsExactly(testByValueOfRawData.getRawData());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.GROUP);

        // check address print
        final String addressAsString = String.valueOf(address);
        assertThat(testByCreate.getAddress()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddress()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddress()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("GroupAddress{addressType=%s, address=%s, address(2-level)=%s, address(3-level)=%s, rawData=%s}",
                AddressType.GROUP, testByCreate.getAddress(), testByCreate.getAddressLevel2(), testByCreate.getAddressLevel3(), ByteFormatter.formatHexAsString(bytes)));
    }

    /**
     * Asserts the {@link GroupAddress} (2-Level) if main and sub groups are correctly parsed
     * <ul>
     * <li>{@link GroupAddress#of(int, int)}</li>
     * <li>{@link GroupAddress#of(byte[])}</li>
     * </ul>
     * <p>
     * The param {@code bytes} is the stream to be compared as well against other parameters.
     *
     * @param main
     * @param sub
     * @param bytes
     */
    private void assertGroupAddress2Level(final int main, final int sub, final byte[] bytes) {
        final GroupAddress testByCreate = GroupAddress.of(main, sub);
        final GroupAddress testByCreateRawData = GroupAddress.of(testByCreate.getRawData());
        final GroupAddress testByValueOfRawData = GroupAddress.of(bytes);

        assertThat(testByCreate.getRawData()).containsExactly(testByCreateRawData.getRawData());
        assertThat(testByCreate.getRawData()).containsExactly(testByValueOfRawData.getRawData());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.GROUP);

        // check address print
        final String addressAsString = String.format("%s/%s", main, sub);
        assertThat(testByCreate.getAddressLevel2()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddressLevel2()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddressLevel2()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("GroupAddress{addressType=%s, address=%s, address(2-level)=%s, address(3-level)=%s, rawData=%s}",
                AddressType.GROUP, testByCreate.getAddress(), testByCreate.getAddressLevel2(), testByCreate.getAddressLevel3(), ByteFormatter.formatHexAsString(bytes)));
    }

    /**
     * Asserts the {@link GroupAddress} (3-Level) if main, middle and sub groups are correctly parsed
     * <ul>
     * <li>{@link GroupAddress#of(int, int, int)}</li>
     * <li>{@link GroupAddress#of(byte[])}</li>
     * </ul>
     * <p>
     * The param {@code bytes} is the stream to be compared as well against other parameters.
     *
     * @param main
     * @param middle
     * @param sub
     * @param bytes
     */
    private void assertGroupAddress3Level(final int main, final int middle, final int sub, final byte[] bytes) {
        final GroupAddress testByCreate = GroupAddress.of(main, middle, sub);
        final GroupAddress testByCreateRawData = GroupAddress.of(testByCreate.getRawData());
        final GroupAddress testByValueOfRawData = GroupAddress.of(bytes);

        assertThat(testByCreate.getRawData()).containsExactly(testByCreateRawData.getRawData());
        assertThat(testByCreate.getRawData()).containsExactly(testByValueOfRawData.getRawData());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.GROUP);

        // check address print
        final String addressAsString = String.format("%s/%s/%s", main, middle, sub);
        assertThat(testByCreate.getAddressLevel3()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddressLevel3()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddressLevel3()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("GroupAddress{addressType=%s, address=%s, address(2-level)=%s, address(3-level)=%s, rawData=%s}",
                AddressType.GROUP, testByCreate.getAddress(), testByCreate.getAddressLevel2(), testByCreate.getAddressLevel3(), ByteFormatter.formatHexAsString(bytes)));
    }
}
