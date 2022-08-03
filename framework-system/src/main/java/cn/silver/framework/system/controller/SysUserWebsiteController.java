package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysUserWebsite;
import cn.silver.framework.system.service.ISysUserWebsiteService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 常用网址Controller
 *
 * @author hb
 * @date 2022-07-06
 */

@RestController
@Api(tags = {"常用网址"})
@RequestMapping("/sys/user/website")
public class SysUserWebsiteController extends DataController<ISysUserWebsiteService, SysUserWebsite> {

    public SysUserWebsiteController() {
        this.authorize = "sys:user:website";
        this.title = "常用网址";
    }
}
