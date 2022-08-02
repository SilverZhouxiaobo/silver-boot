package cn.silver.framework.core.page;

import cn.silver.framework.common.utils.ServletUtils;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 表格数据处理 通过request请求 拦截获取PAGE_NUM，PAGE_SIZE，ORDER_BY_COLUMN，IS_ASC
 * <p>
 * PageBuilder 表明是建造者模式
 *
 * @author JuniorRay
 */
@Data
@ApiModel(value = "PageBuilder", description = "表格数据处理")
public class PageBuilder {
    private static final long serialVersionUID = 1L;
    /**
     * 当前记录起始索引
     */
    @ApiModelProperty(value = "当前记录起始索引")
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    @ApiModelProperty(value = "每页显示记录数")
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    @ApiModelProperty(value = "排序列")
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    @ApiModelProperty(value = "排序的方向 \"desc\" 或者 \"asc\" ")
    public static final String IS_ASC = "isAsc";

    /**
     * 封装分页对象  抓取请求行中PAGE_NUM ，PAGE_SIZE，ORDER_BY_COLUMN，IS_ASC
     */
    private static PageBean getPageDomain(BaseEntity entity) {
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(ServletUtils.getParameterToInt(PAGE_NUM));
        if (pageBean.getPageNum() == null) {
            pageBean.setPageNum(1);
        }
        pageBean.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE));
        if (pageBean.getPageSize() == null) {
            pageBean.setPageSize(10);
        }
        pageBean.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        if (StringUtils.isBlank(pageBean.getOrderByColumn()) && ObjectUtils.isNotEmpty(entity)) {
            pageBean.setOrderByColumn(entity.getOrderColumn());
        }
        String defaultOrder = entity != null ? entity.getOrderType() : "desc";
        pageBean.setIsAsc(ServletUtils.getParameter(IS_ASC), defaultOrder);
        return pageBean;
    }

    public static PageBean buildPageRequest() {
        return getPageDomain(null);
    }

    public static PageBean buildPageRequest(BaseEntity entity) {
        return getPageDomain(entity);
    }
}
