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

package li.pitschmann.parser;

import com.google.common.collect.Lists;
import li.pitschmann.knx.parser.XmlGroupAddress;
import li.pitschmann.knx.parser.XmlProject;
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
        // create some addresses
        final var xmlGroupAddress1 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress1.toString()).thenReturn("xga1");
        final var xmlGroupAddress2 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress2.toString()).thenReturn("xga2");
        final var xmlGroupAddress3 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress3.toString()).thenReturn("xga3");

        // given
        final var xmlProject = new XmlProject();
        xmlProject.setGroupAddresses(Lists.newArrayList(xmlGroupAddress1, xmlGroupAddress2, xmlGroupAddress3));
        xmlProject.setGroupAddressStyle("GA_STYLE");
        xmlProject.setId("PROJECT_ID");
        xmlProject.setName("Project Name");

        // test toString()
        assertThat(xmlProject).hasToString("XmlProject{id=PROJECT_ID, name=Project Name, groupAddressStyle=GA_STYLE, groupAddresses=[xga1, xga2, xga3]}");
    }

}
