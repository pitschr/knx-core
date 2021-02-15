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

package li.pitschmann.knx.core.dib;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * Service Type Family and Version. Designed to be used
 * for {@link SupportedServiceFamiliesDIB}.
 *
 * @author PITSCHR
 */
public final class ServiceTypeFamilyVersion {
    private final ServiceTypeFamily family;
    private final int version;

    public ServiceTypeFamilyVersion(final ServiceTypeFamily family, final int version) {
        Preconditions.checkNonNull(family, "Service Type Family is required.");

        this.family = family;
        this.version = version;
    }

    public ServiceTypeFamily getFamily() {
        return family;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("family", family.name())
                .add("version", version)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ServiceTypeFamilyVersion) {
            final var other = (ServiceTypeFamilyVersion) obj;
            return Objects.equals(this.family, other.family)
                    && Objects.equals(this.version, other.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(family, version);
    }
}
