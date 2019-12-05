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

package li.pitschmann.knx.core.plugin.api;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import li.pitschmann.knx.core.body.address.GroupAddress;
import li.pitschmann.knx.core.body.cemi.APCI;
import li.pitschmann.knx.core.communication.DefaultKnxClient;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.communication.KnxStatistic;
import li.pitschmann.knx.core.communication.KnxStatusData;
import li.pitschmann.knx.core.communication.KnxStatusPool;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.config.ConfigValue;
import li.pitschmann.knx.core.datapoint.value.DataPointValue;
import li.pitschmann.knx.core.knxproj.XmlGroupAddress;
import li.pitschmann.knx.core.knxproj.XmlGroupRange;
import li.pitschmann.knx.core.knxproj.XmlProject;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.Mockito;
import ro.pippo.controller.Controller;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.core.Messages;
import ro.pippo.core.ParameterValue;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.Request;
import ro.pippo.core.Response;
import ro.pippo.core.route.DefaultRouter;
import ro.pippo.core.route.RouteContext;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Extension to test the controller directly without starting up the web service.
 *
 * @author PITSCHR
 */
public final class ControllerTestExtension
        implements ParameterResolver {
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
     * Creates a new instance of {@link Controller}
     *
     * @param annotation
     * @param <T>
     * @return new instance of {@link Controller}
     */
    protected final <T extends Controller> T newController(final ControllerTest annotation) {
        // Create XML Project
        final XmlProject xmlProjectMock;
        if (!Strings.isNullOrEmpty(annotation.projectPath())) {
            final var xmlProjectPath = Paths.get(annotation.projectPath());
            xmlProjectMock = spy(XmlProject.parse(xmlProjectPath));
        } else {
            xmlProjectMock = getXmlProjectMock();
        }

        try {
            // Create a new instance of controller
            @SuppressWarnings("unchecked") final T obj = ((Class<T>) annotation.value()).getDeclaredConstructor().newInstance();

            final var knxClientMock = getKnxClientMock(xmlProjectMock);

            // create guice injector
            final var injector = Guice.createInjector(new AbstractModule() {
                @Provides
                private final KnxClient providesKnxClient() {
                    return knxClientMock;
                }

                @Provides
                private final XmlProject providesXmlProject() {
                    return xmlProjectMock;
                }
            });

            // inject the members to controller
            injector.injectMembers(obj);

            // apply the route context to the controller instance as RouteContext won't
            // be injected by Pippo Framework
            final var routeContextInternal = getDefaultRouteContext();
            final var spyObject = spy(obj);
            when(spyObject.getRouteContext()).thenReturn(routeContextInternal);

            return spyObject;
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected KnxClient getKnxClientMock(final XmlProject xmlProject) {
        // create KNX Client Mock
        final var configMock = mock(Config.class);
        when(configMock.getValue(any(ConfigValue.class))).thenAnswer(i -> ((ConfigValue<?>) i.getArgument(0)).getDefaultValue());

        final var statisticMock = mock(KnxStatistic.class);
        final var statusPoolMock = mock(KnxStatusPool.class);

        final var knxClientMock = mock(DefaultKnxClient.class);
        when(knxClientMock.getConfig()).thenReturn(configMock);
        when(knxClientMock.getStatistic()).thenReturn(statisticMock);
        when(knxClientMock.getStatusPool()).thenReturn(statusPoolMock);
        when(knxClientMock.getConfig(any(ConfigValue.class))).thenCallRealMethod();
        when(knxClientMock.getConfig().getProject()).thenReturn(xmlProject);
        when(knxClientMock.readRequest(any(GroupAddress.class))).thenReturn(true);
        when(knxClientMock.writeRequest(any(GroupAddress.class), any(byte[].class))).thenReturn(true);
        when(knxClientMock.writeRequest(any(GroupAddress.class), any(DataPointValue.class))).thenReturn(true);

        // append pre-defined Group Addresses from XML Project to the KNX Client Mock
        if (Mockito.mockingDetails(xmlProject).isSpy()) {
            for (final var xmlGroupAddress : xmlProject.getGroupAddresses()) {
                final var groupAddress = GroupAddress.of(xmlGroupAddress.getAddress());
                final var groupAddressName = xmlGroupAddress.getName();

                final byte[] apciData;
                if (groupAddressName.contains("DPT")) {
                    final var apciDataAsHexStream = xmlGroupAddress.getName().substring(xmlGroupAddress.getName().lastIndexOf('(') + 1, xmlGroupAddress.getName().lastIndexOf(')'));
                    apciData = Bytes.toByteArray(apciDataAsHexStream);
                } else {
                    apciData = new byte[1];
                }

                final var knxStatusData = spy(new KnxStatusData(groupAddress, APCI.GROUP_VALUE_WRITE, apciData));
                when(knxClientMock.getStatusPool().getStatusFor(eq(groupAddress))).thenReturn(knxStatusData);
            }
        }


        return knxClientMock;
    }

    /**
     * Returns default XML project
     *
     * @return mocked XML project
     */
    protected XmlProject getXmlProjectMock() {
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
        when(xmlProject.getGroupRange(anyInt())).thenReturn(xmlMainGroupRangeMock);

        final var xmlMiddleGroupRangeMock = mock(XmlGroupRange.class);
        when(xmlProject.getGroupRange(anyInt(), anyInt())).thenReturn(xmlMiddleGroupRangeMock);

        final var xmlMainGroupRangesMock = new ArrayList<XmlGroupRange>();
        for (var i = 0; i < 3; i++) {
            xmlMainGroupRangesMock.add(mock(XmlGroupRange.class));
        }
        when(xmlProject.getMainGroupRanges()).thenReturn(xmlMainGroupRangesMock);

        final var xmlGroupRangesMock = new ArrayList<XmlGroupRange>();
        xmlGroupRangesMock.addAll(xmlMainGroupRangesMock);
        xmlGroupRangesMock.add(xmlMiddleGroupRangeMock);
        when(xmlProject.getGroupRanges()).thenReturn(xmlGroupRangesMock);

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
