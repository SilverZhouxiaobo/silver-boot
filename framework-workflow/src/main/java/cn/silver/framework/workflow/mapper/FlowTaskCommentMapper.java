package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 流程任务批注数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowTaskCommentMapper extends BaseMapper<FlowTaskComment> {

    List<FlowTaskComment> selectListByInstanceId(@Param("instanceId") String instanceId);

    List<FlowTaskComment> selectListByTaskIds(@Param("taskIds") Collection<String> taskIds);
}
