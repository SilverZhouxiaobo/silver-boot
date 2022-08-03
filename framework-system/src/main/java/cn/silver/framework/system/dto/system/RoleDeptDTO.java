package cn.silver.framework.system.dto.system;

import cn.silver.framework.core.model.TreeSelect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 【角色部门列表】DTO
 *
 * @author JuniorRay
 * @date 2020-11-14
 */
@Data
@ApiModel(value = "RoleDeptDTO", description = "角色部门列表DTO")
public class RoleDeptDTO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "选中菜单列表")
    private List<String> checkedKeys;

    @ApiModelProperty(value = "下拉树结构列表")
    private List<TreeSelect> depts;

}
