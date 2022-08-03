package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import cn.silver.framework.workflow.mapper.FlowEntryMapper;
import cn.silver.framework.workflow.mapper.FlowEntryPublishMapper;
import cn.silver.framework.workflow.service.IFlowApiService;
import cn.silver.framework.workflow.service.IFlowEntryPublishService;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class FlowEntryPublishServiceImpl extends BaseServiceImpl<FlowEntryPublishMapper, FlowEntryPublish> implements IFlowEntryPublishService {

    @Autowired
    private IFlowApiService flowApiService;
    @Autowired
    private FlowEntryMapper entryMapper;

    @Override
    public List<FlowEntryPublish> selectByDefinitionIds(Collection<String> definitionIds) {
        return baseMapper.selectListByDefinitionIds(definitionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void activate(String id) {
        FlowEntryPublish flowEntryPublish = this.selectById(id);
        if (flowEntryPublish == null) {
            throw new CustomException("数据验证失败，当前流程发布版本并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (Boolean.TRUE.equals(flowEntryPublish.getActiveStatus())) {
            throw new CustomException("数据验证失败，当前流程发布版本已处于激活状态！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        flowEntryPublish.setActiveStatus(true);
        baseMapper.update(flowEntryPublish);
        if (Boolean.TRUE.equals(flowEntryPublish.getMainVersion())) {
            FlowEntry entry = entryMapper.selectByPrimaryKey(flowEntryPublish.getEntryId());
            entry.setActiveStatus(true);
            entryMapper.update(entry);
        }
        flowApiService.activateProcessDefinition(flowEntryPublish.getProcessDefinitionId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void suspend(String id) {
        FlowEntryPublish flowEntryPublish = this.selectById(id);
        if (flowEntryPublish == null) {
            throw new CustomException("数据验证失败，当前流程发布版本并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (Boolean.FALSE.equals(flowEntryPublish.getActiveStatus())) {
            throw new CustomException("数据验证失败，当前流程发布版本已处于挂起状态！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        flowEntryPublish.setActiveStatus(false);
        baseMapper.update(flowEntryPublish);
        if (Boolean.TRUE.equals(flowEntryPublish.getMainVersion())) {
            FlowEntry entry = entryMapper.selectByPrimaryKey(flowEntryPublish.getEntryId());
            entry.setActiveStatus(false);
            entryMapper.update(entry);
        }
        flowApiService.suspendProcessDefinition(flowEntryPublish.getProcessDefinitionId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeMainVersion(JSONObject params) {
        FlowEntryPublish flowEntryPublish = this.selectById(params.getString("newId"));
        if (flowEntryPublish == null) {
            throw new CustomException("数据验证失败，当前流程发布版本并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (ObjectUtil.notEqual(params.getString("entryId"), flowEntryPublish.getEntryId())) {
            throw new CustomException("数据验证失败，当前工作流并不包含该工作流发布版本数据，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (Boolean.TRUE.equals(flowEntryPublish.getMainVersion())) {
            throw new CustomException("数据验证失败，该版本已经为当前工作流的发布主版本，不能重复设置！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        FlowEntry flowEntry = this.entryMapper.selectByPrimaryKey(params.getString("entryId"));
        FlowEntryPublish oldMainFlowEntryPublish =
                this.baseMapper.selectByPrimaryKey(flowEntry.getMainEntryPublishId());
        oldMainFlowEntryPublish.setMainVersion(false);
        baseMapper.update(oldMainFlowEntryPublish);
        flowEntryPublish.setMainVersion(true);
        baseMapper.update(flowEntryPublish);
        flowEntry.setMainEntryPublishId(flowEntryPublish.getId());
        flowEntry.setPublishVersion(flowEntryPublish.getPublishVersion());
        flowEntry.setActiveStatus(flowEntryPublish.getActiveStatus());
        entryMapper.update(flowEntry);
    }
}
