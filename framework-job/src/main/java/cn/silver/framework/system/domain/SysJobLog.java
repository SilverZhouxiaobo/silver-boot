package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 定时任务调度日志表 sys_job_log
 *
 * @author hb
 */
@Data
@ApiModel(value = "SysJobLog", description = "定时任务调度日志表")
public class SysJobLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    @ApiModelProperty(value = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @Excel(name = "任务组名")
    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @Excel(name = "调用目标字符串")
    @ApiModelProperty(value = "调用目标字符串")
    private String invokeTarget;

    /**
     * 日志信息
     */
    @Excel(name = "日志信息")
    @ApiModelProperty(value = "日志信息")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @Excel(name = "执行状态", readConverterExp = "0=正常,1=失败")
    @ApiModelProperty(value = "执行状态（0正常 1失败）")
    private String status;

    /**
     * 异常信息
     */
    @Excel(name = "异常信息")
    @ApiModelProperty(value = "异常信息")
    private String exceptionInfo;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 停止时间
     */
    @ApiModelProperty(value = "停止时间")
    private Date stopTime;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("jobLogId", getId())
                .append("jobName", getJobName())
                .append("jobGroup", getJobGroup())
                .append("jobMessage", getJobMessage())
                .append("status", getStatus())
                .append("exceptionInfo", getExceptionInfo())
                .append("startTime", getStartTime())
                .append("stopTime", getStopTime())
                .toString();
    }
}
