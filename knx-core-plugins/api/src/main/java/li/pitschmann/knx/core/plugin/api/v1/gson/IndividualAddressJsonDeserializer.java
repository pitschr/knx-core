/*
 * KNX Link - A library for KNX Net/IP communication
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

package li.pitschmann.knx.core.plugin.api.v1.gson;

import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.IndividualAddress;

/**
 * De-Serializes a JSON format of group address to an instance of {@link IndividualAddress}
 * <p>
 * Supported JSON formats:
 * <pre>
 * {"type":0,"raw":[-61,45]}
 * [-61,45]
 * "12.3.45"
 * </pre>
 */
public final class IndividualAddressJsonDeserializer extends AbstractKnxAddressJsonDeserializer<IndividualAddress> {
    public static final IndividualAddressJsonDeserializer INSTANCE = new IndividualAddressJsonDeserializer();

    private IndividualAddressJsonDeserializer() {
        // private constructor
    }

    @Override
    protected AddressType supportedAddressType() {
        return AddressType.INDIVIDUAL;
    }

    @Override
    protected IndividualAddress convert(final byte[] addressArray) {
        return IndividualAddress.of(addressArray);
    }

    @Override
    protected IndividualAddress convert(final String address) {
        return IndividualAddress.of(address);
    }
}
