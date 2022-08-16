package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 在线表单数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineFormMapper extends BaseMapper<OnlineForm> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineFormFilter 主表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineForm> getOnlineFormList(
            @Param("onlineFormFilter") OnlineForm onlineFormFilter, @Param("orderBy") String orderBy);
}
