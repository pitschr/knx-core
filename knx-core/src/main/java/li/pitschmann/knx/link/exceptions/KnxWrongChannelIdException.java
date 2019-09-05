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

package li.pitschmann.knx.link.exceptions;

import li.pitschmann.knx.link.ChannelIdAware;

import javax.annotation.Nonnull;

/**
 * Exception in case we received a packet which doesn't belong to our channel id. This exception can be used for
 * ignoring such packets. This exception may apply to objects which contains the {@link ChannelIdAware} interface.
 *
 * @author PITSCHR
 */
public final class KnxWrongChannelIdException extends KnxCommunicationException {
    /**
     * Constructor for {@link KnxWrongChannelIdException} in case we received a packet with unexpected channel id.
     *
     * @param channelIdAware
     * @param expectedChannelId
     */
    public KnxWrongChannelIdException(final @Nonnull ChannelIdAware channelIdAware, final int expectedChannelId) {
        super(String.format("Wrong channel id '%s' received, expected channel id is '%s': %s", channelIdAware.getChannelId(), expectedChannelId, channelIdAware));
    }
}
