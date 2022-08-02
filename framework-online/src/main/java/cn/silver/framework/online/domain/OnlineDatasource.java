package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 在线表单的数据源对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@ApiModel("在线表单的数据源Dto对象")
@Data
@Table(name = "online_datasource")
public class OnlineDatasource extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！")
    @Column(name = "datasource_id")
    private String datasourceId;

    /**
     * 数据源名称。
     */
    @ApiModelProperty(value = "数据源名称")
    @NotBlank(message = "数据验证失败，数据源名称不能为空！")
    @Column(name = "datasource_name")
    private String datasourceName;

    /**
     * 数据源变量名，会成为数据访问url的一部分。
     */
    @ApiModelProperty(value = "数据源变量名，会成为数据访问url的一部分")
    @NotBlank(message = "数据验证失败，数据源变量名不能为空！")
    private String variableName;

    /**
     * 主表所在的数据库链接Id。
     */
    @ApiModelProperty(value = "主表所在的数据库链接Id")
    @NotNull(message = "数据验证失败，数据库链接Id不能为空！")
    @Column(name = "variable_name")
    private String dblinkId;

    /**
     * 主表Id。
     */
    @ApiModelProperty(value = "主表Id")
    @NotNull(message = "数据验证失败，主表Id不能为空！")
    @Column(name = "dblink_id")
    private String masterTableId;

    /**
     * 主表表名。
     */
    @ApiModelProperty(value = "主表表名")
    @NotBlank(message = "数据验证失败，主表名不能为空！")
    @Column(name = "master_table_id")
    private String masterTableName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    private transient OnlineTable masterTable;

    private transient OnlinePageDatasource onlinePageDatasource;
}
