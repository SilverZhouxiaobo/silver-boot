package cn.silver.framework.core.api;

import cn.silver.framework.core.domain.FlowEntity;

import java.util.Collection;

public interface IFlowApi {
    /**
     * 启动流程
     *
     * @param entity
     * @return
     */
    String startFlowProcess(FlowEntity entity);

    /**
     * 完成任务
     *
     * @param entity
     * @return
     */
    String complete(FlowEntity entity);

    /**
     * 保存工单
     *
     * @param entity
     */
    void saveWorkOrder(FlowEntity entity);

    /**
     * 删除工单
     *
     * @param asList
     */
    void deleteWorkOrders(Collection<String> asList);
}
