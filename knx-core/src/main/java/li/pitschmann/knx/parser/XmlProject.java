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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import li.pitschmann.knx.link.body.address.GroupAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
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
    private String groupAddressStyle;
    /**
     * Map of KNX Group Ranges taken from '*.knxproj' file. Key is by Id (e.g. P-06EF-0_GR-4)
     * <pre>{@code
     * <GroupRanges>
     *   <GroupRange />
     *   ...
     * </GroupRanges>
     * }</pre>
     */
    private Map<String, XmlGroupRange> groupRangeMap;
    /**
     * Map of KNX Group Addresses by Key Id (e.g. P-06EF-0_GA-3) , taken from '*.knxproj' file
     * <p/>
     * {@code <GroupAddresses Id="..." />}
     */
    private Map<String, XmlGroupAddress> groupAddressMap;
    /**
     * Map of KNX Group Addresses by Key Address (e.g. 1025), taken from '*.knxproj' file.
     * <p/>
     * {@code <GroupAddresses Address="..." />}
     */
    private Map<Integer, XmlGroupAddress> groupAddressMapByGA;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupAddressStyle() {
        return groupAddressStyle;
    }

    public void setGroupAddressStyle(String groupAddressStyle) {
        this.groupAddressStyle = groupAddressStyle;
    }

    /**
     * Sets the Group Address map. It will internally create two maps whereas one key is the Id taken from '*.knxproj'
     * file for further linking, and the another key is the group address that is used by.
     *
     * @param groupAddressMap
     */
    public void setGroupAddressMap(final @Nonnull Map<String, XmlGroupAddress> groupAddressMap) {
        // 1st map whereas key is the Id
        this.groupAddressMap = ImmutableMap.copyOf(groupAddressMap);
        // 2nd map whereas key is the group address itself
        this.groupAddressMapByGA = this.groupAddressMap.values().stream()
                .collect(
                        Collectors.toUnmodifiableMap(
                                xga -> Integer.parseInt(xga.getAddress()), // parses address to integer
                                Function.identity() // the xml group address itself
                        )
                );
    }

    /**
     * Sets the Group Range map
     *
     * @param groupRangeMap
     */
    public void setGroupRangeMap(final @Nonnull Map<String, XmlGroupRange> groupRangeMap) {
        this.groupRangeMap = ImmutableMap.copyOf(groupRangeMap);
    }

    /**
     * Returns an unmodifiable collection of {@link XmlGroupRange}
     *
     * @return unmodifiable collection of {@link XmlGroupRange}
     */
    @Nonnull
    public Collection<XmlGroupRange> getGroupRanges() {
        return Collections.unmodifiableCollection(this.groupRangeMap.values());
    }

    /**
     * Returns an unmodifiable collection of {@link XmlGroupAddress}
     *
     * @return unmodifiable collection of {@link XmlGroupAddress}
     */
    @Nonnull
    public Collection<XmlGroupAddress> getGroupAddresses() {
        return Collections.unmodifiableCollection(this.groupAddressMap.values());
    }

    /**
     * Returns the {@link XmlGroupAddress} for given {@link GroupAddress}
     *
     * @param groupAddress
     * @return the {@link XmlGroupAddress}, or {@code null} if not found
     */
    @Nullable
    public XmlGroupAddress getGroupAddress(final @Nonnull GroupAddress groupAddress) {
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
        return this.groupAddressMapByGA.get(address);
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
     * Returns collection of {@link XmlGroupRange} for all main groups
     *
     * @return collection of {@link XmlGroupRange}, or empty list if not found
     */
    @Nonnull
    public Collection<XmlGroupRange> getMainGroups() {
        return groupRangeMap.values().stream().filter(xgr -> xgr.getParentId() == null).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns collection of {@link XmlGroupRange} for given {@code main} group
     *
     * @param main
     * @return collection of {@link XmlGroupRange}, or {@link IllegalArgumentException} list if not found
     */
    @Nonnull
    public XmlGroupRange getMainGroup(final int main) {
        Preconditions.checkArgument(groupRangeMap != null && !groupRangeMap.isEmpty(), "No main groups available");

        // find the group range with the proper range start (see GroupAddresses)
        // special rule for main group 0/-/- it is not allowed to have 0/0/0 and the first group address is 0/0/1
        int startRange = main == 0 ? 1 : Integer.valueOf(GroupAddress.of(main, 0).getAddress());
        log.debug("Looking for start range '{}' of group {}/-/- in: {}", startRange, main, groupRangeMap);

        XmlGroupRange xmlGroupRange = null;
        for (final var groupRange : groupRangeMap.values()) {
            if (groupRange.getRangeStart() == startRange) {
                // found
                xmlGroupRange = groupRange;
                break;
            }
        }

        // not found?
        if (xmlGroupRange == null) {
            log.warn("Main group '{}' not found in: {}", main, groupRangeMap.values());
            throw new IllegalArgumentException("Could not find main group '" + main + "'!");
        }
        // otherwise found
        else {
            log.debug("Main group '{}' found: {}", main, xmlGroupRange);
            return xmlGroupRange;
        }
    }

    public XmlGroupRange getMiddleGroup(final int main, final int middle) {
        final var mainGroup = getMainGroup(main);

        // find the group range with the proper range start (see GroupAddresses)
        // special rule for main group 0/-/- and middle group 0/0/- it is not allowed to have 0/0/0 and the first group address is 0/0/1
        int startRange = main == 0 && middle == 0 ? 1 : Integer.valueOf(GroupAddress.of(main, middle, 0).getAddress());
        log.debug("Looking for start range '{}' of group {}/{}/- in: {}", startRange, main, middle, mainGroup);

        XmlGroupRange xmlGroupRange = null;
        for (final var groupRange : mainGroup.getChildGroupRanges()) {
            if (groupRange.getRangeStart() == startRange) {
                // found
                xmlGroupRange = groupRange;
                break;
            }
        }

        // not found?
        if (xmlGroupRange == null) {
            log.warn("Main group '{}' not found in: {}", main, groupRangeMap.values());
            throw new IllegalArgumentException("Could not find main group '" + main + "'!");
        }
        // otherwise found
        else {
            log.debug("Main group '{}' found: {}", main, xmlGroupRange);
            return xmlGroupRange;
        }
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("groupAddressStyle", this.groupAddressStyle)
                .add("groupAddressMap", this.groupAddressMap)
                .add("groupRangeMap", this.groupRangeMap)
                .toString();
        // @formatter:on
    }
}
