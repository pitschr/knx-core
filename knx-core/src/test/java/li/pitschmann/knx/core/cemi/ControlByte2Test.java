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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.address.IndividualAddress;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Test case for {@link ControlByte2}
 *
 * @author PITSCHR
 */
final class ControlByte2Test {

    @Test
    @DisplayName("Test #of(byte) AddressType#INDIVIDUAL, hopCount=0, frameFormat=0")
    void testOf_Byte_0000_0000() {
        final var createByByte = ControlByte2.of(
                (byte) 0b0000_0000
                //       ^---------- Address Type (INDIVIDUAL = 0)
                //        ^^^------- Hop Count (0 = 000)
                //            ^^^^-- Frame Format (0 = 0000)
        );

        final var create = ControlByte2.of(AddressType.INDIVIDUAL, 0, 0);
        assertThat(create.getAddressType()).isSameAs(AddressType.INDIVIDUAL);
        assertThat(create.getHopCount()).isZero();
        assertThat(create.getFrameFormat()).isZero();

        assertThat(create.toByte()).isEqualTo(createByByte.toByte());
        assertThat(create).isEqualTo(createByByte);
        assertThat(create).hasSameHashCodeAs(createByByte);
    }

    @Test
    @DisplayName("Test #of(byte) AddressType#GROUP, hopCount=7, frameFormat=15")
    void testOf_Byte_1111_1111() {
        final var createByByte = ControlByte2.of(
                (byte) 0b1111_1111
                //       ^---------- Address Type (GROUP = 1)
                //        ^^^------- Hop Count (7 = 111)
                //            ^^^^-- Frame Format (15 = 1111)
        );

        final var create = ControlByte2.of(AddressType.GROUP, 7, 15);
        assertThat(create.getAddressType()).isSameAs(AddressType.GROUP);
        assertThat(create.getHopCount()).isEqualTo(7);
        assertThat(create.getFrameFormat()).isEqualTo(15);

        assertThat(create.toByte()).isEqualTo(createByByte.toByte());
        assertThat(create).isEqualTo(createByByte);
        assertThat(create).hasSameHashCodeAs(createByByte);
    }

    @Test
    @DisplayName("Test #of(KnxAddress) with Individual Address")
    void testOf_IndividualAddress() {
        final var address = IndividualAddress.of(1, 2, 3);

        final var controlByte2 = ControlByte2.of(address);
        assertThat(controlByte2.getAddressType()).isSameAs(AddressType.INDIVIDUAL);
        assertThat(controlByte2.getHopCount()).isEqualTo(ControlByte2.DEFAULT_HOP_COUNT);
        assertThat(controlByte2.getFrameFormat()).isEqualTo(ControlByte2.DEFAULT_FRAME_FORMAT);
        assertThat(controlByte2.toByte()).isEqualTo(
                (byte) 0b0110_0000
                //       ^---------- Address Type (INDIVIDUAL = 0)
                //        ^^^------- Hop Count (6 = 110)
                //            ^^^^-- Frame Format (0 = 0000)
        );
    }

    @Test
    @DisplayName("Test #of(KnxAddress) with Group Address")
    void testOf_GroupAddress() {
        final var address = GroupAddress.of(1, 2, 3);

        final var controlByte2 = ControlByte2.of(address);
        assertThat(controlByte2.getAddressType()).isSameAs(AddressType.GROUP);
        assertThat(controlByte2.getHopCount()).isEqualTo(ControlByte2.DEFAULT_HOP_COUNT);
        assertThat(controlByte2.getFrameFormat()).isEqualTo(ControlByte2.DEFAULT_FRAME_FORMAT);
        assertThat(controlByte2.toByte()).isEqualTo(
                (byte) 0b1110_0000
                //       ^---------- Address Type (1 = GROUP)
                //        ^^^------- Hop Count (6 = 110)
                //            ^^^^-- Frame Format (0 = 0000)
        );
    }

    @Test
    @DisplayName("Invalid cases for #of(KnxAddress)")
    void invalidCases_of_KnxAddress() {
        assertThatThrownBy(() -> ControlByte2.of(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Address is required.");
    }

    @Test
    @DisplayName("Invalid cases for #of(AddressType, int, int)")
    void invalidCases_of_AddressType_Int_Int() {
        // null
        assertThatThrownBy(() -> ControlByte2.of(null, 0, 0))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Address Type is required.");

        // hop count out of range
        assertThatThrownBy(() -> ControlByte2.of(mock(AddressType.class), -1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible hop count number. Expected [0..7] but was: -1");
        assertThatThrownBy(() -> ControlByte2.of(mock(AddressType.class), 8, 8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible hop count number. Expected [0..7] but was: 8");

        // frame format out of range
        assertThatThrownBy(() -> ControlByte2.of(mock(AddressType.class), 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible Extended Frame Format. Expected [0..15] but was: -1");
        assertThatThrownBy(() -> ControlByte2.of(mock(AddressType.class), 0, 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Incompatible Extended Frame Format. Expected [0..15] but was: 16");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        final var first = ControlByte2.of(AddressType.GROUP, 3, 8);
        assertThat(first).hasToString(
                "ControlByte2{addressType=GROUP, hopCount=3, frameFormat=8}"
        );

        final var second = ControlByte2.of(AddressType.INDIVIDUAL, 7, 0);
        assertThat(second).hasToString(
                "ControlByte2{addressType=INDIVIDUAL, hopCount=7, frameFormat=0}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ControlByte2.class).verify();
    }

}
