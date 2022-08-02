package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "online_datasource_table")
public class OnlineDatasourceTable extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @Column(name = "id")
    private String id;

    /**
     * 数据源Id。
     */
    @Column(name = "datasource_id")
    private String datasourceId;

    /**
     * 数据源关联Id。
     */
    @Column(name = "relation_id")
    private String relationId;

    /**
     * 数据表Id。
     */
    @Column(name = "table_id")
    private String tableId;
}
