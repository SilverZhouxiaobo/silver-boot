package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

@ApiModel("在线表单的数据源关联Dto对象")
@Data
@Table(name = "online_datasource_relation")
public class OnlineDatasourceRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！")
    @Column(name = "relation_id")
    private String relationId;

    /**
     * 关联名称。
     */
    @ApiModelProperty(value = "关联名称")
    @NotBlank(message = "数据验证失败，关联名称不能为空！")
    @Column(name = "relation_name")
    private String relationName;

    /**
     * 变量名。
     */
    @ApiModelProperty(value = "变量名")
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    @Column(name = "variable_name")
    private String variableName;

    /**
     * 主数据源Id。
     */
    @ApiModelProperty(value = "主数据源Id")
    @NotNull(message = "数据验证失败，主数据源Id不能为空！")
    @Column(name = "datasource_id")
    private String datasourceId;

    /**
     * 关联类型。
     */
    @ApiModelProperty(value = "关联类型")
    @NotNull(message = "数据验证失败，关联类型不能为空！")
    @Column(name = "relation_type")
//    @ConstDictRef(constDictClass = RelationType.class, message = "数据验证失败，关联类型为无效值！")
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    @ApiModelProperty(value = "主表关联字段Id")
    @NotNull(message = "数据验证失败，主表关联字段Id不能为空！")
    @Column(name = "slave_table_id")
    private String masterColumnId;

    /**
     * 从表Id。
     */
    @ApiModelProperty(value = "从表Id")
    @NotNull(message = "数据验证失败，从表Id不能为空！")
    @Column(name = "slave_column_id")
    private String slaveTableId;

    /**
     * 从表名。
     */
    @ApiModelProperty(value = "从表名")
    @NotBlank(message = "数据验证失败，从表名不能为空！")
    @Column(name = "cascade_delete")
    private String slaveTableName;

    /**
     * 从表关联字段Id。
     */
    @ApiModelProperty(value = "从表关联字段Id")
    @NotNull(message = "数据验证失败，从表关联字段Id不能为空！")
    @Column(name = "left_join")
    private String slaveColumnId;

    /**
     * 从表字段名。
     */
    @ApiModelProperty(value = "从表字段名")
    @NotBlank(message = "数据验证失败，从表字段名不能为空！")
    @Column(name = "update_time")
    private String slaveColumnName;

    /**
     * 是否级联删除标记。
     */
    @ApiModelProperty(value = "是否级联删除标记")
    @NotNull(message = "数据验证失败，是否级联删除标记不能为空！")
    @Column(name = "create_time")
    private Boolean cascadeDelete;

    /**
     * 是否左连接标记。
     */
    @ApiModelProperty(value = "是否左连接标记")
    @NotNull(message = "数据验证失败，是否左连接标记不能为空！")
    private Boolean leftJoin;
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

    private transient OnlineColumn masterColumn;

    private transient OnlineTable slaveTable;

    private transient OnlineColumn slaveColumn;

    private transient Map<String, Object> masterColumnIdDictMap;

    private transient Map<String, Object> slaveTableIdDictMap;

    private transient Map<String, Object> slaveColumnIdDictMap;

    private transient Map<String, Object> relationTypeDictMap;
}
