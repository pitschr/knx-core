package li.pitschmann.knx.core.datapoint.value;

/**
 * A marker interface if the payload can be optimized and indicates that the
 * payload of {@link DataPointValue} can be optimized for the APCI data frame
 * <p>
 * If the size {@link DataPointValue} is up to 6-bits only, then payload
 * is subject to be optimized according to the KNX specification.
 */
public interface PayloadOptimizable {
    // nothing...
}
