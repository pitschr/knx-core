package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare that the field, parameter, local variable
 * or method return type may be null.
 * <p>
 * It is in 'javax.annotation' package to allow major IDEs for
 * smart assistance to detect potential NullPointerException areas.
 * <p>
 * This class is copied over to this class from 'com.google.code.findbugs:jsr305'
 * and will avoid potential library dependency because of only one class.
 * <p>
 * If you are still reading ... I am still waiting for an official @Nullable annotation!
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.CLASS)
public @interface Nullable {
    // marker annotation interface
}
