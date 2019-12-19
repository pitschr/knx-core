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

package li.pitschmann.knx.core.knxproj;

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.knxproj.parser.Parser;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * XML DTO holding KNX Project Information which were taken from '*.knxproj' file
 *
 * <pre>{@code
 *   <KNX xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" CreatedBy="ETS5" ToolVersion="5.6.1190.34059" xmlns="http://knx.org/xml/project/14">
 *     <Project Id="P-0501">
 *      <ProjectInformation Name="Project (3-Level)" GroupAddressStyle="ThreeLevel" />
 *     </Project>
 *   </KNX>
 * }</pre>
 *
 * @author pitschr
 */
public final class XmlProject {
    public static final Logger log = LoggerFactory.getLogger(XmlProject.class);
    /**
     * ID of project
     * <p>
     * <Project @Id />
     */
    private String id;
    /**
     * Name of project
     * <p>
     * <ProjectInformation @Name />
     */
    private String name;
    /**
     * Style of Group Address (FreeLevel, TwoLevel, ThreeLevel)
     * <p>
     * <ProjectInformation @GroupAddressStyle />
     */
    private XmlGroupAddressStyle groupAddressStyle;
    /**
     * Version of project
     */
    private int version;
    /**
     * Map of KNX Group Ranges taken from '*.knxproj' file. Key is by Id (e.g. P-06EF-0_GR-4)
     * <pre>{@code
     * <GroupRanges>
     *   <GroupRange Id="..." />
     * </GroupRanges>
     * }</pre>
     */
    private Map<String, XmlGroupRange> groupRangeMap = Map.of();
    /**
     * Immutable list of {@link XmlGroupRange}, sorted by {@link XmlGroupRange#getRangeStart()}
     * and {@link XmlGroupRange#getLevel()}
     */
    private List<XmlGroupRange> groupRanges = List.of();
    /**
     * Immutable list of {@link XmlGroupAddress}, sorted by {@link XmlGroupAddress#getAddress()}
     */
    private List<XmlGroupAddress> groupAddresses = List.of();
    /**
     * Map of KNX Group Addresses by Key {@code Id} (e.g. P-06EF-0_GA-3), taken from '*.knxproj' file.
     * <p/>
     * {@code <GroupAddresses Id="..." />}
     */
    private Map<String, XmlGroupAddress> groupAddressMap = Map.of();
    /**
     * Map of KNX Group Addresses by {@code Address} (e.g. 1025), taken from '*.knxproj' file.
     * <p/>
     * {@code <GroupAddresses Address="..." />}
     */
    private Map<Integer, XmlGroupAddress> groupAddressIntMap = Map.of();

