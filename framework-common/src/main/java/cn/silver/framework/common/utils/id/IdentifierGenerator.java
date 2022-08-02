package cn.silver.framework.common.utils.id;


/**
 * Id生成器接口
 *
 * @author sd-wangtaicheng@sdcncsi.com.cn nieqiuqiu
 * @since 2019-10-15
 * @since 3.3.0
 */
public interface IdentifierGenerator {

    /**
     * 生成Id
     *
     * @param entity 实体
     * @return id
     */
    Number nextId(Object entity);

    /**
     * 生成uuid
     *
     * @param entity 实体
     * @return uuid
     */
    default String nextUUID(Object entity) {
        return IdWorker.get32UUID();
    }
}