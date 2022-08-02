package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "online_page")
public class OnlinePageDatasource extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "id")
    private String id;

    /**
     * 页面主键Id。
     */
    @ApiModelProperty(value = "页面主键Id")
    @Column(name = "page_id")
    private String pageId;

    /**
     * 数据源主键Id。
     */
    @ApiModelProperty(value = "数据源主键Id")
    @Column(name = "datasource_id")
    private String datasourceId;
}
