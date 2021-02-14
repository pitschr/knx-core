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
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxProjectParserException;
import li.pitschmann.knx.core.header.ServiceType;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * KNX Group Address Style used in *.knxproj file
 * <p>
 * Currently following level styles are supported
 * <ul>
 * <li>Free Level (example: 57133)</li>
 * <li>2-Level (example: 27/1837)</li>
 * <li>3-Level (example: 27/7/45)</li>
 * </ul>
 */
public enum XmlGroupAddressStyle {
    /**
     * Free-Level Project
     */
    FREE_LEVEL("Free", "Free Level", GroupAddress::getAddress),
    /**
     * 2-Level Project
     */
    TWO_LEVEL("TwoLevel", "2-Level", GroupAddress::getAddressLevel2),
    /**
     * 3-Level Project
     */
    THREE_LEVEL("ThreeLevel", "3-Level", GroupAddress::getAddressLevel3);

    private final String code;
    private final String friendlyName;
    private final Function<GroupAddress, String> resolver;

    /**
     * Constructor for XML Group Address Style
     */
    XmlGroupAddressStyle(final String code, final String friendlyName, final Function<GroupAddress, String> resolver) {
        this.code = code;
        this.friendlyName = friendlyName;
        this.resolver = resolver;
    }

    /**
     * Returns the {@link XmlGroupAddressStyle} for given xmlMappingValue
     * <p>
     * Note: {@link #valueOf(String)} uses a different look-up approach
     *
     * @param id identifier of XML Group Address Style
     * @return instance of {@link ServiceType} otherwise {@link KnxEnumNotFoundException}
     * if the given {@code code} is not registered one
     */
    public static XmlGroupAddressStyle parse(final @Nullable String id) {
        return Arrays.stream(values()).filter(x -> x.code.equals(id)).findFirst()
                .orElseThrow(() -> new KnxProjectParserException("No group address style found for: " + id));
    }

    /**
     * Returns the code of group address style
     *
     * @return code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Returns the friendly name of group address style
     *
     * @return human-friendly name
     */
    public String getFriendlyName() {
        return this.friendlyName;
    }

    /**
     * Resolves the {@link GroupAddress} into the right format of String
     * representation based on current enumeration instance
     *
     * @param groupAddress group address to be converted
     * @return string representation of {@link GroupAddress}
     */
    public String toString(final GroupAddress groupAddress) {
        return this.resolver.apply(Objects.requireNonNull(groupAddress));
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("name", this.name())
                .add("code", code)
                .add("friendlyName", this.friendlyName)
                .toString();
        // @formatter:on
    }
}
