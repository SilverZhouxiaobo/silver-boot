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

/**
 * 消息推送接口配置对象 sys_message_config
 *
 * @author hb
 * @date 2022-06-28
 */

@Data
@Table(name = "sys_message_config")
@ApiModel(value = "SysMessageConfig", description = "SysMessageConfig对象")
public class SysMessageConfig extends DataEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 消息名称
     */
    @Excel(name = "消息名称", sort = 2)
    @Column(name = "message_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "消息名称")
    private String messageName;

    @Column(name = "message_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送方式")
    private String messageType;

    /**
     * 消息主体标识
     */
    @Excel(name = "消息主体标识", sort = 3)
    @Column(name = "corp_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "消息主体标识")
    private String corpId;
    @Excel(name = "是否自建应用", sort = 4)
    @Column(name = "owner", searchType = SearchType.EQ)
    @ApiModelProperty(value = "是否自建应用")
    private Boolean owner;
    /**
     * 消息推送地址
     */
    @Excel(name = "消息推送地址", sort = 4)
    @Column(name = "message_link", searchType = SearchType.EQ)
    @ApiModelProperty(value = "消息推送地址")
    private String messageLink;

    /**
     * 应用ID
     */
    @Excel(name = "应用ID", sort = 5)
    @Column(name = "agent_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "应用ID")
    private String agentId;

    /**
     * 应用密钥
     */
    @Excel(name = "应用密钥", sort = 6)
    @Column(name = "agent_secret", searchType = SearchType.EQ)
    @ApiModelProperty(value = "应用密钥")
    private String agentSecret;

    @Excel(name = "是否默认", sort = 5)
    @Column(name = "default_value", searchType = SearchType.EQ)
    @ApiModelProperty(value = "是否默认")
    private Boolean defaultValue;

    @Override
    public String getLabel() {
        return messageName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("messageName", getMessageName())
                .append("corpId", getCorpId())
                .append("messageLink", getMessageLink())
                .append("agentId", getAgentId())
                .append("agentSecret", getAgentSecret())
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
