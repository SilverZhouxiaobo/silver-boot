package cn.silver.framework.mq.config;

import cn.silver.framework.mq.constans.StockConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class StockExchangeConfig {

    @Bean
    public Exchange stockExchange() {
        return new TopicExchange(StockConstant.EXCHANGE_KEY, true, false);
    }

    @Bean
    public Queue chinaQueue() {
        return new Queue(StockConstant.CHINA_QUEUE_CODE, true, false, false);
    }

    @Bean
    public Queue hkQueue() {
        return new Queue(StockConstant.HK_QUEUE_CODE, true, false, false);
    }

    @Bean
    public Binding chinaBinding() {
        return new Binding(StockConstant.CHINA_QUEUE_CODE,
                Binding.DestinationType.QUEUE,
                StockConstant.EXCHANGE_KEY,
                StockConstant.CHINA_QUEUE_KEY,
                null);
    }

    @Bean
    public Binding hkBinding() {
        return new Binding(StockConstant.HK_QUEUE_CODE,
                Binding.DestinationType.QUEUE,
                StockConstant.EXCHANGE_KEY,
                StockConstant.HK_QUEUE_KEY,
                null);
    }
}
