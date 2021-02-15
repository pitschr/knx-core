/*
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

import li.pitschmann.knx.core.datapoint.value.DataPointEnumValue;

/**
 * Data Point Type 20 for '8-Bit Enumeration' (1 Octet)
 *
 * <pre>
 *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
 * Field Names | (Field 1)                     |
 * Encoding    | N   N   N   N   N   N   N   N |
 *             +---+---+---+---+---+---+---+---+
 * Format:     8 bit (N<sub>8</sub>)
 * </pre>
 *
 * @author PITSCHR
 */
public final class DPT20 {
    private DPT20() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * <strong>20.001</strong> SCLO Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Autonomous
     *                  1 = Slave
     *                  2 = Master
     * </pre>
     */
    @DataPoint(value = {"20.001", "dpst-20-1"}, description = "SCLO Mode")
    public enum SCLOMode implements DataPointEnum<SCLOMode> {
        @DataPointEnumValue(value = 0, description = "Autonomous")
        AUTONOMOUS, //
        @DataPointEnumValue(value = 1, description = "Slave")
        SLAVE, //
        @DataPointEnumValue(value = 2, description = "Master")
        MASTER
    }

    /**
     * <strong>20.002</strong> Building Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Building in use
     *                  1 = Building not used
     *                  2 = Building protection
     * </pre>
     */
    @DataPoint(value = {"20.002", "dpst-20-2"}, description = "Building Mode")
    public enum BuildingMode implements DataPointEnum<BuildingMode> {
        @DataPointEnumValue(value = 0, description = "Building in use")
        BUILDING_IN_USE, //
        @DataPointEnumValue(value = 1, description = "Building not used")
        BUILDING_NOT_USED, //
        @DataPointEnumValue(value = 2, description = "Building protection")
        BUILDING_PROTECTION
    }

    /**
     * <strong>20.003</strong> Occupancy Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Occupied
     *                  1 = Standby
     *                  2 = Not Occupied
     * </pre>
     */
    @DataPoint(value = {"20.003", "dpst-20-3"}, description = "Occupancy Mode")
    public enum OccupancyMode implements DataPointEnum<OccupancyMode> {
        @DataPointEnumValue(value = 0, description = "Occupied")
        OCCUPIED, //
        @DataPointEnumValue(value = 1, description = "Standby")
        STANDBY, //
        @DataPointEnumValue(value = 2, description = "Not Occupied")
        NOT_OCCUPIED
    }

    /**
     * <strong>20.004</strong> Priority
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = High
     *                  1 = Medium
     *                  2 = Low
     *                  3 = (void)
     * </pre>
     */
    @DataPoint(value = {"20.004", "dpst-20-4"}, description = "Priority")
    public enum Priority implements DataPointEnum<Priority> {
        @DataPointEnumValue(value = 0, description = "High")
        HIGH, //
        @DataPointEnumValue(value = 1, description = "Medium")
        MEDIUM, //
        @DataPointEnumValue(value = 2, description = "Low")
        LOW, //
        @DataPointEnumValue(value = 3, description = "(void)")
        VOID
    }

    /**
     * <strong>20.005</strong> Light Application Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Normal
     *                  1 = Presence Simulation
     *                  2 = Night Round
     * </pre>
     */
    @DataPoint(value = {"20.005", "dpst-20-5"}, description = "Light Application Mode")
    public enum LightApplicationMode implements DataPointEnum<LightApplicationMode> {
        @DataPointEnumValue(value = 0, description = "Normal")
        NORMAL, //
        @DataPointEnumValue(value = 1, description = "Presence Simulation")
        PRESENCE_SIMULATION, //
        @DataPointEnumValue(value = 2, description = "Night Round")
        NIGHT_ROUND
    }

    /**
     * <strong>20.006</strong> Application Area
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   N   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>2</sub> N<sub>6</sub>)
     * Range:      N = {0, 1, 10, 11, 12, 13, 14, 20, 30, 40, 50}
     *                  0 = No Fault
     *                  1 = System and Functions of common interest
     *                 10 = HVAC General FBs
     *                 11 = HVAC Hot Water Heating
     *                 12 = HVAC Direct Electrical Heating
     *                 13 = HVAC Terminal Units
     *                 14 = HVAC VAC
     *                 20 = Lighting
     *                 30 = Security
     *                 40 = Load Management
     *                 50 = Shutters and Blinds
     * </pre>
     */
    @DataPoint(value = {"20.006", "dpst-20-6"}, description = "Application Area")
    public enum ApplicationArea implements DataPointEnum<ApplicationArea> {
        @DataPointEnumValue(value = 0, description = "No Fault")
        NO_FAULT, //
        @DataPointEnumValue(value = 1, description = "System and Functions of common interest")
        SYSTEM_AND_FUNCTIONS, //
        @DataPointEnumValue(value = 10, description = "HVAC General FBs")
        HVAC_GENERAL, //
        @DataPointEnumValue(value = 11, description = "HVAC Hot Water Heating")
        HVAC_HOT_WATER_HEATING, //
        @DataPointEnumValue(value = 12, description = "HVAC Direct Electrical Heating")
        HVAC_DIRECT_ELECTRICAL_HEATING, //
        @DataPointEnumValue(value = 13, description = "HVAC Terminal Units")
        HVAC_TERMINAL_UNITS, //
        @DataPointEnumValue(value = 14, description = "HVAC VAC")
        HVAC_VAC, //
        @DataPointEnumValue(value = 20, description = "Lighting")
        LIGHTING, //
        @DataPointEnumValue(value = 30, description = "Security")
        SECURITY, //
        @DataPointEnumValue(value = 40, description = "Load Management")
        LOAD_MANAGEMENT, //
        @DataPointEnumValue(value = 50, description = "Shutters and Blinds")
        SHUTTERS_AND_BLINDS
    }

    /**
     * <strong>20.007</strong> Alarm Class Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [1 .. 3]
     *                  0 = (reserved); not used
     *                  1 = Simple Alarm
     *                  2 = Basic Alarm
     *                  3 = Extended Alarm
     * </pre>
     */
    @DataPoint(value = {"20.007", "dpst-20-7"}, description = "Alarm Class Type")
    public enum AlarmClassType implements DataPointEnum<AlarmClassType> {
        @DataPointEnumValue(value = 1, description = "Simple Alarm")
        SIMPLE, //
        @DataPointEnumValue(value = 2, description = "Basic Alarm")
        BASIC, //
        @DataPointEnumValue(value = 3, description = "Extended Alarm")
        EXTENDED
    }

    /**
     * <strong>20.008</strong> PSU Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Disabled
     *                  1 = Enabled
     *                  2 = Auto
     * </pre>
     */
    @DataPoint(value = {"20.008", "dpst-20-8"}, description = "PSU Mode")
    public enum PSUMode implements DataPointEnum<PSUMode> {
        @DataPointEnumValue(value = 0, description = "Disabled")
        DISABLED, //
        @DataPointEnumValue(value = 1, description = "Enabled")
        ENABLED, //
        @DataPointEnumValue(value = 2, description = "Auto")
        AUTO
    }

    /**
     * <strong>20.011</strong> Error Class Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>3</sub> N<sub>5</sub>)
     * Range:      N = [0 .. 18]
     *                  0 = No Fault
     *                  1 = General Device Fault (e.g. RAM, EEPROM, UI, watchdog, ...)
     *                  2 = Communication Fault
     *                  3 = Configuration Fault
     *                  4 = Hardware Fault
     *                  5 = Software Fault
     *                  6 = Insufficient non volatile memory
     *                  7 = Insufficient volatile memory
     *                  8 = Memory allocation command with size 0 received
     *                  9 = CRC-error
     *                 10 = Watchdog reset detected
     *                 11 = Invalid opcode detected
     *                 12 = General Protection Fault
     *                 13 = Maximal table length exceeded
     *                 14 = Undefined load command received
     *                 15 = Group Address Table is not sorted
     *                 16 = Invalid connection number (TSAP)
     *                 17 = Invalid Group Object number (ASAP)
     *                 18 = Group Object Type exceeds
     * </pre>
     */
    @DataPoint(value = {"20.011", "dpst-20-11"}, description = "Error Class Type")
    public enum ErrorClassType implements DataPointEnum<ErrorClassType> {
        @DataPointEnumValue(value = 0, description = "No Fault")
        NO_FAULT, //
        @DataPointEnumValue(value = 1, description = "General Device Fault (e.g. RAM, EEPROM, UI, watchdog, ...)")
        GENERAL_DEVICE_FAULT, //
        @DataPointEnumValue(value = 2, description = "Communication Fault")
        COMMUNICATION_FAULT, //
        @DataPointEnumValue(value = 3, description = "Configuration Fault")
        CONFIGURATION_FAULT, //
        @DataPointEnumValue(value = 4, description = "Hardware Fault")
        HARDWARE_FAULT, //
        @DataPointEnumValue(value = 5, description = "Software Fault")
        SOFTWARE_FAULT, //
        @DataPointEnumValue(value = 6, description = "Insufficient non volatile memory")
        INSUFFICIENT_NON_VOLATILE_MEMORY, //
        @DataPointEnumValue(value = 7, description = "Insufficient volatile memory")
        INSUFFICIENT_VOLATILE_MEMORY, //
        @DataPointEnumValue(value = 8, description = "Memory allocation command with size 0 received")
        MEMORY_ALLOCATION_COMMAND_WITH_SIZE_0_RECEIVED, //
        @DataPointEnumValue(value = 9, description = "CRC-error")
        CRC_ERROR, //
        @DataPointEnumValue(value = 10, description = "Watchdog reset detected")
        WATCHDOG_RESET_DETECTED, //
        @DataPointEnumValue(value = 11, description = "Invalid opcode detected")
        INVALID_OPCODE_DETECTED, //
        @DataPointEnumValue(value = 12, description = "General Protection Fault")
        GENERAL_PROTECTION_FAULT, //
        @DataPointEnumValue(value = 13, description = "Maximal table length exceeded")
        MAXIMAL_TABLE_LENGTH_EXCEEDED, //
        @DataPointEnumValue(value = 14, description = "Undefined load command received")
        UNDEFINED_LOAD_COMMAND_RECEIVED, //
        @DataPointEnumValue(value = 15, description = "Group Address Table is not sorted")
        GROUP_ADDRESS_TABLE_IS_NOT_SORTED, //
        @DataPointEnumValue(value = 16, description = "Invalid connection number (TSAP)")
        INVALID_CONNECTION_NUMBER, //
        @DataPointEnumValue(value = 17, description = "Invalid Group Object number (ASAP)")
        INVALID_GROUP_OBJECT_NUMBER, //
        @DataPointEnumValue(value = 18, description = "Group Object Type exceeds")
        GROUP_OBJECT_TYPE_EXCEEDS
    }

