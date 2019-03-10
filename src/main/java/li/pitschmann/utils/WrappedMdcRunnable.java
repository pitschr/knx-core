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

/**
 * Wrapper for {@link Runnable} to use Mapped Diagnostic Context (MDC) for simultaneously executions.
 *
 * @author PITSCHR
 */
public final class WrappedMdcRunnable implements Runnable {
    private final Map<String, String> context = MDC.getCopyOfContextMap();
    private final Runnable runnable;

    public WrappedMdcRunnable(final Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public final void run() {
        final var originalContext = MDC.getCopyOfContextMap();
        MDC.setContextMap(this.context);
        try {
            runnable.run();
        } finally {
            if (originalContext != null) {
                MDC.setContextMap(originalContext);
            }
        }
    }
}
