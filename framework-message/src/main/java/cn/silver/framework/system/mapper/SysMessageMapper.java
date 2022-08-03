package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.system.domain.SysMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短消息Mapper接口
 *
 * @author hb
 * @date 2022-06-20
 */
@Mapper
public interface SysMessageMapper extends BaseMapper<SysMessage> {

}
