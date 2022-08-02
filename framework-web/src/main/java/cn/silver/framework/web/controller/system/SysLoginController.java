package cn.silver.framework.web.controller.system;

import cn.silver.framework.common.utils.ServletUtils;
import cn.silver.framework.core.bean.LoginBody;
import cn.silver.framework.core.bean.LoginBodyNoCode;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.security.service.TokenService;
import cn.silver.framework.system.common.service.SysLoginService;
import cn.silver.framework.system.common.service.SysPermissionService;
import cn.silver.framework.system.domain.SysMenu;
import cn.silver.framework.system.domain.SysUser;
import cn.silver.framework.system.dto.system.TokenDTO;
import cn.silver.framework.system.dto.system.UserInfoDTO;
import cn.silver.framework.system.service.ISysMenuService;
import cn.silver.framework.system.service.ISysUserService;
import cn.silver.framework.system.vo.RouterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author hb
 */
@RestController
@Api(tags = {"【登录验证】"})
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;
    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法 （含验证码）
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ApiOperation("登录方法")
    public Response<TokenDTO> login(@RequestBody LoginBody loginBody) {
        TokenDTO tokenDTO = new TokenDTO();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        tokenDTO.setToken(token);
        return Response.success(tokenDTO);
    }

    /**
     * 登录方法 （不含验证码）
     *
     * @param loginBodyNoCode 登录信息
     * @return 结果
     */
    @PostMapping("/login/noCode")
    @ApiOperation("登录方法（不含验证码）")
    public Response<TokenDTO> loginNoCode(@RequestBody LoginBodyNoCode loginBodyNoCode) {
        TokenDTO tokenDTO = new TokenDTO();
        // 生成令牌
        String token = loginService.login(loginBodyNoCode.getUsername(), loginBodyNoCode.getPassword());
        tokenDTO.setToken(token);
        return Response.success(tokenDTO);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    @ApiOperation("获取用户信息")
    public Response<UserInfoDTO> getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = this.userService.selectUserById(loginUser.getId());
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUser(user);
        userInfoDTO.setRoles(roles);
        userInfoDTO.setPermissions(permissions);
        return Response.success(userInfoDTO);
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    @ApiOperation("获取路由信息")
    public Response<List<RouterVo>> getRouters() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(loginUser.getId());
        return Response.success(menuService.buildMenus(menus));
    }
}
