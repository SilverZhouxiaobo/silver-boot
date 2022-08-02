package cn.silver.framework.online.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.online.domain.OnlineColumn;
import cn.silver.framework.online.domain.OnlineColumnRule;
import cn.silver.framework.online.vo.SqlTableColumn;

import java.util.List;
import java.util.Set;

/**
 * 字段数据数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface OnlineColumnService extends IBaseService<OnlineColumn> {

    /**
     * 保存新增数据表字段列表。
     *
     * @param columnList    新增数据表字段对象列表。
     * @param onlineTableId 在线表对象的主键Id。
     * @return 插入的在线表字段数据。
     */
    List<OnlineColumn> saveNewList(List<SqlTableColumn> columnList, String onlineTableId);

    /**
     * 更新数据对象。
     *
     * @param onlineColumn         更新的对象。
     * @param originalOnlineColumn 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(OnlineColumn onlineColumn, OnlineColumn originalOnlineColumn);

    /**
     * 刷新数据库表字段的数据到在线表字段。
     *
     * @param sqlTableColumn 源数据库表字段对象。
     * @param onlineColumn   被刷新的在线表字段对象。
     */
    void refresh(SqlTableColumn sqlTableColumn, OnlineColumn onlineColumn);

    /**
     * 删除指定数据。
     *
     * @param tableId  表Id。
     * @param columnId 字段Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(String tableId, String columnId);

    /**
     * 批量添加多对多关联关系。
     *
     * @param onlineColumnRuleList 多对多关联表对象集合。
     * @param columnId             主表Id。
     */
    void addOnlineColumnRuleList(List<OnlineColumnRule> onlineColumnRuleList, String columnId);

    /**
     * 更新中间表数据。
     *
     * @param onlineColumnRule 中间表对象。
     * @return 更新成功与否。
     */
    boolean updateOnlineColumnRule(OnlineColumnRule onlineColumnRule);

    /**
     * 获取中间表数据。
     *
     * @param columnId 主表Id。
     * @param ruleId   从表Id。
     * @return 中间表对象。
     */
    OnlineColumnRule getOnlineColumnRule(String columnId, String ruleId);

    /**
     * 移除单条多对多关系。
     *
     * @param columnId 主表Id。
     * @param ruleId   从表Id。
     * @return 成功返回true，否则false。
     */
    boolean removeOnlineColumnRule(String columnId, String ruleId);

    /**
     * 当前服务的支持表为从表，根据主表的主键Id，删除一对多的从表数据。
     *
     * @param tableId 主表主键Id。
     * @return 删除数量。
     */
    int removeByTableId(String tableId);

    /**
     * 删除指定数据表Id集合中的表字段。
     *
     * @param tableIdSet 待删除的数据表Id集合。
     */
    void removeByTableIdSet(Set<String> tableIdSet);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineColumnListWithRelation)方法。
     *
     * @param filter 过滤对象。
     * @return 查询结果集。
     */
    List<OnlineColumn> getOnlineColumnList(OnlineColumn filter);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineColumnList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @return 查询结果集。
     */
    List<OnlineColumn> getOnlineColumnListWithRelation(OnlineColumn filter);

    /**
     * 获取指定数据表Id集合的字段对象列表。
     *
     * @param tableIdSet 指定的数据表Id集合。
     * @return 数据表Id集合所包含的字段对象列表。
     */
    List<OnlineColumn> getOnlineColumnListByTableIds(Set<String> tableIdSet);

    /**
     * 根据表Id和字段列名获取指定字段。
     *
     * @param tableId    字段所在表Id。
     * @param columnName 字段名。
     * @return 查询出的字段对象。
     */
    OnlineColumn getOnlineColumnByTableIdAndColumnName(String tableId, String columnName);
}
