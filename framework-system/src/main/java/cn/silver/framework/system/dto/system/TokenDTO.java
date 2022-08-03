package cn.silver.framework.system.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 【Token】DTO
 *
 * @author JuniorRay
 * @date 2020-11-14
 */
@Data
@ApiModel(value = "TokenDTO", description = "TokenDTO")
public class TokenDTO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "令牌")
    private String token;
}
