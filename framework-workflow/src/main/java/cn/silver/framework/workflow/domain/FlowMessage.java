package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import cn.silver.framework.workflow.constant.FlowMessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import javax.persistence.Table;
import java.util.Date;

/**
 * 工作流通知消息实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@Table(name = "flow_message")
@ApiModel(value = "工作流通知消息对象")
public class FlowMessage extends DataEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 消息类型。
     */
    @ApiModelProperty(value = "消息类型")
    @Column(name = "message_type", searchType = SearchType.EQ)
    private String messageType;

    /**
     * 消息内容。
     */
    @ApiModelProperty(value = "消息内容")
    private String messageContent;

    /**
     * 催办次数。
     */
    @ApiModelProperty(value = "催办次数")
    @Column(name = "remind_count", searchType = SearchType.EQ)
    private Integer remindCount;

    /**
     * 工单Id。
     */
    @ApiModelProperty(value = "工单Id")
    @Column(name = "work_order_id", searchType = SearchType.EQ)
    private String workOrderId;

    /**
     * 流程定义Id。
     */
    @ApiModelProperty(value = "流程定义Id")
    @Column(name = "process_definition_id", searchType = SearchType.EQ)
    private String processDefinitionId;

    /**
     * 流程定义标识。
     */
    @ApiModelProperty(value = "流程定义标识")
    @Column(name = "process_definition_key", searchType = SearchType.EQ)
    private String processDefinitionKey;

    /**
     * 流程名称。
     */
    @ApiModelProperty(value = "流程名称")
    @Column(name = "process_definition_name", searchType = SearchType.EQ)
    private String processDefinitionName;

    /**
     * 流程实例Id。
     */
    @ApiModelProperty(value = "流程实例Id")
    @Column(name = "process_instance_id", searchType = SearchType.EQ)
    private String processInstanceId;

    /**
     * 流程实例发起者。
     */
    @ApiModelProperty(value = "流程实例发起者")
    @Column(name = "process_instance_initiator", searchType = SearchType.EQ)
    private String processInstanceInitiator;

    /**
     * 流程任务Id。
     */
    @ApiModelProperty(value = "流程任务Id")
    @Column(name = "task_id", searchType = SearchType.EQ)
    private String taskId;

    /**
     * 流程任务定义标识。
     */
    @ApiModelProperty(value = "流程任务定义标识")
    @Column(name = "task_definition_key", searchType = SearchType.LIKE)
    private String taskDefinitionKey;

    /**
     * 流程任务名称。
     */
    @ApiModelProperty(value = "流程任务名称")
    @Column(name = "task_name", searchType = SearchType.LIKE)
    private String taskName;

    /**
     * 任务指派人登录名。
     */
    @ApiModelProperty(value = "任务指派人ID")
    @Column(name = "task_assignee", searchType = SearchType.EQ)
    private String taskAssignee;

    @ApiModelProperty(value = "任务启动时间")
    @Column(name = "task_start_time", searchType = SearchType.BETWEEN)
    private Date taskStartTime;
    /**
     * 任务是否已完成。
     */
    @ApiModelProperty(value = "任务是否已完成")
    @Column(name = "task_finished", searchType = SearchType.EQ)
    private Boolean taskFinished;

    /**
     * 业务数据快照。
     */
    @ApiModelProperty(value = "业务数据快照")
    private String businessDataShot;

    /**
     * 是否为在线表单消息数据。
     */
    @ApiModelProperty(value = "是否为在线表单消息数据")
    private Boolean onlineFormData;

    public FlowMessage(ProcessInstance instance, Task task) {
        this.setMessageType(FlowMessageType.COPY_TYPE.getCode());
        this.setRemindCount(0);
        this.setProcessDefinitionId(instance.getProcessDefinitionId());
        this.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        this.setProcessDefinitionName(instance.getProcessDefinitionName());
        this.setProcessInstanceId(instance.getProcessInstanceId());
        this.setProcessInstanceInitiator(instance.getStartUserId());
        this.setTaskId(task.getId());
        this.setTaskDefinitionKey(task.getTaskDefinitionKey());
        this.setTaskName(task.getName());
        this.setTaskStartTime(task.getCreateTime());
        this.setTaskAssignee(task.getAssignee());
        this.setTaskFinished(false);
    }

    public FlowMessage(FlowWorkOrder order, Task task) {
        this.setMessageType(FlowMessageType.REMIND_TYPE.getCode());
        this.setRemindCount(1);
        this.setWorkOrderId(order.getId());
        this.setProcessDefinitionId(order.getProcessDefinitionId());
        this.setProcessDefinitionKey(order.getProcessDefinitionKey());
        this.setProcessDefinitionName(order.getProcessDefinitionName());
        this.setProcessInstanceId(order.getProcessInstanceId());
        this.setProcessInstanceInitiator(order.getSubmitId());
        this.setTaskId(task.getId());
        this.setTaskDefinitionKey(task.getTaskDefinitionKey());
        this.setTaskName(task.getName());
        this.setTaskStartTime(task.getCreateTime());
        this.setTaskAssignee(task.getAssignee());
        this.setTaskFinished(false);
    }
}
