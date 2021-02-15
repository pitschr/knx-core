/*
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link MessageCode} enum class
 *
 * @author PITSCHR
 */
final class MessageCodeTest {

    @Test
    @DisplayName("Test number of enum elements")
    void numberOfElements() {
        assertThat(MessageCode.values()).hasSize(3);
    }

    @Test
    @DisplayName("Valid cases for #valueOf()")
    void validValueOf() {
        assertThat(MessageCode.valueOf(0x11)).isEqualTo(MessageCode.L_DATA_REQ);
        assertThat(MessageCode.valueOf(0x2E)).isEqualTo(MessageCode.L_DATA_CON);
        assertThat(MessageCode.valueOf(0x29)).isEqualTo(MessageCode.L_DATA_IND);
    }

    @Test
    @DisplayName("Invalid cases for #valueOf()")
    void invalidValueOf() {
        assertThatThrownBy(() -> MessageCode.valueOf(-1)).isInstanceOf(KnxEnumNotFoundException.class);
    }

    @Test
    @DisplayName("Test #getFriendlyName()")
    void testGetFriendlyName() {
        assertThat(MessageCode.L_DATA_REQ.getFriendlyName()).isEqualTo("L-Data.req (Request)");
        assertThat(MessageCode.L_DATA_CON.getFriendlyName()).isEqualTo("L-Data.con (Confirmation)");
        assertThat(MessageCode.L_DATA_IND.getFriendlyName()).isEqualTo("L-Data.ind (Indication)");
    }

    @Test
    @DisplayName("Test #toString()")
    void testToString() {
        assertThat(MessageCode.L_DATA_REQ).hasToString("MessageCode{name=L_DATA_REQ, friendlyName=L-Data.req (Request), code=17}");
        assertThat(MessageCode.L_DATA_CON).hasToString("MessageCode{name=L_DATA_CON, friendlyName=L-Data.con (Confirmation), code=46}");
        assertThat(MessageCode.L_DATA_IND).hasToString("MessageCode{name=L_DATA_IND, friendlyName=L-Data.ind (Indication), code=41}");
    }
}
