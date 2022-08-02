package cn.silver.framework.flow.service;

import cn.silver.framework.core.bean.Response;
import cn.silver.framework.core.page.ResponsePageInfo;
import cn.silver.framework.flow.domain.dto.FlowTaskDto;
import cn.silver.framework.flow.domain.vo.FlowTaskVo;

public interface IFlowProcessService {


    /**
     * 认领/签收任务
     *
     * @param taskId 请求实体参数
     */
    void claim(String taskId);

    /**
     * 取消认领/签收任务
     *
     * @param taskId 请求实体参数
     */
    void unClaim(String taskId);


    /**
     * 审批任务
     *
     * @param task 请求实体参数
     * @return
     */
    Response<Object> complete(FlowTaskVo task);

    /**
     * 驳回任务
     *
     * @param flowTaskVo
     */
    void taskReject(FlowTaskVo flowTaskVo);


    /**
     * 退回任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void taskReturn(FlowTaskVo flowTaskVo);


    /**
     * 删除任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void deleteTask(FlowTaskVo flowTaskVo);

    /**
     * 委派任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void delegateTask(FlowTaskVo flowTaskVo);


    /**
     * 转办任务
     *
     * @param flowTaskVo 请求实体参数
     */
    void assignTask(FlowTaskVo flowTaskVo);

    /**
     * 我发起的流程
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponsePageInfo<FlowTaskDto> myProcess(Integer pageNum, Integer pageSize);

    /**
     * 取消申请
     *
     * @param instanceId
     * @return
     */
    Response<Object> stopProcess(String instanceId);

    /**
     * 撤回流程
     *
     * @param instanceId
     * @return
     */
    void revokeProcess(String instanceId);
}
