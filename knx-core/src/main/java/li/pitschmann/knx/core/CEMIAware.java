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

package li.pitschmann.knx.core;

import li.pitschmann.knx.core.cemi.CEMI;

/**
 * Interface for KNX packet bodies which are aware of {@link CEMI}.
 *
 * @author PITSCHR
 */
public interface CEMIAware {
    /**
     * Returns the {@link CEMI} which represents the cEMI message format
     * in a generic structure for KNX Net/IP medium.
     *
     * @return cEMI message
     */
    CEMI getCEMI();
}
