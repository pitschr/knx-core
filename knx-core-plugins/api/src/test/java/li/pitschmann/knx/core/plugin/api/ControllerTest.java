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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ro.pippo.controller.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller Test Annotation to test the behavior of controller without starting up
 * the Web Framework and Mock Server
 * <p>
 * This will call the JUnit {@link ControllerTestExtension} class.
 *
 * @author PITSCHR
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Test
@ExtendWith(ControllerTestExtension.class)
public @interface ControllerTest {
    /**
     * /**
     * Class of {@link Controller} that should be called
     *
     * @return an instance of controller
     */
    Class<? extends Controller> value();

    /**
     * Returns the path of KNX project path (*.knxproj)
     *
     * @return path of project if defined, otherwise empty
     */
    String projectPath() default "";

    /**
     * Returns if the project should be mocked if project path
     * is empty.
     *
     * @return returns {@code true} if project should be mocked if project path is empty, otherwise {@code false}
     */
    boolean mockIfProjectPathIsEmpty() default true;
}
