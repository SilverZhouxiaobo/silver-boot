package cn.silver.framework.mq.service.impl;

import cn.silver.framework.mq.po.Mail;
import cn.silver.framework.mq.service.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PublisherImpl implements Publisher {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void publishMail(Mail mail) {
        rabbitTemplate.convertAndSend("fanout", "", mail);
    }

    @Override
    public void senddirectMail(Mail mail, String routingkey) {
        rabbitTemplate.convertAndSend("direct", routingkey, mail);
    }

    @Override
    public void sendtopicMail(Mail mail, String routingkey) {
        rabbitTemplate.convertAndSend("mytopic", routingkey, mail);
    }
}
