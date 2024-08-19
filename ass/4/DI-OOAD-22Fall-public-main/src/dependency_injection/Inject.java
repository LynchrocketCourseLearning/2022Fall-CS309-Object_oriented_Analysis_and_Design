package dependency_injection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Inject {
}

/*
* Inject 用户自定义注解，题目的要求总结：
* ElementType.FIELD: 只负责注入用户自定义class，都在testClass中出现
* ElementType.CONSTRUCTOR: 已确保每个类只有一个constructor带有@Inject注解。
*
* */