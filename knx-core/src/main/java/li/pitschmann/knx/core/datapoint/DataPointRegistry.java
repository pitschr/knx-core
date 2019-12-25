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

import li.pitschmann.knx.core.datapoint.value.DPTEnumValue;
import li.pitschmann.knx.core.datapoint.value.DataPointEnumValue;
import li.pitschmann.knx.core.exceptions.KnxDataPointTypeNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.core.exceptions.KnxException;
import li.pitschmann.knx.core.utils.Maps;
import li.pitschmann.knx.core.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registry for all data point types to serving all.
 *
 * @author PITSCHR
 */
public final class DataPointRegistry {
    private static final Logger log = LoggerFactory.getLogger(DataPointRegistry.class);
    private static final Map<String, DataPointType> dataPointTypeMap = Maps.newHashMap(1024);
    private static final Map<Object, String[]> dataPointIdentifierMap = Maps.newHashMap(1024);
    private static final Map<Enum, DPTEnumValue> dataPointEnumMap = Maps.newHashMap(1024);

    static {
        // add DPT fields
        registerDataPointType(DPT1.class);
        registerDataPointType(DPT2.class);
        registerDataPointType(DPT3.class);
        registerDataPointType(DPT4.class);
        registerDataPointType(DPT5.class);
        registerDataPointType(DPT6.class);
        registerDataPointType(DPT7.class);
        registerDataPointType(DPT8.class);
        registerDataPointType(DPT9.class);
        registerDataPointType(DPT10.class);
        registerDataPointType(DPT11.class);
        registerDataPointType(DPT12.class);
        registerDataPointType(DPT13.class);
        registerDataPointType(DPT14.class);
        registerDataPointType(DPT15.class);
        registerDataPointType(DPT16.class);
        registerDataPointType(DPT17.class);
        registerDataPointType(DPT18.class);
        registerDataPointType(DPT19.class);
        registerDataPointType(DPT20.class);
        registerDataPointType(DPT21.class);
        registerDataPointType(DPT22.class);
        registerDataPointType(DPT23.class);

        if (log.isDebugEnabled()) {
            log.debug("{} data point types registered: {}", dataPointTypeMap.size(),
                    dataPointTypeMap.keySet().stream().sorted().collect(Collectors.toList()));
        }
    }

    /**
     * Private constructor
     */
    private DataPointRegistry() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Registers the given data point type class. By calling this method the class will be visited with the result that
     * all data point types of given class will be registered.
     *
     * @param dataPointTypeClass DPT class to be registered
     */
    public static void registerDataPointType(final Class<?> dataPointTypeClass) {
        log.debug("Register Data Point Type Class: {}", dataPointTypeClass);

        // find data point types by enumeration classes (e.g. DPT20, DPT23, ...)
        Stream.of(dataPointTypeClass.getClasses()).filter(
                c -> c.isEnum() && DataPointEnum.class.isAssignableFrom(c) && c.isAnnotationPresent(DataPoint.class))
                .forEach(DataPointRegistry::registerDataPointTypeEnums);

        // find data point types by fields (e.g. DPT1, DPT2, ...)
        registerDataPointTypes(dataPointTypeClass);
    }

