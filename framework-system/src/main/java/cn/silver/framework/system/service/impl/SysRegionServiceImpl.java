package cn.silver.framework.system.service.impl;


import cn.silver.framework.core.service.impl.TreeServiceImpl;
import cn.silver.framework.system.domain.SysRegion;
import cn.silver.framework.system.mapper.SysRegionMapper;
import cn.silver.framework.system.service.ISysRegionService;
import org.springframework.stereotype.Service;

/**
 * 行政区域信息Service业务层处理
 *
 * @author hb
 * @date 2022-07-06
 */
@Service
public class SysRegionServiceImpl extends TreeServiceImpl<SysRegionMapper, SysRegion> implements ISysRegionService {

}
