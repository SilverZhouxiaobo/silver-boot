package cn.silver.framework.core.bean;

import cn.silver.framework.core.model.DeptModel;
import cn.silver.framework.core.model.RoleModel;
import cn.silver.framework.security.util.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户身份权限
 *
 * @author hb
 */
@Data
@NoArgsConstructor
@ApiModel(value = "LoginUser", description = "登录用户身份权限")
public class LoginUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户主键")
    private String id;
    @ApiModelProperty(value = "登录帐号")
    private String userName;

    @ApiModelProperty(value = "用户名称")
    private String nickName;

    @JsonIgnore
    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty("工作座机")
    private String phone;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("微信号码")
    private String wechat;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty(value = "部门主键")
    private String deptId;

    @ApiModelProperty(value = "部门主键")
    private String deptName;
    /**
     * 用户唯一标识
     */
    @ApiModelProperty(value = "用户唯一标识")
    private String token;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    private Date loginTime;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    /**
     * 登录IP地址
     */
    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;
    /**
     * 登录地点
     */
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    /**
     * 操作系统
     */
    @ApiModelProperty(value = "操作系统")
    private String os;

    /**
     * 权限列表
     */
    @ApiModelProperty(value = "权限列表")
    private Set<String> permissions;
    @ApiModelProperty(value = "岗位信息主键")
    private Set<String> postIds;

    @ApiModelProperty(value = "角色列表")
    private Set<RoleModel> roles;

    private DeptModel dept;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     *
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     *
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     *
     * @return
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.userName);
    }

    public String getRoleIds() {
        return CollectionUtils.isNotEmpty(roles) ? this.roles.stream().map(RoleModel::getRoleKey).collect(Collectors.joining(",")) : "";
    }
//    //TODO
//    public String getPostIds() {
//        return this.deptId;
//    }

    //TODO
    public String getDeptPostIds() {
        return this.deptId;
    }

    /**
     * 从Http Request对象中获取令牌对象。
     *
     * @return 令牌对象。
     */
    public static LoginUser getInstance() {
        return SecurityUtils.getLoginUser();
    }
}
