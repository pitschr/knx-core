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
import li.pitschmann.knx.link.body.address.GroupAddress;

import java.util.List;
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
    /**
     * <Project @Id />
     */
    private String id;
    /**
     * <ProjectInformation @Name />
     */
    private String name;
    /**
     * <ProjectInformation @GroupAddressStyle />
     */
    private String groupAddressStyle;
    /**
     * List of KNX Group Addresses, taken from '*.knxproj' file
     */
    private List<XmlGroupAddress> groupAddresses;

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

    public List<XmlGroupAddress> getGroupAddresses() {
        return groupAddresses;
    }

    public void setGroupAddresses(List<XmlGroupAddress> groupAddresses) {
        this.groupAddresses = groupAddresses;
    }

    /**
     * Returns the {@link XmlGroupAddress} for given {@link GroupAddress}
     *
     * @param groupAddress
     * @return An instance of {@link Optional} for group address
     */
    public Optional<XmlGroupAddress> getGroupAddress(final GroupAddress groupAddress) {
        return getGroupAddresses().stream().filter(x -> x.getAddress().equals(groupAddress.getAddress())).findFirst();
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("name", this.name)
                .add("groupAddressStyle", this.groupAddressStyle)
                .add("groupAddresses", this.groupAddresses)
                .toString();
        // @formatter:on
    }
}
