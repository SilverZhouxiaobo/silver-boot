package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysMessageConfig;
import cn.silver.framework.system.service.ISysMessageConfigService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息推送接口配置Controller
 *
 * @author hb
 * @date 2022-06-28
 */

@RestController
@Api(tags = {"消息推送接口配置"})
@RequestMapping("/sys/message/config")
public class SysMessageConfigController extends DataController<ISysMessageConfigService, SysMessageConfig> {

    public SysMessageConfigController() {
        this.authorize = "sys:message:config";
        this.title = "消息推送接口配置";
    }
}
