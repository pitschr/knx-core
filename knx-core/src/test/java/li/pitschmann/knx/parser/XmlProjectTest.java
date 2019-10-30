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
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.exceptions.KnxNumberOutOfRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link XmlProject} that contains data from '*.knxproj' file
 */
public class XmlProjectTest {
    private static final Path KNX_PROJECT_V14 = Paths.get("src/test/resources/parser/Project (3-Level, v14).knxproj");

    @Test
    @DisplayName("Tests XmlProject#toString()")
    public void testToString() {
        // create some mock data
        final var xmlGroupAddress1 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress1.getId()).thenReturn("xga1-id");
        when(xmlGroupAddress1.getAddress()).thenReturn("1"); // needed for internal mapping (will be converted to int)
        when(xmlGroupAddress1.toString()).thenReturn("xga1"); // only for #toString() simulation
        final var xmlGroupAddress2 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress2.getId()).thenReturn("xga2-id");
        when(xmlGroupAddress2.getAddress()).thenReturn("2");
        when(xmlGroupAddress2.toString()).thenReturn("xga2");
        final var xmlGroupAddress3 = mock(XmlGroupAddress.class);
        when(xmlGroupAddress3.getId()).thenReturn("xga3-id");
        when(xmlGroupAddress3.getAddress()).thenReturn("3");
        when(xmlGroupAddress3.toString()).thenReturn("xga3");
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
        xmlProject.setGroupAddressStyle(XmlGroupAddressStyle.THREE_LEVEL);
        xmlProject.setId("PROJECT_ID");
        xmlProject.setName("PROJECT_NAME");
        xmlProject.setGroupRanges(groupRanges);
        xmlProject.setGroupAddresses(xmlGroupAddresses);

        // test toString()
        assertThat(xmlProject).hasToString(String.format(
                "XmlProject" + //
                        "{" + //
                        "id=PROJECT_ID, " + //
                        "name=PROJECT_NAME, " + //
                        "groupAddressStyle=%s, " + //
                        "groupAddressMap={xga1-id=xga1, xga2-id=xga2, xga3-id=xga3}, " + //
                        "groupRangeMap={xgg1-id=xgg1, xgg2-id=xgg2}" + //
                        "}", XmlGroupAddressStyle.THREE_LEVEL));
    }

    @Test
    @DisplayName("Test common methods on empty project")
    public void testEmptyProject() {
        final var xmlProject = new XmlProject();

        assertThat(xmlProject.getId()).isNull();
        assertThat(xmlProject.getName()).isNull();
        assertThat(xmlProject.getGroupAddressStyle()).isNull();

        // range groups
        assertThat(xmlProject.getGroupRanges()).isNotNull().isEmpty();
        assertThat(xmlProject.getGroupRangeById("foo")).isNull();

        // group addresses
        assertThat(xmlProject.getGroupAddresses()).isNotNull().isEmpty();
        assertThat(xmlProject.getGroupAddress(0)).isNull();
        assertThat(xmlProject.getGroupAddress(GroupAddress.of(1, 2, 3))).isNull();

        // all main groups
        assertThat(xmlProject.getMainGroups()).isEmpty();

        // main group
        assertThatThrownBy(() -> xmlProject.getMainGroup(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No main groups available");

        // middle group
        assertThatThrownBy(() -> xmlProject.getMiddleGroup(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No main groups available");      // indirectly called by getMainGroup(int)
    }

    @Test
    @DisplayName("Test common methods on non-empty project")
    public void testExistingProject() {
        final var xmlProject = KnxprojParser.parse(KNX_PROJECT_V14);

        assertThat(xmlProject.getId()).isEqualTo("P-0501");
        assertThat(xmlProject.getName()).isEqualTo("Project (3-Level)");
        assertThat(xmlProject.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.THREE_LEVEL);

        assertThat(xmlProject.getGroupRanges()).isNotEmpty();
        assertThat(xmlProject.getGroupAddresses()).isNotEmpty();
    }

    @Test
    @DisplayName("Get all range groups and by id")
    public void testRangeGroups() {
        final var xmlProject = KnxprojParser.parse(KNX_PROJECT_V14);

        // existing range group
        assertThat(xmlProject.getGroupRangeById("P-0501-0_GR-67").getName()).isEqualTo("Main Group - Flags");
        assertThat(xmlProject.getGroupRangeById("P-0501-0_GR-50").getName()).isEqualTo("Middle Group - DPT (3-bytes)");

        // non-exiting range group
        assertThat(xmlProject.getGroupRangeById(null)).isNull();
        assertThat(xmlProject.getGroupRangeById("foo")).isNull();
    }

    @Test
    @DisplayName("Get all group addresses and by id")
    public void testGroupAddresses() {
        final var xmlProject = KnxprojParser.parse(KNX_PROJECT_V14);

        // exiting group addresses
        assertThat(xmlProject.getGroupAddressById("P-0501-0_GA-117").getName()).isEqualTo("Sub Group - DPT 1 (0x00)");
        assertThat(xmlProject.getGroupAddressById("P-0501-0_GA-252").getName()).isEqualTo("Sub Group - DPT 7 (0x38 E3)");
        assertThat(xmlProject.getGroupAddress(275).getName()).isEqualTo("Sub Group - DPT 7 (0xFF FF)");
        assertThat(xmlProject.getGroupAddress(537).getName()).isEqualTo("Sub Group - DPT 11 (0x13 06 1E)");
        assertThat(xmlProject.getGroupAddress(GroupAddress.of(1, 0, 110)).getName()).isEqualTo("Sub Group - Flags (2 GAs, No Flags)");
        assertThat(xmlProject.getGroupAddress(GroupAddress.of(2, 0, 7)).getName()).isEqualTo("Sub Group - Characters (è é ê ë È É Ê Ë)");

        // non-existing group addresses
        assertThat(xmlProject.getGroupAddressById("foo")).isNull();
        assertThat(xmlProject.getGroupAddress(64711)).isNull();
        assertThat(xmlProject.getGroupAddress(GroupAddress.of(31, 2, 3))).isNull();
    }

    @Test
    @DisplayName("Get all main groups")
    public void testAllMainGroups() {
        final var xmlProject = KnxprojParser.parse(KNX_PROJECT_V14);

        final var mainRanges = xmlProject.getMainGroups();
        assertThat(mainRanges).hasSize(3);
        assertThat(mainRanges.get(0).getName()).isEqualTo("Main Group - DPT");
        assertThat(mainRanges.get(1).getName()).isEqualTo("Main Group - Flags");
        assertThat(mainRanges.get(2).getName()).isEqualTo("Main Group - Text / Encoding");
    }

    @Test
    @DisplayName("Get selected main group")
    public void testMainGroup() {
        final var xmlProject = KnxprojParser.parse(KNX_PROJECT_V14);

        // existing main groups
        assertThat(xmlProject.getMainGroup(0).getName()).isEqualTo("Main Group - DPT");
        assertThat(xmlProject.getMainGroup(1).getName()).isEqualTo("Main Group - Flags");
        assertThat(xmlProject.getMainGroup(2).getName()).isEqualTo("Main Group - Text / Encoding");

        // non-exiting main groups
        assertThatThrownBy(() -> xmlProject.getMainGroup(7)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> xmlProject.getMainGroup(11)).isInstanceOf(IllegalArgumentException.class);

        // invalid main groups (because group number is outside of KNX specification)
        assertThatThrownBy(() -> xmlProject.getMainGroup(-1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> xmlProject.getMainGroup(100)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }

    @Test
    @DisplayName("Get selected middle group")
    public void testMiddleGroup() {
        final var xmlProject = KnxprojParser.parse(KNX_PROJECT_V14);

        // existing main groups
        assertThat(xmlProject.getMiddleGroup(0, 0).getName()).isEqualTo("Middle Group - DPT (1-byte)");
        assertThat(xmlProject.getMiddleGroup(0, 5).getName()).isEqualTo("Middle Group - DPT (8-bytes)");
        assertThat(xmlProject.getMiddleGroup(1, 3).getName()).isEqualTo("Middle Group - Filtering");
        assertThat(xmlProject.getMiddleGroup(2, 0).getName()).isEqualTo("Middle Group - Characters");
        assertThat(xmlProject.getMiddleGroup(2, 1).getName()).isEqualTo("Middle Group - Comments");

        // non-exiting main groups
        assertThatThrownBy(() -> xmlProject.getMiddleGroup(2, 7)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> xmlProject.getMiddleGroup(11, 0)).isInstanceOf(IllegalArgumentException.class);

        // invalid main groups (because group number is outside of KNX specification)
        assertThatThrownBy(() -> xmlProject.getMiddleGroup(0, -1)).isInstanceOf(KnxNumberOutOfRangeException.class);
        assertThatThrownBy(() -> xmlProject.getMiddleGroup(0, 100)).isInstanceOf(KnxNumberOutOfRangeException.class);
    }
}
