package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 工作流任务类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public enum FlowTaskType implements BaseContant {

    USER_TYPE("00", "用户任务类型"),
    OTHER_TYPE("99", "其他任务类型");
    private final String code;
    private final String name;

    FlowTaskType(String code, String name) {
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
