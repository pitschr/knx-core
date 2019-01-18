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

import li.pitschmann.knx.link.body.address.*;
import li.pitschmann.knx.link.exceptions.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test case for {@link ControlByte2}
 *
 * @author PITSCHR
 */
public final class ControlByte2Test {

    /**
     * Tests the {@link ControlByte2#useDefault(KnxAddress)} for {@link IndividualAddress}
     */
    @Test
    public void useDefaultIndividual() {
        final KnxAddress knxAddress = IndividualAddress.of(15, 3, 7);

        final ControlByte2 controlByte2Default = ControlByte2.useDefault(knxAddress);
        final ControlByte2 controlByte2CreateBy = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);

        // assert
        assertThat(controlByte2Default.getRawData()).isEqualTo(controlByte2CreateBy.getRawData());
    }

    /**
     * Tests the {@link ControlByte2#useDefault(KnxAddress)} for {@link GroupAddress}
     */
    @Test
    public void useDefaultGroup() {
        final KnxAddress knxAddress = GroupAddress.of(3, 1024);

        final ControlByte2 controlByte2Default = ControlByte2.useDefault(knxAddress);
        final ControlByte2 controlByte2CreateBy = ControlByte2.create(AddressType.GROUP, 6, 0);

        // assert
        assertThat(controlByte2Default.getRawData()).isEqualTo(controlByte2CreateBy.getRawData());
    }

    /**
     * Tests the {@link ControlByte2#create(AddressType, int, int)} and {@link ControlByte2#valueOf(byte)} Case A
     */
    @Test
    public void validCaseA() {
        // create
        final ControlByte2 controlByteByCreate = ControlByte2.create(AddressType.INDIVIDUAL, 0, 0);
        final ControlByte2 controlByteByCreateRawData = ControlByte2.valueOf(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(controlByteByCreateRawData.getHopCount()).isEqualTo(0);
        assertThat(controlByteByCreateRawData.getExtendedFrameFormat()).isEqualTo(0);

        // valueOf
        final ControlByte2 controlByteByValueOf = ControlByte2.valueOf((byte) (0x00 << 7 | 0x00 << 4 | 0x00));
        assertThat(controlByteByValueOf.getAddressType()).isEqualTo(AddressType.INDIVIDUAL);
        assertThat(controlByteByValueOf.getHopCount()).isEqualTo(0);
        assertThat(controlByteByValueOf.getExtendedFrameFormat()).isEqualTo(0);

        // compare raw data of 'create' and 'valueOf'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests the {@link ControlByte2#create(AddressType, int, int)} and {@link ControlByte2#valueOf(byte)} Case B
     */
    @Test
    public void validCaseB() {
        // create
        final ControlByte2 controlByteByCreate = ControlByte2.create(AddressType.GROUP, 3, 8);
        final ControlByte2 controlByteByCreateRawData = ControlByte2.valueOf(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(controlByteByCreateRawData.getHopCount()).isEqualTo(3);
        assertThat(controlByteByCreateRawData.getExtendedFrameFormat()).isEqualTo(8);

        // valueOf
        final ControlByte2 controlByteByValueOf = ControlByte2.valueOf((byte) (0x01 << 7 | 0x03 << 4 | 0x08));
        assertThat(controlByteByValueOf.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(controlByteByValueOf.getHopCount()).isEqualTo(3);
        assertThat(controlByteByValueOf.getExtendedFrameFormat()).isEqualTo(8);

        // compare raw data of 'create' and 'valueOf'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests the {@link ControlByte2#create(AddressType, int, int)} and {@link ControlByte2#valueOf(byte)} Case C
     */
    @Test
    public void validCaseC() {
        // create
        final ControlByte2 controlByteByCreate = ControlByte2.create(AddressType.GROUP, 7, 15);
        final ControlByte2 controlByteByCreateRawData = ControlByte2.valueOf(controlByteByCreate.getRawData());
        assertThat(controlByteByCreateRawData.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(controlByteByCreateRawData.getHopCount()).isEqualTo(7);
        assertThat(controlByteByCreateRawData.getExtendedFrameFormat()).isEqualTo(15);

        // valueOf
        final ControlByte2 controlByteByValueOf = ControlByte2.valueOf((byte) (0x01 << 7 | 0x07 << 4 | 0x0F));
        assertThat(controlByteByValueOf.getAddressType()).isEqualTo(AddressType.GROUP);
        assertThat(controlByteByValueOf.getHopCount()).isEqualTo(7);
        assertThat(controlByteByValueOf.getExtendedFrameFormat()).isEqualTo(15);

        // compare raw data of 'create' and 'valueOf'
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByCreateRawData.getRawData());
        assertThat(controlByteByCreate.getRawData()).isEqualTo(controlByteByValueOf.getRawData());
    }

    /**
     * Tests <strong>invalid</strong> control byte parameters
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> ControlByte2.useDefault(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("knxAddress");
        assertThatThrownBy(() -> ControlByte2.create(null, 0, 0)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("addressType");

        // negative
        assertThatThrownBy(() -> ControlByte2.create(AddressType.GROUP, -1, 0)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("hopCount");
        assertThatThrownBy(() -> ControlByte2.create(AddressType.GROUP, 0, -1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("frameFormat");

        // number out of range (too big)
        assertThatThrownBy(() -> ControlByte2.create(AddressType.GROUP, 0x07 + 1, 0)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("hopCount");
        assertThatThrownBy(() -> ControlByte2.create(AddressType.GROUP, 0, 0x0F + 1)).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("frameFormat");
    }

    /**
     * Test {@link ControlByte2#toString()}
     */
    @Test
    public void testToString() {
        assertThat(ControlByte2.create(AddressType.GROUP, 3, 8)).hasToString(String
                .format("ControlByte2{addressType=%s, hopCount=3 (0x03), extendedFrameFormat=8 (0x08), rawData=0xB8}", AddressType.GROUP));

        assertThat(ControlByte2.create(AddressType.INDIVIDUAL, 6, 0)).hasToString(String.format(
                "ControlByte2{addressType=%s, hopCount=6 (0x06), extendedFrameFormat=0 (0x00), rawData=0x60}", AddressType.INDIVIDUAL));
    }
}
