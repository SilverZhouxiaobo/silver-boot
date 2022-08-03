package cn.silver.framework.system.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.TreeController;
import cn.silver.framework.core.model.DictModel;
import cn.silver.framework.system.domain.SysCategory;
import cn.silver.framework.system.service.ISysCategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date: 2019-05-29
 * @Version: V1.0
 */
@Slf4j
@RestController
@Api(tags = "配置管理-分类字典配置管理")
@RequestMapping("/sys/category")
public class SysCategoryController extends TreeController<ISysCategoryService, SysCategory> {

    public SysCategoryController() {
        this.authorize = "system:category";
        this.title = "分类字典配置管理";
    }

    @GetMapping("/dict/code/{code}")
    public Response<List<DictModel>> listDictByBaseCode(@PathVariable String code) {
        List<SysCategory> resultList = this.baseService.selectByBaseCode(code);
        List<DictModel> dictModels = resultList.stream().map(curr -> new DictModel(curr.getLabel(), curr.getValue())).collect(Collectors.toList());
        return Response.success(dictModels);
    }
}
