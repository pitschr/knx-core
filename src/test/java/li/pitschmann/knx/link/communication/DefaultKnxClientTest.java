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

package li.pitschmann.knx.link.communication;

import li.pitschmann.test.KnxBody;
import li.pitschmann.test.KnxMockServer;
import li.pitschmann.test.KnxTest;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.fail;

/**
 * Test for {@link DefaultKnxClient}
 *
 * @author PITSCHR
 */
public class DefaultKnxClientTest {
    /**
     * Test {@link DefaultKnxClient#DefaultKnxClient(String)}
     *
     * @param mockServer
     */
    @KnxTest(KnxBody.Sequences.MINIMAL_DISCONNECT_BY_REMOTE)
    @DisplayName("Test KNX client instantiation using host address as string")
    public void testInstantiationViaHostAddress(final KnxMockServer mockServer) {
        try (final var client = new DefaultKnxClient("127.0.0.1:" + mockServer.getPort())) {
            // ok
        } catch (final Throwable t) {
            fail("Unexpected test state", t);
        }
    }
}
