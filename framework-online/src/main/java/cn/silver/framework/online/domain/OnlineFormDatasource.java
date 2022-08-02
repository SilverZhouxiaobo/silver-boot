package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "online_form_datasource")
public class OnlineFormDatasource extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @Column(name = "id")
    private String id;

    /**
     * 表单Id。
     */
    @Column(name = "form_id")
    private String formId;

    /**
     * 数据源Id。
     */
    @Column(name = "datasource_id")
    private String datasourceId;
}
