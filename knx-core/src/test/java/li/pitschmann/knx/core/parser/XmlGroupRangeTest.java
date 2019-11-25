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

package li.pitschmann.knx.core.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link XmlGroupRange} that contains data from '*.knxproj' file
 */
public class XmlGroupRangeTest {

    @Test
    @DisplayName("Tests XmlGroupRange#toString()")
    public void testToString() {
        // sub group ranges
        final var xmlSubGroupRange1 = mock(XmlGroupRange.class);
        when(xmlSubGroupRange1.toString()).thenReturn("xsg1");
        final var xmlSubGroupRange2 = mock(XmlGroupRange.class);
        when(xmlSubGroupRange2.toString()).thenReturn("xsg2");

        // group addresses
        final var xmlGroupAddress1 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress1.toString()).thenReturn("xga1-id");
        final var xmlGroupAddress2 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress2.toString()).thenReturn("xga2-id");
        final var xmlGroupAddress3 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress3.toString()).thenReturn("xga3-id");

        // current group range
        final var xmlGroupRange = new XmlGroupRange();
        xmlGroupRange.setId("XGR-ID");
        xmlGroupRange.setLevel(4711);
        xmlGroupRange.setRangeStart(13);
        xmlGroupRange.setRangeEnd(17);
        xmlGroupRange.setName("XGR-NAME");
        xmlGroupRange.setChildGroupRanges(List.of(xmlSubGroupRange1, xmlSubGroupRange2));
        xmlGroupRange.setGroupAddresses(List.of(xmlGroupAddress1, xmlGroupAddress2, xmlGroupAddress3));

        // check if setter/getter are working
        assertThat(xmlGroupRange.getId()).isEqualTo("XGR-ID");
        assertThat(xmlGroupRange.getLevel()).isEqualTo(4711);
        assertThat(xmlGroupRange.getRangeStart()).isEqualTo(13);
        assertThat(xmlGroupRange.getRangeEnd()).isEqualTo(17);
        assertThat(xmlGroupRange.getName()).isEqualTo("XGR-NAME");
        assertThat(xmlGroupRange.getChildGroupRanges()).hasSize(2);
        assertThat(xmlGroupRange.getGroupAddresses()).hasSize(3);

        // @formatter:off
        // test toString()
        assertThat(xmlGroupRange).hasToString("XmlGroupRange" +
                "{" +
                    "id=XGR-ID, " +
                    "level=4711, " +
                    "rangeStart=13, " +
                    "rangeEnd=17, " +
                    "name=XGR-NAME, " +
                    "childGroupRanges=[xsg1, xsg2], " +
                    "groupAddresses=[xga1-id, xga2-id, xga3-id]" +
                "}");
        // @formatter:on
    }

}
