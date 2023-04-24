package cn.silver.framework.web.controller.monitor;

import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.system.domain.SysLogininfor;
import cn.silver.framework.system.service.ISysLogininforService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 *
 * @author hb
 */
@RestController
@Api(tags = {"【系统访问记录】"})
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {
    @Autowired
    private ISysLogininforService logininforService;

    @GetMapping("/list")
    @ApiOperation("查询登录日志信息列表")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
    })
    public ResponsePageInfo<SysLogininfor> list(@ModelAttribute SysLogininfor logininfor) {
        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return toResponsePageInfo(list);
    }

    @PostMapping("/exportData")
    @ApiOperation("导出登录日志Excel")
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    public void export(HttpServletResponse response, @ModelAttribute SysLogininfor logininfor) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(list, "登录日志", response);
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    @ApiOperation("删除登录日志")
    public Response<Integer> remove(@ApiParam(name = "infoIds", value = "登录日志ids{逗号分隔}", required = true) @PathVariable String[] infoIds) {
        return toResponse(logininforService.deleteLogininforByIds(infoIds));
    }

    @DeleteMapping("/clean")
    @ApiOperation("清除登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    public Response clean() {
        logininforService.cleanLogininfor();
        return Response.success();
    }
}
