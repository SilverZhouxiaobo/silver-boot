package cn.silver.framework.flow.service;

import cn.silver.framework.core.page.PageBean;
import cn.silver.framework.workflow.vo.FlowTaskVo;
import com.github.pagehelper.PageInfo;
import org.flowable.bpmn.model.UserTask;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author XuanXuan
 * @date 2021-04-03 14:42
 */
public interface IFlowTaskService {

    /**
     * 获取用户的任务列表。这其中包括当前用户作为指派人和候选人。
     *
     * @param pageParam 分页对象。
     * @param vo        查询过滤条件。
     * @return 用户的任务列表。
     */
    PageInfo<FlowTaskVo> todoList(PageBean pageParam, FlowTaskVo vo);

    /**
     * 获取用户已办任务列表
     *
     * @param pageParam 分页对象
     * @param vo        查询条件
     * @return
     */
    PageInfo<FlowTaskVo> doneList(PageBean pageParam, FlowTaskVo vo);

    /**
     * 获取当前用户的任务总数。这其中包括当前用户作为指派人和候选人。
     *
     * @return
     */
    long getTaskCount();

    boolean checkPermission(String taskId);

    /**
     * 获取所有可回退的节点
     *
     * @param taskId
     *
     * @return
     */
    List<UserTask> findReturnTaskList(String taskId);

    /**
     * 获取所有可达的下一级节点
     *
     * @param taskId
     *
     * @return
     */
    List<UserTask> findNextTaskList(String taskId);

    /**
     * 流程历史流转记录
     *
     * @param procInsId 流程实例Id
     *
     * @return
     */
    Map<String, Object> flowRecord(String procInsId, String deployId);

    /**
     * 获取流程过程图
     *
     * @param processId
     * @return
     */
    InputStream diagram(String processId);

    /**
     * 获取流程变量
     *
     * @param taskId
     * @return
     */
    Map<String, Object> processVariables(String taskId);
}
