package cn.silver.framework.message.listener;

import cn.silver.framework.message.service.IMailService;
import cn.silver.framework.message.service.IWeChatService;
import cn.silver.framework.mq.constans.MessageContant;
import cn.silver.framework.system.domain.SysMessage;
import cn.silver.framework.system.service.ISysMessageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 周晓菠
 */
@Slf4j
@Component
public class MessageListener {
    @Autowired
    private ISysMessageService messageService;
    @Autowired
    private IWeChatService weChatService;
    @Autowired
    private IMailService mailService;


    @RabbitListener(queues = MessageContant.SMS_QUEUE_CODE)
    public void smsMessageListener(Message message, String id, Channel channel) {
        log.info("taskId:{},短信发送服务准备启动", id);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        if (StringUtils.isNotBlank(id)) {
            try {
//                SysMessage sms = this.messageService.selectById(id);
//                this.mailService.send(sms);
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    channel.basicReject(deliveryTag, false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = MessageContant.MAIL_QUEUE_CODE)
    public void mailMessageListener(Message message, String id, Channel channel) {
        log.info("taskId:{},邮件发送服务准备启动", id);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        if (StringUtils.isNotBlank(id)) {
            try {
                SysMessage mail = this.messageService.selectById(id);
                this.mailService.send(mail);
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    channel.basicReject(deliveryTag, false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = MessageContant.WECHAT_QUEUE_CODE)
    public void wechatMessageListener(Message message, String id, Channel channel) {
        log.info("taskId:{},微信消息推送服务准备启动", id);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        if (StringUtils.isNotBlank(id)) {
            try {
                SysMessage wechat = this.messageService.selectById(id);
                this.weChatService.sendMessage(wechat);
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    channel.basicReject(deliveryTag, false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
