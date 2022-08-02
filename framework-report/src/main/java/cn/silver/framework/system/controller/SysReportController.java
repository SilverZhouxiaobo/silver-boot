package cn.silver.framework.system.controller;

import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.system.domain.SysReport;
import cn.silver.framework.system.model.BaseTempExportDTO;
import cn.silver.framework.system.service.ISysReportService;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * ureport2模板管理Controller
 *
 * @author ruoyi
 * @date 2022-01-21
 */
@Api(value = "ureport2模板管理", tags = "ureport2模板管理")
@ApiSupport(author = "hb", order = 10)
@RestController
@RequestMapping("/sys/report")
public class SysReportController extends DataController<ISysReportService, SysReport> {
    @Autowired
    Environment environment;

    /**
     * 跳转到添加模板页面
     *
     * @return
     */
    @ApiOperation(value = "返回添加模板页面路径", notes = "返回添加模板页面路径")
    @ApiOperationSupport(order = 80)
    @GetMapping("/addTemp")
    @PreAuthorize("@ss.hasPermi('sys:report:add')")
    public String addTemp() {
        return environment.getProperty("app.ureporturl") + "/ureport/designer";
    }

    /**
     * 跳转到修改模板页面
     *
     * @return
     */
    @ApiOperation(value = "返回修改模板页面路径", notes = "返回修改模板页面路径")
    @ApiOperationSupport(order = 90)
    @GetMapping("/editTemp/{id}")
    @PreAuthorize("@ss.hasPermi('sys:report:edit')")
    public String editTemp(@PathVariable String id) {
        SysReport report = baseService.selectById(id);
        if (report == null) {
            throw new SecurityException("当前模板不存在");
        }
        return environment.getProperty("app.ureporturl") + "/ureport/designer" + "?_u=" + report.getPrefix() + report.getTempFileName();
    }

    /**
     * 跳转到预览模板页面
     *
     * @return
     */
    @ApiOperationSupport(order = 90)
    @GetMapping("/previwTemp/{id}")
    @PreAuthorize("@ss.hasPermi('sys:report:list')")
    @ApiOperation(value = "返回预览模板页面路径", notes = "返回预览模板页面路径")
    public String previwTemp(@PathVariable String id) {
        SysReport report = baseService.selectById(id);
        if (report == null) {
            throw new SecurityException("当前模板不存在");
        }
        return environment.getProperty("app.ureporturl") + "/ureport/preview?_u=" + report.getPrefix() + report.getTempFileName();
    }

    /**
     * 实际业务--跳转到预览模板页面
     *
     * @return
     */
    @ApiOperation(value = "实际业务--跳转到预览模板页面", notes = "实际业务--跳转到预览模板页面")
    @ApiOperationSupport(order = 90)
    @GetMapping("/businessPreviwTemp")
    public String businessPreviwTemp(@Valid BaseTempExportDTO baseTempExportDTO) {
        SysReport report = baseService.selectParamByCode(baseTempExportDTO.getCode());
        if (report == null) {
            throw new SecurityException("当前模板不存在");
        }
        String extraStr = "";
        if (StringUtils.isNotBlank(baseTempExportDTO.getOrderNo())) {
            extraStr = extraStr + "&orderNo=" + baseTempExportDTO.getOrderNo();
        }
        if (baseTempExportDTO.getExtraParams() != null) {
            String extraParams = baseTempExportDTO.getExtraParams().stream().collect(Collectors.joining(","));
            extraStr = extraStr + "&extraParams=" + extraParams;
        }
        return environment.getProperty("app.ureporturl") + "/ureport/pdf/show?_u=" + report.getPrefix() + report.getTempFileName() + extraStr;
    }

    /**
     * 实际业务--导出excel
     *
     * @return
     */
    @ApiOperation(value = "实际业务--导出excel", notes = "实际业务--导出excel")
    @ApiOperationSupport(order = 100)
    @GetMapping("/businessExportExcel")
    public String businessExportExcel(@Valid BaseTempExportDTO baseTempExportDTO) {
        SysReport report = baseService.selectParamByCode(baseTempExportDTO.getCode());
        if (report == null) {
            throw new SecurityException("当前模板不存在");
        }
        String extraStr = "";
        if (StringUtils.isNotBlank(baseTempExportDTO.getOrderNo())) {
            extraStr = extraStr + "&orderNo=" + baseTempExportDTO.getOrderNo();
        }
        if (baseTempExportDTO.getBeginTime() != null) {
            extraStr = extraStr + "&beginTime=" + DateFormatUtils.format(baseTempExportDTO.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
        }
        if (baseTempExportDTO.getEndTime() != null) {
            extraStr = extraStr + "&endTime=" + DateFormatUtils.format(baseTempExportDTO.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
        }
        if (baseTempExportDTO.getExtraParams() != null) {
            String extraParams = baseTempExportDTO.getExtraParams().stream().collect(Collectors.joining(","));
            extraStr = extraStr + "&extraParams=" + extraParams;
        }
        return environment.getProperty("app.ureporturl") + "/ureport/excel?_u="
                + report.getPrefix() + report.getTempFileName()
                + extraStr;
    }
}
