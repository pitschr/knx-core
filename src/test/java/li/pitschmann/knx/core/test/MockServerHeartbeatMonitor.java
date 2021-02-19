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

package li.pitschmann.knx.core.test;

import li.pitschmann.knx.core.utils.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Health check thread for KNX mock server (package-protected)
 */
class MockServerHeartbeatMonitor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MockServerHeartbeatMonitor.class);
    private final MockServer mockServer;
    private final AtomicLong lastHeartbeat = new AtomicLong(System.currentTimeMillis());
    private final long beginning = System.currentTimeMillis();

    MockServerHeartbeatMonitor(final MockServer mockServer) {
        this.mockServer = mockServer;
    }

    @Override
    public void run() {
        log.info("*** KNX Mock Server [heartbeat] START ***");
        while (System.currentTimeMillis() - lastHeartbeat.get() < 10000
                && !this.mockServer.isCancelled()) {
            Sleeper.seconds(1);
        }

        System.out.println("PITSCHR: beginning: " + beginning + ", last: " + lastHeartbeat.get() +
                ", diff: " + (System.currentTimeMillis() - lastHeartbeat.get()) + ", isCancelled: " + this.mockServer.isCancelled() +
                ", receivedBodies: " + this.mockServer.getReceivedBodies().size() + ", sentBodies: " + this.mockServer.getSentBodies().size());

        this.ping();
        while (System.currentTimeMillis() - lastHeartbeat.get() < 10000
                && !this.mockServer.isCancelled()) {
            Sleeper.seconds(1);
        }
        System.out.println("PITSCHR: beginning: " + beginning + ", last: " + lastHeartbeat.get() +
                ", diff: " + (System.currentTimeMillis() - lastHeartbeat.get()) + ", isCancelled: " + this.mockServer.isCancelled() +
                ", receivedBodies: " + this.mockServer.getReceivedBodies().size() + ", sentBodies: " + this.mockServer.getSentBodies().size());
        log.info("*** KNX Mock Server [heartbeat] END *** ({})", System.currentTimeMillis() - lastHeartbeat.get());

        this.mockServer.cancel();
    }

    public void ping() {
        lastHeartbeat.set(System.currentTimeMillis());
    }
}
