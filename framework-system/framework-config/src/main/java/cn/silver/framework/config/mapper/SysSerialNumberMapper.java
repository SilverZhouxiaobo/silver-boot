package cn.silver.framework.config.mapper;

import cn.silver.framework.config.domain.SysSerialNumber;
import cn.silver.framework.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 单编码规则配置Mapper接口
 *
 * @author hb
 * @date 2022-06-20
 */
@Mapper
public interface SysSerialNumberMapper extends BaseMapper<SysSerialNumber> {

    SysSerialNumber selectByBusinessCode(@Param("businessCode") String businessCode);
}
