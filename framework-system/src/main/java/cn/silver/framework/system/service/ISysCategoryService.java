package cn.silver.framework.system.service;

import cn.silver.framework.core.service.ITreeService;
import cn.silver.framework.system.domain.SysCategory;

import java.util.List;

/**
 * @Description: 分类字典
 * @Author: hb
 * @Date: 2019-05-29
 * @Version: V1.0
 */
public interface ISysCategoryService extends ITreeService<SysCategory> {

    List<SysCategory> selectByBaseCode(String baseCode);
}
