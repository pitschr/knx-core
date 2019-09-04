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
import li.pitschmann.knx.link.datapoint.value.DPT9Value;

/**
 * Data Point Type 9 for '2-Octet Float Value' (2 Octets)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Float Value)                                                 |
 * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
 *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * Format:     2 octets (F<sub>16</sub>)
 * Encoding:   Float Value = (0.01 * M)*2(E)
 *             E = [0 .. 15]
 *             M = [-2048 .. 2047], two's complement notation
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT9 extends AbstractRangeDataPointType<DPT9Value, Double> {
    /**
     * <strong>9.001</strong> Temperature (°C)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-273 .. 670760.96]
     * Unit:       °C
     * Resolution: 0.01 °C
     * </pre>
     */
    @KnxDataPointType(id = "9.001", description = "Temperature")
    public static final DPT9 TEMPERATURE = new DPT9("9.001", "Temperature", -273, 670760.96, "°C");

    /**
     * <strong>9.002</strong> Temperature Difference (K)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       K
     * Resolution: 0.01 K
     * </pre>
     */
    @KnxDataPointType(id = "9.002", description = "Temperature Difference")
    public static final DPT9 TEMPERATURE_DIFFERENCE = new DPT9("9.002", "Temperature Difference", -671088.64, 670760.96, "K");
    /**
     * <strong>9.003</strong> Temperature Kelvin/Hour (K/h)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       K/h
     * Resolution: 0.01 K/h
     * </pre>
     */
    @KnxDataPointType(id = "9.003", description = "Temperature Kelvin/Hour")
    public static final DPT9 TEMPERATURE_KELVIN_HOUR = new DPT9("9.003", "Temperature Kelvin/Hour", -671088.64, 670760.96, "K/h");
    /**
     * <strong>9.004</strong> Luminous Flux (lux)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [0 .. 670760.96]
     * Unit:       lux
     * Resolution: 0.01 lux
     * </pre>
     */
    @KnxDataPointType(id = "9.004", description = "Luminous Flux")
    public static final DPT9 LUMINOUS_FLUX = new DPT9("9.004", "Luminous Flux", 0, 670760.96, "lux");
    /**
     * <strong>9.005</strong> Wind Speed (m/s)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [0 .. 670760.96]
     * Unit:       m/s
     * Resolution: 0.01 m/s
     * </pre>
     */
    @KnxDataPointType(id = "9.005", description = "Wind Speed")
    public static final DPT9 WIND_SPEED = new DPT9("9.005", "Wind Speed", 0, 670760.96, "m/s");
    /**
     * <strong>9.006</strong> Air Pressure (Pa)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [0 .. 670760.96]
     * Unit:       Pa
     * Resolution: 0.01 Pa
     * </pre>
     */
    @KnxDataPointType(id = "9.006", description = "Air Pressure")
    public static final DPT9 AIR_PRESSURE = new DPT9("9.006", "Air Pressure", 0, 670760.96, "Pa");
    /**
     * <strong>9.007</strong> Humidity (%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [0 .. 670760.96]
     * Unit:       %
     * Resolution: 0.01 %
     * </pre>
     */
    @KnxDataPointType(id = "9.007", description = "Humidity")
    public static final DPT9 HUMIDITY = new DPT9("9.007", "Humidity", 0, 670760.96, "%");
    /**
     * <strong>9.008</strong> Air Quality (ppm)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [0 .. 670760.96]
     * Unit:       ppm
     * Resolution: 0.01 ppm
     * </pre>
     */
    @KnxDataPointType(id = "9.008", description = "Air Quality")
    public static final DPT9 AIR_QUALITY = new DPT9("9.008", "Air Quality", 0, 670760.96, "ppm");

    /**
     * <strong>9.009</strong> Air Flow (m<sup>3</sup>/h)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       m<sup>3</sup>/h
     * Resolution: 0.01 m<sup>3</sup>/h
     * </pre>
     */
    @KnxDataPointType(id = "9.009", description = "Air Flow")
    public static final DPT9 AIR_FLOW = new DPT9("9.009", "Air Flow", -671088.64, 670760.96, "m³/h");

    /**
     * <strong>9.010</strong> Time Difference (seconds)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       s
     * Resolution: 0.01 s
     * </pre>
     */
    @KnxDataPointType(id = "9.010", description = "Time Difference")
    public static final DPT9 TIME_DIFFERENCE_SECONDS = new DPT9("9.010", "Time Difference", -671088.64, 670760.96, "s");
    /**
     * <strong>9.011</strong> Time Difference (milliseconds)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       ms
     * Resolution: 0.01 ms
     * </pre>
     */
    @KnxDataPointType(id = "9.011", description = "Time Difference")
    public static final DPT9 TIME_DIFFERENCE_MILLISECONDS = new DPT9("9.011", "Time Difference", -671088.64, 670760.96, "ms");
    /**
     * <strong>9.020</strong> Voltage (mV)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       mV
     * Resolution: 0.01 mV
     * </pre>
     */
    @KnxDataPointType(id = "9.020", description = "Voltage")
    public static final DPT9 VOLTAGE = new DPT9("9.020", "Voltage", -671088.64, 670760.96, "mV");
    /**
     * <strong>9.021</strong> Electric Current (mA)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       mA
     * Resolution: 0.01 mA
     * </pre>
     */
    @KnxDataPointType(id = "9.021", description = "Electric Current")
    public static final DPT9 ELECTRIC_CURRENT = new DPT9("9.021", "Electric Current", -671088.64, 670760.96, "mA");
    /**
     * <strong>9.022</strong> Power Density (W/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       W/m<sup>2</sup>
     * Resolution: 0.01 W/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "9.022", description = "Power Density")
    public static final DPT9 POWER_DENSITY = new DPT9("9.022", "Power Density", -671088.64, 670760.96, "W/m²");
    /**
     * <strong>9.023</strong> Kelvin Per Percent (K/%)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       K/%
     * Resolution: 0.01 K/%
     * </pre>
     */
    @KnxDataPointType(id = "9.023", description = "Kelvin Per Percent")
    public static final DPT9 KELVIN_PER_PERCENT = new DPT9("9.023", "Kelvin Per Percent", -671088.64, 670760.96, "K/%");
    /**
     * <strong>9.024</strong> Power (kW)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       kW
     * Resolution: 0.01 kW
     * </pre>
     */
    @KnxDataPointType(id = "9.024", description = "Power")
    public static final DPT9 POWER = new DPT9("9.024", "Power", -671088.64, 670760.96, "kW");
    /**
     * <strong>9.025</strong> Volume Flow (l/h)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96]
     * Unit:       l/h
     * Resolution: 0.01 l/h
     * </pre>
     */
    @KnxDataPointType(id = "9.025", description = "Volume Flow")
    public static final DPT9 VOLUME_FLOW = new DPT9("9.025", "Volume Flow", -671088.64, 670760.96, "l/h");
    /**
     * <strong>9.026</strong> Rain Amount (l/m<sup>2</sup>)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-671088.64 .. 670760.96.96]
     * Unit:       l/m<sup>2</sup>
     * Resolution: 0.01 l/m<sup>2</sup>
     * </pre>
     */
    @KnxDataPointType(id = "9.026", description = "Rain Amount")
    public static final DPT9 RAIN_AMOUNT = new DPT9("9.026", "Rain Amount", -671088.64f, 670760.96, "l/m²");
    /**
     * <strong>9.027</strong> Temperature (°F)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [-459.6 .. 670760.96]
     * Unit:       °F
     * Resolution: 0.01 °F
     * </pre>
     */
    @KnxDataPointType(id = "9.027", description = "Temperature")
    public static final DPT9 TEMPERATURE_FAHRENHEIT = new DPT9("9.027", "Temperature", -459.6f, 670760.96, "°F");
    /**
     * <strong>9.028</strong> Wind Speed (km/h)
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Float Value)                                                 |
     * Encoding    | M   E   E   E   E   M   M   M   M   M   M   M   M   M   M   M |
     *             +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
     * Format:     2 octets (F<sub>16</sub>)
     * Encoding:   Float Value = (0.01 * M)*2(E)
     *             E = [0 .. 15]
     *             M = [-2048 .. 2047], two's complement notation
     * Range:      [0 .. 670760.96]
     * Unit:       km/h
     * Resolution: 0.01 km/h
     * </pre>
     */
    @KnxDataPointType(id = "9.028", description = "Wind Speed")
    public static final DPT9 WIND_SPEED_KMH = new DPT9("9.028", "Wind Speed", 0f, 670760.96, "km/h");

    /**
     * Constructor for {@link DPT9}
     *
     * @param id
     * @param desc
     * @param lowerValue
     * @param upperValue
     * @param unit
     */
    private DPT9(final String id, final String desc, final double lowerValue, final double upperValue, final String unit) {
        super(id, desc, lowerValue, upperValue, unit);
    }

    @Override
    protected boolean isCompatible(final byte[] bytes) {
        return bytes.length == 2;
    }

    @Override
    protected DPT9Value parse(final byte[] bytes) {
        return new DPT9Value(this, bytes);
    }

    @Override
    protected boolean isCompatible(final String[] args) {
        return args.length == 1;
    }

    @Override
    protected DPT9Value parse(final String[] args) {
        return new DPT9Value(this, Double.parseDouble(args[0]));
    }

    public DPT9Value toValue(final double value) {
        return new DPT9Value(this, value);
    }

    public byte[] toByteArray(final double value) {
        return DPT9Value.toByteArray(value);
    }

}
