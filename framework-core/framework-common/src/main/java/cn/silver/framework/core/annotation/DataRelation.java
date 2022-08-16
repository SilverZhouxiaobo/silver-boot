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
public @interface DataRelation {
    /**
     * 关联方式
     *
     * @return
     */
    String joiner() default "left join";

    /**
     * 关联表
     *
     * @return
     */
    Class target() default void.class;

    /**
     * 当前表别名
     *
     * @return
     */
    String localAlias() default "source";

    /**
     * 目标表别名
     *
     * @return
     */
    String targetAlias() default "target";

    /**
     * 关联字段名
     *
     * @return
     */
    String column() default "";

    /**
     * 目标字段名
     *
     * @return
     */
    String targetColumn() default "id";

    /**
     * 映射字段
     *
     * @return
     */
    String property() default "";
}
