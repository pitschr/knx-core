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

package li.pitschmann.knx.link.body.cemi;

import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link ControlByte1}
 *
 * @author PITSCHR
 */
public final class ControlByte1Test {
    /**
     * Tests the {@link ControlByte1#useDefault()}
     */
    @Test
    public void useDefault() {
        final ControlByte1 controlByte1Default = ControlByte1.useDefault();
        final ControlByte1 controlByte1CreateBy = ControlByte1.of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);

        // assert
        assertThat(controlByte1Default.getRawData()).isEqualTo(controlByte1CreateBy.getRawData());
    }

    /**
     * Tests {@link ControlByte1#of(boolean, boolean, BroadcastType, Priority, boolean, boolean)} and
     * {@link ControlByte1#of(byte)} with all parameters as {@code true}, {@link BroadcastType#SYSTEM} and
     * {@link Priority#SYSTEM}.
     */
    @Test
    public void validCaseA() {
        // create
        final var controlByteByCreate = ControlByte1.of(false, false, BroadcastType.SYSTEM, Priority.SYSTEM, false, false);
        final var controlByteByCreateRawData = ControlByte1.of(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.isStandardFrame()).isFalse();
        assertThat(controlByteByCreateRawData.isRepeatEnabled()).isFalse();
        assertThat(controlByteByCreateRawData.getBroadcastType()).isEqualTo(BroadcastType.SYSTEM);
        assertThat(controlByteByCreateRawData.getPriority()).isEqualTo(Priority.SYSTEM);
        assertThat(controlByteByCreateRawData.isRequestAcknowledge()).isFalse();
        assertThat(controlByteByCreateRawData.isConfirmationFlag()).isFalse();

        // create by bytes
        final var controlByteByValueOf = ControlByte1.of((byte) (0x00 << 7 | 0x01 << 5 | 0x00 << 4 | 0x00 << 2 | 0x00 << 1 | 0x00));
        assertThat(controlByteByValueOf.isStandardFrame()).isFalse();
        assertThat(controlByteByValueOf.isRepeatEnabled()).isFalse();
        assertThat(controlByteByValueOf.getBroadcastType()).isEqualTo(BroadcastType.SYSTEM);
        assertThat(controlByteByValueOf.getPriority()).isEqualTo(Priority.SYSTEM);
        assertThat(controlByteByValueOf.isRequestAcknowledge()).isFalse();
        assertThat(controlByteByValueOf.isConfirmationFlag()).isFalse();

        // compare raw data of 'create' and 'create by bytes'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests {@link ControlByte1#of(boolean, boolean, BroadcastType, Priority, boolean, boolean)} and
     * {@link ControlByte1#of(byte)} with all parameters as {@code true}, {@link BroadcastType#NORMAL} and
     * {@link Priority#LOW}.
     */
    @Test
    public void validCaseB() {
        // create
        final var controlByteByCreate = ControlByte1.of(true, true, BroadcastType.NORMAL, Priority.LOW, true, true);
        final var controlByteByCreateRawData = ControlByte1.of(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.isStandardFrame()).isTrue();
        assertThat(controlByteByCreateRawData.isRepeatEnabled()).isTrue();
        assertThat(controlByteByCreateRawData.getBroadcastType()).isEqualTo(BroadcastType.NORMAL);
        assertThat(controlByteByCreateRawData.getPriority()).isEqualTo(Priority.LOW);
        assertThat(controlByteByCreateRawData.isRequestAcknowledge()).isTrue();
        assertThat(controlByteByCreateRawData.isConfirmationFlag()).isTrue();

        // create by bytes
        final var controlByteByValueOf = ControlByte1.of((byte) (0x01 << 7 | 0x00 << 5 | 0x01 << 4 | 0x03 << 2 | 0x01 << 1 | 0x01));
        assertThat(controlByteByValueOf.isStandardFrame()).isTrue();
        assertThat(controlByteByValueOf.isRepeatEnabled()).isTrue();
        assertThat(controlByteByValueOf.getBroadcastType()).isEqualTo(BroadcastType.NORMAL);
        assertThat(controlByteByValueOf.getPriority()).isEqualTo(Priority.LOW);
        assertThat(controlByteByValueOf.isRequestAcknowledge()).isTrue();
        assertThat(controlByteByValueOf.isConfirmationFlag()).isTrue();

        // compare raw data of 'create' and 'create by bytes'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests {@link ControlByte1#of(boolean, boolean, BroadcastType, Priority, boolean, boolean)} and
     * {@link ControlByte1#of(byte)} in {@code true}/{@code false} sequence, {@link BroadcastType#SYSTEM} and
     * {@link Priority#NORMAL}.
     */
    @Test
    public void validCaseC() {
        // create
        final var controlByteByCreate = ControlByte1.of(true, false, BroadcastType.SYSTEM, Priority.NORMAL, true, false);
        final var controlByteByCreateRawData = ControlByte1.of(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.isStandardFrame()).isTrue();
        assertThat(controlByteByCreateRawData.isRepeatEnabled()).isFalse();
        assertThat(controlByteByCreateRawData.getBroadcastType()).isEqualTo(BroadcastType.SYSTEM);
        assertThat(controlByteByCreateRawData.getPriority()).isEqualTo(Priority.NORMAL);
        assertThat(controlByteByCreateRawData.isRequestAcknowledge()).isTrue();
        assertThat(controlByteByCreateRawData.isConfirmationFlag()).isFalse();

        // create by bytes
        final var controlByteByValueOf = ControlByte1.of((byte) (0x01 << 7 | 0x01 << 5 | 0x00 << 4 | 0x01 << 2 | 0x01 << 1 | 0x00));
        assertThat(controlByteByValueOf.isStandardFrame()).isTrue();
        assertThat(controlByteByValueOf.isRepeatEnabled()).isFalse();
        assertThat(controlByteByValueOf.getBroadcastType()).isEqualTo(BroadcastType.SYSTEM);
        assertThat(controlByteByValueOf.getPriority()).isEqualTo(Priority.NORMAL);
        assertThat(controlByteByValueOf.isRequestAcknowledge()).isTrue();
        assertThat(controlByteByValueOf.isConfirmationFlag()).isFalse();

        // compare raw data of 'create' and 'create by bytes'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests {@link ControlByte1#of(boolean, boolean, BroadcastType, Priority, boolean, boolean)} and
     * {@link ControlByte1#of(byte)} in {@code false}/{@code true} sequence, {@link BroadcastType#NORMAL} and
     * {@link Priority#NORMAL}.
     */
    @Test
    public void validCaseD() {
        // create
        final var controlByteByCreate = ControlByte1.of(false, true, BroadcastType.NORMAL, Priority.URGENT, false, true);
        final var controlByteByCreateRawData = ControlByte1.of(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.isStandardFrame()).isFalse();
        assertThat(controlByteByCreateRawData.isRepeatEnabled()).isTrue();
        assertThat(controlByteByCreateRawData.getBroadcastType()).isEqualTo(BroadcastType.NORMAL);
        assertThat(controlByteByCreateRawData.getPriority()).isEqualTo(Priority.URGENT);
        assertThat(controlByteByCreateRawData.isRequestAcknowledge()).isFalse();
        assertThat(controlByteByCreateRawData.isConfirmationFlag()).isTrue();

        // create by bytes
        final var controlByteByValueOf = ControlByte1.of((byte) (0x00 << 7 | 0x00 << 5 | 0x01 << 4 | 0x02 << 2 | 0x00 << 1 | 0x01));
        assertThat(controlByteByValueOf.isStandardFrame()).isFalse();
        assertThat(controlByteByValueOf.isRepeatEnabled()).isTrue();
        assertThat(controlByteByValueOf.getBroadcastType()).isEqualTo(BroadcastType.NORMAL);
        assertThat(controlByteByValueOf.getPriority()).isEqualTo(Priority.URGENT);
        assertThat(controlByteByValueOf.isRequestAcknowledge()).isFalse();
        assertThat(controlByteByValueOf.isConfirmationFlag()).isTrue();

        // compare raw data of 'create' and 'create by bytes'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests <strong>invalid</strong> control byte parameters
     */
    @Test
    public void invalidCases() {
        assertThatThrownBy(() -> ControlByte1.of(false, false, null, Priority.LOW, false, false)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("broadcastType");
        assertThatThrownBy(() -> ControlByte1.of(false, false, BroadcastType.SYSTEM, null, false, false))
                .isInstanceOf(KnxNullPointerException.class).hasMessageContaining("priority");
    }

    /**
     * Test {@link ControlByte1#toString()}
     */
    @Test
    public void testToString() {
        assertThat(ControlByte1.of(false, false, BroadcastType.SYSTEM, Priority.SYSTEM, false, false)).hasToString(String.format(
                "ControlByte1{standardFrame=false, repeatEnabled=false, broadcastType=%s, priority=%s, requestAcknowledge=false, confirmationFlag=false, rawData=0x20}",
                BroadcastType.SYSTEM, Priority.SYSTEM));

        assertThat(ControlByte1.of(false, true, BroadcastType.NORMAL, Priority.URGENT, false, true)).hasToString(String.format(
                "ControlByte1{standardFrame=false, repeatEnabled=true, broadcastType=%s, priority=%s, requestAcknowledge=false, confirmationFlag=true, rawData=0x19}",
                BroadcastType.NORMAL, Priority.URGENT));
    }
}
