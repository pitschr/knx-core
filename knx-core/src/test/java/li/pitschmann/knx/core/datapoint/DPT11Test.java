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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.datapoint.value.DPT11Value;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleBytesException;
import li.pitschmann.knx.core.exceptions.DataPointTypeIncompatibleSyntaxException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test Class for {@link DPT11}
 *
 * @author PITSCHR
 */
public class DPT11Test extends AbstractDataPointTypeTest<DPT11, DPT11Value> {
    private static final DPT11 DPT_DATE = DPT11.DATE;

    @Override
    @Test
    public void testIdAndDescription() {
        assertThat(DPT_DATE.getId()).isEqualTo("11.001");
        assertThat(DPT_DATE.getDescription()).isEqualTo("Date");
    }

    @Override
    @Test
    public void testCompatibility() {
        // failures
        assertThatThrownBy(() -> DPT_DATE.toValue(new byte[1])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_DATE.toValue(new byte[2])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_DATE.toValue(new byte[4])).isInstanceOf(DataPointTypeIncompatibleBytesException.class);
        assertThatThrownBy(() -> DPT_DATE.toValue(new String[0])).isInstanceOf(DataPointTypeIncompatibleSyntaxException.class);

        // OK
        assertThat(DPT_DATE.toValue(new byte[]{0x01, 0x01, 0x00})).isInstanceOf(DPT11Value.class);
        assertThat(DPT_DATE.toValue(new byte[]{0x1f, 0x0c, 0x63})).isInstanceOf(DPT11Value.class);
        assertThat(DPT_DATE.toValue(LocalDate.now())).isInstanceOf(DPT11Value.class);
        assertThat(DPT_DATE.toValue("2000-01-02")).isInstanceOf(DPT11Value.class);
    }

    @Override
    @Test
    public void testOf() {
        // day = 1, month = 1, year = 90 (=1990)
        this.assertDPT(new byte[]{0x01, 0x01, 0x5a}, LocalDate.of(1990, 1, 1));
        // day = 9, month = 3, year = 91 (=1991)
        this.assertDPT(new byte[]{0x09, 0x03, 0x5b}, LocalDate.of(1991, 3, 9));
        // day = 18, month = 6, year = 99 (=1999)
        this.assertDPT(new byte[]{0x12, 0x06, 0x63}, LocalDate.of(1999, 6, 18));
        // day = 27, month = 9, year = 50 (=2050)
        this.assertDPT(new byte[]{0x1b, 0x09, 0x32}, LocalDate.of(2050, 9, 27));
        // day = 31, month = 12, year = 89 (=2089)
        this.assertDPT(new byte[]{0x1f, 0x0c, 0x59}, LocalDate.of(2089, 12, 31));
    }

    /**
     * Asserts the DPT for given arguments {@code bValueArray} and {@code localDate}
     *
     * @param bValueArray
     * @param localDate
     */
    private void assertDPT(final byte[] bValueArray, final LocalDate localDate) {
        final var dpt = DPT_DATE;
        final var dptValue = dpt.toValue(localDate);

        // assert base DPT
        this.assertBaseDPT(dpt, bValueArray, dptValue);

        // assert specific DPT11
        assertThat(dpt.toValue(localDate.toString())).isEqualTo(dptValue);
        assertThat(dpt.toValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth())).isEqualTo(dptValue);
        assertThat(dpt.toByteArray(localDate)).containsExactly(bValueArray);
        assertThat(dptValue.getDate()).isEqualTo(localDate);
    }
}
