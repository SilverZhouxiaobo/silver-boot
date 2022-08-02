package cn.silver.framework.message.constant;

import cn.silver.framework.core.constant.BaseContant;

public enum MessageStatus implements BaseContant {
    /**
     * 手机短信
     */
    NO_SEND("00", "未推送"),
    /**
     * 电子邮件
     */
    FAILD("01", "推送失败"),
    /**
     * 微信消息
     */
    FINISHED("02", "已推送");

    private final String code;
    private final String name;

    MessageStatus(String code, String name) {
        this.code = code;
        this.name = name;
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
