package cn.silver.framework.workflow.controller;

import cn.silver.framework.core.controller.DataController;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.service.IFlowTaskCommentService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Api(tags = "流程任务接口操作接口")
@RequestMapping("/flow/task")
public class FlowTaskCommentController extends DataController<IFlowTaskCommentService, FlowTaskComment> {

    public FlowTaskCommentController() {
        this.authorize = "flow:task";
        this.title = "流程工单管理";
    }
}
