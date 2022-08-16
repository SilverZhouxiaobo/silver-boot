package cn.silver.framework.core.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.domain.FlowEntity;
import cn.silver.framework.core.model.ApproveModel;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.core.page.PageBuilder;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.core.service.IFlowService;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Administrator
 */
public class FlowController<S extends IFlowService<T>, T extends FlowEntity> extends DataController<S, T> {

    @GetMapping("/draft")
    @ApiOperation(value = "草稿箱查询", notes = "分页获取草稿箱数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query"),
    })
    @PreAuthorize("@ss.hasPermi(this.authorize,'list')")
    @Log(title = "data", businessType = BusinessType.PAGE)
    public ResponsePageInfo<T> draft(@ModelAttribute T entity) {
        PageBean page = PageBuilder.buildPageRequest(entity);
        entity.setDraftFlag(true);
        PageInfo<T> pages = this.baseService.selectPage(page, entity);
        ResponsePageInfo<T> pageInfo = toResponsePageInfo("数据查询成功,共查询到" + pages.getSize() + "条数据", pages);
        pageInfo.setTitle(this.title);
        return pageInfo;
    }

    @GetMapping("/handle")
    @ApiOperation(value = "查询收藏的数据", notes = "分页查询我收藏的数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query"),
    })
    @PreAuthorize("@ss.hasPermi(this.authorize,'list')")
    @Log(title = "data", businessType = BusinessType.PAGE)
    public ResponsePageInfo<T> getHandle(@ModelAttribute T entity) {
        PageBean page = PageBuilder.buildPageRequest(entity);
        entity.setDraftFlag(true);
        PageInfo<T> pages = this.baseService.selectHandles(page, entity);
        ResponsePageInfo<T> pageInfo = toResponsePageInfo("数据查询成功,共查询到" + pages.getSize() + "条数据", pages);
        pageInfo.setTitle(this.title);
        return pageInfo;
    }

    @GetMapping("/initcode")
    @ApiOperation(value = "数据提交审批", notes = "数据保存，并开启流程")
    @PreAuthorize("@ss.hasPermi(this.authorize,'add')")
    @Log(title = "data", businessType = BusinessType.INSERT)
    public Response<T> getCode(@ModelAttribute T entity) {
        entity = this.baseService.init(entity);
        return Response.success("", entity);
    }

    @PostMapping("submit")
    @ApiOperation(value = "数据提交审批", notes = "数据保存，并开启流程")
    @PreAuthorize("@ss.hasPermi(this.authorize,'add')")
    @Log(title = "data", businessType = BusinessType.INSERT)
    public Response<T> submit(@RequestBody T entity) {
        this.baseService.submit(entity);
        return Response.success("提交成功", entity);
    }

    @PostMapping("approve")
    @ApiOperation(value = "批量新增数据", notes = "批量新增数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'add')")
    @Log(title = "data", businessType = BusinessType.INSERT)
    public Response<T> approve(@RequestBody ApproveModel model) {
        T entity = this.baseService.approve(model);
        return Response.success("审批成功", entity);
    }

    @PostMapping("handle")
    @ApiOperation(value = "添加到我的收藏", notes = "添加到我的收藏")
    @PreAuthorize("@ss.hasPermi(this.authorize,'add')")
    @Log(title = "data", businessType = BusinessType.INSERT)
    public Response<T> handle(@RequestBody T entity) {
        this.baseService.handle(entity);
        return Response.success("提交成功", entity);
    }
}
