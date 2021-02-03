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

package li.pitschmann.knx.core.communication;

import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.utils.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Statistic about KNX/IP communications between client and the KNX Net/IP device
 *
 * @author PITSCHR
 */
public final class InternalKnxStatistic implements KnxStatistic {
    private static final Logger log = LoggerFactory.getLogger(InternalKnxStatistic.class);
    /**
     * 14 bytes for Ethernet packet<br>
     * 20 bytes for IPV4<br>
     * 8 bytes for UDP header<br>
     * 6 bytes for KNX header<br>
     * = 48 bytes
     */
    private static final long KNX_PACKET_SIZE = 48L;

    // General statistics
    private final Map<Class<? extends Body>, AtomicLong> numberOfBodyReceivedMap = Maps.newHashMap(10);
    private final Map<Class<? extends Body>, AtomicLong> numberOfBodySentMap = Maps.newHashMap(10);
    private final AtomicLong numberOfBytesReceived = new AtomicLong();
    private final AtomicLong numberOfBytesSent = new AtomicLong();
    private final AtomicLong numberOfBodyReceived = new AtomicLong();
    private final AtomicLong numberOfBodySent = new AtomicLong();
    private final AtomicLong numberOfErrors = new AtomicLong();

    /**
     * Default package-private constructor for {@link InternalKnxStatistic}
     */
    InternalKnxStatistic() {
        log.trace("Internal KNX Statistic object created.");
    }

    @Override
    public long getNumberOfBodyReceived() {
        return this.numberOfBodyReceived.longValue();
    }

    @Override
    public long getNumberOfBodyReceived(final Class<? extends Body> bodyClass) {
        final var al = this.numberOfBodyReceivedMap.get(bodyClass);
        return al == null ? 0L : al.longValue();
    }

    @Override
    public long getNumberOfBodySent() {
        return this.numberOfBodySent.longValue();
    }

    @Override
    public long getNumberOfBodySent(final Class<? extends Body> bodyClass) {
        final var al = this.numberOfBodySentMap.get(bodyClass);
        return al == null ? 0L : al.longValue();
    }

    @Override
    public long getNumberOfBytesReceived() {
        return this.numberOfBytesReceived.longValue();
    }

    @Override
    public long getNumberOfBytesSent() {
        return this.numberOfBytesSent.longValue();
    }

    @Override
    public long getNumberOfErrors() {
        return this.numberOfErrors.longValue();
    }

    /**
     * (internal) Updates the statistics about received {@link Body}
     *
     * @param body the incoming body
     */
    public void onIncomingBody(final Body body) {
        this.numberOfBodyReceived.incrementAndGet();
        this.numberOfBodyReceivedMap.computeIfAbsent(body.getClass(), s -> new AtomicLong()).incrementAndGet();
        this.numberOfBytesReceived.addAndGet(body.toByteArray().length + KNX_PACKET_SIZE);
    }

    /**
     * (internal) Updates the statistics about sent {@link Body}
     *
     * @param body the outgoing body
     */
    public void onOutgoingBody(final Body body) {
        this.numberOfBodySent.incrementAndGet();
        this.numberOfBodySentMap.computeIfAbsent(body.getClass(), s -> new AtomicLong()).incrementAndGet();
        this.numberOfBytesSent.addAndGet(body.toByteArray().length + KNX_PACKET_SIZE);
    }

    /**
     * (internal) Updates the statistics about error
     *
     * @param throwable - not used yet in this class
     */
    public void onError(final Throwable throwable) {
        this.numberOfErrors.incrementAndGet();
    }

    /**
     * Returns an unmodifiable {@link KnxStatistic} to avoid a manipulation from outside.
     *
     * @return an unmodifiable instance of {@link KnxStatistic}
     */
    public KnxStatistic asUnmodifiable() {
        return new UnmodifiableKnxStatistic(this);
    }

    /**
     * Unmodifiable {@link KnxStatistic}
     *
     * @author PITSCHR
     */
    private static final class UnmodifiableKnxStatistic implements KnxStatistic {
        private final Map<Class<? extends Body>, Long> numberOfBodyReceivedMap;
        private final Map<Class<? extends Body>, Long> numberOfBodySentMap;
        private final long numberOfBodyReceived;
        private final long numberOfBodySent;
        private final long numberOfBytesReceived;
        private final long numberOfBytesSent;
        private final long numberOfErrors;
        private final double errorRate;

        private UnmodifiableKnxStatistic(final InternalKnxStatistic statistic) {
            this.numberOfBodyReceivedMap = deepCopy(statistic.numberOfBodyReceivedMap);
            this.numberOfBodySentMap = deepCopy(statistic.numberOfBodySentMap);
            this.numberOfBytesReceived = statistic.getNumberOfBytesReceived();
            this.numberOfBytesSent = statistic.getNumberOfBytesSent();
            this.numberOfBodyReceived = statistic.getNumberOfBodyReceived();
            this.numberOfBodySent = statistic.getNumberOfBodySent();
            this.numberOfErrors = statistic.getNumberOfErrors();
            this.errorRate = statistic.getErrorRate();
        }

        /**
         * Performs a deep copy of given {@link Map} to avoid an origin manipulation.
         *
         * @param map map to be deeply copied
         * @return unmodifiable deep copied map whereas the value is a {@link Long} (and not an {@link AtomicLong})
         */
        private static Map<Class<? extends Body>, Long> deepCopy(final Map<Class<? extends Body>, AtomicLong> map) {
            return new HashMap<>(map).entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().longValue()));
        }

        @Override
        public long getNumberOfBodyReceived() {
            return this.numberOfBodyReceived;
        }

        @Override
        public long getNumberOfBodyReceived(final Class<? extends Body> bodyClass) {
            return this.numberOfBodyReceivedMap.getOrDefault(bodyClass, 0L);
        }

        @Override
        public long getNumberOfBodySent() {
            return this.numberOfBodySent;
        }

        @Override
        public long getNumberOfBodySent(final Class<? extends Body> bodyClass) {
            return this.numberOfBodySentMap.getOrDefault(bodyClass, 0L);
        }

        @Override
        public long getNumberOfBytesReceived() {
            return this.numberOfBytesReceived;
        }

        @Override
        public long getNumberOfBytesSent() {
            return this.numberOfBytesSent;
        }

        @Override
        public long getNumberOfErrors() {
            return this.numberOfErrors;
        }

        @Override
        public double getErrorRate() {
            return this.errorRate;
        }
    }
}
