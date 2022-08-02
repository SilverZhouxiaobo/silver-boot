package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import com.alibaba.fastjson.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 */
public interface IFlowEntryPublishService extends IBaseService<FlowEntryPublish> {

    List<FlowEntryPublish> selectByDefinitionIds(Collection<String> definitionIds);

    void activate(String id);

    void suspend(String id);

    void changeMainVersion(JSONObject params);
}
