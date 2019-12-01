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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.body.TunnelingRequestBody;
import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.body.address.IndividualAddress;
import li.pitschmann.knx.core.body.address.KnxAddress;
import li.pitschmann.knx.core.body.cemi.APCI;
import li.pitschmann.knx.core.body.cemi.AdditionalInfo;
import li.pitschmann.knx.core.body.cemi.CEMI;
import li.pitschmann.knx.core.body.cemi.ControlByte1;
import li.pitschmann.knx.core.body.cemi.ControlByte2;
import li.pitschmann.knx.core.body.cemi.MessageCode;
import li.pitschmann.knx.core.body.cemi.TPCI;
import li.pitschmann.knx.core.communication.KnxStatusData;
import li.pitschmann.knx.core.knxproj.XmlProject;
import li.pitschmann.knx.core.test.strategy.impl.DefaultTunnelingStrategy;
import li.pitschmann.knx.core.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Flow;

/**
 * Project Logic for KNX mock server (package-protected)
 */
public class MockServerProjectLogic implements Flow.Subscriber<Body> {
    private final static Logger log = LoggerFactory.getLogger(MockServerProjectLogic.class);
    private final MockServer mockServer;
    private final XmlProject xmlProject;
    private final Map<KnxAddress, KnxStatusData> groupAddressStatusMap;

    MockServerProjectLogic(final MockServer mockServer, final XmlProject xmlProject) {
        this.mockServer = Objects.requireNonNull(mockServer);
        this.xmlProject = Objects.requireNonNull(xmlProject);
        this.groupAddressStatusMap = new HashMap<>(1024);

        // load project
        initGroupAddresses();
    }

    /**
     * Initializes the Group Addresses
     * <p>
     * For group addresses that contains "Sub Group - DPT" the initial value
     * are within "(" and ")" brackets, example:<br/>
     * <pre>
     * 0/0/10 -> Sub Group - DPT 1 (0x00)
     * 0/0/56 -> Sub Group - DPT 5 (0xAA)
     * </pre>
     */
    private void initGroupAddresses() {
        for (final var xmlGroupAddress : xmlProject.getGroupAddresses()) {
            final var groupAddress = GroupAddress.of(xmlGroupAddress.getAddress());
            final var groupAddressName = xmlGroupAddress.getName();

            final byte[] apciData;
            if (groupAddressName.contains("Sub Group - DPT")) {
                final var apciDataAsHexStream = xmlGroupAddress.getName().substring(xmlGroupAddress.getName().lastIndexOf('(') + 1, xmlGroupAddress.getName().lastIndexOf(')'));
                apciData = Bytes.toByteArray(apciDataAsHexStream);
            } else {
                apciData = new byte[1];
            }

            final var knxStatusData = new KnxStatusData(groupAddress, APCI.GROUP_VALUE_WRITE, apciData);
            log.debug("KNX Status Data loaded: {}", knxStatusData);
            groupAddressStatusMap.put(groupAddress, knxStatusData);
        }
    }

    @Override
    public void onNext(final Body body) {
        log.debug("Body received, but no logic defined: {}", body);

        if (body instanceof TunnelingRequestBody) {
            final var requestBody = (TunnelingRequestBody) body;
            final var cemi = requestBody.getCEMI();
            final var destAddress = cemi.getDestinationAddress();
            if (cemi.getApci() == APCI.GROUP_VALUE_READ) {
                // TODO: analyze if response should be done at all? (e.g. communication flag, ...)
                // read
                final var statusData = this.groupAddressStatusMap.get(destAddress);
                if (statusData != null) {
                    final var apciData = statusData.getApciData();
                    log.debug("apciData: {}", Arrays.toString(apciData));
                    final var cemiIndication = CEMI.of(
                            MessageCode.L_DATA_IND,
                            AdditionalInfo.empty(),
                            ControlByte1.useDefault(),
                            ControlByte2.of(destAddress),
                            IndividualAddress.useDefault(),
                            destAddress,
                            TPCI.UNNUMBERED_PACKAGE,
                            0,
                            APCI.GROUP_VALUE_RESPONSE,
                            apciData);
                    this.mockServer.addToOutbox(new DefaultTunnelingStrategy().createRequest(this.mockServer, cemiIndication).getBody());
                }
            } else if (requestBody.getCEMI().getApci() == APCI.GROUP_VALUE_WRITE) {
                // write action
                this.groupAddressStatusMap.put(cemi.getDestinationAddress(), new KnxStatusData(cemi));
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error during KNX Mock Server Communicator class", throwable);
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
