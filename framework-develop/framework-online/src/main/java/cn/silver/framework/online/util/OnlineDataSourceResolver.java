package cn.silver.framework.online.util;


import cn.silver.framework.online.domain.OnlineDatasourceRelation;
import cn.silver.framework.online.domain.OnlineDblink;
import cn.silver.framework.online.domain.OnlineDict;
import cn.silver.framework.online.domain.OnlineTable;
import cn.silver.framework.online.service.OnlineDblinkService;
import org.apache.commons.mail.DataSourceResolver;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;

/**
 * 目前仅仅应用于在线表单服务对象的多数据源切换的动态解析。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Component
public class OnlineDataSourceResolver implements DataSourceResolver {

    @Resource
    private OnlineDblinkService onlineDblinkService;

    /**
     * 动态解析方法。
     * 先判断第一个参数的类型，在根据具体的类型去获取dblinkId，并根据该值进行多数据源的切换。
     *
     * @param arg        可选的入参。MyDataSourceResolver注解中的arg参数。
     * @param methodArgs 被织入方法的所有参数。
     * @return 返回用于多数据源切换的类型值。DataSourceResolveAspect 切面方法会根据该返回值和配置信息，进行多数据源切换。
     */
    public int resolve(String arg, Object[] methodArgs) {
        Serializable id;
        if (methodArgs[0] instanceof OnlineTable) {
            id = ((OnlineTable) methodArgs[0]).getDblinkId();
        } else if (methodArgs[0] instanceof OnlineDict) {
            id = ((OnlineDict) methodArgs[0]).getDblinkId();
        } else if (methodArgs[0] instanceof OnlineDatasourceRelation) {
            id = ((OnlineDatasourceRelation) methodArgs[0]).getSlaveTable().getDblinkId();
        } else {
            throw new IllegalArgumentException("动态表单操作服务方法，不支持类型 ["
                    + methodArgs[0].getClass().getSimpleName() + "] 作为第一个参数！");
        }
        OnlineDblink dblink = onlineDblinkService.selectById((String) id);
        return dblink.getDblinkConfigConstant();
    }

    @Override
    public DataSource resolve(String s) throws IOException {
        return null;
    }

    @Override
    public DataSource resolve(String s, boolean b) throws IOException {
        return null;
    }
}
