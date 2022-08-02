package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowMessageCandidate;
import cn.silver.framework.workflow.domain.FlowTaskExt;

public interface IFlowMessageCandidateService extends IBaseService<FlowMessageCandidate> {


    /**
     * 判断当前用户是否有权限访问指定消息Id。
     *
     * @param messageId 消息Id。
     * @return true为合法访问者，否则false。
     */
    boolean isCandidateIdentityOnMessage(String messageId);

    void saveBatch(String messageId, String candidateType, String candidateIds);

    void saveBatch(String instanceId, FlowTaskExt flowTaskExt, String messageId);

    void deleteByProcessInstanceId(String processInstanceId);
}
