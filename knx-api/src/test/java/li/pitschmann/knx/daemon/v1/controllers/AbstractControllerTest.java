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

import com.google.gson.JsonParser;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import li.pitschmann.knx.daemon.gson.DaemonGsonEngine;
import li.pitschmann.knx.link.body.TunnelingAckBody;
import li.pitschmann.knx.link.body.address.GroupAddress;
import li.pitschmann.knx.link.communication.DefaultKnxClient;
import li.pitschmann.knx.link.communication.KnxStatistic;
import li.pitschmann.knx.link.communication.KnxStatusPool;
import li.pitschmann.knx.link.datapoint.value.DataPointValue;
import li.pitschmann.knx.parser.XmlGroupAddress;
import li.pitschmann.knx.parser.XmlProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.ControllerApplication;
import ro.pippo.core.Messages;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.Request;
import ro.pippo.core.Response;
import ro.pippo.core.route.DefaultRouter;
import ro.pippo.core.route.RouteContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Abstract Controller Test for testing purposes
 */
public abstract class AbstractControllerTest {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Creates a new instance of {@link AbstractController}
     *
     * @param controller
     * @param knxClient    the default KNX client, if {@code null} falls back to {@link #getDefaultKnxClient()}
     * @param xmlProject   the XML client, if {@code null} falls back to {@link #getDefaultXmlProject()}
     * @param routeContext the route context, if {@code null} falls back to {@link #getDefaultRouteContext()}
     * @param <T>
     * @return new instance of {@link AbstractController}
     */
    protected final <T extends AbstractController> T newController(final @Nonnull Class<T> controller,
                                                                   final @Nullable DefaultKnxClient knxClient,
                                                                   final @Nullable XmlProject xmlProject,
                                                                   final @Nullable RouteContext routeContext) {
        try {
            // Create a new instance of controller
            final T obj = controller.getDeclaredConstructor().newInstance();

            final var injectKnxClient = knxClient == null ? getDefaultKnxClient() : knxClient;
            final var injectXmlProject = xmlProject == null ? getDefaultXmlProject() : xmlProject;

            // create guice injector
            final var injector = Guice.createInjector(new AbstractModule() {
                @Provides
                private final DefaultKnxClient providesKnxClient() {
                    return injectKnxClient;
                }

                @Provides
                private final XmlProject providesXmlProject() {
                    return injectXmlProject;
                }
            });

            // inject the members to controller
            injector.injectMembers(obj);

            // apply the route context to the controller instance as RouteContext won't
            // be injected by Pippo Framework
            final var routeContextInternal = routeContext == null ? getDefaultRouteContext() : routeContext;
            final var spyObject = spy(obj);

            when(spyObject.getRouteContext()).thenReturn(routeContextInternal);
            doReturn(true).when(spyObject).containsExpand(anyString());

            return spyObject;
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates a new instance of {@link AbstractController} with default values
     * from {@link #getDefaultKnxClient()}, {@link #getDefaultXmlProject()} and
     * {@link #getDefaultRouteContext()}
     *
     * @param controller
     * @param <T>
     * @return new instance of {@link AbstractController}
     */
    protected final <T extends AbstractController> T newController(final @Nonnull Class<T> controller) {
        return newController(controller, null, null, null);
    }

    /**
     * Returns the given response object as JSON
     *
     * @param response
     * @return json
     */
    protected final String asJson(final @Nonnull Object response) {
        return DaemonGsonEngine.INSTANCE.toString(response);
    }

    /**
     * Randomize a {@link GroupAddress}. The group address should not matter in the unit testing.
     *
     * @return
     */
    protected final GroupAddress randomGroupAddress() {
        // a range between between 1 and 65535
        int randomInt = new Random().nextInt(65534) + 1;
        return GroupAddress.of(randomInt);
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

        final CompletableFuture<TunnelingAckBody> readCompletableFuture = mock(CompletableFuture.class);
        final CompletableFuture<TunnelingAckBody> writeCompletableFuture = mock(CompletableFuture.class);

        final var readAckBody = mock(TunnelingAckBody.class);
        final var writeAckBody = mock(TunnelingAckBody.class);
        try {
            when(readCompletableFuture.get()).thenReturn(readAckBody);
            when(writeCompletableFuture.get()).thenReturn(writeAckBody);
        } catch (final Exception ex) {
            fail(ex);
        }

        when(knxClient.readRequest(any(GroupAddress.class))).thenReturn(readCompletableFuture);
        when(knxClient.writeRequest(any(GroupAddress.class), any(byte[].class))).thenReturn(writeCompletableFuture);
        when(knxClient.writeRequest(any(GroupAddress.class), any(DataPointValue.class))).thenReturn(writeCompletableFuture);

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

        final var xmlGroupAddress = mock(XmlGroupAddress.class);
        when(xmlGroupAddress.getDatapointType()).thenReturn("1.001");
        when(xmlGroupAddress.getName()).thenReturn("DPT1.Switch Name");
        when(xmlGroupAddress.getDescription()).thenReturn("DPT1.Switch Description");
        when(xmlProject.getGroupAddress(any(GroupAddress.class))).thenReturn(xmlGroupAddress);

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

        if (consumer != null) {
            consumer.accept(routeContext);
        }

        return routeContext;
    }

    /**
     * Reads the given test resource on {@code filePath} and returns the content
     * as an UTF-8 compliant String representation.
     *
     * @param filePath
     * @return content (UTF-8 decoded)
     */
    protected final String readJsonFile(final String filePath) {
        log.debug("File: {}", filePath);
        try {
            final var path = Paths.get(AbstractControllerTest.class.getResource(filePath).toURI());
            if (Files.isReadable(path)) {
                final var content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                log.debug("Content of file '{}': {}", filePath, content);
                // minify json
                return new JsonParser().parse(content).toString();
            }
            throw new AssertionError("File not found or cannot be read: " + filePath);
        } catch (final URISyntaxException | IOException ex) {
            fail(ex);
            throw new AssertionError(ex);
        }
    }
}
