package cn.silver.framework.system.controller;

import cn.silver.framework.core.controller.TreeController;
import cn.silver.framework.system.domain.SysTag;
import cn.silver.framework.system.service.ISysTagService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: sys_tag
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Slf4j
@RestController
@Api(tags = "配置管理-标签管理")
@RequestMapping("/sys/tag")
public class SysTagController extends TreeController<ISysTagService, SysTag> {
    public SysTagController() {
        this.authorize = "system:tag";
        this.title = "系统标签管理";
    }
}
