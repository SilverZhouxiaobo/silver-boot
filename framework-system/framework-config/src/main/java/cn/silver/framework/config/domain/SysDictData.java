package cn.silver.framework.config.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import cn.silver.framework.core.model.DictModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 字典数据表 sys_dict_data
 *
 * @author hb
 */
@Data
@NoArgsConstructor
@Table(name = "sys_dict_data")
@ApiModel(value = "SysDictData", description = "字典数据表")
public class SysDictData extends DataEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典排序
     */
    @Excel(name = "字典排序", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty(value = "字典排序")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @Excel(name = "字典标签")
    @ApiModelProperty(value = "字典标签")
    @NotBlank(message = "字典标签不能为空")
    @Size(min = 0, max = 100, message = "字典标签长度不能超过100个字符")
    @Column(name = "dict_label", searchType = SearchType.LIKE, unique = true, dictable = true)
    private String dictLabel;

    /**
     * 字典键值
     */
    @Excel(name = "字典键值")
    @ApiModelProperty(value = "字典键值")
    @NotBlank(message = "字典键值不能为空")
    @Size(min = 0, max = 100, message = "字典键值长度不能超过100个字符")
    @Column(name = "dict_value", searchType = SearchType.LIKE, unique = true, dictable = true)
    private String dictValue;

    /**
     * 字典类型
     */
    @Excel(name = "字典类型")
    @ApiModelProperty(value = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Column(name = "dict_type", searchType = SearchType.EQ)
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @ApiModelProperty(value = "样式属性（其他样式扩展）")
    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    private String cssClass;

    /**
     * 表格字典样式
     */
    @ApiModelProperty(value = "表格字典样式")
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @Excel(name = "是否默认", readConverterExp = "Y=是,N=否")
    @ApiModelProperty(value = "是否默认（Y是 N否）")
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;

    public SysDictData(DictModel model) {
        this.dictLabel = model.getLabel();
        this.dictValue = model.getValue();
        this.dictSort = model.getSort();
    }

    public DictModel getModel() {
        return new DictModel(this.dictLabel, this.dictValue, this.dictSort, this.cssClass, this.listClass, this.getRemark());
    }

    @Override
    public String getLabel() {
        return dictLabel;
    }

    @Override
    public String getValue() {
        return dictValue;
    }

    @Override
    public String getOrderColumn() {
        return "dict_sort";
    }

    @Override
    public String getOrderType() {
        return "asc";
    }

    @Override
    public boolean checkExists() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("dictCode", getId())
                .append("dictSort", getDictSort())
                .append("dictLabel", getDictLabel())
                .append("dictValue", getDictValue())
                .append("dictType", getDictType())
                .append("cssClass", getCssClass())
                .append("listClass", getListClass())
                .append("isDefault", getIsDefault())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
