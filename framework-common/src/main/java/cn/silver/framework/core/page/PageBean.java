package cn.silver.framework.core.page;

import cn.silver.framework.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页对象实体
 *
 * @author hb
 */
@Data
@ApiModel(value = "SysUser", description = "用户对象")
public class PageBean {
    /**
     * 当前记录起始索引
     */
    @ApiModelProperty(value = "当前记录起始索引")
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    @ApiModelProperty(value = "每页显示记录数")
    private Integer pageSize;

    /**
     * 排序列
     */
    @ApiModelProperty(value = "排序列")
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    @ApiModelProperty(value = "排序的方向desc或者asc")
    private String isAsc;

    public String getOrderBy() {
        if (StringUtils.isEmpty(orderByColumn)) {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc;
    }

    public void setIsAsc(String isAsc, String defaultValue) {
        if (StringUtils.isNotEmpty(isAsc)) {
            // 兼容前端排序类型
            if ("ascending".equals(isAsc)) {
                isAsc = "asc";
            } else if ("descending".equals(isAsc)) {
                isAsc = "desc";
            }
            this.isAsc = isAsc;
        } else {
            this.isAsc = defaultValue;
        }
    }
}
