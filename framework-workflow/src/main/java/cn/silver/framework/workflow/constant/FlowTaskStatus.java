package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 工作流任务类型。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public enum FlowTaskStatus implements BaseContant {

    DRAFT("draft", "草稿"),
    SUBMITTED("submitted", "已提交"),
    APPROVING("approving", "流转中"),
    FINISHED("finished", "已完成"),
    STOPPED("stopped", "已终止"),
    CANCELLED("cancelled", "已取消");
    private final String code;
    private final String name;

    FlowTaskStatus(String code, String name) {
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
