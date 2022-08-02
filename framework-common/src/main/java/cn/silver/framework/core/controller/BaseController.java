package cn.silver.framework.core.controller;

import cn.silver.framework.common.constant.HttpStatus;
import cn.silver.framework.common.utils.DateUtils;
import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.hb.software.gacim.core.page.*;
import cn.silver.framework.db.util.SqlUtil;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.core.page.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author hb
 */
@Slf4j
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageBean pageBean = PageBuilder.buildPageRequest();
        Integer pageNum = pageBean.getPageNum();
        Integer pageSize = pageBean.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageBean.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 设置请求排序数据
     */
    protected void startOrderBy() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy())) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应请求分页数据 可以swagger识别
     *
     * @param list 加入泛型任何list
     * @author JuniorRay
     */
    protected <T> ResponsePageInfo<T> toResponsePageInfo(List<T> list) {
        return toResponsePageInfo(list, new PageInfo(list).getTotal());
    }

    protected <T> ResponsePageInfo<T> toResponsePageInfo(List<T> list, long total) {
        return toResponsePageInfo("查询成功", list, total);
    }

    protected <T> ResponsePageInfo<T> toResponsePageInfo(String msg, PageInfo<T> page) {
        ResponsePageInfo<T> responsePageInfo = new ResponsePageInfo<>(page);
        responsePageInfo.setCode(ResponseEnum.SUCCESS.getCode());
        responsePageInfo.setMsg(msg);
        return responsePageInfo;
    }

    protected <T> ResponsePageInfo<T> toResponsePageInfo(String msg, List<T> list, long total) {
        ResponsePageInfo<T> responsePageInfo = new ResponsePageInfo<>(list, Math.toIntExact(total));
        responsePageInfo.setCode(ResponseEnum.SUCCESS.getCode());
        responsePageInfo.setMsg(msg);
        return responsePageInfo;
    }

    /**
     * 响应返回结果  可以swagger识别
     *
     * @param rows 影响行数
     * @return 操作结果
     * @author JuniorRay
     */
    protected Response<Integer> toResponse(int rows) {
        return rows > 0 ? Response.success(rows) : Response.error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 获取用户缓存信息
     */
    public LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取登录用户id
     */
    public String getUserId() {
        return getLoginUser().getId();
    }

    /**
     * 获取登录部门id
     */
    public String getDeptId() {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取登录用户名
     */
    public String getUsername() {
        return getLoginUser().getUsername();
    }

}
