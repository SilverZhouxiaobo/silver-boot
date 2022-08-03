package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.task.api.TaskInfo;

import javax.persistence.Table;

/**
 * FlowTaskComment实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("FlowTaskComment对象")
@Table(name = "flow_task_comment")
public class FlowTaskComment extends DataEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务主键")
    @Column(name = "business_key", searchType = SearchType.EQ)
    private String businessKey;

    @ApiModelProperty(value = "业务编号")
    @Column(name = "business_code", searchType = SearchType.EQ)
    private String businessCode;

    @ApiModelProperty(value = "业务名称")
    @Column(name = "business_name", searchType = SearchType.EQ)
    private String businessName;

    @ApiModelProperty(value = "业务路由")
    @Column(name = "business_link", searchType = SearchType.EQ)
    private String businessLink;

    /**
     * 流程实例Id。
     */
    @ApiModelProperty(value = "流程实例Id")
    @Column(name = "process_instance_id", searchType = SearchType.EQ)
    private String processInstanceId;

    /**
     * 任务Id。
     */
    @ApiModelProperty(value = "任务Id")
    @Column(name = "task_id", searchType = SearchType.EQ)
    private String taskId;

    /**
     * 任务标识。
     */
    @ApiModelProperty(value = "任务标识")
    @Column(name = "task_key", searchType = SearchType.EQ)
    private String taskKey;

    /**
     * 任务名称。
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 审批类型。
     */
    @ApiModelProperty(value = "审批类型")
    @Column(name = "approval_type", searchType = SearchType.NE)
    private String approvalType;

    /**
     * 委托指定人，比如加签、转办等。
     */
    @ApiModelProperty(value = "委托指定人，比如加签、转办等")
    private String delegateAssignee;

    /**
     * 自定义数据。开发者可自行扩展，推荐使用JSON格式数据。
     */
    @ApiModelProperty(value = "自定义数据s")
    private String customBusinessData;

    public FlowTaskComment(TaskInfo task) {
        this.fillWith(task);
    }

    public FlowTaskComment(FlowWorkOrder order, String approvalType, String approveOpinion) {
        this.businessKey = order.getBusinessKey();
        this.businessCode = order.getCode();
        this.businessName = order.getName();
        this.businessLink = order.getBusinessLink();
        this.approvalType = approvalType;
        this.setRemark(approveOpinion);
    }

    public void fillWith(TaskInfo task) {
        this.taskId = task.getId();
        this.taskKey = task.getTaskDefinitionKey();
        this.taskName = task.getName();
        this.processInstanceId = task.getProcessInstanceId();
    }

    public JSONObject getBusinessParam() {
        JSONObject param = new JSONObject();
        param.put("businessKey", this.getBusinessKey());
        param.put("businessCode", this.getBusinessCode());
        param.put("businessName", this.getBusinessName());
        param.put("businessLink", this.getBusinessLink());
        return param;
    }
}
