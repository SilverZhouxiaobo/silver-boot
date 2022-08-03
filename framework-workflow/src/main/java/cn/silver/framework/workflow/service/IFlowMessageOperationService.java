package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowMessageOperation;

public interface IFlowMessageOperationService extends IBaseService<FlowMessageOperation> {
    /**
     * 读取抄送消息，同时更新当前用户对指定抄送消息的读取状态。
     *
     * @param messageId 消息Id。
     */
    void readCopyTask(String messageId);

    void deleteByProcessInstanceId(String processInstanceId);
}
