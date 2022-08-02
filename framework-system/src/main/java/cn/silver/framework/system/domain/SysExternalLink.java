package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.model.ExternalLinkModel;
import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import java.util.Date;

/**
 * 外链信息对象 sys_external_link
 *
 * @author hb
 * @date 2022-06-13
 */

@Data
@NoArgsConstructor
@Table(name = "sys_external_link")
@ApiModel(value = "SysExternalLink", description = "SysExternalLink对象")
public class SysExternalLink extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 外链名称
     */
    @Excel(name = "外链名称", sort = 2)
    @Column(name = "name", searchType = SearchType.LIKE, nullable = false, unique = true)
    @ApiModelProperty(value = "外链名称")
    private String name;

    /**
     * 外链编码
     */
    @Excel(name = "外链编码", sort = 3)
    @Column(name = "code", searchType = SearchType.EQ, nullable = false, unique = true, dictable = true)
    @ApiModelProperty(value = "外链编码")
    private String code;

    /**
     * 实际地址
     */
    @Excel(name = "实际地址", sort = 4)
    @Column(name = "link_url", searchType = SearchType.EQ)
    @ApiModelProperty(value = "实际地址")
    private String linkUrl;

    /**
     * 外链类型
     */
    @Excel(name = "外链类型", sort = 5)
    @Column(name = "link_type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "外链类型")
    private String linkType;

    /**
     * 附加参数
     */
    @Excel(name = "附加参数", sort = 6)
    @Column(name = "link_params", searchType = SearchType.EQ)
    @ApiModelProperty(value = "附加参数")
    private String linkParams;

    @Excel(name = "活动主键", sort = 7)
    @Column(name = "event_key", searchType = SearchType.EQ)
    @ApiModelProperty(value = "活动主键")
    private String eventKey;

    @Excel(name = "业务主键", sort = 8)
    @Column(name = "business_key", searchType = SearchType.EQ)
    @ApiModelProperty(value = "业务主键")
    private String businessKey;

    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", sort = 7, width = 30, dateFormat = "yyyy-MM-dd")
    @Column(name = "effective_time", searchType = SearchType.EQ)
    @ApiModelProperty(value = "生效时间")
    private Date effectiveTime;

    /**
     * 失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "失效时间", sort = 7, width = 30, dateFormat = "yyyy-MM-dd")
    @Column(name = "dead_time", searchType = SearchType.EQ)
    @ApiModelProperty(value = "失效时间")
    private Date deadTime;

    @ApiModelProperty(value = "访问说明")
    @Excel(name = "访问说明", type = Excel.Type.ALL)
    private String remark;

    @SneakyThrows
    public SysExternalLink(ExternalLinkModel model) {
        BeanUtil.copyProperties(model, this);
    }

    @Override
    public String getLabel() {
        return this.getName();
    }

    @Override
    public String getValue() {
        return this.getCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("code", getCode())
                .append("linkUrl", getLinkUrl())
                .append("linkType", getLinkType())
                .append("linkParams", getLinkParams())
                .append("remark", getRemark())
                .toString();
    }
}
