package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysMessageReceiver;
import cn.silver.framework.system.mapper.SysMessageReceiverMapper;
import cn.silver.framework.system.service.ISysMessageReceiverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息接收人信息Service业务层处理
 *
 * @author hb
 * @date 2022-07-01
 */
@Slf4j
@Service
public class SysMessageReceiverServiceImpl extends BaseServiceImpl<SysMessageReceiverMapper, SysMessageReceiver>
        implements ISysMessageReceiverService {

    @Override
    public List<SysMessageReceiver> selectByMain(String mainId) {
        return this.baseMapper.selectByMain(mainId);
    }
}
