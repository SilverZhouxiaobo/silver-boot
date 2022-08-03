package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段数据数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineColumnMapper extends BaseMapper<OnlineColumn> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineColumnFilter 主表过滤对象。
     * @return 对象列表。
     */
    List<OnlineColumn> getOnlineColumnList(@Param("onlineColumnFilter") OnlineColumn onlineColumnFilter);
}
