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

package li.pitschmann.knx.core.cemi;

import li.pitschmann.knx.core.AbstractSingleRawData;
import li.pitschmann.knx.core.exceptions.KnxNullPointerException;
import li.pitschmann.knx.core.utils.Strings;

public final class ControlByte1 extends AbstractSingleRawData {
    private static final ControlByte1 DEFAULT = of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
    private final boolean standardFrame;
    private final boolean repeatEnabled;
    private final BroadcastType broadcastType;
    private final Priority priority;
    private final boolean requestAcknowledge;
    private final boolean errorConfirmation;

    private ControlByte1(final byte ctrlRawData) {
        super(ctrlRawData);

        // x... .... frame type
        // 0 = extended frame (9-263 octets)
        // 1 = standard frame (8-23 octets)
        this.standardFrame = (ctrlRawData & 0x80) == 0x80;
        // .x.. .... reserved
        // ..x. .... repeat
        // 0 = repeat on medium if error
        // 1 = do not repeat
        this.repeatEnabled = (ctrlRawData & 0x20) == 0x00;
        // ...x .... broadcast
        // 0 = system broadcast
        // 1 = broadcast
        this.broadcastType = BroadcastType.valueOf((ctrlRawData & 0x10) >>> 4);
        // .... xx.. priority
        // 0 = system
        // 1 = normal
        // 2 = urgent
        // 3 = low
        this.priority = Priority.valueOf((ctrlRawData & 0x0C) >>> 2);
        // .... ..x. acknowledge request flag
        // 0 = no ACK requested
        // 1 = ACK requested
        this.requestAcknowledge = (ctrlRawData & 0x02) == 0x02;
        // .... ...x confirmation flag
        // 0 = no error (confirm)
        // 1 = error (L-Data.Connection)
        this.errorConfirmation = (ctrlRawData & 0x01) == 0x01;
    }

    /**
     * Builds a new {@link ControlByte1} instance
     *
     * @param b byte
     * @return a new immutable {@link ControlByte1}
     */
    public static ControlByte1 of(final byte b) {
        return new ControlByte1(b);
    }

    /**
     * Returns the default {@link ControlByte1} with default settings:
     *
     * <ul>
     * <li>Use Standard Frame</li>
     * <li>No Repeat in case of error</li>
     * <li>Use {@link BroadcastType#NORMAL}</li>
     * <li>Use {@link Priority#LOW}</li>
     * <li>No Acknowledge Request</li>
     * <li>No Error</li>
     * </ul>
     *
     * @return re-usable immutable default {@link ControlByte1}
     */
    public static ControlByte1 useDefault() {
        return DEFAULT;
    }

    /**
     * Creates a new {@link ControlByte1} instance
     *
     * @param standardFrame        {@code true} for standard frame, {@code false} for extended frame
     * @param isRepeatEnabled      {@code true} if repeat in error, otherwise {@code false}
     * @param broadcastType        type of broadcast
     * @param priority             priority of transmission
     * @param acknowledgeRequested {@code true} if acknowledge shall be requested
     * @param errorConfirmation    {@code true} if error (negative confirmation), or {@code false} if no error (positive confirmation)
     * @return a new immutable {@link ControlByte1}
     */
    public static ControlByte1 of(final boolean standardFrame,
                                  final boolean isRepeatEnabled,
                                  final BroadcastType broadcastType,
                                  final Priority priority,
                                  final boolean acknowledgeRequested,
                                  final boolean errorConfirmation) {
        // validate
        if (broadcastType == null) {
            throw new KnxNullPointerException("broadcastType");
        } else if (priority == null) {
            throw new KnxNullPointerException("priority");
        }

        // x... .... frame type
        // 0 = extended frame (9-263 octets)
        // 1 = standard frame (8-23 octets)
        final var frameAsByte = standardFrame ? (byte) (0x01 << 7) : 0x00;
        // ..x. .... repeat
        // 0 = repeat on medium if error
        // 1 = do not repeat
        final var repeatAsByte = isRepeatEnabled ? 0x00 : (byte) (0x01 << 5);
        // ...x .... broadcast
        // 0 = system broadcast
        // 1 = broadcast
        final var broadcastTypeAsByte = (byte) (broadcastType.getCode() << 4);
        // .... xx.. priority
        final var priorityAsByte = (byte) (priority.getCodeAsByte() << 2);
        // .... ..x. acknowledge request flag
        // 0 = no ACK requested
        // 1 = ACK requested
        final var requestAckAsByte = acknowledgeRequested ? (byte) (0x01 << 1) : 0x00;
        // .... ...x confirmation flag
        // 0 = no error (confirm)
        // 1 = error (L-Data.Connection)
        final var errorConfirmationAsByte = errorConfirmation ? (byte) 0x01 : 0x00;

        // create byte
        final var b = (byte) (frameAsByte | repeatAsByte | broadcastTypeAsByte | priorityAsByte | requestAckAsByte | errorConfirmationAsByte);
        return of(b);
    }

    @Override
    protected void validate(final byte ctrlRawData) {
        // nothing to be validated
    }

    public boolean isStandardFrame() {
        return this.standardFrame;
    }

    public boolean isRepeatEnabled() {
        return this.repeatEnabled;
    }

    public BroadcastType getBroadcastType() {
        return this.broadcastType;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public boolean isRequestAcknowledge() {
        return this.requestAcknowledge;
    }


    public boolean isErrorConfirmation() {
        return this.errorConfirmation;
    }

    @Override
    public String toString() {
        // @formatter:off
        return Strings.toStringHelper(this)
                .add("standardFrame", this.standardFrame)
                .add("repeatEnabled", this.repeatEnabled)
                .add("broadcastType", this.broadcastType)
                .add("priority", this.priority)
                .add("requestAcknowledge", this.requestAcknowledge)
                .add("errorConfirmation", this.errorConfirmation)
                .add("rawData", this.getRawDataAsHexString())
                .toString();
        // @formatter:on
    }
}
