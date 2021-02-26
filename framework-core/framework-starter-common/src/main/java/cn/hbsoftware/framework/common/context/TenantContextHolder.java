package cn.hbsoftware.framework.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 多租户Holder
 *
 * @author pangu
 * @since 2020-9-8
 */
@UtilityClass
public class TenantContextHolder {
    /**
     * 支持父子线程之间的数据传递
     */
    private static final ThreadLocal<String> CONTEXT = new TransmittableThreadLocal<>();

    public static void setTenant(String tenant) {
        CONTEXT.set(tenant);
    }

    public static String getTenant() {
        return CONTEXT.get();
    }

    /**
     * TTL 设置租户ID<br/>
     * <b>谨慎使用此方法,避免嵌套调用。尽量使用 {@code TenantBroker} </b>
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        CONTEXT.set(tenantId);
    }

    /**
     * 获取TTL中的租户ID
     *
     * @return String
     */
    public String getTenantId() {
        return CONTEXT.get();
    }

    /**
     * 清除tenantId
     */
    public static void clear() {
        CONTEXT.remove();
    }
}