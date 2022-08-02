package cn.silver.framework.core.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.domain.TreeEntity;
import cn.silver.framework.core.model.TreeSelect;
import cn.silver.framework.core.service.ITreeService;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class TreeController<S extends ITreeService<T>, T extends TreeEntity> extends DataController<S, T> {

    @GetMapping("/root")
    @ApiOperation(value = "获取根节点", notes = "获取根节点数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'list')")
    @Log(title = "data", businessType = BusinessType.ROOT)
    public Response<List<T>> rootList() {
        return Response.success(this.baseService.selectByPid(ITreeService.ROOT_PID_VALUE));
    }

    @GetMapping("/child/{pid}")
    @ApiOperation(value = "获取根节点", notes = "获取根节点数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'list')")
    @Log(title = "data", businessType = BusinessType.CHILD)
    public Response<List<T>> getChildren(@PathVariable String pid) {
        return Response.success(this.baseService.selectByPid(pid));
    }

    @GetMapping("/tree")
    @ApiOperation(value = "获取根节点", notes = "获取根节点数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'list')")
    @Log(title = "data", businessType = BusinessType.TREE)
    public Response<List<T>> tree() {
        return Response.success(baseService.buildTree());
    }

    @GetMapping("/treeselect")
    @ApiOperation("获取下拉树列表")
    public Response<List<TreeSelect>> treeselect(@RequestParam(name = "pid", required = false) String pid) {
        return Response.success(baseService.buildTreeSelect(pid));
    }
}