    /**
     * <strong>20.012</strong> Error Class HVAC
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = No Fault
     *                  1 = Sensor Fault
     *                  2 = Process Fault / Controller Fault
     *                  3 = Actuator Fault
     *                  4 = Other Fault
     * </pre>
     */
    @DataPoint(value = {"20.012", "dpst-20-12"}, description = "HVAC Error Class Type")
    public enum ErrorClassHVAC implements DataPointEnum<ErrorClassHVAC> {
        @DataPointEnumValue(value = 0, description = "No Fault")
        NO_FAULT, //
        @DataPointEnumValue(value = 1, description = "Sensor Fault")
        SENSOR_FAULT, //
        @DataPointEnumValue(value = 2, description = "Process Fault / Controller Fault")
        PROCESS_OR_CONTROLLER_FAULT, //
        @DataPointEnumValue(value = 3, description = "Actuator Fault")
        ACTUATOR_FAULT, //
        @DataPointEnumValue(value = 4, description = "Other Fault")
        OTHER_FAULT
    }

    /**
     * <strong>20.013</strong> Time Delay
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>3</sub> N<sub>5</sub>)
     * Range:      N = [0 .. 25]
     *                  0 = not active
     *                  1 = 1 s
     *                  2 = 2 s
     *                  3 = 3 s
     *                  4 = 5 s
     *                  5 = 10 s
     *                  6 = 15 s
     *                  7 = 20 s
     *                  8 = 30 s
     *                  9 = 45 s
     *                 10 = 1 min
     *                 11 = 1 1/4 min
     *                 12 = 1 1/2 min
     *                 13 = 2 min
     *                 14 = 2 1/2 min
     *                 15 = 3 min
     *                 16 = 5 min
     *                 17 = 15 min
     *                 18 = 20 min
     *                 19 = 30 min
     *                 20 = 1 h
     *                 21 = 2 h
     *                 22 = 3 h
     *                 23 = 5 h
     *                 24 = 12 h
     *                 25 = 24 h
     * </pre>
     */
    @DataPoint(value = {"20.013", "dpst-20-13"}, description = "Time Delay")
    public enum TimeDelay implements DataPointEnum<TimeDelay> {
        @DataPointEnumValue(value = 0, description = "not active")
        NO_DELAY, //
        @DataPointEnumValue(value = 1, description = "1 s")
        DELAY_1SEC, //
        @DataPointEnumValue(value = 2, description = "2 s")
        DELAY_2SEC, //
        @DataPointEnumValue(value = 3, description = "3 s")
        DELAY_3SEC, //
        @DataPointEnumValue(value = 4, description = "5 s")
        DELAY_5SEC, //
        @DataPointEnumValue(value = 5, description = "10 s")
        DELAY_10SEC, //
        @DataPointEnumValue(value = 6, description = "15 s")
        DELAY_15SEC, //
        @DataPointEnumValue(value = 7, description = "20 s")
        DELAY_20SEC, //
        @DataPointEnumValue(value = 8, description = "30 s")
        DELAY_30SEC, //
        @DataPointEnumValue(value = 9, description = "45 s")
        DELAY_45SEC, //
        @DataPointEnumValue(value = 10, description = "1 min")
        DELAY_1MIN, //
        @DataPointEnumValue(value = 11, description = "1 1/4 min")
        DELAY_1MIN_AND_15SEC, //
        @DataPointEnumValue(value = 12, description = "1 1/2 min")
        DELAY_1MIN_AND_30SEC, //
        @DataPointEnumValue(value = 13, description = "2 min")
        DELAY_2MIN, //
        @DataPointEnumValue(value = 14, description = "2 1/2 min")
        DELAY_2MIN_AND_30SEC, //
        @DataPointEnumValue(value = 15, description = "3 min")
        DELAY_3MIN, //
        @DataPointEnumValue(value = 16, description = "5 min")
        DELAY_5MIN, //
        @DataPointEnumValue(value = 17, description = "15 min")
        DELAY_15MIN, //
        @DataPointEnumValue(value = 18, description = "20 min")
        DELAY_20MIN, //
        @DataPointEnumValue(value = 19, description = "30 min")
        DELAY_30MIN, //
        @DataPointEnumValue(value = 20, description = "1 h")
        DELAY_1HOUR, //
        @DataPointEnumValue(value = 21, description = "2 h")
        DELAY_2H, //
        @DataPointEnumValue(value = 22, description = "3 h")
        DELAY_3H, //
        @DataPointEnumValue(value = 23, description = "5 h")
        DELAY_5H, //
        @DataPointEnumValue(value = 24, description = "12 h")
        DELAY_12H, //
        @DataPointEnumValue(value = 25, description = "24 h")
        DELAY_24H
    }

    /**
     * <strong>20.014</strong> Beaufort Wind Force Scale
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>4</sub> N<sub>4</sub>)
     * Range:      N = [0 .. 12]
     *                   0 = no wind
     *                   1 = light air
     *                   2 = light breeze
     *                   3 = gentle breeze
     *                   4 = moderate breeze
     *                   5 = fresh breeze
     *                   6 = strong breeze
     *                   7 = near gale / moderate gale
     *                   8 = fresh gale
     *                   9 = strong gale
     *                  10 = whole gale / storm
     *                  11 = violent storm
     *                  12 = hurricane
     * </pre>
     */
    @DataPoint(value = {"20.014", "dpst-20-14"}, description = "Wind Force Scale")
    public enum BeaufortWindForceScale implements DataPointEnum<BeaufortWindForceScale> {
        @DataPointEnumValue(value = 0, description = "no wind")
        NO_WIND, //
        @DataPointEnumValue(value = 1, description = "light air")
        LIGHT_AIR, //
        @DataPointEnumValue(value = 2, description = "light breeze")
        LIGHT_BREEZE, //
        @DataPointEnumValue(value = 3, description = "gentle breeze")
        GENTLE_BREEZE, //
        @DataPointEnumValue(value = 4, description = "moderate breeze")
        MODERATE_BREEZE, //
        @DataPointEnumValue(value = 5, description = "fresh breeze")
        FRESH_BREEZE, //
        @DataPointEnumValue(value = 6, description = "strong breeze")
        STRONG_BREEZE, //
        @DataPointEnumValue(value = 7, description = "near gale / moderate gale")
        MODERATE_GALE, //
        @DataPointEnumValue(value = 8, description = "fresh gale")
        FRESH_GALE, //
        @DataPointEnumValue(value = 9, description = "strong gale")
        STRONG_GALE, //
        @DataPointEnumValue(value = 10, description = "whole gale / storm")
        STORM, //
        @DataPointEnumValue(value = 11, description = "violent storm")
        VIOLENT_STORM, //
        @DataPointEnumValue(value = 12, description = "hurricane")
        HURRICANE
    }

    /**
     * <strong>20.017</strong> Sensor Select/Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = inactive
     *                  1 = digital input not inverted
     *                  2 = digital input inverted
     *                  3 = analog input -> 0% to 100%
     *                  4 = temperature sensor input
     * </pre>
     */
    @DataPoint(value = {"20.017", "dpst-20-17"}, description = "Sensor Mode")
    public enum SensorSelect implements DataPointEnum<SensorSelect> {
        @DataPointEnumValue(value = 0, description = "Inactive")
        INACTIVE, //
        @DataPointEnumValue(value = 1, description = "Digital Input (not inverted)")
        DIGITAL_INPUT_NOT_INVERTED, //
        @DataPointEnumValue(value = 2, description = "Digital Input (inverted)")
        DIGITAL_INPUT_INVERTED, //
        @DataPointEnumValue(value = 3, description = "Analog Input (0..100%)")
        ANALOG_INPUT_0_TO_100, //
        @DataPointEnumValue(value = 4, description = "Temperature Sensor Input")
        TEMPERATURE_SENSOR_INPUT
    }

    /**
     * <strong>20.020</strong> Actuator Connect Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = {1, 2}
     *                  1 = Sensor Connection
     *                  2 = Controller Connection
     * </pre>
     */
    @DataPoint(value = {"20.020", "dpst-20-20"}, description = "Actuator Connect Type")
    public enum ActuatorConnectType implements DataPointEnum<ActuatorConnectType> {
        @DataPointEnumValue(value = 1, description = "Sensor Connection")
        SENSOR, //
        @DataPointEnumValue(value = 2, description = "Controller Connection")
        CONTROLLER
    }

    /**
     * <strong>20.021</strong> Cloud Coverage
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>4</sub> N<sub>4</sub>)
     * Range:      N = [0 .. 9]
     *                  0 = Cloudless
     *                  1 = Sunny
     *                  2 = Sunshiny
     *                  3 = Lightly Cloudy
     *                  4 = Scattered Clouds
     *                  5 = Cloudy
     *                  6 = Very Cloudy
     *                  7 = Slightly Overcast
     *                  8 = Overcast
     *                  9 = Unknown (Sky not recognizable)
     * </pre>
     */
    @DataPoint(value = {"20.021", "dpst-20-21"}, description = "Cloud Coverage")
    public enum CloudCoverage implements DataPointEnum<CloudCoverage> {
        @DataPointEnumValue(value = 0, description = "Cloudless")
        CLOUDLESS, //
        @DataPointEnumValue(value = 1, description = "Sunny")
        SUNNY, //
        @DataPointEnumValue(value = 2, description = "Sunshiny")
        SUNSHINY, //
        @DataPointEnumValue(value = 3, description = "Lightly Cloudy")
        LIGHT_CLOUDY, //
        @DataPointEnumValue(value = 4, description = "Scattered Clouds")
        SCATTERED_CLOUDS, //
        @DataPointEnumValue(value = 5, description = "Cloudy")
        CLOUDY, //
        @DataPointEnumValue(value = 6, description = "Very Cloudy")
        VERY_CLOUDY, //
        @DataPointEnumValue(value = 7, description = "Slightly Overcast")
        SLIGHTLY_OVERCAST, //
        @DataPointEnumValue(value = 8, description = "Overcast")
        OVERCAST, //
        @DataPointEnumValue(value = 9, description = "Unknown")
        UNKNOWN //
    }

    /**
     * <strong>20.022</strong> Power Return Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Do Not Send
     *                  1 = Send Always
     *                  2 = Send On Change
     * </pre>
     */
    @DataPoint(value = {"20.022", "dpst-20-22"}, description = "Power Return Mode")
    public enum PowerReturnMode implements DataPointEnum<PowerReturnMode> {
        @DataPointEnumValue(value = 0, description = "Do Not Send")
        DO_NOT_SEND, //
        @DataPointEnumValue(value = 1, description = "Send Always")
        SEND_ALWAYS, //
        @DataPointEnumValue(value = 2, description = "Send On Change")
        SEND_ON_CHANGE //
    }

