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

package li.pitschmann.knx.link.body.dib;

import com.google.common.base.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;

/**
 * Abstract DIB for all DIB implementations
 *
 * @author PITSCHR
 */
abstract class AbstractDIB extends AbstractMultiRawData {
    private final int length;
    private final DescriptionType descriptionType;

    protected AbstractDIB(byte[] rawData) {
        super(rawData);

        this.length = Bytes.toUnsignedInt(rawData[0]);
        this.descriptionType = DescriptionType.valueOf(Bytes.toUnsignedInt(rawData[1]));
    }

    @Override
    protected void validate(byte[] rawData) {
        if (rawData == null) {
            throw new KnxNullPointerException("rawData");
        } else if (rawData.length < 2 || rawData.length != rawData[0]) {
            // first byte is the required length
            throw new KnxNumberOutOfRangeException("rawData", 2, rawData.length > 0 ? rawData[0] : 2, rawData.length, rawData);
        }
    }

    public int getLength() {
        return this.length;
    }

    public DescriptionType getDescriptionType() {
        return this.descriptionType;
    }

    @Override
    public String toString(boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("length", this.length + " (" + ByteFormatter.formatHex(this.length) + ")")
                .add("descriptionType", this.descriptionType);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }

}
