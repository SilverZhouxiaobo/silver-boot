package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowEntryVariable;

/**
 * 流程变量数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface IFlowEntryVariableService extends IBaseService<FlowEntryVariable> {

    void init(String entryId);

    /**
     * 删除指定流程Id的所有变量。
     *
     * @param entryId 流程Id。
     */
    void removeByEntryId(String entryId);
}
