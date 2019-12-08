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

package li.pitschmann.knx.core.knxproj.parser;

import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.exceptions.KnxProjectParserException;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.knxproj.XmlGroupAddressStyle;
import li.pitschmann.knx.core.knxproj.XmlProject;
import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test the KNX Project Parser
 */
public class ParserTest {
    private static final Path KNX_PROJECT_THREE_LEVEL_V20 = Paths.get("src/test/resources/knxproj/Project (3-Level, v20).knxproj");
    private static final Path KNX_PROJECT_THREE_LEVEL_V14 = Paths.get("src/test/resources/knxproj/Project (3-Level, v14).knxproj");
    private static final Path KNX_PROJECT_FREE_LEVEL = Paths.get("src/test/resources/knxproj/Project (Free-Level, v20).knxproj");
    private static final Path GOOD_EMPTY_PROJECT = Paths.get("src/test/resources/knxproj/Empty Project (No Group Addresses).knxproj");
    private static final Path CORRUPTED_FILE = Paths.get("src/test/resources/knxproj/Corrupted Project (Incomplete).knxproj");
    private static final Path CORRUPTED_NO_PROJECT_ID = Paths.get("src/test/resources/knxproj/Corrupted Project (No Project Id).knxproj");
    private static final Path CORRUPTED_NO_PROJECTINFORMATION_NAME = Paths.get("src/test/resources/knxproj/Corrupted Project (No ProjectInformation Name).knxproj");
    private static final Path CORRUPTED_NO_PROJECTINFORMATION_GROUPADDRESS_STYLE = Paths.get("src/test/resources/knxproj/Corrupted Project (No ProjectInformation GroupAddressStyle).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_ID = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupAddress Id).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_ADDRESS = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupAddress Address).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_NAME = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupAddress Name).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_ID = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupRange Id).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_RANGE_START = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupRange RangeStart).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_RANGE_END = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupRange RangeEnd).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_NAME = Paths.get("src/test/resources/knxproj/Corrupted Project (No GroupRange Name).knxproj");

    @Test
    @DisplayName("(Good) Test KNX Project V14 with 3-Level group addresses")
    public void testThreeLevelProjectV14() {
        final var project = Parser.asXmlProject(KNX_PROJECT_THREE_LEVEL_V14);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0501");
        assertThat(project.getName()).isEqualTo("Project (3-Level)");
        assertThat(project.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.THREE_LEVEL);
        assertThat(project.getGroupAddresses()).hasSize(189);
        assertThat(project.getGroupRanges()).hasSize(18);

        // ---------------------
        // Range Group Check
        // ---------------------
        final var xmlGroupRanges = project.getMainGroupRanges();
        assertThat(xmlGroupRanges).hasSize(3);

        // Main Group: 0
        final var mainGroup0 = xmlGroupRanges.get(0);
        assertThat(mainGroup0.getId()).isEqualTo("P-0501-0_GR-47");
        assertThat(mainGroup0.getChildGroupRanges()).hasSize(8);
        assertThat(mainGroup0.getGroupAddresses()).isEmpty();
        final var subGroup0_0 = mainGroup0.getChildGroupRanges().get(0);
        assertThat(subGroup0_0.getChildGroupRanges()).isEmpty();
        assertThat(subGroup0_0.getGroupAddresses()).hasSize(46);

        // Main Group: 1
        final var mainGroup1 = xmlGroupRanges.get(1);
        assertThat(mainGroup1.getId()).isEqualTo("P-0501-0_GR-67");
        assertThat(mainGroup1.getChildGroupRanges()).hasSize(5);
        assertThat(mainGroup1.getGroupAddresses()).isEmpty();
        final var subGroup1_2 = mainGroup1.getChildGroupRanges().get(2);
        assertThat(subGroup1_2.getChildGroupRanges()).isEmpty();
        assertThat(subGroup1_2.getGroupAddresses()).hasSize(3);

        // Main Group: 2
        final var mainGroup2 = xmlGroupRanges.get(2);
        assertThat(mainGroup2.getId()).isEqualTo("P-0501-0_GR-69");
        assertThat(mainGroup2.getChildGroupRanges()).hasSize(2);
        assertThat(mainGroup2.getGroupAddresses()).isEmpty();
        final var subGroup2_0 = mainGroup2.getChildGroupRanges().get(0);
        assertThat(subGroup2_0.getChildGroupRanges()).isEmpty();
        assertThat(subGroup2_0.getGroupAddresses()).hasSize(11);
    }

    @Test
    @DisplayName("(Good) Test KNX Project V20 with 3-Level group addresses")
    public void testThreeLevelProjectV20() {
        final var project = Parser.asXmlProject(KNX_PROJECT_THREE_LEVEL_V20);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0503");
        assertThat(project.getName()).isEqualTo("Project (3-Level)");
        assertThat(project.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.THREE_LEVEL);
        assertThat(project.getGroupAddresses()).hasSize(189);
        assertThat(project.getGroupRanges()).hasSize(18);

        // ---------------------
        // Range Group Check
        // ---------------------
        final var xmlGroupRanges = project.getMainGroupRanges();
        assertThat(xmlGroupRanges).hasSize(3);

        // Main Group: 0
        final var mainGroup0 = xmlGroupRanges.get(0);
        assertThat(mainGroup0.getId()).isEqualTo("P-0503-0_GR-47");
        assertThat(mainGroup0.getChildGroupRanges()).hasSize(8);
        assertThat(mainGroup0.getGroupAddresses()).isEmpty();
        final var subGroup0_0 = mainGroup0.getChildGroupRanges().get(0);
        assertThat(subGroup0_0.getChildGroupRanges()).isEmpty();
        assertThat(subGroup0_0.getGroupAddresses()).hasSize(46);

        // Main Group: 1
        final var mainGroup1 = xmlGroupRanges.get(1);
        assertThat(mainGroup1.getId()).isEqualTo("P-0503-0_GR-67");
        assertThat(mainGroup1.getChildGroupRanges()).hasSize(5);
        assertThat(mainGroup1.getGroupAddresses()).isEmpty();
        final var subGroup1_2 = mainGroup1.getChildGroupRanges().get(2);
        assertThat(subGroup1_2.getChildGroupRanges()).isEmpty();
        assertThat(subGroup1_2.getGroupAddresses()).hasSize(3);

        // Main Group: 2
        final var mainGroup2 = xmlGroupRanges.get(2);
        assertThat(mainGroup2.getId()).isEqualTo("P-0503-0_GR-69");
        assertThat(mainGroup2.getChildGroupRanges()).hasSize(2);
        assertThat(mainGroup2.getGroupAddresses()).isEmpty();
        final var subGroup2_0 = mainGroup2.getChildGroupRanges().get(0);
        assertThat(subGroup2_0.getChildGroupRanges()).isEmpty();
        assertThat(subGroup2_0.getGroupAddresses()).hasSize(11);
    }

    /**
     * Tests if {@link XmlProject} (Free-Level) has been parsed correctly
     */
    @Test
    @DisplayName("(Good) Test KNX Project with Free-Level group addresses")
    public void testFreeLevelProject() {
        final var project = Parser.asXmlProject(KNX_PROJECT_FREE_LEVEL);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0502");
        assertThat(project.getName()).isEqualTo("Project (Free-Level)");
        assertThat(project.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.FREE_LEVEL);
        assertThat(project.getGroupAddresses()).hasSize(189);
        assertThat(project.getGroupRanges()).hasSize(18);
        assertThat(project.getGroupRangeById("P-0502-0_GR-78").getGroupAddresses()).hasSize(46);
        assertThat(project.getGroupRangeById("P-0502-0_GR-79").getGroupAddresses()).hasSize(30);
    }

    /**
     * Tests if {@link XmlGroupAddress} have been parsed correctly
     */
    @Test
    @DisplayName("Test group address datapoint types")
    public void testDataPoints() {
        final var groupAddresses = Parser.asXmlProject(KNX_PROJECT_THREE_LEVEL_V20).getGroupAddresses();

        // assert DPT-x group address
        assertGroupAddress(groupAddresses, "P-0503-0_GA-117", GroupAddress.of(0, 0, 10), "Sub Group - DPT 1 (0x00)", "DPT-1");
        assertGroupAddress(groupAddresses, "P-0503-0_GA-128", GroupAddress.of(0, 3, 10), "Sub Group - DPT 12 (0x00 00 00 00)", "DPT-12");

//        // assert DPST-x-y group address TODO: Define DPST in XML Project File too
//        assertGroupAddress(groupAddresses, "P-0503-0_GA-133", GroupAddress.of(1, 0, 10), "Sub Group - DPST 1.001 ", "DPST-1-1");
//        assertGroupAddress(groupAddresses, "P-0503-0_GA-143", GroupAddress.of(1, 2, 20), "Sub Group - DPST 11.001", "DPST-11-1");
//
//        // assert group address without DPT TODO: Define No DPT in XML Project File too
//        assertGroupAddress(groupAddresses, "P-0503-0_GA-149", GroupAddress.of(2, 0, 0), "Sub Group - No DPT 1-byte", null);
//        assertGroupAddress(groupAddresses, "P-0503-0_GA-188", GroupAddress.of(2, 3, 0), "Sub Group - No DPT 4-bytes", null);
    }

    @Test
    @DisplayName("(Good) Test KNX project without any group addresses")
    public void testEmptyProject() {
        final var project = Parser.asXmlProject(GOOD_EMPTY_PROJECT);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0700");
        assertThat(project.getName()).isEqualTo("Project (Empty)");
        assertThat(project.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.FREE_LEVEL);
        assertThat(project.getGroupAddresses()).isEmpty();
    }

    /**
     * Asserts a corrupted KNX project without mandatory attributes
     * <ul>
     * <li>Missing {@code @Id} on {@code <Project />}</li>
     * <li>Missing {@code @Name} on {@code <ProjectInformation />}</li>
     * <li>Missing {@code @GroupAddressStyle} on {@code <ProjectInformation />}</li>
     * <li>Missing {@code @Id} on {@code <GroupRange />}</li>
     * <li>Missing {@code @RangeStart} on {@code <GroupRange />}</li>
     * <li>Missing {@code @RangeEnd} on {@code <GroupRange />}</li>
     * <li>Missing {@code @Name} on {@code <GroupRange />}</li>
     * <li>Missing {@code @Id} on {@code <GroupAddress />}</li>
     * <li>Missing {@code @Name} on {@code <GroupAddress />}</li>
     * <li>Missing {@code @Address} on {@code <GroupAddress />}</li>
     * </ul>
     */
    @Test
    @DisplayName("(Corrupted) Test KNX project without mandatory attributes")
    public void testCorruptedProjectMissingAttributes() {
        /*
         * Corrupted Project (general)
         */
        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_PROJECT_ID))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <Project @Id /> not found.");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_PROJECTINFORMATION_NAME))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <ProjectInformation @Name /> not found.");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_PROJECTINFORMATION_GROUPADDRESS_STYLE))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <ProjectInformation @GroupAddressStyle /> not found.");

        /*
         * Group Range Negative Tests
         */
        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPRANGE_ID))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupRange @Id /> not found.");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPRANGE_RANGE_START))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupRange @RangeStart /> not found for: P-0700-0_GR-1");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPRANGE_RANGE_END))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupRange @RangeEnd /> not found for: P-0700-0_GR-1");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPRANGE_NAME))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupRange @Name /> not found for: P-0700-0_GR-1");

        /*
         * Group Address Negative Tests
         */
        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPADDRESS_ID))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupAddress @Id /> not found.");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPADDRESS_NAME))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupAddress @Name /> not found for: P-0700-0_GA-1");

        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_NO_GROUPADDRESS_ADDRESS))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessage("Attribute <GroupAddress @Address /> not found for: P-0700-0_GA-1");
    }

    /**
     * Asserts a corrupted KNX project (incomplete content)
     */
    @Test
    @DisplayName("(Corrupted) Test KNX project with incomplete content")
    public void testCorruptedProjectIncomplete() {
        assertThatThrownBy(() -> Parser.asXmlProject(CORRUPTED_FILE))
                .isInstanceOf(KnxProjectParserException.class)
                .hasMessageStartingWith("Something went wrong during parsing the zip file:");
    }

    /**
     * Test constructor of {@link Parser}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Parser.class);
    }

    private void assertGroupAddress(final Collection<XmlGroupAddress> groupAddresses, final String id, final GroupAddress address, final String name, final String datapointType) {
        final var groupAddress = groupAddresses.stream().filter(xga -> id.equals(xga.getId())).findFirst().get();
        assertThat(groupAddress.getId()).isEqualTo(id);
        assertThat(groupAddress.getAddress()).isEqualTo(address.getAddress());
        assertThat(groupAddress.getName()).isEqualTo(name);
        assertThat(groupAddress.getDataPointType()).isEqualTo(datapointType);
    }
}
