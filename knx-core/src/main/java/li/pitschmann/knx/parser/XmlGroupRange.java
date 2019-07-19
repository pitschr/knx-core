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
import com.google.common.collect.Lists;

import java.util.List;

/**
 * XML DTO holding KNX Group Range data which were taken from '*.knxproj' file
 *
 * <pre>{@code
 *   <GroupRange Id="P-0501-0_GR-48" RangeStart="1" RangeEnd="255" Name="Middle Group - DPT (1-byte)" Puid="173">
 *     ... <GroupAddress /> (two level) ...
 *     - or -
 *     ... <GroupRange /> (three level) ...
 *   </GroupRange>
 * }</pre>
 *
 * @author pitschr
 */
public final class XmlGroupRange {
    private String id;
    private int level;
    private int rangeStart;
    private int rangeEnd;
    private String name;
    private List<XmlGroupRange> childGroupRanges = Lists.newLinkedList();
    private List<XmlGroupAddress> groupAddresses = Lists.newLinkedList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<XmlGroupRange> getChildGroupRanges() {
        return childGroupRanges;
    }

    public void setChildGroupRanges(List<XmlGroupRange> childGroupRanges) {
        this.childGroupRanges = childGroupRanges;
    }

    public List<XmlGroupAddress> getGroupAddresses() {
        return groupAddresses;
    }

    public void setGroupAddresses(List<XmlGroupAddress> groupAddresses) {
        this.groupAddresses = groupAddresses;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("level", level)
                .add("rangeStart", rangeStart)
                .add("rangeEnd", rangeEnd)
                .add("name", name)
                .add("childGroupRanges", childGroupRanges)
                .add("groupAddresses", groupAddresses)
                .toString();
        // @formatter:on
    }
}
