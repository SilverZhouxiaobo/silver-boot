package cn.silver.framework.system.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.message.constant.MessageStatus;
import cn.silver.framework.message.constant.MessageType;
import cn.silver.framework.security.util.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import java.util.Date;

/**
 * 短消息对象 sys_sms
 *
 * @author hb
 * @date 2022-06-20
 */

@Data
@Table(name = "sys_message")
@ApiModel(value = "SysMessage", description = "SysMessage对象")
public class SysMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 消息标题
     */
    @Column(name = "title", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "消息标题")
    private String title;

    @Column(name = "template", searchType = SearchType.EQ)
    @ApiModelProperty(value = "消息模板")
    private String template;
    @Column(name = "config", searchType = SearchType.EQ)
    @ApiModelProperty(value = "消息模板")
    private String config;
    /**
     * 发送方式
     */
    @Column(name = "send_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送方式")
    private String sendType;

    @Column(name = "message_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送方式")
    private String messageType;

    /**
     * 发送方式
     */
    @Column(name = "person_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送对象")
    private String personType;

    /**
     * 接收人
     */
    @Column(name = "receiver", searchType = SearchType.EQ)
    @ApiModelProperty(value = "接收人")
    private String receiver;

    /**
     * 接收人名称
     */
    @Column(name = "receiver_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "接收人名称")
    private String receiverName;

    /**
     * 接收账号
     */
    @Column(name = "receive_account", searchType = SearchType.EQ)
    @ApiModelProperty(value = "接收账号")
    private String receiveAccount;
    @Column(name = "sender", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送人")
    private String sender;

    /**
     * 接收人名称
     */
    @Column(name = "sender_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "发送人名称")
    private String senderName;

    /**
     * 接收账号
     */
    @Column(name = "sender_account", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送账号")
    private String senderAccount;


    /**
     * 发送所需参数Json格式
     */
    private String sendParam;

    /**
     * 推送内容
     */
    @ApiModelProperty(value = "推送内容")
    private String sendContent;

    /**
     * 推送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "send_time", searchType = SearchType.BETWEEN)
    @ApiModelProperty(value = "推送时间")
    private Date sendTime;

    /**
     * 推送状态;0未推送 1推送成功 2推送失败 -1失败不再发送
     */
    @Column(name = "send_status", searchType = SearchType.EQ)
    @ApiModelProperty(value = "推送状态;0未推送 1推送成功 2推送失败 -1失败不再发送")
    private String sendStatus;

    /**
     * 发送次数;超过5次不再发送
     */
    @Column(name = "send_num", searchType = SearchType.EQ)
    @ApiModelProperty(value = "发送次数;超过5次不再发送")
    private Integer sendNum;

    @ApiModelProperty(value = "推送失败原因")
    private String remark;

    @ApiModelProperty(value = "发送所需参数Json格式")
    private JSONObject messageParams;

    public void preInsert() {
        super.preInsert();
        this.sendNum = 0;
        this.sendStatus = MessageStatus.NO_SEND.getCode();
        LoginUser user = SecurityUtils.getLoginUser();
        if (user != null) {
            this.sender = user.getId();
            this.senderName = user.getNickName();
            if (MessageType.SMS.getCode().equals(this.sendType)) {
                this.senderAccount = user.getMobile();
            } else if (MessageType.MAIL.getCode().equals(this.sendType)) {
                this.senderAccount = user.getEmail();
            } else if (MessageType.WE_CHAT.getCode().equals(this.sendType)) {
                this.senderAccount = user.getWechat();
            }
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("type", getSendType())
                .append("receiver", getReceiver())
                .append("receiverName", getReceiverName())
                .append("receiveAccount", getReceiveAccount())
                .append("sendParam", getSendParam())
                .append("sendContent", getSendContent())
                .append("sendTime", getSendTime())
                .append("sendStatus", getSendStatus())
                .append("sendNum", getSendNum())
                .append("remark", getRemark())
                .toString();
    }
}
