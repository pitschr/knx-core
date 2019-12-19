package li.pitschmann.knx.core.utils;

import li.pitschmann.knx.core.annotations.Nullable;

/**
 * Simplified Utility class for JSON to avoid JSON library dependency
 * <p>
 * For a full JSON support another JSON parser should be used (e.g. Gson)
 */
public final class Json {
    private Json() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Escapes the {@link Object} to a JSON compatible format (limited functionality!)
     *
     * @param object
     * @return
     */
    public static String toJson(final @Nullable Object object) {
        if (object == null) {
            return "null";
        } else if (object instanceof Boolean || object instanceof Number) {
            return object.toString();
        } else if (object.getClass().isArray()) {
            // primitive array not supported by this class
            // otherwise this class would be become too big
            if (object.getClass().getComponentType().isPrimitive()) {
                throw new UnsupportedOperationException("Primitive Array is not supported using this class -> please use Gson!");
            }

            final var sb = new StringBuilder();
            sb.append('[');
            for (final Object obj : (Object[]) object) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                sb.append(toJson(obj));
            }
            sb.append(']');
            return sb.toString();
        } else {
            return "\"" + escapeJson(String.valueOf(object)) + "\"";
        }
    }

    /**
     * Escapes the {@link String} to a JSON compatible format
     *
     * @param string
     * @return JSON compatible string representation
     */
    public static String escapeJson(final @Nullable String string) {
        if (string == null) {
            return "";
        }

        final var sb = new StringBuilder(string.length() * 2);
        for (final var ch : string.toCharArray()) {
            switch (ch) {
                case '"':  // --> \\"
                case '\\': // --> \\\\
                case '/':  // --> \\/
                    sb.append("\\").append(ch);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    // Reference: https://www.unicode.org/versions/Unicode12.0.0/ch23.pdf (23.1 Control Codes)
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F')) {
                        final var hexChar = Integer.toHexString(ch).toUpperCase();
                        sb.append("\\u");
                        for (var i = 0; i < 4 - hexChar.length(); i++) {
                            sb.append('0');
                        }
                        sb.append(hexChar);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

}
