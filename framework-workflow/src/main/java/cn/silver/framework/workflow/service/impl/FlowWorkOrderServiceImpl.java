package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.core.service.impl.FlowServiceImpl;
import cn.silver.framework.workflow.constant.FlowTaskStatus;
import cn.silver.framework.workflow.domain.FlowWorkOrder;
import cn.silver.framework.workflow.mapper.FlowWorkOrderMapper;
import cn.silver.framework.workflow.service.IFlowWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流工单表数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowWorkOrderService")
public class FlowWorkOrderServiceImpl extends FlowServiceImpl<FlowWorkOrderMapper, FlowWorkOrder> implements IFlowWorkOrderService {

    @Override
    public List<FlowWorkOrder> selectByBusinessKey(Collection<String> businessKeys) {
        return this.baseMapper.selectByBusinessKeys(businessKeys);
    }

    @Override
    public List<FlowWorkOrder> selectByTaskId(Collection<String> taskIds) {
        return this.baseMapper.selectByTaskId(taskIds);
    }

    @Override
    public List<FlowWorkOrder> selectByInstanceId(Collection<String> instanceIds) {
        return this.baseMapper.selectByInstanceIds(instanceIds);
    }

    @Override
    public FlowWorkOrder selectByBusinessKey(String dataId) {
        return this.baseMapper.selectByBusinessKey(dataId);
    }

    @Override
    public FlowWorkOrder selectByInstanceId(String instanceId) {
        return this.baseMapper.selectByInstanceId(instanceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(FlowWorkOrder entity) {
        entity.preInsert();
        return this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(FlowWorkOrder entity) {
        entity.preUpdate();
        return this.baseMapper.update(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateFlowStatusByProcessInstanceId(String processInstanceId, String flowStatus) {
        FlowWorkOrder flowWorkOrder = baseMapper.selectByInstanceId(processInstanceId);
        if (ObjectUtils.isNotEmpty(flowWorkOrder)) {
            flowWorkOrder.setStatus(flowStatus);
            if (!FlowTaskStatus.FINISHED.getCode().equals(flowStatus)) {
                flowWorkOrder.preUpdate();
            }
            baseMapper.update(flowWorkOrder);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeByProcessInstanceId(String processInstanceId) {
        FlowWorkOrder workOrder = this.selectByInstanceId(processInstanceId);
        this.baseMapper.deleteByInstanceId(processInstanceId);
        this.bussApi.delete(workOrder.getTableName(), workOrder.getTableName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeByBusinessKeys(Collection<String> businessKeys) {
        if (CollectionUtils.isNotEmpty(businessKeys)) {
            this.baseMapper.deleteByBusinessKeys(businessKeys);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        FlowWorkOrder workOrder = this.selectById(id);
        int result = this.baseMapper.deleteByPrimaryKey(id);
        this.bussApi.delete(workOrder.getTableName(), workOrder.getTableName());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> ids) {
        List<FlowWorkOrder> workOrders = this.baseMapper.selectByIds(ids);
        int result = this.baseMapper.deleteBatch(ids);
        workOrders.stream().collect(Collectors.groupingBy(FlowWorkOrder::getTableName)).entrySet().stream().forEach(entry -> {
            this.bussApi.deleteBatch(entry.getKey(), entry.getValue().stream().map(FlowWorkOrder::getBusinessKey).collect(Collectors.toSet()));
        });
        return result;
    }

    @Override
    public boolean hasDataPermOnFlowWorkOrder(String processInstanceId) {
        FlowWorkOrder filter = new FlowWorkOrder();
        filter.setProcessInstanceId(processInstanceId);
        int count = baseMapper.selectCount(filter);
        return count > 0;
    }

    @Override
    public List<FlowWorkOrder> getSearchValue() {
        return baseMapper.getSearchValue();
    }

}
