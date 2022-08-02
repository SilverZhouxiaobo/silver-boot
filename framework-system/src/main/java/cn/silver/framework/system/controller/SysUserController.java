package cn.silver.framework.system.controller;

import cn.silver.framework.common.utils.ServletUtils;
import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.security.service.TokenService;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.constant.UserConstants;
import cn.silver.framework.system.domain.SysUser;
import cn.silver.framework.system.dto.system.UserInfoDetailDTO;
import cn.silver.framework.system.service.ISysConfigService;
import cn.silver.framework.system.service.ISysPostService;
import cn.silver.framework.system.service.ISysRoleService;
import cn.silver.framework.system.service.ISysUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 用户信息
 *
 * @author hb
 */
@Slf4j
@RestController
@Api(tags = {"【用户信息】"})
@RequestMapping("/system/user")
public class SysUserController extends DataController<ISysUserService, SysUser> {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 获取用户列表
     */
    @Override
    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
    })
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public ResponsePageInfo<SysUser> list(@ModelAttribute SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return toResponsePageInfo(list);
    }

    @Override
    @PostMapping("/exportData")
    @ApiOperation("导出用户列表Excel")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    public Response<Void> exportData(HttpServletResponse response, @ModelAttribute SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(list, "用户数据", response);
        return Response.success();
    }

    @Override
    @PostMapping("/importData")
    @ApiOperation("导入用户列表Excel")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    public Response<Void> importData(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "updateSupport") boolean updateSupport, @ModelAttribute SysUser entity) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return Response.success(message, null);
    }

//    @Override
//    @PostMapping("/importTemplate")
//    @ApiOperation("获取用户导入模板")
//    public Response<Void> importTemplate(HttpServletResponse response) {
//        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
//        util.importTemplate("用户导入模板", response);
//        return Response.success();
//    }

    /**
     * 根据用户编号获取详细信息
     */
//    @GetMapping(value = {"/", "/{userId}"})
//    @ApiOperation("根据用户编号获取详细信息")
//    @PreAuthorize("@ss.hasPermi('system:user:query')")
//    public Response<UserInfoDetailDTO> getInfos(@ApiParam(name = "userId", value = "用户编号", required = true) @PathVariable(value = "userId", required = false) String userId) {
//        UserInfoDetailDTO userInfoDetailDTO = new UserInfoDetailDTO();
//        SysUser user = null;
//        if (StringUtils.isNotBlank(userId)) {
//            user = userService.selectUserById(userId);
//            userInfoDetailDTO.setUser(user);
//            userInfoDetailDTO.setPostIds(postService.selectPostListByUserId(userId));
//            userInfoDetailDTO.setRoleIds(roleService.selectRoleListByUserId(userId));
//        }
//        List<SysRole> roles = roleService.selectRoleAll();
//        List<SysRole> rolesRet = ObjectUtils.isNotEmpty(user) && SysUser.isAdmin(user.getUserName()) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
//        userInfoDetailDTO.setRoles(rolesRet);
//        userInfoDetailDTO.setPosts(postService.selectPostAll());
//        return Response.success(userInfoDetailDTO);
//    }

    /**
     * 修改用户
     */
    @Override
    @PutMapping
    @ApiOperation("修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public Response<SysUser> edit(@Validated @RequestBody SysUser user) {
        LoginUser currentUser = getLoginUser();
        if (!currentUser.isAdmin()) {
            userService.checkUserAllowed(user);
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return Response.error(ResponseEnum.USER_UPDATE_ERROR_PHONE);
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return Response.error(ResponseEnum.USER_UPDATE_ERROR_MAIL);
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        userService.updateUser(user);
        return Response.success(user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public Response<Integer> remove(@ApiParam(name = "userIds", value = "用户编号ids{逗号分隔}", required = true) @PathVariable String[] userIds) {
        return toResponse(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd/{id}")
    @ApiOperation("重置密码")
    @PreAuthorize("@ss.hasRole('admin')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public Response<Void> resetPwd(@PathVariable String id) {
        String password = configService.selectConfigByKey("sys.user.initPassword");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(password)) {
            List<SysUser> users = this.userService.selectByIds(Arrays.asList(id.split(",")));
            users.stream().forEach(user -> user.setPassword(SecurityUtils.encryptPassword(password)));
            this.userService.updateBatch(users);
            return Response.success();
        } else {
            return Response.error("默认密码为空，请设置默认密码");
        }
    }

    /**
     * 修改密码
     *
     * @return
     */
    @PutMapping("/updatePwd/{id}/{password}/{newPassword}")
    @ApiOperation("修改密码")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public Response<Integer> updatePwd(@PathVariable String id, @PathVariable String password, @PathVariable String newPassword) {
        SysUser user = userService.selectUserById(id);
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) { //密码不匹配
            return Response.error(ResponseEnum.UPDATE_PASSWORD_ERROR_OLD_ERROR);
        }
        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        return toResponse(userService.updateUser(user));
    }


    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    @ApiOperation("状态修改")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public Response<Integer> changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toResponse(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public Response<UserInfoDetailDTO> authRole(@PathVariable("userId") String userId) {
        UserInfoDetailDTO userInfoDetailDTO = new UserInfoDetailDTO();
        userInfoDetailDTO.setUser(userService.selectUserById(userId));
        userInfoDetailDTO.setRoles(roleService.selectRolesByUserId(userId));
        return Response.success(userInfoDetailDTO);
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public Response insertAuthRole(String userId, String[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return Response.success();
    }
}
