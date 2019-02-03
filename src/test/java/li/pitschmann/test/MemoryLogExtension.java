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

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.*;
import org.slf4j.*;

import javax.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Extension for add / detach {@link MemoryAppender} for given classes
 *
 * @author PITSCHR
 */
public class MemoryLogExtension
        implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger LOG = LoggerFactory.getLogger(MemoryLogExtension.class);
    private static final Map<ExtensionContext, MemoryAppender> memoryAppenders = new ConcurrentHashMap<>();

    @Override
    public Object resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return memoryAppenders.get(context);
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().equals(MemoryAppender.class);
    }

    /**
     * Add {@link MemoryAppender} to the logger before test method execution
     *
     * @param context
     */
    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {
        var classes = getMemoryLogAnnotation(context).value();
        LOG.debug("[{}] MemoryAppender added for classes: {}", context.getRequiredTestMethod(), classes);
        var memoryAppender = new MemoryAppender();
        getLoggers(classes).forEach(memoryAppender::addForLogger);
        memoryAppenders.put(context, memoryAppender);
    }

    /**
     * Detach {@link MemoryAppender} from logger after test method execution
     *
     * @param context
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        var classes = getMemoryLogAnnotation(context).value();
        LOG.debug("[{}] MemoryAppender detached for classes: {}", context.getRequiredTestMethod(), classes);
        var memoryAppender = memoryAppenders.remove(context);
        memoryAppender.reset();
        getLoggers(classes).forEach(memoryAppender::detachForLogger);
    }

    /**
     * Returns the {@link MemoryLog} annotation
     *
     * @param context context of current test method
     * @return The {@link MemoryLog} annotation
     */
    @Nonnull
    private MemoryLog getMemoryLogAnnotation(final ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), MemoryLog.class).get();
    }

    /**
     * Get immutable list of loggers for given classes
     *
     * @param classes classes provided to retrieve the Loggers for those classes
     * @return immutable list of {@link Logger}
     */
    @Nonnull
    private List<Logger> getLoggers(final Class[] classes) {
        return Arrays.stream(classes).map(LoggerFactory::getLogger).collect(Collectors.toUnmodifiableList());
    }
}