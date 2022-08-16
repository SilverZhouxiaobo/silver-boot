package cn.silver.framework.config.controller;

import cn.silver.framework.config.domain.SysDictData;
import cn.silver.framework.config.service.ISysDictDataService;
import cn.silver.framework.config.service.ISysDictTypeService;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.DataController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据字典信息
 *
 * @author hb
 */
@RestController
@RequestMapping("/sys/dict/data")
@Api(tags = {"字典管理-字典数据管理"})
public class SysDictDataController extends DataController<ISysDictDataService, SysDictData> {
    @Autowired
    private ISysDictTypeService dictTypeService;

//    @GetMapping("/list")
//    @ApiOperation("查询数据字典信息列表")
//    @PreAuthorize("@ss.hasPermi('system:dict:list')")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query", required = false),
//            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query", required = false),
//    })
//    public ResponsePageInfo<SysDictData> list(@ModelAttribute SysDictData dictData) {
//        startPage();
//        List<SysDictData> list = baseService.selectDictDataList(dictData);
//        return toResponsePageInfo(list);
//    }

//    @PostMapping("/exportData")
//    @ApiOperation("导出数据字典信息列表Excel")
//    @PreAuthorize("@ss.hasPermi('system:dict:export')")
//    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
//    public void export(HttpServletResponse response, @ModelAttribute SysDictData dictData) {
//        List<SysDictData> list = baseService.selectDictDataList(dictData);
//        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
//        util.exportExcel(list, "字典数据", response);
//    }

    /**
     * 查询字典数据详细
     */
    @GetMapping(value = "/{dictCode}")
    @ApiOperation("查询字典数据详细")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    public Response<SysDictData> getInfo(@ApiParam(name = "dictCode", value = "字典编号", required = true) @PathVariable("dictCode") String dictCode) {
        return Response.success(baseService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    @ApiOperation("根据字典类型查询字典数据信息")
    public Response<List<SysDictData>> dictType(
            @ApiParam(name = "dictType", value = "根据字典类型查询字典数据信息", required = true)
            @PathVariable("dictType") String dictType
    ) {
        return Response.success(dictTypeService.selectDictDataByType(dictType));
    }

//    /**
//     * 新增字典数据
//     */
//    @PostMapping
//    @ApiOperation("新增字典数据")
//    @PreAuthorize("@ss.hasPermi('system:dict:add')")
//    @Log(title = "字典数据", businessType = BusinessType.INSERT)
//    public Response<Integer> add(@Validated @RequestBody SysDictData dict) {
//        dict.setCreateBy(SecurityUtils.getUsername());
//        return toResponse(baseService.insertDictData(dict));
//    }
//
//    /**
//     * 修改保存字典数据
//     */
//    @PutMapping
//    @ApiOperation("修改保存字典数据")
//    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
//    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
//    public Response<Integer> edit(@Validated @RequestBody SysDictData dict) {
//        dict.setUpdateBy(SecurityUtils.getUsername());
//        return toResponse(baseService.updateDictData(dict));
//    }

//    /**
//     * 删除字典数据
//     */
//    @DeleteMapping("/{dictCodes}")
//    @ApiOperation("删除字典数据")
//    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
//    @Log(title = "字典数据", businessType = BusinessType.DELETE)
//    public Response<Integer> remove(
//            @ApiParam(name = "dictCodes", value = "字典编号codes{逗号分隔}", required = true)
//            @PathVariable String[] dictCodes
//    ) {
//        return toResponse(baseService.deleteDictDataByIds(dictCodes));
//    }
}
