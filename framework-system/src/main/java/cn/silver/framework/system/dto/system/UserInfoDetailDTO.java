package cn.silver.framework.system.dto.system;

import cn.silver.framework.system.domain.SysPost;
import cn.silver.framework.system.domain.SysRole;
import cn.silver.framework.system.domain.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 【用户编号获取详细信息】DTO
 *
 * @author JuniorRay
 * @date 2020-11-11
 */
@Data
@ApiModel(value = "UserInfoDetailDTO", description = "用户编号获取详细信息DTO")
public class UserInfoDetailDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色列表")
    private List<SysRole> roles;

    @ApiModelProperty(value = "岗位列表")
    private List<SysPost> posts;

    @ApiModelProperty(value = "用户对象信息(userId不为空才会有值)")
    private SysUser user;

    @ApiModelProperty(value = "选中岗位ID列表(userId不为空才会有值)")
    private List<String> postIds;

    @ApiModelProperty(value = "选中角色ID列表(userId不为空才会有值)")
    private List<String> roleIds;

}
