package cn.silver.framework.workflow.listener;

import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import cn.silver.framework.workflow.util.ApplicationContextHolder;
import cn.silver.framework.workflow.util.FlowCustomExtFactory;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

/**
 * 流程实例监听器，在流程实例结束的时候，需要完成一些自定义的业务行为。如：
 * 1. 更新流程工单表的审批状态字段。
 * 2. 业务数据同步。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
public class FlowFinishedListener implements ExecutionListener {

    private final IFlowWorkOrderService flowWorkOrderService =
            ApplicationContextHolder.getBean(IFlowWorkOrderService.class);
    private final FlowCustomExtFactory flowCustomExtFactory =
            ApplicationContextHolder.getBean(FlowCustomExtFactory.class);

    @Override
    public void notify(DelegateExecution execution) {
        System.out.println("1111111111");
        System.out.println("==================================");
//        if (!StrUtil.equals("end", execution.getEventName())) {
//            return;
//        }
//        String processInstanceId = execution.getProcessInstanceId();
//        flowWorkOrderService.updateFlowStatusByProcessInstanceId(processInstanceId, FlowTaskStatus.FINISHED.getCode());
//        String businessKey = execution.getProcessInstanceBusinessKey();
//        FlowWorkOrder workOrder = flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(processInstanceId);
//        flowCustomExtFactory.getBusinessDataExtHelper()
//                .triggerSync(workOrder.getProcessDefinitionKey(), processInstanceId, businessKey);
    }
}
