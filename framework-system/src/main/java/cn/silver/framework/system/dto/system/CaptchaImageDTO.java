package cn.silver.framework.system.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 【验证码】DTO
 *
 * @author JuniorRay
 * @date 2020-11-14
 */
@Data
@ApiModel(value = "GenTableInfoDTO", description = "验证码DTO")
public class CaptchaImageDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "验证码信息uuid")
    private String uuid;

    @ApiModelProperty(value = "Base64图片")
    private String img;

    @ApiModelProperty(value = "验证码开关")
    private Boolean captchaOnOff;
}
