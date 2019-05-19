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
import com.google.common.collect.ImmutableList;
import li.pitschmann.knx.link.body.address.GroupAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Map of KNX Group Ranges taken from '*.knxproj' file
     *
     * <GroupRanges>
     * <GroupRange />
     * ...
     * </GroupRanges>
     */
    private Map<String, XmlGroupRange> groupRangeMap;
    /**
     * Map of KNX Group Addresses, taken from '*.knxproj' file
     * <p>
     * <GroupAddress />
     */
    private Map<String, XmlGroupAddress> groupAddressMap;

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

    public Collection<XmlGroupAddress> getGroupAddressMap() {
        return groupAddressMap.values();
    }

    public void setGroupAddressMap(Map<String, XmlGroupAddress> groupAddressMap) {
        this.groupAddressMap = groupAddressMap;
    }

    public Map<String, XmlGroupRange> getGroupRangeMap() {
        return groupRangeMap;
    }

    public void setGroupRangeMap(Map<String, XmlGroupRange> groupRangeMap) {
        this.groupRangeMap = groupRangeMap;
    }

    /**
     * Returns an immutable list of {@link XmlGroupAddress}
     *
     * @return immutable list of {@link XmlGroupAddress}
     */
    public List<XmlGroupAddress> getGroupAddresses() {
        return ImmutableList.copyOf(this.groupAddressMap.values());
    }

    /**
     * Returns the {@link XmlGroupAddress} for given {@link GroupAddress}
     *
     * @param groupAddress
     * @return An instance of {@link Optional} for group address
     */
    @Nonnull
    public Optional<XmlGroupAddress> getGroupAddress(final GroupAddress groupAddress) {
        return getGroupAddresses().stream().filter(x -> x.getAddress().equals(groupAddress.getAddress())).findFirst();
    }

    /**
     * Returns the {@link XmlGroupAddress} for given group address {@code id}
     *
     * @param id id of group address in KNX project file
     * @return An instance of {@link Optional} for group address
     */
    @Nonnull
    public Optional<XmlGroupAddress> getGroupAddressById(final String id) {
        return Optional.ofNullable(groupAddressMap == null ? null : groupAddressMap.get(id));
    }

    /**
     * Returns collection of {@link XmlGroupRange} for all main groups
     *
     * @return collection of {@link XmlGroupRange}, or empty list if not found
     */
    @Nonnull
    public Collection<XmlGroupRange> getMainGroups() {
        return Optional.ofNullable(groupRangeMap.values()).orElseGet(() -> Collections.emptyList());
    }

    /**
     * Returns collection of {@link XmlGroupRange} for given {@code main} group
     *
     * @param main
     * @return collection of {@link XmlGroupRange}, or empty list if not found
     */
    @Nonnull
    public Collection<XmlGroupRange> getMiddleGroups(final int main) {
        if (groupRangeMap == null || groupRangeMap.isEmpty()) {
            log.warn("No main groups available");
            return Collections.emptyList();
        }

        // find the group range with the proper range start (see GroupAddresses)
        int startRange = Integer.valueOf(GroupAddress.of(main, 0).getAddress());
        log.debug("Looking for start range '{}' in: {}", startRange, groupRangeMap);

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
            return Collections.emptyList();
        }
        // otherwise found
        else {
            log.debug("Main group '{}' found: {}", main, xmlGroupRange);
            return xmlGroupRange.getChildGroupRanges();
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