    /**
     * <strong>20.100</strong> Fuel Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = Auto
     *                  1 = Oil
     *                  2 = Gas
     *                  3 = Solid State Fuel
     * </pre>
     */
    @DataPoint(value = {"20.100", "dpst-20-100"}, description = "Fuel Type")
    public enum FuelType implements DataPointEnum<FuelType> {
        @DataPointEnumValue(value = 0, description = "Auto")
        AUTO, //
        @DataPointEnumValue(value = 1, description = "Oil")
        OIL, //
        @DataPointEnumValue(value = 2, description = "Gas")
        GAS, //
        @DataPointEnumValue(value = 3, description = "Solid State Fuel")
        SOLID_STATE_FUEL
    }

    /**
     * <strong>20.101</strong> Burner Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [1 .. 3]
     *                  1 = Stage 1
     *                  2 = Stage 2
     *                  3 = Modulating
     * </pre>
     */
    @DataPoint(value = {"20.101", "dpst-20-101"}, description = "Burner Type")
    public enum BurnerType implements DataPointEnum<BurnerType> {
        @DataPointEnumValue(value = 1, description = "Stage 1")
        STAGE_1, //
        @DataPointEnumValue(value = 2, description = "Stage 2")
        STAGE_2, //
        @DataPointEnumValue(value = 3, description = "Modulating")
        MODULATING
    }

    /**
     * <strong>20.102</strong> HVAC Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = Auto
     *                  1 = Comfort
     *                  2 = Standby
     *                  3 = Economy
     *                  4 = Building Protection
     * </pre>
     */
    @DataPoint(value = {"20.102", "dpst-20-102"}, description = "HVAC Mode")
    public enum HVACMode implements DataPointEnum<HVACMode> {
        @DataPointEnumValue(value = 0, description = "Auto")
        AUTO, //
        @DataPointEnumValue(value = 1, description = "Comfort")
        COMFORT, //
        @DataPointEnumValue(value = 2, description = "Standby")
        STANDBY, //
        @DataPointEnumValue(value = 3, description = "Economy")
        ECONOMY, //
        @DataPointEnumValue(value = 4, description = "Building Protection")
        BUILDING_PROTECTION
    }

    /**
     * <strong>20.103</strong> DHW Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = Auto
     *                  1 = Legionella Protection
     *                  2 = Normal
     *                  3 = Reduced
     *                  4 = Off / Frost Protection
     * </pre>
     */
    @DataPoint(value = {"20.103", "dpst-20-103"}, description = "DHW Mode")
    public enum DHWMode implements DataPointEnum<DHWMode> {
        @DataPointEnumValue(value = 0, description = "Auto")
        AUTO, //
        @DataPointEnumValue(value = 1, description = "Legionella Protection")
        LEGIONELLA_PROTECTION, //
        @DataPointEnumValue(value = 2, description = "Normal")
        NORMAL, //
        @DataPointEnumValue(value = 3, description = "Reduced")
        REDUCED, //
        @DataPointEnumValue(value = 4, description = "Off / Frost Protection")
        OFF_FROST_PROTECTION
    }

    /**
     * <strong>20.104</strong> Load Priority
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = None
     *                  1 = Shift Load Priority
     *                  2 = Absolute Load Priority
     * </pre>
     */
    @DataPoint(value = {"20.104", "dpst-20-104"}, description = "Load Priority")
    public enum LoadPriority implements DataPointEnum<LoadPriority> {
        @DataPointEnumValue(value = 0, description = "None")
        NONE, //
        @DataPointEnumValue(value = 1, description = "Shift Load Priority")
        SHIFT, //
        @DataPointEnumValue(value = 2, description = "Absolute Load Priority")
        ABSOLUTE
    }

    /**
     * <strong>20.105</strong> HVAC Control Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>3</sub> N<sub>5</sub>)
     * Range:      N = [0 .. 20]
     *                  0 = Auto
     *                  1 = Heat
     *                  2 = Morning Warmup
     *                  3 = Cool
     *                  4 = Night Purge
     *                  5 = Precool
     *                  6 = Off
     *                  7 = Test
     *                  8 = Emergency Heat
     *                  9 = Fan only
     *                  10 = Free Cool
     *                  11 = Ice
     *                  12 = Maximum Heating Mode
     *                  13 = Economic Heat/Cool Mode
     *                  14 = Dehumidification
     *                  15 = Calibration Mode
     *                  16 = Emergency Cool Mode
     *                  17 = Emergency Steam Mode
     *                  18 = reserved
     *                  19 = reserved
     *                  20 = NoDem
     * </pre>
     */
    @DataPoint(value = {"20.105", "dpst-20-105"}, description = "HVAC Control Mode")
    public enum HVACControlMode implements DataPointEnum<HVACControlMode> {
        @DataPointEnumValue(value = 0, description = "Auto")
        AUTO, //
        @DataPointEnumValue(value = 1, description = "Heat")
        HEAT, //
        @DataPointEnumValue(value = 2, description = "Morning Warmup")
        MORNING_WARMUP, //
        @DataPointEnumValue(value = 3, description = "Cool")
        COOL, //
        @DataPointEnumValue(value = 4, description = "Night Purge")
        NIGHT_PURGE, //
        @DataPointEnumValue(value = 5, description = "Precool")
        PRECOOL, //
        @DataPointEnumValue(value = 6, description = "Off")
        OFF, //
        @DataPointEnumValue(value = 7, description = "Test")
        TEST, //
        @DataPointEnumValue(value = 8, description = "Emergency Heat")
        EMERGENCY_HEAT, //
        @DataPointEnumValue(value = 9, description = "Fan only")
        FAN_ONLY, //
        @DataPointEnumValue(value = 10, description = "Free Cool")
        FREE_COOL, //
        @DataPointEnumValue(value = 11, description = "Ice")
        ICE, //
        @DataPointEnumValue(value = 12, description = "Maximum Heating Mode")
        MAXIMUM_HEATING, //
        @DataPointEnumValue(value = 13, description = "Economic Heat/Cool Mode")
        ECONOMIC_HEAT_COOL, //
        @DataPointEnumValue(value = 14, description = "Dehumidification")
        DEHUMIDIFICATION, //
        @DataPointEnumValue(value = 15, description = "Calibration Mode")
        CALIBRATION_MODE, //
        @DataPointEnumValue(value = 16, description = "Emergency Cool Mode")
        EMERGENCY_COOL, //
        @DataPointEnumValue(value = 17, description = "Emergency Steam Mode")
        EMERGENCY_STEAM, //
        @DataPointEnumValue(value = 20, description = "NoDem")
        NO_DEM
    }

    /**
     * <strong>20.106</strong> HVAC Emergency Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 5]
     *                  0 = Normal
     *                  1 = Emergency Pressure
     *                  2 = Emergency Depressure
     *                  3 = Emergency Purge
     *                  4 = Emergency Shutdown
     *                  5 = Emergency Fire
     * </pre>
     */
    @DataPoint(value = {"20.106", "dpst-20-106"}, description = "HVAC Emergency Mode")
    public enum HVACEmergencyMode implements DataPointEnum<HVACEmergencyMode> {
        @DataPointEnumValue(value = 0, description = "Normal")
        NORMAL, //
        @DataPointEnumValue(value = 1, description = "Emergency Pressure")
        PRESSURE, //
        @DataPointEnumValue(value = 2, description = "Emergency Depressure")
        DEPRESSURE, //
        @DataPointEnumValue(value = 3, description = "Emergency Purge")
        PURGE, //
        @DataPointEnumValue(value = 4, description = "Emergency Shutdown")
        SHUTDOWN, //
        @DataPointEnumValue(value = 5, description = "Emergency Fire")
        FIRE
    }

    /**
     * <strong>20.107</strong> Changeover Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Auto
     *                  1 = Cooling Only
     *                  2 = Heating Only
     * </pre>
     */
    @DataPoint(value = {"20.107", "dpst-20-107"}, description = "Changeover Mode")
    public enum ChangeoverMode implements DataPointEnum<ChangeoverMode> {
        @DataPointEnumValue(value = 0, description = "Auto")
        AUTO, //
        @DataPointEnumValue(value = 1, description = "Cooling Only")
        COOLING_ONLY, //
        @DataPointEnumValue(value = 2, description = "Heating Only")
        HEATING_ONLY
    }

    /**
     * <strong>20.108</strong> Valve Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 5]
     *                  1 = Heat stage A for normal heating
     *                  2 = Heat stage B for heating with two stages (A + B)
     *                  3 = Cool stage A for normal cooling
     *                  4 = Cool stage B for cooling with two stages (A + B)
     *                  5 = Heat/Cool for changeover applications
     * </pre>
     */
    @DataPoint(value = {"20.108", "dpst-20-108"}, description = "Valve Mode")
    public enum ValveMode implements DataPointEnum<ValveMode> {
        @DataPointEnumValue(value = 1, description = "Heat stage A for normal heating")
        HEAT_STAGE_A_FOR_HEATING, //
        @DataPointEnumValue(value = 2, description = "Heat stage B for heating with two stages (A + B)")
        HEAT_STAGE_B_FOR_HEATING_WITH_TWO_STAGES, //
        @DataPointEnumValue(value = 3, description = "Cool stage A for normal cooling")
        COOL_STAGE_A_FOR_COOLING, //
        @DataPointEnumValue(value = 4, description = "Cool stage B for cooling with two stages (A + B)")
        COOL_STAGE_B_FOR_COOLING_WITH_TWO_STAGES, //
        @DataPointEnumValue(value = 5, description = "Heat/Cool for changeover applications")
        HEAT_AND_COOL_CHANGEOVE
    }

    /**
     * <strong>20.109</strong> Damper Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  1 = Fresh air, e.g. for fancoils
     *                  2 = Supply Air. e.g. for VAV
     *                  3 = Extract Air e.g. for VAV
     *                  4 = Extract Air 2 e.g. for VAV
     * </pre>
     */
    @DataPoint(value = {"20.109", "dpst-20-109"}, description = "Damper Mode")
    public enum DamperMode implements DataPointEnum<DamperMode> {
        @DataPointEnumValue(value = 1, description = "Fresh air, e.g. for fancoils")
        FRESH_AIR, //
        @DataPointEnumValue(value = 2, description = "Supply Air. e.g. for VAV")
        SUPPLY_AIR, //
        @DataPointEnumValue(value = 3, description = "Extract Air e.g. for VAV")
        EXTRACT_AIR, //
        @DataPointEnumValue(value = 4, description = "Extract Air 2 e.g. for VAV")
        EXTRACT_AIR_2
    }

