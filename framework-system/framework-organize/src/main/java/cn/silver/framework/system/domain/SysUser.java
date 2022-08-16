package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.common.annotation.Excels;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author hb
 */
@Data
@NoArgsConstructor
@ApiModel(value = "SysUser", description = "用户对象")
public class SysUser extends DataEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 部门ID
     */
    @Excel(name = "部门编号", type = Excel.Type.IMPORT)
    @ApiModelProperty(value = "部门编号")
    private String deptId;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    @Column(name = "user_name", unique = true, searchType = SearchType.LIKE, dictable = true)
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
    @ApiModelProperty(value = "用户名称")
    @Size(max = 30, message = "用户名称长度不能超过30个字符")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Excel(name = "用户邮箱")
    @ApiModelProperty(value = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @Column(name = "email", unique = true)
    private String email;

    @Excel(name = "工作座机")
    @ApiModelProperty(value = "工作座机")
    @Size(max = 11, message = "电话号码长度不能超过11个字符")
    private String phone;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码")
    @ApiModelProperty(value = "手机号码")
    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    @Column(name = "mobile", unique = true)
    private String mobile;

    @Excel(name = "微信账号")
    @ApiModelProperty(value = "微信账号")
    private String wechat;

    /**
     * 用户性别
     */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    @ApiModelProperty(value = "用户性别")
    private String sex;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 盐加密
     */
    @ApiModelProperty(value = "盐加密")
    private transient String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    @ApiModelProperty(value = "帐号状态（0正常 1停用） ")
    private String status;

    /**
     * 最后登录IP
     */
    @Excel(name = "最后登录IP", type = Excel.Type.EXPORT)
    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @ApiModelProperty(value = "最后登录时间")
    private Date loginDate;

    /**
     * 部门对象
     */
    @Excels({
            @Excel(name = "部门名称", targetAttr = "deptName", type = Excel.Type.EXPORT),
            @Excel(name = "部门负责人", targetAttr = "leader", type = Excel.Type.EXPORT)
    })
    @ApiModelProperty(value = "部门对象")
    private SysDept dept;

    /**
     * 角色对象
     */
    @ApiModelProperty(value = "角色对象")
    private List<SysRole> roles;

    @ApiModelProperty(value = "岗位列表")
    private List<SysPost> posts;
    /**
     * 角色组
     */
    @ApiModelProperty(value = "角色组")
    private String[] roleIds;

    /**
     * 岗位组
     */
    @ApiModelProperty(value = "岗位组")
    private String[] postIds;

    /**
     * 角色ID
     */
    private transient String roleId;

    public SysUser(String userId) {
        this.setId(userId);
    }

    public LoginUser getLoginUser() {
        LoginUser user = new LoginUser();
        BeanUtils.copyProperties(this, user);
        return user;
    }

    public static boolean isAdmin(String userName) {
        return StringUtils.isNotBlank(userName) && "admin".equals(userName);
    }

    @Override
    public boolean checkExists() {
        return true;
    }

    public boolean isAdmin() {
        return isAdmin(this.getUserName());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getId())
                .append("deptId", getDeptId())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("email", getEmail())
                .append("mobile", getMobile())
                .append("sex", getSex())
                .append("avatar", getAvatar())
                .append("password", getPassword())
                .append("salt", getSalt())
                .append("status", getStatus())
                .append("loginIp", getLoginIp())
                .append("loginDate", getLoginDate())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("dept", getDept())
                .toString();
    }
}
