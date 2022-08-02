package cn.silver.framework.core.service.impl;

import cn.silver.framework.core.api.IBussApi;
import cn.silver.framework.core.api.IFlowApi;
import cn.silver.framework.core.domain.FlowEntity;
import cn.silver.framework.core.mapper.FlowMapper;
import cn.silver.framework.core.model.ApproveModel;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.core.service.IFlowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * @author Administrator
 */
public class FlowServiceImpl<M extends FlowMapper<T>, T extends FlowEntity> extends BaseServiceImpl<M, T> implements IFlowService<T> {

    @Autowired
    protected IFlowApi flowApi;
    @Autowired
    protected IBussApi bussApi;

    @Override
    public PageInfo<T> selectHandles(PageBean page, T record) {
        record.setCurrUser(getLoginUser().getId());
        PageHelper.startPage(page.getPageNum(), page.getPageSize(), page.getOrderBy());
        return new PageInfo<>(this.baseMapper.selectCollects(record));
    }

    @Override
    public <T extends FlowEntity> T init(T entity) {
        entity.preInsert();
        entity.setCode(baseApi.getCode(entity.getFlowCode()));
        return entity;
    }

    @Override
    public void handle(T entity) {
        this.baseApi.handle(entity.getClass().getAnnotation(Table.class).name(), entity.getId(), entity.getHandleType(), entity.getRemark());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(T entity) {
        if (StringUtils.isBlank(entity.getCode())) {
            entity.setCode(baseApi.getCode(entity.getFlowCode()));
        }
        int result = super.insert(entity);
        if (entity.createOrder()) {
            this.flowApi.saveWorkOrder(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(T entity) {
        int result = super.update(entity);
        this.flowApi.saveWorkOrder(entity);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void submit(T entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.preInsert();
            entity.setCode(baseApi.getCode(entity.getFlowCode()));
        }
        String status = this.flowApi.startFlowProcess(entity);
        entity.setStatus(status);
        entity.setCreateTime(new Date());
        super.insertOrUpdate(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public T approve(ApproveModel model) {
        T entity = this.selectById(model.getId());
        BeanUtils.copyProperties(model, entity);
        String state = this.flowApi.complete(entity);
        entity.setStatus(state);
        this.baseMapper.update(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        this.flowApi.deleteWorkOrders(Arrays.asList(id));
        return this.baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> ids) {
        this.flowApi.deleteWorkOrders(ids);
        return this.baseMapper.deleteBatch(ids);
    }
}
