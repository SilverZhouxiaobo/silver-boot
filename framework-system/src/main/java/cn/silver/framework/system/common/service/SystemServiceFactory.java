package cn.silver.framework.system.common.service;

import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.service.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Getter
@Component
public class SystemServiceFactory {
    @Resource
    protected ISysLogininforService logininforService;
    @Resource
    protected ISysOperLogService operLogService;
    @Resource
    protected ISysDictTypeService dictTypeService;
    @Resource
    protected ISysCategoryService categoryService;
    @Resource
    protected ISysExternalLinkService linkService;
    @Resource
    protected ISysSerialNumberService numberService;
    @Resource
    protected ISysTagService iSysTagService;
    @Resource
    protected ISysConfigService configService;
    @Resource
    protected ISysUserHandleService handleService;
    @Resource
    protected ISysUserService userService;

    protected LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
