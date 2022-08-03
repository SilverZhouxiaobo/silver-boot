package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysMessageReceiver;
import cn.silver.framework.system.service.ISysMessageReceiverService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息接收人信息Controller
 *
 * @author hb
 * @date 2022-07-01
 */
@RestController
@Api(tags = {"消息接收人信息"})
@RequestMapping("/sys_message/receiver")
public class SysMessageReceiverController extends DataController<ISysMessageReceiverService, SysMessageReceiver> {

    public SysMessageReceiverController() {
        this.authorize = "sys:message:receiver";
        this.title = "消息接收人信息";
    }
}
