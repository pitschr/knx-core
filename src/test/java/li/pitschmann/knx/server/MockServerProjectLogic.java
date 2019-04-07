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

package li.pitschmann.knx.server;

import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.body.TunnelingRequestBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.body.address.IndividualAddress;
import li.pitschmann.knx.link.body.cemi.APCI;
import li.pitschmann.knx.link.body.cemi.AdditionalInfo;
import li.pitschmann.knx.link.body.cemi.CEMI;
import li.pitschmann.knx.link.body.cemi.ControlByte1;
import li.pitschmann.knx.link.body.cemi.ControlByte2;
import li.pitschmann.knx.link.body.cemi.MessageCode;
import li.pitschmann.knx.link.body.cemi.TPCI;
import li.pitschmann.knx.link.communication.KnxStatusData;
import li.pitschmann.knx.link.communication.KnxStatusPoolImpl;
import li.pitschmann.knx.server.strategy.impl.DefaultTunnelingStrategy;
import li.pitschmann.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Flow;

/**
 * Communicator for KNX mock server (package-protected)
 */
public class MockServerProjectLogic implements Flow.Subscriber<Body> {
    private final static Logger logger = LoggerFactory.getLogger(MockServerProjectLogic.class);
    private final MockServer mockServer;
    private final KnxStatusPoolImpl statusPool;

    public MockServerProjectLogic(final MockServer mockServer) {
        this.mockServer = Objects.requireNonNull(mockServer);
        this.statusPool = new KnxStatusPoolImpl();

        // load project
        initGroupAddresses();
//        for (final var xmlGroupAddress : this.project.getGroupAddresses()) {
//            final var groupAddress = GroupAddress.of(Integer.parseInt(xmlGroupAddress.getAddress()));
//            final var apciData = new byte[0]; // TODO: set initial values
//
//            final var knxStatusData = new KnxStatusData(groupAddress, APCI.GROUP_VALUE_WRITE, apciData);
//            statusPool.updateStatus(groupAddress, knxStatusData);
//        }
    }

