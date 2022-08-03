package cn.silver.framework.workflow.controller;

import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.workflow.domain.FlowCategory;
import cn.silver.framework.workflow.service.IFlowCategoryService;
import cn.silver.framework.workflow.service.IFlowEntryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作流分类操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Api(tags = "工作流分类操作接口")
@Slf4j
@RestController
@RequestMapping("/flow/category")
public class FlowCategoryController extends DataController<IFlowCategoryService, FlowCategory> {

    @Autowired
    private IFlowEntryService flowEntryService;

    public FlowCategoryController() {
        this.authorize = "flow:category";
        this.title = "工作流分类管理";
    }
}
