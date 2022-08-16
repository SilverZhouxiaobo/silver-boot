package cn.silver.framework.config.mapper;

import cn.silver.framework.config.domain.SysTag;
import cn.silver.framework.core.mapper.TreeMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: sys_tag
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Mapper
public interface SysTagMapper extends TreeMapper<SysTag> {

    List<SysTag> getTagByPid(String id);

    SysTag getTagByCode(String code);

    List<SysTag> getTagByName(String[] name);
}
