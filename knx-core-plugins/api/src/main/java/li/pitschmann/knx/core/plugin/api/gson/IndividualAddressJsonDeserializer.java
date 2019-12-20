package li.pitschmann.knx.core.plugin.api.gson;

import li.pitschmann.knx.core.address.AddressType;
import li.pitschmann.knx.core.address.IndividualAddress;

/**
 * De-Serializes a JSON format of group address to an instance of {@link IndividualAddress}
 * <p>
 * Supported JSON formats:
 * <pre>
 * {"type":0,"raw":[-61,45]}
 * [-61,45]
 * "12.3.45"
 * </pre>
 */
public final class IndividualAddressJsonDeserializer extends AbstractKnxAddressJsonDeserializer<IndividualAddress> {
    public static final IndividualAddressJsonDeserializer INSTANCE = new IndividualAddressJsonDeserializer();

    private IndividualAddressJsonDeserializer() {
        // private constructor
    }

    @Override
    protected AddressType supportedAddressType() {
        return AddressType.INDIVIDUAL;
    }

    @Override
    protected IndividualAddress convert(final byte[] addressArray) {
        return IndividualAddress.of(addressArray);
    }

    @Override
    protected IndividualAddress convert(final String address) {
        return IndividualAddress.of(address);
    }
}
