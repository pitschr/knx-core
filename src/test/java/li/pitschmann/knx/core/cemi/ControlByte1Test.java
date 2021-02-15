/*
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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ControlByte1}
 *
 * @author PITSCHR
 */
final class ControlByte1Test {

    @Test
    @DisplayName("Test #useDefault()")
    void testUseDefault() {
        final var cbDefault = ControlByte1.useDefault();
        assertThat(cbDefault.isStandardFrame()).isTrue();
        assertThat(cbDefault.isRepeatEnabled()).isFalse();
        assertThat(cbDefault.getBroadcastType()).isSameAs(BroadcastType.NORMAL);
        assertThat(cbDefault.getPriority()).isSameAs(Priority.LOW);
        assertThat(cbDefault.isRequestAcknowledge()).isFalse();
        assertThat(cbDefault.isErrorConfirmation()).isFalse();
        assertThat(cbDefault.toByte()).isEqualTo(
                (byte) 0b1011_1100
                //       ^... .... Frame Type
                //       .^.. .... (reserved)
                //       ..^. .... Repetition (0=repeat on error, 1=don't repeat on error)
                //       ...^ .... Broadcast Type
                //       .... ^^.. Priority
                //       .... ..^. Acknowledge Request
                //       .... ...^ Error Confirmation (0=no error, 1=error)
        );
    }

    @Test
    @DisplayName("Test #of(byte) standardFrame=false, repeatEnabled=false, BroadcastType#SYSTEM, Priority#SYSTEM, requestAcknowledge=false, errorConfirmation=false")
    void testOf_Byte_A() {
        final var createByByte = ControlByte1.of(
                (byte) 0b0010_0000
        //               ^... .... Frame Type
        //               .^.. .... (reserved)
        //               ..^. .... Repetition (0=repeat on error, 1=don't repeat on error)
        //               ...^ .... Broadcast Type
        //               .... ^^.. Priority
        //               .... ..^. Acknowledge Request
        //               .... ...^ Error Confirmation (0=no error, 1=error)
        );

        final var create = ControlByte1.of(false, false, BroadcastType.SYSTEM, Priority.SYSTEM, false, false);
        assertThat(create.isStandardFrame()).isFalse();
        assertThat(create.isRepeatEnabled()).isFalse();
        assertThat(create.getBroadcastType()).isSameAs(BroadcastType.SYSTEM);
        assertThat(create.getPriority()).isSameAs(Priority.SYSTEM);
        assertThat(create.isRequestAcknowledge()).isFalse();
        assertThat(create.isErrorConfirmation()).isFalse();

        assertThat(create.toByte()).isEqualTo(createByByte.toByte());
        assertThat(create).isEqualTo(createByByte);
        assertThat(create).hasSameHashCodeAs(createByByte);
    }

    @Test
    @DisplayName("Test #of(byte) standardFrame=true, repeatEnabled=true, BroadcastType#NORMAL, Priority#LOW, requestAcknowledge=true, errorConfirmation=true")
    void testOf_Byte_B() {
        final var createByByte = ControlByte1.of(
                (byte) 0b1001_1111
                //       ^... .... Frame Type
                //       .^.. .... (reserved)
                //       ..^. .... Repetition (0=repeat on error, 1=don't repeat on error)
                //       ...^ .... Broadcast Type
                //       .... ^^.. Priority
                //       .... ..^. Acknowledge Request
                //       .... ...^ Error Confirmation (0=no error, 1=error)
        );

        final var create = ControlByte1.of(true, true, BroadcastType.NORMAL, Priority.LOW, true, true);
        assertThat(create.isStandardFrame()).isTrue();
        assertThat(create.isRepeatEnabled()).isTrue();
        assertThat(create.getBroadcastType()).isSameAs(BroadcastType.NORMAL);
        assertThat(create.getPriority()).isSameAs(Priority.LOW);
        assertThat(create.isRequestAcknowledge()).isTrue();
        assertThat(create.isErrorConfirmation()).isTrue();

        assertThat(create.toByte()).isEqualTo(createByByte.toByte());
        assertThat(create).isEqualTo(createByByte);
        assertThat(create).hasSameHashCodeAs(createByByte);
    }

    @Test
    @DisplayName("Test #of(byte) standardFrame=true, repeatEnabled=false, BroadcastType#SYSTEM, Priority#NORMAL, requestAcknowledge=true, errorConfirmation=false")
    void testOf_Byte_C() {
        final var createByByte = ControlByte1.of(
                (byte) 0b1010_0110
                //       ^... .... Frame Type
                //       .^.. .... (reserved)
                //       ..^. .... Repetition (0=repeat on error, 1=don't repeat on error)
                //       ...^ .... Broadcast Type
                //       .... ^^.. Priority
                //       .... ..^. Acknowledge Request
                //       .... ...^ Error Confirmation (0=no error, 1=error)
        );

        final var create = ControlByte1.of(true, false, BroadcastType.SYSTEM, Priority.NORMAL, true, false);
        assertThat(create.isStandardFrame()).isTrue();
        assertThat(create.isRepeatEnabled()).isFalse();
        assertThat(create.getBroadcastType()).isSameAs(BroadcastType.SYSTEM);
        assertThat(create.getPriority()).isSameAs(Priority.NORMAL);
        assertThat(create.isRequestAcknowledge()).isTrue();
        assertThat(create.isErrorConfirmation()).isFalse();

        assertThat(create.toByte()).isEqualTo(createByByte.toByte());
        assertThat(create).isEqualTo(createByByte);
        assertThat(create).hasSameHashCodeAs(createByByte);
    }

    @Test
    @DisplayName("Invalid cases for #of(boolean, boolean, BroadcastType, Priority, boolean, boolean)")
    void invalidCases_of_2boolean_BroadcastType_Priority_2boolean() {
        // null
        assertThatThrownBy(() -> ControlByte1.of(false, false, null, Priority.LOW, false, false))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Broadcast Type is required.");

        assertThatThrownBy(() -> ControlByte1.of(false, false, BroadcastType.SYSTEM, null, false, false))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Priority is required.");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        final var first = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        assertThat(first).hasToString(
                "ControlByte1{standardFrame=true, repeatEnabled=false, broadcastType=NORMAL, priority=LOW, requestAcknowledge=false, errorConfirmation=false}"
        );

        final var second = ControlByte1.of(false, true, BroadcastType.SYSTEM, Priority.URGENT, false, true);
        assertThat(second).hasToString(
                "ControlByte1{standardFrame=false, repeatEnabled=true, broadcastType=SYSTEM, priority=URGENT, requestAcknowledge=false, errorConfirmation=true}"
        );
    }

    @Test
    @DisplayName("#equals() and #hashCode()")
    void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ControlByte1.class).verify();
    }

}