    private void initGroupAddresses() {
        for (var i = 0; i < 3; i++) {
            // 1/0/10..19 = DPST-1-1
            addToStatusPool(i + "/0/10", "0x00"); // false
            addToStatusPool(i + "/0/11", "0x01"); // true

            // 1/0/20..29 = DPST-2-1
            addToStatusPool(i + "/0/20", "0x00"); // no control, false
            addToStatusPool(i + "/0/21", "0x01"); // no control, true
            addToStatusPool(i + "/0/22", "0x02"); // control, false
            addToStatusPool(i + "/0/23", "0x03"); // control, true

            // 1/0/30..39 = DPST-3-1
            addToStatusPool(i + "/0/30", "0x00"); // Decrease, 0
            addToStatusPool(i + "/0/31", "0x01"); // Decrease, 1
            addToStatusPool(i + "/0/32", "0x02"); // Decrease, 2
            addToStatusPool(i + "/0/33", "0x04"); // Decrease, 4
            addToStatusPool(i + "/0/34", "0x06"); // Decrease, 6
            addToStatusPool(i + "/0/35", "0x08"); // Increase, 0
            addToStatusPool(i + "/0/36", "0x09"); // Increase, 1
            addToStatusPool(i + "/0/37", "0x0B"); // Increase, 3
            addToStatusPool(i + "/0/38", "0x0D"); // Increase, 5
            addToStatusPool(i + "/0/39", "0x0F"); // Increase, 7

            // 1/0/40..49 = DPST-4-1
            addToStatusPool(i + "/0/40", "0x30"); // (ASCII)      0
            addToStatusPool(i + "/0/41", "0x39"); // (ASCII)      9
            addToStatusPool(i + "/0/42", "0x41"); // (ASCII)      A
            addToStatusPool(i + "/0/43", "0x61"); // (ASCII)      a
            addToStatusPool(i + "/0/44", "0xC4"); // (ISO 8895-1) Ä
            addToStatusPool(i + "/0/45", "0xE4"); // (ISO 8895-1) ä
            addToStatusPool(i + "/0/46", "0xE1"); // (ISO 8895-1) á
            addToStatusPool(i + "/0/47", "0xE0"); // (ISO 8895-1) à
            addToStatusPool(i + "/0/48", "0x24"); // (ASCII)      $
            addToStatusPool(i + "/0/49", "0x25"); // (ASCII)      %

            // 1/0/50..59 = DPST-5-1
            addToStatusPool(i + "/0/50", "0x00"); // 0
            addToStatusPool(i + "/0/51", "0x1C"); // 28
            addToStatusPool(i + "/0/52", "0x39"); // 57
            addToStatusPool(i + "/0/53", "0x55"); // 85
            addToStatusPool(i + "/0/54", "0x71"); // 113
            addToStatusPool(i + "/0/55", "0x8E"); // 142
            addToStatusPool(i + "/0/56", "0xAA"); // 170
            addToStatusPool(i + "/0/57", "0xC6"); // 198
            addToStatusPool(i + "/0/58", "0xE3"); // 227
            addToStatusPool(i + "/0/59", "0xFF"); // 255

            // 1/0/60..69 = DPST-6-1
            addToStatusPool(i + "/0/60", "0x00"); // 0
            addToStatusPool(i + "/0/61", "0xFF"); // -128
            addToStatusPool(i + "/0/62", "0xA0"); // -96
            addToStatusPool(i + "/0/63", "0xC0"); // -64
            addToStatusPool(i + "/0/64", "0xE0"); // -32
            addToStatusPool(i + "/0/65", "0x01"); // 1
            addToStatusPool(i + "/0/66", "0x20"); // 32
            addToStatusPool(i + "/0/67", "0x40"); // 64
            addToStatusPool(i + "/0/68", "0x60"); // 96
            addToStatusPool(i + "/0/69", "0x7F"); // 127

            // 1/1/10..19 = DPST-7-1
            addToStatusPool(i + "/1/10", "0x0000"); // 0
            addToStatusPool(i + "/1/11", "0x1C72"); // 7282
            addToStatusPool(i + "/1/12", "0x38E3"); // 14563
            addToStatusPool(i + "/1/13", "0x5555"); // 21845
            addToStatusPool(i + "/1/14", "0x71C7"); // 29127
            addToStatusPool(i + "/1/15", "0x8E38"); // 36408
            addToStatusPool(i + "/1/16", "0xAAAA"); // 43690
            addToStatusPool(i + "/1/17", "0xC71C"); // 50972
            addToStatusPool(i + "/1/18", "0xE38D"); // 58253
            addToStatusPool(i + "/1/19", "0xFFFF"); // 65535

            // 1/1/20..29 = DPST-8-1
            addToStatusPool(i + "/1/20", "0x0000"); // 0
            addToStatusPool(i + "/1/21", "0x8000"); // -32768
            addToStatusPool(i + "/1/22", "0xC001"); // -16383
            addToStatusPool(i + "/1/23", "0xE000"); // -8192
            addToStatusPool(i + "/1/24", "0xF006"); // -4090
            addToStatusPool(i + "/1/25", "0x0001"); // 1
            addToStatusPool(i + "/1/26", "0x1002"); // 4098
            addToStatusPool(i + "/1/27", "0x244C"); // 9292
            addToStatusPool(i + "/1/28", "0x41F4"); // 16884
            addToStatusPool(i + "/1/29", "0x7FFF"); // 32767

            // 1/1/30..39 = DPST-9-1
            addToStatusPool(i + "/1/30", "0x0000"); // 0
            addToStatusPool(i + "/1/31", "0xF800"); // -671088.64
            addToStatusPool(i + "/1/32", "0xF0A3"); // -308838.4
            addToStatusPool(i + "/1/33", "0xE876"); // 158105.6
            addToStatusPool(i + "/1/34", "0xE0F4"); // -73891.84
            addToStatusPool(i + "/1/35", "0x0064"); // 1
            addToStatusPool(i + "/1/36", "0x67C0"); // 81264.64
            addToStatusPool(i + "/1/37", "0x6FAB"); // 160808.96
            addToStatusPool(i + "/1/38", "0x7709"); // 295075.84
            addToStatusPool(i + "/1/39", "0x7FFF"); // 670760.96

            // 1/2/10..19 = DPST-10-1 (WeekDay + Time)
            addToStatusPool(i + "/2/10", "0x000000"); // (No Day),  00:00:00
            addToStatusPool(i + "/2/11", "0x173B3B"); // (No Day),  23:59:59
            addToStatusPool(i + "/2/12", "0x210203"); // Monday,    01:02:03
            addToStatusPool(i + "/2/13", "0x490B0D"); // Tuesday,   09:11:13
            addToStatusPool(i + "/2/14", "0x6C1E0F"); // Wednesday, 12:30:15
            addToStatusPool(i + "/2/15", "0x900E0C"); // Thursday,  16:14:12
            addToStatusPool(i + "/2/16", "0xB41012"); // Friday,    20:16:18
            addToStatusPool(i + "/2/17", "0xD72C37"); // Saturday,  23:44:55
            addToStatusPool(i + "/2/18", "0xEC0000"); // Sunday,    12:00:00
            addToStatusPool(i + "/2/19", "0xF72C37"); // Sunday,    23:59:59

            // 1/2/20..29 = DPST-11-1 (Date)
            addToStatusPool(i + "/2/20", "0x01015A"); // (19)90-01-01
            addToStatusPool(i + "/2/21", "0x070263"); // (19)99-02-07
            addToStatusPool(i + "/2/22", "0x0A0300"); // (20)00-03-10
            addToStatusPool(i + "/2/23", "0x0D040A"); // (20)10-04-13
            addToStatusPool(i + "/2/24", "0x100514"); // (20)20-05-16
            addToStatusPool(i + "/2/25", "0x13061E"); // (20)30-06-19
            addToStatusPool(i + "/2/26", "0x160728"); // (20)40-07-22
            addToStatusPool(i + "/2/27", "0x190832"); // (20)50-08-25
            addToStatusPool(i + "/2/28", "0x1C0A3C"); // (20)60-10-28
            addToStatusPool(i + "/2/29", "0x1F0C59"); // (20)89-12-31

            // 1/3/10..19 = DPST-12-1
            addToStatusPool(i + "/3/10", "0x00000000"); // 0
            addToStatusPool(i + "/3/11", "0x00066612"); // 419346
            addToStatusPool(i + "/3/12", "0x00FE2B52"); // 16657234
            addToStatusPool(i + "/3/13", "0x020032C8"); // 33567432
            addToStatusPool(i + "/3/14", "0x04000019"); // 67108889
            addToStatusPool(i + "/3/15", "0x080F4244"); // 135217732
            addToStatusPool(i + "/3/16", "0x10000185"); // 268435845
            addToStatusPool(i + "/3/17", "0x4BEC5287"); // 1273778823
            addToStatusPool(i + "/3/18", "0x800270FF"); // 2147643647
            addToStatusPool(i + "/3/19", "0xFFFFFFFF"); // 4294967295

            // 1/3/20..29 = DPST-13-1
            addToStatusPool(i + "/3/20", "0x00000000"); // 0
            addToStatusPool(i + "/3/21", "0x80000000"); // -2147483648
            addToStatusPool(i + "/3/22", "0xE0000064"); // -536870812
            addToStatusPool(i + "/3/23", "0xF80186A0"); // -134117728
            addToStatusPool(i + "/3/24", "0xFF9E8480"); // -6388608
            addToStatusPool(i + "/3/25", "0x001FFFFF"); // 2097151
            addToStatusPool(i + "/3/26", "0x0298980F"); // 43554831
            addToStatusPool(i + "/3/27", "0x08061A80"); // 134617728
            addToStatusPool(i + "/3/28", "0x4705C165"); // 1191559525
            addToStatusPool(i + "/3/29", "0x7FFFFFFF"); // 2147483647

            // 1/3/30..39 = DPST-14-1 (double)
            addToStatusPool(i + "/3/30", "0x00000000"); // 0.00
            addToStatusPool(i + "/3/31", "0xFF7FFFFF"); // -3.40282347E38
            addToStatusPool(i + "/3/32", "0xE585B61D"); // -7.892926725953644E22
            addToStatusPool(i + "/3/33", "0xC50D35D7"); // -2259.3649407957196
            addToStatusPool(i + "/3/34", "0xC0B33686"); // -5.600405764881049
            addToStatusPool(i + "/3/35", "0xBD1A9044"); // -0.03773523721714121
            addToStatusPool(i + "/3/36", "0x38C428EB"); // 9.353630138901014E-5
            addToStatusPool(i + "/3/37", "0x42E0F96D"); // 112.48715677535135
            addToStatusPool(i + "/3/38", "0x4D0102BE"); // 1.3527753665155244E8
            addToStatusPool(i + "/3/39", "0x7F7FFFFF"); // 3.40282347e+38

            // 1/3/40..49 = DPST-15-1 (32-bits)
            addToStatusPool(i + "/3/40", "0x00000000"); // bytes[]=0x00 00 00, No Error Detection, Not Accepted, Left-to-Right, No Encryption, Index = 0
            addToStatusPool(i + "/3/41", "0x000000FF"); // bytes[]=0x00 00 00, Error Detection   , Accepted    , Right-to-Left, Encryption   , Index = 15
            addToStatusPool(i + "/3/42", "0x01020310"); // bytes[]=0x01 02 03, No Error Detection, Not Accepted, Left-to-Right, Encryption   , Index = 0
            addToStatusPool(i + "/3/43", "0x04050620"); // bytes[]=0x04 05 06, No Error Detection, Not Accepted, Right-to-Left, No Encryption, Index = 0
            addToStatusPool(i + "/3/44", "0x07080940"); // bytes[]=0x07 08 09, No Error Detection, Accepted    , Left-to-Right, No Encryption, Index = 0
            addToStatusPool(i + "/3/45", "0x0A0B0C80"); // bytes[]=0x0A 0B 0C, Error Detection   , Not Accepted, Left-to-Right, No Encryption, Index = 0
            addToStatusPool(i + "/3/46", "0xF0E0D0C3"); // bytes[]=0xF0 E0 D0, Error Detection   , Accepted    , Left-to-Right, No Encryption, Index = 3
            addToStatusPool(i + "/3/47", "0xC0B0A0E7"); // bytes[]=0xC0 B0 A0, Error Detection   , Accepted    , Right-to-Left, No Encryption, Index = 7
            addToStatusPool(i + "/3/48", "0x090807DC"); // bytes[]=0x09 08 07, Error Detection   , Accepted    , Left-to-Right, Encryption   , Index = 12
            addToStatusPool(i + "/3/49", "0xFFFFFFFF"); // bytes[]=0xFF FF FF, Error Detection   , Accepted    , Right-to-Left, Encryption   , Index = 15

            // 1/7/10..19 = DPST-16-1 (112-bits, String)
            addToStatusPool(i + "/7/10", "0x0000000000000000000000000000"); // (ASCII)      <empty> ""
            addToStatusPool(i + "/7/11", "0x0A200D2009200C00000000000000"); // (ASCII)      <white characters> "\n \r \t \f"
            addToStatusPool(i + "/7/12", "0x4142434445462061626364656600"); // (ASCII)      ABCDEFabcdef
            addToStatusPool(i + "/7/13", "0x3031323334353637383900000000"); // (ASCII)      0123456789
            addToStatusPool(i + "/7/14", "0x2B2D5F2F5CA67C2A252E3A2C3B3D"); // (ISO 8859-1) +-_/\¦|*%.:,;=
            addToStatusPool(i + "/7/15", "0x5EAC2722B4607E00000000000000"); // (ISO 8859-1) ^¬'"´`~
            addToStatusPool(i + "/7/16", "0x2324A2A3A7E7DFFFBFA1213F0000"); // (ISO 8859-1) #$¢£§çßÿ¿¡!?
            addToStatusPool(i + "/7/17", "0xE0E1E2E3E4E5E6C1C2C3C4C5C600"); // (ISO 8859-1) àáâãäåæÁÂÃÄÅÆ
            addToStatusPool(i + "/7/18", "0x28297B7D5B5D3C3EABBB00000000"); // (ISO 8859-1) (){}[]<>«»
            addToStatusPool(i + "/7/19", "0x202048656C6C6F204B4E58212020"); // (ASCII)      <leading/tailing white spaces> "  Hello KNX!  "
        }
    }

