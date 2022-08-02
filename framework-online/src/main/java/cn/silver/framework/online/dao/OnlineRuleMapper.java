package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 验证规则数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineRuleMapper extends BaseMapper<OnlineRule> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineRuleFilter 主表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineRule> getOnlineRuleList(
            @Param("onlineRuleFilter") OnlineRule onlineRuleFilter, @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表数据列表。
     *
     * @param columnId         关联主表Id。
     * @param onlineRuleFilter 从表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 从表数据列表。
     */
    List<OnlineRule> getOnlineRuleListByColumnId(
            @Param("columnId") Long columnId,
            @Param("onlineRuleFilter") OnlineRule onlineRuleFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据关联主表Id，获取关联从表中没有和主表建立关联关系的数据列表。
     *
     * @param columnId         关联主表Id。
     * @param onlineRuleFilter 过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 与主表没有建立关联的从表数据列表。
     */
    List<OnlineRule> getNotInOnlineRuleListByColumnId(
            @Param("columnId") Long columnId,
            @Param("onlineRuleFilter") OnlineRule onlineRuleFilter,
            @Param("orderBy") String orderBy);
}
