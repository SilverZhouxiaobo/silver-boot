package cn.silver.framework.mq.listener;

import cn.silver.framework.mq.po.Mail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
public class DirectListener2 {

    @RabbitListener(queues = "gacim.direct.queue1")
    public void displayMail(Mail mail) throws Exception {
        System.out.println("directqueue2队列监听器2号收到消息" + mail.toString());
    }
}
