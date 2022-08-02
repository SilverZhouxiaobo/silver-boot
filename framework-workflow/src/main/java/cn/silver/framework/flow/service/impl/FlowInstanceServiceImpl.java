package cn.silver.framework.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.flow.domain.FlowInstance;
import cn.silver.framework.flow.domain.dto.FlowViewerDto;
import cn.silver.framework.flow.factory.FlowServiceFactory;
import cn.silver.framework.flow.service.IFlowInstanceService;
import cn.silver.framework.workflow.constant.FlowApprovalType;
import cn.silver.framework.workflow.constant.FlowTaskStatus;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.service.IFlowMessageService;
import cn.silver.framework.workflow.service.IFlowTaskCommentService;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>工作流流程实例管理<p>
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@Slf4j
@Service
public class FlowInstanceServiceImpl extends FlowServiceFactory implements IFlowInstanceService {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IFlowTaskCommentService commentService;
    @Autowired
    private IFlowWorkOrderService orderService;
    @Autowired
    private IFlowMessageService messageService;

    @Override
    @SneakyThrows
    public PageInfo<FlowInstance> selectPage(PageBean page, FlowInstance entity) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
//        if (StringUtils.isNotBlank(entity.getProcessDefinitionKey())) {
//            query.processDefinitionKey(entity.getProcessDefinitionKey());
//        }
//        if (StringUtils.isNotBlank(entity.getProcessDefinitionName())) {
//            query.processDefinitionName(entity.getProcessDefinitionName());
//        }
        if (StringUtils.isNotBlank(entity.getStartUserId())) {
            query.startedBy(entity.getStartUserId());
        }
        if (StringUtils.isNotBlank(entity.getBeginTime()) && StringUtils.isNotBlank(entity.getEndTime())) {
            query.startedAfter(DateUtils.parseDate(entity.getBeginTime(), "yyyy-MM-dd"));
            query.startedBefore(DateUtils.parseDate(entity.getEndTime(), "yyyy-MM-dd"));
        }
        if (ObjectUtils.isNotEmpty(entity.getFinished())) {
            if (BooleanUtils.isTrue(entity.getFinished())) {
                query.finished();
            } else {
                query.unfinished();
            }
        }
        query.orderByProcessInstanceStartTime().desc();
        PageInfo<FlowInstance> pageInfo = new PageInfo<>();
        pageInfo.setTotal(query.count());
        int firstResult = (page.getPageNum() - 1) * page.getPageSize();
        List<HistoricProcessInstance> instanceList = query.listPage(firstResult, page.getPageSize());
        pageInfo.setList(instanceList.stream().map(FlowInstance::new).collect(Collectors.toList()));
        return pageInfo;
    }

    /**
     * 获取流程执行过程
     *
     * @param procInsId
     * @return
     */
    @Override
    public Response<List<FlowViewerDto>> getFlowViewer(String procInsId) {
        List<FlowViewerDto> flowViewerList = new ArrayList<>();
        FlowViewerDto flowViewerDto;
        // 获得活动的节点
        List<HistoricActivityInstance> hisActIns = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId)
                .orderByHistoricActivityInstanceStartTime()
                .asc().list();
        for (HistoricActivityInstance activityInstance : hisActIns) {
            if (!"sequenceFlow".equals(activityInstance.getActivityType())) {
                flowViewerDto = new FlowViewerDto();
                flowViewerDto.setKey(activityInstance.getActivityId());
                flowViewerDto.setCompleted(!Objects.isNull(activityInstance.getEndTime()));
                flowViewerList.add(flowViewerDto);
            }
        }
        return Response.success(flowViewerList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void active(String instanceId) {
        runtimeService.activateProcessInstanceById(instanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void suspend(String instanceId) {
        runtimeService.suspendProcessInstanceById(instanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void stop(String processInstanceId, String stopReason, boolean forCancel) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        if (CollUtil.isEmpty(taskList)) {
            throw new CustomException("数据验证失败，当前流程尚未开始或已经结束！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
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
                throw new CustomException("数据验证失败，不能从子流程直接中止！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
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
            commentService.insert(taskComment);
            // 回复原有输出方向。
            currFlow.setOutgoingFlows(oriSequenceFlows);
        }
        String status = FlowTaskStatus.STOPPED.getCode();
        if (forCancel) {
            status = FlowTaskStatus.CANCELLED.getCode();
        }
        orderService.updateFlowStatusByProcessInstanceId(processInstanceId, status);
        messageService.updateFinishedStatusByProcessInstanceId(processInstanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(String processInstanceId) {
        historyService.deleteHistoricProcessInstance(processInstanceId);
        orderService.removeByProcessInstanceId(processInstanceId);
        messageService.removeByProcessInstanceId(processInstanceId);
    }
}