package li.pitschmann.knx.core.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Utility class for Maps
 */
public final class Maps {
    private static final int INT_MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private Maps() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Creates a new {@link HashMap} with expected size.
     * <p/>
     * Similar to Guava's Maps#newHashMapWithExpectedSize(int)
     *
     * @param expectedSize
     * @param <K>
     * @param <V>
     * @return new {@link HashMap} initialized with expected size
     */
    public static <K, V> HashMap<K, V> newHashMap(final int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    /**
     * Creates a new {@link LinkedHashMap} with expected size.
     * <p/>
     * Similar to Guava's Maps#newLinkedHashMapWithExpectedSize(int)
     *
     * @param expectedSize
     * @param <K>
     * @param <V>
     * @return new {@link LinkedHashMap} initialized with expected size
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int expectedSize) {
        return new LinkedHashMap<>(capacity(expectedSize));
    }

    /**
     * Returns a properly calculated capacity for maps that is large
     * enough to avoid map from resizing/reallocating its capacity.
     *
     * @param expectedSize
     * @return calculated capacity based on {@code expectedSize}
     */
    private static int capacity(final int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        } else if (expectedSize < INT_MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE;
    }
}
