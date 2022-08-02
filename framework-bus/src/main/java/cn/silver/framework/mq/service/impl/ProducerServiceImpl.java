package cn.silver.framework.mq.service.impl;

import cn.silver.framework.mq.po.Mail;
import cn.silver.framework.mq.service.IProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProducerServiceImpl implements IProducerService {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void sendMail(String queue, Mail mail) {
        rabbitTemplate.setDefaultReceiveQueue(queue);
        rabbitTemplate.convertAndSend(queue, mail);
    }

}
