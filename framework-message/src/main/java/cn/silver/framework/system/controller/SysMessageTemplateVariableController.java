package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysMessageTemplateVariable;
import cn.silver.framework.system.service.ISysMessageTemplateVariableService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息模板参数Controller
 *
 * @author hb
 * @date 2022-07-01
 */

@RestController
@Api(tags = {"消息模板参数"})
@RequestMapping("/sys/message/template/variable")
public class SysMessageTemplateVariableController extends DataController<ISysMessageTemplateVariableService, SysMessageTemplateVariable> {

    public SysMessageTemplateVariableController() {
        this.authorize = "sys:message:template:variable";
        this.title = "消息模板参数";
    }
}
