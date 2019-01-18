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

import li.pitschmann.knx.link.body.cemi.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Verifies {@link MessageCode} enum class
 *
 * @author PITSCHR
 */
public final class MessageCodeTest extends AbstractKnxByteEnumTest<MessageCode> {
    @Override
    protected int numberOfElements() {
        return 3;
    }

    @Test
    @Override
    void validValueOf() {
        assertThat(MessageCode.valueOf(0x11)).isEqualTo(MessageCode.L_DATA_REQ);
        assertThat(MessageCode.valueOf(0x2E)).isEqualTo(MessageCode.L_DATA_CON);
        assertThat(MessageCode.valueOf(0x29)).isEqualTo(MessageCode.L_DATA_IND);
    }

    @Test
    @Override
    void friendlyName() {
        assertThat(MessageCode.L_DATA_REQ.getFriendlyName()).isEqualTo("L-Data.req (Request)");
        assertThat(MessageCode.L_DATA_CON.getFriendlyName()).isEqualTo("L-Data.con (Confirmation)");
        assertThat(MessageCode.L_DATA_IND.getFriendlyName()).isEqualTo("L-Data.ind (Indication)");
    }

    @Test
    @Override
    public void testToString() {
        assertThat(MessageCode.L_DATA_REQ).hasToString("MessageCode{name=L_DATA_REQ, friendlyName=L-Data.req (Request), code=17 (0x11)}");
        assertThat(MessageCode.L_DATA_CON).hasToString("MessageCode{name=L_DATA_CON, friendlyName=L-Data.con (Confirmation), code=46 (0x2E)}");
        assertThat(MessageCode.L_DATA_IND).hasToString("MessageCode{name=L_DATA_IND, friendlyName=L-Data.ind (Indication), code=41 (0x29)}");
    }
}
