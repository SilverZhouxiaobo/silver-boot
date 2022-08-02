package cn.silver.framework.online.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.silver.framework.common.utils.id.IdUtils;
import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.db.provider.SqlHelper;
import cn.silver.framework.online.dao.OnlineColumnMapper;
import cn.silver.framework.online.dao.OnlineColumnRuleMapper;
import cn.silver.framework.online.domain.OnlineColumn;
import cn.silver.framework.online.domain.OnlineColumnRule;
import cn.silver.framework.online.domain.constant.FieldFilterType;
import cn.silver.framework.online.service.OnlineColumnService;
import cn.silver.framework.online.service.OnlineTableService;
import cn.silver.framework.online.vo.SqlTableColumn;
import com.github.pagehelper.Page;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 字段数据数据操作服务类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@Service
public class OnlineColumnServiceImpl extends BaseServiceImpl<OnlineColumnMapper, OnlineColumn> implements OnlineColumnService {

    @Resource
    private OnlineColumnMapper onlineColumnMapper;
    @Resource
    private OnlineColumnRuleMapper onlineColumnRuleMapper;
    @Resource
    private OnlineTableService onlineTableService;
//    @Autowired
//    private OnlineProperties onlineProperties;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
//    @Override
//    protected BaseDaoMapper<OnlineColumn> mapper() {
//        return onlineColumnMapper;
//    }

