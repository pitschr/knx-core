package li.pitschmann.knx.utils;

import javax.annotation.Nonnull;
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

    /**
     * Creates a {@link ToStringHelper} for creating string representations.
     * Similar to Guava's {@code ToStringHelper} but simpler.
     *
     * @param obj
     * @return a new instance of {@link ToStringHelper}
     */
    public static ToStringHelper toStringHelper(final @Nonnull Object obj) {
        return new ToStringHelper(obj.getClass().getSimpleName());
    }

    public static class ToStringHelper {
        private final StringBuilder sb = new StringBuilder(200);
        private final String className;

        private ToStringHelper(final @Nonnull String name) {
            this.className = Preconditions.checkNonNull(name);
        }

        public ToStringHelper add(final @Nonnull String name, @Nullable Object value) {
            Preconditions.checkNonNull(name);
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(name).append('=').append(value);
            return this;
        }

        @Override
        public String toString() {
            return className + '{' + sb.toString() + '}';
        }
    }
}
