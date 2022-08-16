package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysUserHandle;
import cn.silver.framework.system.service.ISysUserHandleService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人收藏信息Controller
 *
 * @author hb
 * @date 2022-07-06
 */

@RestController
@Api(tags = {"个人收藏信息"})
@RequestMapping("/sys/user/collect")
public class SysUserHandleController extends DataController<ISysUserHandleService, SysUserHandle> {

    public SysUserHandleController() {
        this.authorize = "sys:user:collect";
        this.title = "个人收藏信息";
    }
}
