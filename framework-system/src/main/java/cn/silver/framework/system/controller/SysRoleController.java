package cn.silver.framework.system.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.domain.SysRole;
import cn.silver.framework.system.domain.SysUser;
import cn.silver.framework.system.domain.SysUserRole;
import cn.silver.framework.system.service.ISysRoleService;
import cn.silver.framework.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author hb
 */
@RestController
@Api(tags = {"角色管理"})
@RequestMapping("/system/role")
public class SysRoleController extends DataController<ISysRoleService, SysRole> {

    public SysRoleController() {
        this.authorize = "system:role";
        this.title = "角色管理";
    }

    @Autowired
    private ISysUserService userService;

//    @GetMapping("/list")
//    @ApiOperation("查询角色信息列表")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
//            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
//    })
//    @PreAuthorize("@ss.hasPermi('system:role:list')")
//    public ResponsePageInfo<SysRole> list(@ModelAttribute SysRole role) {
//        startPage();
//        List<SysRole> list = roleService.selectRoleList(role);
//        return toResponsePageInfo(list);
//    }
//
//    @PostMapping("/exportData")
//    @ApiOperation("导出角色信息列表Excel")
//    @PreAuthorize("@ss.hasPermi('system:role:export')")
//    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
//    public void export(HttpServletResponse response, @ModelAttribute SysRole role) {
//        List<SysRole> list = roleService.selectRoleList(role);
//        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
//        util.exportExcel(list, "角色数据", response);
//    }
//
//    /**
//     * 根据角色编号获取详细信息
//     */
//    @GetMapping(value = "/{roleId}")
//    @ApiOperation("根据角色编号获取详细信息")
//    @PreAuthorize("@ss.hasPermi('system:role:query')")
//    public Response<SysRole> getInfo(@ApiParam(name = "roleId", value = "角色编号", required = true) @PathVariable("roleId") String roleId) {
//        return Response.success(roleService.selectRoleById(roleId));
//    }

    /**
     * 新增角色
     */
//    @PostMapping
//    @ApiOperation("新增角色")
//    @PreAuthorize("@ss.hasPermi('system:role:add')")
//    @Log(title = "角色管理", businessType = BusinessType.INSERT)
//    public Response<Integer> add(@Validated @RequestBody SysRole role) {
//        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
//            return Response.error(ResponseEnum.ROLE_ADD_ERROR_EXIST_NAME);
//        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
//            return Response.error(ResponseEnum.ROLE_ADD_ERROR_EXIST_AUTHORITY);
//        }
//        role.setCreateBy(SecurityUtils.getUsername());
//        return toResponse(roleService.insertRole(role));
//    }
//
//    /**
//     * 修改保存角色
//     */
//    @PutMapping
//    @ApiOperation("修改保存角色")
//    @PreAuthorize("@ss.hasPermi('system:role:edit')")
//    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
//    public Response edit(@Validated @RequestBody SysRole role) {
//        roleService.checkRoleAllowed(role);
//        if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))) {
//            return Response.error(ResponseEnum.ROLE_UPDATE_ERROR_EXIST_NAME);
//        } else if (UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))) {
//            return Response.error(ResponseEnum.ROLE_UPDATE_ERROR_EXIST_AUTHORITY);
//        }
//        role.setUpdateBy(SecurityUtils.getUsername());
//
//        if (roleService.updateRole(role) > 0) {
//            // 更新缓存用户权限
//            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
//            if (StringUtils.isNotNull(loginUser) && !loginUser.isAdmin()) {
//                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getId(), loginUser.isAdmin()));
//                SysUser user = userService.selectUserByUserName(loginUser.getUsername());
//                BeanUtils.copyProperties(user, loginUser);
//                tokenService.setLoginUser(loginUser);
//            }
//            return Response.success();
//        }
//        return Response.error(ResponseEnum.ROLE_UPDATE_ERROR);
//    }

    /**
     * 修改保存数据权限
     */
    @PutMapping("/dataScope")
    @ApiOperation("修改保存数据权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public Response<Integer> dataScope(@RequestBody SysRole role) {
        baseService.checkRoleAllowed(role);
        return toResponse(baseService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public Response<Integer> changeStatus(@RequestBody SysRole role) {
        baseService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUsername());
        return toResponse(baseService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
//    @DeleteMapping("/{roleIds}")
//    @ApiOperation("删除角色")
//    @PreAuthorize("@ss.hasPermi('system:role:remove')")
//    @Log(title = "角色管理", businessType = BusinessType.DELETE)
//    public Response<Integer> remove(@ApiParam(name = "roleIds", value = "角色编号ids{逗号分隔}", required = true) @PathVariable String[] roleIds) {
//        return toResponse(baseService.deleteRoleByIds(roleIds));
//    }

    /**
     * 获取角色选择框列表
     */
    @GetMapping("/optionselect")
    @ApiOperation("获取角色选择框列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    public Response<List<SysRole>> optionselect() {
        return Response.success(baseService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @GetMapping("/authUser/allocatedList")
    @ApiOperation("查询已分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public ResponsePageInfo<SysUser> allocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return toResponsePageInfo(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @GetMapping("/authUser/unallocatedList")
    @ApiOperation("查询未分配用户角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public ResponsePageInfo<SysUser> unallocatedList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return toResponsePageInfo(list);
    }

    /**
     * 取消授权用户
     */
    @PutMapping("/authUser/cancel")
    @ApiOperation("取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    public Response<Integer> cancelAuthUser(@RequestBody SysUserRole userRole) {
        return Response.success(baseService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @PutMapping("/authUser/cancelAll")
    @ApiOperation("批量取消授权用户")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    public Response<Integer> cancelAuthUserAll(String roleId, String[] userIds) {
        return Response.success(baseService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @PutMapping("/authUser/selectAll")
    @ApiOperation("批量选择用户授权")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    public Response<Integer> selectAuthUserAll(String roleId, String[] userIds) {
        baseService.checkRoleDataScope(roleId);
        return Response.success(baseService.insertAuthUsers(roleId, userIds));
    }
}
