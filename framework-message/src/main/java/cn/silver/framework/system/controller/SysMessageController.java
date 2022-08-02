package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysMessage;
import cn.silver.framework.system.service.ISysMessageService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短消息Controller
 *
 * @author hb
 * @date 2022-06-20
 */

@RestController
@Api(tags = {"消息管理"})
@RequestMapping("/sys/message")
public class SysMessageController extends DataController<ISysMessageService, SysMessage> {

    public SysMessageController() {
        this.authorize = "sys:message";
        this.title = "消息管理";
    }
}
