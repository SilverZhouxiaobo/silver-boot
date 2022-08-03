package cn.silver.framework.db.interceptor;

import cn.silver.framework.core.domain.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Administrator
 */
@Slf4j
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MyBatisInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws SQLException {
        log.debug("====开始处理插入更新过滤器======");
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        if (parameter instanceof MapperMethod.ParamMap) {
            HashMap<String, Object> paramMap = (MapperMethod.ParamMap) parameter;
            paramMap.entrySet().forEach(entry -> {
                Object param = entry.getValue();
                if (param instanceof BaseEntity) {
                    execute(mappedStatement.getSqlCommandType(), (BaseEntity) param);
                } else if (param instanceof ArrayList) {
                    ((ArrayList<BaseEntity>) param).stream().forEach(entity -> execute(mappedStatement.getSqlCommandType(), entity));
                }
            });
        } else if (parameter instanceof BaseEntity) {
            execute(mappedStatement.getSqlCommandType(), (BaseEntity) parameter);
        }
        Executor executor = (Executor) invocation.getTarget();
        return executor.update(mappedStatement, parameter);
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    private void execute(SqlCommandType type, BaseEntity entity) {
        if (type.equals(SqlCommandType.DELETE)) {
            return;
        }
        if (type.equals(SqlCommandType.INSERT)) {
            entity.preInsert();
        } else {
            entity.preUpdate();
        }
    }
}
