package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.constant.FlowMessageOperationType;
import cn.silver.framework.workflow.domain.FlowMessageOperation;
import cn.silver.framework.workflow.mapper.FlowMessageOperationMapper;
import cn.silver.framework.workflow.service.IFlowMessageOperationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Administrator
 */
@Service
public class FlowMessageOperationServiceImpl extends BaseServiceImpl<FlowMessageOperationMapper, FlowMessageOperation> implements IFlowMessageOperationService {


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void readCopyTask(String messageId) {
        FlowMessageOperation operation = new FlowMessageOperation();
        operation.preInsert();
        operation.setMessageId(messageId);
        operation.setLoginName(this.getLoginUser().getId());
        operation.setOperationType(FlowMessageOperationType.READ_FINISHED.getCode());
        operation.setOperationTime(new Date());
        this.insert(operation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteByProcessInstanceId(String processInstanceId) {
        this.baseMapper.deleteByProcessInstanceId(processInstanceId);
    }
}
