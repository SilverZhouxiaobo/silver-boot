package cn.silver.framework.generator.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.core.page.TableDataInfo;
import cn.silver.framework.core.text.Convert;
import cn.silver.framework.generator.domain.GenTable;
import cn.silver.framework.generator.domain.GenTableColumn;
import cn.silver.framework.generator.service.IGenTableColumnService;
import cn.silver.framework.generator.service.IGenTableService;
import cn.silver.framework.monitor.annotation.Log;
import cn.silver.framework.monitor.constant.BusinessType;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成 操作处理
 *
 * @author hb
 */
@RestController
@RequestMapping("/tool/gen")
public class GenController extends BaseController {
    @Autowired
    private IGenTableService genTableService;

    @Autowired
    private IGenTableColumnService genTableColumnService;

    /**
     * 查询代码生成列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    public ResponsePageInfo<GenTable> genList(GenTable genTable) {
        startPage();
        List<GenTable> list = genTableService.selectGenTableList(genTable);
        return toResponsePageInfo(list);
    }

    /**
     * 修改代码生成业务
     *
     * @return
     */
    @GetMapping(value = "/{tableId}")
    @PreAuthorize("@ss.hasPermi('tool:gen:query')")
    public Response<Map<String, Object>> getInfo(@PathVariable String tableId) {
        GenTable table = genTableService.selectGenTableById(tableId);
        List<GenTable> tables = genTableService.selectGenTableAll();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("info", table);
        map.put("rows", list);
        map.put("tables", tables);
        return Response.success(map);
    }

    /**
     * 查询数据库列表
     *
     * @return
     */
    @GetMapping("/db/list")
    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    public ResponsePageInfo<GenTable> dataList(GenTable genTable) {
        startPage();
        List<GenTable> list = genTableService.selectDbTableList(genTable);
        return toResponsePageInfo(list);
    }

    /**
     * 查询数据表字段列表
     */
    @GetMapping(value = "/column/{tableId}")
    @PreAuthorize("@ss.hasPermi('tool:gen:list')")
    public ResponsePageInfo<GenTableColumn> columnList(String tableId) {
        TableDataInfo dataInfo = new TableDataInfo();
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
//        dataInfo.setRows(list);
//        dataInfo.setTotal(list.size());
        return toResponsePageInfo(list);
    }

    /**
     * 导入表结构（保存）
     *
     * @return
     */
    @PostMapping("/importTable")
    @PreAuthorize("@ss.hasPermi('tool:gen:import')")
    @Log(title = "代码生成", businessType = BusinessType.IMPORT)
    public Response<Void> importTableSave(String tables) {
        String[] tableNames = Convert.toStrArray(tables);
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        genTableService.importGenTable(tableList);
        return Response.success();
    }

    /**
     * 修改保存代码生成业务
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('tool:gen:edit')")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    public Response<Object> editSave(@Validated @RequestBody GenTable genTable) {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return Response.success();
    }

    /**
     * 删除代码生成
     *
     * @return
     */
    @DeleteMapping("/{tableIds}")
    @PreAuthorize("@ss.hasPermi('tool:gen:remove')")
    @Log(title = "代码生成", businessType = BusinessType.DELETE)
    public Response<Object> remove(@PathVariable String[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return Response.success();
    }

    /**
     * 预览代码
     *
     * @return
     */
    @GetMapping("/preview/{tableId}")
    @PreAuthorize("@ss.hasPermi('tool:gen:preview')")
    public Response<Map<String, String>> preview(@PathVariable("tableId") String tableId) throws IOException {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return Response.success(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @GetMapping("/download/{tableName}")
    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @GetMapping("/genCode/{tableName}")
    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    public Response<Object> genCode(@PathVariable("tableName") String tableName) {
        genTableService.generatorCode(tableName);
        return Response.success();
    }

    /**
     * 同步数据库
     *
     * @return
     */
    @GetMapping("/synchDb/{tableName}")
    @PreAuthorize("@ss.hasPermi('tool:gen:edit')")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    public Response<Object> synchDb(@PathVariable("tableName") String tableName) {
        genTableService.synchDb(tableName);
        return Response.success();
    }

    /**
     * 批量生成代码
     */
    @GetMapping("/batchGenCode")
    @PreAuthorize("@ss.hasPermi('tool:gen:code')")
    @Log(title = "代码生成", businessType = BusinessType.GENCODE)
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException {
        String[] tableNames = Convert.toStrArray(tables);
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException {
        response.reset();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"gacim.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IOUtils.write(data, response.getOutputStream());
    }
}
