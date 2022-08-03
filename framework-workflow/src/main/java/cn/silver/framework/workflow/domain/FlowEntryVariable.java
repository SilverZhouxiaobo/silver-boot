package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.workflow.constant.FlowInitVariable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程变量实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程变量对象")
@Table(name = "flow_entry_variable")
public class FlowEntryVariable extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 流程Id。
     */
    @ApiModelProperty(value = "流程Id")
    @NotNull(message = "数据验证失败，流程Id不能为空！")
    @Column(name = "entry_id", searchType = SearchType.EQ)
    private String entryId;

    /**
     * 变量名。
     */
    @ApiModelProperty(value = "变量名")
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    @Column(name = "variable_name", searchType = SearchType.LIKE, unique = true, nullable = false)
    private String variableName;

    /**
     * 显示名。
     */
    @ApiModelProperty(value = "显示名")
    @NotBlank(message = "数据验证失败，显示名不能为空！")
    @Column(name = "show_name", searchType = SearchType.LIKE, unique = true, nullable = false)
    private String showName;

    /**
     * 流程变量类型。
     */
    @ApiModelProperty(value = "流程变量类型")
    @NotNull(message = "数据验证失败，流程变量类型不能为空！")
    @Column(name = "variable_type", searchType = SearchType.EQ)
    private String variableType;

    /**
     * 绑定数据源Id。
     */
    @ApiModelProperty(value = "绑定数据源Id")
    @Column(name = "bind_datasource_id", searchType = SearchType.EQ)
    private String bindDatasourceId;

    /**
     * 绑定数据源关联Id。
     */
    @ApiModelProperty(value = "绑定数据源关联Id")
    @Column(name = "bind_relation_id", searchType = SearchType.EQ)
    private String bindRelationId;

    /**
     * 绑定字段Id。
     */
    @ApiModelProperty(value = "绑定字段Id")
    @Column(name = "bind_column_id", searchType = SearchType.EQ)
    private String bindColumnId;

    /**
     * 是否内置。
     */
    @ApiModelProperty(value = "是否内置")
    @NotNull(message = "数据验证失败，是否内置不能为空！")
    @Column(name = "builtin", searchType = SearchType.EQ)
    private Boolean builtin;

    public static FlowEntryVariable getDefaultIdentity(String entryId, FlowInitVariable variable) {
        FlowEntryVariable operationTypeVariable = new FlowEntryVariable();
        operationTypeVariable.setEntryId(entryId);
        operationTypeVariable.setVariableName(variable.getVariableName());
        operationTypeVariable.setShowName(variable.getShowName());
        operationTypeVariable.setVariableType(variable.getType());
        operationTypeVariable.setBuiltin(true);
        return operationTypeVariable;
    }

    @Override
    public String getLabel() {
        return this.showName;
    }

    @Override
    public String getValue() {
        return this.variableName;
    }
}
