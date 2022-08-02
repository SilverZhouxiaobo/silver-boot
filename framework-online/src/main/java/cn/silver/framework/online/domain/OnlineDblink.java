package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 在线表单数据表所在数据库链接实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Table(name = "online_dblink")
public class OnlineDblink extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "dblink_id")
    private String dblinkId;

    /**
     * 链接中文名称。
     */
    @ApiModelProperty(value = "链接中文名称")
    @Column(name = "dblink_name")
    private String dblinkName;

    /**
     * 链接英文名称。
     */
    @ApiModelProperty(value = "链接英文名称")
    @Column(name = "variable_name")
    private String variableName;

    /**
     * 链接描述。
     */
    @ApiModelProperty(value = "链接描述")
    @Column(name = "dblink_desc")
    private String dblinkDesc;

    /**
     * 数据源配置常量。
     */
    @ApiModelProperty(value = "数据源配置常量")
    @Column(name = "dblink_config_constant")
    private Integer dblinkConfigConstant;

    /**
     * 创建时间。
     */
    @ApiModelProperty(value = "创建时间")
    @Column(name = "create_time")
    private Date createTime;
}
