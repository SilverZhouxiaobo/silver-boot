package cn.silver.framework.flow.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.silver.framework.core.api.ISysBaseApi;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.flow.domain.dto.FlowCommentDto;
import cn.silver.framework.flow.domain.dto.FlowTaskDto;
import cn.silver.framework.flow.factory.FlowServiceFactory;
import cn.silver.framework.flow.flow.FlowableUtils;
import cn.silver.framework.flow.service.IFlowTaskService;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import cn.silver.framework.workflow.vo.FlowTaskVo;
import com.github.pagehelper.PageInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author XuanXuan
 * @date 2021-04-03
 **/
@Slf4j
@Service
public class FlowTaskServiceImpl extends FlowServiceFactory implements IFlowTaskService {
    @Autowired
    private ISysBaseApi baseApi;
    @Autowired
    private IFlowWorkOrderService orderService;

    @Override
    @SneakyThrows
    public PageInfo<FlowTaskVo> todoList(PageBean pageParam, FlowTaskVo vo) {
        PageInfo<FlowTaskVo> result = new PageInfo<>();
        TaskQuery query = taskService.createTaskQuery().active();
        if (StringUtils.isNotBlank(vo.getDefinitionKey())) {
            query.processDefinitionKey(vo.getDefinitionKey());
        }
        if (StringUtils.isNotBlank(vo.getBeginTime())) {
            query.taskCreatedAfter(DateUtils.parseDate(vo.getBeginTime(), "yyyy-MM-dd hh:mm:ss"));
        }
        if (StringUtils.isNotBlank(vo.getEndTime())) {
            query.taskCreatedBefore(DateUtils.addDays(DateUtils.parseDate(vo.getEndTime(), "yyyy-MM-dd hh:mm:ss"), 0));
        }
        if (StringUtils.isNotBlank(vo.getBusinessCode())) {
            query.processInstanceBusinessKey(vo.getBusinessCode());
        }
        LoginUser user = this.getLoginUser();
        if (!user.isAdmin()) {
            this.buildCandidateCondition(query, user);
        }
        long totalCount = query.count();
        result.setTotal(totalCount);
        if (totalCount > 0) {
            query.orderByTaskCreateTime().desc();
            int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
            List<Task> taskList = query.listPage(firstResult, pageParam.getPageSize());
            List<FlowWorkOrder> workOrders = this.orderService.selectByInstanceId(taskList.stream().map(Task::getProcessInstanceId).collect(Collectors.toList()));
            Map<String, FlowWorkOrder> orderMap = workOrders.stream().collect(Collectors.toMap(FlowWorkOrder::getProcessInstanceId, Function.identity()));
            List<FlowTaskVo> vos;
            if (user.isAdmin()) {
                List<LoginUser> users = this.baseApi.selectUserByIds(taskList.stream().filter(task -> StringUtils.isNotBlank(task.getAssignee())).map(Task::getAssignee).collect(Collectors.toList()));
                Map<String, String> userMap = CollectionUtils.isNotEmpty(users) ? users.stream().collect(Collectors.toMap(LoginUser::getId, LoginUser::getNickName)) : new HashMap<>();
                vos = taskList.stream().map(task -> new FlowTaskVo(task, orderMap.get(task.getProcessInstanceId()), userMap.getOrDefault(task.getAssignee(), ""))).collect(Collectors.toList());
            } else {
                vos = taskList.stream().map(task -> new FlowTaskVo(task, orderMap.get(task.getProcessInstanceId()), user.getNickName())).collect(Collectors.toList());
            }
            result.setList(vos);
        }
        return result;
    }

