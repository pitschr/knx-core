/*
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

import li.pitschmann.knx.core.annotations.Nullable;

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
    public static <T> T checkNonNull(final @Nullable T obj) {
        return Objects.requireNonNull(obj);
    }

    /**
     * Checks that the specified object reference is not {@code null} and
     * throws a customized {@link NullPointerException} if it is.
     * <p>
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
    public static <T> T checkNonNull(final @Nullable T obj, final Object arg, final @Nullable Object... args) {
        if (obj == null) {
            if (arg instanceof String) {
                throw new NullPointerException(Exceptions.toErrorMessage((String) arg, args));
            } else if (args == null || args.length == 0) {
                throw new NullPointerException(Exceptions.toErrorMessage("Null for: {}", arg));
            } else {
                throw new NullPointerException(Exceptions.toErrorMessage("Null for: {}. More Arguments: {}", arg, Arrays.toString(args)));
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
     * <p>
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
    public static void checkArgument(boolean expression, final @Nullable Object arg, final @Nullable Object... args) {
        if (!expression) {
            if (arg instanceof String) {
                throw new IllegalArgumentException(Exceptions.toErrorMessage((String) arg, args));
            } else if (args == null || args.length == 0) {
                throw new IllegalArgumentException(Exceptions.toErrorMessage("Illegal Argument for: {}", arg));
            } else {
                throw new IllegalArgumentException(Exceptions.toErrorMessage("Illegal Argument for: {}. More Arguments: {}", arg, Arrays.toString(args)));
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
     * <p>
     * Example:
     * <pre>
     *     checkState(bool, "The value '%s' should should be between %d and %d.", valueName, 1, 10);
     *     checkState(bool, "The value '{}' should should be between {} and {}.", valueName, 1, 10);
     * </pre>
     *
     * @param expression a boolean expression
     * @param arg        an object reference that should be printed in default error message,
     *                   or the error message itself
     * @param args       arguments for customized error message
     * @throws IllegalStateException if expression is {@code false}
     */
    public static void checkState(boolean expression, final @Nullable Object arg, final @Nullable Object... args) {
        if (!expression) {
            if (arg instanceof String) {
                throw new IllegalStateException(Exceptions.toErrorMessage((String) arg, args));
            } else if (args == null || args.length == 0) {
                throw new IllegalStateException(Exceptions.toErrorMessage("Illegal State for: {}", arg));
            } else {
                throw new IllegalStateException(Exceptions.toErrorMessage("Illegal State for: {}. More Arguments: {}", arg, Arrays.toString(args)));
            }
        }
    }

}
