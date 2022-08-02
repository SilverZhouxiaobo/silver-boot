package cn.silver.framework.system.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;

/**
 * ureport2模板管理对象 base_export_temp
 *
 * @author ruoyi
 * @date 2022-01-21
 */
@Data
@Table(name = "sys_report")
@ApiModel(value = "ureport2模板管理对象")
public class SysReport extends DataEntity {
    private static final long serialVersionUID = 1L;

    /** 模板名称 */
    @Excel(name = "模板名称")
    @ApiModelProperty(value = "模板名称")
    private String name;

    /** 模板编号(该编号取自字典表) */
    @Excel(name = "模板编号(该编号取自字典表)")
    @ApiModelProperty(value = "模板编号(该编号取自字典表)")
    private String code;

    /** 模板文件名称 */
    @Excel(name = "模板文件名称")
    @ApiModelProperty(value = "模板文件名称")
    private String tempFileName;

    /** 访问前缀 */
    @Excel(name = "访问前缀")
    @ApiModelProperty(value = "访问前缀")
    private String prefix;

    /** 存储目录 */
    @Excel(name = "存储目录")
    @ApiModelProperty(value = "存储目录")
    private String fileStoreDir;

    /** 完整路径 */
    @Excel(name = "完整路径")
    @ApiModelProperty(value = "完整路径")
    private String fullPath;

    /** 模板内容 */
    @Excel(name = "模板内容")
    @ApiModelProperty(value = "模板内容")
    private String content;

    /** 启用禁用状态(字典通用是否) */
    @Excel(name = "启用禁用状态(字典通用是否)")
    @ApiModelProperty(value = "启用禁用状态(字典通用是否)")
    private String enableFlag;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("code", getCode())
                .append("tempFileName", getTempFileName())
                .append("prefix", getPrefix())
                .append("fileStoreDir", getFileStoreDir())
                .append("fullPath", getFullPath())
                .append("content", getContent())
                .append("remark", getRemark())
                .toString();
    }
}
