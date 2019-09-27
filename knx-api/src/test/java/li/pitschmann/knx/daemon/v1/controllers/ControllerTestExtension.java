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

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.communication.KnxStatistic;
import li.pitschmann.knx.link.communication.KnxStatusPool;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.knx.parser.KnxprojParser;
import li.pitschmann.knx.parser.XmlGroupAddress;
import li.pitschmann.knx.parser.XmlGroupRange;
import li.pitschmann.knx.parser.XmlProject;
import li.pitschmann.knx.test.MockDaemonTest;
import li.pitschmann.knx.test.MockHttpDaemon;
import li.pitschmann.knx.test.MockServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Controller;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.core.Messages;
import ro.pippo.core.ParameterValue;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.Request;
import ro.pippo.core.Response;
import ro.pippo.core.route.DefaultRouter;
import ro.pippo.core.route.RouteContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Extension to start/stop the {@link MockHttpDaemon} (and {@link MockServer} indirectly).
 * <p/>
 * It will be invoked using {@link MockDaemonTest} annotation.
 *
 * @author PITSCHR
 */
public final class ControllerTestExtension
        implements ParameterResolver {
    private static final Logger log = LoggerFactory.getLogger(ControllerTestExtension.class);

    @Override
    public Controller resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ControllerTest.class).get();
        return newController(annotation);
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return paramContext.getParameter().getType().isAssignableFrom(Controller.class);
    }


    /**
     * Creates a new instance of {@link AbstractController}
     *
     * @param annotation
     * @param <T>
     * @return new instance of {@link AbstractController}
     */
    protected final <T extends AbstractController> T newController(final @Nonnull ControllerTest annotation) {
        // Load XML Project
        final XmlProject xmlProject;
        if (!Strings.isNullOrEmpty(annotation.projectPath())) {
            final var xmlProjectPath = Paths.get(annotation.projectPath());
            xmlProject = KnxprojParser.parse(xmlProjectPath);
        } else {
            xmlProject = getDefaultXmlProject();
        }

        try {
            // Create a new instance of controller
            @SuppressWarnings("unchecked") final T obj = ((Class<T>) annotation.value()).getDeclaredConstructor().newInstance();

            final var injectKnxClient = getDefaultKnxClient();

            // create guice injector
            final var injector = Guice.createInjector(new AbstractModule() {
                @Provides
                private final DefaultKnxClient providesKnxClient() {
                    return injectKnxClient;
                }

                @Provides
                private final XmlProject providesXmlProject() {
                    return xmlProject;
                }
            });

            // inject the members to controller
            injector.injectMembers(obj);

            // apply the route context to the controller instance as RouteContext won't
            // be injected by Pippo Framework
            final var routeContextInternal = getDefaultRouteContext();
            final var spyObject = spy(obj);

            when(spyObject.getRouteContext()).thenReturn(routeContextInternal);
            doReturn(true).when(spyObject).containsExpand(anyString());

            return spyObject;
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns default KNX client
     *
     * @return mocked KNX client
     */
    protected DefaultKnxClient getDefaultKnxClient() {
        return getKnxClient(null);
    }

    /**
     * Returns the KNX client with applied consumer
     *
     * @param consumer
     * @return mocked XML client
     */
    @SuppressWarnings("unchecked")
    protected DefaultKnxClient getKnxClient(final @Nullable Consumer<DefaultKnxClient> consumer) {
        final var knxClient = mock(DefaultKnxClient.class);

        final var statistic = mock(KnxStatistic.class);
        final var statusPool = mock(KnxStatusPool.class);
        when(knxClient.getStatistic()).thenReturn(statistic);
        when(knxClient.getStatusPool()).thenReturn(statusPool);

        when(knxClient.readRequest(any(GroupAddress.class))).thenReturn(true);
        when(knxClient.writeRequest(any(GroupAddress.class), any(byte[].class))).thenReturn(true);
        when(knxClient.writeRequest(any(GroupAddress.class), any(DataPointValue.class))).thenReturn(true);

        if (consumer != null) {
            consumer.accept(knxClient);
        }

        return knxClient;
    }

    /**
     * Returns default XML project
     *
     * @return mocked XML project
     */
    protected XmlProject getDefaultXmlProject() {
        return getXmlProject(null);
    }

    /**
     * Returns the XML project with applied consumer
     *
     * @param consumer
     * @return mocked XML project
     */
    protected XmlProject getXmlProject(final @Nullable Consumer<XmlProject> consumer) {
        final var xmlProject = mock(XmlProject.class);

        // XML Group Addresses
        final var xmlGroupAddressMock = mock(XmlGroupAddress.class);
        when(xmlProject.getGroupAddress(any(GroupAddress.class))).thenReturn(xmlGroupAddressMock);

        final var xmlGroupAddressesMock = new ArrayList<XmlGroupAddress>();
        for (var i = 0; i < 10; i++) {
            xmlGroupAddressesMock.add(mock(XmlGroupAddress.class));
        }
        when(xmlProject.getGroupAddresses()).thenReturn(xmlGroupAddressesMock);

        // XML Group Ranges
        final var xmlMainGroupRangeMock = mock(XmlGroupRange.class);
        when(xmlProject.getMainGroup(anyInt())).thenReturn(xmlMainGroupRangeMock);

        final var xmlMiddleGroupRangeMock = mock(XmlGroupRange.class);
        when(xmlProject.getMiddleGroup(anyInt(), anyInt())).thenReturn(xmlMiddleGroupRangeMock);

        final var xmlMainGroupRangesMock = new ArrayList<XmlGroupRange>();
        for (var i = 0; i < 3; i++) {
            xmlMainGroupRangesMock.add(mock(XmlGroupRange.class));
        }
        when(xmlProject.getMainGroups()).thenReturn(xmlMainGroupRangesMock);

        final var xmlGroupRangesMock = new ArrayList<XmlGroupRange>();
        xmlGroupRangesMock.addAll(xmlMainGroupRangesMock);
        xmlGroupRangesMock.add(xmlMiddleGroupRangeMock);
        when(xmlProject.getGroupRanges()).thenReturn(xmlGroupRangesMock);

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
        return getRouteContext(null);
    }

    /**
     * Returns the route context with applied customer
     *
     * @param consumer
     * @return mocked {@link RouteContext}
     */
    protected RouteContext getRouteContext(final @Nullable Consumer<RouteContext> consumer) {
        final var routeContext = mock(RouteContext.class);
        final var application = mock(ControllerApplication.class);
        final var messages = mock(Messages.class);
        final var settings = mock(PippoSettings.class);
        final var httpServletResponse = mock(HttpServletResponse.class);
        final var request = mock(Request.class);

        when(application.getRouter()).thenReturn(new DefaultRouter());

        final var response = spy(new Response(httpServletResponse, application));

        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getResponse()).thenReturn(response);
        when(routeContext.getApplication()).thenReturn(application);
        when(routeContext.getMessages()).thenReturn(messages);
        when(routeContext.getSettings()).thenReturn(settings);

        when(request.getParameter(anyString())).thenReturn(new ParameterValue());

        if (consumer != null) {
            consumer.accept(routeContext);
        }

        return routeContext;
    }
}
