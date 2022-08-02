package cn.silver.framework.message.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AccessToken", description = "微信通用接口凭证 ")
public class AccessToken {
    @ApiModelProperty("获取到的凭证")
    private String token;
    @ApiModelProperty("凭证有效时间，单位：秒")
    private int expiresIn;
}
