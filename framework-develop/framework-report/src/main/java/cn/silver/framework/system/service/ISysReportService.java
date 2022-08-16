package cn.silver.framework.system.service;

import cn.silver.framework.core.service.IBaseService;
import cn.silver.framework.system.domain.SysReport;

/**
 * ureport2模板管理Service接口
 *
 * @author ruoyi
 * @date 2022-01-21
 */
public interface ISysReportService extends IBaseService<SysReport> {

    /**
     * 根据code查询参数
     *
     * @param code
     *
     * @return
     */
    SysReport selectParamByCode(String code);

    /**
     * 根据模板的存放路径查询唯一的模板信息
     *
     * @param fullPath
     *
     * @return
     */
    SysReport selectSignleByFullPath(String fullPath);
}
