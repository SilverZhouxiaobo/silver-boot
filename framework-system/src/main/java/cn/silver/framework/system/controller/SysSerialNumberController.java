package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysSerialNumber;
import cn.silver.framework.system.service.ISysSerialNumberService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 单编码规则配置Controller
 *
 * @author hb
 * @date 2022-06-20
 */

@RestController
@Api(tags = {"表单编码规则配置"})
@RequestMapping("/sys/serial/number")
public class SysSerialNumberController extends DataController<ISysSerialNumberService, SysSerialNumber> {

    public SysSerialNumberController() {
        this.authorize = "sys:serial:number";
        this.title = "单编码规则配置";
    }
}
