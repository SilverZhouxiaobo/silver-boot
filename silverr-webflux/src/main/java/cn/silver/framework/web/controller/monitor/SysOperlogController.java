package cn.silver.framework.web.controller.monitor;

import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.system.domain.SysOperLog;
import cn.silver.framework.system.service.ISysOperLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author hb
 */
@RestController
@Api(tags = {"【操作日志记录】"})
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {
    @Autowired
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    @ApiOperation("查询操作日志记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
    })
    public ResponsePageInfo<SysOperLog> list(@ModelAttribute SysOperLog operLog) {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return toResponsePageInfo(list);
    }

    @PostMapping("/exportData")
    @ApiOperation("导出操作日志Excel")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    public void export(HttpServletResponse response, @ModelAttribute SysOperLog operLog) {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(list, "操作日志", response);
    }

    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    @ApiOperation("删除操作日志")
    public Response<Integer> remove(
            @ApiParam(name = "infoIds", value = "操作日志ids{逗号分隔}", required = true)
            @PathVariable String[] operIds
    ) {
        return toResponse(operLogService.deleteOperLogByIds(operIds));
    }

    @DeleteMapping("/clean")
    @ApiOperation("清除操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    public Response clean() {
        operLogService.cleanOperLog();
        return Response.success();
    }
}
