package cn.silver.framework.online.controller;


import cn.hb.software.gacim.online.domain.*;
import cn.silver.framework.online.domain.constant.DictType;
import cn.silver.framework.online.domain.constant.RelationType;
import cn.hb.software.gacim.online.service.*;
import cn.silver.framework.online.util.ContextUtil;
import cn.silver.framework.online.util.ErrorCodeEnum;
import cn.silver.framework.online.util.OnlineOperationHelper;
import cn.silver.framework.online.util.ResponseResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.silver.framework.online.domain.*;
import cn.silver.framework.online.service.*;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 在线操作接口的控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Api(tags = "在线操作接口接口")
@Slf4j
@RestController
@ConditionalOnProperty(name = "common-online.operationEnabled", havingValue = "true")
@RequestMapping("${common-online.operationUrlPrefix}/onlineOperation")
public class OnlineOperationController {

    @Autowired
    private OnlineOperationService onlineOperationService;
    @Autowired
    private OnlineDictService onlineDictService;
    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineDatasourceRelationService onlineDatasourceRelationService;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineOperationHelper onlineOperationHelper;
    @Autowired
    private OnlineVirtualColumnService onlineVirtualColumnService;

    /**
     * 新增数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表的数据源Id。
     * @param masterData             主表新增数据。
     * @param slaveData              一对多从表新增数据列表。
     * @return 应答结果。
     */
    @PostMapping("/addDatasource/{datasourceVariableName}")
    public ResponseResult<Void> addDatasource(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody String datasourceId,
            @RequestBody(required = true) JSONObject masterData,
            @RequestBody JSONObject slaveData) throws IOException {
        String errorMessage;
        // 验证数据源的合法性，同时获取主表对象。
        ResponseResult datasourceResult =
                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = (OnlineDatasource) datasourceResult.getData();
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        OnlineTable masterTable = datasource.getMasterTable();
        ResponseResult<List<ColumnData>> columnDataListResult =
                onlineOperationHelper.buildTableData(masterTable, masterData, false, null);
        if (!columnDataListResult.isSuccess()) {
            return ResponseResult.errorFrom(columnDataListResult);
        }
        if (slaveData == null) {
            onlineOperationService.saveNew(masterTable, columnDataListResult.getData());
        } else {
            ResponseResult<Map<OnlineDatasourceRelation, List<List<ColumnData>>>> slaveDataListResult =
                    onlineOperationHelper.buildSlaveDataList(datasourceId, slaveData);
            if (!slaveDataListResult.isSuccess()) {
                return ResponseResult.errorFrom(slaveDataListResult);
            }
            onlineOperationService.saveNewAndSlaveRelation(
                    masterTable, columnDataListResult.getData(), slaveDataListResult.getData());
        }
        return ResponseResult.success();
    }

