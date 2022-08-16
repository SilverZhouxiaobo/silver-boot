package cn.silver.framework.config.controller;

import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.config.constant.UserConstants;
import cn.silver.framework.config.domain.SysDictType;
import cn.silver.framework.config.service.ISysDictTypeService;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.security.util.SecurityUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author hb
 */
@RestController
@RequestMapping("/sys/dict/type")
@Api(tags = {"【数据字典类型信息】"})
public class SysDictTypeController extends BaseController {
    @Autowired
    private ISysDictTypeService dictTypeService;

    @GetMapping("/list")
    @ApiOperation("查询数据字典类型信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
    })
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    public ResponsePageInfo<SysDictType> list(@ModelAttribute SysDictType dictType) {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return toResponsePageInfo(list);
    }

    @PostMapping("/exportData")
    @ApiOperation("导出数据字典类型信息列表Excel")
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    public void export(HttpServletResponse response, @ModelAttribute SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(list, "字典类型", response);
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping(value = "/{dictId}")
    @ApiOperation("查询字典类型详细")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    public Response<SysDictType> getInfo(@ApiParam(name = "dictId", value = "字典ID", required = true) @PathVariable("dictId") String dictId) {
        return Response.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    @ApiOperation("新增字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    public Response<Integer> add(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return Response.error(ResponseEnum.DICTIONARY_ADD_ERROR_EXIST);
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toResponse(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    @ApiOperation("修改字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    public Response<Integer> edit(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return Response.error(ResponseEnum.DICTIONARY_UPDATE_ERROR_EXIST);
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toResponse(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    @ApiOperation("删除字典类型")
    public Response<Integer> remove(@ApiParam(name = "dictIds", value = "字典Ids{逗号分隔}", required = true) @PathVariable String[] dictIds) {
        return toResponse(dictTypeService.deleteDictTypeByIds(dictIds));
    }

    /**
     * 清空缓存
     */
    @DeleteMapping("/clearCache")
    @ApiOperation("清空缓存")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    public Response clearCache() {
        dictTypeService.clearCache();
        return Response.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    @ApiOperation("获取字典选择框列表")
    public Response<List<SysDictType>> optionselect() {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return Response.success(dictTypes);
    }
}
