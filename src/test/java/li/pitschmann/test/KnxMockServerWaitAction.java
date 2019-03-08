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

package li.pitschmann.test;

import com.google.common.base.CharMatcher;
import com.google.common.base.MoreObjects;
import li.pitschmann.knx.link.header.ServiceType;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Wait Action behavior for the KNX Mock Server
 *
 * @author PITSCHR
 */
public final class KnxMockServerWaitAction implements KnxMockServerAction {
    public static final KnxMockServerWaitAction NEXT = new KnxMockServerWaitAction();
    private final ServiceType serviceType;
    private final long duration;
    private final TimeUnit timeUnit;
    private WaitType waitType;

    private KnxMockServerWaitAction() {
        this.serviceType = null;
        this.duration = 0;
        this.timeUnit = null;
        this.waitType = WaitType.NEXT;
    }

    private KnxMockServerWaitAction(final long duration, final TimeUnit timeUnit) {
        this.serviceType = null;
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.waitType = WaitType.DELAY;
    }

    private KnxMockServerWaitAction(final ServiceType serviceType) {
        this.serviceType = serviceType;
        this.duration = 0;
        this.timeUnit = null;
        this.waitType = WaitType.TYPE;
    }

    /**
     * Reads the {@code knxCommand} and returns an instance of {@link KnxMockServerWaitAction}
     *
     * @param knxCommand
     * @return an instance of {@link KnxMockServerWaitAction}
     */
    static KnxMockServerWaitAction of(final String knxCommand) {
        final var waitCommand = knxCommand.split("=")[1];
        // wait for next packet?
        if ("NEXT".equalsIgnoreCase(waitCommand)) {
            return KnxMockServerWaitAction.NEXT;
        }
        // wait delay
        else if (CharMatcher.inRange('0', '9').matchesAllOf(waitCommand)) {
            final var duration = Long.parseLong(waitCommand);
            return new KnxMockServerWaitAction(duration, TimeUnit.MILLISECONDS);
        }
        // else wait for specific packet with service type
        else {
            return new KnxMockServerWaitAction(ServiceType.valueOf(waitCommand.toUpperCase()));
        }
    }

    public long getDuration() {
        return this.duration;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    @Nonnull
    public WaitType getWaitType() {
        return this.waitType;
    }

    @Override
    public String toString() {
        // @formatter:off
        MoreObjects.ToStringHelper sh = MoreObjects.toStringHelper(this);
        if (this.waitType == WaitType.DELAY) {
            sh.add("duration", this.duration);
            sh.add("timeUnit", this.timeUnit);
        } else if (this.waitType == WaitType.TYPE) {
            sh.add("serviceType", this.serviceType.name());
        } else {
            sh.addValue("<next packet>");
        }
        return sh.toString();
        // @formatter:on
    }

    public enum WaitType {
        NEXT, DELAY, TYPE
    }
}