    /**
     * 新增一对多从表数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表的数据源Id。
     * @param relationId             一对多的关联Id。
     * @param slaveData              一对多从表的新增数据列表。
     * @return 应答结果。
     */
    @PostMapping("/addOneToManyRelation/{datasourceVariableName}")
    public ResponseResult<Void> addOneToManyRelation(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) String relationId,
            @RequestBody(required = true) JSONObject slaveData) {
        String errorMessage;
        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
        if (datasource == null) {
            errorMessage = "数据验证失败，数据源Id并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        ResponseResult<OnlineDatasourceRelation> relationResult =
                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
        if (!relationResult.isSuccess()) {
            return ResponseResult.errorFrom(relationResult);
        }
        OnlineDatasourceRelation relation = relationResult.getData();
        OnlineTable slaveTable = relation.getSlaveTable();
        // 拆解主表和一对多关联从表的输入参数，并构建出数据表的待插入数据列表。
        ResponseResult<List<ColumnData>> columnDataListResult =
                onlineOperationHelper.buildTableData(slaveTable, slaveData, false, null);
        if (!columnDataListResult.isSuccess()) {
            return ResponseResult.errorFrom(columnDataListResult);
        }
        onlineOperationService.saveNew(slaveTable, columnDataListResult.getData());
        return ResponseResult.success();
    }

    /**
     * 更新主数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表数据源Id。
     * @param masterData             表数据。这里没有包含的字段将视为NULL。
     * @return 应该结果。
     */
    @PostMapping("/updateDatasource/{datasourceVariableName}")
    public ResponseResult<Void> updateDatasource(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) JSONObject masterData) {
        String errorMessage;
        ResponseResult<OnlineDatasource> datasourceResult =
                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        OnlineTable masterTable = datasource.getMasterTable();
        ResponseResult<List<ColumnData>> columnDataListResult =
                onlineOperationHelper.buildTableData(masterTable, masterData, true, null);
        if (!columnDataListResult.isSuccess()) {
            return ResponseResult.errorFrom(columnDataListResult);
        }
        if (!onlineOperationService.update(masterTable, columnDataListResult.getData())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 更新一对多关联数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表数据源Id。
     * @param relationId             一对多关联Id。
     * @param slaveData              一对多关联从表数据。这里没有包含的字段将视为NULL。
     * @return 应该结果。
     */
    @PostMapping("/updateOneToManyRelation/{datasourceVariableName}")
    public ResponseResult<Void> updateOneToManyRelation(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) String relationId,
            @RequestBody(required = true) JSONObject slaveData) {
        String errorMessage;
        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
        if (datasource == null) {
            errorMessage = "数据验证失败，数据源Id并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        ResponseResult<OnlineDatasourceRelation> relationResult =
                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
        if (!relationResult.isSuccess()) {
            return ResponseResult.errorFrom(relationResult);
        }
        OnlineTable slaveTable = relationResult.getData().getSlaveTable();
        ResponseResult<List<ColumnData>> columnDataListResult =
                onlineOperationHelper.buildTableData(slaveTable, slaveData, true, null);
        if (!columnDataListResult.isSuccess()) {
            return ResponseResult.errorFrom(columnDataListResult);
        }
        if (!onlineOperationService.update(slaveTable, columnDataListResult.getData())) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.success();
    }

    /**
     * 删除主数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表数据源Id。
     * @param dataId                 待删除的数据表主键Id。
     * @return 应该结果。
     */
    @PostMapping("/deleteDatasource/{datasourceVariableName}")
    public ResponseResult<Void> deleteDatasource(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) String dataId) {
        return this.doDelete(datasourceVariableName, datasourceId, CollUtil.newArrayList(dataId));
    }

    /**
     * 批量删除主数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表数据源Id。
     * @param dataIdList             待删除的数据表主键Id列表。
     * @return 应该结果。
     */
    @PostMapping("/deleteBatchDatasource/{datasourceVariableName}")
    public ResponseResult<Void> deleteBatchDatasource(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) List<String> dataIdList) {
        return this.doDelete(datasourceVariableName, datasourceId, dataIdList);
    }

    /**
     * 删除一对多关联表单条数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表数据源Id。
     * @param relationId             一对多关联Id。
     * @param dataId                 一对多关联表主键Id。
     * @return 应该结果。
     */
    @PostMapping("/deleteOneToManyRelation/{datasourceVariableName}")
    public ResponseResult<Void> deleteOneToManyRelation(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) String relationId,
            @RequestBody(required = true) String dataId) {
        return this.doDelete(datasourceVariableName, datasourceId, relationId, CollUtil.newArrayList(dataId));
    }

    /**
     * 批量删除一对多关联表单条数据接口。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           主表数据源Id。
     * @param relationId             一对多关联Id。
     * @param dataIdList             一对多关联表主键Id列表。
     * @return 应该结果。
     */
    @PostMapping("/deleteBatchOneToManyRelation/{datasourceVariableName}")
    public ResponseResult<Void> deleteBatchOneToManyRelation(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestBody(required = true) String datasourceId,
            @RequestBody(required = true) String relationId,
            @RequestBody(required = true) List<String> dataIdList) {
        return this.doDelete(datasourceVariableName, datasourceId, relationId, dataIdList);
    }

    /**
     * 根据数据源Id为动态表单查询数据详情。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           数据源Id。
     * @param dataId                 数据主键Id。
     * @return 详情结果。
     */
    @GetMapping("/viewByDatasourceId/{datasourceVariableName}")
    public ResponseResult<Map<String, Object>> viewByDatasourceId(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestParam String datasourceId,
            @RequestParam String dataId) {
        String errorMessage;
        // 验证数据源及其关联
        ResponseResult<OnlineDatasource> datasourceResult =
                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        OnlineTable masterTable = datasource.getMasterTable();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasourceId, null);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        List<OnlineDatasourceRelation> allRelationList = relationListResult.getData();
        List<OnlineDatasourceRelation> oneToOneRelationList = allRelationList.stream()
                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
        Map<String, Object> result =
                onlineOperationService.getMasterData(masterTable, oneToOneRelationList, allRelationList, dataId);
        return ResponseResult.success(result);
    }

    /**
     * 根据数据源关联Id为动态表单查询数据详情。
     *
     * @param datasourceVariableName 数据源名称。
     * @param datasourceId           数据源Id。
     * @param relationId             一对多关联Id。
     * @param dataId                 一对多关联数据主键Id。
     * @return 详情结果。
     */
    @GetMapping("/viewByOneToManyRelationId/{datasourceVariableName}")
    public ResponseResult<Map<String, Object>> viewByOneToManyRelationId(
            @PathVariable("datasourceVariableName") String datasourceVariableName,
            @RequestParam String datasourceId,
            @RequestParam String relationId,
            @RequestParam String dataId) {
        String errorMessage;
        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
        if (datasource == null) {
            errorMessage = "数据验证失败，数据源Id并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        ResponseResult<OnlineDatasourceRelation> relationResult =
                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
        if (!relationResult.isSuccess()) {
            return ResponseResult.errorFrom(relationResult);
        }
        OnlineDatasourceRelation relation = relationResult.getData();
        Map<String, Object> result = onlineOperationService.getSlaveData(relation, dataId);
        return ResponseResult.success(result);
    }

//    /**
//     * 为数据源主表字段下载文件。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param dataId                 附件所在记录的主键Id。
//     * @param fieldName              数据表字段名。
//     * @param asImage                是否为图片文件。
//     * @param response               Http 应答对象。
//     */
//    @GetMapping("/downloadDatasource/{datasourceVariableName}")
//    public void downloadDatasource(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestParam String datasourceId,
//            @RequestParam(required = false) String dataId,
//            @RequestParam String fieldName,
//            @RequestParam String filename,
//            @RequestParam Boolean asImage,
//            HttpServletResponse response) throws Exception {
//        if (MyCommonUtil.existBlankArgument(fieldName, filename, asImage)) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//        ResponseResult<OnlineDatasource> datasourceResult =
//                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
//        if (!datasourceResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(datasourceResult));
//            return;
//        }
//        OnlineDatasource datasource = datasourceResult.getData();
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
//                    ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
//            return;
//        }
//        OnlineTable masterTable = datasource.getMasterTable();
//        onlineOperationHelper.doDownload(masterTable, dataId, fieldName, filename, asImage, response);
//    }
//
//    /**
//     * 为数据源一对多关联的从表字段下载文件。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param relationId             数据源的一对多关联Id。
//     * @param dataId                 附件所在记录的主键Id。
//     * @param fieldName              数据表字段名。
//     * @param asImage                是否为图片文件。
//     * @param response               Http 应答对象。
//     */
//    @GetMapping("/downloadOneToManyRelation/{datasourceVariableName}")
//    public void downloadOneToManyRelation(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestParam String datasourceId,
//            @RequestParam String relationId,
//            @RequestParam(required = false) String dataId,
//            @RequestParam String fieldName,
//            @RequestParam String filename,
//            @RequestParam Boolean asImage,
//            HttpServletResponse response) throws Exception {
//        String errorMessage;
//        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
//        if (datasource == null) {
//            errorMessage = "数据验证失败，数据源Id并不存在！";
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
//                    ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage));
//            return;
//        }
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
//                    ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
//            return;
//        }
//        ResponseResult<OnlineDatasourceRelation> relationResult =
//                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
//        if (!relationResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(relationResult));
//            return;
//        }
//        OnlineTable slaveTable = relationResult.getData().getSlaveTable();
//        onlineOperationHelper.doDownload(slaveTable, dataId, fieldName, filename, asImage, response);
//    }
//
//    /**
//     * 为数据源主表字段上传文件。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param fieldName              数据表字段名。
//     * @param asImage                是否为图片文件。
//     * @param uploadFile             上传文件对象。
//     */
//    @PostMapping("/uploadDatasource/{datasourceVariableName}")
//    public void uploadDatasource(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestParam String datasourceId,
//            @RequestParam String fieldName,
//            @RequestParam Boolean asImage,
//            @RequestParam("uploadFile") MultipartFile uploadFile) throws Exception {
//        String errorMessage;
//        ResponseResult<OnlineDatasource> datasourceResult =
//                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
//        if (!datasourceResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(datasourceResult));
//            return;
//        }
//        OnlineDatasource datasource = datasourceResult.getData();
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
//                    ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
//            return;
//        }
//        OnlineTable masterTable = datasource.getMasterTable();
//        onlineOperationHelper.doUpload(masterTable, fieldName, asImage, uploadFile);
//    }
//
//    /**
//     * 为数据源一对多关联的从表字段上传文件。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param relationId             数据源的一对多关联Id。
//     * @param fieldName              数据表字段名。
//     * @param asImage                是否为图片文件。
//     * @param uploadFile             上传文件对象。
//     */
//    @PostMapping("/uploadOneToManyRelation/{datasourceVariableName}")
//    public void uploadOneToManyRelation(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestParam String datasourceId,
//            @RequestParam String relationId,
//            @RequestParam String fieldName,
//            @RequestParam Boolean asImage,
//            @RequestParam("uploadFile") MultipartFile uploadFile) throws Exception {
//        String errorMessage;
//        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
//        if (datasource == null) {
//            errorMessage = "数据验证失败，数据源Id并不存在！";
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
//                    ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage));
//            return;
//        }
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN,
//                    ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION));
//            return;
//        }
//        ResponseResult<OnlineDatasourceRelation> relationResult =
//                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
//        if (!relationResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(relationResult));
//            return;
//        }
//        OnlineTable slaveTable = relationResult.getData().getSlaveTable();
//        onlineOperationHelper.doUpload(slaveTable, fieldName, asImage, uploadFile);
//    }
//
//    /**
//     * 根据数据源Id，以及接口参数，为动态表单查询数据列表。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param filterDtoList          多虑数据对象列表。
//     * @param orderParam             排序对象。
//     * @param pageParam              分页对象。
//     * @return 查询结果。
//     */
//    @PostMapping("/listByDatasourceId/{datasourceVariableName}")
//    public ResponseResult listByDatasourceId(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestBody(required = true) String datasourceId,
//            @RequestBody List<OnlineFilter> filterDtoList,
//            @RequestBody MyOrderParam orderParam,
//            @RequestBody MyPageParam pageParam) {
//        String errorMessage;
//        // 1. 验证数据源及其关联
//        ResponseResult<OnlineDatasource> datasourceResult =
//                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
//        if (!datasourceResult.isSuccess()) {
//            return ResponseResult.errorFrom(datasourceResult);
//        }
//        OnlineDatasource datasource = datasourceResult.getData();
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
//        }
//        OnlineTable masterTable = datasource.getMasterTable();
//        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
//                onlineOperationHelper.verifyAndGetRelationList(datasourceId, null);
//        if (!relationListResult.isSuccess()) {
//            return ResponseResult.errorFrom(relationListResult);
//        }
//        List<OnlineDatasourceRelation> allRelationList = relationListResult.getData();
//        // 2. 验证数据过滤对象中的表名和字段，确保没有sql注入。
//        ResponseResult<Void> filterDtoListResult = this.verifyFilterDtoList(filterDtoList);
//        if (!filterDtoListResult.isSuccess()) {
//            return ResponseResult.errorFrom(filterDtoListResult);
//        }
//        // 3. 解析排序参数，同时确保没有sql注入。
//        Map<String, OnlineTable> tableMap = new HashMap<>(4);
//        tableMap.put(masterTable.getTableName(), masterTable);
//        List<OnlineDatasourceRelation> oneToOneRelationList = relationListResult.getData().stream()
//                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(oneToOneRelationList)) {
//            Map<String, OnlineTable> relationTableMap = oneToOneRelationList.stream()
//                    .map(OnlineDatasourceRelation::getSlaveTable).collect(Collectors.toMap(OnlineTable::getTableName, c -> c));
//            tableMap.putAll(relationTableMap);
//        }
//        ResponseResult<String> orderByResult = this.makeOrderBy(orderParam, masterTable, tableMap);
//        if (!orderByResult.isSuccess()) {
//            return ResponseResult.errorFrom(orderByResult);
//        }
//        String orderBy = orderByResult.getData();
//        if (pageParam != null) {
//            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
//        }
//        startPage();
//        List<Map<String, Object>> resultList = onlineOperationService.getMasterDataList(
//                masterTable, oneToOneRelationList, allRelationList, filterDtoList, orderBy);
//        return ResponseResult.success(resultList);
//    }
//
//    /**
//     * 根据数据源Id，以及接口参数，为动态表单导出数据列表。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param filterDtoList          多虑数据对象列表。
//     * @param orderParam             排序对象。
//     * @param exportInfoList         导出字段信息列表。
//     */
//    @PostMapping("/exportByDatasourceId/{datasourceVariableName}")
//    public void exportByDatasourceId(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestBody(required = true) String datasourceId,
//            @RequestBody List<OnlineFilter> filterDtoList,
//            @RequestBody MyOrderParam orderParam,
//            @RequestBody(required = true) List<ExportInfo> exportInfoList) throws IOException {
//        // 1. 验证数据源及其关联
//        ResponseResult<OnlineDatasource> datasourceResult =
//                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
//        if (!datasourceResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, datasourceResult);
//        }
//        OnlineDatasource datasource = datasourceResult.getData();
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
//        }
//        OnlineTable masterTable = datasource.getMasterTable();
//        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
//                onlineOperationHelper.verifyAndGetRelationList(datasourceId, null);
//        if (!relationListResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, relationListResult);
//        }
//        List<OnlineDatasourceRelation> allRelationList = relationListResult.getData();
//        // 2. 验证数据过滤对象中的表名和字段，确保没有sql注入。
//        ResponseResult<Void> filterDtoListResult = this.verifyFilterDtoList(filterDtoList);
//        if (!filterDtoListResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, filterDtoListResult);
//        }
//        // 3. 解析排序参数，同时确保没有sql注入。
//        Map<String, OnlineTable> tableMap = new HashMap<>(4);
//        tableMap.put(masterTable.getTableName(), masterTable);
//        List<OnlineDatasourceRelation> oneToOneRelationList = relationListResult.getData().stream()
//                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(oneToOneRelationList)) {
//            Map<String, OnlineTable> relationTableMap = oneToOneRelationList.stream()
//                    .map(OnlineDatasourceRelation::getSlaveTable).collect(Collectors.toMap(OnlineTable::getTableName, c -> c));
//            tableMap.putAll(relationTableMap);
//        }
//        ResponseResult<String> orderByResult = this.makeOrderBy(orderParam, masterTable, tableMap);
//        if (!orderByResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, orderByResult);
//        }
//        String orderBy = orderByResult.getData();
//        List<Map<String, Object>> resultList = onlineOperationService.getMasterDataList(
//                masterTable, oneToOneRelationList, allRelationList, filterDtoList, orderBy);
//        Map<String, String> headerMap = this.makeExportHeaderMap(masterTable, allRelationList, exportInfoList);
//        String filename = datasourceVariableName + "-" + MyDateUtil.toDateTimeString(DateTime.now()) + ".xlsx";
//        ExportUtil.doExport(resultList, headerMap, filename);
//    }
//
//    /**
//     * 根据数据源Id和数据源关联Id，以及接口参数，为动态表单查询该一对多关联的数据列表。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param relationId             数据源的一对多关联Id。
//     * @param filterDtoList          多虑数据对象列表。
//     * @param orderParam             排序对象。
//     * @param pageParam              分页对象。
//     * @return 查询结果。
//     */
//    @PostMapping("/listByOneToManyRelationId/{datasourceVariableName}")
//    public ResponseResult listByOneToManyRelationId(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestBody(required = true) String datasourceId,
//            @RequestBody(required = true) String relationId,
//            @RequestBody List<OnlineFilter> filterDtoList,
//            @RequestBody MyOrderParam orderParam,
//            @RequestBody MyPageParam pageParam) {
//        String errorMessage;
//        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
//        if (datasource == null) {
//            errorMessage = "数据验证失败，数据源Id并不存在！";
//            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
//        }
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
//        }
//        ResponseResult<OnlineDatasourceRelation> relationResult =
//                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
//        if (!relationResult.isSuccess()) {
//            return ResponseResult.errorFrom(relationResult);
//        }
//        OnlineDatasourceRelation relation = relationResult.getData();
//        OnlineTable slaveTable = relation.getSlaveTable();
//        // 验证数据过滤对象中的表名和字段，确保没有sql注入。
//        ResponseResult<Void> filterDtoListResult = this.verifyFilterDtoList(filterDtoList);
//        if (!filterDtoListResult.isSuccess()) {
//            return ResponseResult.errorFrom(filterDtoListResult);
//        }
//        Map<String, OnlineTable> tableMap = new HashMap<>(1);
//        tableMap.put(slaveTable.getTableName(), slaveTable);
//        if (CollUtil.isNotEmpty(orderParam)) {
//            for (MyOrderParam.OrderInfo orderInfo : orderParam) {
//                orderInfo.setFieldName(StrUtil.removePrefix(orderInfo.getFieldName(),
//                        relation.getVariableName() + OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR));
//            }
//        }
//        ResponseResult<String> orderByResult = this.makeOrderBy(orderParam, slaveTable, tableMap);
//        if (!orderByResult.isSuccess()) {
//            return ResponseResult.errorFrom(orderByResult);
//        }
//        String orderBy = orderByResult.getData();
//        // 分页。
//        if (pageParam != null) {
//            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
//        }
//        startPage();
//        List<Map<String, Object>> resultList =
//                onlineOperationService.getSlaveDataList(relation, filterDtoList, orderBy);
//            return ResponseResult.success(resultList);
//    }
//
//    /**
//     * 根据数据源Id和数据源关联Id，以及接口参数，为动态表单查询该一对多关联的数据列表。
//     *
//     * @param datasourceVariableName 数据源名称。
//     * @param datasourceId           数据源Id。
//     * @param relationId             数据源的一对多关联Id。
//     * @param filterDtoList          多虑数据对象列表。
//     * @param orderParam             排序对象。
//     * @param exportInfoList         导出字段信息列表。
//     */
//    @PostMapping("/exportByOneToManyRelationId/{datasourceVariableName}")
//    public void exportByOneToManyRelationId(
//            @PathVariable("datasourceVariableName") String datasourceVariableName,
//            @RequestBody(required = true) String datasourceId,
//            @RequestBody(required = true) String relationId,
//            @RequestBody List<OnlineFilter> filterDtoList,
//            @RequestBody MyOrderParam orderParam,
//            @RequestBody(required = true) List<ExportInfo> exportInfoList) throws IOException {
//        String errorMessage;
//        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
//        if (datasource == null) {
//            errorMessage = "数据验证失败，数据源Id并不存在！";
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST,
//                    ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage));
//        }
//        if (!datasource.getVariableName().equals(datasourceVariableName)) {
//            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN);
//        }
//        ResponseResult<OnlineDatasourceRelation> relationResult =
//                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
//        if (!relationResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, relationResult);
//        }
//        OnlineDatasourceRelation relation = relationResult.getData();
//        OnlineTable slaveTable = relation.getSlaveTable();
//        // 验证数据过滤对象中的表名和字段，确保没有sql注入。
//        ResponseResult<Void> filterDtoListResult = this.verifyFilterDtoList(filterDtoList);
//        if (!filterDtoListResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, filterDtoListResult);
//        }
//        Map<String, OnlineTable> tableMap = new HashMap<>(1);
//        tableMap.put(slaveTable.getTableName(), slaveTable);
//        if (CollUtil.isNotEmpty(orderParam)) {
//            for (MyOrderParam.OrderInfo orderInfo : orderParam) {
//                orderInfo.setFieldName(StrUtil.removePrefix(orderInfo.getFieldName(),
//                        relation.getVariableName() + OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR));
//            }
//        }
//        ResponseResult<String> orderByResult = this.makeOrderBy(orderParam, slaveTable, tableMap);
//        if (!orderByResult.isSuccess()) {
//            ResponseResult.output(HttpServletResponse.SC_BAD_REQUEST, orderByResult);
//        }
//        String orderBy = orderByResult.getData();
//        List<Map<String, Object>> resultList =
//                onlineOperationService.getSlaveDataList(relation, filterDtoList, orderBy);
//        Map<String, String> headerMap = this.makeExportHeaderMap(
//                null, CollUtil.newArrayList(relation), exportInfoList);
//        String filename = datasourceVariableName + "-relation-"
//                + MyDateUtil.toDateTimeString(DateTime.now()) + ".xlsx";
//        ExportUtil.doExport(resultList, headerMap, filename);
//    }
//

    /**
     * 查询字典数据，并以字典的约定方式，返回数据结果集。
     *
     * @param dictId        字典Id。
     * @param filterDtoList 字典的过滤对象列表。
     * @return 字典数据列表。
     */
    @PostMapping("/listDict")
    public ResponseResult<List<Map<String, Object>>> listDict(
            @RequestBody(required = true) String dictId,
            @RequestBody List<OnlineFilter> filterDtoList) {
        String errorMessage;
        OnlineDict dict = onlineDictService.selectById(dictId);
        if (dict == null) {
            errorMessage = "数据验证失败，字典Id并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!dict.getDictType().equals(DictType.TABLE)) {
            errorMessage = "数据验证失败，该接口仅支持数据表字典！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (CollUtil.isNotEmpty(filterDtoList)) {
            for (OnlineFilter filter : filterDtoList) {
                if (!this.checkTableAndColumnName(filter.getColumnName())) {
                    errorMessage = "数据验证失败，过滤字段名 ["
                            + filter.getColumnName() + " ] 包含 (数字、字母和下划线) 之外的非法字符！";
                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
                }
            }
        }
        List<Map<String, Object>> resultList = onlineOperationService.getDictDataList(dict, filterDtoList);
        return ResponseResult.success(resultList);
    }

    private ResponseResult<Void> doDelete(
            String datasourceVariableName, String datasourceId, List<String> dataIdList) {
        String errorMessage;
        ResponseResult<OnlineDatasource> datasourceResult =
                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        OnlineTable masterTable = datasource.getMasterTable();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasourceId, RelationType.ONE_TO_MANY);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        List<OnlineDatasourceRelation> relationList = relationListResult.getData();
        for (String dataId : dataIdList) {
            if (!onlineOperationService.delete(masterTable, relationList, dataId)) {
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> doDelete(
            String datasourceVariableName, String datasourceId, String relationId, List<String> dataIdList) {
        String errorMessage;
        OnlineDatasource datasource = onlineDatasourceService.selectById(datasourceId);
        if (datasource == null) {
            errorMessage = "数据验证失败，数据源Id并不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!datasource.getVariableName().equals(datasourceVariableName)) {
            ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseResult.error(ErrorCodeEnum.NO_OPERATION_PERMISSION);
        }
        ResponseResult<OnlineDatasourceRelation> relationResult =
                onlineOperationHelper.verifyAndGetOneToManyRelation(datasourceId, relationId);
        if (!relationResult.isSuccess()) {
            return ResponseResult.errorFrom(relationResult);
        }
        OnlineDatasourceRelation relation = relationResult.getData();
        for (String dataId : dataIdList) {
            if (!onlineOperationService.delete(relation.getSlaveTable(), null, dataId)) {
                return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> verifyFilterDtoList(List<OnlineFilter> filterDtoList) {
        if (CollUtil.isEmpty(filterDtoList)) {
            return ResponseResult.success();
        }
        String errorMessage;
        for (OnlineFilter filter : filterDtoList) {
            if (!this.checkTableAndColumnName(filter.getTableName())) {
                errorMessage = "数据验证失败，过滤表名 ["
                        + filter.getColumnName() + " ] 包含 (数字、字母和下划线) 之外的非法字符！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            if (!this.checkTableAndColumnName(filter.getColumnName())) {
                errorMessage = "数据验证失败，过滤字段名 ["
                        + filter.getColumnName() + " ] 包含 (数字、字母和下划线) 之外的非法字符！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
        }
        return ResponseResult.success();
    }

    private boolean checkTableAndColumnName(String name) {
        if (StrUtil.isBlank(name)) {
            return true;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!CharUtil.isLetterOrNumber(c) && !CharUtil.equals('_', c, false)) {
                return false;
            }
        }
        return true;
    }
//
//    private ResponseResult<String> makeOrderBy(
//            MyOrderParam orderParam, OnlineTable masterTable, Map<String, OnlineTable> tableMap) {
//        if (CollUtil.isEmpty(orderParam)) {
//            return ResponseResult.success(null);
//        }
//        String errorMessage;
//        StringBuilder sb = new StringBuilder(128);
//        for (int i = 0; i < orderParam.size(); i++) {
//            MyOrderParam.OrderInfo orderInfo = orderParam.get(i);
//            boolean found = false;
//            String[] orderArray = StrUtil.splitToArray(orderInfo.getFieldName(), '.');
//            // 如果没有前缀，我们就可以默认为主表的字段。
//            if (orderArray.length == 1) {
//                for (OnlineColumn column : masterTable.getColumnMap().values()) {
//                    if (column.getColumnName().equals(orderArray[0])) {
//                        sb.append(masterTable.getTableName()).append(".").append(column.getColumnName());
//                        if (!orderInfo.getAsc()) {
//                            sb.append(" DESC");
//                        }
//                        if (i != orderParam.size() - 1) {
//                            sb.append(", ");
//                        }
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) {
//                    errorMessage = "数据验证失败，排序字段 ["
//                            + orderInfo.getFieldName() + "] 在主表 [" + masterTable.getTableName() + "] 中并不存在！";
//                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
//                }
//            } else {
//                String tableName = orderArray[0];
//                String columnName = orderArray[1];
//                OnlineTable table = tableMap.get(tableName);
//                if (table == null) {
//                    errorMessage = "数据验证失败，排序字段 ["
//                            + orderInfo.getFieldName() + "] 的数据表 [" + tableName + "] 并不属于当前数据源！";
//                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
//                }
//                for (OnlineColumn column : table.getColumnMap().values()) {
//                    if (column.getColumnName().equals(columnName)) {
//                        sb.append(tableName).append(".").append(columnName);
//                        if (!orderInfo.getAsc()) {
//                            sb.append(" DESC");
//                        }
//                        if (i != orderParam.size() - 1) {
//                            sb.append(", ");
//                        }
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) {
//                    errorMessage = "数据验证失败，排序字段 ["
//                            + orderInfo.getFieldName() + "] 在数据表 [" + tableName + "] 中并不存在！";
//                    return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
//                }
//            }
//        }
//        return ResponseResult.success(sb.toString());
//    }
//
//    private Map<String, String> makeExportHeaderMap(
//            OnlineTable masterTable,
//            List<OnlineDatasourceRelation> allRelationList,
//            List<ExportInfo> exportInfoList) {
//        Map<String, String> headerMap = new LinkedHashMap<>(16);
//        Map<Long, OnlineDatasourceRelation> allRelationMap = null;
////        if (CollUtil.isNotEmpty(allRelationList)) {
////            allRelationMap = allRelationList.stream()
////                    .collect(Collectors.toMap(OnlineDatasourceRelation::getSlaveTableId, r -> r));
////        }
//        for (ExportInfo exportInfo : exportInfoList) {
//            if (masterTable != null && exportInfo.getTableId().equals(masterTable.getTableId())) {
//                if (exportInfo.getVirtualColumnId() != null) {
//                    OnlineVirtualColumn virtualColumn =
//                            onlineVirtualColumnService.selectById(exportInfo.getVirtualColumnId());
//                    if (virtualColumn != null) {
//                        headerMap.put(virtualColumn.getObjectFieldName(), exportInfo.showName);
//                    }
//                } else {
//                    OnlineColumn column = masterTable.getColumnMap().get(exportInfo.getColumnId());
//                    if (column.getDictId() != null) {
//                        headerMap.put(column.getColumnName() + "__DictMap.name", exportInfo.getShowName());
//                    } else {
//                        headerMap.put(column.getColumnName(), exportInfo.getShowName());
//                    }
//                }
//            } else {
//                if (MapUtil.isEmpty(allRelationMap)) {
//                    continue;
//                }
//                OnlineDatasourceRelation relation = allRelationMap.get(exportInfo.getTableId());
//                if (relation != null) {
//                    OnlineColumn column = relation.getSlaveTable().getColumnMap().get(exportInfo.getColumnId());
//                    String columnName = relation.getVariableName()
//                            + OnlineConstant.RELATION_TABLE_COLUMN_SEPARATOR + column.getColumnName();
//                    if (column.getDictId() != null) {
//                        columnName = columnName + "__DictMap.name";
//                    }
//                    headerMap.put(columnName, exportInfo.getShowName());
//                }
//            }
//        }
//        return headerMap;
//    }
//
//    @Data
//    public static class ExportInfo {
//        private String tableId;
//        private String columnId;
//        private String virtualColumnId;
//        private String showName;
//    }
}
