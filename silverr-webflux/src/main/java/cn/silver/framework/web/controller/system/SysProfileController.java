package cn.silver.framework.web.controller.system;

import cn.silver.framework.common.config.ApplicationConfig;
import cn.silver.framework.common.utils.ServletUtils;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.file.util.FileUploadUtils;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.security.service.TokenService;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.system.domain.SysUser;
import cn.silver.framework.system.dto.system.PersonInfoDTO;
import cn.silver.framework.system.dto.system.UpdatePwdDTO;
import cn.silver.framework.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人信息 业务处理
 *
 * @author hb
 */
@RestController
@Api(tags = {"【个人信息业务处理】"})
@RequestMapping("/sys/user/profile")
public class SysProfileController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping
    @ApiOperation("个人信息")
    public Response<PersonInfoDTO> profile() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = this.userService.selectUserById(loginUser.getId());
        PersonInfoDTO personInfoDTO = new PersonInfoDTO();
        personInfoDTO.setUser(user);
        personInfoDTO.setPostGroup(userService.selectUserPostGroup(loginUser.getUsername()));
        personInfoDTO.setRoleGroup(userService.selectUserRoleGroup(loginUser.getUsername()));
        return Response.success(personInfoDTO);
    }

    /**
     * 修改用户
     */
    @PutMapping
    @ApiOperation("修改用户")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    public Response updateProfile(@RequestBody SysUser user) {
        if (userService.updateUserProfile(user) > 0) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            // 更新缓存用户信息
            loginUser.setNickName(user.getNickName());
            loginUser.setMobile(user.getMobile());
            loginUser.setEmail(user.getEmail());
            loginUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return Response.success();
        }
        return Response.error(ResponseEnum.USER_UPDATE_ERROR);
    }

    /**
     * 重置密码
     */
    @PutMapping("/updatePwd")
    @ApiOperation("重置密码")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    public Response updatePwd(@RequestBody UpdatePwdDTO updatePwdDTO) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(updatePwdDTO.getOldPassword(), password)) {
            return Response.error(ResponseEnum.UPDATE_PASSWORD_ERROR_OLD_ERROR);
        }
        if (SecurityUtils.matchesPassword(updatePwdDTO.getNewPassword(), password)) {
            return Response.error(ResponseEnum.UPDATE_PASSWORD_ERROR_OLD_REPEAT);
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(updatePwdDTO.getNewPassword())) > 0) {
            // 更新缓存用户密码
            loginUser.setPassword(SecurityUtils.encryptPassword(updatePwdDTO.getNewPassword()));
            tokenService.setLoginUser(loginUser);
            return Response.success();
        }
        return Response.error(ResponseEnum.UPDATE_PASSWORD_ERROR);
    }

    /**
     * 头像上传
     */
    @ApiOperation("头像上传")
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public Response<String> avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            String avatar = FileUploadUtils.upload(ApplicationConfig.getAvatarPath(), file);
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar)) {
                Response response = Response.success(avatar);
                // 更新缓存用户头像
                loginUser.setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return response;
            }
        }
        return Response.error(ResponseEnum.ABNORMAL_PICTURE_UPLOAD);
    }
}
