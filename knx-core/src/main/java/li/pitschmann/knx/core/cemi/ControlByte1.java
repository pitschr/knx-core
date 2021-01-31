/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2021 Pitschmann Christoph
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

import li.pitschmann.knx.core.SingleRawDataAware;
import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Strings;

import java.util.Objects;

/**
 * First Control Field for {@link CEMI}, containing:
 *
 * <ul>
 *     <li><strong>Frame Type (FT):</strong> This field shall specify whether the frame is a
 *     standard frame or an extended frame.</li>
 *     <li><strong>Repetition (R):</strong> This shall specify whether repetitions shall
 *     be sent on the medium. This flag is relevant only on media with possibility of
 *     Data Link Layer controlled frame repetitions (TP1, PL110). Not relevant for
 *     KNXnet/IP frames.</li>
 *     <li><strong>Broadcast Type (SB):</strong> This flag shall only be applicable on open media.</li>
 *     <li><strong>Priority (P):</strong> (bit 3 and 2) This shall specify that Priority that shall be used for
 *     transmission or reception of the frame.</li>
 *     <li><strong>Acknowledge Request (A):</strong> This shall specify whether a L2-acknowledge
 *     shall be requested for the L_Data.req frame or not.</li>
 *     <li><strong>Error Confirmation (C):</strong> In L_Data.con this shall indicate whether
 *     there has been any error in the transmitted frame.
 *     </li>
 * </ul>
 * <pre>
 * +--7--+--6--+--5--+--4--+--3--+--2--+--1--+--0--+
 * | FT  |  /  |  R  |  SB |  Priority |  A  |  C  |
 * +-----+-----+-----+-----+-----+-----+-----+-----+
 * </pre>
 * See: KNX Specification, EMI/IMI
 *
 * @author PITSCHR
 */
public final class ControlByte1 implements SingleRawDataAware {
    private static final ControlByte1 DEFAULT = of(true, false, BroadcastType.NORMAL, Priority.LOW, false, false);
    private final boolean standardFrame;
    private final boolean repeatEnabled;
    private final BroadcastType broadcastType;
    private final Priority priority;
    private final boolean requestAcknowledge;
    private final boolean errorConfirmation;

    private ControlByte1(final byte ctrlRawData) {
        this(
                // x... .... frame type
                // 0 = extended frame (9-263 octets)
                // 1 = standard frame (8-23 octets)
                (ctrlRawData & 0x80) == 0x80,
                // .x.. .... reserved
                // ..x. .... repeat
                // 0 = repeat on medium if error
                // 1 = do not repeat
                (ctrlRawData & 0x20) == 0x00,
                // ...x .... broadcast
                // 0 = system broadcast
                // 1 = broadcast
                BroadcastType.valueOf((ctrlRawData & 0x10) >>> 4),
                // .... xx.. priority
                // 0 = system
                // 1 = normal
                // 2 = urgent
                // 3 = low
                Priority.valueOf((ctrlRawData & 0x0C) >>> 2),
                // .... ..x. acknowledge request flag
                // 0 = no ACK requested
                // 1 = ACK requested
                (ctrlRawData & 0x02) == 0x02,
                // .... ...x confirmation flag
                // 0 = no error (confirm)
                // 1 = error (L-Data.Connection)
                (ctrlRawData & 0x01) == 0x01
        );
    }

