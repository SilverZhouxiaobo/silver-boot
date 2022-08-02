package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.domain.FlowTaskComment;
import cn.silver.framework.workflow.mapper.FlowTaskCommentMapper;
import cn.silver.framework.workflow.service.IFlowTaskCommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 流程任务批注数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowTaskCommentService")
public class FlowTaskCommentServiceImpl extends BaseServiceImpl<FlowTaskCommentMapper, FlowTaskComment> implements IFlowTaskCommentService {


    /**
     * 查询指定流程实例Id下的所有审批任务的批注。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果集。
     */
    @Override
    public List<FlowTaskComment> getFlowTaskCommentList(String processInstanceId) {
        return baseMapper.selectListByInstanceId(processInstanceId);
    }

    @Override
    public List<FlowTaskComment> getFlowTaskCommentListByTaskIds(Set<String> taskIdSet) {
        return baseMapper.selectListByTaskIds(taskIdSet);
    }

    @Override
    public FlowTaskComment getLatestFlowTaskComment(String processInstanceId) {
        List<FlowTaskComment> tasks = baseMapper.selectListByInstanceId(processInstanceId);
        return CollectionUtils.isNotEmpty(tasks) ? tasks.get(0) : null;
    }

    @Override
    public FlowTaskComment getLatestFlowTaskComment(String processInstanceId, String taskDefinitionKey) {
        FlowTaskComment comment = new FlowTaskComment();
        comment.setProcessInstanceId(processInstanceId);
        comment.setTaskKey(taskDefinitionKey);
        List<FlowTaskComment> pageData = baseMapper.selectList(comment);
        return CollectionUtils.isEmpty(pageData) ? null : pageData.get(0);
    }

    @Override
    public FlowTaskComment getFirstFlowTaskComment(String processInstanceId) {
        FlowTaskComment comment = new FlowTaskComment();
        comment.setProcessInstanceId(processInstanceId);
        List<FlowTaskComment> pageData = baseMapper.selectList(comment);
        return CollectionUtils.isEmpty(pageData) ? null : pageData.get(0);
    }
}
