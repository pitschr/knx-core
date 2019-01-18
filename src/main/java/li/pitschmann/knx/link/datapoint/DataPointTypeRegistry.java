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

import com.google.common.base.*;
import com.google.common.collect.*;
import li.pitschmann.knx.link.datapoint.annotation.*;
import li.pitschmann.knx.link.datapoint.value.*;
import li.pitschmann.knx.link.exceptions.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;

/**
 * Registry for all data point types to serving all.
 *
 * @author PITSCHR
 */
public final class DataPointTypeRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(DataPointTypeRegistry.class);
    private static final Map<String, DataPointType<?>> dataPointTypeMap = Maps.newHashMapWithExpectedSize(1024);
    private static final Map<Enum<?>, DPTEnumValue<?>> dataPointEnumMap = Maps.newHashMapWithExpectedSize(1024);

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

        if (LOG.isDebugEnabled()) {
            LOG.debug("{} data point types registered: {}", dataPointTypeMap.size(),
                    dataPointTypeMap.keySet().stream().sorted(Comparator.comparing(BigDecimal::new)).collect(Collectors.toList()));
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
    public static void registerDataPointType(final Class<?> dataPointTypeClass) {
        LOG.debug("Register Data Point Type Class: {}", dataPointTypeClass);

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
    private static <T extends Enum<T> & DataPointTypeEnum<T>> void registerDataPointTypeEnums(final Class<?> clazz) {
        // we can cast safely here
        @SuppressWarnings("unchecked") final Class<T> enumInnerClass = (Class<T>) clazz;

        final KnxDataPointTypeEnum classAnnotation = enumInnerClass.getAnnotation(KnxDataPointTypeEnum.class);
        // inner class is enum class and has data point type annotation
        LOG.debug("Inner Class: {} [id={}, description={}]", enumInnerClass, classAnnotation.id(), classAnnotation.description());
        Preconditions.checkArgument(!dataPointTypeMap.containsKey(classAnnotation.id()),
                String.format("Data point type key '%s' is already registered. Please check your DPT implementation!", classAnnotation.id()));
        final DPTEnum<T> dptEnum = new DPTEnum<>(classAnnotation.id(), classAnnotation.description());

        // iterate for all enum constant fields which have the desired annotation
        for (final Field field : Stream.of(enumInnerClass.getFields())
                .filter(f -> f.isEnumConstant() && f.isAnnotationPresent(KnxDataPointValueEnum.class)).toArray(Field[]::new)) {
            final KnxDataPointValueEnum fieldAnnotation = field.getAnnotation(KnxDataPointValueEnum.class);
            LOG.debug("Field: {}->{} [value={}, description={}]", enumInnerClass, field.getName(), fieldAnnotation.value(),
                    fieldAnnotation.description());

            try {
                // we are safe to cast here with "null" because field is an enumeration
                @SuppressWarnings("unchecked") final T fieldInstance = (T) field.get(null);
                final DPTEnumValue<T> dptEnumValue = new DPTEnumValue<>(dptEnum, fieldInstance, fieldAnnotation.value(),
                        fieldAnnotation.description());
                dptEnum.addValue(dptEnumValue);
                dataPointEnumMap.put(dptEnumValue.getEnumField(), dptEnumValue);
                LOG.debug("Enum Value registered: {}", dptEnumValue);
            } catch (final Exception ex) {
                LOG.error("Exception for field '{}'", field.getName(), ex);
                throw new KnxException(String.format("Could not register enum field '%s'.", field.getName()));
            }
        }

        dataPointTypeMap.put(classAnnotation.id(), dptEnum);
        LOG.debug("Enum Data Point Type registered: {}", dptEnum);
    }

    /**
     * Finds all normal constants with annotation {@link KnxDataPointType}.
     *
     * @param clazz
     */
    private static void registerDataPointTypes(final Class<?> clazz) {
        // iterate for all public/static/final fields
        for (final Field field : Stream
                .of(clazz.getFields()).filter(f -> Modifier.isFinal(f.getModifiers()) //
                        && Modifier.isStatic(f.getModifiers()) //
                        && f.isAnnotationPresent(KnxDataPointType.class))
                .toArray(Field[]::new)) {
            final KnxDataPointType fieldAnnotation = field.getAnnotation(KnxDataPointType.class);
            LOG.debug("Field: {}->{} [id={}, description={}]", clazz, field.getName(), fieldAnnotation.id(), fieldAnnotation.description());

            try {
                Preconditions.checkArgument(!dataPointTypeMap.containsKey(fieldAnnotation.id()), String.format(
                        "Data Point Type key '%s' is already registered. Please check your DPT implementation!", fieldAnnotation.id()));
                final DataPointType<?> fieldInstance = (DataPointType<?>) field.get(null);
                dataPointTypeMap.put(fieldAnnotation.id(), fieldInstance);
            } catch (final Exception ex) {
                LOG.error("Exception for field '{}'", field.getName(), ex);
                throw new KnxException(fieldAnnotation.id());
            }
        }
    }

    /**
     * Returns the data point type by given enumeration field
     *
     * @param e
     * @return
     */
    public static <T extends Enum<T> & DataPointTypeEnum<T>> DPTEnumValue<T> getDataPointType(final Enum<T> e) {
        @SuppressWarnings("unchecked") final DPTEnumValue<T> dpt = (DPTEnumValue<T>) dataPointEnumMap.get(e);
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
    public static <T extends DataPointType<?>> T getDataPointType(final String id) {
        @SuppressWarnings("unchecked") final T dpt = (T) dataPointTypeMap.get(id);
        if (dpt == null) {
            throw new KnxDataPointTypeNotFoundException(id);
        }
        return dpt;
    }
}
