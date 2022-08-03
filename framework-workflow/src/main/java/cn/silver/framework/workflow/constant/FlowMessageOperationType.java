package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 工作流消息操作类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public enum FlowMessageOperationType implements BaseContant {


    READ_NONE("00", "未读"),
    READ_FINISHED("01", "已读"),
    COPY("02", "抄送");

    private final String code;
    private final String name;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    FlowMessageOperationType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public java.lang.String getCode() {
        return code;
    }

    @Override
    public java.lang.String getName() {
        return name;
    }
}
