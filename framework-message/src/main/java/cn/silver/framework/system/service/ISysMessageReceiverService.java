package cn.silver.framework.system.service;


import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.system.domain.SysMessageReceiver;

import java.util.List;

/**
 * 消息接收人信息Service接口
 *
 * @author hb
 * @date 2022-07-01
 */
public interface ISysMessageReceiverService extends IBaseService<SysMessageReceiver> {

    List<SysMessageReceiver> selectByMain(String mainId);
}
