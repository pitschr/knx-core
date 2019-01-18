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
import li.pitschmann.knx.link.datapoint.*;
import li.pitschmann.knx.link.datapoint.value.*;
import li.pitschmann.knx.link.exceptions.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Test case for {@link CEMI}
 *
 * @author PITSCHR
 */
public final class CEMITest {
    /**
     * Tests the {@link CEMI#useDefaultForGroupValueRead(KnxAddress)}
     */
    @Test
    public void useDefaultForGroupValueRead() {
        final KnxAddress knxAddress = IndividualAddress.of(5, 9, 15);

        // useDefaultForGroupValueRead
        final CEMI cemiDefaultForGroupValueRead = CEMI.useDefaultForGroupValueRead(knxAddress);

        // useDefault
        final CEMI cemiDefault = CEMI.useDefault(knxAddress, APCI.GROUP_VALUE_READ, (byte[]) null);

        // complex
        final CEMI cemiCreate = CEMI.create(MessageCode.L_DATA_REQ, AdditionalInfo.empty(), ControlByte1.useDefault(),
                ControlByte2.useDefault(knxAddress), IndividualAddress.useDefault(), knxAddress, TPCI.UNNUMBERED_PACKAGE, 0,
                APCI.GROUP_VALUE_READ, (byte[]) null);

        // assert
        assertThat(cemiDefaultForGroupValueRead.getRawData()).containsExactly(cemiDefault.getRawData());
        assertThat(cemiDefaultForGroupValueRead.getRawData()).containsExactly(cemiCreate.getRawData());
    }

    /**
     * Tests the
     * {@link CEMI#useDefaultForGroupValueWrite(KnxAddress, li.pitschmann.knx.link.datapoint.value.DataPointValue)}
     */
    @Test
    public void useDefaultForGroupValueWrite() {
        final KnxAddress knxAddress = IndividualAddress.of(7, 13, 41);
        final byte[] bytes = new byte[]{(byte) 0xAA, (byte) 0xBB};
        DPT7Value dptValue = DPT7.BRIGHTNESS.toValue(bytes);

        // useDefaultForGroupValueWrite
        final CEMI cemiDefaultForGroupValueWriteWithBytes = CEMI.useDefaultForGroupValueWrite(knxAddress, bytes);
        final CEMI cemiDefaultForGroupValueWriteWithDataPoint = CEMI.useDefaultForGroupValueWrite(knxAddress, dptValue);

        // useDefault
        final CEMI cemiDefaultWithBytes = CEMI.useDefault(knxAddress, APCI.GROUP_VALUE_WRITE, bytes);
        final CEMI cemiDefaultWithDataPoint = CEMI.useDefault(knxAddress, APCI.GROUP_VALUE_WRITE, dptValue);

        // complex
        final CEMI cemiCreateWithBytes = CEMI.create(MessageCode.L_DATA_REQ, AdditionalInfo.empty(), ControlByte1.useDefault(),
                ControlByte2.useDefault(knxAddress), IndividualAddress.useDefault(), knxAddress, TPCI.UNNUMBERED_PACKAGE, 0,
                APCI.GROUP_VALUE_WRITE, bytes);
        final CEMI cemiCreateWithDataPoint = CEMI.create(MessageCode.L_DATA_REQ, AdditionalInfo.empty(), ControlByte1.useDefault(),
                ControlByte2.useDefault(knxAddress), IndividualAddress.useDefault(), knxAddress, TPCI.UNNUMBERED_PACKAGE, 0,
                APCI.GROUP_VALUE_WRITE, dptValue);

        // assert
        assertThat(cemiDefaultForGroupValueWriteWithBytes.getRawData()).containsExactly(cemiDefaultForGroupValueWriteWithDataPoint.getRawData());
        assertThat(cemiDefaultForGroupValueWriteWithBytes.getRawData()).containsExactly(cemiDefaultWithBytes.getRawData());
        assertThat(cemiDefaultForGroupValueWriteWithBytes.getRawData()).containsExactly(cemiDefaultWithDataPoint.getRawData());
        assertThat(cemiDefaultForGroupValueWriteWithBytes.getRawData()).containsExactly(cemiCreateWithBytes.getRawData());
        assertThat(cemiDefaultForGroupValueWriteWithBytes.getRawData()).containsExactly(cemiCreateWithDataPoint.getRawData());
    }

