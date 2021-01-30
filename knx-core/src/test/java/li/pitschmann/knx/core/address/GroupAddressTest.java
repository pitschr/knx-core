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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link GroupAddress}
 *
 * @author PITSCHR
 */
final class GroupAddressTest {
    @Test
    @DisplayName("Test free-level group address structure")
    void testValidFreeLevel() {
        assertGroupAddressFreeLevel(1, new byte[]{0x00, 0x01});
        assertGroupAddressFreeLevel(4610, new byte[]{(byte) 0x12, (byte) 0x02});
        assertGroupAddressFreeLevel(65535, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    @Test
    @DisplayName("Test 2-level group address structure")
    public void testValid2Level() {
        assertGroupAddress2Level(0, 1, new byte[]{0x00, 0x01});
        assertGroupAddress2Level(7, 1024, new byte[]{(byte) 0x3C, (byte) 0x00});
        assertGroupAddress2Level(31, 2047, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    @Test
    @DisplayName("Test 3-level group address structure")
    public void testValid3Level() {
        assertGroupAddress3Level(0, 0, 1, new byte[]{0x00, 0x01});
        assertGroupAddress3Level(7, 3, 128, new byte[]{(byte) 0x3B, (byte) 0x80});
        assertGroupAddress3Level(31, 7, 255, new byte[]{(byte) 0xFF, (byte) 0xFF});
    }

    @Test
    @DisplayName("Test #of(String)")
    public void testAddressByString() {
        assertThat(GroupAddress.of("1")).isEqualTo(GroupAddress.of(1));
        assertThat(GroupAddress.of("65535")).isEqualTo(GroupAddress.of(65535));

        assertThat(GroupAddress.of("0/1")).isEqualTo(GroupAddress.of(0, 1));
        assertThat(GroupAddress.of("31/2047")).isEqualTo(GroupAddress.of(31, 2047));

        assertThat(GroupAddress.of("0/0/1")).isEqualTo(GroupAddress.of(0, 0, 1));
        assertThat(GroupAddress.of("31/7/255")).isEqualTo(GroupAddress.of(31, 7, 255));

        // bad cases
        assertThatThrownBy(() -> GroupAddress.of((String) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> GroupAddress.of("foobar")).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> GroupAddress.of("0/0/0/0")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Invalid cases for #of(byte[])")
    void invalidByteLength() {
        // null
        assertThatThrownBy(() -> GroupAddress.of((byte[]) null))
                .isInstanceOf(NullPointerException.class);

        // address should have 2 bytes
        assertThatThrownBy(() -> GroupAddress.of(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2 Bytes is expected but got: []");
        assertThatThrownBy(() -> GroupAddress.of(new byte[3]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("2 Bytes is expected but got: [0, 0, 0]");
    }

    @Test
    @DisplayName("Invalid cases for #of(int)")
    void invalid_FreeLevel() {
        // out of range
        assertThatThrownBy(() -> GroupAddress.of(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Free-Level address must be between [1, 65535] but was: 0");
        assertThatThrownBy(() -> GroupAddress.of(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Free-Level address must be between [1, 65535] but was: -1");
        assertThatThrownBy(() -> GroupAddress.of(65536))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Free-Level address must be between [1, 65535] but was: 65536");
    }

    @Test
    @DisplayName("Invalid cases for #of(int, int)")
    void invalid_2Level() {
        // not allowed 0/0
        assertThatThrownBy(() -> GroupAddress.of(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group address '0/0' is not allowed.");

        // out of range
        assertThatThrownBy(() -> GroupAddress.of(-1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Main group of 2-Level address must be between [0, 31] but was: -1");
        assertThatThrownBy(() -> GroupAddress.of(32, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Main group of 2-Level address must be between [0, 31] but was: 32");

        assertThatThrownBy(() -> GroupAddress.of(0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sub group of 2-Level address must be between [0, 2047] but was: -1");
        assertThatThrownBy(() -> GroupAddress.of(0, 2048))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sub group of 2-Level address must be between [0, 2047] but was: 2048");
    }

    /**
     * Test <strong>invalid</strong> addresses for {@link GroupAddress#of(int, int, int)}
     */
    @Test
    @DisplayName("Invalid cases for #of(int, int, int)")
    void invalid_3Level() {
        // not allowed 0/0/0
        assertThatThrownBy(() -> GroupAddress.of(0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group address '0/0/0' is not allowed.");

        // out of range
        assertThatThrownBy(() -> GroupAddress.of(-1, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Main group of 3-Level address must be between [0, 31] but was: -1");
        assertThatThrownBy(() -> GroupAddress.of(32, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Main group of 3-Level address must be between [0, 31] but was: 32");

        assertThatThrownBy(() -> GroupAddress.of(0, -1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Middle group of 3-Level address must be between [0, 7] but was: -1");
        assertThatThrownBy(() -> GroupAddress.of(0, 8, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Middle group of 3-Level address must be between [0, 7] but was: 8");

        assertThatThrownBy(() -> GroupAddress.of(0, 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sub group of 3-Level address must be between [0, 255] but was: -1");
        assertThatThrownBy(() -> GroupAddress.of(0, 0, 256))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sub group of 3-Level address must be between [0, 255] but was: 256");
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(GroupAddress.class).verify();
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
     * @param address free-level address number
     * @param bytes   byte array of address
     */
    private void assertGroupAddressFreeLevel(final int address, final byte[] bytes) {
        final var testByCreate = GroupAddress.of(address);
        final var testByCreateRawData = GroupAddress.of(testByCreate.toByteArray());
        final var testByValueOfRawData = GroupAddress.of(bytes);
        final var testByString = GroupAddress.of(String.valueOf(address));

        assertThat(testByCreate.toByteArray()).containsExactly(testByCreateRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByValueOfRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByString.toByteArray());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByString.getAddressType()).isEqualTo(AddressType.GROUP);

        // check address print
        final var addressAsString = String.valueOf(address);
        assertThat(testByCreate.getAddress()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddress()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddress()).isEqualTo(addressAsString);
        assertThat(testByString.getAddress()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("GroupAddress{addressType=%s, address=%s, address(2-level)=%s, address(3-level)=%s}",
                AddressType.GROUP, testByCreate.getAddress(), testByCreate.getAddressLevel2(), testByCreate.getAddressLevel3()));
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
     * @param main  main group range of group address
     * @param sub   sub group range of group address
     * @param bytes byte array of group address
     */
    private void assertGroupAddress2Level(final int main, final int sub, final byte[] bytes) {
        final var testByCreate = GroupAddress.of(main, sub);
        final var testByCreateRawData = GroupAddress.of(testByCreate.toByteArray());
        final var testByValueOfRawData = GroupAddress.of(bytes);
        final var testByString = GroupAddress.of(main + "/" + sub);

        assertThat(testByCreate.toByteArray()).containsExactly(testByCreateRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByValueOfRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByString.toByteArray());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByString.getAddressType()).isEqualTo(AddressType.GROUP);

        // check address print
        final var addressAsString = String.format("%s/%s", main, sub);
        assertThat(testByCreate.getAddressLevel2()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddressLevel2()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddressLevel2()).isEqualTo(addressAsString);
        assertThat(testByString.getAddressLevel2()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("GroupAddress{addressType=%s, address=%s, address(2-level)=%s, address(3-level)=%s}",
                AddressType.GROUP, testByCreate.getAddress(), testByCreate.getAddressLevel2(), testByCreate.getAddressLevel3()));
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
     * @param main   main group range of group address
     * @param middle middle group range of group address
     * @param sub    sub group range of group address
     * @param bytes  byte array of group address
     */
    private void assertGroupAddress3Level(final int main, final int middle, final int sub, final byte[] bytes) {
        final var testByCreate = GroupAddress.of(main, middle, sub);
        final var testByCreateRawData = GroupAddress.of(testByCreate.toByteArray());
        final var testByValueOfRawData = GroupAddress.of(bytes);
        final var testByString = GroupAddress.of(main + "/" + middle + "/" + sub);

        assertThat(testByCreate.toByteArray()).containsExactly(testByCreateRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByValueOfRawData.toByteArray());
        assertThat(testByCreate.toByteArray()).containsExactly(testByString.toByteArray());

        // check address type
        assertThat(testByCreate.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByValueOfRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(testByString.getAddressType()).isEqualTo(AddressType.GROUP);

        // check address print
        final var addressAsString = String.format("%s/%s/%s", main, middle, sub);
        assertThat(testByCreate.getAddressLevel3()).isEqualTo(addressAsString);
        assertThat(testByCreateRawData.getAddressLevel3()).isEqualTo(addressAsString);
        assertThat(testByValueOfRawData.getAddressLevel3()).isEqualTo(addressAsString);
        assertThat(testByString.getAddressLevel3()).isEqualTo(addressAsString);

        // toString
        assertThat(testByCreate).hasToString(String.format("GroupAddress{addressType=%s, address=%s, address(2-level)=%s, address(3-level)=%s}",
                AddressType.GROUP, testByCreate.getAddress(), testByCreate.getAddressLevel2(), testByCreate.getAddressLevel3()));
    }
}
