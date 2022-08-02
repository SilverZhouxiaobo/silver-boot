package cn.silver.framework.system.controller;

import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.domain.SysJob;
import cn.silver.framework.system.exception.TaskException;
import cn.silver.framework.system.service.ISysJobService;
import cn.silver.framework.system.util.CronUtils;
import io.swagger.annotations.*;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度任务信息操作处理
 *
 * @author hb
 */
@RestController
@RequestMapping("/monitor/job")
@Api(tags = {"【调度任务信息操作处理】"})
public class SysJobController extends BaseController {
    @Autowired
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @GetMapping("/list")
    @ApiOperation("查询定时任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
    })
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    public ResponsePageInfo<SysJob> list(@ModelAttribute SysJob sysJob) {
        startPage();
        List<SysJob> list = jobService.selectJobList(sysJob);
        return toResponsePageInfo(list);
    }

    /**
     * 导出定时任务列表
     */
    @PostMapping("/exportData")
    @ApiOperation("导出定时任务列表Excel")
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    public Response<String> export(@ModelAttribute SysJob sysJob) {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        return util.exportExcel(list, "定时任务");
    }

    /**
     * 获取定时任务详细信息
     *
     * @return
     */
    @GetMapping(value = "/{jobId}")
    @ApiOperation("获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    public Response<BaseEntity> getInfo(@ApiParam(name = "jobId", value = "定时任务id", required = true) @PathVariable("jobId") String jobId) {
        return Response.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @PostMapping
    @ApiOperation("新增定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    public Response<Integer> add(@RequestBody SysJob sysJob) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(sysJob.getCronExpression())) {
            return Response.error(ResponseEnum.CRON_ERROR);
        }
        sysJob.setCreateBy(SecurityUtils.getUsername());
        return toResponse(jobService.insertJob(sysJob));
    }

    /**
     * 修改定时任务
     */
    @PutMapping
    @ApiOperation("修改定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    public Response<Integer> edit(@RequestBody SysJob sysJob) throws SchedulerException, TaskException {
        if (!CronUtils.isValid(sysJob.getCronExpression())) {
            return Response.error(ResponseEnum.CRON_ERROR);
        }
        sysJob.setUpdateBy(SecurityUtils.getUsername());
        return toResponse(jobService.updateJob(sysJob));
    }

    /**
     * 定时任务状态修改
     */
    @PutMapping("/changeStatus")
    @ApiOperation("定时任务状态修改")
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    public Response<Integer> changeStatus(@RequestBody SysJob job) throws SchedulerException {
        SysJob newJob = jobService.selectJobById(job.getId());
        newJob.setStatus(job.getStatus());
        return toResponse(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @PutMapping("/run")
    @ApiOperation("定时任务立即执行一次")
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    public Response run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return Response.success();
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/{jobIds}")
    @ApiOperation("删除定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    public Response remove(@ApiParam(name = "jobIds", value = "定时任务ids{逗号分隔}", required = true) @PathVariable String[] jobIds) throws SchedulerException, TaskException {
        jobService.deleteJobByIds(jobIds);
        return Response.success();
    }
}
