package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "online_virtual_column")
public class OnlineVirtualColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @Column(name = "virtual_column_id")
    private String virtualColumnId;

    /**
     * 所在表Id。
     */
    @Column(name = "table_id")
    private String tableId;

    /**
     * 字段名称。
     */
    @Column(name = "object_field_name")
    private String objectFieldName;

    /**
     * 属性类型。
     */
    @Column(name = "object_field_type")
    private String objectFieldType;

    /**
     * 字段提示名。
     */
    @Column(name = "column_prompt")
    private String columnPrompt;

    /**
     * 虚拟字段类型(0: 聚合)。
     */
    @Column(name = "virtual_type")
    private Integer virtualType;

    /**
     * 关联数据源Id。
     */
    @Column(name = "datasource_id")
    private String datasourceId;

    /**
     * 关联Id。
     */
    @Column(name = "relation_id")
    private Long relationId;

    /**
     * 聚合字段所在关联表Id。
     */
    @Column(name = "aggregation_table_id")
    private String aggregationTableId;

    /**
     * 关联表聚合字段Id。
     */
    @Column(name = "aggregation_column_id")
    private Long aggregationColumnId;

    /**
     * 聚合类型(0: count 1: sum 2: avg 3: max 4:min)。
     */
    @Column(name = "aggregation_type")
    private Integer aggregationType;

    /**
     * 存储过滤条件的json。
     */
    @Column(name = "where_clause_json")
    private String whereClauseJson;
}
