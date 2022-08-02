package cn.silver.framework.monitor.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 操作状态
 *
 * @author hb
 */
public enum BusinessStatus implements BaseContant {
    /**
     * 成功
     */
    SUCCESS("00", "成功"),

    /**
     * 失败
     */
    FAIL("01", "失败");
    private final String code;
    private final String name;

    BusinessStatus(String code, String name) {
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
