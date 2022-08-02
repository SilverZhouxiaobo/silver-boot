package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.constant.FlowConstant;
import cn.silver.framework.workflow.domain.FlowMessage;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.mapper.FlowMessageMapper;
import cn.hb.software.gacim.workflow.service.*;
import cn.silver.framework.workflow.vo.TaskInfoVo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.silver.framework.workflow.service.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 工作流消息数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowMessageService")
public class FlowMessageServiceImpl extends BaseServiceImpl<FlowMessageMapper, FlowMessage> implements IFlowMessageService {

    @Autowired
    private IFlowTaskExtService flowTaskExtService;
    @Autowired
    private IFlowApiService flowApiService;
    @Autowired
    private IFlowMessageCandidateService candidateService;
    @Autowired
    private IFlowMessageOperationService operationService;


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveNewRemindMessage(FlowWorkOrder flowWorkOrder) {
        List<Task> taskList =
                flowApiService.getProcessInstanceActiveTaskList(flowWorkOrder.getProcessInstanceId());
        for (Task task : taskList) {
            List<FlowMessage> messageList = baseMapper.selectListByTaskId(task.getId());
            // 同一个任务只能催办一次，多次催办则累加催办次数。
            if (CollUtil.isNotEmpty(messageList)) {
                for (FlowMessage flowMessage : messageList) {
                    flowMessage.setRemindCount(flowMessage.getRemindCount() + 1);
                    baseMapper.update(flowMessage);
                }
                continue;
            }
            FlowMessage flowMessage = new FlowMessage(flowWorkOrder, task);
            this.insert(flowMessage);
            FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
                    flowWorkOrder.getProcessDefinitionId(), task.getTaskDefinitionKey());
            if (flowTaskExt != null) {
                // 插入与当前消息关联任务的候选人
                candidateService.saveBatch(
                        flowWorkOrder.getProcessInstanceId(), flowTaskExt, flowMessage.getId());
            }
            // 插入与当前消息关联任务的指派人。
            if (StrUtil.isNotBlank(task.getAssignee())) {
                this.candidateService.saveBatch(flowMessage.getId(), FlowConstant.GROUP_TYPE_USER_VAR, task.getAssignee());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveNewCopyMessage(Task task, JSONObject copyDataJson) {
        ProcessInstance instance = flowApiService.getProcessInstance(task.getProcessInstanceId());
        FlowMessage flowMessage = new FlowMessage(instance, task);
        // 如果是在线表单，这里就保存关联的在线表单Id，便于在线表单业务数据的查找。
        if (Boolean.TRUE.equals(flowMessage.getOnlineFormData())) {
            TaskInfoVo taskInfo = JSON.parseObject(task.getFormKey(), TaskInfoVo.class);
            flowMessage.setBusinessDataShot(taskInfo.getFormId());
        }
        this.insert(flowMessage);
        for (Map.Entry<String, Object> entries : copyDataJson.entrySet()) {
            this.candidateService.saveBatch(
                    flowMessage.getId(), entries.getKey(), entries.getValue().toString());
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void updateFinishedStatusByTaskId(String taskId) {
        List<FlowMessage> messages = this.baseMapper.selectListByTaskId(taskId);
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(message -> {
                message.setTaskFinished(true);
                baseMapper.update(message);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateFinishedStatusByProcessInstanceId(String processInstanceId) {
        List<FlowMessage> messages = this.baseMapper.selectListByInstanceId(processInstanceId);
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(message -> {
                message.setTaskFinished(true);
                baseMapper.update(message);
            });
        }
    }

    public List<FlowMessage> selectByUser(String messageType) {
        return baseMapper.selecctListByUser(messageType, this.getLoginUser().getId(), flowApiService.buildGroupIdSet());
    }

    @Override
    public List<FlowMessage> getRemindingMessageListByUser() {
        return baseMapper.getRemindingMessageListByUser(this.getLoginUser().getId(), flowApiService.buildGroupIdSet());
    }

    @Override
    public List<FlowMessage> getCopyMessageListByUser(Boolean read) {
        return baseMapper.getCopyMessageListByUser(this.getLoginUser().getId(), flowApiService.buildGroupIdSet(), read);
    }


    @Override
    public int countRemindingMessageListByUser() {
        return baseMapper.countRemindingMessageListByUser(this.getLoginUser().getId(), flowApiService.buildGroupIdSet());
    }

    @Override
    public int countCopyMessageByUser() {
        return baseMapper.countCopyMessageListByUser(this.getLoginUser().getId(), flowApiService.buildGroupIdSet());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void removeByProcessInstanceId(String processInstanceId) {
        candidateService.deleteByProcessInstanceId(processInstanceId);
        operationService.deleteByProcessInstanceId(processInstanceId);
        baseMapper.deleteByInstanceId(processInstanceId);
    }
}
