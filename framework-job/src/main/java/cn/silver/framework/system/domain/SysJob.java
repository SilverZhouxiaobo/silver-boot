package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.domain.DataEntity;
import cn.silver.framework.system.constant.ScheduleConstants;
import cn.silver.framework.system.util.CronUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 定时任务调度表 sys_job
 *
 * @author hb
 */
@Data
@ApiModel(value = "SysJob", description = "定时任务调度表")
public class SysJob extends DataEntity {
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
     * cron执行表达式
     */
    @Excel(name = "执行表达式 ")
    @ApiModelProperty(value = "cron执行表达式")
    private String cronExpression;

    /**
     * cron计划策略
     */
    @Excel(name = "计划策略 ", readConverterExp = "0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行")
    @ApiModelProperty(value = "cron计划策略")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Excel(name = "并发执行", readConverterExp = "0=允许,1=禁止")
    @ApiModelProperty(value = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @ApiModelProperty(value = "任务状态（0正常 1暂停）")
    @Excel(name = "任务状态", readConverterExp = "0=正常,1=暂停")
    private String status;

    @NotBlank(message = "任务名称不能为空")
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符")
    public String getJobName() {
        return jobName;
    }


    @NotBlank(message = "调用目标字符串不能为空")
    @Size(min = 0, max = 500, message = "调用目标字符串长度不能超过500个字符")
    public String getInvokeTarget() {
        return invokeTarget;
    }


    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符")
    public String getCronExpression() {
        return cronExpression;
    }


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getNextValidTime() {
        if (StringUtils.isNotEmpty(cronExpression)) {
            return CronUtils.getNextExecution(cronExpression);
        }
        return null;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("jobId", getId())
                .append("jobName", getJobName())
                .append("jobGroup", getJobGroup())
                .append("cronExpression", getCronExpression())
                .append("nextValidTime", getNextValidTime())
                .append("misfirePolicy", getMisfirePolicy())
                .append("concurrent", getConcurrent())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
