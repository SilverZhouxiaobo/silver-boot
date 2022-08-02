package cn.silver.framework.flow.factory;

import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.security.util.SecurityUtils;
import lombok.Getter;
import org.flowable.engine.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * flowable 引擎注入封装
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@Getter
@Component
public class FlowServiceFactory {

    @Resource
    protected RepositoryService repositoryService;

    @Resource
    protected RuntimeService runtimeService;

    @Resource
    protected IdentityService identityService;

    @Resource
    protected TaskService taskService;

    @Resource
    protected FormService formService;

    @Resource
    protected HistoryService historyService;

    @Resource
    protected ManagementService managementService;

    @Resource
    @Qualifier("processEngine")
    protected ProcessEngine processEngine;

    protected LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
