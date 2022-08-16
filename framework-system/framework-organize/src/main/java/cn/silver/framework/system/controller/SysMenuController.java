package cn.silver.framework.system.controller;

import cn.silver.framework.common.constant.Constants;
import cn.silver.framework.common.utils.ServletUtils;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.model.TreeSelect;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.security.service.TokenService;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.constant.UserConstants;
import cn.silver.framework.system.domain.SysMenu;
import cn.silver.framework.system.dto.system.MenuDTO;
import cn.silver.framework.system.service.ISysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 *
 * @author hb
 */
@RestController
@Api(tags = {"【菜单信息】"})
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    @ApiOperation("获取菜单列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    public Response<List<SysMenu>> list(@ModelAttribute SysMenu menu) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        List<SysMenu> menus = menuService.selectMenuList(menu, loginUser);
        return Response.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    @ApiOperation("根据菜单编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public Response<SysMenu> getInfo(@ApiParam(name = "menuId", value = "菜单编号", required = true) @PathVariable("menuId") String menuId) {
        return Response.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    @ApiOperation("获取菜单下拉树列表")
    public Response<List<TreeSelect>> treeselect(@ModelAttribute SysMenu menu) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        List<SysMenu> menus = menuService.selectMenuList(menu, loginUser);
        return Response.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    @ApiOperation("加载对应角色菜单列表树")
    public Response<MenuDTO> roleMenuTreeselect(@ApiParam(name = "roleId", value = "角色ID", required = true) @PathVariable("roleId") String roleId) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        List<SysMenu> menus = menuService.selectMenuList(loginUser);
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setCheckedKeys(menuService.selectMenuListByRoleId(roleId));
        menuDTO.setMenus(menuService.buildMenuTreeSelect(menus));
        return Response.success(menuDTO);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @ApiOperation("新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    public Response<Integer> add(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return Response.error(ResponseEnum.MENU_ADD_ERROR_EXIST);
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            return Response.error(ResponseEnum.MENU_ADD_ERROR_HTTP);
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toResponse(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @ApiOperation("修改菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    public Response<Integer> edit(@Validated @RequestBody SysMenu menu) {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))) {
            return Response.error(ResponseEnum.MENU_UPDATE_ERROR_EXIST);
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)) {
            return Response.error(ResponseEnum.MENU_UPDATE_ERROR_HTTP);
        } else if (menu.getId().equals(menu.getPid())) {
            return Response.error(ResponseEnum.MENU_UPDATE_ERROR_SELF);
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toResponse(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @ApiOperation("删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    public Response<Integer> remove(@ApiParam(name = "menuId", value = "菜单ID", required = true) @PathVariable("menuId") String menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return Response.error(ResponseEnum.MENU_DELETE_ERROR_SUB);
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return Response.error(ResponseEnum.MENU_DELETE_ERROR_ROLE);
        }
        return toResponse(menuService.deleteMenuById(menuId));
    }
}
