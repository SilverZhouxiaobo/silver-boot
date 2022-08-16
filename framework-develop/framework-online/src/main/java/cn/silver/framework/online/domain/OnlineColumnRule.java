package cn.silver.framework.online.domain;

import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 在线表单数据表字段规则和字段多对多关联对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Table(name = "online_column_rule")
@ApiModel("在线表单数据表字段规则和字段多对多关联Dto对象")
public class OnlineColumnRule extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 字段Id。
     */
    @ApiModelProperty(value = "字段Id")
    @NotNull(message = "数据验证失败，字段Id不能为空！")
    @Column(name = "column_id")
    private String columnId;

    /**
     * 规则Id。
     */
    @ApiModelProperty(value = "规则Id")
    @NotNull(message = "数据验证失败，规则Id不能为空！")
    @Column(name = "rule_id")
    private String ruleId;

    /**
     * 规则属性数据。
     */
    @ApiModelProperty(value = "规则属性数据")
    @Column(name = "prop_data_json")
    private transient String propDataJson;

    private transient OnlineRule onlineRule;
}
