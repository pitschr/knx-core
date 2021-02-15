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

package li.pitschmann.knx.core.datapoint.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test {@link DataPointValue}
 *
 * @author PITSCHR
 */
public class DataPointValueTest {

    @Test
    @DisplayName("Test #toText() that is not being overridden")
    public void testToText() {
        final var dptValue = mock(DataPointValue.class);
        when(dptValue.toByteArray()).thenReturn(new byte[]{0x44, 0x56, 0x78});
        when(dptValue.toText()).thenCallRealMethod();

        assertThat(dptValue.toText()).isEqualTo("0x44 56 78");
    }

}
