package cn.silver.framework.core.annotation;

import cn.silver.framework.core.constant.SearchType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Administrator
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Column {
    /**
     * 列名称
     *
     * @return
     */
    String name() default "";

    /**
     * 是否唯一
     *
     * @return
     */
    boolean unique() default false;

    /**
     * 是否可作为字典值
     *
     * @return
     */
    boolean dictable() default false;

    /**
     * 是否可为空
     */
    boolean nullable() default true;

    /**
     * 是否允许插入
     */
    boolean insertable() default true;

    /**
     * 是否允许更新
     */
    boolean updatable() default true;

    /**
     * (Optional) The SQL fragment that is used when
     * generating the DDL for the column.
     * <p> Defaults to the generated SQL to create a
     * column of the inferred type.
     */
    String columnDefinition() default "";

    /**
     * (Optional) The name of the table that contains the column.
     * If absent the column is assumed to be in the primary table.
     */
    String table() default "";

    SearchType searchType() default SearchType.EQ;

    String target() default "";

    String format() default "yyyy-mm-dd hh24:mi:ss";

    /**
     * (Optional) The column length. (Applies only if a
     * string-valued column is used.)
     */
    int length() default 255;

    /**
     * (Optional) The precision for a decimal (exact numeric)
     * column. (Applies only if a decimal column is used.)
     * Value must be set by developer if used when generating
     * the DDL for the column.
     */
    int precision() default 0;

    /**
     * (Optional) The scale for a decimal (exact numeric) column.
     * (Applies only if a decimal column is used.)
     */
    int scale() default 0;
}
