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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.body.Body;

/**
 * Interface Statistic about KNX/IP communication
 *
 * @author PITSCHR
 */
public interface KnxStatistic {
    /**
     * Returns <strong>total</strong> number of received {@link Body} by KNX Net/IP Client from KNX Net/IP device
     *
     * @return total number of received {@link Body}
     */
    long getNumberOfBodyReceived();

    /**
     * Returns the number of received {@link Body} by KNX Net/IP Client from KNX Net/IP device
     *
     * @param bodyClass class of body
     * @return number of received {@link Body}
     */
    long getNumberOfBodyReceived(Class<? extends Body> bodyClass);

    /**
     * Returns <strong>total</strong> number of sent {@link Body} from KNX Net/IP Client to the KNX Net/IP device
     *
     * @return total number of sent {@link Body}
     */
    long getNumberOfBodySent();

    /**
     * Returns the number of sent {@link Body} from KNX Net/IP Client to the KNX Net/IP device
     *
     * @param bodyClass class of body
     * @return number of sent {@link Body}
     */
    long getNumberOfBodySent(Class<? extends Body> bodyClass);

    /**
     * Returns number of bytes received by KNX Net/IP Client from KNX Net/IP device
     * <p>
     * The number of bytes is not 100% guaranteed, because it depends on underlying protocol as well.
     *
     * @return number of received bytes
     */
    long getNumberOfBytesReceived();

    /**
     * Returns number of bytes sent from KNX Net/IP Client to the KNX Net/IP device.
     * <p>
     * The number of bytes is not 100% guaranteed, because it depends on underlying protocol as well.
     *
     * @return number of sent bytes
     */
    long getNumberOfBytesSent();

    /**
     * Returns <strong>total</strong> number of Errors (Throwable, Exception) of ingoing/outgoing {@link Body}
     *
     * @return total number of errors
     */
    long getNumberOfErrors();

    /**
     * Returns the error rate in percentage based on calculation from received / sent bodies.
     *
     * @return error rate in percentage
     */
    default double getErrorRate() {
        final var numberOfPackets = this.getNumberOfBodyReceived() + this.getNumberOfBodySent();
        if (numberOfPackets == 0) { // avoid division by zero
            return 0d;
        } else {
            return (this.getNumberOfErrors() * 100d) / numberOfPackets;
        }
    }
}
