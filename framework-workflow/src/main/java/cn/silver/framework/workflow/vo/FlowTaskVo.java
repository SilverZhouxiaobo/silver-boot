package cn.silver.framework.workflow.vo;

import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.Date;

/**
 * 流程任务Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程任务Vo对象")
public class FlowTaskVo extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 流程任务Id。
     */
    @ApiModelProperty(value = "流程任务Id")
    private String taskId;

    /**
     * 流程任务名称。
     */
    @ApiModelProperty(value = "流程任务名称")
    private String taskName;

    /**
     * 流程任务标识。
     */
    @ApiModelProperty(value = "流程任务标识")
    private String taskKey;

    /**
     * 任务的表单信息。
     */
    @ApiModelProperty(value = "任务的表单信息")
    private String taskFormKey;

    /**
     * 流程Id。
     */
    @ApiModelProperty(value = "流程Id")
    private String entryId;

    /**
     * 流程定义Id。
     */
    @ApiModelProperty(value = "流程定义Id")
    private String definitionId;

    /**
     * 流程定义名称。
     */
    @ApiModelProperty(value = "流程定义名称")
    private String definitionName;

    /**
     * 流程定义标识。
     */
    @ApiModelProperty(value = "流程定义标识")
    private String definitionKey;

    /**
     * 流程定义版本。
     */
    @ApiModelProperty(value = "流程定义版本")
    private Integer definitionVersion;

    /**
     * 流程实例Id。
     */
    @ApiModelProperty(value = "流程实例Id")
    private String instanceId;

    /**
     * 流程实例发起人。
     */
    @ApiModelProperty(value = "流程实例发起人")
    private String initiator;
    /**
     * 流程实例创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date stopTime;
    /**
     * 流程实例主表业务数据主键。
     */
    @ApiModelProperty(value = "流程实例主表业务数据主键")
    private String businessKey;

    @ApiModelProperty(value = "流程实例主表业务编号")
    private String businessCode;

    @ApiModelProperty(value = "流程实例主表业务名称")
    private String businessName;

    @ApiModelProperty(value = "流程实例主表业务路由")
    private String businessLink;

    @ApiModelProperty(value = "任务处理人")
    private String handler;

    @ApiModelProperty(value = "任务处理人名称")
    private String handlerName;

    public FlowTaskVo(TaskInfo task, FlowWorkOrder order, String handlerName) {
        this.setTaskId(task.getId());
        this.setTaskName(task.getName());
        this.setTaskKey(task.getTaskDefinitionKey());
        this.setTaskFormKey(task.getFormKey());
        this.setDefinitionId(task.getProcessDefinitionId());
        this.setDefinitionName(order.getProcessDefinitionName());
        this.setDefinitionKey(order.getProcessDefinitionKey());
        this.setInstanceId(task.getProcessInstanceId());
        this.setInitiator(order.getSubmitName());
        this.setStartTime(task.getCreateTime());
        if (task instanceof HistoricTaskInstance) {
            this.setStopTime(((HistoricTaskInstance) task).getEndTime());
        } else {
            this.setStopTime(task.getDueDate());
        }
        this.setBusinessKey(order.getBusinessKey());
        this.setBusinessCode(order.getCode());
        this.setBusinessName(order.getName());
        this.setBusinessLink(order.getBusinessLink());
        this.setParams(task.getProcessVariables());
        this.setHandler(task.getAssignee());
        this.setHandlerName(handlerName);
    }
}
