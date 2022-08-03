package cn.silver.framework.workflow.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流程分类的实体对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Table(name = "flow_category")
@ApiModel(value = "FlowCategory", description = "FlowCategory对象")
public class FlowCategory extends DataEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 显示名称。
     */
    @Excel(name = "流程分类名称", sort = 2)
    @ApiModelProperty(value = "显示名称")
    @NotBlank(message = "数据验证失败，显示名称不能为空！")
    @Column(name = "name", searchType = SearchType.LIKE)
    private String name;

    /**
     * 分类编码。
     */
    @Excel(name = "流程分类编码", sort = 2)
    @ApiModelProperty(value = "分类编码")
    @NotBlank(message = "数据验证失败，分类编码不能为空！")
    @Column(name = "code", searchType = SearchType.LIKE, unique = true, dictable = true)
    private String code;

    /**
     * 实现顺序。
     */
    @Excel(name = "实现顺序", sort = 2)
    @ApiModelProperty(value = "实现顺序")
    @NotNull(message = "数据验证失败，实现顺序不能为空！")
    private Integer orderNo;


    @Override
    public String getLabel() {
        return this.getName();
    }

    @Override
    public String getValue() {
        return this.getCode();
    }
}
