package cn.silver.framework.core.page;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象  加入泛型
 *
 * @author JuniorRay
 * @date 2020-11-12
 */
@Data
@ApiModel(description = "表格分页数据对象ResponsePageInfo")
public class ResponsePageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数")
    private long total;

    /**
     * 列表数据
     */
    @ApiModelProperty(value = "列表数据")
    private List<T> rows;

    /**
     * 消息状态码
     */
    @ApiModelProperty(value = "消息状态码")
    private int code;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String msg;

    @ApiModelProperty(value = "请求接口名称")
    private String title;

    /**
     * 表格数据对象
     */
    public ResponsePageInfo() {
    }

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public ResponsePageInfo(List<T> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public ResponsePageInfo(PageInfo<T> page) {
        this.rows = page.getList();
        this.total = page.getTotal();
    }
}
