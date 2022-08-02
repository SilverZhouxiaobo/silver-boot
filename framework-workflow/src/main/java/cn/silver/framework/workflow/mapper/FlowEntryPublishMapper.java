package cn.silver.framework.workflow.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.workflow.domain.FlowEntryPublish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 数据操作访问接口。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Mapper
public interface FlowEntryPublishMapper extends BaseMapper<FlowEntryPublish> {
    FlowEntryPublish selectByDefinitionId(@Param("definitionId") String definitionId);

    List<FlowEntryPublish> selectListByDefinitionIds(@Param("definitionIds") Collection<String> definitionIds);

    List<FlowEntryPublish> selectListByEntryId(@Param("entryId") String entryId);
}
