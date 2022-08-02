package cn.silver.framework.common.utils.id;


/**
 * 默认生成器
 *
 * @author sd-wangtaicheng@sdcncsi.com.cn nieqiuqiu
 * @since 2019-10-15
 * @since 3.3.0
 */
public class DefaultIdentifierGenerator implements IdentifierGenerator {

    private final Sequence sequence;

    public DefaultIdentifierGenerator() {
        this.sequence = new Sequence();
    }

    public DefaultIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public DefaultIdentifierGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long nextId(Object entity) {
        return sequence.nextId();
    }
}
