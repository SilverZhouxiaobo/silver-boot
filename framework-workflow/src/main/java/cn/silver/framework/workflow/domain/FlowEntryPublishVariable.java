package cn.silver.framework.workflow.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * FlowEntryPublishVariable实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@NoArgsConstructor
@ApiModel("流程发布数据变量的实体对象")
@Table(name = "flow_entry_publish_variable")
public class FlowEntryPublishVariable extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 流程Id。
     */
    @ApiModelProperty(value = "流程发布Id")
    @Column(name = "entry_publish_id", searchType = SearchType.EQ)
    private String entryPublishId;

    /**
     * 变量名。
     */
    @ApiModelProperty(value = "变量名")
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    private String variableName;

    /**
     * 显示名。
     */
    @ApiModelProperty(value = "显示名")
    @NotBlank(message = "数据验证失败，显示名不能为空！")
    private String showName;

    /**
     * 变量类型。
     */
    @ApiModelProperty(value = "流程变量类型")
    @NotNull(message = "数据验证失败，流程变量类型不能为空！")
    private String variableType;

    /**
     * 绑定数据源Id。
     */
    @ApiModelProperty(value = "绑定数据源Id")
    private String bindDatasourceId;

    /**
     * 绑定数据源关联Id。
     */
    @ApiModelProperty(value = "绑定数据源关联Id")
    private String bindRelationId;

    /**
     * 绑定字段Id。
     */
    @ApiModelProperty(value = "绑定字段Id")
    private String bindColumnId;

    /**
     * 是否内置。
     */
    @ApiModelProperty(value = "是否内置")
    @NotNull(message = "数据验证失败，是否内置不能为空！")
    private Boolean builtin;

    @Override
    public String getLabel() {
        return this.showName;
    }

    @Override
    public String getValue() {
        return this.variableName;
    }
}
