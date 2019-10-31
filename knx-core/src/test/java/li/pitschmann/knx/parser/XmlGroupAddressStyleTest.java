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

import li.pitschmann.knx.link.body.address.GroupAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests {@link XmlGroupAddressStyle}
 */
public class XmlGroupAddressStyleTest {

    @Test
    @DisplayName("Test code and friendly names")
    public void testCodeAndFriendlyName() {
        final var freeLevel = XmlGroupAddressStyle.FREE_LEVEL;
        assertThat(freeLevel.getCode()).isEqualTo("Free");
        assertThat(freeLevel.getFriendlyName()).isEqualTo("Free Level");

        final var twoLevel = XmlGroupAddressStyle.TWO_LEVEL;
        assertThat(twoLevel.getCode()).isEqualTo("TwoLevel");
        assertThat(twoLevel.getFriendlyName()).isEqualTo("2-Level");

        final var threeLevel = XmlGroupAddressStyle.THREE_LEVEL;
        assertThat(threeLevel.getCode()).isEqualTo("ThreeLevel");
        assertThat(threeLevel.getFriendlyName()).isEqualTo("3-Level");
    }

    @Test
    @DisplayName("Test #parse(String)")
    public void testParse() {
        // valid cases
        assertThat(XmlGroupAddressStyle.parse("Free")).isSameAs(XmlGroupAddressStyle.FREE_LEVEL);
        assertThat(XmlGroupAddressStyle.parse("TwoLevel")).isSameAs(XmlGroupAddressStyle.TWO_LEVEL);
        assertThat(XmlGroupAddressStyle.parse("ThreeLevel")).isSameAs(XmlGroupAddressStyle.THREE_LEVEL);

        // invalid cases
        assertThatThrownBy(() -> XmlGroupAddressStyle.parse(null)).isInstanceOf(KnxprojParserException.class);
        assertThatThrownBy(() -> XmlGroupAddressStyle.parse("foo")).isInstanceOf(KnxprojParserException.class);
    }

    @Test
    @DisplayName("Test #toString(GroupAddress)")
    public void testToStringWithGroupAddress() {
        final var groupAddress = GroupAddress.of(57133);

        assertThat(XmlGroupAddressStyle.FREE_LEVEL.toString(groupAddress)).isEqualTo("57133");
        assertThat(XmlGroupAddressStyle.TWO_LEVEL.toString(groupAddress)).isEqualTo("27/1837");
        assertThat(XmlGroupAddressStyle.THREE_LEVEL.toString(groupAddress)).isEqualTo("27/7/45");
    }

    @Test
    @DisplayName("Test #toString()")
    public void testToString() {
        assertThat(XmlGroupAddressStyle.THREE_LEVEL).hasToString(
                "XmlGroupAddressStyle{name=THREE_LEVEL, code=ThreeLevel, friendlyName=3-Level}"
        );
    }

}