    /**
     * Test for L_Data Connection, A_GroupValue_Read
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.con (0x2e)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0xe0
     * 1... .... = Destination address type: 1
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.255
     * Destination Address 1/2/150 or 1/662
     * NPDU length: 1 octet
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00 0000 0000 = APCI: A_GroupValue_Read (0x0000)
     * </pre>
     */
    @Test
    public void validGroupValueRead() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.GROUP, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 255);
        final GroupAddress destinationAddress = GroupAddress.of(1, 2, 150);

        // create
        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, (byte[]) null);
        final CEMI cemiByCreateEmptyArray = CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress,
                destinationAddress, TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0]);
        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
        // valueOf
        final CEMI cemiByValueOf = CEMI
                .valueOf(new byte[]{0x2e, 0x00, (byte) 0xbc, (byte) 0xe0, 0x10, (byte) 0xff, 0x0a, (byte) 0x96, 0x01, 0x00, 0x00});

        // compare raw data of 'create' and 'valueOf'
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateEmptyArray.getRawData());
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());

        // assert
        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0]);
        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0]);
        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0]);
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Write (value inside APCI byte)
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xa8
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...0 .... = System-Broadcast: 0
     * .... 10.. = Priority: 0x2
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0xbc
     * 1... .... = Destination address type: 1
     * .011 .... = Hop count: 3
     * .... 1100 = Extended Frame Format: 0x0c
     * Source Address 1.0.21
     * Destination Address 1/3/203 or 1/971
     * NPDU length: 1 octet
     * 01.. .... = TPCI: NDT (Numbered Data Packet) (0x40)
     * ..00 01.. = Package Number
     * .... ..00 10.. .... = APCI: A_GroupValue_Write (0x0002)
     * ..00 0001 = Data: 0x01
     * </pre>
     */
    @Test
    public void validGroupValueWriteApciDataInside() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.SYSTEM, Priority.URGENT, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.GROUP, 3, 0x0C);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 21);
        final GroupAddress destinationAddress = GroupAddress.of(1, 3, 203);

        // create
        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.NUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_WRITE, new byte[]{0x01});
        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
        // valueOf
        final CEMI cemiByValueOf = CEMI
                .valueOf(new byte[]{0x29, 0x00, (byte) 0xa8, (byte) 0xbc, 0x10, 0x15, 0x0b, (byte) 0xcb, 0x01, 0x44, (byte) 0x81});

        // compare raw data of 'create' and 'valueOf'
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());

        // assert
        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.NUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_WRITE, new byte[]{0x01});
        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.NUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_WRITE, new byte[]{0x01});
        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
                TPCI.NUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_WRITE, new byte[]{0x01});
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Write (one-byte value outside of APCI byte)
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0xe0
     * 1... .... = Destination address type: 1
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.21
     * Destination Address 1/3/204 or 1/972
     * NPDU length: 2 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_GroupValue_Write (0x0002)
     * data: 5d
     * </pre>
     */
    @Test
    public void validGroupValueWriteApciDataOneByteAppended() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.GROUP, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 21);
        final GroupAddress destinationAddress = GroupAddress.of(1, 3, 204);

        // create
        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x5d});
        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
        // valueOf
        final CEMI cemiByValueOf = CEMI
                .valueOf(new byte[]{0x29, 0x00, (byte) 0xbc, (byte) 0xe0, 0x10, 0x15, 0x0b, (byte) 0xcc, 0x02, 0x00, (byte) 0x80, 0x5d});

        // compare raw data of 'create' and 'valueOf'
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());

        // assert
        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x5d});
        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x5d});
        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x5d});
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Write (two-bytes value outside of APCI byte) *
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0x60
     * 0... .... = Destination address type: 0
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.130
     * Destination Address 6.3.67
     * NPDU length: 3 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_GroupValue_Write (0x0002)
     * data: 0c09
     * </pre>
     */
    @Test
    public void validGroupValueWriteApciDataTwoBytesAppended() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 130);
        final IndividualAddress destinationAddress = IndividualAddress.of(6, 3, 67);

        // create
        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x0c, 0x09});
        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
        // valueOf
        final CEMI cemiByValueOf = CEMI.valueOf(
                new byte[]{0x29, 0x00, (byte) 0xbc, (byte) 0x60, 0x10, (byte) 0x82, 0x63, 0x43, 0x03, 0x00, (byte) 0x80, 0x0c, 0x09});

        // compare raw data of 'create' and 'valueOf'
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());

        // assert
        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x0c, 0x09});
        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x0c, 0x09});
        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x0c, 0x09});
    }

    /**
     * Test for L_Data Indication, A_GroupValue_Response*
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0x60
     * 0... .... = Destination address type: 0
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.130
     * Destination Address 6.3.67
     * NPDU length: 3 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_GroupValue_Response (0x0040)
     * data: 0c09
     * </pre>
     */
    @Test
    public void testGroupValueResponse() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 130);
        final IndividualAddress destinationAddress = IndividualAddress.of(6, 3, 67);

        // create
        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, new byte[]{0x0c, 0x09});
        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
        // valueOf
        final CEMI cemiByValueOf = CEMI.valueOf(
                new byte[]{0x29, 0x00, (byte) 0xbc, (byte) 0x60, 0x10, (byte) 0x82, 0x63, 0x43, 0x03, 0x00, (byte) 0x40, 0x0c, 0x09});

        // compare raw data of 'create' and 'valueOf'
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());

        // assert
        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, new byte[]{0x0c, 0x09});
        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, new byte[]{0x0c, 0x09});
        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 3,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_RESPONSE, new byte[]{0x0c, 0x09});
    }

    /**
     * Test for L_Data Indication, A_IndividualValue_Response
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0x60
     * 0... .... = Destination address type: 0
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.130
     * Destination Address 6.3.67
     * NPDU length: 3 octets
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..00  10.. .... = APCI: A_IndividualValue_Response (0x0140)
     * data: 0b
     * </pre>
     */
    @Test
    public void testIndividualAddressResponse() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 130);
        final IndividualAddress destinationAddress = IndividualAddress.of(6, 3, 67);

        // exception expected
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_RESPONSE, new byte[]{0x0b})).isInstanceOf(KnxException.class).hasMessage("Current APCI is not supported: " + APCI.INDIVIDUAL_ADDRESS_RESPONSE);

