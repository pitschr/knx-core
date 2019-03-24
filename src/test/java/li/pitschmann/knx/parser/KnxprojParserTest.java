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

import li.pitschmann.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link KnxprojParser} class to parse KNX Project files
 */
public class KnxprojParserTest {
    private static final Path KNX_PROJECT_V14 = Paths.get("src/test/resources/parser/Project (3-Level, v14).knxproj");
    private static final Path GOOD_EMPTY_PROJECT = Paths.get("src/test/resources/parser/Empty Project (No Group Addresses).knxproj");
    private static final Path CORRUPTED_FILE = Paths.get("src/test/resources/parser/Corrupted Project (Incomplete).knxproj");
    private static final Path CORRUPTED_NO_PROJECT_ID = Paths.get("src/test/resources/parser/Corrupted Project (No Project Id).knxproj");
    private static final Path CORRUPTED_NO_PROJECTINFORMATION_NAME = Paths.get("src/test/resources/parser/Corrupted Project (No ProjectInformation Name).knxproj");
    private static final Path CORRUPTED_NO_PROJECTINFORMATION_GROUPADDRESS_STYLE = Paths.get("src/test/resources/parser/Corrupted Project (No ProjectInformation GroupAddressStyle).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_ID = Paths.get("src/test/resources/parser/Corrupted Project (No GroupAddress Id).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_ADDRESS = Paths.get("src/test/resources/parser/Corrupted Project (No GroupAddress Address).knxproj");
    private static final Path CORRUPTED_NO_GROUPADDRESS_NAME = Paths.get("src/test/resources/parser/Corrupted Project (No GroupAddress Name).knxproj");


    @Test
    @DisplayName("(Good) Test KNX Project with 3-Level group addresses")
    public void testGoodProjectV14() {
        final var project = KnxprojParser.parse(KNX_PROJECT_V14);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0501");
        assertThat(project.getName()).isEqualTo("Project (3-Level)");
        assertThat(project.getGroupAddressStyle()).isEqualTo("ThreeLevel");

        final var groupAddresses = project.getGroupAddresses();
        assertThat(groupAddresses).hasSize(73);

        // assert DPT-x group address
        // address = 0/0/10 => 0000 0000 0000 1010 => 10
        assertGroupAddress(groupAddresses, 0, "P-0501-0_GA-117", 10, "Sub Group - DPT 1", "DPT-1");
        // address = 0/3/10 => 0000 0011 0000 1010 => 778
        assertGroupAddress(groupAddresses, 11, "P-0501-0_GA-128", 778, "Sub Group - DPT 12", "DPT-12");

        // assert DPST-x-y group address
        // address = 1/0/10 => 0000 1000 0000 1010 => 2058
        assertGroupAddress(groupAddresses, 16, "P-0501-0_GA-133", 2058, "Sub Group - DPST 1.001", "DPST-1-1");
        // address = 1/2/20 => 0000 1010 0001 0100 => 2570
        assertGroupAddress(groupAddresses, 26, "P-0501-0_GA-143", 2580, "Sub Group - DPST 11.001", "DPST-11-1");

        // assert group address without DPT
        // address = 2/0/0 => 0001 0000 0000 0000 => 4096
        assertGroupAddress(groupAddresses, 32, "P-0501-0_GA-149", 4096, "Sub Group - No DPT 1-byte", null);
        // address = 2/3/0 => 0001 0011 0000 0000 => 4864
        assertGroupAddress(groupAddresses, 35, "P-0501-0_GA-188", 4864, "Sub Group - No DPT 4-bytes", null);
    }

    @Test
    @DisplayName("(Good) Test KNX project without any group addresses")
    public void testEmptyProject() {
        final var project = KnxprojParser.parse(GOOD_EMPTY_PROJECT);

        assertThat(project).isNotNull();
        assertThat(project.getId()).isEqualTo("P-0700");
        assertThat(project.getName()).isEqualTo("Project (Empty)");
        assertThat(project.getGroupAddressStyle()).isEqualTo("Free");
        assertThat(project.getGroupAddresses()).isEmpty();
    }

    /**
     * Asserts a corrupted KNX project without mandatory attributes
     * <ul>
     * <li>Missing {@code @Id} on {@code <Project />}</li>
     * <li>Missing {@code @Name} on {@code <ProjectInformation />}</li>
     * <li>Missing {@code @GroupAddressStyle} on {@code <ProjectInformation />}</li>
     * <li>Missing {@code @Id} on {@code <GroupAddress />}</li>
     * <li>Missing {@code @Name} on {@code <GroupAddress />}</li>
     * <li>Missing {@code @Address} on {@code <GroupAddress />}</li>
     * </ul>
     */
    @Test
    @DisplayName("(Corrupted) Test KNX project without mandatory attributes")
    public void testCorruptedProjectMissingAttributes() {
        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_PROJECT_ID))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <Project @Id /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_PROJECTINFORMATION_NAME))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <ProjectInformation @Name /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_PROJECTINFORMATION_GROUPADDRESS_STYLE))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <ProjectInformation @GroupAddressStyle /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPADDRESS_ID))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupAddress @Id /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPADDRESS_NAME))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupAddress @Name /> not found.");

        assertThatThrownBy(() -> KnxprojParser.parse(CORRUPTED_NO_GROUPADDRESS_ADDRESS))
                .isInstanceOf(KnxprojParserException.class)
                .hasMessage("Attribute <GroupAddress @Address /> not found.");
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

    private void assertGroupAddress(final List<XmlGroupAddress> groupAddresses, final int index, final String id, final int address, final String name, final String datapointType) {
        final var groupAddress = groupAddresses.get(index);
        assertThat(groupAddress.getId()).isEqualTo(id);
        assertThat(groupAddress.getAddress()).isEqualTo(address);
        assertThat(groupAddress.getName()).isEqualTo(name);
        assertThat(groupAddress.getDatapointType()).isEqualTo(datapointType);
    }
}
