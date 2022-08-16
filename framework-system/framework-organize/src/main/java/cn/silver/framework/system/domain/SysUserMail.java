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
 * 用户邮箱配置对象 sys_user_mail
 *
 * @author hb
 * @date 2022-07-06
 */

@Data
@Table(name = "sys_user_mail")
@ApiModel(value = "SysUserMail", description = "SysUserMail对象")
public class SysUserMail extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /** 用户信息 */
    @Excel(name = "用户信息", sort = 2)
    @Column(name = "user_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "用户信息")
    private String userId;

    /** 邮箱地址 */
    @Excel(name = "邮箱地址", sort = 3)
    @Column(name = "mail_addr", searchType = SearchType.EQ)
    @ApiModelProperty(value = "邮箱地址")
    private String mailAddr;

    /** smtp服务器地址 */
    @Excel(name = "smtp服务器地址", sort = 4)
    @Column(name = "smtp_addr", searchType = SearchType.EQ)
    @ApiModelProperty(value = "smtp服务器地址")
    private String smtpAddr;

    /** smtp登录用户名 */
    @Excel(name = "smtp登录用户名", sort = 5)
    @Column(name = "smtp_user", searchType = SearchType.EQ)
    @ApiModelProperty(value = "smtp登录用户名")
    private String smtpUser;

    /** smtp登录密码 */
    @Excel(name = "smtp登录密码", sort = 6)
    @Column(name = "smtp_pass", searchType = SearchType.EQ)
    @ApiModelProperty(value = "smtp登录密码")
    private String smtpPass;

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
                .append("mailAddr", getMailAddr())
                .append("smtpAddr", getSmtpAddr())
                .append("smtpUser", getSmtpUser())
                .append("smtpPass", getSmtpPass())
                .toString();
    }
}