    @Override
    @SneakyThrows
    public PageInfo<FlowTaskVo> doneList(PageBean pageParam, FlowTaskVo vo) {
        PageInfo<FlowTaskVo> result = new PageInfo<>();
        String loginName = this.getLoginUser().getId();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(loginName).finished();
//        if (StringUtils.isNotBlank(vo.getDefinitionName())) {
//            query.processDefinitionName(vo.getDefinitionName());
//        }
        if (StringUtils.isNotBlank(vo.getDefinitionKey())) {
            query.processDefinitionKey(vo.getDefinitionKey());
        }
        if (StringUtils.isNotBlank(vo.getBeginTime())) {
            query.taskCompletedAfter(DateUtils.parseDate(vo.getBeginTime(), "yyyy-MM-dd hh:mm:ss"));
        }
        if (StringUtils.isNotBlank(vo.getEndTime())) {
            query.taskCompletedBefore(DateUtils.addDays(DateUtils.parseDate(vo.getEndTime(), "yyyy-MM-dd hh:mm:ss"), 1));
        }
        if (StringUtils.isNotBlank(vo.getBusinessCode())) {
            query.processInstanceBusinessKeyLike(vo.getBusinessCode());
        }
        long totalCount = query.count();
        result.setTotal(totalCount);
        if (totalCount > 0) {
            query.orderByHistoricTaskInstanceEndTime().desc();
            int firstResult = (pageParam.getPageNum() - 1) * pageParam.getPageSize();
            List<HistoricTaskInstance> taskList = query.listPage(firstResult, pageParam.getPageSize());
            List<FlowWorkOrder> workOrders = this.orderService.selectByInstanceId(taskList.stream().map(HistoricTaskInstance::getProcessInstanceId).collect(Collectors.toList()));
            Map<String, FlowWorkOrder> orderMap = workOrders.stream().collect(Collectors.toMap(FlowWorkOrder::getProcessInstanceId, Function.identity()));
            List<FlowTaskVo> vos = taskList.stream().map(task -> new FlowTaskVo(task, orderMap.get(task.getProcessInstanceId()), getLoginUser().getNickName())).collect(Collectors.toList());
            result.setList(vos);
        }
        return result;
    }

    @Override
    public long getTaskCount() {
        TaskQuery query = taskService.createTaskQuery().active();
        this.buildCandidateCondition(query, this.getLoginUser());
        return taskService.createTaskQuery().active().count();
    }

    @Override
    public boolean checkPermission(String taskId) {
        LoginUser user = this.getLoginUser();
        if (user.isAdmin()) {
            return true;
        }
        TaskQuery query = taskService.createTaskQuery().active();
        this.buildCandidateCondition(query, user);
        return query.taskId(taskId).count() > 0;
    }

    /**
     * 获取所有可回退的节点
     *
     * @param taskId
     *
     * @return
     */
    @Override
    public List<UserTask> findReturnTaskList(String taskId) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息，暂不考虑子流程情况
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        // 获取当前任务节点元素
        UserTask source = null;
        if (flowElements != null) {
            for (FlowElement flowElement : flowElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = (UserTask) flowElement;
                }
            }
        }
        // 获取节点的所有路线
        List<List<UserTask>> roads = FlowableUtils.findRoad(source, null, null, null);
        // 可回退的节点列表
        List<UserTask> userTaskList = new ArrayList<>();
        for (List<UserTask> road : roads) {
            if (userTaskList.size() == 0) {
                // 还没有可回退节点直接添加
                userTaskList = road;
            } else {
                // 如果已有回退节点，则比对取交集部分
                userTaskList.retainAll(road);
            }
        }
        return userTaskList;
    }

    @Override
    public List<UserTask> findNextTaskList(String taskId) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process mainProcess = bpmnModel.getMainProcess();
        List<UserTask> userTasks = mainProcess.findFlowElementsOfType(UserTask.class);
        List<UserTask> targetTasks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userTasks)) {
            UserTask current = userTasks.stream().filter(curr -> task.getTaskDefinitionKey().equals(curr.getId())).collect(Collectors.toList()).get(0);
            List<String> targetKeys = current.getOutgoingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
            targetTasks = userTasks.stream().filter(curr -> targetKeys.contains(task.getId())).collect(Collectors.toList());
        }
        return targetTasks;
    }

    /**
     * 流程历史流转记录
     *
     * @param procInsId 流程实例Id
     *
     * @return
     */
    @Override
    public Map<String, Object> flowRecord(String procInsId, String deployId) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(procInsId)) {
            List<HistoricActivityInstance> list = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(procInsId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc().list();
            List<FlowTaskDto> hisFlowList = new ArrayList<>();
            for (HistoricActivityInstance histIns : list) {
                if (StringUtils.isNotBlank(histIns.getTaskId())) {
                    FlowTaskDto flowTask = new FlowTaskDto();
                    flowTask.setTaskId(histIns.getTaskId());
                    flowTask.setTaskName(histIns.getActivityName());
                    flowTask.setCreateTime(histIns.getStartTime());
                    flowTask.setFinishTime(histIns.getEndTime());
//                    if (StringUtils.isNotBlank(histIns.getAssignee())) {
//                        SysUser sysUser = sysUserService.selectUserById(histIns.getAssignee());
//                        flowTask.setAssigneeId(sysUser.getId());
//                        flowTask.setAssigneeName(sysUser.getNickName());
//                        flowTask.setDeptName(sysUser.getDept().getDeptName());
//                    }
                    // 展示审批人员
                    List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(histIns.getTaskId());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HistoricIdentityLink identityLink : linksForTask) {
                        if ("candidate".equals(identityLink.getType())) {
//                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
//                                SysUser sysUser = sysUserService.selectUserById(identityLink.getUserId());
//                                stringBuilder.append(sysUser.getNickName()).append(",");
//                            }
//                            if (StringUtils.isNotBlank(identityLink.getGroupId())) {
//                                SysRole sysRole = sysRoleService.selectRoleById(identityLink.getGroupId());
//                                stringBuilder.append(sysRole.getRoleName()).append(",");
//                            }
                        }
                    }
                    if (StringUtils.isNotBlank(stringBuilder)) {
                        flowTask.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }

                    flowTask.setDuration(histIns.getDurationInMillis() == null || histIns.getDurationInMillis() == 0 ? null : getDate(histIns.getDurationInMillis()));
                    // 获取意见评论内容
                    List<Comment> commentList = taskService.getProcessInstanceComments(histIns.getProcessInstanceId());
                    commentList.forEach(comment -> {
                        if (histIns.getTaskId().equals(comment.getTaskId())) {
                            flowTask.setComment(FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build());
                        }
                    });
                    hisFlowList.add(flowTask);
                }
            }
            map.put("flowList", hisFlowList);
//            // 查询当前任务是否完成
//            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
//            if (CollectionUtils.isNotEmpty(taskList)) {
//                map.put("finished", true);
//            } else {
//                map.put("finished", false);
//            }
        }
        return map;
    }

    /**
     * 获取流程过程图
     *
     * @param processId
     *
     * @return
     */
    @Override
    public InputStream diagram(String processId) {
        String processDefinitionId;
        // 获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        // 如果流程已经结束，则得到结束节点
        if (Objects.isNull(processInstance)) {
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();

            processDefinitionId = pi.getProcessDefinitionId();
        } else {// 如果流程没有结束，则取当前活动节点
            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }

        // 获得活动的节点
        List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();

        List<String> highLightedFlows = new ArrayList<>();
        List<String> highLightedNodes = new ArrayList<>();
        //高亮线
        for (HistoricActivityInstance tempActivity : highLightedFlowList) {
            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
                //高亮线
                highLightedFlows.add(tempActivity.getActivityId());
            } else {
                //高亮节点
                highLightedNodes.add(tempActivity.getActivityId());
            }
        }
