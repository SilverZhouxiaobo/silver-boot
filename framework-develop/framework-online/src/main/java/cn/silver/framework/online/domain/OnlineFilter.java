package cn.silver.framework.online.domain;

import cn.silver.framework.online.domain.constant.FieldFilterType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@ApiModel("在线表单数据过滤参数对象")
@Data
public class OnlineFilter {
    /**
     * 表名。
     */
    @ApiModelProperty(value = "表名")
    private String tableName;

    /**
     * 过滤字段名。
     */
    @ApiModelProperty(value = "过滤字段名")
    private String columnName;

    /**
     * 过滤值。
     */
    @ApiModelProperty(value = "过滤值")
    private Object columnValue;

    /**
     * 范围比较的最小值。
     */
    @ApiModelProperty(value = "范围比较的最小值")
    private Object columnValueStart;

    /**
     * 范围比较的最大值。
     */
    @ApiModelProperty(value = "范围比较的最大值")
    private Object columnValueEnd;

    /**
     * 仅当操作符为IN的时候使用。
     */
    @ApiModelProperty(value = "仅当操作符为IN的时候使用")
    private Set<Object> columnValueList;

    /**
     * 过滤类型，参考FieldFilterType常量对象。缺省值就是等于过滤了。
     */
    @ApiModelProperty(value = "过滤类型")
    private Integer filterType = FieldFilterType.EQUAL_FILTER;
}
