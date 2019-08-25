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

import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link XmlProject} that contains data from '*.knxproj' file
 */
public class XmlProjectTest {

    @Test
    @DisplayName("Tests XmlProject#toString()")
    public void testToString() {
        // create some mock data
        final var xmlGroupAddress1 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress1.toString()).thenReturn("xga1"); // only for #toString() simulation
        when(xmlGroupAddress1.getAddress()).thenReturn("1"); // needed for internal mapping (will be converted to int)
        final var xmlGroupAddress2 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress2.toString()).thenReturn("xga2");
        when(xmlGroupAddress2.getAddress()).thenReturn("2");
        final var xmlGroupAddress3 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress3.toString()).thenReturn("xga3");
        when(xmlGroupAddress3.getAddress()).thenReturn("3");
        final var xmlGroupAddresses = Lists.newArrayList(xmlGroupAddress1, xmlGroupAddress2, xmlGroupAddress3);

        final var groupRange1 = mock(XmlGroupRange.class);
        when(groupRange1.getId()).thenReturn("xgg1-id");
        when(groupRange1.toString()).thenReturn("xgg1");
        final var groupRange2 = mock(XmlGroupRange.class);
        when(groupRange2.getId()).thenReturn("xgg2-id");
        when(groupRange2.toString()).thenReturn("xgg2");
        final var groupRanges = Lists.newArrayList(groupRange1, groupRange2);

        // given
        final var xmlProject = new XmlProject();
        xmlProject.setGroupAddressStyle("GA_STYLE");
        xmlProject.setId("PROJECT_ID");
        xmlProject.setName("PROJECT_NAME");
        xmlProject.setGroupRanges(groupRanges);
        xmlProject.setGroupAddresses(xmlGroupAddresses);

        // @formatter:off
        // test toString()
        assertThat(xmlProject).hasToString("" +
                "XmlProject{" +
                    "id=PROJECT_ID, " +
                    "name=PROJECT_NAME, " +
                    "groupAddressStyle=GA_STYLE, " +
                    "groupAddressMap={xga1-id=xga1, xga2-id=xga2, xga3-id=xga3}, " +
                    "groupRangeMap={xgg1-id=xgg1, xgg2-id=xgg2}" +
                "}");
        // @formatter:on
    }

}
