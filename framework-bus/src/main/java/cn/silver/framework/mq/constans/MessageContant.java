package cn.silver.framework.mq.constans;


/**
 * 消息管理 mq 配置常量
 */
public class MessageContant {

    public static final String EXCHANGE_KEY = "framework-message-event-exchange";

    public static final String SMS_QUEUE_KEY = "framework.message.sms";
    public static final String SMS_QUEUE_CODE = "framework.message.sms.queue";

    public static final String MAIL_QUEUE_KEY = "framework.message.email";
    public static final String MAIL_QUEUE_CODE = "framework.message.email.queue";

    public static final String WECHAT_QUEUE_KEY = "framework.message.wechat";
    public static final String WECHAT_QUEUE_CODE = "framework.message.wechat.queue";

    private MessageContant() {

    }
}
