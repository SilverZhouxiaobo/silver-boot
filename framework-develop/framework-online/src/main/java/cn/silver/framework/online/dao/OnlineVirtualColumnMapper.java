package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineVirtualColumn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 虚拟字段数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineVirtualColumnMapper extends BaseMapper<OnlineVirtualColumn> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineVirtualColumnFilter 主表过滤对象。
     * @param orderBy                   排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineVirtualColumn> getOnlineVirtualColumnList(
            @Param("onlineVirtualColumnFilter") OnlineVirtualColumn onlineVirtualColumnFilter, @Param("orderBy") String orderBy);
}
