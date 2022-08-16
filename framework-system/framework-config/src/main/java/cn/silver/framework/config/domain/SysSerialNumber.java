package cn.silver.framework.config.domain;

import cn.silver.framework.common.annotation.Excel;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 单编码规则配置对象 sys_serial_number
 *
 * @author hb
 * @date 2022-06-20
 */

@Data
@Table(name = "sys_serial_number")
@ApiModel(value = "SysSerialNumber", description = "SysSerialNumber对象")
public class SysSerialNumber extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 规则名称
     */
    @Excel(name = "规则名称", sort = 2)
    @NotBlank(message = "规则名称不能为空")
    @Column(name = "name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "规则名称")
    private String name;

    /**
     * 业务名称
     */
    @Excel(name = "业务名称", sort = 3)
    @Column(name = "business_name", searchType = SearchType.LIKE)
    @ApiModelProperty(value = "业务名称")
    private String businessName;

    /**
     * 模块编码
     */
    @Excel(name = "模块编码", sort = 4)
    @Column(name = "business_code", searchType = SearchType.EQ)
    @ApiModelProperty(value = "模块编码")
    private String businessCode;

    @Excel(name = "前缀", sort = 4)
    @Column(name = "prefix", searchType = SearchType.EQ)
    @ApiModelProperty(value = "前缀")
    private String prefix;

    @Excel(name = "日期表达式", sort = 4)
    @Column(name = "date_format_str", searchType = SearchType.EQ)
    @ApiModelProperty(value = "日期表达式")
    private String dateFormatStr;

    @Excel(name = "数字表达式", sort = 4)
    @Column(name = "number_expression", searchType = SearchType.EQ)
    @ApiModelProperty(value = "数字表达式")
    private String numberExpression;

    /**
     * 配置模板;使用的序列号模板
     */
    @Excel(name = "配置模板", sort = 5)
    @Column(name = "config_templet", searchType = SearchType.EQ)
    @ApiModelProperty(value = "配置模板")
    private String configTemplet;

    /**
     * 最大序列号
     */
    @Excel(name = "最大序列号", sort = 6)
    @Column(name = "max_serial", searchType = SearchType.EQ)
    @ApiModelProperty(value = "最大序列号")
    private Integer maxSerial;

    /**
     * 预生成序列号存放到缓存的个数
     */
    @Excel(name = "预生成序列号存放到缓存的个数", sort = 7)
    @Column(name = "pre_max_num", searchType = SearchType.EQ)
    @ApiModelProperty(value = "预生成序列号存放到缓存的个数")
    private Integer preMaxNum;

    /**
     * 最后获取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后获取时间", sort = 8, width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_get_time", searchType = SearchType.BETWEEN)
    @ApiModelProperty(value = "最后获取时间")
    private Date lastGetTime;

    /**
     * 是否自增
     */
    @Excel(name = "是否自增", sort = 9)
    @Column(name = "auto_increment", searchType = SearchType.EQ)
    @ApiModelProperty(value = "是否自增")
    private Boolean autoIncrement;

    /**
     * 启用/禁用
     */
    @Excel(name = "启用/禁用", sort = 10)
    @Column(name = "enable", searchType = SearchType.EQ)
    @ApiModelProperty(value = "启用/禁用")
    private Boolean enable;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("businessName", getBusinessName())
                .append("businessCode", getBusinessCode())
                .append("configTemplet", getConfigTemplet())
                .append("maxSerial", getMaxSerial())
                .append("preMaxNum", getPreMaxNum())
                .append("lastGetTime", getLastGetTime())
                .append("autoIncrement", getAutoIncrement())
                .append("enable", getEnable())
                .toString();
    }
}
