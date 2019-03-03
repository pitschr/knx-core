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
    private int address;
    private String name;
    private String datapointType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatapointType() {
        return datapointType;
    }

    public void setDatapointType(final String datapointType) {
        this.datapointType = datapointType;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("id",id)
                .add("address", address)
                .add("name", name)
                .add("datapointType", datapointType)
                .toString();
        // @formatter:on
    }
}
