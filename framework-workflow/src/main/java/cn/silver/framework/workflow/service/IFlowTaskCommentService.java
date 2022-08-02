package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowTaskComment;

import java.util.List;
import java.util.Set;

/**
 * 流程任务批注数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface IFlowTaskCommentService extends IBaseService<FlowTaskComment> {

    /**
     * 查询指定流程实例Id下的所有审批任务的批注。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果集。
     */
    List<FlowTaskComment> getFlowTaskCommentList(String processInstanceId);

    /**
     * 查询与指定流程任务Id集合关联的所有审批任务的批注。
     *
     * @param taskIdSet 流程任务Id集合。
     * @return 查询结果集。
     */
    List<FlowTaskComment> getFlowTaskCommentListByTaskIds(Set<String> taskIdSet);

    /**
     * 获取指定流程实例的最后一条审批任务。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果。
     */
    FlowTaskComment getLatestFlowTaskComment(String processInstanceId);

    /**
     * 获取指定流程实例和任务定义标识的最后一条审批任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskDefinitionKey 任务定义标识。
     * @return 查询结果。
     */
    FlowTaskComment getLatestFlowTaskComment(String processInstanceId, String taskDefinitionKey);

    /**
     * 获取指定流程实例的第一条审批任务。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果。
     */
    FlowTaskComment getFirstFlowTaskComment(String processInstanceId);
}
