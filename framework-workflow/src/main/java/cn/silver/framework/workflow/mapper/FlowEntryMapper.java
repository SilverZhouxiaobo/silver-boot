package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FlowEntry数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowEntryMapper extends BaseMapper<FlowEntry> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param flowEntryFilter 主表过滤对象。
     * @param orderBy         排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<FlowEntry> getFlowEntryList(
            @Param("flowEntryFilter") FlowEntry flowEntryFilter, @Param("orderBy") String orderBy);

    FlowEntry selectOneByDefinitionKey(@Param("key") String key);
}
