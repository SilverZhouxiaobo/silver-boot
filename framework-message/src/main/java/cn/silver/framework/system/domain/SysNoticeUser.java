package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import java.util.Date;

/**
 * 系统消息通知对象 sys_notice_user
 *
 * @author hb
 * @date 2022-06-20
 */

@Data
@Table(name = "sys_notice_user")
@ApiModel(value = "SysNoticeUser", description = "SysNoticeUser对象")
public class SysNoticeUser extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 通告ID
     */
    @Excel(name = "通告ID", sort = 2)
    @Column(name = "notice_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "通告ID")
    private String noticeId;

    /**
     * 用户id
     */
    @Excel(name = "用户id", sort = 3)
    @Column(name = "user_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 阅读状态
     */
    @Excel(name = "阅读状态", sort = 4)
    @Column(name = "read_flag", searchType = SearchType.EQ)
    @ApiModelProperty(value = "阅读状态")
    private String readFlag;

    /**
     * 阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "阅读时间", sort = 5, width = 30, dateFormat = "yyyy-MM-dd")
    @Column(name = "read_time", searchType = SearchType.EQ)
    @ApiModelProperty(value = "阅读时间")
    private Date readTime;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("noticeId", getNoticeId())
                .append("userId", getUserId())
                .append("readFlag", getReadFlag())
                .append("readTime", getReadTime())
                .toString();
    }
}
