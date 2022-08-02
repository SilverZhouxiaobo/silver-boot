package cn.silver.framework.core.mapper;

import cn.silver.framework.core.domain.TreeEntity;
import cn.silver.framework.db.provider.BaseSqlProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 */
@RegisterMapper
public interface TreeMapper<T extends TreeEntity> extends BaseMapper<T> {

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends TreeEntity> List<T> selectByPid(@Param("pid") String pid);

    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends TreeEntity> List<T> selectByPids(@Param("pids") Collection<String> pids);
}
