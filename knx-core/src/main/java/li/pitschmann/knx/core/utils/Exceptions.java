package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.annotations.Nullable;

import java.util.Arrays;

public final class Exceptions {

    private Exceptions() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Converts the error message which is in printf-style and arguments
     * into a formatted string representation. It uses the {@code {}} as
     * placeholder similar SLF4J.
     * <p>
     * Example:
     * <pre>
     *     toErrorMessage(bool, "The value '{}' should should be between {} and {}.", valueName, 1, 10);
     *     toErrorMessage(bool, "The value '{}' should should be between {} and {}.", valueName, 1, 10);
     * </pre>
     *
     * @param errorMessage error messages (may be in printf-style)
     * @param args         arguments
     * @return formatted error message
     */
    public static String toErrorMessage(final String errorMessage, final @Nullable Object... args) {
        final var occurrences = (errorMessage.length() - errorMessage.replace("{}", "").length()) / 2;
        if (args != null && args.length > 0) {
            if (occurrences == args.length) {
                // error message is well formatted
                return formatErrorMessage(errorMessage, args);
            } else if (occurrences == args.length - 1) {
                // one arg more than occurrence -> check if last argument is Throwable
                // it won't be considered for error message
                if (args[args.length - 1] instanceof Throwable) {
                    // all good!
                    final var newArgs = new Object[args.length - 1];
                    System.arraycopy(args, 0, newArgs, 0, newArgs.length);
                    return formatErrorMessage(errorMessage, newArgs);
                }
            }

            throw new IllegalArgumentException(
                    String.format("Difference detected between error message and number of arguments " +
                            "[errorMessage: %s, args: %s]", errorMessage, Arrays.toString(args))
            );

        }
        // args is null or empty
        else if (occurrences > 0) {
            return formatErrorMessage(errorMessage, new Object[occurrences]);
        } else {
            return errorMessage;
        }

    }

    /**
     * Returns if the throwable is present in last index of {@code args} array.
     *
     * @param args array of arguments that may contain the {@link Throwable} at the end
     * @return An instance of {@link Throwable} if found, otherwise {@code null}
     */
    @Nullable
    public static Throwable getThrowableIfPresent(final Object[] args) {
        if (args != null && args.length > 0) {
            if (args[args.length - 1] instanceof Throwable) {
                return (Throwable) args[args.length - 1];
            }
        }
        return null;
    }

    /**
     * Converts and return an array of human-friendly argument for error message
     *
     * @param args array of arguments
     * @return array of formatted arguments
     */
    private static String formatErrorMessage(final String errorMessage, final Object[] args) {
        final Object[] tmpArgs = new Object[args.length];
        for (var i = 0; i < args.length; i++) {
            tmpArgs[i] = formatArgument(args[i]);
        }

        return String.format(errorMessage.replace("{}", "%s"), tmpArgs);
    }

    /**
     * Converts and return to a human-friendly argument for error message
     *
     * @param arg argument
     * @return formatted argument
     */
    private static Object formatArgument(final @Nullable Object arg) {
        if (arg == null) {
            return "<null>";
        }

        if (arg instanceof byte[]) {
            return ByteFormatter.formatHexAsString((byte[]) arg);
        } else if (arg.getClass().isArray()) {
            return Arrays.toString((Object[]) arg);
        } else {
            return arg;
        }
    }
}
