package cn.silver.framework.mq.service;

import cn.silver.framework.mq.po.Mail;

/**
 * @author zhoux
 */
public interface IProducerService {
    void sendMail(String queue, Mail mail);//向队列queue发送消息
}
