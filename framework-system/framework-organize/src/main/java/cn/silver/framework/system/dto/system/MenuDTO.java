package cn.silver.framework.system.dto.system;

import cn.silver.framework.core.model.TreeSelect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 【角色菜单】DTO
 *
 * @author JuniorRay
 * @date 2020-11-14
 */
@Data
@ApiModel(value = "MenuDTO", description = "角色菜单DTO")
public class MenuDTO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "选中菜单列表")
    private List<String> checkedKeys;

    @ApiModelProperty(value = "下拉树结构列表")
    private List<TreeSelect> menus;

}
