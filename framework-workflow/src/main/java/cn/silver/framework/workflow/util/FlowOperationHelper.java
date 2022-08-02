package cn.silver.framework.workflow.util;

import cn.hutool.core.util.StrUtil;
import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.workflow.constant.FlowApprovalType;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.constant.FlowPublishStatus;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.service.IFlowEntryPublishService;
import cn.silver.framework.workflow.service.IFlowEntryService;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import cn.silver.framework.workflow.vo.TaskInfoVo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 工作流操作的通用帮助对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Component
public class FlowOperationHelper {

    @Autowired
    private IFlowEntryService flowEntryService;
    @Autowired
    private IFlowEntryPublishService flowEntryPublishService;
    @Autowired
    private IFlowApiService flowApiService;
    @Autowired
    private IFlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    /**
     * 验证并获取流程对象。
     *
     * @param processDefinitionKey 流程引擎的流程定义标识。
     * @return 流程对象。
     */
    public FlowEntry verifyAndGetFlowEntry(String processDefinitionKey) {
        FlowEntry flowEntry = flowEntryService.selectByValue(processDefinitionKey);
        if (flowEntry == null) {
            throw new CustomException("数据验证失败，该流程并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (flowEntry.getStatus().equals(FlowPublishStatus.UNPUBLISHED.getCode())) {
            throw new CustomException("数据验证失败，该流程尚未发布，请刷新后重试！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        FlowEntryPublish flowEntryPublish =
                flowEntryPublishService.selectById(flowEntry.getMainEntryPublishId());
        flowEntry.setMainFlowEntryPublish(flowEntryPublish);
        return flowEntry;
    }

    /**
     * 工作流静态表单的参数验证工具方法。根据流程定义标识，获取关联的流程并对其进行合法性验证。
     *
     * @param processDefinitionKey 流程定义标识。
     * @return 返回流程对象。
     */
    public FlowEntry verifyFullAndGetFlowEntry(String processDefinitionKey) {
        // 验证流程管理数据状态的合法性。
        FlowEntry entry = this.verifyAndGetFlowEntry(processDefinitionKey);
        // 验证流程一个用户任务的合法性。
        FlowEntryPublish publish = entry.getMainFlowEntryPublish();
        if (!publish.getActiveStatus()) {
            throw new CustomException("数据验证失败，当前流程发布对象已被挂起，不能启动新流程！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        this.verifyAndGetInitialTaskInfo(publish, true);
        return entry;
    }

    /**
     * 工作流静态表单的参数验证工具方法。根据参数验证并获取指定的流程任务对象。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @param flowTaskComment   流程审批对象。
     * @return 验证后的流程任务对象。
     */
    public Task verifySubmitAndGetTask(
            String processInstanceId, String taskId, FlowTaskComment flowTaskComment) {
        // 验证流程任务的合法性。
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        this.verifyAndGetRuntimeTaskInfo(task);
        flowApiService.verifyAssigneeOrCandidateAndClaim(task);
        ProcessInstance instance = flowApiService.getProcessInstance(processInstanceId);
        if (StrUtil.isBlank(instance.getBusinessKey())) {
            return task;
        }
        if (flowTaskComment != null && StrUtil.equals(flowTaskComment.getApprovalType(), FlowApprovalType.TRANSFER.getCode())) {
            if (StrUtil.isBlank(flowTaskComment.getDelegateAssignee())) {
                throw new CustomException("数据验证失败，加签或转办任务指派人不能为空！！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
        }
        return task;
    }

    /**
     * 工作流静态表单的参数验证工具方法。根据参数验证并获取指定的历史流程实例对象。
     * 仅当登录用户为任务的分配人时，才能通过验证。
     *
     * @param processInstanceId 历史流程实例Id。
     * @param taskId            历史流程任务Id。
     * @return 验证后并返回的历史流程实例对象。
     */
    public HistoricProcessInstance verifyAndHistoricProcessInstance(String processInstanceId, String taskId) {
        // 验证流程实例的合法性。
        HistoricProcessInstance instance = flowApiService.getHistoricProcessInstance(processInstanceId);
        if (instance == null) {
            throw new CustomException("数据验证失败，指定的流程实例Id并不存在，请刷新后重试！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        String loginName = LoginUser.getInstance().getId();
        if (StrUtil.isBlank(taskId)) {
            if (!StrUtil.equals(loginName, instance.getStartUserId())) {
                if (!flowWorkOrderService.hasDataPermOnFlowWorkOrder(processInstanceId)) {
                    throw new CustomException("数据验证失败，指定历史流程的发起人与当前用户不匹配，或者没有查看该工单详情的数据权限！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
                }
            }
        } else {
            HistoricTaskInstance taskInstance = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
            if (taskInstance == null) {
                throw new CustomException("数据验证失败，指定的任务Id并不存在，请刷新后重试！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
            if (!StrUtil.equals(loginName, taskInstance.getAssignee())) {
                if (!flowWorkOrderService.hasDataPermOnFlowWorkOrder(processInstanceId)) {
                    throw new CustomException("数据验证失败，历史任务的指派人与当前用户不匹配，或者没有查看该工单详情的数据权限！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
                }
            }
        }
        return instance;
    }

    /**
     * 验证并获取流程的实时任务信息。
     *
     * @param task 流程引擎的任务对象。
     * @return 任务信息对象。
     */
    public TaskInfoVo verifyAndGetRuntimeTaskInfo(Task task) {
        if (task == null) {
            throw new CustomException("数据验证失败，指定的任务Id，请刷新后重试！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        if (!flowApiService.isAssigneeOrCandidate(task)) {
            throw new CustomException("数据验证失败，当前用户不是指派人也不是候选人之一！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        if (StrUtil.isBlank(task.getFormKey())) {
            throw new CustomException("数据验证失败，指定任务的formKey属性不存在，请重新修改流程图！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        TaskInfoVo taskInfo = JSON.parseObject(task.getFormKey(), TaskInfoVo.class);
        taskInfo.setTaskKey(task.getTaskDefinitionKey());
        return taskInfo;
    }

    /**
     * 验证并获取启动任务的对象信息。
     *
     * @param flowEntryPublish 流程发布对象。
     * @param checkStarter     是否检查发起用户。
     * @return 第一个可执行的任务信息。
     */
    public TaskInfoVo verifyAndGetInitialTaskInfo(
            FlowEntryPublish flowEntryPublish, boolean checkStarter) {
        if (StrUtil.isBlank(flowEntryPublish.getRemark())) {
            throw new CustomException("数据验证失败，当前流程发布的数据中，没有包含初始任务信息！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        TaskInfoVo taskInfo = JSON.parseObject(flowEntryPublish.getRemark(), TaskInfoVo.class);
        if (checkStarter) {
            String loginName = LoginUser.getInstance().getId();
            if (!StrUtil.equalsAny(taskInfo.getAssignee(), loginName, FlowConstant.START_USER_NAME_VAR)) {
                throw new CustomException("数据验证失败，该工作流第一个用户任务的指派人并非当前用户，不能执行该操作！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
        }
        return taskInfo;
    }

    /**
     * 判断当前用户是否有当前流程实例的数据上传或下载权限。
     * 如果taskId为空，则验证当前用户是否为当前流程实例的发起人，否则判断是否为当前任务的指派人或候选人。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @return 验证结果。
     */
    public void verifyUploadOrDownloadPermission(String processInstanceId, String taskId) {
        if (StrUtil.isBlank(taskId)) {
            if (!flowApiService.isProcessInstanceStarter(processInstanceId)) {
                throw new CustomException("数据验证失败，当前用户并非指派人或候选人，因此没有权限下载！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
            }
        } else {
            TaskInfo task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
            if (task == null) {
                task = flowApiService.getHistoricTaskInstance(processInstanceId, taskId);
                if (task == null) {
                    throw new CustomException("数据验证失败，指定任务Id不存在！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
                }
            }
            if (!flowApiService.isAssigneeOrCandidate(task)) {
                throw new CustomException("数据验证失败，当前用户并非指派人或候选人，因此没有权限下载！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
            }
        }
    }

    /**
     * 根据已有的过滤对象，补充添加缺省过滤条件。如流程标识、创建用户等。
     *
     * @param filterDto            工单过滤对象。
     * @param processDefinitionKey 流程标识。
     * @return 创建并转换后的流程工单过滤对象。
     */
    public FlowWorkOrder makeWorkOrderFilter(FlowWorkOrder filterDto, String processDefinitionKey) {
        FlowWorkOrder filter = new FlowWorkOrder();
        BeanUtils.copyProperties(filterDto, filter);
        if (filter == null) {
            filter = new FlowWorkOrder();
        }
        filter.setProcessDefinitionKey(processDefinitionKey);
        // 下面的方法会帮助构建工单的数据权限过滤条件，和业务希望相比，如果当前系统没有支持数据权限，
        // 用户则只能看到自己发起的工单，否则按照数据权限过滤。然而需要特殊处理的是，如果用户的数据
        // 权限中，没有包含能看自己，这里也需要自动给加上。
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
//        if (!flowIdentityExtHelper.supprtDataPerm()) {
//            filter.setCreateUserId(TokenData.takeFromRequest().getUserId());
//        }
        return filter;
    }

    /**
     * 组装工作流工单列表中的流程任务数据。
     *
     * @param flowWorkOrderVoList 工作流工单列表。
     */
//    public void buildWorkOrderTaskInfo(List<FlowWorkOrder> flowWorkOrderVoList) {
//        if (CollUtil.isEmpty(flowWorkOrderVoList)) {
//            return;
//        }
//        Set<String> definitionIdSet =
//                flowWorkOrderVoList.stream().map(FlowWorkOrder::getProcessDefinitionId).collect(Collectors.toSet());
//        List<FlowEntryPublish> flowEntryPublishList = flowEntryPublishService.selectByDefinitionIds(definitionIdSet);
//        Map<String, FlowEntryPublish> flowEntryPublishMap =
//                flowEntryPublishList.stream().collect(Collectors.toMap(FlowEntryPublish::getProcessDefinitionId, c -> c));
//        for (FlowWorkOrder flowWorkOrderVo : flowWorkOrderVoList) {
//            FlowEntryPublish flowEntryPublish = flowEntryPublishMap.get(flowWorkOrderVo.getProcessDefinitionId());
//            flowWorkOrderVo.setRemark(flowEntryPublish.getRemark());
//        }
//        List<String> unfinishedProcessInstanceIds = flowWorkOrderVoList.stream()
//                .filter(c -> !c.getFlowStatus().equals(FlowTaskStatus.FINISHED))
//                .map(FlowWorkOrder::getProcessInstanceId)
//                .collect(Collectors.toList());
//        if (CollUtil.isEmpty(unfinishedProcessInstanceIds)) {
//            return;
//        }
//        List<Task> taskList = flowApiService.getTaskListByProcessInstanceIds(unfinishedProcessInstanceIds);
//        Map<String, List<Task>> taskMap =
//                taskList.stream().collect(Collectors.groupingBy(Task::getProcessInstanceId));
//        for (FlowWorkOrder flowWorkOrderVo : flowWorkOrderVoList) {
//            List<Task> instanceTaskList = taskMap.get(flowWorkOrderVo.getProcessInstanceId());
//            if (instanceTaskList == null) {
//                continue;
//            }
//            JSONArray taskArray = new JSONArray();
//            for (Task task : instanceTaskList) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("taskId", task.getId());
//                jsonObject.put("taskName", task.getName());
//                jsonObject.put("taskKey", task.getTaskDefinitionKey());
//                jsonObject.put("assignee", task.getAssignee());
//                taskArray.add(jsonObject);
//            }
//            flowWorkOrderVo.setRuntimeTaskInfoList(taskArray);
//        }
//    }

    /**
     * 组装工作流工单中的业务数据。
     *
     * @param workOrderVoList 工单列表。
     * @param dataList        业务数据列表。
     * @param idGetter        获取业务对象主键字段的返回方法。
     * @param <T>             业务主对象类型。
     * @param <K>             业务主对象的主键字段类型。
     */
//    public <T, K> void buildWorkOrderBusinessData(
//            List<FlowWorkOrder> workOrderVoList, List<T> dataList, Function<T, K> idGetter) {
//        if (CollUtil.isEmpty(dataList)) {
//            return;
//        }
//        Map<Object, T> dataMap = dataList.stream().collect(Collectors.toMap(idGetter, c -> c));
//        K id = idGetter.apply(dataList.get(0));
//        for (FlowWorkOrder flowWorkOrderVo : workOrderVoList) {
//            Object dataId = flowWorkOrderVo.getBusinessKey();
//            if (id instanceof Long) {
//                dataId = Long.valueOf(flowWorkOrderVo.getBusinessKey());
//            } else if (id instanceof Integer) {
//                dataId = Integer.valueOf(flowWorkOrderVo.getBusinessKey());
//            }
//            T data = dataMap.get(dataId);
//            if (data != null) {
//                flowWorkOrderVo.setMasterData(BeanUtil.beanToMap(data));
//            }
//        }
//    }
}
