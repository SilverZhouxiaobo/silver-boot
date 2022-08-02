package cn.silver.framework.mq.listener;

import cn.silver.framework.mq.po.Mail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class DirectListener1 {

    @RabbitListener(queues = "framework.direct.queue1")
    public void displayMail(Mail mail) throws Exception {
        System.out.println("directqueue1队列监听器1号收到消息" + mail.toString());
    }
}
