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

package li.pitschmann.knx.core.parser;

import li.pitschmann.knx.core.utils.Strings;

/**
 * XML DTO holding KNX Group Address data which were taken from '*.knxproj' file
 *
 * <pre>{@code
 *   <GroupAddress Id="P-0501-0_GA-117" Address="1" Name="Sub Group - DPT 1" Description="1.* (1-bit)" DatapointType="DPT-1" Puid="174" />
 * }</pre>
 *
 * @author pitschr
 */
public final class XmlGroupAddress {
    private String id;
    private String parentId;
    private String address;
    private String name;
    private String description;
    private String dataPointType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataPointType() {
        return dataPointType;
    }

    public void setDataPointType(final String dataPointType) {
        this.dataPointType = dataPointType;
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("id",id)
                .add("parentId", parentId)
                .add("address", address)
                .add("name", name)
                .add("description", description)
                .add("dataPointType", dataPointType)
                .toString();
        // @formatter:on
    }
}
