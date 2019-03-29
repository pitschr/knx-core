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

package li.pitschmann.knx.server;

import li.pitschmann.knx.server.strategy.ResponseStrategy;
import li.pitschmann.knx.server.strategy.impl.DefaultConnectStrategy;
import li.pitschmann.knx.server.strategy.impl.DefaultConnectionStateStrategy;
import li.pitschmann.knx.server.strategy.impl.DefaultDescriptionStrategy;
import li.pitschmann.knx.server.strategy.impl.DefaultDisconnectStrategy;
import li.pitschmann.knx.server.strategy.impl.DefaultTunnelingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * KNX Test Annotation to define the strategy and behavior with {@link MockServer}.
 * <p/>
 * This will call the JUnit {@link MockServerTestExtension} class.
 *
 * @author PITSCHR
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@ExtendWith(MockServerTestExtension.class)
public @interface MockServerTest {
    /**
     * Description strategy
     * <p/>
     * Default: {@link DefaultDescriptionStrategy}
     *
     * @return array with description strategies
     */
    Class<? extends ResponseStrategy>[] descriptionStrategy() default {DefaultDescriptionStrategy.class};

    /**
     * Connect strategy
     * <p/>
     * Default: {@link DefaultConnectStrategy}
     *
     * @return array with connect strategies
     */
    Class<? extends ResponseStrategy>[] connectStrategy() default {DefaultConnectStrategy.class};

    /**
     * Connection State strategy
     * <p/>
     * Default: {@link DefaultConnectionStateStrategy}
     *
     * @return array with connection state strategies
     */
    Class<? extends ResponseStrategy>[] connectionStateStrategy() default {DefaultConnectionStateStrategy.class};

    /**
     * Disconnect strategy
     * <p/>
     * Default: {@link DefaultConnectStrategy}
     *
     * @return array with disconnect strategies
     */
    Class<? extends ResponseStrategy>[] disconnectStrategy() default {DefaultDisconnectStrategy.class};

    /**
     * Tunneling strategy
     * <p/>
     * Default: {@link DefaultTunnelingStrategy}
     *
     * @return array with tunneling strategies
     */
    Class<? extends ResponseStrategy>[] tunnelingStrategy() default {DefaultTunnelingStrategy.class};

    /**
     * Returns the path of KNX Project that is being used for communication strategy
     *
     * @return
     */
    String projectPath() default "";

    /**
     * Trigger when disconnect request frame should be sent by KNX mock server
     * <p/>
     * <ul>
     * <li>If empty, no trigger is setup and no action taken by KNX mock server.</li>
     * <li>If defined, the command will be parsed and disconnect request
     * will be sent by KNX mock server.<br>
     * Example: {@code "wait-request(1)=CONNECTION_STATE_REQUEST", "wait=100"}<br>
     * (Given example, the KNX mock server will wait for 1st CONNECTION_STATE_REQUEST frame,
     * wait 100 milliseconds and then send disconnect frame)
     * </li>
     * </ul>
     * <p>
     * Default: No disconnect trigger from KNX mock server, awaiting disconnect from client
     *
     * @return trigger command sequence
     */
    String[] disconnectTrigger() default {};

    /**
     * Defines list of requests to be sent by KNX mock server (e.g. tunneling requests)
     * <p/>
     * <ul>
     * <li>If empty, no request is setup and no action taken by KNX mock server.</li>
     * <li>If defined, the commands are parsed and request packets are sent by KNX mock server<br>
     * Example: {@code "wait-request(2)=TUNNELING_REQUEST", "wait=200", "cemi=0x01..", "wait=5000",
     * "repeat(10){cemi=0x02..}"}<br>
     * (Given example, the KNX mock server will wait for 2nd TUNNELING request frame from KNX client,
     * wait 200 milliseconds, send tunneling request (CEMI=0x01..), wait 5000 milliseconds,
     * and then send tunneling request (CEMI=0x02..) 10-times.
     * </li>
     * </ul>
     * Default: no requests
     *
     * @return request command sequence
     */
    String[] requests() default {};
}
