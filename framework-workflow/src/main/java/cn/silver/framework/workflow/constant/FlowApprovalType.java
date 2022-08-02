package cn.silver.framework.workflow.constant;

import cn.silver.framework.core.constant.BaseContant;
import org.apache.commons.lang3.StringUtils;

/**
 * 工作流任务触发BUTTON。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public enum FlowApprovalType implements BaseContant {
    /**
     * 发起申请
     */
    SAVE("save", "保存"),
    /**
     * 审批同意
     */
    AGREE("agree", "同意"),
    /**
     * 审批驳回
     */
    REJECT("reject", "驳回"),
    /**
     * 审批撤销
     */
    REVOKE("revoke", "撤销"),
    /**
     * 转办
     */
    TRANSFER("transfer", "转办"),
    /**
     * 审批同意
     */
    AUTO_AGREE("auto_agree", "超时自动通过"),
    /**
     * 审批驳回
     */
    AUTO_REJECT("auto_reject", "超时自动驳回"),
    /**
     * 多实例会签
     */
    MULTI_SIGN("multi_sign", "多实例会签"),
    /**
     * 会签同意
     */
    MULTI_AGREE("multi_agree", "会签同意"),
    /**
     * 会签拒绝
     */
    MULTI_REFUSE("multi_refuse", "会签拒绝"),
    /**
     * 会签弃权
     */
    MULTI_ABSTAIN("multi_abstain", "会签弃权"),
    /**
     * 多实例加签
     */
    MULTI_CONSIGN("multi_consign", "多实例加签"),
    /**
     * 流程中止
     */
    STOP("stop", "中止");

    private final String code;
    private final String name;

    public static String getName(String code) {
        String result = "";
        if (StringUtils.isNotBlank(code)) {
            for (FlowApprovalType curr : FlowApprovalType.values()) {
                if (curr.getCode().equals(code)) {
                    result = curr.getName();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    FlowApprovalType(String code, String name) {
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
