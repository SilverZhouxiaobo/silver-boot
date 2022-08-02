package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.FlowMapper;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 工作流工单表数据操作访问接口。
 * 如果当前系统支持数据权限过滤，当前用户必须要能看自己的工单数据，所以需要把EnableDataPerm
 * 的mustIncludeUserRule参数设置为true，即便当前用户的数据权限中并不包含DataPermRuleType.TYPE_USER_ONLY，
 * 数据过滤拦截组件也会自动补偿该类型的数据权限，以便当前用户可以看到自己发起的工单。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowWorkOrderMapper extends FlowMapper<FlowWorkOrder> {

    List<FlowWorkOrder> selectByBusinessKeys(@Param("businessKeys") Collection<String> businessKeys);

    List<FlowWorkOrder> selectByInstanceIds(@Param("instanceIds") Collection<String> instanceIds);

    List<FlowWorkOrder> selectByTaskId(@Param("taskIds") Collection<String> taskIds);

    FlowWorkOrder selectByBusinessKey(@Param("businessKey") String businessKey);

    FlowWorkOrder selectByInstanceId(@Param("instanceId") String instanceId);

    void deleteByInstanceId(@Param("instanceId") String instanceId);

    void deleteByBusinessKeys(@Param("businessKeys") Collection<String> businessKeys);

    List<FlowWorkOrder> getSearchValue();

}
