package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.domain.FlowEntryPublishVariable;

public interface IFlowEntryPublishVariableService extends IBaseService<FlowEntryPublishVariable> {
    void init(FlowEntryPublish publish);
}
