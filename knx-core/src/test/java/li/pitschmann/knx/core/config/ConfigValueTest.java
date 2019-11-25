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

package li.pitschmann.knx.core.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ConfigValue}
 */
public final class ConfigValueTest {

    @Test
    @DisplayName("Test config constant")
    public void testConfigValue() {
        final Function<String, Integer> converter = Integer::parseInt;
        final Supplier<Integer> defaultValue = () -> 13;
        final Predicate<Integer> predicate = (value) -> value > 3 && value < 10;
        final var ConfigValue = new ConfigValue<>("aAa", Integer.class, converter, defaultValue, predicate);

        assertThat(ConfigValue.getKey()).isEqualTo("aaa"); // lower cased!
        assertThat(ConfigValue.getClassType()).isEqualTo(Integer.class);
        assertThat(ConfigValue.getConverter()).isSameAs(converter);
        assertThat(ConfigValue.getDefaultValue()).isEqualTo(13);
        assertThat(ConfigValue.getPredicate()).isEqualTo(predicate);

        assertThat(ConfigValue.isValid(null)).isFalse();
        assertThat(ConfigValue.isValid(0)).isFalse();
        assertThat(ConfigValue.isValid(4)).isTrue();
        assertThat(ConfigValue.isValid(8)).isTrue();
        assertThat(ConfigValue.isValid(11)).isFalse();
        assertThat(ConfigValue.convert("4711")).isEqualTo(4711);
        assertThat(ConfigValue.convert("-1")).isEqualTo(-1);

        assertThat(ConfigValue).hasToString(
                String.format("ConfigValue{key=aaa, classType=%s, converter=%s, defaultValue=13, predicate=%s}", Integer.class, converter, predicate)
        );
    }

    @Test
    @DisplayName("Test config constant without predicate function")
    public void testConfigValueWithoutPredicate() {
        final Function<String, Boolean> converter = (value) -> "yes".equalsIgnoreCase(value);
        final Supplier<Boolean> defaultValue = () -> Boolean.FALSE;
        final var ConfigValue = new ConfigValue<>("BBB", Boolean.class, converter, defaultValue, null);

        assertThat(ConfigValue.getKey()).isEqualTo("bbb");
        assertThat(ConfigValue.getClassType()).isEqualTo(Boolean.class);
        assertThat(ConfigValue.getConverter()).isSameAs(converter);
        assertThat(ConfigValue.getDefaultValue()).isFalse();
        assertThat(ConfigValue.getPredicate()).isNull();

        assertThat(ConfigValue.isValid(null)).isFalse();
        assertThat(ConfigValue.isValid(true)).isTrue();
        assertThat(ConfigValue.isValid(false)).isTrue();
        assertThat(ConfigValue.convert("yes")).isTrue();
        assertThat(ConfigValue.convert("no")).isFalse();

        assertThat(ConfigValue).hasToString(
                String.format("ConfigValue{key=bbb, classType=%s, converter=%s, defaultValue=false, predicate=null}", Boolean.class, converter)
        );
    }

}
