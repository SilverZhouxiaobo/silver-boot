package cn.silver.framework.core.mapper;

import cn.silver.framework.core.domain.FlowEntity;
import cn.silver.framework.db.provider.BaseSqlProvider;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;
@RegisterMapper
public interface FlowMapper<T extends FlowEntity> extends BaseMapper<T> {
    @SelectProvider(type = BaseSqlProvider.class, method = "dynamicSQL")
    <T extends FlowEntity> List<T> selectCollects(T record);
}
