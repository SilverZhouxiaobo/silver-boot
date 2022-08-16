package cn.silver.framework.monitor.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 操作人类别
 *
 * @author hb
 */
public enum OperatorType implements BaseContant {
    /**
     * 其它
     */

    /**
     * 后台用户
     */
    MANAGE("01", "后台用户"),

    /**
     * 手机端用户
     */
    MOBILE("02", "手机端用户"),

    OTHER("99", "其他");
    private final String code;
    private final String name;

    OperatorType(String code, String name) {
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
