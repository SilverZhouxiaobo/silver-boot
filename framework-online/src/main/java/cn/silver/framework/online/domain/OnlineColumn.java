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

/**
 * 在线表单数据表字段Dto对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@ApiModel("在线表单数据表字段Dto对象")
@Data
@Table(name = "online_column")
public class OnlineColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！")
    @Column(name = "column_id")
    private String columnId;

    /**
     * 字段名。
     */
    @ApiModelProperty(value = "字段名")
    @NotBlank(message = "数据验证失败，字段名不能为空！")
    @Column(name = "column_name")
    private String columnName;

    /**
     * 数据表Id。
     */
    @ApiModelProperty(value = "数据表Id")
    @NotNull(message = "数据验证失败，数据表Id不能为空！")
    @Column(name = "table_id")
    private String tableId;

    /**
     * 数据表中的字段类型。
     */
    @ApiModelProperty(value = "数据表中的字段类型")
    @NotBlank(message = "数据验证失败，数据表中的字段类型不能为空！")
    @Column(name = "column_type")
    private String columnType;

    /**
     * 数据表中的完整字段类型(包括了精度和刻度)。
     */
    @ApiModelProperty(value = "数据表中的完整字段类型")
    @NotBlank(message = "数据验证失败，数据表中的完整字段类型(包括了精度和刻度)不能为空！")
    @Column(name = "full_column_type")
    private String fullColumnType;

    /**
     * 是否为主键。
     */
    @ApiModelProperty(value = "是否为主键")
    @NotNull(message = "数据验证失败，是否为主键不能为空！")
    @Column(name = "primary_key")
    private Boolean primaryKey;

    /**
     * 是否是自增主键(0: 不是 1: 是)。
     */
    @ApiModelProperty(value = "是否是自增主键")
    @NotNull(message = "数据验证失败，是否是自增主键(0: 不是 1: 是)不能为空！")
    @Column(name = "auto_increment")
    private Boolean autoIncrement;

    /**
     * 是否可以为空 (0: 不可以为空 1: 可以为空)。
     */
    @ApiModelProperty(value = "是否可以为空")
    @NotNull(message = "数据验证失败，是否可以为空 (0: 不可以为空 1: 可以为空)不能为空！")
    @Column(name = "nullable")
    private Boolean nullable;

    /**
     * 缺省值。
     */
    @ApiModelProperty(value = "缺省值")
    @Column(name = "column_default")
    private String columnDefault;

    /**
     * 字段在数据表中的显示位置。
     */
    @ApiModelProperty(value = "字段在数据表中的显示位置")
    @NotNull(message = "数据验证失败，字段在数据表中的显示位置不能为空！")
    @Column(name = "column_show_order")
    private Integer columnShowOrder;

    /**
     * 数据表中的字段注释。
     */
    @ApiModelProperty(value = "数据表中的字段注释")
    @Column(name = "column_comment")
    private String columnComment;

    /**
     * 对象映射字段名称。
     */
    @ApiModelProperty(value = "对象映射字段名称")
    @NotBlank(message = "数据验证失败，对象映射字段名称不能为空！")
    @Column(name = "object_field_name")
    private String objectFieldName;

    /**
     * 对象映射字段类型。
     */
    @ApiModelProperty(value = "对象映射字段类型")
    @NotBlank(message = "数据验证失败，对象映射字段类型不能为空！")
    @Column(name = "object_field_type")
    private String objectFieldType;

    /**
     * 过滤类型字段。
     */
    @ApiModelProperty(value = "过滤类型字段")
    @NotNull(message = "数据验证失败，过滤类型字段不能为空！")
    @Column(name = "filter_type")
    private Integer filterType;

    /**
     * 是否是主键的父Id。
     */
    @ApiModelProperty(value = "是否是主键的父Id")
    @NotNull(message = "数据验证失败，是否是主键的父Id不能为空！")
    @Column(name = "parent_key")
    private Boolean parentKey;

    /**
     * 是否部门过滤字段。
     */
    @ApiModelProperty(value = "是否部门过滤字段")
    @NotNull(message = "数据验证失败，是否部门过滤字段标记不能为空！")
    @Column(name = "dept_filter")
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    @ApiModelProperty(value = "是否用户过滤字段")
    @NotNull(message = "数据验证失败，是否用户过滤字段标记不能为空！")
    @Column(name = "user_filter")
    private Boolean userFilter;

    /**
     * 字段类别。
     */
    @ApiModelProperty(value = "字段类别")
    @Column(name = "field_kind")
    private Integer fieldKind;

    /**
     * 包含的文件文件数量，0表示无限制。
     */
    @ApiModelProperty(value = "包含的文件文件数量，0表示无限制")
    @Column(name = "max_file_count")
    private Integer maxFileCount;

    /**
     * 字典Id。
     */
    @ApiModelProperty(value = "字典Id")
    @Column(name = "dict_id")
    private String dictId;

    /**
     * 更新时间。
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @Column(name = "create_time")
    private Date createTime;
}