// 获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessEngineConfiguration engConf = processEngine.getProcessEngineConfiguration();

        ProcessDiagramGenerator diagramGenerator = engConf.getProcessDiagramGenerator();
//		ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "bmp", highLightedNodes, highLightedFlows, engConf.getActivityFontName(),
                engConf.getLabelFontName(), engConf.getAnnotationFontName(), engConf.getClassLoader(), 1.0, true);
//        //获取流程图
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
//        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
//        //获取自定义图片生成器
//        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
//        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(),
//                configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);
        return in;

    }

    /**
     * 获取流程变量
     *
     * @param taskId
     *
     * @return
     */
    @Override
    public Map<String, Object> processVariables(String taskId) {
        // 流程变量
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        } else {
            Map<String, Object> variables = taskService.getVariables(taskId);
            return variables;
        }
    }

    /**
     * 流程完成时间处理
     *
     * @param ms
     *
     * @return
     */
    private String getDate(long ms) {

        long day = ms / (24 * 60 * 60 * 1000);
        long hour = (ms / (60 * 60 * 1000) - day * 24);
        long minute = ((ms / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (day > 0) {
            return day + "天" + hour + "小时" + minute + "分钟";
        }
        if (hour > 0) {
            return hour + "小时" + minute + "分钟";
        }
        if (minute > 0) {
            return minute + "分钟";
        }
        if (second > 0) {
            return second + "秒";
        } else {
            return 0 + "秒";
        }
    }

    private void buildCandidateCondition(TaskQuery query, LoginUser tokenData) {
        Set<String> groupIdSet = new HashSet<>();
        // NOTE: 需要注意的是，部门Id、部门岗位Id，或者其他类型的分组Id，他们之间一定不能重复。
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
        if (CollectionUtils.isNotEmpty(groupIdSet)) {
            query.or().taskCandidateGroupIn(groupIdSet).taskCandidateOrAssigned(tokenData.getId()).endOr();
        } else {
            query.taskCandidateOrAssigned(tokenData.getId());
        }
    }

}