    /**
     * <strong>20.110</strong> Heater Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  1 = Heat Stage A On/Off
     *                  2 = Heat Stage A Proportional
     *                  3 = Heat Stage B Proportional
     * </pre>
     */
    @DataPoint(value = {"20.110", "dpst-20-110"}, description = "Heater Mode")
    public enum HeaterMode implements DataPointEnum<HeaterMode> {
        @DataPointEnumValue(value = 1, description = "Heat Stage A On/Off")
        HEAT_STAGE_A_ON_OFF, //
        @DataPointEnumValue(value = 2, description = "Heat Stage A Proportional")
        HEAT_STAGE_A_PROPORTIONAL, //
        @DataPointEnumValue(value = 3, description = "Heat Stage B Proportional")
        HEAT_STAGE_B_PROPORTIONAL
    }

    /**
     * <strong>20.111</strong> Fan Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Not Running
     *                  1 = Permanently Running
     *                  2 = Running in Intervals
     * </pre>
     */
    @DataPoint(value = {"20.111", "dpst-20-111"}, description = "Fan Mode")
    public enum FanMode implements DataPointEnum<FanMode> {
        @DataPointEnumValue(value = 0, description = "Not Running")
        NOT_RUNNING, //
        @DataPointEnumValue(value = 1, description = "Permanently Running")
        RUNNING_PERMANENTLY, //
        @DataPointEnumValue(value = 2, description = "Running in Intervals")
        RUNNING_IN_INTERVALS
    }

    /**
     * <strong>20.112</strong> Master/Slave Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Auto
     *                  1 = Master
     *                  2 = Slave
     * </pre>
     */
    @DataPoint(value = {"20.112", "dpst-20-112"}, description = "Master/Slave Mode")
    public enum MasterSlaveMode implements DataPointEnum<MasterSlaveMode> {
        @DataPointEnumValue(value = 0, description = "Auto")
        AUTO, //
        @DataPointEnumValue(value = 1, description = "Master")
        MASTER, //
        @DataPointEnumValue(value = 2, description = "Slave")
        SLAVE
    }

    /**
     * <strong>20.113</strong> Status Room Setpoint
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Normal Setpoint
     *                  1 = Alternative Setpoint
     *                  2 = Building Protection Setpoint
     * </pre>
     */
    @DataPoint(value = {"20.113", "dpst-20-113"}, description = "Status Room Setpoint")
    public enum StatusRoomSetpoint implements DataPointEnum<StatusRoomSetpoint> {
        @DataPointEnumValue(value = 0, description = "Normal Setpoint")
        NORMAL, //
        @DataPointEnumValue(value = 1, description = "Alternative Setpoint")
        ALTERNATIVE, //
        @DataPointEnumValue(value = 2, description = "Building Protection Setpoint")
        BUILDING_PROTECTION
    }

    /**
     * <strong>20.114</strong> Metering Device Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | N   N   N   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = {0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 32, 33, 40, 41, 255}
     *                  0 = Other device type
     *                  1 = Oil meter
     *                  2 = Electricity meter
     *                  3 = Gas meter
     *                  4 = Heat meter
     *                  5 = Steam meter
     *                  6 = Warm Water meter
     *                  7 = Water meter
     *                  8 = Heat cost allocator
     *                 10 = Cooling Load meter (outlet)
     *                 11 = Cooling Load meter (inlet)
     *                 12 = Heat (inlet)
     *                 13 = Heat and Cool
     *                 32 = breaker (electricity)
     *                 33 = valve (gas or water)
     *                 40 = waste water meter
     *                 41 = garbage
     *                255 = void device type
     * </pre>
     */
    @DataPoint(value = {"20.114", "dpst-20-114"}, description = "Metering Device Type")
    public enum MeteringDeviceType implements DataPointEnum<MeteringDeviceType> {
        @DataPointEnumValue(value = 0, description = "Other device type")
        OTHER_DEVICE_TYPE, //
        @DataPointEnumValue(value = 1, description = "Oil meter")
        OIL_METER, //
        @DataPointEnumValue(value = 2, description = "Electricity meter")
        ELECTRICITY_METER, //
        @DataPointEnumValue(value = 3, description = "Gas meter")
        GAS_METER, //
        @DataPointEnumValue(value = 4, description = "Heat meter")
        HEAT_METER, //
        @DataPointEnumValue(value = 5, description = "Steam meter")
        STEAM_METER, //
        @DataPointEnumValue(value = 6, description = "Warm Water meter")
        WARM_WATER_METER, //
        @DataPointEnumValue(value = 7, description = "Water meter")
        WATER_METER, //
        @DataPointEnumValue(value = 8, description = "Heat cost allocator")
        HEAT_COST_ALLOCATOR, //
        @DataPointEnumValue(value = 10, description = "Cooling Load meter (outlet)")
        COOLING_LOAD_METER_OUTLET, //
        @DataPointEnumValue(value = 11, description = "Cooling Load meter (inlet)")
        COOLING_LOAD_METER_INLET, //
        @DataPointEnumValue(value = 12, description = "Heat")
        HEAT, //
        @DataPointEnumValue(value = 13, description = "Heat and Cool")
        HEAT_AND_COOL, //
        @DataPointEnumValue(value = 32, description = "breaker (electricity)")
        BREAKER, //
        @DataPointEnumValue(value = 33, description = "valve (gas or water)")
        VALVE, //
        @DataPointEnumValue(value = 40, description = "waste water meter")
        WASTE_WATER_METER, //
        @DataPointEnumValue(value = 41, description = "garbage")
        GARBAGE, //
        @DataPointEnumValue(value = 255, description = "void device type")
        VOID
    }

    /**
     * <strong>20.115</strong> Humidification Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Inactive
     *                  1 = Humidification
     *                  2 = De-Humidification
     * </pre>
     */
    @DataPoint(value = {"20.115", "dpst-20-115"}, description = "Humidification Mode")
    public enum HumidificationMode implements DataPointEnum<HumidificationMode> {
        @DataPointEnumValue(value = 0, description = "Inactive")
        INACTIVE, //
        @DataPointEnumValue(value = 1, description = "Humidification")
        HUMIDIFICATION, //
        @DataPointEnumValue(value = 2, description = "De-Humidification")
        DEHUMIDIFICATION
    }

    // TODO: ETS 20.116 Enable A/C Stage ???

    /**
     * <strong>20.120</strong> ADA Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [1 .. 2]
     *                  1 = Air Damper
     *                  2 = VAV
     * </pre>
     */
    @DataPoint(value = {"20.120", "dpst-20-120"}, description = "ADA Type")
    public enum ADAType implements DataPointEnum<ADAType> {
        @DataPointEnumValue(value = 1, description = "Air Damper")
        AIR_DAMPER, //
        @DataPointEnumValue(value = 2, description = "VAV")
        VAV
    }

    /**
     * <strong>20.121</strong> Backup Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   0   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>7</sub> N<sub>1</sub>)
     * Range:      N = [0 .. 1]
     *                  0 = Backup Value
     *                  1 = Keep Last State
     * </pre>
     */
    @DataPoint(value = {"20.121", "dpst-20-121"}, description = "Backup Mode")
    public enum BackupMode implements DataPointEnum<BackupMode> {
        @DataPointEnumValue(value = 0, description = "Backup Value")
        BACKUP_VALUE, //
        @DataPointEnumValue(value = 1, description = "Keep Last State")
        KEEP_LAST_STATE
    }

    /**
     * <strong>20.122</strong> Start Synchronization
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Position Unchanged
     *                  1 = Single Close
     *                  2 = Single Open
     * </pre>
     */
    @DataPoint(value = {"20.122", "dpst-20-122"}, description = "Start Synchronization")
    public enum StartSynchronization implements DataPointEnum<StartSynchronization> {
        @DataPointEnumValue(value = 0, description = "Position Unchanged")
        POSITION_UNCHANGED, //
        @DataPointEnumValue(value = 1, description = "Single Close")
        SINGLE_CLOSE, //
        @DataPointEnumValue(value = 2, description = "Single Open")
        SINGLE_OPEN
    }

    /**
     * <strong>20.600</strong> Behavior Lock/Unlock
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 6]
     *                  0 = Off
     *                  1 = On
     *                  2 = No Change
     *                  3 = value according additional parameter
     *                  4 = memory function value
     *                  5 = updated value
     *                  6 = value before locking
     * </pre>
     */
    @DataPoint(value = {"20.600", "dpst-20-600"}, description = "Behavior Lock/Unlock")
    public enum BehaviorLockUnlock implements DataPointEnum<BehaviorLockUnlock> {
        @DataPointEnumValue(value = 0, description = "Off")
        OFF, //
        @DataPointEnumValue(value = 1, description = "On")
        ON, //
        @DataPointEnumValue(value = 2, description = "No Change")
        NO_CHANGE, //
        @DataPointEnumValue(value = 3, description = "value according additional parameter")
        VALUE_ACCORDING_ADDITIONAL_PARAMETER, //
        @DataPointEnumValue(value = 4, description = "memory function value")
        MEMORY_FUNCTION_VALUE, //
        @DataPointEnumValue(value = 5, description = "updated value")
        UPDATED_VALUE, //
        @DataPointEnumValue(value = 6, description = "value before locking")
        VALUE_BEFORE_LOCKING
    }

    /**
     * <strong>20.601</strong> Behavior Bus Power Up/Down
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = Off
     *                  1 = On
     *                  2 = No Change
     *                  3 = value according additional parameter
     *                  4 = last value before bus power down
     * </pre>
     */
    @DataPoint(value = {"20.601", "dpst-20-601"}, description = "Behavior Bus Power Up/Down")
    public enum BehaviorBusPowerUpDown implements DataPointEnum<BehaviorBusPowerUpDown> {
        @DataPointEnumValue(value = 0, description = "Off")
        OFF, //
        @DataPointEnumValue(value = 1, description = "On")
        ON, //
        @DataPointEnumValue(value = 2, description = "No Change")
        NO_CHANGE, //
        @DataPointEnumValue(value = 3, description = "value according additional parameter")
        VALUE_ACCORDING_ADDITIONAL_PARAMETER, //
        @DataPointEnumValue(value = 4, description = "last value before bus power down")
        LAST_VALUE_BEFORE_BUS_POWER_DOWN
    }