//        // create
//        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_RESPONSE, new byte[]{0x0b});
//        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
//        // valueOf
//        final CEMI cemiByValueOf = CEMI.valueOf(
//                new byte[]{0x29, 0x00, (byte) 0xbc, (byte) 0x60, 0x10, (byte) 0x82, 0x63, 0x43, 0x02, (byte) 0x01, (byte) 0x40, 0x0b});
//
//        // compare raw data of 'create' and 'valueOf'
//        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
//        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());
//
//        // assert
//        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_RESPONSE, new byte[]{0x0b});
//        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_RESPONSE, new byte[]{0x0b});
//        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 2,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_RESPONSE, new byte[]{0x0b});
    }

    /**
     * Test for L_Data Indication, A_IndivdualValue_Read*
     *
     * <pre>
     * cEMI
     * messagecode: L_Data.ind (0x29)
     * add information length: 0 octets
     * Controlfield 1: 0xbc
     * 1... .... = Frametype: 1
     * ..1. .... = Repeat: 1
     * ...1 .... = System-Broadcast: 1
     * .... 11.. = Priority: 0x3
     * .... ..0. = Acknowledge-Request: 0
     * .... ...0 = Confirm-Flag: 0
     * Controlfield 2: 0x60
     * 0... .... = Destination address type: 0
     * .110 .... = Hop count: 6
     * .... 0000 = Extended Frame Format: 0x0
     * Source Address 1.0.130
     * Destination Address 6.3.67
     * NPDU length: 1 octet
     * 00.. .... = TPCI: UDT (Unnumbered Data Packet) (0x0)
     * .... ..01  00.. .... = APCI: A_IndividualValue_Read (0x0100)
     * </pre>
     */
    @Test
    public void testIndividualAddressRead() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 130);
        final IndividualAddress destinationAddress = IndividualAddress.of(6, 3, 67);

        // exception expected
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_READ, new byte[0])).isInstanceOf(KnxException.class).hasMessage("Current APCI is not supported: " + APCI.INDIVIDUAL_ADDRESS_READ);

