package cn.silver.framework.config.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.DataEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;

/**
 * 参数配置表 sys_config
 *
 * @author hb
 */
@Data
@Table(name = "sys_config")
@ApiModel(value = "SysConfig", description = "参数配置表")
public class SysConfig extends DataEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名称
     */
    @Excel(name = "参数名称")
    @ApiModelProperty(value = "参数名称")
    @Column(name = "config_name", searchType = SearchType.LIKE)
    private String configName;

    /**
     * 参数键名
     */
    @Excel(name = "参数键名")
    @ApiModelProperty(value = "参数键名")
    @Column(name = "config_key", searchType = SearchType.LIKE)
    private String configKey;

    /**
     * 参数键值
     */
    @Excel(name = "参数键值")
    @ApiModelProperty(value = "参数键值")
    @Column(name = "config_value", searchType = SearchType.EQ)
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否")
    @ApiModelProperty(value = "系统内置（Y是 N否）")
    private Boolean configType;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("configId", getId())
                .append("configName", getConfigName())
                .append("configKey", getConfigKey())
                .append("configValue", getConfigValue())
                .append("configType", getConfigType())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
