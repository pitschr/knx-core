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

package li.pitschmann.knx.core.datapoint;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.datapoint.value.DPT14Value;

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
public final class DPT14 extends AbstractRangeDataPointType<DPT14Value, Double> {
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
    @DataPoint({"14.000", "dpt-14", "dpst-14-0"})
    public static final DPT14 ACCELERATION = new DPT14("Acceleration", "m/s²");

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
    @DataPoint({"14.001", "dpst-14-1"})
    public static final DPT14 ACCELERATION_ANGULAR = new DPT14("Acceleration Angular", "rad/s²");

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
    @DataPoint({"14.002", "dpst-14-2"})
    public static final DPT14 ACTIVATION_ENERGY = new DPT14("Activation Energy", "J/mol");

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
    @DataPoint({"14.003", "dpst-14-3"})
    public static final DPT14 ACTIVITY = new DPT14("Activity", "s⁻¹");

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
    @DataPoint({"14.004", "dpst-14-4"})
    public static final DPT14 MOL = new DPT14("Mol", "mol");

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
    @DataPoint({"14.005", "dpst-14-5"})
    public static final DPT14 AMPLITUDE = new DPT14("Amplitude", null);

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
    @DataPoint({"14.006", "dpst-14-6"})
    public static final DPT14 ANGLE_RADIANT = new DPT14("Angle Radiant", "rad");

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
    @DataPoint({"14.007", "dpst-14-7"})
    public static final DPT14 ANGLE_DEGREE = new DPT14("Angle Degree", "°");

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
    @DataPoint({"14.008", "dpst-14-8"})
    public static final DPT14 ANGULAR_MOMENTUM = new DPT14("Angular Momentum", "J s");

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
    @DataPoint({"14.009", "dpst-14-9"})
    public static final DPT14 ANGULAR_VELOCITY = new DPT14("Angular Velocity", "rad/s");

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
    @DataPoint({"14.010", "dpst-14-10"})
    public static final DPT14 AREA = new DPT14("Area", "m²");

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
    @DataPoint({"14.011", "dpst-14-11"})
    public static final DPT14 CAPACITANCE = new DPT14("Capacitance", "F");

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
    @DataPoint({"14.012", "dpst-14-12"})
    public static final DPT14 CHARGE_DENSITY_SURFACE = new DPT14("Charge Density Surface", "C/m²");

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
    @DataPoint({"14.013", "dpst-14-13"})
    public static final DPT14 CHARGE_DENSITY_VOLUME = new DPT14("Charge Density Volume", "C/m³");

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
    @DataPoint({"14.014", "dpst-14-14"})
    public static final DPT14 COMPRESSIBILITY = new DPT14("Compressibility", "m²/N");

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
    @DataPoint({"14.015", "dpst-14-15"})
    public static final DPT14 CONDUCTANCE = new DPT14("Conductance", "Ω⁻¹");

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
    @DataPoint({"14.016", "dpst-14-16"})
    public static final DPT14 ELECTRICAL_CONDUCTIVITY = new DPT14("Electrical Conductivity", "S/m");

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
    @DataPoint({"14.017", "dpst-14-17"})
    public static final DPT14 DENSITY = new DPT14("Density", "kg/m³");

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
    @DataPoint({"14.018", "dpst-14-18"})
    public static final DPT14 ELECTRIC_CHARGE = new DPT14("Electric Charge", "C");

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
    @DataPoint({"14.019", "dpst-14-19"})
    public static final DPT14 ELECTRIC_CURRENT = new DPT14("Electric Current", "A");

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
    @DataPoint({"14.020", "dpst-14-20"})
    public static final DPT14 ELECTRIC_CURRENT_DENSITY = new DPT14("Electric Current Density", "A/m²");

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
    @DataPoint({"14.021", "dpst-14-21"})
    public static final DPT14 ELECTRIC_DIPOLE_MOMENT = new DPT14("Electric Dipole Moment", "C m");

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
    @DataPoint({"14.022", "dpst-14-22"})
    public static final DPT14 ELECTRIC_DISPLACEMENT = new DPT14("NAME", "C/m²");

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
    @DataPoint({"14.023", "dpst-14-23"})
    public static final DPT14 ELECTRIC_FIELD_STRENGTH = new DPT14("Electric Field Strength", "V/m");

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
    @DataPoint({"14.024", "dpst-14-24"})
    public static final DPT14 ELECTRIC_FLUX = new DPT14("Electric Flux", "c");

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
    @DataPoint({"14.025", "dpst-14-25"})
    public static final DPT14 ELECTRIC_FLUX_DENSITY = new DPT14("Electric Flux Density", "C/m²");

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
    @DataPoint({"14.026", "dpst-14-26"})
    public static final DPT14 ELECTRIC_POLARIZATION = new DPT14("Electric Polarization", "C/m²");

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
    @DataPoint({"14.027", "dpst-14-27"})
    public static final DPT14 ELECTRIC_POTENTIAL = new DPT14("Electric Potential", "V");

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
    @DataPoint({"14.028", "dpst-14-28"})
    public static final DPT14 ELECTRIC_POTENTIAL_DIFFERENCE = new DPT14("Electric Potential Difference", "V");

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
    @DataPoint({"14.029", "dpst-14-29"})
    public static final DPT14 ELECTROMAGNETIC_MOMENT = new DPT14("NAME", "A m²");

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
    @DataPoint({"14.030", "dpst-14-30"})
    public static final DPT14 ELECTROMOTIVE_FORCE = new DPT14("Electromotive Force", "V");

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
    @DataPoint({"14.031", "dpst-14-31"})
    public static final DPT14 ENERGY = new DPT14("Energy", "J");

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
    @DataPoint({"14.032", "dpst-14-32"})
    public static final DPT14 FORCE = new DPT14("Force", "N");

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
    @DataPoint({"14.033", "dpst-14-33"})
    public static final DPT14 FREQUENCY = new DPT14("Frequency", "Hz");

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
    @DataPoint({"14.034", "dpst-14-34"})
    public static final DPT14 ANGULAR_FREQUENCY = new DPT14("Angular Frequency", "rad/s");

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
    @DataPoint({"14.035", "dpst-14-35"})
    public static final DPT14 HEAT_CAPACITY = new DPT14("Heat Capacity", "J/K");

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
    @DataPoint({"14.036", "dpst-14-36"})
    public static final DPT14 HEAT_FLOW_RATE = new DPT14("Heat Flow Rate", "W");

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
    @DataPoint({"14.037", "dpst-14-37"})
    public static final DPT14 HEAT_QUANTITY = new DPT14("Heat Quantity", "J");

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
    @DataPoint({"14.038", "dpst-14-38"})
    public static final DPT14 IMPEDANCE = new DPT14("Impedance", "Ω");

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
    @DataPoint({"14.039", "dpst-14-39"})
    public static final DPT14 LENGTH = new DPT14("Length", "m");

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
    @DataPoint({"14.040", "dpst-14-40"})
    public static final DPT14 LIGHT_QUANTITY = new DPT14("Light Quantity", "J");

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
    @DataPoint({"14.041", "dpst-14-41"})
    public static final DPT14 LUMINANCE = new DPT14("Luminance", "cd/m²");

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
    @DataPoint({"14.042", "dpst-14-42"})
    public static final DPT14 LUMINOUS_FLUX = new DPT14("Luminous Flux", "lm");

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
    @DataPoint({"14.043", "dpst-14-43"})
    public static final DPT14 LUMINOUS_INTENSITY = new DPT14("Lumnious Intensity", "cd");

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
    @DataPoint({"14.044", "dpst-14-44"})
    public static final DPT14 MAGNETIC_FIELD_STRENGTH = new DPT14("Magnetic Field Strength", "A/m");

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
    @DataPoint({"14.045", "dpst-14-45"})
    public static final DPT14 MAGNETIC_FLUX = new DPT14("Magnetic Flux", "Wb");

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
    @DataPoint({"14.046", "dpst-14-46"})
    public static final DPT14 MAGNETIC_FLUX_DENSITY = new DPT14("Magnetic Flux Density", "T");

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
    @DataPoint({"14.047", "dpst-14-47"})
    public static final DPT14 MAGNETIC_MOMENT = new DPT14("Magnetic Moment", "A m²");

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
    @DataPoint({"14.048", "dpst-14-48"})
    public static final DPT14 MAGNETIC_POLARIZATION = new DPT14("Magnetic Polarization", "T");

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
    @DataPoint({"14.049", "dpst-14-49"})
    public static final DPT14 MAGNETIZATION = new DPT14("Magnetization", "A/m");

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
    @DataPoint({"14.050", "dpst-14-50"})
    public static final DPT14 MAGNETOMOTIVE_FORCE = new DPT14("Magnetomotive Force", "A");

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
    @DataPoint({"14.051", "dpst-14-51"})
    public static final DPT14 MASS = new DPT14("Mass", "kg");

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
    @DataPoint({"14.052", "dpst-14-52"})
    public static final DPT14 MASS_FLUX = new DPT14("Mass Flux", "kg/s");

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
    @DataPoint({"14.053", "dpst-14-53"})
    public static final DPT14 MOMENTUM = new DPT14("Momentum", "N/s");

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
    @DataPoint({"14.054", "dpst-14-54"})
    public static final DPT14 PHASE_ANGLE_RADIANT = new DPT14("Phase Angle Radiant", "rad");

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
    @DataPoint({"14.055", "dpst-14-55"})
    public static final DPT14 PHASE_ANGLE_DEGREE = new DPT14("Phase Angle Degree", "°");

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
    @DataPoint({"14.056", "dpst-14-56"})
    public static final DPT14 POWER = new DPT14("Power", "W");

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
    @DataPoint({"14.057", "dpst-14-57"})
    public static final DPT14 POWER_FACTOR = new DPT14("Power Factor", "");

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
    @DataPoint({"14.058", "dpst-14-58"})
    public static final DPT14 PRESSURE = new DPT14("Pressure", "Pa");

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
    @DataPoint({"14.059", "dpst-14-59"})
    public static final DPT14 REACTANCE = new DPT14("Reactance", "Ω");

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
    @DataPoint({"14.060", "dpst-14-60"})
    public static final DPT14 RESISTANCE = new DPT14("Resistance", "Ω");

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
    @DataPoint({"14.061", "dpst-14-61"})
    public static final DPT14 RESISTIVITY = new DPT14("Resistivity", "Ωm");

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
    @DataPoint({"14.062", "dpst-14-62"})
    public static final DPT14 SELF_INDUCTANCE = new DPT14("Self Inductance", "H");

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
    @DataPoint({"14.063", "dpst-14-63"})
    public static final DPT14 SOLID_ANGLE = new DPT14("Solid Angle", "sr");

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
    @DataPoint({"14.064", "dpst-14-64"})
    public static final DPT14 SOUND_INTENSITY = new DPT14("Sound Intensity", "W/m²");

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
    @DataPoint({"14.065", "dpst-14-65"})
    public static final DPT14 SPEED = new DPT14("Speed", "m/s");

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
    @DataPoint({"14.066", "dpst-14-66"})
    public static final DPT14 STRESS = new DPT14("Stress", "Pa");

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
    @DataPoint({"14.067", "dpst-14-67"})
    public static final DPT14 SURFACE_TENSION = new DPT14("Surface Tension", "N/m");

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
    @DataPoint({"14.068", "dpst-14-68"})
    public static final DPT14 TEMPERATURE = new DPT14("Temperature", "°C");

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
    @DataPoint({"14.069", "dpst-14-69"})
    public static final DPT14 TEMPERATURE_KELVIN = new DPT14("Temperature Kelvin", "K");

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
    @DataPoint({"14.070", "dpst-14-70"})
    public static final DPT14 TEMPERATURE_DIFFERENCE = new DPT14("Temperature Difference", "K");

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
    @DataPoint({"14.071", "dpst-14-71"})
    public static final DPT14 THERMAL_CAPACITY = new DPT14("Thermal Capacity", "J/K");

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
    @DataPoint({"14.072", "dpst-14-72"})
    public static final DPT14 THERMAL_CONDUCTIVITY = new DPT14("Thermal Conductivity", "W/(m•K)");

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
    @DataPoint({"14.073", "dpst-14-73"})
    public static final DPT14 THERMOELECTRIC_POWER = new DPT14("Thermoelectric Power", "V/K");

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
    @DataPoint({"14.074", "dpst-14-74"})
    public static final DPT14 TIME = new DPT14("Time", "s");

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
    @DataPoint({"14.075", "dpst-14-75"})
    public static final DPT14 TORQUE = new DPT14("Torque", "Nm");

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
    @DataPoint({"14.076", "dpst-14-76"})
    public static final DPT14 VOLUME = new DPT14("Volume", "m³");

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
    @DataPoint({"14.077", "dpst-14-77"})
    public static final DPT14 VOLUME_FLUX = new DPT14("Volume Flux", "m³/s");

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
    @DataPoint({"14.078", "dpst-14-78"})
    public static final DPT14 WEIGHT = new DPT14("Weight", "N");

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
    @DataPoint({"14.079", "dpst-14-79"})
    public static final DPT14 WORK = new DPT14("Work", "J");

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
    @DataPoint({"14.1200", "dpst-14-1200"})
    public static final DPT14 VOLUME_FLUX_METER = new DPT14("Volume Flux For Meters", "m³/h");

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
    @DataPoint({"14.1201", "dpst-14-1201"})
    public static final DPT14 VOLUME_FLUX_LITER_PER_SECONDS = new DPT14("Volume Flux For Meters", "l/s");

    /**
     * Constructor for {@link DPT14}
     *
     * @param desc description for {@link DPT14}
     * @param unit the unit representation for {@link DPT14}
     */
    private DPT14(final String desc,
                  final @Nullable String unit) {
        super(desc, -3.40282347e+38, 3.40282347e+38, unit);
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

    public DPT14Value of(final double value) {
        return new DPT14Value(this, value);
    }

    public byte[] toByteArray(final double value) {
        return DPT14Value.toByteArray(value);
    }
}
