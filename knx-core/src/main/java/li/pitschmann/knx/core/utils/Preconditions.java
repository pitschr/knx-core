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
package li.pitschmann.knx.core.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Preconditions (similar to Guava Preconditions)
 */
public final class Preconditions {
    private Preconditions() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a null-arg {@link NullPointerException} if it is.
     *
     * @param obj the object reference to check for nullity
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    @Nonnull
    public static <T> T checkNonNull(final @Nullable T obj) {
        return Objects.requireNonNull(obj);
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     * <p/>
     * Example:
     * <pre>
     *     checkNotNull(obj, "The object is null");
     *     checkNotNull(obj, "The object is null for body: {}", body);
     * </pre>
     *
     * @param obj  the object reference to check for nullity
     * @param arg  an object reference that should be printed in default error message,
     *             or the error message itself
     * @param args arguments for customized error message
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    @Nonnull
    public static <T> T checkNonNull(final @Nullable T obj, final @Nonnull Object arg, final @Nullable Object... args) {
        if (obj == null) {
            if (arg instanceof String) {
                throw new NullPointerException(toErrorMessage((String) arg, args));
            } else {
                throw new NullPointerException(toErrorMessage("Null for: {}. More Arguments: {}", arg, Arrays.toString(args)));
            }
        }
        return obj;
    }

    /**
     * Checks the truth of given {@code expression}
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks the truth of given {@code expression}. In case the expression was not meet,
     * an {@link IllegalArgumentException} with custom error message will be thrown.
     * <p/>
     * Example:
     * <pre>
     *     checkArgument(bool, "The value '%s' should should be between %d and %d.", valueName, 1, 10);
     *     checkArgument(bool, "The value '{}' should should be between {} and {}.", valueName, 1, 10);
     * </pre>
     *
     * @param expression a boolean expression
     * @param arg        an object reference that should be printed in default error message,
     *                   or the error message itself
     * @param args       arguments for customized error message
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void checkArgument(boolean expression, final @Nonnull Object arg, final @Nullable Object... args) {
        if (!expression) {
            if (arg instanceof String) {
                throw new IllegalArgumentException(toErrorMessage((String) arg, args));
            } else {
                throw new IllegalArgumentException(toErrorMessage("Illegal Argument for: {}. More Arguments: {}", arg, Arrays.toString(args)));
            }
        }
    }

    /**
     * Ensures the truth of given expression if the state was meet.
     *
     * @param expression a boolean expression
     * @throws IllegalStateException if expression is {@code false}
     */
    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures the truth of given expression if the state was meet. In case
     * the state was not meet, an {@link IllegalStateException} with custom error message
     * will be thrown.
     * <p/>
     * Example:
     * <pre>
     *     checkState(bool, "The value '%s' should should be between %d and %d.", valueName, 1, 10);
     *     checkState(bool, "The value '{}' should should be between {} and {}.", valueName, 1, 10);
     * </pre>
     *
     * @param expression a boolean expression
     * @param obj        an object reference that should be printed in default error message,
     *                   or the error message itself
     * @param args       arguments for customized error message
     * @throws IllegalStateException if expression is {@code false}
     */
    public static void checkState(boolean expression, final @Nullable Object obj, final @Nullable Object... args) {
        if (!expression) {
            if (obj instanceof String) {
                throw new IllegalStateException(toErrorMessage((String) obj, args));
            } else {
                throw new IllegalStateException(toErrorMessage("Illegal State for: {}. More Arguments: {}", obj, Arrays.toString(args)));
            }
        }
    }

    /**
     * Converts the error message which is in printf-style and arguments into a formatted string representation.
     * It follows the same rule like defined in {@link java.util.Formatter}, plus {@code {}} is considered as
     * synonym for {@code %s} to allow same error message like SLF4J
     * <p/>
     * Example:
     * <pre>
     *     toErrorMessage(bool, "The value '%s' should should be between %d and %d.", valueName, 1, 10);
     *     toErrorMessage(bool, "The value '{}' should should be between {} and {}.", valueName, 1, 10);
     * </pre>
     *
     * @param errorMessage error messages (may be in printf-style)
     * @param args         arguments
     * @return formatted error message
     */
    public static String toErrorMessage(final @Nonnull String errorMessage, final @Nullable Object... args) {
        if (errorMessage.contains("{}") || errorMessage.contains("%")) {
            return String.format(errorMessage.replaceAll("\\{\\}", "%s"), toErrorMessageArguments(args));
        } else if (args.length > 0) {
            return String.format("%s (Arguments: %s)", errorMessage, Arrays.toString(toErrorMessageArguments(args)));
        } else {
            return errorMessage;
        }
    }

    /**
     * Converts and return an array of human-friendly argument for error message
     *
     * @param args array of arguments
     * @return array of formatted arguments
     */
    private static Object[] toErrorMessageArguments(final @Nonnull Object[] args) {
        if (args == null) {
            return new Object[]{toErrorMessageArgument(null)};
        }

        final Object[] tmpArgs = new Object[args.length];
        for (var i = 0; i < args.length; i++) {
            tmpArgs[i] = toErrorMessageArgument(args[i]);
        }
        return tmpArgs;
    }

    /**
     * Converts and return to a human-friendly argument for error message
     *
     * @param arg argument
     * @return formatted argument
     */
    private static Object toErrorMessageArgument(final @Nullable Object arg) {
        if (arg == null) {
            return "<null>";
        }

        if (arg instanceof byte[]) {
            return ByteFormatter.formatHexAsString((byte[]) arg);
        } else {
            return arg;
        }
    }
}
