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

package li.pitschmann.knx.core.test.strategy;

/**
 * Marker interface for description strategy to provide description about the
 * KNX mock server. The KNX Net/IP client will check the predication if the
 * communication between the KNX mock server and client are meet.
 * <p/>
 * <u>Possible workflow:</u>
 * <pre>
 * [ Client ] --- request --> [ Mock Server ]
 * [ Client ] <-- response -- [ Mock Server ]
 * </pre>
 */
public interface DescriptionStrategy extends ResponseStrategy {
    // empty
}