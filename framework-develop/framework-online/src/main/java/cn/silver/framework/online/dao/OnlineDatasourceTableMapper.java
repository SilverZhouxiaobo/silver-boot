package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineDatasourceTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineDatasourceTableMapper extends BaseMapper<OnlineDatasourceTable> {
}
