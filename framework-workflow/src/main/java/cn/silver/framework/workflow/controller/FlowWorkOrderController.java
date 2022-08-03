package cn.silver.framework.workflow.controller;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.FlowController;
import cn.silver.framework.core.model.DictModel;
import cn.silver.framework.flow.service.IFlowInstanceService;
import cn.silver.framework.flow.service.IFlowTaskService;
import cn.silver.framework.workflow.constant.FlowTaskStatus;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.service.IFlowMessageService;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@Api(tags = "流程工单管理接口")
@RequestMapping("/flow/order")
public class FlowWorkOrderController extends FlowController<IFlowWorkOrderService, FlowWorkOrder> {
    @Autowired
    private IFlowInstanceService instanceService;
    @Autowired
    private IFlowMessageService messageService;
    @Autowired
    private IFlowTaskService taskService;

    public FlowWorkOrderController() {
        this.authorize = "flow:order";
        this.title = "流程工单管理";
    }

    @GetMapping("/processor/{bussinessKey}")
    @ApiOperation(value = "获取下一级审批人", notes = "根据任务编号获取下一级审批人")
    public Response<List<JSONObject>> getProcessor(@PathVariable("bussinessKey") String businessKey) {
        FlowWorkOrder workOrder = this.baseService.selectByBusinessKey(businessKey);
        if (ObjectUtils.isEmpty(workOrder)) {
            throw new CustomException("工单数据不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (StringUtils.isBlank(workOrder.getProcessDefinitionId()) && StringUtils.isBlank(workOrder.getTaskDefinitionKey())) {
            throw new CustomException("关联的任务流程信息不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(workOrder.getProcessDefinitionId()).singleResult();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
//        Process mainProcess = bpmnModel.getMainProcess();
//        List<UserTask> userTasks = mainProcess.findFlowElementsOfType(UserTask.class);
//        if (CollectionUtils.isNotEmpty(userTasks)) {
//            UserTask current = userTasks.stream().filter(task -> workOrder.getTaskDefinitionKey().equals(task.getId())).collect(Collectors.toList()).get(0);
//            List<String> targetKeys = current.getOutgoingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
//            List<UserTask> targetTasks = userTasks.stream().filter(task -> targetKeys.contains(task.getId())).collect(Collectors.toList());
//            targetTasks.stream().forEach(task -> log.info("task:" + task));
//        }
        return Response.success();
    }

    @ApiOperation(value = "获取所有可回退的节点")
    @GetMapping(value = "/roads/{businessKey}")
    public Response<List<DictModel>> findReturnTaskList(@PathVariable String businessKey) {
        FlowWorkOrder workOrder = this.baseService.selectByBusinessKey(businessKey);
        if (ObjectUtils.isEmpty(workOrder)) {
            throw new CustomException("工单数据不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        List<UserTask> userTasks = taskService.findReturnTaskList(workOrder.getTaskId());
        return Response.success(userTasks.stream().map(task -> new DictModel(task.getName(), task.getId())).collect(Collectors.toList()));
    }

    @GetMapping("check/{businessKey}")
    public Response<Boolean> checkPermission(@PathVariable String businessKey, @RequestParam(name = "status", required = false) String status) {
        FlowWorkOrder workOrder = this.baseService.selectByBusinessKey(businessKey);
        if (ObjectUtils.isEmpty(workOrder)) {
            throw new CustomException("任务数据不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (StringUtils.isBlank(workOrder.getTaskId()) && !workOrder.getStatus().equals("finished")) {
            throw new CustomException("任务信息不存在", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (StringUtils.isNotBlank(status) && !status.equalsIgnoreCase(workOrder.getTaskDefinitionKey())) {
            return Response.success(false);
        }
        return Response.success(this.taskService.checkPermission(workOrder.getTaskId()));

    }

    @PostMapping("/cancel/{id}")
    public Response<Void> cancelWorkOrder(@PathVariable String id, @RequestBody String cancelReason) {
        FlowWorkOrder flowWorkOrder = baseService.selectById(id);
        if (flowWorkOrder == null) {
            return Response.error(ResponseEnum.DATA_ERROR_NOT_FOUND);
        }
        String errorMessage;
        if (!FlowTaskStatus.SUBMITTED.getCode().equals(flowWorkOrder.getStatus())) {
            errorMessage = "数据验证失败，当前流程已经进入审批状态，不能撤销工单！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (!flowWorkOrder.getCreateBy().equals(this.getLoginUser().getId())) {
            errorMessage = "数据验证失败，当前用户不是工单所有者，不能撤销工单！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        instanceService.stop(flowWorkOrder.getProcessInstanceId(), cancelReason, true);
        return Response.success();
    }

    /**
     * 催办工单，只有流程发起人才可以催办工单。
     * 催办场景必须要取消数据权限过滤，因为流程的指派很可能是跨越部门的。
     * 既然被指派和催办了，这里就应该禁用工单表的数据权限过滤约束。
     * 如果您的系统没有支持数据权限过滤，DisableDataFilter不会有任何影响，建议保留。
     *
     * @return 应答结果。
     */
    @PostMapping("/remind/{id}")
    public Response<Void> remindRuntimeTask(@PathVariable String id) {
        FlowWorkOrder flowWorkOrder = baseService.selectById(id);
        if (flowWorkOrder == null) {
            return Response.error(ResponseEnum.DATA_ERROR_NOT_FOUND);
        }
        if (!flowWorkOrder.getCreateBy().equals(this.getLoginUser().getId())) {
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), "数据验证失败，只有流程发起人才能催办工单!");
        }
        if (FlowTaskStatus.FINISHED.getCode().equals(flowWorkOrder.getStatus())) {
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), "数据验证失败，已经结束的流程，不能催办工单！");
        }
        messageService.saveNewRemindMessage(flowWorkOrder);
        return Response.success();
    }

//    @GetMapping("/getData/{businessKey}")
//    public Response getData(@PathVariable String businessKey){
//        FlowWorkOrder flowWorkOrder = baseService.selectByBusinessKey(businessKey);
//        return Response.success(flowWorkOrder);
//    }


}
