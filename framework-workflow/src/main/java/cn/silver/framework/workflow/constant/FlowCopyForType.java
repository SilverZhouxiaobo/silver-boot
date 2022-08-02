package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;

public enum FlowCopyForType implements BaseContant {

    USER("user", "抄送人"),
    DEPT("dept", "抄送部门"),
    ROLE("role", "抄送角色"),
    SELF_DEPT_LEADER("deptPostLeader", "审批人部门领导"),
    UP_DEPT_LEADER("upDeptPostLeader", "审批人上级部门领导"),
    POST("allDeptPost", "抄送岗位"),
    SELF_DEPT_POST("selfDeptPost", "审批人部门岗位"),
    UP_DEPT_POST("upDeptPost", "审批人上级部门岗位"),
    DEPT_POST("deptPost", "指定部门岗位");

    private final String code;
    private final String name;

    FlowCopyForType(String code, String name) {
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
