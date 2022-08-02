package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysMessageTemplate;
import cn.silver.framework.system.domain.SysMessageTemplateVariable;
import cn.silver.framework.system.mapper.SysMessageTemplateMapper;
import cn.silver.framework.system.service.ISysMessageTemplateService;
import cn.silver.framework.system.service.ISysMessageTemplateVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 短消息模板Service业务层处理
 *
 * @author hb
 * @date 2022-06-20
 */
@Service
public class SysMessageTemplateServiceImpl extends BaseServiceImpl<SysMessageTemplateMapper, SysMessageTemplate> implements ISysMessageTemplateService {

    @Autowired
    private ISysMessageTemplateVariableService variableService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insert(SysMessageTemplate entity) {
        entity.setTemplateTestJson(getParams(entity.getTemplateContent(), entity.getVariables()));
        int result = super.insert(entity);
        entity.getVariables().stream().forEach(variable -> variable.setTemplate(entity.getId()));
        this.variableService.insertBatch(entity.getVariables());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int update(SysMessageTemplate entity) {
        entity.setTemplateTestJson(getParams(entity.getTemplateContent(), entity.getVariables()));
        entity.getVariables().stream().forEach(variable -> variable.setTemplate(entity.getId()));
        this.variableService.insertOrUpdateBatch(entity.getVariables());
        return super.update(entity);
    }

    public String getParams(String template, List<SysMessageTemplateVariable> variables) {
        for (SysMessageTemplateVariable variable : variables) {
            template.replace("${" + variable.getVariableName() + "}", variable.getDefaultValue());
        }
        return template;
    }
}