    /**
     * <strong>20.602</strong> DALI Fade Time
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>4</sub> N<sub>4</sub>)
     * Range:      N = [0 .. 15]
     *                  0 = 0 s (no fade)
     *                  1 = 0,7 s
     *                  2 = 1,0 s
     *                  3 = 1,4 s
     *                  4 = 2,0 s
     *                  5 = 2,8 s
     *                  6 = 4,0 s
     *                  7 = 5,7 s
     *                  8 = 8,0 s
     *                  9 = 11,3 s
     *                 10 = 16,0 s
     *                 11 = 22,6 s
     *                 12 = 32,0 s
     *                 13 = 45,3 s
     *                 14 = 64,0 s
     *                 15 = 90,5 s
     * </pre>
     */
    @DataPoint(value = {"20.602", "dpst-20-602"}, description = "DALI Fade Time")
    public enum DALIFadeTime implements DataPointEnum<DALIFadeTime> {
        @DataPointEnumValue(value = 0, description = "0 s (no fade)")
        NO_FADE, //
        @DataPointEnumValue(value = 1, description = "0,7 s")
        FADE_700MS, //
        @DataPointEnumValue(value = 2, description = "1,0 s")
        FADE_1SEC, //
        @DataPointEnumValue(value = 3, description = "1,4 s")
        FADE_1SEC_AND_400MS, //
        @DataPointEnumValue(value = 4, description = "2,0 s")
        FADE_2SEC, //
        @DataPointEnumValue(value = 5, description = "2,8 s")
        FADE_2SEC_AND_800MS, //
        @DataPointEnumValue(value = 6, description = "4,0 s")
        FADE_4SEC, //
        @DataPointEnumValue(value = 7, description = "5,7 s")
        FADE_5SEC_AND_700MS, //
        @DataPointEnumValue(value = 8, description = "8,0 s")
        FADE_8SEC, //
        @DataPointEnumValue(value = 9, description = "11,3 s")
        FADE_11SEC_AND_300MS, //
        @DataPointEnumValue(value = 10, description = "16,0 s")
        FADE_16SEC, //
        @DataPointEnumValue(value = 11, description = "22,6 s")
        FADE_22SEC_AND_600MS, //
        @DataPointEnumValue(value = 12, description = "32,0 s")
        FADE_32SEC, //
        @DataPointEnumValue(value = 13, description = "45,3 s")
        FADE_45SEC_AND_300MS, //
        @DataPointEnumValue(value = 14, description = "64,0 s")
        FADE_64SEC, //
        @DataPointEnumValue(value = 15, description = "90,5 s")
        FADE_90SEC_AND_500MS
    }

    /**
     * <strong>20.603</strong> Blinking Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Blinking Disabled
     *                  1 = Without Acknowledge
     *                  2 = Blinking With Acknowledge
     * </pre>
     */
    @DataPoint(value = {"20.603", "dpst-20-603"}, description = "Blinking Mode")
    public enum BlinkingMode implements DataPointEnum<BlinkingMode> {
        @DataPointEnumValue(value = 0, description = "Blinking Disabled")
        DISABLED, //
        @DataPointEnumValue(value = 1, description = "Without Acknowledge")
        WITHOUT_ACKNOWLEDGE, //
        @DataPointEnumValue(value = 2, description = "Blinking With Acknowledge")
        WITH_ACKNOWLEDGE
    }

    /**
     * <strong>20.604</strong> Light Control Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   0   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>7</sub> N<sub>1</sub>)
     * Range:      N = [0 .. 1]
     *                  0 = Automatic Light Control
     *                  1 = Manual Light Control
     * </pre>
     */
    @DataPoint(value = {"20.604", "dpst-20-604"}, description = "Light Control Mode")
    public enum LightControlMode implements DataPointEnum<LightControlMode> {
        @DataPointEnumValue(value = 0, description = "Automatic Light Control")
        AUTOMATIC, //
        @DataPointEnumValue(value = 1, description = "Manual Light Control")
        MANUAL
    }

    /**
     * <strong>20.605</strong> PB Switch Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [1 .. 2]
     *                  1 = One PB/binary input mode
     *                  2 = Two PBs/binary inputs mode
     * </pre>
     */
    @DataPoint(value = {"20.605", "dpst-20-605"}, description = "PB Switch Mode")
    public enum SwitchPBModel implements DataPointEnum<SwitchPBModel> {
        @DataPointEnumValue(value = 1, description = "One PB/binary input mode")
        ONE_INPUT, //
        @DataPointEnumValue(value = 2, description = "Two PBs/binary inputs mode")
        TWO_INPUTS
    }

    /**
     * <strong>20.606</strong> PB Action Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = inactive (no message sent)
     *                  1 = SwitchOff message sent
     *                  2 = SwitchOn message sent
     *                  3 = inverse
     * </pre>
     */
    @DataPoint(value = {"20.606", "dpst-20-606"}, description = "PB Action Mode")
    public enum PBAction implements DataPointEnum<PBAction> {
        @DataPointEnumValue(value = 0, description = "inactive (no message sent)")
        INACTIVE, //
        @DataPointEnumValue(value = 1, description = "SwitchOff message sent")
        SWITCH_OFF, //
        @DataPointEnumValue(value = 2, description = "SwitchOn message sent")
        SWITCH_ON, //
        @DataPointEnumValue(value = 3, description = "inverse")
        INVERSE
    }

    /**
     * <strong>20.607</strong> PB Dimming Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [1 .. 4]
     *                  1 = one PB/binary input; SwitchOnOff inverts on each transmission
     *                  2 = one PB/binary input, On / DimUp message sent
     *                  3 = one PB/binary input, Off / DimDown message sent
     *                  4 = two PBs/binary inputs mode
     * </pre>
     */
    @DataPoint(value = {"20.607", "dpst-20-607"}, description = "PB Dimming Mode")
    public enum DimmingPBModel implements DataPointEnum<DimmingPBModel> {
        @DataPointEnumValue(value = 1, description = "one PB/binary input; SwitchOnOff inverts on each transmission")
        ONE_BINARY_INPUT_SWITCHONOFF_INVERTS_ON_EACH_TRANSMISSION, //
        @DataPointEnumValue(value = 2, description = "one PB/binary input, On / DimUp message sent")
        ONE_BINARY_INPUT_ON_DIMUP_MESSAGE_SENT, //
        @DataPointEnumValue(value = 3, description = "one PB/binary input, Off / DimDown message sent")
        ONE_BINARY_INPUT_OFF_DIMDOWN_MESSAGE_SENT, //
        @DataPointEnumValue(value = 4, description = "two PBs/binary inputs mode")
        TWO_BINARY_INPUTS_MODE
    }

    /**
     * <strong>20.608</strong> Switch On Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = last actual value
     *                  1 = value according additional parameter
     *                  2 = last received absolute setvalue
     * </pre>
     */
    @DataPoint(value = {"20.608", "dpst-20-608"}, description = "Switch On Mode")
    public enum SwitchOnMode implements DataPointEnum<SwitchOnMode> {
        @DataPointEnumValue(value = 0, description = "last actual value")
        LAST_ACTUAL_VALUE, //
        @DataPointEnumValue(value = 1, description = "value according additional parameter")
        VALUE_ACCORDING_ADDITIONAL_PARAMETER, //
        @DataPointEnumValue(value = 2, description = "last received absolute setvalue")
        LAST_RECEIVED_ABSOLUTE_SETVALUE
    }

    /**
     * <strong>20.609</strong> Load Type Set
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>4</sub> N<sub>4</sub>)
     * Range:      N = [0 .. 8]
     *                  0 = automatic (resistive, capacitive or inductive)
     *                  1 = leading edge (inductive load)
     *                  2 = trailing edge (capacitive load)
     *                  3 = switch mode only (non-dimmable load)
     *                  4 = automatic once
     *                  5 = Compact Fluorescent Lamps, leading
     *                  6 = Compact Fluorescent Lamps, trailing
     *                  7 = LED, leading
     *                  8 = LED, trailing
     * </pre>
     */
    @DataPoint(value = {"20.609", "dpst-20-609"}, description = "Load Type")
    public enum LoadTypeSet implements DataPointEnum<LoadTypeSet> {
        @DataPointEnumValue(value = 0, description = "automatic (resistive, capacitive or inductive)")
        AUTOMATIC, //
        @DataPointEnumValue(value = 1, description = "leading edge (inductive load)")
        LEADING_EDGE, //
        @DataPointEnumValue(value = 2, description = "trailing edge (capacitive load)")
        TRAILING_EDGE, //
        @DataPointEnumValue(value = 3, description = "switch mode only (non-dimmable load)")
        SWITCH_MODE_ONLY, //
        @DataPointEnumValue(value = 4, description = "automatic once")
        AUTOMATIC_ONCE, //
        @DataPointEnumValue(value = 5, description = "Compact Fluorescent Lamps, leading")
        CFL_LEADING, //
        @DataPointEnumValue(value = 6, description = "Compact Fluorescent Lamps, trailing")
        CFL_TRAILING, //
        @DataPointEnumValue(value = 7, description = "LED, leading")
        LED_LEADING, //
        @DataPointEnumValue(value = 8, description = "LED, trailing")
        LED_TRAILING
    }

    /**
     * <strong>20.610</strong> Load Type Detected
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>4</sub> N<sub>4</sub>)
     * Range:      N = [0 .. 8]
     *                  0 = undefined
     *                  1 = leading edge (inductive load)
     *                  2 = trailing edge (capacitive load)
     *                  3 = detection not possible or error
     *                  4 = calibration pending, waiting on trigger
     *                  5 = Compact Fluorescent Lamps, leading
     *                  6 = Compact Fluorescent Lamps, trailing
     *                  7 = LED, leading
     *                  8 = LED, trailing
     * </pre>
     */
    @DataPoint(value = {"20.610", "dpst-20-610"}, description = "Load Type Detected")
    public enum LoadTypeDetected implements DataPointEnum<LoadTypeDetected> {
        @DataPointEnumValue(value = 0, description = "undefined")
        UNDEFINED, //
        @DataPointEnumValue(value = 1, description = "leading edge (inductive load)")
        LEADING_EDGE, //
        @DataPointEnumValue(value = 2, description = "trailing edge (capacitive load)")
        TRAILING_EDGE, //
        @DataPointEnumValue(value = 3, description = "detection not possible or error")
        DETECTION_NOT_POSSIBLE_OR_ERROR, //
        @DataPointEnumValue(value = 4, description = "calibration pending, waiting on trigger")
        CALIBRATION_PENDING, //
        @DataPointEnumValue(value = 5, description = "Compact Fluorescent Lamps, leading")
        CFL_LEADING, //
        @DataPointEnumValue(value = 6, description = "Compact Fluorescent Lamps, trailing")
        CFL_TRAILING, //
        @DataPointEnumValue(value = 7, description = "LED, leading")
        LED_LEADING, //
        @DataPointEnumValue(value = 8, description = "LED, trailing")
        LED_TRAILING
    }

