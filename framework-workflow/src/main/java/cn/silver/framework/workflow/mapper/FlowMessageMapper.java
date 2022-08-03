package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 工作流消息数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowMessageMapper extends BaseMapper<FlowMessage> {


    List<FlowMessage> selecctListByUser(@Param("messageType") String messageType, @Param("loginName") String loginName, @Param("groupIdSet") Set<String> groupIdSet);

    /**
     * 获取指定用户和身份分组Id集合的催办消息列表。
     *
     * @param loginName  用户的登录名。与流程任务的assignee精确匹配。
     * @param groupIdSet 用户身份分组Id集合。
     * @return 查询后的催办消息列表。
     */
    List<FlowMessage> getRemindingMessageListByUser(
            @Param("loginName") String loginName, @Param("groupIdSet") Set<String> groupIdSet);

    /**
     * 获取指定用户和身份分组Id集合的抄送消息列表。
     *
     * @param loginName  用户登录名。
     * @param groupIdSet 用户身份分组Id集合。
     * @param read       true表示已读，false表示未读。
     * @return 查询后的抄送消息列表。
     */
    List<FlowMessage> getCopyMessageListByUser(
            @Param("loginName") String loginName,
            @Param("groupIdSet") Set<String> groupIdSet,
            @Param("read") Boolean read);

    /**
     * 计算当前用户催办消息的数量。
     *
     * @param loginName  用户登录名。
     * @param groupIdSet 用户身份分组Id集合。
     * @return 数据数量。
     */
    int countRemindingMessageListByUser(
            @Param("loginName") String loginName, @Param("groupIdSet") Set<String> groupIdSet);

    /**
     * 计算当前用户未读抄送消息的数量。
     *
     * @param loginName  用户登录名。
     * @param groupIdSet 用户身份分组Id集合。
     * @return 数据数量
     */
    int countCopyMessageListByUser(@Param("loginName") String loginName, @Param("groupIdSet") Set<String> groupIdSet);

    List<FlowMessage> selectListByTaskId(@Param("taskId") String taskId);

    void deleteByInstanceId(@Param("instanceId") String instanceId);

    List<FlowMessage> selectListByInstanceId(@Param("instanceId") String instanceId);
}
