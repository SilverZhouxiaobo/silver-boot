package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 工作流消息类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public enum FlowMessageType implements BaseContant {

    REMIND_TYPE("00", "催办消息"),
    COPY_TYPE("01", "抄送消息");

    private final String code;
    private final String name;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    FlowMessageType(String code, String name) {
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
