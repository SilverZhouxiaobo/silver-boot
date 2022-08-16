package cn.silver.framework.config.domain;


import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.TreeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

/**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date: 2019-05-29
 * @Version: V1.0
 */
@Data
@Table(name = "sys_category")
@ApiModel(value = "SysCategory", description = "分类配置表")
public class SysCategory extends TreeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 类型名称
     */
    @Excel(name = "类型名称")
    @ApiModelProperty("类型名称")
    @Column(name = "name", unique = true, searchType = SearchType.LIKE)
    private String name;

    @Excel(name = "大类编码")
    @ApiModelProperty("大类编码")
    private String baseCode;
    /**
     * 类型编码
     */
    @Excel(name = "类型编码")
    @ApiModelProperty("类型编码")
    @Column(name = "code", unique = true, dictable = true, searchType = SearchType.LIKE)
    private String code;

    @Excel(name = "上级编码")
    @ApiModelProperty("上级编码")
    private String pcode;

    @Override
    public String getLabel() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.code;
    }

    @Override
    public boolean checkExists() {
        return true;
    }

    @Override
    public String toString() {
        return "SysCategory [code=" + code + ", name=" + name + "]";
    }

}
