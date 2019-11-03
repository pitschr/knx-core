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

import li.pitschmann.knx.link.KnxByteEnum;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.utils.ByteFormatter;
import li.pitschmann.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Arrays;

public enum Priority implements KnxByteEnum {
    /**
     * System priority
     */
    SYSTEM(0x00, "System Priority"),
    /**
     * Normal priority
     */
    NORMAL(0x01, "Normal Priority"),
    /**
     * Urgent priority
     */
    URGENT(0x02, "Urgent Priority"),
    /**
     * Low priority
     */
    LOW(0x03, "Low Priority");

    private final int code;
    private final String friendlyName;

    Priority(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    /**
     * A matching {@link Priority} for the given {@code code}
     *
     * @param code
     * @return existing {@link Priority}, or {@link KnxEnumNotFoundException} if no {@link Priority}
     * for given {@code code} exists
     */
    @Nonnull
    public static Priority valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(Priority.class, code));
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Nonnull
    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:off
    }
}