//        // create
//        final CEMI cemiByCreate = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_READ, new byte[0]);
//        final CEMI cemiByCreateRawData = CEMI.valueOf(cemiByCreate.getRawData());
//        // valueOf
//        final CEMI cemiByValueOf = CEMI.valueOf(new byte[]{0x29, 0x00, (byte) 0xbc, (byte) 0x60, 0x10, (byte) 0x82, 0x63, 0x43, 0x01, 0x01, 0x00});
//
//        // compare raw data of 'create' and 'valueOf'
//        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByCreateRawData.getRawData());
//        assertThat(cemiByCreate.getRawData()).isEqualTo(cemiByValueOf.getRawData());
//
//        // assert
//        this.assertCEMI(cemiByCreate, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_READ, new byte[0]);
//        this.assertCEMI(cemiByCreateRawData, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_READ, new byte[0]);
//        this.assertCEMI(cemiByValueOf, MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress, 1,
//                TPCI.UNNUMBERED_PACKAGE, 0, APCI.INDIVIDUAL_ADDRESS_READ, new byte[0]);
    }

    /**
     * Tests <strong>invalid</strong> CEMI parameters
     */
    @Test
    public void invalidCases() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, true, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.GROUP, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 255);
        final GroupAddress destinationAddress = GroupAddress.of(1, 2, 150);

        // null
        assertThatThrownBy(() -> CEMI.valueOf(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("cemiRawData");

        assertThatThrownBy(() -> CEMI.create(null, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, (byte[]) null)).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("messageCode");

        assertThatThrownBy(() -> CEMI.create(null, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("messageCode");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, null, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("additionalInfo");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, null, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlByte1");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, null, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("controlByte2");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, null, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("sourceAddress");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, null,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class)
                .hasMessageContaining("destinationAddress");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                null, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("tpci");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, null, new byte[0])).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("apci");

        // out of range
        assertThatThrownBy(() -> CEMI.valueOf(new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class);

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, -1, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("tpciPacketNumber");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0xFF + 1, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("tpciPacketNumber");

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[15])).isInstanceOf(KnxNumberOutOfRangeException.class)
                .hasMessageContaining("apciData");

        // illegal state
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 1, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxIllegalStateException.class);

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[1])).isInstanceOf(KnxIllegalStateException.class);

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_CONTROL_DATA, 0, APCI.GROUP_VALUE_WRITE, (byte[]) null)).isInstanceOf(KnxIllegalStateException.class);
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_CONTROL_DATA, 0, APCI.GROUP_VALUE_WRITE, new byte[0])).isInstanceOf(KnxIllegalStateException.class);

        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_CONTROL_DATA, 0, APCI.GROUP_VALUE_RESPONSE, (byte[]) null)).isInstanceOf(KnxIllegalStateException.class);
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_CONTROL_DATA, 0, APCI.GROUP_VALUE_RESPONSE, new byte[0])).isInstanceOf(KnxIllegalStateException.class);

        // conflict between ControlByte2 and Destination Address instance type
        // ControlByte2#addressType = GROUP, destination = INDIVIDUAL
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, sourceAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0])).isInstanceOf(KnxIllegalStateException.class);
        // ControlByte2#addressType = INDIVIDUAL, destination = GROUP
        final ControlByte2 controlByte2Individual = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);
        assertThatThrownBy(() -> CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2Individual, sourceAddress,
                destinationAddress, TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, new byte[0]))
                .isInstanceOf(KnxIllegalStateException.class);

        // test invalid additional info length
        assertThatThrownBy(() -> CEMI.valueOf(new byte[]{0x29, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}))
                .isInstanceOf(UnsupportedOperationException.class);

        // test incorrect NPDU length value (0x10 = 16 which means that there should be 16 additional data added)
        assertThatThrownBy(() -> CEMI.valueOf(new byte[]{0x29, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x40}))
                .isInstanceOf(KnxNumberOutOfRangeException.class).hasMessageContaining("cemiRawData/npdu");
    }

    /**
     * Asserts the given {@link CEMI} it has been read and processed correctly.
     *
     * @param cemi
     * @param messageCode
     * @param additionalInfo
     * @param controlByte1
     * @param controlByte2
     * @param sourceAddress
     * @param destinationAddress
     * @param npduLength
     * @param tpci
     * @param tpciPacketNumber
     * @param apci
     * @param apciData
     */
    private void assertCEMI(final CEMI cemi, final MessageCode messageCode, final AdditionalInfo additionalInfo, final ControlByte1 controlByte1,
                            final ControlByte2 controlByte2, final IndividualAddress sourceAddress, final KnxAddress destinationAddress, final int npduLength,
                            final TPCI tpci, final int tpciPacketNumber, final APCI apci, final byte[] apciData) {
        assertThat(cemi.getMessageCode()).isEqualTo(messageCode);
        assertThat(cemi.getAdditionalInfo()).isEqualTo(additionalInfo);
        assertThat(cemi.getControlByte1()).isEqualTo(controlByte1);
        assertThat(cemi.getControlByte2()).isEqualTo(controlByte2);
        assertThat(cemi.getSourceAddress()).isEqualTo(sourceAddress);
        assertThat(cemi.getDestinationAddress()).isEqualTo(destinationAddress);
        assertThat(cemi.getNpduLength()).isEqualTo(npduLength);
        assertThat(cemi.getTpci()).isEqualTo(tpci);
        assertThat(cemi.getTpciPacketNumber()).isEqualTo(tpciPacketNumber);
        assertThat(cemi.getApci()).isEqualTo(apci);
        assertThat(cemi.getApciData()).containsExactly(apciData);
    }

    /**
     * Test {@link CEMI#toString()}
     */
    @Test
    public void testToStringWithRawData() {
        // with raw data
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.GROUP, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 255);
        final GroupAddress destinationAddress = GroupAddress.of(1, 2, 150);

        final CEMI cemi = CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, (byte[]) null);

        assertThat(cemi).hasToString(String.format(
                "CEMI{messageCode=%s, additionalInfo=%s, controlByte1=%s, controlByte2=%s, sourceAddress=%s, "
                        + "destinationAddress=%s, npduLength=1 (0x01), tpci=%s, tpciPacketNumber=0 (0x00), apci=%s, "
                        + "apciData=[] (null), rawData=0x2E 00 BC E0 10 FF 0A 96 01 00 00}",
                MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, APCI.GROUP_VALUE_READ));
    }

    /**
     * Test {@link CEMI#toString()} #2
     */
    @Test
    public void testToStringWithRawData2() {
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.INDIVIDUAL, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 130);
        final IndividualAddress destinationAddress = IndividualAddress.of(6, 3, 67);

        // create
        final CEMI cemi = CEMI.create(MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_WRITE, new byte[]{0x0c, 0x09});

        assertThat(cemi).hasToString(String.format(
                "CEMI{messageCode=%s, additionalInfo=%s, controlByte1=%s, controlByte2=%s, sourceAddress=%s, "
                        + "destinationAddress=%s, npduLength=3 (0x03), tpci=%s, tpciPacketNumber=0 (0x00), apci=%s, "
                        + "apciData=[12, 9] (0x0C 09), rawData=0x29 00 BC 60 10 82 63 43 03 00 80 0C 09}",
                MessageCode.L_DATA_IND, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, APCI.GROUP_VALUE_WRITE));
    }

    /**
     * Test {@link CEMI#toString(boolean)}
     */
    @Test
    public void testToStringWithoutRawData() {
        // with raw data
        final AdditionalInfo additionalInfo = AdditionalInfo.empty();
        final ControlByte1 controlByte1 = ControlByte1.create(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
        final ControlByte2 controlByte2 = ControlByte2.create(AddressType.GROUP, 6, 0);
        final IndividualAddress sourceAddress = IndividualAddress.of(1, 0, 255);
        final GroupAddress destinationAddress = GroupAddress.of(1, 2, 150);

        final CEMI cemi = CEMI.create(MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, 0, APCI.GROUP_VALUE_READ, (byte[]) null);

        assertThat(cemi.toString(false)).isEqualTo(String.format(
                "CEMI{messageCode=%s, additionalInfo=%s, controlByte1=%s, controlByte2=%s, sourceAddress=%s, "
                        + "destinationAddress=%s, npduLength=1 (0x01), tpci=%s, tpciPacketNumber=0 (0x00), apci=%s, "
                        + "apciData=[] (null)}",
                MessageCode.L_DATA_CON, additionalInfo, controlByte1, controlByte2, sourceAddress, destinationAddress,
                TPCI.UNNUMBERED_PACKAGE, APCI.GROUP_VALUE_READ));
    }
}
