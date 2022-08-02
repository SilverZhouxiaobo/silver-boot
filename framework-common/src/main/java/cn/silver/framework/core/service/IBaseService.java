package cn.silver.framework.core.service;

import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.page.PageBean;
import com.github.pagehelper.PageInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 */
public interface IBaseService<T extends BaseEntity> {

    /**
     * 查询全部数据
     *
     * @return
     */
    List<T> selectAll();

    List<T> selectList(T entity);

    List<T> selectByIds(Collection<String> ids);

    List<T> selectByValues(Collection<String> values);

    PageInfo<T> selectPage(PageBean page, T entity);

    T selectOne(T eventRecord);

    T selectById(String id);

    List<T> selectExists(T entity);

    T selectByValue(String value);

    String getLabelByValue(String value);

    int save(T entity, boolean updateSupport);

    int insert(T entity);

    int insertBatch(Collection<T> entities);

    int update(T entity);

    int updateBatch(Collection<T> entities);

    int insertOrUpdate(T entity);

    int insertOrUpdateBatch(Collection<T> entities);

    int delete(String id);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    int deleteBatch(Collection<String> ids);
}
