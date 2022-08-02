package cn.silver.framework.system.controller;

import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.system.domain.SysJobLog;
import cn.silver.framework.system.service.ISysJobLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度日志操作处理
 *
 * @author hb
 */
@RestController
@Api(tags = {"【调度日志操作处理】"})
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController {
    @Autowired
    private ISysJobLogService jobLogService;

    /**
     * 查询定时任务调度日志列表
     */
    @GetMapping("/list")
    @ApiOperation("查询定时任务调度日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query"),
    })
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    public ResponsePageInfo<SysJobLog> list(@ModelAttribute SysJobLog sysJobLog) {
        startPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        return toResponsePageInfo(list);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/exportData")
    @ApiOperation("导出定时任务调度日志列表Excel")
    public Response<String> export(@ModelAttribute SysJobLog sysJobLog) {
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<>(SysJobLog.class);
        return util.exportExcel(list, "调度日志");
    }

    /**
     * 根据调度编号获取详细信息
     *
     * @return
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{configId}")
    @ApiOperation("根据调度编号获取详细信息")
    public Response<BaseEntity> getInfo(@ApiParam(name = "jobLogId", value = "调度编号ids", required = true) @PathVariable("jobLogId") String jobLogId) {
        return Response.success(jobLogService.selectJobLogById(jobLogId));
    }


    /**
     * 删除定时任务调度日志
     */
    @DeleteMapping("/{jobLogIds}")
    @ApiOperation("删除定时任务调度日志")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    public Response<Integer> remove(@ApiParam(name = "jobLogIds", value = "调度编号ids{逗号分隔}", required = true) @PathVariable String[] jobLogIds) {
        return toResponse(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    /**
     * 清空定时任务调度日志
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空定时任务调度日志")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    public Response clean() {
        jobLogService.cleanJobLog();
        return Response.success();
    }
}
