package cn.silver.framework.online.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.online.dao.OnlineFormDatasourceMapper;
import cn.silver.framework.online.dao.OnlineFormMapper;
import cn.silver.framework.online.domain.OnlineForm;
import cn.silver.framework.online.domain.OnlineFormDatasource;
import cn.silver.framework.online.service.OnlineFormService;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线表单数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service("onlineFormService")
public class OnlineFormServiceImpl extends BaseServiceImpl<OnlineFormMapper, OnlineForm> implements OnlineFormService {

    @Autowired
    private OnlineFormMapper onlineFormMapper;
    @Autowired
    private OnlineFormDatasourceMapper onlineFormDatasourceMapper;


    /**
     * 保存新增对象。
     *
     * @param onlineForm      新增对象。
     * @param datasourceIdSet 在线表单关联的数据源Id集合。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OnlineForm saveNew(OnlineForm onlineForm, Set<String> datasourceIdSet) {
        onlineForm.setFormId(IdUtil.randomUUID());
        Date now = new Date();
        onlineForm.setUpdateTime(now);
        onlineForm.setCreateTime(now);
        onlineFormMapper.insert(onlineForm);
        if (CollUtil.isNotEmpty(datasourceIdSet)) {
            for (String datasourceId : datasourceIdSet) {
                OnlineFormDatasource onlineFormDatasource = new OnlineFormDatasource();
                onlineFormDatasource.setId(IdUtil.randomUUID());
                onlineFormDatasource.setFormId(onlineForm.getFormId());
                onlineFormDatasource.setDatasourceId(datasourceId);
                onlineFormDatasourceMapper.insert(onlineFormDatasource);
            }
        }
        return onlineForm;
    }

    /**
     * 更新数据对象。
     *
     * @param onlineForm         更新的对象。
     * @param originalOnlineForm 原有数据对象。
     * @param datasourceIdSet    在线表单关联的数据源Id集合。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlineForm onlineForm, OnlineForm originalOnlineForm, Set<String> datasourceIdSet) {
        onlineForm.setUpdateTime(new Date());
        onlineForm.setCreateTime(originalOnlineForm.getCreateTime());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        if (onlineFormMapper.update(onlineForm) != 1) {
            return false;
        }
        OnlineFormDatasource formDatasourceFilter = new OnlineFormDatasource();
        formDatasourceFilter.setFormId(onlineForm.getFormId());
        onlineFormDatasourceMapper.delete(formDatasourceFilter);
        if (CollUtil.isNotEmpty(datasourceIdSet)) {
            for (String datasourceId : datasourceIdSet) {
                OnlineFormDatasource onlineFormDatasource = new OnlineFormDatasource();
                onlineFormDatasource.setId(IdUtil.randomUUID());
                onlineFormDatasource.setFormId(onlineForm.getFormId());
                onlineFormDatasource.setDatasourceId(datasourceId);
                onlineFormDatasourceMapper.insert(onlineFormDatasource);
            }
        }
        return true;
    }

    /**
     * 删除指定数据。
     *
     * @param formId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String formId) {
        if (onlineFormMapper.deleteByPrimaryKey(formId) != 1) {
            return false;
        }
        OnlineFormDatasource formDatasourceFilter = new OnlineFormDatasource();
        formDatasourceFilter.setFormId(formId);
        onlineFormDatasourceMapper.delete(formDatasourceFilter);
        return true;
    }

    /**
     * 根据PageId，删除其所属的所有表单，以及表单关联的数据源数据。
     *
     * @param pageId 指定的pageId。
     * @return 删除数量。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByPageId(String pageId) {
        OnlineForm filter = new OnlineForm();
        filter.setPageId(pageId);
        List<OnlineForm> formList = onlineFormMapper.selectList(filter);
        Set<String> formIdSet = formList.stream().map(OnlineForm::getFormId).collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(formIdSet)) {
            onlineFormDatasourceMapper.deleteBatch(formIdSet);
        }
        return onlineFormMapper.delete(filter);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineFormListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineForm> getOnlineFormList(OnlineForm filter, String orderBy) {
        return onlineFormMapper.getOnlineFormList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineFormList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineForm> getOnlineFormListWithRelation(OnlineForm filter, String orderBy) {
        List<OnlineForm> resultList = onlineFormMapper.getOnlineFormList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        return resultList;
    }

    /**
     * 获取使用指定数据表的表单列表。
     *
     * @param tableId 数据表Id。
     * @return 使用该数据表的表单列表。
     */
    @Override
    public List<OnlineForm> getOnlineFormListByTableId(String tableId) {
        OnlineForm filter = new OnlineForm();
        filter.setMasterTableId(tableId);
        return onlineFormMapper.selectList(filter);
    }

    /**
     * 获取指定表单的数据源列表。
     *
     * @param formId 指定的表单。
     * @return 表单和数据源的多对多关联对象列表。
     */
    @Override
    public List<OnlineFormDatasource> getFormDatasourceListByFormId(String formId) {
        OnlineFormDatasource onlineFormDatasource = new OnlineFormDatasource();
        onlineFormDatasource.setFormId(formId);
        return onlineFormDatasourceMapper.selectList(onlineFormDatasource);
    }

    /**
     * 查询正在使用当前数据源的表单。
     *
     * @param datasourceId 数据源Id。
     * @return 正在使用当前数据源的表单列表。
     */
    @Override
    public List<OnlineForm> getOnlineFormListByDatasourceId(String datasourceId) {
        OnlineForm onlineForm = new OnlineForm();
        List<String> ids = new ArrayList<>();
        ids.add(datasourceId);
        List<OnlineFormDatasource> formDatasourceList = onlineFormDatasourceMapper.selectByIds(ids);
        if (CollUtil.isEmpty(formDatasourceList)) {
            return new LinkedList<>();
        }
        Collection<String> formIdSet = formDatasourceList.stream()
                .map(OnlineFormDatasource::getFormId).collect(Collectors.toSet());
        return onlineFormMapper.selectByIds(formIdSet);
    }

    @Override
    public List<OnlineForm> getOnlineFormListByPageIds(Set<String> pageIdSet) {
        return onlineFormMapper.selectByIds(pageIdSet);
    }

//    /**
//     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
//     *
//     * @param onlineForm         最新数据对象。
//     * @param originalOnlineForm 原有数据对象。
//     * @return 数据全部正确返回true，否则false。
//     */
//    @Override
//    public Response verifyRelatedData(OnlineForm onlineForm, OnlineForm originalOnlineForm) {
//        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
//        //这里是基于字典的验证。
//        if (this.needToVerify(onlineForm, originalOnlineForm, OnlineForm::getMasterTableId)
//                && !onlineTableService.existId(onlineForm.getMasterTableId())) {
//            return Response.error(String.format(errorMessageFormat, "表单主表id"));
//        }
//        //这里是一对多的验证
//        if (this.needToVerify(onlineForm, originalOnlineForm, OnlineForm::getPageId)
//                && !onlinePageService.existId(onlineForm.getPageId())) {
//            return Response.error(String.format(errorMessageFormat, "页面id"));
//        }
//        return Response.success();
//    }

}
