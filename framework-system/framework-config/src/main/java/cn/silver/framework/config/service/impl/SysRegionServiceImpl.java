package cn.silver.framework.config.service.impl;


import cn.silver.framework.config.domain.SysRegion;
import cn.silver.framework.config.mapper.SysRegionMapper;
import cn.silver.framework.config.service.ISysRegionService;
import cn.silver.framework.core.service.impl.TreeServiceImpl;
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
