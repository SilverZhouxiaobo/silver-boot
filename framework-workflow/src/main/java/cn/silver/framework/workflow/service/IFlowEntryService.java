package cn.silver.framework.workflow.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.workflow.domain.FlowEntry;
import cn.silver.framework.workflow.domain.FlowTaskExt;

import javax.xml.stream.XMLStreamException;
import java.util.List;

/**
 * FlowEntry数据操作服务接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface IFlowEntryService extends IBaseService<FlowEntry> {

    /**
     * 发布指定流程。
     *
     * @param flowEntry       待发布的流程对象。
     * @param initTaskInfo    第一个非开始节点任务的附加信息。
     * @param flowTaskExtList 所有用户任务的自定义扩展数据列表。
     * @throws XMLStreamException 解析bpmn.xml的异常。
     */
    void publish(FlowEntry flowEntry, String initTaskInfo, List<FlowTaskExt> flowTaskExtList) throws XMLStreamException;

}
