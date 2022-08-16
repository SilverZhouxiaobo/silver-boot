package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.bean.LoginLog;
import cn.silver.framework.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author hb
 */
@Data
@ApiModel(value = "SysLogininfor", description = "系统访问记录表")
public class SysLogininfor extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    @ApiModelProperty(value = "登录状态 0成功 1失败")
    private String status;

    /**
     * 登录IP地址
     */
    @Excel(name = "登录地址")
    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @Excel(name = "登录地点")
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Excel(name = "浏览器")
    @ApiModelProperty(value = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统")
    @ApiModelProperty(value = "操作系统")
    private String os;

    /**
     * 提示消息
     */
    @Excel(name = "提示消息")
    @ApiModelProperty(value = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;

    public SysLogininfor() {
    }

    public SysLogininfor(LoginLog loginLog) {
        BeanUtils.copyProperties(loginLog, this);
    }
}
