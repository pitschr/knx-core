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

package li.pitschmann.knx.daemon.v1.controllers;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.parser.XmlProject;
import ro.pippo.controller.Controller;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.core.Messages;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.Request;
import ro.pippo.core.Response;
import ro.pippo.core.route.RouteContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Abstract Controller Test for testing purposes
 */
public abstract class AbstractControllerTest {

    /**
     * Creates a new instance of {@link Controller}
     *
     * @param controller
     * @param knxClient    the default KNX client, if {@code null} falls back to {@link #getDefaultKnxClient()}
     * @param xmlProject   the XML client, if {@code null} falls back to {@link #getDefaultXmlProject()}
     * @param routeContext the route context, if {@code null} falls back to {@link #getDefaultRouteContext()}
     * @param <T>
     * @return new instance of {@link Controller}
     */
    protected final <T extends Controller> T newController(final @Nonnull Class<T> controller,
                                                           final @Nullable DefaultKnxClient knxClient,
                                                           final @Nullable XmlProject xmlProject,
                                                           final @Nullable RouteContext routeContext) {
        try {
            // Create a new instance of controller
            final T obj = controller.getDeclaredConstructor().newInstance();

            // create guice injector
            final var injector = Guice.createInjector(new AbstractModule() {
                @Provides
                private final DefaultKnxClient providesKnxClient() {
                    return knxClient == null ? getDefaultKnxClient() : knxClient;
                }

                @Provides
                private final XmlProject providesXmlProject() {
                    return xmlProject == null ? getDefaultXmlProject() : xmlProject;
                }
            });

            // inject the members to controller
            injector.injectMembers(obj);

            // apply the route context to the controller instance as RouteContext won't
            // be injected by Pippo Framework
            final var routeContextInternal = routeContext == null ? getDefaultRouteContext() : routeContext;
            final var spyObject = spy(obj);
            when(spyObject.getRouteContext()).thenReturn(routeContextInternal);

            return spyObject;
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates a new instnace of {@link Controller} with default values from {@link #getDefaultKnxClient()},
     * {@link #getDefaultXmlProject()} and {@link #getDefaultRouteContext()}
     *
     * @param controller
     * @param <T>
     * @return new instance of {@link Controller}
     */
    protected final <T extends Controller> T newController(final @Nonnull Class<T> controller) {
        return newController(controller, null, null, null);
    }

    /**
     * Returns default KNX client
     *
     * @return mocked KNX client
     */
    protected DefaultKnxClient getDefaultKnxClient() {
        return mock(DefaultKnxClient.class);
    }

    /**
     * Returns default XML project
     *
     * @return mocked XML project
     */
    protected XmlProject getDefaultXmlProject() {
        return getXmlProject(null);
    }

    protected XmlProject getXmlProject(final @Nonnull Consumer<XmlProject> consumer) {
        final var xmlProject = mock(XmlProject.class);
        if (consumer != null) {
            consumer.accept(xmlProject);
        }
        return xmlProject;
    }

    /**
     * Returns default route context
     *
     * @return mocked {@link RouteContext}
     */
    protected RouteContext getDefaultRouteContext() {
        final var routeContext = mock(RouteContext.class);
        final var request = mock(Request.class);
        final var response = mock(Response.class);
        final var application = mock(ControllerApplication.class);
        final var messages = mock(Messages.class);
        final var settings = mock(PippoSettings.class);

        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getResponse()).thenReturn(response);
        when(routeContext.getApplication()).thenReturn(application);
        when(routeContext.getMessages()).thenReturn(messages);
        when(routeContext.getSettings()).thenReturn(settings);

        return routeContext;
    }
}
