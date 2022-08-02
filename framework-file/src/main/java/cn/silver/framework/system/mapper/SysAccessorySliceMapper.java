package cn.silver.framework.system.mapper;

import cn.silver.framework.core.mapper.BaseMapper;
import cn.silver.framework.system.domain.SysAccessorySlice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 附件管理-分片信息
 * @Author: jeecg-boot
 * @Date: 2022-01-04
 * @Version: V1.0
 */
public interface SysAccessorySliceMapper extends BaseMapper<SysAccessorySlice> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<SysAccessorySlice> selectByMainId(@Param("mainId") String mainId);
}
