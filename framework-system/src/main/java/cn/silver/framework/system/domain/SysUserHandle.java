package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.security.util.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 个人收藏信息对象 sys_user_collect
 *
 * @author hb
 * @date 2022-07-06
 */

@Data
@NoArgsConstructor
@Table(name = "sys_user_handle")
@ApiModel(value = "SysUserHandle", description = "SysUserHandle对象")
public class SysUserHandle extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /** 用户主键 */
    @Excel(name = "用户主键", sort = 2)
    @Column(name = "user_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "用户主键")
    private String userId;

    /** 业务类型 */
    @Excel(name = "业务类型", sort = 3)
    @NotBlank(message = "业务类型不能为空")
    @Column(name = "business_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /** 业务主键 */
    @Excel(name = "业务主键", sort = 4)
    @NotBlank(message = "业务主键不能为空")
    @Column(name = "business_key", searchType = SearchType.EQ)
    @ApiModelProperty(value = "业务主键")
    private String businessKey;

    @ApiModelProperty(value = "操作类型")
    @Excel(name = "操作类型", sort = 5, width = 30)
    @Column(name = "handle_type", searchType = SearchType.EQ)
    private String handleType;
    /** 收藏时间 */
    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", sort = 5, width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "handle_time", searchType = SearchType.EQ)
    private Date handleTime;

    @ApiModelProperty(value = "描述信息")
    @Excel(name = "描述信息", type = Excel.Type.ALL)
    private String remark;

    public SysUserHandle(String handleType, String businessType, String businessKey) {
        this.handleType = handleType;
        this.businessType = businessType;
        this.businessKey = businessKey;
    }

    public void preInsert() {
        super.preInsert();
        this.handleTime = new Date();
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
                .append("businessType", getBusinessType())
                .append("businessKey", getBusinessKey())
                .append("handleType", getHandleType())
                .append("handleTime", getHandleTime())
                .append("remark", getRemark())
                .toString();
    }
}
