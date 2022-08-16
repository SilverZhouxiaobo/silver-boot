package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.security.util.SecurityUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;

/**
 * 常用网址对象 sys_user_website
 *
 * @author hb
 * @date 2022-07-06
 */

@Data
@Table(name = "sys_user_website")
@ApiModel(value = "SysUserWebsite", description = "SysUserWebsite对象")
public class SysUserWebsite extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /** 用户信息 */
    @Excel(name = "用户信息", sort = 2)
    @Column(name = "user_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "用户信息")
    private String userId;

    /** 网址名称 */
    @Excel(name = "网址名称", sort = 3)
    @Column(name = "name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "网址名称")
    private String name;

    /** 访问地址 */
    @Excel(name = "访问地址", sort = 4)
    @Column(name = "addr", searchType = SearchType.EQ)
    @ApiModelProperty(value = "访问地址")
    private String addr;

    /** 排序 */
    @Excel(name = "排序", sort = 5)
    @Column(name = "sort", searchType = SearchType.EQ)
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /** 是否公用网址 */
    @Excel(name = "是否公用网址", sort = 6)
    @Column(name = "public_flag", searchType = SearchType.EQ)
    @ApiModelProperty(value = "是否公用网址")
    private Boolean publicFlag;

    public void preInsert() {
        super.preInsert();
        LoginUser user = SecurityUtils.getLoginUser();
        if (user != null) {
            this.userId = user.getId();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("name", getName())
                .append("addr", getAddr())
                .append("sort", getSort())
                .append("publicFlag", getPublicFlag())
                .toString();
    }
}
