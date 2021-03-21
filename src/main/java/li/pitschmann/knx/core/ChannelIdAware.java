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

package li.pitschmann.knx.core;

/**
 * Interface for KNX packet bodies which are aware of channel id.
 * <p>
 * The KNX Net/IP Server shall assign a Communication Channel ID
 * to each established communication channel and is initially
 * set in the CONNECT_REQUEST for unique identification.
 * <p>
 * KNX Specification, Core
 *
 * @author PITSCHR
 */
public interface ChannelIdAware {
    /**
     * Returns the Channel ID that is set by the KNX/IP Net device.
     * It is in byte format and channel id is between {@code 0 (0x00)}
     * and {@code 255 (0xFF)}
     * <p>
     * The channel id is required for tunneling communication only to
     * distinguish the KNX traffic between several clients/endpoints.
     *
     * @return channel id
     */
    int getChannelId();
}
