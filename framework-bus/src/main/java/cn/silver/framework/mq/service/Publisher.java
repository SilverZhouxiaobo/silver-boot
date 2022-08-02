package cn.silver.framework.mq.service;

import cn.silver.framework.mq.po.Mail;

public interface Publisher {
    void publishMail(Mail mail);//使用fanout交换机发布消息给所有队列

    void senddirectMail(Mail mail, String routingkey);//使用direct交换机发送消息

    void sendtopicMail(Mail mail, String routingkey);//使用topic交换机发送消息
}
