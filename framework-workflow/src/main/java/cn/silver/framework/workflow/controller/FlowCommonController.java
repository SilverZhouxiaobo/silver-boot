package cn.silver.framework.workflow.controller;

import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.controller.BaseController;
import cn.silver.framework.workflow.constant.FlowApprovalType;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.hb.software.gacim.workflow.service.*;
import cn.silver.framework.workflow.util.FlowCustomExtFactory;
import cn.silver.framework.workflow.util.FlowOperationHelper;
import cn.silver.framework.workflow.vo.TaskInfoVo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.silver.framework.workflow.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 流程操作接口类
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Api(tags = "通用流程操作接口")
@Slf4j
@RestController
@RequestMapping("/flow/common")
public class FlowCommonController extends BaseController {
    @Autowired
    private IFlowTaskCommentService flowTaskCommentService;
    @Autowired
    private IFlowTaskExtService flowTaskExtService;
    @Autowired
    private IFlowApiService flowApiService;
    @Autowired
    private IFlowWorkOrderService flowWorkOrderService;
    @Autowired
    private IFlowMessageService flowMessageService;
    @Autowired
    private FlowOperationHelper flowOperationHelper;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    /**
     * 根据指定流程的主版本，发起一个流程实例。
     *
     * @param definitionKey 流程标识。
     * @return 应答结果对象。
     */
    @PostMapping("/startOnly/{definitionKey}")
    public Response<Void> startOnly(@PathVariable String definitionKey) {
        // 1. 验证流程数据的合法性。
        FlowEntry entry = flowOperationHelper.verifyAndGetFlowEntry(definitionKey);

        // 2. 验证流程一个用户任务的合法性。
        FlowEntryPublish flowEntryPublish = entry.getMainFlowEntryPublish();
        flowOperationHelper.verifyAndGetInitialTaskInfo(flowEntryPublish, false);
        flowApiService.start(flowEntryPublish.getProcessDefinitionId(), null);
        return Response.success();
    }