    /**
     * Finds enumerated data point types. Only enumeration constants with annotation {@link DataPoint} are
     * considered.
     *
     * @param clazz Class must be an enumeration class and it must implements the {@link DataPointEnum} interface
     */
    private static <T extends Enum<T> & DataPointEnum<T>> void registerDataPointTypeEnums(final Class<?> clazz) {
        // we can cast safely here
        @SuppressWarnings("unchecked") final var enumInnerClass = (Class<T>) clazz;

        final var classAnnotation = enumInnerClass.getAnnotation(DataPoint.class);
        final var mainDptId = classAnnotation.value()[0];
        // inner class is enum class and has data point type annotation
        log.debug("Inner Class: {} [dptId={}]", enumInnerClass, mainDptId);
        Preconditions.checkArgument(!dataPointTypeMap.containsKey(mainDptId),
                String.format("Data point type key '%s' is already registered. Please check your DPT implementation!", mainDptId));
        final var dptEnum = new DPTEnum<T>(mainDptId, classAnnotation.description());

        // iterate for all enum constant fields which have the desired annotation
        for (final var field : Stream.of(enumInnerClass.getFields())
                .filter(f -> f.isEnumConstant() && f.isAnnotationPresent(DataPointEnumValue.class)).toArray(Field[]::new)) {
            final var fieldAnnotation = field.getAnnotation(DataPointEnumValue.class);
            log.debug("Field: {}->{} [value={}, description={}]", enumInnerClass, field.getName(), fieldAnnotation.value(),
                    fieldAnnotation.description());

            try {
                // we are safe to cast here with "null" because field is an enumeration
                @SuppressWarnings("unchecked") final var fieldInstance = (T) field.get(null);
                final var dptEnumValue = new DPTEnumValue<>(dptEnum, fieldInstance, fieldAnnotation.value(),
                        fieldAnnotation.description());
                dptEnum.addValue(dptEnumValue);
                dataPointEnumMap.put(dptEnumValue.getEnum(), dptEnumValue);
                log.debug("Enum Value registered: {}", dptEnumValue);
            } catch (final Exception ex) {
                log.error("Exception for field '{}'", field.getName(), ex);
                throw new KnxException(String.format("Could not register enum field '%s'.", field.getName()));
            }
        }

        for (final var id : classAnnotation.value()) {
            dataPointTypeMap.put(id, dptEnum);
            log.debug("Enum Data Point Type registered: {}={}", id, dptEnum);
        }
        dataPointIdentifierMap.put(dptEnum, classAnnotation.value().clone());
    }

    /**
     * Finds all normal constants with annotation {@link DataPoint}.
     *
     * @param clazz the class that contains data point type
     */
    private static void registerDataPointTypes(final Class<?> clazz) {
        var first = true;
        // iterate for all public/static/final fields
        for (final var field : Stream
                .of(clazz.getFields()).filter(f -> Modifier.isFinal(f.getModifiers()) //
                        && Modifier.isStatic(f.getModifiers()) //
                        && f.isAnnotationPresent(DataPoint.class))
                .toArray(Field[]::new)) {
            final var fieldAnnotation = field.getAnnotation(DataPoint.class);
            try {
                final var fieldInstance = (DataPointType<?>) field.get(null);
                for (final var id : fieldAnnotation.value()) {
                    Preconditions.checkArgument(!dataPointTypeMap.containsKey(id), String.format(
                            "Data Point Type key '{}' is already registered. Please check your DPT implementation!", id));
                    dataPointTypeMap.put(id, fieldInstance);
                }
                dataPointIdentifierMap.put(fieldInstance, fieldAnnotation.value().clone());
                log.debug("Field: {}->{} [{}]", clazz, field.getName(), Arrays.toString(fieldAnnotation.value()));
            } catch (final Exception ex) {
                throw new KnxException(String.format("Exception for field '{}'", field.getName(), ex));
            }
        }
    }

    /**
     * Returns the data point type by given enumeration field
     *
     * @param e   the enum instance that should be looked up in the map
     * @param <T> the type of DPT enum value
     * @return {@link DPTEnumValue}
     */
    public static <T extends Enum<T> & DataPointEnum<T>> DPTEnumValue<T> getDataPointType(final Enum<T> e) {
        @SuppressWarnings("unchecked") final DPTEnumValue<T> dpt = dataPointEnumMap.get(Objects.requireNonNull(e));
        if (dpt == null) {
            throw new KnxEnumNotFoundException("Could not find enum data point type for: " + e);
        }
        return dpt;
    }

    /**
     * Returns the data point type by given {@code id}
     *
     * @param id  the DPT identifier that should be looked up in the map
     * @param <T> the type of DPT
     * @return {@link DataPointType}
     */
    public static <T extends DataPointType> T getDataPointType(final String id) {
        @SuppressWarnings("unchecked") final T dpt = (T) dataPointTypeMap.get(id.toLowerCase());
        if (dpt == null) {
            throw new KnxDataPointTypeNotFoundException(id);
        }
        return dpt;
    }

    public static String[] getDataPointIdentifiers(final DPTEnum<?> e) {
        final String[] identifiers = dataPointIdentifierMap.get(e);
        return Preconditions.checkNonNull(identifiers, "Could not find Data Point Enum '{}' in identifier map.", e);
    }

    public static String[] getDataPointIdentifiers(final DataPointType<?> dpt) {
        final String[] identifiers = dataPointIdentifierMap.get(dpt);
        return Preconditions.checkNonNull(identifiers, "Could not find Data Point Type '{}' in identifier map.", dpt);
    }
}
