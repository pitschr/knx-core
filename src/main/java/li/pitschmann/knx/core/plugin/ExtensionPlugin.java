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

package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.communication.KnxClient;

/**
 * Plug-in to be invoked when {@link KnxClient} starts up / shuts down.
 *
 * @author PITSCHR
 */
public interface ExtensionPlugin extends Plugin {
    /**
     * Notifies the plug-in when KNX communication is established.
     * <p>
     * The start of KNX communication is done when description & connect frames
     * have been exchanged between the KNX client and the KNX Net/IP device.
     */
    void onStart();

    /**
     * Notifies the plug-in after KNX communication stop.
     * <p>
     * The stop of KNX communication is done when disconnect frames have been
     * exchanged between the KNX client and the KNX Net/IP device.
     */
    void onShutdown();
}
