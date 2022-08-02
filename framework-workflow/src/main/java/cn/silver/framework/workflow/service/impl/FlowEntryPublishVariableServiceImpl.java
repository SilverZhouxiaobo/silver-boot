package cn.silver.framework.workflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.silver.framework.common.utils.id.IdWorker;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowEntryPublishVariable;
import cn.silver.framework.workflow.domain.FlowEntryVariable;
import cn.silver.framework.workflow.mapper.FlowEntryPublishVariableMapper;
import cn.silver.framework.workflow.service.IFlowEntryPublishVariableService;
import cn.silver.framework.workflow.service.IFlowEntryVariableService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 */
@Service
public class FlowEntryPublishVariableServiceImpl extends BaseServiceImpl<FlowEntryPublishVariableMapper, FlowEntryPublishVariable> implements IFlowEntryPublishVariableService {

    @Autowired
    private IFlowEntryVariableService variableService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void init(FlowEntryPublish publish) {
        FlowEntryVariable flowEntryVariableFilter = new FlowEntryVariable();
        flowEntryVariableFilter.setEntryId(publish.getEntryId());
        List<FlowEntryVariable> variables = variableService.selectList(flowEntryVariableFilter);
        if (CollUtil.isNotEmpty(variables)) {
            for (FlowEntryVariable variable : variables) {
                FlowEntryPublishVariable publishVariable = new FlowEntryPublishVariable();
                BeanUtils.copyProperties(variable, publishVariable);
                publishVariable.setId(IdWorker.getIdStr());
                publishVariable.setEntryPublishId(publish.getId());
                this.insert(publishVariable);
            }
        }
    }
}
