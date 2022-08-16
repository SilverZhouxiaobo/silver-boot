package cn.silver.framework.core.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "LoginLog", description = "用户访问记录-登录日志")
public class LoginLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("登录帐号")
    private String userName;
    @ApiModelProperty("IP地址")
    private String ipaddr;
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;
    @ApiModelProperty(value = "浏览器")
    private String browser;
    @ApiModelProperty(value = "操作系统")
    private String os;
    @ApiModelProperty(value = "登录状态")
    private String status;
    @ApiModelProperty(value = "提示消息")
    private String msg;
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;
}