    /**
     * <strong>20.611</strong> Converter Test Control
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 8]
     *                  0 = Reserved, no effect
     *                  1 = Start Function Test (FT) Acc. DALI Cmd. 227
     *                  2 = Start Duration Test (DT) Acc. DALI Cmd. 228
     *                  3 = Start Partial Duration Test (PDT)
     *                  4 = Stop Test Acc. DALI Cmd 229
     *                  5 = Reset Function Test Done Flag Acc. DALI Cmd. 230
     *                  6 = Reset Duration Test Done Acc. DALI Cmd. 231
     * </pre>
     */
    @DataPoint(value = {"20.611", "dpst-20-611"}, description = "Converter Test Control")
    public enum ConverterTestControl implements DataPointEnum<ConverterTestControl> {
        @DataPointEnumValue(value = 0, description = "Reserved, no effect")
        NO_EFFECT, //
        @DataPointEnumValue(value = 1, description = "Start Function Test (FT) Acc. DALI Cmd. 227")
        START_FUNCTION_TEST, //
        @DataPointEnumValue(value = 2, description = "Start Duration Test (DT) Acc. DALI Cmd. 228")
        START_DURATION_TEST, //
        @DataPointEnumValue(value = 3, description = "Start Partial Duration Test (PDT)")
        START_PARTIAL_DURATION_TEST, //
        @DataPointEnumValue(value = 4, description = "Stop Test Acc. DALI Cmd 229")
        STOP_TEST_ACC, //
        @DataPointEnumValue(value = 5, description = "Reset Function Test Done Flag Acc. DALI Cmd. 230")
        RESET_FUNCTION_TEST_DONE, //
        @DataPointEnumValue(value = 6, description = "Reset Duration Test Done Acc. DALI Cmd. 231")
        RESET_DURATION_TEST_DONE
    }

    /**
     * <strong>20.612</strong> Converter Control
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = Restore Factory Default Settings Acc. DALI Cmd. 254
     *                  1 = Goto Rest Mode Acc. DALI Cmd. 224
     *                  2 = Goto Inhibit Mode Acc. DALI Cmd. 225
     *                  3 = Re-Light / Reset Inhibit Acc. DALI Cmd. 226
     *                  4 = Reset Lamp Time Resets the Lamp Emergency Time and the Lamp Total Operation Time. Acc. DALI Cmd. 232
     * </pre>
     */
    @DataPoint(value = {"20.612", "dpst-20-612"}, description = "Converter Control")
    public enum ConverterControl implements DataPointEnum<ConverterControl> {
        @DataPointEnumValue(value = 0, description = "Restore Factory Default Settings Acc. DALI Cmd. 254")
        RESTORE_FACTORY_SETTINGS, //
        @DataPointEnumValue(value = 1, description = "Goto Rest Mode Acc. DALI Cmd. 224")
        GOTO_REST_MODE, //
        @DataPointEnumValue(value = 2, description = "Goto Inhibit Mode Acc. DALI Cmd. 225")
        GOTO_INHIBIT, //
        @DataPointEnumValue(value = 3, description = "Re-Light / Reset Inhibit Acc. DALI Cmd. 226")
        RE_LIGHT_RESET_INHIBIT, //
        @DataPointEnumValue(value = 4, description = "Reset Lamp Time Resets the Lamp Emergency Time and the Lamp Total Operation Time. Acc. DALICmd.232")
        RESET_LAMP_TIME_AND_OPERATION_TIME
    }

    /**
     * <strong>20.613</strong> Converter Data Request
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>4</sub> N<sub>4</sub>)
     * Range:      N = [0 .. 8]
     *                  0 = Reserved, no effect
     *                  1 = Request Converter Status
     *                  2 = Request Converter Test Result
     *                  3 = Request Battery Info
     *                  4 = Request Converter FT Info
     *                  5 = Request Converter DT Info
     *                  6 = Request Converter PDT Info
     *                  7 = Request Converter Info
     *                  8 = Request Converter Info Fix
     * </pre>
     */
    @DataPoint(value = {"20.613", "dpst-20-613"}, description = "Converter Data Request")
    public enum ConverterDataRequest implements DataPointEnum<ConverterDataRequest> {
        @DataPointEnumValue(value = 0, description = "Reserved, no effect")
        NO_EFFECT, //
        @DataPointEnumValue(value = 1, description = "Request Converter Status")
        REQUEST_CONVERTER_STATUS, //
        @DataPointEnumValue(value = 2, description = "Request Converter Test Result")
        REQUEST_CONVERTER_TEST_RESULT, //
        @DataPointEnumValue(value = 3, description = "Request Battery Info")
        REQUEST_BATTERY_INFO, //
        @DataPointEnumValue(value = 4, description = "Request Converter FT Info")
        REQUEST_CONVERTER_FT_INFO, //
        @DataPointEnumValue(value = 5, description = "Request Converter DT Info")
        REQUEST_CONVERTER_DT_INFO, //
        @DataPointEnumValue(value = 6, description = "Request Converter PDT Info")
        REQUEST_CONVERTER_PDT_INFO, //
        @DataPointEnumValue(value = 7, description = "Request Converter Info")
        REQUEST_CONVERTER_INFO, //
        @DataPointEnumValue(value = 8, description = "Request Converter Info Fix")
        REQUEST_CONVERTER_INFO_FIX
    }

    /**
     * <strong>20.801</strong> SAB Except Behavior
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = up
     *                  1 = down
     *                  2 = no change
     *                  3 = value according additional parameter
     *                  4 = stop
     * </pre>
     */
    @DataPoint(value = {"20.801", "dpst-20-801"}, description = "SAB Except Behavior")
    public enum SABExceptBehavior implements DataPointEnum<SABExceptBehavior> {
        @DataPointEnumValue(value = 0, description = "up")
        UP, //
        @DataPointEnumValue(value = 1, description = "down")
        DOWN, //
        @DataPointEnumValue(value = 2, description = "no change")
        NO_CHANGE, //
        @DataPointEnumValue(value = 3, description = "value according additional parameter")
        VALUE_ACCORDING_ADDITIONAL_PARAMETER, //
        @DataPointEnumValue(value = 4, description = "stop")
        STOP
    }

    /**
     * <strong>20.802</strong> SAB Behavior On Lock/Unlock
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [0 .. 6]
     *                  0 = up
     *                  1 = down
     *                  2 = no change
     *                  3 = value according additional parameter
     *                  4 = stop
     *                  5 = updated value
     *                  6 = value before locking
     * </pre>
     */
    @DataPoint(value = {"20.802", "dpst-20-802"}, description = "SAB Behavior On Lock/Unlock")
    public enum SABBehaviorLockUnlock implements DataPointEnum<SABBehaviorLockUnlock> {
        @DataPointEnumValue(value = 0, description = "up")
        UP, //
        @DataPointEnumValue(value = 1, description = "down")
        DOWN, //
        @DataPointEnumValue(value = 2, description = "no change")
        NO_CHANGE, //
        @DataPointEnumValue(value = 3, description = "value according additional parameter")
        VALUE_ACCORDING_ADDITIONAL_PARAMETER, //
        @DataPointEnumValue(value = 4, description = "stop")
        STOP, //
        @DataPointEnumValue(value = 5, description = "updated value")
        UPDATED_VALUE, //
        @DataPointEnumValue(value = 6, description = "value before locking")
        VALUE_BEFORE_LOCKING
    }

    /**
     * <strong>20.803</strong> SSSB Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>5</sub> N<sub>3</sub>)
     * Range:      N = [1 .. 4]
     *                  1 = one push button/binary input; MoveUpDown inverts on each transmission
     *                  2 = one push button/binary input, MoveUp / StepUp message sent
     *                  3 = one push button/binary input, MoveDown / StepDown message sent
     *                  4 = two push buttons/binary inputs mode
     * </pre>
     */
    @DataPoint(value = {"20.803", "dpst-20-803"}, description = "SSSB Mode")
    public enum SSSBMode implements DataPointEnum<SSSBMode> {
        @DataPointEnumValue(value = 1, description = "one push button/binary input; MoveUpDown inverts on each transmission")
        ONE_PUSH_INPUT_MOVEUPDOWN, //
        @DataPointEnumValue(value = 2, description = "one push button/binary input, MoveUp / StepUp message sent")
        ONE_PUSH_INPUT_MOVEUP, //
        @DataPointEnumValue(value = 3, description = "one push button/binary input, MoveDown / StepDown message sent")
        ONE_PUSH_INPUT_MOVEDOWN, //
        @DataPointEnumValue(value = 4, description = "two push buttons/binary inputs mode")
        TWO_PUSH_INPUTS
    }

    /**
     * <strong>20.804</strong> Blinds Control Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   0   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>7</sub> N<sub>1</sub>)
     * Range:      N = [0 .. 1]
     *                  0 = Automatic Control
     *                  1 = Manual Control
     * </pre>
     */
    @DataPoint(value = {"20.804", "dpst-20-804"}, description = "Blinds Control Mode")
    public enum BlindsControlMode implements DataPointEnum<BlindsControlMode> {
        @DataPointEnumValue(value = 0, description = "Automatic Control")
        AUTOMATIC, //
        @DataPointEnumValue(value = 1, description = "Manual Control")
        MANUAL
    }

    /**
     * <strong>20.1000</strong> Communication Mode
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | N   N   N   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 255]
     *                  0 = Data Link Layer
     *                  1 = Busmonitor
     *                  2 = Data Link Layer Raw Frames
     *                  3 = Network Layer
     *                  4 = TL Group Oriented
     *                  5 = TL Connection Oriented
     *                  6 = cEMI Transport Layer
     *                  7 .. 239 = Reserved for other destination layers
     *                240 .. 254 = Reserved for Manufacturer specific use
     *                255 = No Layer
     *
     * </pre>
     */
    @DataPoint(value = {"20.1000", "dpst-20-1000"}, description = "Communication Mode")
    public enum CommunicationMode implements DataPointEnum<CommunicationMode> {
        @DataPointEnumValue(value = 0, description = "Data Link Layer")
        DATA_LINK_LAYER, //
        @DataPointEnumValue(value = 1, description = "Busmonitor")
        BUSMONITOR, //
        @DataPointEnumValue(value = 2, description = "Data Link Layer Raw Frames")
        DATA_LINK_LAYER_RAW_FRAMES, //
        @DataPointEnumValue(value = 3, description = "Network Layer")
        NETWORK_LAYER, //
        @DataPointEnumValue(value = 4, description = "TL Group Oriented")
        TL_GROUP_ORIENTED, //
        @DataPointEnumValue(value = 5, description = "TL Connection Oriented")
        TL_CONNECTION_ORIENTED, //
        @DataPointEnumValue(value = 6, description = "cEMI Transport Layer")
        CEMI_TRANSPORT_LAYER, //
        @DataPointEnumValue(value = 255, description = "No Layer")
        NO_LAYER
    }

