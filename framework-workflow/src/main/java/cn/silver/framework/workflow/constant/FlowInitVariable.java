package cn.silver.framework.workflow.constant;

/**
 * @author Administrator
 */

public enum FlowInitVariable {
    /**
     * 流程实例发起人
     */
    INITIATOR_VAR(FlowVariableType.INSTANCE.getCode(), "initiator", "流程实例发起人"),
    /**
     * 操作类型
     */
    OPERATION_TYPE(FlowVariableType.INSTANCE.getCode(), "operationType", "操作类型"),
    /**
     * 审批意见
     */
    OPERATION_OPINION(FlowVariableType.INSTANCE.getCode(), "operationOpinion", "审批意见"),
    /**
     * 流程启动ID
     */
    START_USER_ID(FlowVariableType.TASK.getCode(), "startUserId", "流程启动用户ID"),
    /**
     * 流程启动用户
     */
    START_USER_NAME(FlowVariableType.TASK.getCode(), "startUserName", "流程启动用户");

    private final String type;
    private final String showName;
    private final String variableName;

    FlowInitVariable(String type, String showName, String variableName) {
        this.type = type;
        this.showName = showName;
        this.variableName = variableName;
    }

    public String getType() {
        return type;
    }

    public String getShowName() {
        return showName;
    }

    public String getVariableName() {
        return variableName;
    }
}
