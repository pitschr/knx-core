package li.pitschmann.utils;

import javax.annotation.Nullable;

/**
 * Utility class for strings
 */
public final class Strings {
    private Strings() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Returns {@code true} if the given {@link String} is null or empty.
     *
     * @param string
     * @return {@code true} if the string is null or is empty
     */
    public static boolean isNullOrEmpty(final @Nullable String string) {
        return string == null || string.isEmpty();
    }
}