    /**
     * <strong>20.1001</strong> Additional Info Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | N   N   N   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 7, 255]
     *                  1 = PL medium Domain Address
     *                  2 = RF Control Octet and Serial Number or DoA
     *                  3 = Busmonitor Error Flags
     *                  4 = Relative timestamp
     *                  5 = Time delay
     *                  6 = Extended Relative Timestamp
     *                  7 = BiBat information
     *                255 = reserved for future system extensions (ESC code)
     * </pre>
     */
    @DataPoint(value = {"20.1001", "dpst-20-1001"}, description = "Additional Info Type")
    public enum AdditionalInfoTypes implements DataPointEnum<AdditionalInfoTypes> {
        @DataPointEnumValue(value = 1, description = "PL medium Domain Address")
        PL_MEDIUM_DOMAIN_ADDRESS, //
        @DataPointEnumValue(value = 2, description = "RF Control Octet and Serial Number or DoA")
        RF_CONTROL_OCTET_AND_SERIAL_NUMBER_OR_DOA, //
        @DataPointEnumValue(value = 3, description = "Busmonitor Error Flags")
        BUSMONITOR_ERROR_FLAGS, //
        @DataPointEnumValue(value = 4, description = "Relative timestamp")
        RELATIVE_TIMESTAMP, //
        @DataPointEnumValue(value = 5, description = "Time delay")
        TIME_DELAY, //
        @DataPointEnumValue(value = 6, description = "Extended Relative Timestamp")
        EXTENDED_RELATIVE_TIMESTAMP, //
        @DataPointEnumValue(value = 7, description = "BiBat information")
        BIBAT_INFORMATION, //
        @DataPointEnumValue(value = 255, description = "reserved for future system extensions (ESC code)")
        ESC_CODE
    }

    /**
     * <strong>20.1002</strong> RF Mode Selection
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = asynchronous
     *                  1 = asynchronous + BiBat Master
     *                  2 = asynchronous + BiBat Slave
     * </pre>
     */
    @DataPoint(value = {"20.1002", "dpst-20-1002"}, description = "RF Mode Selection")
    public enum RFModeSelect implements DataPointEnum<RFModeSelect> {
        @DataPointEnumValue(value = 0, description = "asynchronous")
        ASYNCHRONOUS, //
        @DataPointEnumValue(value = 1, description = "asynchronous + BiBat Master")
        ASYNCHRONOUS_BIBAT_MASTER, //
        @DataPointEnumValue(value = 2, description = "asynchronous + BiBat Slave")
        ASYNCHRONOUS_BIBAT_SLAVE
    }

    /**
     * <strong>20.1003</strong> RF Filter Mode Selection
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>6</sub> N<sub>2</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = no filtering, all supported received frames shall be passed to the cEMI client using L_Data.ind
     *                  1 = filtering by Domain Address
     *                  2 = filtering by KNX Serial Number table
     *                  3 = filtering by Domain Address and by Serial number table
     * </pre>
     */
    @DataPoint(value = {"20.1003", "dpst-20-1003"}, description = "RF Filter Mode Selection")
    public enum RFFilterSelect implements DataPointEnum<RFFilterSelect> {
        @DataPointEnumValue(value = 0, description = "no filtering, all supported received frames shall be passed to the cEMI clientusing L_Data.ind")
        NO_FILTERING, //
        @DataPointEnumValue(value = 1, description = "filtering by Domain Address")
        BY_DOMAIN_ADDRESS, //
        @DataPointEnumValue(value = 2, description = "filtering by KNX Serial Number table")
        BY_SERIAL_NUMBER, //
        @DataPointEnumValue(value = 3, description = "filtering by Domain Address and by Serial numbertable")
        BY_DOMAIN_ADDRESS_AND_BY_SERIAL_NUMBER
    }

    /**
     * <strong>20.1004</strong> Medium
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = {0, 1, 2, 5}
     *                  0 = KNX TP1
     *                  1 = KNX PL110
     *                  2 = KNX RF
     *                  5 = KNX IP
     * </pre>
     */
    @DataPoint(value = {"20.1004", "dpst-20-1004"}, description = "Medium")
    public enum Medium implements DataPointEnum<Medium> {
        @DataPointEnumValue(value = 0, description = "KNX_TP1")
        KNX_TP1, //
        @DataPointEnumValue(value = 1, description = "KNX_PL110")
        KNX_PL110, //
        @DataPointEnumValue(value = 2, description = "KNX_RF")
        KNX_RF, //
        @DataPointEnumValue(value = 5, description = "KNX_IP")
        KNX_IP
    }

    /**
     * <strong>20.1005</strong> PB Function
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   N   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (r<sub>2</sub> N<sub>6</sub>)
     * Range:      N = [1 .. 55]
     *                  1 = default function
     *                  2 = ON
     *                  3 = OFF
     *                  4 = Toggle
     *                  5 = Dimming Up Down
     *                  6 = Dimming Up
     *                  7 = Dimming Down
     *                  8 = On / Off
     *                  9 = Timed On Off
     *                 10 = Forced On
     *                 11 = Forced Off
     *                 12 = Shutter Up (for PB)
     *                 13 = Shutter Down (for (PB)
     *                 14 = Shutter Up Down (for PB)
     *                 15 = reserved
     *                 16 = Forced Up
     *                 17 = Forced Down
     *                 18 = Wind Alarm
     *                 19 = Rain Alarm
     *                 20 = HVAC Mode Comfort / Economy
     *                 21 = HVAC Mode Comfort / -
     *                 22 = HVAC Mode Economy / -
     *                 23 = HVAC Mode Building protection / HVAC mode auto
     *                 24 = Shutter Stop
     *                 25 = Timed Comfort Standby
     *                 26 = Forced Comfort
     *                 27 = Forced Building protection
     *                 28 = Scene 1
     *                 29 = Scene 2
     *                 30 = Scene 3
     *                 31 = Scene 4
     *                 32 = Scene 5
     *                 33 = Scene 6
     *                 34 = Scene 7
     *                 35 = Scene 8
     *                 36 = Absolute dimming 25 %
     *                 37 = Absolute dimming 50 %
     *                 38 = Absolute dimming 75 %
     *                 39 = Absolute dimming 100 %
     *                 40 = Shutter Up / - (for switch)
     *                 41 = Shutter Down / - (for switch)
     *                 42 = Shutter Up / Down (for switch)
     *                 43 = Shutter Down / Up (for switch)
     *                 44 = Light sensor
     *                 45 = System clock
     *                 46 = Battery status
     *                 47 = HVAC Mode Standby / -
     *                 48 = HVAC Mode Auto / -
     *                 49 = HVAC Mode Comfort / Standby
     *                 50 = HVAC Mode Building protection / -
     *                 51 = Timed toggle
     *                 52 = Dimming Absolute switch
     *                 53 = Scene switch
     *                 54 = Smoke alarm
     *                 55 = Sub detector
     * </pre>
     */
    @DataPoint(value = {"20.1005", "dpst-20-1005"}, description = "PB Function")
    public enum PBFunction implements DataPointEnum<PBFunction> {
        @DataPointEnumValue(value = 1, description = "default function")
        DEFAULT_FUNCTION, //
        @DataPointEnumValue(value = 2, description = "ON")
        ON, //
        @DataPointEnumValue(value = 3, description = "OFF")
        OFF, //
        @DataPointEnumValue(value = 4, description = "Toggle")
        TOGGLE, //
        @DataPointEnumValue(value = 5, description = "Dimming Up Down")
        DIMMING_UP_DOWN, //
        @DataPointEnumValue(value = 6, description = "Dimming Up")
        DIMMING_UP, //
        @DataPointEnumValue(value = 7, description = "Dimming Down")
        DIMMING_DOWN, //
        @DataPointEnumValue(value = 8, description = "On / Off")
        ON_OFF, //
        @DataPointEnumValue(value = 9, description = "Timed On Off")
        TIMED_ON_OFF, //
        @DataPointEnumValue(value = 10, description = "Forced On")
        FORCED_ON, //
        @DataPointEnumValue(value = 11, description = "Forced Off")
        FORCED_OFF, //
        @DataPointEnumValue(value = 12, description = "Shutter Up (for PB)")
        SHUTTER_UP, //
        @DataPointEnumValue(value = 13, description = "Shutter Down (for (PB)")
        SHUTTER_DOWN, //
        @DataPointEnumValue(value = 14, description = "Shutter Up Down (for PB)")
        SHUTTER_UP_DOWN, //
        @DataPointEnumValue(value = 15, description = "reserved")
        RESERVED, //
        @DataPointEnumValue(value = 16, description = "Forced Up")
        FORCED_UP, //
        @DataPointEnumValue(value = 17, description = "Forced Down")
        FORCED_DOWN, //
        @DataPointEnumValue(value = 18, description = "Wind Alarm")
        WIND_ALARM, //
        @DataPointEnumValue(value = 19, description = "Rain Alarm")
        RAIN_ALARM, //
        @DataPointEnumValue(value = 20, description = "HVAC Mode Comfort / Economy")
        HVAC_MODE_COMFORT_ECONOMY, //
        @DataPointEnumValue(value = 21, description = "HVAC Mode Comfort / -")
        HVAC_MODE_COMFORT, //
        @DataPointEnumValue(value = 22, description = "HVAC Mode Economy / -")
        HVAC_MODE_ECONOMY, //
        @DataPointEnumValue(value = 23, description = "HVAC Mode Building protection / HVAC mode auto")
        HVAC_MODE_BUILDING_PROTECTION_AUTO, //
        @DataPointEnumValue(value = 24, description = "Shutter Stop")
        SHUTTER_STOP, //
        @DataPointEnumValue(value = 25, description = "Timed Comfort Standby")
        TIMED_COMFORT_STANDBY, //
        @DataPointEnumValue(value = 26, description = "Forced Comfort")
        FORCED_COMFORT, //
        @DataPointEnumValue(value = 27, description = "Forced Building protection")
        FORCED_BUILDING_PROTECTION, //
        @DataPointEnumValue(value = 28, description = "Scene 1")
        SCENE_1, //
        @DataPointEnumValue(value = 29, description = "Scene 2")
        SCENE_2, //
        @DataPointEnumValue(value = 30, description = "Scene 3")
        SCENE_3, //
        @DataPointEnumValue(value = 31, description = "Scene 4")
        SCENE_4, //
        @DataPointEnumValue(value = 32, description = "Scene 5")
        SCENE_5, //
        @DataPointEnumValue(value = 33, description = "Scene 6")
        SCENE_6, //
        @DataPointEnumValue(value = 34, description = "Scene 7")
        SCENE_7, //
        @DataPointEnumValue(value = 35, description = "Scene 8")
        SCENE_8, //
        @DataPointEnumValue(value = 36, description = "Absolute dimming 25 %")
        ABSOLUTE_DIMMING_25, //
        @DataPointEnumValue(value = 37, description = "Absolute dimming 50 %")
        ABSOLUTE_DIMMING_50, //
        @DataPointEnumValue(value = 38, description = "Absolute dimming 75 %")
        ABSOLUTE_DIMMING_75, //
        @DataPointEnumValue(value = 39, description = "Absolute dimming 100 %")
        ABSOLUTE_DIMMING_100, //
        @DataPointEnumValue(value = 40, description = "Shutter Up / - (for switch)")
        SWITCH_SHUTTER_UP, //
        @DataPointEnumValue(value = 41, description = "Shutter Down / - (for switch)")
        SWITCH_SHUTTER_DOWN, //
        @DataPointEnumValue(value = 42, description = "Shutter Up / Down (for switch)")
        SWITCH_SHUTTER_UP_DOWN, //
        @DataPointEnumValue(value = 43, description = "Shutter Down / Up (for switch)")
        SWITCH_SHUTTER_DOWN_UP, //
        @DataPointEnumValue(value = 44, description = "Light sensor")
        LIGHT_SENSOR, //
        @DataPointEnumValue(value = 45, description = "System clock")
        SYSTEM_CLOCK, //
        @DataPointEnumValue(value = 46, description = "Battery status")
        BATTERY_STATUS, //
        @DataPointEnumValue(value = 47, description = "HVAC Mode Standby / -")
        HVAC_MODE_STANDBY, //
        @DataPointEnumValue(value = 48, description = "HVAC Mode Auto / -")
        HVAC_MODE_AUTO, //
        @DataPointEnumValue(value = 49, description = "HVAC Mode Comfort / Standby")
        HVAC_MODE_COMFORT_STANDBY, //
        @DataPointEnumValue(value = 50, description = "HVAC Mode Building protection / -")
        HVAC_MODE_BUILDING_PROTECTION, //
        @DataPointEnumValue(value = 51, description = "Timed toggle")
        TIMED_TOGGLE, //
        @DataPointEnumValue(value = 52, description = "Dimming Absolute switch")
        DIMMING_ABSOLUTE_SWITCH, //
        @DataPointEnumValue(value = 53, description = "Scene switch")
        SCENE_SWITCH, //
        @DataPointEnumValue(value = 54, description = "Smoke alarm")
        SMOKE_ALARM, //
        @DataPointEnumValue(value = 55, description = "Sub detector")
        SUB_DETECTOR
    }

