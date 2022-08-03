package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.api.IFlowApi;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.domain.FlowEntity;
import cn.silver.framework.security.util.SecurityUtils;
import cn.silver.framework.workflow.constant.FlowApprovalType;
import cn.silver.framework.workflow.constant.FlowTaskStatus;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.service.IFlowEntryService;
import cn.silver.framework.workflow.service.IFlowTaskCommentService;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import cn.silver.framework.workflow.util.FlowOperationHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
@Service
public class FlowApiImpl implements IFlowApi {

    @Autowired
    private IFlowApiService apiService;
    @Autowired
    private IFlowEntryService entryService;
    @Autowired
    private IFlowTaskCommentService commentService;
    @Autowired
    private IFlowWorkOrderService orderService;
    @Autowired
    private FlowOperationHelper flowOperationHelper;
    @Autowired
    private TaskService taskService;


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveWorkOrder(FlowEntity entity) {
        FlowWorkOrder order = this.orderService.selectByBusinessKey(entity.getId());
        if (ObjectUtils.isEmpty(order)) {
            FlowEntry entry = this.entryService.selectByValue(entity.getFlowCode());
            this.orderService.insert(new FlowWorkOrder(entity, entry));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteWorkOrders(Collection<String> bussinessKeys) {
        this.orderService.removeByBusinessKeys(bussinessKeys);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String startFlowProcess(FlowEntity entity) {
        // 1. 验证流程数据的合法性。
        FlowEntry entry = flowOperationHelper.verifyAndGetFlowEntry(entity.getFlowCode());
        // 2. 验证流程一个用户任务的合法性。
        FlowEntryPublish flowEntryPublish = entry.getMainFlowEntryPublish();
        if (!flowEntryPublish.getActiveStatus()) {
            throw new CustomException("数据验证失败，当前流程发布对象已被挂起，不能启动新流程！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        //根据实体类信息的id查询工单是否存在
        FlowWorkOrder order = this.orderService.selectByBusinessKey(entity.getId());
        if (ObjectUtils.isEmpty(order)) {
            //若工单不存在则创建工单
            order = new FlowWorkOrder(entity, entry);
        }
        //创建taskcomment，保存操作信息
        FlowTaskComment comment = new FlowTaskComment(order, FlowApprovalType.SAVE.getCode(), "提交申请");
        //如果工单中的进程id为空的，则开启流程
        if (StringUtils.isBlank(order.getProcessInstanceId())) {
            ProcessInstance instance = apiService.startAndTakeFirst(flowEntryPublish.getProcessDefinitionId(), entity.getCode(), comment, entity.getFlowParams());
            order.setProcessInstanceId(instance.getId());
            order.setProcessDefinitionId(instance.getProcessDefinitionId());
        }
        //工单的进程id不为空,则根据进程id拿到流程任务的信息
        Task task = taskService.createTaskQuery().processInstanceId(order.getProcessInstanceId()).active().singleResult();
        //对comment对象进行属性注入
        comment.fillWith(task);
        //调用接口完成任务
        this.apiService.completeTask(task, comment, entity.getFlowParams());
        task = taskService.createTaskQuery().processInstanceId(order.getProcessInstanceId()).active().singleResult();
        task.setDescription(comment.getBusinessParam().toJSONString());
        taskService.saveTask(task);
        taskService.setVariables(task.getId(), comment.getBusinessParam());
        order.setTaskId(task.getId());
        order.setTaskName(task.getName());
        order.setTaskDefinitionKey(task.getTaskDefinitionKey());
        order.setStatus(FlowTaskStatus.APPROVING.getCode());
        this.orderService.insertOrUpdate(order);
        return task.getTaskDefinitionKey();
    }

    @Override
    public String complete(FlowEntity entity) {
        FlowWorkOrder order = this.orderService.selectByBusinessKey(entity.getId());
        Task task = taskService.createTaskQuery().processInstanceId(order.getProcessInstanceId()).active().singleResult();
        if (StringUtils.isBlank(entity.getApproveOpinion())) {
            entity.setApproveOpinion(order.getTaskName() + FlowApprovalType.getName(entity.getApproveType()));
        }
        FlowTaskComment comment = new FlowTaskComment(order, entity.getApproveType(), entity.getApproveOpinion());
        comment.fillWith(task);
        if (MapUtils.isNotEmpty(entity.getFlowParams())) {
            comment.setCustomBusinessData(entity.getFlowParams().toJSONString());
        }
        if (FlowApprovalType.TRANSFER.getCode().equals(comment.getApprovalType())) {
            comment.setDelegateAssignee(entity.getNextAssignee());
        }
        this.apiService.completeTask(task, comment, entity.getFlowParams());
        task = taskService.createTaskQuery().processInstanceId(order.getProcessInstanceId()).active().singleResult();
        String result = "";
        if (task != null) {
            task.setDescription(comment.getBusinessParam().toJSONString());
            if (StringUtils.isNotBlank(entity.getNextAssignee())) {
                task.setAssignee(entity.getNextAssignee());
            }
            if (ObjectUtils.isNotEmpty(entity.getDueTime())) {
                task.setDueDate(entity.getDueTime());
            }
            taskService.saveTask(task);
            taskService.setVariables(task.getId(), comment.getBusinessParam());
            order.setTaskId(task.getId());
            order.setTaskName(task.getName());
            order.setTaskDefinitionKey(task.getTaskDefinitionKey());
            order.setStatus(FlowTaskStatus.APPROVING.getCode());
            result = task.getTaskDefinitionKey();
        } else {
            order.setStatus(FlowTaskStatus.FINISHED.getCode());
            order.setTaskId(null);
            order.setTaskName(null);
            order.setTaskName(null);
            order.setTaskDefinitionKey(null);
            result = "finished";
        }
        this.orderService.update(order);
        return result;
    }

    protected LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
