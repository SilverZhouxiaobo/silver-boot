package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.message.constant.MessageType;
import cn.silver.framework.system.domain.SysMessage;
import cn.silver.framework.system.domain.SysMessageTemplate;
import cn.silver.framework.system.mapper.SysMessageMapper;
import cn.silver.framework.system.service.ISysMessageService;
import cn.silver.framework.system.service.ISysMessageTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 短消息Service业务层处理
 *
 * @author hb
 * @date 2022-06-20
 */
@Service
public class SysMessageServiceImpl extends BaseServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

    @Autowired
    private RabbitTemplate template;
    @Autowired
    private ISysMessageTemplateService templateService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(SysMessage message) {
        if (StringUtils.isNotBlank(message.getTemplate())) {
            SysMessageTemplate messageTemplate = templateService.selectById(message.getTemplate());
            String content = messageTemplate.getTemplateContent();
            for (Map.Entry<String, Object> entry : message.getMessageParams().entrySet()) {
                content = content.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }
            message.setSendContent(content);
        }
        int result = super.insert(message);
        MessageType messageType = MessageType.getType(message.getSendType());
        if (messageType != null) {
            this.template.convertAndSend(messageType.getRouting(), message.getId());
        }
        return result;
    }

}
