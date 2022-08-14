package cn.silver.framework.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.workflow.constant.FlowApprovalType;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.constant.FlowInitVariable;
import cn.silver.framework.workflow.constant.FlowTaskStatus;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.silver.framework.workflow.object.FlowTaskMultiSignAssign;
import cn.silver.framework.workflow.object.FlowTaskOperation;
import cn.silver.framework.workflow.object.FlowTaskPostCandidateGroup;
import cn.silver.framework.workflow.service.*;
import cn.silver.framework.workflow.util.BaseFlowIdentityExtHelper;
import cn.silver.framework.workflow.util.FlowCustomExtFactory;
import cn.silver.framework.workflow.vo.FlowTaskVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service("flowApiService")
public class FlowApiServiceImpl implements IFlowApiService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    protected ManagementService managementService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IFlowEntryService flowEntryService;
    @Autowired
    private IFlowEntryPublishService flowEntryPublishService;
    @Autowired
    private IFlowTaskCommentService flowTaskCommentService;
    @Autowired
    private IFlowTaskExtService flowTaskExtService;
    @Autowired
    private IFlowWorkOrderService flowWorkOrderService;
    @Autowired
    private IFlowMessageService flowMessageService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProcessInstance start(String processDefinitionId, String dataId) {
        LoginUser loginUser = this.getLoginUser();
        Map<String, Object> variableMap;
        if (dataId == null) {
            variableMap = new HashMap<>(2);
            variableMap.put(FlowInitVariable.INITIATOR_VAR.getVariableName(), loginUser.getId());
            variableMap.put(FlowInitVariable.START_USER_ID.getVariableName(), loginUser.getId());
            variableMap.put(FlowInitVariable.START_USER_NAME.getVariableName(), loginUser.getNickName());
        } else {
            variableMap = this.initAndGetProcessInstanceVariables(processDefinitionId);
        }
        Authentication.setAuthenticatedUserId(loginUser.getId());
        return runtimeService.startProcessInstanceById(processDefinitionId, dataId, variableMap);
    }

    @Override
    public ProcessInstance start(String processDefinitionId, String processDefinitionKey, String dataId) {
        LoginUser loginName = this.getLoginUser();
        Map<String, Object> variableMap;
        variableMap = new HashMap<>(2);
        variableMap.put(FlowInitVariable.INITIATOR_VAR.getVariableName(), loginName.getId());
        variableMap.put(FlowInitVariable.START_USER_ID.getVariableName(), loginName.getId());
        variableMap.put(FlowInitVariable.START_USER_NAME.getVariableName(), loginName.getNickName());
        Authentication.setAuthenticatedUserId(loginName.getId());

        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        processInstanceBuilder.processDefinitionId(processDefinitionId);
        // 流程实例标题
        processInstanceBuilder.name(loginName.getNickName());
        // 业务key
        processInstanceBuilder.businessKey(dataId);
        processInstanceBuilder.variables(variableMap);
        return processInstanceBuilder.start();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ProcessInstance startAndTakeFirst(
            String processDefinitionId, String dataId, FlowTaskComment comment, JSONObject taskVariableData) {
        LoginUser loginUser = this.getLoginUser();
        Map<String, Object> variableMap = this.initAndGetProcessInstanceVariables(processDefinitionId);
        variableMap.put(FlowInitVariable.INITIATOR_VAR.getVariableName(), loginUser.getId());
        variableMap.put(FlowInitVariable.START_USER_ID.getVariableName(), loginUser.getId());
        variableMap.put(FlowInitVariable.START_USER_NAME.getVariableName(), loginUser.getNickName());
        Authentication.setAuthenticatedUserId(loginUser.getId());
        // 设置流程变量。
        // 根据当前流程的主版本，启动一个流程实例，同时将businessKey参数设置为主表主键值。
        ProcessInstance instance = runtimeService.startProcessInstanceById(
                processDefinitionId, dataId, variableMap);
        // 获取流程启动后的第一个任务。
        Task task = taskService.createTaskQuery().processInstanceId(instance.getId()).active().singleResult();
        if (FlowConstant.INITIATOR.equals(task.getTaskDefinitionKey())) {
            this.taskService.claim(task.getId(), loginUser.getId());
        }
        if (loginUser.getId().equals(task.getAssignee())) {
            // 按照规则，调用该方法的用户，就是第一个任务的assignee，因此默认会自动执行complete。
            comment.fillWith(task);
            this.completeTask(task, comment, taskVariableData);
        }
        return instance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void submitConsign(HistoricTaskInstance startTaskInstance, Task multiInstanceActiveTask, String newAssignees) {
        JSONArray assigneeArray = JSON.parseArray(newAssignees);
        for (int i = 0; i < assigneeArray.size(); i++) {
            Map<String, Object> variables = new HashMap<>(2);
            variables.put("assignee", assigneeArray.getString(i));
            variables.put(FlowConstant.MULTI_SIGN_START_TASK_VAR, startTaskInstance.getId());
            runtimeService.addMultiInstanceExecution(
                    multiInstanceActiveTask.getTaskDefinitionKey(), multiInstanceActiveTask.getProcessInstanceId(), variables);
        }
        FlowTaskComment flowTaskComment = new FlowTaskComment();
        flowTaskComment.fillWith(startTaskInstance);
        flowTaskComment.setApprovalType(FlowApprovalType.MULTI_CONSIGN.getCode());
        String showName = this.getLoginUser().getNickName();
        String comment = String.format("用户 [%s] 加签 [%s]。", showName, newAssignees);
        flowTaskComment.setRemark(comment);
        flowTaskCommentService.insert(flowTaskComment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void completeTask(Task task, FlowTaskComment comment, JSONObject taskVariableData) {
        JSONObject passCopyData = null;
        if (taskVariableData != null) {
            passCopyData = (JSONObject) taskVariableData.remove(FlowConstant.COPY_DATA_KEY);
        }
        if (comment != null) {
            if (FlowApprovalType.MULTI_SIGN.getCode().equals(comment.getApprovalType())) {
                // 这里处理多实例会签逻辑。
                String loginName = this.getLoginUser().getNickName();
                if (taskVariableData == null) {
                    taskVariableData = new JSONObject();
                }
                String assigneeList = taskVariableData.getString(FlowConstant.MULTI_ASSIGNEE_LIST_VAR);
                if (StrUtil.isBlank(assigneeList)) {
                    FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
                            task.getProcessDefinitionId(), task.getTaskDefinitionKey());
                    assigneeList = this.buildMutiSignAssigneeList(flowTaskExt.getOperationListJson());
                    if (assigneeList != null) {
                        taskVariableData.put(FlowConstant.MULTI_ASSIGNEE_LIST_VAR, StrUtil.split(assigneeList, ','));
                    }
                }
                Assert.isTrue(StrUtil.isNotBlank(assigneeList));
                taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_SIGN_NUM_OF_INSTANCES_VAR, 0);
                taskVariableData.put(FlowConstant.MULTI_SIGN_START_TASK_VAR, task.getId());
                String commentInfo = String.format("用户 [%s] 会签 [%s]。", loginName, assigneeList);
                comment.setRemark(commentInfo);
            } else if (FlowApprovalType.TRANSFER.getCode().equals(comment.getApprovalType())) {
                // 处理转办。
                taskService.setAssignee(task.getId(), comment.getDelegateAssignee());
                comment.fillWith(task);
                flowTaskCommentService.insert(comment);
                return;
            } else {
                taskService.setAssignee(task.getId(), getLoginUser().getId());
            }
            if (taskVariableData == null) {
                taskVariableData = new JSONObject();
                taskVariableData.putAll(comment.getBusinessParam());
            }
            this.handleMultiInstanceApprovalType(
                    task.getExecutionId(), comment.getApprovalType(), taskVariableData);
            taskVariableData.put(FlowConstant.OPERATION_TYPE_VAR, comment.getApprovalType());
            comment.fillWith(task);
            flowTaskCommentService.insert(comment);
        }
        // 判断当前完成执行的任务，是否存在抄送设置。
        Object copyData = runtimeService.getVariable(
                task.getProcessInstanceId(), FlowConstant.COPY_DATA_MAP_PREFIX + task.getTaskDefinitionKey());
        if (copyData != null || passCopyData != null) {
            JSONObject copyDataJson = this.mergeCopyData(copyData, passCopyData);
            flowMessageService.saveNewCopyMessage(task, copyDataJson);
        }
        taskService.complete(task.getId(), taskVariableData);
        flowMessageService.updateFinishedStatusByTaskId(task.getId());
    }

    private JSONObject mergeCopyData(Object copyData, JSONObject passCopyData) {
        LoginUser tokenData = this.getLoginUser();
        // passCopyData是传阅数据，copyData是抄送数据。
        JSONObject resultCopyDataJson = passCopyData;
        if (resultCopyDataJson == null) {
            resultCopyDataJson = JSON.parseObject(copyData.toString());
        } else if (copyData != null) {
            JSONObject copyDataJson = JSON.parseObject(copyData.toString());
            for (Map.Entry<String, Object> entry : copyDataJson.entrySet()) {
                String value = resultCopyDataJson.getString(entry.getKey());
                if (value == null) {
                    resultCopyDataJson.put(entry.getKey(), entry.getValue());
                } else {
                    List<String> list1 = Arrays.asList(StrUtil.split(value, ","));
                    List<String> list2 = Arrays.asList(StrUtil.split(entry.getValue().toString(), ","));
                    Set<String> valueSet = new HashSet<>(list1);
                    valueSet.addAll(list2);
                    resultCopyDataJson.put(entry.getKey(), StrUtil.join(",", valueSet));
                }
            }
        }
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        for (Map.Entry<String, Object> entry : resultCopyDataJson.entrySet()) {
            String type = entry.getKey();
            switch (type) {
                case FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR:
                    Object upLeaderDeptPostId =
                            flowIdentityExtHelper.getUpLeaderDeptPostId(tokenData.getDeptId());
                    entry.setValue(upLeaderDeptPostId);
                    break;
                case FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR:
                    Object leaderDeptPostId =
                            flowIdentityExtHelper.getLeaderDeptPostId(tokenData.getDeptId());
                    entry.setValue(leaderDeptPostId);
                    break;
                case FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR:
                    Set<String> selfPostIdSet = Arrays.stream(entry.getValue().toString().split(",")).collect(Collectors.toSet());
                    Map<String, String> deptPostIdMap =
                            flowIdentityExtHelper.getDeptPostIdMap(tokenData.getDeptId(), selfPostIdSet);
                    String deptPostIdValues = "";
                    if (deptPostIdMap != null) {
                        deptPostIdValues = StrUtil.join(",", deptPostIdMap.values());
                    }
                    entry.setValue(deptPostIdValues);
                    break;
                case FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR:
                    Set<String> upPostIdSet = Arrays.stream(StrUtil.split(entry.getValue().toString(), ",")).collect(Collectors.toSet());
                    Map<String, String> upDeptPostIdMap =
                            flowIdentityExtHelper.getUpDeptPostIdMap(tokenData.getDeptId(), upPostIdSet);
                    String upDeptPostIdValues = "";
                    if (upDeptPostIdMap != null) {
                        upDeptPostIdValues = StrUtil.join(",", upDeptPostIdMap.values());
                    }
                    entry.setValue(upDeptPostIdValues);
                    break;
                default:
                    break;
            }
        }
        return resultCopyDataJson;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void verifyAssigneeOrCandidateAndClaim(Task task) {
        String loginName = this.getLoginUser().getNickName();
        // 这里必须先执行拾取操作，如果当前用户是候选人，特别是对于分布式场景，更是要先完成候选人的拾取。
        if (task.getAssignee() == null) {
            // 没有指派人
            if (!this.isAssigneeOrCandidate(task)) {
                throw new CustomException("数据验证失败，当前用户不是该待办任务的候选人，请刷新后重试！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
            // 作为候选人主动拾取任务。
            taskService.claim(task.getId(), loginName);
        } else {
            if (!task.getAssignee().equals(loginName)) {
                throw new CustomException("数据验证失败，当前用户不是该待办任务的指派人，请刷新后重试！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
        }
    }

    @Override
    public Map<String, Object> initAndGetProcessInstanceVariables(String processDefinitionId) {
        LoginUser tokenData = this.getLoginUser();
        String loginName = tokenData.getNickName();
        // 设置流程变量。
        Map<String, Object> variableMap = new HashMap<>(4);
        variableMap.put(FlowInitVariable.INITIATOR_VAR.getVariableName(), loginName);
        variableMap.put(FlowInitVariable.START_USER_NAME.getVariableName(), loginName);
        List<FlowTaskExt> flowTaskExtList = flowTaskExtService.getByProcessDefinitionId(processDefinitionId);
        boolean hasDeptPostLeader = false;
        boolean hasUpDeptPostLeader = false;
        boolean hasPostCandidateGroup = false;
        for (FlowTaskExt flowTaskExt : flowTaskExtList) {
            if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
                hasUpDeptPostLeader = true;
            } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
                hasDeptPostLeader = true;
            } else if (StrUtil.equals(flowTaskExt.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
                hasPostCandidateGroup = true;
            }
        }
        // 如果流程图的配置中包含用户身份相关的变量(如：部门领导和上级领导审批)，flowIdentityExtHelper就不能为null。
        // 这个需要子类去实现 BaseFlowIdentityExtHelper 接口，并注册到FlowCustomExtFactory的工厂中。
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        if (hasUpDeptPostLeader) {
            Assert.notNull(flowIdentityExtHelper);
            Object upLeaderDeptPostId = flowIdentityExtHelper.getUpLeaderDeptPostId(tokenData.getDeptId());
            if (upLeaderDeptPostId != null) {
                variableMap.put(FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR, upLeaderDeptPostId.toString());
            }
        }
        if (hasDeptPostLeader) {
            Assert.notNull(flowIdentityExtHelper);
            Object leaderDeptPostId = flowIdentityExtHelper.getLeaderDeptPostId(tokenData.getDeptId());
            if (leaderDeptPostId != null) {
                variableMap.put(FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR, leaderDeptPostId.toString());
            }
        }
        if (hasPostCandidateGroup) {
            Assert.notNull(flowIdentityExtHelper);
            Map<String, Object> postGroupDataMap =
                    this.buildPostCandidateGroupData(flowIdentityExtHelper, flowTaskExtList);
            variableMap.putAll(postGroupDataMap);
        }
        this.buildCopyData(flowTaskExtList, variableMap);
        return variableMap;
    }

    private void buildCopyData(List<FlowTaskExt> flowTaskExtList, Map<String, Object> variableMap) {
        LoginUser tokenData = this.getLoginUser();
        for (FlowTaskExt flowTaskExt : flowTaskExtList) {
            if (StrUtil.isBlank(flowTaskExt.getCopyListJson())) {
                continue;
            }
            List<JSONObject> copyDataList = JSON.parseArray(flowTaskExt.getCopyListJson(), JSONObject.class);
            Map<String, Object> copyDataMap = new HashMap<>(copyDataList.size());
            for (JSONObject copyData : copyDataList) {
                String type = copyData.getString("type");
                String id = copyData.getString("id");
                copyDataMap.put(type, id == null ? "" : id);
            }
            variableMap.put(FlowConstant.COPY_DATA_MAP_PREFIX + flowTaskExt.getTaskId(), JSON.toJSONString(copyDataMap));
        }
    }

    private Map<String, Object> buildPostCandidateGroupData(
            BaseFlowIdentityExtHelper flowIdentityExtHelper, List<FlowTaskExt> flowTaskExtList) {
        Map<String, Object> postVariableMap = new HashMap<>();
        Set<String> selfPostIdSet = new HashSet<>();
        Set<String> upPostIdSet = new HashSet<>();
        for (FlowTaskExt flowTaskExt : flowTaskExtList) {
            if (flowTaskExt.getGroupType().equals(FlowConstant.GROUP_TYPE_POST)) {
                Assert.notNull(flowTaskExt.getDeptPostListJson());
                List<FlowTaskPostCandidateGroup> groupDataList =
                        JSONArray.parseArray(flowTaskExt.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
                for (FlowTaskPostCandidateGroup groupData : groupDataList) {
                    if (groupData.getType().equals(FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR)) {
                        selfPostIdSet.add(groupData.getPostId());
                    } else if (groupData.getType().equals(FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR)) {
                        upPostIdSet.add(groupData.getPostId());
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(selfPostIdSet)) {
            Map<String, String> deptPostIdMap =
                    flowIdentityExtHelper.getDeptPostIdMap(this.getLoginUser().getDeptId(), selfPostIdSet);
            for (String postId : selfPostIdSet) {
                if (MapUtil.isNotEmpty(deptPostIdMap) && deptPostIdMap.containsKey(postId)) {
                    String deptPostId = deptPostIdMap.get(postId);
                    postVariableMap.put(FlowConstant.SELF_DEPT_POST_PREFIX + postId, deptPostId);
                } else {
                    postVariableMap.put(FlowConstant.SELF_DEPT_POST_PREFIX + postId, "");
                }
            }
        }
        if (CollUtil.isNotEmpty(upPostIdSet)) {
            Map<String, String> upDeptPostIdMap =
                    flowIdentityExtHelper.getUpDeptPostIdMap(this.getLoginUser().getDeptId(), upPostIdSet);
            for (String postId : upPostIdSet) {
                if (MapUtil.isNotEmpty(upDeptPostIdMap) && upDeptPostIdMap.containsKey(postId)) {
                    String upDeptPostId = upDeptPostIdMap.get(postId);
                    postVariableMap.put(FlowConstant.UP_DEPT_POST_PREFIX + postId, upDeptPostId);
                } else {
                    postVariableMap.put(FlowConstant.UP_DEPT_POST_PREFIX + postId, "");
                }
            }
        }
        return postVariableMap;
    }

    @Override
    public boolean isAssigneeOrCandidate(TaskInfo task) {
        String loginName = this.getLoginUser().getNickName();
        if (StrUtil.isNotBlank(task.getAssignee())) {
            return StrUtil.equals(loginName, task.getAssignee());
        }
        TaskQuery query = taskService.createTaskQuery();
        this.buildCandidateCondition(query, loginName);
        return query.active().count() != 0;
    }

    @Override
    public Collection<FlowElement> getProcessAllElements(String processDefinitionId) {
        Process process = repositoryService.getBpmnModel(processDefinitionId).getProcesses().get(0);
        return this.getAllElements(process.getFlowElements(), null);
    }

    @Override
    public boolean isProcessInstanceStarter(String processInstanceId) {
        String loginName = this.getLoginUser().getNickName();
        return historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).startedBy(loginName).count() != 0;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void setBusinessKeyForProcessInstance(String processInstanceId, Object dataId) {
        runtimeService.updateBusinessKey(processInstanceId, dataId.toString());
    }

    @Override
    public boolean existActiveProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).active().count() != 0;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public Task getProcessInstanceActiveTask(String processInstanceId, String taskId) {
        TaskQuery query = taskService.createTaskQuery().processInstanceId(processInstanceId);
        if (StrUtil.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        return query.active().singleResult();
    }

    @Override
    public List<Task> getProcessInstanceActiveTaskList(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public Task getTaskById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public PageInfo<Task> getTaskListByUserName(
            String username, String definitionKey, String definitionName, String taskName, PageBean pageParam) {
        PageInfo<Task> result = new PageInfo<>();
        TaskQuery query = taskService.createTaskQuery().active();
        if (StrUtil.isNotBlank(definitionKey)) {
            query.processDefinitionKey(definitionKey);
        }
        if (StrUtil.isNotBlank(definitionName)) {
            query.processDefinitionNameLike("%" + definitionName + "%");
        }
        if (StrUtil.isNotBlank(taskName)) {
            query.taskNameLike("%" + taskName + "%");
        }
        this.buildCandidateCondition(query, username);
        long totalCount = query.count();
        result.setTotal(totalCount);
        query.orderByTaskCreateTime().desc();
        int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        List<Task> taskList = query.listPage(firstResult, pageParam.getPageSize());
        result.setList(taskList);
        return result;
    }

    @Override
    public long getTaskCountByUserName(String username) {
        return taskService.createTaskQuery().taskCandidateOrAssigned(username).active().count();
    }

    @Override
    public List<Task> getTaskListByProcessInstanceIds(List<String> processInstanceIdSet) {
        return taskService.createTaskQuery().processInstanceIdIn(processInstanceIdSet).active().list();
    }

    @Override
    public List<ProcessInstance> getProcessInstanceList(Set<String> processInstanceIdSet) {
        return runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();
    }

    @Override
    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionList(Set<String> processDefinitionIdSet) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefinitionIdSet).list();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void suspendProcessDefinition(String processDefinitionId) {
        repositoryService.suspendProcessDefinitionById(processDefinitionId);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void activateProcessDefinition(String processDefinitionId) {
        repositoryService.activateProcessDefinitionById(processDefinitionId);
    }

    @Override
    public BpmnModel getBpmnModelByDefinitionId(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    @Override
    public ProcessDefinition getProcessDefinitionByDeployId(String deployId) {
        return repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String variableName) {
        return runtimeService.getVariable(processInstanceId, variableName);
    }

    @Override
    public List<FlowTaskVo> convertToFlowTaskList(List<Task> taskList) {
        List<FlowTaskVo> flowTaskVoList = new LinkedList<>();
        if (CollUtil.isEmpty(taskList)) {
            return flowTaskVoList;
        }
        Set<String> processDefinitionIdSet = taskList.stream()
                .map(Task::getProcessDefinitionId).collect(Collectors.toSet());
        Set<String> procInstanceIdSet = taskList.stream()
                .map(Task::getProcessInstanceId).collect(Collectors.toSet());
        List<FlowEntryPublish> flowEntryPublishList =
                flowEntryPublishService.selectByDefinitionIds(processDefinitionIdSet);
        Map<String, FlowEntryPublish> flowEntryPublishMap =
                flowEntryPublishList.stream().collect(Collectors.toMap(FlowEntryPublish::getProcessDefinitionId, c -> c));
        List<ProcessInstance> instanceList = this.getProcessInstanceList(procInstanceIdSet);
        Map<String, ProcessInstance> instanceMap =
                instanceList.stream().collect(Collectors.toMap(ProcessInstance::getId, c -> c));
        List<ProcessDefinition> definitionList = this.getProcessDefinitionList(processDefinitionIdSet);
        Map<String, ProcessDefinition> definitionMap =
                definitionList.stream().collect(Collectors.toMap(ProcessDefinition::getId, c -> c));
        for (Task task : taskList) {
            FlowTaskVo flowTaskVo = new FlowTaskVo();
            flowTaskVo.setTaskId(task.getId());
            flowTaskVo.setTaskName(task.getName());
            flowTaskVo.setTaskKey(task.getTaskDefinitionKey());
            flowTaskVo.setTaskFormKey(task.getFormKey());
            flowTaskVo.setEntryId(flowEntryPublishMap.get(task.getProcessDefinitionId()).getId());
            ProcessDefinition processDefinition = definitionMap.get(task.getProcessDefinitionId());
            flowTaskVo.setDefinitionId(processDefinition.getId());
            flowTaskVo.setDefinitionName(processDefinition.getName());
            flowTaskVo.setDefinitionKey(processDefinition.getKey());
            flowTaskVo.setDefinitionVersion(processDefinition.getVersion());
            ProcessInstance processInstance = instanceMap.get(task.getProcessInstanceId());
            flowTaskVo.setInstanceId(processInstance.getId());
            Object initiator = this.getProcessInstanceVariable(
                    processInstance.getId(), FlowInitVariable.INITIATOR_VAR.getVariableName());
            flowTaskVo.setInitiator(initiator.toString());
            flowTaskVo.setStartTime(processInstance.getStartTime());
            flowTaskVo.setBusinessKey(processInstance.getBusinessKey());
            flowTaskVoList.add(flowTaskVo);
        }
        return flowTaskVoList;
    }

    @Override
    public void addProcessInstanceEndListener(BpmnModel bpmnModel, Class<? extends ExecutionListener> listenerClazz) {
        Assert.notNull(listenerClazz);
        Process process = bpmnModel.getMainProcess();
        FlowableListener activitiListener = new FlowableListener();
        activitiListener.setEvent("end");
        activitiListener.setImplementationType("class");
        activitiListener.setImplementation(listenerClazz.getName());
        process.getExecutionListeners().add(activitiListener);
    }

    @Override
    public void addTaskCreateListener(UserTask userTask, Class<? extends TaskListener> listenerClazz) {
        Assert.notNull(listenerClazz);
        FlowableListener activitiListener = new FlowableListener();
        activitiListener.setEvent("create");
        activitiListener.setImplementationType("class");
        activitiListener.setImplementation(listenerClazz.getName());
        userTask.getTaskListeners().add(activitiListener);
    }

    @Override
    public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public List<HistoricProcessInstance> getHistoricProcessInstanceList(Set<String> processInstanceIdSet) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();
    }

    @Override
    @SneakyThrows
    public PageInfo<HistoricProcessInstance> getHistoricProcessInstanceList(
            String processDefinitionKey,
            String processDefinitionName,
            String startUser,
            String beginDate,
            String endDate,
            PageBean pageParam,
            boolean finishedOnly) {
        PageInfo<HistoricProcessInstance> result = new PageInfo<>();
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StrUtil.isNotBlank(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }
        if (StrUtil.isNotBlank(processDefinitionName)) {
            query.processDefinitionName(processDefinitionName);
        }
        if (StrUtil.isNotBlank(startUser)) {
            query.startedBy(startUser);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            query.startedAfter(DateUtils.parseDate(beginDate, "yyyy-MM-dd"));
        }
        if (StrUtil.isNotBlank(endDate)) {
            query.startedBefore(DateUtils.parseDate(endDate, "yyyy-MM-dd"));
        }
        if (finishedOnly) {
            query.finished();
        }
        query.orderByProcessInstanceStartTime().desc();
        long totalCount = query.count();
        result.setTotal(totalCount);
        int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        List<HistoricProcessInstance> instanceList = query.listPage(firstResult, pageParam.getPageSize());
        result.setList(instanceList);
        return result;
    }

    @Override
    @SneakyThrows
    public PageInfo<HistoricTaskInstance> getHistoricTaskInstanceFinishedList(
            String processDefinitionName,
            String beginDate,
            String endDate,
            PageBean pageParam) {
        PageInfo<HistoricTaskInstance> result = new PageInfo<>();
        String loginName = this.getLoginUser().getNickName();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(loginName)
                .finished();
        if (StrUtil.isNotBlank(processDefinitionName)) {
            query.processDefinitionName(processDefinitionName);
        }
        if (StrUtil.isNotBlank(beginDate)) {
            query.taskCompletedAfter(DateUtils.parseDate(beginDate, "yyyy-MM-dd"));
        }
        if (StrUtil.isNotBlank(endDate)) {
            query.taskCompletedBefore(DateUtils.parseDate(endDate, "yyyy-MM-dd"));
        }
        long totalCount = query.count();
        result.setTotal(totalCount);
        query.orderByHistoricTaskInstanceEndTime().desc();
        int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
        List<HistoricTaskInstance> instanceList = query.listPage(firstResult, pageParam.getPageSize());
        result.setList(instanceList);
        return result;
    }

    @Override
    public List<HistoricActivityInstance> getHistoricActivityInstanceList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
    }

    @Override
    public List<HistoricActivityInstance> getHistoricActivityInstanceListOrderByStartTime(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
    }

    @Override
    public HistoricTaskInstance getHistoricTaskInstance(String processInstanceId, String taskId) {
        return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).taskId(taskId).singleResult();
    }

    @Override
    public List<HistoricActivityInstance> getHistoricUnfinishedInstanceList(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).unfinished().list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void stopProcessInstance(String processInstanceId, String stopReason, boolean forCancel) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        if (CollUtil.isEmpty(taskList)) {
            throw new CustomException("数据验证失败，当前流程尚未开始或已经结束！");
        }
        for (Task task : taskList) {
            String currActivityId = task.getTaskDefinitionKey();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            FlowNode currFlow = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currActivityId);
            if (currFlow == null) {
                List<SubProcess> subProcessList =
                        bpmnModel.getMainProcess().findFlowElementsOfType(SubProcess.class);
                for (SubProcess subProcess : subProcessList) {
                    FlowElement flowElement = subProcess.getFlowElement(currActivityId);
                    if (flowElement != null) {
                        currFlow = (FlowNode) flowElement;
                        break;
                    }
                }
            }
            EndEvent endEvent = bpmnModel.getMainProcess()
                    .findFlowElementsOfType(EndEvent.class, false).get(0);
            if (!(currFlow.getParentContainer().equals(endEvent.getParentContainer()))) {
                throw new CustomException("数据验证失败，不能从子流程直接中止！");
            }
            // 保存原有的输出方向。
            List<SequenceFlow> oriSequenceFlows = Lists.newArrayList();
            oriSequenceFlows.addAll(currFlow.getOutgoingFlows());
            // 清空原有方向。
            currFlow.getOutgoingFlows().clear();
            // 建立新方向。
            SequenceFlow newSequenceFlow = new SequenceFlow();
            String uuid = UUID.randomUUID().toString().replace("-", "");
            newSequenceFlow.setId(uuid);
            newSequenceFlow.setSourceFlowElement(currFlow);
            newSequenceFlow.setTargetFlowElement(endEvent);
            currFlow.setOutgoingFlows(CollUtil.newArrayList(newSequenceFlow));
            // 完成任务并跳转到新方向。
            taskService.complete(task.getId());
            FlowTaskComment taskComment = new FlowTaskComment(task);
            taskComment.setApprovalType(FlowApprovalType.STOP.getCode());
            taskComment.setRemark(stopReason);
            flowTaskCommentService.insert(taskComment);
            // 回复原有输出方向。
            currFlow.setOutgoingFlows(oriSequenceFlows);
        }
        String status = FlowTaskStatus.STOPPED.getCode();
        if (forCancel) {
            status = FlowTaskStatus.CANCELLED.getCode();
        }
        flowWorkOrderService.updateFlowStatusByProcessInstanceId(processInstanceId, status);
        flowMessageService.updateFinishedStatusByProcessInstanceId(processInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteProcessInstance(String processInstanceId) {
        historyService.deleteHistoricProcessInstance(processInstanceId);
        flowWorkOrderService.removeByProcessInstanceId(processInstanceId);
        flowMessageService.removeByProcessInstanceId(processInstanceId);
    }

    @Override
    public Object getTaskVariable(String taskId, String variableName) {
        return taskService.getVariable(taskId, variableName);
    }

    @Override
    public BpmnModel convertToBpmnModel(String bpmnXml) throws XMLStreamException {
        BpmnXMLConverter converter = new BpmnXMLConverter();
        InputStream in = new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8));
        @Cleanup XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
        return converter.convertToBpmnModel(reader);
    }

    @Override
    @Transactional
    public void backToRuntimeTask(Task task, String targetKey, boolean forReject, String reason) {
        ProcessDefinition processDefinition = this.getProcessDefinitionById(task.getProcessDefinitionId());
        Collection<FlowElement> allElements = this.getProcessAllElements(processDefinition.getId());
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        for (FlowElement flowElement : allElements) {
            if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                source = flowElement;
                if (StrUtil.isBlank(targetKey)) {
                    break;
                }
            }
            if (StrUtil.isNotBlank(targetKey)) {
                if (flowElement.getId().equals(targetKey)) {
                    target = flowElement;
                }
            }
        }
        if (targetKey != null && target == null) {
            throw new CustomException("数据验证失败，被驳回的指定目标节点不存在！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        UserTask oneUserTask = null;
        List<String> targetIds = null;
        if (target == null) {
            List<UserTask> parentUserTaskList =
                    this.getParentUserTaskList(source, null, null);
            if (CollUtil.isEmpty(parentUserTaskList)) {
                throw new CustomException("数据验证失败，当前节点为初始任务节点，不能驳回！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
            // 获取活动ID, 即节点Key
            Set<String> parentUserTaskKeySet = new HashSet<>();
            parentUserTaskList.forEach(item -> parentUserTaskKeySet.add(item.getId()));
            List<HistoricActivityInstance> historicActivityIdList =
                    this.getHistoricActivityInstanceListOrderByStartTime(task.getProcessInstanceId());
            // 数据清洗，将回滚导致的脏数据清洗掉
            List<String> lastHistoricTaskInstanceList =
                    this.cleanHistoricTaskInstance(allElements, historicActivityIdList);
            // 此时历史任务实例为倒序，获取最后走的节点
            targetIds = new ArrayList<>();
            // 循环结束标识，遇到当前目标节点的次数
            int number = 0;
            StringBuilder parentHistoricTaskKey = new StringBuilder();
            for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
                // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
                if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                    continue;
                }
                parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
                if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                    number++;
                }
                if (number == 2) {
                    break;
                }
                // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
                if (parentUserTaskKeySet.contains(historicTaskInstanceKey)) {
                    targetIds.add(historicTaskInstanceKey);
                }
            }
            // 目的获取所有需要被跳转的节点 currentIds
            // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
            oneUserTask = parentUserTaskList.get(0);
        }
        // 获取所有正常进行的执行任务的活动节点ID，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Execution> runExecutionList =
                runtimeService.createExecutionQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runActivityIdList = runExecutionList.stream()
                .filter(c -> StrUtil.isNotBlank(c.getActivityId()))
                .map(Execution::getActivityId).collect(Collectors.toList());
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runExecutionList 比对，获取需要撤回的任务
        List<FlowElement> currentFlowElementList = this.getChildUserTaskList(
                target != null ? target : oneUserTask, runActivityIdList, null, null);
        currentFlowElementList.forEach(item -> currentIds.add(item.getId()));
        if (target == null) {
            // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
            if (targetIds.size() > 1 && currentIds.size() > 1) {
                throw new CustomException("数据验证失败，任务出现多对多情况，无法撤回！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
        }
        AtomicReference<List<HistoricActivityInstance>> tmp = new AtomicReference<>();
        // 用于下面新增网关删除信息时使用
        String targetTmp = targetKey != null ? targetKey : String.join(",", targetIds);
        // currentIds 为活动ID列表
        // currentExecutionIds 为执行任务ID列表
        // 需要通过执行任务ID来设置驳回信息，活动ID不行
        currentIds.forEach(currentId -> runExecutionList.forEach(runExecution -> {
            if (StrUtil.isNotBlank(runExecution.getActivityId()) && currentId.equals(runExecution.getActivityId())) {
                // 查询当前节点的执行任务的历史数据
                tmp.set(historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId())
                        .executionId(runExecution.getId())
                        .activityId(runExecution.getActivityId()).list());
                // 如果这个列表的数据只有 1 条数据
                // 网关肯定只有一条，且为包容网关或并行网关
                // 这里的操作目的是为了给网关在扭转前提前加上删除信息，结构与普通节点的删除信息一样，目的是为了知道这个网关也是有经过跳转的
                if (tmp.get() != null && tmp.get().size() == 1 && StrUtil.isNotBlank(tmp.get().get(0).getActivityType())
                        && ("parallelGateway".equals(tmp.get().get(0).getActivityType()) || "inclusiveGateway".equals(tmp.get().get(0).getActivityType()))) {
                    // singleResult 能够执行更新操作
                    // 利用 流程实例ID + 执行任务ID + 活动节点ID 来指定唯一数据，保证数据正确
                    historyService.createNativeHistoricActivityInstanceQuery().sql(
                            "UPDATE ACT_HI_ACTINST SET DELETE_REASON_ = 'Change activity to " + targetTmp + "'  WHERE PROC_INST_ID_='" + task.getProcessInstanceId() + "' AND EXECUTION_ID_='" + runExecution.getId() + "' AND ACT_ID_='" + runExecution.getActivityId() + "'").singleResult();
                }
            }
        }));
        try {
            if (StrUtil.isNotBlank(targetKey)) {
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId())
                        .moveActivityIdsToSingleActivityId(currentIds, targetKey).changeState();
            } else {
                // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
                if (targetIds.size() > 1) {
                    // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                    ChangeActivityStateBuilder builder = runtimeService.createChangeActivityStateBuilder()
                            .processInstanceId(task.getProcessInstanceId())
                            .moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds);
                    for (String targetId : targetIds) {
                        FlowTaskComment taskComment =
                                flowTaskCommentService.getLatestFlowTaskComment(task.getProcessInstanceId(), targetId);
                        // 如果驳回后的目标任务包含指定人，则直接通过变量回抄，如果没有则自动忽略该变量，不会给流程带来任何影响。
                        String submitLoginName = taskComment.getCreateBy();
                        if (StrUtil.isNotBlank(submitLoginName)) {
                            builder.localVariable(targetId, FlowConstant.TASK_APPOINTED_ASSIGNEE_VAR, submitLoginName);
                        }
                    }
                    builder.changeState();
                }
                // 如果父级任务只有一个，因此当前任务可能为网关中的任务
                if (targetIds.size() == 1) {
                    // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                    // 如果驳回后的目标任务包含指定人，则直接通过变量回抄，如果没有则自动忽略该变量，不会给流程带来任何影响。
                    ChangeActivityStateBuilder builder = runtimeService.createChangeActivityStateBuilder()
                            .processInstanceId(task.getProcessInstanceId())
                            .moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0));
                    FlowTaskComment taskComment =
                            flowTaskCommentService.getLatestFlowTaskComment(task.getProcessInstanceId(), targetIds.get(0));
                    String submitLoginName = taskComment.getCreateName();
                    if (StrUtil.isNotBlank(submitLoginName)) {
                        builder.localVariable(targetIds.get(0), FlowConstant.TASK_APPOINTED_ASSIGNEE_VAR, submitLoginName);
                    }
                    builder.changeState();
                }
            }
            FlowTaskComment comment = new FlowTaskComment();
            comment.setTaskId(task.getId());
            comment.setTaskKey(task.getTaskDefinitionKey());
            comment.setTaskName(task.getName());
            comment.setApprovalType(forReject ? FlowApprovalType.REJECT.getCode() : FlowApprovalType.REVOKE.getName());
            comment.setProcessInstanceId(task.getProcessInstanceId());
            comment.setRemark(reason);
            flowTaskCommentService.insert(comment);
        } catch (Exception e) {
            log.error("Failed to execute moveSingleActivityIdToActivityIds", e);
            throw new CustomException(e.getMessage());
        }
    }

    private List<UserTask> getParentUserTaskList(
            FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            userTaskList = getParentUserTaskList(source.getSubProcess(), hasSequenceFlow, userTaskList);
        }
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 类型为用户节点，则新增父级节点
                if (sequenceFlow.getSourceFlowElement() instanceof UserTask) {
                    userTaskList.add((UserTask) sequenceFlow.getSourceFlowElement());
                    continue;
                }
                // 类型为子流程，则添加子流程开始节点出口处相连的节点
                if (sequenceFlow.getSourceFlowElement() instanceof SubProcess) {
                    // 获取子流程用户任务节点
                    List<UserTask> childUserTaskList = findChildProcessUserTasks(
                            (StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 网关场景的继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                userTaskList = getParentUserTaskList(
                        sequenceFlow.getSourceFlowElement(), new HashSet<>(hasSequenceFlow), userTaskList);
            }
        }
        return userTaskList;
    }

    private List<FlowElement> getChildUserTaskList(
            FlowElement source, List<String> runActiveIdList, Set<String> hasSequenceFlow, List<FlowElement> flowElementList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        flowElementList = flowElementList == null ? new ArrayList<>() : flowElementList;
        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof EndEvent && source.getSubProcess() != null) {
            flowElementList = getChildUserTaskList(
                    source.getSubProcess(), runActiveIdList, hasSequenceFlow, flowElementList);
        }
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果为用户任务类型，或者为网关
                // 活动节点ID 在运行的任务中存在，添加
                FlowElement targetElement = sequenceFlow.getTargetFlowElement();
                if ((targetElement instanceof UserTask || targetElement instanceof Gateway)
                        && runActiveIdList.contains(targetElement.getId())) {
                    flowElementList.add(sequenceFlow.getTargetFlowElement());
                    continue;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    List<FlowElement> childUserTaskList = getChildUserTaskList(
                            (FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), runActiveIdList, hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        flowElementList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                flowElementList = getChildUserTaskList(
                        sequenceFlow.getTargetFlowElement(), runActiveIdList, new HashSet<>(hasSequenceFlow), flowElementList);
            }
        }
        return flowElementList;
    }

    private List<String> cleanHistoricTaskInstance(
            Collection<FlowElement> allElements, List<HistoricActivityInstance> historicActivityList) {
        // 会签节点收集
        List<String> multiTask = new ArrayList<>();
        allElements.forEach(flowElement -> {
            if (flowElement instanceof UserTask) {
                // 如果该节点的行为为会签行为，说明该节点为会签节点
                if (((UserTask) flowElement).getBehavior() instanceof ParallelMultiInstanceBehavior
                        || ((UserTask) flowElement).getBehavior() instanceof SequentialMultiInstanceBehavior) {
                    multiTask.add(flowElement.getId());
                }
            }
        });
        // 循环放入栈，栈 LIFO：后进先出
        Stack<HistoricActivityInstance> stack = new Stack<>();
        historicActivityList.forEach(stack::push);
        // 清洗后的历史任务实例
        List<String> lastHistoricTaskInstanceList = new ArrayList<>();
        // 网关存在可能只走了部分分支情况，且还存在跳转废弃数据以及其他分支数据的干扰，因此需要对历史节点数据进行清洗
        // 临时用户任务 key
        StringBuilder userTaskKey = null;
        // 临时被删掉的任务 key，存在并行情况
        List<String> deleteKeyList = new ArrayList<>();
        // 临时脏数据线路
        List<Set<String>> dirtyDataLineList = new ArrayList<>();
        // 由某个点跳到会签点,此时出现多个会签实例对应 1 个跳转情况，需要把这些连续脏数据都找到
        // 会签特殊处理下标
        int multiIndex = -1;
        // 会签特殊处理 key
        StringBuilder multiKey = null;
        // 会签特殊处理操作标识
        boolean multiOpera = false;
        while (!stack.empty()) {
            // 从这里开始 userTaskKey 都还是上个栈的 key
            // 是否是脏数据线路上的点
            final boolean[] isDirtyData = {false};
            for (Set<String> oldDirtyDataLine : dirtyDataLineList) {
                if (oldDirtyDataLine.contains(stack.peek().getActivityId())) {
                    isDirtyData[0] = true;
                }
            }
            // 删除原因不为空，说明从这条数据开始回跳或者回退的
            // MI_END：会签完成后，其他未签到节点的删除原因，不在处理范围内
            if (stack.peek().getDeleteReason() != null && !"MI_END".equals(stack.peek().getDeleteReason())) {
                // 可以理解为脏线路起点
                String dirtyPoint = "";
                if (stack.peek().getDeleteReason().contains("Change activity to ")) {
                    dirtyPoint = stack.peek().getDeleteReason().replace("Change activity to ", "");
                }
                // 会签回退删除原因有点不同
                if (stack.peek().getDeleteReason().contains("Change parent activity to ")) {
                    dirtyPoint = stack.peek().getDeleteReason().replace("Change parent activity to ", "");
                }
                FlowElement dirtyTask = null;
                // 获取变更节点的对应的入口处连线
                // 如果是网关并行回退情况，会变成两条脏数据路线，效果一样
                for (FlowElement flowElement : allElements) {
                    if (flowElement.getId().equals(stack.peek().getActivityId())) {
                        dirtyTask = flowElement;
                    }
                }
                // 获取脏数据线路
                Set<String> dirtyDataLine = findDirtyRoads(
                        dirtyTask, null, null, StrUtil.split(dirtyPoint, ','), null);
                // 自己本身也是脏线路上的点，加进去
                dirtyDataLine.add(stack.peek().getActivityId());
                log.info(stack.peek().getActivityId() + "点脏路线集合：" + dirtyDataLine);
                // 是全新的需要添加的脏线路
                boolean isNewDirtyData = true;
                for (Set<String> strings : dirtyDataLineList) {
                    // 如果发现他的上个节点在脏线路内，说明这个点可能是并行的节点，或者连续驳回
                    // 这时，都以之前的脏线路节点为标准，只需合并脏线路即可，也就是路线补全
                    if (strings.contains(userTaskKey.toString())) {
                        isNewDirtyData = false;
                        strings.addAll(dirtyDataLine);
                    }
                }
                // 已确定时全新的脏线路
                if (isNewDirtyData) {
                    // deleteKey 单一路线驳回到并行，这种同时生成多个新实例记录情况，这时 deleteKey 其实是由多个值组成
                    // 按照逻辑，回退后立刻生成的实例记录就是回退的记录
                    // 至于驳回所生成的 Key，直接从删除原因中获取，因为存在驳回到并行的情况
                    deleteKeyList.add(dirtyPoint + ",");
                    dirtyDataLineList.add(dirtyDataLine);
                }
                // 添加后，现在这个点变成脏线路上的点了
                isDirtyData[0] = true;
            }
            // 如果不是脏线路上的点，说明是有效数据，添加历史实例 Key
            if (!isDirtyData[0]) {
                lastHistoricTaskInstanceList.add(stack.peek().getActivityId());
            }
            // 校验脏线路是否结束
            for (int i = 0; i < deleteKeyList.size(); i++) {
                // 如果发现脏数据属于会签，记录下下标与对应 Key，以备后续比对，会签脏数据范畴开始
                if (multiKey == null && multiTask.contains(stack.peek().getActivityId())
                        && deleteKeyList.get(i).contains(stack.peek().getActivityId())) {
                    multiIndex = i;
                    multiKey = new StringBuilder(stack.peek().getActivityId());
                }
                // 会签脏数据处理，节点退回会签清空
                // 如果在会签脏数据范畴中发现 Key改变，说明会签脏数据在上个节点就结束了，可以把会签脏数据删掉
                if (multiKey != null && !multiKey.toString().equals(stack.peek().getActivityId())) {
                    deleteKeyList.set(multiIndex, deleteKeyList.get(multiIndex).replace(stack.peek().getActivityId() + ",", ""));
                    multiKey = null;
                    // 结束进行下校验删除
                    multiOpera = true;
                }
                // 其他脏数据处理
                // 发现该路线最后一条脏数据，说明这条脏数据线路处理完了，删除脏数据信息
                // 脏数据产生的新实例中是否包含这条数据
                if (multiKey == null && deleteKeyList.get(i).contains(stack.peek().getActivityId())) {
                    // 删除匹配到的部分
                    deleteKeyList.set(i, deleteKeyList.get(i).replace(stack.peek().getActivityId() + ",", ""));
                }
                // 如果每组中的元素都以匹配过，说明脏数据结束
                if ("".equals(deleteKeyList.get(i))) {
                    // 同时删除脏数据
                    deleteKeyList.remove(i);
                    dirtyDataLineList.remove(i);
                    break;
                }
            }
            // 会签数据处理需要在循环外处理，否则可能导致溢出
            // 会签的数据肯定是之前放进去的所以理论上不会溢出，但还是校验下
            if (multiOpera && deleteKeyList.size() > multiIndex && "".equals(deleteKeyList.get(multiIndex))) {
                // 同时删除脏数据
                deleteKeyList.remove(multiIndex);
                dirtyDataLineList.remove(multiIndex);
                multiIndex = -1;
                multiOpera = false;
            }
            // pop() 方法与 peek() 方法不同，在返回值的同时，会把值从栈中移除
            // 保存新的 userTaskKey 在下个循环中使用
            userTaskKey = new StringBuilder(stack.pop().getActivityId());
        }
        log.info("清洗后的历史节点数据：" + lastHistoricTaskInstanceList);
        return lastHistoricTaskInstanceList;
    }

    private void handleMultiInstanceApprovalType(String executionId, String approvalType, JSONObject taskVariableData) {
        if (StrUtil.isBlank(approvalType)) {
            return;
        }
        if (StrUtil.equalsAny(approvalType,
                FlowApprovalType.MULTI_AGREE.getCode(),
                FlowApprovalType.MULTI_REFUSE.getCode(),
                FlowApprovalType.MULTI_ABSTAIN.getCode())) {
            Map<String, Object> variables = runtimeService.getVariables(executionId);
            Integer agreeCount = (Integer) variables.get(FlowConstant.MULTI_AGREE_COUNT_VAR);
            Integer refuseCount = (Integer) variables.get(FlowConstant.MULTI_REFUSE_COUNT_VAR);
            Integer abstainCount = (Integer) variables.get(FlowConstant.MULTI_ABSTAIN_COUNT_VAR);
            Integer nrOfInstances = (Integer) variables.get(FlowConstant.NUMBER_OF_INSTANCES_VAR);
            taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, agreeCount);
            taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, refuseCount);
            taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, abstainCount);
            taskVariableData.put(FlowConstant.MULTI_SIGN_NUM_OF_INSTANCES_VAR, nrOfInstances);
            if (approvalType.equals(FlowApprovalType.MULTI_AGREE.getCode())) {
                if (agreeCount == null) {
                    agreeCount = 0;
                }
                taskVariableData.put(FlowConstant.MULTI_AGREE_COUNT_VAR, agreeCount + 1);
            } else if (FlowApprovalType.MULTI_REFUSE.getCode().equals(approvalType)) {
                if (refuseCount == null) {
                    refuseCount = 0;
                }
                taskVariableData.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, refuseCount + 1);
            } else if (FlowApprovalType.MULTI_ABSTAIN.getCode().equals(approvalType)) {
                if (abstainCount == null) {
                    abstainCount = 0;
                }
                taskVariableData.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, abstainCount + 1);
            }
        }
    }

    private void buildCandidateCondition(TaskQuery query, String loginName) {
        Set<String> groupIdSet = new HashSet<>();
        // NOTE: 需要注意的是，部门Id、部门岗位Id，或者其他类型的分组Id，他们之间一定不能重复。
        LoginUser tokenData = this.getLoginUser();
        Object deptId = tokenData.getDeptId();
        if (deptId != null) {
            groupIdSet.add(deptId.toString());
        }
        String roleIds = tokenData.getRoleIds();
        if (StrUtil.isNotBlank(tokenData.getRoleIds())) {
            groupIdSet.addAll(Arrays.asList(StrUtil.split(roleIds, ",")));
        }
        groupIdSet.addAll(tokenData.getPostIds());
        String deptPostIds = tokenData.getDeptPostIds();
        if (StrUtil.isNotBlank(deptPostIds)) {
            groupIdSet.addAll(Arrays.asList(StrUtil.split(deptPostIds, ",")));
        }
        if (CollUtil.isNotEmpty(groupIdSet)) {
            query.or().taskCandidateGroupIn(groupIdSet).taskCandidateOrAssigned(loginName).endOr();
        } else {
            query.taskCandidateOrAssigned(loginName);
        }
    }

    private String buildMutiSignAssigneeList(String operationListJson) {
        FlowTaskMultiSignAssign multiSignAssignee = null;
        List<FlowTaskOperation> taskOperationList = JSONArray.parseArray(operationListJson, FlowTaskOperation.class);
        for (FlowTaskOperation taskOperation : taskOperationList) {
            if ("multi_sign".equals(taskOperation.getType())) {
                multiSignAssignee = taskOperation.getMultiSignAssignee();
                break;
            }
        }
        Assert.notNull(multiSignAssignee);
        if (FlowTaskMultiSignAssign.ASSIGN_TYPE_USER.equals(multiSignAssignee.getAssigneeType())) {
            return multiSignAssignee.getAssigneeList();
        }
        Set<String> usernameSet = null;
        BaseFlowIdentityExtHelper extHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        Set<String> idSet = CollUtil.newHashSet(StrUtil.split(multiSignAssignee.getAssigneeList(), ","));
        switch (multiSignAssignee.getAssigneeType()) {
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_ROLE:
                usernameSet = extHelper.getUsernameListByRoleIds(idSet);
                break;
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_DEPT:
                usernameSet = extHelper.getUsernameListByDeptIds(idSet);
                break;
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_POST:
                usernameSet = extHelper.getUsernameListByPostIds(idSet);
                break;
            case FlowTaskMultiSignAssign.ASSIGN_TYPE_DEPT_POST:
                usernameSet = extHelper.getUsernameListByDeptPostIds(idSet);
                break;
            default:
                break;
        }
        return CollUtil.isEmpty(usernameSet) ? null : CollUtil.join(usernameSet, ",");
    }

    private Collection<FlowElement> getAllElements(Collection<FlowElement> flowElements, Collection<FlowElement> allElements) {
        allElements = allElements == null ? new ArrayList<>() : allElements;
        for (FlowElement flowElement : flowElements) {
            allElements.add(flowElement);
            if (flowElement instanceof SubProcess) {
                allElements = getAllElements(((SubProcess) flowElement).getFlowElements(), allElements);
            }
        }
        return allElements;
    }

    private List<SequenceFlow> getElementIncomingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = null;
        if (source instanceof org.flowable.bpmn.model.Task) {
            sequenceFlows = ((org.flowable.bpmn.model.Task) source).getIncomingFlows();
        } else if (source instanceof Gateway) {
            sequenceFlows = ((Gateway) source).getIncomingFlows();
        } else if (source instanceof SubProcess) {
            sequenceFlows = ((SubProcess) source).getIncomingFlows();
        } else if (source instanceof StartEvent) {
            sequenceFlows = ((StartEvent) source).getIncomingFlows();
        } else if (source instanceof EndEvent) {
            sequenceFlows = ((EndEvent) source).getIncomingFlows();
        }
        return sequenceFlows;
    }

    private List<SequenceFlow> getElementOutgoingFlows(FlowElement source) {
        List<SequenceFlow> sequenceFlows = null;
        if (source instanceof org.flowable.bpmn.model.Task) {
            sequenceFlows = ((org.flowable.bpmn.model.Task) source).getOutgoingFlows();
        } else if (source instanceof Gateway) {
            sequenceFlows = ((Gateway) source).getOutgoingFlows();
        } else if (source instanceof SubProcess) {
            sequenceFlows = ((SubProcess) source).getOutgoingFlows();
        } else if (source instanceof StartEvent) {
            sequenceFlows = ((StartEvent) source).getOutgoingFlows();
        } else if (source instanceof EndEvent) {
            sequenceFlows = ((EndEvent) source).getOutgoingFlows();
        }
        return sequenceFlows;
    }

    private List<UserTask> findChildProcessUserTasks(FlowElement source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果为用户任务类型，且任务节点的 Key 正在运行的任务中存在，添加
                if (sequenceFlow.getTargetFlowElement() instanceof UserTask) {
                    userTaskList.add((UserTask) sequenceFlow.getTargetFlowElement());
                    continue;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    List<UserTask> childUserTaskList = findChildProcessUserTasks((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, null);
                    // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
                    if (childUserTaskList != null && childUserTaskList.size() > 0) {
                        userTaskList.addAll(childUserTaskList);
                        continue;
                    }
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                userTaskList = findChildProcessUserTasks(sequenceFlow.getTargetFlowElement(), new HashSet<>(hasSequenceFlow), userTaskList);
            }
        }
        return userTaskList;
    }

    private Set<String> findDirtyRoads(
            FlowElement source, List<String> passRoads, Set<String> hasSequenceFlow, List<String> targets, Set<String> dirtyRoads) {
        passRoads = passRoads == null ? new ArrayList<>() : passRoads;
        dirtyRoads = dirtyRoads == null ? new HashSet<>() : dirtyRoads;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
        if (source instanceof StartEvent && source.getSubProcess() != null) {
            dirtyRoads = findDirtyRoads(source.getSubProcess(), passRoads, hasSequenceFlow, targets, dirtyRoads);
        }
        // 根据类型，获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 新增经过的路线
                passRoads.add(sequenceFlow.getSourceFlowElement().getId());
                // 如果此点为目标点，确定经过的路线为脏线路，添加点到脏线路中，然后找下个连线
                if (targets.contains(sequenceFlow.getSourceFlowElement().getId())) {
                    dirtyRoads.addAll(passRoads);
                    continue;
                }
                // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
                if (sequenceFlow.getSourceFlowElement() instanceof SubProcess) {
                    dirtyRoads = findChildProcessAllDirtyRoad(
                            (StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, dirtyRoads);
                    // 是否存在子流程上，true 是，false 否
                    Boolean isInChildProcess = dirtyTargetInChildProcess(
                            (StartEvent) ((SubProcess) sequenceFlow.getSourceFlowElement()).getFlowElements().toArray()[0], null, targets, null);
                    if (isInChildProcess) {
                        // 已在子流程上找到，该路线结束
                        continue;
                    }
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                dirtyRoads = findDirtyRoads(sequenceFlow.getSourceFlowElement(),
                        new ArrayList<>(passRoads), new HashSet<>(hasSequenceFlow), targets, dirtyRoads);
            }
        }
        return dirtyRoads;
    }

    private Set<String> findChildProcessAllDirtyRoad(
            FlowElement source, Set<String> hasSequenceFlow, Set<String> dirtyRoads) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        dirtyRoads = dirtyRoads == null ? new HashSet<>() : dirtyRoads;
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 添加脏路线
                dirtyRoads.add(sequenceFlow.getTargetFlowElement().getId());
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    dirtyRoads = findChildProcessAllDirtyRoad(
                            (FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, dirtyRoads);
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                dirtyRoads = findChildProcessAllDirtyRoad(
                        sequenceFlow.getTargetFlowElement(), new HashSet<>(hasSequenceFlow), dirtyRoads);
            }
        }
        return dirtyRoads;
    }

    private Boolean dirtyTargetInChildProcess(
            FlowElement source, Set<String> hasSequenceFlow, List<String> targets, Boolean inChildProcess) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        inChildProcess = inChildProcess != null && inChildProcess;
        // 根据类型，获取出口连线
        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(source);
        if (sequenceFlows != null && !inChildProcess) {
            // 循环找到目标元素
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                // 如果发现连线重复，说明循环了，跳过这个循环
                if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                    continue;
                }
                // 添加已经走过的连线
                hasSequenceFlow.add(sequenceFlow.getId());
                // 如果发现目标点在子流程上存在，说明只到子流程为止
                if (targets.contains(sequenceFlow.getTargetFlowElement().getId())) {
                    inChildProcess = true;
                    break;
                }
                // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
                if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
                    inChildProcess = dirtyTargetInChildProcess((FlowElement) (((SubProcess) sequenceFlow.getTargetFlowElement()).getFlowElements().toArray()[0]), hasSequenceFlow, targets, inChildProcess);
                }
                // 继续迭代
                // 注意：已经经过的节点与连线都应该用浅拷贝出来的对象
                // 比如分支：a->b->c与a->d->c，走完a->b->c后走另一个路线是，已经经过的节点应该不包含a->b->c路线的数据
                inChildProcess = dirtyTargetInChildProcess(sequenceFlow.getTargetFlowElement(), new HashSet<>(hasSequenceFlow), targets, inChildProcess);
            }
        }
        return inChildProcess;
    }

    @Override
    public Set<String> buildGroupIdSet() {
        Set<String> groupIdSet = new HashSet<>(1);
        groupIdSet.add(this.getLoginUser().getId());
        groupIdSet.addAll(this.getLoginUser().getPostIds());
        this.parseAndAddIdArray(groupIdSet, this.getLoginUser().getDeptId());
        this.parseAndAddIdArray(groupIdSet, this.getLoginUser().getDeptPostIds());
        if (this.getLoginUser().getDeptId() != null) {
            groupIdSet.add(this.getLoginUser().getDeptId());
        }
        return groupIdSet;
    }

    private void parseAndAddIdArray(Set<String> groupIdSet, String idArray) {
        if (StrUtil.isNotBlank(idArray)) {
            if (groupIdSet == null) {
                groupIdSet = new HashSet<>();
            }
            groupIdSet.addAll(StrUtil.split(idArray, ','));
        }
    }

    private LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
