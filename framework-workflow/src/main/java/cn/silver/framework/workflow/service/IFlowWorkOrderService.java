package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IFlowService;
import cn.silver.framework.workflow.domain.FlowWorkOrder;

import java.util.Collection;
import java.util.List;

/**
 * 工作流工单表数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface IFlowWorkOrderService extends IFlowService<FlowWorkOrder> {
    /**
     * 根据业务主键批量查询工单
     *
     * @param businessKeys
     * @return
     */
    List<FlowWorkOrder> selectByBusinessKey(Collection<String> businessKeys);

    /**
     * 根据任务信息批量查询工单
     *
     * @param taskIds
     * @return
     */
    List<FlowWorkOrder> selectByTaskId(Collection<String> taskIds);

    /**
     * 根据流程实例批量查询工单
     *
     * @param instanceIds
     * @return
     */
    List<FlowWorkOrder> selectByInstanceId(Collection<String> instanceIds);

    /**
     * 根据业务主键查询工单
     *
     * @param dataId
     * @return
     */
    FlowWorkOrder selectByBusinessKey(String dataId);

    /**
     * 根据流程实例批量查询工单
     *
     * @param instanceId
     * @return
     */
    FlowWorkOrder selectByInstanceId(String instanceId);

    /**
     * 根据流程实例Id，更新流程状态。
     *
     * @param processInstanceId 流程实例Id。
     * @param flowStatus        新的流程状态值。
     */
    void updateFlowStatusByProcessInstanceId(String processInstanceId, String flowStatus);

    /**
     * 删除指定流程实例Id的关联工单。
     *
     * @param processInstanceId 流程实例Id。
     */
    void removeByProcessInstanceId(String processInstanceId);

    /**
     * 根据业务主键批量删除工单
     *
     * @param businessKeys
     */
    void removeByBusinessKeys(Collection<String> businessKeys);

    /**
     * 是否有查看该工单的数据权限。
     *
     * @param processInstanceId 流程实例Id。
     * @return 存在返回true，否则false。
     */
    boolean hasDataPermOnFlowWorkOrder(String processInstanceId);

    /**
     * 查询工单表中所有的流程名字
     *
     * @return
     */
    List<FlowWorkOrder> getSearchValue();
}
