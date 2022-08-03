package cn.silver.framework.workflow.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.workflow.constant.FlowPublishStatus;
import cn.silver.framework.workflow.domain.FlowCategory;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.mapper.FlowCategoryMapper;
import cn.silver.framework.workflow.service.IFlowCategoryService;
import cn.silver.framework.workflow.service.IFlowEntryService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FlowCategory数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("flowCategoryService")
public class FlowCategoryServiceImpl extends BaseServiceImpl<FlowCategoryMapper, FlowCategory> implements IFlowCategoryService {
    @Autowired
    private IFlowEntryService entryService;

    /**
     * 更新数据对象。
     *
     * @param flowCategory 更新的对象。
     * @return 成功返回true，否则false。
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(FlowCategory flowCategory) {
        FlowCategory originalFlowCategory = this.selectById(flowCategory.getId());
        if (originalFlowCategory == null) {
            throw new CustomException("数据验证失败，当前流程分类并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        if (!StrUtil.equals(flowCategory.getCode(), originalFlowCategory.getCode())) {
            FlowEntry filter = new FlowEntry();
            filter.setCategoryId(flowCategory.getId());
            filter.setStatus(FlowPublishStatus.UNPUBLISHED.getCode());
            List<FlowEntry> flowEntryList = entryService.selectList(filter);
            if (CollUtil.isNotEmpty(flowEntryList)) {
                throw new CustomException("数据验证失败，当前流程分类存在已经发布的流程数据，因此分类标识不能修改！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
            }
        }
        flowCategory.preUpdate();
        return baseMapper.update(flowCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        // 验证关联Id的数据合法性
        FlowCategory originalFlowCategory = this.selectById(id);
        if (originalFlowCategory == null) {
            throw new CustomException("数据验证失败，当前流程分类并不存在，请刷新后重试！", ResponseEnum.DATA_ERROR_NOT_FOUND.getCode());
        }
        FlowEntry filter = new FlowEntry();
        filter.setCategoryId(id);
        List<FlowEntry> flowEntryList = entryService.selectList(filter);
        if (CollUtil.isNotEmpty(flowEntryList)) {
            throw new CustomException("数据验证失败，请先删除当前流程分类关联的流程数据！", ResponseEnum.DATA_VALIDATED_FAILED.getCode());
        }
        return this.baseMapper.deleteByPrimaryKey(id);
    }
}
