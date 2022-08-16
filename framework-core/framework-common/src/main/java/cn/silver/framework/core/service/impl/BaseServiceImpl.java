package cn.silver.framework.core.service.impl;

import cn.silver.framework.common.exception.CustomException;
import cn.silver.framework.core.api.ISysBaseApi;
import cn.silver.framework.core.bean.LoginUser;
import cn.silver.framework.core.bean.ResponseEnum;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.security.util.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> implements IBaseService<T> {

    @Autowired
    protected M baseMapper;
    @Autowired
    protected ISysBaseApi baseApi;

    @Override
    public List<T> selectAll() {
        return this.baseMapper.selectAll();
    }

    @Override
    public List<T> selectList(T entity) {
        return this.baseMapper.selectList(entity);
    }

    @Override
    public List<T> selectByIds(Collection<String> ids) {
        return this.baseMapper.selectByIds(ids);
    }

    @Override
    public List<T> selectByValues(Collection<String> values) {
        return this.baseMapper.selectByValues(values);
    }

    @Override
    public PageInfo<T> selectPage(PageBean bean, T entity) {
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize(), bean.getOrderBy());
        return new PageInfo<>(this.baseMapper.selectList(entity));
    }

    @Override
    public T selectOne(T entity) {
        return this.baseMapper.selectOne(entity);
    }

    @Override
    public T selectById(String id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public List<T> selectExists(T entity) {
        return this.baseMapper.selectExists(entity);
    }

    @Override
    public T selectByValue(String value) {
        return this.baseMapper.selectByValue(value);
    }

    @Override
    public String getLabelByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String label = "";
        if (value.contains(",")) {
            List<T> entities = this.baseMapper.selectByValues(Arrays.asList(value.split(",")));
            label = CollectionUtils.isNotEmpty(entities) ? entities.stream().map(BaseEntity::getLabel).collect(Collectors.joining(",")) : "";
        } else {
            T entity = this.baseMapper.selectByValue(value);
            label = ObjectUtils.isNotEmpty(entity) ? entity.getLabel() : "";
        }
        return label;
    }

    @Override
    public int save(T entity, boolean updateSupport) {
        int result = 0;
        if (updateSupport) {
            List<T> exists = this.selectExists(entity);
            if (CollectionUtils.isNotEmpty(exists)) {
                entity.setId(exists.get(0).getId());
                result = this.update(entity);
            } else {
                result = this.insert(entity);
            }
        } else {
            result = this.insert(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(T entity) {
        if (entity.checkExists()) {
            List<T> exists = this.selectExists(entity);
            if (CollectionUtils.isNotEmpty(exists)) {
                throw new CustomException(ResponseEnum.DATA_ERROR_EXIST);
            }
        }
        entity.preInsert();
        return this.baseMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertBatch(Collection<T> entities) {
        if (CollectionUtils.isNotEmpty(entities)) {
            entities.stream().forEach(entity -> {
                entity.preInsert();
            });
            return this.baseMapper.insertBatch(entities);
        } else {
            return 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(T entity) {
        if (entity.checkExists()) {
            List<T> exists = this.selectExists(entity);
            if (CollectionUtils.isNotEmpty(exists)) {
                throw new CustomException(ResponseEnum.DATA_ERROR_EXIST);
            }
        }
        entity.preUpdate();
        return this.baseMapper.update(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateBatch(Collection<T> entities) {
        int result = 0;
        for (T entity : entities) {
            entity.preUpdate();
            result += this.baseMapper.update(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertOrUpdate(T entity) {
        if (StringUtils.isBlank(entity.getId())) {
            return this.insert(entity);
        } else {
            T exist = this.selectById(entity.getId());
            if (ObjectUtils.isNotEmpty(exist)) {
                return this.update(entity);
            } else {
                return this.insert(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertOrUpdateBatch(Collection<T> entities) {
        Set<String> ids = entities.stream().filter(entity -> StringUtils.isNotBlank(entity.getId())).map(BaseEntity::getId).collect(Collectors.toSet());
        List<T> insertEntities = new ArrayList<>(entities.size());
        List<T> updateEntities = new ArrayList<>(entities.size());
        if (CollectionUtils.isNotEmpty(ids)) {
            Collection<T> exists = this.baseMapper.selectByIds(ids);
            if (CollectionUtils.isNotEmpty(exists)) {
                ids = exists.stream().filter(entity -> StringUtils.isNotBlank(entity.getId())).map(BaseEntity::getId).collect(Collectors.toSet());
            } else {
                ids = null;
            }
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            for (T entity : entities) {
                if (StringUtils.isNotBlank(entity.getId()) && ids.contains(entity.getId())) {
                    updateEntities.add(entity);
                } else {
                    insertEntities.add(entity);
                }
            }
        } else {
            insertEntities.addAll(entities);
        }
        int result = 0;
        if (CollectionUtils.isNotEmpty(updateEntities)) {
            result += this.updateBatch(updateEntities);
        }
        if (CollectionUtils.isNotEmpty(insertEntities)) {
            result += this.insertBatch(insertEntities);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String id) {
        return this.baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteBatch(Collection<String> ids) {
        return this.baseMapper.deleteBatch(ids);
    }

    protected LoginUser getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
