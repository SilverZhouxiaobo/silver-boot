package cn.silver.framework.core.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录对象（免验证码）
 *
 * @author hb
 */
@Data
@ApiModel(value = "LoginBodyNoCode", description = "用户登录对象（免验证码）")
public class LoginBodyNoCode implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    private String password;


}
