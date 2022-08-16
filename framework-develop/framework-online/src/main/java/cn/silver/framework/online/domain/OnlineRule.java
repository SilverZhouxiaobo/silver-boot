package cn.silver.framework.online.domain;

import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

@Data
@Table(name = "online_rule")
public class OnlineRule extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键Id。
     */
    @ApiModelProperty(value = "主键Id")
    @Column(name = "rule_id")
    private String ruleId;

    /**
     * 规则名称。
     */
    @ApiModelProperty(value = "规则名称")
    @Column(name = "rule_name")
    private String ruleName;

    /**
     * 规则类型。
     */
    @ApiModelProperty(value = "规则类型")
    @Column(name = "rule_type")
    private Integer ruleType;

    /**
     * 内置规则标记。
     */
    @ApiModelProperty(value = "内置规则标记")
    @Column(name = "builtin")
    private Boolean builtin;

    /**
     * 自定义规则的正则表达式。
     */
    @ApiModelProperty(value = "自定义规则的正则表达式")
    @Column(name = "pattern")
    private String pattern;

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

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    /**
     * ruleId 的多对多关联表数据对象，数据对应类型为OnlineColumnRuleVo。
     */
    @ApiModelProperty(value = "ruleId 的多对多关联表数据对象")
    private Map<String, Object> onlineColumnRule;

    /**
     * ruleType 常量字典关联数据。
     */
    @ApiModelProperty(value = "ruleType 常量字典关联数据")
    private Map<String, Object> ruleTypeDictMap;

}
