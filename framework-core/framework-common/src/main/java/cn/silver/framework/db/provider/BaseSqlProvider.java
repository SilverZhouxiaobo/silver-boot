package cn.silver.framework.db.provider;

import cn.silver.framework.common.utils.StringUtils;
import cn.silver.framework.core.annotation.Column;
import cn.silver.framework.core.annotation.DataRelation;
import cn.silver.framework.core.constant.SearchType;
import cn.silver.framework.core.domain.BaseEntity;
import cn.silver.framework.core.domain.DataEntity;
import cn.silver.framework.core.domain.FlowEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import tk.mybatis.mapper.LogicDeleteException;
import tk.mybatis.mapper.annotation.LogicDelete;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.util.MetaObjectUtil;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author Administrator
 */
@Slf4j
public class BaseSqlProvider<T extends BaseEntity> extends MapperTemplate {

    public BaseSqlProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    @SneakyThrows
    public String selectList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        DataRelation relation = entityClass.getAnnotation(DataRelation.class);
        String alias = relation != null ? relation.localAlias() : "t";
        if (relation != null) {
            sql.append(SqlHelper.selectAllColumns(entityClass, relation.localAlias()));
            Class<?> targetClass = relation.target();
            entityClassMap.put(relation.property(), targetClass);
            try {
                setResultType(ms, entityClass);
                sql.append("," + SqlHelper.getAllColumns(relation.target(), relation.targetAlias()));
            } catch (Exception e) {
                log.error("未找到目标表映射信息:" + e.getMessage(), e);
                log.info("开始初始化目标表信息");
                EntityHelper.initEntityNameMap(targetClass, mapperHelper.getConfig());
                setResultType(ms, entityClass);
                sql.append("," + SqlHelper.getAllColumns(relation.target(), relation.targetAlias()));
            }
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
            sql.append(relation.localAlias() + " " + relation.joiner() + " " + tableName(relation.target()) + " " + relation.targetAlias()
                    + " on " + relation.localAlias() + "." + relation.column() + "=" + relation.targetAlias() + "." + relation.targetColumn());
            if (relation.target().newInstance() instanceof DataEntity) {
                sql.append(" and " + relation.targetAlias() + ".deleted='0' ");
            }
        } else {
            sql.append(SqlHelper.selectAllColumns(entityClass, alias));
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)) + alias);
        }
        // 逻辑删除的未删除查询条件
        sql.append("<where>");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (SearchType.BETWEEN.getCode().equals(column.searchType().getCode())) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(column.target())) {
                        sql.append("<if test=\"params." + column.target() + "_begin != null and params." + column.target() + "_begin != ''\">");
                        sql.append("AND " + alias + "." + column.name() + " &gt;= to_date(#{params." + column.target() + "_begin},'" + column.format() + "')</if>");
                        sql.append("<if test=\"params." + column.target() + "_end != null and params." + column.target() + "_end != ''\">");
                        sql.append("AND " + alias + "." + column.name() + " &lt;= (to_date(#{params." + column.target() + "_end},'" + column.format() + "'))</if>");
                    } else {
                        sql.append("<if test=\"beginTime != null and beginTime != ''\">");
                        sql.append("AND " + alias + "." + column.name() + " &gt;= to_date(#{beginTime},'" + column.format() + "')</if>");
                        sql.append("<if test=\"endTime != null and endTime != ''\">");
                        sql.append("AND " + alias + "." + column.name() + " &lt;= (to_date(#{endTime},'" + column.format() + "'))</if>");
                    }
                } else if (SearchType.IN.getCode().equals(column.searchType().getCode())) {
                    String fieldName = StringUtils.isNotBlank(column.target()) ? column.target() : "params." + field.getName();
                    sql.append("<if test=\"" + fieldName + " != null and " + fieldName + " != ''\">");
                    sql.append(" and " + alias + "." + column.name() + " in ");
                    sql.append("<foreach collection=\"" + fieldName + "\" open=\"(\" close=\")\" separator=\",\" item=\"item\">\n" +
                            "            #{item}\n" +
                            "</foreach>");
                    sql.append("</if>");
                } else {
                    String fieldName = StringUtils.isNotBlank(column.target()) ? column.target() : field.getName();
                    String target = SearchType.LIKE.getCode().equals(column.searchType().getCode()) ? " concat(concat('%',#{" + fieldName + "}),'%')"
                            : "Date".equals(field.getType().getSimpleName()) ? " to_date(#{" + fieldName + "},'" + column.format() + "')" : "#{" + fieldName + "}";
                    sql.append("<if test=\"" + fieldName + " != null and " + fieldName + " != ''\">");
                    sql.append(" and " + alias + "." + column.name() + " " + column.searchType().getExpression() + target + "</if>");
                }
            }
        }
        if (entityClass.newInstance() instanceof DataEntity) {
            sql.append("<if test=\"beginTime != null and beginTime != ''\">");
            sql.append("AND " + alias + ".create_time &gt;= to_date(#{beginTime},'yyyy-mm-dd hh24:mi:ss')</if>");
            sql.append("<if test=\"endTime != null and endTime != ''\">");
            sql.append("AND " + alias + ".create_time &lt;= (to_date(#{endTime},'yyyy-mm-dd hh24:mi:ss'))</if>");
        }
        if (entityClass.newInstance() instanceof FlowEntity) {
            sql.append("<if test=\"draftFlag != null and draftFlag\"> and " + alias + ".status = 'draft'</if>");
            sql.append("<if test=\"draftFlag != null and !draftFlag\"> and " + alias + ".status &lt;> 'draft'</if>");
            sql.append("<if test=\"status != null and status != ''\"> and " + alias + ".status=#{status} </if>");
            sql.append("<if test=\"code != null and code != ''\"> and " + alias + ".code=#{code} </if>");
        }
        sql.append("<if test=\"id != null and id != ''\"> and " + alias + ".id &lt;> #{id} </if>");
        sql.append(SqlHelper.whereLogicDelete(entityClass, alias, false));
        sql.append("</where>");
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    public String selectCollects(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        String tableName = tableName(entityClass);
        sql.append(SqlHelper.fromTable(entityClass, tableName));
        // 逻辑删除的未删除查询条件
        sql.append("<where>");
        sql.append(" id in (select business_key from sys_user_collect where user_id=#{currUser} and business_type = '" + tableName + "')");
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    public String selectByIds(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        // 逻辑删除的未删除查询条件
        sql.append("<where>");
        sql.append(" id in ");
        sql.append("<foreach collection=\"ids\" open=\"(\" close=\")\" separator=\",\" item=\"id\">\n" +
                "            #{id}\n" +
                "        </foreach>");
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    public String selectByPid(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        // 逻辑删除的未删除查询条件
        sql.append(" where pid=#{pid} and deleted='0'");
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    public String selectByPids(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        // 逻辑删除的未删除查询条件
        sql.append("<where>");
        sql.append(" pid = #{pid}");
        sql.append("<foreach collection=\"pids\" open=\"(\" close=\")\" separator=\",\" item=\"pid\">\n" +
                "            #{pid}\n" +
                "        </foreach>");
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    public String selectByValues(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        Field[] fields = entityClass.getDeclaredFields();
        String columnName = "id";
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).dictable()) {
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
                break;
            }
        }
        sql.append(columnName + " in ");
        sql.append("<foreach collection=\"values\" open=\"(\" close=\")\" separator=\",\" item=\"value\">\n" +
                "            #{value}\n" +
                "        </foreach>");
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    @SneakyThrows
    public String selectById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        DataRelation relation = entityClass.getAnnotation(DataRelation.class);
        String alias = relation != null ? relation.localAlias() : "t";
        if (relation != null) {
            sql.append(SqlHelper.selectAllColumns(entityClass, relation.localAlias()));
            Class<?> targetClass = relation.target();
            entityClassMap.put(relation.property(), targetClass);
            try {
                setResultType(ms, entityClass);
                sql.append("," + SqlHelper.getAllColumns(relation.target(), relation.targetAlias()));
            } catch (Exception e) {
                log.error("未找到目标表映射信息:" + e.getMessage(), e);
                log.info("开始初始化目标表信息");
                EntityHelper.initEntityNameMap(targetClass, mapperHelper.getConfig());
                setResultType(ms, entityClass);
                sql.append("," + SqlHelper.getAllColumns(relation.target(), relation.targetAlias()));
            }
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
            sql.append(relation.localAlias() + " " + relation.joiner() + " " + tableName(relation.target()) + " " + relation.targetAlias()
                    + " on " + relation.localAlias() + "." + relation.column() + "=" + relation.targetAlias() + "." + relation.targetColumn());
            if (relation.target().newInstance() instanceof DataEntity) {
                sql.append(" and " + relation.targetAlias() + ".deleted='0' ");
            }
        } else {
            sql.append(SqlHelper.selectAllColumns(entityClass, alias));
            sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)) + alias);
        }
        // 逻辑删除的未删除查询条件
        sql.append("<where> " + alias + ".id=#{id}");
        sql.append(SqlHelper.whereLogicDelete(entityClass, alias, false));
        sql.append("</where>");
        return sql.toString();
    }

    public String selectByValue(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        Field[] fields = entityClass.getDeclaredFields();
        String columnName = "id";
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).dictable()) {
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
                break;
            }
        }
        sql.append(columnName + "=#{value}");
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    public String selectExists(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>");
        Field[] fields = entityClass.getDeclaredFields();
        sql.append("<if test=\"id != null and id != '' \"> AND id &lt;> #{id}</if>");
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).unique()) {
                Column column = field.getAnnotation(Column.class);
                String fieldName = StringUtils.isNotBlank(column.target()) ? column.target() : field.getName();
                sql.append("<if test=\"" + fieldName + " != null and " + fieldName + " != ''\">");
                sql.append(" and " + column.name() + "=#{" + fieldName + "}</if>");
            }
        }
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    public String insert(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append("values (");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (logicDeleteColumn != null && logicDeleteColumn == column) {
                sql.append(SqlHelper.getLogicDeletedValue(column, false)).append(",");
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            sql.append("#{" + column.getProperty() + ", jdbcType= " + SqlHelper.getJdbcType(column) + "},");
        }
        sql = sql.delete(sql.length() - 1, sql.length());
        sql.append(")");
        return sql.toString();
    }

    public String insertBatch(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        String tableName = SqlHelper.getDynamicTableName(entityClass, tableName(entityClass));
        sql.append("insert all ");
        sql.append("<foreach item=\"item\" index=\"index\" collection=\"entities\">");
        sql.append(" INTO " + tableName);
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(" values (");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (logicDeleteColumn != null && logicDeleteColumn == column) {
                sql.append(SqlHelper.getLogicDeletedValue(column, false)).append(",");
                continue;
            }
            sql.append("#{item." + column.getProperty() + ", jdbcType= " + SqlHelper.getJdbcType(column) + "},");
        }
        sql = sql.delete(sql.length() - 1, sql.length());
        sql.append(")");
        sql.append("</foreach>");
        sql.append("select 1 from dual");
        return sql.toString();
    }

    public String update(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        // 逻辑删除列
        EntityColumn logicDeleteColumn = null;
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new LogicDeleteException(entityClass.getCanonicalName() + " 中包含多个带有 @LogicDelete 注解的字段，一个类中只能存在一个带有 @LogicDelete 注解的字段!");
                }
                logicDeleteColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                sql.append("<if test=\"" + column.getProperty() + " != null\">");
                sql.append(column.getColumn() + "=#{" + column.getProperty() + ", jdbcType= " + SqlHelper.getJdbcType(column) + "},</if>");
            }
        }
        sql.append("</set>");
        sql.append(SqlHelper.wherePKColumns(entityClass, true));
        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms
     *
     * @return
     */
    public String updateSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, null, true, isNotEmpty()));
        sql.append(SqlHelper.wherePKColumns(entityClass, true));
        return sql.toString();
    }

    public String deleteBatch(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        //如果设置了安全删除，就不允许执行不带查询条件的 delete 方法
        if (getConfig().isSafeDelete()) {
            sql.append(SqlHelper.notAllNullParameterCheck("_parameter", EntityHelper.getColumns(entityClass)));
        }
        // 如果是逻辑删除，则修改为更新表，修改逻辑删除字段的值
        if (SqlHelper.hasLogicDeleteColumn(entityClass)) {
            sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
            sql.append("<set>");
            sql.append(SqlHelper.logicDeleteColumnEqualsValue(entityClass, true));
            sql.append("</set>");
            MetaObjectUtil.forObject(ms).setValue("sqlCommandType", SqlCommandType.UPDATE);
        } else {
            sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        }
        sql.append(" where id in ");
        sql.append("<foreach collection=\"ids\" open=\"(\" close=\")\" separator=\",\" item=\"id\">\n" +
                "            #{id}\n" +
                "        </foreach>");
        return sql.toString();
    }
}
