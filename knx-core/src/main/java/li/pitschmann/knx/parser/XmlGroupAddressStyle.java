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
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.link.header.ServiceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    FREE_LEVEL("Free", GroupAddress::getAddress),
    /**
     * 2-Level Project
     */
    TWO_LEVEL("TwoLevel", GroupAddress::getAddressLevel2),
    /**
     * 3-Level Project
     */
    THREE_LEVEL("ThreeLevel", GroupAddress::getAddressLevel3);

    private final String xmlMappingValue;
    private final Function<GroupAddress, String> resolver;

    /**
     * Constructor for Response/Acknowledge Service Type identifiers
     *
     * @param xmlMappingValue
     * @param resolver
     */
    XmlGroupAddressStyle(final @Nonnull String xmlMappingValue, final @Nonnull Function<GroupAddress, String> resolver) {
        this.xmlMappingValue = xmlMappingValue;
        this.resolver = resolver;
    }

    /**
     * Returns the {@link XmlGroupAddressStyle} for given xmlMappingValue
     * <p/>
     * Note: {@link #valueOf(String)} uses a different look-up approach
     *
     * @param id
     * @return instance of {@link ServiceType} otherwise {@link KnxEnumNotFoundException}
     * if the given {@code code} is not registered one
     */
    @Nonnull
    public static XmlGroupAddressStyle parse(final @Nullable String id) {
        return Arrays.stream(values()).filter(x -> x.xmlMappingValue.equals(id)).findFirst()
                .orElseThrow(() -> new KnxprojParserException("No group address style found for: " + id));
    }

    /**
     * Resolves the {@link GroupAddress} into the right format of String
     * representation based on current enumeration instance
     *
     * @param groupAddress group address to be converted
     * @return string representation of {@link GroupAddress}
     */
    @Nonnull
    public String toString(final @Nonnull GroupAddress groupAddress) {
        return this.resolver.apply(Objects.requireNonNull(groupAddress));
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("name", this.name())
                .add("xmlMappingValue", this.xmlMappingValue)
                .toString();
        // @formatter:on
    }
}
