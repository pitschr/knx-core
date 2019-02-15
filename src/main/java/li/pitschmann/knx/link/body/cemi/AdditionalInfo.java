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

package li.pitschmann.knx.link.body.cemi;

import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.AbstractMultiRawData;
import li.pitschmann.knx.link.exceptions.KnxNullPointerException;
import li.pitschmann.utils.Bytes;

/**
 * Additional info container for CEMI
 *
 * @author PITSCHR
 */
public final class AdditionalInfo extends AbstractMultiRawData {
    private static final AdditionalInfo EMPTY = new AdditionalInfo(new byte[]{0x00});
    private final int length;
    private final int totalLength;

    private AdditionalInfo(final byte[] addInfoRawData) {
        super(addInfoRawData);

        this.length = Bytes.toUnsignedInt(addInfoRawData[0]);
        this.totalLength = addInfoRawData.length;
    }

    /**
     * Returns an empty {@link AdditionalInfo} instance
     *
     * @return immutable empty {@link AdditionalInfo} instance
     */
    public static AdditionalInfo empty() {
        return EMPTY;
    }

    /**
     * Builds a new {@link AdditionalInfo} instance
     *
     * @param bytes complete byte array for {@link AdditionalInfo}
     * @return immutable {@link AdditionalInfo}
     */
    public static AdditionalInfo valueOf(final byte[] bytes) {
        return new AdditionalInfo(bytes);
    }

    @Override
    protected void validate(final byte[] addInfoRawData) {
        if (addInfoRawData == null) {
            throw new KnxNullPointerException("addInfoRawData");
        }

        // validate length
        if (Bytes.toUnsignedInt(addInfoRawData[0]) > 0) {
            throw new UnsupportedOperationException("Not implemented yet!");
        }
    }

    public int getLength() {
        return this.length;
    }

    public int getTotalLength() {
        return this.totalLength;
    }

    @Override
    public String toString(final boolean inclRawData) {
        // @formatter:off
        final var h = MoreObjects.toStringHelper(this)
                .add("length", this.length)
                .add("totalLength", this.totalLength);
        // @formatter:on
        if (inclRawData) {
            h.add("rawData", this.getRawDataAsHexString());
        }
        return h.toString();
    }
}
