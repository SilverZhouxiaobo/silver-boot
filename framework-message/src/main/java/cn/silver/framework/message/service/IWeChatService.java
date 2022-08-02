package cn.silver.framework.message.service;

import cn.silver.framework.system.domain.SysMessage;

public interface IWeChatService {
    void sendMessage(SysMessage message);
}
