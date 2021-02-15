/*
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

package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.test.TestHelpers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test cases for {@link Json} class
 *
 * @author PITSCHR
 */
public class JsonTest {

    @Test
    @DisplayName("Constructor not instantiable")
    public void testConstructorNonInstantiable() {
        TestHelpers.assertThatNotInstantiable(Json.class);
    }

    @Test
    @DisplayName("Test escaping several strings")
    public void testEscapeJson() {

        // empty
        assertThat(Json.escapeJson(null)).isEmpty();
        assertThat(Json.escapeJson("")).isEmpty();

        // escape characters
        assertThat(Json.escapeJson(Character.toString('\"'))).isEqualTo("\\\"");
        assertThat(Json.escapeJson(Character.toString('\\'))).isEqualTo("\\\\");
        assertThat(Json.escapeJson(Character.toString('/'))).isEqualTo("\\/");

        // control characters
        assertThat(Json.escapeJson(Character.toString('\b'))).isEqualTo("\\b");
        assertThat(Json.escapeJson(Character.toString('\f'))).isEqualTo("\\f");
        assertThat(Json.escapeJson(Character.toString('\n'))).isEqualTo("\\n");
        assertThat(Json.escapeJson(Character.toString('\r'))).isEqualTo("\\r");
        assertThat(Json.escapeJson(Character.toString('\t'))).isEqualTo("\\t");

        // unicode characters
        assertThat(Json.escapeJson("\u0040")).isEqualTo("@");
        assertThat(Json.escapeJson("\u00B2")).isEqualTo("²");
        assertThat(Json.escapeJson("\u000F")).isEqualTo("\\u000F");
        assertThat(Json.escapeJson("\u0081")).isEqualTo("\\u0081");

        // normal texts
        assertThat(Json.escapeJson("{}[]():;²")).isEqualTo("{}[]():;²");
        assertThat(Json.escapeJson("Hello World")).isEqualTo("Hello World");
        assertThat(Json.escapeJson("Hällo Wörld")).isEqualTo("Hällo Wörld");
    }

    @Test
    @DisplayName("Test escaping a Java Object")
    public void testToJson() throws UnknownHostException {
        // empty
        assertThat(Json.toJson(null)).isEqualTo("null");

        // boolean
        assertThat(Json.toJson(true)).isEqualTo("true");
        assertThat(Json.toJson(Boolean.TRUE)).isEqualTo("true");

        // char
        assertThat(Json.toJson('a')).isEqualTo("\"a\"");
        assertThat(Json.toJson(Character.forDigit(11, 16))).isEqualTo("\"b\"");

        // numbers
        assertThat(Json.toJson((short) 132)).isEqualTo("132");
        assertThat(Json.toJson((short) -45)).isEqualTo("-45");
        assertThat(Json.toJson(4711)).isEqualTo("4711");
        assertThat(Json.toJson(-4712)).isEqualTo("-4712");
        assertThat(Json.toJson(47111317233L)).isEqualTo("47111317233");
        assertThat(Json.toJson(-89332323332L)).isEqualTo("-89332323332");
        assertThat(Json.toJson(3.14f)).isEqualTo("3.14");
        assertThat(Json.toJson(-14.3f)).isEqualTo("-14.3");
        assertThat(Json.toJson(7.1300066d)).isEqualTo("7.1300066");
        assertThat(Json.toJson(-6.4500088d)).isEqualTo("-6.4500088");
        assertThat(Json.toJson(BigInteger.valueOf(3456789))).isEqualTo("3456789");
        assertThat(Json.toJson(BigDecimal.valueOf(567890123, 3))).isEqualTo("567890.123");

        // objects
        assertThat(Json.toJson("Hello Earth")).isEqualTo("\"Hello Earth\"");
        assertThat(Json.toJson("[foo](bar)")).isEqualTo("\"[foo](bar)\"");
        assertThat(Json.toJson(LocalDate.of(2019, 11, 15))).isEqualTo("\"2019-11-15\"");
        assertThat(Json.toJson(InetAddress.getByAddress("my-host", new byte[]{1, 2, 3, 4}))).isEqualTo("\"my-host\\/1.2.3.4\"");

        // arrays
        assertThat(Json.toJson(new Integer[]{4, 1134, 13, 112})).isEqualTo("[4,1134,13,112]");
        assertThat(Json.toJson(new Object[]{"foobar", 456L, Locale.US})).isEqualTo("[\"foobar\",456,\"en_US\"]");

        // unsupported
        assertThatThrownBy(() -> Json.toJson(new boolean[]{true, false})).isInstanceOf(UnsupportedOperationException.class);
    }
}
