package cn.silver.framework.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * direct直连模式的交换机配置,包括一个direct交换机，两个队列，三根网线binding
 *
 * @author Administrator
 */
@SpringBootConfiguration
public class DirectExchangeConfig {
    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange("gacim-direct");
        return directExchange;
    }

    @Bean
    public Queue directQueue1() {
        Queue queue = new Queue("gacim.direct.queue1");
        return queue;
    }

    @Bean
    public Queue directQueue2() {
        Queue queue = new Queue("gacim.direct.queue2");
        return queue;
    }

    @Bean
    public Binding bindingorange() {
        Binding binding = BindingBuilder.bind(directQueue1()).to(directExchange()).with("orange");
        return binding;
    }

    @Bean
    public Binding bindingblack() {
        Binding binding = BindingBuilder.bind(directQueue2()).to(directExchange()).with("black");
        return binding;
    }

    @Bean
    public Binding bindinggreen() {
        Binding binding = BindingBuilder.bind(directQueue2()).to(directExchange()).with("green");
        return binding;
    }


}
