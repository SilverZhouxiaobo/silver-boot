package cn.silver.framework.system.service.impl;

import cn.silver.framework.core.service.impl.BaseServiceImpl;
import cn.silver.framework.system.domain.SysReport;
import cn.silver.framework.system.mapper.SysReportMapper;
import cn.silver.framework.system.service.ISysReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ureport2模板管理Service业务层处理
 *
 * @author ruoyi
 * @date 2022-01-21
 */
@Service
@Transactional
public class SysReportServiceImpl extends BaseServiceImpl<SysReportMapper, SysReport> implements ISysReportService {

    /**
     * 根据code查询参数
     *
     * @param code
     *
     * @return
     */
    public SysReport selectParamByCode(String code) {
        return baseMapper.selectParamByCode(code);
    }

    /**
     * 根据模板的存放路径查询唯一的模板信息
     *
     * @param fullPath
     *
     * @return
     */
    public SysReport selectSignleByFullPath(String fullPath) {
        return baseMapper.selectSignleByFullPath(fullPath);
    }
}
