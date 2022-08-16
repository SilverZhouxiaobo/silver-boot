package cn.silver.framework.config.mapper;

import cn.silver.framework.config.domain.SysCategory;
import cn.silver.framework.core.mapper.TreeMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date: 2019-05-29
 * @Version: V1.0
 */
@Mapper
public interface SysCategoryMapper extends TreeMapper<SysCategory> {

    List<SysCategory> selectByBaseCode(@Param("baseCode") String baseCode);
}
