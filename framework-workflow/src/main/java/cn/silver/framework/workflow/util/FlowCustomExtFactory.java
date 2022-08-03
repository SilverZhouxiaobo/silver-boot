package cn.silver.framework.workflow.util;

import org.springframework.stereotype.Component;

/**
 * 工作流自定义扩展工厂类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Component
public class FlowCustomExtFactory {

    private BaseFlowIdentityExtHelper flowIdentityExtHelper;

    /**
     * 获取业务模块自行实现的用户身份相关的扩展帮助实现类。
     *
     * @return 业务模块自行实现的用户身份相关的扩展帮助实现类。
     */
    public BaseFlowIdentityExtHelper getFlowIdentityExtHelper() {
        return flowIdentityExtHelper;
    }
}
