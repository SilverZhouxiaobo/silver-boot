package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * @author Administrator
 */

public enum FlowPublishStatus implements BaseContant {
    /**
     * 未发布
     */
    UNPUBLISHED("00", "未发布"),

    /**
     * 已发布
     */
    PUBLISHED("01", "已发布");
    private final String code;
    private final String name;

    FlowPublishStatus(String code, String name) {
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
