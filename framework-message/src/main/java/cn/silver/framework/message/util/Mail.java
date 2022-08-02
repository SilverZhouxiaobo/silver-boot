package cn.silver.framework.message.util;

import java.util.Date;


/**
 * ClassName: Mail
 *
 * @author chenqc
 * @Description: 邮件bean
 * @date 2015-9-24
 */
public class Mail {
    //邮件id
    private String messageId;
    //主题
    private String subject;
    //发送时间
    private Date sentDate;
    //是否需要回复
    private Boolean replySign;
    //是否已读
    private Boolean isNew;
    //发送人地址
    private String from;
    //收信人地址
    private String to;
    //抄送
    private String cc;
    //暗抄
    private String bcc;
    //邮件内容
    private String bodyText;
    //是否包含附件
    private Boolean isContainAttach;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean getReplySign() {
        return replySign;
    }

    public void setReplySign(Boolean replySign) {
        this.replySign = replySign;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public Boolean getIsContainAttach() {
        return isContainAttach;
    }

    public void setIsContainAttach(Boolean isContainAttach) {
        this.isContainAttach = isContainAttach;
    }


//	System.out.println("邮件　" + i + "　主题:　" + re.getSubject());
//	System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
//	System.out.println("邮件　" + i + "　是否需要回复:　" + re.getReplySign());
//	System.out.println("邮件　" + i + "　是否已读:　" + re.isNew());
//	System.out.println("邮件　" + i + "　是否包含附件:　"
//			+ re.isContainAttach((Part) message[i]));
//	System.out.println("邮件　" + i + "　发送人地址:　" + re.getFrom());
//	System.out
//			.println("邮件　" + i + "　收信人地址:　" + re.getMailAddress("to"));
//	System.out.println("邮件　" + i + "　抄送:　" + re.getMailAddress("cc"));
//	System.out.println("邮件　" + i + "　暗抄:　" + re.getMailAddress("bcc"));
//	re.setDateFormat("yy年MM月dd日　HH:mm");
//	System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
//	System.out.println("邮件　" + i + "　邮件ID:　" + re.getMessageId());
//	re.getMailContent((Part) message[i]);
//	System.out.println("邮件　" + i + "　正文内容:　\r\n" + re.getBodyText());
}
