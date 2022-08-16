package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.system.domain.SysComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统评论记录Mapper接口
 *
 * @author hb
 * @date 2022-07-06
 */
@Mapper
public interface SysCommentMapper extends BaseMapper<SysComment> {

}
