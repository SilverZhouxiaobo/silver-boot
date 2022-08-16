package cn.silver.framework.online.domain;


import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 在线表单关联的字典实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Table(name = "online_dict")
public class OnlineDict extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "dict_id")
    private String dictId;

    /**
     * 字典名称。
     */
    @ApiModelProperty(value = "字典名称")
    @Column(name = "dict_name")
    private String dictName;

    /**
     * 字典类型。
     */
    @ApiModelProperty(value = "字典类型")
    @Column(name = "dict_type")
    private Integer dictType;

    /**
     * 数据库链接Id。
     */
    @ApiModelProperty(value = "数据库链接Id")
    @Column(name = "dblink_id")
    private String dblinkId;

    /**
     * 字典表名称。
     */
    @ApiModelProperty(value = "字典表名称")
    @Column(name = "table_name")
    private String tableName;

    /**
     * 字典表键字段名称。
     */
    @ApiModelProperty(value = "字典表键字段名称")
    @Column(name = "key_column_name")
    private String keyColumnName;

    /**
     * 字典表父键字段名称。
     */
    @ApiModelProperty(value = "字典表父键字段名称")
    @Column(name = "parent_key_column_name")
    private String parentKeyColumnName;

    /**
     * 字典值字段名称。
     */
    @ApiModelProperty(value = "字典值字段名称")
    @Column(name = "value_column_name")
    private String valueColumnName;

    /**
     * 逻辑删除字段。
     */
    @ApiModelProperty(value = "逻辑删除字段")
    @Column(name = "deleted_column_name")
    private String deletedColumnName;

    /**
     * 用户过滤滤字段名称。
     */
    @ApiModelProperty(value = "用户过滤滤字段名称")
    @Column(name = "user_filter_column_name")
    private String userFilterColumnName;

    /**
     * 部门过滤字段名称。
     */
    @ApiModelProperty(value = "部门过滤字段名称")
    @Column(name = "dept_filter_column_name")
    private String deptFilterColumnName;

    /**
     * 租户过滤字段名称。
     */
    @ApiModelProperty(value = "租户过滤字段名称")
    @Column(name = "tenant_filter_column_name")
    private String tenantFilterColumnName;

    /**
     * 是否树形标记。
     */
    @ApiModelProperty(value = "是否树形标记")
    @Column(name = "tree_flag")
    private Boolean treeFlag;

    /**
     * 获取字典数据的url。
     */
    @ApiModelProperty(value = "获取字典数据的url")
    @Column(name = "dict_list_url")
    private String dictListUrl;

    /**
     * 根据主键id批量获取字典数据的url。
     */
    @ApiModelProperty(value = "根据主键id批量获取字典数据的url")
    @Column(name = "dict_ids_url")
    private String dictIdsUrl;

    /**
     * 字典的JSON数据。
     */
    @ApiModelProperty(value = "字典的JSON数据")
    @Column(name = "dict_data_json")
    private String dictDataJson;

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
}
