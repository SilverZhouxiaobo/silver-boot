package cn.silver.framework.system.controller;


import cn.silver.framework.core.controller.TreeController;
import cn.silver.framework.system.domain.SysRegion;
import cn.silver.framework.system.service.ISysRegionService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行政区域信息Controller
 *
 * @author hb
 * @date 2022-07-06
 */

@RestController
@Api(tags = {"行政区域信息"})
@RequestMapping("/sys/region")
public class SysRegionController extends TreeController<ISysRegionService, SysRegion> {

    public SysRegionController() {
        this.authorize = "sys:region";
        this.title = "行政区域信息";
    }
}
