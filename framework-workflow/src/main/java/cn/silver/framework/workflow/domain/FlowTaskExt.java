package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 * 流程任务扩展实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程任务扩展实体对象")
@Table(name = "flow_task_ext")
public class FlowTaskExt extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 流程引擎的定义Id。
     */
    @ApiModelProperty(value = "流程引擎的定义Id")
    @Column(name = "process_definition_id", searchType = SearchType.EQ)
    private String processDefinitionId;

    /**
     * 流程引擎任务Id。
     */
    @ApiModelProperty(value = "流程引擎任务Id")
    @Column(name = "task_id", searchType = SearchType.EQ)
    private String taskId;

    /**
     * 操作列表JSON。
     */
    @ApiModelProperty(value = "操作列表JSON")
    private String operationListJson;

    /**
     * 变量列表JSON。
     */
    @ApiModelProperty(value = "变量列表JSON")
    private String variableListJson;

    /**
     * 存储多实例的assigneeList的JSON。
     */
    @ApiModelProperty(value = "存储多实例的assigneeList的JSON")
    private String assigneeListJson;

    /**
     * 分组类型。
     */
    @ApiModelProperty(value = "分组类型")
    private String groupType;

    /**
     * 保存岗位相关的数据。
     */
    @ApiModelProperty(value = "保存岗位相关的数据")
    private String deptPostListJson;

    /**
     * 逗号分隔的角色Id。
     */
    @ApiModelProperty(value = "逗号分隔的角色Id")
    private String roleIds;

    /**
     * 逗号分隔的部门Id。
     */
    @ApiModelProperty(value = "逗号分隔的部门Id")
    private String deptIds;

    /**
     * 逗号分隔候选用户名。
     */
    @ApiModelProperty(value = "逗号分隔候选用户名")
    private String candidateUsernames;

    /**
     * 抄送相关的数据。
     */
    @ApiModelProperty(value = "抄送相关的数据")
    private String copyListJson;
}
