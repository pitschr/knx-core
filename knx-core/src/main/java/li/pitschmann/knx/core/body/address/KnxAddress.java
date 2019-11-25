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

package li.pitschmann.knx.core.body.address;

import li.pitschmann.knx.core.AbstractMultiRawData;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.exceptions.KnxNumberOutOfRangeException;

import javax.annotation.Nonnull;

/**
 * Abstract KNX Address class for {@link IndividualAddress} and {@link GroupAddress} implementations
 *
 * @author PITSCHR
 */
public abstract class KnxAddress extends AbstractMultiRawData {
    public static final int STRUCTURE_LENGTH = 2;

    public KnxAddress(final @Nonnull byte[] addressRawData) {
        super(addressRawData);
    }

    /**
     * Returns the {@link AddressType} of current implementation
     *
     * @return {@link AddressType}
     */
    public abstract AddressType getAddressType();

    /**
     * Returns the address as String
     *
     * @return address in human-friendly format.
     */
    public abstract String getAddress();

    @Override
    protected void validate(final @Nonnull byte[] addressRawData) {
        if (addressRawData == null) {
            throw new KnxNullPointerException("addressRawData");
        } else if (addressRawData.length != STRUCTURE_LENGTH) {
            // 2 bytes for this body
            throw new KnxNumberOutOfRangeException("addressRawData", STRUCTURE_LENGTH, STRUCTURE_LENGTH, addressRawData.length, addressRawData);
        }
    }
}
