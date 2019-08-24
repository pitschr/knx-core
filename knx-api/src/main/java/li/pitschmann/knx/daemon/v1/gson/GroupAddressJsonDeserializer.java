package li.pitschmann.knx.daemon.v1.gson;

import li.pitschmann.knx.link.body.address.AddressType;
import li.pitschmann.knx.link.body.address.GroupAddress;

/**
 * De-Serializes a JSON format of group address to an instance of {@link GroupAddress}
 * <p/>
 * Supported JSON formats:
 * <pre>
 * {"type":1,"raw":[15,19]}
 * [15,59]
 * 3899
 * "3899"
 * "1/1851"
 * "1/7/59"
 * </pre>
 */
public final class GroupAddressJsonDeserializer extends AbstractKnxAddressJsonDeserializer<GroupAddress> {
    public static final GroupAddressJsonDeserializer INSTANCE = new GroupAddressJsonDeserializer();

    private GroupAddressJsonDeserializer() {
        // private constructor
    }

    @Override
    protected AddressType supportedAddressType() {
        return AddressType.GROUP;
    }

    @Override
    protected GroupAddress convert(final byte[] addressArray) {
        return GroupAddress.of(addressArray);
    }

    @Override
    protected GroupAddress convert(final String address) {
        return GroupAddress.of(address);
    }
}
