package cn.silver.framework.online.dao;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.online.domain.OnlineColumnRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface OnlineColumnRuleMapper extends BaseMapper<OnlineColumnRule> {

    /**
     * 获取指定字段Id关联的字段规则对象列表，同时还关联了每个OnlineRule对象。
     *
     * @param columnIdSet 字段Id集合。
     * @return 关联的字段规则对象列表。
     */
    List<OnlineColumnRule> getOnlineColumnRuleListByColumnIds(@Param("columnIdSet") Set<Long> columnIdSet);
}
