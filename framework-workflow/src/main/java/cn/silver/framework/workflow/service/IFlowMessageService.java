package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowMessage;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import com.alibaba.fastjson.JSONObject;
import org.flowable.task.api.Task;

import java.util.List;

/**
 * 工作流消息数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface IFlowMessageService extends IBaseService<FlowMessage> {


    /**
     * 根据工单参数，保存催单消息对象。如果当前工单存在多个待办任务，则插入多条催办消息数据。
     *
     * @param flowWorkOrder 待催办的工单。
     */
    void saveNewRemindMessage(FlowWorkOrder flowWorkOrder);

    /**
     * 保存抄送消息对象。
     *
     * @param task         待抄送的任务。
     * @param copyDataJson 抄送人员或者组的Id数据。
     */
    void saveNewCopyMessage(Task task, JSONObject copyDataJson);

    /**
     * 更新指定运行时任务Id的消费为已完成状态。
     *
     * @param taskId 运行时任务Id。
     */
    void updateFinishedStatusByTaskId(String taskId);

    /**
     * 更新指定流程实例Id的消费为已完成状态。
     *
     * @param processInstanceId 流程实例IdId。
     */
    void updateFinishedStatusByProcessInstanceId(String processInstanceId);

    /**
     * 获取当前用户的催办消息列表。
     *
     * @return 查询后的催办消息列表。
     */
    List<FlowMessage> getRemindingMessageListByUser();

    /**
     * 获取当前用户的抄送消息列表。
     *
     * @param read true表示已读，false表示未读。
     * @return 查询后的抄送消息列表。
     */
    List<FlowMessage> getCopyMessageListByUser(Boolean read);

    /**
     * 计算当前用户催办消息的数量。
     *
     * @return 当前用户催办消息数量。
     */
    int countRemindingMessageListByUser();

    /**
     * 计算当前用户未读抄送消息的数量。
     *
     * @return 当前用户未读抄送消息数量。
     */
    int countCopyMessageByUser();

    /**
     * 删除指定流程实例的消息。
     *
     * @param processInstanceId 流程实例Id。
     */
    void removeByProcessInstanceId(String processInstanceId);
}