    /**
     * <strong>20.1200</strong> MBus Breaker Valve State
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | N   N   N   N   N   N   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = {0, 1, 2, 255}
     *                  0 = Breaker/Valve is closed
     *                  1 = Breaker/Valve is open
     *                  2 = Breaker/Valve is released
     *                255 = invalid
     * </pre>
     */
    @DataPoint(value = {"20.1200", "dpst-20-1200"}, description = "MBus Breaker Valve State")
    public enum MBusBreakerValveState implements DataPointEnum<MBusBreakerValveState> {
        @DataPointEnumValue(value = 0, description = "Breaker/Valve is closed")
        IS_CLOSED, //
        @DataPointEnumValue(value = 1, description = "Breaker/Valve is open")
        IS_OPEN, //
        @DataPointEnumValue(value = 2, description = "Breaker/Valve is released")
        IS_RELEASED, //
        @DataPointEnumValue(value = 255, description = "invalid")
        INVALID
    }

    /**
     * <strong>20.1202</strong> Gas Measurement Condition
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = Unknown
     *                  1 = Temperature converted
     *                  2 = At base condition
     *                  3 = At measurement condition
     * </pre>
     */
    @DataPoint(value = {"20.1202", "dpst-20-1202"}, description = "Gas Measurement Condition")
    public enum GasMeasurementCondition implements DataPointEnum<GasMeasurementCondition> {
        @DataPointEnumValue(value = 0, description = "Unknown")
        UNKNOWN, //
        @DataPointEnumValue(value = 1, description = "Temperature converted")
        TEMPERATURE_CONVERTED, //
        @DataPointEnumValue(value = 2, description = "At base condition")
        AT_BASE_CONDITION, //
        @DataPointEnumValue(value = 3, description = "At measurement condition")
        AT_MEASUREMENT_CONDITION
    }

    /**
     * <strong>20.1203</strong> Breaker Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 6]
     *                  0 = Closed
     *                  1 = Open on Overload
     *                  2 = Open on Overvoltage
     *                  3 = Open on Load Shedding
     *                  4 = Open on PLC
     *                  5 = Open on Overheat (over maximum)
     *                  6 = Open on Overheat (under maximum)
     * </pre>
     */
    @DataPoint(value = {"20.1203", "dpst-20-1203"}, description = "Breaker Status")
    public enum BreakerStatus implements DataPointEnum<BreakerStatus> {
        @DataPointEnumValue(value = 0, description = "Closed")
        CLOSED, //
        @DataPointEnumValue(value = 1, description = "Open on Overload")
        OPEN_ON_OVERLOAD, //
        @DataPointEnumValue(value = 2, description = "Open on Overvoltage")
        OPEN_ON_OVERVOLTAGE, //
        @DataPointEnumValue(value = 3, description = "Open on Load Shedding")
        OPEN_ON_LOAD_SHEDDING, //
        @DataPointEnumValue(value = 4, description = "Open on PLC")
        OPEN_ON_PLC, //
        @DataPointEnumValue(value = 5, description = "Open on Overheat (over maximum)")
        OPEN_ON_OVERHEAT_OVER_MAXIMUM, //
        @DataPointEnumValue(value = 6, description = "Open on Overheat (under maximum)")
        OPEN_ON_OVERHEAT_UNDER_MAXIMUM //
    }

    /**
     * <strong>20.1204</strong> Euridis Communication Interface Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = Deactivated
     *                  1 = Activated without security
     *                  2 = Activated with security
     * </pre>
     */
    @DataPoint(value = {"20.1204", "dpst-20-1204"}, description = "Euridis Communication Interface Status")
    public enum EuridisCommunicationInterfaceStatus implements DataPointEnum<EuridisCommunicationInterfaceStatus> {
        @DataPointEnumValue(value = 0, description = "Deactivated")
        DEACTIVATED, //
        @DataPointEnumValue(value = 1, description = "Activated without security")
        ACTIVATED_WITHOUT_SECURITY, //
        @DataPointEnumValue(value = 2, description = "Activated with security")
        ACTIVATED_WITH_SECURITY //
    }

    /**
     * <strong>20.1205</strong> PLC Status
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 2]
     *                  0 = New / Unlock
     *                  1 = New / Lock
     *                  2 = Registered
     * </pre>
     */
    @DataPoint(value = {"20.1205", "dpst-20-1205"}, description = "PLC Status")
    public enum PLCStatus implements DataPointEnum<PLCStatus> {
        @DataPointEnumValue(value = 0, description = "New / Unlock")
        NEW_UNLOCK, //
        @DataPointEnumValue(value = 1, description = "New / Lock")
        NEW_LOCK, //
        @DataPointEnumValue(value = 2, description = "Registered")
        REGISTERED //
    }

    /**
     * <strong>20.1206</strong> Peak Event Notice
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = No Notice Peak Event
     *                  1 = Notice Peak Event 1
     *                  2 = Notice Peak Event 2
     *                  3 = Notice Peak Event 3
     * </pre>
     */
    @DataPoint(value = {"20.1206", "dpst-20-1206"}, description = "Peak Event Notice")
    public enum PeakEventNotice implements DataPointEnum<PeakEventNotice> {
        @DataPointEnumValue(value = 0, description = "No Notice Peak Event")
        NO_NOTICE_PEAK_EVENT, //
        @DataPointEnumValue(value = 1, description = "Notice Peak Event 1")
        NOTICE_PEAK_EVENT_1, //
        @DataPointEnumValue(value = 2, description = "Notice Peak Event 2")
        NOTICE_PEAK_EVENT_2, //
        @DataPointEnumValue(value = 3, description = "Notice Peak Event 3")
        NOTICE_PEAK_EVENT_3 //
    }

    /**
     * <strong>20.1207</strong> Peak Event
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 3]
     *                  0 = No Peak Event
     *                  1 = Peak Event 1
     *                  2 = Peak Event 2
     *                  3 = Peak Event 3
     * </pre>
     */
    @DataPoint(value = {"20.1207", "dpst-20-1207"}, description = "Peak Event")
    public enum PeakEvent implements DataPointEnum<PeakEvent> {
        @DataPointEnumValue(value = 0, description = "No Peak Event")
        NO_PEAK_EVENT, //
        @DataPointEnumValue(value = 1, description = "Peak Event 1")
        PEAK_EVENT_1, //
        @DataPointEnumValue(value = 2, description = "Peak Event 2")
        PEAK_EVENT_2, //
        @DataPointEnumValue(value = 3, description = "Peak Event 3")
        PEAK_EVENT_3 //
    }

    /**
     * <strong>20.1208</strong> TIC Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 1]
     *                  0 = Historical
     *                  1 = Standard
     * </pre>
     */
    @DataPoint(value = {"20.1208", "dpst-20-1208"}, description = "TIC Type")
    public enum TICType implements DataPointEnum<TICType> {
        @DataPointEnumValue(value = 0, description = "Historical")
        HISTORICAL, //
        @DataPointEnumValue(value = 1, description = "Standard")
        STANDARD //
    }


    /**
     * <strong>20.1209</strong> TIC Channel Type
     *
     * <pre>
     *             +-7-+-6-+-5-+-4-+-3-+-2-+-1-+-0-+
     * Field Names | (Field 1)                     |
     * Encoding    | 0   0   0   0   0   0   N   N |
     *             +---+---+---+---+---+---+---+---+
     * Format:     8 bit (N<sub>8</sub>)
     * Range:      N = [0 .. 4]
     *                  0 = None
     *                  1 = Historical Single-Phase
     *                  2 = Historical Three-Phase
     *                  3 = Standard Single-Phase
     *                  4 = Standard Three-Phase
     * </pre>
     */
    @DataPoint(value = {"20.1209", "dpst-20-1209"}, description = "TIC Channel Type")
    public enum TICChannelType implements DataPointEnum<TICChannelType> {
        @DataPointEnumValue(value = 0, description = "None")
        NONE, //
        @DataPointEnumValue(value = 1, description = "Historical Single-Phase")
        HISTORICAL_SINGLE_PHASE, //
        @DataPointEnumValue(value = 2, description = "Historical Three-Phase")
        HISTORICAL_THREE_PHASE, //
        @DataPointEnumValue(value = 3, description = "Standard Single-Phase")
        STANDARD_SINGLE_PHASE, //
        @DataPointEnumValue(value = 4, description = "Standard Three-Phase")
        STANDARD_THREE_PHASE, //
    }
}
