package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowMessageOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 流程任务消息所属用户的操作数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowMessageOperationMapper extends BaseMapper<FlowMessageOperation> {

    /**
     * 删除指定流程实例的消息关联数据。
     *
     * @param processInstanceId 流程实例Id。
     */
    void deleteByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
