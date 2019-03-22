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

package li.pitschmann.knx.server.trigger;

import li.pitschmann.knx.link.header.ServiceType;
import li.pitschmann.knx.server.MockServerLogic;

/**
 * A trigger rule to match the occurrence of given {@link ServiceType}
 */
public final class WaitRequestTriggerRule implements TriggerRule {
    private final MockServerLogic logic;
    private final ServiceType serviceType;
    private final int occurrence;

    public WaitRequestTriggerRule(final MockServerLogic logic, final ServiceType serviceType, final int occurrence) {
        this.logic = logic;
        this.serviceType = serviceType;
        this.occurrence = occurrence;
    }

    public boolean apply() {
        return logic.getServiceTypeOccurrence(serviceType) == occurrence;
    }
}
