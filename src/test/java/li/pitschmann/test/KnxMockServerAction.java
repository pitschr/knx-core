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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Action behavior for the KNX Mock Server
 *
 * @author PITSCHR
 */
public interface KnxMockServerAction {
    /**
     * Parses the command and returns the correct {@link KnxMockServerAction} instance
     *
     * @param command to be parsed
     * @return an instance of {@link KnxMockServerAction}
     */
    static List<KnxMockServerAction> parse(final String command) {
        // no action?
        if (Strings.isNullOrEmpty(command) || "NO_ACTION".equalsIgnoreCase(command)) {
            return Collections.singletonList(KnxMockServerNoAction.INSTANCE);
        }
        // action is "WAIT="
        // Example #1: WAIT=NEXT for waiting for next packet
        // Example #2: WAIT=500 for waiting 500 ms
        // Example #3: WAIT=DISCONNECT_REQUEST for waiting receiving DISCONNECT_REQUEST frame
        else if (command.toUpperCase().startsWith("WAIT=")) {
            return Collections.singletonList(KnxMockServerWaitAction.of(command));
        }
        // action is "REPEAT="
        // Example #1: REPEAT=5{...} for repeating 5-times
        else if (command.toUpperCase().startsWith("REPEAT=")) {
            // get number of repeats
            int numberOfRepeats = Integer.parseInt(command.substring(7, command.indexOf("{")));
            // get repeat commands (within "{" and "}")
            final String innerCommand = command.substring(command.indexOf("{") + 1, command.lastIndexOf("}"));
            final List<KnxMockServerAction> innerCommands = Lists.newLinkedList();
            Arrays.stream(innerCommand.split(",")).forEach(c -> innerCommands.addAll(parse(c)));
            // add the inner commands N-times
            final List<KnxMockServerAction> innerKnxActions = Lists.newLinkedList();
            for (int i = 0; i < numberOfRepeats; i++) {
                innerKnxActions.addAll(innerCommands);
            }
            return innerKnxActions;
        } else if (command.contains(",")) {
            if (command.contains("{")) {
                // bit tricky because "," within "{" and "}" should not be splitted
                // workaround is just to use another character
                var sb = new StringBuilder(command.length());
                boolean withinBracket = false;
                for (char c : command.toCharArray()) {
                    var newChar = c;
                    if (c == '{') {
                        withinBracket = true;
                    } else if (c == '}') {
                        withinBracket = false;
                    } else if (c == ',' && withinBracket) {
                        newChar = '|';
                    }
                    sb.append(newChar);
                }
                return Arrays.stream(sb.toString().split(",")).flatMap(s -> parse(s.replaceAll("\\|", ",")).stream()).collect(Collectors.toList());
            } else {
                return Arrays.stream(command.split(",")).flatMap(s -> parse(s).stream()).collect(Collectors.toList());
            }
        }
        // else: assume it is a send action
        else {
            return Collections.singletonList(KnxMockServerSendAction.of(command));
        }
    }
}
