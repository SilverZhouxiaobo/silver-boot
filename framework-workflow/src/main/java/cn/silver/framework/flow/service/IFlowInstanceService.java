package cn.silver.framework.flow.service;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.flow.domain.FlowInstance;
import cn.silver.framework.flow.domain.dto.FlowViewerDto;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author XuanXuan
 * @date 2021-04-03 14:40
 */
public interface IFlowInstanceService {

    PageInfo<FlowInstance> selectPage(PageBean page, FlowInstance entity);


    /**
     * 获取流程执行过程
     *
     * @param procInsId
     * @return
     */
    Response<List<FlowViewerDto>> getFlowViewer(String procInsId);

    void active(String instanceId);

    void suspend(String instanceId);

    void stop(String processInstanceId, String stopReason, boolean forCancel);

    void delete(String processInstanceId);
}
