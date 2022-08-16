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
 * 系统评论记录对象 sys_comment
 *
 * @author hb
 * @date 2022-07-06
 */

@Data
@NoArgsConstructor
@Table(name = "sys_comment")
@ApiModel(value = "SysComment", description = "SysComment对象")
public class SysComment extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /** 业务类型 */
    @Excel(name = "业务类型", sort = 2)
    @NotBlank(message = "业务类型不能为空")
    @Column(name = "business_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /** 业务主键 */
    @Excel(name = "业务主键", sort = 3)
    @NotBlank(message = "业务主键不能为空")
    @Column(name = "business_key", searchType = SearchType.EQ)
    @ApiModelProperty(value = "业务主键")
    private String businessKey;

    /** 操作人 */
    @Excel(name = "操作人", sort = 4)
    @Column(name = "operator", searchType = SearchType.EQ)
    @ApiModelProperty(value = "操作人")
    private String operator;

    /** 操作人名称 */
    @Excel(name = "操作人名称", sort = 5)
    @Column(name = "operator_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "操作人名称")
    private String operatorName;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "操作时间", sort = 6, width = 30, dateFormat = "yyyy-MM-dd")
    @Column(name = "operate_time", searchType = SearchType.EQ)
    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    /** 操作类型 */
    @Excel(name = "评论类型", sort = 7)
    @Column(name = "comment_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "评论类型")
    private String commentType;

    @ApiModelProperty(value = "评论内容")
    @Excel(name = "评论内容", type = Excel.Type.ALL)
    private String remark;

    public SysComment(String handleInfo) {
        this.remark = handleInfo;
    }

    public void preInsert() {
        super.preInsert();
        this.operateTime = new Date();
        LoginUser user = SecurityUtils.getLoginUser();
        if (user != null) {
            this.operator = user.getId();
            this.operatorName = user.getNickName();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("businessType", getBusinessType())
                .append("businessKey", getBusinessKey())
                .append("operator", getOperator())
                .append("operatorName", getOperatorName())
                .append("operateTime", getOperateTime())
                .append("commentType", getCommentType())
                .append("remark", getRemark())
                .toString();
    }
}
