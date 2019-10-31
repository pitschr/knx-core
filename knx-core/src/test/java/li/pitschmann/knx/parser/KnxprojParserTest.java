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

import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link KnxprojParser} class to parse KNX Project files
 */
public class KnxprojParserTest {
    private static final Path KNX_PROJECT_V14 = Paths.get("src/test/resources/parser/Project (3-Level, v14).knxproj");
    private static final Path KNX_PROJECT_FREELEVEL_V14 = Paths.get("src/test/resources/parser/Project (Free-Level, v14).knxproj");
    private static final Path GOOD_EMPTY_PROJECT = Paths.get("src/test/resources/parser/Empty Project (No Group Addresses).knxproj");
    private static final Path CORRUPTED_FILE = Paths.get("src/test/resources/parser/Corrupted Project (Incomplete).knxproj");
    private static final Path CORRUPTED_NO_PROJECT_ID = Paths.get("src/test/resources/parser/Corrupted Project (No Project Id).knxproj");
    private static final Path CORRUPTED_NO_PROJECTINFORMATION_NAME = Paths.get("src/test/resources/parser/Corrupted Project (No ProjectInformation Name).knxproj");
    private static final Path CORRUPTED_NO_PROJECTINFORMATION_GROUPADDRESS_STYLE = Paths.get("src/test/resources/parser/Corrupted Project (No ProjectInformation GroupAddressStyle).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_ID = Paths.get("src/test/resources/parser/Corrupted Project (No GroupAddress Id).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_ADDRESS = Paths.get("src/test/resources/parser/Corrupted Project (No GroupAddress Address).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_NAME = Paths.get("src/test/resources/parser/Corrupted Project (No GroupAddress Name).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_ID = Paths.get("src/test/resources/parser/Corrupted Project (No GroupRange Id).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_RANGE_START = Paths.get("src/test/resources/parser/Corrupted Project (No GroupRange RangeStart).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_RANGE_END = Paths.get("src/test/resources/parser/Corrupted Project (No GroupRange RangeEnd).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_NAME = Paths.get("src/test/resources/parser/Corrupted Project (No GroupRange Name).knxproj");
    private static final Path CORRUPTED_NO_GROUPRANGE_GROUPADDRESS_ID = Paths.get("src/test/resources/parser/Corrupted Project (No GroupRange GroupAddress Id).knxproj");

    /**
     * Tests if {@link XmlProject} (3-Level) has been parsed correctly
     */
    @Test
    @DisplayName("(Good) Test KNX Project with 3-Level group addresses")
    public void testThreeLevelProjectV14() {
        final var project = KnxprojParser.parse(KNX_PROJECT_V14);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0501");
        assertThat(project.getName()).isEqualTo("Project (3-Level)");
        assertThat(project.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.THREE_LEVEL);
        assertThat(project.getGroupAddresses()).hasSize(189);
        assertThat(project.getGroupRanges()).hasSize(18);

        // ---------------------
        // Range Group Check
        // ---------------------
        final var xmlGroupRanges = project.getMainGroups();
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
        final var subGroup2_0 = mainGroup2.getChildGroupRanges().get(1); // on second index (group addresses in knxproj file is not ordered)
        assertThat(subGroup2_0.getChildGroupRanges()).isEmpty();
        assertThat(subGroup2_0.getGroupAddresses()).hasSize(11);
    }

    /**
     * Tests if {@link XmlProject} (Free-Level) has been parsed correctly
     */
    @Test
    @DisplayName("(Good) Test KNX Project with Free-Level group addresses")
    public void testFreeLevelProjectV14() {
        final var project = KnxprojParser.parse(KNX_PROJECT_FREELEVEL_V14);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-06EF");
        assertThat(project.getName()).isEqualTo("Project (Free-Level)");
        assertThat(project.getGroupAddressStyle()).isSameAs(XmlGroupAddressStyle.FREE_LEVEL);
        assertThat(project.getGroupAddresses()).hasSize(7);
        assertThat(project.getGroupRanges()).hasSize(2);
        assertThat(project.getGroupRangeById("P-06EF-0_GR-3").getGroupAddresses()).hasSize(3);
        assertThat(project.getGroupRangeById("P-06EF-0_GR-4").getGroupAddresses()).hasSize(4);
    }

    /**
     * Tests if {@link XmlGroupAddress} have been parsed correctly
     */
    @Test
    @DisplayName("Test group address datapoint types")
    public void testDataPoints() {
        final var groupAddresses = KnxprojParser.parse(KNX_PROJECT_V14).getGroupAddresses();

        // assert DPT-x group address
        assertGroupAddress(groupAddresses, "P-0501-0_GA-117", GroupAddress.of(0, 0, 10), "Sub Group - DPT 1 (0x00)", "DPT-1");
        assertGroupAddress(groupAddresses, "P-0501-0_GA-128", GroupAddress.of(0, 3, 10), "Sub Group - DPT 12 (0x00 00 00 00)", "DPT-12");

//        // assert DPST-x-y group address TODO: Define DPST in XML Project File too
//        assertGroupAddress(groupAddresses, "P-0501-0_GA-133", GroupAddress.of(1, 0, 10), "Sub Group - DPST 1.001 ", "DPST-1-1");
//        assertGroupAddress(groupAddresses, "P-0501-0_GA-143", GroupAddress.of(1, 2, 20), "Sub Group - DPST 11.001", "DPST-11-1");
//
//        // assert group address without DPT TODO: Define No DPT in XML Project File too
//        assertGroupAddress(groupAddresses, "P-0501-0_GA-149", GroupAddress.of(2, 0, 0), "Sub Group - No DPT 1-byte", null);
//        assertGroupAddress(groupAddresses, "P-0501-0_GA-188", GroupAddress.of(2, 3, 0), "Sub Group - No DPT 4-bytes", null);
    }

    /**
     * Tests if flags of {@link XmlGroupAddress} have been parsed correctly
     */
    @Test
    @DisplayName("Test group address flags")
    public void testGroupAddressFlags() {
        final var groupAddresses = KnxprojParser.parse(KNX_PROJECT_V14).getGroupAddresses();

        // No Flags
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-150", false, false, false, false, false);
        // Communication only
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-151", true, false, false, false, false);
        // Read only
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-152", false, true, false, false, false);
        // Write only
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-153", false, false, true, false, false);
        // Transmit only
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-154", false, false, false, true, false);
        // Update only
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-155", false, false, false, false, true);
        // Communication + Read
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-156", true, true, false, false, false);
        // Communication + Write
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-157", true, false, true, false, false);
        // Communication + Read + Write
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-158", true, true, true, false, false);
        // All flags
        assertGroupAddressFlags(groupAddresses, "P-0501-0_GA-159", true, true, true, true, true);
    }

    @Test
    @DisplayName("(Good) Test KNX project without any group addresses")
    public void testEmptyProject() {
        final var project = KnxprojParser.parse(GOOD_EMPTY_PROJECT);

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
        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_PROJECT_ID))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <Project @Id /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_PROJECTINFORMATION_NAME))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <ProjectInformation @Name /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_PROJECTINFORMATION_GROUPADDRESS_STYLE))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <ProjectInformation @GroupAddressStyle /> not found.");

        /*
         * Group Range Negative Tests
         */
        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPRANGE_ID))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupRange @Id /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPRANGE_RANGE_START))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupRange @RangeStart /> not found for: P-0700-0_GR-1");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPRANGE_RANGE_END))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupRange @RangeEnd /> not found for: P-0700-0_GR-1");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPRANGE_NAME))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupRange @Name /> not found for: P-0700-0_GR-1");

        /*
         * Group Address Negative Tests
         */
        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPADDRESS_ID))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupAddress @Id /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPADDRESS_NAME))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupAddress @Name /> not found for: P-0700-0_GA-1");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPADDRESS_ADDRESS))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupAddress @Address /> not found for: P-0700-0_GA-1");
    }

    /**
     * Asserts a corrupted KNX project (incomplete content)
     */
    @Test
    @DisplayName("(Corrupted) Test KNX project with incomplete content")
    public void testCorruptedProjectIncomplete() {
        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_FILE))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessageStartingWith("Something went wrong during parsing the zip file:");
    }

    /**
     * Test constructor of {@link KnxprojParser}
     */
    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(KnxprojParser.class);
    }

    private void assertGroupAddress(final Collection<XmlGroupAddress> groupAddresses, final String id, final GroupAddress address, final String name, final String datapointType) {
        final var groupAddress = groupAddresses.stream().filter(xga -> id.equals(xga.getId())).findFirst().get();
        assertThat(groupAddress.getId()).isEqualTo(id);
        assertThat(groupAddress.getAddress()).isEqualTo(address.getAddress());
        assertThat(groupAddress.getName()).isEqualTo(name);
        assertThat(groupAddress.getDataPointType()).isEqualTo(datapointType);
    }

    private void assertGroupAddressFlags(final Collection<XmlGroupAddress> groupAddresses, final String id, final boolean communication, final boolean read, final boolean write, final boolean transmit, final boolean update) {
        final var groupAddress = groupAddresses.stream().filter(xga -> id.equals(xga.getId())).findFirst().get();
        assertThat(groupAddress.getCommunicationFlag()).isEqualTo(communication ? "Enabled" : null);
        assertThat(groupAddress.getReadFlag()).isEqualTo(read ? "Enabled" : null);
        assertThat(groupAddress.getWriteFlag()).isEqualTo(write ? "Enabled" : null);
        assertThat(groupAddress.getTransmitFlag()).isEqualTo(transmit ? "Enabled" : null);
        assertThat(groupAddress.getUpdateFlag()).isEqualTo(update ? "Enabled" : null);
    }
}
