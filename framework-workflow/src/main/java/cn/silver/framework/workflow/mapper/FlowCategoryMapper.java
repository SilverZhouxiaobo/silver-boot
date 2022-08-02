package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FlowCategory数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowCategoryMapper extends BaseMapper<FlowCategory> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param flowCategoryFilter 主表过滤对象。
     * @param orderBy            排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<FlowCategory> getFlowCategoryList(
            @Param("flowCategoryFilter") FlowCategory flowCategoryFilter, @Param("orderBy") String orderBy);
}
