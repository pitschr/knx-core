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

/**
 * Multi Byte Raw Data Aware interface.
 * <p>
 * Classes which implements this interface are aware of the raw data in byte array format.
 *
 * @author PITSCHR
 */
public interface MultiRawDataAware {
    /**
     * Returns the body as raw data in byte array format
     *
     * @return byte array, defensively copied
     */
    byte[] toByteArray();
}
