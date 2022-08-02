package cn.silver.framework.flow.constant;

import cn.silver.framework.core.constant.BaseContant;

/**
 * 流程意见类型
 *
 * @author Xuan xuan
 * @date 2021/4/19
 */
public enum FlowComment implements BaseContant {

    /**
     * 说明
     */
    NORMAL("1", "正常意见"),
    REBACK("2", "退回意见"),
    REJECT("3", "驳回意见"),
    DELEGATE("4", "委派意见"),
    ASSIGN("5", "转办意见"),
    STOP("6", "终止流程");

    /**
     * 类型
     */
    private final String code;

    /**
     * 说明
     */
    private final String name;

    FlowComment(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
