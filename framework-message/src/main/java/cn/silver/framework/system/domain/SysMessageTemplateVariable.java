package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * 消息模板参数对象 sys_message_template_variable
 *
 * @author hb
 * @date 2022-07-01
 */

@Data
@Table(name = "sys_message_template_variable")
@ApiModel(value = "SysMessageTemplateVariable", description = "SysMessageTemplateVariable对象")
public class SysMessageTemplateVariable extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 模板id
     */
    @Excel(name = "模板id", sort = 2)
    @NotBlank(message = "模板id不能为空")
    @Column(name = "template", searchType = SearchType.EQ)
    @ApiModelProperty(value = "模板id")
    private String template;

    /**
     * 显示名
     */
    @Excel(name = "显示名", sort = 3)
    @Column(name = "show_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "显示名")
    private String showName;

    /**
     * 参数名
     */
    @Excel(name = "参数名", sort = 4)
    @Column(name = "variable_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "参数名")
    private String variableName;

    /**
     * 参数校验
     */
    @Excel(name = "参数校验", sort = 5)
    @Column(name = "variable_require", searchType = SearchType.EQ)
    @ApiModelProperty(value = "参数校验")
    private Boolean variableRequire;

    /**
     * 默认值
     */
    @Excel(name = "默认值", sort = 6)
    @Column(name = "default_value", searchType = SearchType.EQ)
    @ApiModelProperty(value = "默认值")
    private String defaultValue;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("template", getTemplate())
                .append("showName", getShowName())
                .append("variableName", getVariableName())
                .append("variableRequire", getVariableRequire())
                .append("defaultValue", getDefaultValue())
                .toString();
    }
}
