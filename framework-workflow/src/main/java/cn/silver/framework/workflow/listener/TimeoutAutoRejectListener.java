package cn.silver.framework.workflow.listener;

import cn.silver.framework.core.api.IBussApi;
import cn.silver.framework.core.model.ApproveModel;
import cn.silver.framework.workflow.constant.FlowApprovalType;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 超时自动驳回
 */
@Slf4j
@Component
public class TimeoutAutoRejectListener implements ExecutionListener {

    @Autowired
    private IFlowWorkOrderService orderService;
    @Autowired
    private IBussApi bussApi;

    @Override
    public void notify(DelegateExecution execution) {
        FlowWorkOrder workOrder = this.orderService.selectByInstanceId(execution.getProcessInstanceId());
        this.bussApi.approveFlow(workOrder.getTableName(), new ApproveModel(workOrder.getBusinessKey(), FlowApprovalType.AUTO_REJECT.getCode(), "处理超时，自动驳回"));
    }
}
