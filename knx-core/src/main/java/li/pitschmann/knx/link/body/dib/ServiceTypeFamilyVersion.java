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

import li.pitschmann.knx.utils.Strings;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Service Type Family and Version. Designed to be used for {@link SupportedDeviceFamiliesDIB}.
 *
 * @author PITSCHR
 */
public final class ServiceTypeFamilyVersion {
    private final ServiceTypeFamily family;
    private int version;

    public ServiceTypeFamilyVersion(final @Nonnull ServiceTypeFamily family, final int version) {
        this.family = Objects.requireNonNull(family);
        this.version = version;
    }

    @Nonnull
    public ServiceTypeFamily getFamily() {
        return this.family;
    }

    public int getVersion() {
        return this.version;
    }

    @Nonnull
    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("family", this.family)
                .add("version", this.version)
                .toString();
        // @formatter:on
    }
}
