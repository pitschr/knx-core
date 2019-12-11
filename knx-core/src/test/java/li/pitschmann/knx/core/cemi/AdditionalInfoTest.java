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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test case for {@link AdditionalInfo}
 *
 * @author PITSCHR
 */
public final class AdditionalInfoTest {

    /**
     * Test {@link AdditionalInfo} with no length
     */
    @Test
    public void emptyAdditionalInfo() {
        // create
        final var addInfoByCreate = AdditionalInfo.empty();
        assertThat(addInfoByCreate.getLength()).isEqualTo(0);
        assertThat(addInfoByCreate.getTotalLength()).isEqualTo(1);

        // create by bytes
        final var addInfoByValueOf = AdditionalInfo.of(new byte[]{0x00});
        assertThat(addInfoByValueOf.getLength()).isEqualTo(0);
        assertThat(addInfoByValueOf.getTotalLength()).isEqualTo(addInfoByCreate.getRawData().length);

        // compare raw data of 'create' and 'create by bytes'
        assertThat(addInfoByCreate.getRawData()).isEqualTo(addInfoByValueOf.getRawData());

        // with raw data
        assertThat(AdditionalInfo.empty()).hasToString("AdditionalInfo{length=0, totalLength=1, rawData=0x00}");
        // without raw data
        assertThat(AdditionalInfo.empty().toString(false)).isEqualTo("AdditionalInfo{length=0, totalLength=1}");
    }

    /**
     * Tests <strong>invalid</strong> control byte parameters
     */
    @Test
    public void invalidCases() {
        // null
        assertThatThrownBy(() -> AdditionalInfo.of(null)).isInstanceOf(KnxNullPointerException.class).hasMessageContaining("addInfoRawData");

        // additional info are not supported yet
        assertThatThrownBy(() -> AdditionalInfo.of(new byte[]{0x01, 0x00})).isInstanceOf(UnsupportedOperationException.class);
    }
}
