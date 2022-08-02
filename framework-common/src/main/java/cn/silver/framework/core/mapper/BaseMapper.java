package cn.silver.framework.core.mapper;

import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.db.provider.BaseSqlProvider;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.base.BaseDeleteMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

import java.util.Collection;
import java.util.List;

@RegisterMapper
public interface BaseMapper<T extends BaseEntity> extends BaseSelectMapper<T>, BaseUpdateMapper<T>, BaseDeleteMapper<T> {

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends BaseEntity> List<T> selectList(T entity);

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends BaseEntity> List<T> selectByIds(@Param("ids") Collection<String> ids);

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends BaseEntity> List<T> selectByValues(@Param("values") Collection<String> values);

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends BaseEntity> List<T> selectExists(T entity);

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    T selectById(@Param("id") String id);

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    T selectByValue(@Param("value") String value);

    @InsertProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    int insert(T entity);

    @InsertProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends BaseEntity> int insertBatch(@Param("entities") Collection<T> entities);

    @UpdateProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    int update(T entity);

    @DeleteProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    int deleteBatch(@Param("ids") Collection<String> ids);
}
