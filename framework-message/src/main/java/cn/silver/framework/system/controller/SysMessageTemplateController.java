package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysMessageTemplate;
import cn.silver.framework.system.service.ISysMessageTemplateService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短消息模板Controller
 *
 * @author hb
 * @date 2022-06-20
 */

@RestController
@Api(tags = {"消息模板管理"})
@RequestMapping("/sys/message/template")
public class SysMessageTemplateController extends DataController<ISysMessageTemplateService, SysMessageTemplate> {

    public SysMessageTemplateController() {
        this.authorize = "sys:message:template";
        this.title = "消息模板管理";
    }
}