    /**
     * 获取开始节点之后的第一个任务节点的数据。
     *
     * @param definitionKey 流程标识。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewInitialTaskInfo/{definitionKey}")
    public Response<TaskInfoVo> viewInitialTaskInfo(@PathVariable String definitionKey) {
        FlowEntry entry = flowOperationHelper.verifyAndGetFlowEntry(definitionKey);
        FlowEntryPublish flowEntryPublish = entry.getMainFlowEntryPublish();
        String initTaskInfo = flowEntryPublish.getRemark();
        TaskInfoVo taskInfo = StrUtil.isBlank(initTaskInfo)
                ? null : JSON.parseObject(initTaskInfo, TaskInfoVo.class);
        if (taskInfo != null) {
            String loginName = LoginUser.getInstance().getId();
            taskInfo.setAssignedMe(StrUtil.equalsAny(
                    taskInfo.getAssignee(), loginName, FlowConstant.START_USER_NAME_VAR));
        }
        return Response.success(taskInfo);
    }

    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewRuntimeTaskInfo")
    public Response<TaskInfoVo> viewRuntimeTaskInfo(@RequestParam String processDefinitionId,
                                                    @RequestParam String processInstanceId,
                                                    @RequestParam String taskId) {
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        TaskInfoVo taskInfoVo = flowOperationHelper.verifyAndGetRuntimeTaskInfo(task);
        FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskInfoVo.getTaskKey());
        if (flowTaskExt != null) {
            if (StrUtil.isNotBlank(flowTaskExt.getOperationListJson())) {
                taskInfoVo.setOperationList(JSON.parseArray(flowTaskExt.getOperationListJson(), JSONObject.class));
            }
            if (StrUtil.isNotBlank(flowTaskExt.getVariableListJson())) {
                taskInfoVo.setVariableList(JSON.parseArray(flowTaskExt.getVariableListJson(), JSONObject.class));
            }
        }
        return Response.success(taskInfoVo);
    }

    /**
     * 获取流程运行时指定任务的信息。
     *
     * @param processDefinitionId 流程引擎的定义Id。
     * @param processInstanceId   流程引擎的实例Id。
     * @param taskId              流程引擎的任务Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewHistoricTaskInfo")
    public Response<TaskInfoVo> viewHistoricTaskInfo(
            @RequestParam String processDefinitionId,
            @RequestParam String processInstanceId,
            @RequestParam String taskId) {
        String errorMessage;
        HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
        String loginName = LoginUser.getInstance().getId();
        if (!StrUtil.equals(taskInstance.getAssignee(), loginName)) {
            errorMessage = "数据验证失败，当前用户不是指派人！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        TaskInfoVo taskInfoVo = JSON.parseObject(taskInstance.getFormKey(), TaskInfoVo.class);
        FlowTaskExt flowTaskExt =
                flowTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskInstance.getTaskDefinitionKey());
        if (flowTaskExt != null) {
            if (StrUtil.isNotBlank(flowTaskExt.getOperationListJson())) {
                taskInfoVo.setOperationList(JSON.parseArray(flowTaskExt.getOperationListJson(), JSONObject.class));
            }
            if (StrUtil.isNotBlank(flowTaskExt.getVariableListJson())) {
                taskInfoVo.setVariableList(JSON.parseArray(flowTaskExt.getVariableListJson(), JSONObject.class));
            }
        }
        return Response.success(taskInfoVo);
    }

    /**
     * 获取第一个提交表单数据的任务信息。
     *
     * @param processInstanceId 流程实例Id。
     * @return 任务节点的自定义对象数据。
     */
    @GetMapping("/viewInitialHistoricTaskInfo")
    public Response<TaskInfoVo> viewInitialHistoricTaskInfo(@RequestParam String processInstanceId) {
        String errorMessage;
        List<FlowTaskComment> taskCommentList =
                flowTaskCommentService.getFlowTaskCommentList(processInstanceId);
        if (CollUtil.isEmpty(taskCommentList)) {
            return Response.error(ResponseEnum.DATA_ERROR_NOT_FOUND);
        }
        FlowTaskComment taskComment = taskCommentList.get(0);
        HistoricTaskInstance task = flowApiService.getHistoricTaskInstance(processInstanceId, taskComment.getTaskId());
        if (StrUtil.isBlank(task.getFormKey())) {
            errorMessage = "数据验证失败，指定任务的formKey属性不存在，请重新修改流程图！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        TaskInfoVo taskInfo = JSON.parseObject(task.getFormKey(), TaskInfoVo.class);
        taskInfo.setTaskKey(task.getTaskDefinitionKey());
        return Response.success(taskInfo);
    }


    /**
     * 提交多实例加签。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            多实例任务的上一级任务Id。
     * @param newAssignees      新的加签人列表，多个指派人之间逗号分隔。
     * @return 应答结果。
     */
    @PostMapping("/submitConsign")
    public Response<Void> submitConsign(@RequestBody() String processInstanceId, @RequestBody() String taskId, @RequestBody() String newAssignees) {
        String errorMessage;
        if (!flowApiService.existActiveProcessInstance(processInstanceId)) {
            errorMessage = "数据验证失败，当前流程实例已经结束，不能执行加签！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
        if (taskInstance == null) {
            errorMessage = "数据验证失败，当前任务不存在！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (!StrUtil.equals(taskInstance.getAssignee(), this.getLoginUser().getId())) {
            errorMessage = "数据验证失败，任务指派人与当前用户不匹配！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        List<Task> activeTaskList = flowApiService.getProcessInstanceActiveTaskList(processInstanceId);
        Task activeMultiInstanceTask = null;
        for (Task activeTask : activeTaskList) {
            Object startTaskId = flowApiService.getTaskVariable(
                    activeTask.getId(), FlowConstant.MULTI_SIGN_START_TASK_VAR);
            if (startTaskId != null && startTaskId.toString().equals(taskId)) {
                activeMultiInstanceTask = activeTask;
                break;
            }
        }
        if (activeMultiInstanceTask == null) {
            errorMessage = "数据验证失败，指定加签任务不存在或已审批完毕！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        flowApiService.submitConsign(taskInstance, activeMultiInstanceTask, newAssignees);
        return Response.success();
    }


    /**
     * 主动驳回当前的待办任务到开始节点，只用当前待办任务的指派人或者候选者才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待办任务Id。
     * @param comment           驳回备注。
     * @return 操作应答结果。
     */
    @PostMapping("/rejectToStartUserTask")
    public Response<Void> rejectToStartUserTask(
            @RequestBody(required = true) String processInstanceId,
            @RequestBody(required = true) String taskId,
            @RequestBody(required = true) String comment) {
        Task task = flowOperationHelper.verifySubmitAndGetTask(processInstanceId, taskId, null);
        FlowTaskComment firstTaskComment = flowTaskCommentService.getFirstFlowTaskComment(processInstanceId);
        flowApiService.backToRuntimeTask(task, firstTaskComment.getTaskKey(), true, comment);
        return Response.success();
    }

    /**
     * 主动驳回当前的待办任务，只用当前待办任务的指派人或者候选者才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待办任务Id。
     * @param comment           驳回备注。
     * @return 操作应答结果。
     */
    @PostMapping("/rejectRuntimeTask")
    public Response<Void> rejectRuntimeTask(
            @RequestBody String processInstanceId,
            @RequestBody String taskId,
            @RequestBody String comment) {
        Task task = flowOperationHelper.verifySubmitAndGetTask(processInstanceId, taskId, null);
        flowApiService.backToRuntimeTask(task, null, true, comment);
        return Response.success();
    }

    /**
     * 撤回当前用户提交的，但是尚未被审批的待办任务。只有已办任务的指派人才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待撤回的已办任务Id。
     * @param comment           撤回备注。
     * @return 操作应答结果。
     */
    @PostMapping("/revokeHistoricTask")
    public Response<Void> revokeHistoricTask(
            @RequestBody(required = true) String processInstanceId,
            @RequestBody(required = true) String taskId,
            @RequestBody(required = true) String comment) {
        String errorMessage;
        if (!flowApiService.existActiveProcessInstance(processInstanceId)) {
            errorMessage = "数据验证失败，当前流程实例已经结束，不能执行撤回！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
        if (taskInstance == null) {
            errorMessage = "数据验证失败，当前任务不存在！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (!StrUtil.equals(taskInstance.getAssignee(), this.getUserId())) {
            errorMessage = "数据验证失败，任务指派人与当前用户不匹配！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        FlowTaskComment taskComment = flowTaskCommentService.getLatestFlowTaskComment(processInstanceId);
        if (taskComment == null) {
            errorMessage = "数据验证失败，当前实例没有任何审批提交记录！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (!taskComment.getTaskId().equals(taskId)) {
            errorMessage = "数据验证失败，当前审批任务已被办理，不能撤回！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        List<Task> activeTaskList = flowApiService.getProcessInstanceActiveTaskList(processInstanceId);
        if (CollUtil.isEmpty(activeTaskList)) {
            errorMessage = "数据验证失败，当前流程没有任何待办任务！";
            return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
        }
        if (taskComment.getApprovalType().equals(FlowApprovalType.TRANSFER)) {
            if (activeTaskList.size() > 1) {
                errorMessage = "数据验证失败，转办任务数量不能多于1个！";
                return Response.error(ResponseEnum.DATA_VALIDATED_FAILED.getCode(), errorMessage);
            }
            // 如果是转办任务，无需节点跳转，将指派人改为当前用户即可。
            Task task = activeTaskList.get(0);
            task.setAssignee(this.getUserId());
        } else {
            flowApiService.backToRuntimeTask(activeTaskList.get(0), null, false, comment);
        }
        return Response.success();
    }

    /**
     * 获取指定流程定义的流程图。
     *
     * @param definitionId 流程定义Id。
     * @return 流程图。
     */
    @GetMapping("/viewProcessBpmn/{definitionId}")
    public Response<String> viewProcessBpmn(@PathVariable String definitionId) throws IOException {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        BpmnModel bpmnModel = flowApiService.getBpmnModelByDefinitionId(definitionId);
        byte[] xmlBytes = converter.convertToXML(bpmnModel);
        InputStream in = new ByteArrayInputStream(xmlBytes);
        return Response.success(StreamUtils.copyToString(in, StandardCharsets.UTF_8));
    }


}
