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

package li.pitschmann.utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Wrapper for {@link Supplier} to use Mapped Diagnostic Context (MDC) for simultaneously executions.
 *
 * @param <V>
 * @author PITSCHR
 */
public final class WrappedMdcSupplier<V> implements Supplier<V> {
    private final Map<String, String> context = MDC.getCopyOfContextMap();
    private final Supplier<V> supplier;

    public WrappedMdcSupplier(final Supplier<V> supplier) {
        this.supplier = supplier;
    }

    @Override
    public final V get() {
        final var originalContext = MDC.getCopyOfContextMap();
        MDC.setContextMap(this.context);
        try {
            return supplier.get();
        } finally {
            if (originalContext != null) {
                MDC.setContextMap(originalContext);
            }
        }
    }
}
