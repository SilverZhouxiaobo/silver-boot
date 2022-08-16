package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

@Data
@Table(name = "online_page")
public class OnlinePage extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "page_id")
    private String pageId;

    /**
     * 页面编码。
     */
    @ApiModelProperty(value = "页面编码")
    @Column(name = "page_code")
    private String pageCode;

    /**
     * 页面名称。
     */
    @ApiModelProperty(value = "页面名称")
    @Column(name = "page_name")
    private String pageName;

    /**
     * 页面类型。
     */
    @ApiModelProperty(value = "页面类型")
    @Column(name = "page_type")
    private Integer pageType;

    /**
     * 页面编辑状态。
     */
    @ApiModelProperty(value = "页面编辑状态")
    @Column(name = "status")
    private Integer status;

    /**
     * 是否发布。
     */
    @ApiModelProperty(value = "是否发布")
    @Column(name = "published")
    private Boolean published;

    /**
     * 更新时间。
     */
    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "pageType 常量字典关联数据")
    private transient Map<String, Object> pageTypeDictMap;

    @ApiModelProperty(value = "status 常量字典关联数据")
    private transient Map<String, Object> statusDictMap;

}
