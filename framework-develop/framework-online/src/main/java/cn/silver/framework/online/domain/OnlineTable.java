package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Table(name = "online_table")
public class OnlineTable extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "table_id")
    private String tableId;

    /**
     * 表名称。
     */
    @ApiModelProperty(value = "表名称")
    @Column(name = "table_name")
    private String tableName;

    /**
     * 实体名称。
     */
    @ApiModelProperty(value = "实体名称")
    @Column(name = "model_name")
    private String modelName;

    /**
     * 数据库链接Id。
     */
    @ApiModelProperty(value = "数据库链接Id")
    @Column(name = "dblink_id")
    private String dblinkId;

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

    private transient List<OnlineColumn> columnList;

    /**
     * 该字段会被缓存，因此在线表单执行操作时可以从缓存中读取该数据，并可基于columnId进行快速检索。
     */
    private transient Map<String, OnlineColumn> columnMap;

    /**
     * 当前表的主键字段，该字段仅仅用于动态表单运行时的SQL拼装。
     */
    private transient OnlineColumn primaryKeyColumn;

    /**
     * 当前表的逻辑删除字段，该字段仅仅用于动态表单运行时的SQL拼装。
     */
    private transient OnlineColumn logicDeleteColumn;
}
