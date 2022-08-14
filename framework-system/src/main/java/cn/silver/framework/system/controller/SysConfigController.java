package cn.silver.framework.system.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.system.domain.SysConfig;
import cn.silver.framework.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 参数配置 信息操作处理
 *
 * @author hb
 */
@Slf4j
@RestController
@RequestMapping("/sys/config")
@Api(tags = "配置管理-系统参数配置")
public class SysConfigController extends DataController<ISysConfigService, SysConfig> {

    public SysConfigController() {
        this.authorize = "system:config";
        this.title = "系统参数配置管理";
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    @ApiOperation("根据参数键名查询参数值")
    public Response<String> getConfigKey(@ApiParam(name = "configKey", value = "参数键名", required = true) @PathVariable("configKey") String configKey) {
        return Response.success(baseService.selectConfigByKey(configKey));
    }

    /**
     * 清空缓存
     */
    //TODO 后期移动到缓存管理中
    @DeleteMapping("/clearCache")
    @ApiOperation("清空缓存")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    public Response<Void> clearCache() {
        baseService.clearCache();
        return Response.success();
    }
}
