package cn.silver.framework.message.service;

import cn.silver.framework.system.domain.SysMessage;

public interface IMailService {

    void send(SysMessage message);

}
