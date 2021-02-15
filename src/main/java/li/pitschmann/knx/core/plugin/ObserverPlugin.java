/*
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

package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.body.Body;

/**
 * Plug-in to be notified when incoming / outgoing {@link Body} or if there was a problem with communication of
 * {@link Body}
 *
 * @author PITSCHR
 */
public interface ObserverPlugin extends Plugin {
    /**
     * Notifies the plug-in about incoming successful {@link Body} message
     *
     * @param item the incoming body
     */
    void onIncomingBody(Body item);

    /**
     * Notifies the plug-in about outgoing successful {@link Body} message
     *
     * @param item the outgoing body
     */
    void onOutgoingBody(Body item);

    /**
     * Notifies the plug-in about a {@link Throwable}.
     *
     * @param throwable the cause
     */
    void onError(Throwable throwable);
}
