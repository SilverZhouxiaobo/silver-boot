package cn.silver.framework.mq.config;

import cn.silver.framework.mq.constans.MessageContant;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoux
 */
@Configuration
public class MessageExchangeConfig {

    @Bean
    public Exchange messageExchange() {
        return new TopicExchange(MessageContant.EXCHANGE_KEY);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(MessageContant.SMS_QUEUE_CODE);
    }

    @Bean
    public Queue mailQueue() {
        return new Queue(MessageContant.MAIL_QUEUE_CODE);
    }

    @Bean
    public Queue wechatQueue() {
        return new Queue(MessageContant.WECHAT_QUEUE_CODE);
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(messageExchange()).with(MessageContant.SMS_QUEUE_KEY).noargs();
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(messageExchange()).with(MessageContant.MAIL_QUEUE_KEY).noargs();
    }

    @Bean
    public Binding wechatBinding() {
        return BindingBuilder.bind(wechatQueue()).to(messageExchange()).with(MessageContant.WECHAT_QUEUE_KEY).noargs();
    }
}
