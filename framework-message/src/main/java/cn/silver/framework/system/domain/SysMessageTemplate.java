package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 短消息模板对象 sys_sms_template
 *
 * @author hb
 * @date 2022-06-20
 */

@Data
@Table(name = "sys_message_template")
@ApiModel(value = "SysMessageTemplate", description = "SysMessageTemplate对象")
public class SysMessageTemplate extends DataEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 模板标题
     */
    @Excel(name = "模板名称", sort = 2)
    @NotBlank(message = "模板名称不能为空")
    @Column(name = "template_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "模板名称")
    private String templateName;

    /**
     * 模板CODE
     */
    @Excel(name = "模板编码", sort = 3)
    @NotBlank(message = "模板编码不能为空")
    @Column(name = "template_code", searchType = SearchType.EQ, unique = true, dictable = true)
    @ApiModelProperty(value = "模板编码")
    private String templateCode;

    /**
     * 模板类型：1短信;2邮件 3微信
     */
    @Excel(name = "模板类型", sort = 4)
    @NotBlank(message = "模板类型不能为空")
    @Column(name = "template_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "模板类型")
    private String templateType;

    /**
     * 模板内容
     */
    @Excel(name = "模板内容", sort = 5)
    @NotBlank(message = "模板内容不能为空")
    @Column(name = "template_content", searchType = SearchType.EQ)
    @ApiModelProperty(value = "模板内容")
    private String templateContent;

    /**
     * 模板测试json
     */
    @Excel(name = "样例数据", sort = 6)
    @Column(name = "template_test_json", searchType = SearchType.EQ)
    @ApiModelProperty(value = "样例数据")
    private String templateTestJson;

    private transient List<SysMessageTemplateVariable> variables;

    @Override
    public String getLabel() {
        return this.templateName;
    }

    @Override
    public String getValue() {
        return this.templateCode;
    }

    @Override
    public String getGroup() {
        return this.templateType;
    }

    @Override
    public String DictRemark() {
        return this.templateTestJson;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("templateName", getTemplateName())
                .append("templateCode", getTemplateCode())
                .append("templateType", getTemplateType())
                .append("templateContent", getTemplateContent())
                .append("templateTestJson", getTemplateTestJson())
                .append("createBy", getCreateBy())
                .append("createName", getCreateName())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateName", getUpdateName())
                .append("updateTime", getUpdateTime())
                .append("deleted", getDeleted())
                .toString();
    }
}
