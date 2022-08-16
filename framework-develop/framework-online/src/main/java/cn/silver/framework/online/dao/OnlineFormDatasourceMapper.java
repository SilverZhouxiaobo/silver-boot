package cn.silver.framework.online.dao;


import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineFormDatasource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 在线表单与数据源多对多关联的数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineFormDatasourceMapper extends BaseMapper<OnlineFormDatasource> {
}
