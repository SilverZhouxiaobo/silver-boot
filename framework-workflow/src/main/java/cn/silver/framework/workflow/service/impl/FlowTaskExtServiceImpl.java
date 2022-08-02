package cn.silver.framework.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.domain.FlowTaskExt;
import cn.silver.framework.workflow.mapper.FlowTaskExtMapper;
import cn.silver.framework.workflow.service.IFlowTaskExtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 流程任务扩展数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowTaskExtService")
public class FlowTaskExtServiceImpl extends BaseServiceImpl<FlowTaskExtMapper, FlowTaskExt> implements IFlowTaskExtService {

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveBatch(List<FlowTaskExt> flowTaskExtList) {
        if (CollUtil.isNotEmpty(flowTaskExtList)) {
            baseMapper.insertList(flowTaskExtList);
        }
    }

    @Override
    public FlowTaskExt getByProcessDefinitionIdAndTaskId(String processDefinitionId, String taskId) {
        FlowTaskExt filter = new FlowTaskExt();
        filter.setProcessDefinitionId(processDefinitionId);
        filter.setTaskId(taskId);
        return baseMapper.selectOne(filter);
    }

    @Override
    public List<FlowTaskExt> getByProcessDefinitionId(String processDefinitionId) {
        FlowTaskExt filter = new FlowTaskExt();
        filter.setProcessDefinitionId(processDefinitionId);
        return baseMapper.selectList(filter);
    }
}
