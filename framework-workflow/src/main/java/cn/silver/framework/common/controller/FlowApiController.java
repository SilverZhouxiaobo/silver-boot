package cn.silver.framework.common.controller;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.flow.service.IFlowTaskService;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Api(tags = {"Api管理-流程管理"})
@RequestMapping("/api/flow")
public class FlowApiController extends BaseController {

    @Autowired
    private IFlowWorkOrderService orderService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IFlowTaskService taskService;

    /**
     * 获取流程工单所有的流程任务名称
     *
     * @return
     */
    @GetMapping("/processor/{bussinessKey}")
    @ApiOperation(value = "获取下一级审批人", notes = "根据任务编号获取下一级审批人")
    public Response<List<JSONObject>> getProcessor(@PathVariable("bussinessKey") String businessKey) {
        FlowWorkOrder workOrder = this.orderService.selectByBusinessKey(businessKey);
        if (ObjectUtils.isEmpty(workOrder)) {
            throw new CustomException("工单数据不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (StringUtils.isBlank(workOrder.getProcessDefinitionId()) && StringUtils.isBlank(workOrder.getTaskDefinitionKey())) {
            throw new CustomException("关联的任务流程信息不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(workOrder.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process mainProcess = bpmnModel.getMainProcess();
        List<UserTask> userTasks = mainProcess.findFlowElementsOfType(UserTask.class);
        if (CollectionUtils.isNotEmpty(userTasks)) {
            UserTask current = userTasks.stream().filter(task -> workOrder.getTaskDefinitionKey().equals(task.getId())).collect(Collectors.toList()).get(0);
            List<String> targetKeys = current.getOutgoingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
            List<UserTask> targetTasks = userTasks.stream().filter(task -> targetKeys.contains(task.getId())).collect(Collectors.toList());
            targetTasks.stream().forEach(task -> log.info("task:" + task));
        }
        return Response.success();
    }

    @ApiOperation(value = "获取所有可回退的节点")
    @PostMapping(value = "/returnList/{businessKey}")
    public Response<List<UserTask>> findReturnTaskList(@PathVariable String businessKey) {
        FlowWorkOrder workOrder = this.orderService.selectByBusinessKey(businessKey);
        if (ObjectUtils.isEmpty(workOrder)) {
            throw new CustomException("工单数据不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        return Response.success(taskService.findReturnTaskList(workOrder.getTaskId()));
    }
}
