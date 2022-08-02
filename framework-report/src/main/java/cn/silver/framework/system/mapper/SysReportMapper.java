package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.system.domain.SysReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ureport2模板管理Mapper接口
 *
 * @author ruoyi
 * @date 2022-01-21
 */
@Mapper
public interface SysReportMapper extends BaseMapper<SysReport> {

    /**
     * 根据code查询参数
     *
     * @param code
     *
     * @return
     */
    SysReport selectParamByCode(@Param("code") String code);

    /**
     * 根据模板的存放路径查询唯一的模板信息
     *
     * @param fullPath
     *
     * @return
     */
    SysReport selectSignleByFullPath(@Param("fullPath") String fullPath);
}
