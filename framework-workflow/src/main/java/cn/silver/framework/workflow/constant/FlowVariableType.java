package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 流程变量类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public enum FlowVariableType implements BaseContant {

    INSTANCE("00", "流程实例变量"),
    TASK("01", "任务变量");

    private final String code;
    private final String name;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    FlowVariableType(String code, String name) {
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
