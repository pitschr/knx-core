/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.address.GroupAddress;
import li.pitschmann.knx.core.cemi.APCI;
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
import li.pitschmann.knx.core.plugin.api.v1.controllers.AbstractController;
import li.pitschmann.knx.core.utils.Bytes;
import li.pitschmann.knx.core.utils.Strings;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.mockito.Mockito;

import java.nio.file.Paths;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    public AbstractController resolveParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        final var annotation = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ControllerTest.class).get();
        return newController(annotation);
    }

    @Override
    public boolean supportsParameter(final ParameterContext paramContext, final ExtensionContext context) throws ParameterResolutionException {
        return AbstractController.class.isAssignableFrom(paramContext.getParameter().getType());
    }

    /**
     * Creates a new instance of {@link AbstractController}
     *
     * @param annotation annotation of {@link ControllerTest} that contains test related configuration
     * @param <T>        an instance that extends {@link AbstractController}
     * @return new instance of controller that extends {@link AbstractController}
     */
    private <T extends AbstractController> T newController(final ControllerTest annotation) {
        // Create XML Project
        final XmlProject xmlProjectMock;
        if (!Strings.isNullOrEmpty(annotation.projectPath())) {
            final var xmlProjectPath = Paths.get(annotation.projectPath());
            xmlProjectMock = spy(XmlProject.of(xmlProjectPath));
        } else if (annotation.mockIfProjectPathIsEmpty()) {
            xmlProjectMock = getXmlProjectMock();
        } else {
            xmlProjectMock = null;
        }

        try {
            final var knxClientMock = getKnxClientMock(xmlProjectMock);

            // Create a new instance of controller
            @SuppressWarnings("unchecked") final T obj = ((Class<T>) annotation.value())
                    .getDeclaredConstructor(KnxClient.class)
                    .newInstance(knxClientMock);

            return spy(obj);
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns a mocked {@link KnxClient}
     *
     * @param xmlProject the {@link XmlProject} that should be considered by {@link KnxClient}
     * @return mocked {@link KnxClient}
     */
    @SuppressWarnings("unchecked")
    private KnxClient getKnxClientMock(final XmlProject xmlProject) {
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
        when(knxClientMock.writeRequest(any(GroupAddress.class), any(DataPointValue.class))).thenReturn(true);

        // append pre-defined Group Addresses from XML Project to the KNX Client Mock
        if (Mockito.mockingDetails(xmlProject).isSpy()) {
            for (final var xmlGroupAddress : xmlProject.getGroupAddresses()) {
                final var groupAddress = GroupAddress.of(xmlGroupAddress.getAddress());
                final var groupAddressName = xmlGroupAddress.getName();

                final byte[] data;
                if (groupAddressName.contains("DPT")) {
                    final var dataAsHexStream = xmlGroupAddress.getName().substring(xmlGroupAddress.getName().lastIndexOf('(') + 1, xmlGroupAddress.getName().lastIndexOf(')'));
                    data = Bytes.toByteArray(dataAsHexStream);
                } else {
                    data = new byte[1];
                }

                final var knxStatusData = spy(new KnxStatusData(groupAddress, APCI.GROUP_VALUE_WRITE, data));
                when(knxClientMock.getStatusPool().getStatusFor(eq(groupAddress))).thenReturn(knxStatusData);
            }
        }

        return knxClientMock;
    }

    /**
     * Creates a mock {@link XmlProject}
     *
     * @return mocked {@link XmlProject}
     */
    private XmlProject getXmlProjectMock() {
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
}
