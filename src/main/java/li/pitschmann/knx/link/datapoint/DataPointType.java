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

package li.pitschmann.knx.link.datapoint;

import li.pitschmann.knx.link.datapoint.value.*;
import li.pitschmann.knx.link.exceptions.*;

/**
 * Data Point Types according to KNX Specification
 *
 * <pre>
 *                         [ Data Point Type ]
 *                                 |
 *             .-------------------+-------------------.
 *             |                                       |
 *       [ Data Type]                            [ Dimension ]
 *             |                                       |
 *     .-------+-------.                       .-------+-------.
 *     |               |                       |               |
 * [ Format ]     [ Encoding ]             [ Range ]        [ Unit ]
 * </pre>
 * <p>
 * The Data Point Types are defined as a combination of a data type and a dimension. It has been preferred not to define
 * the data types separately from any dimension.
 * <p>
 * Any Data Point Type thus standardizes one combination of format, encoding, range and unit. The Data Point Types will be
 * used to describe further KNX Interworking Standards.
 * <p>
 * The Data Point Types are identified by a 16 bit main number separated by a dot from a 16-bit sub-number, e.g. "7.002".
 * <p>
 * The coding is as follows:<br>
 * - main number (on left) which stands for format and encoding<br>
 * - sub number (on right) which stands for range and unit
 * <p>
 * Data Point Types with the same main number thus have the same format and encoding. Data Point Types with the same main
 * number have the same data type. A different subnumber indicates a different dimension (different range and/or
 * different unit).
 * <p>
 * (Description taken from KNX Specifications v2.1)
 *
 * @author PITSCHR
 */
public interface DataPointType<V extends DataPointValue<?>> {
    /**
     * Returns the id
     *
     * @return id
     */
    String getId();

    /**
     * Returns the description
     *
     * @return description
     */
    String getDescription();

    /**
     * Returns a {@link DataPointValue} for specified byte array.
     *
     * @param bytes
     * @return data point value
     * @throws DataPointTypeIncompatibleBytesException to be thrown if wrong byte array structure was provided
     */
    V toValue(final byte[] bytes) throws DataPointTypeIncompatibleBytesException;

    /**
     * Returns a {@link DataPointValue} for specified string arguments.
     * <p>
     * Per default it just parses the string arguments as hex string. This behavior can differ for the specific DPT
     * class.
     *
     * @param args arguments to be parsed
     * @return data point value
     * @throws DataPointTypeIncompatibleSyntaxException to be thrown if the arguments could not be interpreted
     */
    V toValue(final String[] args) throws DataPointTypeIncompatibleSyntaxException;
}
