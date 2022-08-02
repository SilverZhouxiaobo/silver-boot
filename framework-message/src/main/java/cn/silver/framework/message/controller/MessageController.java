package cn.silver.framework.message.controller;

import cn.silver.framework.message.constant.MessageType;
import cn.silver.framework.message.service.IMailService;
import cn.silver.framework.message.service.IWeChatService;
import cn.silver.framework.system.domain.SysMessage;
import cn.silver.framework.system.service.ISysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private IMailService mailService;
    @Autowired
    private IWeChatService weChatService;
    @Autowired
    private ISysMessageService messageService;

    @PostMapping("/send/{id}")
    public String sendMail(@PathVariable String id) {
        SysMessage message = this.messageService.selectById(id);
        switch (MessageType.getType(message.getSendType())) {
            case MAIL:
                this.mailService.send(message);
                break;
            case WE_CHAT:
                this.weChatService.sendMessage(message);
                break;
            case SMS:
            default:
                break;
        }
        return "消息发送成功";
    }
}