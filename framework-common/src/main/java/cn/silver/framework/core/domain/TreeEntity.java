package cn.silver.framework.core.domain;

import cn.silver.framework.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree基类
 *
 * @author hb
 */
@Data
@ApiModel(value = "TreeEntity", description = "TreeEntity基类")
public class TreeEntity extends DataEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID
     */
    @ApiModelProperty(value = "上级ID")
    @Excel(name = "上级名称", sort = 1)
    protected String pid;

    @ApiModelProperty(value = "是否有下级")
    protected transient boolean hasChild = true;

    @ApiModelProperty(value = "上级名称")
    protected transient String parentName;
    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    protected Integer orderNum;

    /**
     * 祖级列表
     */
    @ApiModelProperty(value = "祖级列表")
    protected transient String ancestors;
    /**
     * 子部门
     */
    @ApiModelProperty(value = "子部门")
    private transient List<TreeEntity> children = new ArrayList<>();

    @Override
    public String getOrderColumn() {
        return "order_num";
    }
}
