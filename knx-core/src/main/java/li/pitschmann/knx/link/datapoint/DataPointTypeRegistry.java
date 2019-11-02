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
import li.pitschmann.knx.link.datapoint.annotation.KnxDataPointTypeEnum;
import li.pitschmann.knx.link.datapoint.annotation.KnxDataPointValueEnum;
import li.pitschmann.knx.link.datapoint.value.DPTEnumValue;
import li.pitschmann.knx.link.exceptions.KnxDataPointTypeNotFoundException;
import li.pitschmann.knx.link.exceptions.KnxEnumNotFoundException;
import li.pitschmann.knx.link.exceptions.KnxException;
import li.pitschmann.utils.Maps;
import li.pitschmann.utils.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Registry for all data point types to serving all.
 *
 * @author PITSCHR
 */
public final class DataPointTypeRegistry {
    private static final Logger log = LoggerFactory.getLogger(DataPointTypeRegistry.class);
    private static final Map<String, DataPointType> dataPointTypeMap = Maps.newHashMap(1024);
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
    private DataPointTypeRegistry() {
        throw new AssertionError("Do not touch me!");
    }

    /**
     * Registers the given data point type class. By calling this method the class will be visited with the result that
     * all data point types of given class will be registered.
     *
     * @param dataPointTypeClass
     */
    public static void registerDataPointType(final @Nonnull Class<?> dataPointTypeClass) {
        log.debug("Register Data Point Type Class: {}", dataPointTypeClass);

        // find data point types by enumeration classes (e.g. DPT20, DPT23, ...)
        Stream.of(dataPointTypeClass.getClasses()).filter(
                c -> c.isEnum() && DataPointTypeEnum.class.isAssignableFrom(c) && c.isAnnotationPresent(KnxDataPointTypeEnum.class))
                .forEach(DataPointTypeRegistry::registerDataPointTypeEnums);

        // find data point types by fields (e.g. DPT1, DPT2, ...)
        registerDataPointTypes(dataPointTypeClass);
    }

    /**
     * Finds enumerated data point types. Only enumeration constants with annotation {@link KnxDataPointType} are
     * considered.
     *
     * @param clazz Class must be an enumeration class and it must implements the {@link DataPointTypeEnum} interface
     */
    private static <T extends Enum<T> & DataPointTypeEnum<T>> void registerDataPointTypeEnums(final @Nonnull Class<?> clazz) {
        // we can cast safely here
        @SuppressWarnings("unchecked") final var enumInnerClass = (Class<T>) clazz;

        final var classAnnotation = enumInnerClass.getAnnotation(KnxDataPointTypeEnum.class);
        // inner class is enum class and has data point type annotation
        log.debug("Inner Class: {} [id={}, description={}]", enumInnerClass, classAnnotation.id(), classAnnotation.description());
        Preconditions.checkArgument(!dataPointTypeMap.containsKey(classAnnotation.id()),
                String.format("Data point type key '{}' is already registered. Please check your DPT implementation!", classAnnotation.id()));
        final var dptEnum = new DPTEnum<T>(classAnnotation.id(), classAnnotation.description());

        // iterate for all enum constant fields which have the desired annotation
        for (final var field : Stream.of(enumInnerClass.getFields())
                .filter(f -> f.isEnumConstant() && f.isAnnotationPresent(KnxDataPointValueEnum.class)).toArray(Field[]::new)) {
            final var fieldAnnotation = field.getAnnotation(KnxDataPointValueEnum.class);
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

        dataPointTypeMap.put(classAnnotation.id(), dptEnum);
        log.debug("Enum Data Point Type registered: {}", dptEnum);
    }

    /**
     * Finds all normal constants with annotation {@link KnxDataPointType}.
     *
     * @param clazz
     */
    private static void registerDataPointTypes(final @Nonnull Class<?> clazz) {
        var first = true;
        // iterate for all public/static/final fields
        for (final var field : Stream
                .of(clazz.getFields()).filter(f -> Modifier.isFinal(f.getModifiers()) //
                        && Modifier.isStatic(f.getModifiers()) //
                        && f.isAnnotationPresent(KnxDataPointType.class))
                .toArray(Field[]::new)) {
            final var fieldAnnotation = field.getAnnotation(KnxDataPointType.class);
            log.debug("Field: {}->{} [id={}, description={}]", clazz, field.getName(), fieldAnnotation.id(), fieldAnnotation.description());

            try {
                Preconditions.checkArgument(!dataPointTypeMap.containsKey(fieldAnnotation.id()), String.format(
                        "Data Point Type key '{}' is already registered. Please check your DPT implementation!", fieldAnnotation.id()));
                final var fieldInstance = (DataPointType<?>) field.get(null);
                dataPointTypeMap.put(fieldAnnotation.id(), fieldInstance);

                // register DPT-x and DPST-x-y format as well which are used in '*.knxproj' file
                final var dptIds = fieldAnnotation.id().split("\\.");
                // register DPT-x for first field of class only!
                // example: 1.001 -> DPT-1
                if (first) {
                    first = false;
                    dataPointTypeMap.put("DPT-" + dptIds[0], fieldInstance);
                }
                // register DPST-x-y for all fields
                // example: 1.001 -> DPST-1-1
                dataPointTypeMap.put("DPST-" + dptIds[0] + "-" + Integer.parseInt(dptIds[1]), fieldInstance);

            } catch (final Exception ex) {
                log.error("Exception for field '{}'", field.getName(), ex);
                throw new KnxException(fieldAnnotation.id());
            }
        }
    }

    /**
     * Returns the data point type by given enumeration field
     *
     * @param e
     * @return {@link DPTEnumValue}
     */
    @Nonnull
    public static <T extends Enum<T> & DataPointTypeEnum<T>> DPTEnumValue<T> getDataPointType(final @Nonnull Enum<T> e) {
        @SuppressWarnings("unchecked") final DPTEnumValue<T> dpt = dataPointEnumMap.get(Objects.requireNonNull(e));
        if (dpt == null) {
            throw new KnxEnumNotFoundException("Could not find enum data point type for: " + e);
        }
        return dpt;
    }

    /**
     * Returns the data point type by given {@code id}
     *
     * @param id
     * @return {@link DataPointType}
     */
    @Nonnull
    public static <T extends DataPointType> T getDataPointType(final @Nonnull String id) {
        @SuppressWarnings("unchecked") final T dpt = (T) dataPointTypeMap.get(Objects.requireNonNull(id));
        if (dpt == null) {
            throw new KnxDataPointTypeNotFoundException(id);
        }
        return dpt;
    }
}
