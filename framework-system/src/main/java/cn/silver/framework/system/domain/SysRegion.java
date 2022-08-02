package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.TreeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 行政区域信息对象 sys_region
 *
 * @author hb
 * @date 2022-07-06
 */

@Data
@Table(name = "sys_region")
@ApiModel(value = "SysRegion", description = "SysRegion对象")
public class SysRegion extends TreeEntity {

    private static final long serialVersionUID = 1L;
    /** 区域名称 */
    @Excel(name = "区域名称", sort = 2)
    @NotBlank(message = "区域名称不能为空")
    @Column(name = "name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "区域名称")
    private String name;

    /** 简称 */
    @Excel(name = "简称", sort = 3)
    @Column(name = "short_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "简称")
    private String shortName;

    /** 区域编码 */
    @Excel(name = "区域编码", sort = 4)
    @NotBlank(message = "区域编码不能为空")
    @Column(name = "code", searchType = SearchType.EQ)
    @ApiModelProperty(value = "区域编码")
    private String code;

    /** 节点编码 */
    @Excel(name = "节点编码", sort = 5)
    @Column(name = "node", searchType = SearchType.EQ)
    @ApiModelProperty(value = "节点编码")
    private String node;

    /** 区域类型 */
    @Excel(name = "区域类型", sort = 6)
    @NotBlank(message = "区域类型不能为空")
    @Column(name = "type", searchType = SearchType.EQ)
    @ApiModelProperty(value = "区域类型")
    private String type;

    /** 电话区号 */
    @Excel(name = "电话区号", sort = 7)
    @Column(name = "area_code", searchType = SearchType.EQ)
    @ApiModelProperty(value = "电话区号")
    private String areaCode;

    /** 邮编 */
    @Excel(name = "邮编", sort = 8)
    @Column(name = "post_code", searchType = SearchType.EQ)
    @ApiModelProperty(value = "邮编")
    private String postCode;

    /** 区域描述 */
    @Excel(name = "区域描述", sort = 11)
    @Column(name = "description", searchType = SearchType.EQ)
    @ApiModelProperty(value = "区域描述")
    private String description;

    /** 创建人 */
    @Excel(name = "创建人", sort = 12)
    @Column(name = "created_id", searchType = SearchType.EQ)
    @ApiModelProperty(value = "创建人")
    private String createdId;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名", sort = 13)
    @Column(name = "created_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "创建人姓名")
    private String createdName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", sort = 14, width = 30, dateFormat = "yyyy-MM-dd")
    @Column(name = "created_time", searchType = SearchType.EQ)
    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    /** 更新人 */
    @Excel(name = "更新人", sort = 15)
    @Column(name = "updated_by", searchType = SearchType.EQ)
    @ApiModelProperty(value = "更新人")
    private String updatedBy;

    /** 更新人姓名 */
    @Excel(name = "更新人姓名", sort = 16)
    @Column(name = "updated_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "更新人姓名")
    private String updatedName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", sort = 17, width = 30, dateFormat = "yyyy-MM-dd")
    @Column(name = "updated_time", searchType = SearchType.EQ)
    @ApiModelProperty(value = "更新时间")
    private Date updatedTime;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("shortName", getShortName())
                .append("code", getCode())
                .append("node", getNode())
                .append("type", getType())
                .append("areaCode", getAreaCode())
                .append("postCode", getPostCode())
                .append("pid", getPid())
                .append("description", getDescription())
                .append("createdId", getCreatedId())
                .append("createdName", getCreatedName())
                .append("createdTime", getCreatedTime())
                .append("updatedBy", getUpdatedBy())
                .append("updatedName", getUpdatedName())
                .append("updatedTime", getUpdatedTime())
                .append("deleted", getDeleted())
                .toString();
    }
}
