package cn.silver.framework.core.controller;

import cn.silver.framework.common.annotation.RepeatSubmit;
import cn.silver.framework.common.utils.poi.ExcelUtil;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.model.DictModel;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.core.page.PageBuilder;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
public class DataController<S extends IBaseService<T>, T extends BaseEntity> extends BaseController {

    public String authorize = "data:business";
    public String title = "通用数据接口";

    @Autowired
    protected S baseService;

    @GetMapping("/all")
    @ApiOperation(value = "获取全部数据", notes = "获取全部数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'all')")
    @Log(title = "data", businessType = BusinessType.ALL)
    public Response<List<T>> all(@ModelAttribute T entity) {
        List<T> list = baseService.selectList(entity);
        return Response.success(list);
    }

    @GetMapping("/list")
    @ApiOperation(value = "分页列表查询", notes = "分页获取数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量", dataType = "int", paramType = "query"),
    })
    @PreAuthorize("@ss.hasPermi(this.authorize,'list')")
    @Log(title = "data", businessType = BusinessType.PAGE)
    public ResponsePageInfo<T> list(@ModelAttribute T entity) {
        PageBean page = PageBuilder.buildPageRequest(entity);
        PageInfo<T> pages = this.baseService.selectPage(page, entity);
        ResponsePageInfo<T> pageInfo = toResponsePageInfo("数据查询成功,共查询到" + pages.getSize() + "条数据", pages);
        pageInfo.setTitle(this.title);
        return pageInfo;
    }

    /**
     * 以字典形式返回全部FlowCategory数据集合。字典的键值为[categoryId, name]。
     * 白名单接口，登录用户均可访问。
     *
     * @param entity 过滤对象。
     *
     * @return 应答结果对象，包含的数据为 List<Map<String, String>>，map中包含两条记录，key的值分别是id和name，value对应具体数据。
     */
    @GetMapping("/dict")
    public Response<List<DictModel>> listDict(@ModelAttribute T entity) {
        List<T> resultList = this.baseService.selectList(entity);
        List<DictModel> dictModels = resultList.stream().map(curr -> new DictModel(curr.getId(), curr.getLabel(), curr.getValue(), curr.getGroup(), curr.DictRemark())).collect(Collectors.toList());
        return Response.success(dictModels);
    }

    @GetMapping("/dict/{ids}")
    public Response<List<DictModel>> listDictById(@PathVariable String[] ids, @ModelAttribute T entity) {
        List<T> resultList = this.baseService.selectByValues(Arrays.asList(ids));
        List<DictModel> dictModels = resultList.stream().map(curr -> new DictModel(curr.getLabel(), curr.getValue())).collect(Collectors.toList());
        return Response.success(dictModels);
    }

    /**
     * 获取客户信息详细信息
     *
     * @return
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "精确查询", notes = "根据id查询数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'query')")
    @Log(title = "data", businessType = BusinessType.DETAIL)
    public Response<T> getInfo(@PathVariable("id") String id) {
        T entity = baseService.selectById(id);
        Response<T> response = Response.success("查询成功", entity);
        response.setTitle(this.title);
        return response;
    }

    @GetMapping("/exists")
    @ApiOperation(value = "数据重复性校验", notes = "数据重复性校验")
    @PreAuthorize("@ss.hasPermi(this.authorize,'query')")
    @Log(title = "data", businessType = BusinessType.EXISTS)
    public Response<Void> exists(@ModelAttribute T entity) {
        if (entity.checkExists()) {
            List<T> exists = this.baseService.selectExists(entity);
            if (CollectionUtils.isNotEmpty(exists)) {
                return Response.error(ResponseEnum.DATA_ERROR_EXIST);
            }
        }
        return Response.success("数据可用", null);
    }

    /**
     * 新增客户信息
     */
    @PostMapping
    @RepeatSubmit
    @PreAuthorize("@ss.hasPermi(this.authorize,'add')")
    @ApiOperation(value = "新增数据", notes = "新增一条新数据")
    @Log(title = "data", businessType = BusinessType.INSERT)
    public Response<T> add(@Validated @RequestBody T entity) {
        baseService.insert(entity);
        Response<T> response = Response.success("保存成功", entity);
        response.setTitle(this.title);
        return response;
    }

    /**
     * 修改客户信息
     */
    @PutMapping
    @RepeatSubmit
    @PreAuthorize("@ss.hasPermi(this.authorize,'edit')")
    @ApiOperation(value = "修改数据", notes = "根据Id修改一条数据")
    @Log(title = "data", businessType = BusinessType.UPDATE)
    public Response<T> edit(@Validated @RequestBody T entity) {
        baseService.update(entity);
        Response<T> response = Response.success("修改成功", entity);
        response.setTitle(this.title);
        return response;
    }

    @RepeatSubmit
    @PostMapping("save")
    @ApiOperation(value = "保存数据", notes = "保存数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'add')")
    @Log(title = "data", businessType = BusinessType.INSERT)
    public Response<T> save(@Validated @RequestBody T entity) {
        this.baseService.insertOrUpdate(entity);
        Response<T> response = Response.success("保存成功", entity);
        response.setTitle(this.title);
        return response;
    }

    /**
     * 删除客户信息
     */
    @RepeatSubmit
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除数据", notes = "根据Id删除一条数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'delete')")
    @Log(title = "data", businessType = BusinessType.DELETE)
    public Response<Void> remove(@PathVariable String id) {
        int success = this.baseService.delete(id);
        Response<Void> response = Response.success("数据删除成功,共删除" + success + "条数据", null);
        response.setTitle(this.title);
        return response;
    }

    @RepeatSubmit
    @DeleteMapping("/batch/{ids}")
    @ApiOperation(value = "删除数据", notes = "根据Id删除一条数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'delete')")
    @Log(title = "data", businessType = BusinessType.DELETE)
    public Response<Void> removeBatch(@PathVariable String[] ids) {
        int success = this.baseService.deleteBatch(Arrays.asList(ids));
        Response<Void> response = Response.success("数据删除成功,共删除" + success + "条数据", null);
        response.setTitle(this.title);
        return response;
    }

    /**
     * 导出数据
     */
    @RepeatSubmit
    @PostMapping("/exportData")
    @ApiOperation("导出数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'export')")
    @Log(title = "data", businessType = BusinessType.EXPORT)
    public Response<Void> exportData(HttpServletResponse response, @ModelAttribute T entity) {
        List<T> list;
        if (CollectionUtils.isNotEmpty(entity.getIds())) {
            List<String> ids = entity.getIds();
            list = baseService.selectByIds(ids);
        } else {
            list = baseService.selectList(entity);
        }
        ExcelUtil<T> util = new ExcelUtil(entity.getClass());
        util.exportExcel(list, "导出数据", response);
        Response<Void> response1 = Response.success("数据导出成功", null);
        response1.setTitle(this.title);
        return response1;
    }

    @PostMapping("/importTemplate")
    @ApiOperation("获取数据导入模板")
    @PreAuthorize("@ss.hasPermi(this.authorize,'import')")
    @Log(title = "data", businessType = BusinessType.IMPORT_TEMPLATE)
    public Response<Void> importTemplate(HttpServletResponse response, @ModelAttribute T entity) {
        ExcelUtil<T> util = new ExcelUtil(entity.getClass());
        util.importTemplate("数据导入模板", response);
        Response<Void> response1 = Response.success("获取模板成功", null);
        response1.setTitle(this.title);
        return response1;
    }

    @RepeatSubmit
    @PostMapping("/importData")
    @ApiOperation("导入数据")
    @PreAuthorize("@ss.hasPermi(this.authorize,'import')")
    public Response<Void> importData(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "updateSupport") boolean updateSupport, @ModelAttribute T entity) throws Exception {
        ExcelUtil<T> util = new ExcelUtil(entity.getClass());
        List<T> list = util.importExcel(file.getInputStream());
        int success = 0;
        for (int i = 0; i < list.size(); i++) {
            success += this.baseService.save(list.get(i), updateSupport);
//            if (entity.checkExists() && updateSupport) {
//                success += this.baseService.insertOrUpdate(list.get(i));
//            } else {
//                success += this.baseService.insert(list.get(i));
//            }
        }
        Response<Void> response = Response.success("导入成功,共导入" + success + "条数据", null);
        response.setTitle(this.title);
        return response;
    }

//    /**
//     * 条件选择ids导出数据
//     * @param
//     * @return
//     */
//    @PostMapping("/exportSelData")
//    @PreAuthorize("@ss.hasPermi(this.authorize,'export')")
//    @Log(title = "data", businessType = BusinessType.EXPORT)
//    public OutputStream exportSelData(HttpServletResponse response, T entity){
//        Class<T> clazz = (Class<T>) entity.getClass();
//        entity.getIds();
//        return null;
//    }
}
