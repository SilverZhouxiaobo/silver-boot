package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysUserMail;
import cn.silver.framework.system.service.ISysUserMailService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户邮箱配置Controller
 *
 * @author hb
 * @date 2022-07-06
 */

@RestController
@Api(tags = {"用户邮箱配置"})
@RequestMapping("/sys/user/mail")
public class SysUserMailController extends DataController<ISysUserMailService, SysUserMail> {

    public SysUserMailController() {
        this.authorize = "sys:user:mail";
        this.title = "用户邮箱配置";
    }
}
