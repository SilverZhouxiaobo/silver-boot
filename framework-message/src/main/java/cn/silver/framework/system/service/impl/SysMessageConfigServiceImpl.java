package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysMessageConfig;
import cn.silver.framework.system.mapper.SysMessageConfigMapper;
import cn.silver.framework.system.service.ISysMessageConfigService;
import org.springframework.stereotype.Service;

/**
 * 消息推送接口配置Service业务层处理
 *
 * @author hb
 * @date 2022-06-28
 */
@Service
public class SysMessageConfigServiceImpl extends BaseServiceImpl<SysMessageConfigMapper, SysMessageConfig> implements ISysMessageConfigService {

}
