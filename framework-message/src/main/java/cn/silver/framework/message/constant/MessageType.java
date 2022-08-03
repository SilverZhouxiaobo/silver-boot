package cn.silver.framework.message.constant;

import cn.silver.framework.core.constant.BaseContant;
import cn.silver.framework.mq.constans.MessageContant;

public enum MessageType implements BaseContant {
    /**
     * 手机短信
     */
    SMS("mobile", "短信", MessageContant.SMS_QUEUE_KEY),
    /**
     * 电子邮件
     */
    MAIL("email", "邮件", MessageContant.MAIL_QUEUE_KEY),
    /**
     * 微信消息
     */
    WE_CHAT("wechat", "微信", MessageContant.WECHAT_QUEUE_KEY);

    private final String code;
    private final String name;

    private final String routing;

    MessageType(String code, String name, String routing) {
        this.code = code;
        this.name = name;
        this.routing = routing;
    }

    public static MessageType getType(String code) {
        MessageType result = null;
        for (MessageType type : MessageType.values()) {
            if (type.getCode().equals(code)) {
                result = type;
                break;
            }
        }
        return result;
    }

    public String getRouting() {
        return routing;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
