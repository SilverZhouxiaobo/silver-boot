package cn.silver.framework.config.mapper;

import cn.silver.framework.config.domain.SysRegion;
import cn.silver.framework.core.mapper.TreeMapper;
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
