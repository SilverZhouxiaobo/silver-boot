package cn.silver.framework.system.controller;

import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.TreeController;
import cn.silver.framework.system.domain.SysDept;
import cn.silver.framework.system.dto.system.RoleDeptDTO;
import cn.silver.framework.system.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

/**
 * 部门信息
 *
 * @author hb
 */
@RestController
@RequestMapping("/sys/dept")
@Api(tags = {"部门管理"})
public class SysDeptController extends TreeController<ISysDeptService, SysDept> {

    /**
     * 获取部门列表
     */
//    @GetMapping("/list")
//    @ApiOperation("查询部门信息列表")
//    @PreAuthorize("@ss.hasPermi('system:dept:list')")
//    public Response<List<SysDept>> list(@ModelAttribute SysDept dept) {
//        List<SysDept> depts = deptService.selectDeptList(dept);
//        return Response.success(depts);
//    }

    /**
     * 查询部门列表（排除节点）
     */
    @GetMapping("/list/exclude/{deptId}")
    @ApiOperation("查询部门信息列表（排除节点）")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    public Response<List<SysDept>> excludeChild(@ApiParam(name = "deptId", value = "部门ID", required = true)
                                                @PathVariable(value = "deptId", required = false) String deptId) {
        List<SysDept> depts = baseService.selectDeptList(new SysDept());
        Iterator<SysDept> it = depts.iterator();
        while (it.hasNext()) {
            SysDept d = it.next();
            if (d.getId().equals(deptId) || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + "")) {
                it.remove();
            }
        }
        return Response.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/{deptId}")
    @ApiOperation("根据部门编号获取详细信息")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    public Response<SysDept> getInfo(@ApiParam(name = "deptId", value = "部门ID", required = true) @PathVariable("deptId") String deptId) {
        return Response.success(baseService.selectDeptById(deptId));
    }

//    /**
//     * 获取部门下拉树列表
//     */
//    @GetMapping("/treeselect")
//    @ApiOperation("获取部门下拉树列表")
//    public Response<List<TreeSelect>> treeselect(@ModelAttribute SysDept dept) {
//        List<SysDept> depts = baseService.selectDeptList(dept);
//        List<TreeSelect> treeSelects = baseService.buildDeptTreeSelect(depts);
//        return Response.success(baseService.buildDeptTreeSelect(depts));
//    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    @ApiOperation("加载对应角色部门列表树")
    public Response<RoleDeptDTO> roleDeptTreeselect(@ApiParam(name = "roleId", value = "角色ID", required = true) @PathVariable("roleId") String roleId) {
        List<SysDept> depts = baseService.selectDeptList(new SysDept());
        RoleDeptDTO roleDeptDTO = new RoleDeptDTO();
        roleDeptDTO.setCheckedKeys(baseService.selectDeptListByRoleId(roleId));
        roleDeptDTO.setDepts(baseService.buildDeptTreeSelect(depts));
        return Response.success(roleDeptDTO);
    }
}
