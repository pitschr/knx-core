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
import li.pitschmann.knx.core.address.GroupAddress;

/**
 * De-Serializes a JSON format of group address to an instance of {@link GroupAddress}
 * <p>
 * Supported JSON formats:
 * <pre>
 * {"type":1,"raw":[15,19]}
 * [15,59]
 * 3899
 * "3899"
 * "1/1851"
 * "1/7/59"
 * </pre>
 */
public final class GroupAddressJsonDeserializer extends AbstractKnxAddressJsonDeserializer<GroupAddress> {
    public static final GroupAddressJsonDeserializer INSTANCE = new GroupAddressJsonDeserializer();

    private GroupAddressJsonDeserializer() {
        // private constructor
    }

    @Override
    protected AddressType supportedAddressType() {
        return AddressType.GROUP;
    }

    @Override
    protected GroupAddress convert(final byte[] addressArray) {
        return GroupAddress.of(addressArray);
    }

    @Override
    protected GroupAddress convert(final String address) {
        return GroupAddress.of(address);
    }
}
