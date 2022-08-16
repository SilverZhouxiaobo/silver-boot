package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlinePageDatasource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 在线表单页面和数据源关联对象的数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlinePageDatasourceMapper extends BaseMapper<OnlinePageDatasource> {
}
