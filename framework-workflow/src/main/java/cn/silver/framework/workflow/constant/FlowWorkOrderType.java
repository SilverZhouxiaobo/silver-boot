package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

public enum FlowWorkOrderType implements BaseContant {
    /**
     * 活动计划审批工单
     */
    EVENT_PLAN("event_plan", "活动计划审批工单"),
    /**
     * 活动业务工单
     */
    EVENT_ACTIVITY("event_activity", "活动业务工单"),
    /**
     * 问题流程工单
     */
    QA_QUESTION_RECORD("qa_question_record", "问题流程工单");

    private final String code;
    private final String name;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    FlowWorkOrderType(String code, String name) {
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
