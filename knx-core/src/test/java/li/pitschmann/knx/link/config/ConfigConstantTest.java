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

package li.pitschmann.knx.link.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ConfigConstant}
 */
public class ConfigConstantTest {

    @Test
    @DisplayName("Test config constant")
    public void testConfigConstant() {
        final Function<String, Integer> converter = Integer::parseInt;
        final Supplier<Integer> defaultValue = () -> 13;
        final Predicate<Integer> predicate = (value) -> value > 3 && value < 10;
        final var configConstant = new ConfigConstant<>("aAa", Integer.class, converter, defaultValue, predicate, false);

        assertThat(configConstant.getKey()).isEqualTo("aaa"); // lower cased!
        assertThat(configConstant.getClassType()).isEqualTo(Integer.class);
        assertThat(configConstant.getConverter()).isSameAs(converter);
        assertThat(configConstant.getDefaultValue()).isEqualTo(13);
        assertThat(configConstant.isSettable()).isFalse();

        assertThat(configConstant.isValid(0)).isFalse();
        assertThat(configConstant.isValid(4)).isTrue();
        assertThat(configConstant.isValid(8)).isTrue();
        assertThat(configConstant.isValid(11)).isFalse();
        assertThat(configConstant.convert("4711")).isEqualTo(4711);
        assertThat(configConstant.convert("-1")).isEqualTo(-1);

        assertThat(configConstant).hasToString(
                String.format("ConfigConstant{key=aaa, settable=false, classType=%s, converter=%s, defaultValue=13, predicate=%s}", Integer.class, converter, predicate)
        );
    }

    @Test
    @DisplayName("Test config constant without predicate function")
    public void testConfigConstantWithoutPredicate() {
        final Function<String, Boolean> converter = (value) -> "yes".equalsIgnoreCase(value);
        final Supplier<Boolean> defaultValue = () -> Boolean.FALSE;
        final var configConstant = new ConfigConstant<>("BBB", Boolean.class, converter, defaultValue, true);

        assertThat(configConstant.getKey()).isEqualTo("bbb");
        assertThat(configConstant.getClassType()).isEqualTo(Boolean.class);
        assertThat(configConstant.getConverter()).isSameAs(converter);
        assertThat(configConstant.getDefaultValue()).isFalse();
        assertThat(configConstant.isSettable()).isTrue();

        assertThat(configConstant.isValid(true)).isTrue();
        assertThat(configConstant.isValid(false)).isTrue();
        assertThat(configConstant.convert("yes")).isTrue();
        assertThat(configConstant.convert("no")).isFalse();

        assertThat(configConstant).hasToString(
                String.format("ConfigConstant{key=bbb, settable=true, classType=%s, converter=%s, defaultValue=false, predicate=null}", Boolean.class, converter)
        );
    }

}
