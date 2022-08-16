package cn.silver.framework.online.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.online.dao.*;
import cn.silver.framework.online.domain.*;
import cn.silver.framework.online.service.OnlinePageService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在线表单页面数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("onlinePageService")
public class OnlinePageServiceImpl extends BaseServiceImpl<OnlinePageMapper, OnlinePage> implements OnlinePageService {

    @Autowired
    private OnlinePageMapper onlinePageMapper;
    @Autowired
    private OnlinePageDatasourceMapper onlinePageDatasourceMapper;
    @Autowired
    private OnlineFormMapper onlineFormMapper;
    @Resource
    private OnlineDatasourceTableMapper onlineDatasourceTableMapper;
    @Autowired
    private OnlineDatasourceMapper onlineDatasourceMapper;
    @Autowired
    private OnlineDatasourceRelationMapper onlineDatasourceRelationMapper;
    @Autowired
    private OnlineTableMapper onlineTableMapper;

//    /**
//     * 返回当前Service的主表Mapper对象。
//     *
//     * @return 主表Mapper对象。
//     */
//    @Override
//    protected BaseDaoMapper<OnlinePage> mapper() {
//        return onlinePageMapper;
//    }

    /**
     * 保存新增对象。
     *
     * @param onlinePage 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnlinePage saveNew(OnlinePage onlinePage) {
        onlinePage.setPageId(IdUtil.randomUUID());
        Date now = new Date();
        onlinePage.setUpdateTime(now);
        onlinePage.setCreateTime(now);
        onlinePage.setPublished(false);
//        MyModelUtil.setDefaultValue(onlinePage, "status", PageStatus.BASIC);
        onlinePageMapper.insert(onlinePage);
        return onlinePage;
    }

    /**
     * 更新数据对象。
     *
     * @param onlinePage         更新的对象。
     * @param originalOnlinePage 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlinePage onlinePage, OnlinePage originalOnlinePage) {
        onlinePage.setUpdateTime(new Date());
        onlinePage.setCreateTime(originalOnlinePage.getCreateTime());
        onlinePage.setPublished(originalOnlinePage.getPublished());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
//        UpdateWrapper<OnlinePage> uw = this.createUpdateQueryForNullValue(onlinePage, onlinePage.getPageId());
        return onlinePageMapper.update(onlinePage) == 1;
    }

    /**
     * 更新页面对象的发布状态。
     *
     * @param pageId    页面对象Id。
     * @param published 新的状态。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePublished(String pageId, Boolean published) {
        OnlinePage onlinePage = new OnlinePage();
        onlinePage.setPageId(pageId);
        onlinePage.setPublished(published);
        onlinePageMapper.update(onlinePage);
    }

    /**
     * 删除指定数据，及其包含的表单和数据源等。
     *
     * @param pageId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String pageId) {
        OnlinePage onlinePage = new OnlinePage();
        onlinePage.setPageId(pageId);
        if (onlinePageMapper.delete(onlinePage) == 0) {
            return false;
        }
        // 开始删除关联表单。
        OnlineForm filter = new OnlineForm();
        filter.setPageId(pageId);
        List<OnlineForm> formList = onlineFormMapper.selectList(filter);
        Set<String> formIdSet = formList.stream().map(OnlineForm::getFormId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(formIdSet)) {
            onlineFormMapper.deleteBatch(formIdSet);
        }
        // 先获取出关联的表单和数据源。
        OnlinePageDatasource pageDatasourceFilter = new OnlinePageDatasource();
        pageDatasourceFilter.setPageId(pageId);
        List<OnlinePageDatasource> pageDatasourceList =
                onlinePageDatasourceMapper.selectList(pageDatasourceFilter);
        if (CollUtil.isNotEmpty(pageDatasourceList)) {
            for (OnlinePageDatasource pageDatasource : pageDatasourceList) {
                OnlineDatasource onlineDatasource = new OnlineDatasource();
                onlineDatasource.setDatasourceId(pageDatasource.getDatasourceId());
                if (onlineDatasourceMapper.delete(onlineDatasource) == 0) {
                    return false;
                }
                OnlineDatasourceRelation deletedObject = new OnlineDatasourceRelation();
                deletedObject.setDatasourceId(pageDatasource.getDatasourceId());
                onlineDatasourceRelationMapper.deleteByPrimaryKey(pageDatasource.getDatasourceId());
                // 开始删除多对多父表的关联
                OnlinePageDatasource onlinePageDatasource = new OnlinePageDatasource();
                onlinePageDatasource.setDatasourceId(pageDatasource.getDatasourceId());
                onlinePageDatasourceMapper.delete(onlinePageDatasource);
                OnlineDatasourceTable datasourceTable = new OnlineDatasourceTable();
                datasourceTable.setDatasourceId(pageDatasource.getDatasourceId());
                List<OnlineDatasourceTable> datasourceTableList = onlineDatasourceTableMapper.selectList(datasourceTable);
                onlineDatasourceTableMapper.delete(datasourceTable);
                Set<String> tableIdSet = datasourceTableList.stream()
                        .map(OnlineDatasourceTable::getTableId).collect(Collectors.toSet());
                onlineTableMapper.deleteBatch(tableIdSet);
            }
        }
        return true;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlinePageListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlinePage> getOnlinePageList(OnlinePage filter, String orderBy) {
        return onlinePageMapper.getOnlinePageList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlinePageList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlinePage> getOnlinePageListWithRelation(OnlinePage filter, String orderBy) {
        List<OnlinePage> resultList = onlinePageMapper.getOnlinePageList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
//        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param onlinePageDatasourceList 多对多关联表对象集合。
     * @param pageId                   主表Id。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOnlinePageDatasourceList(List<OnlinePageDatasource> onlinePageDatasourceList, String pageId) {
        for (OnlinePageDatasource onlinePageDatasource : onlinePageDatasourceList) {
            onlinePageDatasource.setPageId(pageId);
            onlinePageDatasourceMapper.insert(onlinePageDatasource);
        }
    }

    /**
     * 获取中间表数据。
     *
     * @param pageId       主表Id。
     * @param datasourceId 从表Id。
     * @return 中间表对象。
     */
    @Override
    public OnlinePageDatasource getOnlinePageDatasource(String pageId, String datasourceId) {
        OnlinePageDatasource filter = new OnlinePageDatasource();
        filter.setPageId(pageId);
        filter.setDatasourceId(datasourceId);
        return onlinePageDatasourceMapper.selectOne(filter);
    }

    @Override
    public List<OnlinePageDatasource> getOnlinePageDatasourceListByPageId(String pageId) {
        OnlinePageDatasource filter = new OnlinePageDatasource();
        filter.setPageId(pageId);
        return onlinePageDatasourceMapper.selectList(filter);
    }

    /**
     * 根据数据源Id，返回使用该数据源的OnlinePage对象。
     *
     * @param datasourceId 数据源Id。
     * @return 使用该数据源的页面列表。
     */
    @Override
    public List<OnlinePage> getOnlinePageListByDatasourceId(String datasourceId) {
        return onlinePageMapper.getOnlinePageListByDatasourceId(datasourceId);
    }

    /**
     * 移除单条多对多关系。
     *
     * @param pageId       主表Id。
     * @param datasourceId 从表Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeOnlinePageDatasource(String pageId, String datasourceId) {
        OnlinePageDatasource filter = new OnlinePageDatasource();
        filter.setPageId(pageId);
        filter.setDatasourceId(datasourceId);
        return onlinePageDatasourceMapper.delete(filter) > 0;
    }
}
