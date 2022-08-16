package cn.silver.framework.core.service;

import cn.silver.framework.core.domain.FlowEntity;
import cn.silver.framework.core.model.ApproveModel;
import cn.silver.framework.core.page.PageBean;
import com.github.pagehelper.PageInfo;

/**
 * @author Administrator
 */
public interface IFlowService<T extends FlowEntity> extends IBaseService<T> {

    /**
     * 获取我操作的数据（收藏、点赞、评论、分享）
     *
     * @return
     */
    PageInfo<T> selectHandles(PageBean page, T entity);

    /**
     * 初始化对象，生成code，id
     *
     * @param entity
     * @param <T>
     *
     * @return
     */
    <T extends FlowEntity> T init(T entity);

    void handle(T entity);

    void submit(T entity);

    T approve(ApproveModel model);

}
