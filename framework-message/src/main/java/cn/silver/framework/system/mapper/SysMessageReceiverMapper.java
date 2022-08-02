package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.system.domain.SysMessageReceiver;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 消息接收人信息Mapper接口
 *
 * @author hb
 * @date 2022-07-01
 */
@Mapper
public interface SysMessageReceiverMapper extends BaseMapper<SysMessageReceiver> {

    List<SysMessageReceiver> selectByMain(String mainId);
}