    /**
     * 保存新增数据表字段列表。
     *
     * @param columnList    新增数据表字段对象列表。
     * @param onlineTableId 在线表对象的主键Id。
     * @return 插入的在线表字段数据。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<OnlineColumn> saveNewList(List<SqlTableColumn> columnList, String onlineTableId) {
        List<OnlineColumn> onlineColumnList = new LinkedList<>();
        if (CollUtil.isEmpty(columnList)) {
            return onlineColumnList;
        }
//        this.evictTableCache(onlineTableId);
        for (SqlTableColumn column : columnList) {
            OnlineColumn onlineColumn = new OnlineColumn();
            BeanUtil.copyProperties(column, onlineColumn, false);
            onlineColumn.setColumnId(IdUtils.randomUUID());
            onlineColumn.setTableId(onlineTableId);
            this.setDefault(onlineColumn);
            onlineColumnMapper.insert(onlineColumn);
            onlineColumnList.add(onlineColumn);
        }
        return onlineColumnList;
    }

    /**
     * 更新数据对象。
     *
     * @param onlineColumn         更新的对象。
     * @param originalOnlineColumn 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(OnlineColumn onlineColumn, OnlineColumn originalOnlineColumn) {
//        this.evictTableCache(onlineColumn.getTableId());
        onlineColumn.setUpdateTime(new Date());
        onlineColumn.setCreateTime(originalOnlineColumn.getCreateTime());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
//        UpdateWrapper<OnlineColumn> uw = this.createUpdateQueryForNullValue(onlineColumn, onlineColumn.getColumnId());
        return onlineColumnMapper.update(onlineColumn) == 1;
    }

    /**
     * 刷新数据库表字段的数据到在线表字段。
     *
     * @param sqlTableColumn 源数据库表字段对象。
     * @param onlineColumn   被刷新的在线表字段对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refresh(SqlTableColumn sqlTableColumn, OnlineColumn onlineColumn) {
//        this.evictTableCache(onlineColumn.getTableId());
        BeanUtil.copyProperties(sqlTableColumn, onlineColumn, false);
        String objectFieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, onlineColumn.getColumnName());
        onlineColumn.setObjectFieldName(objectFieldName);
        String objectFieldType = SqlHelper.getJavaType(onlineColumn.getColumnType());
        onlineColumn.setObjectFieldType(objectFieldType);
        onlineColumnMapper.update(onlineColumn);
    }

    /**
     * 删除指定数据。
     *
     * @param tableId  表Id。
     * @param columnId 字段Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(String tableId, String columnId) {
        OnlineColumn onlineColumn = new OnlineColumn();
        onlineColumn.setColumnId(columnId);
        return onlineColumnMapper.delete(onlineColumn) == 1;
    }

    /**
     * 当前服务的支持表为从表，根据主表的主键Id，删除一对多的从表数据。
     *
     * @param tableId 主表主键Id。
     * @return 删除数量。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByTableId(String tableId) {
        OnlineColumn deletedObject = new OnlineColumn();
        deletedObject.setTableId(tableId);
        return onlineColumnMapper.delete(deletedObject);
    }

    /**
     * 删除指定数据表Id集合中的表字段。
     *
     * @param tableIdSet 待删除的数据表Id集合。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByTableIdSet(Set<String> tableIdSet) {
        onlineColumnMapper.deleteBatch(tableIdSet);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineColumnListWithRelation)方法。
     *
     * @param filter 过滤对象。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineColumn> getOnlineColumnList(OnlineColumn filter) {
        return onlineColumnMapper.getOnlineColumnList(filter);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineColumnList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @return 查询结果集。
     */
    @Override
    public List<OnlineColumn> getOnlineColumnListWithRelation(OnlineColumn filter) {
        List<OnlineColumn> resultList = onlineColumnMapper.getOnlineColumnList(filter);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
//        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 获取指定数据表Id集合的字段对象列表。
     *
     * @param tableIdSet 指定的数据表Id集合。
     * @return 数据表Id集合所包含的字段对象列表。
     */
    @Override
    public List<OnlineColumn> getOnlineColumnListByTableIds(Set<String> tableIdSet) {
        return onlineColumnMapper.selectByIds(tableIdSet);
    }

    /**
     * 根据表Id和字段列名获取指定字段。
     *
     * @param tableId    字段所在表Id。
     * @param columnName 字段名。
     * @return 查询出的字段对象。
     */
    @Override
    public OnlineColumn getOnlineColumnByTableIdAndColumnName(String tableId, String columnName) {
        OnlineColumn filter = new OnlineColumn();
        filter.setTableId(tableId);
        filter.setColumnName(columnName);
        return onlineColumnMapper.selectOne(filter);
    }

    /**
     * 批量添加多对多关联关系。
     *
     * @param onlineColumnRuleList 多对多关联表对象集合。
     * @param columnId             主表Id。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOnlineColumnRuleList(List<OnlineColumnRule> onlineColumnRuleList, String columnId) {
        for (OnlineColumnRule onlineColumnRule : onlineColumnRuleList) {
            onlineColumnRule.setColumnId(columnId);
            onlineColumnRuleMapper.insert(onlineColumnRule);
        }
    }

    /**
     * 更新中间表数据。
     *
     * @param onlineColumnRule 中间表对象。
     * @return 更新成功与否。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOnlineColumnRule(OnlineColumnRule onlineColumnRule) {
        OnlineColumnRule filter = new OnlineColumnRule();
        filter.setColumnId(onlineColumnRule.getColumnId());
        filter.setRuleId(onlineColumnRule.getRuleId());
//        UpdateWrapper<OnlineColumnRule> uw =
//                BaseService.createUpdateQueryForNullValue(onlineColumnRule, OnlineColumnRule.class);
//        uw.setEntity(filter);
        return onlineColumnRuleMapper.update(filter) > 0;
    }

    /**
     * 获取中间表数据。
     *
     * @param columnId 主表Id。
     * @param ruleId   从表Id。
     * @return 中间表对象。
     */
    @Override
    public OnlineColumnRule getOnlineColumnRule(String columnId, String ruleId) {
        OnlineColumnRule filter = new OnlineColumnRule();
        filter.setColumnId(columnId);
        filter.setRuleId(ruleId);
        return onlineColumnRuleMapper.selectOne(filter);
    }

    /**
     * 移除单条多对多关系。
     *
     * @param columnId 主表Id。
     * @param ruleId   从表Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeOnlineColumnRule(String columnId, String ruleId) {
        OnlineColumnRule filter = new OnlineColumnRule();
        filter.setColumnId(columnId);
        filter.setRuleId(ruleId);
        return onlineColumnRuleMapper.delete(filter) == 1;
    }

//    /**
//     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
//     *
//     * @param onlineColumn         最新数据对象。
//     * @param originalOnlineColumn 原有数据对象。
//     * @return 数据全部正确返回true，否则false。
//     */
//    @Override
//    public Response verifyRelatedData(OnlineColumn onlineColumn, OnlineColumn originalOnlineColumn) {
//        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
//        //这里是一对多的验证
//        if (this.needToVerify(onlineColumn, originalOnlineColumn, OnlineColumn::getTableId)
//                && !onlineTableService.existId(onlineColumn.getTableId())) {
//            return Response.error(String.format(errorMessageFormat, "数据表Id"));
//        }
//        return Response.ok();
//    }

    private void setDefault(OnlineColumn onlineColumn) {
        String objectFieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, onlineColumn.getColumnName());
        onlineColumn.setObjectFieldName(objectFieldName);
        String objectFieldType = SqlHelper.getJavaType(objectFieldName);
        onlineColumn.setObjectFieldType(objectFieldType);
        onlineColumn.setFilterType(FieldFilterType.NO_FILTER);
        onlineColumn.setParentKey(false);
        onlineColumn.setDeptFilter(false);
        onlineColumn.setUserFilter(false);
        Date now = new Date();
        onlineColumn.setUpdateTime(now);
        onlineColumn.setCreateTime(now);
    }

//    private void evictTableCache(String tableId) {
//        String tableIdKey = RedisKeyUtil.makeOnlineTableKey(tableId);
//        redissonClient.getBucket(tableIdKey).delete();
//    }

//    private String convertToJavaType(String columnType) {
//        if (onlineProperties.getDatabaseType().equals(CoreProperties.MYSQL_TYPE)) {
//            if ("varchar".equals(columnType)
//                    || "char".equals(columnType)
//                    || "text".equals(columnType)
//                    || "longtext".equals(columnType)
//                    || "mediumtext".equals(columnType)
//                    || "tinytext".equals(columnType)) {
//                return "String";
//            }
//            if ("int".equals(columnType)
//                    || "mediumint".equals(columnType)
//                    || "smallint".equals(columnType)
//                    || "tinyint".equals(columnType)) {
//                return "Integer";
//            }
//            if ("bit".equals(columnType)) {
//                return "Boolean";
//            }
//            if ("bigint".equals(columnType)) {
//                return "Long";
//            }
//            if ("decimal".equals(columnType)) {
//                return "BigDecimal";
//            }
//            if ("float".equals(columnType)
//                    || "double".equals(columnType)) {
//                return "Double";
//            }
//            if ("date".equals(columnType)
//                    || "datetime".equals(columnType)
//                    || "timestamp".equals(columnType)
//                    || "time".equals(columnType)) {
//                return "Date";
//            }
//            if ("blob".equals(columnType)) {
//                return "byte[]";
//            }
//        } else if (onlineProperties.getDatabaseType().equals(CoreProperties.POSTGRESQL_TYPE)) {
//            if ("varchar".equals(columnType)
//                    || "char".equals(columnType)
//                    || "text".equals(columnType)) {
//                return "String";
//            }
//            if ("int4".equals(columnType)
//                    || "int2".equals(columnType)
//                    || "bit".equals(columnType)) {
//                return "Integer";
//            }
//            if ("bool".equals(columnType)) {
//                return "Boolean";
//            }
//            if ("int8".equals(columnType)) {
//                return "Long";
//            }
//            if ("numeric".equals(columnType)) {
//                return "BigDecimal";
//            }
//            if ("float4".equals(columnType)
//                    || "float8".equals(columnType)) {
//                return "Double";
//            }
//            if ("date".equals(columnType)
//                    || "timestamp".equals(columnType)
//                    || "time".equals(columnType)) {
//                return "Date";
//            }
//            if ("bytea".equals(columnType)) {
//                return "byte[]";
//            }
//        }
//        throw new RuntimeException("Unsupported Data Type");
//    }
}