    private void addToStatusPool(final String groupAddressAsString, final String apciDataAsHexStream) {
        final var groupAddress = GroupAddress.of(groupAddressAsString);
        final var apciData = Bytes.toByteArray(apciDataAsHexStream);

        final var knxStatusData = new KnxStatusData(groupAddress, APCI.GROUP_VALUE_WRITE, apciData);
        statusPool.updateStatus(groupAddress, knxStatusData);
    }

    protected MockServer getMockServer() {
        return mockServer;
    }

    @Override
    public void onNext(final Body body) {
        logger.debug("Body received, but no logic defined: {}", body);

        if (body instanceof TunnelingRequestBody) {
            final var requestBody = (TunnelingRequestBody) body;
            if (requestBody.getCEMI().getApci() == APCI.GROUP_VALUE_READ) {
                // TODO: analyze if response should be done at all? (e.g. communication flag, ...)
                // read
                final var destAddress = requestBody.getCEMI().getDestinationAddress();
                final var statusData = this.statusPool.getStatusFor(destAddress);
                if (statusData != null) {
                    final var apciData = statusData.getApciData();
                    logger.debug("apciData: {}", Arrays.toString(apciData));
                    final var cemi = CEMI.create(
                            MessageCode.L_DATA_IND,
                            AdditionalInfo.empty(),
                            ControlByte1.useDefault(),
                            ControlByte2.useDefault(destAddress),
                            IndividualAddress.useDefault(),
                            destAddress,
                            TPCI.UNNUMBERED_PACKAGE,
                            0,
                            APCI.GROUP_VALUE_RESPONSE,
                            apciData);
                    this.mockServer.addToOutbox(new DefaultTunnelingStrategy().createRequest(this.mockServer, cemi).getBody());
                }
            } else if (requestBody.getCEMI().getApci() == APCI.GROUP_VALUE_WRITE) {
                // write action
                this.statusPool.updateStatus(requestBody.getCEMI());
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("Error during KNX Mock Server Communicator class", throwable);
    }

    @Override
    public void onComplete() {
        // NO-OP
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }
}
