package cn.silver.framework.workflow.controller;

import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.workflow.domain.FlowEntryVariable;
import cn.silver.framework.workflow.service.IFlowEntryVariableService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程变量操作控制器类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestController
@Api(tags = "工作流变量操作接口")
@RequestMapping("/flow/entry/variable")
public class FlowEntryVariableController extends DataController<IFlowEntryVariableService, FlowEntryVariable> {
    public FlowEntryVariableController() {
        this.authorize = "flow:entry:variable";
        this.title = "工作流变量管理";
    }
}
