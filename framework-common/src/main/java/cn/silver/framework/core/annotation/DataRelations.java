package cn.silver.framework.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Administrator
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DataRelations {
    DataRelation[] value();
}
