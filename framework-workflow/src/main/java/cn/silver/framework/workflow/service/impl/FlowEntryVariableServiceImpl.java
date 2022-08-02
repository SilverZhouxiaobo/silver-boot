package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.constant.FlowInitVariable;
import cn.silver.framework.workflow.domain.FlowEntryVariable;
import cn.silver.framework.workflow.mapper.FlowEntryVariableMapper;
import cn.silver.framework.workflow.service.IFlowEntryVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 流程变量数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowEntryVariableService")
public class FlowEntryVariableServiceImpl extends BaseServiceImpl<FlowEntryVariableMapper, FlowEntryVariable> implements IFlowEntryVariableService {

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void init(String entryId) {
        Arrays.stream(FlowInitVariable.values()).forEach(varable -> {
            this.insert(FlowEntryVariable.getDefaultIdentity(entryId, varable));
        });
    }

    /**
     * 删除指定流程Id的所有变量。
     *
     * @param entryId 流程Id。
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeByEntryId(String entryId) {
        baseMapper.deleteByEntryId(entryId);
    }
}