    private ControlByte1(final boolean standardFrame,
                         final boolean repeatEnabled,
                         final BroadcastType broadcastType,
                         final Priority priority,
                         final boolean requestAcknowledge,
                         final boolean errorConfirmation) {
        Preconditions.checkNonNull(broadcastType, "Broadcast Type is required.");
        Preconditions.checkNonNull(priority, "Priority is required.");

        this.standardFrame = standardFrame;
        this.repeatEnabled = repeatEnabled;
        this.broadcastType = broadcastType;
        this.priority = priority;
        this.requestAcknowledge = requestAcknowledge;
        this.errorConfirmation = errorConfirmation;
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
     * @param standardFrame      {@code true} for standard frame, {@code false} for extended frame
     * @param repeatEnabled      {@code true} if repeat in error, otherwise {@code false}
     * @param broadcastType      type of broadcast
     * @param priority           priority of transmission
     * @param requestAcknowledge {@code true} if acknowledge shall be requested
     * @param errorConfirmation  {@code true} if error (negative confirmation), or {@code false} if no error (positive confirmation)
     * @return a new immutable {@link ControlByte1}
     */
    public static ControlByte1 of(final boolean standardFrame,
                                  final boolean repeatEnabled,
                                  final BroadcastType broadcastType,
                                  final Priority priority,
                                  final boolean requestAcknowledge,
                                  final boolean errorConfirmation) {
        return new ControlByte1(standardFrame, repeatEnabled, broadcastType, priority, requestAcknowledge, errorConfirmation);
    }

    public boolean isStandardFrame() {
        return standardFrame;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    public BroadcastType getBroadcastType() {
        return broadcastType;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isRequestAcknowledge() {
        return requestAcknowledge;
    }

    public boolean isErrorConfirmation() {
        return errorConfirmation;
    }

    @Override
    public byte toByte() {
        // x... .... frame type
        // 0 = extended frame (9-263 octets)
        // 1 = standard frame (8-23 octets)
        final var frameAsByte = standardFrame ? (byte) (0x01 << 7) : 0x00;
        // ..x. .... repeat
        // 0 = repeat on medium if error
        // 1 = do not repeat
        final var repeatAsByte = repeatEnabled ? 0x00 : (byte) (0x01 << 5);
        // ...x .... broadcast
        // 0 = system broadcast
        // 1 = broadcast
        final var broadcastTypeAsByte = (byte) (broadcastType.getCode() << 4);
        // .... xx.. priority
        final var priorityAsByte = (byte) (priority.getCodeAsByte() << 2);
        // .... ..x. acknowledge request flag
        // 0 = no ACK requested
        // 1 = ACK requested
        final var requestAckAsByte = requestAcknowledge ? (byte) (0x01 << 1) : 0x00;
        // .... ...x confirmation flag
        // 0 = no error (confirm)
        // 1 = error (L-Data.Connection)
        final var errorConfirmationAsByte = errorConfirmation ? (byte) 0x01 : 0x00;

        // create byte
        return (byte) (
                frameAsByte                             // bit 7: Frame Type (FT)
                        // bit 6: (not-used / reserved)
                        | repeatAsByte                  // bit 5: Repetition (R)
                        | broadcastTypeAsByte           // bit 4: Broadcast Type (SB)
                        | priorityAsByte                // bit 3+2: Priority (P)
                        | requestAckAsByte              // bit 1: Acknowledge Request (A)
                        | errorConfirmationAsByte       // bit 0: Error Confirmation (C)
        );
    }

    @Override
    public String toString() {
        return Strings.toStringHelper(this)
                .add("standardFrame", standardFrame)
                .add("repeatEnabled", repeatEnabled)
                .add("broadcastType", broadcastType.name())
                .add("priority", priority.name())
                .add("requestAcknowledge", requestAcknowledge)
                .add("errorConfirmation", errorConfirmation)
                .toString();
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ControlByte1) {
            final var other = (ControlByte1) obj;
            return this.standardFrame == other.standardFrame //
                    && this.repeatEnabled == other.repeatEnabled //
                    && Objects.equals(this.broadcastType, other.broadcastType) //
                    && Objects.equals(this.priority, other.priority) //
                    && this.requestAcknowledge == other.requestAcknowledge
                    && this.errorConfirmation == other.errorConfirmation; //
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(standardFrame, repeatEnabled, broadcastType, priority, requestAcknowledge, errorConfirmation);
    }
}
