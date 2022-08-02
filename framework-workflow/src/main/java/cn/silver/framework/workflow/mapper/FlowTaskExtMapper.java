package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程任务扩展数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowTaskExtMapper extends BaseMapper<FlowTaskExt> {

    /**
     * 批量插入流程任务扩展信息列表。
     *
     * @param flowTaskExtList 流程任务扩展信息列表。
     */
    void insertList(List<FlowTaskExt> flowTaskExtList);
}
