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

package li.pitschmann.knx.core.exceptions;

/**
 * KNX communication partner exception that is being thrown when KNX client cannot establish a communication to the
 * KNX Net/IP device because it doesn't support the tunneling.
 *
 * @author PITSCHR
 */
public class KnxNoTunnelingException extends KnxException {

    public KnxNoTunnelingException(final String message) {
        super(message);
    }

}
