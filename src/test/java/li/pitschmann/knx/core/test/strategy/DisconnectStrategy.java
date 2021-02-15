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

package li.pitschmann.knx.core.test.strategy;

/**
 * Marker interface for disconnect strategy to handle the disconnection.
 * The disconnect can be initiated by the KNX Net/IP client and by KNX mock server
 * <p>
 * <u>Possible workflows:</u><br>
 * KNX client sends the disconnect request if it wants to close the connection and the
 * disconnect request will be received by the KNX mock server which will respond the
 * client with a disconnect response frame.
 * <pre>
 * [ Client ] --- request --> [ Mock Server ]
 * [ Client ] <-- response -- [ Mock Server ]
 * </pre>
 * KNX mock server will send the disconnect request to KNX client to close the connection.
 * Once disconnect frame was received by the KNX client the client should send a disconnect
 * response to KNX mock server.
 * <pre>
 * [ Client ] <-- request --- [ Mock Server ]
 * [ Client ] -- response --> [ Mock Server ]
 * </pre>
 */
public interface DisconnectStrategy extends RequestStrategy<Void>, ResponseStrategy {
    // empty
}
