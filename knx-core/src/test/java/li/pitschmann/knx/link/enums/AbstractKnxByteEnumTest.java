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

package li.pitschmann.knx.link.enums;

import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract Test for {@link KnxByteEnum} classes
 *
 * @param <E>
 * @author PITSCHR
 */
abstract class AbstractKnxByteEnumTest<E extends Enum<E> & KnxByteEnum> extends AbstractKnxEnumTest<E> {

    /**
     * Tests if the {@link KnxByteEnum#getCode()} and {@link KnxByteEnum#getCodeAsByte()} is same for single byte enum
     */
    @Test
    public void verifyCodeIsSameAsCodeAsByte() {
        for (final var identifier : EnumSet.allOf(this.getCurrentClass())) {
            final var identifierCodeAsInt = identifier.getCode();
            final var identifierCodeAsByte = identifier.getCodeAsByte();
            assertThat(identifierCodeAsByte).inBinary().isSameAs((byte) (identifierCodeAsInt & 0xFF));
        }
    }

    /**
     * Tests the {@code valueOf(int)} static method with <strong>valid</strong> samples
     */
    @Override
    abstract void validValueOf();

    /**
     * Tests the {@code valueOf(int)} static method with <strong>invalid</strong> samples
     */
    @Override
    @Test
    public void invalidValueOf() {
        // bytes
        assertThrows(KnxEnumNotFoundException.class, () -> this.invokeValueOf((byte) -1));

        // ints
        final var testInvalidInts = new int[]{-1, 0xFF};
        for (final var testInvalidInt : testInvalidInts) {
            assertThrows(KnxEnumNotFoundException.class, () -> this.invokeValueOf(testInvalidInt));
        }
    }

    /**
     * Tests the {@link KnxByteEnum#getFriendlyName()}
     */
    @Override
    abstract void friendlyName();

}
