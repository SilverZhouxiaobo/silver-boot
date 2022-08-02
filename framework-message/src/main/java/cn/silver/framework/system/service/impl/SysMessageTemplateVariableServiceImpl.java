package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysMessageTemplateVariable;
import cn.silver.framework.system.mapper.SysMessageTemplateVariableMapper;
import cn.silver.framework.system.service.ISysMessageTemplateVariableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 消息模板参数Service业务层处理
 *
 * @author hb
 * @date 2022-07-01
 */
@Slf4j
@Service
public class SysMessageTemplateVariableServiceImpl extends BaseServiceImpl<SysMessageTemplateVariableMapper, SysMessageTemplateVariable>
        implements ISysMessageTemplateVariableService {

}
