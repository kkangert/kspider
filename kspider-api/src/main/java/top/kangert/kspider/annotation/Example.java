package top.kangert.kspider.annotation;

import java.lang.annotation.*;

/**
 * 该注解用来标注自定义的方法注释，用来页面代码案例
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Example {
    String value();
}
