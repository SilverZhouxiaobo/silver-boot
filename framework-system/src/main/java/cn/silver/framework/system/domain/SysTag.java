package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.TreeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

/**
 * @Description: sys_tag
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Data
@Table(name = "sys_tag")
@ApiModel(value = "SysTag", description = "系统标签库")
public class SysTag extends TreeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 标签编号
     */
    @Excel(name = "标签编号")
    @ApiModelProperty(value = "标签编号")
    @Column(name = "code", unique = true, searchType = SearchType.LIKE, dictable = true)
    private String code;

    /**
     * 标签名称
     */
    @Excel(name = "标签名称")
    @ApiModelProperty(value = "标签名称")
    @Column(name = "name", unique = true, searchType = SearchType.LIKE)
    private String name;

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
}
