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

import li.pitschmann.knx.link.datapoint.annotation.KnxDataPointType;
import li.pitschmann.knx.link.datapoint.value.DPT14Value;

/**
 * Data Point Type 14 for '4-Octet Float Value' (4 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | S   (Exponent)                  (Fraction)                    |
 * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 *             | (Fraction)                                                    |
 *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     4 octets (F<sub>32</sub>)
 * Encoding:   S = {0, 1}
 *             Exponent = [0 .. 255]
 *             Fraction = [0 .. 8388607]
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT14 extends AbstractRangeUnitDataPointType<DPT14Value, Double> {
    /**
     * <strong>14.000</strong> Acceleration (m/s<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m/s<sup>2</sup>
     * Resolution: 1 m/s<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.000", description = "Acceleration")
    public static final DPT14 ACCELERATION = new DPT14("14.000", "Acceleration", "m/s²");

    /**
     * <strong>14.001</strong> Acceleration Angular (rad/s<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       rad/s<sup>2</sup>
     * Resolution: 1 rad/s<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.001", description = "Acceleration Angular")
    public static final DPT14 ACCELERATION_ANGULAR = new DPT14("14.001", "Acceleration Angular", "rad/s²");

    /**
     * <strong>14.002</strong> Activation Energy (J/mol)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J/mol
     * Resolution: J/mol
     * </pre>
     */
    @KnxDataPointType(id = "14.002", description = "Activation Energy")
    public static final DPT14 ACTIVATION_ENERGY = new DPT14("14.002", "Activation Energy", "J/mol");

    /**
     * <strong>14.003</strong> Activity (s<sup>-1</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       s<sup>-1</sup>
     * Resolution: 1 s<sup>-1</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.003", description = "Activity")
    public static final DPT14 ACTIVITY = new DPT14("14.003", "Activity", "s⁻¹");

    /**
     * <strong>14.004</strong> Mol (mol)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       mol
     * Resolution: 1 mol
     * </pre>
     */
    @KnxDataPointType(id = "14.004", description = "Mol")
    public static final DPT14 MOL = new DPT14("14.004", "Mol", "mol");

    /**
     * <strong>14.005</strong> Amplitude
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       N/A
     * Resolution: N/A
     * </pre>
     */
    @KnxDataPointType(id = "14.005", description = "Amplitude")
    public static final DPT14 AMPLITUDE = new DPT14("14.005", "Amplitude", null);

    /**
     * <strong>14.006</strong> Angle Radiant (rad)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       rad
     * Resolution: 1 rad
     * </pre>
     */
    @KnxDataPointType(id = "14.006", description = "Angle Radiant")
    public static final DPT14 ANGLE_RADIANT = new DPT14("14.006", "Angle Radiant", "rad");

    /**
     * <strong>14.007</strong> Angle Degree (°)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       °
     * Resolution: 1°
     * </pre>
     */
    @KnxDataPointType(id = "14.007", description = "Angle Degree")
    public static final DPT14 ANGLE_DEGREE = new DPT14("14.007", "Angle Degree", "°");

    /**
     * <strong>14.008</strong> Angular Momentum (J s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J s
     * Resolution: 1 J s
     * </pre>
     */
    @KnxDataPointType(id = "14.008", description = "Angular Momentum")
    public static final DPT14 ANGULAR_MOMENTUM = new DPT14("14.008", "Angular Momentum", "J s");

    /**
     * <strong>14.009</strong> Angular Velocity (rad/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       rad/s
     * Resolution: 1 rad/s
     * </pre>
     */
    @KnxDataPointType(id = "14.009", description = "Angular Velocity")
    public static final DPT14 ANGULAR_VELOCITY = new DPT14("14.009", "Angular Velocity", "rad/s");

    /**
     * <strong>14.010</strong> Area (m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m<sup>2</sup>
     * Resolution: 1 m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.010", description = "Area")
    public static final DPT14 AREA = new DPT14("14.010", "Area", "m²");

    /**
     * <strong>14.011</strong> Capacitance (F)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       F
     * Resolution: 1 F
     * </pre>
     */
    @KnxDataPointType(id = "14.011", description = "Capacitance")
    public static final DPT14 CAPACITANCE = new DPT14("14.011", "Capacitance", "F");

    /**
     * <strong>14.012</strong> Charge Density Surface (C/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C/m<sup>2</sup>
     * Resolution: C/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.012", description = "Charge Density Surface")
    public static final DPT14 CHARGE_DENSITY_SURFACE = new DPT14("14.012", "Charge Density Surface", "C/m²");

    /**
     * <strong>14.013</strong> Charge Density Volume (C/m<sup>3</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C/m<sup>3</sup>
     * Resolution: 1 C/m<sup>3</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.013", description = "Charge Density Volume")
    public static final DPT14 CHARGE_DENSITY_VOLUME = new DPT14("14.013", "Charge Density Volume", "C/m³");

    /**
     * <strong>14.014</strong> Compressibility (m<sup>2</sup>/N)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m<sup>2</sup>/N
     * Resolution: 1 m<sup>2</sup>/N
     * </pre>
     */
    @KnxDataPointType(id = "14.014", description = "Compressibility")
    public static final DPT14 COMPRESSIBILITY = new DPT14("14.014", "Compressibility", "m²/N");

    /**
     * <strong>14.015</strong> Conductance (Ω<sup>-1</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Ω<sup>-1</sup>
     * Resolution: 1 Ω<sup>-1</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.015", description = "Conductance")
    public static final DPT14 CONDUCTANCE = new DPT14("14.015", "Conductance", "Ω⁻¹");

    /**
     * <strong>14.016</strong> Electrical Conductivity (S/m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       S/m
     * Resolution: 1 S/m
     * </pre>
     */
    @KnxDataPointType(id = "14.016", description = "Electrical Conductivity")
    public static final DPT14 ELECTRICAL_CONDUCTIVITY = new DPT14("14.016", "Electrical Conductivity", "S/m");

    /**
     * <strong>14.017</strong> Density (kg/m<sup>3</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       kg/m<sup>3</sup>
     * Resolution: 1 kg/m<sup>3</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.017", description = "Density")
    public static final DPT14 DENSITY = new DPT14("14.017", "Density", "kg/m³");

    /**
     * <strong>14.018</strong> Electric Charge (C)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C
     * Resolution: 1 C
     * </pre>
     */
    @KnxDataPointType(id = "14.018", description = "Electric Charge")
    public static final DPT14 ELECTRIC_CHARGE = new DPT14("14.018", "Electric Charge", "C");

    /**
     * <strong>14.019</strong> Electric Current (A)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A
     * Resolution: 1 A
     * </pre>
     */
    @KnxDataPointType(id = "14.019", description = "Electric Current")
    public static final DPT14 ELECTRIC_CURRENT = new DPT14("14.019", "Electric Current", "A");

    /**
     * <strong>14.020</strong> Electric Current Density (A/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A/m<sup>2</sup>
     * Resolution: 1 A/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.020", description = "Electric Current Density")
    public static final DPT14 ELECTRIC_CURRENT_DENSITY = new DPT14("14.020", "Electric Current Density", "A/m²");

    /**
     * <strong>14.021</strong> Electric Dipole Moment (C m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C m
     * Resolution: 1 C m
     * </pre>
     */
    @KnxDataPointType(id = "14.021", description = "Electric Dipole Moment")
    public static final DPT14 ELECTRIC_DIPOLE_MOMENT = new DPT14("14.021", "Electric Dipole Moment", "C m");

    /**
     * <strong>14.022</strong> Electric Displacement (C/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C/m<sup>2</sup>
     * Resolution: 1 C/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.022", description = "NAME")
    public static final DPT14 ELECTRIC_DISPLACEMENT = new DPT14("14.022", "NAME", "C/m²");

    /**
     * <strong>14.023</strong> Electric Field Strength (V/m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       V/m
     * Resolution: 1 V/m
     * </pre>
     */
    @KnxDataPointType(id = "14.023", description = "Electric Field Strength")
    public static final DPT14 ELECTRIC_FIELD_STRENGTH = new DPT14("14.023", "Electric Field Strength", "V/m");

    /**
     * <strong>14.024</strong> Electric Flux (c)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       c
     * Resolution: 1 c
     * </pre>
     */
    @KnxDataPointType(id = "14.024", description = "Electric Flux")
    public static final DPT14 ELECTRIC_FLUX = new DPT14("14.024", "Electric Flux", "c");

    /**
     * <strong>14.025</strong> Electric Flux Density (C/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C/m<sup>2</sup>
     * Resolution: 1 C/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.025", description = "Electric Flux Density")
    public static final DPT14 ELECTRIC_FLUX_DENSITY = new DPT14("14.025", "Electric Flux Density", "C/m²");

    /**
     * <strong>14.026</strong> Electric Polarization (C/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       C/m<sup>2</sup>
     * Resolution: 1 C/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.026", description = "Electric Polarization")
    public static final DPT14 ELECTRIC_POLARIZATION = new DPT14("14.026", "Electric Polarization", "C/m²");

    /**
     * <strong>14.027</strong> Electric Potential (V)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       V
     * Resolution: 1 V
     * </pre>
     */
    @KnxDataPointType(id = "14.027", description = "Electric Potential")
    public static final DPT14 ELECTRIC_POTENTIAL = new DPT14("14.027", "Electric Potential", "V");

    /**
     * <strong>14.028</strong> Electric Potential Difference (V)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       V
     * Resolution: 1 V
     * </pre>
     */
    @KnxDataPointType(id = "14.028", description = "Electric Potential Difference")
    public static final DPT14 ELECTRIC_POTENTIAL_DIFFERENCE = new DPT14("14.028", "Electric Potential Difference", "V");

    /**
     * <strong>14.029</strong> Electromagnetic Moment (A m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A m<sup>2</sup>
     * Resolution: 1 A m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.029", description = "NAME")
    public static final DPT14 ELECTROMAGNETIC_MOMENT = new DPT14("14.029", "NAME", "A m²");

    /**
     * <strong>14.030</strong> Electromotive Force (V)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       V
     * Resolution: 1 V
     * </pre>
     */
    @KnxDataPointType(id = "14.030", description = "Electromotive Force")
    public static final DPT14 ELECTROMOTIVE_FORCE = new DPT14("14.030", "Electromotive Force", "V");

    /**
     * <strong>14.031</strong> Energy (J)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J
     * Resolution: 1 J
     * </pre>
     */
    @KnxDataPointType(id = "14.031", description = "Energy")
    public static final DPT14 ENERGY = new DPT14("14.031", "Energy", "J");

    /**
     * <strong>14.032</strong> Force (N)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       N
     * Resolution: 1 N
     * </pre>
     */
    @KnxDataPointType(id = "14.032", description = "Force")
    public static final DPT14 FORCE = new DPT14("14.032", "Force", "N");

    /**
     * <strong>14.033</strong> Frequency (Hz)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Hz
     * Resolution: 1 Hz
     * </pre>
     */
    @KnxDataPointType(id = "14.033", description = "Frequency")
    public static final DPT14 FREQUENCY = new DPT14("14.033", "Frequency", "Hz");

    /**
     * <strong>14.034</strong> Angular Frequency (rad/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       rad/s
     * Resolution: 1 rad/s
     * </pre>
     */
    @KnxDataPointType(id = "14.034", description = "Angular Frequency")
    public static final DPT14 ANGULAR_FREQUENCY = new DPT14("14.034", "Angular Frequency", "rad/s");

    /**
     * <strong>14.035</strong> Heat Capacity (J/K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J/K
     * Resolution: 1 J/K
     * </pre>
     */
    @KnxDataPointType(id = "14.035", description = "Heat Capacity")
    public static final DPT14 HEAT_CAPACITY = new DPT14("14.035", "Heat Capacity", "J/K");

    /**
     * <strong>14.036</strong> Heat Flow Rate (W)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       W
     * Resolution: 1 W
     * </pre>
     */
    @KnxDataPointType(id = "14.036", description = "Heat Flow Rate")
    public static final DPT14 HEAT_FLOW_RATE = new DPT14("14.036", "Heat Flow Rate", "W");

    /**
     * <strong>14.037</strong> Heat Quantity (J)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J
     * Resolution: 1 J
     * </pre>
     */
    @KnxDataPointType(id = "14.037", description = "Heat Quantity")
    public static final DPT14 HEAT_QUANTITY = new DPT14("14.037", "Heat Quantity", "J");

    /**
     * <strong>14.038</strong> Impedance (Ω)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Ω
     * Resolution: 1 Ω
     * </pre>
     */
    @KnxDataPointType(id = "14.038", description = "Impedance")
    public static final DPT14 IMPEDANCE = new DPT14("14.038", "Impedance", "Ω");

    /**
     * <strong>14.039</strong> Length (m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m
     * Resolution: 1 m
     * </pre>
     */
    @KnxDataPointType(id = "14.039", description = "Length")
    public static final DPT14 LENGTH = new DPT14("14.039", "Length", "m");

    /**
     * <strong>14.040</strong> Light Quantity (J)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J
     * Resolution: 1 J
     * </pre>
     */
    @KnxDataPointType(id = "14.040", description = "Light Quantity")
    public static final DPT14 LIGHT_QUANTITY = new DPT14("14.040", "Light Quantity", "J");

    /**
     * <strong>14.041</strong> Luminance (cd/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       cd/m<sup>2</sup>
     * Resolution: 1 cd/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.041", description = "Luminance")
    public static final DPT14 LUMINANCE = new DPT14("14.041", "Luminance", "cd/m²");

    /**
     * <strong>14.042</strong> Luminous Flux (lm)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       lm
     * Resolution: 1 lm
     * </pre>
     */
    @KnxDataPointType(id = "14.042", description = "Luminous Flux")
    public static final DPT14 LUMINOUS_FLUX = new DPT14("14.042", "Luminous Flux", "lm");

    /**
     * <strong>14.043</strong> Lumnious Intensity (cd)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       cd
     * Resolution: 1 cd
     * </pre>
     */
    @KnxDataPointType(id = "14.043", description = "Lumnious Intensity")
    public static final DPT14 LUMINOUS_INTENSITY = new DPT14("14.043", "Lumnious Intensity", "cd");

    /**
     * <strong>14.044</strong> Magnetic Field Strength (A/m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A/m
     * Resolution: 1 A/m
     * </pre>
     */
    @KnxDataPointType(id = "14.044", description = "Magnetic Field Strength")
    public static final DPT14 MAGNETIC_FIELD_STRENGTH = new DPT14("14.044", "Magnetic Field Strength", "A/m");

    /**
     * <strong>14.045</strong> Magnetic Flux (Wb)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Wb
     * Resolution: 1 Wb
     * </pre>
     */
    @KnxDataPointType(id = "14.045", description = "Magnetic Flux")
    public static final DPT14 MAGNETIC_FLUX = new DPT14("14.045", "Magnetic Flux", "Wb");

    /**
     * <strong>14.046</strong> Magnetic Flux Density (T)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       T
     * Resolution: 1 T
     * </pre>
     */
    @KnxDataPointType(id = "14.046", description = "Magnetic Flux Density")
    public static final DPT14 MAGNETIC_FLUX_DENSITY = new DPT14("14.046", "Magnetic Flux Density", "T");

    /**
     * <strong>14.047</strong> Magnetic Moment (A m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A m<sup>2</sup>
     * Resolution: 1 A m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.047", description = "Magnetic Moment")
    public static final DPT14 MAGNETIC_MOMENT = new DPT14("14.047", "Magnetic Moment", "A m²");

    /**
     * <strong>14.048</strong> Magnetic Polarization (T)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       T
     * Resolution: 1 T
     * </pre>
     */
    @KnxDataPointType(id = "14.048", description = "Magnetic Polarization")
    public static final DPT14 MAGNETIC_POLARIZATION = new DPT14("14.048", "Magnetic Polarization", "T");

    /**
     * <strong>14.049</strong> Magnetization (A/m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A/m
     * Resolution: 1 A/m
     * </pre>
     */
    @KnxDataPointType(id = "14.049", description = "Magnetization")
    public static final DPT14 MAGNETIZATION = new DPT14("14.049", "Magnetization", "A/m");

    /**
     * <strong>14.050</strong> Magnetomotive Force (A)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       A
     * Resolution: 1 A
     * </pre>
     */
    @KnxDataPointType(id = "14.050", description = "Magnetomotive Force")
    public static final DPT14 MAGNETOMOTIVE_FORCE = new DPT14("14.050", "Magnetomotive Force", "A");

    /**
     * <strong>14.051</strong> Mass (kg)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       kg
     * Resolution: 1 kg
     * </pre>
     */
    @KnxDataPointType(id = "14.051", description = "Mass")
    public static final DPT14 MASS = new DPT14("14.051", "Mass", "kg");

    /**
     * <strong>14.052</strong> Mass Flux (kg/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       kg/s
     * Resolution: 1 kg/s
     * </pre>
     */
    @KnxDataPointType(id = "14.052", description = "Mass Flux")
    public static final DPT14 MASS_FLUX = new DPT14("14.052", "Mass Flux", "kg/s");

    /**
     * <strong>14.053</strong> Momentum (N/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       N/s
     * Resolution: 1 N/s
     * </pre>
     */
    @KnxDataPointType(id = "14.053", description = "Momentum")
    public static final DPT14 MOMENTUM = new DPT14("14.053", "Momentum", "N/s");

    /**
     * <strong>14.054</strong> Phase Angle Radiant (rad)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       rad
     * Resolution: 1 rad
     * </pre>
     */
    @KnxDataPointType(id = "14.054", description = "Phase Angle Radiant")
    public static final DPT14 PHASE_ANGLE_RADIANT = new DPT14("14.054", "Phase Angle Radiant", "rad");

    /**
     * <strong>14.055</strong> Phase Angle Degree (°)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       °
     * Resolution: 1 °
     * </pre>
     */
    @KnxDataPointType(id = "14.055", description = "Phase Angle Degree")
    public static final DPT14 PHASE_ANGLE_DEGREE = new DPT14("14.055", "Phase Angle Degree", "°");

    /**
     * <strong>14.056</strong> Power (W)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       W
     * Resolution: 1 W
     * </pre>
     */
    @KnxDataPointType(id = "14.056", description = "Power")
    public static final DPT14 POWER = new DPT14("14.056", "Power", "W");

    /**
     * <strong>14.057</strong> Power Factor
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       N/A
     * Resolution: N/A
     * </pre>
     */
    @KnxDataPointType(id = "14.057", description = "Power Factor")
    public static final DPT14 POWER_FACTOR = new DPT14("14.057", "Power Factor", "");

    /**
     * <strong>14.058</strong> Pressure (Pa)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Pa
     * Resolution: 1 Pa
     * </pre>
     */
    @KnxDataPointType(id = "14.058", description = "Pressure")
    public static final DPT14 PRESSURE = new DPT14("14.058", "Pressure", "Pa");

    /**
     * <strong>14.059</strong> Reactance (Ω)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Ω
     * Resolution: 1 Ω
     * </pre>
     */
    @KnxDataPointType(id = "14.059", description = "Reactance")
    public static final DPT14 REACTANCE = new DPT14("14.059", "Reactance", "Ω");

    /**
     * <strong>14.060</strong> Resistance (Ω)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Ω
     * Resolution: 1 Ω
     * </pre>
     */
    @KnxDataPointType(id = "14.060", description = "Resistance")
    public static final DPT14 RESISTANCE = new DPT14("14.060", "Resistance", "Ω");

    /**
     * <strong>14.061</strong> Resistivity (Ωm)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Ωm
     * Resolution: 1 Ωm
     * </pre>
     */
    @KnxDataPointType(id = "14.061", description = "Resistivity")
    public static final DPT14 RESISTIVITY = new DPT14("14.061", "Resistivity", "Ωm");

    /**
     * <strong>14.062</strong> Self Inductance (H)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       H
     * Resolution: 1 H
     * </pre>
     */
    @KnxDataPointType(id = "14.062", description = "Self Inductance")
    public static final DPT14 SELF_INDUCTANCE = new DPT14("14.062", "Self Inductance", "H");

    /**
     * <strong>14.063</strong> Solid Angle (sr)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       sr
     * Resolution: 1 sr
     * </pre>
     */
    @KnxDataPointType(id = "14.063", description = "Solid Angle")
    public static final DPT14 SOLID_ANGLE = new DPT14("14.063", "Solid Angle", "sr");

    /**
     * <strong>14.064</strong> Sound Intensity (W/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       W/m<sup>2</sup>
     * Resolution: 1 W/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.064", description = "Sound Intensity")
    public static final DPT14 SOUND_INTENSITY = new DPT14("14.064", "Sound Intensity", "W/m²");

    /**
     * <strong>14.065</strong> Speed (m/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m/s
     * Resolution: 1 m/s
     * </pre>
     */
    @KnxDataPointType(id = "14.065", description = "Speed")
    public static final DPT14 SPEED = new DPT14("14.065", "Speed", "m/s");

    /**
     * <strong>14.066</strong> Stress (Pa)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Pa
     * Resolution: 1 Pa
     * </pre>
     */
    @KnxDataPointType(id = "14.066", description = "Stress")
    public static final DPT14 STRESS = new DPT14("14.066", "Stress", "Pa");

    /**
     * <strong>14.067</strong> Surface Tension (N/m)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       N/m
     * Resolution: 1 N/m
     * </pre>
     */
    @KnxDataPointType(id = "14.067", description = "Surface Tension")
    public static final DPT14 SURFACE_TENSION = new DPT14("14.067", "Surface Tension", "N/m");

    /**
     * <strong>14.068</strong> Temperature (°C)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       °C
     * Resolution: 1 °C
     * </pre>
     */
    @KnxDataPointType(id = "14.068", description = "Temperature")
    public static final DPT14 TEMPERATURE = new DPT14("14.068", "Temperature", "°C");

    /**
     * <strong>14.069</strong> Temperature Kelvin (K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       K
     * Resolution: 1 K
     * </pre>
     */
    @KnxDataPointType(id = "14.069", description = "Temperature Kelvin")
    public static final DPT14 TEMPERATURE_KELVIN = new DPT14("14.069", "Temperature Kelvin", "K");

    /**
     * <strong>14.070</strong> Temperature Difference (K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       K
     * Resolution: 1 K
     * </pre>
     */
    @KnxDataPointType(id = "14.070", description = "Temperature Difference")
    public static final DPT14 TEMPERATURE_DIFFERENCE = new DPT14("14.070", "Temperature Difference", "K");

    /**
     * <strong>14.071</strong> Thermal Capacity (J/K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J/K
     * Resolution: 1 J/K
     * </pre>
     */
    @KnxDataPointType(id = "14.071", description = "Thermal Capacity")
    public static final DPT14 THERMAL_CAPACITY = new DPT14("14.071", "Thermal Capacity", "J/K");

    /**
     * <strong>14.072</strong> Thermal Conductivity (W/(m•K))
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       W/(m•K)
     * Resolution: 1 W/(m•K)
     * </pre>
     */
    @KnxDataPointType(id = "14.072", description = "Thermal Conductivity")
    public static final DPT14 THERMAL_CONDUCTIVITY = new DPT14("14.072", "Thermal Conductivity", "W/(m•K)");

    /**
     * <strong>14.073</strong> Thermoelectric Power (V/K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       V/K
     * Resolution: 1 V/K
     * </pre>
     */
    @KnxDataPointType(id = "14.073", description = "Thermoelectric Power")
    public static final DPT14 THERMOELECTRIC_POWER = new DPT14("14.073", "Thermoelectric Power", "V/K");

    /**
     * <strong>14.074</strong> Time (s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       s
     * Resolution: 1 s
     * </pre>
     */
    @KnxDataPointType(id = "14.074", description = "Time")
    public static final DPT14 TIME = new DPT14("14.074", "Time", "s");

    /**
     * <strong>14.075</strong> Torque (Nm)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       Nm
     * Resolution: 1 Nm
     * </pre>
     */
    @KnxDataPointType(id = "14.075", description = "Torque")
    public static final DPT14 TORQUE = new DPT14("14.075", "Torque", "Nm");

    /**
     * <strong>14.076</strong> Volume (m<sup>3</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m<sup>3</sup>
     * Resolution: 1 m<sup>3</sup>
     * </pre>
     */
    @KnxDataPointType(id = "14.076", description = "Volume")
    public static final DPT14 VOLUME = new DPT14("14.076", "Volume", "m³");

    /**
     * <strong>14.077</strong> Volume Flux (m<sup>3</sup>/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m<sup>3</sup>/s
     * Resolution: 1 m<sup>3</sup>/s
     * </pre>
     */
    @KnxDataPointType(id = "14.077", description = "Volume Flux")
    public static final DPT14 VOLUME_FLUX = new DPT14("14.077", "Volume Flux", "m³/s");

    /**
     * <strong>14.078</strong> Weight (N)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       N
     * Resolution: 1 N
     * </pre>
     */
    @KnxDataPointType(id = "14.078", description = "Weight")
    public static final DPT14 WEIGHT = new DPT14("14.078", "Weight", "N");

    /**
     * <strong>14.079</strong> Work (J)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       J
     * Resolution: 1 J
     * </pre>
     */
    @KnxDataPointType(id = "14.079", description = "Work")
    public static final DPT14 WORK = new DPT14("14.079", "Work", "J");

    /**
     * <strong>14.1200</strong> Volume Flux Meter (m<sup>3</sup>/h)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       m<sup>3</sup>/h
     * Resolution: 1 m<sup>3</sup>/h
     * </pre>
     */
    @KnxDataPointType(id = "14.1200", description = "Volume Flux For Meters")
    public static final DPT14 VOLUME_FLUX_METER = new DPT14("14.1200", "Volume Flux For Meters", "m³/h");

    /**
     * <strong>14.1201</strong> Volume Flux Meter (l/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | S   (Exponent)                  (Fraction)                    |
     * Encoding    | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     *             | (Fraction)                                                    |
     *             | F   F   F   F   F   F   F   F   F   F   F   F   F   F   F   F |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     4 octets (F<sub>32</sub>)
     * Encoding:   S = {0, 1}
     *             Exponent = [0 .. 255]
     *             Fraction = [0 .. 8388607]
     * Unit:       l/s
     * Resolution: 1 l/s
     * </pre>
     */
    @KnxDataPointType(id = "14.1201", description = "Volume Flux For Meters")
    public static final DPT14 VOLUME_FLUX_LITER_PER_SECONDS = new DPT14("14.1201", "Volume Flux For Meters", "l/s");

    /**
     * Constructor for {@link DPT14}
     *
     * @param id
     * @param desc
     * @param unit
     */
    private DPT14(final String id, final String desc, final String unit) {
        super(id, desc, -3.40282347e+38, 3.40282347e+38, unit);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 4;
    }

    @Override
    protected DPT14Value parse(final byte[] bytes) {
        return new DPT14Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT14Value parse(final String[] args) {
        return new DPT14Value(this, Double.parseDouble(args[0]));
    }

    public DPT14Value toValue(final double value) {
        return new DPT14Value(this, value);
    }

    public byte[] toByteArray(final double value) {
        return DPT14Value.toByteArray(value);
    }
}
