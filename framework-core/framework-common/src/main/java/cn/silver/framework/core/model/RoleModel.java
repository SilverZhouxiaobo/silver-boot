package cn.silver.framework.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "RoleModel", description = "角色数据对象")
public class RoleModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("角色主键")
    private String id;
    @ApiModelProperty("角色名称")
    private String roleName;
    @ApiModelProperty("角色编码")
    private String roleKey;
    @ApiModelProperty("数据权限")
    private String dataScope;
    @ApiModelProperty("角色描述")
    private String remark;
}
