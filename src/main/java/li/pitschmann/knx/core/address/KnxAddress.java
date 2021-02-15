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

package li.pitschmann.knx.core.address;

import li.pitschmann.knx.core.MultiRawDataAware;

/**
 * KNX Address interface for {@link IndividualAddress} and
 * {@link GroupAddress} implementations.
 *
 * @author PITSCHR
 */
public interface KnxAddress extends MultiRawDataAware {
    int STRUCTURE_LENGTH = 2;

    /**
     * Returns the {@link AddressType} of current implementation
     *
     * @return {@link AddressType}
     */
    AddressType getAddressType();

    /**
     * Returns the address as String
     *
     * @return address in human-friendly format.
     */
    String getAddress();
}
