package cn.silver.framework.web.controller.common;

import cn.silver.framework.common.utils.QRCodeManageUtil;
import cn.silver.framework.core.api.ISysBaseApi;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.constant.BaseContant;
import cn.silver.framework.core.constant.FileType;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.core.model.DictModel;
import cn.silver.framework.core.model.QrCodeModel;
import cn.silver.framework.flow.constant.FlowComment;
import cn.silver.framework.message.constant.MessageStatus;
import cn.silver.framework.message.constant.MessageType;
import cn.silver.framework.monitor.constant.BusinessStatus;
import cn.silver.framework.monitor.constant.BusinessType;
import cn.silver.framework.monitor.constant.OperatorType;
import cn.silver.framework.system.domain.SysCategory;
import cn.silver.framework.system.service.ISysCategoryService;
import cn.silver.framework.system.service.ISysConfigService;
import cn.silver.framework.workflow.constant.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@Api("通用数据接口")
@RequestMapping("/common")
public class CommonController extends BaseController {

    private static final HashMap<String, BaseContant[]> enumMap = new HashMap<>();

    static {
        enumMap.put("business-type", BusinessType.values());
        enumMap.put("business-status", BusinessStatus.values());
        enumMap.put("operator-type", OperatorType.values());
        enumMap.put("search-type", SearchType.values());
        enumMap.put("stock-data-type", StockDataType.values());
        enumMap.put("flow-comment", FlowComment.values());
        enumMap.put("file-type", FileType.values());
        enumMap.put("flow-approval-type", FlowApprovalType.values());
        enumMap.put("flow-message-operation-type", FlowMessageOperationType.values());
        enumMap.put("flow-message-type", FlowMessageType.values());
        enumMap.put("flow-publish-status", FlowPublishStatus.values());
        enumMap.put("flow-task-status", FlowTaskStatus.values());
        enumMap.put("flow-task-type", FlowTaskType.values());
        enumMap.put("flow-variable-type", FlowVariableType.values());
        enumMap.put("flow-copy-for-type", FlowCopyForType.values());
        enumMap.put("flow-work-order-type", FlowWorkOrderType.values());
        enumMap.put("message-status", MessageStatus.values());
        enumMap.put("message-type", MessageType.values());
        enumMap.put("apply-config", SignUpConfig.values());
    }

    @Autowired
    private ISysBaseApi baseAPI;
    @Autowired
    private ISysCategoryService categoryService;
    @Autowired
    private ISysConfigService configService;

    @GetMapping("/dict/{type}")
    @ApiOperation(value = "查询字典数据", notes = "根据字典类型查询字典数据")
    public Response<List<DictModel>> getDicts(@ApiParam(value = "type", required = true) @PathVariable String type) {
        return Response.success(this.baseAPI.selectDictByType(type));
    }

    @GetMapping("/category/{baseCode}")
    @ApiOperation("获取配置映射")
    public Response<Map<String, String>> getCategories(@PathVariable String baseCode) {
        List<SysCategory> sysCategories = this.categoryService.selectByBaseCode(baseCode);
        Map<String, String> dictModels = sysCategories.stream().collect(Collectors.toMap(SysCategory::getCode, SysCategory::getName));
        return Response.success(dictModels);
    }

    @GetMapping("/config/{key}")
    @ApiOperation("获取配置映射")
    public Response<String> getConfig(@ApiParam(name = "key", value = "参数键名", required = true) @PathVariable("key") String configKey) {
        String result = this.configService.selectConfigByKey(configKey);
        return Response.success("", result);
    }

    @GetMapping("/static/{code}")
    @ApiOperation(value = "查询枚举数据", notes = "根据枚举编码查询枚举数据")
    public Response<List<DictModel>> getStaticData(@ApiParam(value = "code", required = true) @PathVariable String code) {
        BaseContant[] contants = enumMap.get(code);
        List<DictModel> models = Arrays.stream(contants).map(contant -> new DictModel(contant.getName(), contant.getCode())).collect(Collectors.toList());
        return Response.success(models);
    }

    @GetMapping("/qrCode")
    public void getQRCode(@RequestParam(name = "code", required = false) String code, @RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "url") String url, @RequestParam(name = "contant", required = false) String content, HttpServletResponse response) {
        log.info("codeContent=" + code);
        try {
            /*
             * 调用工具类生成二维码并输出到输出流中
             */
            QRCodeManageUtil.createCodeToOutputStream(new QrCodeModel(code, name, URLDecoder.decode(url, "UTF-8"), content), response.getOutputStream());
            log.info("成功生成二维码!");
        } catch (IOException e) {
            log.error("发生错误， 错误信息是：{}！", e.getMessage());
        }
    }
}
