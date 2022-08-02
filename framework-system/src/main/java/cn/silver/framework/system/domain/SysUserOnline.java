package cn.silver.framework.system.domain;

import cn.silver.framework.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 当前在线会话
 *
 * @author hb
 */
@Data
@ApiModel(value = "SysUserOnline", description = "当前在线会话")
public class SysUserOnline extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 会话编号
     */
    @ApiModelProperty(value = "会话编号")
    private String tokenId;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 登录IP地址
     */
    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    /**
     * 登录地址
     */
    @ApiModelProperty(value = "登录地址")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    /**
     * 操作系统
     */
    @ApiModelProperty(value = "操作系统")
    private String os;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "登录时间")
    private Date loginTime;


}