    /**
     * Parses the given {@link Path} and return a new {@link XmlProject} instance
     *
     * @param path
     * @return a new instance of {@link XmlProject}
     */
    public static XmlProject of(final Path path) {
        return Parser.asXmlProject(path);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public XmlGroupAddressStyle getGroupAddressStyle() {
        return groupAddressStyle;
    }

    public void setGroupAddressStyle(final XmlGroupAddressStyle groupAddressStyle) {
        this.groupAddressStyle = groupAddressStyle;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Returns an immutable list of {@link XmlGroupRange}
     *
     * @return immutable list of {@link XmlGroupRange}
     */
    public List<XmlGroupRange> getGroupRanges() {
        return groupRanges;
    }

    /**
     * Sets the group ranges by given collection of {@link XmlGroupRange}.
     * Internally it will be converted into a map for faster lookup by id.
     * <p>
     * The group ranges will be sorted by {@link XmlGroupRange#getRangeStart()}.
     * if both are same, then it will be compared against {@link XmlGroupRange#getLevel()}.
     *
     * @param groupRanges
     */
    public void setGroupRanges(final Collection<XmlGroupRange> groupRanges) {
        // immutable list of KNX Group Ranges sorted by range start, and then level
        this.groupRanges = groupRanges.stream()
                .sorted(
                        (o1, o2) -> {
                            if (o1.getRangeStart() == o2.getRangeStart()) {
                                // range start is same, sort by level then
                                return Integer.compare(o1.getLevel(), o2.getLevel());
                            } else {
                                return Integer.compare(o1.getRangeStart(), o2.getRangeStart());
                            }
                        }
                ).collect(Collectors.toUnmodifiableList());

        this.groupRangeMap = this.groupRanges.stream()
                .collect(
                        Collectors.toUnmodifiableMap(
                                XmlGroupRange::getId, // key is the XML GroupRange ID
                                Function.identity() // element itself
                        )
                );
    }

    /**
     * Returns an immutable list of {@link XmlGroupAddress}
     *
     * @return immutable list of {@link XmlGroupAddress}
     */
    public List<XmlGroupAddress> getGroupAddresses() {
        return groupAddresses;
    }

    /**
     * Sets the group addresses by the given collection of {@link XmlGroupAddress}
     * Internally it will create two maps:<br>
     * <ol>
     * <li>one map, whereas key is the Id taken from '*.knxproj'</li>
     * <li>one map, whereas key is the KNX group address (unique as integer)</li>
     * </ol>
     * Using those two maps we can find the group address quickly by id from XML Project file
     * or by the KNX group address.
     *
     * @param groupAddresses
     */
    public void setGroupAddresses(final Collection<XmlGroupAddress> groupAddresses) {
        // immutable list of KNX Group Address sorted by Group Address as Integer
        this.groupAddresses = groupAddresses.stream()
                .sorted(
                        Comparator.comparingInt(x -> Integer.parseInt(x.getAddress()))
                ).collect(Collectors.toUnmodifiableList());

        // 1st map whereas key is the XML GroupAddress Id
        this.groupAddressMap = this.groupAddresses.stream()
                .collect(
                        Collectors.toUnmodifiableMap(
                                XmlGroupAddress::getId, // key is the XML GroupAddress ID
                                Function.identity() // element itself
                        )
                );

        // 2nd map whereas key is the KNX GroupAddress in Integer format
        this.groupAddressIntMap = this.groupAddresses.stream()
                .collect(
                        Collectors.toUnmodifiableMap(
                                x -> Integer.parseInt(x.getAddress()), // key is the KNX group address (as an integer)
                                Function.identity() // element itself
                        )
                );
    }

    /**
     * Returns the {@link XmlGroupAddress} for given {@link GroupAddress}
     *
     * @param groupAddress
     * @return the {@link XmlGroupAddress}, or {@code null} if not found
     */
    @Nullable
    public XmlGroupAddress getGroupAddress(final GroupAddress groupAddress) {
        return getGroupAddress(groupAddress.getAddressAsInt());
    }

    /**
     * Returns the {@link XmlGroupAddress} for given {@code address} that is used
     * by all levels (free-level, two-level and three-level) internally.
     *
     * @param address
     * @return the {@link XmlGroupAddress}, or {@code null} if not found
     */
    @Nullable
    public XmlGroupAddress getGroupAddress(final int address) {
        return this.groupAddressIntMap.get(address);
    }

    /**
     * Returns the {@link XmlGroupRange} by the id defined in '*.knxproj' file
     *
     * @param id id of group range in KNX project file
     * @return An instance of {@link XmlGroupRange}, or {@code null} if not found
     */
    @Nullable
    public XmlGroupRange getGroupRangeById(final String id) {
        return groupRangeMap.get(id);
    }

    /**
     * Returns the {@link XmlGroupAddress} by the id defined in '*.knxproj' file
     *
     * @param id id of group address in KNX project file
     * @return An instance of {@link XmlGroupAddress}, or {@code null} if not found
     */
    @Nullable
    public XmlGroupAddress getGroupAddressById(final String id) {
        return groupAddressMap.get(id);
    }

    /**
     * Returns immutable list of {@link XmlGroupRange} for all main groups
     *
     * @return immutable list of {@link XmlGroupRange}, or empty list if not found
     */
    public List<XmlGroupRange> getMainGroupRanges() {
        return groupRanges.isEmpty()
                ? List.of()
                : groupRanges.stream().filter(xgr -> xgr.getLevel() == 0).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the XML Group Range for given {@code main} group
     *
     * @param main
     * @return an instance of {@link XmlGroupRange}, or {@link IllegalArgumentException} if not found
     */
    public XmlGroupRange getGroupRange(final int main) {
        Preconditions.checkArgument(!groupRanges.isEmpty(), "No main groups available");

        // find the group range with the proper range start (see GroupAddresses)
        // special rule for main group 0/-/- it is not allowed to have 0/0/0 and the first group address is 0/0/1
        int startRange = main == 0 ? 1 : GroupAddress.of(main, 0).getAddressAsInt();
        log.debug("Looking for start range '{}' of group {}/-/- in: {}", startRange, main, groupRanges);

        final var mainGroups = getMainGroupRanges();
        XmlGroupRange xmlGroupRange = null;
        for (final var groupRange : mainGroups) {
            if (groupRange.getRangeStart() == startRange) {
                xmlGroupRange = groupRange;
                break;
            }
        }

        // not found?
        if (xmlGroupRange == null) {
            log.warn("Main group '{}' not found in: {}", main, mainGroups);
            throw new IllegalArgumentException("Could not find main group '" + main + "'!");
        }
        // otherwise found
        else {
            log.debug("Main group '{}' found: {}", main, xmlGroupRange);
            return xmlGroupRange;
        }
    }

    /**
     * Returns the XML Group Range for given {@code main/middle} group
     *
     * @param main
     * @param middle
     * @return an instance of {@link XmlGroupRange}, or {@link IllegalArgumentException} if not found
     */
    public XmlGroupRange getGroupRange(final int main, final int middle) {
        final var mainGroup = getGroupRange(main);

        // find the group range with the proper range start (see GroupAddresses)
        // special rule for main group 0/-/- and middle group 0/0/- it is not allowed to have 0/0/0 and the first group address is 0/0/1
        int startRange = main == 0 && middle == 0 ? 1 : GroupAddress.of(main, middle, 0).getAddressAsInt();
        log.debug("Looking for start range '{}' of group {}/{}/- in: {}", startRange, main, middle, mainGroup);

        final var childGroups = mainGroup.getChildGroupRanges();
        XmlGroupRange xmlGroupRange = null;
        for (final var groupRange : childGroups) {
            if (groupRange.getRangeStart() == startRange) {
                xmlGroupRange = groupRange;
                break;
            }
        }

        // not found?
        if (xmlGroupRange == null) {
            log.warn("Middle group '{}' not found in: {}", main, childGroups);
            throw new IllegalArgumentException("Could not find main group '" + main + "'!");
        }
        // otherwise found
        else {
            log.debug("Middle group '{}' found: {}", main, xmlGroupRange);
            return xmlGroupRange;
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("version", this.version)
                .add("groupAddressStyle", this.groupAddressStyle)
                .add("groupRanges", this.groupRanges)
                .add("groupAddresses", this.groupAddresses)
                .toString();
        // @formatter:on
    }
}
