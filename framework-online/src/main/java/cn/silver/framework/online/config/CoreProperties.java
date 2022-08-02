package cn.silver.framework.online.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * common-core的配置属性类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "common-core")
public class CoreProperties {

    public static final String MYSQL_TYPE = "mysql";
    public static final String POSTGRESQL_TYPE = "postgresql";

    /**
     * 数据库类型。
     */
    private String databaseType = MYSQL_TYPE;

    /**
     * 是否为MySQL。
     *
     * @return 是返回true，否则false。
     */
    public boolean isMySql() {
        return this.databaseType.equals(MYSQL_TYPE);
    }

    /**
     * 是否为PostgreSQl。
     *
     * @return 是返回true，否则false。
     */
    public boolean isPostgresql() {
        return this.databaseType.equals(POSTGRESQL_TYPE);
    }
}
