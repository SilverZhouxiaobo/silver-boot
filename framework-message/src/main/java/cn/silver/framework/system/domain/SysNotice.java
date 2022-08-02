package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Xss;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 通知公告表 sys_notice
 *
 * @author hb
 */
@Data
@Table(name = "sys_notice")
@ApiModel(value = "SysNotice", description = "通知公告表")
public class SysNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 公告标题
     */
    @ApiModelProperty(value = "公告标题")
    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    @Column(name = "notice_title", searchType = SearchType.LIKE)
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @Column(name = "notice_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "公告类型（1通知 2公告）")
    private String noticeType;

    /**
     * 公告内容
     */
    @Column(name = "notice_content", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "公告内容")
    private String noticeContent;

    /**
     * 公告状态（0正常 1关闭）
     */
    @Column(name = "status", searchType = SearchType.EQ)
    @ApiModelProperty(value = "公告状态（0正常 1关闭）")
    private String status;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("noticeId", getId())
                .append("noticeTitle", getNoticeTitle())
                .append("noticeType", getNoticeType())
                .append("noticeContent", getNoticeContent())
                .append("status", getStatus())
                .toString();
    }
}
