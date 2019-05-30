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

package li.pitschmann.knx.test;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.knx.link.header.ServiceType;

import javax.annotation.Nonnull;

/**
 * Bytes Body
 * <p/>
 * With KNX mock server we are simulating bad / corrupt scenarios.
 */
public abstract class BytesBody extends AbstractMultiRawData implements Body {
    protected BytesBody(final byte[] bytes) {
        super(bytes);
    }

    @Nonnull
    @Override
    public ServiceType getServiceType() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void validate(byte[] rawData) throws KnxException {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        }
    }

    @Override
    public String toString(boolean inclRawData) {
        return MoreObjects.toStringHelper(this).add("rawData", this.getRawDataAsHexString()).toString();
    }
}
