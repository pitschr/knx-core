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

        // current group range
        final var xmlGroupRange = new XmlGroupRange();
        xmlGroupRange.setId("XGR-ID");
        xmlGroupRange.setLevel(4711);
        xmlGroupRange.setRangeStart(13);
        xmlGroupRange.setRangeEnd(17);
        xmlGroupRange.setName("XGR-NAME");
        xmlGroupRange.setChildGroupRanges(Lists.newArrayList(xmlSubGroupRange1, xmlSubGroupRange2));
        xmlGroupRange.setGroupAddressIds(Lists.newArrayList("xga1-id", "xga2-id", "xga3-id"));

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
                    "groupAddressIds=[xga1-id, xga2-id, xga3-id]" +
                "}");
        // @formatter:on
    }

}
