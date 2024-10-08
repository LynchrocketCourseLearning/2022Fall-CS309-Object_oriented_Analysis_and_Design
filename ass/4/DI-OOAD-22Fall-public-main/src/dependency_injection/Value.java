package dependency_injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Value {
    String value();
    String delimiter() default ",";
}

/*
* 题目中应用范围：
* boolean, int, String
* boolean[], int[], String[]
* List<?>, Set<?>, Map<?, ?>: Boolean, Integer, String
*
* ElementType.FIELD：
* ElementType.PARAMETER：
*
* */

