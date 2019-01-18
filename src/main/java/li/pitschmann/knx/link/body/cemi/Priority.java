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

import com.google.common.base.*;
import li.pitschmann.knx.link.*;
import li.pitschmann.knx.link.exceptions.*;
import li.pitschmann.utils.*;

import java.util.*;

public enum Priority implements KnxByteEnum {
    // @formatter:off
    SYSTEM(0x00, "System Priority"),
    NORMAL(0x01, "Normal Priority"),
    URGENT(0x02, "Urgent Priority"),
    LOW(0x03, "Low Priority")
    // @formatter:on
    ;

    private final int code;
    private final String friendlyName;

    Priority(final int code, final String friendlyName) {
        this.code = code;
        this.friendlyName = friendlyName;
    }

    public static Priority valueOf(final int code) {
        return Arrays.stream(values()).filter(x -> x.getCode() == code).findFirst()
                .orElseThrow(() -> new KnxEnumNotFoundException(Priority.class, code));
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @Override
    public String toString() {
        // @formatter:off
        return MoreObjects.toStringHelper(this)
                .add("name", this.name())
                .add("friendlyName", this.friendlyName)
                .add("code", this.code + " (" + ByteFormatter.formatHex(this.code) + ")")
                .toString();
        // @formatter:off
    }
}
