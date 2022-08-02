package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.TreeMapper;
import cn.silver.framework.system.domain.SysRegion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行政区域信息Mapper接口
 *
 * @author hb
 * @date 2022-07-06
 */
@Mapper
public interface SysRegionMapper extends TreeMapper<SysRegion> {

}
