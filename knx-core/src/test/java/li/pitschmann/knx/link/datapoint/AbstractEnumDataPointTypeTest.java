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

/**
 * Abstract test for Enumeration DPT classes like {@link DPT20} and {@link DPT23}
 *
 * @author PITSCHR
 */
public abstract class AbstractEnumDataPointTypeTest implements DPTTest {
    // private static final Logger log = LoggerFactory.getLogger(AbstractEnumDataPointTypeTest.class);
    //

    /**
     * Returns the current class that holds enum fields and values
     *
     * @return
     */
    abstract protected Class<?> currentEnumClass();
    //
    // /**
    // * Tests invalid/unknown parameters
    // */
    // @Test
    // public void testOfInvalid() {
    // // invalid
    // assertThatThrownBy(() ->
    // ObsoleteDataPointContainer.toEnumValue(null)).isInstanceOf(KnxEnumNotFoundException.class);
    // assertThatThrownBy(() ->
    // ObsoleteDataPointContainer.toEnumValue(TestEnumDPT.TEST)).isInstanceOf(KnxEnumNotFoundException.class);
    //
    // assertThatThrownBy(() ->
    // ObsoleteDataPointContainer.getEnumDescriptor(null)).isInstanceOf(KnxEnumNotFoundException.class);
    // assertThatThrownBy(() ->
    // ObsoleteDataPointContainer.getEnumDescriptor(TestEnumDPT.TEST)).isInstanceOf(KnxEnumNotFoundException.class);
    // }
    //
    // /**
    // * Tests the enum classes and fields
    // *
    // * @param clazz
    // */
    // @Test
    // public void testEnumClassesAndFields() {
    // int numberOfDPTs = 0;
    // int numberOfValues = 0;
    // final Set<Integer> knownHashCodes = new HashSet<>(1024);
    //
    // for (Class<?> enumClass : this.currentEnumClass().getDeclaredClasses()) {
    // log.debug("Enum Class: {}", enumClass.getSimpleName());
    // numberOfDPTs++;
    // // iterate through all enum classes in DPT23
    // assertThat(enumClass.isEnum());
    // for (final Field enumField : enumClass.getDeclaredFields()) {
    // final int enumFieldModifier = enumField.getModifiers();
    // // consider only public, final and static (enum)
    // if (Modifier.isPublic(enumFieldModifier) && Modifier.isFinal(enumFieldModifier) &&
    // Modifier.isStatic(enumFieldModifier)
    // && DataPointEnum.class.isAssignableFrom(enumField.getType())) {
    // numberOfValues++;
    //
    // // get EnumDPT
    // final DataPointEnum<?> enumDpt;
    // try {
    // enumDpt = (DataPointEnum<?>) enumField.get(enumClass);
    // assertThat(enumDpt.getClass().isEnum()).isTrue();
    // } catch (IllegalArgumentException | IllegalAccessException e) {
    // fail("Something went wrong by retrieving enum object from class '" + enumClass + "' and field '" + enumField +
    // "'.");
    // break;
    // }
    //
    // // get DPT value
    // final DataPointEnumValue<?> dptValue = enumDpt.toValue();
    // log.debug(" Enum Field: {}={}", enumField.getName(), dptValue);
    //
    // // Check hash code integrity
    // final int dpt20ValueHashCode = dptValue.hashCode();
    // if (knownHashCodes.contains(dpt20ValueHashCode)) {
    // fail("Duplicate hash code '" + dpt20ValueHashCode + "' detected for '" + dptValue + "': '" + knownHashCodes);
    // }
    // knownHashCodes.add(dpt20ValueHashCode);
    //
    // // Chaining test
    // assertThat(dptValue.getDPT()).isEqualTo(enumDpt);
    // assertThat(dptValue.getDPT().toValue()).isEqualTo(dptValue);
    // assertThat(enumDpt.toValue().getDPT()).isEqualTo(enumDpt);
    // assertThat(enumDpt.toValue().getDPT().toValue()).isEqualTo(dptValue);
    //
    // final byte[] byteArray = dptValue.toByteArray();
    //
    // // assert DPT value
    // assertThat(enumDpt.toValue()).isEqualTo(dptValue);
    // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue(enumDpt)).isEqualTo(dptValue);
    // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue(enumDpt.getId(),
    // Bytes.toUnsignedInt(byteArray)))
    // .isEqualTo(dptValue);
    // assertThat((DataPointEnumValue<?>) ObsoleteDataPointContainer.toEnumValue(enumDpt.getId(),
    // byteArray)).isEqualTo(dptValue);
    //
    // // assert byte array
    // assertThat(enumDpt.toByteArray()).containsExactly(byteArray);
    // assertThat(dptValue.toByteArray()).containsExactly(byteArray);
    // } else {
    // log.trace(" Enum Field (not public): {}", enumField);
    // }
    // }
    // }
    //
    // log.debug("Number of DPTs: {}", numberOfDPTs);
    // log.debug("Number of DPT values: {}", numberOfValues);
    // }
    //
    // /**
    // * Test {@link DataPointEnum} to reach throwing an exception.
    // *
    // * @author PITSCHR
    // *
    // */
    // private enum TestEnumDPT implements DataPointEnum<TestEnumDPT> {
    // TEST;
    // }
}
