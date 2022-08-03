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

/**
 * 消息接收人信息对象 sys_message_receiver
 *
 * @author hb
 * @date 2022-07-01
 */

@Data
@Table(name = "sys_message_receiver")
@ApiModel(value = "SysMessageReceiver", description = "SysMessageReceiver对象")
public class SysMessageReceiver extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 消息ID
     */
    @Excel(name = "消息ID", sort = 2)
    @Column(name = "message_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "消息ID")
    private String messageId;

    /**
     * 接收人
     */
    @Excel(name = "接收人", sort = 3)
    @Column(name = "receiver", searchType = SearchType.EQ)
    @ApiModelProperty(value = "接收人")
    private String receiver;

    /**
     * 接收人名称
     */
    @Excel(name = "接收人名称", sort = 4)
    @Column(name = "receiver_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "接收人名称")
    private String receiverName;

    /**
     * 接收账号
     */
    @Excel(name = "接收账号", sort = 5)
    @Column(name = "receive_account", searchType = SearchType.EQ)
    @ApiModelProperty(value = "接收账号")
    private String receiveAccount;

    /**
     * 推送状态
     */
    @Excel(name = "推送状态", sort = 6)
    @Column(name = "send_status", searchType = SearchType.EQ)
    @ApiModelProperty(value = "推送状态")
    private String sendStatus;

    /**
     * 发送次数
     */
    @Excel(name = "发送次数", sort = 7)
    @Column(name = "send_num", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送次数")
    private Integer sendNum;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("messageId", getMessageId())
                .append("receiver", getReceiver())
                .append("receiverName", getReceiverName())
                .append("receiveAccount", getReceiveAccount())
                .append("sendStatus", getSendStatus())
                .append("sendNum", getSendNum())
                .toString();
    }
}
