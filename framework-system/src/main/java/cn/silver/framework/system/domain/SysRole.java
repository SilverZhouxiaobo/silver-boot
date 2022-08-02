package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 角色表 sys_role
 *
 * @author hb
 */
@Data
@Table(name = "sys_role")
@ApiModel(value = "SysRole", description = "角色表")
public class SysRole extends DataEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @Excel(name = "角色名称")
    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    @Column(name = "role_name", searchType = SearchType.LIKE)
    private String roleName;

    /**
     * 角色权限
     */
    @Excel(name = "角色权限")
    @ApiModelProperty(value = "角色权限")
    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    @Column(name = "role_key", searchType = SearchType.LIKE)
    private String roleKey;

    /**
     * 角色排序
     */
    @Excel(name = "角色排序")
    @ApiModelProperty(value = "角色排序")
    @NotBlank(message = "显示顺序不能为空")
    private String roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限）
     */
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限")
    @ApiModelProperty(value = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限）")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    @ApiModelProperty(value = "菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）")
    private boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）
     */
    @ApiModelProperty(value = "部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）")
    private boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
    @ApiModelProperty(value = "角色状态（0正常 1停用）")
    private String status;

    @javax.persistence.Column(name = "update_time")
    @Excel(name = "最后更新时间", type = Excel.Type.EXPORT, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    @ApiModelProperty(value = "用户是否存在此角色标识 默认不存在")
    private boolean flag = false;

    /**
     * 菜单组
     */
    @ApiModelProperty(value = "菜单组")
    private transient String[] menuIds;

    /**
     * 部门组（数据权限）
     */
    @ApiModelProperty(value = "部门组（数据权限）")
    private transient String[] deptIds;

    public SysRole() {
    }

    public SysRole(String roleId) {
        this.setId(roleId);
    }

    public static boolean isAdmin(String roleKey) {
        return StringUtils.isNotBlank(roleKey) && "admin".equals(roleKey);
    }

    public boolean isAdmin() {
        return isAdmin(this.getRoleKey());
    }

    public boolean checkExists() {
        return true;
    }

    public String getLabel() {
        return this.roleName;
    }

    public String getValue() {
        return this.roleKey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("roleId", getId())
                .append("roleName", getRoleName())
                .append("roleKey", getRoleKey())
                .append("roleSort", getRoleSort())
                .append("dataScope", getDataScope())
                .append("menuCheckStrictly", isMenuCheckStrictly())
                .append("deptCheckStrictly", isDeptCheckStrictly())
                .append("status", getStatus())
                .append("deleted", getDeleted())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
