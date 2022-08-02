package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.FlowEntity;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.workflow.constant.FlowTaskStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 工作流工单实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("工作流工单对象")
@Table(name = "flow_work_order")
public class FlowWorkOrder extends FlowEntity {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "工单名称")
    @Column(name = "name", searchType = SearchType.EQ)
    private String name;

    /**
     * 流程实例Id。
     */
    @ApiModelProperty(value = "流程实例Id")
    @Column(name = "process_instance_id", searchType = SearchType.EQ)
    private String processInstanceId;

    /**
     * 流程引擎的定义Id。
     */
    @ApiModelProperty(value = "流程引擎的定义Id")
    @Column(name = "process_definition_id", searchType = SearchType.EQ)
    private String processDefinitionId;

    /**
     * 流程定义标识。
     */
    @ApiModelProperty(value = "流程定义标识")
    @Column(name = "process_definition_key", searchType = SearchType.LIKE)
    private String processDefinitionKey;

    /**
     * 流程名称。
     */
    @ApiModelProperty(value = "流程名称")
    @Column(name = "process_definition_name", searchType = SearchType.LIKE)
    private String processDefinitionName;

    /**
     * 流程引擎的定义Id。
     */
    @ApiModelProperty(value = "待执行的任务Id")
    @Column(name = "task_id", searchType = SearchType.EQ)
    private String taskId;

    /**
     * 流程定义标识。
     */
    @ApiModelProperty(value = "待执行的任务标识")
    @Column(name = "task_definition_key", searchType = SearchType.LIKE)
    private String taskDefinitionKey;

    /**
     * 流程名称。
     */
    @ApiModelProperty(value = "待执行的任务名称")
    @Column(name = "task_name", searchType = SearchType.LIKE)
    private String taskName;

    /**
     * 在线表单的主表Id。
     */
    @ApiModelProperty(value = "在线表单的主表Id")
    @Column(name = "online_table_id", searchType = SearchType.EQ)
    private String onlineTableId;

    /**
     * 静态表单所使用的数据表名。
     */
    @ApiModelProperty(value = "静态表单所使用的数据表名")
    @Column(name = "table_name", searchType = SearchType.EQ)
    private String tableName;

    /**
     * 业务主键值。
     */
    @ApiModelProperty(value = "业务主键值")
    @Column(name = "business_key", searchType = SearchType.EQ)
    private String businessKey;

    @ApiModelProperty(value = "业务路由")
    @Column(name = "business_link", searchType = SearchType.EQ)
    private String businessLink;

    /**
     * 提交用户登录名称。
     */
    @ApiModelProperty(value = "提交用户ID")
    @Column(name = "submit_id", searchType = SearchType.EQ)
    private String submitId;

    @ApiModelProperty(value = "提交用户名称")
    private String submitName;

    /**
     * 提交用户所在部门Id。
     */
    @ApiModelProperty(value = "提交用户所在部门Id")
    @Column(name = "dept_id", searchType = SearchType.EQ)
    private String deptId;

    @ApiModelProperty(value = "提交用户所在部门名称")
    private String deptName;

    public FlowWorkOrder(FlowEntity entity, FlowEntry entry) {
        this.setCode(entity.getCode());
        this.setName(entity.getLabel());
        this.setBusinessKey(entity.getId());
        this.setBusinessLink(entry.getDefaultRouterName());
        this.setStatus(FlowTaskStatus.DRAFT.getCode());
        this.setTaskName(entity.getInitTaskName());
        this.setTaskDefinitionKey(entity.getInitTaskKey());
        this.setTableName(entity.getClass().getAnnotation(Table.class).name());
        this.setProcessDefinitionName(entry.getProcessDefinitionName());
        this.setProcessDefinitionKey(entry.getProcessDefinitionKey());
    }

    @Override
    public void preInsert() {
        super.preInsert();
        LoginUser user = SecurityUtils.getLoginUser();
        this.setSubmitId(user.getId());
        this.setSubmitName(user.getNickName());
        this.setDeptId(user.getDeptId());
        this.setDeptName(user.getDeptName());
    }
}
