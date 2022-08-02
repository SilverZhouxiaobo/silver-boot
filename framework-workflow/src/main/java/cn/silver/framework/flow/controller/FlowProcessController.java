package cn.silver.framework.flow.controller;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.flow.domain.dto.FlowTaskDto;
import cn.silver.framework.flow.domain.vo.FlowTaskVo;
import cn.silver.framework.flow.service.IFlowProcessService;
import cn.silver.framework.flow.service.IFlowTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Api(tags = "流程管理-任务处理")
@RequestMapping("/flowable/process")
public class FlowProcessController extends BaseController {

    @Autowired
    private IFlowProcessService processService;
    @Autowired
    private IFlowTaskService taskService;

    @PostMapping(value = "/claim/{taskId}")
    @ApiOperation(value = "认领/签收任务")
    public Response<Object> claim(@PathVariable String taskId) {
        processService.claim(taskId);
        return Response.success();
    }

    @ApiOperation(value = "取消认领/签收任务")
    @PostMapping(value = "/unclaim/{taskId}")
    public Response<Object> unclaim(@PathVariable String taskId) {
        processService.unClaim(taskId);
        return Response.success();
    }

    @GetMapping(value = "/flow")
    @ApiOperation(value = "流程历史流转记录", response = FlowTaskDto.class)
    public Response<Map<String, Object>> flowRecord(String procInsId, String deployId) {
        return Response.success(taskService.flowRecord(procInsId, deployId));
    }

    @GetMapping(value = "/variables/{taskId}")
    @ApiOperation(value = "获取流程变量", response = FlowTaskDto.class)
    public Response<Map<String, Object>> processVariables(@ApiParam(value = "流程任务Id") @PathVariable(value = "taskId") String taskId) {
        return Response.success(taskService.processVariables(taskId));
    }

    @ApiOperation(value = "获取所有可回退的节点")
    @PostMapping(value = "/returnList/{taskId}")
    public Response<List<UserTask>> findReturnTaskList(@PathVariable String taskId) {
        return Response.success(taskService.findReturnTaskList(taskId));
    }

//    @PostMapping(value = "/stop")
//    @ApiOperation(value = "任务管理-取消申请", response = FlowTaskDto.class)
//    public Response<Object> stopProcess(@RequestBody FlowTaskVo flowTaskVo) {
//        return flowTaskService.stopProcess(flowTaskVo);
//    }

    @PostMapping(value = "/revoke/{instanceId}")
    @ApiOperation(value = "任务管理-撤回流程")
    public Response<Void> revokeProcess(@PathVariable String instanceId) {
        processService.revokeProcess(instanceId);
        return Response.success();
    }

    @ApiOperation(value = "委派任务")
    @PostMapping(value = "/delegate")
    public Response<Object> delegate(@RequestBody FlowTaskVo flowTaskVo) {
        processService.delegateTask(flowTaskVo);
        return Response.success();
    }

    @ApiOperation(value = "转办任务")
    @PostMapping(value = "/assign")
    public Response<Object> assign(@RequestBody FlowTaskVo flowTaskVo) {
        processService.assignTask(flowTaskVo);
        return Response.success();
    }

    @ApiOperation(value = "审批任务")
    @PostMapping(value = "/complete")
    public Response<Object> complete(@RequestBody FlowTaskVo flowTaskVo) {
        return processService.complete(flowTaskVo);
    }

    @ApiOperation(value = "驳回任务")
    @PostMapping(value = "/reject")
    public Response<Object> taskReject(@RequestBody FlowTaskVo flowTaskVo) {
        processService.taskReject(flowTaskVo);
        return Response.success();
    }

    @ApiOperation(value = "退回任务")
    @PostMapping(value = "/return")
    public Response<Object> taskReturn(@RequestBody FlowTaskVo flowTaskVo) {
        processService.taskReturn(flowTaskVo);
        return Response.success();
    }

    @ApiOperation(value = "删除任务")
    @DeleteMapping(value = "/delete")
    public Response<Object> delete(@RequestBody FlowTaskVo flowTaskVo) {
        processService.deleteTask(flowTaskVo);
        return Response.success();
    }
}
