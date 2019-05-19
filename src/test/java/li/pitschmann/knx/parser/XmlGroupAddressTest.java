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

package li.pitschmann.knx.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link XmlGroupAddress} that contains data from '*.knxproj' file
 */
public class XmlGroupAddressTest {

    @Test
    @DisplayName("Tests XmlGroupAddress#toString()")
    public void testToString() {
        final var xmlGroupAddress = new XmlGroupAddress();
        xmlGroupAddress.setId("ID");
        xmlGroupAddress.setParentId("PARENT_ID");
        xmlGroupAddress.setName("NAME");
        xmlGroupAddress.setAddress("ADDRESS");
        xmlGroupAddress.setDescription("DESCRIPTION");
        xmlGroupAddress.setDatapointType("DPT");
        xmlGroupAddress.setCommunicationFlag("C");
        xmlGroupAddress.setReadFlag("R");
        xmlGroupAddress.setWriteFlag("W");
        xmlGroupAddress.setTransmitFlag("T");
        xmlGroupAddress.setUpdateFlag("U");

        // @formatter:off
        // test toString()
        assertThat(xmlGroupAddress).hasToString("XmlGroupAddress" +
                "{" +
                    "id=ID, " +
                    "parentId=PARENT_ID, " +
                    "address=ADDRESS, " +
                    "name=NAME, " +
                    "description=DESCRIPTION, " +
                    "datapointType=DPT, " +
                    "communicationFlag=C, " +
                    "readFlag=R, " +
                    "writeFlag=W, " +
                    "transmitFlag=T, " +
                    "updateFlag=U" +
                "}");
        // @formatter:on
    }

}
